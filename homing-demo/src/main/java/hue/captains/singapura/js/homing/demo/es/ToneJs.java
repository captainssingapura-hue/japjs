package hue.captains.singapura.js.homing.demo.es;

import hue.captains.singapura.js.homing.core.ExternalModule;
import hue.captains.singapura.js.homing.core.Exportable;
import hue.captains.singapura.js.homing.core.ExportsOf;

import java.util.List;

public record ToneJs() implements ExternalModule<ToneJs> {

    public static final ToneJs INSTANCE = new ToneJs();

    public record Synth() implements Exportable._Class<ToneJs> {}
    public record MembraneSynth() implements Exportable._Class<ToneJs> {}
    public record NoiseSynth() implements Exportable._Class<ToneJs> {}
    public record Filter() implements Exportable._Class<ToneJs> {}
    public record start() implements Exportable._Constant<ToneJs> {}

    @Override
    public ExportsOf<ToneJs> exports() {
        return new ExportsOf<>(INSTANCE, List.of(
                new Synth(),
                new MembraneSynth(),
                new NoiseSynth(),
                new Filter(),
                new start()
        ));
    }
}
