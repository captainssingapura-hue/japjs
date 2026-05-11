package hue.captains.singapura.js.homing.studio.base.theme;

import hue.captains.singapura.js.homing.core.ExportsOf;
import hue.captains.singapura.js.homing.core.SvgBeing;
import hue.captains.singapura.js.homing.core.SvgGroup;

import java.util.List;

/**
 * SvgGroup for the {@link HomingMapleBridge} theme's atmospheric backdrop.
 * One being — {@link nocturne} — points at the SVG illustration the framework
 * renders as inline DOM behind every studio page when Maple Bridge is active.
 *
 * <p>The SVG carries its own night/dawn palettes internally (switched by
 * {@code @media (prefers-color-scheme)}) and exposes per-element class
 * markers (the moon, the temple, etc.) so theme CSS can target them for
 * hover effects and transitions.</p>
 */
public record HomingMapleBridgeBg() implements SvgGroup<HomingMapleBridgeBg> {

    /** The full nocturne illustration. */
    public record nocturne() implements SvgBeing<HomingMapleBridgeBg> {}

    public static final HomingMapleBridgeBg INSTANCE = new HomingMapleBridgeBg();

    @Override
    public List<SvgBeing<HomingMapleBridgeBg>> svgBeings() {
        return List.of(new nocturne());
    }

    @Override
    public ExportsOf<HomingMapleBridgeBg> exports() {
        return new ExportsOf<>(this, List.copyOf(svgBeings()));
    }
}
