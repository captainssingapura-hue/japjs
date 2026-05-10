# Rename Dossier — japjs → Homing

**Status:** ✅ **Implemented** (2026-05-04). The rename has been executed in 6 phases. Final naming: project `homing.js`, groupId `io.github.captainssingapura-hue.homing.js`, child artifactIds `homing-{core,server,conformance,demo,studio}`, Java package root `hue.captains.singapura.js.homing`. See `docs/rename/EXECUTION-PLAN.md` and the live tracker at `/app?app=rename-plan` for the executed plan.

**Date drafted:** 2026-04-25
**Date implemented:** 2026-05-04
**Decision context:** End of an extended brand-and-positioning session. Engineering work resumes on the action plan; the rename is a clean sweep to be executed before — or as part of — Phase 0 (Spring Boot adapter).

---

## 1. The decision in one paragraph

The project name **japjs** is being retired in favor of **Homing**. The driver is that "jap" carries an English-language ethnic slur connotation that creates a permanent ceiling on adoption — HR, comms, conferences, enterprise procurement will all stumble on it, regardless of the name's innocent origin. The new name was chosen for a three-layer metaphor (homecoming → sad truth → fire and forget) that maps unusually tightly to the framework's actual technical principles. Renaming costs roughly one day of focused work *now*, versus a release-cycle of upheaval if deferred past first external adoption.

---

## 2. Why japjs had to go

The "jap" syllable, regardless of intended derivation (likely *Java + AppS + JS* or a coined construction), reads in English as a slur for people of Japanese descent. The connotation is well-known across international tech communities. Even when the team and the project mean nothing offensive by it, the consequences accumulate:

- HR and comms teams in target customer organizations will flag the name in due-diligence reviews.
- Conference organizers will tiptoe around speaker bios that mention it.
- Job postings referencing the framework will sound off.
- Translated documentation in any locale that recognizes the slur becomes uncomfortable.
- Search-engine results bring up unrelated and unfortunate adjacencies.

None of this is fixable through clever taglines. The name itself is the wound. Renaming is the only clean response.

The cost of doing this *now*, before adoption: roughly half a day of mechanical refactoring plus half a day of logo / asset regeneration. The cost of doing it *after* adoption: months of breaking-change communication, broken external links, abandoned tutorials, and reputation residue.

---

## 3. Candidates considered

A roughly thirty-name search across multiple naming directions. Recorded here so the search doesn't repeat next time.

### Geographic (neighbors of Java the island)
- **Sunda** — the strait between Java and Sumatra; perfect bridge metaphor; modest cultural-naming consideration
- **Bali, Borneo, Sumatra** — heavier SEO collision (Sumatra PDF Reader, etc.)

### Bridge / structural metaphor
- **Strand** — a connecting filament; clean, accessible
- **Joist** — horizontal structural beam; workspace-aligned
- **Truss** — bridge support; rejected (Liz Truss connotation in UK politics)
- **Lattice** — type-theory adjacent; slightly long
- **Span** — too generic

### Forge / craft (records as forged artifacts)
- **Forge, Anvil, Crucible** — all already taken by major dev tools
- **Kiln, Mint, Stamp** — interesting but underdeveloped

### Coined J-names (preserve mark continuity)
- **Jove** — Roman name for Jupiter; archaic but weighty
- **Jett** — sharp, short; some collisions
- **Jasper** — fatal collision with JasperReports

### Friendly / approachable tone
- **Knit** — short, warm, encodes wiring; underrated
- **Bun, Yarn, Pug** — all taken (JS runtimes / templating)
- **Brio** — Italian; minor collisions

### Home / homecoming theme (the eventual winner's category)
- **Hearth** — strong, the home itself; static
- **Homy / Homey** — first proposal; brand collision (Athom Homey smart-home), tone clash with serious brand voice, `.js` suffix steers wrong
- **Haven, Roost, Lodge** — variants on home/return
- **Domus** — Latin for home; some Domus magazine / Academy collisions
- **Homestead** — strong but long
- **Hither** — archaic but distinctive
- **Homing** — *winner*

---

## 4. Why Homing won

**Homing** carries three distinct readings, each true to a different facet of the framework. A name with one resonance is a name. A name with three is a thesis.

### Reading 1 — Homecoming (the public face)

> *"Where JavaScript comes home to Java."*

The framework reverses the usual JS-first relationship. Most frameworks are JS-first with Java as backend support; this one declares in Java and treats JS as a downstream artifact. "Coming home" is the emotional and conceptual summary of that reversal — return, belonging, reunion, the end of a journey.

This reading is what executives, marketing materials, conference talks, and first-impression docs lead with. It is warm, narrative, accessible, and tells a story even non-engineers can carry.

