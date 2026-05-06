package hue.captains.singapura.js.homing.demo.es;

import hue.captains.singapura.js.homing.core.*;
import hue.captains.singapura.js.homing.libs.ThreeJsSvgLoader;
import hue.captains.singapura.js.homing.libs.ThreeJs;

import java.util.List;

/**
 * Library that decomposes a flat SVG into exploded 3D layers,
 * spreading each SVG path along the z-axis for visual inspection.
 */
public record SvgDecomposer() implements EsModule<SvgDecomposer> {

    public static final SvgDecomposer INSTANCE = new SvgDecomposer();

    public record decomposeSvg() implements Exportable._Constant<SvgDecomposer> {}

    @Override
    public ImportsFor<SvgDecomposer> imports() {
        return ImportsFor.<SvgDecomposer>builder()
                .add(new ModuleImports<>(List.of(
                        new ThreeJs.Group(),
                        new ThreeJs.Mesh(),
                        new ThreeJs.MeshStandardMaterial(),
                        new ThreeJs.ShapeGeometry(),
                        new ThreeJs.ExtrudeGeometry(),
                        new ThreeJs.Color(),
                        new ThreeJs.DoubleSide()
                ), ThreeJs.INSTANCE))
                .add(new ModuleImports<>(List.of(
                        new ThreeJsSvgLoader.SVGLoader()
                ), ThreeJsSvgLoader.INSTANCE))
                .build();
    }

    @Override
    public ExportsOf<SvgDecomposer> exports() {
        return new ExportsOf<>(INSTANCE, List.of(new decomposeSvg()));
    }
}
