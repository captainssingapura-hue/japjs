# 0.0.100 — Typed Levels, Wallpapers, Audio Cues, Interactive Themes

| Field | Value |
|---|---|
| **Version** | 0.0.100 *(binary — fourth release)* |
| **Released** | 2026-05-12 |
| **Predecessor** | [0.0.11](#ref:rel-0-0-11) |
| **Highlight** | The framework's sensory layer fills out — wallpaper backdrops, typed audio cues, and a worked-example interactive theme. In parallel, the catalogue tree moves into the type system: sealed `L0..L8` family, typed `parent()`, split `subCatalogues()` / `leaves()`. Defect 0004 (flat breadcrumbs) closes structurally. |

---

## Summary

0.0.100 has two parallel headlines.

**The sensory layer goes typed end-to-end.** [RFC 0006](#ref:rfc-6) lands wallpaper backdrops as a first-class theme mechanism (the `Theme.backdrop()` hook seeded in 0.0.11 is now a stable contract — and the writing-media textures dimension is wired in). [RFC 0007](#ref:rfc-7) ships the typed `Cue` ADT for theme audio — a sealed sum of Notes, Chords, Sequences, and full synthesised cues that themes can attach to navigation / hover / focus events without ever touching Tone.js directly. [RFC 0008](#ref:rfc-8) demonstrates the whole stack converging: Jazz Drum Kit, a theme that is *also* an interactive instrument — every drum element on the page responds to clicks with sampled-synth percussion, themes-as-experience as the worked example.

**The catalogue tree's shape becomes a compile-time fact.** [RFC 0005-ext2](#ref:rfc-5-ext2) refactors `Catalogue` from a flat single-method interface into a sealed family of 9 typed level interfaces (`L0_Catalogue`, `L1_Catalogue<P>`, …, `L8_Catalogue<P>`). Non-root catalogues declare a typed `parent()` whose return type is constrained by the parent's level — the breadcrumb chain is now walked through that typed accessor, not inferred at runtime. The `entries()` method splits into `subCatalogues()` (typed by level — children of an L2 are bounded to L3) and `leaves()` (Docs / Plans / AppModules). Cycle detection, multi-parent detection, and depth-correctness all collapse from runtime checks to compile errors. [Defect 0004](#ref:def-4) — the flat-breadcrumb bug that motivated the work — closes as a direct consequence: doc and plan pages now render the full catalogue chain (`Studio › RFCs › Architecture › RFC 0005-ext2`) without renderer or URL changes.

The studio itself dogfoods the new typed levels with two parallel L1→L2 sub-trees: a new `RfcsCatalogue` (Architecture / Documents / Visual System) and a re-divided `JourneysCatalogue` (RFCs / Operations). What used to be a 12-tile flat RFC list is now an organised browse surface.

---

## What shipped

### Framework primitives (homing-core / homing-server)

- **Sealed `Catalogue` family** ([RFC 0005-ext2](#ref:rfc-5-ext2)) — 9 new non-sealed interfaces in `homing-studio-base/.../app/`: `L0_Catalogue` (root, no parent), then `L1_Catalogue<P extends L0_Catalogue>` through `L8_Catalogue<P extends L7_Catalogue<?>>`. Each non-root level declares `P parent()`. The `Catalogue` base interface gains `subCatalogues()` (typed by level) and `leaves()` (the Entry sum); `entries()` is gone.
- **`Entry` sum slimmed to 3 variants** — `OfDoc | OfApp | OfPlan`. `Entry.OfCatalogue` and the `Entry.of(Catalogue)` factory are removed; sub-catalogues live in their own typed accessor.
- **`Cue` ADT** ([RFC 0007](#ref:rfc-7)) — sealed sum covering `Note`, `Chord`, `Sequence`, and full theme cues. Themes attach cues to navigation / hover / focus surfaces; the framework's Tone.js plumbing renders them. Per-element WeakMap debounce solves the fast-hover-sweep silencing.
- **`Theme.backdrop()` typed contract** ([RFC 0006](#ref:rfc-6)) — the seeded hook from 0.0.11 stabilises. Themes return an `SvgRef<?>`; the framework injects `<div class="theme-backdrop"><svg>…</svg></div>` as `<body>`'s first child. Writing-media textures (the second theme dimension RFC 0006 proposes) wire through the same path.
- **`CatalogueRegistry` reverse indices** — `Map<UUID, Catalogue> docHome` and `Map<Class<? extends Plan>, Catalogue> planHome` built at construction. Power the typed breadcrumb derivation: `breadcrumbsForDoc(uuid)` / `breadcrumbsForPlan(class)` walk the typed `parent()` chain from the leaf to root. Cycle and multi-parent runtime checks are deleted — both are impossible at the type level now.
- **`DocRefsGetAction` carries `breadcrumbs`** — the typed catalogue chain is serialised alongside the doc's title / summary / references. `DocReader` consumes it and replaces the legacy `[{ text: "Home" }]` stub with the full chain.
- **`PlanGetAction` carries `breadcrumbs`** — same treatment. `PlanHostRenderer._brandHeader` prefers `data.breadcrumbs` over the single brand-label crumb.

### Studio chrome (homing-studio-base)

- **Audio cue plumbing in `Header` + `Card` + `ListItem`** — every clickable surface in the chrome wires through a `Cue` lookup when the active theme declares one. Themes that don't ship cues are unaffected.
- **Render order for catalogue listings** — Option A from RFC 0005-ext2 §11: sub-catalogues render before leaves. Within-group ordering preserved.
- **Brand-aware document `<title>`** — a new `AppMeta` record in `homing-server` carries the downstream studio's brand label through `AppHtmlGetAction`, so the served HTML's `<title>` is `<page-kind> · <brand>` from byte zero (no framework-default flash). The four hardcoded `AppModule.title()` strings (`"Homing · studio · doc"`, `"studio · catalogue"`, …) collapse to bare page-kind labels (`"doc"`, `"catalogue"`, `"plan"`, `"themes"`); `AppMeta.label()` provides the brand half. Each renderer further refines `document.title` to `<page-subject> · <brand>` (e.g. `"Doctrine — Dual-Audience Skills · Homing · studio"`) once the per-page data arrives.

### Offline reading export (MHTML)

The doc reader is now reliably exportable via the browser's *Save As → Webpage, Single File* (MHTML). Three small changes converge to make it work:

- **TOC links survive MHTML's anchor-rewriting.** Chrome's MHTML save rewrites fragment-only `href="#slug"` attributes to absolute URLs, which on reopen attempt cross-document navigation (back to a server that doesn't exist offline). `DocReaderRenderer` now sets an inline `onclick` attribute that calls `scrollIntoView` and short-circuits with `return false` — inline event attributes are preserved verbatim in MHTML and execute when the saved file reopens, regardless of how the href was rewritten. A small `_tocScrollHandler(slug)` helper keeps the handler-string construction in one named place.
- **Brand-correct `<title>` survives the save.** Tab titles in the exported file now read `<doc-title> · <brand>` instead of the framework-default `"Homing · studio · doc"` — same string the user sees live, courtesy of the `AppMeta` work above plus the renderers' `document.title` update at load time.
- **Themes survive intact.** Every CSS bundle (cascade-layered `StudioStyles` + per-theme `chunks()`) inlines as text into the saved file; `Theme.backdrop()` SVGs are inline DOM (not background-image sandboxes), so their classed elements + CSS hover effects survive losslessly. A doc saved under Retro 90s reads as Retro 90s offline; saved under Maple Bridge, the moon still grows on hover.

What does **not** survive MHTML export is the framework JS runtime — the ES modules served from `/homing/js/…` can't resolve from a `file://` reopen, so RFC 0007 audio cues fall silent, the `IntersectionObserver` scroll-spy active-highlight stops following the heading you're reading, and the theme picker / play-mode toggle become inert. Acceptable trade-off: the reader keeps the content, formatting, theme, breadcrumbs, working TOC, and brand identity in a single ~30–80 KB file — much smaller than the ~400–800 KB a print-to-PDF produces, and with text reflow + theme fidelity that PDF can't match.

### New theme: HomingJazzDrumKit ([RFC 0008](#ref:rfc-8))

Activate with `?theme=jazz-drum-kit`. The first theme that is also an interactive instrument.

- **Inline-DOM backdrop** rendering a full drum kit (kick + snare + hi-hat + ride + crash + two toms + floor tom + sticks). Each drum element is classed and instrumented.
- **Per-drum audio cue** — clicking any drum triggers its sampled percussive cue through the RFC 0007 `Cue` pipeline. Polyphony fixed via render-once-play-many.
- **Fast-hover sweep silencing fix** — per-element WeakMap debounce (documented as a [Gotcha](#ref:def-4)-adjacent fix) ensures rapid mouseovers don't double-fire and silence the second cue.
- **The theme demonstrates a doctrine point** — themes vary paint, shape, *and now sound* (RFC 0007), but not behaviour. Each drum click goes through the same `Cue.play()` entry point any other theme element would use; the theme adds no JS event handling beyond what the framework provides.

### Studio dogfood — the catalogue tree itself

The `homing-studio` workspace now showcases the typed-levels work it ships:

- **New L1 `RfcsCatalogue`** with three L2 sub-catalogues: `ArchitectureRfcsCatalogue` (RFC 0001 + 0005 family), `ContentRfcsCatalogue` (RFC 0004 + 0004-ext1), `VisualSystemRfcsCatalogue` (RFC 0002, 0002-ext1, 0003, 0006, 0007, 0008). The previously flat RFC list reads as a structured browse surface.
- **`JourneysCatalogue` re-divided** into `RfcJourneysCatalogue` (7 RFC plan trackers) + `OperationsJourneysCatalogue` (Rename, V1, Instruments). The studio is now genuinely L2-deep in two parallel branches — exercising the typed-levels stack at depth.
- **`StudioCatalogue.subCatalogues()`** lists 5 typed L1 children; **`StudioCatalogue.leaves()`** holds the two flat-list Navigables (DocBrowser, Themes).

### Documentation

- **[RFC 0005-ext2](#ref:rfc-5-ext2)** — Typed Catalogue Levels. 11-section design + the §11 sub-catalogue / leaf refinement that completed the type-system migration.
- **[Defect 0004](#ref:def-4)** — Flat Breadcrumbs in Multi-Level Catalogues. Created and closed in the same release window; the resolution narrative explains why a runtime breadcrumb walk was the wrong fix and structural typing was the right one.
- **[Catalogue-as-Container doctrine](#ref:doc-cc)** — gains the *Typed levels* section. Notes that "multiple parents" is no longer permitted (was always a runtime check; now a compile error). Documents the bounded-depth invariant — `L8` is terminal, adding `L9` requires the framework to declare the new permit.
- **[Release Checklist](#ref:checklist)** — new canonical guide codifying the seven-step recipe (scope sweep, build green, Doc + .md, three-place registration, re-test, migration skill if breaking, git tag). Extracted from the pattern 0.0.11 established and used as the spec for this release.
- **Migration skill** (in `homing-skills`): `migrate-from-0-0-11` — six-change mechanical recipe for upgrading downstream studios. Updated `create-homing-studio` skill to teach the new typed-level API.
- **`homing-skills` module is now portable** — canonical `SKILL.md` sources moved from the repo-root `.claude/skills/` directory (gitignored, dev-local) into the module itself at `homing-skills/src/main/resources/claude-skills/<slug>/SKILL.md`. The cross-module resource path in `homing-skills/pom.xml` is gone; the module now builds standalone (`mvn -pl homing-skills install` on a fresh clone produces the same jar as the full reactor — previously it shipped an empty `claude-skills/` directory because the cross-module path silently resolved to nothing). The repo-root `.claude/skills/` mirror — used by Claude Code's in-repo skill discovery — is now generated on demand from the dump CLI: `mvn -pl homing-skills exec:java -Dexec.mainClass=hue.captains.singapura.js.homing.skills.SkillsCli -Dexec.args="--target .claude/skills"`. Single source of truth; framework dogfoods its own dump path.

---

## Numbers

| Module | Tests passing | Net file count |
|---|---|---|
| `homing-core` | (in reactor) | typed `Cue` ADT + `Layer` ladder additions |
| `homing-server` | 83 tests | breadcrumb plumbing in `DocRefsGetAction` + `PlanGetAction` |
| `homing-studio-base` | 23 tests | +9 `L<N>_Catalogue` interfaces, `Entry` slimmed, `CatalogueRegistry` rewritten, audio cue wiring in chrome |
| `homing-studio` | 159 tests | +1 release doc, +1 release checklist guide, +1 defect (0004), +3 RFCs (0005-ext2, 0007, 0008 + 0006 implementation), +6 sub-catalogues (RfcsCatalogue + 3 L2 RFC cats + 2 L2 Journeys cats), catalogue tree restructured |
| `homing-skills` | (in reactor) | +1 skill (migrate-from-0-0-11), `create-homing-studio` skill updated for new API |

Reactor `mvn install` clean. No failing tests. No skipped tests. Conformance suites green across the board (Doc / Href / CSS / Catalogue / Plan / ManagerInjection / CdnFree / GroupImplConsistency / Doctrine).

---

## Typed catalogue levels ↔ what was once runtime

The headline tractor pull of RFC 0005-ext2:

| Invariant the framework wants | 0.0.11 | 0.0.100 |
|---|---|---|
| Catalogue has exactly one parent | Boot-time DFS / map-walk error | **Compile error** — a class can only declare one parent type |
| Tree has no cycles | Boot-time DFS error | **Compile error** — `L<N>` parents must be `L<N-1>`; chain strictly descends |
| Sub-catalogue is exactly one level deeper than its container | Boot-time validation | **Compile error** — `subCatalogues()` is bounded to the next level |
| Brand home-app is the root | Boot-time validation | Boot-time validation *(narrowed to a level-check)* |
| Doc / Plan UUID resolvable | Boot-time validation | Boot-time validation |
| Breadcrumb chain for any doc/plan | Inferred from `Entry.OfCatalogue` walk | **Walked via typed `parent()`** in `N+1` calls |

What remains as a runtime check is closure (every sub-catalogue is in the registered list), instance-equality parent-match (the singleton-INSTANCE convention can't be enforced by types alone), doc/plan reachability, and the L8-terminal invariant (no L9 type exists to constrain against).

---

## Compatibility

**Six breaking changes**, all caught by the compiler, all mechanical to migrate. Every change is local to one record at a time.

1. `implements Catalogue` no longer compiles — pick a level (`L0_Catalogue` for root, `L1_Catalogue<Root>` for direct children, etc.).
2. `entries()` is gone — split into `subCatalogues()` (typed by level) and `leaves()` (the Entry sum).
3. `Entry.OfCatalogue` variant and `Entry.of(Catalogue)` factory are removed.
4. Non-root catalogues must declare `public ParentClass parent() { return ParentClass.INSTANCE; }`.
5. A class can only declare one parent type. Multi-parent for a catalogue is no longer allowed.
6. Render order for catalogues with both sub-catalogues and leaves is now fixed: sub-catalogues first, leaves second.

**Migration is covered by the `migrate-from-0-0-11` skill** shipped in `homing-skills`. Dump it via `mvn exec:java -Dexec.mainClass=hue.captains.singapura.js.homing.skills.SkillsCli -Dexec.args="--target ./.claude/skills"` or read it in any studio that runs `homing-skills` at `Skills · Studio › Migrate from 0.0.11`.

**Browser support floor** unchanged from 0.0.11 — `@layer` (Chrome 99 / Firefox 97 / Safari 15.4) and `:has()` (Chrome 105 / Firefox 121 / Safari 15.4). RFC 0007 adds Tone.js (carried by the `homing-libs` bundle, no CDN); RFC 0006 wallpapers + RFC 0008 interactive theme are pure SVG + CSS, no new runtime requirements.

**`homing-skills` layout change.** Forks or downstream projects that authored skills under the repo-root `.claude/skills/` directory and relied on the old `homing-skills/pom.xml` cross-module resource path will see their custom skills no longer make it into the built jar — the resource block is gone. Migrating is mechanical: move `<your-skill>/SKILL.md` files from `<repo>/.claude/skills/` into `<homing-skills-fork>/src/main/resources/claude-skills/`, then rebuild. The framework's own four skills are already moved. Contributors working on the framework regenerate the repo-root `.claude/skills/` mirror via `mvn -pl homing-skills exec:java -Dexec.mainClass=hue.captains.singapura.js.homing.skills.SkillsCli -Dexec.args="--target .claude/skills"` whenever they want Claude Code's in-repo discovery to see updated content.

Maven artifact version stays at `1.0-SNAPSHOT`. The release identity is the git tag and this Doc. Downstream studios on 0.0.11 upgrade by bumping the version they consume + running through the migration skill (typical effort: 5–15 minutes per studio).

---

## What's next

The work paths visible from 0.0.100's vantage:

- **Per-doc preferred home** — a doc that's referenced from both a structured catalogue and the flat `DocBrowser` now picks its breadcrumb home via first-registered-wins. Most cases this is the right behaviour; for the others we'd want a typed "preferred catalogue" hint on the Doc itself.
- **Writing-media texture library** ([RFC 0006](#ref:rfc-6) §library) — the 10-SVG historical-textures pack (vellum, silk, bamboo, papyrus, …). Mechanical work once the typed `WritingMedium` contract is stable.
- **More themes opting into RFC 0007 cues** — Maple Bridge could carry a temple-bell cue on navigation; Retro 90s a CRT-power-on chord. Both would take ~20 lines per theme.
- **Skills for the audio + backdrop pipelines** — a `create-themed-experience` skill complementing `create-homing-theme`, covering the `Cue` + `Theme.backdrop()` patterns specifically.
- **Formalise MHTML export as a one-button feature** — `Save As → Webpage, Single File` already works as of this release; a small "Export" affordance in the doc-reader chrome (and a matching `offline-reading-export` skill) would surface the capability for downstream studios without requiring users to discover it from the browser menu.

The framework gets simpler at every layer where typing took over from runtime; the surface authors hold gets richer at the layers (sensory, navigation) where typing now serves the user-visible work. 0.0.101 has room to be a smaller, less-foundational release.
