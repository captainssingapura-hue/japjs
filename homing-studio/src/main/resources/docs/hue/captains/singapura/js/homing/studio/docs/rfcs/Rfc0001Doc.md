# RFC 0001 — App Registry & Typed Navigation

| Field | Value |
|---|---|
| **Status** | **Implemented** — landed 2026-05-03 across all 12 steps. See [`docs/brand/RENAME-TO-HOMING.md`](#ref:rename-doc) and live tracker at `/app?app=rfc0001-plan` in the studio. |
| **Author** | Howard, with Homing |
| **Created** | 2026-05-01 |
| **Last revised** | 2026-05-03 |
| **Implemented** | 2026-05-03 (Steps 01–12) |
| **Supersedes** | None |
| **Superseded by** | None |
| **Target phase** | Phase 0 (alongside the Spring Boot adapter) — landed |

---

## 0. Status notice

This is a **draft RFC** capturing a design conversation. It is intended for iterative refinement before implementation. Sections marked **OPEN** are explicitly deferred decisions; sections marked **PROPOSED** are recommended defaults that may change. Anything else is the working consensus.

When amending: edit the relevant numbered section and update the `Last revised` field. Significant changes should leave a note in §13 (Revision log).

---

## 1. Motivation

The current AppModule URL contract is:

```
/app?class=hue.captains.singapura.js.homing.demo.es.PitchDeck
```

This has four problems:

1. **URL leaks Java package structure** — internal naming becomes a permanent public API.
2. **Refactoring breaks bookmarks** — renaming the class, moving its package, or restructuring the namespace silently invalidates every saved URL.
3. **No first-class parameter passing** — apps that want to receive query parameters must hand-parse `window.location.search`; there is no typed contract.
4. **No first-class linking between apps** — apps that want to construct links to other apps must hand-concatenate URLs from string literals, with no compile-time check that the target app exists or its parameter shape matches.

Fixing these unlocks a use case the framework otherwise discourages: **traditional multi-page web applications** (admin portals, approval workflows, content sites, deep-linkable dashboards). Homing's underlying model — each AppModule independently served at its own URL — is naturally well-suited to MPAs; what's missing is the type-safe contract for *what data crosses the navigation boundary.*

This RFC proposes a small, internally-consistent extension to the kernel that:

- Replaces class-name-based URLs with a stable, friendly-name URL contract.
- Adds a typed parameter mechanism for AppModules that accept query parameters.
- Adds a typed cross-app linking primitive (`AppLink<M>`) consistent with the existing `Exportable` family.

The change is roughly half a day of kernel work and is fully backward-compatible at the source level (existing AppModules require no changes; sensible defaults apply).

---

## 2. Design overview

Three new concepts. Two new kernel types. One changed URL key.

### 2.1 The new URL contract

```
/app?app=<simple-name>&<param1>=<value1>&<param2>=<value2>...
```

Reserved keys: `app`, `theme`, `locale`. All other query keys are interpreted as the AppModule's typed parameters.

### 2.2 The simple name (proxy registration)

Each AppModule has a `simpleName()` — its public URL identifier. Default = kebab-case of the simple Java class name (`PitchDeck` → `pitch-deck`). Apps may override to lock the URL contract independently of the Java class name.

A `SimpleAppResolver` is built at server startup from a small list of **entry apps** — typically just the home page, the catalogue, or whatever serves as the public-facing root. The resolver then **transitively walks `AppLink<?>` import edges** to discover every other reachable app and registers them all automatically. The user does not enumerate the full app graph; the import declarations already describe it.

This means:

- Adding a new linked app and importing its `link()` is enough — no separate registry update.
- Removing a link removes the target from the registry (unless something else still links to it or it's an entry app).
- The same dependency-graph walking mechanism Homing already uses for CSS, SVG, and module imports applies here.

The resolver maps `simpleName → AppModule` over the transitive closure and rejects collisions at boot time.

### 2.3 Typed parameters

Each AppModule may declare a `Params` record. The kernel:

- Generates a `params` JS object at the top of the AppModule's compiled JS, populated from `window.location.search`.
- Generates typed link-construction functions in any AppModule that imports the target's `link` reference.

### 2.4 Cross-app linking — `AppLink<L>`

A new sealed `Exportable` flavor. Each linkable target (an `AppModule` or a `ProxyApp` — see §2.5) advertises a `link()` record that other modules can import. The writer generates a `nav` const containing typed link-construction functions for every imported `link()`.

### 2.5 External destinations as proxy apps

External URLs (GitHub, documentation sites, third-party services) are modeled as **proxy apps** — typed Java declarations with a `simpleName()`, a `Params` record, and a URL template. Proxy apps:

- Have no JS module, no CSS, no DOM rendering, no `appMain`.
- Are not served by the kernel — `/app?app=<proxy-name>` returns 404 (proxies are not navigation targets in their own right; they are URL builders).
- *Are* registered alongside AppModules in the same resolver and discovered through the same transitive walk (§3.3).
- Are imported via the same `link()` mechanism and appear in the generated `nav` object indistinguishably from internal apps.

This is the move that makes §6.2 (conformance enforcement) clean: every `href` in JS goes through `nav.X(...)`, where `X` is either an internal app or a proxy. No escape hatch for "raw external URLs" is needed because external URLs become typed proxy declarations.

### 2.6 Conformance enforcement — the `href` manager

Mirroring the existing `css` injection (`css.setClass`, `css.addClass`, etc.), the kernel injects a runtime `href` manager into every DomModule that imports any `AppLink<?>`. **Every href operation in user JS must flow through `href.X(...)`** — there are no allowed exceptions, including string concatenation, template-literal interpolation, and same-page fragment anchors.

The conformance scanner enforces a single, simple rule:

> The literal substring `href` may appear in DomModule JS **only** as the manager identifier — i.e., immediately followed by `.` and a recognised manager method. Every other appearance (literal `href=` attribute, `.href` property access, `"href"` string argument) is forbidden.

This is uniform with how `css` discipline works today. See §6.2 for the manager API and the scanner specification.

---

## 3. Detailed design — Java side

### 3.1 The `AppModule` interface — additions

```java
public interface AppModule<M extends AppModule<M>> extends DomModule<M> {

    /** Public URL identifier. Defaults to kebab-case of the simple Java class name. */
    default String simpleName() {
        return defaultSimpleName(this.getClass());
    }

    /** Optional typed parameter record. Default = no parameters. */
    default Class<?> paramsType() {
        return Void.class;
    }

    /** Existing methods unchanged. */
    String title();
    // ...
}
```

`defaultSimpleName(Class<?>)` is a static helper in the kernel. Algorithm:

> Take the simple class name (`PitchDeck`); split on uppercase boundaries (`Pitch`, `Deck`); lowercase and join with `-` (`pitch-deck`). Acronyms split on the next non-uppercase boundary (`HTTPHandler` → `http-handler`).

### 3.2 The `Linkable` and `AppLink<L>` interfaces

A common supertype unifies internal apps and proxy apps as navigation targets:

```java
package hue.captains.singapura.js.homing.core;

public sealed interface Linkable permits AppModule, ProxyApp {
    String simpleName();
    Class<?> paramsType();
}
```

Both `AppModule` and `ProxyApp` extend `Linkable`. The link primitive is parameterized on `Linkable`:

```java
public sealed interface AppLink<L extends Linkable>
        extends Exportable
        permits AppLink {
    // marker; the writer reads the importing module's metadata
}
```

Each linkable target declares an inner `link()` record:

```java
public record PitchDeck() implements AppModule<PitchDeck> {
    public record link() implements AppLink<PitchDeck> {}
    public static final PitchDeck INSTANCE = new PitchDeck();
    // ...
}
```

The convention `record link()` is fixed (always named `link`). It is never used as a JS identifier directly — the writer derives identifiers from the target's class metadata.

Note: `Exportable<M extends EsModule<M>>` may need to be loosened (drop the bound or split into two interfaces) so that `AppLink<L extends Linkable>` can extend it cleanly without forcing `Linkable` to extend `EsModule`. **OPEN — see §9.10.**

### 3.3 The `SimpleAppResolver` — transitive registration

The resolver accepts a list of **entry apps** and walks the `AppLink<?>` import graph to discover the transitive closure of reachable apps. Only entry apps need to be enumerated by the user; every linked app is registered automatically.

```java
public final class SimpleAppResolver {
    private final Map<String, AppModule<?>> bySimpleName;
    private final Map<Class<?>, AppModule<?>> byClass;

    /**
     * Build the registry from a set of entry apps.
     * Walks AppLink&lt;?&gt; import edges transitively; every reachable app
     * is registered. Cycles are tolerated via a visited set.
     * Throws IllegalStateException on simple-name collision in the closure.
     */
    public SimpleAppResolver(List<AppModule<?>> entryApps) {
        var collected = new LinkedHashMap<Class<?>, AppModule<?>>();
        for (var app : entryApps) {
            collect(app, collected);
        }
        // build bySimpleName / byClass from collected; check name uniqueness
    }

    private void collect(AppModule<?> app, Map<Class<?>, AppModule<?>> out) {
        if (out.containsKey(app.getClass())) return;   // cycle guard
        out.put(app.getClass(), app);
        for (var entry : app.imports().getAllImports().entrySet()) {
            var sourceModule = entry.getKey();
            if (sourceModule instanceof AppModule<?> appDep) {
                boolean importsLink = entry.getValue().stream()
                        .anyMatch(e -> e instanceof AppLink<?>);
                if (importsLink) {
                    collect(appDep, out);
                }
            }
        }
    }

    public AppModule<?> resolve(String simpleName) { ... }
    public AppModule<?> resolveByClass(Class<?> cls) { ... }
    public Collection<AppModule<?>> all() { ... }
}
```

**Edge-following rule.** The walker follows an import edge **only when the import contains an `AppLink<?>`**. Other kinds of imports (CSS, SVG, plain `Exportable._Constant`) do *not* trigger registration of the imported target. Rationale: the `AppLink<?>` import is the explicit declaration of "I navigate to this target." Other imports are code-level dependencies, not navigation dependencies. Registering only what's actually navigable keeps the registry honest and avoids surprising entries appearing in the URL surface that nothing intends to expose.

**Both AppModules and ProxyApps are walked.** The `Linkable` supertype lets the resolver register both kinds in the same closure pass. Internal apps and proxy apps are stored in separate maps (so that `/app?app=<name>` dispatch returns 404 for proxy names) but discovered through the same edge-following rule. The user enumerates entry apps; both internal and external link targets come along.

**Visited-set semantics.** Cycles are explicitly OK. App A linking to App B which links back to App A is a perfectly valid pattern (e.g., list view ↔ detail view). The visited set prevents infinite recursion; both apps end up in the registry once.

**Orphan apps.** An AppModule that exists in the codebase but is not reachable from any entry app — and is not itself an entry app — is **not** registered. This is correct behavior: the URL surface is exactly the set of apps that some entry path leads to. To make a previously-orphan app reachable, either add it as an entry app or have something link to it.

### 3.4 Server boot

```java
public class WonderlandDemoServer {
    public static void main(String[] args) {
        // Only entry apps. Everything DemoCatalogue links to (and everything
        // those apps link to) is registered transitively.
        var resolver = new SimpleAppResolver(List.of(
            DemoCatalogue.INSTANCE
        ));
        var registry = new JapjsActionRegistry(resolver);
        var host = new VertxActionHost(registry, 8080);
        host.start();
    }
}
```

A small project may have a single entry app (the catalogue or home page). A larger app suite may have several entry points (a public landing page, an admin home, a partner portal). In all cases, the registry contents = transitive closure of reachable apps. The user never maintains a separate enumeration.

No classpath scanning, no annotations, no static state. The dependency graph is the registry. IDE refactor tools see every reference because every reference is a normal Java import.

### 3.5 Importing an app's link

```java
public record DemoCatalogue() implements AppModule<DemoCatalogue> {
    @Override public ImportsFor<DemoCatalogue> imports() {
        return ImportsFor.<DemoCatalogue>builder()
            .add(new ModuleImports<>(
                List.of(new PitchDeck.link(),
                        new WonderlandDemo.link(),
                        new TurtleDemo.link()),
                /* importing modules list */))
            .add(/* CSS imports etc. */)
            .build();
    }
}
```

Same `ImportsFor` mechanism as every other Exportable. No new import API surface.

### 3.6 Declaring parameters

```java
public record ProductDetail() implements AppModule<ProductDetail> {
    public record Params(String productId, String tab) {}

    public static final ProductDetail INSTANCE = new ProductDetail();

    public record link() implements AppLink<ProductDetail> {}
    record appMain() implements AppModule._AppMain<ProductDetail> {}

    @Override public Class<Params> paramsType() { return Params.class; }
    @Override public String title() { return "Product Detail"; }
    // imports/exports as usual
}
```

### 3.7 Declaring a proxy app for an external destination

```java
public record GitHubProxy() implements ProxyApp<GitHubProxy> {

    public record Params(String repo, Optional<String> path) {}
    public record link() implements AppLink<GitHubProxy> {}

    public static final GitHubProxy INSTANCE = new GitHubProxy();

    @Override public String simpleName() { return "github"; }
    @Override public Class<Params> paramsType() { return Params.class; }

    @Override public String urlTemplate() {
        return "https://github.com/{repo}/{path?}";
    }
}
```

`ProxyApp<P extends ProxyApp<P>>` interface:

```java
public interface ProxyApp<P extends ProxyApp<P>> extends Linkable {

    /** URL template with {param} interpolation. See §3.8. */
    String urlTemplate();

    /** Default kebab-case from class name; override to lock the public name. */
    @Override default String simpleName() {
        return AppModule.defaultSimpleName(this.getClass());
    }
}
```

A proxy app:
- **Has no JS file, no CSS, no SVG, no `imports()`, no `exports()`.** It is metadata only.
- **Has a `Params` record** declaring its typed parameters (same shape rules as AppModule params, §4.3).
- **Has a `urlTemplate()`** parsed by the writer at compile time.

### 3.8 URL template syntax

A small declarative DSL parsed by the writer:

| Syntax | Meaning |
|---|---|
| `{name}` | Required interpolation of param `name`. URL-encode the value. |
| `{name?}` | Optional interpolation. Omit the segment if the param is absent (`Optional.empty()` or `null`). |
| `{name?:default}` | Optional with a literal default value if absent. |
| Anything else | Literal text. |

Examples:

| Template | Params | Generated URL |
|---|---|---|
| `https://github.com/{repo}` | `(repo="acme/proj")` | `https://github.com/acme%2Fproj` |
| `https://github.com/{repo}/{path?}` | `(repo="acme/proj", path=Optional.empty())` | `https://github.com/acme%2Fproj/` |
| `https://github.com/{repo}/{path?}` | `(repo="acme/proj", path=Optional.of("README.md"))` | `https://github.com/acme%2Fproj/README.md` |
| `https://docs.example.com/{section}#{anchor?:top}` | `(section="api", anchor=...)` | `https://docs.example.com/api#top` (or anchor value) |

**Out of scope for v1 (deferred):** query-string templating (`?key={val}`), nested record interpolation, conditional path segments. If a proxy needs more than the DSL provides, declare a separate proxy with a different template.

---

## 4. Detailed design — generated JS

### 4.1 `nav` object — for AppModules that import other apps' links

For each `AppLink<?>` in the importing module's import list, the writer adds an entry to a frozen `nav` object generated at the top of the compiled JS:

```js
// generated header in DemoCatalogue.js
const nav = Object.freeze({
    PitchDeck:      function(p) { return _homingBuildAppUrl("pitch",          p); },
    WonderlandDemo: function(p) { return _homingBuildAppUrl("wonderland",     p); },
    TurtleDemo:     function(p) { return _homingBuildAppUrl("turtle-demo",    p); }
});

function _homingBuildAppUrl(simpleName, params) {
    var u = "/app?app=" + encodeURIComponent(simpleName);
    if (params) for (var k in params) {
        if (params[k] != null) u += "&" + encodeURIComponent(k) + "=" + encodeURIComponent(String(params[k]));
    }
    var here = new URLSearchParams(window.location.search);
    if (here.get("theme")  && (!params || params.theme  == null)) u += "&theme="  + encodeURIComponent(here.get("theme"));
    if (here.get("locale") && (!params || params.locale == null)) u += "&locale=" + encodeURIComponent(here.get("locale"));
    return u;
}
```

**Design choices** (PROPOSED, may change):

- **JS identifier = simple Java class name** of the target (`nav.PitchDeck`), not the URL simple name. Tracks Java refactors. The URL simple name is a generated value inside the function.
- **Function-style** — encapsulates URL construction and theme/locale propagation. Most navigation should preserve current params; explicit overrides win.
- **Frozen object** — prevents accidental mutation.
- **Helper namespaced with `_Homing` prefix** to avoid collisions with user code.

### 4.0.1 The injected `href` manager

For any DomModule that imports an `AppLink<?>`, the kernel auto-injects a runtime `href` helper into the module's JS scope, in the same way `css` is injected today for modules with CSS imports. The helper exposes a small, fixed API and is the **only** sanctioned way to construct, set, or follow URLs in user code.

```js
// Available in every DomModule that imports an AppLink<?>.
//
// All methods accept a Link — i.e., the value returned by nav.X({...params...}).
//
href.toAttr(link)             // → 'href="/app?app=...&..."'  — attribute fragment for innerHTML
href.set(el, link)            // sets el's href; returns el
href.create(link, opts?)      // returns a new <a> element with href set; opts: { text, className, target }
href.openNew(link, opts?)     // window.open(link, "_blank", opts?.windowFeatures)
href.navigate(link, opts?)    // window.location.assign(link); opts.replace=true → .replace()
href.fragment(slug)           // → 'href="#<slug>"'           — same-page anchors must use this
```

Any future first-party API for URL handling (history pushState integration, instrumentation, intercept hooks) lives here. The single chokepoint means future additions are non-breaking.

Examples (the only patterns user code is allowed to write):

```js
// innerHTML construction:
parent.innerHTML =
    '<a ' + href.toAttr(nav.PitchDeck()) + ' class="' + cn(card) + '">Open</a>';

// DOM creation:
const a = href.create(nav.PitchDeck({slide: 7}), { text: "Open the deck", className: cn(card) });
parent.appendChild(a);

// Direct property assignment:
href.set(myAnchor, nav.PitchDeck());

// Programmatic navigation (e.g., in a save handler):
href.navigate(nav.ProductDetail({productId: savedId}));

// Open in a new tab:
href.openNew(nav.GitHubProxy({repo: "acme/proj"}));

// Same-page anchor:
parent.innerHTML = '<a ' + href.fragment("section-2") + '>Jump</a>';
```

Note: `nav.X({...})` continues to return a URL string, exactly as in §4.1. The `href` manager is the gateway for *operations on* those URLs. Returning a string from `nav` keeps it useful for cases where the URL needs to leave the framework's control (sharing, copy-to-clipboard, fetch — though those are out of scope for the conformance scanner).

### 4.1.1 Generated entries for proxy app targets

When the imported `link()` belongs to a `ProxyApp`, the writer parses its `urlTemplate()` at compile time and generates a function that constructs the external URL by interpolation:

```js
// generated, given GitHubProxy with template "https://github.com/{repo}/{path?}"
const nav = Object.freeze({
    PitchDeck:    function(p) { return _homingBuildAppUrl("pitch", p); },     // internal app
    GitHubProxy:  function(p) {                                              // proxy
        var u = "https://github.com/" + encodeURIComponent(p.repo);
        if (p.path != null) u += "/" + encodeURIComponent(p.path);
        return u;
    }
});
```

From the JS author's perspective, internal apps and proxy apps are indistinguishable — both are `nav.X(params)` calls returning a URL string. The conformance scanner treats both as approved navigation sources.

### 4.2 `params` const — for AppModules with declared parameters

For any AppModule whose `paramsType()` is non-Void, the writer generates a `params` const at the top of the compiled JS, populated from the page URL:

```js
// generated header in ProductDetail.js (for Params(String productId, String tab))
const params = (function() {
    var sp = new URLSearchParams(window.location.search);
    return Object.freeze({
        productId: sp.get("productId") || "",
        tab:       sp.get("tab")       || ""
    });
})();
```

The user's `appMain` reads `params.productId`, `params.tab` directly. No `appMain` signature change.

### 4.3 Type coercion for params

PROPOSED rules — the writer generates type-appropriate parsing per record component:

| Java type | Generated JS parse | Default if missing |
|---|---|---|
| `String` | `sp.get(name) \|\| ""` | empty string |
| `int` / `Integer` | `parseInt(sp.get(name), 10)` | `0` (or `null` for `Integer`) |
| `long` / `Long` | `parseInt(sp.get(name), 10)` (consider BigInt for >2^53) | `0` |
| `double` / `Float` | `parseFloat(sp.get(name))` | `0.0` |
| `boolean` / `Boolean` | `sp.get(name) === "true"` | `false` |
| `Optional<T>` | `sp.has(name) ? <coerce>(sp.get(name)) : undefined` | `undefined` |
| `List<T>` | `sp.getAll(name).map(<coerce>)` | `[]` |
| Enum | `validateEnum(sp.get(name), [...allowedNames])` | `null` |
| Record (nested) | OPEN — see §9 |

### 4.4 Where the generated headers go

Both the `nav` object and the `params` const are written **after** the imports and **before** the user's JS content. The generated section is clearly demarcated:

```js
import { ... } from "...";

// === homing generated — do not edit ===
const nav = Object.freeze({ ... });
const params = (function() { ... })();
// === end generated ===

function appMain(rootElement) {
    // user's code, using nav.X(...) and params.x
}

export { ... };
```

---

## 5. URL contract

| Pattern | Meaning |
|---|---|
| `/app?app=pitch` | Open the app registered under `pitch` |
| `/app?app=product-detail&productId=P-12345&tab=reviews` | Open `product-detail` with typed params |
| `/app?app=pitch&theme=dracula&locale=en` | Theme & locale propagate as before |
| `/module?app=...` | OPEN — see §9 |
| `/css?app=...` | OPEN — see §9 |

### 5.1 Reserved keys

`app`, `theme`, `locale`. The writer rejects (at compile time) any `Params` record whose component name collides with these.

### 5.2 Backwards compatibility — `?class=...`

PROPOSED: drop entirely. Project is pre-adoption; the legacy URL contract has no users to protect. One contract, no legacy surface.

ALTERNATIVE (if any URLs are already in circulation): keep `?class=...` as a deprecated alias that resolves through `resolveByClass()` and warns in dev mode.

---

## 6. Type-safety chain & enforcement

### 6.1 The compile-and-link chain

| Step | Where | Failure mode if broken |
|---|---|---|
| Linkable target has a `link` record | Java | Compile error in any importing module |
| Importer references `Target.link` | Java | Compile error if `Target` renamed/removed |
| Generated `nav.Target` matches the import | Writer-generated | No drift possible — writer derives from metadata |
| Importer's `AppLink<Target>` import → `Target` enters the registry | Boot-time transitive walk | Boot fails fast if collisions detected in the closure |
| JS code calls `nav.Target(...)` | Runtime | `TypeError` if writer is broken; otherwise correct URL |
| Server resolves `?app=<simpleName>` | Runtime | 404 if name not registered, 404 if name is a proxy (proxies aren't routable) |

**The registry contents are derived, not maintained.** Adding a new linkable target requires only:

1. Declare the target's `link()` record.
2. Import that link in any reachable consumer.

Both are normal Java edits. The registry updates automatically at next boot. There is no parallel registration list to keep in sync; the import graph **is** the registration list.

### 6.2 Conformance enforcement — every `href` flows through the manager

The compile-and-link chain only delivers safety if **every** href operation in user JS goes through the injected `href` manager. The conformance test enforces a single uniform rule, mirroring how `css.*` discipline works today.

```java
class StudioHrefConformanceTest extends HrefConformanceTest {
    @Override
    protected List<DomModule<?>> domModules() {
        return List.of(/* every DomModule in the project */);
    }
    @Override
    protected Set<Class<? extends DomModule<?>>> allowList() {
        return Set.of(/* explicitly-allowed exceptions, each justified inline */);
    }
}
```

#### The rule

> **The literal substring `href` may appear in DomModule JS only as the manager identifier — i.e., immediately followed by `.` and a recognised manager method (`href.toAttr`, `href.set`, `href.create`, `href.openNew`, `href.navigate`, `href.fragment`).** Every other appearance is forbidden.

This is intentionally absolute. There is no "but it's followed by a `nav.X()`" carve-out. There is no "but fragments are not navigation" carve-out — same-page anchors use `href.fragment("slug")`. There is no "but it's a `mailto:`" carve-out — those are proxy apps (§9.11). The CSS scanner works the same way: there is no exception for "but the class name happens to be a string." Uniform discipline is the point.

#### Forbidden patterns (regex specification)

The scanner rejects any of:

| Pattern | What it matches |
|---|---|
| `\bhref\s*=` | Literal `href=` in HTML strings, `el.href = ...` assignments |
| `\.href\b` | Property access `.href` on any element |
| `["']href["']` | The string `"href"` (typically as `setAttribute("href", …)` argument) |
| `\bwindow\.location\b` | All `window.location.*` mutations and reads |
| `\bwindow\.open\s*\(` | Bypasses the manager — use `href.openNew(...)` |
| `setAttribute\s*\(\s*["']href` | Belt-and-braces, in case the previous patterns miss something |

#### Allowed pattern (the only one)

| Pattern | What it matches |
|---|---|
| `\bhref\s*\.\s*(toAttr\|set\|create\|openNew\|navigate\|fragment)\s*\(` | The injected manager identifier with a recognised method |

Anything in JS that is not one of these forbidden patterns AND not the allowed pattern simply does not contain the substring `href`. That is: any source line that includes the four characters `href` must either be the manager method call or be removed.

#### Comments and doc strings

The scanner ignores `// ...` line comments, `/* ... */` block comments, and JSDoc. References to `href` in documentation are fine; the scanner only inspects executable code.

#### What this guarantees

Together with §6.1's compile-and-link chain and the proxy-app design:

- **Every URL in the rendered DOM** is the return value of a `nav.X(...)` call passed through the `href` manager.
- **Every `nav.X`** is bound to a Java-declared `Linkable` (AppModule or ProxyApp) explicitly imported by the consuming module.
- **Renaming, removing, or restructuring** any link target propagates through `javac` to every consumer; CI catches any user-side bypass.
- **Every external destination** (GitHub, vendor APIs, mailto/tel) is a typed Java declaration somewhere in the codebase. There is no place a stray URL string can hide.
- **Future cross-cutting URL behaviour** (analytics instrumentation, history pushState, intercept hooks for opt-in client-side routing) lives in one place — the `href` manager. No user code changes when these land.

The CSS-discipline analogy completes itself: just as no Homing UI ever sets `el.className = "btn-primary"`, no Homing UI ever writes `el.href = "/some/url"`. Both go through their respective injected managers, and CI verifies it on every commit.

Renaming `PitchDeck` to `Deck`:
1. The `link()` record moves with the class.
2. Every importing module's `new PitchDeck.link()` now reads `new Deck.link()` — `javac` error at every importer.
3. Fix the imports (mechanical).
4. Regenerated JS produces `nav.Deck` instead of `nav.PitchDeck`.
5. JS authors update `nav.PitchDeck` references to `nav.Deck`.
6. URL stays `pitch-deck` if `simpleName()` was overridden, or changes to `deck` if relying on the default — author's choice.

The Java rename and the URL contract are decoupled. Either can change without forcing the other.

---

## 7. Backwards compatibility (source-level)

Existing AppModules require **no changes**. Default behavior:

- `simpleName()` defaults to kebab-case of class name → existing apps get sensible URLs automatically.
- `paramsType()` defaults to `Void.class` → no `params` const generated; no behavior change.
- Apps that don't import any `AppLink<?>` get no `nav` const generated.
- Apps that don't declare a `link()` record cannot be linked to (compile-time enforced) but otherwise work normally.

The opt-in surface is incremental — apps adopt the new features as they need them.

---

## 8. Edge cases

| Case | Resolution |
|---|---|
| Two AppModules with the same `simpleName()` | `SimpleAppResolver` constructor throws `IllegalStateException` listing the colliding pair. |
| Request for `?app=unknown` | 404. In dev mode, response body lists available app names. In prod, opaque message. |
| Param record component named `app` / `theme` / `locale` | Compile-time error from the writer (reserved key collision). |
| Param value contains characters needing URL encoding | Writer always applies `encodeURIComponent` on construction; `URLSearchParams.get()` decodes on parse. |
| Optional param missing from URL | Coerces to `undefined` (per §4.3 table). |
| Multi-value list param with no occurrences | Empty array, not `null`. |
| Theme/locale explicitly passed in `nav.X({theme: "..."})` | User's value wins; auto-propagation skipped for that key. |
| `appMain` declared with extra params (legacy code) | No change — the writer-generated `params` is a sibling const, not an argument. |
| AppModule imported as a CSS/SVG/`_Constant` dependency but **not** as `AppLink` | Imported app is **not** auto-registered. Only `AppLink<?>` imports trigger registration. Code-level dependencies do not become URL-reachable. |
| Cycle in `AppLink<?>` imports (A links to B, B links to A) | Registered correctly — visited set prevents recursion; both apps end up in the registry once. |
| AppModule exists in the codebase but no entry app reaches it | **Not** registered. Reachable URL surface = transitive closure from entry apps only. To expose, either add as entry app or have something link to it. |
| Two paths reach the same app (diamond dependency: A→B→D, A→C→D) | Registered once; visited set deduplicates. |
| User wants an app registered but **not** routable from outside (internal-only target of programmatic navigation) | OPEN — see §9.8 |
| Direct request to `/app?app=<proxy-name>` | 404. Proxy apps are URL builders, not navigation targets. The resolver knows which kind a name is and rejects routing for proxies. |
| Two proxy apps with the same `simpleName` | Boot-time collision error, same as for AppModules. |
| AppModule and ProxyApp share the same `simpleName` | Boot-time collision error. The simple-name namespace is shared across both kinds. |
| ProxyApp template references a param not in `Params` record | Compile-time error from the writer (template-vs-record mismatch). |
| ProxyApp template uses required `{name}` for an `Optional` param | Compile-time error from the writer (require `{name?}` for optional). |
| Same-page fragment anchor | **Use `href.fragment("slug")`.** Literal `<a href="#…">` is forbidden — same uniform rule. |
| Programmatic "open in new tab" | **Use `href.openNew(nav.GitHubProxy({…}))`.** Direct `window.open(…)` is forbidden. |
| Inline `<a target="_blank">` to a proxy app | `'<a ' + href.toAttr(nav.GitHubProxy({…})) + ' target="_blank">'`. The literal `href` substring never appears in user code. |
| `mailto:` / `tel:` / `sms:` links | **Modeled as built-in proxy apps.** `href.toAttr(nav.Mailto({to: "user@example.com"}))`. See §9.11. |
| User legitimately needs to read `el.href` (not write) | Forbidden by the scanner. If a real use case appears, add `href.read(el)` to the manager API rather than weakening the rule. |
| Third-party widget code that the framework cannot enforce against | Out of scope for the scanner — it only inspects DomModule resources Homing owns. Wrap the widget in a `DomModule` that does its href operations through `href.X` and treat the widget itself as opaque. |

---

## 9. Open questions

These are explicitly deferred decisions. Resolve them through amendment to this RFC.

### 9.1 Should `?app=...` apply to `/module` and `/css` endpoints too?

Currently the kernel uses `?class=...` consistently across `/app`, `/module`, `/css`, `/css-content`. If we change the AppModule URL key to `?app=...`, what about modules that aren't AppModules (regular EsModules, CssGroups, SvgGroups)?

Options:
- **(a)** Apply `?app=` only to `/app`. Other endpoints still use `?class=...`. Mild inconsistency.
- **(b)** Generalize to `?name=...` everywhere with a registry per type. More invasive, possibly cleaner.
- **(c)** Generalize the simple-name idea to all module types. Largest change; most consistent.

**Recommendation:** start with (a); revisit later if it feels inconsistent in practice.

### 9.2 Nested record types in `Params`

If `Params` includes a record component that is itself a record (e.g., `record Params(String id, Pagination page)`), how does the writer parse it from URL keys?

Options:
- Forbid in v1; only flat scalar / enum / `Optional` / `List` allowed.
- Flatten with dotted keys (`?page.size=20&page.cursor=abc`).
- JSON-encode the nested value.

**Recommendation:** forbid in v1; revisit if real demand surfaces.

### 9.3 Should `simpleName()` be allowed to contain slashes?

For path-style URLs like `/app?app=admin/users/list`. Tempting for traditional MPA hierarchies, but conflates two concerns (registry key vs URL path).

**Recommendation:** disallow in v1. Simple names are flat identifiers.

### 9.4 Per-deployment vs per-process registry

The `SimpleAppResolver` is bound to one server. Two questions:
- Should there be a way to merge resolvers (e.g., for plugin-style architectures)?
- Should app simple names be globally unique across multi-server deployments, or per-server?

**Recommendation:** per-server in v1. Federation is a future-feature problem.

### 9.5 Should `nav.X(...)` return an object instead of a string?

Today: `nav.X({...})` returns a URL string.

Alternative: `nav.X({...})` returns `{ url, simpleName, params, navigate() }` — richer object with helpers like `navigate()` (calls `window.location.assign`) and `open()` (calls `window.open`).

**Recommendation:** start with strings (simpler, explicit); add a richer return type later if patterns emerge.

### 9.6 Server-side prerendering hooks

For SEO / first-paint optimization, traditional MPAs sometimes want server-side rendering. Should the registry expose a hook for "render this app's initial HTML on the server"?

**Recommendation:** out of scope for v1. The framework's value here is in URL stability and typed params, not SSR. Defer until there's a concrete use case.

### 9.7 `?class=...` deprecation strategy

See §5.2. Pre-adoption status suggests dropping cleanly; if there are early adopters, a deprecation period may be needed.

**Recommendation:** drop entirely. Decide at implementation time based on actual adoption state.

### 9.8 Should there be an opt-out from auto-registration?

Transitive registration via `AppLink<?>` imports is the default. But there may be valid cases where an app *imports* `Target.link()` for typed link construction yet does not want `Target` to be reachable as a top-level entry.

Example: a "preview-only" `EditMode` link that should only be reachable via internal redirect from a save handler, never from a bookmarkable URL.

Options:

- **(a)** No opt-out. Every linked app is reachable. Simpler model; users who need this can implement gating in `appMain` (check a session token, etc.).
- **(b)** A `@Hidden` marker (annotation or method override) on `AppModule` that excludes it from registration even if linked. The link function still works; the app responds with 404 unless reached via internal routing.
- **(c)** A separate `InternalAppLink<M>` flavor that imports the URL builder but does *not* trigger registration. Explicit, type-visible.

**Recommendation:** start with (a). If real demand surfaces, add (c) as the typed escape hatch — an `InternalAppLink<M>` that exists alongside `AppLink<M>` but skips the registration walk. Keeps the simple case simple while preserving an honest path for the unusual case.

### 9.9 Does the resolver need to be queried at link-construction time?

Currently the writer bakes the simple name into the generated `nav.Target` function at compile time. This means changing `simpleName()` requires recompilation of every consuming module.

Alternative: generate `nav.Target = function(p) { return _HomingResolveAppUrl("Target", p); }` and let the runtime helper look up the simple name via a server-provided manifest.

- **Compile-time baking** (current) — simpler, no runtime dependency, but `simpleName()` changes require recompile of consumers.
- **Runtime resolution** — flexible (theme-style hot reloads, A/B routing, plugin overrides), but adds a runtime indirection and a manifest-fetch step.

**Recommendation:** compile-time baking in v1. Revisit only if real demand for runtime flexibility appears.

### 9.10 Loosening `Exportable<M extends EsModule<M>>`

`AppLink<L extends Linkable>` extends `Exportable`, but `Linkable` does not extend `EsModule` (proxy apps have no JS module). The current `Exportable<M extends EsModule<M>>` bound prevents this.

Options:

- **(a)** Drop the bound on `Exportable<M>` to allow any type. Simplest; small loss of compile-time information.
- **(b)** Split `Exportable` into `ModuleExportable<M extends EsModule<M>>` (current behavior) and `LinkableExportable<L extends Linkable>` (new). Two parallel type families.
- **(c)** Promote a more general `Anchorable` supertype that both `EsModule` and `Linkable` extend. Cleanest; biggest refactor.

**Recommendation:** start with (a). The bound was protective rather than essential; removing it is a one-line change. Revisit if a more rigorous taxonomy becomes valuable.

### 9.11 Built-in proxy apps for `mailto:`, `tel:`, `sms:`, fragment

Under the stricter §6.2 rule, every `href` use goes through the manager — including `mailto:`, `tel:`, fragments, etc. There are no scheme-based escape hatches. To make these ergonomic, the kernel ships **built-in proxy apps** plus the `href.fragment()` shortcut.

Built-ins (in `homing-core`):

| Proxy | Params | Template |
|---|---|---|
| `Mailto` | `(String to, Optional<String> subject, Optional<String> body, Optional<List<String>> cc, Optional<List<String>> bcc)` | `mailto:{to}` plus query-encoded options |
| `Tel`    | `(String number)` | `tel:{number}` |
| `Sms`    | `(String number, Optional<String> body)` | `sms:{number}` plus body |

Usage matches any other proxy: `href.toAttr(nav.Mailto({to: "user@example.com", subject: "Hi"}))`.

For same-page fragment anchors, the manager exposes `href.fragment(slug)` directly — no proxy needed (fragments are not "navigation" in the Linkable sense; they are intra-document anchors).

**Recommendation:** ship the built-ins; document them prominently in the user guide alongside how to declare project-specific proxies (organisation directories, internal vendor links, etc.).

### 9.12 Should the conformance scanner be strict by default for new modules?

Existing demos may have raw hrefs that need migration. Two approaches to roll-out:

- **(a)** Scanner enforces strictly across all DomModules from day one; migration must complete before merging.
- **(b)** Scanner has an explicit `legacyAllowList` that grandfathers in modules with known violations; migrate them progressively.

**Recommendation:** (a) for this codebase, since it's pre-adoption and the demo set is small. For larger downstream codebases adopting Homing later, (b) becomes important — recommend a `legacyAllowList()` method on the conformance test class for that case.

---

## 10. Migration path

### 10.1 Kernel changes

1. Add `Linkable` sealed supertype to `homing-core`.
2. Add `AppLink<L extends Linkable>` sealed interface to `homing-core`.
3. Add `ProxyApp<P extends ProxyApp<P>>` interface to `homing-core`, including the URL template parser.
4. Add default `simpleName()` and `paramsType()` methods to `AppModule`.
5. Add `defaultSimpleName(Class<?>)` static helper.
6. Add `SimpleAppResolver` to `homing-core` (registers AppModules and ProxyApps in separate maps; see §11).
7. Loosen `Exportable<M extends EsModule<M>>` per §9.10.
8. Extend `EsModuleWriter` to emit `nav` const for `AppLink<?>` imports — handling both AppModule and ProxyApp targets.
9. Extend `EsModuleWriter` to emit `params` const for non-Void `paramsType()` (any Linkable, including proxies — though proxies don't use `params` themselves).
10. Update `JapjsActionRegistry` to accept a `SimpleAppResolver` and dispatch `?app=` only to AppModule entries (404 for proxy names).
11. Ship built-in proxies for common non-HTTP schemes — `Mailto`, `Tel`, `Sms` (per §9.11 recommendation).

### 10.2 Conformance & manager changes

12. Add the injected `href` manager to the JS bootstrap (mirroring how `css` is injected today). Implement the six methods: `toAttr`, `set`, `create`, `openNew`, `navigate`, `fragment`.
13. Add `HrefConformanceTest` base class to `homing-conformance`, mirroring the existing `CssConformanceTest`.
14. Implement the single-rule scanner per §6.2 (one allowed pattern, six forbidden patterns).
15. Wire `DemoHrefConformanceTest` in `homing-demo` and `StudioHrefConformanceTest` in `homing-studio` covering all DomModules.

### 10.3 Demo migrations

For each existing AppModule:

- Default `simpleName()` is fine for most; override only where the URL contract should be locked (e.g., `PitchDeck` → `pitch` if the slide deck URL needs to be short).
- Add `link()` record for any app that should be linkable.
- Migrate `DemoCatalogue.java` to import the `link()` records of every demo it lists; update `DemoCatalogue.js` to use `nav.X()` calls instead of string-concatenated URLs.
- Update `WonderlandDemoServer.java` to construct `SimpleAppResolver` from a single entry app (`DemoCatalogue.INSTANCE`); transitive registration handles the rest.

The migration validates the transitive-registration design end-to-end: the catalogue imports every demo's `link()`, every demo registers automatically, the URL surface is the catalogue's import list.

### 10.4 Documentation

- Update the main `README.md` with the new URL contract.
- Add a "Linking between apps" section to `docs/user-guide.md`.
- Add a "Modeling external destinations as proxy apps" section to `docs/user-guide.md`.
- Update the white paper §4.5 (Layer 4 — Workspace) to mention typed nav as a kernel-level capability.
- Update the brochure §02 / §03 to add MPA support and conformance enforcement as positioning points.
- Reference the new conformance test from the existing CSS conformance docs as a sibling discipline.

---

## 11. Implementation notes

### 11.1 Where `SimpleAppResolver` lives

OPEN: should it be in `homing-core` (so any host adapter can use it) or `homing-server` (since it's a server concern)?

**Recommendation:** `homing-core`. It's a pure data structure — no Vert.x, no HTTP. Adapters in `homing-server`, future `Homing-spring-boot`, etc., all need it.

### 11.2 Writer extension complexity

Both new generated headers (`nav`, `params`) are pure transformations of import metadata. No new source-of-truth, no new files to read. Writer changes are localized to two new methods:

- `EsModuleWriter.writeNavConst(...)` — emits the `nav` block when any `AppLink<?>` is in the import set.
- `EsModuleWriter.writeParamsConst(...)` — emits the `params` block when the module is an `AppModule` with non-Void `paramsType()`.

### 11.3 Testing

- Unit tests for `defaultSimpleName(Class<?>)` with edge cases (acronyms, single-letter classes, etc.).
- Unit tests for `SimpleAppResolver` collision detection.
- Integration test: end-to-end `nav.X({...})` URL generation for a sample param shape.
- Integration test: `params` const parsing for each scalar type.
- Conformance test: verify no AppModule's compiled JS contains hand-concatenated `/app?` URLs.

---

## 12. Estimated effort

| Task | Effort |
|---|---|
| Kernel interfaces + helpers (`Linkable`, `AppLink`, `ProxyApp`, `simpleName()`, `paramsType()`, `SimpleAppResolver`) | 3 hours |
| URL template parser for `ProxyApp.urlTemplate()` | 2 hours |
| `EsModuleWriter` extensions (nav for both kinds, params, proxy template emit) | 4 hours |
| `JapjsActionRegistry` changes (`?app=` dispatch, 404 for proxies) | 1 hour |
| Built-in proxies (`Mailto`, `Tel`, `Sms`) | 1 hour |
| Injected `href` manager — JS-side runtime + bootstrap injection | 2 hours |
| `HrefConformanceTest` + single-rule scanner | 2 hours (simpler than the prior multi-pattern design) |
| Tests (unit + integration + conformance) | 3 hours |
| Demo / studio migration (every DomModule to `href.X(nav.Y(...))`) | 3 hours |
| Documentation updates | 1 hour |
| **Total** | **~22 engineer-hours / 2.5–3 working days** |

Marginal increase over Amendment 2's estimate: the added href-manager work (~2 hours) is mostly offset by the simpler scanner (single rule vs the prior multi-pattern heuristic). Still within Phase 0 scope; can land alongside the Spring Boot adapter.

---

## 13. Revision log

| Date | Change | Author |
|---|---|---|
| 2026-05-01 | Initial draft from design conversation | Howard, with Homing |
| 2026-05-01 | **Amendment 1 — transitive registration.** Resolver now walks `AppLink<?>` import edges from a small set of entry apps; full app graph is discovered automatically. §2.2, §3.3, §3.4, §6, §8 updated. New open question §9.8 (opt-out from auto-registration) and §9.9 (compile-time vs runtime simple-name resolution) added. | Howard, with Homing |
| 2026-05-01 | **Amendment 2 — conformance enforcement & proxy apps for external destinations.** Introduces `Linkable` supertype, `ProxyApp` interface, `urlTemplate()` DSL, and built-in proxies for `mailto:` / `tel:` / `sms:`. New conformance test `HrefConformanceTest` forbids raw href usage; all navigation must flow through `nav.X(...)`. §2.4 updated, new §2.5 (proxy apps) and §2.6 (conformance), §3.2 broadened, new §3.7 (declaring proxies) and §3.8 (URL template syntax), new §4.1.1 (proxy-target nav), §6 restructured into §6.1 (chain) + §6.2 (enforcement), §8 expanded with proxy and conformance edge cases, new open questions §9.10 (Exportable bound), §9.11 (mailto/tel proxies), §9.12 (legacy allow-list rollout). Effort estimate doubled to ~2–3 working days. | Howard, with Homing |
| 2026-05-01 | **Amendment 3 — strict CSS-style discipline via the injected `href` manager.** Replaces the multi-pattern allowed/forbidden table from Amendment 2 with a single uniform rule mirroring how `css.*` works: every href operation flows through the injected `href` manager (`href.toAttr`, `href.set`, `href.create`, `href.openNew`, `href.navigate`, `href.fragment`); the literal substring `href` may appear in user JS only as the manager identifier. No carve-outs for fragments, concatenation, or `window.open`. §2.6 strengthened, new §4.0.1 (the manager API), §6.2 rewritten with the single-rule scanner specification, §8 edge cases updated to remove now-stale "concatenation OK" rows, §9.11 reframed (built-in proxies + `href.fragment()`), §10.2 expanded with manager implementation. Marginal effort delta (manager implementation offset by simpler scanner). | Howard, with Homing |
| | *(amendments to follow)* | |

---

## 14. References

- [Main white paper §4.5 Workspace](#ref:whitepaper) — context for typed cross-tier contracts
- Action plan Phase 0 — where this work slots in
- Session summary — broader design context

---

## 15. Discussion notes

Captured here for future amendments. Add new notes inline; don't delete prior ones.

> *2026-05-01:* Initial design conversation — proxy-singleton pattern proposed by Howard, expanding the simpler `?class=` proposal. Key insight: URL stability across Java refactors is a first-class feature for traditional MPAs, not a nice-to-have. The decoupling of `simpleName()` (URL contract) from Java class identity (refactor surface) is the central design move. The cross-app `link()` import remains the type-safety mechanism — registration alone does not authorize linking; the import does.

> *2026-05-01 (amendment 1):* Transitive registration via `AppLink<?>` imports proposed by Howard. The original explicit-list registration was redundant — the import graph already encodes which apps are reachable. By walking only `AppLink<?>` edges (not all imports), the registry stays semantically aligned with "navigable surface" rather than "code-level dependency surface." This also matches how every other dependency in Homing (CSS, SVG, modules) is resolved transitively. The user enumerates entry points; the framework discovers everything else. Notable consequence: orphan apps (in the codebase but not reachable from any entry) are automatically excluded from the URL surface — a healthy default that prevents accidentally exposing unfinished or internal apps.

> *2026-05-01 (amendment 2):* Conformance enforcement (no raw `href` in JS) and the proxy-app modeling for external destinations proposed by Howard. The two go together: enforcement without proxy apps would require an arbitrary "external URLs are special" escape hatch that erodes discipline; proxy apps without enforcement would be optional and inconsistently adopted. Together, they form a complete story — every URL in the rendered DOM comes from a typed Java declaration somewhere in the codebase, internal apps and external destinations alike. The proxy-app concept also surfaces an unexpected payoff: third-party URLs (GitHub, docs sites, vendor APIs) become first-class refactor targets. A vendor changes their URL scheme? Update the proxy template; every consumer adapts at next build. This is meaningfully better than the typical SPA pattern where third-party URLs accumulate as untracked string literals across the codebase. The `Linkable` supertype is the small piece of type machinery that lets internal and external links share a single `AppLink<?>` import mechanism — looser than the original `AppLink<M extends AppModule<M>>` but not loose enough to lose type safety. Side note: this amendment doubles the estimated implementation effort and adds three new open questions, but also resolves a long-standing weakness (untyped external URLs) that was not even called out as a problem in the original RFC.

> *2026-05-01 (amendment 3):* Strict CSS-style discipline proposed by Howard. Amendment 2's allowed/forbidden table — concatenation OK, template-literal OK, `window.open(nav.X())` OK, fragments OK — was a softer enforcement than the css analog. Howard's observation: align the two. Just as `css.setClass(...)` is the only sanctioned class manipulation, `href.X(...)` should be the only sanctioned href manipulation. The conformance scanner collapses to a single rule: the literal substring `href` may appear only as the manager identifier. No exceptions, no carve-outs, no heuristic regex. The cost is one new injected manager (~2 hours) and one user-visible API surface to learn (`href.toAttr`, `href.set`, `href.create`, `href.openNew`, `href.navigate`, `href.fragment`). The benefit is dramatic: the scanner is trivially correct (one regex match vs the prior six-pattern heuristic), the rule is uniform with css discipline, and there's a single chokepoint for any future cross-cutting URL behavior (analytics, history pushState, intercept hooks). Same-page fragments and `mailto:` / `tel:` / `sms:` — the cases where Amendment 2 still allowed scheme-based escape hatches — also collapse cleanly: fragments use `href.fragment("slug")`; non-HTTP schemes are first-class proxy apps shipped with the kernel. The discipline is now genuinely uniform end-to-end. This amendment also clarifies a meta-principle for the RFC family: **when an enforcement rule has carve-outs, the rule is wrong; collapse the carve-outs into the design instead.** Same-page anchors aren't navigation, but they are href-attribute writes, and unifying them under the manager is cleaner than scanner exceptions.
