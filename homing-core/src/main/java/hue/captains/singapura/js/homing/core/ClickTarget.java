package hue.captains.singapura.js.homing.core;

/**
 * A typed clickable element on a theme's surface — the binding key for
 * a {@link Cue}. Theme authors implement this via a theme-scoped sealed
 * sub-interface, with one record per element (e.g.
 * {@code permits Temple, Moon, TempleWindow}). The {@link #classToken()}
 * is a constant on each record — the DOM class attribute the framework's
 * runtime watches for clicks.
 *
 * <p>By design this is a regular interface, not sealed at the framework
 * level — themes are the natural authority over which elements are
 * clickable in their surface. Theme-local sub-interfaces are typically
 * sealed to give compile-time exhaustiveness over the theme's targets.</p>
 *
 * <p>Theme authors never write the class token as a free-form string —
 * they reference the implementing record (e.g. {@code new Temple()}) and
 * the framework reads {@code classToken()} from it at JS-gen time.</p>
 *
 * @param <TH> the theme this target lives on
 */
public interface ClickTarget<TH extends Theme> {

    /**
     * Stable DOM class token — the framework's runtime watches click
     * events on elements carrying this class. Constant on each
     * implementing record; never concatenated or parsed by user code.
     */
    String classToken();
}
