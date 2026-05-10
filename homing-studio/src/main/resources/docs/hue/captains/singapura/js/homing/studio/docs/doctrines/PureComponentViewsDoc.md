# Doctrine — Pure-Component Views

> **No HTML in any consumer code. Every UI element in every AppModule is a Component invocation. The only HTML allowed is the outermost mount-point `<div id="app">` provided by the framework's bootstrap page.**

This is the rule. Read it. Internalise it. Welcome.

---

## In practice

### Banned

In any AppModule's JS (`appMain`, helpers, anything reachable from `appMain`):

- **No HTML tag literals.** No `'<div>…</div>'` strings, no `'<a href=…>'`, no `'<h1>'`.
- **No `innerHTML` / `outerHTML` assignments.** Setting these to anything other than `""` (clear) is forbidden.
- **No raw `document.createElement('div')`.** The HTML element vocabulary is reached through Components, not through the DOM API.

### Permitted

- **Component invocations.** `Heading.render({level: 1, text: "Hi"})`, `Card.render({title, body, footer})`, `Stack.render({children: [a, b]})`.
- **String props.** Text content passed into Components is just a string — Components escape it before emitting it as text content.
- **Event listeners attached to Component-returned DOM nodes.** A Component returns a `Node`; the consumer may `node.addEventListener(...)` on it.
- **`document.getElementById` / `querySelector` for traversal.** Reading the DOM is fine. Mutating it is fine *via* Component re-renders (replace a node with another Component's output), not via raw HTML.

### Out of scope (the doctrine doesn't apply here)

- **Component implementations themselves.** `ComponentImpl<C, TH>` template strings or JS render bodies *are* the HTML the framework emits. The doctrine bans HTML in **consumer** code (AppModule JS), not in **provider** code (Component impls).
- **The framework bootstrap page.** `AppHtmlGetAction`'s rendered HTML, the theme picker, etc. is HTML. That's where the outermost `<div id="app">` lives.
- **Generated framework JS.** The auto-prepended `nav`, `params`, `css`, `href`, `_components` blocks are JS code, not HTML, and aren't authored per AppModule.

---

## Why it's worth the strictness

### Type safety crosses the import boundary

Today, the Java-side typing of CSS classes, nav targets, Doc records, query params gets thrown away the moment a JS file starts concatenating `'<a class="' + cn(st_card) + '">'`. The string is opaque to the compiler. A typo, a stale class name, an XSS slip — none of it is caught.

Under the doctrine, the typed handles flow into typed Components. Rename a CSS class on the Java side and every consumer's call site stays correct (or breaks at compile time on the impl side, where it should). The typing reaches the leaves of the view tree.

### Themes get full reach

A theme can override **any** visual element — heading style, button shape, card frame, list bullets, table chrome — because every element is a Component with `ComponentImpl<C, TH>` impls available.

Without the doctrine, themes can repaint but cannot reshape. The "we forgot to make this themable" backdoor doesn't exist when there are no HTML strings to forget.

### Duplication ends at the source

If `<h1>` is `Heading.render({level: 1, …})`, then "make all H1s use Calibri" is one impl change. Today it's a `:root` CSS rule plus prayer plus grep.

Likewise: every studio AppModule today re-emits the same card chrome inline. Under the doctrine, `Card` is one declaration, used everywhere. A redesign of the card (different frame, new badge slot, accessibility audit) lands in one file.

### The ban is enforceable

A conformance test scans every consumer JS file for tag literals and `innerHTML` assignments. The doctrine isn't a guideline that drifts — it's a build break. New AppModules cannot regress; migrated AppModules cannot un-migrate.

### Doctrine clarity

"No HTML in consumer code" is a clean rule. "Don't use innerHTML, prefer DOM API, use Components when convenient" is murky and erodes. Bright lines hold; smudgy ones don't.

---

## What the framework owes in return

A strict doctrine fails when the alternative is too verbose. The framework commits to:

- **A complete atom vocabulary** — every HTML element a consumer might reach for has a Component (`Heading`, `Paragraph`, `Span`, `Section`, `List`, `Image`, `Button`, `Input`, `Table`, …). No element is unreachable through Components.
- **Ergonomic composition** — children, lists, conditional content express naturally. `Stack.render({children: [a, b, c]})` is not painful.
- **Auto-injection** — Components arrive as named consts in every JS module, like `nav` / `css` / `href` today. No imports to write per consumer.
- **Migration path** — existing AppModules keep working un-migrated; conformance applies opt-in per module via a marker. Migration is per-PR, not flag-day.

If any of those promises slips, the doctrine becomes a hardship and authors will work around it. The framework is responsible for keeping the doctrine cheap.

---

## How to think about it

The doctrine is the answer to a natural question that arises after reading [Defect 0002](#ref:def-2) and [RFC 0003](#ref:rfc-3):

> "Why not just provide Components and let consumers use HTML when convenient?"

Because the moment HTML is convenient *anywhere*, it becomes convenient *everywhere*. Type safety leaks. Themes lose reach. Duplication regrows. The doctrine is what holds the design together; the Component primitive is what makes the doctrine cheap to honour.

The two are the same idea told from opposite ends:

- **Bottom-up**: "Components are a useful primitive."
- **Top-down**: "Views must be pure compositions; therefore we need Components."

Both readings arrive at the same code. The top-down framing is the doctrine. Consumer code lives under it.

---

## See also

- [RFC 0003 — Pure-Component Views & Themeable Form](#ref:rfc-3) — the design that makes this doctrine expressible.
- [Defect 0002 — Themes Vary Paint, Not Form](#ref:def-2) — the gap that motivates it.
