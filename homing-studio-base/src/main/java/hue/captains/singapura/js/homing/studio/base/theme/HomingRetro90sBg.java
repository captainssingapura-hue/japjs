package hue.captains.singapura.js.homing.studio.base.theme;

import hue.captains.singapura.js.homing.core.ExportsOf;
import hue.captains.singapura.js.homing.core.SvgBeing;
import hue.captains.singapura.js.homing.core.SvgGroup;

import java.util.List;

/**
 * SvgGroup for the {@link HomingRetro90s} theme's atmospheric backdrop.
 * One being — {@link desktop} — points at the Win95 desktop illustration
 * the framework renders as inline DOM behind every studio page when the
 * Retro 90s theme is active.
 *
 * <p>The SVG carries iconic desktop icons (My Computer, My Documents,
 * Network Neighborhood, Recycle Bin) as classed elements so the host
 * stylesheet can wire per-icon hover effects — the same pattern Maple
 * Bridge uses for its moon. {@code preserveAspectRatio="xMinYMid slice"}
 * pins the icons to the left edge of the viewport regardless of window
 * aspect ratio.</p>
 */
public record HomingRetro90sBg() implements SvgGroup<HomingRetro90sBg> {

    /** The full Win95 desktop illustration — solid backdrop + four classic icons. */
    public record desktop() implements SvgBeing<HomingRetro90sBg> {}

    public static final HomingRetro90sBg INSTANCE = new HomingRetro90sBg();

    @Override
    public List<SvgBeing<HomingRetro90sBg>> svgBeings() {
        return List.of(new desktop());
    }

    @Override
    public ExportsOf<HomingRetro90sBg> exports() {
        return new ExportsOf<>(this, List.copyOf(svgBeings()));
    }
}
