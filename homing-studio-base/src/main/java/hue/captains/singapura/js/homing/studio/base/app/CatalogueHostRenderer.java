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
 * Shared JS renderer for {@link CatalogueAppHost} (RFC 0005). Consumes the JSON
 * served by {@link CatalogueGetAction} — fully-resolved catalogue payload with
 * brand, breadcrumbs, name, summary, and per-entry display data — and emits the
 * page DOM.
 *
 * <p>Pattern-matches on the JSON {@code kind} discriminator per entry
 * ({@code doc} / {@code catalogue} / {@code app}) and renders the appropriate tile
 * shape. Server pre-resolves all URLs; renderer does no URL construction.</p>
 *
 * <p>Coexists with the legacy {@link CatalogueRenderer} (consumed by old
 * {@link CatalogueAppModule}-based catalogues) until RFC 0005 Phase 7 deletes the
 * legacy kit.</p>
 *
 * @since RFC 0005
 */
public record CatalogueHostRenderer() implements DomModule<CatalogueHostRenderer> {

    public record renderCatalogueHost() implements Exportable._Constant<CatalogueHostRenderer> {}

    public static final CatalogueHostRenderer INSTANCE = new CatalogueHostRenderer();

    @Override
    public ImportsFor<CatalogueHostRenderer> imports() {
        return ImportsFor.<CatalogueHostRenderer>builder()
                .add(new ModuleImports<>(List.of(new HrefManager.HrefManagerInstance()),
                        HrefManager.INSTANCE))
                .add(new ModuleImports<>(List.of(
                        new StudioElements.Header(),
                        new StudioElements.Card(),
                        new StudioElements.Section(),
                        new StudioElements.Footer()
                ), StudioElements.INSTANCE))
                .add(new ModuleImports<>(List.of(
                        new StudioStyles.st_root(),
                        new StudioStyles.st_main(),
                        new StudioStyles.st_kicker(),
                        new StudioStyles.st_title(),
                        new StudioStyles.st_subtitle(),
                        new StudioStyles.st_loading(),
                        new StudioStyles.st_error()
                ), StudioStyles.INSTANCE))
                .build();
    }

    @Override
    public ExportsOf<CatalogueHostRenderer> exports() {
        return new ExportsOf<>(INSTANCE, List.of(new renderCatalogueHost()));
    }
}
