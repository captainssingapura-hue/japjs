---
name: migrate-from-0-0-100
description: Use this skill when the user is upgrading a Homing studio from release 0.0.100 to 0.0.101. The migration is one mechanical change — `StudioBootstrap.start(...)` is deleted; downstream studios introduce a typed `Studio<L0>` record (carrying what was previously hand-listed in `*Server.main()`) and rewrite their `main()` to three lines constructing an `Umbrella`, a `DefaultFixtures`, and a `Bootstrap`. Triggers — "upgrade homing", "migrate from 0.0.100", "StudioBootstrap doesn't compile", "StudioBootstrap.start not found", "what's RFC 0012", "Studio<L0>". Skip if the studio is already on 0.0.101 or later, or if the user is migrating from 0.0.11 (use `migrate-from-0-0-11` first, then this skill).
---

# Migrate a Homing Studio from 0.0.100 to 0.0.101

0.0.101 lands [RFC 0012 — Typed Studio Composition](https://github.com/captainssingapura-hue/homing/blob/main/homing-studio/src/main/resources/docs/hue/captains/singapura/js/homing/studio/docs/rfcs/Rfc0012Doc.md). The bootstrap reshapes from a ten-parameter static funnel into a typed record stack: `Studio` / `Umbrella` / `Fixtures` / `RuntimeParams` / `Bootstrap`. Per the new [Functional Objects doctrine](https://github.com/captainssingapura-hue/homing/blob/main/homing-studio/src/main/resources/docs/hue/captains/singapura/js/homing/studio/docs/doctrines/FunctionalObjectsDoc.md), `StudioBootstrap` (the public-static funnel) is deleted; there is no back-compat shim.

Downstream studios touch **two files per studio**:

1. **A new `MyStudio.java`** — typed `Studio<L0>` record carrying home / apps / plans / themes / brand.
2. **Rewrite `MyStudioServer.main()`** — three lines: `Umbrella` + `DefaultFixtures` + `Bootstrap.start()`.

Effort: **~5 minutes mechanical work per studio**. The compiler tells you exactly what's missing.

## TL;DR — the one change

| # | Was (0.0.100) | Is (0.0.101) |
|---|---|---|
| 1 | `StudioBootstrap.start(port, apps, catalogues, plans, brand)` in `main()` with everything hand-listed | New `MyStudio<MyHome>` record + `new Bootstrap<>(new DefaultFixtures<>(new Umbrella.Solo<>(MyStudio.INSTANCE)), new DefaultRuntimeParams(port)).start();` |

That's the entire migration. Everything that was once a `start(...)` argument lives on the new `Studio` record.

---

## The mechanical recipe

### Step 1 — Create `MyStudio.java`

Pick the existing studio's package (e.g. `com.example.studio`). Add a new record file:

```java
package com.example.studio;

import hue.captains.singapura.js.homing.core.AppModule;
import hue.captains.singapura.js.homing.core.SvgRef;
import hue.captains.singapura.js.homing.studio.base.Studio;
import hue.captains.singapura.js.homing.studio.base.app.StudioBrand;
import hue.captains.singapura.js.homing.studio.base.tracker.Plan;

import java.util.List;

public record MyStudio() implements Studio<MyHomeCatalogue> {

    public static final MyStudio INSTANCE = new MyStudio();

    @Override public MyHomeCatalogue home() { return MyHomeCatalogue.INSTANCE; }

    // Override apps() ONLY if your studio shipped intrinsic AppModules
    // beyond the framework's harness apps (CatalogueAppHost, PlanAppHost,
    // DocReader, ThemesIntro — those are now layered on automatically by
    // DefaultFixtures). If your old main() listed apps beyond those four,
    // include them here:
    //
    // @Override public List<AppModule<?, ?>> apps() {
    //     return List.of(MyCustomAppModule.INSTANCE);
    // }

    // Override plans() if your old main() passed a non-empty plans list:
    //
    // @Override public List<Plan> plans() {
    //     return List.of(MyMigrationPlanData.INSTANCE);
    // }

    @Override public StudioBrand standaloneBrand() {
        // Whatever StudioBrand your old main() passed.
        return new StudioBrand(
                "My Studio",
                MyHomeCatalogue.class,
                new SvgRef<>(MyStudioLogo.INSTANCE, new MyStudioLogo.logo())
                // Drop the SvgRef arg if you had no custom logo.
        );
    }
}
```

**What goes where (mechanical port from old `main()` to `MyStudio`)**:

| Old `main()` had | Goes on `MyStudio` |
|---|---|
| `apps = List.of(CatalogueAppHost, PlanAppHost, DocReader, ThemesIntro)` | **Drop entirely** — these four are harness apps, supplied by `DefaultFixtures.harnessApps()` automatically. |
| `apps = List.of(…, MyCustomApp.INSTANCE)` | `apps() → List.of(MyCustomApp.INSTANCE)` — keep only the studio-intrinsic ones. |
| `catalogues = List.of(MyHomeCatalogue.INSTANCE, MyDoctrineCatalogue.INSTANCE, …)` | **Drop entirely** — `Studio.catalogues()` defaults to a BFS walk from `home().subCatalogues()`. If your tree was wholly reachable from the home, you list nothing. |
| `catalogues` contained orphans unreachable from home | `catalogues() → List.of(MyHomeCatalogue.INSTANCE, MyOrphan.INSTANCE)` — override only for orphans. |
| `plans = List.of(…)` | `plans() → List.of(…)` — same list verbatim. |
| `brand = new StudioBrand(…)` | `standaloneBrand() → new StudioBrand(…)` — same constructor call. |

The compiler will flag missing pieces if you miss anything.

### Step 2 — Rewrite `MyStudioServer.main()`

Replace the body with three lines:

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

Port stays whatever you had. Imports of `StudioBootstrap`, `AppModule`, `Catalogue`, `Plan`, `StudioBrand`, `CatalogueAppHost`, `PlanAppHost`, `DocReader`, `ThemesIntro` from the old `main()` go.

### Step 3 — Build, smoke test

```bash
mvn -DskipTests=false test
```

Same boot expected. Same routes (`/`, `/app?app=catalogue&id=…`, `/doc`, `/themes`, `/brand`, …). Same brand, same theme, same doc rendering. Browser-visible behaviour is identical — the migration is purely structural.

If the boot fails:

| Symptom | Likely cause |
|---|---|
| `at least one AppModule required` | Old `main()` listed an intrinsic app that didn't get moved to `MyStudio.apps()`. |
| `non-empty catalogues list requires a non-null StudioBrand` | Forgot to override `standaloneBrand()`. |
| Boot succeeds but a page 404s | A custom app was previously listed in `main()`'s `apps` list and didn't get moved to `MyStudio.apps()`. |
| Plan/doc reference resolves nowhere | A plan was previously in `main()`'s `plans` list and didn't get moved to `MyStudio.plans()`. |

---

## Multi-studio composition (new capability)

0.0.101 also opens up homogeneous multi-studio composition via `Umbrella.Group`. If you previously hand-merged multiple studios' apps / catalogues / plans into one `main()` (the way `homing-demo`'s `DemoStudioServer.main()` did in 0.0.100), you can now write:

