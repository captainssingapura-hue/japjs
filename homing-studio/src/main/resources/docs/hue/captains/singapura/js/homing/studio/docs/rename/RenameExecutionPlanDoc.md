# japjs → Homing — Execution Plan

| Field | Value |
|---|---|
| **Status** | Draft — ready to execute |
| **Drafted** | 2026-05-03 |
| **Decision context** | [`docs/brand/RENAME-TO-HOMING.md`](#ref:rename-doc) (the WHY) |
| **This document** | The HOW — concrete steps, phases, verification gates |
| **Estimated effort** | ~1 working day (~8 engineer-hours) |
| **Reversibility** | Fully reversible until end of Phase 6; partly reversible after |

---

## 0. Naming decisions (resolve before starting)

### 0.1 Project name

> **Homing** — no `.js` suffix.

Rationale (from the dossier):
- Java is the source of truth; `.js`-suffixed names tell the JavaScript-first story
- The framework is multi-language (Java + JS + CSS + SVG); committing to one suffix is misleading
- `Homing` reads cleanly as a project name, like `Spring`, `Quarkus`, `Vert.x`

If the user chooses `homing.js` instead, only §3 (artifactIds) changes — `homing-core` becomes `homing.js-core`. Mechanically identical. Discouraged for the reasons above; possible.

### 0.2 Java package root

Two options:

| Option | Package | Rationale |
|---|---|---|
| **A** *(default)* | `hue.captains.singapura.homing.*` | Preserves the author's personal Maven coordinate prefix; minimal change |
| **B** | `io.homing.*` | Project-level namespace; cleaner for distribution; bigger change |

**Recommendation:** Option A for this rename. Option B is a separate rebrand-to-distributable concern that can happen later if the project goes public. Resolve at a later strategic moment, not as part of mechanical renaming.

### 0.3 Maven groupId

| Option | groupId | Rationale |
|---|---|---|
| **A** *(default)* | `io.github.captainssingapura-hue.homing` | Preserves Maven Central coordinates; only the artifactId tail changes |
| **B** | `io.homing` | Requires Maven Central namespace ownership |

**Recommendation:** Option A. Same logic as 0.2.

### 0.4 Resource paths

`japjs/js/...`, `japjs/css/...`, `japjs/svg/...` → `homing/js/...`, `homing/css/...`, `homing/svg/...`. Internal convention; rename for consistency.

### 0.5 System properties

`japjs.devRoot` → `homing.devRoot`
`japjs.studio.docsRoot` → `homing.studio.docsRoot`

### 0.6 URL contract

`/app`, `/module`, `/css`, `/css-content`, `/doc-content`, `/step-data` — none contain "japjs"; **no change**. Public URL surface is preserved.

`?app=<simple-name>` and `?class=<canonical>` — preserved. The simple-name URL contract (RFC 0001) means most URLs already don't reference "japjs" as a string — only the legacy `?class=` URLs that contain `hue.captains.singapura.japjs.*` change shape (because the package is renamed).

---

## 1. Current scope (audit)

Counted as of 2026-05-03, post-RFC 0001 implementation:

| Surface | Count | Notes |
|---|---|---|
| Maven modules | 5 | `japjs-core`, `japjs-server`, `japjs-conformance`, `japjs-demo`, `japjs-studio` |
| Java source files containing "japjs" | 136 | Mostly package declarations + imports |
| JS resource files containing "japjs" | ~25 | Comments, string literals, generated-comment markers |
| CSS resource files containing "japjs" | 4 | Header comments only |
| SVG asset files containing "japjs" | 4 | Title text in brand SVGs |
| Markdown doc files containing "japjs" | 18 | Whitepaper, brochure, RFCs, READMEs, session notes |
| Java class names with `Japjs` prefix | 1 | `JapjsActionRegistry` |
| Total files touched | ~236 | |
| Filesystem renames (directories) | ~30 | One per `japjs/` package leaf |
| Filesystem renames (files) | ~5 | `JapjsActionRegistry.java`, brand SVG filenames, etc. |

**No external adoption** — this rename happens entirely inside the codebase. No published artifacts to deprecate, no external links to redirect.

---

## 2. Phased execution

Six phases. Each phase has a verification gate. Stop at any phase whose verification fails.

### Phase 1 — Snapshot (15 minutes)

Create a clean point of return.

```bash
git status                                  # ensure tree is clean
git tag pre-rename-japjs                    # snapshot tag
git checkout -b rename/japjs-to-homing      # working branch
```

**Verification:** `git tag | grep pre-rename` returns the tag. `git status` shows clean tree on the new branch.

### Phase 2 — Java side (2 hours)

The biggest mechanical edit. Use IDE refactor tools where possible.

#### 2.1 Maven artifactIds (5 modules)

Edit each `pom.xml`:
- `japjs` (root) → `homing`
- `japjs-core` → `homing-core`
- `japjs-server` → `homing-server`
- `japjs-conformance` → `homing-conformance`
- `japjs-demo` → `homing-demo`
- `japjs-studio` → `homing-studio`

Plus update parent `<modules>` and `<artifactId>` references in all child `pom.xml`s. Plus update inter-module `<dependency>` blocks.

#### 2.2 Maven directory names

```bash
git mv japjs-core    homing-core
git mv japjs-server  homing-server
git mv japjs-conformance homing-conformance
git mv japjs-demo    homing-demo
git mv japjs-studio  homing-studio
```

Update `<module>` entries in root pom.

#### 2.3 Java packages (IDE refactor)

In every module: `Refactor → Rename Package` from `hue.captains.singapura.japjs` → `hue.captains.singapura.homing`. IDE handles all imports and references.

Affects ~136 Java files. Single operation per IDE.

#### 2.4 Java class renames

- `JapjsActionRegistry` → `HomingActionRegistry` (IDE refactor)
- Test classes: `DemoCssConformanceTest`, `DemoHrefConformanceTest`, `StudioCssConformanceTest`, `StudioHrefConformanceTest` keep their names — they reference module families, not the framework name

#### 2.5 System property names

Find/replace across Java code only:
- `japjs.devRoot` → `homing.devRoot`
- `japjs.studio.docsRoot` → `homing.studio.docsRoot`

Affects probably 3-4 files (`ResourceReader`, `StudioServer`, `DocContentGetAction`, possibly READMEs).

**Verification:** `mvn clean install` — full build green; all tests pass. No file under `*/japjs/*` exists in the source tree.

```bash
find . -type d -name 'japjs' | grep -v target   # should be empty
mvn clean install                                # green
```

### Phase 3 — Resource paths (1 hour)

Rename the on-classpath resource directories from `japjs/...` to `homing/...`.

```bash
# Each module: rename the resource subtree
for m in homing-core homing-server homing-conformance homing-demo homing-studio; do
  if [ -d "$m/src/main/resources/japjs" ]; then
    git mv "$m/src/main/resources/japjs" "$m/src/main/resources/homing"
  fi
done
```

#### 3.1 Update path constants in Java

- `ResourceReader` — defaults to `homing/...`
- `CssConformanceTest`, `HrefConformanceTest` — `basePath = "homing/js/..."`
- Path constants in `EsModuleGetAction`, `CssContentGetAction`, `SvgGroupContentProvider`, `CssGroupContentProvider`
- `WonderlandDemoServer`, `StudioServer` if any explicit paths

#### 3.2 Update `japjs.devRoot` documentation

The old default was `src/main/resources/japjs/`; new default is `src/main/resources/homing/`. Updated automatically by §2.5 system-property rename, but verify the resolution logic doesn't have a hardcoded path.

**Verification:** `mvn clean install` green; demo + studio servers boot and serve a page successfully.

### Phase 4 — JS / CSS / SVG content sweep (1 hour)

These are text replacements inside resource files (not paths or class references — content). Mostly comments and brand strings.

#### 4.1 Comments and labels in JS files

Find/replace inside `**/*.js`:
- `japjs` → `homing` (case-sensitive)
- `japjs · ` → `homing · ` (brand prefix)
- Header comment markers like `// === japjs generated nav ===` → `// === homing generated nav ===`

Generated-marker strings live in `NavWriter.java` and `ParamsWriter.java` — update those too.

#### 4.2 CSS file headers

`/* japjs studio — design & project management suite styles */` → `homing studio — …`. Cosmetic.

#### 4.3 Brand assets (SVG)

`docs/brand/*.svg` and `homing-demo/.../svg/PitchDeckDiagrams/*.svg` reference `japjs` in titles and labels. Replace with `Homing`.

#### 4.4 The brand mark redesign

The lowercase `j` glyph in `logo-mark.svg`, `logo-primary.svg`, `logo-light.svg`, `logo-extended.svg`, `logo-wordmark.svg`, `logo-mono-dark.svg`, `logo-mono-light.svg`, `favicon.svg` needs to become a lowercase `h` per the dossier's Direction 1.

This is a real design task, not mechanical replacement. **Estimate: 1–2 hours.**

If schedule pressure: **defer the logo redesign** as a separate task. Replace the wordmark text (`japjs` → `Homing`) but keep the j-shaped mark temporarily, with a note that the mark redesign is pending.

**Verification:** `grep -ri "japjs" homing-*/src` returns only false-positives (URLs, bookmarks, error messages with capital `Japjs` brand-name uses that are intentional).

### Phase 5 — Documentation sweep (1 hour)

Find/replace across `docs/`, `README.md`, `*.md`:

- `japjs` → `homing` (lowercase, in code-y contexts: `homing-core`, `homing.devRoot`)
- `japjs` → `Homing` (capitalized, in prose: "Homing is a framework…")

Must distinguish — running it as a single global replace will mis-case prose. Recommend two passes:
1. First pass: `japjs` → `__JAPJS_TEMP__` (placeholder)
2. Manual review and substitute: `Homing` for prose, `homing` for code

Or use a pattern-aware sed: `s/^\(\s*\)japjs/\1homing/g` for code-block lines and a separate `s/\bjapjs\b/Homing/g` for prose lines.

**Specific files:**

- `README.md` — extensive references
- `docs/user-guide.md` — extensive
- `docs/whitepaper/homing-whitepaper.md` — and rename the file to `homing-whitepaper.md`
- `docs/whitepaper/homing-shell-flexibility-whitepaper.md` — rename to `homing-shell-flexibility-whitepaper.md`
- `docs/comparison/homing-vs-react-vue.md` — rename to `homing-vs-react-vue.md`
- `docs/brochure/00-index.md` through `06-architecture-at-a-glance.md`
- `docs/brand/README.md`, `docs/brand/RENAME-TO-HOMING.md`
- `docs/rfcs/0001-app-registry-and-typed-nav.md`
- `docs/SESSION-SUMMARY-2026-04-25.md`, `docs/ACTION-PLAN-2026-04-25.md`
- `homing-studio/README.md`
- All session notes

#### 5.1 DocRegistry update

`homing-studio/.../DocRegistry.java` paths reference `whitepaper/homing-whitepaper.md` etc. Update to the renamed paths.

#### 5.2 Cross-references and anchors

Any markdown link to a renamed file needs updating. Tooling: `grep -rn "japjs-whitepaper" docs/` etc.

**Verification:**
```bash
grep -rn "japjs" docs/ README.md                 # every match should be intentional
mvn clean install                                # green
mvn -pl homing-studio exec:java ...              # studio boots; DocBrowser lists docs correctly
```

Open the studio's DocBrowser and visually confirm every doc card still resolves. Open the RFC plan and confirm all 12 steps still render.

### Phase 6 — Verification gate + commit (30 minutes)

Comprehensive smoke test before finalizing.

#### 6.1 Build matrix

```bash
mvn clean install                  # all 5 modules, all tests
mvn -pl homing-demo  exec:java ... # WonderlandDemoServer boots, /app?app=demo-catalogue 200s
mvn -pl homing-studio exec:java ...# StudioServer boots, /app?app=studio-catalogue 200s
```

Then in the browser:
- DemoCatalogue lists every demo; clicking each one renders correctly
- PitchDeck navigates through all 13 slides; BGM toggle works
- StudioCatalogue → DocBrowser → click any doc → renders with TOC; click TOC anchor — scrolls
- StudioCatalogue → Rfc0001Plan — shows 12/12 DONE; click any step → renders detail; prev/next works

#### 6.2 Conformance scanners

```bash
mvn -pl homing-demo,homing-studio test -Dtest='*Conformance*' -Dsurefire.failIfNoSpecifiedTests=false
```

All four conformance tests still pass — `Demo{Css,Href}ConformanceTest`, `Studio{Css,Href}ConformanceTest`.

#### 6.3 Git tag the rename commit

```bash
git add -A
git commit -m "Rename japjs → Homing (full migration; see docs/rename/EXECUTION-PLAN.md)"
git tag rename-complete
git tag homing-v0.1
```

**Verification:** Branch passes all tests, both servers boot, all browser flows work, no `japjs` strings remain except in deliberate places (this plan doc and the dossier explicitly reference the old name).

---

## 3. Rollback plan

| Phase | If verification fails | Rollback |
|---|---|---|
| 1 | — | N/A |
| 2 | Build broken | `git checkout pre-rename-japjs` |
| 3 | Resource paths broken | `git checkout pre-rename-japjs` (Phase 2's gain is preserved if 2 was committed; Phase 3 done in single commit makes rollback clean) |
| 4 | JS/CSS errors | Revert the Phase 4 commit |
| 5 | Doc inconsistencies | Revert and re-do |
| 6 | Servers don't run | Revert and investigate; do not commit |

Until Phase 6's commit, everything is on a working branch. `git checkout pre-rename-japjs` returns to a fully-functional `japjs` codebase.

---

## 4. Risk register

| Risk | Likelihood | Mitigation |
|---|---|---|
| IDE package refactor misses a reflection-style string reference | Medium | Run `grep -rn "japjs" homing-*/src` after Phase 2; investigate every match |
| Maven cache pollution from old artifactIds | Low | `mvn clean install -U` after artifactId rename |
| Resource path mismatch (Java says `homing/js/X`, file is at `japjs/js/X`) | Medium | Phase 3 does both at once; verification step catches a server 404 |
| Brand SVG redesign takes longer than estimated | Medium | Defer to a follow-up task; replace wordmark text only in this rename |
| Documentation casing accidents (`Japjs` vs `Homing`) | Medium | Two-pass replacement strategy in §5; visual review |
| External tools (IDE bookmarks, shell history) reference old paths | Low | Document in commit message; affected developer re-saves |
| Logo mark still shows `j` after rename | Cosmetic | Documented as a known issue if Phase 4.4 is deferred |
| Brochure PPTX needs regeneration | Low | `cd docs/brochure/deck && node build.js` after docs sweep |

---

## 5. What this plan deliberately does NOT change

These survive the rename unchanged. Document for future-you:

- **External Maven dependencies** — `ja-http`, Vert.x, etc. remain as-is.
- **Domain names** — none acquired yet; future decision.
- **`hue.captains.singapura` namespace prefix** — preserved per §0.2 / §0.3 decisions.
- **The four `/app`, `/module`, `/css`, `/css-content` URL paths** — none contain "japjs" today.
- **The simple-name URL contract from RFC 0001** — `?app=pitch-deck` URLs survive intact since they don't reference the package.
- **Demo simple names** — `demo-catalogue`, `pitch-deck`, etc. don't contain "japjs"; preserved.
- **The CSS conformance and href conformance disciplines** — these are framework features, names don't contain "japjs".
- **Studio's `Rfc0001Plan` / `Rfc0001Step`** — RFC numbering is stable.

The post-rename URL surface is byte-for-byte the same as the pre-rename URL surface (modulo the legacy `?class=` URLs that referenced the old package name).

---

## 6. Open decisions to confirm before executing

1. **Project name suffix.** Plain `Homing` (recommended) vs `homing.js`. See §0.1.
2. **Java package root.** Keep `hue.captains.singapura.homing.*` (recommended) vs migrate to `io.homing.*`. See §0.2.
3. **Brand logo redesign timing.** Do it inside Phase 4.4 (full job), or defer as a follow-up (wordmark only). See §4.4.
4. **Single-PR vs phased commits.** Single commit at end of Phase 6 (clean history) vs commit-per-phase (easier rollback granularity). Recommend phased commits, single PR.

---

## 7. Suggested studio integration (optional)

Mirror the RFC 0001 implementation tracker pattern. Create `homing-studio/.../rename/` with:

- `RenameSteps.java` — declarative phase records
- `RenamePlan` AppModule — overview view
- `RenameStep` AppModule — per-phase detail

The studio gains a second tracker. Each phase's `done` flag is flipped in code as work progresses. Live tracker at `/app?app=rename-plan`.

This is a nice-to-have, not a blocker. The plan can execute equally well with this Markdown doc as the source of truth.

---

## 8. Resume checklist

When you're ready to start:

1. Read this document end-to-end.
2. Resolve the four open decisions in §6.
3. Confirm a clean working tree (`git status`).
4. Execute Phase 1 (snapshot + branch).
5. Walk through Phases 2–6 in order. Stop at each verification gate.
6. Commit and merge when Phase 6 is green.
7. Optionally: create the studio rename tracker (§7).
8. Update [`docs/brand/RENAME-TO-HOMING.md`](#ref:rename-doc) status from "Draft" to "Implemented" once the rename ships.

---

## 9. Estimated total effort

| Phase | Effort |
|---|---|
| 1 — Snapshot | 15 min |
| 2 — Java side | 2 hours |
| 3 — Resource paths | 1 hour |
| 4 — JS/CSS/SVG sweep (incl. logo redesign) | 1–3 hours (1 hour without logo) |
| 5 — Documentation | 1 hour |
| 6 — Verification + commit | 30 min |
| **Total (with logo)** | **6–7.5 hours** |
| **Total (logo deferred)** | **~5 hours** |

One working day. Same order-of-magnitude as the original dossier estimate; refined based on the post-RFC-0001 file count.

---

## References

- [Rename Dossier](#ref:rename-doc) — the WHY (decision context, three-layer metaphor, brand voice)
- [Brand Guide](#ref:brand-readme) — current japjs brand identity (will be updated post-rename)
- [RFC 0001](#ref:rfc-1) — implemented; the rename touches the artifacts but not the design
