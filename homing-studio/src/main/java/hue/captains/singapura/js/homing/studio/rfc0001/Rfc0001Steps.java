package hue.captains.singapura.js.homing.studio.rfc0001;

import java.util.List;

/**
 * Implementation tracker for RFC 0001 — App Registry &amp; Typed Navigation.
 *
 * <p><b>How to record progress:</b> edit the {@link #STEPS} list below.
 * Flip a {@link Task#done()} from false → true; change a {@link Status}; add a
 * note to the discussion log. Recompile, refresh the studio, the new state
 * appears in the plan and step views. Git history is the change log.</p>
 *
 * <p>This file is the single source of truth. {@code Rfc0001PlanData} adapts
 * it to the shared {@link hue.captains.singapura.js.homing.studio.base.tracker.Plan}
 * shape; {@code Rfc0001Plan} / {@code Rfc0001Step} embed the serialised data
 * in their auto-generated JS via the tracker kit (no separate data endpoint).</p>
 */
public final class Rfc0001Steps {

    public enum Status {
        NOT_STARTED("Not started", "not-started"),
        IN_PROGRESS("In progress", "in-progress"),
        BLOCKED("Blocked", "blocked"),
        DONE("Done", "done");

        public final String label;
        public final String slug;
        Status(String label, String slug) { this.label = label; this.slug = slug; }
    }

    public record Task(String description, boolean done) {}

    public record Dependency(String stepId, String reason) {}

    public record Step(
            String id,                  // "01", "02", ... — used in the URL
            String label,               // "Core Linkable Types"
            String summary,             // one-line headline
            String description,         // multi-paragraph context
            Status status,
            List<Task> tasks,
            List<Dependency> dependsOn,
            String acceptance,          // acceptance criteria
            String effort,              // estimated effort
            String rfcSection,          // pointer back to the RFC
            String notes                // free-form discussion
    ) {
        public int progressPercent() {
            if (tasks.isEmpty()) return 0;
            long done = tasks.stream().filter(Task::done).count();
            return (int) (done * 100 / tasks.size());
        }
    }

    public static final String RFC_ID    = "0001";
    public static final String RFC_TITLE = "RFC 0001 — App Registry & Typed Navigation";
    public static final String RFC_PATH  = "rfcs/0001-app-registry-and-typed-nav.md";

