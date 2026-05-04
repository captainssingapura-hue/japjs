package hue.captains.singapura.js.homing.demo.es;

import hue.captains.singapura.js.homing.core.*;

import java.util.List;

public record WonderlandDemo() implements AppModule<WonderlandDemo> {

    // lowercase record name — maps to JS identifier "appMain"
    record appMain() implements AppModule._AppMain<WonderlandDemo> {}

    public record link() implements AppLink<WonderlandDemo> {}

    public static final WonderlandDemo INSTANCE = new WonderlandDemo();

    @Override
    public String title() {
        return "Wonderland Demo";
    }

    @Override
    public ImportsFor<WonderlandDemo> imports() {
        return ImportsFor.<WonderlandDemo>builder()
                .add(new ModuleImports<>(List.of(new BobModule.Bob()), BobModule.INSTANCE))
                .build();
    }

    @Override
    public ExportsOf<WonderlandDemo> exports() {
        return new ExportsOf<>(INSTANCE, List.of(new appMain()));
    }
}
