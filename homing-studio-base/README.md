# Downstream Studio Pattern — Bootstrap a Studio with `homing-studio-base`

How to spin up a new studio project (homepage + apps + typed markdown docs +
theme picker) on top of `homing-studio-base` in **one file** plus per-app pairs.

This guide is written for coding agents. Each section gives a concrete
template you can paste in, a one-line verification command, and the failure
mode if the template is wrong. Skim the templates; copy the matching ones
for your project; ignore the rest.

---

## What you get for free

Depending on `homing-studio-base` and calling `StudioBootstrap.start(...)`
gives you:

| Endpoint | Behaviour |
|---|---|
| `/`               | redirects to your home app |
| `/app?app=<name>` | renders any registered `AppModule`, kebab-cased simple name |
| `/module`         | served ES module per Java module (auto-imports + nav + params) |
| `/css-content`    | typed `CssGroupImpl`-backed stylesheets, theme-keyed |
| `/theme-vars`     | semantic CSS variables for the active theme |
| `/theme-globals`  | global CSS rules (light/dark `@media` overrides) |
| `/doc`            | classpath markdown / html / txt / json / svg by `?path=` |

Plus: a fixed-position **theme picker** in every page (top-right), the
**Default / Forest / Sunset** themes, light/dark adaptation per theme, and
an **internal default** for `?theme=` so the URL stays clean.

---

## When this pattern fits

Reach for this whenever you want a Java-backed reference site, plan tracker,
or design tool that:

- has a small fixed set of "apps" (homepage + a handful of pages)
- serves typed reference docs from the classpath
- benefits from typed Java-side declarations (so renames are compile errors)
- doesn't need user accounts, persistence, or server-side state

If the project is a public-facing product UI, generic CRUD app, or anything
needing auth / DB persistence, this is the wrong stack.

---

## Project layout

A studio is a Maven multi-module with three pieces:

```
my-studio/
├── pom.xml                          ← parent: dependency management only
├── my-docs/                         ← markdown + typed Doc records
│   ├── pom.xml
│   └── src/main/
│       ├── java/<pkg>/MyDocs.java
│       └── resources/docs/**.md
└── studio-main/                     ← server entry + AppModules + JS views
    ├── pom.xml
    └── src/main/
        ├── java/<pkg>/MyStudioServer.java
        ├── java/<pkg>/MyHome.java   ← AppModule per page
        └── resources/homing/js/<pkg>/MyHome.js
```

**Why split docs vs. studio-main?** The docs module is a reusable data
artifact — a different studio (or a CLI) can depend on it without pulling in
the Vert.x server. If you don't anticipate that reuse, a single module is
fine.

### Parent `pom.xml`

```xml
<project>
    <modelVersion>4.0.0</modelVersion>
    <groupId>com.example.studio</groupId>
    <artifactId>my-studio</artifactId>
    <version>1.0-SNAPSHOT</version>
    <packaging>pom</packaging>

    <properties>
        <maven.compiler.source>21</maven.compiler.source>
        <maven.compiler.target>21</maven.compiler.target>
        <homing.version>0.0.1</homing.version>
    </properties>

    <modules>
        <module>my-docs</module>
        <module>studio-main</module>
    </modules>

    <dependencyManagement>
        <dependencies>
            <dependency>
                <groupId>io.github.captainssingapura-hue.homing.js</groupId>
                <artifactId>homing-studio-base</artifactId>
                <version>${homing.version}</version>
            </dependency>
            <dependency>
                <groupId>io.github.captainssingapura-hue.homing.js</groupId>
                <artifactId>homing-libs</artifactId>
                <version>${homing.version}</version>
            </dependency>
            <dependency>
                <groupId>${project.groupId}</groupId>
                <artifactId>my-docs</artifactId>
                <version>${project.version}</version>
            </dependency>
        </dependencies>
    </dependencyManagement>
</project>
```

### `my-docs/pom.xml`

