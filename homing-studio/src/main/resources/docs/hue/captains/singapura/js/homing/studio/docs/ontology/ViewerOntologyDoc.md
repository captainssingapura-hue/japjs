# Ontology — Viewer

A **Viewer** is the framework type that renders content of one specific kind. It is the bridge between a Doc's identity and its rendered surface — the addressable, registered, kind-bound machinery that turns "this Doc, please" into a page.

This entry states what a Viewer *is*. It does not prescribe how to design good viewer UX, when to introduce a new viewer kind, what library to use for a given content kind, or how to migrate content from one viewer to another. Those are operational concerns and belong in Doctrines. See *Scope* below.

## Definition

A Viewer is a stateless, read-only, registered binding between a content kind and an AppModule that renders Docs of that kind.

## Identity

- **V1 — Kind-identified.** A Viewer is identified by its *content kind* — a string discriminator (e.g. `"doc"`, `"plan"`, `"studio-graph"`). The kind is the Viewer's primary identifier; URLs, registrations, and Doc routing all use it.
- **V2 — Realisation identity.** Each Viewer is realised as a Java record implementing the framework's Viewer interface, paired with an AppModule that performs the rendering. The record carries the kind; the AppModule carries the rendering.

## Axioms

- **V3 — Kind exclusivity.** Within a deployment, each content kind has at most one registered Viewer. Two Viewers declaring the same kind is a boot-time error. The mapping content-kind → Viewer is a function.

- **V4 — Doc routing through kind.** Every Doc declares a content kind (implicit in its subtype). The framework routes the Doc to the Viewer registered for that kind. A Doc with a kind that has no registered Viewer is unrenderable; the framework refuses to register such a Doc.

- **V5 — One Viewer, many Docs.** A Viewer renders many Docs of its kind — every Doc of that kind in the deployment. A Viewer carries no Doc-specific state; the Doc id passed to it at request time is the only per-render variable.

- **V6 — Canonical URL composition.** A Viewer composes URLs deterministically from a Doc identifier via `urlFor(id)`. The URL it produces is the canonical address of any Doc of its kind. Two calls with the same id produce the same URL.

- **V7 — Read-only rendering.** A Viewer never mutates the Doc it renders, the framework's registries, or the user's session. The rendering pipeline is read-only end to end. Mirrors Doc A10.

- **V8 — Stateless across users.** A Viewer produces the same rendered output for the same `(Doc id, deployment-cosmetic-settings)` pair regardless of who is viewing. No per-user state, no per-session memory inside the Viewer. Mirrors Doc A11.

- **V9 — Registration as activation.** A Viewer is registered at framework boot via `Fixtures.contentViewers()` (or the equivalent registry surface). Unregistered Viewers are inert — their AppModules may exist as compiled code but are unreachable as content renderers.

- **V10 — Stateless functional object.** A Viewer's record carries no mutable state and no per-instance configuration; the type is the behaviour. New rendering variants are new Viewer subtypes, not parameterised instances of existing Viewers.

