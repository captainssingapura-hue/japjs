# RFC 0004-ext1 — Managed Markdown References

| Field | Value |
|---|---|
| **Status** | **Draft** — open for iteration. Implementation in flight. |
| **Author** | Howard, with Homing |
| **Created** | 2026-05-10 |
| **Last revised** | 2026-05-10 |
| **Extends** | RFC 0004 — Typed Docs, UUIDs, and Public/Private Visibility |
| **Supersedes** | None |
| **Superseded by** | None |
| **Addresses** | Brittleness of stringly-typed cross-references and external URLs in public markdown content. Closes the residual stringly-typed surface that RFC 0004 deliberately scoped out (RFC 0004 §10 listed "Markdown cross-doc links" as a known follow-up). |
| **Honours** | The typed-nav doctrine extended from RFC 0001 (apps) and RFC 0004 (Doc bytes) to all references inside Doc content. |
| **Target phase** | Phase 1 — small, mechanical, lands as a single sweep. |

---

## 0. Status notice

**Draft RFC** captured at the end of the RFC 0004 implementation session. The motivation is a defect surfaced during that session (existing markdown contains relative-path cross-references that don't resolve under the new typed-Doc URL contract) plus the broader observation that even external URLs are unmanaged content the framework has no audit handle over. The fix unifies both kinds of reference behind one Java-side declaration mechanism rendered as a "References" section beneath the markdown body.

---

## 1. Motivation

RFC 0004 closed the loop on doc identity (UUID), doc bytes (`Doc.contents()` self-provided), and the wire format (`?id=<uuid>` / `?doc=<uuid>`). The one residual stringly-typed surface is **inside the doc content** — every link a markdown body writes today is one of:

| Category | Example | Today's storage | Today's problems |
|---|---|---|---|
| **A. Cross-doc** | `[Pure-Component Views](#ref:pcv)` | Inline relative path | Broken under typed-Doc URLs (resolves against `/app?app=…`); brittle under file rename |
| **B. External** | `[CSS spec](https://www.w3.org/TR/css/)` | Inline URL | Unmanaged — no audit list, no per-environment swap, no live-ness check |
| **C. In-doc anchor** | `[Phase 09](#phase-09)` | Inline `#slug` | Works correctly; intra-document; rename-safe |

Categories A and B share a deeper property: **they reference resources outside the document, and the framework has no typed handle on them.** Anything outside the document should be a typed Java reference, just like apps (RFC 0001) and Doc bytes (RFC 0004). Category C is intra-document and stays inline.

The proposal: **all out-of-document references are declared as typed `Reference` records on the Doc. The DocReader renders a deterministic "References" section beneath the markdown body, with stable in-page IDs. Markdown writers cite those references using normal anchor links (`[label](#ref:key)`). The renderer does no DOM walking — it just emits the references section.**

---

## 2. Proposed model

### 2.1 The `Reference` sealed interface and three subtypes

```java
public sealed interface Reference
        permits DocReference, ExternalReference, ImageReference {

    /** Short anchor key — used in markdown as `#ref:<name>` and as the id on the rendered element. */
    String name();
}

/** Cross-reference to another typed Doc — renders as a card with title + summary + reader link. */
public record DocReference(String name, Doc target) implements Reference {}

/** External web URL — renders as a card with label + description + outbound link. */
public record ExternalReference(String name, String url, String label, String description)
        implements Reference {}

/** Image shipped on the classpath — renders as a thumbnail with caption. */
public record ImageReference(String name, String resourcePath, String alt, String caption)
        implements Reference {}
```

Doc records declare their references:

```java
public record Rfc0003Doc() implements ClasspathMarkdownDoc {
    private static final UUID ID = UUID.fromString("…");
    public static final Rfc0003Doc INSTANCE = new Rfc0003Doc();

    @Override public UUID   uuid()    { return ID; }
    @Override public String title()   { return "RFC 0003 — Themeable Form & Component Primitive"; }
    @Override public String summary() { /* … */ }
    @Override public String category(){ return "RFC"; }

