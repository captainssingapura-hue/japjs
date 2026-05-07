package hue.captains.singapura.js.homing.studio.base.theme;

import hue.captains.singapura.js.homing.core.Theme;

/**
 * The default Homing theme — the visual identity that ships with
 * {@code homing-studio-base}. Any consumer depending on this module gets
 * working CSS out of the box without designing their own theme.
 *
 * <p>Slug {@code "homing-default"}; this is what the browser sends as the
 * {@code ?theme=…} query parameter, and what {@code CssGroupImpl} instances
 * keyed by this theme are looked up by.</p>
 *
 * @see hue.captains.singapura.js.homing.studio.base.theme.StudioStylesHomingDefault
 */
public record HomingDefault() implements Theme {

    public static final HomingDefault INSTANCE = new HomingDefault();

    @Override public String slug()  { return "homing-default"; }
    @Override public String label() { return "Homing default"; }
}
