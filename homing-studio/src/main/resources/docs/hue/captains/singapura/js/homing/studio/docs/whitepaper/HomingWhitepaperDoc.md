# Homing — A Type-Safe Workspace Framework for Java Teams

**White paper, draft 1 · 2026-04-24**

> *"A type-safe, stateless, bring-your-own-HTTP framework for Java teams building long-lived workspace-style SPAs — with auditable DOM ownership at runtime, typed end-to-end messaging between widgets and between browser and server, and compile-time guarantees across the Java↔JS boundary."*

---

## 1. Executive Summary

Modern single-page applications are overwhelmingly authored in TypeScript against React or Vue. For Java shops — particularly those building **long-running internal tools, trading dashboards, admin consoles, devtools, and workspace-style applications** — that stack imposes a heavy, parallel toolchain (Node, npm, bundlers, state managers, routers) and a language boundary that the type system can never fully cross.

**Homing** takes the opposite bet: declare the JavaScript module graph, asset bundles, CSS bindings, and inter-tier messages in Java, generate plain ES modules, serve them from whatever HTTP server the team already runs, and use DOM-ownership and typed-channel primitives to keep long-lived UIs honest.

This paper describes the design, the layered architecture, the positioning against existing alternatives, and — honestly — what is built, what is designed, and what is still to be proven.

---

## 2. The Problem

### 2.1 Full-stack is now the expectation, not the exception

Smaller teams, AI-assisted coding, and cost-driven consolidation have pushed backend-heavy engineers into shipping UI. Java teams are being asked to own the full stack.

### 2.2 The mainstream answer is a tax

The default path — React/Vue + TypeScript + Vite/Webpack + Node — requires Java teams to adopt and maintain a second toolchain, a second language, a second build system, a second dependency ecosystem. For small internal tools, the tax often exceeds the payload.

### 2.3 Types stop at the door

TypeScript is type-safe within the JS tier. It is not type-safe across the tier boundary. Renaming a backend field, a CSS class, an SVG asset, or a WebSocket message schema is a multi-repo, multi-language ritual. Refactor confidence collapses at the boundary.

### 2.4 Long-lived UIs leak

Workspace and dashboard apps run for hours or days without a page reload. Event listeners, observers, timers, and third-party widgets leak quietly. Frameworks clean up what they rendered — not what imperative code attached. Leaks are detected by users, not by tooling.

### 2.5 Existing Java-side full-stack options are heavy

Vaadin Flow ties you to stateful servers. Vaadin Hilla reintroduces the Node toolchain. JHipster is a generator that dumps hundreds of opinionated files into your tree. htmx is lightweight but is a *pattern*, not a framework — you still build everything yourself. Kotlin/JS requires a second compiler pipeline.

**There is no lightweight, type-safe, server-agnostic Java-side framework aimed specifically at workspace-class UIs.** Homing is that framework.

---

## 3. Design Principles

1. **Java is the source of truth.** Module graphs, CSS classes, SVG assets, channels, messages, and commands are declared as Java records. The JS side is generated, not hand-authored at the boundary.
2. **Generate plain ES modules, no bundler.** Browsers load standards-based modules directly. No Webpack, no Vite, no npm.
3. **Bring your own HTTP.** The core depends on nothing. Four endpoints, stateless, served by Spring Boot, Vert.x, Micronaut, Quarkus, plain Servlets — whatever the team runs.
4. **Explicit DOM ownership.** Every element is created on a named branch owned by a component instance. Teardown is one call. Leaks are detectable at runtime.
5. **Typed end-to-end messaging.** Widgets communicate through Java-declared channels with Java-declared message records. The same types govern widget↔widget and widget↔server traffic.
6. **No hidden magic.** No reflection tricks, no annotation-driven auto-wiring, no runtime classpath scanning. Records, interfaces, compile-time generation.
7. **Small core, opt-in extensions.** Core stays minimal. Workspace, bus, and server adapters are separate modules.

---

## 4. Architecture Overview

Homing is organized as a **four-layer stack**. Each layer is useful on its own; higher layers depend on lower ones.

