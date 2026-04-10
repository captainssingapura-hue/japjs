package hue.captains.singapura.japjs.core;

import java.util.List;

/**
 * A group of SVG resources that generates a single ES module.
 * Each SvgBeing in the group becomes a frozen marker object export.
 */
public interface SvgGroup<G extends SvgGroup<G>> extends EsModule<G> {

    /**
     * Svg Group by default has no import.
     * @return
     */
    @Override
    default ImportsFor<G> imports() {
        return ImportsFor.noImports();
    }

    List<SvgBeing<G>> svgBeings();
}
