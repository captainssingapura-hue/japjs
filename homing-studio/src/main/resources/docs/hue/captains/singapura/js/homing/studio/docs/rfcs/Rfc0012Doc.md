# RFC 0012 — Typed Studio Composition

| Field | Value |
|---|---|
| **Status** | Proposed |
| **Author** | Howard, with Homing |
| **Filed** | 2026-05-13 (rewritten 2026-05-14) |
| **Target release** | 0.0.101 |
| **Scope** | Framework — replaces the procedural `StudioBootstrap.start(...)` with a typed four-type composition primitive: `Studio<L0>` (intrinsic studio declaration), `Umbrella<S>` (ADT placing studios in a tree), `Fixtures<S>` (harness wrapping, the extensibility seam), `RuntimeParams` (deployment knobs), and a `Bootstrap<S, F>` record with a no-arg `start()`. |

---

## 1. Motivation

Two problems converge on one solution.

### 1.1 The multi-studio gap (validated by RFC 0010)

RFC 0010 shipped the launcher pattern: an umbrella catalogue composes multiple source studios onto one server. The demo server demonstrates it with three (Demo, Skills, Homing).

What `DemoStudioServer.main()` actually does is **hand-merge every studio's contributions**:

```java
List<AppModule<?, ?>> apps = List.of(
    CatalogueAppHost.INSTANCE,
    PlanAppHost.INSTANCE,
    DocReader.INSTANCE,
    DocBrowser.INSTANCE,            // ← from homing-studio
    ThemesIntro.INSTANCE
);

List<Catalogue<?>> catalogues = List.of(
    MultiStudioHome.INSTANCE,
    LearningStudioCategory.INSTANCE,
    ToolingStudioCategory.INSTANCE,
    CoreStudioCategory.INSTANCE,
    DemoStudio.INSTANCE,
    SkillsHome.INSTANCE,
    StudioCatalogue.INSTANCE,
    DoctrineCatalogue.INSTANCE,
    /* … 9 more, one per homing-studio catalogue … */
);
List<Plan> plans = List.of(/* 10 plans from homing-studio */);
```

The fact that `homing-studio` needs `DocBrowser.INSTANCE` as an AppModule, or that it ships 10 plans, is encoded **only in `main()`'s body**. It's a *convention*, not a *contract*. When a source studio's needs change, every umbrella deploying it silently breaks (URLs 404) until a human notices. No compile error. No conformance test failure. No boot validation.

The framework has typed primitives for each piece (`Catalogue`, `AppModule`, `Plan`, `Theme`, `StudioBrand`) but **no typed binding for "studio X = these specific pieces"**, and **no typed organisation for "how these studios compose into a navigable tree."** Composition works by Maven-classpath transitivity + hand-merge.

### 1.2 The StudioBootstrap parameter explosion

The current bootstrap has a ten-parameter static `start()` overload:

```java
public static void start(int port,
                         List<AppModule<?, ?>> apps,
                         List<Catalogue<?>> catalogues,
                         List<Plan> plans,
                         StudioBrand brand,
                         ThemeRegistry themeRegistry,
                         Theme defaultTheme,
                         AppModule<?, ?> rootApp,
                         Map<String, GetAction<RoutingContext, ?, ?, ?>> extraGetActions,
                         Map<String, PostAction<RoutingContext, ?, ?, ?>> extraPostActions);
```

