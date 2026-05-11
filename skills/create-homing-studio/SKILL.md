---
name: create-homing-studio
description: Use this skill when the user wants to bootstrap a new studio on top of homing-studio-base — a Catalogue/Plan-shaped studio with branded header, theme picker, doc browser/reader, and optional plan trackers. STRICTLY NO MANUAL JS — every page renders through the framework's built-in components (CatalogueAppHost, PlanAppHost, DocReader, ThemesIntro, Header, Card, ListItem). Triggers — "create studio", "new homing studio", "bootstrap studio-base", "downstream studio". Skip if the user wants an app with custom JS / hand-written renderers (use the AppModule path instead).
---

# Create a Homing Studio

A Homing studio is a Maven module that depends on `homing-studio-base` and configures **only data** — Catalogue records, Doc records, optional Plan records, plus a `StudioBrand`. The framework provides every page renderer; **you do not author JS**.

If you find yourself wanting to write a `.js` file, stop — you've left this skill's scope. Either compose existing builders (Card, ListItem, Listing, Section, Header, Footer) via a custom AppModule, or extend a Catalogue with a custom Entry type. Authoring JS to render a tile / row / header is a smell.

## Surface area you'll touch

A minimal studio is **4–5 files**:

| File | Purpose | Manual JS? |
|---|---|---|
| `pom.xml` | depends on `homing-studio-base` | — |
| `MyHomeCatalogue.java` | typed `Catalogue` record + `DocProvider` | no |
| `MyIntroDoc.java` + `MyIntroDoc.md` | typed `Doc` record + classpath markdown body | no |
| `MyStudioServer.java` | `main()` calling `StudioBootstrap.start(...)` | no |
| (optional) `MyStudioLogo.java` + `<logo>.svg` | typed `SvgGroup` for brand mark | no |

That's the entire surface. No `.js`, no HTML templates, no custom CSS class declarations beyond what `homing-studio-base` ships.

## Step-by-step

### 1. Decide the package + Maven coordinates

Pick a package root, e.g. `com.example.studio`. The studio's home catalogue, doc, and server all live under this root. Markdown classpath path mirrors the package.

### 2. pom.xml dependency

Add to the studio's `pom.xml`:

```xml
<dependency>
    <groupId>io.github.captainssingapura-hue.homing.js</groupId>
    <artifactId>homing-studio-base</artifactId>
    <version>${homing.version}</version>
</dependency>
```

`homing-studio-base` transitively pulls `homing-server`, `homing-libs`, `homing-core`. No other Homing deps required.

### 3. Home Catalogue

```java
package com.example.studio;

import hue.captains.singapura.js.homing.core.AppModule;
import hue.captains.singapura.js.homing.studio.base.Doc;
import hue.captains.singapura.js.homing.studio.base.DocProvider;
import hue.captains.singapura.js.homing.studio.base.app.Catalogue;
import hue.captains.singapura.js.homing.studio.base.app.Entry;
import hue.captains.singapura.js.homing.studio.base.app.Navigable;
import hue.captains.singapura.js.homing.studio.base.theme.ThemesIntro;

import java.util.List;

public record MyHomeCatalogue() implements Catalogue, DocProvider {

    public static final MyHomeCatalogue INSTANCE = new MyHomeCatalogue();

    @Override public String name()    { return "My Studio"; }
    @Override public String summary() { return "One-line description shown on the home page."; }

    @Override public List<Entry> entries() {
        return List.of(
                Entry.of(MyIntroDoc.INSTANCE),                              // a Doc
                // Sub-catalogues take just the catalogue:
                // Entry.of(MyDoctrineCatalogue.INSTANCE),
                // Plans take just the plan:
                // Entry.of(MyMigrationPlanData.INSTANCE),
                // AppModules need a Navigable wrapper supplying the tile name + summary:
                Entry.of(new Navigable<>(
                        ThemesIntro.INSTANCE,
                        AppModule._None.INSTANCE,
                        "Themes",
                        "Palette previews and one-click activation."))
        );
    }

    /** RFC 0004: every Doc referenced by an Entry.OfDoc must come from a registered DocProvider. */
    @Override public List<Doc> docs() {
        return List.of(MyIntroDoc.INSTANCE);
    }
}
```

**Rules**:
- Catalogue is a record (stateless). Identity = the Java class.
- Implement `DocProvider` and return the same Docs you reference, or boot will fail with "Catalogue X references Doc Y which is not in the DocRegistry".
- Use `Entry.of(navigable)` for AppModule entries — supply tile name + summary at the binding site, not on the AppModule.

### 4. Intro Doc

`com/example/studio/MyIntroDoc.java`:

