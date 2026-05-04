# japjs

A Java library for declaratively defining ES6 module structure — imports, exports, and inter-module dependencies — and generating valid JavaScript ES modules from that definition. You write the module logic in `.js` files; japjs wires them together with correct `import`/`export` statements based on a dependency graph you define in Java.

## Why?

ES module graphs are easy to get wrong at scale: circular imports, missing exports, mismatched paths. japjs moves the module wiring into Java, where you get compile-time type safety, IDE refactoring support, and a single source of truth for your JS dependency graph. The actual JavaScript logic stays in plain `.js` files — japjs only generates the glue.

## How It Works

1. **Declare modules in Java** — implement `EsModule<M>` to define what each module imports and exports.
2. **Write JS logic in resource files** — plain `.js` files containing classes, functions, and constants (no `import`/`export` lines needed).
3. **Generate complete ES modules** — `EsModuleWriter` combines your declarations with the JS content to produce valid ES6 modules with correct `import { ... } from "..."` and `export { ... }` statements.

### Core Abstractions

| Type | Role |
|------|------|
| `EsModule<M>` | Declares a module's imports and exports |
| `DomModule<M>` | EsModule that interacts with the DOM |
| `AppModule<M>` | DomModule that serves as a single-page application entry point |
| `Exportable._Class<M>` / `Exportable._Constant<M>` | Type-safe markers for exported items |
| `ImportsFor<M>` | Builder-based import declaration |
| `EsModuleWriter<M>` | Orchestrates generation: imports -> content -> exports |
| `ModuleNameResolver` | Pluggable strategy for resolving import paths |
| `ContentProvider<M>` | Pluggable strategy for loading JS content |
| `SvgGroup<G>` / `SvgBeing<G>` | SVG asset bundles as ES modules |
| `CssGroup<C>` / `CssClass<C>` | Type-safe CSS resource modules with class bindings |
| `ExternalModule<M>` | Third-party JS library wrappers (CDN or global) |

## Quick Start

### 1. Define a module

```java
public record Alice() implements EsModule<Alice> {

    record Alice1() implements Exportable._Constant<Alice> {}
    record Alice2() implements Exportable._Constant<Alice> {}
    record AliceClass() implements Exportable._Class<Alice> {}

    public static final Alice INSTANCE = new Alice();

    @Override
    public ImportsFor<Alice> imports() {
        return ImportsFor.<Alice>builder().build(); // no imports
    }

    @Override
    public ExportsOf<Alice> exports() {
        return new ExportsOf<>(new Alice(), List.of(new Alice1(), new Alice2(), new AliceClass()));
    }
}
```

Pair it with `Alice.js` on the classpath:

```javascript
class AliceClass {
    constructor(index) {
        this.i = index;
    }
    name() {
        return "Alice No." + this.i;
    }
}

const Alice1 = new AliceClass(1);
const Alice2 = new AliceClass(2);
```

### 2. Define a dependent module

```java
public record BobModule() implements DomModule<BobModule> {

    public static final BobModule INSTANCE = new BobModule();

    public record Bob() implements Exportable._Class<BobModule> {}

    @Override
    public ImportsFor<BobModule> imports() {
        return ImportsFor.<BobModule>builder()
                .add(new ModuleImports<>(List.of(new Alice.Alice1(), new Alice.Alice2()), Alice.INSTANCE))
                .build();
    }

    @Override
    public ExportsOf<BobModule> exports() {
        return new ExportsOf<>(BobModule.INSTANCE, List.of(new Bob()));
    }
}
```

### 3. Generate the modules

```java
Path rootPath = Path.of(args[0]);
ModuleNameResolver nameResolver = new SimplePrefixResolver("/test/");

for (var m : new EsModule<?>[]{ BobModule.INSTANCE, Alice.INSTANCE }) {
    var writer = new EsModuleWriter(m,
            new ReadContentFromResources(m),
            nameResolver,
            ExportWriter.INSTANCE,
            new SimpleImportsWriterResolver(nameResolver));

    var outputFile = rootPath.resolve(nameResolver.resolve(m) + ".js");
    Files.createDirectories(outputFile.getParent());
    Files.write(outputFile, writer.writeModule());
}
```

### Generated output

**BobModule.js:**
```javascript
import { Alice1, Alice2 } from /test/.../Alice.js;

class Bob {
    test1() { return Alice1.name(); }
    test2() { return Alice2.name(); }
}

export { Bob };
```

**Alice.js:**
```javascript
class AliceClass {
    constructor(index) { this.i = index; }
    name() { return "Alice No." + this.i; }
}

const Alice1 = new AliceClass(1);
const Alice2 = new AliceClass(2);

export { Alice1, Alice2, AliceClass };
```