```xml
<dependencies>
    <dependency>
        <groupId>io.github.captainssingapura-hue.homing.js</groupId>
        <artifactId>homing-studio-base</artifactId>
    </dependency>
</dependencies>
```

### `studio-main/pom.xml`

```xml
<dependencies>
    <dependency>
        <groupId>${project.groupId}</groupId>
        <artifactId>my-docs</artifactId>
    </dependency>
    <dependency>
        <groupId>io.github.captainssingapura-hue.homing.js</groupId>
        <artifactId>homing-studio-base</artifactId>
    </dependency>
    <dependency>
        <groupId>io.github.captainssingapura-hue.homing.js</groupId>
        <artifactId>homing-libs</artifactId>
    </dependency>
</dependencies>
```

---

## The server entry — one file

`studio-main/src/main/java/<pkg>/MyStudioServer.java`:

```java
package com.example.studio;

import hue.captains.singapura.js.homing.core.AppModule;
import hue.captains.singapura.js.homing.studio.base.StudioBootstrap;

import java.util.List;

public class MyStudioServer {
    public static void main(String[] args) {
        StudioBootstrap.start(8081, List.<AppModule<?>>of(
                MyHome.INSTANCE,
                MyDocBrowser.INSTANCE,
                MyDocReader.INSTANCE
        ));
    }
}
```

That's the whole server. The **first app in the list is the home** — `/`
redirects to it.

If you need a custom theme registry or extra HTTP endpoints, use the longer
overload:

```java
StudioBootstrap.start(8081, apps,
        MyThemeRegistry.INSTANCE,    // ThemeRegistry — defaults to StudioThemeRegistry
        MyDefaultTheme.INSTANCE,     // Theme         — defaults to HomingDefault
        Map.of("/my-data", new MyDataGetAction()),  // extra GET routes
        Map.of());                                  // extra POST routes
```

**Verify:** `curl -s -o /dev/null -w '%{http_code}\n' http://localhost:8081/`
should print `200` (redirects to your home), and the body should be a tiny
`<meta http-equiv="refresh">` page pointing at `/app?app=my-home`.

---

## Anatomy of an `AppModule`

Every page is one Java class + one JS file. The Java side declares typed
imports (CSS, nav targets, libs); the framework auto-emits the corresponding
`import { … } from "/module?class=…"` lines into the served JS, so the JS
can reference those bindings as plain consts.

### `MyHome.java`

```java
public record MyHome() implements AppModule<MyHome> {

    record appMain() implements AppModule._AppMain<MyHome> {}
    public record link() implements AppLink<MyHome> {}
    public static final MyHome INSTANCE = new MyHome();

    @Override public String title() { return "My studio"; }

    @Override
    public ImportsFor<MyHome> imports() {
        return ImportsFor.<MyHome>builder()
                // Nav targets — every app you'll link to needs its `link()` here.
                .add(new ModuleImports<>(List.of(new MyHome.link()),       MyHome.INSTANCE))
                .add(new ModuleImports<>(List.of(new MyDocBrowser.link()), MyDocBrowser.INSTANCE))
                // Studio chrome — the CSS classes your JS references.
                .add(new ModuleImports<>(List.of(
                        new StudioStyles.st_root(),
                        new StudioStyles.st_header(),
                        new StudioStyles.st_brand(),
                        new StudioStyles.st_main(),
                        new StudioStyles.st_title(),
                        new StudioStyles.st_subtitle(),
                        new StudioStyles.st_section()
                        // …list every st_* you actually use. Unlisted classes
                        // are still type-checked but won't be exported into
                        // your JS module — referencing them errors out.
                ), StudioStyles.INSTANCE))
                .build();
    }

    @Override
    public ExportsOf<MyHome> exports() {
        return new ExportsOf<>(INSTANCE, List.of(new appMain()));
    }
}
```

### `MyHome.js`

`studio-main/src/main/resources/homing/js/<pkg>/MyHome.js` — path mirrors
the Java package exactly. Plain ES6: the framework prepends every `import`
itself (typed CSS classes, nav, params, doc handles, libs), so the file
contains only the `appMain` function body and any helpers it needs:

