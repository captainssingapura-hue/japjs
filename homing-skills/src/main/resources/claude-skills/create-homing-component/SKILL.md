---
name: create-homing-component
description: Use this skill when the user wants to create a customised view-layer UI component for a Homing studio — a card variant, a status widget, a progress indicator, a custom list row, etc. **MPA only**: the framework's current chrome serves each AppModule as its own page; SPA-style components (with reactive state, view diffing, unmount cleanup) need disciplines this skill does not cover. The skill leads with composition (reuse Card/ListItem/Section/Listing), then extension (typed CssClass over existing markup), then build-new only when the first two paths can't work. Triggers — "add custom component", "custom card variant", "new status widget", "build a UI piece", "compose a new view block", "extend an existing builder". Skip if the user wants a whole new page kind (use `create-homing-studio` for AppModule scaffolding), purely visual restyle of existing chrome (use `create-homing-theme`), or any SPA-shaped state-aware component (out of scope for this skill).
---

# Create a Homing Component (MPA, view-layer only)

A Homing **component** is a function that takes a typed props object and returns a DOM `Node`. It runs once per page render — *not* re-rendered, *not* memoised, *not* reactive. The browser tears the tree down when the user navigates; the next page render builds fresh.

This is the **MPA model**. Components don't manage mount/unmount lifecycles. They don't subscribe to anything. They don't worry about cleanup. The framework's existing builders — `Header`, `Card`, `ListItem`, `Section`, `Listing`, `Footer` — are all this shape. Yours will be too.

## Decision tree — compose, extend, or build new

Most "new components" don't need new code. Walk the tree:

```
Does an existing builder produce close-enough markup?
│
├─ YES, same markup + slightly different look
│   → PATH 2 (Extend): add a typed CssClass with InLayer<Component>
│                       and let the existing builder render the markup.
│
├─ YES, just combine two-three existing builders together
│   → PATH 1 (Compose): call them inside your AppModule's SelfContent.
│                       No new component code at all.
│
└─ NO — fundamentally different DOM shape, different interaction model
    → PATH 3 (Build new): typed CssClass records + a JS renderer +
                          conformance baseline. Strict recipe below.
```

Default to **Path 1 or 2**. Reach for **Path 3** only when the existing builders' DOM shape is structurally wrong for your need — a custom kind of card you can re-skin via CSS is still Path 2.

## Inventory of shipped builders

Lives in `homing-studio-base`'s building blocks catalogue. One-liner each:

| Builder | What it produces | When to compose with |
|---|---|---|
| `Header({ brand, crumbs })` | Brand + breadcrumb strip | every page top |
| `Card({ href, title, summary, badge, link })` | Tile with badge + summary + action link | grid listings, tile-shaped entries |
| `ListItem({ href, title, summary, meta })` | Single row with title + summary | dense vertical lists |
| `Section({ title, children })` | Titled group of children | structural grouping |
| `Listing({ ... })` | Auto-laid-out grid of Cards / ListItems | catalogue-style listings |
| `Footer({ ... })` | Page bottom strip | every page bottom |

Full inventory and per-builder prop shapes live in `Building Blocks › Atoms` in any running studio. Read that doc before assuming a builder doesn't fit.

---

## Path 1 — Compose (no new code)

Use the existing builders directly inside an AppModule's `SelfContent`:

```java
@Override
public List<String> selfContent(ModuleNameResolver nameResolver) {
    return List.of(
            "function appMain(rootElement) {",
            "    var page = document.createElement('div');",
            "    css.addClass(page, st_root);",
            "",
            "    page.appendChild(Header({ brand: { label: 'My Studio', href: '/' }, crumbs: [{ text: 'Settings' }] }));",
            "",
            "    var section = Section({",
            "        title: 'Recent activity',",
            "        children: items.map(function(it) {",
            "            return Card({ href: it.url, title: it.title, summary: it.summary, badge: it.kind });",
            "        })",
            "    });",
            "    page.appendChild(section);",
            "",
            "    rootElement.replaceChildren(page);",
            "}"
    );
}
```

Imports for the builders flow through your AppModule's `imports()` method. See an existing AppModule (e.g. `DocReader`) for the `ImportsFor.<MyApp>builder().add(new ModuleImports<>(...))` chain.

**This is the right path for ~80% of "I want a new component" cases.** No new CssClass, no new JS file, no conformance test surface.

---

## Path 2 — Extend (one new CssClass, no new JS)

The existing builder's DOM is right; you just want a different look. Add a typed `CssClass` and let the existing builder render its markup, plus your class.