```java
package com.example.studio;

import hue.captains.singapura.js.homing.studio.base.ClasspathMarkdownDoc;
import hue.captains.singapura.js.homing.studio.base.Reference;

import java.util.List;
import java.util.UUID;

public record MyIntroDoc() implements ClasspathMarkdownDoc {
    private static final UUID ID = UUID.fromString("REPLACE-WITH-FRESH-UUID");
    public static final MyIntroDoc INSTANCE = new MyIntroDoc();

    @Override public UUID   uuid()    { return ID; }
    @Override public String title()   { return "Welcome"; }
    @Override public String summary() { return "Short summary shown on the catalogue tile."; }
    @Override public String category(){ return "INTRO"; }

    @Override public List<Reference> references() { return List.of(); }
}
```

Generate the UUID with `uuidgen` or any UUID library. **Once chosen, never change it** — it's the wire-stable handle for cross-doc references.

`src/main/resources/docs/com/example/studio/MyIntroDoc.md`:

```markdown
# Welcome

Body content. Standard markdown. Headings become a TOC sidebar automatically.
```

The classpath path mirrors the Java package exactly: `docs/<package>/<DocClassName>.md`. If this is wrong, the framework returns 404 for the doc body.

### 5. (Optional) Brand logo

If you want a custom logo instead of the default coloured square, create an `SvgGroup`:

```java
package com.example.studio;

import hue.captains.singapura.js.homing.core.ExportsOf;
import hue.captains.singapura.js.homing.core.SvgBeing;
import hue.captains.singapura.js.homing.core.SvgGroup;

import java.util.List;

public record MyStudioLogo() implements SvgGroup<MyStudioLogo> {
    public record logo() implements SvgBeing<MyStudioLogo> {}

    public static final MyStudioLogo INSTANCE = new MyStudioLogo();

    @Override public List<SvgBeing<MyStudioLogo>> svgBeings() { return List.of(new logo()); }
    @Override public ExportsOf<MyStudioLogo> exports() { return new ExportsOf<>(this, List.copyOf(svgBeings())); }
}
```

Drop the SVG at `src/main/resources/homing/svg/com/example/studio/MyStudioLogo/logo.svg`.