    public static final List<Step> STEPS = List.of(

            new Step("01",
                    "Core Linkable Types",
                    "The sealed Linkable supertype, the AppLink<L> primitive, and the kebab-case helper.",
                    "Foundational kernel types that everything else depends on. `Linkable` unifies AppModule and ProxyApp under a common navigation supertype. `AppLink<L>` is the new Exportable flavor that other modules import to declare a navigation edge. `defaultSimpleName(Class)` provides the kebab-case derivation used by every Linkable's default `simpleName()`.",
                    Status.DONE,
                    List.of(
                            new Task("Add `Linkable` sealed interface (permits AppModule for now; expands to also permit ProxyApp in Step 03)", true),
                            new Task("Add `AppLink<L extends Linkable>` interface extending Exportable (NOT sealed — RFC's `permits AppLink` self-reference proved infeasible; see file javadoc)", true),
                            new Task("Loosen `Exportable<M extends EsModule>` bound to `Exportable<M>` (per §9.10 option (a))", true),
                            new Task("Add `Linkable.defaultSimpleName(Class<?>)` static helper with acronym handling", true),
                            new Task("Wire `AppModule extends Linkable` with default `simpleName()` and `paramsType()` (Step 02 work bundled — required for backwards compatibility)", true),
                            new Task("10 unit tests in `LinkableTest`: simple split, short names, acronym boundary, trailing acronym, all-upper, digits, AppModule defaults, override, AppLink shape", true)
                    ),
                    List.of(),
                    "All four types compile. `defaultSimpleName(PitchDeck.class)` returns `\"pitch-deck\"`; `defaultSimpleName(HTTPHandler.class)` returns `\"http-handler\"`. All five modules build green; existing demo and studio AppModules continue to work without source changes.",
                    "3 hours (actual: ~2h)",
                    "§3.1, §3.2",
                    "**Landed 2026-05-03.** Two RFC-spec deviations discovered during implementation:\n\n1. **AppLink cannot be `sealed permits AppLink`** — that's a self-referential seal that doesn't compile. Each Linkable's inner `record link()` is a distinct implementation; the set is open. AppLink is therefore a regular marker interface; discipline is enforced by the writer reading import metadata (Step 05) and by the conformance scanner (Step 10).\n\n2. **Step 02 work bundled into Step 01.** When `AppModule` was declared to implement the sealed `Linkable`, it needed to provide implementations of `simpleName()` and `paramsType()` — otherwise every existing AppModule would break. Providing the defaults *is* what Step 02 specified, so we bundled it. Step 02's task list was completed alongside; the original separation made sense in the RFC but not in implementation order."
            ),

            new Step("02",
                    "AppModule Extensions",
                    "Default `simpleName()` and `paramsType()` methods on the AppModule interface.",
                    "Existing AppModules opt into typed navigation by overriding these defaults. Defaults are sensible: `simpleName()` derives from class name, `paramsType()` returns Void.class. No existing AppModule needs to change.",
                    Status.DONE,
                    List.of(
                            new Task("Add default `simpleName()` to AppModule", true),
                            new Task("Add default `paramsType()` to AppModule", true),
                            new Task("Document the `Params` record convention in javadoc", true),
                            new Task("Verify all existing AppModules still compile (full `mvn install` green)", true)
                    ),
                    List.of(new Dependency("01", "Linkable interface must exist before AppModule extends it")),
                    "All existing AppModules compile unchanged. New override surface visible in IDE autocomplete.",
                    "1 hour (bundled with Step 01)",
                    "§3.1",
                    "**Landed 2026-05-03 alongside Step 01.** The `permits AppModule` clause on `Linkable` requires AppModule to implement Linkable, which in turn requires `simpleName()` and `paramsType()` to be present. Since the only acceptable behaviour for existing AppModules is for these methods to default sensibly, the defaults landed in the same change as Step 01. The two steps remain logically distinct in the RFC; they're just physically inseparable in the kernel. The `Params` record convention is documented in `AppModule.paramsType()` javadoc; no existing AppModule declares a Params record yet (Step 06 will exercise this surface for the first time)."
            ),

            new Step("03",
                    "ProxyApp + URL Template DSL",
                    "ProxyApp interface, urlTemplate() DSL specification, and the template parser.",
                    "ProxyApp is the typed-Java declaration for an external destination — GitHub repos, vendor APIs, anything not served by the kernel. The URL template DSL (`{name}`, `{name?}`, `{name?:default}`) lets developers declare external URLs without raw string handling. The parser validates templates against the Params record at compile time.",
                    Status.DONE,
                    List.of(
                            new Task("Define `ProxyApp<P extends ProxyApp<P>>` interface (non-sealed; default simpleName via Linkable.defaultSimpleName)", true),
                            new Task("Expand `Linkable permits` clause to also permit ProxyApp", true),
                            new Task("Spec the URL template DSL syntax (§3.8) in javadoc on UrlTemplate and ProxyApp.urlTemplate()", true),
                            new Task("Implement the template parser (`UrlTemplate.compile`) — tokenizes braces, parses name/optional/default", true),
                            new Task("Validate template params match the Params record at compile time (uses RecordComponent reflection)", true),
                            new Task("Reject required `{name}` for Optional components AND optional `{name?}` for required components — strict pairing", true),
                            new Task("Reject malformed templates (unclosed brace, stray brace, empty slot, bad optional syntax, non-record paramsType, slot-without-Params)", true),
                            new Task("Implement `UrlTemplate.render(Object)` with URL-encoding via URLEncoder.encode", true),
                            new Task("20 unit tests in `UrlTemplateTest`: literals, required/optional/default, two-required, paramNames, all 8 error paths, ProxyApp end-to-end, default simpleName", true)
                    ),
                    List.of(new Dependency("01", "Linkable supertype")),
                    "Templates `https://github.com/{repo}/{path?}` parse correctly. Invalid templates fail parse with clear error messages. Compile-time mismatch between template and Params is caught.",
                    "5 hours (actual: ~3h)",
                    "§3.7, §3.8",
                    "**Landed 2026-05-03.** No RFC deviations. The strict pairing rule (required slot ↔ required component, optional slot ↔ Optional component) is enforced both ways — this caught one minor RFC under-specification: §3.8 said \"optional must be Optional\" but didn't say \"required must NOT be Optional.\" The latter case (`{name}` on `Optional<String> path`) would silently render the toString of the Optional wrapper, which is useless. The implementation rejects it; the test `optionalSlotOnRequiredComponent` and its sibling cover both directions.\n\nURLEncoder uses `application/x-www-form-urlencoded` form, which encodes `/` as `%2F` and space as `+`. This is the safe default for inserted values; for path segments containing slashes that should remain unencoded, the documented pattern is to declare separate Params components per segment (e.g., `Params(String org, String repo)` with template `https://github.com/{org}/{repo}`). The `TwoRequired` test demonstrates this.\n\nThe `Segment` sealed interface inside `UrlTemplate` is a textbook use of Java 21 sealed records + pattern matching — clean two-case dispatch in `render()`."
            ),

            new Step("04",
                    "SimpleAppResolver — Transitive Walk",
                    "The boot-time registry that walks AppLink edges from entry apps to discover all reachable Linkables.",
                    "Implements Amendment 1's transitive registration. The user enumerates entry apps; the resolver walks `AppLink<?>` import edges (only those, not all imports) to find every reachable AppModule and ProxyApp. Cycle-tolerant via visited set. Collisions in the closure fail fast at boot.",
                    Status.DONE,
                    List.of(
                            new Task("Type-system foundation: introduce `Importable` sealed supertype permitting EsModule + Linkable", true),
                            new Task("Loosen `ModuleImports<M extends EsModule>` → `ModuleImports<M extends Importable>`", true),
                            new Task("Loosen `ImportsFor` map key from `EsModule` → `Importable`", true),
                            new Task("Update `EsModuleWriter` to skip non-EsModule keys (proxies have no JS imports — Step 05 will emit nav entries)", true),
                            new Task("Define `SimpleAppResolver` class with two maps (apps + proxies, indexed by both name and class)", true),
                            new Task("Implement the `collect()` walker following only AppLink edges (CSS/SVG/_Constant imports do NOT trigger registration)", true),
                            new Task("Cycle detection via visited set (HashMap putIfAbsent)", true),
                            new Task("Simple-name collision detection within each kind AND across both kinds", true),
                            new Task("`resolve(name)`, `resolveApp(name)`, `resolveProxy(name)`, `resolveByClass(cls)`, `apps()`, `proxies()`, `all()` lookups", true),
                            new Task("15 unit tests: standalone, linear, chain, diamond, cycle, orphan, css-only-not-followed, name collision (apps), proxies via entry, resolve cross-kind, kind filtering, resolveByClass, cross-kind collision, all() combined, multiple entries", true)
                    ),
                    List.of(new Dependency("01", "AppLink and Linkable must exist"),
                            new Dependency("03", "ProxyApp must exist to be walked")),
                    "Given DemoCatalogue importing PitchDeck, WonderlandDemo, and GitHubProxy, all three end up in the registry. Adding a cycle (A→B→A) does not infinite-loop. A collision (two Linkables with simpleName `\"foo\"`) throws at boot.",
                    "4 hours (actual: ~2.5h, including the type-system foundation)",
                    "§3.3, Amendment 1",
                    "**Landed 2026-05-03.** The type-system foundation (Importable sealed supertype) was a small but necessary addition not explicitly called out in the RFC's Step 04 task list. ProxyApp can now appear as the `from` of a ModuleImports without pretending to be an EsModule. The pattern-matching switch in `collect()` (`case AppModule`/`case ProxyApp`/`case EsModule`) is clean and exhaustive thanks to Importable being sealed and Linkable also being sealed.\n\nThe writer's instanceof filter is a temporary measure: Step 05 will replace the silent skip of Linkable keys with proper `nav` const generation. The `@SuppressWarnings({\"unchecked\", \"rawtypes\"})` cast in the writer is the only ugly spot — it sits behind an `instanceof EsModule<?>` check, so it's safe, but Step 05 may refactor this once nav generation lands.\n\nThe cross-kind name collision check was straightforward but easy to forget; the test `crossKindCollision` catches the case where an AppModule and a ProxyApp share a simpleName, which would otherwise route ambiguously."
            ),

            new Step("05",
                    "Writer — `nav` Generation",
                    "EsModuleWriter emits the typed `nav` const in any module that imports an AppLink<?>.",
                    "For every `AppLink<?>` in a module's imports, the writer adds an entry to a frozen `nav` object generated at the top of the module's compiled JS. Entry shape differs slightly between AppModule targets (build `/app?app=...` URL with params) and ProxyApp targets (interpolate the URL template). Both kinds appear identical to the consuming JS.",
                    Status.DONE,
                    List.of(
                            new Task("New `NavWriter` class — pattern-matches over Linkable subtypes", true),
                            new Task("Detect `AppLink<?>` imports by scanning all import lists for `instanceof AppLink<?>`", true),
                            new Task("Emit nav block with `// === homing generated nav ===` markers", true),
                            new Task("Shared `_homingBuildAppUrl(simpleName, params)` helper for AppModule entries", true),
                            new Task("AppModule entries: `nav.X = function(p) { return _homingBuildAppUrl(\"x\", p); }`", true),
                            new Task("Theme/locale propagation in helper (override-wins logic)", true),
                            new Task("ProxyApp entries: inline interpolation function from `UrlTemplate.toJsExpression()`", true),
                            new Task("New `UrlTemplate.toJsExpression(paramVar)` method emitting JS code for the template", true),
                            new Task("`Object.freeze` the nav object", true),
                            new Task("URL contract uses `?app=<simpleName>` not `?class=`", true),
                            new Task("JS-string escaping for both literal segments and simpleName values (handles backslash, quote, control chars, slash)", true),
                            new Task("Insert nav block into `EsModuleWriter.writeModule()` between imports and content", true),
                            new Task("Empty output when no AppLink imports present (no helper, no nav)", true),
                            new Task("14 unit tests in `NavWriterTest`: empty, css-only, AppModule target, override name, proxy with params, proxy literal-only, multiple targets order, theme propagation, Object.freeze, app=URL contract, plus 4 UrlTemplate.toJsExpression tests", true)
                    ),
                    List.of(new Dependency("01", "AppLink type"),
                            new Dependency("03", "URL template parser for proxy entries"),
                            new Dependency("04", "Resolver provides simpleName at write time")),
                    "A consuming module's compiled JS contains a `nav.PitchDeck()` function returning the correct URL. A consuming module that imports a ProxyApp's link gets a `nav.GitHubProxy({repo: ...})` function returning the templated external URL.",
                    "4 hours (actual: ~3h)",
                    "§4.0.1, §4.1, §4.1.1",
                    "**Landed 2026-05-03.** The implementation diverges from the RFC in one minor way: §4.1.1 implied resolver-time lookup of simple names, but baking them into the generated JS at write time is simpler and matches §9.9's compile-time-baking recommendation. No runtime resolver dependency from the JS side.\n\nThe `UrlTemplate.toJsExpression(String paramVar)` method is the most interesting bit — it walks the same compiled segments that `render()` uses, but emits JS source instead of computing the URL directly. Pattern-matching over the sealed `Segment` interface keeps both code paths in lock-step: any future template feature (e.g., conditional segments) extends `Segment` and gets implementations on both sides.\n\nThe URL escaping uses `encodeURIComponent` in JS and `URLEncoder.encode` (Java) — these agree on the characters that matter for typical params (alphanumerics, slashes, query separators) but diverge on a few edge cases (spaces: `+` vs `%20`). This is documented as a known minor difference; tests exercise the agreement cases.\n\nThe writer's instanceof filter from Step 04 stays in place — it just no longer 'silently skips' Linkable keys, since `NavWriter` now picks them up. The two passes share `module.imports().getAllImports()` once via a local var, avoiding the duplicate fetch."
            ),

            new Step("06",
                    "Writer — `params` Generation",
                    "EsModuleWriter emits the typed `params` const in any AppModule with a non-Void paramsType.",
                    "For any AppModule whose `paramsType()` is non-Void, the writer generates a `params` const at the top of its compiled JS, populated from `window.location.search` with type-appropriate coercion per record component (String, int, Optional, List, Enum, etc.).",
                    Status.DONE,
                    List.of(
                            new Task("New `ParamsWriter` class — reflects on Params record components", true),
                            new Task("Detect non-Void paramsType in `EsModuleWriter` via `module instanceof AppModule`", true),
                            new Task("Generate `params` const initializer using URLSearchParams + IIFE wrapper", true),
                            new Task("Type coercion: String → `sp.get() || \"\"`", true),
                            new Task("Type coercion: primitive int/long/short/byte → parseInt with 0 default", true),
                            new Task("Type coercion: boxed Integer/Long/etc → parseInt with null default", true),
                            new Task("Type coercion: primitive double/float → parseFloat with 0 default", true),
                            new Task("Type coercion: boxed Double/Float → parseFloat with null default", true),
                            new Task("Type coercion: boolean / Boolean → strict `=== \"true\"` check", true),
                            new Task("Type coercion: Enum → `_enum` helper + allowed-values list (validates, returns null on mismatch)", true),
                            new Task("Optional<T> handling — `undefined` when absent, coerced T when present (via `scalarCoerce`)", true),
                            new Task("List<T> handling — `sp.getAll() || []` then per-element coerce", true),
                            new Task("Optional<Enum> and List<Enum> work via shared `_enum` closure", true),
                            new Task("Reserved-key collision check (`app`, `theme`, `locale`) at write time", true),
                            new Task("Forbid nested record components (per RFC §9.2)", true),
                            new Task("Forbid non-record paramsType (besides Void.class)", true),
                            new Task("Wire ParamsWriter into EsModuleWriter pipeline (between nav and content)", true),
                            new Task("`Object.freeze` the params object", true),
                            new Task("24 unit tests in `ParamsWriterTest`: each scalar type (10), Optional<String/Int>, List<String/Int>, Enum, Optional<Enum>, List<Enum>, combined, 3 reserved keys, nested-record forbidden, non-record forbidden, integration with EsModuleWriter (with and without Params)", true)
                    ),
                    List.of(new Dependency("02", "AppModule.paramsType() default")),
                    "An AppModule with `Params(String productId, int quantity, Optional<String> tab)` gets a generated `params` const that correctly parses query string into typed values. Missing optional → undefined. Missing required → empty string / 0.",
                    "5 hours (actual: ~3h)",
                    "§4.2, §4.3",
                    "**Landed 2026-05-03.** No RFC deviations. Per the RFC's §4.3 type coercion table, the implementation handles all eleven cases plus the tricky combinations (Optional<Enum>, List<Enum>) by lifting the `_enum` validator helper into the IIFE scope where every coercion can reference it via closure.\n\nRFC §9.2 deferred nested record components for v1; the implementation rejects them with a clear error message that quotes the RFC section. If a real use case appears later, this is one place where the writer would extend rather than break.\n\nThe writer integration is clean: `module instanceof AppModule<?> app ? app.paramsType() : Void.class` keeps non-AppModules (CssGroup-only generated modules) unaffected. The two test cases `writerEmitsParamsForAppWithParams` and `writerSkipsParamsForVoid` exercise both branches.\n\nPrimitive vs boxed types are handled distinctly: missing primitive → 0 (since you can't have a null primitive); missing boxed → null (faithful to Java semantics in JS-land where null and undefined are distinguishable). This was an under-specified point in the RFC; surfacing it in the type table makes the contract clear."
            ),

            new Step("07",
                    "Server — `?app=` Dispatch",
                    "HomingActionRegistry adds `?app=` URL contract and routes via SimpleAppResolver. `?class=` retained for the Step 11 migration window.",
                    "The kernel learns the `/app?app=<simpleName>` contract. The action registry consults the SimpleAppResolver to dispatch. Proxy app names return 404 (proxies are URL builders, not navigation targets). Both `?app=` and `?class=` are accepted during the migration window; `?app=` wins when both provided.",
                    Status.DONE,
                    List.of(
                            new Task("New `AppQuery` record supporting both `?app=` and `?class=` (with `hasSimpleName()` / `hasClassName()` helpers)", true),
                            new Task("`AppHtmlGetAction` reworked to dual-contract dispatch — `?app=` wins; `?class=` legacy fallback", true),
                            new Task("Resolver lookup via `resolveApp(simpleName)`; 404 if not registered", true),
                            new Task("Proxy-name explicit 404 — `resolveProxy(name) != null` short-circuits with clear message", true),
                            new Task("Action constructed without resolver → `?app=` requests fail cleanly (legacy-only mode preserved)", true),
                            new Task("Decided: keep `?class=` during Step 11 migration; drop after migration completes (per RFC §5.2)", true),
                            new Task("`HomingActionRegistry` accepts optional `SimpleAppResolver` (3 constructor overloads for backwards compat)", true),
                            new Task("Update `WonderlandDemoServer` to construct resolver from all 10 demo AppModules as entries", true),
                            new Task("Update `StudioServer` to construct resolver from all 5 studio AppModules as entries", true),
                            new Task("Update `StudioActionRegistry` to pass through the resolver", true),
                            new Task("Server boot prints registered apps with their `?app=` URLs", true),
                            new Task("Migrate existing `AppHtmlGetActionTest` to `AppQuery` (6 legacy tests still pass)", true),
                            new Task("4 new tests in `AppHtmlGetActionTest`: resolves by simpleName, 404 for unknown, fails cleanly without resolver, ?app= wins over ?class= when both present", true)
                    ),
                    List.of(new Dependency("04", "SimpleAppResolver")),
                    "GET `/app?app=pitch-deck` serves the same content as the old `/app?class=...PitchDeck`. GET `/app?app=github` (a proxy name) returns 404. GET `/app?app=unknown` returns 404 with available names listed in dev mode.",
                    "2 hours (actual: ~1.5h)",
                    "§3.4, §5",
                    "**Landed 2026-05-03.** Two RFC-spec deviations:\n\n1. **Kept `?class=` rather than dropping it.** RFC §5.2 recommended dropping cleanly given pre-adoption status — but the existing demo and studio JS still use `?class=` extensively (catalogue links, breadcrumbs, etc.). Migration to `?app=` is Step 11 work. Until then, `?class=` is the legacy fallback. After Step 11, `?class=` can be removed if desired (one-liner change).\n\n2. **Entry-app enumeration is verbose.** The RFC's transitive walking via AppLink imports is the long-term goal — but no AppModule has `link()` records yet (Step 11 work). Until then, every demo and studio app is registered as its own entry. Once Step 11 lands link() records and migrates the catalogues to import them, the entry list collapses to a single root per server (DemoCatalogue.INSTANCE, StudioCatalogue.INSTANCE).\n\nThe `AppHtmlGetAction` constructor accepts a nullable `SimpleAppResolver`. When null, only `?class=` works — preserves backwards compatibility for any caller still using the original two-arg constructor. The dual-contract dispatch is `if (hasSimpleName) { resolver path } else if (hasClassName) { reflection path } else { 404 }`.\n\nProxy-name 404 is a deliberate failure mode: a proxy is a URL builder, not a navigation target. The error message is explicit so a developer who accidentally tries to navigate to one understands why."
            ),

            new Step("08",
                    "Built-in Proxies",
                    "Mailto, Tel, Sms shipped in homing-core.proxies as ready-to-import ProxyApps.",
                    "Common non-HTTP URL schemes need typed declarations under the strict §6.2 rule. Rather than special-casing them in the scanner, they ship as proxy apps. Users who need `mailto:`, `tel:`, or `sms:` URLs import the built-in proxy and call `nav.Mailto({to: \"...\"})` like any other linkable.",
                    Status.DONE,
                    List.of(
                            new Task("`Mailto` with Params(to, subject?, body?, cc?, bcc?) — template `mailto:{to}?subject={subject?}&body={body?}&cc={cc?}&bcc={bcc?}`", true),
                            new Task("`Tel` with Params(number) — template `tel:{number}`", true),
                            new Task("`Sms` with Params(number, body?) — template `sms:{number}?body={body?}`", true),
                            new Task("Proper URL encoding (`@` → `%40`, `+` → `%2B`, etc.) verified by tests", true),
                            new Task("All three placed in `homing-core.proxies` sub-package — concrete instances, not framework primitives", true),
                            new Task("14 unit tests in `BuiltInProxiesTest`: each proxy template compiles + renders, simpleNames, link types, end-to-end resolver discovery, NavWriter emits typed entries for all three", true),
                            new Task("Documented v1 limitation: optional fields produce empty `&key=` segments (deferred query-string templating per RFC §3.8)", true),
                            new Task("User-guide examples — DEFERRED to Step 12 (documentation)", true)
                    ),
                    List.of(new Dependency("03", "ProxyApp + template parser")),
                    "Importing `Mailto.link()` and calling `nav.Mailto({to: \"u@e.com\", subject: \"Hi\"})` produces `\"mailto:u@e.com?subject=Hi\"`. Same for Tel and Sms.",
                    "1 hour (actual: ~45m)",
                    "§9.11",
                    "**Landed 2026-05-03.** One v1 simplification: when an optional Mailto/Sms field is absent, the URL still contains its empty key (e.g. `mailto:to?subject=&body=`). RFC §3.8 explicitly defers query-string templating with conditional segments — these built-ins live with that limitation. Mail clients accept the empty fields; the URL just looks slightly verbose. If users need cleaner URLs, they can declare a project-specific Mailto variant with only the fields they care about.\n\nFor `cc` / `bcc` with multiple recipients, users pass a comma-separated string within the single `Optional<String>` (RFC 6068 mailto syntax). `List<String>` would have required deferred List interpolation in the template DSL.\n\nSub-package `proxies` was added to `homing-core` to keep these *concrete instances* visually distinct from the framework primitives (AppModule, Linkable, ProxyApp). Future built-ins (Geo? Magnet? Custom protocol handlers?) slot into the same sub-package."
            ),

            new Step("09",
                    "`href` Manager — JS Runtime + Injection",
                    "The injected `href` helper, mirroring `css`. Six methods: toAttr, set, create, openNew, navigate, fragment.",
                    "Amendment 3's central piece. The `href` manager is auto-injected into any DomModule that imports an AppLink<?> — same mechanism as `css` today. Six methods cover every legitimate href use; no other interface is exposed. The manager is the chokepoint where future cross-cutting URL behaviour (analytics, history pushState, intercept hooks) will live.",
                    Status.DONE,
                    List.of(
                            new Task("New `HrefManager` EsModule (homing-server) — exports `HrefManagerInstance`, mirrors `CssClassManager` shape exactly", true),
                            new Task("`HrefManager.js` resource with `Object.freeze`d API", true),
                            new Task("JS: `href.toAttr(link)` returning `'href=\"...\"'` with attribute escape", true),
                            new Task("JS: `href.set(el, link)` setting el.href via setAttribute (returns el for chaining)", true),
                            new Task("JS: `href.create(link, opts)` returning HTMLAnchorElement (opts: text, className, target, rel, id)", true),
                            new Task("JS: `href.openNew(link, opts)` calling window.open with _blank target (opts: name, windowFeatures)", true),
                            new Task("JS: `href.navigate(link, opts)` calling window.location.assign or .replace per opts.replace", true),
                            new Task("JS: `href.fragment(slug)` returning `'href=\"#slug\"'` with attribute escape", true),
                            new Task("Type-checked link argument — TypeError if non-string passed (instead of cryptic runtime error)", true),
                            new Task("Auto-injection in `EsModuleGetAction.createWriter` — mirrors withCssManager exactly", true),
                            new Task("Injection scope: any module with at least one `AppLink<?>` import — matches CSS injection rule (per-need, not blanket)", true),
                            new Task("Helper method `EsModuleGetAction.importsAnyAppLink` (package-private for testing)", true),
                            new Task("8 unit tests in `HrefManagerTest`: EsModule shape, JS resource present + 6 methods + Object.freeze, fragment shape, detection true/false on 4 fixture cases", true)
                    ),
                    List.of(new Dependency("05", "nav generation must produce links to feed into href manager")),
                    "In a DomModule that imports an AppLink, the `href` identifier is in scope. Calling `href.toAttr(nav.PitchDeck())` returns `'href=\"/app?app=pitch-deck\"'`. All six methods work as documented in §4.0.1.",
                    "3 hours (actual: ~1.5h — pattern was straightforward to mirror from CssClassManager)",
                    "§4.0.1, Amendment 3",
                    "**Landed 2026-05-03.** No RFC deviations. The implementation mirrors CssClassManager precisely — same EsModule + JS-resource + `withXxxManager` injection pattern. The injection rule (\"any module with AppLink imports\") matches the CSS rule (\"any DomModule with cssGroups\") in shape: per-need, not blanket. Modules that don't navigate don't get the manager.\n\nThe JS API is intentionally narrow: 6 methods, no escape hatches, all wrapped in `Object.freeze`. Argument validation via TypeError gives users a clear error if they accidentally pass a Link object (Step 05's nav still returns strings, not objects, so this is for forward-compat / typo cases).\n\nThe `EsModuleGetAction.importsAnyAppLink` helper is package-private rather than private — it's the cleanest way to unit-test detection without mocking the whole action. Same trade as testing CssClassManager's detection."
            ),

            new Step("10",
                    "Conformance Scanner — Single Rule",
                    "HrefConformanceTest base class enforcing the single rule: `href` may only appear as the manager identifier.",
                    "The scanner. Mirrors the existing CssConformanceTest. One allowed pattern (`href\\.method(`), six forbidden patterns (literal href=, .href, \"href\" string, window.location, window.open, setAttribute href). Comment-aware. Allow-list mechanism for justified exceptions (used sparingly).",
                    Status.DONE,
                    List.of(
                            new Task("`HrefConformanceTest` base class in homing-conformance with @TestFactory dynamic-test-per-DomModule", true),
                            new Task("Forbidden: `\\bhref\\s*=` (literal href= attribute or property assignment)", true),
                            new Task("Forbidden: `\\.href\\b` (property access on element)", true),
                            new Task("Forbidden: `[\"']href[\"']` (the string \"href\" — typically setAttribute argument)", true),
                            new Task("Forbidden: `\\bwindow\\.location\\b` (any window.location read or write)", true),
                            new Task("Forbidden: `\\bwindow\\.open\\s*\\(` (window.open call)", true),
                            new Task("Forbidden: `setAttribute\\s*\\(\\s*[\"']href` (setAttribute with href arg — belt-and-braces)", true),
                            new Task("Allowed pattern is implicit — `href.toAttr(...)` etc. naturally don't match any forbidden pattern", true),
                            new Task("Comment-stripping pre-pass: state-machine handles // line comments, /* … */ block comments, AND preserves string literals (so `'href=' inside string` is still scanned)", true),
                            new Task("Multi-line block comments preserve newlines so reported line numbers stay accurate", true),
                            new Task("Allowlist mechanism via overrideable `allowList()` returning Set of DomModule classes", true),
                            new Task("Public static `scan(List<String>)` method — testable in isolation, used by base class internally", true),
                            new Task("`Violation` record (lineNumber, line, pattern) with toString() for diagnostic output", true),
                            new Task("Test failure message includes all 6 manager methods as remediation guidance", true),
                            new Task("Resilient to missing JS resources (skips silently — for generated-only modules)", true),
                            new Task("26 unit tests in `HrefConformanceScannerTest`: 6 forbidden patterns × allowed-pattern, 5 'still forbidden under strict rule' (concat with nav, template literal with nav, assign from nav, openNew with nav, fragment literal), 4 comment-handling cases (line, single-block, multi-block, // inside string), 3 line-number tests, multiple-violations, 2 stripComments helper tests", true)
                    ),
                    List.of(new Dependency("09", "href manager must exist before scanner can require it")),
                    "A DomModule with `'<a href=\"...\">'` literal fails the test. A DomModule with only `href.toAttr(nav.X())` passes. Comments containing `href=` are ignored.",
                    "2 hours (actual: ~1.5h)",
                    "§6.2",
                    "**Landed 2026-05-03.** Implementation matches RFC §6.2 exactly. The single-rule discipline turned out cleaner than I expected: by leaning on the *natural* property that `href.X(...)` doesn't match any forbidden pattern, no separate allow-pattern matching is needed. The denylist alone gives the right behavior.\n\nTwo design choices worth noting:\n\n1. **Strings are NOT excluded from scanning.** A literal `'<a href=\"...\">'` inside a JS string is exactly the pattern we want to forbid — that's where most user-side violations live. The state machine tracks string boundaries only to handle comment-detection correctly inside strings (so `\"// ...\"` isn't mistaken for a line comment). String contents flow through to the scanner unchanged. Test `comment_doubleSlashInsideString` verifies this.\n\n2. **Multiple patterns can match the same line.** `el.href = ...` matches both `\\bhref\\s*=` and `\\.href\\b`. The scanner reports both — same behavior as `CssConformanceTest`. Initially I expected exactly-1 in some test cases; corrected to `>= 1` and `allMatch(lineNumber == N)` to test line-accuracy without coupling to dedup.\n\nNo wiring into demo/studio yet — that's Step 11 (migration). Wiring before migration would just produce a sea of expected failures from current code that uses `?class=` URLs through hand-built strings."
            ),

            new Step("11",
                    "Demo & Studio Migration",
                    "Migrate every existing DomModule to use href.X(nav.Y(...)) — no raw href anywhere.",
                    "The migration sweep. Touches DemoCatalogue, every studio app, and the server bootstraps. After this step, both HrefConformanceTest implementations pass, and the URL contract is `?app=` everywhere in user-facing JS.",
                    Status.DONE,
                    List.of(
                            new Task("Add `link()` record to every linkable AppModule (10 demo + 5 studio = 15 in total)", true),
                            new Task("Add `Params` record + paramsType() override to DocReader (`?path=`) and Rfc0001Step (`?id=`)", true),
                            new Task("Update Java `imports()` of catalogues + browsers to bring in target `link()` records — DemoCatalogue (10 targets), StudioCatalogue (3), DocBrowser (2), DocReader (2), Rfc0001Plan (3), Rfc0001Step (4)", true),
                            new Task("Migrate `DemoCatalogue.js` — every card uses `nav.X()` via per-entry `link` function; `href.toAttr(...)` for innerHTML", true),
                            new Task("Migrate `StudioCatalogue.js` — brand link, app pills all via nav + href", true),
                            new Task("Migrate `DocBrowser.js` — brand, breadcrumb home, doc cards (`nav.DocReader({path: ...})`)", true),
                            new Task("Migrate `DocReader.js` — brand, breadcrumbs, TOC anchor (`href.fragment(slug)`); `params.path` replaces URLSearchParams read", true),
                            new Task("Migrate `Rfc0001Plan.js` — brand, breadcrumb, step cards, RFC doc link (raw JSON link demoted to plain code)", true),
                            new Task("Migrate `Rfc0001Step.js` — brand, breadcrumbs, dep links, RFC reference, prev/next nav; `params.id` replaces URLSearchParams read", true),
                            new Task("MovingAnimal — kept as-is, allowlisted in DemoHrefConformanceTest with rationale (theme-state read needs kernel work)", true),
                            new Task("Simplify `WonderlandDemoServer` to single entry app — `DemoCatalogue.INSTANCE` (transitive walking discovers everything else)", true),
                            new Task("Simplify `StudioServer` to single entry app — `StudioCatalogue.INSTANCE`", true),
                            new Task("Wire `DemoHrefConformanceTest` (homing-demo, 14 modules scanned, 1 allowlist)", true),
                            new Task("Wire `StudioHrefConformanceTest` (homing-studio, 5 modules scanned, 0 allowlist)", true),
                            new Task("Verify both HrefConformance tests pass", true)
                    ),
                    List.of(new Dependency("07", "?app= dispatch must work"),
                            new Dependency("09", "href manager must be available"),
                            new Dependency("10", "Scanner enforces the discipline")),
                    "Every existing demo and studio app loads correctly under the new URL scheme. Both DemoHrefConformanceTest and StudioHrefConformanceTest pass. No raw href substring exists outside `href.X(...)` calls in any DomModule JS.",
                    "3 hours (actual: ~3h)",
                    "§10.3",
                    "**Landed 2026-05-03.** The migration was as mechanical as expected. Four points worth recording:\n\n1. **Single-entry server bootstraps.** Pre-Step-11 we had to enumerate every demo as an entry app. Post-migration, each catalogue imports every linkable target's `link()`, and the resolver discovers the whole graph transitively from the catalogue alone. WonderlandDemoServer's entry-list dropped from 10 lines to 1; StudioServer's from 5 to 1. This validates the entire transitive-walk design (Step 04 / Amendment 1) end-to-end.\n\n2. **MovingAnimal allowlisted.** The platformer reads `?theme=` to highlight the active theme button, then writes a new URL to switch theme. The write migrates cleanly to `href.navigate(nav.MovingAnimal({theme: t.key}))` — but the read needs `window.location.search` because theme is a reserved key in Params (can't appear in user's Params record). The clean fix is to auto-include theme/locale in the generated `params` const at the kernel level — a small follow-up change, but explicitly out of Step 11 scope. The allowlist carries an inline justification and points to the future fix.\n\n3. **`?class=` is no longer in user JS, but the action still accepts it.** The kernel's `AppHtmlGetAction` retains the legacy `?class=` path for backwards compatibility — pre-existing bookmarks and external links keep working. With Step 11 complete, we *could* drop it; deferring that one-liner change to the user's discretion.\n\n4. **Bug found and fixed during browser verification: duplicate `link` import collision.** First page load failed with `Uncaught SyntaxError: Identifier 'link' has already been declared`. Root cause: `SingleModuleImportWriter` was emitting `import { link } from \"...\"` for every `AppLink<?>` import. Each target's inner record is named `link`, so multiple targets caused JS-level identifier collision. AppLink records are pure Java metadata for `nav` generation — they don't correspond to JS-side exports. **Fix:** filter out `AppLink<?>` entries before emitting the `import { … }` line; if the list becomes empty, emit no line at all. Two regression tests added in `SingleModuleImportWriterTest` (`writeImports_skipsAppLinkOnlyImports` and `writeImports_filtersAppLinkFromMixedImports`). This bug is a class of issue worth noting for future RFCs that introduce new Exportable flavors: ask up-front whether the new flavor corresponds to a real JS export or is metadata-only, and have the writer treat the two kinds differently from day one."
            ),

            new Step("12",
                    "Documentation",
                    "Update kernel README, user guide, white paper, brochure to reflect typed nav as a first-class feature.",
                    "Closing the loop. The kernel README documents the new URL contract. The user guide gains \"Linking between apps\" and \"Modeling external destinations as proxy apps\" sections. White paper §4.5 and brochure §02/§03 add MPA support and conformance enforcement as positioning points. The CSS conformance docs reference href conformance as a sibling discipline.",
                    Status.DONE,
                    List.of(
                            new Task("Updated kernel README — endpoints table shows `?app=`/`?class=` dual contract; new top-level `Typed Navigation` section walks through link records, Params, nav+href in JS, server bootstrap, proxies, conformance", true),
                            new Task("Updated user guide — endpoints table; ToC reorganised; new `Linking Between Apps` section with full walkthrough; new `Modeling External Destinations` section covering built-in proxies + custom ProxyApp + URL template DSL", true),
                            new Task("User guide conformance section — added `HrefConformanceTest` as sibling-discipline subsection with single-rule explanation", true),
                            new Task("Updated white paper §4.2 — typed-navigation paragraph added (typed nav is a Layer 1 capability that extends the records-as-source-of-truth pattern to URLs)", true),
                            new Task("Updated white paper §7.1 — typed nav moved from `designed` to `built`; new modules listed; date updated to 2026-05-03", true),
                            new Task("Updated brochure §02 (business case) — positioning row added covering URL stability, typed link/params, proxy-app modeling for external URLs", true),
                            new Task("Marked RFC 0001 header as **Implemented** with landing date and pointer to studio's live tracker", true),
                            new Task("Brochure §03 (competitive landscape) — DEFERRED: existing positioning still accurate; no contract-level change needed since competitors don't differentiate on URL design", true),
                            new Task("DocRegistry update — DEFERRED: no new files added; existing entries (RFC 0001, brand README, etc.) already cover the surface", true)
                    ),
                    List.of(new Dependency("11", "Migration complete — examples in docs reflect actual runnable code")),
                    "All four major documentation surfaces (kernel README, user guide, white paper, brochure) reflect the new contract. Cross-references between CSS and href conformance exist. RFC 0001 status is updated to Implemented.",
                    "1 hour (actual: ~1.5h)",
                    "§10.4",
                    "**Landed 2026-05-03.** RFC 0001 implementation complete — all 12 steps DONE. Two docs items deferred with rationale:\n\n1. **Brochure §03 (competitive landscape).** Reviewed; the existing comparison table doesn't have a row for URL/navigation design (none of the competitor frameworks differentiate on it), so adding one would be inventing a category. The competitive story is still accurate as written. If marketing later wants to surface the typed-nav advantage, it belongs in a new section or a separate brochure piece.\n\n2. **DocRegistry update.** No new doc files were added during the implementation; existing entries (the RFC itself, brand README, session summary, action plan) already index the surface. The studio's DocBrowser correctly lists everything.\n\n**Closing observation.** The end-to-end loop is now closed: edit `Rfc0001Steps.java` → recompile → refresh the studio at `/app?app=rfc0001-plan` → see live state. The progress tracker is itself an `AppModule` declared via the very framework it tracks. Cleanest possible eat-your-own-dog-food validation."
            )
    );

    public static Step byId(String id) {
        for (var s : STEPS) if (s.id().equals(id)) return s;
        return null;
    }

    public static int totalProgressPercent() {
        if (STEPS.isEmpty()) return 0;
        int sum = 0;
        int totalTasks = 0;
        for (var s : STEPS) {
            sum += s.tasks().stream().filter(Task::done).count();
            totalTasks += s.tasks().size();
        }
        return totalTasks == 0 ? 0 : (sum * 100 / totalTasks);
    }

    private Rfc0001Steps() {}
}
