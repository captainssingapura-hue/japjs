# Live Tracker Pattern — Build a Studio App for Any Project Plan

> **⚠️ This guide describes the legacy per-plan pattern (~900 LoC per tracker, hand-written `*Plan.js`, `*Step.js`, `*DataGetAction.java`). It was superseded on 2026-05-09 by the [tracker kit](#ref:def-1) in `homing-studio/.../tracker/`. New trackers should use the kit (~80 LoC: implement `Plan`, write a thin adapter, declare two `record` AppModules extending `PlanAppModule` / `PlanStepAppModule`).**
>
> The doctrines section below (single source of truth, edit-Java-refresh, deep-linkable, versioned with code) still describes the value of the pattern accurately. The implementation details (HTML-string renderers, `*DataGetAction`, hand-listed CSS imports) are obsolete — replaced by the kit's auto-generated JS via `SelfContent`.
>
> **Use this guide for the *why* and the *shape*. Use the [Defect 0001 resolution notes](#ref:def-1) for the *how*.**

How to use the same technique that powers `homing-studio`'s tracker views to track *any* multi-phase project — implementation plans, migrations, audits, rollouts — with a live web view that updates the moment you edit the source.

---

## What you get

- **Single source of truth** — one Java file holds every phase, task, decision, dependency, and status. The tracker view is a pure projection of it.
- **Edit Java, refresh, see the update** — no spreadsheets, no wiki edits, no out-of-band TODO files.
- **Deep-linkable** — every phase has its own URL (`/app?app=my-plan-step&phase=03`).
- **Versioned with code** — the plan lives in the same git history as the work it tracks.
- **Eat-your-own-dogfood** — the tracker itself is built on the framework, so it serves as a worked example of every primitive.

This isn't a roadmap tool for external stakeholders — it's a working surface for the engineer doing the work. Use it when the plan will change while you execute it.

---

## When to reach for this

| Use it when… | Skip it when… |
|---|---|
| Plan has 3+ phases each with multiple tasks | Single-step task — just do it |
| You'll iterate on the plan while executing it | Plan is fixed and won't change |
| Decisions need recording alongside tasks | Tracking is for an external audience (use a real PM tool) |
| Work spans days/weeks | Work fits in a single sitting |
| You want commit history of the plan's evolution | You don't care about the plan's history |

The pattern shines for migrations, RFC implementations, multi-step refactors, release rollouts, and audit checklists.

---

## Architecture

Three pieces, one route, one tile:

```
┌──────────────────────────┐         ┌──────────────────────────┐
│  MyPlanSteps.java        │         │  MyPlanDataGetAction     │
│  ────────────────        │ ──────▶ │  ──────────────────      │
│  records: Phase, Task,   │ reads   │  GET /my-plan-data       │
│           Decision, …    │         │  GET /my-plan-data       │
│  static List<Phase>      │         │       ?phase=03          │
│  helper: progressPercent │         │  → application/json      │
└──────────────────────────┘         └──────────────────────────┘
                                                  │
                                                  ▼ fetch
                       ┌──────────────────────────────────────────┐
                       │  MyPlan.js  +  MyPlanStep.js             │
                       │  ──────────────────────────              │
                       │  appMain(rootElement) {                  │
                       │    fetch('/my-plan-data')                │
                       │      .then(d => render(d))               │
                       │  }                                        │
                       └──────────────────────────────────────────┘
                                                  ▲
                                                  │ wired by
                       ┌──────────────────────────────────────────┐
                       │  MyPlan.java  +  MyPlanStep.java         │
                       │  (AppModule definitions: title +         │
                       │   imports + exports)                     │
                       └──────────────────────────────────────────┘
```

You add **6 files + 3 wiring edits**. That's it.

---

## Recipe

The reference implementation lives in `homing-studio/src/main/java/.../studio/rename/` and `homing-studio/src/main/java/.../studio/rfc0001/`. Read those two trackers alongside this guide — every step below has a concrete file you can copy.

### Prerequisite — what to depend on

The Homing reactor splits the studio surface across two modules. Pick the one that matches your project:

| Module | What it gives you | When to depend on it |
|---|---|---|
| **`homing-studio`** | Everything below + `StudioStyles` (the CSS palette your views will reference), `StudioCatalogue` / `DocBrowser` / `DocReader` AppModules, the rename + RFC 0001 tracker apps | **Recommended for tracker apps today** — you'll use `StudioStyles` heavily and want `StudioCatalogue` as the back-link target |
| **`homing-studio-base`** | Typed `Doc<D>` / `DocGroup<D>` proxy, `DocManager` runtime, `DocGetAction` (`/doc?path=…` classpath markdown server) | Standalone if you only want the typed-doc-proxy infrastructure and are bringing your own catalogue + styles |
| `homing-server` | Action registry, Vert.x adapter, `EsModuleGetAction` | Always pulled in transitively |
| `homing-core` | `AppModule`, `AppLink`, `DomModule`, `ImportsFor`, `SelfContent`, `ManagerInjector` | Always pulled in transitively |
| `homing-libs` | Bundled 3rd-party JS (`MarkedJs`, `ThreeJs`, `ToneJs`) — already in homing-studio's classpath via the studio dep | Pulled in transitively |

For most cases — including all the templates in this guide — you want **`homing-studio`**. Add this to your project's pom:

```xml
<dependency>
    <groupId>io.github.captainssingapura-hue.homing.js</groupId>
    <artifactId>homing-studio</artifactId>
    <version>1.0-SNAPSHOT</version>
</dependency>
```

That single dep transitively brings in `homing-studio-base`, `homing-libs`, `homing-server`, and `homing-core`. The `MyPlan.java` and `MyPlanStep.java` templates below import `StudioStyles` and `StudioCatalogue` directly — both live in `homing-studio`.

**Trajectory note:** `homing-studio-base` is the long-term home for the reusable studio infrastructure. As more pieces get extracted from `homing-studio` (StudioStyles, DocBrowser, DocReader, a generic StudioCatalogue base, StudioServer base), depending only on `homing-studio-base` will become the cleaner choice for downstream projects that don't want Homing's own tracker apps shipped with their JAR. Today's split is partial — the typed Doc proxy is in studio-base; everything else is still in homing-studio.

---

### Step 1 — Define the data class (`MyPlanSteps.java`)

This is the **only file you'll edit while the plan is live**. Put plain `record`s in a `List.of(...)` static field, with helper methods on the records for derived state (progress %, remaining tasks, etc.).

```java
package com.example.myproject.studio.myplan;

import java.util.List;

public final class MyPlanSteps {

    public enum Status {
        NOT_STARTED("Not started", "not-started"),
        IN_PROGRESS("In progress", "in-progress"),
        BLOCKED   ("Blocked",     "blocked"),
        DONE      ("Done",        "done");

        public final String label;
        public final String slug;
        Status(String label, String slug) { this.label = label; this.slug = slug; }
    }

    public record Task(String description, boolean done) {}
    public record Dependency(String phaseId, String reason) {}
    public record Phase(
            String id,            // "01", "02", … (zero-padded for stable sort)
            String label,         // short title shown on the card
            String summary,       // one-line description
            String description,   // markdown / multi-paragraph context
            Status status,
            List<Task> tasks,
            List<Dependency> dependsOn,
            String verification,  // the gate that must pass before claiming "done"
            String rollback,      // what to do if this phase needs to be reverted
            String effort,        // estimate (free-form: "2 hours", "half a day")
            String notes
    ) {
        public int progressPercent() {
            if (tasks.isEmpty()) return 0;
            long done = tasks.stream().filter(Task::done).count();
            return (int) (done * 100 / tasks.size());
        }
    }

    public static final List<Phase> PHASES = List.of(
            new Phase("01", "Snapshot", "Tag the current state.",
                    "Establish a clean rollback point.",
                    Status.NOT_STARTED,
                    List.of(
                            new Task("Working tree is clean", false),
                            new Task("Tag pre-migration", false)
                    ),
                    List.of(),
                    "git tag | grep pre-migration returns the tag",
                    "N/A — this phase IS the rollback point",
                    "15 minutes",
                    "")
            // … more phases …
    );

    public static int totalProgressPercent() {
        if (PHASES.isEmpty()) return 0;
        long doneTasks  = PHASES.stream().flatMap(p -> p.tasks().stream()).filter(Task::done).count();
        long totalTasks = PHASES.stream().mapToLong(p -> p.tasks().size()).sum();
        return totalTasks == 0 ? 0 : (int) (doneTasks * 100 / totalTasks);
    }

    private MyPlanSteps() {}
}
```

**Optional but recommended** — add a `Decision` record alongside `Phase` for open questions you want to surface in the UI before execution:

```java
public record Decision(String id, String question, String recommendation,
                       String chosenValue, DecisionStatus status,
                       String rationale, String notes) {}

public static final List<Decision> DECISIONS = List.of(
        new Decision("D1",
                "Which auth provider — OIDC or SAML?",
                "OIDC. Lower complexity; matches existing identity stack.",
                null,                         // null while OPEN; fill when RESOLVED
                DecisionStatus.OPEN,
                "SAML works too but adds a config surface we'd rather avoid.",
                "")
);
```

Decisions sit at the top of the plan view, so the user resolves them before clicking into phases.

---

### Step 2 — Write the JSON action (`MyPlanDataGetAction.java`)

One GET endpoint, two shapes:
- `GET /my-plan-data` → `{decisions: [...], phases: [...], totalProgress, openDecisions}`
- `GET /my-plan-data?phase=03` → a single phase object

The data class also needs a lookup helper `phaseById(id)` — add it now:

```java
// In MyPlanSteps.java, at the bottom:
public static Phase phaseById(String id) {
    for (var p : PHASES) if (p.id().equals(id)) return p;
    return null;
}
```

The full action — copy this verbatim and substitute `MyPlanSteps` and the field names of your records. Manual JSON serialization avoids pulling Jackson/Gson into the dep graph and is well under 100 lines:

```java
package com.example.myproject.studio.myplan;

import hue.captains.singapura.js.homing.server.EmptyParam;
import hue.captains.singapura.js.homing.server.ResourceNotFound;
import hue.captains.singapura.tao.http.action.GetAction;
import hue.captains.singapura.tao.http.action.Param;
import hue.captains.singapura.tao.http.action.ParamMarshaller;
import hue.captains.singapura.tao.http.action.TypedContent;
import io.vertx.ext.web.RoutingContext;

import java.util.concurrent.CompletableFuture;

public class MyPlanDataGetAction
        implements GetAction<RoutingContext, MyPlanDataGetAction.Query,
                              EmptyParam.NoHeaders, MyPlanDataGetAction.Json> {

    public record Query(String phaseId) implements Param._QueryString {}
    public record Json(String body) implements TypedContent {
        @Override public String contentType() { return "application/json; charset=utf-8"; }
    }

    @Override
    public ParamMarshaller._QueryString<RoutingContext, Query> queryStrMarshaller() {
        return ctx -> new Query(ctx.request().getParam("phase"));
    }

    @Override
    public ParamMarshaller._Header<RoutingContext, EmptyParam.NoHeaders> headerMarshaller() {
        return ctx -> new EmptyParam.NoHeaders();
    }

    @Override
    public CompletableFuture<Json> execute(Query query, EmptyParam.NoHeaders headers) {
        if (query.phaseId() == null || query.phaseId().isBlank()) {
            return CompletableFuture.completedFuture(new Json(serializeAll()));
        }
        var phase = MyPlanSteps.phaseById(query.phaseId());
        if (phase == null) {
            return CompletableFuture.failedFuture(notFound(query.phaseId(), "Unknown phase id"));
        }
        return CompletableFuture.completedFuture(new Json(serializePhase(phase)));
    }

    // ---- minimal JSON serialization (no external dep) --------------------

    private static String serializeAll() {
        StringBuilder sb = new StringBuilder();
        sb.append("{\"totalProgress\":").append(MyPlanSteps.totalProgressPercent()).append(",")
          .append("\"phases\":[");
        boolean first = true;
        for (var p : MyPlanSteps.PHASES) {
            if (!first) sb.append(","); first = false;
            sb.append(serializePhase(p));
        }
        sb.append("]}");
        return sb.toString();
        // If you have decisions, add them here too — see RenameDataGetAction.java
    }

    private static String serializePhase(MyPlanSteps.Phase p) {
        StringBuilder sb = new StringBuilder();
        sb.append("{")
          .append("\"id\":").append(jstr(p.id())).append(",")
          .append("\"label\":").append(jstr(p.label())).append(",")
          .append("\"summary\":").append(jstr(p.summary())).append(",")
          .append("\"description\":").append(jstr(p.description())).append(",")
          .append("\"status\":").append(jstr(p.status().name())).append(",")
          .append("\"statusLabel\":").append(jstr(p.status().label)).append(",")
          .append("\"statusSlug\":").append(jstr(p.status().slug)).append(",")
          .append("\"progress\":").append(p.progressPercent()).append(",")
          .append("\"tasks\":[");
        boolean firstT = true;
        for (var t : p.tasks()) {
            if (!firstT) sb.append(","); firstT = false;
            sb.append("{\"description\":").append(jstr(t.description()))
              .append(",\"done\":").append(t.done()).append("}");
        }
        sb.append("],\"verification\":").append(jstr(p.verification())).append(",")
          .append("\"effort\":").append(jstr(p.effort())).append(",")
          .append("\"notes\":").append(jstr(p.notes()))
          .append("}");
        return sb.toString();
    }

    /** RFC 8259 string escape — handles backslash, quote, control chars. */
    private static String jstr(String v) {
        if (v == null) return "null";
        StringBuilder sb = new StringBuilder("\"");
        for (int i = 0; i < v.length(); i++) {
            char c = v.charAt(i);
            switch (c) {
                case '\\' -> sb.append("\\\\");
                case '"'  -> sb.append("\\\"");
                case '\n' -> sb.append("\\n");
                case '\r' -> sb.append("\\r");
                case '\t' -> sb.append("\\t");
                case '\b' -> sb.append("\\b");
                case '\f' -> sb.append("\\f");
                default -> {
                    if (c < 0x20) sb.append(String.format("\\u%04x", (int) c));
                    else sb.append(c);
                }
            }
        }
        sb.append("\"");
        return sb.toString();
    }

    private static ResourceNotFound notFound(String resource, String reason) {
        return new ResourceNotFound(
                new ResourceNotFound._InternalError(null, reason + ": " + resource),
                new ResourceNotFound._ExternalError(resource, reason)
        );
    }
}
```

If your data class also has `Decision` records, add a `serializeDecision(...)` helper and a `decisions` array in `serializeAll()` — copy the pattern from `RenameDataGetAction.java`.

**Verify** (after Step 3 wires the route, before Step 4):
```bash
mvn -pl <your-module> install -DskipTests
# Then start the server and check:
curl -s -o /dev/null -w "%{http_code}\n" "http://localhost:8080/my-plan-data"            # → 200
curl -s -o /dev/null -w "%{http_code}\n" "http://localhost:8080/my-plan-data?phase=01"   # → 200
curl -s "http://localhost:8080/my-plan-data" | head -c 100                               # JSON preview
```

---

### Step 3 — Wire the action into the registry

Find your project's `ActionRegistry` (in `homing-studio` it's `StudioActionRegistry.java`). Add three things: a final field, init-it in the constructor, register the route in `getActions()`:

```java
public class StudioActionRegistry implements ActionRegistry<RoutingContext> {

    private final HomingActionRegistry inner;
    private final DocContentGetAction docContentAction;
    private final StepDataGetAction stepDataAction;
    private final RenameDataGetAction renameDataAction;
    private final MyPlanDataGetAction myPlanDataAction;        // ← 1. new field

    public StudioActionRegistry(ModuleNameResolver nameResolver, Path docsRoot,
                                SimpleAppResolver appResolver) {
        this.inner = new HomingActionRegistry(nameResolver, appResolver);
        this.docContentAction = new DocContentGetAction(docsRoot);
        this.stepDataAction = new StepDataGetAction();
        this.renameDataAction = new RenameDataGetAction();
        this.myPlanDataAction = new MyPlanDataGetAction();      // ← 2. new init
    }

    @Override
    public Map<String, Action<RoutingContext, ?, ?, ?>> getActions() {
        var all = new HashMap<>(inner.getActions());
        all.put("/doc-content",  docContentAction);
        all.put("/step-data",    stepDataAction);
        all.put("/rename-data",  renameDataAction);
        all.put("/my-plan-data", myPlanDataAction);             // ← 3. new route
        return all;
    }
}
```

The route path (`/my-plan-data`) must exactly match what the JS view will `fetch(...)` in Step 5.

---

### Step 4 — Define the AppModules (`MyPlan.java`, `MyPlanStep.java`)

Two AppModules: one for the plan overview, one for a single phase. The pair gives you stable URLs (`/app?app=my-plan` and `/app?app=my-plan-step&phase=03`) and a clean back-navigation breadcrumb.

#### `MyPlan.java` — the overview view

```java
package com.example.myproject.studio.myplan;

import hue.captains.singapura.js.homing.core.*;
import hue.captains.singapura.js.homing.studio.css.StudioStyles;
import hue.captains.singapura.js.homing.studio.es.StudioCatalogue;

import java.util.List;

public record MyPlan() implements AppModule<MyPlan> {

    record appMain() implements AppModule._AppMain<MyPlan> {}
    public record link() implements AppLink<MyPlan> {}
    public static final MyPlan INSTANCE = new MyPlan();

    @Override public String title() { return "MyProject · my plan"; }

    @Override public ImportsFor<MyPlan> imports() {
        return ImportsFor.<MyPlan>builder()
                .add(new ModuleImports<>(List.of(new StudioCatalogue.link()), StudioCatalogue.INSTANCE))
                .add(new ModuleImports<>(List.of(new MyPlanStep.link()),     MyPlanStep.INSTANCE))
                .add(new ModuleImports<>(STUDIO_STYLES_BASELINE, StudioStyles.INSTANCE))
                .build();
    }

    @Override public ExportsOf<MyPlan> exports() {
        return new ExportsOf<>(INSTANCE, List.of(new appMain()));
    }

    /**
     * Baseline StudioStyles imports — the set actually used by the existing
     * RenamePlan/RFC0001Plan views. Use as-is initially; if your JS view
     * doesn't reference some of these, the Css conformance test will report
     * an unused import you can prune.
     */
    private static final List<CssClass<StudioStyles>> STUDIO_STYLES_BASELINE = List.of(
            new StudioStyles.st_root(), new StudioStyles.st_header(),
            new StudioStyles.st_brand(), new StudioStyles.st_brand_dot(), new StudioStyles.st_brand_word(),
            new StudioStyles.st_breadcrumbs(), new StudioStyles.st_crumb(), new StudioStyles.st_crumb_sep(),
            new StudioStyles.st_main(), new StudioStyles.st_kicker(), new StudioStyles.st_title(), new StudioStyles.st_subtitle(),
            new StudioStyles.st_section(), new StudioStyles.st_section_title(),
            new StudioStyles.st_overall_progress(), new StudioStyles.st_overall_bar(),
            new StudioStyles.st_overall_fill(), new StudioStyles.st_overall_pct(),
            new StudioStyles.st_step_card(), new StudioStyles.st_step_head(),
            new StudioStyles.st_step_id(), new StudioStyles.st_step_label(), new StudioStyles.st_step_summary(),
            new StudioStyles.st_step_progress(), new StudioStyles.st_step_progress_bar(), new StudioStyles.st_step_progress_fill(),
            new StudioStyles.st_step_meta(),
            new StudioStyles.st_status_badge(),
            new StudioStyles.st_status_not_started(), new StudioStyles.st_status_in_progress(),
            new StudioStyles.st_status_blocked(), new StudioStyles.st_status_done(),
            new StudioStyles.st_panel(), new StudioStyles.st_panel_title(),
            new StudioStyles.st_card(), new StudioStyles.st_card_title(), new StudioStyles.st_card_summary(),
            new StudioStyles.st_loading(), new StudioStyles.st_error(), new StudioStyles.st_footer()
    );
}
```

#### `MyPlanStep.java` — the detail view (one phase)

Two key additions vs. `MyPlan.java`:
1. A `Params` record + `paramsType()` override — this triggers the RFC 0001 generator to emit a typed JS helper `nav.MyPlanStep({phase: "03"})` and inject `params.phase` into the JS view.
2. `MarkedJs` in `imports()` — so the JS can call `marked.parse(...)` on multi-paragraph descriptions/notes. (Skip this if all your text is plain.)

```java
package com.example.myproject.studio.myplan;

import hue.captains.singapura.js.homing.core.*;
import hue.captains.singapura.js.homing.libs.MarkedJs;
import hue.captains.singapura.js.homing.studio.css.StudioStyles;
import hue.captains.singapura.js.homing.studio.es.StudioCatalogue;

import java.util.List;

public record MyPlanStep() implements AppModule<MyPlanStep> {

    record appMain() implements AppModule._AppMain<MyPlanStep> {}
    public record link() implements AppLink<MyPlanStep> {}

    /** Typed query parameter. Add fields here for any other query params. */
    public record Params(String phase) {}

    public static final MyPlanStep INSTANCE = new MyPlanStep();

    @Override public String title()          { return "MyProject · phase"; }
    @Override public Class<?> paramsType()   { return Params.class; }

    @Override public ImportsFor<MyPlanStep> imports() {
        return ImportsFor.<MyPlanStep>builder()
                .add(new ModuleImports<>(List.of(new StudioCatalogue.link()), StudioCatalogue.INSTANCE))
                .add(new ModuleImports<>(List.of(new MyPlan.link()),          MyPlan.INSTANCE))
                .add(new ModuleImports<>(List.of(new MyPlanStep.link()),      MyPlanStep.INSTANCE))   // self-link for prev/next nav
                .add(new ModuleImports<>(List.of(new MarkedJs.marked()),      MarkedJs.INSTANCE))     // markdown rendering
                .add(new ModuleImports<>(List.of(
                        new StudioStyles.st_root(), new StudioStyles.st_header(),
                        new StudioStyles.st_brand(), new StudioStyles.st_brand_dot(), new StudioStyles.st_brand_word(),
                        new StudioStyles.st_breadcrumbs(), new StudioStyles.st_crumb(), new StudioStyles.st_crumb_sep(),
                        new StudioStyles.st_main(), new StudioStyles.st_kicker(), new StudioStyles.st_title(), new StudioStyles.st_subtitle(),
                        new StudioStyles.st_section(), new StudioStyles.st_section_title(),
                        new StudioStyles.st_step_id(),
                        new StudioStyles.st_step_progress(), new StudioStyles.st_step_progress_bar(), new StudioStyles.st_step_progress_fill(),
                        new StudioStyles.st_status_badge(),
                        new StudioStyles.st_status_not_started(), new StudioStyles.st_status_in_progress(),
                        new StudioStyles.st_status_blocked(), new StudioStyles.st_status_done(),
                        new StudioStyles.st_panel(), new StudioStyles.st_panel_title(),
                        new StudioStyles.st_task_list(), new StudioStyles.st_task_item(),
                        new StudioStyles.st_task_done(), new StudioStyles.st_task_box(),
                        new StudioStyles.st_dep(), new StudioStyles.st_acceptance(), new StudioStyles.st_effort(),
                        new StudioStyles.st_doc(),
                        new StudioStyles.st_loading(), new StudioStyles.st_error()
                ), StudioStyles.INSTANCE))
                .build();
    }

    @Override public ExportsOf<MyPlanStep> exports() {
        return new ExportsOf<>(INSTANCE, List.of(new appMain()));
    }
}
```

**Verify** (after Step 4):
```bash
mvn -pl <your-module> install -DskipTests
# both classes should compile; if you see "cannot find symbol" for a StudioStyles record,
# check that you depend on homing-studio in your pom.xml.
```

---

### Step 5 — Write the JS views (`MyPlan.js`, `MyPlanStep.js`)

The JS files live at:
```
src/main/resources/homing/js/<package-mirror>/MyPlan.js
src/main/resources/homing/js/<package-mirror>/MyPlanStep.js
```
where `<package-mirror>` is the dot-to-slash mirror of the Java package, e.g. `com/example/myproject/studio/myplan/`. **The JS file path must match the Java class's package** — that's how the framework's resource resolver finds it.

#### Dialect convention

The codebase uses **ES6+ JS** throughout — `class`, `const`/`let`, arrow functions, template literals, etc. Reason: ES6 modules are the loading mechanism (the `import` statement is required for the framework to work at all), so every browser running the code already supports the rest. The framework auto-injects modern primitives (`nav.*`, `href.*`, `params.*`, `css.className(...)`); your code should never hand-build URLs or class-name strings.

#### `MyPlan.js` — overview view template

```javascript
// =============================================================================
// my-plan — overview view
// =============================================================================

function appMain(rootElement) {

    function cn() {
        var parts = [];
        for (var i = 0; i < arguments.length; i++) {
            if (arguments[i]) parts.push(css.className(arguments[i]));
        }
        return parts.join(" ");
    }

    function escape(s) {
        return String(s == null ? "" : s)
            .replace(/&/g, "&amp;").replace(/</g, "&lt;").replace(/>/g, "&gt;")
            .replace(/"/g, "&quot;").replace(/'/g, "&#39;");
    }

    function statusBadgeClass(slug) {
        switch (slug) {
            case "not-started": return st_status_not_started;
            case "in-progress": return st_status_in_progress;
            case "blocked":     return st_status_blocked;
            case "done":        return st_status_done;
            default:            return st_status_not_started;
        }
    }

    var shellHtml = ''
        + '<div class="' + cn(st_root) + '">'
        + '<div class="' + cn(st_header) + '">'
        + '  <a class="' + cn(st_brand) + '" ' + href.toAttr(nav.StudioCatalogue()) + '>'
        + '    <span class="' + cn(st_brand_dot) + '"></span>'
        + '    <span class="' + cn(st_brand_word) + '">studio</span>'
        + '  </a>'
        + '  <div class="' + cn(st_breadcrumbs) + '">'
        + '    <a class="' + cn(st_crumb) + '" ' + href.toAttr(nav.StudioCatalogue()) + '>Home</a>'
        + '    <span class="' + cn(st_crumb_sep) + '">/</span>'
        + '    <span class="' + cn(st_crumb) + '">My Plan</span>'
        + '  </div>'
        + '</div>'
        + '<div class="' + cn(st_main) + '">'
        + '  <div id="planRoot"><div class="' + cn(st_loading) + '">Loading…</div></div>'
        + '</div>'
        + '</div>';

    rootElement.innerHTML = shellHtml;
    var planRoot = document.getElementById("planRoot");

    fetch("/my-plan-data")
        .then(function(r) { if (!r.ok) throw new Error("HTTP " + r.status); return r.json(); })
        .then(function(d) { renderPlan(d); })
        .catch(function(err) {
            planRoot.innerHTML = '<div class="' + cn(st_error) + '">Failed to load: ' + escape(err.message) + '</div>';
        });

    function renderPlan(data) {
        var html = ''
            + '<div class="' + cn(st_kicker) + '">project tracker</div>'
            + '<h1 class="' + cn(st_title) + '">My Plan</h1>'
            + '<p class="' + cn(st_subtitle) + '">' + data.totalProgress + '% complete</p>'
            + '<div class="' + cn(st_overall_progress) + '">'
            + '  <div class="' + cn(st_overall_bar) + '">'
            + '    <div class="' + cn(st_overall_fill) + '" style="width:' + data.totalProgress + '%;"></div>'
            + '  </div>'
            + '  <span class="' + cn(st_overall_pct) + '">' + data.totalProgress + '%</span>'
            + '</div>'
            + '<div class="' + cn(st_section) + '">'
            + '  <div class="' + cn(st_section_title) + '">Phases</div>';

        for (var i = 0; i < data.phases.length; i++) {
            var p = data.phases[i];
            html += '<a class="' + cn(st_step_card) + '" ' + href.toAttr(nav.MyPlanStep({phase: p.id})) + '>'
                  +   '<div class="' + cn(st_step_head) + '">'
                  +     '<span class="' + cn(st_step_id) + '">' + escape(p.id) + '</span>'
                  +     '<span class="' + cn(st_step_label) + '">' + escape(p.label) + '</span>'
                  +     '<span class="' + cn(st_status_badge) + ' ' + cn(statusBadgeClass(p.statusSlug)) + '">' + escape(p.statusLabel) + '</span>'
                  +   '</div>'
                  +   '<div class="' + cn(st_step_summary) + '">' + escape(p.summary) + '</div>'
                  +   '<div class="' + cn(st_step_progress) + '">'
                  +     '<div class="' + cn(st_step_progress_bar) + '"><div class="' + cn(st_step_progress_fill) + '" style="width:' + p.progress + '%;"></div></div>'
                  +     '<span class="' + cn(st_step_meta) + '">' + p.progress + '% · ' + escape(p.effort) + '</span>'
                  +   '</div>'
                  + '</a>';
        }

        html += '</div>';
        planRoot.innerHTML = html;
    }
}
```

#### `MyPlanStep.js` — detail view (single phase, with markdown)

The framework injects `params.phase` from the URL. If `MarkedJs` is in `imports()`, an `import { marked } from <MarkedJs>` line is auto-prepended at serve time, and you can call `marked.parse(...)`.

```javascript
// =============================================================================
// my-plan — phase detail view
// =============================================================================

function appMain(rootElement) {

    function cn() {
        var parts = [];
        for (var i = 0; i < arguments.length; i++) {
            if (arguments[i]) parts.push(css.className(arguments[i]));
        }
        return parts.join(" ");
    }

    function escape(s) {
        return String(s == null ? "" : s)
            .replace(/&/g, "&amp;").replace(/</g, "&lt;").replace(/>/g, "&gt;")
            .replace(/"/g, "&quot;").replace(/'/g, "&#39;");
    }

    function statusBadgeClass(slug) {
        switch (slug) {
            case "not-started": return st_status_not_started;
            case "in-progress": return st_status_in_progress;
            case "blocked":     return st_status_blocked;
            case "done":        return st_status_done;
            default:            return st_status_not_started;
        }
    }

    var phaseId = params.phase || "";

    var shellHtml = ''
        + '<div class="' + cn(st_root) + '">'
        + '<div class="' + cn(st_header) + '">'
        + '  <div class="' + cn(st_breadcrumbs) + '">'
        + '    <a class="' + cn(st_crumb) + '" ' + href.toAttr(nav.StudioCatalogue()) + '>Home</a>'
        + '    <span class="' + cn(st_crumb_sep) + '">/</span>'
        + '    <a class="' + cn(st_crumb) + '" ' + href.toAttr(nav.MyPlan()) + '>My Plan</a>'
        + '    <span class="' + cn(st_crumb_sep) + '">/</span>'
        + '    <span class="' + cn(st_crumb) + '">Phase ' + escape(phaseId) + '</span>'
        + '  </div>'
        + '</div>'
        + '<div class="' + cn(st_main) + '">'
        + '  <div id="phaseRoot"><div class="' + cn(st_loading) + '">Loading phase…</div></div>'
        + '</div>'
        + '</div>';

    rootElement.innerHTML = shellHtml;
    var phaseRoot = document.getElementById("phaseRoot");

    if (!phaseId) {
        phaseRoot.innerHTML = '<div class="' + cn(st_error) + '">No phase id specified. Use <code>?phase=…</code>.</div>';
        return;
    }

    fetch("/my-plan-data?phase=" + encodeURIComponent(phaseId))
        .then(function(r) { if (!r.ok) throw new Error("HTTP " + r.status); return r.json(); })
        .then(function(p) { renderPhase(p); })
        .catch(function(err) {
            phaseRoot.innerHTML = '<div class="' + cn(st_error) + '">Failed: ' + escape(err.message) + '</div>';
        });

    function renderPhase(p) {
        // Use marked when present (auto-injected via MyPlanStep.imports()); fall back to escaped text
        var descHtml = (typeof marked !== "undefined" && marked.parse)
            ? marked.parse(p.description || "")
            : "<p>" + escape(p.description || "") + "</p>";

        var tasksHtml = p.tasks.length
            ? '<ul class="' + cn(st_task_list) + '">'
              + p.tasks.map(function(t) {
                    var doneCls = t.done ? " " + cn(st_task_done) : "";
                    var box = '<span class="' + cn(st_task_box) + '">' + (t.done ? "✓" : "") + '</span>';
                    return '<li class="' + cn(st_task_item) + doneCls + '">' + box + '<span>' + escape(t.description) + '</span></li>';
                }).join("")
              + '</ul>'
            : '<p style="font-style:italic;">(no tasks declared)</p>';

        var html = ''
            + '<div class="' + cn(st_kicker) + '">Phase ' + escape(p.id) + '</div>'
            + '<h1 class="' + cn(st_title) + '">' + escape(p.label) + '</h1>'
            + '<p class="' + cn(st_subtitle) + '">' + escape(p.summary) + '</p>'
            + '<div style="display:flex;gap:14px;align-items:center;margin:16px 0 24px;">'
            + '  <span class="' + cn(st_status_badge) + ' ' + cn(statusBadgeClass(p.statusSlug)) + '">' + escape(p.statusLabel) + '</span>'
            + '  <span class="' + cn(st_effort) + '">est. ' + escape(p.effort) + '</span>'
            + '</div>'
            + '<div class="' + cn(st_panel) + '">'
            + '  <div class="' + cn(st_panel_title) + '">Description</div>'
            + '  <div class="' + cn(st_doc) + '">' + descHtml + '</div>'
            + '</div>'
            + '<div class="' + cn(st_panel) + '">'
            + '  <div class="' + cn(st_panel_title) + '">Tasks</div>'
            +    tasksHtml
            + '</div>'
            + '<div class="' + cn(st_panel) + '">'
            + '  <div class="' + cn(st_panel_title) + '">Verification gate</div>'
            + '  <div class="' + cn(st_acceptance) + '">' + escape(p.verification) + '</div>'
            + '</div>';

        phaseRoot.innerHTML = html;
    }
}
```

**Verify** (after Step 5, before catalogue wiring):
```bash
mvn -pl <your-module> install -DskipTests
# Restart the studio server. Then in the browser DevTools network tab:
#   1. Open /app?app=my-plan      — should render header + phase cards
#   2. Click a phase card         — should navigate to /app?app=my-plan-step&phase=01
#   3. Network tab: /my-plan-data and /my-plan-data?phase=01 both 200, application/json
```

---

### Step 6 — Register in the catalogue

Two places — both required:

**`StudioCatalogue.java`** — add the import declaration so the framework knows to wire `nav.MyPlan()` and `nav.MyPlanStep(...)` into the catalogue's generated nav:

```java
// inside StudioCatalogue.imports(), alongside the other .add(...) calls:
.add(new ModuleImports<>(List.of(new MyPlan.link()),     MyPlan.INSTANCE))
.add(new ModuleImports<>(List.of(new MyPlanStep.link()), MyPlanStep.INSTANCE))
```

**`StudioCatalogue.js`** — add a tile to the `apps` array so users can click into the tracker from Home:

```javascript
{
    link:     function() { return nav.MyPlan(); },
    label:    "My Plan",
    desc:     "Live tracker for the migration. Edit MyPlanSteps.java to revise.",
    icon:     "M",
    featured: false
}
```

---

### Step 7 — Update the conformance allow-lists

Three test classes need the new modules added to their lists. **All three are required** — missing any one means the scanner skips your tracker, and a regression can sneak in later.

#### `StudioCssConformanceTest.domModules()` — for CSS class-name discipline
```java
import com.example.myproject.studio.myplan.MyPlan;
import com.example.myproject.studio.myplan.MyPlanStep;

return List.of(
        // … existing entries …
        MyPlan.INSTANCE,
        MyPlanStep.INSTANCE
);
```

#### `StudioHrefConformanceTest.domModules()` — for href usage discipline
Same edit, same imports.

#### `StudioCdnFreeConformanceTest.esModules()` — for the no-CDN rule
```java
import com.example.myproject.studio.myplan.MyPlan;
import com.example.myproject.studio.myplan.MyPlanStep;

return List.of(
        // … existing entries …
        MyPlan.INSTANCE,
        MyPlanStep.INSTANCE
);
```

This third test was added with the bundled-libs work and ensures no future change reintroduces a `https://...` import in your tracker JS files.

**Verify** (final):
```bash
mvn install
# Look for these test counts in the output:
#   StudioCssConformanceTest      — N tests, 0 failures
#   StudioHrefConformanceTest     — N tests, 0 failures
#   StudioCdnFreeConformanceTest  — N tests, 0 failures
# (N grows by 1 per concrete module you added.)
```

---

## Themes (RFC 0002 + ext1)

If your tracker app reuses studio-base's `StudioStyles`, you get the default visual identity for free — `homing-studio` ships `HomingDefault` as a fully-populated theme. You don't need to do anything.

If you want to add a *new* theme (brand variant, dark mode, etc.), or you've defined your own `CssGroup` and need to provide its CSS, follow the shape below. The full design is in [RFC 0002-ext1](#ref:rfc-2-ext1); the short version:

**1. Declare the theme** — a record implementing `Theme` (identity only — `slug()` + optional `label()`):

```java
public record HomingDark() implements Theme {
    public static final HomingDark INSTANCE = new HomingDark();
    @Override public String slug()  { return "homing-dark"; }
    @Override public String label() { return "Homing dark"; }

    public record Vars() implements ThemeVariables<HomingDark> {
        public static final Vars INSTANCE = new Vars();
        @Override public HomingDark theme() { return HomingDark.INSTANCE; }
        @Override public Map<CssVar, String> values() { return VALUES; }
        private static final Map<CssVar, String> VALUES = Map.ofEntries(
                Map.entry(StudioVars.ST_OFFWHITE,  "#0F1320"),
                Map.entry(StudioVars.ST_GRAY_DK,   "#E2E8F0"),
                // … every CssVar the studio's classes reference
        );
    }

    public record Globals() implements ThemeGlobals<HomingDark> {
        public static final Globals INSTANCE = new Globals();
        @Override public HomingDark theme() { return HomingDark.INSTANCE; }
        @Override public String css() { return ""; }   // optional escape hatch
    }
}
```

**2. Register the theme** in your deployment's `ThemeRegistry` (e.g. `StudioThemeRegistry`):

```java
@Override public List<Theme>              themes()    { return List.of(HomingDefault.INSTANCE, HomingDark.INSTANCE); }
@Override public List<ThemeVariables<?>>  variables() { return List.of(HomingDefault.Vars.INSTANCE,    HomingDark.Vars.INSTANCE); }
@Override public List<ThemeGlobals<?>>    globals()   { return List.of(HomingDefault.Globals.INSTANCE, HomingDark.Globals.INSTANCE); }
```

That's it. The framework serves `/theme-vars?theme=homing-dark` and `/theme-globals?theme=homing-dark` automatically; the browser cascade switches between themes via the `?theme=<slug>` URL parameter.

**For a brand-new CssGroup of your own:** declare CssClass records and put their CSS body inline via `body()`. Reference theme tokens via `var(--name)` for theme-varying values:

```java
public record MyStyles() implements CssGroup<MyStyles> {
    public static final MyStyles INSTANCE = new MyStyles();

    public record my_card() implements CssClass<MyStyles> {
        @Override public String body() {
            return "background: var(--color-surface-raised); padding: var(--space-4);";
        }
    }
    // … more records, each with body() inline
}
```

No per-(group, theme) impl class needed. Theme variation enters the cascade through the variables layer.

---

## Utility composition (RFC 0002-ext1)

`homing-studio-base` ships a small `Util` group with hand-curated utilities — colour/visual bases (`bg_accent`, `color_link`, `border_emphasis` etc.) plus layout primitives (`p_*`, `m_*`, `gap_*`, `flex`, `grid`). Color/visual utilities expose `:hover` / `:focus` / `:active` variants automatically.

**Tailwind-style call sites:**

```js
// hover state on a phase dependency badge:
return '<a class="' + cn(st_dep, border_emphasis.hover) + '" ' + href.toAttr(...) + '>';
```

`base.hover` (no parens — variants are precomputed `CssClass` instances) renders to the auto-synthesized `.hover-border-emphasis:hover { … }` rule. Other variant properties (`.focus`, `.active`) work the same way for utilities that opt in via `UtilityCssClass<G>`.

**Defining your own opt-in utility group:**

```java
public final class Util implements CssGroup<Util> {
    public static final Util INSTANCE = new Util();

    public record bg_accent() implements UtilityCssClass<Util> {     // gets hover/focus/active
        @Override public String body() { return "background: var(--color-accent);"; }
    }
    public record p_4() implements CssClass<Util> {                  // no variants
        @Override public String body() { return "padding: var(--space-4);"; }
    }
    // …
}
```

`UtilityCssClass<G>` is shorthand for "give me hover + focus + active variants automatically." Pure layout helpers (display, padding, margin) use plain `CssClass<G>` since they don't need state variants.

---

## Workflow once it's wired

1. **Open** `/app?app=my-plan` in the browser.
2. **Edit** `MyPlanSteps.java` — flip a `Task("…", false)` to `Task("…", true)`, change a `Status`, resolve a `Decision`, add a new task.
3. **Recompile** the studio module (`mvn -pl homing-studio install -DskipTests`).
4. **Restart** the studio server (or rely on `homing.devRoot` live-reload for JS/CSS — Java still needs a recompile + restart).
5. **Refresh** the browser. The tracker now shows the new state.

The whole loop is sub-30-second. For long plans, this is faster than maintaining a separate todo doc.

---

## What you commit

Every plan revision = one commit. The data class is your changelog:

```
git log --follow homing-studio/src/main/java/.../rename/RenameSteps.java
```

…shows you every status change, task addition, and decision resolution as a separate commit. That's the audit trail.

---

## Strengths and trade-offs

**Strengths**
- One file = one source of truth. No drift between code and plan.
- Plan changes are PR-reviewable.
- Tracker IS an AppModule, so it proves the framework end-to-end.
- Sub-second iteration loop on JS/CSS via live-reload.

**Trade-offs**
- Java recompile required for data-class changes (~3s; not a real cost in practice).
- No multi-user edit story — this is single-engineer or merge-via-PR.
- No notifications, no Slack integration, no burndown chart — it's a viewer, not a full PM tool.
- Requires Homing as a runtime dependency.

If those trade-offs don't fit, you probably want a real project tracker — but for "I'm executing a plan and I want to see live state without leaving my editor," this pattern is hard to beat.

---

## Common pitfalls

Stuff we actually hit while building the existing trackers — worth scanning before you commit:

1. **`Class.forName()` and nested classes.** If you ever load a tracker class by name (e.g. in a test fixture), use `getName()` not `getCanonicalName()` — the binary name uses `$` for nested classes, which `Class.forName` requires. `getCanonicalName()` returns dots and silently fails to resolve.

2. **Forgotten `StudioStyles` import = silent CSS fail.** If a class your JS calls via `cn(st_panel_title)` isn't listed in your AppModule's `imports()`, `css.className(...)` returns the raw record name and the styling silently breaks. The CSS conformance test catches *unused* imports but not *missing* ones — visual smoke is the real gate.

3. **Same package mirror for Java + JS.** The framework's resource resolver maps `com.example.myproject.studio.myplan.MyPlan` → `homing/js/com/example/myproject/studio/myplan/MyPlan.js`. If you put the JS file in the wrong directory, the request returns `ResourceNotFound` with no helpful pointer.

4. **Don't write your own `import` line in the JS view.** The framework auto-prepends every `import` declared in your AppModule's `imports()` method. If you also write `import { marked } from "..."` in the JS file, you'll get duplicate-declaration errors at module evaluation time. Just call `marked.parse(...)` — the import is injected for you.

5. **`params.<name>` is always a `String`** in the JS view, even if your Java `Params` record declares `int` or `boolean`. Cast/parse on the JS side: `var phase = parseInt(params.phase, 10) || 0;`.

6. **Markdown rendering needs `MarkedJs.INSTANCE` *and* `new MarkedJs.marked()`** in the AppModule's `imports()`. The first names the module being imported from; the second names the symbol to import. Skipping either means `marked` is `undefined` at runtime — your JS should fall back gracefully (`typeof marked !== "undefined" && marked.parse`).

7. **Live-reload is JS/CSS only.** `homing.devRoot=<path>` lets you edit `MyPlan.js` and refresh — instant. But editing `MyPlanSteps.java` or `MyPlanDataGetAction.java` requires a recompile (`mvn -pl <module> install -DskipTests`) and a server restart. The whole loop is still ~5 seconds.

---

## Reviewer checklist

Before merging a new tracker app, verify:

- [ ] `MyPlanSteps.java` compiles standalone (no imports outside `java.util`).
- [ ] `MyPlanSteps.phaseById(id)` returns the expected `Phase` for valid ids and `null` for unknown ids.
- [ ] `MyPlanDataGetAction` returns valid JSON for `GET /my-plan-data` and `GET /my-plan-data?phase=01`; returns 4xx for `?phase=99` (unknown id).
- [ ] `MyPlan.imports()` lists every `StudioStyles.*` class the view's JS references.
- [ ] `MyPlanStep` has a `Params` record, a `paramsType()` override returning `Params.class`, and `MarkedJs.INSTANCE` in `imports()` if the view renders any markdown.
- [ ] JS files live at `src/main/resources/homing/js/<package-mirror>/MyPlan{,Step}.js` (paths exactly mirror the Java packages).
- [ ] `MyPlan.js` and `MyPlanStep.js` use only `nav.*`, `href.toAttr(...)`, `params.*`, `css.className(...)` — no hand-built URLs or raw class strings, no `import` statements (those are auto-injected).
- [ ] `StudioActionRegistry` has the new field, init, and `getActions()` route entry.
- [ ] `StudioCatalogue.java`'s `imports()` includes `new MyPlan.link()` AND `new MyPlanStep.link()`.
- [ ] `StudioCatalogue.js` has the new tile in the `apps` array.
- [ ] All **three** conformance tests have the new modules added: `StudioCssConformanceTest`, `StudioHrefConformanceTest`, `StudioCdnFreeConformanceTest`.
- [ ] `mvn install` is green; no allow-list exceptions added.
- [ ] Browser smoke: `/app?app=my-plan` renders cards, clicking a card navigates to `/app?app=my-plan-step&phase=01`, and the detail page renders without console errors.

---

## Reference implementations

- **`homing-studio/src/main/java/.../studio/rename/`** — the rename tracker that ran the japjs → Homing migration this guide is itself part of. Includes `Decision`s alongside `Phase`s.
- **`homing-studio/src/main/java/.../studio/rfc0001/`** — the RFC 0001 implementation tracker. Includes per-step acceptance criteria.

Both are < 500 lines each end-to-end. Copy whichever shape is closest to your plan and adapt.
