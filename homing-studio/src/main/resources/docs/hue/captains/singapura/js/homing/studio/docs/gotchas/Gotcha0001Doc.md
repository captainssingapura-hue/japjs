# Gotcha 0001 — `String.formatted()` in code-gen text blocks

| Field | Value |
|---|---|
| **Kind** | Gotcha — small craft note. Not a defect (no framework gap), not a doctrine (no foundational rule). A paper-cut you only learn by being cut. |
| **Bit us** | Three times in one session, 2026-05-12, building the audio runtime in `AppHtmlGetAction.renderAudioRuntime`. |
| **Severity** | Low impact (build green, server runs), high pain (404 on every audio page; stack trace points at a line number with `IllegalFormatFlagsException` that nothing in the source code suggests). |
| **Where** | Java text blocks emitting JS / CSS / HTML via `.formatted()`. |

---

## 1. The symptom

Build green. Tests green. Server starts. First request returns 500 with a stack trace:

```
java.util.IllegalFormatFlagsException: Flags = ' '
    at java.base/java.util.Formatter$FormatSpecifier.checkText(Formatter.java:3326)
    ...
    at java.base/java.lang.String.formatted(String.java:4452)
    at hue.captains.singapura.js.homing.server.AppHtmlGetAction.renderAudioRuntime(AppHtmlGetAction.java:799)
```

Or `UnknownFormatConversionException: Conversion = ' '`. Both come from the same root cause: a `%` character in the text block that isn't followed by a valid printf conversion.

## 2. Why it bites

`String.formatted()` (and `String.format()`) treat `%` as the format specifier introducer. Every `%` in your text block has to be one of:

- A valid format specifier: `%s`, `%d`, `%n`, etc.
- An escape: `%%` (which produces a single `%`).

Anything else throws — at *call time*, not at compile time. The Java compiler doesn't validate format strings.

When the text block contains plain text, this is fine: `%` rarely appears naturally. When the text block contains **code in another language**, `%` appears everywhere.

## 3. The three strikes that motivated this gotcha

All within `AppHtmlGetAction.renderAudioRuntime`, all caught only after hitting the running server:

### Strike 1 — CSS keyframes

The injected runtime style includes a `@keyframes` block:

```css
@keyframes homing-audio-played {
    0%   { transform: scale(1); }
    30%  { transform: scale(0.94); }
    60%  { transform: scale(1.03); }
    100% { transform: scale(1); }
}
```

Each `%` in the keyframe percentages is a printf format flag attempt. `0%   {` → `% ` is `Conversion = ' '`. Throws.

### Strike 2 — JS modulo operator

The hash function for variant selection:

```js
function hashIndex(el, n) {
    // ...
    return Math.abs(h) % n;
}
```

`% n` is `Flags = ' '`. Throws.

### Strike 3 — URL-encoded `#` in the favicon

Inline SVG favicon data URI:

```html
<link rel="icon" href="data:image/svg+xml,<svg ... fill='#F4B942'>H</text></svg>">
```

To put a `#` inside a `data:` URI value, you URL-encode it as `%23`. In the text block: `fill='%23F4B942'`. Throws.

Each fix was an `escape-as-%%` patch. Each new feature with new `%` characters re-discovered the bug.

## 4. The fix — named placeholders + String.replace()

Switch from positional `%s` formatting to literal-string substitution:

```java
// Before — fragile, requires %% escaping any literal %.
return """
        const VOCAL_PALETTE = %s;
        const RATE = 1 / 100%%;
        """.formatted(paletteJs);

// After — `%` is an ordinary character.
return """
        const VOCAL_PALETTE = __VOCAL_PALETTE__;
        const RATE = 1 / 100%;
        """
        .replace("__VOCAL_PALETTE__", paletteJs);
```

The trade-offs:

| Property | `.formatted()` | `.replace()` with `__NAME__` |
|---|---|---|
| `%` handling | Special — must escape `%%` | Ordinary character |
| Placeholder syntax | Positional `%s` | Named `__FOO__` |
| Argument order | Implicit by position | Explicit at call site |
| Compile-time check | None (silent failure) | None (also silent) |
| Performance | Marginally faster (one parse) | Marginally slower (N replaces) — negligible |
| Reads at the call site like | a printf | a Mustache template |

The placeholder pattern `__NAME__` is chosen because it doesn't naturally occur in any code substrate we emit (JS variables don't start with double-underscore, CSS doesn't use it, URLs don't use it). `.replace()` does literal substitution with no parsing — even if the substituted value happened to contain `__OTHER__`, subsequent replacements wouldn't re-scan it. Order-independent within the call chain.

## 5. Where to apply this fix

- **Always** when the text block contains JS / CSS / HTML / any language that uses `%` (i.e., most of them).
- **Optionally** when the text block is plain prose with no `%` characters — `.formatted()` is fine there and reads naturally.
- **Particularly** in code-generation paths: route handlers emitting browser-bound source, build steps emitting config files, anything where the substrate's syntax conflicts with the format-string syntax.

## 6. Why we caught this late

Three reasons the bug evades early detection:

1. **No compile-time validation.** Java doesn't statically check format strings against argument types or escape rules.
2. **No unit tests cover the format-string assembly.** The audio runtime is exercised manually by loading a page; the format error only fires when `String.formatted()` actually runs on the assembled template.
3. **The stack trace points at the `.formatted()` call site**, not at the offending `%` character. You have to scan the entire text block to find the `%` that lacks an escape.

A linter that scans Java text blocks for unescaped `%` not followed by a valid conversion specifier would catch this. Worth considering if we keep doing code generation in `.formatted()` style — but the better answer is the structural fix above.

## 7. The general rule

> **Prefer named-placeholder substitution over `%s`-style formatting when the text block contains code in another language.** `String.formatted()` is for prose; `String.replace()` with `__NAME__` placeholders is for code generation.

If you're writing a Java method that emits JS / CSS / HTML / SQL / etc. as a string, default to `.replace()` and never look at `%%` escapes again.

## 8. Related

- The other `.formatted()` call in `AppHtmlGetAction.execute()` (the outer HTML page template) still uses `%s` because its `%` characters (favicon URL encoding) are stable and already escaped. Migrate opportunistically when next that code is touched.
- This isn't a Java-specific gotcha — any printf-style formatter in any language has the same problem when emitting code. The pattern (named placeholders for code-gen) applies universally.
