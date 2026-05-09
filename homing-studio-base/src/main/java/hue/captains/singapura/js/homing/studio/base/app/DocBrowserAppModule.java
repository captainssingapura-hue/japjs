package hue.captains.singapura.js.homing.studio.base.app;

import hue.captains.singapura.js.homing.core.AppModule;
import hue.captains.singapura.js.homing.core.ModuleNameResolver;
import hue.captains.singapura.js.homing.core.SelfContent;

import java.util.List;

/**
 * Generic AppModule for a doc-browser page. Concrete browsers implement
 * {@link #docBrowserData()} and inherit the auto-generated JS body.
 */
public interface DocBrowserAppModule<M extends DocBrowserAppModule<M>> extends AppModule<M>, SelfContent {

    DocBrowserData docBrowserData();

    default String brandLabel() { return "Homing · studio"; }

    default String homeAppSimpleName() { return simpleName(); }

    @Override
    default String title() {
        return brandLabel() + " · " + docBrowserData().title().toLowerCase();
    }

    @Override
    default List<String> selfContent(ModuleNameResolver nameResolver) {
        String json    = DocBrowserJson.of(docBrowserData());
        String brandJs = jstr(brandLabel());
        String homeUrl = jstr("/app?app=" + homeAppSimpleName());
        return List.of(
                "const docBrowserData = " + json + ";",
                "function appMain(rootElement) {",
                "    var brand = { href: " + homeUrl + ", label: " + brandJs + " };",
                "    rootElement.replaceChildren(renderDocBrowser({ data: docBrowserData, brand: brand }));",
                "}"
        );
    }

    private static String jstr(String s) {
        if (s == null) return "null";
        return "\"" + s.replace("\\", "\\\\").replace("\"", "\\\"") + "\"";
    }
}
