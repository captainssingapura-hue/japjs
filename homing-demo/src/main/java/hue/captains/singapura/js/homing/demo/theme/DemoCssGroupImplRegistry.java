package hue.captains.singapura.js.homing.demo.theme;

import hue.captains.singapura.js.homing.core.CssGroupImpl;

import java.util.List;

/**
 * Manual registry of every {@link CssGroupImpl} the homing-demo ships.
 *
 * <p>RFC 0002-ext1 Phase 11 — empty after the marker-shape migration. All
 * CssClass records carry inline {@code body()} overrides that reference
 * theme-provided {@code var(--…)} tokens; no per-(group, theme) impl files
 * remain. The framework's {@code CssGroupImplConsistencyTest} accepts a
 * group with all-inline bodies as satisfying the per-default-theme
 * reachability requirement without a registered impl.</p>
 */
public final class DemoCssGroupImplRegistry {

    /** Every CssGroup × Theme implementation shipped by homing-demo (now empty). */
    public static final List<CssGroupImpl<?, ?>> ALL = List.of();

    private DemoCssGroupImplRegistry() {}
}
