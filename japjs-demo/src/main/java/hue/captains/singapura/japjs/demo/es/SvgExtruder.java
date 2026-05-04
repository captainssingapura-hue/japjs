package hue.captains.singapura.japjs.demo.es;

import hue.captains.singapura.japjs.core.*;

import java.util.List;

/**
 * Generic library that turns a flat SVG string into a 3D Three.js Group
 * by extruding each path along the z-axis.
 */
public record SvgExtruder() implements EsModule<SvgExtruder> {

    public static final SvgExtruder INSTANCE = new SvgExtruder();

    public record extrudeSvg() implements Exportable._Constant<SvgExtruder> {}

    @Override
    public ImportsFor<SvgExtruder> imports() {
        return ImportsFor.<SvgExtruder>builder()
                .add(new ModuleImports<>(List.of(
                        new ThreeJs.Group(),
                        new ThreeJs.Mesh(),
                        new ThreeJs.MeshStandardMaterial(),
                        new ThreeJs.ShapeGeometry(),
                        new ThreeJs.ExtrudeGeometry(),
                        new ThreeJs.Color(),
                        new ThreeJs.DoubleSide(),
                        new ThreeJs.BackSide()
                ), ThreeJs.INSTANCE))
                .add(new ModuleImports<>(List.of(
                        new ThreeJsSvgLoader.SVGLoader()
                ), ThreeJsSvgLoader.INSTANCE))
                .build();
    }

    @Override
    public ExportsOf<SvgExtruder> exports() {
        return new ExportsOf<>(INSTANCE, List.of(new extrudeSvg()));
    }
}