```java
Umbrella<Studio<?>> umbrella = new Umbrella.Group<>(
        "My Multi-Studio Deploy",
        "Two studios composed onto one server.",
        List.of(
                new Umbrella.Solo<>(StudioA.INSTANCE),
                new Umbrella.Solo<>(StudioB.INSTANCE)
        ));
new Bootstrap<>(new DefaultFixtures<>(umbrella), new DefaultRuntimeParams(port)).start();
```

The Bootstrap unions every studio's catalogues / apps / plans automatically (deduplicated by class). Brand defaults to the first studio's `standaloneBrand()` — put your "umbrella" studio at index 0.

This is **optional** — single-studio deploys keep using `Umbrella.Solo<>` and look identical to before.

---

## Custom harness (advanced)

If your old code subclassed `StudioBootstrap` or passed `extraGetActions` / `extraPostActions` to its full-form overload, you'll now write a custom `Fixtures<S>` instead. Typical shape:

```java
public record MyFixtures(Umbrella<Studio<?>> umbrella) implements Fixtures<Studio<?>> {

    @Override public List<AppModule<?, ?>> harnessApps() {
        var defaults = new DefaultFixtures<>(umbrella).harnessApps();
        return Stream.concat(defaults.stream(), Stream.of(MyExtraApp.INSTANCE)).toList();
    }

    @Override public Map<String, GetAction<RoutingContext, ?, ?, ?>> harnessGetActions() {
        return Map.of("/my-endpoint", MyGetAction.INSTANCE);
    }

    @Override public NodeChrome chromeFor(Umbrella<Studio<?>> node) {
        return new DefaultFixtures<>(umbrella).chromeFor(node);  // or customise
    }
}
```

