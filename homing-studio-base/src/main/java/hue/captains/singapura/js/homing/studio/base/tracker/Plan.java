package hue.captains.singapura.js.homing.studio.base.tracker;

import java.util.List;

/**
 * Typed view of a multi-phase plan tracker. Every plan in {@code homing-studio}
 * exposes itself as a {@code Plan} so the generic {@link PlanAppModule} +
 * {@link PlanStepAppModule} machinery can render it without per-plan JS.
 *
 * <p>The shape mirrors what the old per-plan {@code *DataGetAction} hand-rolled
 * JSON used to expose. It is a flat, serialisable view — no plan-specific
 * fields — so the JS-side {@code PlanRenderer} can render any concrete plan
 * with the same code.</p>
 */
public interface Plan {

    /** Short label above the page title (e.g. "project rename", "RFC 0001"). */
    String kicker();

    /** Page title (h1). */
    String title();

    /** Page subtitle / lede paragraph (plain text). */
    String subtitle();

    /** 0..100, computed across all phases. */
    int totalProgress();

    /** Number of decisions still in OPEN state. */
    int openDecisions();

    /**
     * Optional companion doc path served by DocReader (e.g.
     * {@code rename/EXECUTION-PLAN.md}). Null = no link rendered.
     */
    String executionDoc();

    /** Optional secondary doc path. Null = no link rendered. */
    String dossierDoc();

    /** Phases, in display order. */
    List<Phase> phases();

    /** Open decisions to resolve. Empty list = no decisions section. */
    List<Decision> decisions();

    /** Look up a phase by id; null if no match. */
    default Phase phaseById(String id) {
        for (var p : phases()) if (p.id().equals(id)) return p;
        return null;
    }
}
