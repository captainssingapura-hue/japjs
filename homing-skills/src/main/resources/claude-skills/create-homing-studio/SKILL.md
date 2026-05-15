---
name: create-homing-studio
description: Use this skill when the user wants to bootstrap a new studio on top of homing-studio-base — a Catalogue/Plan-shaped studio with branded header, theme picker, doc browser/reader, and optional plan trackers. STRICTLY NO MANUAL JS — every page renders through the framework's built-in components (CatalogueAppHost, PlanAppHost, DocReader, ThemesIntro, Header, Card, ListItem). Triggers — "create studio", "new homing studio", "bootstrap studio-base", "downstream studio". Skip if the user wants an app with custom JS / hand-written renderers (use the AppModule path instead).
---

# Create a Homing Studio

A Homing studio is a Maven module that depends on `homing-studio-base` and configures **only data** — Catalogue records, Doc records, optional Plan records, plus a `StudioBrand`. The framework provides every page renderer; **you do not author JS**.

If you find yourself wanting to write a `.js` file, stop — you've left this skill's scope. Either compose existing builders (Card, ListItem, Listing, Section, Header, Footer) via a custom AppModule, or extend a Catalogue with a custom Entry type. Authoring JS to render a tile / row / header is a smell.

## Surface area you'll touch

A minimal studio is **5–6 files**:

| File | Purpose | Manual JS? |
|---|---|---|
| `pom.xml` | depends on `homing-studio-base` | — |
| `MyHomeCatalogue.java` | typed `Catalogue` record + `DocProvider` | no |
| `MyIntroDoc.java` + `MyIntroDoc.md` | typed `Doc` record + classpath markdown body | no |
| `MyStudio.java` | typed `Studio<L0>` record — home, apps, plans, brand | no |
| `MyStudioServer.java` | `main()` constructing a `Bootstrap<>` + `start()` | no |
| (optional) `MyStudioLogo.java` + `<logo>.svg` | typed `SvgGroup` for brand mark | no |

That's the entire surface. No `.js`, no HTML templates, no custom CSS class declarations beyond what `homing-studio-base` ships.

The `Studio<L0>` record (RFC 0012) is the typed binding between your catalogue tree and the framework — it declares your home, your intrinsic apps, your plans, your brand, all in one place. The server's `main()` is then three lines: construct an `Umbrella`, wrap in `DefaultFixtures`, hand to `Bootstrap`.

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

Per RFC 0005-ext2 (shipped in 0.0.100), `Catalogue` is sealed — every concrete catalogue picks a typed level (`L0_Catalogue` for the studio root, `L1_Catalogue<Root>` for direct children, etc.) and exposes children through two methods: `subCatalogues()` for typed sub-trees and `leaves()` for Docs / Plans / AppModule entries.

```java
package com.example.studio;

import hue.captains.singapura.js.homing.core.AppModule;
import hue.captains.singapura.js.homing.studio.base.Doc;
import hue.captains.singapura.js.homing.studio.base.DocProvider;
import hue.captains.singapura.js.homing.studio.base.app.Entry;
import hue.captains.singapura.js.homing.studio.base.app.L0_Catalogue;
import hue.captains.singapura.js.homing.studio.base.app.L1_Catalogue;
import hue.captains.singapura.js.homing.studio.base.app.Navigable;
import hue.captains.singapura.js.homing.studio.base.theme.ThemesIntro;

import java.util.List;

public record MyHomeCatalogue() implements L0_Catalogue, DocProvider {

    public static final MyHomeCatalogue INSTANCE = new MyHomeCatalogue();

    @Override public String name()    { return "My Studio"; }
    @Override public String summary() { return "One-line description shown on the home page."; }

    /** Typed L1 sub-catalogues. Only declare this if you have any —
     *  otherwise inherit the empty default. */
    @Override public List<L1_Catalogue<MyHomeCatalogue>> subCatalogues() {
        return List.of(
                // MyDoctrineCatalogue.INSTANCE
        );
    }

    /** Docs, Plans, AppModules — everything that's not a sub-catalogue. */
    @Override public List<Entry> leaves() {
        return List.of(
                Entry.of(MyIntroDoc.INSTANCE),                              // a Doc
                // Entry.of(MyMigrationPlanData.INSTANCE),                  // a Plan
                Entry.of(new Navigable<>(                                   // an AppModule
                        ThemesIntro.INSTANCE,
                        AppModule._None.INSTANCE,
                        "Themes",
                        "Palette previews and one-click activation."))
        );
    }

    /** RFC 0004: every Doc referenced from leaves() must come from a registered DocProvider. */
    @Override public List<Doc> docs() {
        return List.of(MyIntroDoc.INSTANCE);
    }
}
```