```js
function appMain(rootElement) {

    function cn() {
        var parts = [];
        for (var i = 0; i < arguments.length; i++) {
            if (arguments[i]) parts.push(css.className(arguments[i]));
        }
        return parts.join(" ");
    }

    rootElement.innerHTML = ''
        + '<div class="' + cn(st_root) + '">'
        + '  <div class="' + cn(st_header) + '">'
        + '    <a class="' + cn(st_brand) + '" ' + href.toAttr(nav.MyHome()) + '>My studio</a>'
        + '  </div>'
        + '  <div class="' + cn(st_main) + '">'
        + '    <h1 class="' + cn(st_title) + '">Hello</h1>'
        + '    <p class="' + cn(st_subtitle) + '">'
        + '      <a ' + href.toAttr(nav.MyDocBrowser()) + '>Docs</a>'
        + '    </p>'
        + '  </div>'
        + '</div>';
}
```

**Auto-injected globals available in every `.js`:**
- `nav.<AppName>(params?)` — typed URL builder per app you imported (RFC 0001)
- `params.<name>` — typed query params from your `Params` record (see "Typed query params")
- `css` — `CssClassManager` instance (`css.className`, `addClass`, `toggleClass`, etc.)
- `href` — `HrefManager` (`href.toAttr(url)` produces a properly-escaped `href="…"` attribute)
- Each imported CSS class as a const (`st_root`, `st_header`, …)
- Each imported Doc record / Lib symbol as a const

**Verify:** `curl -s "http://localhost:8081/app?app=my-home" | grep title`
returns the rendered page; `curl -s "http://localhost:8081/module?class=com.example.studio.MyHome" | head -5`
shows the auto-prepended `import` block + your `appMain` function + `export {appMain}`.

---

## Typed query params

For pages that take URL parameters (e.g. a doc reader takes `?path=…`),
declare a `Params` record:

```java
public record MyDocReader() implements AppModule<MyDocReader> {
    public record Params(String path) {}
    @Override public Class<?> paramsType() { return Params.class; }
    // … rest as MyHome
}
```

The framework emits a frozen `params` const into the served JS:
```js
// auto-generated
const params = Object.freeze({ path: (sp.get("path") || "") });
```

Build links to this app from other apps with `nav.MyDocReader({path: "x"})`.

---

## Typed Docs (markdown reference)

Drop `.md` files under `my-docs/src/main/resources/docs/` then declare them
as records inside a `DocGroup`:

`my-docs/src/main/java/<pkg>/MyDocs.java`:

```java
public record MyDocs() implements DocGroup<MyDocs> {

    public static final MyDocs INSTANCE = new MyDocs();

    public record Readme() implements MarkdownDoc<MyDocs> {
        @Override public String path()     { return "docs/README.md"; }
        @Override public String title()    { return "README"; }
        @Override public String category() { return "OVERVIEW"; }
    }
    public record GettingStarted() implements MarkdownDoc<MyDocs> {
        @Override public String path()     { return "docs/getting-started.md"; }
        @Override public String title()    { return "Getting started"; }
        @Override public String category() { return "GUIDE"; }
    }
    // …one record per .md file

    @Override
    public List<Doc<MyDocs>> docs() {
        return List.of(new Readme(), new GettingStarted());
    }
}
```

Doc kinds beyond markdown — `HtmlDoc`, `PlainTextDoc`, `JsonDoc`, `SvgDoc`,
or `Doc<D>` directly with custom `contentType()` + `fileExtension()`.

A doc browser `AppModule` imports these records and projects them through
the auto-injected `docs` manager:

```javascript
// in MyDocBrowser.js — every Doc record imported by name is a const here.
var allDocs = [Readme, GettingStarted, /* … */];
allDocs.forEach(function(d) {
    console.log(docs.title(d), docs.summary(d), docs.category(d), docs.url(d));
    // docs.fetch(d) → Promise<string> body via /doc?path=…
});
```

