package hue.captains.singapura.js.homing.studio.base.app;

import hue.captains.singapura.js.homing.core.AppLink;
import hue.captains.singapura.js.homing.core.AppModule;
import hue.captains.singapura.js.homing.core.ExportsOf;
import hue.captains.singapura.js.homing.core.ImportsFor;
import hue.captains.singapura.js.homing.core.ModuleImports;
import hue.captains.singapura.js.homing.core.ModuleNameResolver;
import hue.captains.singapura.js.homing.core.SelfContent;

import java.util.List;

/**
 * Shared concrete AppModule for reading any markdown doc by path. Downstream
 * studios just register {@code DocReader.INSTANCE} alongside their other apps;
 * no per-studio subclass needed.
 *
 * <p>URL: {@code /app?app=doc-reader&path=<classpath-relative.md>}.
 * The reader fetches the bytes via {@code /doc?path=…} (served by
 * {@code DocGetAction} from this same module), renders with marked.js, and
 * builds a heading TOC sidebar.</p>
 *
 * <p>The auto-generated body uses the studio brand by default. Downstream
 * that wants a custom brand can subclass and override {@link #brandLabel()};
 * the typical case is just to use the shared instance.</p>
 */
public record DocReader() implements AppModule<DocReader>, SelfContent {

    record appMain() implements AppModule._AppMain<DocReader> {}

    public record link() implements AppLink<DocReader> {}

    /** Typed query parameter — classpath-relative path to the doc to render. */
    public record Params(String path) {}

    public static final DocReader INSTANCE = new DocReader();

    @Override public Class<?> paramsType() { return Params.class; }

    @Override public String title() { return "Homing · studio · doc"; }

    /** Brand label shown in the header. Override in a subclass for custom branding. */
    public String brandLabel() { return "Homing · studio"; }

    /**
     * Simple-name of the home / root catalogue app for the brand link. Default
     * {@code studio-catalogue} so the brand link goes home in the standard studio.
     * Downstream studios with a different home app should subclass and override.
     */
    public String homeAppSimpleName() { return "studio-catalogue"; }

    @Override
    public ImportsFor<DocReader> imports() {
        return ImportsFor.<DocReader>builder()
                .add(new ModuleImports<>(List.of(new DocReaderRenderer.renderDocReader()),
                        DocReaderRenderer.INSTANCE))
                .build();
    }

    @Override
    public ExportsOf<DocReader> exports() {
        return new ExportsOf<>(INSTANCE, List.of(new appMain()));
    }

    @Override
    public List<String> selfContent(ModuleNameResolver nameResolver) {
        String brandJs = jstr(brandLabel());
        String homeUrl = jstr("/app?app=" + homeAppSimpleName());
        return List.of(
                "function appMain(rootElement) {",
                "    var brand = { href: " + homeUrl + ", label: " + brandJs + " };",
                "    rootElement.replaceChildren(renderDocReader({",
                "        docPath:     params.path,",
                "        brand:       brand,",
                "        crumbsAbove: [{ text: \"Home\", href: " + homeUrl + " }]",
                "    }));",
                "}"
        );
    }

    private static String jstr(String s) {
        if (s == null) return "null";
        return "\"" + s.replace("\\", "\\\\").replace("\"", "\\\"") + "\"";
    }
}
