package hue.captains.singapura.japjs.demo.es;

import hue.captains.singapura.japjs.core.*;

import java.util.List;

public record PlatformEngine() implements EsModule<PlatformEngine> {

    record createPlatformEngine() implements Exportable._Constant<PlatformEngine> {}

    public static final PlatformEngine INSTANCE = new PlatformEngine();

    @Override
    public ImportsFor<PlatformEngine> imports() {
        return ImportsFor.<PlatformEngine>builder().build();
    }

    @Override
    public ExportsOf<PlatformEngine> exports() {
        return new ExportsOf<>(INSTANCE, List.of(new createPlatformEngine()));
    }
}
