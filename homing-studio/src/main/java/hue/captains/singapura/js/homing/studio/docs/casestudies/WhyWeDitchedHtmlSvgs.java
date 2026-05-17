package hue.captains.singapura.js.homing.studio.docs.casestudies;

import hue.captains.singapura.js.homing.core.ExportsOf;
import hue.captains.singapura.js.homing.core.SvgBeing;
import hue.captains.singapura.js.homing.core.SvgGroup;

import java.util.List;

/**
 * SvgGroup for the "Why We Ditched HTML" case study's illustrations.
 * One being today — {@link segmentAdt} — diagrams the typed-segment
 * variants that replace ad-hoc HTML. More may join over time without
 * any rewiring.
 *
 * <p>Theme participation: per RFC 0017, the SVG uses {@code currentColor}
 * and {@code var(--color-*)} tokens so the diagram inherits the active
 * theme.</p>
 */
public record WhyWeDitchedHtmlSvgs() implements SvgGroup<WhyWeDitchedHtmlSvgs> {

    /** Diagram — the four typed segment variants stacked inside a
     *  ComposedDoc envelope. The self-proof's central visual. */
    public record segmentAdt() implements SvgBeing<WhyWeDitchedHtmlSvgs> {}

    public static final WhyWeDitchedHtmlSvgs INSTANCE = new WhyWeDitchedHtmlSvgs();

    @Override
    public List<SvgBeing<WhyWeDitchedHtmlSvgs>> svgBeings() {
        return List.of(new segmentAdt());
    }

    @Override
    public ExportsOf<WhyWeDitchedHtmlSvgs> exports() {
        return new ExportsOf<>(this, List.copyOf(svgBeings()));
    }
}
