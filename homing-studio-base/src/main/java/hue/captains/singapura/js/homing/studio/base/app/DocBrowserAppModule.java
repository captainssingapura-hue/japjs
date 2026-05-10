package hue.captains.singapura.js.homing.studio.base.app;

import hue.captains.singapura.js.homing.core.AppModule;
import hue.captains.singapura.js.homing.core.ModuleNameResolver;
import hue.captains.singapura.js.homing.core.SelfContent;

import java.util.List;

/**
 * Generic AppModule for a doc-browser page. Concrete browsers implement
 * {@link #docBrowserData()} and inherit the auto-generated JS body.
 */
public interface DocBrowserAppModule<M extends DocBrowserAppModule<M>> extends AppModule<AppModule._None, M>, SelfContent {

    DocBrowserData docBrowserData();

    default String brandLabel() { return "Homing · studio"; }

    /** RFC 0005: URL the brand link navigates to. Default {@code "/"} (root redirect lands on home). */
    default String homeUrl() { return "/"; }

    @Override
    default String title() {
        return brandLabel() + " · " + docBrowserData().title().toLowerCase();
    }

    @Override
    default List<String> selfContent(ModuleNameResolver nameResolver) {
        String json = DocBrowserJson.of(docBrowserData());
        // Brand (label + logo + homeUrl) comes from /brand — populated from
        // StudioBrand at boot. brandLabel() / homeUrl() defaults remain as
        // a safety net for the /brand action when no StudioBrand is registered.
        return List.of(
                "const docBrowserData = " + json + ";",
                "function appMain(rootElement) {",
                "    fetch(\"/brand\").then(function(r) { return r.json(); }).then(function(brand) {",
                "        rootElement.replaceChildren(renderDocBrowser({",
                "            data:  docBrowserData,",
                "            brand: { href: brand.homeUrl, label: brand.label, logo: brand.logo }",
                "        }));",
                "    });",
                "}"
        );
    }
}
