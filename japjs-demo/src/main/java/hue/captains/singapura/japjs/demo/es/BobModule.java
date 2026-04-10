package hue.captains.singapura.japjs.demo.es;

import hue.captains.singapura.japjs.core.*;

import java.util.List;

public record BobModule() implements DomModule<BobModule> {

    public static final BobModule INSTANCE = new BobModule();

    public record Bob() implements Exportable._Class<BobModule>{}

    @Override
    public ImportsFor<BobModule> imports() {
        return ImportsFor.<BobModule>builder()
                .add(new ModuleImports<>(List.of(new Alice.Alice1(), new Alice.Alice2(), new Alice.AliceClass()), Alice.INSTANCE))
                .add(new ModuleImports<>(List.of(
                        new Wonderland.CheshireCat(),
                        new Wonderland.WhiteRabbit(),
                        new Wonderland.MadHatter(),
                        new Wonderland.QueenOfHearts()
                ), Wonderland.INSTANCE))
                .build();
    }

    @Override
    public ExportsOf<BobModule> exports() {
        return new ExportsOf<>(BobModule.INSTANCE, List.of(new Bob()));
    }
}
