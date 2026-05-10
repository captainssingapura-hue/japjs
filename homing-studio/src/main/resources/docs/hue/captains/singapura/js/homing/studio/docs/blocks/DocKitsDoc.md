# DocBrowser & DocReader Kits

Two complementary kits for serving markdown content. Pair the **DocBrowser** (searchable card grid that lists docs) with the **DocReader** (renders one doc by typed reference). Both auto-generate their JS — concrete consumers write Java data only.

Per **RFC 0004**, every doc the studio knows about is a typed `Doc` record. The browser lists them; the reader renders one by UUID; the studio's `DocRegistry` (built at boot from the `DocProvider` closure) is the single source of truth.

**Where**: `homing-studio-base/.../base/`
- `Doc.java` + `ClasspathMarkdownDoc.java` + `InlineDoc.java` + `ResourceMarkdownDoc.java` — typed Doc model.
- `DocProvider.java` + `DocRegistry.java` — boot-time aggregation of contributed Docs.
- `DocGetAction.java` — `GET /doc?id=<uuid>` (serves `Doc.contents()` from the registry).
- `app/DocBrowserAppModule.java` + `DocBrowserData.java` + `DocBrowserEntry.java` + `DocBrowserJson.java` + `DocBrowserRenderer.java` + `DocBrowserRenderer.js`.
- `app/DocReader.java` (shared concrete `AppModule` — register and use as-is) + `DocReaderRenderer.java` + `DocReaderRenderer.js`.

---

## Doc records — the source of truth

A `Doc` is a typed Java record with a stable UUID and self-provided content. The dominant case is markdown shipped on the classpath next to its record:

```java
package com.example.studio.docs.guides;

import hue.captains.singapura.js.homing.studio.base.ClasspathMarkdownDoc;
import java.util.UUID;

public record IntroDoc() implements ClasspathMarkdownDoc {
    private static final UUID ID = UUID.fromString("3f9c2a8e-…");   // generate once, freeze
    public static final IntroDoc INSTANCE = new IntroDoc();

    @Override public UUID   uuid()    { return ID; }
    @Override public String title()   { return "Introduction"; }
    @Override public String summary() { return "Getting started."; }
    @Override public String category(){ return "GUIDE"; }
}
```

The companion markdown lives at:

```
src/main/resources/docs/com/example/studio/docs/guides/IntroDoc.md
```

`ClasspathMarkdownDoc.resourcePath()` derives that path from the record's class name automatically — co-locating the resource under the matching `docs/<package>/` subtree. Renaming the record (with an IDE refactor) renames the file in lock-step. The UUID never changes.

Other shapes:
- `InlineDoc` — `contents()` returned as a Java text block; no companion file.
- `ResourceMarkdownDoc` — explicit `resourcePath()` for the rare case the convention doesn't fit.

---

## DocBrowser — searchable grouped card grid

```java
public record MyDocBrowser() implements DocBrowserAppModule<MyDocBrowser>, DocProvider {

    record appMain() implements AppModule._AppMain<MyDocBrowser> {}
    public record link() implements AppLink<MyDocBrowser> {}
    public static final MyDocBrowser INSTANCE = new MyDocBrowser();

    private static final List<DocBrowserEntry> ENTRIES = List.of(
            new DocBrowserEntry(
                    IntroDoc.INSTANCE,                                              // typed Doc
                    "Guides",                                                       // catLabel — filter button + section title
                    CssClassName.toCssName(StudioStyles.st_badge_reference.class)   // browser badge style
            )
            // …more entries
    );

    @Override public List<Doc> docs() {
        // Contribute to the studio's DocRegistry — the same set the browser displays.
        return ENTRIES.stream().map(DocBrowserEntry::doc).toList();
    }

    @Override public DocBrowserData docBrowserData() {
        return new DocBrowserData(
                "documents", "Browse",
                "Every doc — searchable and filterable.",
                List.of(
                        new CatalogueCrumb("Home",      "/app?app=my-home"),
                        new CatalogueCrumb("Documents", null)
                ),
                DocReader.INSTANCE.simpleName(),
                ENTRIES,
                "Optional footer text."
        );
    }

    @Override public ImportsFor<MyDocBrowser> imports() {
        return ImportsFor.<MyDocBrowser>builder()
                .add(new ModuleImports<>(List.of(new DocReader.link()), DocReader.INSTANCE))
                .add(new ModuleImports<>(List.of(new DocBrowserRenderer.renderDocBrowser()),
                        DocBrowserRenderer.INSTANCE))
                .build();
    }

    @Override public ExportsOf<MyDocBrowser> exports() {
        return new ExportsOf<>(INSTANCE, List.of(new appMain()));
    }
}
```

