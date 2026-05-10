package hue.captains.singapura.js.homing.demo.es;

import hue.captains.singapura.js.homing.core.*;
import hue.captains.singapura.js.homing.libs.ThreeJs;

import java.util.List;

public record TurtleDemo() implements AppModule<AppModule._None, TurtleDemo> {

    record appMain() implements AppModule._AppMain<AppModule._None, TurtleDemo> {}

    public record link() implements AppLink<TurtleDemo> {}

    public static final TurtleDemo INSTANCE = new TurtleDemo();

    @Override
    public String title() {
        return "3D Turtle";
    }

    @Override
    public ImportsFor<TurtleDemo> imports() {
        return ImportsFor.<TurtleDemo>builder()
                .add(new ModuleImports<>(List.of(
                        new ThreeJs.Scene(),
                        new ThreeJs.PerspectiveCamera(),
                        new ThreeJs.WebGLRenderer(),
                        new ThreeJs.AmbientLight(),
                        new ThreeJs.DirectionalLight(),
                        new ThreeJs.MeshStandardMaterial(),
                        new ThreeJs.SphereGeometry(),
                        new ThreeJs.CylinderGeometry(),
                        new ThreeJs.Mesh(),
                        new ThreeJs.Group(),
                        new ThreeJs.Color()
                ), ThreeJs.INSTANCE))
                .build();
    }

    @Override
    public ExportsOf<TurtleDemo> exports() {
        return new ExportsOf<>(INSTANCE, List.of(new appMain()));
    }
}
