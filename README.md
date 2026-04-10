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
| `Exportable._Class<M>` / `Exportable._Constant<M>` | Type-safe markers for exported items |
| `ImportsFor<M>` | Builder-based import declaration |
| `EsModuleWriter<M>` | Orchestrates generation: imports → content → exports |
| `ModuleNameResolver` | Pluggable strategy for resolving import paths |
| `ContentProvider<M>` | Pluggable strategy for loading JS content |

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
public record BobModule() implements EsModule<BobModule> {

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

## Real-World Scenarios

### Server-Side JS Generation

A Java web application that serves dynamically generated ES modules to the browser. Your backend defines the module graph, resolves import paths based on the deployment environment (CDN prefix, versioned paths, etc.), and writes modules on-the-fly or at startup. The `ModuleNameResolver` is the key extension point — implement it to produce paths like `https://cdn.example.com/v2/modules/Alice.js` or `./assets/Alice.abc123.js`.

```java
// Resolve import paths to your CDN
ModuleNameResolver cdnResolver = module ->
    "https://cdn.example.com/v2/" + module.getClass().getSimpleName();
```

### Build-Time Code Generation

Integrate japjs into a Maven or Gradle build step to generate a complete ES module bundle from Java-declared dependency graphs. Your Java source is the single source of truth for which modules exist, what they export, and how they depend on each other. A build plugin iterates over all `EsModule` implementations, generates the `.js` files, and places them in the output directory alongside your compiled classes or in a static resources folder.

This is particularly useful when your JS modules are consumed by a downstream bundler (Vite, esbuild, Rollup) — japjs generates the individual modules with correct import/export wiring, and the bundler handles optimization.

### Polyglot Monorepos

In projects where a Java backend and browser-side JavaScript coexist, japjs keeps the module wiring in Java alongside the rest of your application code. Instead of maintaining a separate `package.json` dependency graph, your Java code declares the module relationships. This is valuable when:

- Your JS modules are tightly coupled to backend logic (e.g., generated API clients, shared constants)
- You want refactoring tools and compile-time checks for your module graph
- Multiple backend services each need to produce slightly different JS module bundles

### Dynamic Module Assembly

Build different JS module bundles based on runtime conditions. Since module declarations are plain Java objects, you can conditionally include or exclude exports, swap implementations, or alter the dependency graph based on configuration, feature flags, or user context.

```java
// Conditionally include exports based on feature flags
@Override
public ExportsOf<Dashboard> exports() {
    var exports = new ArrayList<Exportable<Dashboard>>();
    exports.add(new CoreWidget());
    if (featureFlags.isEnabled("beta-charts")) {
        exports.add(new BetaChartWidget());
    }
    return new ExportsOf<>(this, exports);
}
```

### Templated JS with Type-Safe Wiring

Use japjs as a type-safe templating layer. The Java side enforces that if module B imports `Alice1` from module A, then module A actually exports `Alice1` — this is checked at compile time via generics. The JS files are templates that assume their imports exist; japjs guarantees the wiring is correct. This catches broken imports before any JavaScript ever runs, which is especially valuable in large codebases or when multiple teams contribute modules.

## Server-Side Hosting

japjs includes a server module (`japjs-server`) that serves ES modules and SPA applications over HTTP, using `VertxActionHost` from `ja-http`.

### JapjsActionRegistry

An `ActionRegistry<RoutingContext>` that dynamically serves modules and apps:

| Path | Description |
|------|-------------|
| `/module?class=<canonical.class.name>` | Serves a single ES module (generated JS with imports/exports) |
| `/app?class=<AppModule.class.name>` | Serves a full SPA — HTML scaffold + inline ES module with `appMain(rootElement)` entry point |

### AppModule

A specialisation of `EsModule` for single-page applications:

```java
public record WonderlandDemo() implements AppModule<WonderlandDemo> {
    record appMain() implements AppModule._AppMain<WonderlandDemo> {}

    public static final WonderlandDemo INSTANCE = new WonderlandDemo();

    @Override public String title() { return "Wonderland Demo"; }
    @Override public ImportsFor<WonderlandDemo> imports() {
        return ImportsFor.<WonderlandDemo>builder()
                .add(new ModuleImports<>(List.of(new BobModule.Bob()), BobModule.INSTANCE))
                .build();
    }
    @Override public ExportsOf<WonderlandDemo> exports() {
        return new ExportsOf<>(INSTANCE, List.of(new appMain()));
    }
}
```

The corresponding `WonderlandDemo.js` sits on the classpath and contains the `appMain(rootElement)` function. The server generates the HTML page, inlines the module, and calls `appMain` with a root DOM element.

### Hosting with VertxActionHost

The demo server uses `VertxActionHost` from `ja-http` to serve japjs modules over HTTP:

```java
var registry = new JapjsActionRegistry(new QueryParamResolver());
var host = new VertxActionHost(registry, 8080);
host.start();
```

Run with:
```bash
mvn -pl japjs-demo exec:java \
  -Dexec.mainClass="hue.captains.singapura.japjs.demo.WonderlandDemoServer"
```

Then open:
- `http://localhost:8080/app?class=hue.captains.singapura.japjs.demo.es.WonderlandDemo`
- `http://localhost:8080/app?class=hue.captains.singapura.japjs.demo.es.DancingAnimals`
- `http://localhost:8080/app?class=hue.captains.singapura.japjs.demo.es.MovingAnimal`

## Project Structure

```
japjs/
  japjs-core/     Core library — module interfaces, writers, resolvers
  japjs-server/   Server module — JapjsActionRegistry, QueryParamResolver, AppModule hosting
  japjs-demo/     Demo apps — WonderlandDemo, DancingAnimals, MovingAnimal
```

### External Dependencies

| Module | From `ja-http` | Purpose |
|--------|----------------|---------|
| `japjs-server` | `vertx-host` | HTTP action hosting via Vert.x Router |

## Requirements

- Java 21+
- Maven
- `ja-http` artifacts installed locally (`mvn install -DskipTests` in the ja-http project)