`DocBrowserEntry` carries `(Doc doc, String catLabel, String badgeClass)`. Title / summary / category / id all flow from the typed Doc; the browser-specific filter label and badge style stay on the entry.

**Renderer features** (free with the kit):
- Live search across `title`, `summary`, `category`.
- Category filter buttons with per-category counts.
- Cards grouped into per-category sections.
- "All (N)" filter to clear.
- "No matches" placeholder.

Auto-generated JS embeds a frozen array of `{id, title, summary, category, catLabel, badgeClass}`; renderer attaches event listeners to the search input and filter buttons (Owned References — handles held from creation, no `getElementById`). Tile clicks navigate to `/app?app=doc-reader&doc=<uuid>`.

---

## DocReader — single shared markdown reader

Often you don't need a per-studio reader subclass. Just register the shared instance:

```java
StudioBootstrap.start(8080, List.of(
        MyHome.INSTANCE,
        MyDocBrowser.INSTANCE,
        DocReader.INSTANCE   // shared, no subclass needed
));
```

`DocReader.INSTANCE.simpleName()` is `"doc-reader"`. URL: `?app=doc-reader&doc=<uuid>`. Reader fetches `/doc?id=<uuid>` (served by `DocGetAction` against the studio's `DocRegistry`), parses with marked.js, builds a heading TOC sidebar with `IntersectionObserver` scroll-spy.

**Subclass** (rare) when you want a different brand or home-app simple-name:

```java
public record MyDocReader() implements AppModule<MyDocReader>, SelfContent {
    // override brandLabel() / homeAppSimpleName()
}
```

---

## Doc-serving endpoint

`DocGetAction` in `homing-studio-base` registers `GET /doc?id=<uuid>` — looks up the `Doc` in the studio's `DocRegistry` and returns `doc.contents()` with `doc.contentType()`. Wired automatically by `StudioBootstrap.start(...)`, which builds the registry by walking the `AppModule` closure for `DocProvider` implementors.

User-supplied input never reaches a path: only registered Docs are reachable, and the wire input is parsed as a UUID before any lookup. Path-traversal validation lives at registry boot, on the developer-supplied `Doc.path()`-equivalent values, not on every request.

---

## Managed references (RFC 0004-ext1)

Markdown bodies must not contain raw URLs to other project artifacts — every cross-doc, external, or image reference is declared as a typed `Reference` on the Doc record:

```java
@Override public List<Reference> references() {
    return List.of(
        new DocReference("pcv", PureComponentViewsDoc.INSTANCE),
        new ExternalReference("css-spec", "https://www.w3.org/TR/css/",
                "CSS Snapshot 2024", "W3C reference"),
        new ImageReference("arch-diagram", "docs/diagrams/architecture.svg",
                "Architecture diagram", "Four-layer overview")
    );
}
```

The markdown body cites them with normal anchor links:

```md
The doctrine in [Pure-Component Views](#ref:pcv) explains why HTML is banned.
For the formal grammar, see the [CSS spec](#ref:css-spec).
The [architecture](#ref:arch-diagram) shows the four layers.
```

DocReader emits a "References" section beneath the body with stable `id="ref:<name>"` per declared Reference; the browser handles fragment navigation natively (no DOM walking, no href substitution). `DocConformanceTest` extends to gate every `#ref:KEY` to a declared `Reference.name()` and to reject any non-anchor URL in markdown content. See `Rfc0004Ext1Doc` for the full doctrine.

---

## See also

- [Atoms — StudioElements](#ref:atoms) — Card / Section / Footer / Header used by the browser.
- [Catalogue Kit](#ref:cat-kit) — same shape, lighter (no search / filter / fetch).
- [Bootstrap & Conformance](#ref:bac) — `StudioBootstrap.start(...)` wires the registry + `/doc` automatically; `DocConformanceTest` gates UUID uniqueness, content resolution, and managed references.
- [RFC 0004](#ref:rfc-4) — the design rationale for the typed-Doc + UUID + visibility model.
