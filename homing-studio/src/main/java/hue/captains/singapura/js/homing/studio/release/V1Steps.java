package hue.captains.singapura.js.homing.studio.release;

import java.util.List;

/**
 * Implementation tracker for <b>Release v1 — Ship Checkpoint</b>.
 *
 * <p>Tracks the gaps between "main is green" and "downstream can adopt this
 * confidently." Captured as a Plan tracker so the work renders live in the
 * Journeys catalogue and the same registry / conformance machinery that
 * validates every other plan validates this one too.</p>
 *
 * <p>This file is the living plan. No companion RFC — the gaps were
 * audited from the codebase, not designed up front; phases here are the
 * fix list, decisions here are the open questions before each phase can
 * land.</p>
 */
public final class V1Steps {

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

    // ------------------------------------------------------------------
    // OPEN DECISIONS — to be resolved before the phases that depend on
    // them can land. Audit framing recorded inline.
    // ------------------------------------------------------------------
    public static final List<Decision> DECISIONS = List.of(

            new Decision("D1",
                    "README modernization — full rewrite or surgical patches?",
                    "Surgical patches focused on (a) the new preferred StudioBootstrap signature, (b) Catalogue-as-home replacing AppModule-as-home, (c) the 6-overload menu, (d) a new Conformance baseline section.",
                    "surgical patches",
                    DecisionStatus.RESOLVED,
                    "User: indifferent — README rewrite is cheap going forward, easily redone if needed. Going with surgical patches: faster tonight, preserves the agent-targeted templates (CSS, theme picker, AppModule anatomy) that still apply, and the structural shift is contained to a few sections.",
                    "Unblocks Phase 02."
            ),

            new Decision("D2",
                    "Demo migration scope — minimal Catalogue/Plan example added or full reshape as mini-studio?",
                    "Minimal example: add a tiny CatalogueAppHost-served catalogue alongside the existing DemoCatalogue (renamed if needed). Don't reshape the SVG/animation gallery — those still exercise homing-core's primitives.",
                    "descoped from v1",
                    DecisionStatus.RESOLVED,
                    "User: leave demo out of scope for v1; focus is a working studio base. Demo migration deferred to v1.x. Phase 04 marked SKIPPED.",
                    "Phase 04 skipped; DemoManagerInjectionConformanceTest deferred."
            ),

            new Decision("D3",
                    "acceptance() population scope for v1 — this tracker only (recursion-proof), or backfill all 7 trackers?",
                    "This tracker only. v1 trackers keep List.of() stubs; this tracker populates a meaningful list as the recursion-proof.",
                    "this tracker only + add Objectives pillar",
                    DecisionStatus.RESOLVED,
                    "User: this tracker only AND add an Objectives section at the top of the Plan render. New 4th structural pillar on Plan: Objective(label, description) — describes what the tracker is trying to achieve, distinct from per-phase outcomes and from acceptance criteria. Rendered at top of the index view as a high-level orienting section.",
                    "Adds new framework work: Objectives pillar (Plan.objectives(), Objective record, JSON, renderer). Captured as Phase 06 (was: stretch) — now MUST."
            )
    );

