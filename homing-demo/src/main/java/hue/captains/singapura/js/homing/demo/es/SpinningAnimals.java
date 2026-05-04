package hue.captains.singapura.js.homing.demo.es;

import hue.captains.singapura.js.homing.core.*;
import hue.captains.singapura.js.homing.demo.css.SpinningStyles;

import java.util.List;

public record SpinningAnimals() implements AppModule<SpinningAnimals> {

    record appMain() implements AppModule._AppMain<SpinningAnimals> {}

    public record link() implements AppLink<SpinningAnimals> {}

    public static final SpinningAnimals INSTANCE = new SpinningAnimals();

    @Override
    public String title() {
        return "Spinning Animals";
    }

    @Override
    public ImportsFor<SpinningAnimals> imports() {
        return ImportsFor.<SpinningAnimals>builder()
                .add(new ModuleImports<>(List.of(new AnimalCell.createAnimalCell(), new AnimalCell.createAnimalSelector()), AnimalCell.INSTANCE))
                .add(new ModuleImports<>(List.of(
                        new SpinningStyles.spin_title(),
                        new SpinningStyles.spin_hint(),
                        new SpinningStyles.spin_controls(),
                        new SpinningStyles.spin_grid(),
                        new SpinningStyles.spin_cell(),
                        new SpinningStyles.paused()
                ), SpinningStyles.INSTANCE))
                .build();
    }

    @Override
    public ExportsOf<SpinningAnimals> exports() {
        return new ExportsOf<>(INSTANCE, List.of(new appMain()));
    }
}