### Reading 2 — The sad truth (the honesty layer)

> *"JavaScript is homing — not home."*

"Homing" is a gerund — it implies *motion toward*, not arrival. The framework's actual physics: it installs a *gravitational pull* toward Java (typed records, generated bindings, compile-time enforcement), but it cannot transmute JS code into Java. JS still executes as JS in the browser. The journey home is partial.

This honest framing matches the project's existing voice — every claim in the white paper is marked `built` / `designed` / `proposed`; the brochure has a five-point caveats list under the executive summary; the action plan defers things explicitly rather than pretending. **"Homing" makes structural-honesty the name itself.** A framework whose grammar embodies its truth is rare and valuable.

### Reading 3 — Fire and forget (the engineering aesthetic)

> *Declare once in Java; the framework tracks the typed dependencies autonomously.*

The third resonance, surfaced during the naming conversation, is the most technically precise. "Fire and forget" describes the framework's actual operating principle:

| Phase | Imperative-stack reality | Homing reality |
|---|---|---|
| Declare ("fire") | Write the type, the JS import, the CSS class string, the TS interface, the API client | Write one Java record |
| Track | Manually keep all sites in sync forever | Nothing — `javac` enforces it |
| Verify | Hope tests cover the right paths | Compile-time guarantee + conformance scanner |
| Refactor | Rename and chase every consumer manually | Rename in Java; consumers break at compile time |

Every primitive in Homing is a fire-and-forget declaration:

| Feature | Fire | Forget |
|---|---|---|
| Module imports | Declare a record | Never write `import { btn } from "..."` again |
| CSS classes | Declare a `CssGroup` | Never type the kebab-case string `"btn-primary"` |
| SVG bundling | Declare an `SvgBeing` | Never write `<img src="...">` or load logic |
| External libs | Declare an `ExternalModule` | Never hand-write a CDN import shim |
| Typed bus *(designed)* | Declare a channel + message records | Never hand-maintain JSON / TS / wire formats |
| Workspace layout *(designed)* | Declare `WorkspaceLayout` | Never hand-write JSON serialization |

This reading is for engineer-to-engineer audiences only — technical docs, design rationale, conference talks, code-review vocabulary. The military-systems baggage of "fire and forget" would jar a CTO reading the brochure, so it is **explicitly not** for executive-facing materials.

---

## 5. Brand voice and audience mapping

| Audience | Layer | Vocabulary |
|---|---|---|
| Executives, decision-makers | Homecoming | "Where JavaScript comes home to Java." Warm, narrative, no engineering jargon. |
| Engineers (primary daily users) | Fire and forget | "Declare once; the framework delivers wherever it's needed." Precise, technical. |
| Anyone reading the README in detail | Sad truth | "JS is homing, not home." Honest about partial-progress; no overclaim. |

One word, three audiences, no conflict. Each audience reads the layer they recognize.

---

## 6. Tagline candidates (in order of recommendation)

1. **"Homing — where JavaScript finds its way back to Java."** *(Recommended)*
2. **"For when JavaScript needs to come home — even if it can't quite arrive."** *(Most poetic; honest about incompleteness)*
3. **"The homing framework. JavaScript with a Java compass."**
4. **"JavaScript is homing, not home. Type-safe pathways back to the JVM."** *(Most engineering-flavored)*

The first is the safest first-impression line. The second is the strongest writer's-magazine quote. Use the first for the website hero; use the second on the brochure cover.

---

## 7. Visual identity direction

The current japjs logo set is in `docs/brand/`. Eight assets built around a lowercase `j` glyph + bridged amber tittles motif. The Homing redesign preserves the design language but swaps the central letterform.

### Direction 1 — Lowercase `h` with homing arch (recommended)

Keep the design language exactly:
- Navy stroke (`#1E2761`)
- Square-cap geometry (records aesthetic)
- Amber accents at significant points (`#F4B942`)
- Hairline as a typed channel
- Midnight Executive palette intact
- Georgia italic wordmark
- Calibri tagline

Swap the `j`-glyph for an `h`. The lowercase `h` has a vertical stem and an arch that curves out to the right and down. That arch becomes the *homing trajectory* — leaving the stem (origin), arcing out, landing at a destination point.

Mark composition (1024×1024 viewBox):
- Vertical navy stem (left)
- Arch curving from mid-stem out and down to a landing point on the lower-right
- Amber square at the apex of the arch (departure / "fire")
- Larger amber square at the landing point (target / "home")
- Optional fine navy hairline tracing the trajectory between them

This preserves brand continuity with the current j-mark while encoding the new metaphor more directly.

