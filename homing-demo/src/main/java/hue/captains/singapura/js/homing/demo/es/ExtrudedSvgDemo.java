package hue.captains.singapura.js.homing.demo.es;

import hue.captains.singapura.js.homing.core.*;

import java.util.List;

public record ExtrudedSvgDemo() implements AppModule<ExtrudedSvgDemo> {

    record appMain() implements AppModule._AppMain<ExtrudedSvgDemo> {}

    public record link() implements AppLink<ExtrudedSvgDemo> {}

    public static final ExtrudedSvgDemo INSTANCE = new ExtrudedSvgDemo();

    @Override
    public String title() {
        return "SVG Extruder";
    }

    @Override
    public ImportsFor<ExtrudedSvgDemo> imports() {
        return ImportsFor.<ExtrudedSvgDemo>builder()
                .add(new ModuleImports<>(List.of(
                        new ThreeJs.Scene(),
                        new ThreeJs.PerspectiveCamera(),
                        new ThreeJs.WebGLRenderer(),
                        new ThreeJs.AmbientLight(),
                        new ThreeJs.DirectionalLight(),
                        new ThreeJs.Color(),
                        new ThreeJs.Box3(),
                        new ThreeJs.Vector3()
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
    public ExportsOf<ExtrudedSvgDemo> exports() {
        return new ExportsOf<>(INSTANCE, List.of(new appMain()));
    }
}
