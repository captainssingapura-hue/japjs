# Building Blocks

Everything reusable in `homing-studio-base` that downstream studios compose. The promise: **no JS to write for the common case**. Every page (catalogue, doc browser, doc reader, plan tracker) is a kit; you provide typed Java data, the framework auto-generates the served JS.

For richer surfaces, drop down to the **atoms** — 13 small visual builders that compose any UI shape. Below those, **typed data shapes** that data adapters fill, and **conformance** test bases that gate downstream's correctness.

---

## Index

- [**Atoms** — `StudioElements`](#ref:atoms) — 13 visual builders (Header, Card, Pill, Section, Footer, StatusBadge, OverallProgress, StepCard, DecisionCard, TodoList, MetricsTable, Panel, Brand). Building blocks for everything above.

- [**Catalogue Kit** — `CatalogueAppModule`](#ref:cat-kit) — auto-generates a launcher / sub-catalogue / index page from typed Java data. Used by Studio's home, Journeys, Doctrines, Building Blocks pages themselves.

- [**DocBrowser & DocReader Kits**](#ref:doc-kits) — searchable card grid + shared markdown reader. Pair them and your studio has a documentation surface with zero JS.

- [**Tracker Kit** — `PlanAppModule` + `PlanStepAppModule`](#ref:trk-kit) — two-page tracker for any multi-phase plan. Implement `Plan`, get a working tracker. Includes the `Metric` primitive for before/after measurement display.

- [**Bootstrap & Conformance**](#ref:bac) — `StudioBootstrap.start(...)` (one-call server) + the five conformance test bases (Doctrine, CdnFree, Css, Href, CssGroupImplConsistency).

---

## Promise per goal

| Downstream goal | What you write | What you get |
|---|---|---|
| Home page with launcher tiles | `MyHome implements CatalogueAppModule` (~50 LoC, no JS) | Auto-generated launcher, header + breadcrumbs + tile grid + footer |
| Doc browser + reader | `MyDocBrowser implements DocBrowserAppModule` + register `DocReader.INSTANCE` | Search / filter / grouped card grid + markdown rendering with TOC |
| Plan tracker | `MyPlanData implements Plan` + thin `MyPlan` / `MyStep` (~190 LoC across three files) | Index page (overall progress + decisions + phase cards) + per-phase detail page (tasks / deps / metrics / acceptance / nav) |
| Custom-shape page | Compose atoms in a small `selfContent` block | Doctrine-compliant DOM construction; conformance-test passing |
| Server | `StudioBootstrap.start(port, apps)` | Vert.x host, `/`, `/app`, `/module`, `/css-content`, `/doc`, `/theme-vars`, `/theme-globals`, `/asset` (when a `ThemeAssets` is registered) |

This index page itself is built from these blocks — a `CatalogueAppModule` page listing markdown docs that the shared `DocReader` renders. Recursive proof that the kit covers its own surface.