## Type-Safe CSS

japjs provides a type-safe CSS class management system that eliminates raw string-based DOM class operations. CSS resources are declared as `CssGroup` modules that export typed `CssClass` objects.

### 1. Declare a CSS module with typed classes

```java
public record ButtonStyles() implements CssGroup<ButtonStyles> {
    public static final ButtonStyles INSTANCE = new ButtonStyles();

    // Each record maps to a CSS class name (snake_case -> kebab-case)
    public record btn() implements CssClass<ButtonStyles> {}
    public record btn_primary() implements CssClass<ButtonStyles> {}
    public record btn_disabled() implements CssClass<ButtonStyles> {}

    @Override
    public CssImportsFor<ButtonStyles> cssImports() {
        return CssImportsFor.none(this);
    }

    @Override
    public List<CssClass<ButtonStyles>> cssClasses() {
        return List.of(new btn(), new btn_primary(), new btn_disabled());
    }
}
```

### 2. Import CSS classes in your DomModule

```java
public record MyWidget() implements DomModule<MyWidget> {
    // ...
    @Override
    public ImportsFor<MyWidget> imports() {
        return ImportsFor.<MyWidget>builder()
            .add(new ModuleImports<>(List.of(
                    new ButtonStyles.btn(),
                    new ButtonStyles.btn_primary()
            ), ButtonStyles.INSTANCE))
            .build();
    }
}
```

### 3. Use the type-safe API in JavaScript

The server auto-injects a `css` manager into any DomModule with CSS imports. In your JS file, use `css.*` methods instead of raw `.className` or `.classList` operations:

```javascript
function appMain(rootElement) {
    const button = document.createElement("button");
    css.setClass(button, btn);            // sets className to "btn"
    css.addClass(button, btn_primary);    // adds "btn-primary"
    css.removeClass(button, btn_primary); // removes "btn-primary"
    css.toggleClass(button, btn_disabled, isDisabled); // conditional toggle
    css.hasClass(button, btn_primary);    // returns boolean

    // For passing to shared components that accept string class names:
    const className = css.className(btn); // returns "btn"
}
```

The `CssClassManager` also handles CSS file loading automatically — `CssGroup` modules are generated as header-only ES modules that load their CSS and export frozen class objects.

### Naming Convention

Record names use `snake_case`, which maps 1:1 to `kebab-case` CSS class names:

| Java Record | CSS Class |
|-------------|-----------|
| `btn_primary` | `btn-primary` |
| `pg_theme_switcher` | `pg-theme-switcher` |
| `spin_cell` | `spin-cell` |

### Theme Support

CSS files support theme variants. Place theme-specific CSS alongside the default:

```
japjs/css/.../PlaygroundStyles.css           # default
japjs/css/.../PlaygroundStyles.beach.css     # beach theme
japjs/css/.../PlaygroundStyles.alpine.css    # alpine theme
```

Theme is selected via the `?theme=` query parameter and propagates through import URLs automatically.

## Typed Navigation

japjs provides type-safe navigation between AppModules — and to typed external destinations — through the same Java-records-as-source-of-truth pattern that drives modules, CSS, and assets. Introduced in [RFC 0001](docs/rfcs/0001-app-registry-and-typed-nav.md).

### 1. Make an AppModule linkable

Each AppModule that other modules want to navigate to declares an inner `link()` record:

```java
public record PitchDeck() implements AppModule<PitchDeck> {
    public static final PitchDeck INSTANCE = new PitchDeck();

    record appMain() implements AppModule._AppMain<PitchDeck> {}
    public record link() implements AppLink<PitchDeck> {}
    // ... title, imports, exports
}
```

Optionally, declare typed query parameters via a `Params` record:

```java
public record ProductDetail() implements AppModule<ProductDetail> {
    public record Params(String productId, Optional<String> tab) {}
    public record link() implements AppLink<ProductDetail> {}

    @Override public Class<?> paramsType() { return Params.class; }
    // ...
}
```

### 2. Import the link in any consumer

```java
@Override
public ImportsFor<MyApp> imports() {
    return ImportsFor.<MyApp>builder()
        .add(new ModuleImports<>(List.of(new PitchDeck.link()),     PitchDeck.INSTANCE))
        .add(new ModuleImports<>(List.of(new ProductDetail.link()), ProductDetail.INSTANCE))
        // ... other imports
        .build();
}
```

### 3. Use `nav` and `href` in JS

The writer auto-generates a `nav` object and auto-injects an `href` manager into any DomModule that imports an `AppLink<?>`:

