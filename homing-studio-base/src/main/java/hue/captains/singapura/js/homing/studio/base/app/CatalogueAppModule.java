package hue.captains.singapura.js.homing.studio.base.app;

import hue.captains.singapura.js.homing.core.AppModule;
import hue.captains.singapura.js.homing.core.ModuleNameResolver;
import hue.captains.singapura.js.homing.core.SelfContent;

import java.util.List;

/**
 * Generic AppModule for a studio catalogue page (launcher / sub-catalogue /
 * doc-list). Each concrete catalogue (StudioCatalogue, JourneysCatalogue,
 * DoctrineCatalogue, …) implements this interface, supplies its
 * {@link #catalogueData()}, and inherits the auto-generated JS body — no
 * per-catalogue {@code .js} resource needed.
 *
 * <p>Concrete catalogues still declare their own {@code appMain} / {@code link}
 * marker records, {@code INSTANCE} field, and {@code imports()} / {@code exports()}
 * because those references are typed by the concrete self-type. Imports
 * include navigation targets (the apps the catalogue's tiles point to) plus
 * {@code CatalogueRenderer.renderCatalogue()}.</p>
 */
public interface CatalogueAppModule<M extends CatalogueAppModule<M>> extends AppModule<M>, SelfContent {

    /** The catalogue's data. */
    CatalogueData catalogueData();

    /** Brand label for the header. Default: {@code "Homing · studio"}. */
    default String brandLabel() {
        return "Homing · studio";
    }

    /** Simple-name of the home / root catalogue app for the brand link. Default: this app's own simpleName. */
    default String homeAppSimpleName() {
        return simpleName();
    }

    @Override
    default String title() {
        return brandLabel() + " · " + catalogueData().title().toLowerCase();
    }

    @Override
    default List<String> selfContent(ModuleNameResolver nameResolver) {
        String json    = CatalogueJson.of(catalogueData());
        String brandJs = jstr(brandLabel());
        String homeUrl = jstr("/app?app=" + homeAppSimpleName());
        return List.of(
                "const catalogueData = " + json + ";",
                "function appMain(rootElement) {",
                "    var brand = { href: " + homeUrl + ", label: " + brandJs + " };",
                "    rootElement.replaceChildren(renderCatalogue({ data: catalogueData, brand: brand }));",
                "}"
        );
    }

    private static String jstr(String s) {
        if (s == null) return "null";
        return "\"" + s.replace("\\", "\\\\").replace("\"", "\\\"") + "\"";
    }
}
