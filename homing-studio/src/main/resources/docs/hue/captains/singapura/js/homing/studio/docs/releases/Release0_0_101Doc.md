# 0.0.101 — Typed Studio Composition

| Field | Value |
|---|---|
| **Version** | 0.0.101 *(binary — fifth release)* |
| **Released** | 2026-05-15 |
| **Predecessor** | [0.0.100](#ref:rel-0-0-100) |
| **Highlight** | The framework's bootstrap reshapes from a ten-parameter static funnel into a typed record stack — `Studio` / `Umbrella` / `Fixtures` / `RuntimeParams` / `Bootstrap`. Per-studio `*Server.main()` collapses to three lines. Two new doctrines (Functional Objects, Weighed Complexity) name the principles the reshape obeys. |

---

## Summary

0.0.101 is one focused move with a long tail of clean-up.

**[RFC 0012 — Typed Studio Composition](#ref:rfc-12)** lifts the studio's shape into the type system. Every studio is now a `Studio<L0>` record declaring its home catalogue, intrinsic apps, plans, themes, and standalone brand in one place. Studios compose into a typed `Umbrella<S>` ADT (sealed `Group` / `Solo`) at deployment time. The harness — apps that wrap studios, action maps, theme registry, brand resolution, node chrome — moves onto a `Fixtures<S>` interface, the downstream extensibility seam. Deployment knobs (port, resource reader) live on `RuntimeParams`. And the bootstrap itself is a `Bootstrap<S, F>(F fixtures, RuntimeParams params)` record with a single no-arg `start()` method.

Two doctrines land in parallel to name what the reshape obeys.

**[Functional Objects](#ref:doc-fo)** — *no public static methods anywhere in the framework, ever*. Behaviour belongs on methods of immutable typed objects (stateless functional objects with an `INSTANCE` field when the behaviour is pure; dependency-holding records when it isn't). The legacy `StudioBootstrap.start(...)` static funnel was the canonical violation; this release removes it. Future code that introduces a public static method is a doctrine violation regardless of how trivial the body looks.

**[Weighed Complexity](#ref:doc-wc)** — *lines of code aren't equal cost*. The doctrine names the multi-dimensional measure (cognitive density, blast radius, reversibility, authoring tax, failure mode) that justified accepting RFC 0012's mechanical retypes across the catalogue spine. Filed during the work so the principle generalises beyond this RFC.

One side-effect: the cross-tree breadcrumb bug in the multi-studio demo (the source studio's L0 was being dropped between the host tile and its descendants) closes as a one-line fix in `CatalogueRegistry.augmentForProxy`. Visiting `Building Blocks` now reads `Homing Studios / Core / Homing / Building Blocks` rather than `Homing Studios / Core / Building Blocks`.

---

## What shipped

### Framework primitives (homing-studio-base)

- **`Studio<L0>` interface** — plain interface, six accessors (`home`, `catalogues`, `apps`, `plans`, `themes`, `standaloneBrand`), only `home()` is required. Every other field has a sensible default. A studio whose tree is wholly reachable from `home().subCatalogues()` declares nothing else but its brand.
- **`CatalogueClosure` functional object** — singleton `INSTANCE`, one method `walk(root)`. BFS over `Catalogue.subCatalogues()`, dedup by class. Used as the default body of `Studio.catalogues()`. Per the new Functional Objects doctrine: no static methods, instance-dispatched via the `INSTANCE` field.
- **`Umbrella<S>` sealed ADT** — `Group(name, summary, children)` + `Solo(studio)` records. Pure structure; no display chrome. Sealed-switch `studios()` method walks the tree to a flat list. A standalone deploy is a `Solo`; a multi-studio deploy is a `Group` of `Solo`s (or further nested `Group`s for richer categorisation).
- **`Fixtures<S>` interface** — the harness wrapping. Owns `umbrella()`, `harnessApps()`, `harnessGetActions()`, `harnessPostActions()`, `themeRegistry()`, `defaultTheme()`, `brand()`, and `chromeFor(node)` returning a `NodeChrome(badge, icon)` record. The brand defaults to the first studio's `standaloneBrand()`. Downstream that wants extra apps or actions writes its own `Fixtures<S>` subtype.
- **`DefaultFixtures<S>` record** — the framework's standard harness: the four harness apps (`CatalogueAppHost`, `PlanAppHost`, `DocReader`, `ThemesIntro`), a Solo/Group chrome switch, theme/brand defaults fall through to the interface.
- **`RuntimeParams` interface** + **`DefaultRuntimeParams(int port)` record** — deployment knobs. `port()` required, `resourceReader()` defaults to `ResourceReader.fromSystemProperty()`. Independent of Fixtures; a Fixtures arrives at the Bootstrap already-initialised with whatever downstream provides.
- **`Bootstrap<S, F extends Fixtures<S>>(F fixtures, RuntimeParams params)` record** — the typed bootstrap. Construct, call `start()`. No static methods, no `INSTANCE` field, no parameter explosion. The record IS the functional object. Composition logic (union apps / catalogues / plans, dedup by class, brand resolution, registry construction, Vert.x boot) is one private method.
- **Legacy `StudioBootstrap.java` deleted** — the 280-line procedural file with six `start(...)` overloads is gone. Zero public static methods left in the bootstrap path.

### Breadcrumb fix (homing-studio-base)

- **`CatalogueRegistry.augmentForProxy` keeps the source L0** — previously the cross-tree breadcrumb chain dropped `chain[0]` (the source studio's L0) on the theory that the host tile "replaces" the L0 slot. Correct when rendering the L0 itself, wrong when rendering a sub-page: visiting `BuildingBlocksCatalogue` was producing `Homing Studios / Core / Building Blocks` (missing the `Homing` rung). Fixed by `out.addAll(chain)` instead of `chain.subList(1, …)`. Result: `Homing Studios / Core / Homing / Building Blocks`.

### Per-studio records

Every existing source studio gains one `*Studio.java` record. The `*Server.main()` files collapse to three lines.

- **`HomingStudio`** (homing-studio) — owns `DocBrowser` as intrinsic app + all 10 plans + the "Homing · studio" brand with `StudioLogo`.
- **`SkillsStudio`** (homing-skills) — minimal: home is `SkillsHome`, no intrinsic apps, no plans, the "Homing · skills" brand.
- **`DemoBaseStudio`** (homing-demo) — minimal: home is `DemoStudio`, "Homing · demo" brand.
- **`MultiStudio`** (homing-demo, `studio/multi/`) — the umbrella studio for the multi-studio demo deploy. Owns `MultiStudioHome` (the synthetic L0 launcher) plus the three category L1s (Learning / Tooling / Core). Standalone brand is the turtle-logoed "Homing · multi-studio · demo" — wins as the harness brand because it sits at index 0 in the umbrella group.

### Server entry points

All three `*Server.main()` files now share the same shape:

```java
public static void main(String[] args) {
    Umbrella<Studio<?>> umbrella = new Umbrella.Solo<>(HomingStudio.INSTANCE);
    new Bootstrap<>(new DefaultFixtures<>(umbrella), new DefaultRuntimeParams(8080)).start();
}
```

The demo's `main()` swaps `Solo` for `Group<>("Homing Multi-Studio Demo", "…", List.of(Solo<>(MultiStudio.INSTANCE), Solo<>(DemoBaseStudio.INSTANCE), Solo<>(SkillsStudio.INSTANCE), Solo<>(HomingStudio.INSTANCE)))`. Same three lines, different umbrella shape.

### Documentation

- **[RFC 0012](#ref:rfc-12)** — Typed Studio Composition. Combined "typing improvement and standardised studio organisation" RFC; covers the four-piece type stack, the cascade (`F extends Fixtures<S>`), the role/owner split (studio author vs deployer vs harness author vs operator), the no-arg `start()`, and the implementation order.
- **[Functional Objects doctrine](#ref:doc-fo)** — *no public static methods anywhere*. Names the structural problem with statics (no polymorphism, no constructor injection, no class-level generics, ambient-dependency leakage, parameter explosion, test friction) and the alternative (singleton-`INSTANCE` records for pure behaviour, dependency-holding records for the rest). The framework refuses statics absolutely from this release forward.
- **[Weighed Complexity doctrine](#ref:doc-wc)** — *lines of code aren't equal cost*. Names the multi-dimensional measure (cognitive density × blast radius × reversibility × authoring tax × failure mode) so future decisions can be argued on the right grounds.
- **[Catalogue-as-Container doctrine](#ref:doc-cc)** — gains an *Open extensions — the Umbrella one level up* section, extending the open-set / closed-shape pattern from catalogue containers to studio-as-leaf-of-Umbrella.
- **Migration skill** (in `homing-skills`): `migrate-from-0-0-100` — one-change recipe for upgrading downstream studios from 0.0.100. Updated `create-homing-studio` skill teaches the new four-piece-stack pattern.

---

## Numbers

| Module | Tests passing | Net file count |
|---|---|---|
| `homing-studio-base` | (in reactor) | +7 new (`Studio`, `CatalogueClosure`, `Umbrella`, `Fixtures`, `DefaultFixtures`, `RuntimeParams`, `DefaultRuntimeParams`, `Bootstrap`), −1 deleted (`StudioBootstrap`), one bug fix (`CatalogueRegistry.augmentForProxy`) |
| `homing-studio` | (in reactor) | +1 (`HomingStudio`), `StudioServer.main()` collapsed, +1 release doc, +1 RFC (0012), +2 doctrines (Functional Objects, Weighed Complexity) |
| `homing-skills` | (in reactor) | +1 (`SkillsStudio`), `SkillsStudioServer.start(port)` collapsed, +1 skill (migrate-from-0-0-100), `create-homing-studio` skill updated |
| `homing-demo` | (in reactor) | +2 (`DemoBaseStudio`, `MultiStudio`), `DemoStudioServer.main()` collapsed |

**83 tests passing across the reactor.** No failing tests. No skipped tests. Conformance suites green across the board.

---

## Compatibility

**One breaking change**, mechanical to migrate. Every existing standalone studio's `*Server.main()` stops compiling because `StudioBootstrap.start(...)` is gone.

The migration is one new file (`MyStudio.java` — a Studio record carrying what was previously hand-listed in `main()`) and one rewrite (`MyStudioServer.main()` becomes three lines constructing an `Umbrella.Solo` + `DefaultFixtures` + `DefaultRuntimeParams` and calling `new Bootstrap<>(...).start()`). Total per-studio effort: ~5 minutes mechanical work, covered step-by-step in the `migrate-from-0-0-100` skill.

Dump the skill via `mvn -pl homing-skills exec:java -Dexec.mainClass=hue.captains.singapura.js.homing.skills.SkillsCli -Dexec.args="--target ./.claude/skills"` or read it in any studio running `homing-skills` at *Skills · Studio › Migrate from 0.0.100*.

**Browser support floor** unchanged from 0.0.100.

**Doctrinal change for new code.** The Functional Objects doctrine binds going forward — new framework code, new downstream code following framework conventions, and migration of any existing public static method we happen to touch during regular work. Existing public statics in the framework's source are debt; the doctrine doesn't sweep them proactively, but a regression that adds a new public static is a doctrine violation.

Maven artifact version stays at `1.0-SNAPSHOT`. Release identity is the git tag + this Doc.

---

## What's next

The work paths visible from 0.0.101's vantage:

- **`Fixtures.chromeFor` consumer in the rendering pipeline** — the interface ships in this release, the default Solo/Group switch is in place, but nothing in the breadcrumb / TOC / landing-tile renderers reads it yet. Wiring it through `DocReader` and `CatalogueGetAction` would be a small follow-up.
- **Synthesise multi-studio catalogue chrome from `Umbrella.Group`** — currently the demo still hand-codes `MultiStudioHome` + 3 `*StudioCategory` L1 catalogues. With `Fixtures.chromeFor` consumed, the framework could generate these from the umbrella's `Group` tree directly, eliminating the parallel hand-written catalogue chrome.
- **Direct `BootstrapTest` coverage** — `Bootstrap.compose()` is testable without Vert.x; a unit test exercising the dedup / brand-resolution / boot-error paths would lock the contracts.
- **More downstream-extensible Fixtures examples** — the skill mentions custom Fixtures briefly. A worked example downstream module would solidify the seam.
- **Existing public statics audit** — the Functional Objects doctrine binds future code, but a one-time scan of the framework's existing statics (most are debt, some are constants and stay) would give a list for opportunistic migration.

The bootstrap is now the same shape every other typed primitive in the framework is: a record with methods. 0.0.102 can be smaller again.