```java
// 1. Declare the new typed class. InLayer<Component> tags the cascade
//    tier per RFC 0003 — without it, your rule may lose to default
//    component rules at the same specificity.
package com.example.studio.css;

import hue.captains.singapura.js.homing.core.Component;
import hue.captains.singapura.js.homing.core.CssClass;
import hue.captains.singapura.js.homing.core.InLayer;

public final class MyStudioStyles implements hue.captains.singapura.js.homing.core.CssGroup {

    public record st_card_warning() implements CssClass<MyStudioStyles>, InLayer<Component> {
        @Override public String body() {
            return """
                background: #fff5e6;
                border-left: 4px solid #d97706;
                """;
        }
    }
    // ... other classes
}
```

```javascript
// 2. Use the existing builder, then attach your class to the returned node.
var card = Card({ title: "Quota exceeded", summary: "Reduce usage or upgrade." });
css.addClass(card, st_card_warning);
page.appendChild(card);
```

No new component definition needed. The framework's existing `Card` builder produces the markup; your class layers visual differentiation on top. Conformance tests pick up the new class automatically; no new test code needed.

**Use this path when**: same structural intent as an existing builder, different visual treatment. A "warning card" / "success row" / "draft section" are all Path 2.

---

## Path 3 — Build new

Reach here when the markup itself needs to be different. The framework's component contract has five rules; the conformance suite enforces them.

### 3.1 The contract (Pure-Component Views + Owned References + Encapsulated Components)

A new component is a **function that takes a typed props object and returns a DOM `Node`**. It owns:

1. Its own DOM (uses `document.createElement`, never `innerHTML`).
2. Its own CSS (typed `CssClass` records, applied via `css.addClass`, never raw `.className` writes).
3. Its own hrefs (uses `href.set`, never raw `href=` writes).
4. Its own typed refs to nested elements (returned from the function or held in closure — never queried by the caller via `.querySelector`).
5. Behaviour that's local to the component (event listeners attached to its own elements only).

Read the relevant doctrines before writing: **Pure-Component Views**, **Owned References**, **Methods Over Props**, **Managed DOM Ops**, **Encapsulated Components**. They're in any running studio's *Doctrines* catalogue. Each doctrine is a 1–2 minute read; the conformance suite enforces them mechanically.

### 3.2 Typed CssClass records

```java
package com.example.studio.css;

import hue.captains.singapura.js.homing.core.Component;
import hue.captains.singapura.js.homing.core.CssClass;
import hue.captains.singapura.js.homing.core.InLayer;
import hue.captains.singapura.js.homing.core.Layout;

public final class StatusBadgeStyles implements hue.captains.singapura.js.homing.core.CssGroup {

    public record st_status_badge() implements CssClass<StatusBadgeStyles>, InLayer<Component> {
        @Override public String body() {
            return """
                display: inline-flex;
                align-items: center;
                gap: 6px;
                padding: 4px 10px;
                border-radius: 12px;
                font-size: 12px;
                font-weight: 600;
                """;
        }
    }

    public record st_status_badge_dot() implements CssClass<StatusBadgeStyles>, InLayer<Component> {
        @Override public String body() {
            return """
                width: 8px;
                height: 8px;
                border-radius: 50%;
                background: currentColor;
                """;
        }
    }

    // Variant modifier classes — each in its own tier-tagged record.
    public record st_status_badge_ok() implements CssClass<StatusBadgeStyles>, InLayer<Component> {
        @Override public String body() {
            return "color: #15803d; background: #dcfce7;";
        }
    }
    public record st_status_badge_warn() implements CssClass<StatusBadgeStyles>, InLayer<Component> {
        @Override public String body() {
            return "color: #b45309; background: #fef3c7;";
        }
    }
}
```

### 3.3 JS renderer

```javascript
// src/main/resources/homing/js/com/example/studio/StatusBadge.js
function StatusBadge(props) {
    var status = props.status || "neutral";      // "ok" | "warn" | "neutral"
    var label  = props.label  || "";

    var root = document.createElement("span");
    css.addClass(root, st_status_badge);
    if (status === "ok")   css.addClass(root, st_status_badge_ok);
    if (status === "warn") css.addClass(root, st_status_badge_warn);

    var dot = document.createElement("span");
    css.addClass(dot, st_status_badge_dot);
    root.appendChild(dot);

    root.appendChild(document.createTextNode(label));

    return root;
}
```

Notes:
- No `export` — the framework's bundler auto-injects names.
- No `innerHTML`, no `setAttribute("class", …)`. Use `css.addClass`.
- `href.set` for links (none here — this is a span).
- The function is pure: same props → same DOM. No closure-captured state.
- If the component needs refs to nested elements (e.g., for in-page updates after a fetch), return them in an object alongside the root: `return { root, dot, labelEl };`. Callers receive the typed structure.

### 3.4 Java EsModule binding