```js
// nav.X(...) returns the URL string for X.
// href.X(...) is the only sanctioned way to apply that URL.
parent.innerHTML = '<a ' + href.toAttr(nav.PitchDeck()) + '>Open the deck</a>';

// With params:
parent.innerHTML = '<a ' + href.toAttr(nav.ProductDetail({productId: "P-12345", tab: "reviews"})) + '>...</a>';

// Other href methods:
href.set(elem, nav.PitchDeck());                    // set el.href
const a = href.create(nav.PitchDeck(), {text:"Go"}); // returns <a> element
href.openNew(nav.PitchDeck());                       // window.open in new tab
href.navigate(nav.PitchDeck({slide: 7}));            // programmatic navigation
href.fragment("section-2");                          // 'href="#section-2"' for same-page anchors
```

### 4. Receive typed params on the receiving side

For any AppModule with a non-Void `paramsType()`, the writer generates a `params` const at the top of its compiled JS, populated from the URL with type coercion:

```js
function appMain(rootElement) {
    if (params.productId) {
        loadProduct(params.productId, params.tab);   // typed values, no URLSearchParams boilerplate
    } else {
        showProductPicker();
    }
}
```

### 5. External URLs via proxy apps

Non-`/app` destinations (GitHub, vendor APIs, `mailto:`) are modeled as `ProxyApp` declarations with a URL template:

```java
public record GitHubProxy() implements ProxyApp<GitHubProxy> {
    public static final GitHubProxy INSTANCE = new GitHubProxy();
    public record Params(String repo, Optional<String> path) {}
    public record link() implements AppLink<GitHubProxy> {}

    @Override public String simpleName()  { return "github"; }
    @Override public Class<?> paramsType() { return Params.class; }
    @Override public String urlTemplate() { return "https://github.com/{repo}/{path?}"; }
}
```

From JS: `href.toAttr(nav.GitHubProxy({repo: "acme/proj", path: Optional.of("README.md")}))`.

Built-in proxies for common non-HTTP schemes (`Mailto`, `Tel`, `Sms`) ship in `japjs-core.proxies`.

### 6. Server bootstrap — single entry point

Pass one (or a few) entry apps to `SimpleAppResolver`; the resolver walks `AppLink<?>` import edges to discover every reachable AppModule and ProxyApp transitively. Adding a new linked app requires no resolver update — just import its `link()`.

```java
var resolver = new SimpleAppResolver(List.of(MyCatalogue.INSTANCE));
var registry = new JapjsActionRegistry(new QueryParamResolver(), resolver);
```

### 7. Conformance enforcement

A new `HrefConformanceTest` (sibling to `CssConformanceTest`) enforces that the literal substring `href` may appear in DomModule JS **only** as the manager identifier (`href.toAttr(...)`, `href.set(...)`, etc.). Raw `href=` attributes, `el.href = ...` assignments, `window.location.*`, `window.open(...)` are all rejected by the scanner. See [RFC 0001 §6.2](docs/rfcs/0001-app-registry-and-typed-nav.md).

```java
class MyHrefConformanceTest extends HrefConformanceTest {
    @Override protected List<DomModule<?>> domModules() {
        return List.of(/* every DomModule in your project */);
    }
}
```

## SVG Groups

An `SvgGroup` bundles SVG assets into a single ES module. Each SVG file becomes a template literal constant.

```java
public record Icons() implements SvgGroup<Icons> {

    record search() implements SvgBeing<Icons> {}
    record menu() implements SvgBeing<Icons> {}

    public static final Icons INSTANCE = new Icons();

    @Override
    public List<SvgBeing<Icons>> svgBeings() {
        return List.of(new search(), new menu());
    }

    @Override
    public ExportsOf<Icons> exports() {
        return new ExportsOf<>(this, List.copyOf(svgBeings()));
    }
}
```

Place SVG files at `japjs/svg/<canonical-path>/Icons/search.svg`, etc. Other modules import SVGs like any other export.

## External Modules

`ExternalModule` wraps third-party JS libraries (CDN or global script):

```java
public record ToneJs() implements ExternalModule<ToneJs> {
    public static final ToneJs INSTANCE = new ToneJs();

    public record Synth() implements Exportable._Class<ToneJs> {}
    public record start() implements Exportable._Constant<ToneJs> {}
    // ...
}
```

The JS resource file handles the actual loading strategy (CDN import, global variable, etc.).

## Server-Side Hosting

japjs includes a server module (`japjs-server`) that serves ES modules and SPA applications over HTTP, using `VertxActionHost` from `ja-http`.

### Endpoints

