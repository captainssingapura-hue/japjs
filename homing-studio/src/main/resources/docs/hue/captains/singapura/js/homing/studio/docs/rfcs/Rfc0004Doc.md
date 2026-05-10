# RFC 0004 — Typed Docs, UUIDs, and Public/Private Visibility

| Field | Value |
|---|---|
| **Status** | **Draft** — open for iteration. |
| **Author** | Howard, with Homing |
| **Created** | 2026-05-09 |
| **Last revised** | 2026-05-09 |
| **Supersedes** | None |
| **Superseded by** | None |
| **Addresses** | The `?path=blocks/atoms.md` doctrine violation surfaced while wiring `BuildingBlocksCatalogue` (stringly-typed doc references on the wire and in Java call sites). Closes the unused `DocGroup` / `Doc<D>` machinery shipped with RFC 0002-ext2 and never adopted. Removes the legacy `/doc-content` filesystem endpoint. |
| **Honours** | Typed-nav contract from [RFC 0001](#ref:rfc-1) (extends the same model from `Linkable` to `Doc`). The [Pure-Component Views](#ref:pcv) and [Owned References](#ref:or) doctrines (no view-side changes). |
| **Target phase** | Phase 1 — after RFC 0003 if it lands first; otherwise independent. |

---

## 0. Status notice

This is a **draft RFC** capturing a design conversation. Sections marked **OPEN** are explicitly deferred decisions; sections marked **PROPOSED** are recommended defaults that may change. Anything else is the working consensus.

When amending: edit the relevant numbered section and update `Last revised`. Significant changes should leave a note in §11 (Revision log).

---

## 1. Motivation

Three problems that turned out to be the same problem.

### 1.1 The `?path=` URL is stringly-typed

The doc reader's wire contract today is:

```
/app?app=doc-reader&path=blocks/atoms.md
/doc?path=blocks/atoms.md
```

The `path` value is a free-form string. A typo isn't caught at build time; renaming a markdown file silently breaks every link to it; bookmarks die when files move. This is the same class of brittleness RFC 0001 fixed for `?app=…` (with `Linkable.simpleName()` + the `SimpleAppResolver` registry), reapplied to docs.

### 1.2 `DocGroup` is unused dead weight

RFC 0002-ext2 shipped `Doc<D extends DocGroup<D>>` + `DocGroup<D>` + `DocManager` + `DocManagerInstance` to provide typed doc handles to JS-side consumers. Eight months on, **no `DocGroup` exists anywhere in the codebase**. The catalogues, browsers, and trackers that own docs already group them; layering a `DocGroup` on top is a second taxonomy that earns nothing.

### 1.3 Two endpoints, one job

`StudioActionRegistry` registers both `/doc` (`DocGetAction`, classpath-backed via `ResourceReader`) and `/doc-content` (`DocContentGetAction`, filesystem-backed via `homing.studio.docsRoot`). The latter is tagged "legacy" in the javadoc but still load-bearing — `DocReaderRenderer.js` was inadvertently relying on it after the kit migration. Two endpoints to do the same thing means two paths to keep secure (path-traversal, extension whitelist) and two paths to drift.

The unifying observation: **a `Doc` should be a typed, registered, UUID-addressable thing — like a `Linkable` — and it should own its bytes.**

---

## 2. Proposed model

### 2.1 `Doc` — typed, UUID-keyed, self-providing

```java
public interface Doc {
    /** Stable surrogate identity. Generated once with UUID.randomUUID() and frozen. */
    UUID uuid();

    /** Display title shown in browsers and reader headers. */
    String title();

    /** The bytes of this doc. The single source of truth for content. */
    String contents();

    default String summary()       { return ""; }
    default String category()      { return ""; }
    default String contentType()   { return "text/markdown; charset=utf-8"; }
    default String fileExtension() { return ".md"; }
}
```

Three things that fall out of this shape:

- **`uuid()` is the wire identity.** `?id=<uuid>` is what the URL carries. The doc record's Java class name, file path, package, and on-disk filename are all free to change without breaking any external reference.
- **`contents()` is the content sourcing contract.** The framework calls `doc.contents()`; the Doc owns where the bytes come from. Subinterfaces (§2.3) provide sensible defaults for the static-classpath case, the inline-text case, and any custom case downstream wants.
- **No type parameter.** `Doc<D>` becomes `Doc`. Docs aren't grouped at the type level — the things that *display* them are already groupings.

### 2.2 `DocProvider` — contributors, not containers

```java
public interface DocProvider {
    /** Docs this provider contributes to the registry. */
    List<Doc> docs();
}
```

Implemented by the things that already aggregate docs: `BuildingBlocksCatalogue`, `DocBrowser`, future trackers that ship reference material. The provider *displays* the docs and *owns* their declarations. Boot-time `DocRegistry` walks `SimpleAppResolver.apps()` for `instanceof DocProvider` and collects every contributed `Doc`.

This is the deliberate opposite of `DocGroup`'s model:

| | `DocGroup` (deprecated) | `DocProvider` (new) |
|---|---|---|
| **Shape** | A separate EsModule whose only job is to list Docs | A marker interface on the existing displayer |
| **Discovery** | Explicit registration, separate JS module per group | Walked from the `AppModule` closure already built for `SimpleAppResolver` |
| **JS-side** | Auto-emits `const X = _docs.doc(...)` per Doc, auto-injects `DocManagerInstance as docs` into consumers | None — typed Doc references stay Java-side until a consumer needs JS handles (then a future opt-in subinterface adds them) |
| **Lines of code** | ~110 (`DocGroup.java`) + JS runtime | ~5 (the marker) |

The JS-handle pathway isn't lost — it's deferred until something actually needs it. Today's call sites (catalogues building hrefs, browsers building tile data) all run Java-side. A future RFC can add `JsDoc extends Doc, Exportable._Constant<…>` if a JS module ever needs to import a typed Doc reference.

### 2.3 Self-provide subinterfaces — the static cases

The dominant case is "this doc is a markdown file shipped on the classpath next to its record". One subinterface covers it with zero ceremony:

```java
/**
 * A markdown doc whose .md file lives next to its record class on the classpath,
 * under the conventional `docs/` resource prefix.
 *
 * <p>The Doc record at {@code com.example.studio.docs.blocks.AtomsDoc} expects
 * its companion file at {@code resources/docs/com/example/studio/docs/blocks/AtomsDoc.md}.
 * Renaming or moving the record renames or moves the file in lock-step (most IDEs
 * do this automatically when the resource sits in the matching package layout).</p>
 */
public interface ClasspathMarkdownDoc extends Doc {
    /** Default: docs/<package>/<SimpleName>.md */
    default String resourcePath() {
        return "docs/" + getClass().getName().replace('.', '/') + fileExtension();
    }

    @Override
    default String contents() {
        try (var in = getClass().getClassLoader().getResourceAsStream(resourcePath())) {
            if (in == null) {
                throw new IllegalStateException(
                        "Doc " + getClass().getName() + " missing classpath resource: " + resourcePath());
            }
            return new String(in.readAllBytes(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}
```

Two further subinterfaces cover the remaining static cases:

```java
/**
 * A doc whose contents are a Java text block — no resource file at all.
 * Useful for changelog snippets, status notices, small fixtures, generated content.
 */
public interface InlineDoc extends Doc {
    @Override String contents();   // user provides the text directly
}

/**
 * A doc whose .md file is on the classpath but at an explicitly given path,
 * not derived from the record's package. Escape hatch when convention doesn't fit.
 */
public interface ResourceMarkdownDoc extends Doc {
    String resourcePath();
    @Override default String contents() { /* same loader as ClasspathMarkdownDoc */ }
}
```

Downstream may implement `Doc` directly with a custom `contents()` for non-static sources (a doc fetched from a wiki, a doc generated from a database, a doc compiled from source comments). The framework places no constraints.

### 2.4 `DocRegistry` — UUID resolver

```java
public final class DocRegistry {
    private final Map<UUID, Doc> byUuid;

    public DocRegistry(Collection<? extends Doc> docs) {
        var m = new LinkedHashMap<UUID, Doc>();
        for (var d : docs) {
            // Validate file extension matches contentType convention.
            // Validate uuid is non-null.
            var prev = m.put(d.uuid(), d);
            if (prev != null && prev != d) {
                throw new IllegalStateException(
                        "Doc UUID collision: " + d.uuid() + " is used by both "
                      + prev.getClass().getName() + " and " + d.getClass().getName());
            }
        }
        this.byUuid = Map.copyOf(m);
    }

    public Doc resolve(UUID id)       { return byUuid.get(id); }
    public Collection<Doc> all()      { return byUuid.values(); }

    /** Walk a SimpleAppResolver's closure for every DocProvider and union their docs(). */
    public static DocRegistry from(SimpleAppResolver apps) { /* … */ }
}
```

UUID uniqueness is the only registry-time invariant. Path validation moves out of the wire layer (no longer attack surface — see §2.5) and becomes a developer-discipline check at registry construction; failures are programmer errors caught at boot, not at request time.

### 2.5 New wire format

| Endpoint | Before | After |
|---|---|---|
| Doc bytes | `/doc?path=blocks/atoms.md` | `/doc?id=<uuid>` |
| Reader page | `/app?app=doc-reader&path=blocks/atoms.md` | `/app?app=doc-reader&doc=<uuid>` |
| Legacy filesystem | `/doc-content?path=…` | **removed** |

Server side:

- `DocGetAction(DocRegistry registry)` — parse `id` as `UUID`, `registry.resolve(id)`, return `doc.contents()`. No filesystem, no `ResourceReader`, no path validation. Three lines of logic.
- `DocReader.Params(UUID doc)` — typed query parameter. The reader page resolves the UUID via the registry to populate header / breadcrumbs at render time and passes the UUID through to JS for the `/doc?id=…` fetch.

**Security improvement**: user-supplied input never reaches a filesystem or classpath path. Only registered docs are reachable; the entire path-traversal surface (`..`, leading `/`, extension whitelist) collapses into "is this UUID registered". The remaining file-extension and content-type concerns live on the Doc record itself, validated once at registry boot.

JS runtime (`DocManager.js`) — kept for the future-JS-handle case but pointed at the new endpoints:

```js
url(d)   { return "/app?app=doc-reader&doc=" + encodeURIComponent(d._u); },
fetch(d) { return window.fetch("/doc?id=" + encodeURIComponent(d._u)); }
```

`DocReaderRenderer.js` fetches `"/doc?id=" + encodeURIComponent(docId)` (where `docId` is now passed in via `params.doc`, not `params.path`).

---

## 3. Visibility tiers — public on classpath, private off

### 3.1 The two tiers

| Tier | Purpose | Storage | Ships in |
|---|---|---|---|
| **Public** | How the project evolved (RFCs, defects, rename history); how to use it (block docs, doctrines, user guide); polished examples (whitepaper, comparisons, brand) | `homing-studio/src/main/resources/docs/<package>/<DocClass>.md` | The `homing-studio` jar |
| **Private** | Session notes, raw action plans, brochure / pitch deliberations, long-term vision drafts | Repo-root `.docs/` (renamed from `docs/`); leading dot signals hidden / off-classpath | Nowhere; working tree only |

### 3.2 Public-doc layout

Public markdown sits under `src/main/resources/docs/` mirroring the Java package of its record. With a Doc record at:

```
homing-studio/src/main/java/hue/captains/singapura/js/homing/studio/docs/blocks/AtomsDoc.java
```

the companion markdown lives at:

```
homing-studio/src/main/resources/docs/hue/captains/singapura/js/homing/studio/docs/blocks/AtomsDoc.md
```

Default `ClasspathMarkdownDoc.resourcePath()` resolves to `docs/hue/.../studio/docs/blocks/AtomsDoc.md`, which is exactly where the file ships on the classpath. Most IDEs offer to move the matching resource when refactoring the Java class — meaning rename / package-move stays a one-action operation, with both sides moving together.

The `docs/` prefix on the classpath is deliberate: it isolates documentation resources from the Java package tree so a downstream studio inspecting the homing-studio jar can locate every public doc by listing one classpath subtree, and so a future `DocBrowser` plugin could discover public docs from any imported jar by walking `META-INF/.../docs/**`.

### 3.3 Private folder

Repo-root `docs/` is renamed to `.docs/`. The leading dot:

- Signals "hidden / private" by convention (mirrors `.git`, `.idea`, `.claude`).
- Is naturally ignored by `find` / `tree` / IDE search defaults.
- Cannot accidentally be picked up by Maven's default resource-directory scanning.

The `<resource>` directive in `homing-studio/pom.xml` that mapped repo-root `docs/` onto the classpath (added in the run-up to this RFC as a workaround) is **deleted**. Public docs ship via standard `src/main/resources` layout; private docs are simply not on any classpath.

### 3.4 What goes where (one-time sort)

| Currently in `docs/` | New tier |
|---|---|
| `whitepaper/*.md` | public |
| `rfcs/*.md` | public |
| `defects/*.md` | public |
| `doctrines/*.md` | public |
| `guides/*.md`, `user-guide.md` | public |
| `comparison/*.md` | public |
| `brand/*.md` | public |
| `blocks/*.md` | public |
| `rename/EXECUTION-PLAN.md` | public |
| `brochure/*.md` | **private** (raw pitch material; revisit after polish) |
| `SESSION-SUMMARY-2026-04-25.md` | private |
| `ACTION-PLAN-2026-04-25.md` | private |

The brochure could in principle be public — it's the polished pitch — but for this RFC it stays private until the content is reviewed; it can be promoted later by moving it under `homing-studio/src/main/resources/docs/...` and adding a Doc record.

---

## 4. Migration

### 4.1 Framework

1. New: `Doc`, `ClasspathMarkdownDoc`, `InlineDoc`, `ResourceMarkdownDoc`, `DocProvider`, `DocRegistry`.
2. Rewrite `DocGetAction` to take `DocRegistry`, parse `id` as UUID, return `doc.contents()`.
3. Rewrite `DocReader.Params` to `(UUID doc)`. Update `selfContent()` to thread UUID into the JS body.
4. Update `DocReaderRenderer.js` — fetch `/doc?id=<uuid>`.
5. Update `DocManager.js` — `docs.url(d)` builds `?app=doc-reader&doc=<uuid>`, `docs.fetch(d)` uses `/doc?id=<uuid>`.
6. Wire `DocRegistry.from(appResolver)` into `StudioBootstrap.start(...)`.
7. **Delete**: `DocGroup.java`, `DocGroupServingTest.java`, the `<D extends DocGroup<D>>` parameter from `MarkdownDoc / HtmlDoc / SvgDoc / PlainTextDoc / JsonDoc`.
8. **Delete**: `DocContentGetAction.java`, `/doc-content` route registration in `StudioActionRegistry`, the `homing.studio.docsRoot` system property and its `Path docsRoot` plumbing through `StudioServer` / `StudioActionRegistry`.
9. **Delete**: the `<resource>` directive in `homing-studio/pom.xml` (the `../docs` mapping).

### 4.2 Doc records (one-time per file)

For every public markdown file:

1. Create a Java record at the matching `studio.docs.<subtree>.<name>Doc` path implementing `ClasspathMarkdownDoc` (or `MarkdownDoc` if downstream defines a sub-interface convention).
2. Generate a fresh UUID via `UUID.randomUUID()`, paste as a `static final UUID ID` constant.
3. Move the `.md` file from `docs/<subtree>/<name>.md` to `homing-studio/src/main/resources/docs/<full-package>/<NameDoc>.md`.
4. Add the record to its owner's `docs()` method (`BuildingBlocksCatalogue.docs()`, `DocBrowser.docs()`, etc.).

Mechanical, ~30 records, ~one hour.

### 4.3 Call-site cleanup

- `BuildingBlocksCatalogue.Block` is **deleted**. The `Doc` records are the blocks; tile data comes from `Doc.title() / .summary() / .uuid()`.
- `DocBrowserEntry(String path, String title, String summary, String category, String catLabel, String badgeClass)` becomes `DocBrowserEntry(Doc doc, String catLabel, String badgeClass)` — title / summary / category / id all sourced from the typed Doc.
- `CatalogueTile.docCard(Doc doc, String badgeClass)` — single helper that builds the URL from `doc.uuid()`. No path strings at any call site.

### 4.4 Conformance tests

Three additions:

- **`DocUuidUniquenessTest`** — `DocRegistry.from(appResolver)` succeeds (UUIDs are unique). Largely a re-assert of registry-construction behaviour, but pinned as a CI gate so a UUID copy-paste doesn't slip in.
- **`DocContentResolvableTest`** — for every `Doc` in the closure, `doc.contents()` returns non-empty. Catches "added the record but forgot to move the .md".
- **`TypedDocReferenceTest`** — scan Java sources for hand-built `"app=doc-reader&doc="` or `"app=doc-reader&path="` URL fragments outside the canonical builder helper. Forces all reader links through the typed path.

Conformance scaffolding under `homing-conformance/`, subclassed once per studio (matching the existing five bases).

### 4.5 Documentation

`docs/blocks/doc-kits.md` (the kit reference doc) is updated to the new flow: `DocProvider`, UUIDs, `Doc.contents()`. The "URL: ?app=doc-reader&path=…" line becomes "URL: ?app=doc-reader&doc=<uuid>".

The kit-reference docs themselves get `Doc` records during the migration — the doc that documents the kit is itself migrated to the new model (§2 of the recursion-as-proof argument from the building-blocks page).

---

## 5. Trade-offs and rejected alternatives

### 5.1 UUIDs vs. friendly slugs

UUIDs are ugly on the wire. A doc URL becomes `?doc=550e8400-e29b-41d4-a716-446655440000` instead of `?path=blocks/atoms.md`.

Considered but **rejected** for v1: a `slug()` method providing an alternative friendly key (e.g. `?doc=atoms`). Rationale:

- Slugs reintroduce the rename problem the UUID is solving (renaming the doc means changing its slug means breaking external links).
- Two namespaces are worse than one; URL builders would have to choose, and consumers checking equality would have two answers.
- If aesthetics matter for a specific surface (a public landing page), that surface can ship its own slug→UUID redirect table without coupling to the framework.

UUIDs win on stability; slugs lose on stability and earn only on aesthetics. Stability is the goal.

### 5.2 `Doc.contents()` vs. `Doc.path()` + framework-side reader

Considered: keep `Doc.path()` and have the framework's `DocGetAction` perform the resource read.

**Rejected**. Pulling the read into the framework means:
- The framework owns the storage choice (classpath / filesystem / network) on every Doc's behalf.
- Mocking a Doc in a test requires mocking a `ResourceReader` instead of returning a fixture string.
- The `InlineDoc` case becomes awkward (the framework has to special-case a Doc that doesn't want resource reading).

`Doc.contents()` keeps the framework dumb and the Doc smart. `ClasspathMarkdownDoc` (the dominant case) packages the read into a default method, so individual records still pay zero ceremony.

### 5.3 Deleting `DocGroup` vs. deprecating

Considered: mark `DocGroup` `@Deprecated` and keep it for one release cycle.

**Rejected**. Nothing depends on it (zero implementations). Deprecation cycles exist to give downstream consumers time to migrate; there is no downstream consumer. Delete cleanly.

### 5.4 Co-locating `.md` next to Java vs. flat `docs/` tree

Considered: keep markdown in a flat `src/main/resources/docs/blocks/atoms.md` layout and have `Doc` records carry an explicit `resourcePath()`.

**Rejected** (in favour of co-located mirror). The flat layout reintroduces the path-string-in-Java problem at the boundary between record and file. Co-located mirror lets the IDE refactor both at once and lets the default `resourcePath()` be derived from the class name.

`ResourceMarkdownDoc` is kept as an explicit-path escape hatch for rare cases where mirroring doesn't fit (existing third-party markdown shipped under a fixed path, generated content, etc.).

---

## 6. Open questions

- **OPEN — `MarkdownDoc` vs. `ClasspathMarkdownDoc` naming.** Today `MarkdownDoc<D>` is a generic content-type sub-interface (sets `contentType` / `fileExtension` defaults). After this RFC it loses the type parameter; do we collapse it with `ClasspathMarkdownDoc` (the classpath-resolution default), or keep them as two layers (`MarkdownDoc` for the content-type, `ClasspathMarkdownDoc` for the loader)? Two layers feels cleaner; the RFC sketches assume the latter.

- **OPEN — UUID generation tooling.** Should we ship a tiny `mvn` goal or test scaffold that prints a fresh UUID for copy-paste? Or is `UUID.randomUUID()` in a JShell session adequate for the ~30 one-time generations? Leaning toward "no tooling, just JShell".

- **OPEN — `DocBrowserEntry.catLabel`.** The category and the category label are today distinct fields. With `Doc` carrying `category()`, where does `catLabel` (the longer human-readable name shown on filter buttons) live? Options: (a) add `categoryLabel()` to `Doc`; (b) keep `catLabel` on `DocBrowserEntry` as a browser-side override; (c) derive `catLabel` from `category()` by Title-Casing. Leaning toward (b) — the label is a browser concern, not intrinsic to the doc.

- **OPEN — Whether `BuildingBlocksCatalogue` should also be a `DocReader` itself.** Today blocks link out to the shared `DocReader`; an alternative is for the catalogue to in-line-render the chosen doc beneath the tile grid. Out of scope for this RFC (existing nav stays); flagged as a follow-up.

---

## 7. Migration order

1. **This RFC** — accepted, captured in `docs/rfcs/0004-typed-docs-and-doc-visibility.md` (i.e. this file).
2. **Framework primitives** — `Doc`, `DocProvider`, `DocRegistry`, the three subinterfaces. Ships as additive — old `Doc<D>` and `DocGroup<D>` still compile alongside.
3. **`DocGetAction` rewrite + `DocReader.Params(UUID doc)`** — wire format flips; `DocReaderRenderer.js` updated. `DocManager.js` updated. `/doc-content` and `DocContentGetAction` removed.
4. **`StudioBootstrap` wiring** — `DocRegistry.from(appResolver)` plumbed in, passed to `DocGetAction`.
5. **First consumer migration** — `BuildingBlocksCatalogue` (5 docs). Validates the API at small scale.
6. **Bulk consumer migration** — `DocBrowser` (~26 docs). Mostly mechanical.
7. **Public-doc relocation** — markdown files moved into `homing-studio/src/main/resources/docs/...`; `<resource>` directive in pom deleted.
8. **Private-doc rename** — repo-root `docs/` → `.docs/`, with only the private subset retained.
9. **Conformance tests** — three new bases + per-studio subclasses.
10. **Cleanup** — delete `DocGroup`, `DocGroupServingTest`, the type parameters on the content-kind sub-interfaces. Delete `DocContentGetAction`, the `homing.studio.docsRoot` plumbing, the `Path docsRoot` parameter on `StudioActionRegistry`.
11. **Doc updates** — `docs/blocks/doc-kits.md` (now `BuildingBlocksCatalogue.DocKitsDoc`) revised to describe the typed flow.

Each step compiles and tests pass before moving on. Steps 2–4 can land as one atomic change (the wire flip is all-or-nothing); 5–11 are incremental.

---

## 8. Effort estimate

- Framework primitives + endpoint rewrite: ~1 day.
- Doc records + markdown moves (~30): ~half day, mechanical.
- `DocBrowser` / `BuildingBlocksCatalogue` / `DocReader` rewrites: ~half day.
- Conformance tests: ~half day.
- Documentation updates: ~half day.
- Net code change: roughly **−500 LoC** (DocGroup + DocContentGetAction + ResourceReader plumbing − the new primitives is a net loss).

---

## 9. Doctrine implications

This RFC strengthens the typed-nav doctrine from RFC 0001:

- **Linkable closure** (apps, proxies) now has a peer: the **Doc closure**, walked through `DocProvider` from the same entry-app set.
- The four view doctrines are unaffected (no view-side changes).
- A new conformance test (`TypedDocReferenceTest`) extends the static-enforcement coverage from CSS / href / Linkable references to Doc references.

No existing doctrine is broken; one is reinforced.

---

## 10. Out of scope

- **JS-side typed Doc handles.** A future RFC can add `JsDoc extends Doc, Exportable._Constant<…>` if a JS module needs to import a typed Doc reference. Today's call sites all build URLs Java-side; adding the JS handle pre-emptively would re-introduce the kind of unused infrastructure this RFC is removing.
- **Doc revisions / history.** A `Doc` is the current bytes; versioning is a separate concern.
- **Discovery from external jars.** The classpath layout (§3.2) anticipates this but no walker is shipped here.
- **Search / indexing.** `DocBrowser` already does free-text search over the in-memory list; full-text indexing of `Doc.contents()` for cross-doc search is a follow-up.

---

## 11. Revision log

- **2026-05-09** — Initial draft. Captures the design conversation that started from "`?path=blocks/atoms.md` is stringly-typed; check the doctrine" through the public/private split and the self-provide contract.
