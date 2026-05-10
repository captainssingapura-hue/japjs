# Bootstrap & Conformance

Two server-side primitives that sit at the edges of every studio: `StudioBootstrap.start(...)` to launch, and the conformance test bases to enforce the doctrines.

---

## `StudioBootstrap.start(...)` — server entrypoint

**Where**: `homing-studio-base/.../base/StudioBootstrap.java`.

A one-call bootstrap for any downstream studio. Wires the standard stack — `HomingActionRegistry` + typed-impl-backed `/css-content` + `/doc` (classpath markdown serving) + `StudioThemeRegistry` + `HomingDefault` as the default theme + `QueryParamResolver` + `SimpleAppResolver` + `VertxActionHost` + a `/` redirect to the home app — from a list of `AppModule`s and a port number.

```java
public class MyStudioServer {
    public static void main(String[] args) {
        StudioBootstrap.start(8081, List.of(
                MyHome.INSTANCE,         // first = home (/ redirects here)
                MyDocBrowser.INSTANCE,
                DocReader.INSTANCE       // shared reader from the kit
        ));
    }
}
```

That's the whole server. ~5 LoC of `main`.

**Overloads** for custom theme registry / default theme / extra GET / POST routes:

```java
StudioBootstrap.start(port, apps,
        myThemeRegistry,    // ThemeRegistry — defaults to StudioThemeRegistry
        myDefaultTheme,     // Theme         — defaults to HomingDefault
        Map.of("/my-data", new MyDataGetAction()),  // extra GET routes
        Map.of());                                  // extra POST routes
```

---

## Conformance bases

Six abstract test classes in `homing-conformance` enforce the framework's correctness contracts. Downstream subclasses each one and lists its modules (or, for the Doc base, just its entry apps). CI fails if any module slips a doctrine.

### `DoctrineConformanceTest`

Enforces the **universal view doctrines**:
- [Pure-Component Views](#ref:pcv): no HTML tag literals; no `innerHTML` / `outerHTML` writes (except `= ""`).
- [Owned References](#ref:or): no `document.getElementById`, `querySelector`, `querySelectorAll`.

The third view doctrine ([Managed DOM Ops](#ref:mdo)) is SPA-scoped and currently unenforced (no static signal). The fourth ([Methods Over Props](#ref:mop)) is structural and not statically detectable.

### `CdnFreeConformanceTest`

Every JS module's imports must resolve to local classpath resources, not to a CDN URL. Detects accidental `import … from "https://…"` slips.

### `CssConformanceTest`

DomModule JS that imports CssGroups must use the typed `css.*` API (`css.addClass`, `css.toggleClass`, `css.setClass`, …) and never the raw `el.classList.*` operations.

### `HrefConformanceTest`

DomModule JS that imports `AppLink<?>` must use the typed `href.*` API (`href.set`, `href.create`, `href.toAttr`, `href.fragment`, `href.openNew`, `href.navigate`) and never raw `.href = …` or `setAttribute("href", …)` outside the manager.

### `CssGroupImplConsistencyTest`

For every CssGroup × Theme combination, either the typed `CssGroupImpl<G, TH>` is registered, or the group's classes all carry inline `body()` overrides (the marker model from RFC 0002-ext1). Catches "we forgot to add a theme implementation" cases.

### `DocConformanceTest` (RFC 0004)

Walks the `SimpleAppResolver`'s app closure for `DocProvider` implementors and asserts:
- every contributed `Doc` has a non-null `UUID`;
- UUIDs are unique across the closure (also verified by `DocRegistry` boot);
- every `Doc.contents()` resolves to a non-empty body — catches "added the record but forgot the .md" or a stale `resourcePath()` after a refactor.

The base takes only the studio's entry apps (the same list passed to `StudioBootstrap.start`); the doc closure is derived automatically.

### Wiring — six concrete subclasses per studio

```java
class MyDoctrineConformanceTest extends DoctrineConformanceTest {
    @Override
    protected List<EsModule<?>> esModules() {
        return List.<EsModule<?>>of(
                MyHome.INSTANCE,
                MyDocBrowser.INSTANCE
                // every JS-bearing module
        );
    }
}
```

Repeat for the other five bases. Most are ~30 lines; `StudioDocConformanceTest` is ~10 lines (just hands the entry apps to the base). The `homing-studio` module's tests are the worked example.

---

## Future — auto-discovering conformance base

A planned `StudioBootstrapConformanceTest` would pull modules from the same `SimpleAppResolver` already used by `StudioBootstrap.start`, eliminating the per-test module list. Downstream would write one line per suite instead of five 30-line subclasses. Not yet implemented; subclassing each base remains the contract for now.

---

## See also

- Doctrines — the rules these tests enforce.
- [Defect 0001 §8](#ref:def-1) — how the studio reached `Set.of()` allowlist.
- Building Blocks index — every other primitive a studio composes from.
