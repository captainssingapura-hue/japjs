package hue.captains.singapura.js.homing.studio.rfc0005ext1;

import hue.captains.singapura.js.homing.studio.docs.rfcs.Rfc0005Ext1Doc;

import java.util.List;

/**
 * Implementation tracker for RFC 0005-ext1 — Typed Plan Containers.
 *
 * <p>Companion document: {@link Rfc0005Ext1Doc} (typed reference, UUID-stable).
 * Mirrors the {@code Rfc0005Steps} layout — same Status / DecisionStatus /
 * Task / Dependency / Decision / Phase / Metric records.</p>
 */
public final class Rfc0005Ext1Steps {

    public enum Status {
        NOT_STARTED("Not started", "not-started"),
        IN_PROGRESS("In progress", "in-progress"),
        BLOCKED("Blocked", "blocked"),
        DONE("Done", "done");

        public final String label;
        public final String slug;
        Status(String label, String slug) { this.label = label; this.slug = slug; }
    }

    public enum DecisionStatus {
        OPEN("Open", "open"),
        RESOLVED("Resolved", "resolved");

        public final String label;
        public final String slug;
        DecisionStatus(String label, String slug) { this.label = label; this.slug = slug; }
    }

    public record Task(String description, boolean done) {}

    public record Dependency(String phaseId, String reason) {}

    public record Decision(
            String id,
            String question,
            String recommendation,
            String chosenValue,
            DecisionStatus status,
            String rationale,
            String notes
    ) {}

    public record Phase(
            String id,
            String label,
            String summary,
            String description,
            Status status,
            List<Task> tasks,
            List<Dependency> dependsOn,
            String verification,
            String rollback,
            String effort,
            String notes,
            List<Metric> metrics
    ) {
        public int progressPercent() {
            if (tasks.isEmpty()) return 0;
            long done = tasks.stream().filter(Task::done).count();
            return (int) (done * 100 / tasks.size());
        }
    }

    public record Metric(String label, String before, String after, String delta) {}

    /** Typed reference to RFC 0005-ext1's prose. UUID-stable per RFC 0004. */
    public static final String RFC_DOC = "93605dda-c709-4d91-9a84-91d8f81c28f2"; // Rfc0005Ext1Doc.ID

    // ------------------------------------------------------------------
    // RESOLVED DECISIONS — captured during the design conversation that
    // produced the RFC.
    // ------------------------------------------------------------------
    public static final List<Decision> DECISIONS = List.of(

            new Decision("D1",
                    "Acceptance shape — single string narrative or list of typed criteria?",
                    "List of typed `Acceptance(label, description, met)` records. Each phase carries its own; cross-phase references are well supported.",
                    "List<Acceptance> per plan + per phase",
                    DecisionStatus.RESOLVED,
                    "A list keeps each acceptance criterion individually checkable — `met` is a boolean per item, not an opinion buried in prose. Per-phase ownership matches how plans naturally evolve (each phase ships its own outcomes); cross-phase references are handled at the Plan level when needed.",
                    "Captured in RFC §4 Acceptance pillar."
            ),

            new Decision("D2",
                    "Per-phase metrics — keep the Metric record from the legacy Plan kit, or drop?",
                    "Keep. The Metric(label, before, after, delta) shape already exists and is consumed by the renderer; no reason to drop.",
                    "keep Metric record",
                    DecisionStatus.RESOLVED,
                    "Metrics are optional per phase. The legacy shape is good and the renderer already handles it. No need to invent something new in this RFC.",
                    "Captured in RFC §5 Phase shape."
            ),

            new Decision("D3",
                    "Three-pillar enforcement — runtime check or compiler-enforced?",
                    "Compiler-enforced. `Plan` declares `decisions()`, `phases()`, `acceptance()` as abstract; concrete plans MUST implement all three or fail to compile.",
                    "abstract methods on Plan interface",
                    DecisionStatus.RESOLVED,
                    "Compiler enforcement is strictly stronger than runtime / conformance enforcement — the framework cannot be misused by accident. Anything not expressible at the compiler level (e.g. acceptance non-emptiness for a 'finished' plan) falls to conformance tests.",
                    "Captured in RFC §3 Open vs Closed."
            ),

            new Decision("D4",
                    "`Decision` model — keep RFC-specific shape or promote to a first-class plan primitive?",
                    "Promote. Decisions belong on the Plan interface alongside phases and acceptance — they are one of the three pillars.",
                    "first-class Plan.decisions()",
                    DecisionStatus.RESOLVED,
                    "Decisions are the 'questions' pillar — every meaningful plan tracks open questions and their resolutions. Promoting them to a first-class concept matches the doctrine and removes per-tracker boilerplate.",
                    "Captured in RFC §4 Questions pillar."
            )
    );