Pass `new MyFixtures(umbrella)` to `Bootstrap` instead of `new DefaultFixtures<>(umbrella)`. The four-piece-stack split (Studio / Umbrella / Fixtures / RuntimeParams) is specifically there so custom harness code lives in *one* place — no more flowing extras through `start(...)` parameters.

---

## What stayed the same

- Every `Catalogue` record's API. `L0_Catalogue`, `L<N>_Catalogue<Parent>`, `subCatalogues()`, `leaves()`, `parent()` — all unchanged. This is **not** an RFC 0005-ext2-class migration.
- Every `Doc` record's API. UUIDs, classpath `.md` paths, `references()` — all unchanged.
- Every `Plan` record's API. `decisions()`, `phases()`, `acceptance()`, `objectives()` — all unchanged.
- The `StudioBrand` record. Same three fields, same constructors.
- The `Theme` interface, `ThemeRegistry`, `SvgRef`, `SvgGroup`, `CssGroup` — all unchanged.
- The boot-time validation errors. Same registry-construction errors, same brand-not-null check, same doc-reachability check.

The migration affects **two files per studio**, no more.

---

## Doctrinal change

0.0.101 also lands the [Functional Objects doctrine](https://github.com/captainssingapura-hue/homing/blob/main/homing-studio/src/main/resources/docs/hue/captains/singapura/js/homing/studio/docs/doctrines/FunctionalObjectsDoc.md): *no public static methods anywhere in the framework, ever*. The doctrine binds future code (framework + downstream following framework conventions). Existing public statics in downstream studios are debt — the doctrine doesn't force you to sweep them, but new code should follow the pattern.

If you've been writing `MyUtil.foo(...)` static helpers in your studio code, the doctrine asks you to migrate them opportunistically to `MyUtil.INSTANCE.foo(...)` singletons as you happen to touch those files. Details in the doctrine doc.

---

## Reference reading

- [RFC 0012 — Typed Studio Composition](https://github.com/captainssingapura-hue/homing/blob/main/homing-studio/src/main/resources/docs/hue/captains/singapura/js/homing/studio/docs/rfcs/Rfc0012Doc.md) — the full design, including the role/owner split and the rejected alternatives.
- [Release 0.0.101 notes](https://github.com/captainssingapura-hue/homing/blob/main/homing-studio/src/main/resources/docs/hue/captains/singapura/js/homing/studio/docs/releases/Release0_0_101Doc.md) — what shipped, what changed, what's next.
- `homing-studio/.../StudioServer.java` — the framework's own reference migration. Three lines, post-migration.
- `homing-demo/.../DemoStudioServer.java` — multi-studio composition with `Umbrella.Group`.
- The updated `create-homing-studio` skill — the new five-file pattern for greenfield studios.
