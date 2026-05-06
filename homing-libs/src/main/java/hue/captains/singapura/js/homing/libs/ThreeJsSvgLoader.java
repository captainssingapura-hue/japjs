package hue.captains.singapura.js.homing.libs;

import hue.captains.singapura.js.homing.core.BundledExternalModule;
import hue.captains.singapura.js.homing.core.Exportable;
import hue.captains.singapura.js.homing.core.ExportsOf;

import java.util.List;

/**
 * three.js SVGLoader add-on. Bundled at build time. Note: esm.sh's {@code ?bundle}
 * inlines three.js into this file too, so the SVGLoader bundle is fully
 * self-contained but does duplicate three.js bytes.
 *
 * @see <a href="https://threejs.org/docs/?q=svgloader#examples/en/loaders/SVGLoader">SVGLoader</a>
 */
public record ThreeJsSvgLoader() implements BundledExternalModule<ThreeJsSvgLoader> {

    public static final ThreeJsSvgLoader INSTANCE = new ThreeJsSvgLoader();

    @Override public String sourceUrl()    { return "https://esm.sh/three@0.170.0/addons/loaders/SVGLoader.js?bundle"; }
    @Override public String resourcePath() { return "lib/three@0.170.0/addons/loaders/SVGLoader.js"; }
    @Override public String sha512()       {
        return "c6d000ae5cea9889b564f27eaf17ef3674db8377b9ddf9e7a329992851ea5fcb"
             + "51921d159ab1e91040000caccdb466c98ea238d73cb6ff239fc1edacbab4b4e4";
    }

    public record SVGLoader() implements Exportable._Class<ThreeJsSvgLoader> {}

    @Override
    public ExportsOf<ThreeJsSvgLoader> exports() {
        return new ExportsOf<>(INSTANCE, List.of(new SVGLoader()));
    }
}