- **V11 — Chrome composition is mandated by the type system.** Every Viewer composes the framework's standard page chrome around its kind-specific content. The chrome is framework-owned and homogeneous across all viewers: a brand-aware Header (label, logo, breadcrumb chain, theme picker) at the top; the standard {@code st-root} / {@code st-main} layout primitives; the framework's audio runtime (RFC 0007) reachable on every page. A Viewer that omits any part of the chrome is structurally impossible — not a discipline violation, a *compile* failure.

  **Two-layer structure**:
  - **Bare-viewer layer** (subclass-supplied) — what's heterogeneous: the kind-specific body that populates the main slot, the typed Params and appMain record, the simpleName / paramsType / title. Each viewer is free to render the body however its kind requires.
  - **Common-infra layer** (framework-owned, *physically one type*) — the standard chrome construction. Lives in {@code app.DocViewer<P,M>} as {@code final} methods ({@code selfContent}, {@code imports}, {@code exports}). The chrome is encoded once; every concrete viewer inherits the exact same construction. No subclass can override it because the methods are {@code final}.

  The Viewer's *only* decision is **how to render its content** in the main slot. The shell around the content is non-negotiable and physically reused — not duplicated across viewers, not pluggable, not parameterisable beyond the body itself.

  This is the symmetric principle to "Chrome is framework-owned" for catalogue Cards (every tile goes through the framework's `Card`): every viewer composes the framework's page chrome (every page goes through the framework's `DocViewer` base).

  **Why a concrete final-methods abstract class** rather than a default-method interface: Java permits any record / class to override an interface's default method. Convention-based chrome composition (a `DocViewerChrome.compose(...)` helper that every viewer is expected to call) is fragile — the bug that introduced this axiom (`SvgViewer` skipped the helper and quietly lost the page header + audio cues) was exactly that failure mode. The abstract class with `final` methods removes the possibility entirely: subclasses *cannot* skip chrome because there's no method to override that controls it.

  **Concrete subclasses are classes, not records.** Java disallows record-extends-class; the trade-off is small (singleton {@code INSTANCE} + {@code final class} recover most record discipline) and the type-safety gain is large.

## Relationships

- **Viewer ↔ ContentKind.** Bijection within a deployment: one kind, one Viewer (V3). A kind without a Viewer is not a valid content kind.
- **Viewer ↔ Doc.** Each Doc routes through exactly one Viewer; each Viewer renders many Docs of its kind (V4, V5). The relationship is many-to-one (Doc → Viewer).
- **Viewer ↔ AppModule.** Each Viewer is paired with an AppModule that performs the rendering (V2). The AppModule is the addressable entity at the HTTP layer; the Viewer is the registration entry that declares "this AppModule serves this kind."
- **Viewer ↔ DocTree.** A DocTree's content leaves carry Docs of various kinds; navigating to a content leaf invokes the Viewer registered for that Doc's kind. The DocTree does not know about specific Viewers — it knows about leaf content; the Doc's kind drives the routing.

## Realisations

The Viewer realisations in the current framework:

- **DocReader** — renders `ProseDoc` content via the bundled `marked.js` markdown pipeline. Kind: prose markdown.
- **PlanAppHost** — renders `PlanDoc` content (structured trackers — objectives, decisions, phases, acceptance). Kind: plan.
- **SvgViewer** — renders `SvgDoc` content (inline SVG markup) centered on a standard page. Kind: svg. (RFC 0016.) First viewer realised through the typed `DocViewer<P,M>` base — chrome composition is structurally guaranteed.
- **StudioGraphInspector** — renders the live `StudioGraph` projection (RFC 0014). Kind: studio-graph (in TREE and TYPES view modes).
- **(Future) DiagramViewer, CodeViewer, ForceLayout3DViewer** — each registers its own kind; framework routing extends without code change.

All realisations compose the standard chrome per V11 — same Header, same breadcrumb chain (server-resolved from `/doc-refs`), same theme picker, same audio runtime. The kind-specific rendering happens only inside the main slot.

**Migration status (Phase 1):** `SvgViewer` is the first realisation to extend the typed `DocViewer<P,M>` base. The pre-existing viewers (`DocReader`, `PlanAppHost`, `CatalogueAppHost`, `StudioGraphInspector`) currently encode their own chrome in their `selfContent` — chrome conformance is via convention, not type. Migration to the typed base is a queued cleanup phase; until then, a conformance test asserting every Doc viewer extends `DocViewer` will catch regressions.

Downstream studios add Viewer realisations by implementing the framework's Viewer interface (when it lands) and registering via `Fixtures.contentViewers()`.

## Scope

This entry defines what a Viewer *is*. It does not contain operational guidance.

- **The shape of the standard chrome** (Header layout, breadcrumb visual style, theme-picker placement, audio runtime wiring) — *framework-owned, not operational*. Per V11, chrome is mandated, but its specific visual treatment is a framework concern, not a Viewer concern. Viewers compose chrome; they don't define what chrome looks like.

- **How to design a good viewer UX inside the main slot** — typography, navigation affordances within the body, keyboard shortcuts, scroll behaviour. Operational; lives in Doctrines or per-viewer design notes.
- **When to introduce a new viewer kind** vs reuse an existing one with parameters. Authoring guidance; lives in Doctrines.
- **What library to use** for rendering a given content shape — marked.js for markdown, three.js for 3D, cytoscape for graphs. Implementation choice; not ontological.
- **How to migrate content from one Viewer to another** when a kind is renamed or split. Operational; lives in migration plans.
- **Performance budgets, lazy loading, progressive rendering, MHTML export** — all operational concerns for individual Viewer implementations.

The split is intentional: changing how a Viewer renders is a frequent, evolving concern; changing what a Viewer *is* is a rare, foundational concern.

## Conformance

A `ViewerConformanceTest` is the intended build-time enforcer for these axioms over the registered Viewer set — kind uniqueness (V3), Doc-kind routing completeness (V4), URL determinism (V6), statelessness checks (V7, V8, V10). Not yet implemented; queued alongside the realisation of the typed `ContentViewer` interface from RFC 0015 §2.7.