### Direction 2 — Abstract arc (alternative)

Drop the letterform entirely. The mark becomes:
- A single navy arc (the homing trajectory)
- Amber square at the origin
- Slightly larger amber square at the destination
- More diagrammatic, less typographic

Better suited if a future redesign wants to lean fully into the *typed channel* metaphor and de-emphasize the letterform connection. Likely too abstract for first iteration; revisit later.

### Recommendation

**Start with Direction 1.** It preserves brand continuity, keeps the typographic anchor, and the lowercase `h` arch is a beautiful natural form. Direction 2 is a viable v2 if the brand later shifts toward more abstraction.

### Wordmark

"Homing" set in **Georgia italic, 280pt, navy `#1E2761`, letter-spacing -3**. Same treatment as the existing japjs wordmark, just with the new word.

### Asset list to regenerate

All eight files in `docs/brand/`:
- `logo-primary.svg` (light bg, mark + wordmark)
- `logo-light.svg` (dark bg, mark + wordmark)
- `logo-extended.svg` (mark + wordmark + tagline)
- `logo-wordmark.svg` (text only)
- `logo-mark.svg` (square mark)
- `logo-mono-dark.svg` (monochrome navy)
- `logo-mono-light.svg` (monochrome white on navy)
- `favicon.svg` (simplified for ≤ 64 px)
- Update `README.md` (brand guide) with new concept text

Estimated effort: half a day for the redesign + asset regeneration.

---

## 8. Rename logistics — concrete checklist

### Java packages

```
hue.captains.singapura.japjs.*  →  hue.captains.singapura.homing.*
```

Affects:
- `japjs-core`
- `japjs-server`
- `japjs-conformance`
- `japjs-demo`

Single IDE refactor (`Refactor → Rename Package`). Takes minutes.

### Maven artifacts

| Old | New |
|---|---|
| `japjs-core` | `homing-core` |
| `japjs-server` | `homing-server` |
| `japjs-conformance` | `homing-conformance` |
| `japjs-demo` | `homing-demo` |

Future modules (per the action plan): `homing-spring-boot`, `homing-commons`, `homing-workspace`.

Edit `<artifactId>` in each `pom.xml` (currently 5 modules; will be more after Phase 0).

### Resource paths

```
japjs/js/...    →  homing/js/...
japjs/css/...   →  homing/css/...
japjs/svg/...   →  homing/svg/...
```

Verify `ResourceReader`, `ContentProvider`, and `ModuleNameResolver` implementations don't hardcode `"japjs"` paths anywhere; the convention is configurable but defaults will need updating.

### Code references

A grep for the literal string `japjs` will surface:
- Resource path constants
- The `JapjsActionRegistry` class name → `HomingActionRegistry`
- The `japjs.devRoot` system property → `homing.devRoot`
- README examples and quick-start snippets
- Comments and Javadoc

### Documentation sweep

Find/replace across all of `docs/`:
- `japjs` → `homing` (lowercase, in code-y contexts)
- `japjs` → `Homing` (capitalized, in prose)
- Be careful in: file paths (`docs/brochure/homing-vs-react-vue.md` would be renamed too)

Files affected:
- `README.md`
- `docs/comparison/homing-vs-react-vue.md` → `homing-vs-react-vue.md`
- `docs/whitepaper/homing-whitepaper.md` → `homing-whitepaper.md`
- `docs/whitepaper/homing-shell-flexibility-whitepaper.md` → `homing-shell-flexibility-whitepaper.md`
- `docs/brochure/00-index.md` through `06-architecture-at-a-glance.md`
- `docs/brochure/svg/01-04*.svg` (text content within)
- `docs/brochure/deck/build.js` and rebuild the .pptx
- `docs/SESSION-SUMMARY-2026-04-25.md`
- `docs/ACTION-PLAN-2026-04-25.md`
- All inline references to "japjs" inside the white papers