```
┌──────────────────────────────────────────────────────────────────────────┐
│  Layer 4 — Workspace                                                     │
│  SplitPane, MultiTabPane, dockable panels, command registry, layout      │
│  persistence                                                             │
├──────────────────────────────────────────────────────────────────────────┤
│  Layer 3 — Typed Channel Bus                                             │
│  Java-declared channels + message records, branch-scoped subscriptions,  │
│  local in-process bus, pluggable remote transport (Vert.x / Spring WS)   │
├──────────────────────────────────────────────────────────────────────────┤
│  Layer 2 — DOM Ownership (DomOpsParty)                                   │
│  Tree-structured element registry, named elements, set-once activation,  │
│  single-call dissolve, WeakRef leak detection                            │
├──────────────────────────────────────────────────────────────────────────┤
│  Layer 1 — Module & Asset Wiring (homing-core)                            │
│  EsModule, DomModule, AppModule, SvgGroup, CssGroup, ExternalModule,     │
│  EsModuleWriter, ContentProvider, ModuleNameResolver                     │
├──────────────────────────────────────────────────────────────────────────┤
│  Layer 0 — Server Adapter (pluggable)                                    │
│  /app  /module  /css  /css-content  — four stateless endpoints,          │
│  implemented against any Java HTTP stack                                 │
└──────────────────────────────────────────────────────────────────────────┘
```

### 4.1 Layer 0 — Server Adapter (pluggable)

Four query-addressed endpoints; each returns bytes with a content type. No session state, no sticky routing, no framework lock-in.

| Endpoint | Response | Role |
|---|---|---|
| `/app?class=…` | `text/html` | Bootstrap page for an `AppModule` |
| `/module?class=…` | `application/javascript` | Generated ES module |
| `/css?class=…` | `application/json` | Resolved CSS dependency chain |
| `/css-content?class=…` | `text/css` | Raw CSS content |

Reference adapters: **Vert.x** (shipping), **Spring Boot** (planned).

### 4.2 Layer 1 — Module & Asset Wiring

Java records declare what each ES module imports and exports. `EsModuleWriter` composes declarations + `.js` content into valid ES modules. `CssGroup` + `CssClass` give type-safe CSS class bindings. `SvgGroup` + `SvgBeing` bundle SVG assets. `ExternalModule` wraps third-party JS. Renaming a Java identifier refactors every dependent import, at `javac` time.

