package hue.captains.singapura.js.homing.demo.es;

import hue.captains.singapura.js.homing.core.*;
import hue.captains.singapura.js.homing.libs.ThreeJs;

import java.util.List;

public record ExtrudedTurtleDemo() implements AppModule<ExtrudedTurtleDemo> {

    record appMain() implements AppModule._AppMain<ExtrudedTurtleDemo> {}

    public record link() implements AppLink<ExtrudedTurtleDemo> {}

    public static final ExtrudedTurtleDemo INSTANCE = new ExtrudedTurtleDemo();

    @Override
    public String title() {
        return "SVG Coin";
    }

    @Override
    public ImportsFor<ExtrudedTurtleDemo> imports() {
        return ImportsFor.<ExtrudedTurtleDemo>builder()
                .add(new ModuleImports<>(List.of(
                        new ThreeJs.Scene(),
                        new ThreeJs.PerspectiveCamera(),
                        new ThreeJs.WebGLRenderer(),
                        new ThreeJs.AmbientLight(),
                        new ThreeJs.DirectionalLight(),
                        new ThreeJs.Color(),
                        new ThreeJs.Box3(),
                        new ThreeJs.Vector3(),
                        new ThreeJs.CylinderGeometry(),
                        new ThreeJs.MeshStandardMaterial(),
                        new ThreeJs.Mesh(),
                        new ThreeJs.Group()
                ), ThreeJs.INSTANCE))
                .add(new ModuleImports<>(List.of(
                        new SvgExtruder.extrudeSvg()
                ), SvgExtruder.INSTANCE))
                .add(new ModuleImports<>(List.of(
                        new CuteAnimal.turtle(),
                        new CuteAnimal.ghost(),
                        new CuteAnimal.broom(),
                        new CuteAnimal.penguin(),
                        new CuteAnimal.crocodile(),
                        new CuteAnimal.whale()
                ), CuteAnimal.INSTANCE))
                .build();
    }

    @Override
    public ExportsOf<ExtrudedTurtleDemo> exports() {
        return new ExportsOf<>(INSTANCE, List.of(new appMain()));
    }
}
