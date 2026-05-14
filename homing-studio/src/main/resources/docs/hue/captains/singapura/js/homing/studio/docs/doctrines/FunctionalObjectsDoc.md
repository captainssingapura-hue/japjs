# Doctrine — Functional Objects

> **No public static methods, anywhere. Behaviour belongs on methods of immutable typed objects. A pure function attaches to a stateless functional object — a singleton record whose `INSTANCE` field carries the same call-site ergonomics as a static method but without the structural problems static methods bring. The framework refuses public static methods absolutely; future code that introduces one is a doctrine violation regardless of how trivial the body looks.**

This is the doctrine the framework commits to from this point forward. Filed retrospectively while reshaping `StudioBootstrap` — the parameter-explosion symptom was downstream of a deeper question: *why is orchestration code public-static at all?* The doctrine names the answer so the principle generalises beyond bootstrap.

---

## What this doctrine commits to

Every method that does anything — composition, IO, pure transformation, sealed-switch dispatch, **anything** — is a method on an immutable typed object. The object is either:

- a **stateless functional object** — a record with no fields (or only `final` config fields), exposing methods that depend on nothing but their arguments. Reached via a `public static final INSTANCE` field on the type;
- a **dependency-holding object** — an immutable type with `final` references to its collaborators, set once at construction, exposing methods that close over those references.

Both shapes call-site-look like static methods at the read site (`Foo.INSTANCE.bar(...)` is a few characters longer than `Foo.bar(...)` but no more) and behave like them at the write site (no thread state, no global mutation). What they avoid is the structural baggage:

| Static-method problem | What the functional-object pattern gives instead |
|---|---|
| Cannot participate in polymorphism / sealed dispatch | The hosting type can be sealed; subtypes override |
| Cannot be injected at the constructor | The hosting object's constructor is the natural injection site |
| Cannot use class-level generic type parameters | The hosting object can be generic; methods inherit its parameters |
| Implicit ambient dependencies leak as more parameters | Dependencies live on the object, runtime inputs on the method |
| Forces parameter-explosion as the system grows | Constructor grows; method signatures stay clean |
| Awkward to mock / substitute in tests | Construct an alternate INSTANCE with different deps |

`StudioBootstrap.start(port, apps, catalogues, plans, brand, themeRegistry, defaultTheme, rootApp, extraGet, extraPost)` is the canonical example of the static-method shape failing. Every parameter is an implicit dependency that should have lived on the object.

## What this doctrine bans

- **`public static`** anything that has a body and returns a value or runs code. The keyword combination is the violation; the body's content doesn't matter.
- **Helper utility classes** with `private XxxUtil() {}` constructors + a pile of `public static` methods (`Files.*`, `Collections.*` style). The framework writes its own typed equivalents as functional objects.
- **Static factory methods on classes** that return a constructed instance (`Foo.make(...)`, `Bar.fromBytes(...)`). The hosting type's constructor is the public entry; if more shaping is needed, a dedicated `FooBuilder` object (with `INSTANCE` field) provides it.

## What this doctrine permits

- **`public static final` constants** — fields, not methods. `Cues.KICK`, `ChordPalette.CHORDS`, `Note.C4` are values, not behaviour. Untouched.
- **`public static final INSTANCE` fields** — the canonical singleton handle for a stateless functional object. Idiom: each record / final class with behaviour exposes one.
- **`private static`** helpers — internal organisation of a single class. They aren't part of the type's public surface, and the doctrine constrains the public surface.
- **`public static` permits clauses on sealed interfaces** — these are type-system declarations, not methods.
- **Direct constructor invocation with diamond inference** — `new OfDoc<>(doc)` is not a static method call, it's just construction. Type inference handles the generics.

## Where this doctrine doesn't apply

- **Existing public static methods** in the framework's source. The doctrine binds future code. Existing violations are debt; they migrate opportunistically as the code is touched for unrelated reasons. We don't sweep proactively.
- **Java platform statics** (`Math.max`, `Integer.parseInt`, `Files.readString`). Out of the framework's authoring scope; we accept the JDK's surface as-is.
- **Third-party libraries**. Same reasoning — the framework's discipline binds the framework.
- **Sealed-type pattern-match dispatch**, when expressed as a `default` method on the sealed interface that uses `switch (this)`. That's instance dispatch, not a static method.

The doctrine binds: **new framework code, new downstream-studio code following the framework's conventions, the migration of every existing public static method we happen to touch during regular work.**

---

## The canonical pattern

