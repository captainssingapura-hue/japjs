package hue.captains.singapura.js.homing.demo.es;

import hue.captains.singapura.js.homing.core.ExternalModule;
import hue.captains.singapura.js.homing.core.Exportable;
import hue.captains.singapura.js.homing.core.ExportsOf;

import java.util.List;

public record ThreeJs() implements ExternalModule<ThreeJs> {

    public static final ThreeJs INSTANCE = new ThreeJs();

    public record Scene() implements Exportable._Class<ThreeJs> {}
    public record PerspectiveCamera() implements Exportable._Class<ThreeJs> {}
    public record WebGLRenderer() implements Exportable._Class<ThreeJs> {}
    public record AmbientLight() implements Exportable._Class<ThreeJs> {}
    public record DirectionalLight() implements Exportable._Class<ThreeJs> {}
    public record MeshStandardMaterial() implements Exportable._Class<ThreeJs> {}
    public record SphereGeometry() implements Exportable._Class<ThreeJs> {}
    public record CylinderGeometry() implements Exportable._Class<ThreeJs> {}
    public record Mesh() implements Exportable._Class<ThreeJs> {}
    public record Group() implements Exportable._Class<ThreeJs> {}
    public record Color() implements Exportable._Class<ThreeJs> {}
    public record ShapeGeometry() implements Exportable._Class<ThreeJs> {}
    public record ExtrudeGeometry() implements Exportable._Class<ThreeJs> {}
    public record DoubleSide() implements Exportable._Constant<ThreeJs> {}
    public record BackSide() implements Exportable._Constant<ThreeJs> {}
    public record Box3() implements Exportable._Class<ThreeJs> {}
    public record Vector3() implements Exportable._Class<ThreeJs> {}

    @Override
    public ExportsOf<ThreeJs> exports() {
        return new ExportsOf<>(INSTANCE, List.of(
                new Scene(),
                new PerspectiveCamera(),
                new WebGLRenderer(),
                new AmbientLight(),
                new DirectionalLight(),
                new MeshStandardMaterial(),
                new SphereGeometry(),
                new CylinderGeometry(),
                new Mesh(),
                new Group(),
                new Color(),
                new ShapeGeometry(),
                new ExtrudeGeometry(),
                new DoubleSide(),
                new BackSide(),
                new Box3(),
                new Vector3()
        ));
    }
}
