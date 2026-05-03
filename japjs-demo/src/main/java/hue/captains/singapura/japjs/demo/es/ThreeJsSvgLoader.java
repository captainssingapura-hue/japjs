package hue.captains.singapura.japjs.demo.es;

import hue.captains.singapura.japjs.core.ExternalModule;
import hue.captains.singapura.japjs.core.Exportable;
import hue.captains.singapura.japjs.core.ExportsOf;

import java.util.List;

public record ThreeJsSvgLoader() implements ExternalModule<ThreeJsSvgLoader> {

    public static final ThreeJsSvgLoader INSTANCE = new ThreeJsSvgLoader();

    public record SVGLoader() implements Exportable._Class<ThreeJsSvgLoader> {}

    @Override
    public ExportsOf<ThreeJsSvgLoader> exports() {
        return new ExportsOf<>(INSTANCE, List.of(new SVGLoader()));
    }
}