Symptoms — long lists, order-positional argument swaps, parameter cascade through every overload when a new concept is added, scattered validation, and a doctrine violation: the bootstrap is a public static method carrying orchestration, contrary to the [Functional Objects doctrine](#ref:doc-fo).

Both problems share one root: **the framework has no typed primitive for "the bundle of things one studio brings"** *and* **no typed organisation for "how studios assemble into a deployable server."** This RFC introduces both, as a single typed shape.

## 2. Design — four types, one cascade

The composition is described by four types. Each is owned and extended by a different party.

| Type | Describes | Owned by |
|---|---|---|
| `Studio<L0>` | A studio's intrinsic bundle: catalogue tree, apps, plans, themes, standalone brand | The studio's author (one implementation per source studio) |
| `Umbrella<S>` | How studios compose into a navigable tree (leaves = studios, branches = grouping categories) | The deployer (constructed at composition time) |
| `Fixtures<S>` | The harness wrapping: extra apps and actions that surround the studios, plus visual chrome for umbrella nodes | The harness author (framework ships `DefaultFixtures`; downstream extends) |
| `RuntimeParams` | Deployment knobs: port, resource reader, env-derived defaults | The operator (chooses subtype with deployment-specific fields) |

The four types compose into a `Bootstrap<S, F>` record. The only generic cascade is `F extends Fixtures<S>` — RuntimeParams is independent because Fixtures arrives already-initialised with everything downstream provides; RuntimeParams only describes how this *process* is wired.

### 2.1 `Studio<L0>`

```java
/**
 * A studio's intrinsic declaration of what it brings to a server. Plain
 * interface — every field defaults except {@link #home()}.
 */
public interface Studio<L0 extends L0_Catalogue<L0>> {

    /** The studio's root L0 catalogue. Required — every other field defaults. */
    L0 home();

    /** Full catalogue closure. Default: BFS from {@link #home()} via subCatalogues().
     *  Override only to include orphan catalogues not reachable from the home tree. */
    default List<? extends Catalogue<?>> catalogues() {
        return CatalogueClosure.INSTANCE.walk(home());
    }

    /** AppModules this studio needs to function — custom server logic, doc
     *  browsers, anything intrinsic to the studio. Empty default. */
    default List<AppModule<?, ?>> apps()  { return List.of(); }

    /** Plans this studio ships. */
    default List<Plan> plans()            { return List.of(); }

    /** Themes this studio contributes — merged into the server's ThemeRegistry. */
    default List<Theme> themes()          { return List.of(); }

    /** Brand for standalone deploy. Ignored when composed under an umbrella —
     *  the harness's brand wins. {@code null} is valid for studios that only
     *  ever run under an umbrella. */
    default StudioBrand standaloneBrand() { return null; }
}
```

No Standard / Extended split. Raw HTTP actions are not a Studio concern — they live on Fixtures. The Studio interface stays pure-typed-primitives across the board.

### 2.2 `Umbrella<S>` — ADT for studio organisation

```java
/**
 * A tree placing studios at the leaves and pure organisational categories
 * at the branches. Pure structure — no display chrome, no behaviour
 * beyond the closure walk. Fixtures supplies the chrome.
 */
public sealed interface Umbrella<S extends Studio<?>>
        permits Umbrella.Group, Umbrella.Solo {

    record Group<S extends Studio<?>>(
            String name,
            String summary,
            List<Umbrella<S>> children) implements Umbrella<S> {}

    record Solo<S extends Studio<?>>(S studio) implements Umbrella<S> {}

    /** All studios in tree-order. Sealed-switch dispatch. */
    default List<S> studios() {
        return switch (this) {
            case Solo<S>(var s)                -> List.of(s);
            case Group<S>(_, _, var children)  ->
                    children.stream().flatMap(u -> u.studios().stream()).toList();
        };
    }
}
```

The deployer constructs the tree at composition time. A standalone studio is a `Solo`; a multi-studio deploy is a `Group` with `Solo` leaves (or further nested `Group`s for richer categorisation, mirroring today's Learning / Tooling / Core split).

### 2.3 `Fixtures<S>` — the harness extensibility seam

```java
/**
 * The harness wrapping — apps, actions, and node chrome that surround
 * the studios. This is the downstream extensibility seam: a custom
 * harness writes its own {@code Fixtures<MyStudio>} (or extends
 * {@code DefaultFixtures}) to add apps or change chrome. Bound by
 * {@code S} so harness apps can reference the studio set.
 */
public interface Fixtures<S extends Studio<?>> {

    /** The studio tree being served. */
    Umbrella<S> umbrella();

    /** Apps the harness contributes on top of each studio's own apps. */
    List<AppModule<?, ?>> harnessApps();

    /** Raw HTTP actions the harness contributes. Empty by default. */
    default List<Action> harnessActions() { return List.of(); }

    /** Visual chrome for a node in the umbrella tree — applied by the
     *  framework when rendering breadcrumbs, TOCs, and landing tiles. */
    NodeChrome chromeFor(Umbrella<S> node);

    record NodeChrome(String badge, String icon) {}
}
```

`DefaultFixtures` ships with the framework's standard harness apps (`CatalogueAppHost`, `PlanAppHost`, `DocReader`) and a sensible default `chromeFor` implementation that switches on `Solo` vs `Group`. Downstream that wants extra apps writes:

```java
public record MyFixtures(Umbrella<DefaultStudio> umbrella) implements Fixtures<DefaultStudio> {
    @Override public List<AppModule<?, ?>> harnessApps() {
        return List.of(/* defaults */, MyCustomApp.INSTANCE);
    }
    @Override public NodeChrome chromeFor(Umbrella<DefaultStudio> node) {
        return switch (node) {
            case Solo<DefaultStudio> s   -> new NodeChrome("STUDIO", s.studio().icon());
            case Group<DefaultStudio> g  -> new NodeChrome("CATEGORY", "📁");
        };
    }
}
```

### 2.4 `RuntimeParams`

```java
/**
 * Deployment knobs. Independent of {@code Fixtures} — Fixtures arrives
 * already-initialised; RuntimeParams only describes how this deployment
 * process is wired. Downstream may implement a richer interface; the
 * bootstrap only reads through this base contract.
 */
public interface RuntimeParams {
    int port();
    ResourceReader resourceReader();
}
```

A trivial `DefaultRuntimeParams(int port)` record covers most cases. Operators with env-specific fields (TLS, bind address, profiling toggles) write their own implementation.

### 2.5 `Bootstrap<S, F>` — the record

```java
/**
 * The bootstrap as a typed record. Construct with a Fixtures and a
 * RuntimeParams; call {@link #start()}. No static methods, no INSTANCE
 * field, no parameter explosion — the record IS the functional object.
 */
public record Bootstrap<S extends Studio<?>, F extends Fixtures<S>>(
        F fixtures,
        RuntimeParams params) {

    public Bootstrap {
        Objects.requireNonNull(fixtures);
        Objects.requireNonNull(params);
    }

    public void start() {
        // composition + boot logic — see §3
    }
}
```

One cascade (`F extends Fixtures<S>`). Two fields. One method. Satisfies the [Functional Objects doctrine](#ref:doc-fo) by construction — no public static methods anywhere in the new surface.

### 2.6 The `CatalogueClosure` helper

The default `Studio.catalogues()` implementation needs a closure walk. Rather than a static method, the framework ships a stateless functional object:

```java
public record CatalogueClosure() {
    public static final CatalogueClosure INSTANCE = new CatalogueClosure();

    /** BFS walk from a root catalogue, visiting every reachable sub-catalogue
     *  via {@link Catalogue#subCatalogues()}. Returns the closure with the
     *  root first. Dedup by class identity. */
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
```

Reached as `CatalogueClosure.INSTANCE.walk(home)`. Two lines of BFS, no static, doctrine-clean.

## 3. Composition logic — what `start()` does

`Bootstrap.start()` runs typed transformations on typed inputs:

1. **Studio set** — `fixtures.umbrella().studios()` is the typed list (tree-ordered).
2. **Catalogue union** — concat each studio's `catalogues()`; dedup by class.
3. **AppModule union** — concat each studio's `apps()` plus `fixtures.harnessApps()`; dedup by class; boot error on same-class-different-instances.
4. **Plan union** — concat each studio's `plans()`; dedup by class.
5. **Theme union** — concat each studio's `themes()`; dedup by `slug()`; boot error on slug collision with mismatched `label()` / `vars()`.
6. **Action set** — `fixtures.harnessActions()` only. Studios don't declare raw actions; that's a harness-level concern.
7. **Chrome resolution** — `fixtures.chromeFor(node)` called per umbrella node when rendering breadcrumbs / TOCs / landing tiles.
8. **Brand resolution** — the harness's brand wins. (Default: `fixtures` carries it via `Fixtures.brand()`, defaulted from the umbrella root studio's `standaloneBrand()`. A `Solo`'s brand is just that studio's `standaloneBrand()`.)
9. **Build typed registries** — `CatalogueRegistry`, `PlanRegistry`, `DocRegistry`, `ThemeRegistry`, `StudioProxyManager`.
10. **Bind to `params.port()` using `params.resourceReader()`** for classpath/disk reads.

Every step is typed in / typed out. The hand-merge from `DemoStudioServer.main()` becomes one record construction at the call site.

## 4. Usage cases

### 4.1 Single studio, standalone

```java
new Bootstrap<>(
        new DefaultFixtures(new Umbrella.Solo<>(HomingStudio.INSTANCE)),
        new DefaultRuntimeParams(8080)
).start();
```

### 4.2 Multi-studio composition (the demo)

```java
Umbrella<DefaultStudio> tree = new Umbrella.Group<>("Homing Demo", "Three studios, one server.", List.of(
    new Umbrella.Group<>("Learning", "Demos and tutorials.", List.of(
            new Umbrella.Solo<>(DemoBaseStudio.INSTANCE))),
    new Umbrella.Group<>("Tooling",  "Skills & utilities.",     List.of(
            new Umbrella.Solo<>(SkillsStudio.INSTANCE))),
    new Umbrella.Group<>("Core",     "Framework reference.",    List.of(
            new Umbrella.Solo<>(HomingStudio.INSTANCE)))
));

new Bootstrap<>(new DefaultFixtures(tree), new DefaultRuntimeParams(8082)).start();
```

### 4.3 Custom-harness composition

```java
new Bootstrap<>(
        new MyFixtures(new Umbrella.Solo<>(AdminStudio.INSTANCE)),  // adds extra apps + actions
        new MyRuntimeParams(9000, "0.0.0.0", tlsConfig)             // adds bind address + TLS
).start();
```

Both extension axes are independent: a custom Fixtures composes with stock RuntimeParams, and vice versa. No re-implementation of the bootstrap.

### 4.4 Heterogeneous studios

A deployer wanting to compose studios of different concrete types (`StudioA` and `StudioB`) writes them under a common upper bound — typically `Studio<?>` or a custom interface. The Umbrella's `S` parameter widens to the upper bound. No special case in the bootstrap; the type system handles it.

## 5. Doctrine alignment

### 5.1 Functional Objects

Every public method in the new surface lives on an instance of a typed object. No public static methods. `CatalogueClosure.INSTANCE` is a stateless functional object; `Bootstrap` is a dependency-holding record (its fields ARE its dependencies). The shape encodes the doctrine.

### 5.2 Catalogue-as-Container

The Umbrella ADT is the same "open set, closed shape" principle one level lifted. Where a catalogue holds entries (heterogeneous in type, organised by tree shape), an Umbrella holds studios. The catalogue-as-container doctrine gains one paragraph:

> *Studios compose into a typed tree via the `Umbrella<S>` ADT. Leaves are studios; branches are pure organisational categories. The tree is constructed by the deployer at composition time; the framework reads it as input. The same "open set of types, closed structural shape" principle applies — the Umbrella's permitted shape is fixed (Group / Solo), but the set of studios it can carry is open.*

## 6. Migration

Each existing studio gains one record exposing it as a `Studio<L0>`:

| Module | Today's `*Server.main()` | After RFC 0012 |
|---|---|---|
| `homing-studio` | 40 lines hand-listing 11 catalogues, 10 plans, 5 apps, brand | New `HomingStudio.java` (≈25 lines). `StudioServer.main()` constructs one `Bootstrap<>` and calls `start()`. |
| `homing-skills` | 15 lines | New `SkillsStudio.java`. `SkillsStudioServer.start(port)` becomes one bootstrap construction. |
| `homing-demo` | 95 lines of hand-merge | New `DemoBaseStudio.java` (the demo-studio-local studio record). `DemoStudioServer.main()` constructs the Umbrella tree + one `Bootstrap<>` + calls `start()`. |

The `Studio` records live in their owning module (co-located with the studio's catalogues) but are referenced by any composing umbrella.

The legacy `StudioBootstrap.start(...)` static overloads are **removed** — no back-compat shim. Migration is mechanical (write one `*Studio.java` record per source studio, replace the `main()` body), but it touches every entry point. Single commit per the framework's release discipline.

## 7. Decisions (locked)

1. **Four types, not two or three.** Studio / Umbrella / Fixtures / RuntimeParams each have a distinct owner; conflating any pair forces the wrong party to touch code outside their lane.
2. **`Studio<L0>` is a plain interface, not sealed.** Raw HTTP actions live on Fixtures; there's no Standard / Extended distinction at the Studio level.
3. **`Umbrella<S>` is sealed (Group / Solo).** It IS an ADT — the closed shape is the point.
4. **Catalogue closure derivation** — `CatalogueClosure.INSTANCE.walk(home)` by default; studios override only for orphan catalogues.
5. **AppModule dedup** — by class identity; boot error on same-class-different-instances.
6. **Plan dedup** — by class.
7. **Theme dedup** — by `slug()`; boot error on mismatched `label()` / `vars()` for same slug.
8. **Brand precedence** — the harness's brand wins. Default behaviour: `DefaultFixtures` derives its brand from the umbrella root (the first studio in tree-order, or a synthesised group brand for multi-studio).
9. **Actions live on Fixtures only.** Studios never declare raw HTTP paths.
10. **No `extraGetActions` / `extraPostActions` legacy parameters** — gone with the bootstrap rewrite.
11. **No public static methods anywhere in the new surface.** Doctrine-enforced.
12. **`RuntimeParams` is unparameterised.** Fixtures arrives already-initialised; RuntimeParams describes the process, not the application.

## 8. Cost — Weighed Complexity

| Dimension | Cost |
|---|---|
| **Cognitive density** | Moderate. Four types is more than two, but each names a real role with a real owner. The cascade `F extends Fixtures<S>` is one standard Java generic idiom; no CRTP gymnastics. |
| **Blast radius** | New: 6 files in `homing-studio-base` (`Studio.java`, `Umbrella.java`, `Fixtures.java`, `DefaultFixtures.java`, `RuntimeParams.java` + default record, `Bootstrap.java`, `CatalogueClosure.java`). Rewritten: `StudioBootstrap.java` (delete, fold into `Bootstrap`). Per source studio: one `*Studio.java` record. Per `*Server.main()`: collapse to one construction + `start()` call. |
| **Reversibility** | Moderate. The four-type shape, once shipped, is sticky. Adding fields to any of the interfaces is cheap (defaulted); removing is breaking. |
| **Authoring tax** | One `*Studio.java` record per source studio (~25 lines, written once). Replaces 40–95 lines of `*Server.main()` boilerplate. **Net negative authoring overhead.** Bootstrap call sites are ~3 lines (umbrella construction + Bootstrap construction + start). |
| **Failure mode** | Boot errors for unregistered apps, theme slug clashes, action path conflicts. **Zero new silent-runtime-failure modes.** |
| **Doctrine compliance** | Functional Objects: pass (no public statics). Catalogue-as-Container: extends the doctrine cleanly (Umbrella is the new container shape). Weighed Complexity: net-negative LOC in well-engineered modules, payoff in eliminated failure modes. |

Cost is favourable. The framework eliminates an entire class of silent runtime failures and removes the only remaining static-method orchestration surface in one move.

## 9. Decision

**Adopt.** This RFC fixes both motivating problems with one typed primitive — closes the multi-studio gap exposed by RFC 0010 and resolves the bootstrap's parameter explosion + doctrine violation in one stroke. The four-type composition makes every dependency's owner explicit at the type level.

## 10. Implementation order

1. **`Studio<L0>` interface** + **`CatalogueClosure` functional object** — new files in `homing-studio-base`.
2. **`Umbrella<S>` sealed ADT** with `Group` and `Solo` records — new file.
3. **`Fixtures<S>` interface** + **`DefaultFixtures` record** — new files; `DefaultFixtures` carries the framework's standard harness apps and chrome.
4. **`RuntimeParams` interface** + **`DefaultRuntimeParams(int port)` record** — new files.
5. **`Bootstrap<S, F>` record** — new file. Internal composition logic (§3) is largely lifted from the legacy `StudioBootstrap.composeAndStart(...)`.
6. **Delete legacy `StudioBootstrap.java`** — all callers migrate.
7. **`HomingStudio.java`** in `homing-studio` — captures apps, plans, brand, references `StudioCatalogue.INSTANCE`. Update `StudioServer.main()` to one bootstrap construction.
8. **`SkillsStudio.java`** in `homing-skills`. Update `SkillsStudioServer.start(port)`.
9. **`DemoBaseStudio.java`** in `homing-demo`. Update `DemoStudioServer.main()` to construct the Umbrella tree + bootstrap.
10. **Doctrine update** — one paragraph to Catalogue-as-Container.
11. **Skill update** — `create-homing-studio` skill's SKILL.md becomes the new four-type pattern.
12. **Build green across the reactor.**

Single commit (breaking change; partial migration wouldn't compile). All existing tests must pass; new `BootstrapTest` validates the composition logic.

## 11. Why this is the right time

RFC 0005-ext2 typed the catalogue tree's vertical structure. RFC 0009 typed its visual structure. RFC 0010 used both to compose multiple trees onto one server, exposing the gap this RFC closes. RFC 0011 typed the cross-tree entry-host relationship. The Functional Objects doctrine forbade the only remaining static-method orchestration surface — `StudioBootstrap`.

After this RFC, the studio bundle and its composition into a deployable server become the final typed primitives in the framework's stack. There is no more "thing the framework knows about but doesn't carry as a typed declaration." The whole composition path from `Studio<L0>` down to individual `CssClass` records is type-system-consistent, and no public static method orchestrates any of it.
