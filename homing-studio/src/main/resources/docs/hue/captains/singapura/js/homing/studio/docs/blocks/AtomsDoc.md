# Atoms — `StudioElements`

The 13 visual builders that everything else composes. Each is a function that takes a typed props object and returns a DOM `Node`. None of them author HTML strings; consumers compose these instead of writing markup directly.

**Where**: `homing-studio-base/.../base/ui/StudioElements.java` (Java declaration) + `…/base/ui/StudioElements.js` (JS implementation).

**Use**: declare `new StudioElements.Card()` (and friends) in your AppModule's `imports()`. The framework auto-prepends `import { Card, Pill, Header, … } from "…StudioElements"` into your served JS, so consumer code calls the names directly.

---

## Header / Brand / Crumbs

`Header({ brand: { href, label }, crumbs: [{ text, href? }] })` → page header bar with brand link + breadcrumb trail. `crumbs` entries with `href` render as links; without `href`, plain text (current page).

`Brand({ href, label })` → just the brand link. Header uses it internally; rarely called directly.

---

## Tile / Card / Pill

`Card({ href, title, summary, badge, badgeClass, link })` → linkable card with title (h3), summary, badge, and "Open →"-style link. Use for grouped doc lists, doctrine catalogue, etc. `badgeClass` is a typed `CssClass` handle.

`Pill({ href, icon, label, desc, dark })` → icon + label + desc launcher tile. Use for studio home tiles, journey lists. `dark: true` switches to the inverted variant.

---

## Layout / Wrappers

`Section({ title, children, gridless })` → labelled grid section. `children` is an array of nodes (cards or pills). `gridless: true` drops the grid wrapper for non-grid contents.

`Footer({ children })` → wrap children with the studio footer chrome. Children can be DOM nodes or strings (auto-text-noded).

`Panel({ title, children })` → labelled inner panel. Used by `PlanRenderer` for grouped detail blocks (Tasks, Dependencies, …).

---

## Tracker chrome

`StatusBadge({ statusSlug, statusLabel })` → coloured pill matching one of `not-started` / `in-progress` / `blocked` / `done` / `resolved`.

`OverallProgress({ caption, summary, percent })` → top-of-page progress bar with caption + summary line + fill bar + percentage on the right.

`StepCard({ href, idLabel, title, summary, statusSlug, statusLabel, progress, doneCount, totalCount, effort })` → linkable phase card with id pill, status badge, summary, progress bar, meta line.

`DecisionCard({ id, question, recommendation, rationale, chosen, statusSlug, statusLabel })` → non-link card with question + recommendation + rationale + optional "Chosen" line + status badge.

`TodoList({ tasks: [{ description, done }] })` → checklist with done / pending indicators.

`MetricsTable({ rows: [{ label, before, after, delta }] })` → 4-column before/after measurements table. Returns `null` when `rows` is empty so callers can `if (node)` without a guard.

---

## Worked example

A minimal home page composed entirely from atoms:

```java
public record MyHome() implements AppModule<MyHome>, SelfContent {
    @Override
    public ImportsFor<MyHome> imports() {
        return ImportsFor.<MyHome>builder()
                .add(new ModuleImports<>(List.of(
                        new StudioElements.Header(),
                        new StudioElements.Pill(),
                        new StudioElements.Section()
                ), StudioElements.INSTANCE))
                .build();
    }
    // ... selfContent generates appMain that calls Header(), Pill(), Section()
}
```

```js
// auto-generated body (illustrative — kits do this for you)
function appMain(rootElement) {
    var root = document.createElement("div");
    root.appendChild(Header({ brand: { href: "/app?app=my-home", label: "My Studio" }, crumbs: [{ text: "Home" }] }));
    root.appendChild(Section({
        title: "Apps",
        children: [
            Pill({ href: "/app?app=my-app", icon: "A", label: "My App", desc: "...", dark: true })
        ]
    }));
    rootElement.replaceChildren(root);
}
```

In practice you wouldn't write that body — the [Catalogue Kit](#ref:cat-kit) generates it from typed Java data.

---

## See also

- [Catalogue Kit](#ref:cat-kit) — composes Header + Pill / Card + Section + Footer into a launcher page.
- [DocBrowser & DocReader Kits](#ref:doc-kits) — same atoms plus marked.js renderer.
- [Tracker Kit](#ref:trk-kit) — uses StatusBadge / OverallProgress / StepCard / DecisionCard / TodoList / MetricsTable / Panel.
- [Doctrine — Pure-Component Views](#ref:pcv) — why these atoms exist (the alternative to HTML strings in consumer code).
