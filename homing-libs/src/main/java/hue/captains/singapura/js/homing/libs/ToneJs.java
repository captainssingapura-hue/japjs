package hue.captains.singapura.js.homing.libs;

import hue.captains.singapura.js.homing.core.BundledExternalModule;
import hue.captains.singapura.js.homing.core.Exportable;
import hue.captains.singapura.js.homing.core.ExportsOf;

import java.util.List;

/**
 * Tone.js — Web Audio framework for music. Bundled at build time from esm.sh
 * and served from the homing.js classpath. No runtime CDN call.
 *
 * @see <a href="https://github.com/Tonejs/Tone.js">Tonejs/Tone.js</a>
 */
public record ToneJs() implements BundledExternalModule<ToneJs> {

    public static final ToneJs INSTANCE = new ToneJs();

    @Override public String sourceUrl()    { return "https://esm.sh/tone@15.1.22?bundle"; }
    @Override public String resourcePath() { return "lib/tone@15.1.22/tone.module.js"; }
    @Override public String sha512()       {
        return "073fe032559ab8c3ca4ec347bf5a088245736096c454a295bba87d025c1c1c29"
             + "e6fffc4512e198be5d761887063b5429dec761eba717d7ccf090e3d67d9aa502";
    }

    public record Synth()         implements Exportable._Class<ToneJs> {}
    public record MembraneSynth() implements Exportable._Class<ToneJs> {}
    public record NoiseSynth()    implements Exportable._Class<ToneJs> {}
    public record Filter()        implements Exportable._Class<ToneJs> {}
    public record start()         implements Exportable._Constant<ToneJs> {}

    @Override
    public ExportsOf<ToneJs> exports() {
        return new ExportsOf<>(INSTANCE, List.of(
                new Synth(), new MembraneSynth(), new NoiseSynth(), new Filter(), new start()
        ));
    }
}