A reader fetches the body and renders it; for markdown, depend on
`MarkedJs` from `homing-libs`:

```java
.add(new ModuleImports<>(List.of(new MarkedJs.marked()), MarkedJs.INSTANCE))
```

```js
// in the reader's .js
fetch("/doc?path=" + encodeURIComponent(params.path))
    .then(function(r) { return r.text(); })
    .then(function(md) { bodyEl.innerHTML = marked.parse(md); });
```

**Verify:** `curl "http://localhost:8081/doc?path=docs/README.md"` returns
the markdown body; the browser's served JS module has the line
`const Readme = _docs.doc("docs/README.md", …)` near the top.

---

## Custom themes (optional)

`StudioThemeRegistry.INSTANCE` ships Default / Forest / Sunset. To add your
own theme alongside them, declare a `Theme` record + `ThemeVariables<TH>` +
`ThemeGlobals<TH>` mirroring `HomingDefault.java`, then compose a registry
that includes both yours and the studio's:

```java
public record MyThemeRegistry() implements ThemeRegistry {
    public static final MyThemeRegistry INSTANCE = new MyThemeRegistry();

    @Override public List<Theme>             themes()    {
        return List.concat(StudioThemeRegistry.INSTANCE.themes(), List.of(MyTheme.INSTANCE));
    }
    @Override public List<ThemeVariables<?>> variables() { /* same pattern */ }
    @Override public List<ThemeGlobals<?>>   globals()   { /* same pattern */ }
}
```

Pass it to `StudioBootstrap.start(port, apps, MyThemeRegistry.INSTANCE, MyTheme.INSTANCE, …)`.
The picker auto-renders your theme as an extra `<option>`.

For the variable vocabulary (`COLOR_SURFACE`, `COLOR_TEXT_PRIMARY`,
`SPACE_*`, `RADIUS_*`), see `StudioVars.java`.

---

## Cookbook of pitfalls

| Symptom | Likely cause |
|---|---|
| `404 No app registered with this simple name` | Forgot to add the app to the `List` passed to `StudioBootstrap.start` |
| `nav.SomeApp` is undefined in the JS | Forgot `.add(new ModuleImports<>(List.of(new SomeApp.link()), SomeApp.INSTANCE))` in the importing app |
| CSS class works but is unstyled | Forgot to list the `st_*` class in the `StudioStyles` import block; or the class is misspelt — Java is case-sensitive |
| Doc browser missing some docs | Forgot to add the new `Doc` record to `MyDocs.docs()` list |
| `/doc?path=…` returns 404 | Path must start `docs/…` and end in a registered extension; no `..`, no leading slash |
| Theme picker doesn't appear | `themeRegistry.themes().size() < 2` — picker hides itself when there's only one option |
| All pages render unstyled (white background) | `StudioStyles` not imported, or the bundle endpoints failed (check `/theme-vars` returns CSS, not 404) |

---

## Reference layout — verified end-to-end

The `notation-studio` project at `Q:/repos/music/notation-studio` is a
working reference. Three modules, ~500 LoC of Java + JS, 31 typed Docs.
Every pattern above is in use there.

---

## Build + run

```bash
mvn install               # at parent
java -cp "studio-main/target/classes;<every dep jar>" com.example.studio.MyStudioServer
```

Or for a fat-jar / `exec:java` setup, add the standard
`maven-shade-plugin` / `exec-maven-plugin` configuration to `studio-main/pom.xml`.

After launch:

```bash
curl -s -o /dev/null -w 'root=%{http_code}\n'    http://localhost:8081/
curl -s -o /dev/null -w 'home=%{http_code}\n'    "http://localhost:8081/app?app=my-home"
curl -s -o /dev/null -w 'css=%{http_code}\n'     "http://localhost:8081/css-content?class=hue.captains.singapura.js.homing.studio.base.css.StudioStyles"
curl -s -o /dev/null -w 'theme=%{http_code}\n'   "http://localhost:8081/theme-vars?theme=default"
```

Any non-200 → check the cookbook above.