A non-root sub-catalogue picks `L1_Catalogue<MyHomeCatalogue>` (one level deeper than its parent) and declares the typed `parent()`:

```java
public record MyDoctrineCatalogue() implements L1_Catalogue<MyHomeCatalogue> {

    public static final MyDoctrineCatalogue INSTANCE = new MyDoctrineCatalogue();

    @Override public MyHomeCatalogue parent() { return MyHomeCatalogue.INSTANCE; }
    @Override public String name()           { return "Doctrines"; }

    // L2 children would go here via subCatalogues() — most studios won't need this.

    @Override public List<Entry> leaves() {
        return List.of(Entry.of(MyDoctrineDoc.INSTANCE));
    }
}
```

**Rules**:
- Catalogue is a record (stateless). Identity = the Java class.
- The root catalogue (the one passed to `StudioBrand`) is **L0**. Direct children are **L1**, then L2, L3, … up to L8.
- Non-root catalogues **must** declare `public ParentClass parent() { return ParentClass.INSTANCE; }` — the framework reads it to derive breadcrumbs.
- Implement `DocProvider` and return the same Docs you reference from `leaves()`, or boot will fail with "Catalogue X references Doc Y which is not in the DocRegistry".
- Use `Entry.of(navigable)` for AppModule entries — supply tile name + summary at the binding site, not on the AppModule.
- The renderer surfaces sub-catalogues *before* leaves. If you want a specific position, restructure rather than rely on insertion order.

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

### 6. Studio record (RFC 0012)

The `Studio<L0>` record is your studio's typed declaration of what it brings to a server — home catalogue, intrinsic apps, plans, themes, and standalone brand. Six accessors, one INSTANCE field, written once and rarely touched.

`com/example/studio/MyStudio.java`:

```java
package com.example.studio;

import hue.captains.singapura.js.homing.core.SvgRef;
import hue.captains.singapura.js.homing.studio.base.Studio;
import hue.captains.singapura.js.homing.studio.base.app.StudioBrand;

public record MyStudio() implements Studio<MyHomeCatalogue> {

    public static final MyStudio INSTANCE = new MyStudio();

    @Override public MyHomeCatalogue home() { return MyHomeCatalogue.INSTANCE; }

    // apps(), plans(), themes() all default to empty — override only when you add some.

    @Override public StudioBrand standaloneBrand() {
        return new StudioBrand(
                "My Studio",
                MyHomeCatalogue.class,
                new SvgRef<>(MyStudioLogo.INSTANCE, new MyStudioLogo.logo())
                // Drop the SvgRef arg if no custom logo — defaults to a coloured square.
        );
    }
}
```

**Rules**:
- `home()` is the only required method — it names your L0 catalogue.
- `catalogues()` defaults to a BFS walk from `home().subCatalogues()` — override only if you have orphan catalogues unreachable from the home tree.
- `apps()` is for your studio's intrinsic apps (e.g. a custom DocBrowser-style page). The harness apps (CatalogueAppHost, PlanAppHost, DocReader, ThemesIntro) are layered on by `DefaultFixtures` — don't list them here.
- `plans()` is empty by default; supply your `List<Plan>` here if any.
- `standaloneBrand()` is what appears when this studio runs standalone. Under a multi-studio umbrella, the umbrella's brand wins.

### 7. Server

The server constructs an `Umbrella` (just a `Solo` for a single-studio deploy), wraps it in `DefaultFixtures`, and hands the pair to `Bootstrap`. Three lines.

