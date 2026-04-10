package hue.captains.singapura.japjs.demo.es;

import hue.captains.singapura.japjs.core.*;

import java.util.List;

public record JumpPhysics() implements EsModule<JumpPhysics> {

    record createJumpPhysics() implements Exportable._Constant<JumpPhysics> {}

    public static final JumpPhysics INSTANCE = new JumpPhysics();

    @Override
    public ImportsFor<JumpPhysics> imports() {
        return ImportsFor.<JumpPhysics>builder().build();
    }

    @Override
    public ExportsOf<JumpPhysics> exports() {
        return new ExportsOf<>(INSTANCE, List.of(new createJumpPhysics()));
    }
}
