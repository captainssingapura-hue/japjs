package hue.captains.singapura.japjs.demo.es;

import hue.captains.singapura.japjs.core.*;

import java.util.List;

public record AnimalCell() implements DomModule<AnimalCell> {

    record createAnimalCell() implements Exportable._Constant<AnimalCell> {}
    record createAnimalSelector() implements Exportable._Constant<AnimalCell> {}

    public static final AnimalCell INSTANCE = new AnimalCell();

    @Override
    public ImportsFor<AnimalCell> imports() {
        return ImportsFor.<AnimalCell>builder()
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
    public ExportsOf<AnimalCell> exports() {
        return new ExportsOf<>(INSTANCE, List.of(new createAnimalCell(), new createAnimalSelector()));
    }
}
