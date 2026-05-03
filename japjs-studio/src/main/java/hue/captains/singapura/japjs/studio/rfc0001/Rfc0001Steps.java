package hue.captains.singapura.japjs.studio.rfc0001;

import java.util.List;

/**
 * Implementation tracker for RFC 0001 — App Registry &amp; Typed Navigation.
 *
 * <p><b>How to record progress:</b> edit the {@link #STEPS} list below.
 * Flip a {@link Task#done()} from false → true; change a {@link Status}; add a
 * note to the discussion log. Recompile, refresh the studio, the new state
 * appears in the plan and step views. Git history is the change log.</p>
 *
 * <p>This file is the single source of truth. The JS view layer fetches it via
 * {@code /step-data?rfc=0001} and renders read-only.</p>
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
                    Status.NOT_STARTED,
                    List.of(
                            new Task("Define `ProxyApp<P extends ProxyApp<P>>` interface", false),
                            new Task("Spec the URL template DSL syntax (§3.8)", false),
                            new Task("Implement the template parser (Java)", false),
                            new Task("Validate template params match the Params record at compile time", false),
                            new Task("Reject required `{name}` for Optional params", false),
                            new Task("Unit tests covering all template forms", false)
                    ),
                    List.of(new Dependency("01", "Linkable supertype")),
                    "Templates `https://github.com/{repo}/{path?}` parse correctly. Invalid templates fail parse with clear error messages. Compile-time mismatch between template and Params is caught.",
                    "5 hours",
                    "§3.7, §3.8",
                    ""
            ),

            new Step("04",
                    "SimpleAppResolver — Transitive Walk",
                    "The boot-time registry that walks AppLink edges from entry apps to discover all reachable Linkables.",
                    "Implements Amendment 1's transitive registration. The user enumerates entry apps; the resolver walks `AppLink<?>` import edges (only those, not all imports) to find every reachable AppModule and ProxyApp. Cycle-tolerant via visited set. Collisions in the closure fail fast at boot.",
                    Status.NOT_STARTED,
                    List.of(
                            new Task("Define `SimpleAppResolver` class skeleton with two maps (apps + proxies)", false),
                            new Task("Implement the `collect()` walker following only AppLink edges", false),
                            new Task("Cycle detection via visited set", false),
                            new Task("Simple-name collision detection across both kinds", false),
                            new Task("`resolve(simpleName)` returning the Linkable or null", false),
                            new Task("`resolveByClass(Class)` for direct lookups", false),
                            new Task("`all()` for diagnostic listing", false),
                            new Task("Unit tests: linear chain, diamond, cycle, orphan, collision", false)
                    ),
                    List.of(new Dependency("01", "AppLink and Linkable must exist"),
                            new Dependency("03", "ProxyApp must exist to be walked")),
                    "Given DemoCatalogue importing PitchDeck, WonderlandDemo, and GitHubProxy, all three end up in the registry. Adding a cycle (A→B→A) does not infinite-loop. A collision (two Linkables with simpleName `\"foo\"`) throws at boot.",
                    "4 hours",
                    "§3.3, Amendment 1",
                    ""
            ),

            new Step("05",
                    "Writer — `nav` Generation",
                    "EsModuleWriter emits the typed `nav` const in any module that imports an AppLink<?>.",
                    "For every `AppLink<?>` in a module's imports, the writer adds an entry to a frozen `nav` object generated at the top of the module's compiled JS. Entry shape differs slightly between AppModule targets (build `/app?app=...` URL with params) and ProxyApp targets (interpolate the URL template). Both kinds appear identical to the consuming JS.",
                    Status.NOT_STARTED,
                    List.of(
                            new Task("Detect `AppLink<?>` imports during module writing", false),
                            new Task("Generate `nav` const header section", false),
                            new Task("Emit AppModule entries: build `/app?app=<simpleName>&params...` URL", false),
                            new Task("Emit ProxyApp entries: interpolate urlTemplate with params", false),
                            new Task("Theme/locale propagation in generated functions", false),
                            new Task("`Object.freeze` the nav object", false),
                            new Task("Integration tests with sample modules", false)
                    ),
                    List.of(new Dependency("01", "AppLink type"),
                            new Dependency("03", "URL template parser for proxy entries"),
                            new Dependency("04", "Resolver provides simpleName at write time")),
                    "A consuming module's compiled JS contains a `nav.PitchDeck()` function returning the correct URL. A consuming module that imports a ProxyApp's link gets a `nav.GitHubProxy({repo: ...})` function returning the templated external URL.",
                    "4 hours",
                    "§4.0.1, §4.1, §4.1.1",
                    ""
            ),

            new Step("06",
                    "Writer — `params` Generation",
                    "EsModuleWriter emits the typed `params` const in any AppModule with a non-Void paramsType.",
                    "For any AppModule whose `paramsType()` is non-Void, the writer generates a `params` const at the top of its compiled JS, populated from `window.location.search` with type-appropriate coercion per record component (String, int, Optional, List, Enum, etc.).",
                    Status.NOT_STARTED,
                    List.of(
                            new Task("Detect non-Void paramsType during module writing", false),
                            new Task("Generate `params` const initializer using URLSearchParams", false),
                            new Task("Type coercion for String, int, long, double, boolean", false),
                            new Task("Optional<T> handling — undefined when absent", false),
                            new Task("List<T> handling — getAll + per-element coerce", false),
                            new Task("Enum validation — accept only declared values", false),
                            new Task("Reserved-key collision check (params component named `app`/`theme`/`locale`)", false),
                            new Task("Integration tests with sample param shapes", false)
                    ),
                    List.of(new Dependency("02", "AppModule.paramsType() default")),
                    "An AppModule with `Params(String productId, int quantity, Optional<String> tab)` gets a generated `params` const that correctly parses query string into typed values. Missing optional → undefined. Missing required → empty string / 0.",
                    "5 hours",
                    "§4.2, §4.3",
                    ""
            ),

            new Step("07",
                    "Server — `?app=` Dispatch",
                    "JapjsActionRegistry replaces the `?class=` URL contract with `?app=` and routes via SimpleAppResolver.",
                    "The kernel's URL contract changes from `/app?class=<canonical>` to `/app?app=<simpleName>`. The action registry consults the SimpleAppResolver to dispatch. Proxy app names return 404 (proxies are URL builders, not navigation targets). Pre-adoption status means we drop `?class=` rather than maintain a deprecated alias.",
                    Status.NOT_STARTED,
                    List.of(
                            new Task("JapjsActionRegistry accepts a SimpleAppResolver", false),
                            new Task("AppHtmlGetAction reads `?app=` parameter", false),
                            new Task("Lookup via resolver; 404 if not found", false),
                            new Task("Reject proxy lookups (proxies are not routable) with 404", false),
                            new Task("Decide: drop `?class=` entirely vs deprecate (RFC §5.2)", false),
                            new Task("Update WonderlandDemoServer / StudioServer to construct resolver", false),
                            new Task("Integration tests for `?app=` happy path, missing, proxy, malformed", false)
                    ),
                    List.of(new Dependency("04", "SimpleAppResolver")),
                    "GET `/app?app=pitch-deck` serves the same content as the old `/app?class=...PitchDeck`. GET `/app?app=github` (a proxy name) returns 404. GET `/app?app=unknown` returns 404 with available names listed in dev mode.",
                    "2 hours",
                    "§3.4, §5",
                    ""
            ),

            new Step("08",
                    "Built-in Proxies",
                    "Mailto, Tel, Sms shipped in japjs-core as ready-to-import ProxyApps.",
                    "Common non-HTTP URL schemes need typed declarations under the strict §6.2 rule. Rather than special-casing them in the scanner, they ship as proxy apps. Users who need `mailto:`, `tel:`, or `sms:` URLs import the built-in proxy and call `nav.Mailto({to: \"...\"})` like any other linkable.",
                    Status.NOT_STARTED,
                    List.of(
                            new Task("Mailto proxy with Params(to, subject?, body?, cc?, bcc?)", false),
                            new Task("Tel proxy with Params(number)", false),
                            new Task("Sms proxy with Params(number, body?)", false),
                            new Task("URL templates for each scheme with proper encoding", false),
                            new Task("User-guide examples for each", false)
                    ),
                    List.of(new Dependency("03", "ProxyApp + template parser")),
                    "Importing `Mailto.link()` and calling `nav.Mailto({to: \"u@e.com\", subject: \"Hi\"})` produces `\"mailto:u@e.com?subject=Hi\"`. Same for Tel and Sms.",
                    "1 hour",
                    "§9.11",
                    ""
            ),

            new Step("09",
                    "`href` Manager — JS Runtime + Injection",
                    "The injected `href` helper, mirroring `css`. Six methods: toAttr, set, create, openNew, navigate, fragment.",
                    "Amendment 3's central piece. The `href` manager is auto-injected into any DomModule that imports an AppLink<?> — same mechanism as `css` today. Six methods cover every legitimate href use; no other interface is exposed. The manager is the chokepoint where future cross-cutting URL behaviour (analytics, history pushState, intercept hooks) will live.",
                    Status.NOT_STARTED,
                    List.of(
                            new Task("JS runtime: `href.toAttr(link)` returning attribute fragment", false),
                            new Task("JS runtime: `href.set(el, link)` setting el.href", false),
                            new Task("JS runtime: `href.create(link, opts)` returning HTMLAnchorElement", false),
                            new Task("JS runtime: `href.openNew(link, opts)` calling window.open", false),
                            new Task("JS runtime: `href.navigate(link, opts)` calling window.location.assign", false),
                            new Task("JS runtime: `href.fragment(slug)` for same-page anchors", false),
                            new Task("Bootstrap auto-injection alongside `css` injection", false),
                            new Task("Decide injection scope — every DomModule, or only those with AppLink imports", false)
                    ),
                    List.of(new Dependency("05", "nav generation must produce links to feed into href manager")),
                    "In a DomModule that imports an AppLink, the `href` identifier is in scope. Calling `href.toAttr(nav.PitchDeck())` returns `'href=\"/app?app=pitch-deck\"'`. All six methods work as documented in §4.0.1.",
                    "3 hours",
                    "§4.0.1, Amendment 3",
                    ""
            ),

            new Step("10",
                    "Conformance Scanner — Single Rule",
                    "HrefConformanceTest base class enforcing the single rule: `href` may only appear as the manager identifier.",
                    "The scanner. Mirrors the existing CssConformanceTest. One allowed pattern (`href\\.method(`), six forbidden patterns (literal href=, .href, \"href\" string, window.location, window.open, setAttribute href). Comment-aware. Allow-list mechanism for justified exceptions (used sparingly).",
                    Status.NOT_STARTED,
                    List.of(
                            new Task("HrefConformanceTest base class with @TestFactory", false),
                            new Task("Forbidden: `\\bhref\\s*=` regex", false),
                            new Task("Forbidden: `\\.href\\b` regex", false),
                            new Task("Forbidden: `[\"']href[\"']` string regex", false),
                            new Task("Forbidden: `\\bwindow\\.location\\b` regex", false),
                            new Task("Forbidden: `\\bwindow\\.open\\s*\\(` regex", false),
                            new Task("Forbidden: `setAttribute\\s*\\(\\s*[\"']href` regex", false),
                            new Task("Allowed: `\\bhref\\s*\\.\\s*(toAttr|set|create|openNew|navigate|fragment)\\s*\\(` regex", false),
                            new Task("Comment-stripping pre-pass (line and block comments)", false),
                            new Task("Allowlist mechanism with justification field", false),
                            new Task("Unit tests with positive and negative samples", false)
                    ),
                    List.of(new Dependency("09", "href manager must exist before scanner can require it")),
                    "A DomModule with `'<a href=\"...\">'` literal fails the test. A DomModule with only `href.toAttr(nav.X())` passes. Comments containing `href=` are ignored.",
                    "2 hours",
                    "§6.2",
                    ""
            ),

            new Step("11",
                    "Demo & Studio Migration",
                    "Migrate every existing DomModule to use href.X(nav.Y(...)) — no raw href anywhere.",
                    "The migration sweep. Touches DemoCatalogue, PitchDeck, MovingAnimal, every studio app. After this step, the existing CSS conformance scanner has a sibling href conformance scanner, both passing. The `?class=` URL pattern is fully retired in user-facing code.",
                    Status.NOT_STARTED,
                    List.of(
                            new Task("Migrate DemoCatalogue.js to nav + href", false),
                            new Task("Migrate PitchDeck (any internal cross-slide links)", false),
                            new Task("Migrate MovingAnimal (theme switching)", false),
                            new Task("Migrate StudioCatalogue.js", false),
                            new Task("Migrate DocBrowser.js (card hrefs to DocReader)", false),
                            new Task("Migrate DocReader.js (back-link, breadcrumbs, anchor links)", false),
                            new Task("Migrate Rfc0001Plan.js (links to Step app)", false),
                            new Task("Migrate Rfc0001Step.js (links back to plan, to RFC doc)", false),
                            new Task("Add link() records to every linkable AppModule", false),
                            new Task("Update WonderlandDemoServer + StudioServer to construct resolver", false),
                            new Task("Verify HrefConformanceTest passes for both modules", false)
                    ),
                    List.of(new Dependency("07", "?app= dispatch must work"),
                            new Dependency("09", "href manager must be available"),
                            new Dependency("10", "Scanner enforces the discipline")),
                    "Every existing demo and studio app loads correctly under the new URL scheme. Both DemoHrefConformanceTest and StudioHrefConformanceTest pass. No raw href substring exists outside `href.X(...)` calls in any DomModule JS.",
                    "3 hours",
                    "§10.3",
                    ""
            ),

            new Step("12",
                    "Documentation",
                    "Update kernel README, user guide, white paper, brochure to reflect typed nav as a first-class feature.",
                    "Closing the loop. The kernel README documents the new URL contract. The user guide gains \"Linking between apps\" and \"Modeling external destinations as proxy apps\" sections. White paper §4.5 and brochure §02/§03 add MPA support and conformance enforcement as positioning points. The CSS conformance docs reference href conformance as a sibling discipline.",
                    Status.NOT_STARTED,
                    List.of(
                            new Task("Update kernel README with `?app=` URL contract", false),
                            new Task("Add `Linking between apps` to docs/user-guide.md", false),
                            new Task("Add `Modeling external destinations` to docs/user-guide.md", false),
                            new Task("Update docs/whitepaper/japjs-whitepaper.md §4.5", false),
                            new Task("Update docs/brochure/02-business-case.md positioning", false),
                            new Task("Update docs/brochure/03-competitive-landscape.md", false),
                            new Task("Cross-reference href conformance from CSS conformance docs", false),
                            new Task("Mark RFC 0001 as Implemented in its own header", false),
                            new Task("Update DocRegistry.java if any new docs added", false)
                    ),
                    List.of(new Dependency("11", "Migration complete — examples in docs reflect actual runnable code")),
                    "All four major documentation surfaces (kernel README, user guide, white paper, brochure) reflect the new contract. Cross-references between CSS and href conformance exist. RFC 0001 status is updated to Implemented.",
                    "1 hour",
                    "§10.4",
                    ""
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