```java
package com.example.studio;

import hue.captains.singapura.js.homing.core.EsModule;
import hue.captains.singapura.js.homing.core.ExportsOf;
import java.util.List;

public record StatusBadge() implements EsModule<StatusBadge> {

    public static final StatusBadge INSTANCE = new StatusBadge();

    // The typed function name the framework's name resolver will find
    // when other modules import "StatusBadge".
    public record StatusBadge_fn() {}

    @Override
    public ExportsOf<StatusBadge> exports() {
        return new ExportsOf<>(INSTANCE, List.of(new StatusBadge_fn()));
    }
}
```

(The exact `EsModule` shape varies — model from an existing builder file like `Card.java` in `homing-studio-base`. Pattern: one record per JS-exported function, registered via `exports()`.)

### 3.5 Use it from an AppModule

```java
@Override
public ImportsFor<MyApp> imports() {
    return ImportsFor.<MyApp>builder()
            .add(new ModuleImports<>(List.of(new StatusBadge.StatusBadge_fn()), StatusBadge.INSTANCE))
            // ... other imports
            .build();
}
```

Then call `StatusBadge({ status: "ok", label: "Synced" })` in your `SelfContent` JS.

---

## MPA constraints — what you can and cannot assume

This framework serves each page from the server with a typed AppModule + JS that runs once on load. There is no client-side router, no view tree, no reactive state graph. Consequences for your component:

| ✅ You can | ❌ You cannot (without leaving this skill's scope) |
|---|---|
| Hold local mutable state in closure (e.g. cached fetch result) | Memoise across page navigations (browser tears it down) |
| Attach event listeners; the browser cleans them up on unload | Subscribe to a global store or pub/sub for cross-component updates |
| Mutate your own owned refs after data arrives (e.g. fill in title after `/doc-refs` fetch) | Re-render the whole component when props change — the function is called once |
| Trigger imperative animations (`element.animate(...)`, CSS class toggles) | Wire a reactive `useEffect` / `watch` pattern across props |
| Fetch data asynchronously and update owned refs when it lands | Coordinate with sibling components — each owns its own world |
| Use the AppModule's typed Params for cross-page state | Build virtual-DOM diffing — wrong tool, wrong shape |

**If you find yourself wanting any of the right-column behaviours**, stop. The component you're building is SPA-shaped, and this framework intentionally doesn't model that. Either reframe to MPA shape (move state into URL params, let page navigation be the "re-render") or escalate the design — the framework would need new primitives, not just a skill.

---

## Doctrine compliance checklist

Before declaring your component done, your code passes all of these (the conformance suite checks them automatically when you wire the studio's existing test base classes):

- [ ] **No `innerHTML`** — use `document.createElement` + `appendChild` (Managed DOM Ops).
- [ ] **No raw `.className`, `.classList.add(...)`** — use `css.addClass(el, st_…)` (Managed DOM Ops).
- [ ] **No raw `href=` writes** — use `href.set(el, url)` (covered by `HrefConformanceTest`).
- [ ] **Every CssClass declares `InLayer<L>`** — typically `InLayer<Component>` for component-tier styles (covered by cascade-ladder discipline).
- [ ] **Pure function signature** — same props produce same DOM (Pure-Component Views).
- [ ] **No querying outside your own subtree** — never `document.querySelector(...)` for an element you didn't create (Owned References).
- [ ] **Component owns its CSS, markup, and behaviour** — three together, in one place (Encapsulated Components).

---

## What to never do

- **Don't write JS that produces HTML as strings.** No template literals returning markup, no `String.format`-style markup builders. The framework's conformance scan refuses raw HTML in JS.
- **Don't reach into another component's refs.** If you need data from a sibling, route it through the parent (props in, callbacks out — though callbacks should be rare in MPA).
- **Don't add a CssClass without `InLayer<L>` tagging.** It'll silently lose cascade conflicts at unpredictable moments.
- **Don't recreate the chrome.** Header, Footer, theme picker, breadcrumbs — all framework-provided. Don't write your own.
- **Don't add SPA disciplines.** No client-side router, no state library, no view tree. If the design needs them, it's the wrong design for this framework as it stands.
- **Don't skip the doctrine docs.** They're short. The conformance suite assumes you read them.

---

## Reference reading inside a running studio

- `Building Blocks › Atoms` — every shipped CssClass primitive, grouped by purpose.
- `Building Blocks › Blocks Index` — every shipped composite builder (Card, Section, etc.) with prop shape.
- `Doctrines › Pure Component Views` — the function-of-props discipline.
- `Doctrines › Owned References` — the no-`querySelector`-outside-your-tree rule.
- `Doctrines › Methods Over Props` — what to put on the JS API surface.
- `Doctrines › Managed DOM Ops` — no raw `innerHTML` / `classList` / `setAttribute` writes.
- `Doctrines › Encapsulated Components` — markup + behaviour + CSS owned together.

The framework's own components — `Card.java`, `ListItem.java`, `Section.java`, `Header.java` in `homing-studio-base` — are the canonical worked examples. When in doubt, model from them.
