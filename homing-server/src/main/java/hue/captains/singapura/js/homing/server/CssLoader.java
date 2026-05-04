package hue.captains.singapura.js.homing.server;

import hue.captains.singapura.js.homing.core.*;

import java.util.List;

/**
 * Framework-level EsModule that provides a CSS loader singleton.
 * Modules with CSS dependencies auto-import {@code CssLoaderInstance}
 * and call {@code CssLoaderInstance.load(cssBeing)} at the top level.
 */
public record CssLoader() implements EsModule<CssLoader> {

    public static final CssLoader INSTANCE = new CssLoader();

    public record CssLoaderInstance() implements Exportable._Constant<CssLoader> {}

    @Override
    public ImportsFor<CssLoader> imports() {
        return ImportsFor.noImports();
    }

    @Override
    public ExportsOf<CssLoader> exports() {
        return new ExportsOf<>(INSTANCE, List.of(new CssLoaderInstance()));
    }
}