```java
package com.example.studio;

import hue.captains.singapura.js.homing.studio.base.Bootstrap;
import hue.captains.singapura.js.homing.studio.base.DefaultFixtures;
import hue.captains.singapura.js.homing.studio.base.DefaultRuntimeParams;
import hue.captains.singapura.js.homing.studio.base.Studio;
import hue.captains.singapura.js.homing.studio.base.Umbrella;

public final class MyStudioServer {
    private MyStudioServer() {}

    public static void main(String[] args) {
        Umbrella<Studio<?>> umbrella = new Umbrella.Solo<>(MyStudio.INSTANCE);
        new Bootstrap<>(new DefaultFixtures<>(umbrella), new DefaultRuntimeParams(8080)).start();
    }
}
```

**Port**: pick something — 8080 is conventional. If running alongside another studio, pick a different port.

**Multi-studio composition**: compose multiple studios under one umbrella by switching `Solo` to `Group`:

```java
Umbrella<Studio<?>> umbrella = new Umbrella.Group<>(
        "My Multi-Studio Deploy",
        "Two studios composed onto one server.",
        List.of(
                new Umbrella.Solo<>(MyStudio.INSTANCE),
                new Umbrella.Solo<>(OtherStudio.INSTANCE)
        ));
new Bootstrap<>(new DefaultFixtures<>(umbrella), new DefaultRuntimeParams(8080)).start();
```

The Bootstrap unions every studio's catalogues, apps, and plans automatically. Brand defaults to the first studio's `standaloneBrand()`.

**Custom harness**: to add framework-level apps or actions on top of the defaults, write your own `Fixtures<Studio<?>>` implementation:

```java
public record MyFixtures(Umbrella<Studio<?>> umbrella) implements Fixtures<Studio<?>> {
    @Override public List<AppModule<?,?>> harnessApps() {
        var defaults = new DefaultFixtures<>(umbrella).harnessApps();
        return Stream.concat(defaults.stream(), Stream.of(MyExtraApp.INSTANCE)).toList();
    }
    @Override public NodeChrome chromeFor(Umbrella<Studio<?>> node) {
        return new DefaultFixtures<>(umbrella).chromeFor(node);   // or customize
    }
}
```

Pass `new MyFixtures(umbrella)` to `Bootstrap` instead of `new DefaultFixtures<>(umbrella)`.

### 8. Verify

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
- **Sub-catalogues**: add another catalogue record one level deeper (e.g. `MyDoctrineCatalogue implements L1_Catalogue<MyHomeCatalogue>`), and add it to the parent's `subCatalogues()` method. Per RFC 0012, your `MyStudio.catalogues()` default walks the closure automatically — you don't list catalogues anywhere else. Children type-checked: an L2 catalogue can't be listed as a parent's L1 child.
- **Plan trackers**: see `PlanKitDoc` (Building Blocks catalogue) — two files (`Steps.java` + `PlanData.java`), append to `MyStudio.plans()`.
- **Custom themes**: see the `create-homing-theme` skill. Returned from `MyStudio.themes()`.

## What to never do

- **Author manual `.js` files in `homing/js/<your-package>/`** — if you need this, you've left this skill. The framework's intent is that downstream code is **Java records + classpath markdown + classpath SVG**, never hand-rolled JavaScript.
- **Bypass `StudioBrand`** — don't hardcode brand strings in your renderers. Brand data comes from `/brand` server-side and threads through everywhere automatically.
- **Skip the conformance tests** — they're 4-line subclasses each and catch entire classes of silent-from-CI / broken-in-browser bugs.
- **Reuse another studio's UUIDs** for your Docs — UUIDs are wire-stable identities; collisions cause registry errors.
- **Reach for `public static` methods anywhere in the studio code** — per the Functional Objects doctrine and RFC 0012, the framework has no public static methods; downstream studios follow suit. Carry behaviour on `INSTANCE` fields of records.
- **Hand-list catalogues, apps, or plans in `MyStudioServer.main()`** — that's the old shape. Everything lives on `MyStudio` now; the server is three lines.

## Reference reading inside a running studio

- `homing-studio` (the framework's own studio) — `StudioServer.java` is a complete working reference.
- `homing-demo/.../studio/DemoStudioServer.java` — minimal example shipping a single Catalogue + intro Doc + custom (turtle) logo.
- `homing-studio-base/README.md` — full agent-targeted reference.
- `CatalogueKitDoc` and `PlanKitDoc` (in any running studio's Building Blocks catalogue) — the typed-container recipe.
