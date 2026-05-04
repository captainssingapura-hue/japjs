package hue.captains.singapura.js.homing.demo.es;

import hue.captains.singapura.js.homing.core.ExternalModule;
import hue.captains.singapura.js.homing.core.Exportable;
import hue.captains.singapura.js.homing.core.ExportsOf;

import java.util.List;

public record ThreeJsSvgLoader() implements ExternalModule<ThreeJsSvgLoader> {

    public static final ThreeJsSvgLoader INSTANCE = new ThreeJsSvgLoader();

    public record SVGLoader() implements Exportable._Class<ThreeJsSvgLoader> {}

    @Override
    public ExportsOf<ThreeJsSvgLoader> exports() {
        return new ExportsOf<>(INSTANCE, List.of(new SVGLoader()));
    }
}