    @Override public List<Reference> references() {
        return List.of(
            new DocReference("def-2",         Defect0002Doc.INSTANCE),
            new DocReference("rfc-2",         Rfc0002Doc.INSTANCE),
            new DocReference("rfc-2-ext1",    Rfc0002Ext1Doc.INSTANCE),
            new DocReference("pcv",           PureComponentViewsDoc.INSTANCE),
            new ExternalReference("css-spec", "https://www.w3.org/TR/css/",
                    "CSS Snapshot 2024", "W3C reference")
        );
    }
}
```

### 2.2 Markdown cites references with normal anchor links

```md
The doctrine in [Pure-Component Views](#ref:pcv) explains why HTML literals are banned.
For the formal grammar, see the [CSS spec](#ref:css-spec).
This RFC was prompted by [Defect 0002](#ref:def-2).

Skip ahead to [Phase 09](#phase-09) if you've already read the prelude.
```

The `#ref:` prefix:
- **Explicit** — a writer reading the markdown immediately knows the link is a managed reference, not a heading anchor.
- **Collision-proof** — auto-slugify (lowercase + alphanumeric + hyphen) never produces a slug containing a colon, so `#ref:pcv` cannot collide with any heading.
- **Browser-native** — the rendered References section emits `id="ref:pcv"`, browsers navigate fragments by literal id match (no escape needed at the URL layer; CSS selectors would need `#ref\:pcv` but no CSS uses these IDs).

### 2.3 Renderer — append a deterministic References section

The DocReader's HTML page becomes:

```
┌─────────────────────────┐
│ Header / breadcrumbs    │
├─────────────────────────┤
│ <article> markdown body │   ← marked.js output, untouched
│ (TOC sidebar in aside)  │
├─────────────────────────┤
│ <section> References    │   ← Java-driven, stable ids
│   id=ref:pcv  …         │
│   id=ref:css-spec …     │
│   id=ref:arch-diagram … │
└─────────────────────────┘
```

**Renderer responsibility: zero DOM walking.** No href interception, no link rewriting, no markdown parsing. Just emit two sections concatenated. Browser handles `#ref:foo` navigation natively because the matching `id` exists in the document.

Per-subtype rendering inside the References section:

| Subtype | Rendered shape |
|---|---|
| `DocReference` | Card: title (h3, link to `nav.docReader(target.uuid())`) + summary paragraph |
| `ExternalReference` | Card: label (h3, link to URL with `target="_blank" rel="noopener"`) + description paragraph |
| `ImageReference` | `<figure>`: `<img src="/asset?path=<resourcePath>" alt="…">` + `<figcaption>` |

Out of scope for v1: `ImageReference` rendering depends on an `/asset` endpoint that doesn't exist yet. **Image references are declared and registered in v1 but image rendering itself is deferred** — the References section emits a placeholder block with the alt text + caption + the resource path, sufficient to validate the data shape. A follow-up RFC adds the asset-serving endpoint.

### 2.4 Conformance — `MarkdownReferenceConformanceTest` (folded into `DocConformanceTest`)

Per-Doc dynamic test added to the existing `DocConformanceTest` factory:

For every Doc whose `fileExtension()` is `.md`:

1. Parse `contents()` with regex `\[([^\]]*)\]\(([^)]+)\)`.
2. For each match, classify the URL by prefix:
   - **`#ref:KEY`** — managed reference. Look up KEY in `references().stream().map(Reference::name)`. Fail if not declared.
   - **`#anchor`** (no `ref:` prefix) — in-doc heading anchor. Allowed unconditionally; not validated against actual headings (out of scope).
   - **anything else** (`http://`, `https://`, `mailto:`, `../path`, bare filename, etc.) — **fail**: out-of-document references must be declared in `Doc.references()` and cited via `#ref:<name>`.

Failure message for a category-A or category-B violation:

```
Doc Rfc0003Doc contains banned out-of-document link in markdown body:
  [Pure-Component Views](#ref:pcv)
RFC 0004-ext1: out-of-document references must be declared in Doc.references()
and cited as [label](#ref:<name>). Add to Rfc0003Doc.references():
  new DocReference("pcv", PureComponentViewsDoc.INSTANCE)
Then change the markdown link to:
  [Pure-Component Views](#ref:pcv)
```

Failure message for an unresolved managed reference:

```
Doc Rfc0003Doc cites #ref:pcv but no Reference with name "pcv" is declared in references().
Either add the Reference, or fix the typo in the markdown link.
```

**Direction enforced: required only.** Every `#ref:KEY` in markdown must resolve. **Not required**: every declared `Reference` need not be cited inline — generic "further reading" entries are valid (they appear in the References section without prose mention).

### 2.5 No URL escapes the Java surface

After this RFC lands, the only URL strings that appear in markdown content are:
- `#anchor` slugs auto-generated by the renderer's TOC pass (not authored).
- `#ref:KEY` anchor citations (validated against the typed `references()` list).

Every actual URL — external web link, doc reader URL, asset URL — lives in a typed Java reference. The framework can audit, swap, or live-check the entire link surface from one place.

---

## 3. Migration

### 3.1 Affected docs (estimate from a current scan)

| Area | Files with category-A or B links | Notes |
|---|---|---|
| RFCs | 5 (all) | Pairwise cross-links to defects / doctrines / each other |
| Doctrines | 4 (all) | Pairwise cross-links + pointers to RFCs |
| Defects | 2 (both) | Each links to RFCs that resolve them |
| Whitepapers | 0–2 | Sparse external citations (W3C, Mozilla docs) |
| Brand | 1 | `BrandReadmeDoc` links to `RenameToHomingDoc` |
| Blocks | 6 | Block specs cross-link to each other and to atoms |
| Other | 0 | |

**Estimate: ~18 docs need editing.** Of those, ~12 have category-A links only, ~6 have a mix.

### 3.2 Per-doc edit pattern

For each markdown file with out-of-document links:

1. Identify each `[label](url)` link.
2. For category A (cross-doc): map filename to Doc record (e.g. `pure-component-views.md` → `PureComponentViewsDoc`). Add `DocReference("short-name", DocClass.INSTANCE)` to `references()`.
3. For category B (external): pick a short name. Add `ExternalReference("short-name", "https://…", "Display label", "Description")` to `references()`.
4. Replace the inline link `(url)` with `(#ref:short-name)`. Label text usually stays.
5. (Optional) Restructure prose if the inline link reads awkwardly — sometimes the natural fix is to drop the inline mention and let the References section carry it as a "further reading" entry.

### 3.3 Sequencing

1. **Framework**: add `Reference` sealed interface + three subtype records + `Doc.references()` default method.
2. **Renderer**: thread `references` through `DocReader.selfContent()` JS body → `DocReaderRenderer.js` "References" section emission. Per-subtype rendering helpers.
3. **Conformance**: extend `DocConformanceTest` with the markdown-content link scan.
4. **Migration**: pass through the ~18 affected docs once, declaring `references()` + replacing inline links with `#ref:<name>`. Conformance goes from N failures to green.
5. **Tracker**: `Rfc0004Ext1Steps` / `PlanData` / `Plan` / `Step`.
6. **Doc updates**: `DocKitsDoc.md` mentions `references()` and the `#ref:<name>` syntax.

---

## 4. Trade-offs and rejected alternatives

### 4.1 Unified `Reference` sealed interface vs. separate methods (`seeAlso()`, `externalRefs()`, `images()`)

**Unified chosen.** Three reasons:
- **One namespace, one resolver.** Every `#ref:KEY` resolves against one list — no per-kind disambiguation, no chance of a Doc name colliding with an external name.
- **One conformance test, one section render.** The reader page renders one References section; the test scans one list.
- **Per-subtype rendering** dispatches on `instanceof` in the renderer — small and bounded.

The cost is `Reference` as a sealed-interface dispatch site, but Java records + sealed types make this idiomatic.

### 4.2 In-page anchor section vs. server-side substitution / rewriting

Considered: the renderer parses the markdown, walks `<a href>` elements, looks up `#ref:KEY`, substitutes the actual URL.

**Rejected.** That's a complex interception layer that does work the browser already does for free. By emitting the actual references as DOM elements with stable IDs, fragment navigation handles everything without a single line of JS. The page is also simpler to debug — the References section is a literal DOM section, not a transformation.

### 4.3 Anchor prefix `#:foo` vs. `#ref:foo` vs. `#ref-foo`

**Picked `#ref:foo`** for two reasons:
- **Explicit**: a writer reading the markdown immediately knows the link is managed. `#:foo` is too cryptic.
- **Correctness top priority** (per the design conversation): `:` in the fragment guarantees collision-impossibility with auto-slug-generated heading IDs (which only contain `[a-z0-9-]`). `#ref-foo` *could* collide with a heading literally titled "Ref Foo" → slug `ref-foo`.

### 4.4 Conformance: required direction only vs. bidirectional

**Required only.** Every cited `#ref:KEY` must resolve (otherwise the in-page anchor scrolls to nowhere). But declared `Reference`s need not all be cited — generic "further reading" entries belong in the References section without prose mention. Forcing inline citation would constrain natural "further reading" prose patterns.

### 4.5 Cross-doc references inline (`#ref:doc-name`) vs. footer-only (`seeAlso()`)

The earlier draft proposed two surfaces: `seeAlso()` for the structural footer + a separate inline mechanism. **Collapsed to one.** A `DocReference` declaration appears in the References section (footer-equivalent) and can also be cited inline via `#ref:<name>`. The writer chooses inline-cite vs. references-only-mention per case; one declaration covers both. Half the API, same flexibility.

### 4.6 ImageReference rendering deferred

Images need an `/asset` endpoint to serve classpath bytes (analogous to `/doc` for text). That endpoint is out of scope for this RFC — the surface is small but it's a separate cleanup. **Image references are declared in v1 and rendered as text-only placeholders** (alt text + caption + resource path); a follow-up RFC adds `/asset` and updates the renderer to emit `<img>`.

The doctrine still holds with this v1 limitation: zero unmanaged URLs in markdown. No public doc currently has inline images, so the deferral has zero migration impact.

---

## 5. Doctrine implications

This RFC promotes a doctrine that follows directly from the line of reasoning behind RFC 0001 (typed app nav), RFC 0004 (typed Doc identity):

> **Every project-internal reference is checked at compile time. Markdown content is content; cross-references and external citations are typed Java declarations.**

Three layers:
- **Apps**: `AppLink<L>` (RFC 0001) — IDE-traceable, registry-validated nav.
- **Doc bytes**: `Doc` records + UUIDs (RFC 0004) — IDE-traceable identity, registry-validated wire.
- **Doc references**: `Reference` records + named anchors (this RFC) — IDE-traceable cross-refs, conformance-validated content.

Markdown content stays a content-only surface. Every URL on the wire is a typed Java reference.

---

## 6. Open questions

- **OPEN — Anchor key naming convention.** Free-form short kebab-case keys give the writer flexibility but allow inconsistency. Convention options: (a) free-form (`pcv`, `css-spec`); (b) prefix by kind (`doc-pcv`, `ext-css-spec`); (c) match the target's class name (`pure-component-views-doc`). Lean: free-form — names are local to one Doc, the writer optimises for short and readable, and the conformance check guarantees they resolve. Decide before §3.3 step 1.
- **OPEN — Should `references()` ordering be preserved in render?** Yes — `List<Reference>` is naturally ordered, render in declaration order. Trivial decision but worth pinning. **Lean: declaration order.**
- **OPEN — Exception for the RFC frontmatter table?** RFC headers like `Extends` / `Addresses` / `Supersedes` currently contain inline links — would fail the conformance scan. Options: (a) carve out the standard table as a fenced exception; (b) replace links with plain text and restate the relation in prose; (c) promote to typed `RfcDoc extends Doc` with structured relation fields rendered above the markdown body. Lean: (b) — plain text in the table, matching prose paragraph below the table contains the citation as a `#ref:<name>` link. Smallest change. Decide before §3.3 step 4.

---

## 7. Migration order (definitive)

1. Decide §6 open questions.
2. Add `Reference` sealed interface + `DocReference` / `ExternalReference` / `ImageReference` records + `Doc.references()` default method (~5 small files in `homing-studio-base`).
3. Update `DocReader.selfContent()` to emit references data into the JS body. Update `DocReaderRenderer.js` to render the References section (~30 LoC of new JS).
4. Extend `DocConformanceTest` with the markdown-content reference scan (~50 LoC).
5. Run conformance. Capture the violation list.
6. Per-doc migration sweep. Edit ~18 docs: declare `references()` + replace inline links with `#ref:<name>`.
7. Confirm conformance is green.
8. Update `DocKitsDoc.md` to describe `references()` + the `#ref:<name>` syntax.
9. Mark this RFC's tracker phases DONE as work completes (per the recursion proof — RFC 0004-ext1 itself uses managed references for its own cross-links).

---

## 8. Effort estimate

- Framework + renderer + conformance: **~2 hours**.
- Per-doc migration sweep (~18 docs × 5–8 min): **~2 hours**.
- Tracker (mirroring RFC 0004's): **~30 minutes**.
- Documentation: **~15 minutes**.

**Total: ~5 hours.** Half a working session.

---

## 9. Out of scope

- **`/asset` endpoint** for image rendering (placeholder text-only render in v1).
- **External URL liveness checks.** Slow, network-dependent — wrong shape for a unit-test conformance gate. The typed surface enables a future scheduled job; not in this RFC.
- **Markdown content quality lints** (broken anchors against actual headings, prose style, etc.). Separate concern.
- **Inverse "what links here?" view.** Solvable later from the same data.
- **Doc relations beyond `Reference`** (typed `Addresses` / `Supersedes` / `PartOf` semantic edges). Possibly a sibling RFC if it's worth doing — current `Reference` flat-list is sufficient for v1.

---

## 10. Revision log

- **2026-05-10** — Initial draft. Captured at the end of the RFC 0004 implementation session.
- **2026-05-10** — Revised. Original draft proposed `seeAlso()`-only with B/C allowed inline; conversation surfaced the unified-indirection model. Rewrote §1 motivation, §2 entire design (sealed `Reference` + `#ref:` anchors + zero-DOM-walking renderer), §3 migration. Resolved several open questions; three remain (key naming convention, render ordering, frontmatter exception). Effort estimate revised up from 4h to 5h.