```java
// Stateless functional object — pure functions, no dependencies.
public record CatalogueClosure() {
    public static final CatalogueClosure INSTANCE = new CatalogueClosure();

    /** BFS walk from a root catalogue, visiting every reachable sub-catalogue. */
    public List<Catalogue<?>> walk(Catalogue<?> root) {
        List<Catalogue<?>> out = new ArrayList<>();
        Set<Class<?>> seen = new HashSet<>();
        Deque<Catalogue<?>> q = new ArrayDeque<>();
        q.add(root);
        while (!q.isEmpty()) {
            Catalogue<?> c = q.poll();
            if (!seen.add(c.getClass())) continue;
            out.add(c);
            q.addAll(c.subCatalogues());
        }
        return List.copyOf(out);
    }
}

// Call site reads the same way a static method would.
List<Catalogue<?>> all = CatalogueClosure.INSTANCE.walk(home);
```

And for dependency-holding code:

```java
// Dependency-holding object — same shape, deps held in final fields.
public final class StudioBootstrap {

    public static final StudioBootstrap INSTANCE = new StudioBootstrap(/* framework defaults */);

    private final ModuleNameResolver nameResolver;
    private final SimpleAppResolver appResolver;
    private final ThemeRegistry themeRegistry;
    private final ResourceReader resourceReader;

    public StudioBootstrap(ModuleNameResolver nameResolver,
                            SimpleAppResolver appResolver,
                            ThemeRegistry themeRegistry,
                            ResourceReader resourceReader) {
        this.nameResolver  = Objects.requireNonNull(nameResolver);
        this.appResolver   = Objects.requireNonNull(appResolver);
        this.themeRegistry = themeRegistry != null ? themeRegistry : ThemeRegistry.EMPTY;
        this.resourceReader = resourceReader != null ? resourceReader : ResourceReader.fromSystemProperty();
    }

    public <S extends Studio<?>> void start(int port, S umbrella, List<? extends S> contributors) {
        // Uses this.nameResolver, this.appResolver, etc — no parameter threading.
    }
}

// Call sites: default deps via INSTANCE, custom deps via constructor.
StudioBootstrap.INSTANCE.start(8082, umbrella, contributors);
new StudioBootstrap(custom, custom, custom, custom).start(8082, umbrella, contributors);
```

Note how the call site looks like a static-method invocation but **doesn't bring static's problems with it**.

---

## Why the strictness is worth it

- **Public static methods don't compose with the type system.** They can't be parameters in generic methods, can't be overridden in sealed switches, can't carry class-level type parameters. The framework's typed-everything spine breaks at static boundaries.
- **Parameter explosion is a downstream symptom of static-method orchestration.** `StudioBootstrap.start(port, ...10 params...)` exists because the dependencies have nowhere else to live. Moving them to the object's construction site eliminates the explosion as a class of bug.
- **The doctrine prevents the shortcut from becoming the convention.** Every team has at least one helper class that started as "just one little static method" and grew into a parameter-pyramid. By refusing the first static, the doctrine refuses the pyramid.
- **Instance dispatch is a more honest API.** A caller writes `StudioBootstrap.INSTANCE.start(...)` — the `INSTANCE` is a visible, audit-able handle. A caller writing `StudioBootstrap.start(...)` has no way to see that some other deployment might want a different bootstrap configuration. Making the handle visible makes the configuration point visible.
- **The doctrine is self-enforcing.** Once internalised, the pattern is shorter to type than the static-method version (no `static` keyword, no class-level helper file scaffolding). Authors stop reaching for static instinctively.

---

## How to think about it

Two checks before writing any method:

1. *Does it have a body that does work?* → not a constant. It belongs on an object.
2. *Does it depend on anything that varies per deployment / per test / per call context?* → dependency-holding object (deps in constructor). Otherwise → stateless functional object (singleton INSTANCE).

Then write:

```java
public final class Foo {
    public static final Foo INSTANCE = new Foo();
    private Foo() {}
    public Bar work(/* runtime args only */) { … }
}
```

Or, when records are appropriate:

```java
public record Foo() {
    public static final Foo INSTANCE = new Foo();
    public Bar work(/* runtime args only */) { … }
}
```

Both shapes are correct. Records win for value-equality semantics; classes win when the type might gain mutable invariants under construction (e.g., a validated builder).

---

## See also

- [Catalogue-as-Container](#ref:doc-cc) — the same "open set, closed shape" principle applied to data-shape. Functional Objects applies it to behaviour-shape.
- [Weighed Complexity](#ref:wc) — the doctrine that justifies *why* a slight ergonomic cost (typing `.INSTANCE`) is worth the structural benefit. Lines aren't equal; the cost of a static-method habit compounds; the cost of an `INSTANCE` field is one-time.
