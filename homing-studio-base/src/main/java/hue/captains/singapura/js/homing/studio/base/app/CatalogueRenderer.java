package hue.captains.singapura.js.homing.studio.base.app;

import hue.captains.singapura.js.homing.core.DomModule;
import hue.captains.singapura.js.homing.core.Exportable;
import hue.captains.singapura.js.homing.core.ExportsOf;
import hue.captains.singapura.js.homing.core.ImportsFor;
import hue.captains.singapura.js.homing.core.ModuleImports;
import hue.captains.singapura.js.homing.server.HrefManager;
import hue.captains.singapura.js.homing.studio.base.css.StudioStyles;
import hue.captains.singapura.js.homing.studio.base.ui.StudioElements;

import java.util.List;

/**
 * Shared JS renderer for any {@link CatalogueAppModule}. Exports
 * {@code renderCatalogue({data, brand})} returning a {@code Node}; the kit's
 * auto-generated {@code appMain} embeds the data + invokes this.
 */
public record CatalogueRenderer() implements DomModule<CatalogueRenderer> {

    public record renderCatalogue() implements Exportable._Constant<CatalogueRenderer> {}

    public static final CatalogueRenderer INSTANCE = new CatalogueRenderer();

    @Override
    public ImportsFor<CatalogueRenderer> imports() {
        return ImportsFor.<CatalogueRenderer>builder()
                .add(new ModuleImports<>(List.of(new HrefManager.HrefManagerInstance()),
                        HrefManager.INSTANCE))
                .add(new ModuleImports<>(List.of(
                        new StudioElements.Header(),
                        new StudioElements.Card(),
                        new StudioElements.Pill(),
                        new StudioElements.Section(),
                        new StudioElements.Footer()
                ), StudioElements.INSTANCE))
                .add(new ModuleImports<>(List.of(
                        new StudioStyles.st_root(),
                        new StudioStyles.st_main(),
                        new StudioStyles.st_kicker(),
                        new StudioStyles.st_title(),
                        new StudioStyles.st_subtitle()
                ), StudioStyles.INSTANCE))
                .build();
    }

    @Override
    public ExportsOf<CatalogueRenderer> exports() {
        return new ExportsOf<>(INSTANCE, List.of(new renderCatalogue()));
    }
}
