package hue.captains.singapura.js.homing.server;

import hue.captains.singapura.js.homing.core.*;

import java.util.List;

/**
 * Framework-level EsModule that provides CSS class management.
 * <p>Subsumes {@link CssLoader}: handles CSS file loading and provides
 * type-safe CSS class operations (addClass, removeClass, etc.) using
 * frozen CssClass objects.</p>
 */
public record CssClassManager() implements EsModule<CssClassManager> {

    public static final CssClassManager INSTANCE = new CssClassManager();

    public record CssClassManagerInstance() implements Exportable._Constant<CssClassManager> {}

    @Override
    public ImportsFor<CssClassManager> imports() {
        return ImportsFor.noImports();
    }

    @Override
    public ExportsOf<CssClassManager> exports() {
        return new ExportsOf<>(INSTANCE, List.of(new CssClassManagerInstance()));
    }
}