| Path | Response | Description |
|------|----------|-------------|
| `/app?app=<simple-name>` | `text/html` | Full HTML page that boots the app (RFC 0001) |
| `/app?class=<AppModule>` | `text/html` | Legacy fallback — accepts canonical class name |
| `/module?class=<EsModule>` | `application/javascript` | Generated ES module with imports/exports |
| `/css?class=<CssGroup>` | `application/json` | Resolved CSS dependency chain |
| `/css-content?class=<CssGroup>` | `text/css` | Raw CSS file content |

Query parameters `theme` and `locale` are supported on all endpoints and propagate through import URLs for DOM-aware modules.

The `?app=<simple-name>` URL contract — introduced in [RFC 0001](docs/rfcs/0001-app-registry-and-typed-nav.md) — is the public surface. Simple names default to a kebab-case derivation of the AppModule's class name (e.g. `PitchDeck` → `pitch-deck`); each AppModule may override `simpleName()` to lock the URL contract independently of its Java class. The legacy `?class=` form is retained for backwards compatibility.

### Running

```java
// RFC 0001 Step 11: single entry app — the resolver discovers everything
// the catalogue links to (and what those link to, transitively).
var resolver = new SimpleAppResolver(List.of(MyCatalogue.INSTANCE));
var registry = new JapjsActionRegistry(new QueryParamResolver(), resolver);
var host = new VertxActionHost(registry, 8080);
host.start();
```

```bash
mvn -pl japjs-demo -am compile exec:java \
  -Dexec.mainClass="hue.captains.singapura.japjs.demo.WonderlandDemoServer"
```

### Live Reload

Set `japjs.devRoot` to read resource files from the filesystem instead of the classpath:

```bash
mvn compile exec:java \
  -Dexec.mainClass="com.example.DevServer" \
  -Djapjs.devRoot=src/main/resources
```

Edit JS/CSS/SVG files and refresh — no restart needed. Java declaration changes still require a recompile.

## Demo Applications

The `japjs-demo` module includes several interactive demos:

| App | URL | Description |
|-----|-----|-------------|
| DemoCatalogue | `/app?app=demo-catalogue` | Launcher — start here. Lists every demo. |
| PitchDeck | `/app?app=pitch-deck` | 13-slide interactive executive deck with BGM |
| WonderlandDemo | `/app?app=wonderland-demo` | Simple intro — imports from Alice and SVG groups |
| DancingAnimals | `/app?app=dancing-animals` | 5x5 grid of animals, keyboard-controlled direction flipping |
| SpinningAnimals | `/app?app=spinning-animals` | Grid animation with pause/resume controls |
| MovingAnimal | `/app?app=moving-animal` | Platformer game with physics, sound, and theme switching |
| TurtleDemo | `/app?app=turtle-demo` | 3D turtle visualization with Three.js |

### Themes

The MovingAnimal platformer supports theme switching via URL parameter:

| Theme | Description |
|-------|-------------|
| `light` | Default light theme |
| `dark` | Dark mode |
| `beach` | Tropical sand and ocean |
| `dracula` | Dracula's castle, dark and eerie |
| `alpine` | Alpine mountain with forest and snow peaks |

Each theme has its own CSS (`PlaygroundStyles.<theme>.css`) and BGM (`PlatformerBgm.<theme>.js`).

## Conformance Testing

The `japjs-conformance` module provides a base class for verifying that DomModule JS files use the type-safe `css.*` API instead of raw CSS class operations.

```java
class MyCssConformanceTest extends CssConformanceTest {

    @Override
    protected List<DomModule<?>> domModules() {
        return List.of(MyWidget.INSTANCE, MyApp.INSTANCE);
    }

    @Override
    protected Set<Class<? extends DomModule<?>>> allowList() {
        return Set.of(/* modules that intentionally use raw CSS */);
    }
}
```

The `@TestFactory` generates a dynamic test per DomModule with CSS imports, scanning for `.className =`, `.classList.add/remove/toggle/replace/contains` patterns in the JS resource files.

## Project Structure

```
japjs/
  japjs-core/          Core library — module interfaces, writers, resolvers
  japjs-server/        Server module — HTTP hosting, CssClassManager, content providers
  japjs-conformance/   Test framework — CSS conformance base class
  japjs-demo/          Demo apps — WonderlandDemo, DancingAnimals, MovingAnimal, etc.
```

### External Dependencies

| Module | From `ja-http` | Purpose |
|--------|----------------|---------|
| `japjs-server` | `vertx-host` | HTTP action hosting via Vert.x Router |

## Requirements

- Java 21+
- Maven
- `ja-http` artifacts installed locally (`mvn install -DskipTests` in the ja-http project)
