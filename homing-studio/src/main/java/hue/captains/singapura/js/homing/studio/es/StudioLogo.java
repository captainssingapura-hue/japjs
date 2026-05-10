package hue.captains.singapura.js.homing.studio.es;

import hue.captains.singapura.js.homing.core.ExportsOf;
import hue.captains.singapura.js.homing.core.SvgBeing;
import hue.captains.singapura.js.homing.core.SvgGroup;

import java.util.List;

/**
 * SvgGroup for the homing-studio brand mark. One being — {@link logo} — drives
 * the brand glyph in {@code StudioBrand}. Same group can grow more typed SVG
 * assets later (favicon variant, social-card mark, etc.) without changing the
 * brand wiring; each new {@code SvgBeing} just needs an SVG resource at the
 * conventional path
 * {@code homing/svg/<this package>/StudioLogo/<being simple name>.svg}.
 */
public record StudioLogo() implements SvgGroup<StudioLogo> {

    /** The brand glyph — a Bauhaus-flavoured "H" coloured by {@code var(--color-accent)}. */
    public record logo() implements SvgBeing<StudioLogo> {}

    public static final StudioLogo INSTANCE = new StudioLogo();

    @Override
    public List<SvgBeing<StudioLogo>> svgBeings() {
        return List.of(new logo());
    }

    @Override
    public ExportsOf<StudioLogo> exports() {
        return new ExportsOf<>(this, List.copyOf(svgBeings()));
    }
}
