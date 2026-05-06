package hue.captains.singapura.js.homing.libs;

import hue.captains.singapura.js.homing.core.BundledExternalModule;
import hue.captains.singapura.js.homing.core.Exportable;
import hue.captains.singapura.js.homing.core.ExportsOf;

import java.util.List;

/**
 * three.js — 3D graphics library. Bundled at build time from esm.sh and
 * served from the homing.js classpath. No runtime CDN call.
 *
 * @see <a href="https://github.com/mrdoob/three.js">mrdoob/three.js</a>
 */
public record ThreeJs() implements BundledExternalModule<ThreeJs> {

    public static final ThreeJs INSTANCE = new ThreeJs();

    @Override public String sourceUrl()    { return "https://esm.sh/three@0.170.0?bundle"; }
    @Override public String resourcePath() { return "lib/three@0.170.0/three.module.js"; }
    @Override public String sha512()       {
        return "5156dc6c5e91cbf345398509da32ebbd933b16e6c88bd34b4cf507ccd156376a"
             + "4fd24103fc488532b0bc299fd3d019739c9e148d203dd2fe09dff3ab0c16be7e";
    }

    public record Scene()                implements Exportable._Class<ThreeJs> {}
    public record PerspectiveCamera()    implements Exportable._Class<ThreeJs> {}
    public record WebGLRenderer()        implements Exportable._Class<ThreeJs> {}
    public record AmbientLight()         implements Exportable._Class<ThreeJs> {}
    public record DirectionalLight()     implements Exportable._Class<ThreeJs> {}
    public record MeshStandardMaterial() implements Exportable._Class<ThreeJs> {}
    public record SphereGeometry()       implements Exportable._Class<ThreeJs> {}
    public record CylinderGeometry()     implements Exportable._Class<ThreeJs> {}
    public record Mesh()                 implements Exportable._Class<ThreeJs> {}
    public record Group()                implements Exportable._Class<ThreeJs> {}
    public record Color()                implements Exportable._Class<ThreeJs> {}
    public record ShapeGeometry()        implements Exportable._Class<ThreeJs> {}
    public record ExtrudeGeometry()      implements Exportable._Class<ThreeJs> {}
    public record DoubleSide()           implements Exportable._Constant<ThreeJs> {}
    public record BackSide()             implements Exportable._Constant<ThreeJs> {}
    public record Box3()                 implements Exportable._Class<ThreeJs> {}
    public record Vector3()              implements Exportable._Class<ThreeJs> {}

    @Override
    public ExportsOf<ThreeJs> exports() {
        return new ExportsOf<>(INSTANCE, List.of(
                new Scene(), new PerspectiveCamera(), new WebGLRenderer(),
                new AmbientLight(), new DirectionalLight(),
                new MeshStandardMaterial(),
                new SphereGeometry(), new CylinderGeometry(),
                new Mesh(), new Group(), new Color(),
                new ShapeGeometry(), new ExtrudeGeometry(),
                new DoubleSide(), new BackSide(),
                new Box3(), new Vector3()
        ));
    }
}
