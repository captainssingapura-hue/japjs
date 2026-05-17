# Ontology — Studio

A **Studio** is a unit of typed composition. It declares everything one logical product brings to a server — a home catalogue, the apps and plans and themes that ship with it, optionally a standalone brand — and is composed with other Studios under an Umbrella to form a runtime.

This entry states what a Studio *is*. It does not prescribe how to scope a Studio (one product, one feature area, one bounded context), when to split a monolithic Studio into multiple, how to share content across Studios, or how to design the Studio's L0 catalogue. Those are operational concerns and belong in Doctrines. See *Scope* below.

## Definition

A Studio is a stateless, immutable, class-identified, singleton typed-composition unit. It declares exactly one home L0 catalogue and contributes apps, plans, themes, and an optional standalone brand to whatever Bootstrap composes it.

## Identity

- **S1 — Class-identified.** A Studio is identified by its Java class. Two Studios are the same Studio if and only if their classes are equal.
- **S2 — Singleton instance.** Each Studio class has exactly one instance — the framework's `INSTANCE` constant convention. The framework refuses to compose two distinct instances of the same Studio class.

## Axioms

- **S3 — Exactly one home.** Every Studio declares exactly one L0 catalogue via `home()`. The home is the studio's structural entry point; it cannot be null, cannot be an L1 or deeper catalogue, and cannot vary across instances of the same Studio class.

- **S4 — Five contribution surfaces.** A Studio contributes to the composed runtime through exactly five surfaces:
  - `home()` — the L0 catalogue (required, S3)
  - `apps()` — the AppModules this Studio adds
  - `catalogues()` — the catalogues reachable from `home()` (typically derived automatically via the catalogue closure)
  - `plans()` — the Plan trackers this Studio ships
  - `themes()` — the Themes this Studio registers

  Plus one optional declaration:
  - `standaloneBrand()` — the Studio's identity when running solo (label, logo); ignored when composed under an Umbrella that supplies its own brand.

  No other contribution surface exists; Studios cannot reach into framework internals to inject behaviour.

- **S5 — Statelessness.** A Studio carries no mutable state, no per-user state, no per-session state, no per-request state. Its declaration is fixed at JVM startup and identical across the process lifetime.

- **S6 — Immutability of declaration.** A Studio's contributions are immutable values — the same `home()`, the same `apps()`, the same `plans()`, the same `themes()` every time. The framework caches contributions safely.

- **S7 — Composability under Umbrella.** A Studio can be composed solo (`Umbrella.Solo`) or grouped with peers (`Umbrella.Group`) without modification. The Studio is unaware of the umbrella's shape; the Bootstrap layer arranges composition.

- **S8 — Closed contribution set.** A Studio contributes through `home()`, `apps()`, `catalogues()`, `plans()`, `themes()`, and `standaloneBrand()` — and through nothing else. The contribution shape is closed at the protocol level; new contribution kinds require an extension of the Studio ontology, not a back-door registration.

## Relationships

- **Studio ↔ L0_Catalogue.** Each Studio has exactly one home L0 catalogue (S3); each L0 is the home of at most one Studio (otherwise it would not be a single product's root).
- **Studio ↔ Bootstrap.** A Studio is composed into a `Bootstrap` (alone or with peers under an Umbrella). The Bootstrap is the composition; the Studio is the contributor.
- **Studio ↔ Umbrella.** Studios compose under `Umbrella.Solo` (one Studio) or `Umbrella.Group` (many Studios). The Umbrella is structural; it carries no behaviour beyond grouping.
- **Studio ↔ AppModule.** A Studio contributes AppModules via `apps()`; framework apps (harness apps) are contributed separately via `Fixtures`.
- **Studio ↔ Plan.** A Studio contributes Plan trackers via `plans()`; plans become Docs (per RFC 0015 PlanDoc) and live as catalogue leaves.
- **Studio ↔ Theme.** A Studio contributes Themes via `themes()`; themes affect rendering but are orthogonal to content identity.
- **Studio ↔ DocTree.** Every Studio's `home()` is an L0_Catalogue, which is a DocTree root (per DocTree T9). Studios are how DocTrees of typed-catalogue realisation enter the runtime.

## Realisations

The Studios in the current framework:

- **HomingStudio** — the framework's own self-documentation Studio. Home: `StudioCatalogue`.
- **DemoBaseStudio** — the multi-studio demo's content-bearing Studio. Home: `DemoBaseHome`.
- **MultiStudio** — the multi-studio demo's umbrella launcher. Home: `MultiStudioHome` (which has `Entry.OfStudio` leaves wrapping the other Studios' L0s per RFC 0011).
- **SkillsStudio** — packaged skills delivered as catalogued docs. Home: `SkillsCatalogue`.

Downstream Studios add their own by implementing the `Studio<S>` interface and providing an `INSTANCE` constant.

## Scope

This entry defines what a Studio *is*. It does not contain operational guidance.

- **How to scope a Studio** — one product, one feature area, one bounded context. Authoring decision; lives in Doctrines.
- **When to split a Studio** into multiple as it grows. Refactoring guidance; lives in Doctrines.
- **How to share content across Studios** — typed cross-references (RFC 0011), shared catalogues, shared docs registered in both. Composition guidance; lives in Doctrines.
- **How to design the L0 catalogue's children** — depth, fan-out, naming. Catalogue authoring; lives in Doctrines.
- **When to ship a Theme with a Studio** vs let downstream choose. UX policy; lives in Doctrines.
- **How `standaloneBrand` resolves under different Umbrella compositions** — implementation detail of the Bootstrap layer; not ontological.

The split is intentional: changing how to organise Studios is a frequent, evolving concern; changing what a Studio *is* is a rare, foundational concern.

## Conformance

A `StudioConformanceTest` is the intended build-time enforcer for these axioms over the registered Studio set — singleton verification (S2), home L0 type (S3), declaration immutability (S6), no cross-Studio class collisions. Not yet implemented; queued alongside the typed Studio family work.
