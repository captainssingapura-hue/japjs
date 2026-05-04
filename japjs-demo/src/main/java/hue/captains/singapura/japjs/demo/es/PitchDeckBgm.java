package hue.captains.singapura.japjs.demo.es;

import hue.captains.singapura.japjs.core.*;

import java.util.List;

public record PitchDeckBgm() implements DomModule<PitchDeckBgm> {

    record getBgm() implements Exportable._Constant<PitchDeckBgm> {}

    public static final PitchDeckBgm INSTANCE = new PitchDeckBgm();

    @Override
    public ImportsFor<PitchDeckBgm> imports() {
        return ImportsFor.<PitchDeckBgm>builder().build();
    }

    @Override
    public ExportsOf<PitchDeckBgm> exports() {
        return new ExportsOf<>(INSTANCE, List.of(new getBgm()));
    }
}