Estimated: ~1–2 hours with careful review (don't blindly sed — some `japjs` substrings might be in URLs, in commit hashes, etc.).

### Domain / namespace decisions (open)

- Domain: `homing.dev` / `homing.framework` / `homing.io` — to acquire
- Java package root: `io.homing.*` or `com.homing.*` (current is `hue.captains.singapura.*` — the personal namespace; consider whether to keep that or move to a project-level namespace)
- npm scope (if ever publishing JS-side packages): `@homing/...`
- GitHub org / repo names: keep current repo, rename or transfer

### Demo app

The interactive `PitchDeck` app inside `japjs-demo` should also be renamed. Options:

- `HomingPitch` — keeps it on-brand
- `HomingDeck` — neutral
- Keep as `PitchDeck` — the deck *pitches* Homing, doesn't need to share its name

Recommendation: keep as `PitchDeck`. Cleaner separation of "the framework" and "the demo app that pitches it."

### Total estimated effort

| Task | Effort |
|---|---|
| Java package refactor | 30 minutes |
| Maven artifact renames | 30 minutes |
| Resource path migration | 30 minutes |
| Code reference sweep (incl. system properties) | 1 hour |
| Documentation find-and-replace | 1–2 hours |
| Logo redesign | 4 hours |
| Brand asset regeneration | 1 hour |
| Verification (build + conformance + demo boot) | 30 minutes |
| **Total** | **~1 working day** |

---

## 9. Strategic caveats

- **Do the rename before Phase 0** of the action plan if possible. Once `japjs-spring-boot` ships and external code starts referencing Maven artifact names, the rename gets meaningfully harder.
- **Do not lean visual brand toward weapons imagery.** "Fire and forget" stays in engineering vocabulary only. No targeting reticles, missiles, lock-on imagery, or sci-fi tropes in the logo set.
- **No `.js` suffix.** Homing is a *Java* framework that produces JS as a downstream artifact; the suffix would tell the wrong story (see the executive brochure's positioning argument).
- **The brand voice and palette stay.** Midnight Executive (navy + ice + amber), Georgia italic for headings, structural geometry for marks. The rename does not justify a brand-voice overhaul. Same craftsman-tone, new word.
- **Rename the project, not the personal namespace** (initially). The `hue.captains.singapura.*` namespace is the author's personal Maven coordinate prefix; whether to keep it or move to `io.homing.*` is a separate decision and can be made independently of the project rename.

---

## 10. Open questions to resolve when resuming

1. **Final domain.** `homing.dev` vs `homing.framework` vs `homing.io`. Check availability and price.
2. **Java root namespace.** Stay personal (`hue.captains.singapura.homing.*`) or migrate to project-level (`io.homing.*` / `com.homing.*`)?
3. **Order of operations.** Rename *before* Phase 0 (clean slate), or *after* Phase 0 ships (less disruption to the current sprint)? Recommendation: before, but defer the call to the moment of resumption.
4. **Logo direction.** Direction 1 (lowercase `h` with homing arch) is recommended; produce both directions as proofs and compare.
5. **Demo app naming.** Keep `PitchDeck`, or rename to `HomingPitch`? Recommendation: keep.
6. **External announcement strategy.** Since adoption is essentially zero today, no announcement is needed. If there are early adopters or watchers, draft a one-line note for them.
7. **Old name redirect / archive.** Keep `japjs` as a Git tag at the rename commit, so future archaeology can find the rebrand point.

---

## 11. Resume checklist (for future-you)

When picking this back up:

1. Read this dossier.
2. Re-read `docs/SESSION-SUMMARY-2026-04-25.md` for full session context.
3. Re-read `docs/ACTION-PLAN-2026-04-25.md` for the engineering plan; decide whether the rename happens *before* Phase 0 (recommended) or as part of Phase 0.
4. Resolve the open questions in §10 above.
5. Execute in this order:
   1. Domain / namespace decisions
   2. Java refactor (packages + Maven artifacts)
   3. Resource path migration
   4. Code reference sweep
   5. Verification — `mvn clean install`, conformance tests pass, demo boots cleanly
   6. Documentation sweep
   7. Logo redesign (Direction 1 first; produce all 8 assets)
   8. Brand asset regeneration
   9. Final verification — open the renamed PitchDeck demo, confirm everything reads right
6. Tag the rename commit `rebrand/japjs-to-homing` for archaeology.
7. Commit and continue with Phase 0 of the action plan.

---

## 12. The one-paragraph version

> The project named `japjs` is becoming **Homing**, because "jap" reads as an English-language slur and a serious framework cannot carry that. The new name carries three resonant readings — *homecoming* (JavaScript returning to Java), *the sad truth* (JS homes toward but never arrives), and *fire and forget* (declare once in Java; the framework tracks autonomously) — that map closely to the framework's technical principles. The rename is one working day of focused work: Java refactor, resource paths, documentation sweep, and a logo redesign that swaps the lowercase `j` motif for a lowercase `h` with a homing arch while preserving the Midnight Executive palette and Georgia italic wordmark. Execute before Phase 0 of the action plan if possible. The thesis-strength of the new name justifies the cost; the cost grows steeply once external adoption begins.

---

*See also:*
- *[Brand guide README](#ref:brand-readme)* — current japjs brand identity (will be updated post-rename)
- *Session summary* — full session context*
- *Action plan* — engineering sequence the rename slots into*
