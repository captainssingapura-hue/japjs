# japjs-studio

A design and project management suite, built on japjs itself. Sibling module to `japjs-demo`.

## What it is

A workspace UI for the design, documentation, and project artifacts that drive japjs forward — white papers, brochure pages, RFCs, brand assets, session notes, action plans. The studio is itself a japjs application: every page is an `AppModule`, every CSS class is a typed `CssClass<StudioStyles>`, every external library is an `ExternalModule`. Builds on the same primitives the framework offers to its users.

This is *eat-your-own-dog-food* validation. If japjs can host the studio cleanly, it can host real workspace applications.

## Apps shipped today (v0)

| App | Class | Purpose |
|---|---|---|
| **Studio Catalogue** | `StudioCatalogue` | Launcher / home — lists available studio apps. |
| **Document Browser** | `DocBrowser` | Searchable, category-filtered list of every project document. |
| **Document Reader** | `DocReader` | Markdown viewer with live TOC sidebar and active-section tracking. |

## Apps deliberately deferred

These are natural follow-ons. Each is a new sibling AppModule under `studio.es.*`; no kernel changes required.

- **RFC Tracker** — list of RFCs with status (`Draft` / `Accepted` / `Implemented` / `Superseded`), revision counts, open-question summaries.
- **Decision Log** — capture and browse architectural decisions ("Homing rename", "transitive registration", etc.) with rationale and links back to the RFCs that drove them.
- **Action Plan View** — phases and tasks with progress, decision-gate state, dependency edges.
- **Open Questions Dashboard** — aggregates all `OPEN` sections from RFCs and design docs into a single resolution worklist.
- **Brand Asset Browser** — visual gallery of every brand SVG with download links.
- **Glossary / Concept Map** — definitions and cross-references for `Linkable`, `AppLink`, `ProxyApp`, `DomOpsParty`, etc.

## Architecture

```
StudioServer
  └── StudioActionRegistry   (composes JapjsActionRegistry + DocContent)
        ├── /app           ← japjs core
        ├── /module        ← japjs core
        ├── /css           ← japjs core
        ├── /css-content   ← japjs core
        └── /doc-content?path=<rel>   ← studio

DocRegistry (Java)          — catalog of known documents (path, title, summary, category)
DocContentGetAction          — serves markdown bodies from the configured docs root
StudioStyles (CssGroup)      — shared studio styling (Midnight Executive palette)
MarkedJs (ExternalModule)    — markdown rendering via esm.sh CDN
```

## Running

```bash
# from project root
mvn -pl japjs-studio -am compile

mvn -pl japjs-studio exec:java \
  -Dexec.mainClass="hue.captains.singapura.japjs.studio.StudioServer"
```

Then open:

- `http://localhost:8080/app?class=hue.captains.singapura.japjs.studio.es.StudioCatalogue` — the launcher
- `http://localhost:8080/app?class=hue.captains.singapura.japjs.studio.es.DocBrowser` — direct to the browser

## Configuration

| Property | Default | Purpose |
|---|---|---|
| `japjs.studio.docsRoot` | `./docs` | Filesystem root for markdown documents served by `/doc-content`. |
| `japjs.devRoot` | (unset) | Standard japjs live-reload root for JS/CSS/SVG resources. Set to `japjs-studio/src/main/resources` during development. |

## URL surface

| Path | Method | Returns | Notes |
|---|---|---|---|
| `/app?class=<App>` | GET | `text/html` | AppModule bootstrap (japjs core) |
| `/module?class=<EsModule>` | GET | `application/javascript` | Generated ES module (japjs core) |
| `/css?class=<CssGroup>` | GET | `application/json` | Resolved CSS chain (japjs core) |
| `/css-content?class=<CssGroup>` | GET | `text/css` | Raw CSS body (japjs core) |
| `/doc-content?path=<rel>` | GET | `text/markdown` | **Studio.** Markdown body, served from docs root. Validates path. |

## Adding a new document

1. Create the markdown file under `docs/`.
2. Add a `Doc` entry to `DocRegistry.java`.
3. Mirror the entry in `DocBrowser.js`'s `docs` array and `DocReader.js`'s `docMeta` map.

The triple-mirror is a v0 limitation. A future iteration ships the registry as JSON via a `/doc-list` endpoint, removing the duplication.

## Adding a new studio app

1. Create a new `AppModule` class under `studio.es.*`.
2. Create the matching JS resource under `resources/japjs/js/.../studio/es/`.
3. Add a tile entry to `StudioCatalogue.js`'s `apps` array.

The catalogue is hand-edited per new app. When RFC 0001 (typed nav) lands, the tiles will move to `nav.X(...)` calls.

## Future-state migration notes

The studio code is written against the **current** kernel (the `?class=...` URL contract, hand-built links, manual category-class mapping in JS mirrors). It will migrate cleanly once these RFCs land:

| RFC | What changes |
|---|---|
| **RFC 0001 — App Registry & Typed Nav** | Hand-built `?class=...` URLs become `nav.X(...)` calls. The triple-mirror of doc metadata becomes a single Java source feeding generated JS. Conformance scanner enforces no raw `href` in studio JS. |

Until then, the studio is the **before** picture. Post-RFC-0001 it becomes the **after** picture, with measurable LOC and clarity reductions.

## Status

v0 — usable, intentionally minimal, designed to evolve.