**SVG requirements**:
- Include `width="100%" height="100%"` on the root `<svg>` (browsers default to 300×150 otherwise — your logo will overflow the 22×22 brand wrapper).
- Hardcode brand colours via `fill="#hex"` attributes. Do NOT use `var(--color-accent)` as a bare attribute (XML attributes don't process CSS vars). Use `style="fill: #hex"` if you need CSS context.
- No `<!-- comment with -- in it -->` — XML comments forbid `--`.

### 6. Server

```java
package com.example.studio;

import hue.captains.singapura.js.homing.core.AppModule;
import hue.captains.singapura.js.homing.core.SvgRef;
import hue.captains.singapura.js.homing.studio.base.StudioBootstrap;
import hue.captains.singapura.js.homing.studio.base.app.Catalogue;
import hue.captains.singapura.js.homing.studio.base.app.CatalogueAppHost;
import hue.captains.singapura.js.homing.studio.base.app.DocReader;
import hue.captains.singapura.js.homing.studio.base.app.StudioBrand;
import hue.captains.singapura.js.homing.studio.base.theme.ThemesIntro;
import hue.captains.singapura.js.homing.studio.base.tracker.Plan;
import hue.captains.singapura.js.homing.studio.base.tracker.PlanAppHost;

import java.util.List;

public class MyStudioServer {
    public static void main(String[] args) {

        // Built-in AppModules every studio gets. Add your own custom AppModules
        // here only if you have app-level pages that aren't Catalogue / Plan / Doc.
        List<AppModule<?, ?>> apps = List.of(
                CatalogueAppHost.INSTANCE,   // /app?app=catalogue&id=<fqn>
                PlanAppHost.INSTANCE,        // /app?app=plan&id=<fqn>
                DocReader.INSTANCE,          // /app?app=doc-reader&doc=<uuid>
                ThemesIntro.INSTANCE         // /app?app=themes
        );

        // Your catalogues — order doesn't matter; the brand picks the home one.
        List<Catalogue> catalogues = List.of(
                MyHomeCatalogue.INSTANCE
        );

        // Your plans — pass List.of() if none yet.
        List<Plan> plans = List.of();

        // Brand: label + home catalogue class + optional typed SVG logo.
        StudioBrand brand = new StudioBrand(
                "My Studio",
                MyHomeCatalogue.class,
                new SvgRef<>(MyStudioLogo.INSTANCE, new MyStudioLogo.logo())
                // Drop the SvgRef arg if no custom logo — defaults to a coloured square.
        );

        StudioBootstrap.start(8080, apps, catalogues, plans, brand);
    }
}
```

**Port**: pick something — 8080 is conventional. If running alongside another studio, pick a different port.

### 7. Verify

```bash
mvn install
java -cp "target/classes;<every dep jar>" com.example.studio.MyStudioServer
```

Then in a browser:

| URL | Expected |
|---|---|
| `http://localhost:8080/`                           | Redirects to `/app?app=catalogue&id=com.example.studio.MyHomeCatalogue` |
| `http://localhost:8080/app?app=catalogue&id=…`      | Renders the home catalogue with your tiles |
| `http://localhost:8080/app?app=doc-reader&doc=<uuid>` | Renders the intro doc |
| `http://localhost:8080/app?app=themes`              | Theme picker page (4 default themes auto-registered) |
| `http://localhost:8080/brand`                       | JSON `{label, logo, homeUrl}` — the source of truth for brand |
| `http://localhost:8080/themes`                      | JSON catalogue of registered themes |

Theme picker (top-right of the sticky header) flips between Default / Forest / Sunset / Bauhaus / Forbidden City / Letterpress out of the box.

## Conformance baseline

Subclass these in `src/test/java` to lock the framework's guarantees in CI. **Every studio should ship at least these tests.** Each is a 4-line override.

| Test base | What it pins |
|---|---|
| `CssConformanceTest`                  | No raw `.classList` / `.className` ops in JS |
| `HrefConformanceTest`                 | No raw `href=` / `window.location` / `.href` ops |
| `CdnFreeConformanceTest`              | No external `<script src=>` or CDN deps |
| `DoctrineConformanceTest`             | Pure-component views + owned references |
| `DocConformanceTest`                  | Every `#ref:<name>` anchor in markdown maps to a declared `Reference` |
| `CssGroupImplConsistencyTest`         | Every `CssGroup` has matching `CssGroupImpl` per theme |
| `ManagerInjectionConformanceTest`     | No `var/let/const <name>` redeclaration of framework-auto-injected `href`/`css`/etc |
| `StudioCatalogueConstructsTest` (pattern) | Your `CatalogueRegistry` constructs cleanly |
| `StudioPlanConstructsTest` (pattern)      | Your `PlanRegistry` constructs cleanly |

Example shape:

```java
class MyManagerInjectionConformanceTest extends ManagerInjectionConformanceTest {
    @Override protected List<EsModule<?>> esModules() {
        return List.of(
                CatalogueAppHost.INSTANCE,
                PlanAppHost.INSTANCE,
                DocReader.INSTANCE,
                ThemesIntro.INSTANCE
                // Plus any custom AppModules you add.
        );
    }
}
```

## What to add next

- **More docs**: each new typed `Doc` record + matching `.md` file. Add to `MyHomeCatalogue.docs()` so the registry can validate references.
- **Sub-catalogues**: add another `Catalogue` record (e.g. `MyDoctrineCatalogue`), register in `StudioBootstrap.start(...)` `catalogues` list, expose as `Entry.of(MyDoctrineCatalogue.INSTANCE)` from a parent.
- **Plan trackers**: see `PlanKitDoc` (Building Blocks catalogue) — two files (`Steps.java` + `PlanData.java`), register in `plans` list.
- **Custom themes**: see the `create-homing-theme` skill.

## What to never do

- **Author manual `.js` files in `homing/js/<your-package>/`** — if you need this, you've left this skill. The framework's intent is that downstream code is **Java records + classpath markdown + classpath SVG**, never hand-rolled JavaScript.
- **Bypass `StudioBrand`** — don't hardcode brand strings in your renderers. Brand data comes from `/brand` server-side and threads through everywhere automatically.
- **Skip the conformance tests** — they're 4-line subclasses each and catch entire classes of silent-from-CI / broken-in-browser bugs.
- **Reuse another studio's UUIDs** for your Docs — UUIDs are wire-stable identities; collisions cause registry errors.
- **Treat the StudioBootstrap.start(port, apps) overload as preferred** — it exists for back-compat (no catalogues / no brand / no `/`-redirect); the 5-arg overload above is the canonical one.

## Reference reading inside a running studio

- `homing-studio` (the framework's own studio) — `StudioServer.java` is a complete working reference.
- `homing-demo/.../studio/DemoStudioServer.java` — minimal example shipping a single Catalogue + intro Doc + custom (turtle) logo.
- `homing-studio-base/README.md` — full agent-targeted reference.
- `CatalogueKitDoc` and `PlanKitDoc` (in any running studio's Building Blocks catalogue) — the typed-container recipe.
