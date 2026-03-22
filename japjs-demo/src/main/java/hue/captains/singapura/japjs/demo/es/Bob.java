package hue.captains.singapura.japjs.demo.es;

import hue.captains.singapura.japjs.core.EsModule;
import hue.captains.singapura.japjs.core.ExportsOf;
import hue.captains.singapura.japjs.core.ImportsFor;
import hue.captains.singapura.japjs.core.ModuleImports;

import java.util.List;

public record Bob() implements EsModule<Bob> {

    public static final Bob INSTANCE = new Bob();

    @Override
    public ImportsFor<Bob> imports() {
        return ImportsFor.<Bob>builder()
                .add(new ModuleImports<>(List.of(new Alice.AliceConst()), Alice.INSTANCE))
                .build();
    }

    @Override
    public ExportsOf<Bob> exports() {
        return new ExportsOf<>(Bob.INSTANCE, List.of());
    }
}