    // ------------------------------------------------------------------
    // PHASES — execute in order, marking DONE as work completes.
    // ------------------------------------------------------------------
    public static final List<Phase> PHASES = List.of(

            new Phase("01",
                    "Framework — `Plan` interface promotion + `Acceptance` record",
                    "Promote `Plan` from a thin adapter shape to the typed three-pillar interface. Add `Acceptance` record. Compile-only landing.",
                    "Reshaped `Plan` in `homing-studio-base/.../base/tracker/`: abstract pillar methods `name() + decisions() + phases() + acceptance()` (was previously a thin adapter shape with no compile-time pillar contract). Added defaults: `summary, kicker, subtitle, executionDoc, dossierDoc, totalProgress, openDecisions, acceptanceMet, phaseById`. Created `Acceptance(String label, String description, boolean met)` record alongside the existing `Phase` / `Decision` / `Task` / `Dependency` / `Metric` records.",
                    Status.DONE,
                    List.of(
                            new Task("Add `Acceptance(label, description, met)` record", true),
                            new Task("Reshape `Plan` interface — abstract `name() + decisions() + phases() + acceptance()`", true),
                            new Task("Default helpers: `totalProgress`, `openDecisions`, `acceptanceMet`, `phaseById`", true),
                            new Task("`mvn install -pl homing-studio-base` GREEN", true)
                    ),
                    List.of(),
                    "homing-studio-base compiles; new `Plan` shape reachable from downstream.",
                    "Revert Plan + delete Acceptance.",
                    "30 minutes",
                    "",
                    List.of()
            ),

            new Phase("02",
                    "`PlanAppHost` + `PlanGetAction` — single AppModule + JSON endpoint",
                    "URL contract: `/app?app=plan&id=<class-fqn>` (HTML bootstrap) + `/plan?id=<fqn>` (JSON payload). Mirrors `CatalogueAppHost` from RFC 0005.",
                    "Created `PlanAppHost` (AppModule + SelfContent, simpleName=`plan`) and `PlanGetAction` (`/plan?id=<class-fqn>`) in `homing-studio-base/.../base/tracker/`. AppHost's `Params(String id, String phase)` carries the plan class FQN plus optional phase id; one URL serves both index and step views (renderer dispatches on `phase`). PlanGetAction resolves the class via PlanRegistry and serialises the full payload (brand, breadcrumbs, name, summary, decisions, phases, acceptance, per-phase tasks/deps/metrics) as JSON. `urlFor(Class)` + `urlFor(Class, phaseId)` static helpers for typed link construction.",
                    Status.DONE,
                    List.of(
                            new Task("Created `PlanAppHost` record (AppModule + SelfContent, simpleName=plan)", true),
                            new Task("`Params(String id, String phase)` — id required, phase optional", true),
                            new Task("`urlFor(Class)` + `urlFor(Class, phaseId)` static helpers", true),
                            new Task("Created `PlanGetAction` serving `/plan?id=<fqn>` JSON payload", true),
                            new Task("Per-pillar serialization: decisions / phases (with tasks+deps+metrics) / acceptance", true),
                            new Task("Build green; AppHost ready to serve once registered", true)
                    ),
                    List.of(new Dependency("01", "Plan interface shape.")),
                    "AppHost + GetAction wired; awaiting bootstrap registration.",
                    "Delete PlanAppHost + PlanGetAction.",
                    "45 minutes",
                    "",
                    List.of()
            ),

            new Phase("03",
                    "`PlanRegistry` + boot-time validations",
                    "Boot-time registry construction performs 8 validations (class uniqueness, name non-blank, phase id uniqueness within plan, decision id uniqueness within plan, status non-null, dependency targets resolve, executionDoc reachable, dossierDoc reachable when set).",
                    "Created `PlanRegistry` in `homing-studio-base/.../base/tracker/`. Constructor takes (Collection<Plan>, DocRegistry). Walks every registered plan, building class → plan lookup map. Validations: (1) plan class registered at most once, (2) name non-blank, (3) phase ids unique within plan, (4) decision ids unique within plan, (5) phase status non-null, (6) every dependsOn target resolves to a phase id in the same plan, (7) executionDoc UUID exists in DocRegistry when set, (8) dossierDoc UUID exists in DocRegistry when set. `resolve(Class)` lookup method.",
                    Status.DONE,
                    List.of(
                            new Task("Created `PlanRegistry(Collection<Plan>, DocRegistry)`", true),
                            new Task("Built class → plan lookup map", true),
                            new Task("Validate class uniqueness, name non-blank", true),
                            new Task("Validate phase id + decision id uniqueness within plan", true),
                            new Task("Validate phase status non-null", true),
                            new Task("Validate dependency targets resolve to known phase ids", true),
                            new Task("Validate executionDoc + dossierDoc UUIDs are in DocRegistry", true)
                    ),
                    List.of(new Dependency("01", "Plan interface shape.")),
                    "Registry construction succeeds for valid plans, throws clearly for each violation.",
                    "Delete PlanRegistry.",
                    "1 hour",
                    "",
                    List.of()
            ),

            new Phase("04",
                    "`PlanHostRenderer` (Java + JS) — single renderer for index + step views",
                    "Renderer dedicated to `PlanAppHost`. One JS file, dispatches index vs step view based on the `phase` param.",
                    "Created `PlanHostRenderer.java` (DomModule with imports for HrefManager + StudioElements + StudioStyles) and `PlanHostRenderer.js`. Single fetch to `/plan?id=`. JS reads server-pre-resolved JSON (brand, breadcrumbs, name, summary, decisions, phases, acceptance, per-phase data). Dispatches: if `params.phase` is set → step view (single phase + its tasks/deps/metrics); else → index view (3-pillar overview: questions / phases-summary / acceptance). Replaces both legacy `PlanRenderer` (index) and `PlanStepRenderer` (step) with one cohesive module.",
                    Status.DONE,
                    List.of(
                            new Task("Created `PlanHostRenderer.java` (imports HrefManager + StudioElements + StudioStyles)", true),
                            new Task("Created `PlanHostRenderer.js` — single fetch + dispatch", true),
                            new Task("Index view: 3-pillar layout (questions / phases / acceptance)", true),
                            new Task("Step view: phase header + tasks + deps + metrics", true),
                            new Task("Breadcrumb + brand from server-resolved payload", true),
                            new Task("Build green", true)
                    ),
                    List.of(
                            new Dependency("02", "AppHost is the consumer."),
                            new Dependency("03", "Registry provides the data.")
                    ),
                    "Renderer + AppHost pair functional once a downstream registers plans.",
                    "Delete PlanHostRenderer.java + .js.",
                    "1.5 hours",
                    "",
                    List.of()
            ),

            new Phase("05",
                    "`Entry.OfPlan` — fourth sealed Entry subtype",
                    "Add `OfPlan(Plan)` to the sealed `Entry` interface so JourneysCatalogue can list Plans typed alongside Doc / Catalogue / NavigableApp entries.",
                    "Extended sealed `Entry` interface in `homing-studio-base/.../base/app/` with new permitted subtype `OfPlan(Plan)` plus `Entry.of(Plan)` static factory. CatalogueGetAction's pattern-match switch updated to handle the new branch — emits an entry with kind=`plan`, server-resolved URL via `PlanAppHost.urlFor(plan.getClass())`. CatalogueRegistry's doc-reachability validation extended: for OfPlan entries, validates executionDoc + dossierDoc UUIDs are in the DocRegistry (so a plan referenced by a catalogue cannot dangle).",
                    Status.DONE,
                    List.of(
                            new Task("Added `OfPlan(Plan)` to sealed Entry permits", true),
                            new Task("Added `Entry.of(Plan)` static factory", true),
                            new Task("CatalogueGetAction switch handles OfPlan branch — kind=plan", true),
                            new Task("Server-resolved URL via PlanAppHost.urlFor(plan.getClass())", true),
                            new Task("Build green", true)
                    ),
                    List.of(
                            new Dependency("02", "PlanAppHost.urlFor() helper."),
                            new Dependency("03", "PlanRegistry exists to resolve plan classes.")
                    ),
                    "Catalogues can list Plans typed; URL pre-resolution works end-to-end.",
                    "Revert Entry shape + CatalogueGetAction switch.",
                    "30 minutes",
                    "",
                    List.of()
            ),

            new Phase("06",
                    "`StudioBootstrap` wiring — register plans + DocRegistry + routes at boot",
                    "Bootstrap accepts the plan list alongside the existing apps + catalogues + brand. Builds PlanRegistry; registers /plan route.",
                    "Updated `StudioBootstrap.start(...)` and `StudioActionRegistry` to accept `(plans)` alongside existing `(apps, catalogues, brand)`. New overload `start(port, apps, catalogues, plans, brand)` is RFC 0005-ext1's preferred entry. Builds `PlanRegistry` at boot from the explicit list + DocRegistry; registers the `/plan` route (powered by `PlanGetAction`) when plans is non-empty. JourneysCatalogue rewritten to use `Entry.of(Rfc0001PlanData.INSTANCE), …` (Plans as Entry.OfPlan).",
                    Status.DONE,
                    List.of(
                            new Task("`StudioBootstrap.start(port, apps, catalogues, plans, brand)` overload added", true),
                            new Task("`buildRegistry` overload accepts plans; conditionally registers /plan route", true),
                            new Task("`StudioActionRegistry` extended to register PlanGetAction", true),
                            new Task("JourneysCatalogue rewritten — entries are Entry.of(PlanData)", true),
                            new Task("Boot output cleaner: 16 AppModules collapsed to 4", true)
                    ),
                    List.of(new Dependency("03", "Registry must exist."), new Dependency("05", "Entry.OfPlan exists.")),
                    "Empty-plan boot still works; non-empty boot validates + serves.",
                    "Revert bootstrap + action-registry changes.",
                    "30 minutes",
                    "",
                    List.of()
            ),

            new Phase("07",
                    "Migrate seven concrete plan trackers — collapse 4 files to 2",
                    "`Rfc0001`, `Rfc0002`, `Rfc0002Ext1`, `Rename`, `Rfc0004`, `Rfc0004Ext1`, `Rfc0005`: each tracker drops to two files (Steps + PlanData). Per-tracker AppModule + StepAppModule deleted.",
                    "All 7 PlanData files updated: `title()` → `name()`, added `acceptance()` returning `List.of()` stub. Deleted 14 files: `Rfc*Plan.java` + `Rfc*Step.java` + `RenamePlan.java` + `RenameStep.java`. Per-tracker boilerplate eliminated: a new tracker is now 2 files (Steps + PlanData) instead of 4. StudioServer.main shrunk from 16 AppModule entries to 4 (`CatalogueAppHost`, `PlanAppHost`, `DocBrowser`, `DocReader`) plus 4 catalogues + 7 plans + StudioBrand.",
                    Status.DONE,
                    List.of(
                            new Task("All 7 PlanData files: name() + acceptance() stub", true),
                            new Task("Deleted 7 × Rfc*Plan.java + 7 × Rfc*Step.java (14 files)", true),
                            new Task("Deleted PlanAppModule + PlanStepAppModule + legacy PlanRenderer + PlanJson", true),
                            new Task("StudioServer.main: 16 AppModules → 4", true)
                    ),
                    List.of(
                            new Dependency("04", "Renderer consumes the new shape."),
                            new Dependency("06", "Bootstrap wires the registry.")
                    ),
                    "Studio renders all 7 plans identically to before. Browser-visible URLs change (`?app=rfc-0005` → `?app=plan&id=<fqn>`); content unchanged.",
                    "Revert per-tracker. Each is independent.",
                    "1.5 hours",
                    "Net code reduction estimated ~−250 LoC after this phase (legacy ~−950 LoC, new ~+700 LoC).",
                    List.of()
            ),

            new Phase("08",
                    "Conformance — single registry-construction test",
                    "Per D10 of RFC 0005 (mirrored here): no full conformance base. One test that constructs `PlanRegistry` from the studio's explicit plan list. Boot-time invariants are mechanically enforced by the registry constructor; the test pins success in CI.",
                    "Added `StudioPlanConstructsTest` — single test that constructs `PlanRegistry` from the studio's DocRegistry + explicit plan list. assertDoesNotThrow on the constructor; if any §3 invariant fails (duplicate ids, null status, missing dep target, doc unreachability), the test fails with the registry's clear message.",
                    Status.DONE,
                    List.of(
                            new Task("Added `StudioPlanConstructsTest` (~30 LoC, sits alongside StudioCatalogueConstructsTest)", true),
                            new Task("Constructs DocRegistry + PlanRegistry from the studio's plan list", true),
                            new Task("assertDoesNotThrow on the constructor", true),
                            new Task("Test GREEN", true)
                    ),
                    List.of(new Dependency("07", "Plans exist to be checked.")),
                    "All studio tests GREEN, including new plan conformance.",
                    "Delete the test.",
                    "20 minutes",
                    "",
                    List.of()
            ),

            new Phase("09",
                    "Tracker recursion + close-out",
                    "Confirm RFC 0005-ext1's own tracker (this file) renders end-to-end and the implementation is fully landed.",
                    "Close-out. All 8 prior phases DONE. Build green; the legacy PlanAppModule + PlanStepAppModule + per-tracker AppModule files are gone; the new shape (Plan-with-three-pillars + PlanRegistry + PlanAppHost + PlanGetAction + PlanHostRenderer + Acceptance + Entry.OfPlan) is in place; StudioServer.main rewritten with explicit registration. Per-tracker `acceptance()` methods currently return `List.of()` stubs — populating them meaningfully is follow-up work, not blocking this RFC's landing.",
                    Status.DONE,
                    List.of(
                            new Task("All studio tests GREEN after the full landing", true),
                            new Task("Phases 01–08 marked DONE in this file", true),
                            new Task("This tracker (Rfc0005Ext1Steps + Rfc0005Ext1PlanData) is the recursion-proof", true),
                            new Task("Registered in JourneysCatalogue + StudioPlanConstructsTest", true)
                    ),
                    List.of(new Dependency("08", "Conformance closes the loop.")),
                    "RFC 0005-ext1 status flips Draft → Implemented; tracker fully populated.",
                    "n/a — close-out phase.",
                    "15 minutes (verification)",
                    "Per-tracker `acceptance()` population is deliberate follow-up, separate from RFC landing.",
                    List.of()
            )
    );

    // ------------------------------------------------------------------
    // Helpers
    // ------------------------------------------------------------------

    public static Phase phaseById(String id) {
        for (var p : PHASES) if (p.id().equals(id)) return p;
        return null;
    }

    public static Decision decisionById(String id) {
        for (var d : DECISIONS) if (d.id().equals(id)) return d;
        return null;
    }

    public static int totalProgressPercent() {
        if (PHASES.isEmpty()) return 0;
        int doneTasks = 0;
        int totalTasks = 0;
        for (var p : PHASES) {
            doneTasks += (int) p.tasks().stream().filter(Task::done).count();
            totalTasks += p.tasks().size();
        }
        if (totalTasks == 0) return 0;
        return (int) ((long) doneTasks * 100 / totalTasks);
    }

    public static int openDecisionsCount() {
        return (int) DECISIONS.stream().filter(d -> d.status() == DecisionStatus.OPEN).count();
    }

    private Rfc0005Ext1Steps() {}
}
