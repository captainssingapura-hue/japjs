package hue.captains.singapura.js.homing.studio.base.app;

import hue.captains.singapura.js.homing.core.AppModule;
import hue.captains.singapura.js.homing.studio.base.tracker.PlanAppHost;

/**
 * RFC 0015 Phase 5 — framework-default {@link ContentViewer} for Plan
 * trackers (kind == {@code "plan"}). Binds the plan kind to
 * {@link PlanAppHost}.
 *
 * <p>Serves every Doc whose {@code kind()} returns {@code "plan"} — the
 * Phase 3 {@code PlanDoc} subtype wrapping any {@code Plan} tracker.
 * The {@code contentId} is the wrapped Plan's class FQN; the URL
 * resolves to {@code PlanAppHost} which fetches {@code /plan?id=<fqn>}
 * and renders the typed plan structure (objectives, decisions, phases,
 * acceptance).</p>
 *
 * <p>Registered by {@code DefaultFixtures.contentViewers()}.</p>
 *
 * @since RFC 0015 Phase 5
 */
public record PlanContentViewer() implements ContentViewer {

    public static final PlanContentViewer INSTANCE = new PlanContentViewer();

    @Override public String kind() { return "plan"; }

    @Override public AppModule<?, ?> app() { return PlanAppHost.INSTANCE; }

    @Override public String urlFor(String contentId) {
        return "/app?app=plan&id=" + contentId;
    }

    @Override public String summary() {
        return "Plan tracker renderer — fetches /plan?id=<class-fqn> and renders objectives, decisions, phases, acceptance.";
    }
}
