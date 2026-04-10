package hue.captains.singapura.japjs.demo.es;

import hue.captains.singapura.japjs.core.*;

import java.util.List;

/**
 * Theme-aware BGM module. Because it implements DomModule,
 * the theme query parameter propagates into its import URL,
 * allowing the server to serve theme-specific JS content
 * (e.g. PlatformerBgm.dracula.js) with fallback to the default.
 */
public record PlatformerBgm() implements DomModule<PlatformerBgm> {

    record getBgm() implements Exportable._Constant<PlatformerBgm> {}

    public static final PlatformerBgm INSTANCE = new PlatformerBgm();

    @Override
    public ImportsFor<PlatformerBgm> imports() {
        return ImportsFor.<PlatformerBgm>builder().build();
    }

    @Override
    public ExportsOf<PlatformerBgm> exports() {
        return new ExportsOf<>(INSTANCE, List.of(new getBgm()));
    }
}
