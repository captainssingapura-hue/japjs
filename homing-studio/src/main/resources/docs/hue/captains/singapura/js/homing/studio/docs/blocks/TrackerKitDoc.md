# Tracker Kit — `PlanAppModule` + `PlanStepAppModule`

A two-page kit for any multi-phase plan tracker (RFC implementation, migration, audit, rollout). The data file is the source of truth; the views are auto-generated. The kit closes [Defect 0001](#ref:def-1).

**Where**: `homing-studio-base/.../base/tracker/`
- Data shapes: `Plan` (interface), `Phase`, `Task`, `Decision`, `Dependency`, `Metric`, `PhaseStatus`, `DecisionStatus`.
- `PlanAppModule<M>` — generic AppModule for the index page.
- `PlanStepAppModule<M>` — generic AppModule for the per-phase detail page.
- `PlanRenderer.java` + `PlanRenderer.js` — `renderPlan` / `renderStep` functions.
- `PlanJson.java` — Plan → JS literal.

---

## Data shape

```java
public interface Plan {
    String kicker();          // "project rename"
    String title();           // "japjs → Homing"
    String subtitle();        // lede paragraph
    int    totalProgress();   // 0..100
    int    openDecisions();   // count of decisions in OPEN state
    String executionDoc();    // optional companion doc path; null = no link
    String dossierDoc();      // optional secondary doc path
    List<Phase>    phases();
    List<Decision> decisions();
}
```

```java
public record Phase(
        String id, String label, String summary, String description,
        PhaseStatus status,
        List<Task> tasks,
        List<Dependency> dependsOn,
        String verification, String rollback, String effort, String notes,
        List<Metric> metrics    // empty list if your plan doesn't quantify
) { /* progressPercent() helper */ }

public record Task(String description, boolean done) {}
public record Dependency(String phaseId, String reason) {}
public record Decision(String id, String question, String recommendation,
                       String chosenValue, DecisionStatus status,
                       String rationale, String notes) {}
public record Metric(String label, String before, String after, String delta) {}
```

---

## Concrete tracker — three Java files, no JS

**1. `MyPlanData.java`** — adapter implementing `Plan` over your project's source-of-truth data file:

```java
public final class MyPlanData implements Plan {
    public static final MyPlanData INSTANCE = new MyPlanData();

    @Override public String kicker()        { return "RFC 9999"; }
    @Override public String title()         { return "My Plan"; }
    @Override public String subtitle()      { return "Source of truth: MyPlanSteps.java."; }
    @Override public int    totalProgress() { return MyPlanSteps.totalProgressPercent(); }
    @Override public int    openDecisions() { return 0; }
    @Override public String executionDoc()  { return null; }
    @Override public String dossierDoc()    { return null; }
    @Override public List<Phase> phases()       { return MyPlanSteps.PHASES.stream().map(this::adapt).toList(); }
    @Override public List<Decision> decisions() { return List.of(); }
    // adapt() maps your local records to the kit's shapes
}
```

**2. `MyPlan.java`** — index page:

```java
public record MyPlan() implements PlanAppModule<MyPlan> {
    record appMain() implements AppModule._AppMain<MyPlan> {}
    public record link() implements AppLink<MyPlan> {}
    public static final MyPlan INSTANCE = new MyPlan();

    @Override public Plan   plan()              { return MyPlanData.INSTANCE; }
    @Override public String stepAppSimpleName() { return MyStep.INSTANCE.simpleName(); }

    @Override public ImportsFor<MyPlan> imports() {
        return ImportsFor.<MyPlan>builder()
                .add(new ModuleImports<>(List.of(new MyStep.link()),     MyStep.INSTANCE))
                .add(new ModuleImports<>(List.of(new PlanRenderer.renderPlan()), PlanRenderer.INSTANCE))
                .build();
    }

    @Override public ExportsOf<MyPlan> exports() {
        return new ExportsOf<>(INSTANCE, List.of(new appMain()));
    }
}
```

**3. `MyStep.java`** — per-phase detail page (mostly identical with `Params(String phase)` + `PlanStepAppModule`).

**Total: ~190 LoC across three files** for a fully working tracker. The four existing trackers in `homing-studio` (Rename, Rfc0001, Rfc0002, Rfc0002-ext1) follow this exact shape.

---

## Renderer features

- **Index page**: header + breadcrumbs, kicker / title / subtitle, overall progress bar, open-decisions section (cards), phase grid (step-cards linking to detail page), optional doc-link footer.
- **Step page**: header with breadcrumb chain, status badge + effort + percent, description prose, Tasks panel (`TodoList`), optional Metrics panel (`MetricsTable`), Dependencies panel, Verification / Rollback / Notes panels, prev / next nav.

Per-phase `metrics()` enables before/after measurement display — useful for cleanup / refactor / migration plans where the value is the quantitative delta. Empty list = panel hidden.

---

## See also

- [Atoms — StudioElements](#ref:atoms) — the tracker chrome (`StatusBadge`, `OverallProgress`, `StepCard`, `DecisionCard`, `TodoList`, `MetricsTable`, `Panel`).
- [Defect 0001](#ref:def-1) — the duplication problem this kit resolves; §8 has the full migration story.
- [Bootstrap & Conformance](#ref:bac) — registering trackers with `StudioBootstrap.start(...)`.
