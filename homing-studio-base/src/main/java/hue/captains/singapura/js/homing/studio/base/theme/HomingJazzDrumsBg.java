package hue.captains.singapura.js.homing.studio.base.theme;

import hue.captains.singapura.js.homing.core.ExportsOf;
import hue.captains.singapura.js.homing.core.SvgBeing;
import hue.captains.singapura.js.homing.core.SvgGroup;

import java.util.List;

/**
 * SvgGroup for the {@link HomingJazzDrums} theme's atmospheric backdrop —
 * a jazz drum kit illustrated as inline DOM, each drum + cymbal classed
 * for per-element CSS targeting (hover) and audio-cue binding (click).
 *
 * <p>One being — {@link kit} — points at the full drum-kit illustration.
 * Same backdrop pattern as Maple Bridge's nocturne and Retro 90s'
 * desktop; the kit replaces "atmosphere" with "instrument."</p>
 */
public record HomingJazzDrumsBg() implements SvgGroup<HomingJazzDrumsBg> {

    /** The full drum kit — bass drum, snare, hi-hat, 3 toms, 2 cymbals. */
    public record kit() implements SvgBeing<HomingJazzDrumsBg> {}

    public static final HomingJazzDrumsBg INSTANCE = new HomingJazzDrumsBg();

    @Override
    public List<SvgBeing<HomingJazzDrumsBg>> svgBeings() {
        return List.of(new kit());
    }

    @Override
    public ExportsOf<HomingJazzDrumsBg> exports() {
        return new ExportsOf<>(this, List.copyOf(svgBeings()));
    }
}
