# Homing User Guide

Build functional, beautiful, and composable UI building blocks in Java + JavaScript.

Homing lets you define your frontend module graph, SVG assets, and CSS dependencies in Java, then write the actual UI logic in plain `.js` and `.css` files. The framework generates correct ES6 module wiring, serves everything over HTTP, and handles CSS dependency resolution automatically.

---

## Table of Contents

1. [Getting Started](#getting-started)
2. [Project Setup](#project-setup)
3. [Building Blocks](#building-blocks)
   - [ES Modules](#es-modules)
   - [SVG Groups](#svg-groups)
   - [CSS Beings](#css-beings)
   - [External Modules](#external-modules)
   - [App Modules](#app-modules)
4. [Composing UI Components](#composing-ui-components)
5. [Styling with Type-Safe CSS](#styling-with-type-safe-css)
6. [Linking Between Apps](#linking-between-apps)
7. [Modeling External Destinations](#modeling-external-destinations)
8. [Running the Dev Server](#running-the-dev-server)
9. [Live Reload During Development](#live-reload-during-development)
10. [Resource File Conventions](#resource-file-conventions)
11. [Server Endpoints Reference](#server-endpoints-reference)
12. [Conformance Testing](#conformance-testing)
13. [Walkthrough: Dancing Animals](#walkthrough-dancing-animals)
14. [Walkthrough: Moving Animal (Shared Components)](#walkthrough-moving-animal-shared-components)

---

## Getting Started

### Prerequisites

- Java 21+
- Maven
- `ja-http` artifacts installed locally:
  ```bash
  cd /path/to/ja-http && mvn install -DskipTests
  ```

### Your First App in 5 Minutes

```
my-app/
  pom.xml
  src/main/java/com/example/
    MyApp.java            # AppModule declaration
    MyServer.java         # Server entry point
  src/main/resources/homing/js/com/example/
    MyApp.js              # UI logic
```

**1. Declare your app module:**

```java
package com.example;

import hue.captains.singapura.js.homing.core.*;
import java.util.List;

public record MyApp() implements AppModule<MyApp> {

    record appMain() implements AppModule._AppMain<MyApp> {}

    public static final MyApp INSTANCE = new MyApp();

    @Override public String title() { return "My App"; }

    @Override
    public ImportsFor<MyApp> imports() {
        return ImportsFor.noImports();
    }

    @Override
    public ExportsOf<MyApp> exports() {
        return new ExportsOf<>(INSTANCE, List.of(new appMain()));
    }
}
```

**2. Write the UI logic:**

Place at `src/main/resources/homing/js/com/example/MyApp.js`:

```javascript
function appMain(rootElement) {
    const h1 = document.createElement("h1");
    h1.textContent = "Hello from Homing!";
    rootElement.appendChild(h1);
}
```

**3. Start a server:**

```java
package com.example;

import hue.captains.singapura.js.homing.server.*;
import hue.captains.singapura.tao.http.vertx.VertxActionHost;

public class MyServer {
    public static void main(String[] args) {
        var registry = new JapjsActionRegistry(new QueryParamResolver());
        new VertxActionHost(registry, 8080).start();
    }
}
```

**4. Open your browser:**

```
http://localhost:8080/app?class=com.example.MyApp
```

The server generates an HTML page that imports your module and calls `appMain` with a root DOM element. That's it.

---

## Project Setup

### Maven Dependencies

```xml
<!-- In your module's pom.xml -->
<dependencies>
    <!-- Core: module interfaces, writers, resolvers -->
    <dependency>
        <groupId>io.github.captainssingapura-hue.Homing</groupId>
        <artifactId>homing-core</artifactId>
        <version>${Homing.version}</version>
    </dependency>

    <!-- Server: HTTP hosting for modules and apps -->
    <dependency>
        <groupId>io.github.captainssingapura-hue.Homing</groupId>
        <artifactId>homing-server</artifactId>
        <version>${Homing.version}</version>
    </dependency>

    <!-- Conformance: CSS conformance test base class (test scope) -->
    <dependency>
        <groupId>io.github.captainssingapura-hue.Homing</groupId>
        <artifactId>homing-conformance</artifactId>
        <version>${Homing.version}</version>
        <scope>test</scope>
    </dependency>
</dependencies>
```

### Recommended Project Layout

```
your-project/
  src/main/java/com/example/
    es/                         # ES module declarations
      MyWidget.java
      HelperModule.java
    css/                        # CSS being declarations
      WidgetStyles.java
      ThemeStyles.java
    MyAppServer.java            # Server entry point
  src/main/resources/
    homing/js/com/example/es/    # JavaScript content files
      MyWidget.js
      HelperModule.js
    homing/css/com/example/css/  # CSS content files
      WidgetStyles.css
      WidgetStyles.dark.css     # Theme variant
      ThemeStyles.css
    homing/svg/com/example/es/   # SVG asset files
      Icons/                    # Named after SvgGroup class
        search.svg
        menu.svg
```

---

## Building Blocks

Homing provides five core building blocks for composable UIs. Each is a Java record that declares structure; the actual content lives in resource files.

### ES Modules

An `EsModule` declares what a JavaScript module imports and exports. The record name maps to the module identity; inner records map to exported JS identifiers.

```java
public record Counter() implements EsModule<Counter> {

    // Each inner record becomes a named export
    record CounterClass() implements Exportable._Class<Counter> {}
    record createCounter() implements Exportable._Constant<Counter> {}

    public static final Counter INSTANCE = new Counter();

    @Override
    public ImportsFor<Counter> imports() {
        return ImportsFor.noImports();
    }

    @Override
    public ExportsOf<Counter> exports() {
        return new ExportsOf<>(INSTANCE, List.of(
            new CounterClass(), new createCounter()
        ));
    }
}
```

Paired with `homing/js/com/example/es/Counter.js`:

```javascript
class CounterClass {
    constructor(initial) { this.value = initial; }
    increment() { return ++this.value; }
    decrement() { return --this.value; }
}

function createCounter(initial) {
    return new CounterClass(initial || 0);
}
```

**Generated output** (served at `/module?class=com.example.es.Counter`):

```javascript
class CounterClass {
    constructor(initial) { this.value = initial; }
    increment() { return ++this.value; }
    decrement() { return --this.value; }
}

function createCounter(initial) {
    return new CounterClass(initial || 0);
}

export {CounterClass, createCounter};
```

**Key rules:**
- Inner record names must exactly match the JS identifiers they represent
- Use `Exportable._Class` for classes, `Exportable._Constant` for functions/constants
- The JS file contains only the implementation. Homing generates `import` and `export` lines.

### Importing from Other Modules

```java
public record Dashboard() implements DomModule<Dashboard> {

    record DashboardView() implements Exportable._Class<Dashboard> {}

    public static final Dashboard INSTANCE = new Dashboard();

    @Override
    public ImportsFor<Dashboard> imports() {
        return ImportsFor.<Dashboard>builder()
            .add(new ModuleImports<>(
                List.of(new Counter.createCounter()),   // what to import
                Counter.INSTANCE                         // from which module
            ))
            .build();
    }

    @Override
    public ExportsOf<Dashboard> exports() {
        return new ExportsOf<>(INSTANCE, List.of(new DashboardView()));
    }
}
```

In `Dashboard.js`, you can use `createCounter` directly -- Homing generates the import statement:

```javascript
class DashboardView {
    constructor(rootElement) {
        const counter = createCounter(0);
        // ... build UI using the imported function
    }
}
```

**Generated output:**

```javascript
import {createCounter} from "/module?class=com.example.es.Counter";

class DashboardView {
    constructor(rootElement) {
        const counter = createCounter(0);
        // ...
    }
}

export {DashboardView};
```

### SVG Groups

An `SvgGroup` bundles SVG assets into a single ES module. Each SVG file becomes a template literal constant.

```java
public record Icons() implements SvgGroup<Icons> {

    record search() implements SvgBeing<Icons> {}
    record menu() implements SvgBeing<Icons> {}
    record close() implements SvgBeing<Icons> {}

    public static final Icons INSTANCE = new Icons();

    @Override
    public List<SvgBeing<Icons>> svgBeings() {
        return List.of(new search(), new menu(), new close());
    }

    @Override
    public ExportsOf<Icons> exports() {
        return new ExportsOf<>(this, List.copyOf(svgBeings()));
    }
}
```

Place SVG files at:
```
homing/svg/com/example/es/Icons/search.svg
homing/svg/com/example/es/Icons/menu.svg
homing/svg/com/example/es/Icons/close.svg
```

**Generated output:**

```javascript
const search = `<svg xmlns="http://www.w3.org/2000/svg" ...>...</svg>`;
const menu = `<svg xmlns="http://www.w3.org/2000/svg" ...>...</svg>`;
const close = `<svg xmlns="http://www.w3.org/2000/svg" ...>...</svg>`;

export {search, menu, close};
```

Other modules import SVGs like any other export:

```java
@Override
public ImportsFor<NavBar> imports() {
    return ImportsFor.<NavBar>builder()
        .add(new ModuleImports<>(
            List.of(new Icons.menu(), new Icons.close()),
            Icons.INSTANCE))
        .build();
}
```

In your JS, use them as raw SVG strings:

```javascript
const wrapper = document.createElement("div");
wrapper.innerHTML = menu;  // SVG string from the Icons module
```

### CSS Beings

A `CssGroup` is an ES module that declares a CSS resource, its dependencies, and typed CSS class exports. The server generates a header-only JS module that loads the CSS and exports frozen `CssClass` objects.

```java
public record CardStyles() implements CssGroup<CardStyles> {
    public static final CardStyles INSTANCE = new CardStyles();

    // Inner records map to CSS class names (snake_case -> kebab-case)
    public record card() implements CssClass<CardStyles> {}
    public record card_header() implements CssClass<CardStyles> {}
    public record card_body() implements CssClass<CardStyles> {}

    @Override
    public CssImportsFor<CardStyles> cssImports() {
        return CssImportsFor.none(this);  // no CSS dependencies
    }

    @Override
    public List<CssClass<CardStyles>> cssClasses() {
        return List.of(new card(), new card_header(), new card_body());
    }
}
```

Place the CSS at `homing/css/com/example/css/CardStyles.css`:

```css
.card {
    border: 1px solid #e2e8f0;
    border-radius: 8px;
    box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
}

.card-header {
    padding: 12px 16px;
    font-weight: 600;
    border-bottom: 1px solid #e2e8f0;
}

.card-body {
    padding: 16px;
}
```

**Generated output** (header-only JS module):

```javascript
import { CssClassManagerInstance as _css } from "/module?class=...CssClassManager";
await _css.loadCss("com.example.css.CardStyles", "light");
const card = _css.cls("card");
const card_header = _css.cls("card-header");
const card_body = _css.cls("card-body");

export {card, card_header, card_body};
```

Each exported value is a frozen `{ _n: "css-name" }` object that the `CssClassManager` recognizes.

**CSS dependencies** are declared in `cssImports()`:

```java
public record FormStyles() implements CssGroup<FormStyles> {
    public static final FormStyles INSTANCE = new FormStyles();

    public record form_field() implements CssClass<FormStyles> {}

    @Override
    public CssImportsFor<FormStyles> cssImports() {
        // FormStyles depends on CardStyles -- CardStyles loads first
        return new CssImportsFor<>(this, List.of(CardStyles.INSTANCE));
    }

    @Override
    public List<CssClass<FormStyles>> cssClasses() {
        return List.of(new form_field());
    }
}
```

### External Modules

An `ExternalModule` wraps a third-party JS library. The JS resource file handles the actual loading strategy (CDN import or global script reference).

```java
public record ToneJs() implements ExternalModule<ToneJs> {
    public static final ToneJs INSTANCE = new ToneJs();

    public record Synth() implements Exportable._Class<ToneJs> {}
    public record MembraneSynth() implements Exportable._Class<ToneJs> {}
    public record start() implements Exportable._Constant<ToneJs> {}

    @Override
    public ExportsOf<ToneJs> exports() {
        return new ExportsOf<>(INSTANCE, List.of(
            new Synth(), new MembraneSynth(), new start()
        ));
    }
}
```

The corresponding JS file re-exports from the CDN or extracts globals:

```javascript
const { Synth, MembraneSynth, start } = await import("https://cdn.example.com/tone.js");
```

### App Modules

An `AppModule` is a `DomModule` that serves as a full single-page application entry point. It must export an `appMain` function.

```java
public record MyApp() implements AppModule<MyApp> {

    // The record name "appMain" maps to the JS function name
    record appMain() implements AppModule._AppMain<MyApp> {}

    public static final MyApp INSTANCE = new MyApp();

    @Override public String title() { return "My Application"; }

    @Override
    public ImportsFor<MyApp> imports() {
        return ImportsFor.<MyApp>builder()
            .add(new ModuleImports<>(
                List.of(new Counter.createCounter()),
                Counter.INSTANCE))
            .add(new ModuleImports<>(List.of(
                    new CardStyles.card(),
                    new CardStyles.card_body()
            ), CardStyles.INSTANCE))
            .build();
    }

    @Override
    public ExportsOf<MyApp> exports() {
        return new ExportsOf<>(INSTANCE, List.of(new appMain()));
    }
}
```

The server generates an HTML page at `/app?class=com.example.MyApp`:

```html
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>My Application</title>
</head>
<body>
    <div id="app"></div>
    <script type="module">
        import { appMain } from "/module?class=com.example.MyApp";
        appMain(document.getElementById("app"));
    </script>
</body>
</html>
```

---

## Composing UI Components

The power of Homing is composition. Each building block is self-contained and reusable. You compose them by declaring imports.

### Example: A Themed Dashboard

```
ThemeStyles (CssGroup)              # Base colors, typography
  <- DashboardStyles (CssGroup)     # Dashboard-specific layout

Icons (SvgGroup)                    # search, menu, close
Counter (EsModule)                  # CounterClass, createCounter
UserCard (DomModule)                # imports Counter + Icons

DashboardApp (AppModule)            # imports UserCard + DashboardStyles CSS classes
```

The Java declarations create a compile-time-checked dependency graph. If you rename an export in `Counter`, every module that imports it fails to compile until you fix the reference. No broken imports at runtime.

### Reusing Across Projects

Since modules are plain Maven artifacts, you can publish a library of building blocks:

```xml
<dependency>
    <groupId>com.example</groupId>
    <artifactId>my-ui-blocks</artifactId>
    <version>1.0</version>
</dependency>
```

Consumers import your exports just like any other module:

```java
// In a different project
@Override
public ImportsFor<MyPage> imports() {
    return ImportsFor.<MyPage>builder()
        .add(new ModuleImports<>(
            List.of(new Counter.createCounter()),
            Counter.INSTANCE))
        .build();
}
```

The JS resource files, SVG assets, and CSS files ship inside the JAR. The server reads them from the classpath automatically.

---

## Styling with Type-Safe CSS

### How It Works

When a `DomModule` imports `CssClass` objects from a `CssGroup`, the server automatically:

1. Generates a header-only JS module for the `CssGroup` that loads its CSS and exports frozen class objects
2. Injects a `css` manager (`CssClassManager`) into the consuming DomModule
3. The DomModule's JS file uses `css.*` methods with the imported class objects

**1. Create a CssGroup with typed classes:**

```java
public record CardStyles() implements CssGroup<CardStyles> {
    public static final CardStyles INSTANCE = new CardStyles();

    public record card() implements CssClass<CardStyles> {}
    public record card_header() implements CssClass<CardStyles> {}

    @Override
    public CssImportsFor<CardStyles> cssImports() {
        return CssImportsFor.none(this);
    }

    @Override
    public List<CssClass<CardStyles>> cssClasses() {
        return List.of(new card(), new card_header());
    }
}
```

**2. Import CSS classes in your DomModule:**

```java
@Override
public ImportsFor<MyWidget> imports() {
    return ImportsFor.<MyWidget>builder()
        .add(new ModuleImports<>(List.of(
                new CardStyles.card(),
                new CardStyles.card_header()
        ), CardStyles.INSTANCE))
        .build();
}
```

**3. Use type-safe CSS operations in JavaScript:**

```javascript
function appMain(rootElement) {
    const el = document.createElement("div");
    css.setClass(el, card);              // el.className = "card"
    css.addClass(el, card_header);       // el.classList.add("card-header")
    css.removeClass(el, card_header);    // el.classList.remove("card-header")
    css.toggleClass(el, card_header, condition); // el.classList.toggle(...)
    css.hasClass(el, card_header);       // el.classList.contains(...)
    const name = css.className(card);    // returns "card" as a string
}
```

**4. The generated JS includes:**

```javascript
import { CssClassManagerInstance as css } from "/module?class=...CssClassManager";
import { card, card_header } from "/module?class=...CardStyles";

function appMain(rootElement) { ... }

export {appMain};
```

### Naming Convention

Record names use `snake_case`, which maps 1:1 to `kebab-case` CSS class names via simple underscore-to-hyphen replacement:

| Java Record | CSS Class |
|-------------|-----------|
| `btn_primary` | `btn-primary` |
| `pg_theme_switcher` | `pg-theme-switcher` |
| `paused` | `paused` |

### CSS Dependency Chains

CSS beings can depend on other CSS beings:

```java
public record DarkTheme() implements CssGroup<DarkTheme> {
    public static final DarkTheme INSTANCE = new DarkTheme();

    public record dark_bg() implements CssClass<DarkTheme> {}

    @Override
    public CssImportsFor<DarkTheme> cssImports() {
        // DarkTheme builds on BaseTheme
        return new CssImportsFor<>(this, List.of(BaseTheme.INSTANCE));
    }

    @Override
    public List<CssClass<DarkTheme>> cssClasses() {
        return List.of(new dark_bg());
    }
}
```

When a module imports from `DarkTheme`, the resolver loads `BaseTheme.css` first, then `DarkTheme.css`. Diamond dependencies are resolved once -- no duplicate stylesheets.

### Theme Variants

Place theme-specific CSS files alongside the default:

```
homing/css/.../PlaygroundStyles.css           # default
homing/css/.../PlaygroundStyles.beach.css     # beach theme
homing/css/.../PlaygroundStyles.alpine.css    # alpine theme
homing/css/.../PlaygroundStyles.dracula.css   # dracula theme
```

Theme is selected via `?theme=<name>` and propagates through import URLs automatically, so `CssGroup` modules load the correct theme variant.

Similarly, theme-aware JS modules (like background music) can have theme variants:

```
homing/js/.../PlatformerBgm.js                # default
homing/js/.../PlatformerBgm.beach.js          # beach BGM
```

### Composability Boundary

Shared components that receive CSS class names as parameters (e.g., a cell factory) sit at the composability boundary. They accept raw string class names via `css.className()`:

```javascript
// In the consuming module:
const cell = createAnimalCell(css.className(subway_cell));

// In the shared AnimalCell module:
function createAnimalCell(className) {
    const cell = document.createElement("div");
    cell.className = className;  // raw string -- intentional
    // ...
}
```

The shared module doesn't import CSS classes itself; it receives them from its consumers.

### Organizing CSS

Recommended convention: put CSS beings in a `css` subpackage to separate concerns:

```
com.example/
  es/           # EsModules, AppModules, SvgGroups
  css/          # CssGroups
```

---

## Linking Between Apps

Introduced in [RFC 0001](#ref:rfc-1). This section covers how to declare typed navigation between AppModules — replacing hand-built `?class=` URL strings.

### How it works

Three pieces:

1. **Each AppModule declares an inner `link()` record.** The presence of the record marks the AppModule as a navigation target.
2. **Consumers import that link record** via the standard `ImportsFor` mechanism. The writer adds an entry to a generated `nav` object in the consumer's compiled JS.
3. **An `href` manager is auto-injected** into any DomModule that imports an `AppLink<?>`. All href operations go through this manager — see the [Conformance Testing](#conformance-testing) section.

### Make an AppModule linkable

```java
public record PitchDeck() implements AppModule<PitchDeck> {
    public static final PitchDeck INSTANCE = new PitchDeck();

    record appMain() implements AppModule._AppMain<PitchDeck> {}
    public record link() implements AppLink<PitchDeck> {}

    @Override public String title() { return "Pitch Deck"; }
    @Override public ImportsFor<PitchDeck> imports() { /* ... */ return ImportsFor.noImports(); }
    @Override public ExportsOf<PitchDeck> exports() {
        return new ExportsOf<>(this, List.of(new appMain()));
    }
}
```

The `simpleName()` defaults to a kebab-case derivation of the class name (`PitchDeck` → `pitch-deck`) used as the `?app=` URL identifier. Override it to lock the URL contract independently:

```java
@Override public String simpleName() { return "pitch"; }   // → /app?app=pitch
```

### Declare typed query parameters

If an AppModule accepts query parameters, declare a `Params` record and override `paramsType()`:

```java
public record ProductDetail() implements AppModule<ProductDetail> {
    public record Params(String productId, Optional<String> tab) {}
    public record link() implements AppLink<ProductDetail> {}

    @Override public Class<?> paramsType() { return Params.class; }
    // ... title, imports, exports
}
```

The writer generates a typed `params` const at the top of the module's compiled JS:

```js
function appMain(rootElement) {
    if (params.productId) {
        loadProduct(params.productId, params.tab);  // typed; no URLSearchParams boilerplate
    }
}
```

Supported component types: `String`, primitive numerics, boxed numerics, `boolean`/`Boolean`, `Optional<T>`, `List<T>`, and any `Enum`. Reserved keys (`app`, `theme`, `locale`) collide and are rejected at server boot.

### Import the link in any consumer

```java
@Override
public ImportsFor<MyApp> imports() {
    return ImportsFor.<MyApp>builder()
        // Navigation targets — typed AppLink imports.
        .add(new ModuleImports<>(List.of(new PitchDeck.link()),     PitchDeck.INSTANCE))
        .add(new ModuleImports<>(List.of(new ProductDetail.link()), ProductDetail.INSTANCE))
        // CSS, SVG, EsModule imports as usual.
        .build();
}
```

### Use `nav` and `href` in JS

The framework injects two identifiers into your DomModule:
- `nav.X(params?)` — returns the URL string for app `X`. The function name is the **simple Java class name** of the target.
- `href.X(...)` — applies a URL. The only sanctioned API for href operations.

```js
// Build an <a> tag for innerHTML:
parent.innerHTML = '<a ' + href.toAttr(nav.PitchDeck()) + '>Open the deck</a>';

// With typed params:
const url = nav.ProductDetail({productId: "P-12345", tab: "reviews"});
parent.innerHTML = '<a ' + href.toAttr(url) + '>...</a>';

// Set href directly:
href.set(myAnchor, nav.PitchDeck());

// Create an <a> element:
const a = href.create(nav.PitchDeck(), { text: "Open", className: cn(card) });
parent.appendChild(a);

// Programmatic navigation:
href.navigate(nav.ProductDetail({productId: "..."}));
href.navigate(nav.PitchDeck(), {replace: true});  // history.replaceState

// Open in a new tab:
href.openNew(nav.PitchDeck());

// Same-page anchor:
parent.innerHTML = '<a ' + href.fragment("section-2") + '>Jump</a>';
```

### Server bootstrap — single entry app

Pass one (or a few) entry apps to `SimpleAppResolver`. The resolver walks `AppLink<?>` import edges from the entries to discover the transitive closure of reachable apps. Adding a new linked app requires no resolver update — just import its `link()` somewhere reachable from an entry.

```java
public static void main(String[] args) {
    var resolver = new SimpleAppResolver(List.of(
        MyCatalogue.INSTANCE        // entry — links to everything else
    ));
    var registry = new JapjsActionRegistry(new QueryParamResolver(), resolver);
    var host = new VertxActionHost(registry, 8080);
    host.start();
}
```

### Theme and locale propagation

The generated `nav.X(...)` functions automatically propagate the current `?theme=` and `?locale=` query parameters from `window.location`. Pass an explicit value in params to override:

```js
nav.PitchDeck()                                  // inherits current theme
nav.PitchDeck({theme: "dracula"})                // explicit override
```

---

## Modeling External Destinations

External URLs (GitHub, vendor APIs, `mailto:`, internal team tools) are modeled as **proxy apps** — typed Java declarations with a URL template.

### Built-in proxies

`homing-core.proxies` ships three ready-to-import proxies for common non-HTTP schemes:

| Proxy | Params | Template |
|-------|--------|----------|
| `Mailto` | `(to, subject?, body?, cc?, bcc?)` | `mailto:{to}?subject={subject?}&body={body?}&cc={cc?}&bcc={bcc?}` |
| `Tel`    | `(number)`                          | `tel:{number}` |
| `Sms`    | `(number, body?)`                   | `sms:{number}?body={body?}` |

Import them like any other AppLink:

```java
.add(new ModuleImports<>(List.of(new Mailto.link()), Mailto.INSTANCE))
```

```js
href.toAttr(nav.Mailto({to: "support@example.com", subject: Optional.of("Bug report")}))
href.toAttr(nav.Tel({number: "+15555551234"}))
```

### Declaring your own proxy

For a third-party site or vendor tool, declare a `ProxyApp`:

```java
public record GitHubProxy() implements ProxyApp<GitHubProxy> {
    public static final GitHubProxy INSTANCE = new GitHubProxy();

    public record Params(String repo, Optional<String> path) {}
    public record link() implements AppLink<GitHubProxy> {}

    @Override public String simpleName()  { return "github"; }
    @Override public Class<?> paramsType() { return Params.class; }
    @Override public String urlTemplate() {
        return "https://github.com/{repo}/{path?}";
    }
}
```

URL template syntax (parsed at compile time against the Params record):

| Syntax | Meaning |
|---|---|
| `{name}` | Required interpolation. `name` must exist on Params and must NOT be `Optional`. |
| `{name?}` | Optional interpolation. Must exist and MUST be `Optional`. Substitutes empty string when absent. |
| `{name?:default}` | Optional with literal default value. |
| anything else | Literal text. |

All interpolated values are URL-encoded.

### Why proxies, not raw URLs?

The conformance scanner (next section) forbids raw `href` substrings in user JS — including `'<a href="https://github.com/...">'`. Proxy apps are the typed escape hatch that keeps the discipline uniform: every URL in the rendered DOM, internal or external, comes from a typed Java declaration somewhere.

A useful side effect: when a vendor changes their URL scheme, you update one `urlTemplate()` and every consumer adapts at the next build.

---

## Running the Dev Server

### Basic Setup

```java
public class DevServer {
    public static void main(String[] args) {
        var registry = new JapjsActionRegistry(new QueryParamResolver());
        var host = new VertxActionHost(registry, 8080);

        host.start().onSuccess(server -> {
            System.out.println("Listening on port " + server.actualPort());
        });
    }
}
```

### Running with Maven

```bash
mvn compile exec:java \
  -Dexec.mainClass="com.example.DevServer"
```

All modules, apps, SVGs, and CSS are served dynamically -- no build step for the frontend.

---

## Live Reload During Development

By default, JS, SVG, and CSS content is read from the classpath (compiled resources). This means you need to restart the server after every resource change.

To enable live file reading, set the `homing.devRoot` system property to your source resource directory:

```bash
mvn compile exec:java \
  -Dexec.mainClass="com.example.DevServer" \
  -Dhoming.devRoot=src/main/resources
```

Now the server reads JS, SVG, and CSS files directly from the filesystem. Edit a file, refresh your browser -- no restart needed.

> Note: Java declaration changes (new modules, new exports) still require a recompile. Only resource file content is live-reloaded.

---

## Resource File Conventions

Homing locates resource files by convention based on the Java class's canonical name:

| Building Block | Resource Path Pattern |
|----------------|----------------------|
| EsModule | `homing/js/<canonical-path>.js` |
| EsModule (themed) | `homing/js/<canonical-path>.<theme>.js` |
| SvgGroup | `homing/svg/<canonical-path>/<SvgBeing>.svg` |
| CssGroup | `homing/css/<canonical-path>.css` |
| CssGroup (themed) | `homing/css/<canonical-path>.<theme>.css` |

### Examples

| Java Class | Resource Path |
|------------|---------------|
| `com.example.es.Counter` | `homing/js/com/example/es/Counter.js` |
| `com.example.es.Icons` (SvgGroup) | `homing/svg/com/example/es/Icons/search.svg` |
| `com.example.css.CardStyles` | `homing/css/com/example/css/CardStyles.css` |
| `com.example.css.CardStyles` (dark) | `homing/css/com/example/css/CardStyles.dark.css` |

---

## Server Endpoints Reference

| Endpoint | Response | Description |
|----------|----------|-------------|
| `/app?app=<simple-name>` | `text/html` | Full HTML page that boots the app (RFC 0001 — preferred) |
| `/app?class=<AppModule>` | `text/html` | Legacy — accepts canonical class name |
| `/module?class=<EsModule>` | `application/javascript` | Generated ES module with imports/exports |
| `/css?class=<CssGroup>` | `application/json` | Resolved CSS dependency chain (name + href) |
| `/css-content?class=<CssGroup>` | `text/css` | Raw CSS file content |

All endpoints support `theme` and `locale` query parameters.

---

## Conformance Testing

The `homing-conformance` module provides `CssConformanceTest`, an abstract base class for verifying that DomModule JS files use the type-safe `css.*` API instead of raw CSS class operations.

### Usage

Add `homing-conformance` as a test dependency:

```xml
<dependency>
    <groupId>io.github.captainssingapura-hue.Homing</groupId>
    <artifactId>homing-conformance</artifactId>
    <version>${Homing.version}</version>
    <scope>test</scope>
</dependency>
```

Create a test class:

```java
class MyCssConformanceTest extends CssConformanceTest {

    @Override
    protected List<DomModule<?>> domModules() {
        return List.of(
                MyWidget.INSTANCE,
                MyApp.INSTANCE
        );
    }

    @SuppressWarnings("unchecked")
    @Override
    protected Set<Class<? extends DomModule<?>>> allowList() {
        return Set.of(
                // Modules that intentionally use raw CSS (composability boundary)
                (Class<? extends DomModule<?>>) (Class<?>) SharedCell.class
        );
    }
}
```

### What It Checks

The `@TestFactory` generates a dynamic test per DomModule that has CSS imports (via `CssGroup` in its `imports()`). Each test scans the module's JS resource file for raw CSS class operations:

- `.className =` assignments
- `.classList.add(...)`, `.classList.remove(...)`, `.classList.toggle(...)`
- `.classList.replace(...)`, `.classList.contains(...)`

Modules in the `allowList()` are skipped. Modules without CSS imports are skipped (no `css` manager to use).

### Sibling discipline: `HrefConformanceTest`

`homing-conformance` also ships `HrefConformanceTest`, a sibling base class enforcing the same discipline for href operations (RFC 0001 §6.2 / Amendment 3). The single rule:

> **The literal substring `href` may appear in DomModule JS only as the manager identifier — i.e., immediately followed by `.` and a recognised manager method (`href.toAttr`, `href.set`, `href.create`, `href.openNew`, `href.navigate`, `href.fragment`).**

Forbidden patterns include literal `href=` attributes, `.href` property access, `setAttribute("href", …)`, `window.location.*`, and `window.open(…)`. Comments are stripped before scanning; string contents are preserved (a literal `'<a href="…">'` inside a JS string is exactly the kind of pattern this catches).

Usage mirrors `CssConformanceTest`:

```java
class MyHrefConformanceTest extends HrefConformanceTest {
    @Override protected List<DomModule<?>> domModules() {
        return List.of(MyWidget.INSTANCE, MyApp.INSTANCE);
    }
    @Override protected Set<Class<? extends DomModule<?>>> allowList() {
        return Set.of();   // typically empty — proxy apps remove the need for raw URLs
    }
}
```

The two scanners are sibling disciplines: `css.*` for class operations, `href.*` for URL operations. Together they keep all DOM-mutating operations under typed, refactor-safe control.

---

## Walkthrough: Dancing Animals

This walkthrough builds a complete interactive UI from composable blocks: SVG assets, a shared component, CSS theming, and an app module.

### Step 1: Define SVG Assets

```java
public record CuteAnimal() implements SvgGroup<CuteAnimal> {

    record turtle() implements SvgBeing<CuteAnimal> {}

    public static final CuteAnimal INSTANCE = new CuteAnimal();

    @Override
    public List<SvgBeing<CuteAnimal>> svgBeings() {
        return List.of(new turtle());
    }

    @Override
    public ExportsOf<CuteAnimal> exports() {
        return new ExportsOf<>(this, List.copyOf(svgBeings()));
    }
}
```

Place `turtle.svg` at `homing/svg/.../CuteAnimal/turtle.svg`.

### Step 2: Create a Shared Component

Rather than having each app import the raw SVG directly, extract a shared `AnimalCell` module that wraps the SVG into a reusable DOM element factory:

```java
public record AnimalCell() implements DomModule<AnimalCell> {

    record createAnimalCell() implements Exportable._Constant<AnimalCell> {}

    public static final AnimalCell INSTANCE = new AnimalCell();

    @Override
    public ImportsFor<AnimalCell> imports() {
        return ImportsFor.<AnimalCell>builder()
            .add(new ModuleImports<>(
                List.of(new CuteAnimal.turtle()),
                CuteAnimal.INSTANCE))
            .build();
    }

    @Override
    public ExportsOf<AnimalCell> exports() {
        return new ExportsOf<>(INSTANCE, List.of(new createAnimalCell()));
    }
}
```

In `AnimalCell.js`, the exported function accepts a CSS class name so each consumer controls its own styling:

```javascript
const animals = [turtle];

function createAnimalCell(className) {
    const cell = document.createElement("div");
    cell.className = className;
    const wrapper = document.createElement("div");
    wrapper.innerHTML = animals[Math.floor(Math.random() * animals.length)];
    cell.appendChild(wrapper);
    return cell;
}
```

This creates a dependency chain: `CuteAnimal` -> `AnimalCell` -> consuming apps.

### Step 3: Define Typed CSS Classes

```java
public record SubwayStyles() implements CssGroup<SubwayStyles> {
    public static final SubwayStyles INSTANCE = new SubwayStyles();

    public record subway_title() implements CssClass<SubwayStyles> {}
    public record subway_hint() implements CssClass<SubwayStyles> {}
    public record subway_grid() implements CssClass<SubwayStyles> {}
    public record subway_cell() implements CssClass<SubwayStyles> {}

    @Override
    public CssImportsFor<SubwayStyles> cssImports() {
        return CssImportsFor.none(this);
    }

    @Override
    public List<CssClass<SubwayStyles>> cssClasses() {
        return List.of(new subway_title(), new subway_hint(),
                       new subway_grid(), new subway_cell());
    }
}
```

Write `SubwayStyles.css` with your visual theme -- dark backgrounds, glowing grids, urban typography.

### Step 4: Compose the App

The app imports `createAnimalCell` from the shared component and CSS class objects from `SubwayStyles`:

```java
public record DancingAnimals() implements AppModule<DancingAnimals> {

    record appMain() implements AppModule._AppMain<DancingAnimals> {}
    public static final DancingAnimals INSTANCE = new DancingAnimals();

    @Override public String title() { return "Dancing Animals"; }

    @Override
    public ImportsFor<DancingAnimals> imports() {
        return ImportsFor.<DancingAnimals>builder()
            .add(new ModuleImports<>(
                List.of(new AnimalCell.createAnimalCell()),
                AnimalCell.INSTANCE))
            .add(new ModuleImports<>(List.of(
                    new SubwayStyles.subway_title(),
                    new SubwayStyles.subway_hint(),
                    new SubwayStyles.subway_grid(),
                    new SubwayStyles.subway_cell()
            ), SubwayStyles.INSTANCE))
            .build();
    }

    @Override
    public ExportsOf<DancingAnimals> exports() {
        return new ExportsOf<>(INSTANCE, List.of(new appMain()));
    }
}
```

### Step 5: Write the UI

In `DancingAnimals.js`, use the type-safe `css.*` API with imported class objects:

```javascript
function appMain(rootElement) {
    const h1 = document.createElement("h1");
    css.setClass(h1, subway_title);
    h1.textContent = "Dancing Animals";
    rootElement.appendChild(h1);

    const grid = document.createElement("div");
    css.setClass(grid, subway_grid);
    rootElement.appendChild(grid);

    const cells = [];
    for (let i = 0; i < 25; i++) {
        const cell = createAnimalCell(css.className(subway_cell));
        grid.appendChild(cell);
        cells.push(cell);
    }

    document.addEventListener("keydown", (e) => {
        if (e.key === "ArrowLeft") {
            cells.forEach(cell => cell.style.transform = "scaleX(-1)");
        } else if (e.key === "ArrowRight") {
            cells.forEach(cell => cell.style.transform = "scaleX(1)");
        }
    });
}
```

Note: `createAnimalCell` sits at the composability boundary -- it accepts a raw string class name via `css.className(subway_cell)`, while all direct DOM class operations use `css.setClass()`.

### What Happens at Runtime

1. Browser requests `/app?class=...DancingAnimals`
2. Server returns HTML scaffold that imports the DancingAnimals module
3. Browser requests `/module?class=...DancingAnimals`
4. Server generates:
   ```javascript
   import { CssClassManagerInstance as css } from "/module?class=...CssClassManager";
   import { subway_title, subway_hint, subway_grid, subway_cell } from "/module?class=...SubwayStyles";
   import {createAnimalCell} from "/module?class=...AnimalCell";

   function appMain(rootElement) { ... }

   export {appMain};
   ```
5. Browser requests the `SubwayStyles` module, which loads CSS and exports frozen class objects
6. Browser requests `/module?class=...AnimalCell`, which in turn imports from `/module?class=...CuteAnimal`
7. `appMain` runs, builds the grid using the shared `createAnimalCell` factory

The result: a styled, interactive 5x5 grid of turtles with keyboard-driven animations. Built from composable blocks: one SvgGroup, one shared DomModule, one CssGroup, and one AppModule.

---

## Walkthrough: Moving Animal (Shared Components)

This walkthrough demonstrates how a second app reuses the same shared `AnimalCell` component from the Dancing Animals walkthrough. The dependency graph:

```
CuteAnimal (SvgGroup) -- exports --> turtle
       ^
AnimalCell (DomModule) -- exports --> createAnimalCell
       ^                    ^
DancingAnimals          MovingAnimal
```

Both apps import `createAnimalCell` from `AnimalCell`. Each brings its own CSS theme and UI logic. The shared component is defined once.

### Step 1: Define Typed CSS Classes

```java
public record PlaygroundStyles() implements CssGroup<PlaygroundStyles> {
    public static final PlaygroundStyles INSTANCE = new PlaygroundStyles();

    public record pg_title() implements CssClass<PlaygroundStyles> {}
    public record pg_playground() implements CssClass<PlaygroundStyles> {}
    public record pg_animal() implements CssClass<PlaygroundStyles> {}
    public record pg_platform() implements CssClass<PlaygroundStyles> {}
    public record pg_platform_active() implements CssClass<PlaygroundStyles> {}
    public record pg_lava() implements CssClass<PlaygroundStyles> {}
    public record pg_score() implements CssClass<PlaygroundStyles> {}
    // ... and more

    @Override
    public CssImportsFor<PlaygroundStyles> cssImports() {
        return CssImportsFor.none(this);
    }

    @Override
    public List<CssClass<PlaygroundStyles>> cssClasses() {
        return List.of(new pg_title(), new pg_playground(), new pg_animal(),
                       new pg_platform(), new pg_platform_active(),
                       new pg_lava(), new pg_score() /* ... */);
    }
}
```

Write `PlaygroundStyles.css` with styles for the playground container, platforms, lava, and score display. Theme variants (`PlaygroundStyles.beach.css`, `PlaygroundStyles.alpine.css`, etc.) provide different visual themes.

### Step 2: Compose the App

`MovingAnimal` imports `createAnimalCell` from the same shared `AnimalCell` module, plus all CSS class objects from `PlaygroundStyles`:

```java
public record MovingAnimal() implements AppModule<MovingAnimal> {

    record appMain() implements AppModule._AppMain<MovingAnimal> {}
    public static final MovingAnimal INSTANCE = new MovingAnimal();

    @Override public String title() { return "Moving Animal"; }

    @Override
    public ImportsFor<MovingAnimal> imports() {
        return ImportsFor.<MovingAnimal>builder()
            .add(new ModuleImports<>(
                List.of(new AnimalCell.createAnimalCell()),
                AnimalCell.INSTANCE))
            .add(new ModuleImports<>(List.of(
                    new PlaygroundStyles.pg_title(),
                    new PlaygroundStyles.pg_playground(),
                    new PlaygroundStyles.pg_animal(),
                    new PlaygroundStyles.pg_platform(),
                    new PlaygroundStyles.pg_platform_active(),
                    new PlaygroundStyles.pg_lava(),
                    new PlaygroundStyles.pg_score()
                    // ... remaining CSS classes
            ), PlaygroundStyles.INSTANCE))
            .build();
    }

    @Override
    public ExportsOf<MovingAnimal> exports() {
        return new ExportsOf<>(INSTANCE, List.of(new appMain()));
    }
}
```

### Step 3: Write the UI

In `MovingAnimal.js`, use the type-safe CSS API throughout:

```javascript
function appMain(rootElement) {
    // ... title, hint, controls ...

    const playground = document.createElement("div");
    css.setClass(playground, pg_playground);
    playground.style.height = "500px";
    rootElement.appendChild(playground);

    const animal = createAnimalCell(css.className(pg_animal));  // composability boundary
    world.appendChild(animal);

    // Platform management
    function syncPlatformDom() {
        for (var i = 0; i < platforms.length; i++) {
            var el = document.createElement("div");
            css.setClass(el, pg_platform);
            // ...
            css.toggleClass(el, pg_platform_active, p === activePlatform);
        }
    }
}
```

### Theme Switching

The MovingAnimal demo supports theme switching via URL parameters. Each theme provides:
- A CSS variant: `PlaygroundStyles.<theme>.css`
- A BGM variant: `PlatformerBgm.<theme>.js`

Available themes: light (default), dark, beach, dracula, alpine.

### Running the Demo

```bash
mvn -pl homing-demo -am compile exec:java \
  -Dexec.mainClass="hue.captains.singapura.js.homing.demo.WonderlandDemoServer"
```

Open:
- `http://localhost:8080/app?class=hue.captains.singapura.js.homing.demo.es.DancingAnimals`
- `http://localhost:8080/app?class=hue.captains.singapura.js.homing.demo.es.SpinningAnimals`
- `http://localhost:8080/app?class=hue.captains.singapura.js.homing.demo.es.MovingAnimal`
- `http://localhost:8080/app?class=hue.captains.singapura.js.homing.demo.es.MovingAnimal&theme=alpine`
- `http://localhost:8080/app?class=hue.captains.singapura.js.homing.demo.es.TurtleDemo`
- `http://localhost:8080/app?class=hue.captains.singapura.js.homing.demo.es.WonderlandDemo`
