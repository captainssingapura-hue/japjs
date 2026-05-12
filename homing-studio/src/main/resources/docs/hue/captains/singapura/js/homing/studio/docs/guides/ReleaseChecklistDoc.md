# Release Checklist

The recipe for shipping a Homing release. Extracted from the pattern [release 0.0.11](#ref:rel-0-0-11) established and used for every release after it. Follow this list top to bottom; each step has a verification gate.

---

## Versioning convention

**Versions are binary.** Drop the leading `0.0.` and read the third group as base-2.

| Spoken | Written | Decimal |
|---|---|---|
| First release | 0.0.11 | 3 |
| Second release | 0.0.100 | 4 |
| Third release | 0.0.101 | 5 |
| Fourth release | 0.0.110 | 6 |
| … | … | … |

Two consequences worth surfacing in the release notes:

- The "third group" never has digits `2..9`. A draft tag with `0.0.5` or `0.0.12` is a bug.
- Hitting `0.1.x` means **eight** releases shipped. That's the first time semver's *minor* increment carries real meaning here — the *patch* digits before it are binary, the *minor* is decimal. The mixed-base joke is intentional.

The Maven parent `<version>` stays at `1.0-SNAPSHOT`. The release number lives in the `Release<X_Y_Z>Doc` record + the git tag; the POM doesn't track it.

---

## The checklist

### Step 1 — Scope sweep

Establish the *delta* since the previous release. Sweep these locations:

- **`docs/rfcs/`** — new `Rfc*Doc.java` records (and their `.md`).
- **`docs/defects/`** — new `Defect*Doc.java` records, paying attention to ones whose status moved to *Resolved* in this window.
- **`docs/doctrines/`** — new doctrines (rare — typically 0–1 per release).
- **`docs/gotchas/`** — new `Gotcha*Doc.java` records.
- **`docs/guides/`** — new guide or whitepaper docs.
- **Catalogue tree changes** — new L1/L2 sub-catalogues, sub-catalogue/leaf restructures, breadcrumb shape changes.
- **Theme additions** — new `Homing<Theme>` packages in `homing-studio-base/.../theme/`.
- **Framework primitives** — new sealed interfaces / `Layer` permits / `Cue` variants in `homing-core` or `homing-server`.
- **Breaking API changes** — anything a downstream studio would have to edit to upgrade. This is the *most important* sweep — drives the migration skill in Step 6.

Output: a bulleted list grouped by area (Framework primitives / Framework serving / Studio chrome / Themes / Documentation / Breaking changes). This list seeds the *What shipped* section of the release notes.

**Gate:** the sweep is complete when every new artifact under `docs/` either appears in the bullets or has been explicitly skipped with a reason ("internal", "experiment, not user-facing").

### Step 2 — Build green

```bash
mvn install
```

Must complete with:

- Zero failing tests.
- Zero skipped tests (a skip is a signal to investigate, not to ship).
- Conformance suites green (`StudioDocConformanceTest`, `StudioHrefConformanceTest`, `StudioManagerInjectionConformanceTest`, `StudioCatalogueConstructsTest`, `StudioPlanConstructsTest`, `Demo*ConformanceTest`).

If anything is red, fix before continuing. The release notes assert "no failing tests" — that has to be true.

**Gate:** `BUILD SUCCESS` + the test summary block matches your expectations for the new release shape (test count visibly larger than the previous release's *Numbers* table).

### Step 3 — Author `Release<X_Y_Z>Doc.java`

```java
public record Release0_0_100Doc() implements ClasspathMarkdownDoc {
    private static final UUID ID = UUID.fromString("<fresh-uuid>");
    public static final Release0_0_100Doc INSTANCE = new Release0_0_100Doc();

    @Override public UUID   uuid()    { return ID; }
    @Override public String title()   { return "0.0.100 — <Headline>"; }
    @Override public String summary() { return "<1-paragraph what-landed>"; }
    @Override public String category(){ return "RELEASE"; }

    @Override public List<Reference> references() {
        return List.of(
                new DocReference("rfc-N",  RfcNDoc.INSTANCE),
                new DocReference("def-N",  DefectNDoc.INSTANCE),
                // ... every artifact called out by `#ref:NAME` in the .md
        );
    }
}
```

A new UUID per release (don't reuse the previous release's). Title format: `"<version> — <headline-noun-phrase>"`. Summary is one paragraph (4–6 sentences) — enough to explain *what landed and why it matters* on a tile in `ReleasesCatalogue`. Category is the literal string `"RELEASE"`.

### Step 4 — Author the matching `.md`

Classpath path mirrors the Java FQN: `homing-studio/src/main/resources/docs/hue/captains/singapura/js/homing/studio/docs/releases/Release<X_Y_Z>Doc.md`.

Sections in this order (the [0.0.11 release](#ref:rel-0-0-11) is the canonical example):

1. **Header table** — `# <Title>` then a 4-row table: Version / Released / Predecessor / Highlight. Highlight is a one-sentence elevator pitch.
2. **Summary** — 1–2 paragraphs framing the release. Foundations / features / themes / docs — pick the headline and lead with it.
3. **What shipped** — grouped by area (`### Framework primitives (homing-core)` / `### Framework serving (homing-server)` / `### Studio chrome (homing-studio-base)` / `### New themes` / `### Documentation`). Bulleted, every bullet self-contained. Cross-link RFCs/Defects/Doctrines via `[Title](#ref:name)`.
4. **Numbers** — a per-module table: `Module | Tests passing | Net file count`. Numbers come from Step 2's test report.
5. **(Optional) cross-cut table** — only when there's a meaningful thematic anchor (e.g. the cascade ladder ↔ doctrine table in 0.0.11). Skip when there isn't.
6. **Compatibility** — explicitly state breaking changes (or "No breaking changes."). Note any browser-floor bumps, runtime requirements, or downstream-studio actions required. Include a single sentence telling downstream how to upgrade.
7. **What's next** — 2–4 bullets on the visible work-paths from this release's vantage. Not a roadmap — just the obvious next moves.

