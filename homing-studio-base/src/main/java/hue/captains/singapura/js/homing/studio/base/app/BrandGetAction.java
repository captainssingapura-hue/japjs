package hue.captains.singapura.js.homing.studio.base.app;

import hue.captains.singapura.js.homing.server.EmptyParam;
import hue.captains.singapura.js.homing.studio.base.DocContent;
import hue.captains.singapura.tao.http.action.GetAction;
import hue.captains.singapura.tao.http.action.ParamMarshaller;
import io.vertx.ext.web.RoutingContext;

import java.util.concurrent.CompletableFuture;

/**
 * {@code GET /brand} — emits the active studio brand as JSON. Consumed by
 * AppModules that need the brand (label / logo / homeUrl) but don't have
 * access to the {@link StudioBrand} at construction time
 * ({@code DocReader}, {@code DocBrowserAppModule}).
 *
 * <p>Catalogue / Plan host modules don't need this endpoint — their host
 * actions ({@link CatalogueGetAction}, {@link
 * hue.captains.singapura.js.homing.studio.base.tracker.PlanGetAction}) already
 * inline the brand into the response payload they're already returning.</p>
 *
 * <p>Response shape:</p>
 * <pre>{@code
 * {
 *   "label":   "Homing · studio",
 *   "logo":    "<svg…>…</svg>"          // empty string when no logo configured
 *   "homeUrl": "/app?app=catalogue&id=…" // "/" when no catalogues registered
 * }
 * }</pre>
 *
 * <p>Sensible defaults when the bootstrap registered no {@link StudioBrand}
 * (legacy minimal configurations): label {@code "Homing · studio"}, empty
 * logo, homeUrl {@code "/"}.</p>
 */
public class BrandGetAction
        implements GetAction<RoutingContext, EmptyParam.NoQuery, EmptyParam.NoHeaders, DocContent> {

    private final StudioBrand brand;            // nullable
    private final boolean hasCatalogues;        // gate for the homeUrl shape

    public BrandGetAction(StudioBrand brand, boolean hasCatalogues) {
        this.brand = brand;
        this.hasCatalogues = hasCatalogues;
    }

    @Override
    public ParamMarshaller._QueryString<RoutingContext, EmptyParam.NoQuery> queryStrMarshaller() {
        return ctx -> new EmptyParam.NoQuery();
    }

    @Override
    public ParamMarshaller._Header<RoutingContext, EmptyParam.NoHeaders> headerMarshaller() {
        return ctx -> new EmptyParam.NoHeaders();
    }

    @Override
    public CompletableFuture<DocContent> execute(EmptyParam.NoQuery query, EmptyParam.NoHeaders headers) {
        String label   = (brand != null) ? brand.label() : "Homing · studio";
        String logo    = (brand != null && brand.logo() != null)
                ? brand.logo().resolve().orElse("")
                : "";
        String homeUrl = (brand != null && hasCatalogues)
                ? "/app?app=catalogue&id=" + brand.homeApp().getName()
                : "/";

        String json = "{"
                + "\"label\":"   + jstr(label)   + ","
                + "\"logo\":"    + jstr(logo)    + ","
                + "\"homeUrl\":" + jstr(homeUrl)
                + "}";
        return CompletableFuture.completedFuture(
                new DocContent(json, "application/json; charset=utf-8"));
    }

    private static String jstr(String s) {
        if (s == null) return "null";
        return "\"" + s.replace("\\", "\\\\").replace("\"", "\\\"")
                       .replace("\n", "\\n").replace("\r", "\\r") + "\"";
    }
}