**Typed navigation** ([RFC 0001](#ref:rfc-1), implemented 2026-05-03) extends this layer with the same Java-records-as-source-of-truth pattern applied to URLs. Each `AppModule` declares an inner `link()` record; consumers import it; the writer generates a typed `nav.X(params)` object and an injected `href.X(...)` manager. External destinations are modeled as `ProxyApp` declarations with URL templates. The `?app=<simple-name>` URL contract replaces leaky `?class=<canonical>` URLs. A sibling `HrefConformanceTest` enforces that no raw `href` substring may appear in user JS outside the manager identifier — the same discipline pattern as `CssConformanceTest`. Net effect: every URL in the rendered DOM, internal or external, is traceable to a typed Java declaration.

### 4.3 Layer 2 — DOM Ownership

Every DOM element is created through a named branch in a single hierarchy. Every branch has an owner (tracked via `WeakRef`). Tearing down a component is one call (`dissolve()`); it removes every element and sub-branch. A leak scanner walks the tree and reports branches whose owners have been garbage-collected without dissolve.

This is what allows long-lived workspace UIs to stay honest.

### 4.4 Layer 3 — Typed Channel Bus

Channels and messages are Java records. The module writer generates JS bindings. A local in-process bus handles widget↔widget traffic; a pluggable remote transport handles browser↔server traffic with identical typed semantics. Subscriptions are scoped to DomOpsParty branches — dissolve a branch, its subscriptions disappear.

### 4.5 Layer 4 — Workspace

Recursive splittable panes, tabbed panes with drag/reorder/detach, dockable panels, a command registry, and serializable layout form the workspace shell.

**Ownership and layout are separate concerns.** A widget does not live *inside* the branch of the pane currently hosting it — that would destroy the widget every time a user dragged its tab to another pane. Instead:

- Each **widget** owns its own DomOpsParty branch (its internal DOM, its subscriptions, its state). The branch is parented under a stable **widget registry** at the workspace root, not under the pane.
- Each **pane** is a host. It renders whichever widgets its current tab list points at, by appending the widget's root element into its tab panel container.
- A **tab** is a lightweight reference `{ paneId, widgetId }` — metadata, not a component.

Dragging a tab from one pane to another is a single `targetContainer.appendChild(widgetRoot)` call plus a tab-list update. The widget's branch, state, subscriptions, and scroll position are untouched. Detaching a tab into a floating modal is the same move with a different container. Closing a tab (explicit user destroy) is a real teardown: the widget registry dissolves the widget's branch.

This gives the workspace three independent structures that don't need to agree on shape:

| Structure | Purpose | Shape |
|---|---|---|
| **Ownership tree** (DomOpsParty) | lifetime, cleanup, leak detection | shell chrome is recursive (mirrors the layout tree); widget registry is flat |
| **Layout tree** (workspace) | presentation — recursive splits, pane positions, tab order | binary tree: split nodes + leaf panes |
| **Bus topology** | communication — channels and subscriptions | graph — any widget may subscribe to any channel |

#### Recursive pane splitting

The layout starts with a small seed (often a single pane, or a fixed 2-pane or 3-pane arrangement) and grows by splitting: any leaf pane can be split horizontally or vertically into two child panes, recursively, to arbitrary depth. This is the standard BSP layout pattern used by VSCode, IntelliJ, tmux, and react-mosaic.

The layout tree is a sealed Java record hierarchy:

```java
public sealed interface LayoutNode permits SplitNode, PaneLeaf {}

public record SplitNode(
    Orientation orientation,   // HORIZONTAL | VERTICAL
    double ratio,              // 0.0–1.0
    LayoutNode first,
    LayoutNode second
) implements LayoutNode {}

public record PaneLeaf(
    String paneId,
    List<TabRef> tabs,
    String activeTabId
) implements LayoutNode {}

public record TabRef(String tabId, String widgetId, String label) {}

public record WorkspaceLayout(LayoutNode root, int schemaVersion) {}
```

A dedicated DomOpsParty subtree mirrors this shape for the shell chrome only: one branch per split node (owning its divider element and two child-frame containers), one branch per leaf pane (owning its tab bar and tab-panel container). The widget registry stays flat and independent.

**Split** — splitting leaf `P`: dissolve `P`'s branch, create a new `SplitNode` branch in `P`'s slot, create two fresh `PaneLeaf` branches under it, redistribute the tab list, `appendChild` widgets into their new host pane's panel container. Because widgets aren't owned by pane branches, they survive the operation without interruption. No branch re-parenting primitive is needed.

**Merge on empty** — when a leaf loses its last tab it collapses: dissolve both the empty leaf and its parent `SplitNode`, promote the sibling up one level. This prevents the layout filling with empty panes over time.

**Minimum size cascading** — recursive splits cascade min-size constraints; splitting a pane narrower than `2 × minPx` is refused with UI feedback, not silently accepted.

#### Persistence

`WorkspaceLayout` serializes directly to JSON. Persistence is a plain save/restore:

- **Local / per-session:** `JSON.stringify(layout)` → `localStorage`.
- **Server-side / shared / multi-device:** a typed endpoint (`GET/PUT /workspace-layout`) exchanging the same record. Same Java record drives both sides; JS bindings are generated by the module writer like any other message schema.
- **Restore:** rebuild the layout tree and shell branches first, then resolve each `TabRef.widgetId` against the live widget registry. Unresolved widgets (plugin removed, model no longer available) render as a placeholder tab rather than failing the restore.
- **Schema evolution:** `schemaVersion` + additive-fields-only rule. Old readers ignore unknown fields; new readers can interpret old layouts.

---

## 5. Key Diagrams

### 5.1 Module dependency graph

```
  CssGroup<ButtonStyles>                SvgGroup<Icons>
      │                                       │
      │  imports btn, btn_primary             │  imports search, menu
      ▼                                       ▼
  ┌────────────────── DomModule<MyWidget> ──────────────────┐
  │                                                         │
  │   imports:  ButtonStyles.btn, ButtonStyles.btn_primary  │
  │             Icons.search                                │
  │   exports:  createMyWidget                              │
  │                                                         │
  └─────────────────────────────────────────────────────────┘
      │
      │  consumed by
      ▼
  AppModule<MyApp>  —>  /app?class=MyApp  (served HTML boots it)
```

All edges are Java-typed. Breaking an edge is a `javac` error, never a runtime surprise.

### 5.2 Ownership tree vs. layout tree (runtime)

The workspace maintains two independent structures. Widgets live in the ownership tree. Panes and tabs live in the layout tree. Tabs reference widgets by id.

**Ownership tree (DomOpsParty)** — shell chrome mirrors the recursive layout; widget registry stays flat:

```
  domOpsParty (root)
    ├── workspace-shell                            ← Workspace controller (chrome only)
    │     ├── split-H                              ← SplitNode branch (horizontal divider)
    │     │     ├── split-V                        ← SplitNode branch (vertical divider)
    │     │     │     ├── pane-nav                 ← PaneLeaf branch (tab bar + panel container)
    │     │     │     └── pane-status              ← PaneLeaf branch
    │     │     └── pane-editor                    ← PaneLeaf branch
    │     └── status-bar                           ← Global chrome
    │
    └── widgets                                    ← Widget registry (flat, stable)
          ├── filetree-src     ← FileTreePanel
          ├── filetree-build   ← BuildTreePanel
          ├── editor-readme    ← CodePanel
          ├── editor-app-js    ← CodePanel
          │     ├── editor-el
          │     └── gutter
          └── help-viewer      ← HelpPanel        (currently detached to modal)
```

**Layout tree (workspace)** — recursive binary tree of SplitNodes and PaneLeaves; tabs are references, not owners:

```
  WorkspaceLayout
    └── SplitNode (H, ratio=0.30)
          ├── SplitNode (V, ratio=0.85)
          │     ├── PaneLeaf "pane-nav"
          │     │     ├── Tab { id: "files",  widget: filetree-src   } ★ active
          │     │     └── Tab { id: "build",  widget: filetree-build }
          │     └── PaneLeaf "pane-status"
          │           └── (empty — e.g. reserved for future tab)
          └── PaneLeaf "pane-editor"
                ├── Tab { id: "readme",  widget: editor-readme  }
                └── Tab { id: "app.js",  widget: editor-app-js  } ★ active

  Modals
    └── Modal { widget: help-viewer }                       ← detached from editor pane
```

Splitting `pane-editor` into two adds a `SplitNode` in its slot and replaces it with two fresh `PaneLeaf`s. Ownership-tree consequence: dissolve `pane-editor`'s branch, create one `split-…` branch and two new `pane-…` branches under it. Widgets previously hosted by `pane-editor` are `appendChild`'d into whichever new leaf now holds their tab — their own branches are untouched.

At render time: each active tab's pane calls `tabPanelContainer.appendChild(widget.rootEl)`. Moving a tab between panes is one `appendChild` into the new container — the widget's branch does not move, its subscriptions do not break, its scroll position survives.

`dissolve()` on any ownership node collapses the entire subtree. Closing a tab dissolves the referenced widget's branch and removes the tab entry from the layout. The leak scanner walks the ownership tree; any widget still registered after its owning controller is GC'd is reported.

### 5.3 Typed channel bus — end-to-end request/response

```
  ┌───── Browser (workspace) ─────────┐                 ┌───── Server (JVM) ─────────┐
  │                                    │                │                              │
  │  FilterPanel                       │                │                              │
  │     │                              │                │                              │
  │     │ bus.send(                    │                │                              │
  │     │   DataChannel.QueryRows,     │                │                              │
  │     │   new QueryRowsRequest(...)  │                │                              │
  │     │ )                            │                │                              │
  │     ▼                              │   WebSocket    │                              │
  │  ┌───────────────────────┐         │   (Vert.x      │     ┌──────────────────┐     │
  │  │ Local bus (JS)        │◀────────┼───EventBus     ┼────▶│ DataChannelHost  │     │
  │  │ - channels: Java-gen  │         │   bridge /     │     │  (Java handler)  │     │
  │  │ - msgs: Java records  │         │   SockJS /     │     └──────────────────┘     │
  │  │ - correlation IDs     │         │   Spring WS)   │              │               │
  │  └───────────────────────┘         │                │              │ returns       │
  │     ▲                              │                │              │ QueryRowsReply│
  │     │                              │                │              ▼               │
  │     │ response delivered to        │                │     (Java record,           │
  │     │ awaiting Promise / subscriber│                │      serialized as JSON)     │
  │                                    │                │                              │
  │  ChartPanel                        │                │                              │
  │     subscribes(RowsUpdated)  ◀─────┼── broadcast ───┼── server publishes update ───│
  │                                    │                │                              │
  └────────────────────────────────────┘                └──────────────────────────────┘

  All channels and messages are Java records.
  The JS bindings are generated, not hand-written.
  Subscriptions are owned by DomOpsParty branches and auto-unsubscribed on dissolve.
  Dragging a widget between panes does not move its branch, so subscriptions survive
  layout changes without any special handling.
```

### 5.4 Deployment — bring your own HTTP

```
                     ┌─────────────────────────────────────┐
                     │  Your existing Java service         │
                     │                                     │
                     │  ┌───────────────────────────────┐  │
                     │  │ Spring Boot  / Vert.x  /      │  │
                     │  │ Micronaut    / Quarkus  /     │  │
                     │  │ plain Servlet container       │  │
                     │  │                               │  │
                     │  │   existing REST controllers   │  │
                     │  │   existing auth / logging     │  │
                     │  │   existing metrics / tracing  │  │
                     │  │                               │  │
                     │  │   + Homing adapter (~4 routes) │  │
                     │  │     /app  /module             │  │
                     │  │     /css  /css-content        │  │
                     │  │   + bus transport adapter     │  │
                     │  └───────────────────────────────┘  │
                     │                │                    │
                     │                │ calls into         │
                     │                ▼                    │
                     │  ┌───────────────────────────────┐  │
                     │  │ homing-core (stateless)        │  │
                     │  │ EsModuleWriter, CssLoader,    │  │
                     │  │ Resolvers, ContentProviders   │  │
                     │  └───────────────────────────────┘  │
                     └─────────────────────────────────────┘
                                      │
                                      │ HTTP / WebSocket
                                      ▼
                     ┌─────────────────────────────────────┐
                     │  Browser                            │
                     │    native ES modules                │
                     │  + DomOpsParty (lifecycle)          │
                     │  + bus client (typed messages)      │
                     │  + workspace shell                  │
                     └─────────────────────────────────────┘
```

No Node. No bundler. No stateful UI server. No framework replacement — Homing slots into what you already run.

---

## 6. Positioning

### 6.1 One-line comparison

| Option | Toolchain weight | Runtime weight | Types across tiers | Server coupling | Leak auditing | Workspace-ready |
|---|---|---|---|---|---|---|
| **React / Vue + TS** | heavy (Node, bundler) | light | partial (TS only) | none (SPA) | framework-managed | 3rd-party libs, fragmented |
| **Vaadin Flow** | medium | heavy (stateful server) | yes | Vaadin-specific | framework-managed | yes, heavy |
| **Vaadin Hilla** | heavy (Node + Spring) | medium | yes (generated) | Spring-centric | framework-managed | via React ecosystem |
| **JHipster** | very heavy (generator) | medium | partial | opinionated | framework-managed | not focused |
| **htmx + Spring** | light | light | no (HTML over wire) | any | n/a | no |
| **Homing** | light (Maven only) | light (stateless) | **yes, end-to-end** | **any (adapter)** | **yes (DomOpsParty)** | **yes, first-class** |

### 6.2 When Homing is the right choice

- Internal tools, admin consoles, trading dashboards, devtools, data-exploration workspaces.
- Long-running SPAs where leaks, ownership, and refactor safety matter.
- Teams that already run a Java service and don't want to maintain a parallel Node toolchain.
- Greenfield UI in a Java-heavy org where hiring pool is backend-leaning.

### 6.3 When Homing is the wrong choice

- Public consumer product with SEO and performance budgets met by Next.js/Nuxt.
- Teams already committed to a React/Vue codebase with shared component libraries.
- UIs dominated by **dense fan-out reactivity** — forms, dashboards, or configuration UIs where dozens of small derived values must stay in sync without manual wiring. Signal-based frameworks (SolidJS, Svelte 5, Vue 3) are purpose-built for this; Homing has you wire updates explicitly through the bus or direct calls.
- **Extreme canvas/WebGL apps that want a reactive scene-graph abstraction** (react-three-fiber-style JSX for 3D). Homing hosts Three.js and `<canvas>` cleanly — see the `TurtleDemo`, `ExtrudedTurtleDemo`, and SVG-extrusion demos — but doesn't provide a reactive 3D binding layer on top. Teams that want `<mesh>`-as-JSX will miss it; teams comfortable with the imperative Three.js API won't.
- Projects that must hire from a broad frontend talent market on short notice.

> **Not on this list:** large tabular datasets and high-throughput rendering.
> Virtualized grids work well with direct-DOM frameworks — AG Grid, SlickGrid, Monaco, and CodeMirror all ship vanilla JS cores for exactly this reason. Homing can host the same patterns; the absence of a VDOM reconciler is neutral-to-helpful at this scale.

---

## 7. What's Built, What's Designed, What's Next

Honest status as of **2026-05-03**.

### 7.1 Built and working

- `homing-core` — module, CSS, SVG, external-module primitives; `EsModuleWriter`.
- `homing-server` — Vert.x-based server adapter; `CssClassManager`; `HrefManager`; live reload via `homing.devRoot`.
- `homing-conformance` — CSS and href raw-op scanners with base test classes.
- `homing-demo` — WonderlandDemo, DancingAnimals, SpinningAnimals, MovingAnimal, TurtleDemo, ExtrudedTurtleDemo, DecomposedSvgDemo, ExtrudedSvgDemo, PitchDeck, DemoCatalogue.
- `homing-studio` — DocBrowser, DocReader, Rfc0001Plan, Rfc0001Step on a custom server with `/doc-content` and `/step-data` endpoints.
- Type-safe CSS class bindings with theme variants.
- **Typed navigation (RFC 0001).** `Linkable`, `AppLink<L>`, `ProxyApp`, `SimpleAppResolver` (transitive walking), `?app=<simple-name>` URL contract, generated `nav` const + injected `href` manager + generated `params` const, `HrefConformanceTest` enforcement, built-in proxies (`Mailto`, `Tel`, `Sms`). Demo and studio fully migrated; both conformance scanners green.

### 7.2 Designed, not yet built

- **DomOpsParty integration.** The library exists (external repo); Homing-side adoption is a design, not code. Contract: every `DomModule` receives a branch like it receives a `css` handle today.
- **Workspace module.** SplitPane and MultiTabPane prototypes exist in a separate JS demo; re-homing as `DomModule`s with typed configs is planned. Target shape: **recursive BSP pane layout** (split any pane H/V to arbitrary depth), flat widget registry, tabs as `{paneId, widgetId}` references, merge-on-empty rule, `WorkspaceLayout` record serialized to localStorage or server.
- **Typed channel bus.** Channel/message record shape, local JS bus, Vert.x transport, branch-scoped subscriptions — designed, not coded.
- **Spring Boot adapter.** Demonstrated architecturally (core has zero Vert.x references); no published starter yet.

### 7.3 Explicitly out of scope

- Durable messaging, event sourcing, distributed clustering. (Users needing these reach for Kafka/NATS.)
- A component library competing with Material UI or Vuetify. (Homing-workspace provides layout primitives, not a design system.)
- Server-side rendering. (Workspace apps don't need it; the niche doesn't ask for it.)
- A reactive rendering model. (Homing targets workloads where direct DOM suffices.)

### 7.4 Near-term milestones

1. A minimal `Homing-spring-boot` adapter (~100 lines) to prove bring-your-own-HTTP in practice.
2. DomOpsParty wired into the demo apps; conformance test for "no raw `document.createElement` outside allowlist."
3. `Homing-workspace` module with SplitPane + MultiTabPane as typed `DomModule`s.
4. `Homing-bus-core` + `Homing-bus-vertx` with one end-to-end typed request/response demo.
5. A single flagship demo app combining all four layers — ideally a data-exploration workspace.

---

## 8. Risks and Honest Caveats

- **Ecosystem size.** Homing has no component catalog, no hiring pool, no Stack Overflow corpus. Small-framework gravity is real. AI-assisted coding softens this but does not eliminate it.
- **Bus factor.** Homing and DomOpsParty are both authored by a small team. Adoption should happen with that risk understood.
- **AI training gap.** Large models know React and Vue deeply; they know Homing not at all. A published docs corpus and example apps are prerequisites for AI-assisted productivity.
- **Convention debt.** A component contract (props shape, event shape, instance model) must be standardized before serious sharing is viable. It's a small design, not a large one, but it has not shipped.
- **Production proof.** No large-scale production deployment exists publicly. Until one does, Homing is a credible design, not a validated platform.
- **SVG and third-party libs partially bypass DomOpsParty.** Template-string SVG (inside a branch-owned element) is fine; WebGL canvases and libraries that manage their own DOM subtrees need a documented escape-hatch pattern.

None of these are blockers. All of them are work.

---

## 9. Call to Action

If you are:

- **A Java team leader** considering a full-stack push — Homing is worth a spike for your next internal tool. A bring-your-own-HTTP adapter is a few hours of work against a service you already run.
- **A full-stack engineer** tired of the Node toolchain tax on small projects — the demo apps boot in one `mvn exec:java` command.
- **A framework builder** interested in end-to-end type safety across tiers — the generated-module approach is a direction worth stress-testing.

Source: this repository. Contact: issues, PRs, discussion.

---

## Appendix A — Minimal Example

```java
// Declare a CSS module
public record ButtonStyles() implements CssGroup<ButtonStyles> {
    public static final ButtonStyles INSTANCE = new ButtonStyles();
    public record btn() implements CssClass<ButtonStyles> {}
    public record btn_primary() implements CssClass<ButtonStyles> {}
    @Override public CssImportsFor<ButtonStyles> cssImports() { return CssImportsFor.none(this); }
    @Override public List<CssClass<ButtonStyles>> cssClasses() {
        return List.of(new btn(), new btn_primary());
    }
}

// Declare a workspace panel
public record FilterPanel() implements DomModule<FilterPanel> {
    public static final FilterPanel INSTANCE = new FilterPanel();
    public record createFilterPanel() implements Exportable._Constant<FilterPanel> {}

    @Override public ImportsFor<FilterPanel> imports() {
        return ImportsFor.<FilterPanel>builder()
            .add(new ModuleImports<>(
                List.of(new ButtonStyles.btn(), new ButtonStyles.btn_primary()),
                ButtonStyles.INSTANCE))
            .build();
    }
    @Override public ExportsOf<FilterPanel> exports() {
        return new ExportsOf<>(INSTANCE, List.of(new createFilterPanel()));
    }
}
```

```javascript
// FilterPanel.js — plain JS, typed imports generated for you
function createFilterPanel(parentBranch, config) {
    const branch = parentBranch.createBranch(config.id);
    branch.activate(config.owner);

    const root = branch.createElement("root", "div");
    const btn  = branch.createElement("apply", "button");
    css.setClass(btn, btn_primary);
    btn.textContent = "Apply";

    btn.addEventListener("click", () => {
        bus.send(DataChannel.QueryRows, { filter: currentFilter() });
    });

    return { el: root, destroy: () => branch.dissolve() };
}
```

Result: a typed, leak-safe, bus-connected panel in ~15 lines of JS with zero hand-written import statements and zero `document.createElement` outside the ownership tree.
