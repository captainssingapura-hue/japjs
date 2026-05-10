# RFC 0003 — Pure-Component Views & Themeable Form

| Field | Value |
|---|---|
| **Status** | **Draft** — open for iteration. |
| **Author** | Howard, with Homing |
| **Created** | 2026-05-08 |
| **Last revised** | 2026-05-08 |
| **Supersedes** | None |
| **Superseded by** | None |
| **Addresses** | [Defect 0002](#ref:def-2). Partially relieves [Defect 0001](#ref:def-1) (view-layer chrome). |
| **Honours** | [Pure-Component Views](#ref:pcv), [Methods Over Props](#ref:mop), [Managed DOM Ops](#ref:mdo), [Owned References](#ref:or). |
| **Pending revision** | The render-style API (`render(props) → Node`) and `Props` records throughout this RFC predate the Methods-Over-Props doctrine. They will be reshaped to `mount(slot) → Handle` with constructor args + typed methods + callbacks. Update tracked in §9. |
| **Target phase** | Phase 1 (after the studio's current cleanup work lands). |

---

## 0. Status notice

This is a **draft RFC** capturing a design conversation. Sections marked **OPEN** are explicitly deferred decisions; sections marked **PROPOSED** are recommended defaults that may change. Anything else is the working consensus.

---

## 1. Motivation

Two surfaces make a single gap tangible (see [Defect 0002](#ref:def-2)):

1. The moving-animal demo's platform stays a flat rectangle no matter the theme. Themes vary background colour and music, but not the platform's *form* — they cannot ship a forest floor, a desert dune, or snow.
2. Every studio AppModule re-emits the same card HTML inline. A theme that wants parchment-edged or geometric-cornered cards has no hook to deliver them, and the duplication is itself a code smell.

Both reduce to the same observation: **the framework has no Component primitive**. CSS gives us `CssGroup<C>` + `CssGroupImpl<C, TH>` for paint; the missing twin is `Component<C>` + `ComponentImpl<C, TH>` for form. Once that primitive exists, themes can vary form, and the duplicated UI patterns become typed-once-used-everywhere.

But that motivation alone is too weak. The strong position is in §2.

---

## 2. Foundational doctrine — views are pure Component compositions

> **No HTML in any consumer code. Every UI element in every AppModule is a Component invocation. The only HTML allowed is the outermost mount-point `<div id="app">` provided by the framework's bootstrap page.**

This is the doctrine the framework commits to. The Component primitive (§3) is what makes it *expressible*; this section is what makes it *required*.

### 2.1 What the doctrine bans

In any AppModule's JS (`appMain`, helpers, anything reachable from `appMain`):

- **No HTML tag literals.** No `'<div>…</div>'` strings, no `'<a href=…>'`, no `'<h1>'`.
- **No `innerHTML`/`outerHTML` assignments.** Setting these to anything other than `""` (clear) is forbidden.
- **No raw `document.createElement('div')`.** The HTML element vocabulary is reached through Components, not through the DOM API.

### 2.2 What the doctrine permits

- **Component invocations only.** `Heading.render({level: 1, text: "Hi"})`, `Card.render({title, body, footer})`, `Stack.render({children: [a, b]})`.
- **String props.** Text content passed into Components is just a string — Components escape it before emitting it as text content.
- **Event listeners attached to Component-returned DOM nodes.** A Component returns a `Node`; the consumer may `node.addEventListener(...)` on it.
- **`document.getElementById` / `querySelector` for traversal.** Reading the DOM is fine. Mutating it is fine *via* Component re-renders (replace a node with another Component's output), not via raw HTML.

### 2.3 Where the doctrine doesn't apply

- **Component implementations themselves** (`ComponentImpl<C, TH>` template strings or JS render bodies) are HTML — they *are* the HTML the framework emits. The doctrine bans HTML in **consumer** code (AppModule JS), not in **provider** code (Component impls).
- **The framework bootstrap page** (`AppHtmlGetAction`'s rendered HTML, the theme picker, etc.) is HTML. That's where the outermost `<div id="app">` lives.
- **Generated framework JS** (the auto-prepended `nav`, `params`, `css`, `href`, `_components` blocks) is JS code, not HTML, and is exempt anyway because it isn't authored per AppModule.

### 2.4 Why it's worth the strictness

- **Type safety crosses the import boundary.** Today, the Java-side typing of CSS classes / nav targets / Doc records gets thrown away the moment a JS file starts concatenating `'<a class="' + cn(st_card) + '">'`. Under the doctrine, the typed handles are wired into typed Components; rename a CSS class on the Java side and every consumer's call site stays correct (or breaks at compile time on the impl side).
- **Themes get full reach.** A theme can override *any* visual element — heading style, button shape, card frame, list bullets — because every element is a Component with `ComponentImpl<C, TH>` impls. There is no "we forgot to make this themable" backdoor.
- **Duplication ends at the source.** If `<h1>` is `Heading.render({level: 1, …})`, then "make all h1s use Calibri" is one impl change. Today it's a `:root` CSS rule plus prayer.
- **The ban is enforceable.** A conformance test scans every consumer JS file for tag literals and `innerHTML` assignments. The doctrine isn't a guideline that drifts — it's a build break.
- **Doctrine clarity.** "No HTML in consumer code" is a clean rule. "Don't use innerHTML, prefer DOM API, use Components when convenient" is murky and erodes.

### 2.5 The cost the framework owes in return

Strict doctrines fail when the alternative is too verbose. The framework owes:

- A **complete atom vocabulary** (§3.4) — every HTML element a consumer might want has a Component. No element is unreachable.
- **Ergonomic composition** (§3.5) — children, lists, conditional content express naturally. Writing `Stack.render({children: [a, b, c]})` is not painful.
- **Auto-injection** — Components arrive as named consts, like `nav` / `css` / `href` today. No imports to write.
- **Migration path** (§5) — existing AppModules keep working until migrated, one at a time.

---

## 3. Proposed design

### 3.1 Component identity (Java)

Mirroring `CssGroup<C>`:

```java
public interface Component<C extends Component<C>> extends EsModule<C> {
    /** Typed prop record this Component renders against. */
    Class<? extends Record> propsType();
}
```

A concrete Component declares its props record:

```java
public record Card() implements Component<Card> {
    public record Props(String href, String title, String summary, String badge,
                        Node body /* structural slot — see §3.5 */) {}

    public static final Card INSTANCE = new Card();

    @Override public Class<? extends Record> propsType() { return Props.class; }

    @Override public ImportsFor<Card> imports() {
        return ImportsFor.<Card>builder()
                .add(new ModuleImports<>(List.of(
                        new StudioStyles.st_card(),
                        new StudioStyles.st_card_title(),
                        new StudioStyles.st_card_summary()
                ), StudioStyles.INSTANCE))
                .build();
    }
}
```

### 3.2 Per-theme implementation (Java)

Mirroring `CssGroupImpl<C, TH>`:

```java
public interface ComponentImpl<C extends Component<C>, TH extends Theme> {
    C  component();
    TH theme();
    Render render();

    sealed interface Render {
        record Template(String htmlOrSvg) implements Render {}    // §3.3 Mode A
        record Function(String jsBody)    implements Render {}    // §3.3 Mode B
    }
}
```

### 3.3 Two render modes (PROPOSED — hybrid)

**Mode A — HTML/SVG template strings with `${name}` slots.**
- Slot interpolation HTML-escapes by default; `${name | raw}` for explicit unescaped (e.g. an SVG already prepared by the impl, or a structural slot from another Component).
- Server-renderable; cacheable; easy to author.
- Suits ~90% of Component impls (atoms, organisms, theme variants).

**Mode B — JS render function bodies.**
- Theme provides a `function(props, ctx) { … }` body. Runtime wraps + invokes.
- Has access to `props`, the auto-injected `css`/`nav`/`href` runtime, and a `ctx` exposing helpers (asset lookup, child rendering).
- Returns a `Node` (or array of nodes).
- Suits app-specific Components (animal-platform with per-theme particles + sounds) and any Component needing per-theme behaviour.

### 3.4 The atom vocabulary the doctrine demands

For the doctrine to hold, the framework ships a Component for every HTML element a consumer might want. This is bigger than a "card library" — it's a typed wrapper around HTML.

**Text:** `Heading` (with `level` prop), `Paragraph`, `Span`, `Strong`, `Em`, `Code`, `Anchor`, `Time`, `Mark`.

**Block:** `Section`, `Article`, `Aside`, `Header`, `Footer`, `Nav`, `Main`, `Figure`, `FigCaption`.

**Layout:** `Box`, `Stack`, `Inline`, `Grid`, `Spacer`, `Divider`. (These are framework primitives — `Box` is "a div with semantic-token padding", `Stack` is "vertical flex with gap", etc.)

**List:** `List`, `OrderedList`, `ListItem`, `DefinitionList`, `Term`, `Definition`.

**Media:** `Image`, `Video`, `Audio`, `Iframe`, `Svg`, `Canvas`.

**Form:** `Form`, `Input`, `Select`, `Option`, `Textarea`, `Label`, `Button`, `Checkbox`, `Radio`.

**Table:** `Table`, `TableHead`, `TableBody`, `TableRow`, `TableCell`, `TableHeader`.

That's ~40 atoms. Tedious to enumerate; one-time cost; covers HTML completely so the doctrine has no escape hatches.

> **OPEN — D6: Do we ship the full atom vocabulary as one package, or as opt-in sub-packages?** One package is simpler to reason about; sub-packages let downstream skip ones they don't need (a non-form-heavy studio doesn't import `Form`/`Input`/etc.). PROPOSED: one package, with the import-walker only emitting what consumers actually `imports()`.

### 3.5 Slot model — structural by default

**Structural slot** — pass a `Node` (or `Node[]`) from another Component's render:
```js
Card.render({
    title: "Some doc",
    body: Stack.render({ children: [
        Paragraph.render({ text: "First paragraph" }),
        Paragraph.render({ text: "Second paragraph" })
    ]})
})
```

**String slot** — pass a string for primitive text:
```js
Heading.render({ level: 1, text: "Hello" })
```

The Component's props record types each slot as `String` or `Node` (or `List<Node>`). String slots HTML-escape on insertion; structural slots paste already-rendered DOM into the parent.

> **D2 (revised) — structural slots are required in v1.** Without them, composition under the doctrine collapses. (Earlier draft punted to v2; the doctrine forces them in.)

### 3.6 Auto-injection into consumer modules

Like `nav` / `css` / `href` / `params` / `docs` today: the framework auto-prepends one binding per imported Component:

```js
// auto-prepended (when MyApp.imports() includes Card.INSTANCE):
const Card     = _components.handle("…ui.Card");
const Heading  = _components.handle("…ui.Heading");
const Stack    = _components.handle("…ui.Stack");
const Anchor   = _components.handle("…ui.Anchor");
```

Consumer:
```js
function appMain(rootElement) {
    rootElement.appendChild(Stack.render({ children: [
        Heading.render({ level: 1, text: "Notation Studio" }),
        Card.render({
            title: "Documents",
            body: Anchor.render({ href: nav.NotationDocBrowser(), text: "Browse →" })
        })
    ]}));
}
```

Note: **no HTML strings.** Doctrine holds.

### 3.7 Theme registration

```java
public final class ComponentImplRegistry {
    public static final List<ComponentImpl<?, ?>> ALL = List.of(
            new HeadingImpl_DefaultTheme(),
            new HeadingImpl_ForestTheme(),
            new HeadingImpl_SunsetTheme(),
            new CardImpl_DefaultTheme(),
            // …
    );
}
```

A new server endpoint `/components-bundle?class=<ConsumerClass>&theme=<slug>` resolves transitively (every Component the consumer imports + each one's active-theme impl) and returns them in one response. Cached per-(consumer, theme).

### 3.8 Asset management

For Mode B Components needing theme-keyed images / sounds / SVG fragments:

```java
public interface ThemeAssets<TH extends Theme> {
    TH theme();
    Map<String, String> assets();   // key → classpath path
}
```

Served via `/asset?theme=<slug>&key=<name>`. Resolved in templates via `${asset:grass-tuft}`, in JS render bodies via `ctx.asset("grass-tuft")`. Cached aggressively (immutable per build).

### 3.9 Conformance — making the doctrine enforceable

Add a `ComponentConformanceTest` to `homing-conformance` that scans every JS file under `src/main/resources/homing/js/` and rejects:
- Any string literal containing `<` followed by `[a-zA-Z]` (HTML tag start)
- Any `\.innerHTML\s*=` or `\.outerHTML\s*=` assignment (except `= ""`)
- Any `document.createElement\(` call (except inside files marked as Component impls)

The test runs in CI; the doctrine becomes a build break.

> **OPEN — D7: Strict from day one, or grace period during migration?** PROPOSED: opt-in per AppModule via a marker (`@ComponentMigrated` or similar); once a module is migrated, the conformance test enforces the doctrine for that module. New AppModules must be Component-only from the start.

---

## 4. Scope flavours

The doctrine raises the floor. F1 must include the full atom vocabulary or the doctrine is unhonourable. Three plausible scopes:

### F1 — Full atom vocabulary + studio chrome migration

- All ~40 atoms (§3.4)
- `StudioPage`, `StudioHeader`, `BreadcrumbTrail`, `CardGrid` organisms
- Theme impls for Default / Forest / Sunset for every Component
- One studio AppModule fully migrated as a worked example (likely `DocBrowser`)
- Mode A only; no assets; no Mode B

~5–6 weeks. Honours the doctrine for the studio's own consumer-side code; defers theme-keyed *form* (still paint-only theming for now).

### F2 — Above + Mode B + assets ✅ PROPOSED v1

Adds Mode B (JS render functions) and theme assets so app-specific Components like the moving-animal platform can ship per-theme behaviour and per-theme images/sounds.

Adds the moving-animal demo's `Platform<MovingAnimalApp>` migration as a second worked example demonstrating themeable form end-to-end.

~7–8 weeks. The full vision of the RFC: doctrine + themeable form together.

### F3 — Above + conformance + runtime theme switching

Adds the `ComponentConformanceTest` + the `@ComponentMigrated` marker discipline. Makes theme switching work without page reload (the runtime swaps Component impls in-place).

~10–12 weeks. Defer to a follow-up — the conformance test can land as soon as the first AppModule is migrated and we know what shapes the false-positive false-negative rules need.

---

## 5. Migration story

### 5.1 Per-AppModule migration

For each AppModule:
1. Identify every HTML tag in its JS. (Mostly mechanical — grep for `'<`.)
2. Replace each with the matching Component invocation.
3. Add the Components to the AppModule's `imports()` block.
4. Mark the AppModule as `@ComponentMigrated` (under D7) so conformance applies.

Existing AppModules keep working un-migrated. There is no flag day. The studio's own AppModules migrate one per PR.

### 5.2 Studio chrome → Components

The repeated `<div class="st-root"><div class="st-header">…` block becomes:

```js
StudioPage.render({
    header: StudioHeader.render({
        brand: "Notation · studio",
        breadcrumbs: BreadcrumbTrail.render({ items: ["Home", "Documents"] })
    }),
    body: /* … */
})
```

Per AppModule, ~40–60% LoC reduction, plus inheritance of any future header/breadcrumb redesign for free.

### 5.3 Plan trackers (Defect 0001) → Component compositions

Plan/Step views become `<StudioPage><CardGrid>{phases.map(p => Card.render(p))}</CardGrid></StudioPage>` style compositions. Combined with `PlanKit<S>` (Defect 0001's resolution), a new tracker is just data + one-line registration.

### 5.4 Moving-animal platform → themed `Platform<MovingAnimalApp>`

The demo declares its own `Platform<MovingAnimalApp>` Component (Mode B render function). Each theme registers a `PlatformImpl<MovingAnimalApp, ForestTheme>` (forest floor + grass tufts asset + footstep audio asset), `PlatformImpl<MovingAnimalApp, SunsetTheme>` (desert dune + sand particles), etc. Theme switch reshapes, repaints, and re-sounds.

---

## 6. Trade-offs accepted

- **One more abstraction layer.** Two new typed pairs (`Component`/`ComponentImpl`) on top of `CssGroup`/`CssGroupImpl`. Cost paid once; the symmetry is the mental model.
- **Atom vocabulary is large.** ~40 atoms is a lot to ship in v1. Cost paid once by the framework so consumers never pay it.
- **Mode B is real complexity.** JS render-function bodies need careful sandboxing and a stable `ctx` API. Worth it because it's the only way to get theme-keyed *behaviour* (animations, sounds, custom physics).
- **Asset management adds a route + a registry.** Bounded cost; pays off as soon as one theme ships SVGs.
- **First-paint performance.** A `/components-bundle` request adds one round-trip on initial app load. Mitigated by aggressive theme-keyed caching (immutable per build).
- **Migration is total, not local.** Every existing AppModule's JS gets rewritten. The migration is per-PR, not all-at-once, but every consumer eventually pays.

---

## 7. Decisions still open

| Tag | Question | PROPOSED default |
|---|---|---|
| **D1** | Mode B in v1 or follow-up? | **In v1** — needed for the animal-demo worked example. |
| **D2** | Structural slots in v1? | **Yes, required** — the doctrine forces them. |
| **D3** | Render returns DOM or HTML string? | **`Node`** — non-negotiable under the doctrine. |
| **D4** | Server-side or client-side impl resolution? | **Server-side**, bundled per consumer. |
| **D5** | Asset management in v1? | **Yes**, paired with Mode B. |
| **D6** | Atom vocabulary as one package or sub-packages? | **One package**; the import-walker emits only what's actually imported. |
| **D7** | Conformance from day one? | **Opt-in per AppModule** via `@ComponentMigrated`; new AppModules must be Component-only. |

---

## 8. Acceptance criteria

V1 (F2) lands when:

1. `Component<C>` + `ComponentImpl<C, TH>` interfaces shipped (likely `homing-core`; possibly `homing-studio-base` if we keep them studio-scoped).
2. Full atom vocabulary (§3.4) shipped in `homing-studio-base/.../ui/atoms/` with three theme impls each (Default, Forest, Sunset).
3. Studio organisms (`StudioPage`, `StudioHeader`, `BreadcrumbTrail`, `CardGrid`, etc.) shipped in `homing-studio-base/.../ui/organisms/`.
4. `/components-bundle` and `/asset` endpoints registered automatically in `StudioBootstrap`.
5. **At least one studio AppModule** fully migrated to be Component-only — JS contains zero HTML tag literals, zero `innerHTML` writes, zero `createElement` calls.
6. **Moving-animal demo's `Platform`** migrated as the second worked example. Theme switch demonstrably reshapes the platform.
7. README in `homing-studio-base` updated with a Components section + a worked migration example showing before / after.
8. Defect 0002 closed.

V2 (F3) follow-up lands when:

9. `ComponentConformanceTest` enforces the doctrine on every `@ComponentMigrated` AppModule.
10. All studio + demo AppModules are migrated; the marker is removed; the conformance test applies globally.
11. Runtime theme switching works without page reload.

---

## 9. Revision log

- **2026-05-08** — Initial draft (titled "Themeable Form & Component Primitive"). Captured the deliberation that produced Defect 0002.
- **2026-05-08** — Revised. Title changed to "Pure-Component Views & Themeable Form" to lead with the doctrine. Added §2 "Foundational doctrine", expanded §3.4 atom vocabulary to cover all of HTML, flipped D2 (structural slots) and D3 (Node return) from open to required, added D6 + D7, restructured scope flavours so F1 is the floor required for the doctrine to hold, added §3.9 conformance test plan.
- **2026-05-08** — Three further doctrines added externally: **Methods Over Props**, **Managed DOM Ops**, **Owned References**. The render-style API drafted in this RFC (`Component.render(props) → Node` and `Props` records) is now superseded by those doctrines but the design sections have not yet been rewritten. Pending edits:
  - §3.1: replace `Component.render(props)` with `Component.mount(slot) → Handle`. Drop `propsType()` (no Props record); replace with constructor args + typed method API.
  - §3.2: `ComponentImpl.render()` becomes the impl of mount/update/unmount routed through `DomOpsParty`.
  - §3.5: "Slot model" replaces "structural slots" — slots become typed insertion points returned from parent handles.
  - §3.6: example rewritten in object/method style (no `.render(props)` calls).
  - §3.9: conformance scans extended to ban `document.createElement`, `getElementById`, `querySelector`, plus the existing HTML / `innerHTML` rules.
  - §7: D2 / D3 wording updated to track the new vocabulary.
  - Throughout: replace "props" with "constructor args + methods" or "handle API" as appropriate.
