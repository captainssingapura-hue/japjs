package hue.captains.singapura.js.homing.demo.es;

import hue.captains.singapura.js.homing.core.*;
import hue.captains.singapura.js.homing.libs.ToneJs;
import hue.captains.singapura.js.homing.demo.css.PlaygroundStyles;

import java.util.List;

public record MovingAnimal() implements AppModule<MovingAnimal> {

    record appMain() implements AppModule._AppMain<MovingAnimal> {}

    public record link() implements AppLink<MovingAnimal> {}

    public static final MovingAnimal INSTANCE = new MovingAnimal();

    @Override
    public String title() {
        return "Moving Animal";
    }

    @Override
    public ImportsFor<MovingAnimal> imports() {
        return ImportsFor.<MovingAnimal>builder()
                .add(new ModuleImports<>(List.of(new AnimalCell.createAnimalCell(), new AnimalCell.createAnimalSelector()), AnimalCell.INSTANCE))
                .add(new ModuleImports<>(List.of(new JumpPhysics.createJumpPhysics()), JumpPhysics.INSTANCE))
                .add(new ModuleImports<>(List.of(new PlatformEngine.createPlatformEngine()), PlatformEngine.INSTANCE))
                .add(new ModuleImports<>(List.of(
                        new ToneJs.Synth(),
                        new ToneJs.MembraneSynth(),
                        new ToneJs.start()
                ), ToneJs.INSTANCE))
                .add(new ModuleImports<>(List.of(new PlatformerBgm.getBgm()), PlatformerBgm.INSTANCE))
                .add(new ModuleImports<>(List.of(
                        new PlaygroundStyles.pg_title(),
                        new PlaygroundStyles.pg_hint(),
                        new PlaygroundStyles.pg_controls(),
                        new PlaygroundStyles.pg_size_display(),
                        new PlaygroundStyles.pg_theme_switcher(),
                        new PlaygroundStyles.pg_theme_label(),
                        new PlaygroundStyles.pg_theme_btn(),
                        new PlaygroundStyles.pg_theme_btn_active(),
                        new PlaygroundStyles.pg_playground(),
                        new PlaygroundStyles.pg_sky(),
                        new PlaygroundStyles.pg_world(),
                        new PlaygroundStyles.pg_animal(),
                        new PlaygroundStyles.pg_platform(),
                        new PlaygroundStyles.pg_platform_active(),
                        new PlaygroundStyles.pg_vehicle(),
                        new PlaygroundStyles.pg_vehicle_v1(),
                        new PlaygroundStyles.pg_vehicle_v2(),
                        new PlaygroundStyles.pg_vehicle_v3(),
                        new PlaygroundStyles.pg_lava(),
                        new PlaygroundStyles.pg_score(),
                        new PlaygroundStyles.pg_gameover(),
                        new PlaygroundStyles.pg_final_score()
                ), PlaygroundStyles.INSTANCE))
                .build();
    }

    @Override
    public ExportsOf<MovingAnimal> exports() {
        return new ExportsOf<>(INSTANCE, List.of(new appMain()));
    }
}