    // ------------------------------------------------------------------
    // PHASES — execute in order. MUST phases gate the v1 release;
    // STRETCH phases ship in v1.x if not done tonight.
    // ------------------------------------------------------------------
    public static final List<Phase> PHASES = List.of(

            new Phase("01",
                    "Tracker landed (this file)",
                    "Captures gaps + open questions in the new Plan format; renders live in Journeys.",
                    "V1Steps + V1PlanData created; registered in JourneysCatalogue and StudioPlanConstructsTest. The audit's Tier 1/2/3 collapsed to MUST / STRETCH on the phase list. Decisions D1-D3 captured as OPEN.",
                    Status.DONE,
                    List.of(
                            new Task("Create V1Steps.java + V1PlanData.java under hue…studio.release", true),
                            new Task("Register V1PlanData.INSTANCE in JourneysCatalogue", true),
                            new Task("Register V1PlanData.INSTANCE in StudioPlanConstructsTest", true),
                            new Task("Studio tests GREEN with new tracker present", true),
                            new Task("Tracker visible at /app?app=plan&id=…release.V1PlanData", true)
                    ),
                    List.of(),
                    "Studio tests green; tracker fetchable via PlanAppHost.",
                    "Delete the four edits (2 new files + 2 list entries).",
                    "20 minutes",
                    "",
                    List.of()
            ),

            new Phase("02",
                    "[MUST] README modernization for RFC 0005 + 0005-ext1",
                    "Bring homing-studio-base/README.md up to date with the typed-container world. Without this, downstream agents reading the docs build the wrong shape.",
                    "Surgical patches to homing-studio-base/README.md per D1: (a) 'What you get for free' endpoint table now includes /catalogue, /plan, and the typed-container framing; (b) 'The server entry' rewritten to show the new signature with apps + catalogues + plans + brand; (c) 'Anatomy of an AppModule' renamed to 'custom AppModule' with a banner steering downstream to Catalogue/Plan for the common case; (d) two new 'Anatomy' sections for Catalogue and Plan with copy-pasteable templates; (e) StudioBootstrap.start overloads documented as a 4-row table with the canonical row highlighted; (f) cross-refs to CatalogueKitDoc + PlanKitDoc.",
                    Status.DONE,
                    List.of(
                            new Task("Audited the existing 443 lines — flagged the bootstrap, anatomy, and theme sections", true),
                            new Task("Patched the 'server entry' section with the new 5-arg signature", true),
                            new Task("Renamed 'AppModule anatomy' to 'custom AppModule', added Catalogue/Plan-first banner", true),
                            new Task("Documented the StudioBootstrap.start overloads as a 4-row table", true),
                            new Task("Added 'Conformance baseline' section listing the 9 recommended test bases", true),
                            new Task("Cross-referenced CatalogueKitDoc + PlanKitDoc", true)
                    ),
                    List.of(new Dependency("01", "Tracker exists to capture progress.")),
                    "Fresh agent reading README produces a Catalogue-first, brand-aware studio scaffold on first try.",
                    "git revert the README commit; previous version still buildable.",
                    "1.5–2 hours (depending on D1)",
                    "Blocked on D1 (rewrite vs patches).",
                    List.of()
            ),

            new Phase("03",
                    "[MUST] PlanKitDoc — peer to CatalogueKitDoc",
                    "Add the kit-level reference doc for Plan trackers. Currently the Plan recipe is only learnable by reading 7 existing trackers.",
                    "Created PlanKitDoc.java + .md under homing-studio/.../docs/blocks/. Mirrors CatalogueKitDoc's shape: identity, three pillars (decisions/phases/acceptance) + the optional 4th (objectives), the Steps.java + PlanData.java recipe, registration via PlanRegistry, URL contract, boot-time validations (8), conformance posture. Cross-references PlanContainerDoc + Rfc0005Ext1Doc + CatalogueKitDoc + AtomsDoc + BootstrapAndConformanceDoc. Registered as Entry + Doc in BuildingBlocksCatalogue.",
                    Status.DONE,
                    List.of(
                            new Task("Drafted PlanKitDoc.md (recipe + identity + URL contract + validations + conformance)", true),
                            new Task("Created PlanKitDoc.java with UUID + 5 DocReferences", true),
                            new Task("Registered in BuildingBlocksCatalogue (Entry + docs())", true),
                            new Task("Managed-reference scan green (all 5 #ref: anchors resolve)", true),
                            new Task("Studio tests GREEN with new doc reachable (123/123)", true)
                    ),
                    List.of(new Dependency("01", "Tracker shape pinned.")),
                    "PlanKitDoc renders at /app?app=doc-reader&doc=<uuid>; managed-reference scan green; cross-link from CatalogueKitDoc resolves.",
                    "Delete PlanKitDoc.java + .md, revert BuildingBlocksCatalogue entry.",
                    "45 minutes",
                    "",
                    List.of()
            ),

            new Phase("04",
                    "[SKIPPED — v1.x] Demo migration — minimal Catalogue + Plan example",
                    "homing-demo currently uses pre-RFC-0005 patterns. Downstream looking at the demo gets the wrong template.",
                    "Per D2: descoped from v1. Demo stays as the homing-core feature gallery for now; Catalogue/Plan migration defers to v1.x once the studio-base story is fully shipped and stabilised. The studio itself (homing-studio) remains the canonical 'copy this' template for downstream until then.",
                    Status.NOT_STARTED,
                    List.of(
                            new Task("[skipped] Add Catalogue record in homing-demo", false),
                            new Task("[skipped] Add Plan tracker in homing-demo", false),
                            new Task("[skipped] Wire CatalogueAppHost + PlanAppHost + StudioBrand into demo bootstrap", false),
                            new Task("[skipped] Add DemoManagerInjectionConformanceTest", false)
                    ),
                    List.of(),
                    "n/a — descoped.",
                    "n/a.",
                    "deferred",
                    "Carry to v1.x. Track separately if needed.",
                    List.of()
            ),

            new Phase("05",
                    "[MUST] Conformance baseline section in README",
                    "Pin the 'extend these N tests and you're CI-safe' recipe. Currently nothing tells downstream which conformance bases to subclass.",
                    "Added 'Conformance baseline' section to homing-studio-base/README.md listing the 9 recommended bases (Css, Href, Cdn, Doctrine, Doc, CssGroupImpl, ManagerInjection — 7 abstract bases — plus the 2 construct-test patterns for CatalogueRegistry + PlanRegistry). 9-row purpose table + a 4-line subclass example for ManagerInjection. Sweep through the Cookbook also added pitfall rows for the new error classes (catalogue 404, doc-reachability, the auto-injection collision).",
                    Status.DONE,
                    List.of(
                            new Task("Enumerated 9 conformance bases with one-line purposes (7 abstract + 2 construct-tests)", true),
                            new Task("Provided 4-line subclass example", true),
                            new Task("Cross-linked from the 'What you get for free' table via the new typed-container framing paragraph", true)
                    ),
                    List.of(new Dependency("02", "Lands together with the README pass.")),
                    "README has a 'Conformance baseline' section; downstream can copy 9 four-line subclasses and have CI guardrails.",
                    "Revert the section.",
                    "30 minutes",
                    "Often subsumed by Phase 02; broken out for visibility.",
                    List.of()
            ),

            new Phase("06",
                    "[MUST] Add Objectives pillar to Plan + populate this tracker's acceptance()",
                    "Per D3: add a 4th structural pillar to the Plan interface (Objective(label, description) — describes what the tracker is trying to achieve) and populate this tracker's acceptance() as the recursion-proof.",
                    "Framework change landed: Objective(label, description) record under base/tracker/, Plan interface extended with default `objectives()` returning List<Objective> (additive — all 7 existing trackers compile unchanged). PlanGetAction emits `objectives` JSON array (above `phases`). PlanHostRenderer's index view renders an Objectives section at the top — above the progress bar — when non-empty, hidden when empty. V1PlanData populates 4 objectives (downstream-ready / docs / silent-failure conformance / recursion-proof) and 5 acceptance criteria (one per MUST phase + ship gate). Distinguished from acceptance: objectives un-checkboxed (goals), acceptance checkboxed (pass/fail).",
                    Status.DONE,
                    List.of(
                            new Task("Added Objective(label, description) record under base/tracker/", true),
                            new Task("Added default `objectives()` to Plan interface", true),
                            new Task("PlanGetAction emits `objectives` JSON array", true),
                            new Task("PlanHostRenderer renders Objectives section at top of index view when non-empty", true),
                            new Task("Populated V1PlanData.objectives() — 4 entries", true),
                            new Task("Populated V1PlanData.acceptance() — 5 entries (one per MUST phase + ship gate)", true),
                            new Task("Studio tests GREEN (123/123)", true)
                    ),
                    List.of(new Dependency("01", "Tracker exists.")),
                    "Tracker's index view shows populated Objectives section at top + Acceptance section below; framework change is additive (defaulted method) so all 7 existing trackers compile unchanged.",
                    "Revert framework + V1 populations; objectives() default to List.of() so removal is safe.",
                    "1 hour",
                    "Promoted from STRETCH to MUST per D3.",
                    List.of()
            ),

            new Phase("07",
                    "[MUST] Strong-typed Navigable — App + Params binding",
                    "User reframe (round 2): 'an App is not fully navigable without parameters.' AppModule alone is half a URL; bare CatalogueAppHost / PlanAppHost / DocReader URLs are broken without their params. Reintroduce Navigable as a typed (App, Params, name, summary) record; lift name + summary OFF AppModule.",
                    "Two-phase migration: (1) parameterise AppModule on its Params type — AppModule<P extends _Param, M extends AppModule<P, M>>; ProxyApp parameterised symmetrically; introduce AppModule._None sentinel for paramless apps; (2) introduce Navigable<P, M>(app, params, name, summary) record with compile-time type matching, hosting tile display data; rewire Entry.OfApp(Navigable<?, ?>) replacing OfApp(AppModule<?>). The compiler now enforces 'wrong params for app' at the catalogue construction site — no silent broken URLs. ProxyApp + Mailto/Sms/Tel updated symmetrically. Every existing AppModule implementor + every Params record updated to the new typed shape across all 8 modules.",
                    Status.DONE,
                    List.of(
                            new Task("AppModule<P extends _Param, M extends AppModule<P, M>> — typed on Params + self-type", true),
                            new Task("AppModule._None sentinel record + AppModule._Param marker", true),
                            new Task("ProxyApp parameterised symmetrically — ProxyApp<P, M>", true),
                            new Task("Mailto / Sms / Tel + their Params records updated", true),
                            new Task("ParamsWriter + UrlTemplate handle _None.class same as Void", true),
                            new Task("Navigable<P, M>(app, params, name, summary) record with reflective url() builder", true),
                            new Task("Entry.OfApp(Navigable<?, ?>) replaces OfApp(AppModule<?>)", true),
                            new Task("CatalogueGetAction reads name/summary/url from nav directly", true),
                            new Task("CatalogueRegistry pattern-matches on Navigable<?, ?>", true),
                            new Task("Every AppModule implementor updated — kernel, server, libs, studio-base, studio, demo, tests", true),
                            new Task("README's Catalogue anatomy example uses new Navigable wrapping", true),
                            new Task("Full multi-module build GREEN (263 tests)", true)
                    ),
                    List.of(new Dependency("06", "Plan interface stable before AppModule changes.")),
                    "All 8 modules build green; the framework's URL contract is type-safe end-to-end — wrong-params-for-app is a compile error, not a runtime broken-link.",
                    "Revert AppModule + ProxyApp + Mailto/Sms/Tel + ParamsWriter + UrlTemplate to single-arg generics; delete Navigable; revert Entry.OfApp; restore old AppModule.name()/summary() defaults if reverting fully.",
                    "90 minutes",
                    "Round-2 framing per user — AppModule.name()/summary() defaults from Round 1 were wrong; tile display lives on the bound Navigable, not the unbound AppModule.",
                    List.of()
            ),

            new Phase("08",
                    "[STRETCH] StudioBootstrap.start overloads — collapse or annotate",
                    "6 overloads is confusing. Either reduce or annotate the canonical one with @PreferredOverload-style javadoc.",
                    "Audit the 6 overloads. Likely: keep 2 (minimal: apps only; full: apps + catalogues + plans + brand + theme + extras). Deprecate the rest with @Deprecated + replacement pointer. Or: keep all 6 but mark one canonical via javadoc.",
                    Status.NOT_STARTED,
                    List.of(
                            new Task("List all 6 overloads with usage counts (grep)", false),
                            new Task("Pick the canonical 1–2", false),
                            new Task("Either delete or @Deprecate the rest", false),
                            new Task("Update README references", false)
                    ),
                    List.of(new Dependency("02", "README will reference the canonical overload.")),
                    "Downstream sees 1–2 obvious overloads; old call-sites still compile (if @Deprecated path chosen).",
                    "Restore deprecation removals.",
                    "45 minutes",
                    "",
                    List.of()
            ),

            new Phase("09",
                    "[CLOSE-OUT] Ship green",
                    "All MUST phases DONE; full build green.",
                    "Final verification pass. Run full multi-module build. MUST set: 02 (README), 03 (PlanKitDoc), 05 (Conformance baseline), 06 (Objectives pillar + acceptance). Phases 04 (demo), 07 (NavigableApp tightening), 08 (overload collapse) carry to v1.x.",
                    Status.DONE,
                    List.of(
                            new Task("All MUST phases (02, 03, 05, 06) marked DONE", true),
                            new Task("Full mvn install GREEN across all 7 modules", true),
                            new Task("Studio boot manually verified — Journeys page shows V1 tracker with Objectives + Acceptance populated", true),
                            new Task("This tracker's index page shows green totalProgress on MUST phases", true)
                    ),
                    List.of(
                            new Dependency("02", "README modernization."),
                            new Dependency("03", "PlanKitDoc."),
                            new Dependency("05", "Conformance baseline section."),
                            new Dependency("06", "Objectives pillar + this tracker's acceptance().")
                    ),
                    "Multi-module build green; tracker shows 100% on MUST phases; downstream-ready studio base.",
                    "n/a — close-out.",
                    "30 minutes",
                    "Phases 04 / 07 / 08 deferred to v1.x.",
                    List.of()
            )
    );

    // ------------------------------------------------------------------
    // Helpers — same shape as every other tracker
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

    private V1Steps() {}
}