### Step 5 — Register the Doc in three places

The doc has to flow into the DocRegistry, the structured catalogue, and the flat searchable browser.

| Where | What to edit |
|---|---|
| `ReleasesCatalogue.leaves()` | Prepend `Entry.of(Release<X_Y_Z>Doc.INSTANCE)` — newest first. |
| `ReleasesCatalogue.docs()` | Same — `Release<X_Y_Z>Doc.INSTANCE` to the front of the `List.of(...)`. |
| `DocBrowser` | Add `entry(Release<X_Y_Z>Doc.INSTANCE, "Releases", StudioStyles.st_badge_release.class)`. Convention: keep the release entries near the top of `DocBrowser.LISTING` in newest-first order. |

If the release ships a new doctrine, defect, RFC, or gotcha that's *also* called out in the release notes' `references()`, those need their own registrations too — but those should already have been done at the time the artifact was written. The release author's job at this step is to add only the release doc itself.

### Step 6 — Migration skill (if breaking)

If Step 1's scope sweep surfaced *any* breaking change for downstream studios, add a `MigrateFrom<X_Y_Z>SkillDoc` covering the diff from the *previous* release.

- File: `homing-skills/src/main/java/.../skills/MigrateFrom<X_Y_Z>SkillDoc.java`
- Resource: `homing-skills/.claude/skills/migrate-from-<x-y-z>/SKILL.md` (the *previous* version's slug — "migrate **from** 0.0.11", not "migrate **to** 0.0.100")
- Manifest: add an entry to `SkillsManifest.ALL`

The skill's `description` frontmatter should list trigger phrases like *"upgrade homing"*, *"migrate from 0.0.X"*, *"breaking changes"*. The body should be structured by the same areas as the release notes' breaking-changes list, with **before/after code snippets** and **mechanical find-and-replace recipes** where applicable.

Also update any *existing* skills whose example code shows the now-broken API (e.g. if you migrated `Catalogue` to typed levels, the `create-homing-studio` skill's example code needs updating). The release isn't done until skills compile against the new API in print.

### Step 7 — Re-run tests + git tag

```bash
mvn test
git status        # confirm only the expected files changed
git diff --stat   # eyeball the diff size
git add -p        # selective stage
git commit -m "Release 0.0.<bin>"
git tag 0.0.<bin>
git push && git push --tags
```

`mvn test` after Step 5 catches forgotten registrations: `StudioDocConformanceTest` scans the DocRegistry vs. the catalogue tree and complains if the new doc isn't reachable. `StudioHrefConformanceTest` resolves every `#ref:NAME` in the new markdown.

Do not force-push the tag. If the release notes need correcting after the tag lands, write `Release<X_Y_Z>_corrigendum.md` and ship it in the next release — never amend a shipped tag.

---

## Cross-references

- [Release 0.0.11](#ref:rel-0-0-11) — the canonical example. Read its structure before authoring the new one.
- [Dual-Audience Skills doctrine](#ref:dual-skills) — explains why the migration skill in Step 6 doubles as a `SKILL.md` for Claude Code agents and a Doc the human reader sees in the studio.

## Anti-patterns

- **Don't bump POM versions.** Maven stays at `1.0-SNAPSHOT`. Release identity lives in the Doc + tag.
- **Don't skip the migration skill.** If something downstream breaks, a `migrate-from-…` skill is the difference between a 5-minute upgrade and an hour of source-code archaeology.
- **Don't write the release notes before Step 2 passes.** Numbers tables that don't match `mvn test` output undermine the rest of the doc.
- **Don't lump multiple release-worth changes into one tag.** If you've shipped two distinct "headlines" of work, that's two releases. Binary increments are cheap — `0.0.110` follows `0.0.101` follows `0.0.100`.
