package hue.captains.singapura.js.homing.studio.base.app;

import hue.captains.singapura.js.homing.server.EmptyParam;
import hue.captains.singapura.js.homing.server.ResourceNotFound;
import hue.captains.singapura.js.homing.studio.base.Doc;
import hue.captains.singapura.tao.http.action.GetAction;
import hue.captains.singapura.tao.http.action.Param;
import hue.captains.singapura.tao.http.action.ParamMarshaller;
import hue.captains.singapura.js.homing.studio.base.DocContent;
import io.vertx.ext.web.RoutingContext;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

/**
 * {@code GET /catalogue?id=<class-fqn>} — serves a {@link Catalogue}'s resolved data
 * as JSON for the {@code CatalogueAppHost}'s renderer to consume.
 *
 * <p>Per <a href="../../../../../../../../../../docs/rfcs/Rfc0005Doc.md">RFC 0005</a>,
 * the catalogue's structural data is augmented server-side with all derived display
 * data: per-entry display fields (title/summary/url for Doc; name/summary/url for
 * sub-Catalogue and App entries), the breadcrumb chain (from the registry's parent
 * index), and the brand label + home URL. The renderer receives one fully-resolved
 * payload and emits HTML — no client-side resolution needed.</p>
 *
 * <p>Response shape:</p>
 * <pre>{@code
 * {
 *   "name":    "...",
 *   "summary": "...",
 *   "brand":   { "label": "...", "homeUrl": "..." },
 *   "breadcrumbs": [ { "name": "...", "url": "..." }, ... ],   // root → leaf
 *   "entries": [
 *     { "kind": "doc",       "title": "...", "summary": "...", "category": "...", "url": "/app?app=doc-reader&doc=<uuid>" },
 *     { "kind": "catalogue", "name":  "...", "summary": "...", "category": "CATALOGUE", "url": "/app?app=catalogue&id=<fqn>" },
 *     { "kind": "app",       "name":  "...", "summary": "...", "category": "APP",       "url": "/app?app=<simpleName>"       },
 *     { "kind": "plan",      "name":  "...", "summary": "...", "category": "...", "url": "/app?app=plan&id=<fqn>"           }
 *   ]
 * }
 * }</pre>
 *
 * @since RFC 0005
 */
public class CatalogueGetAction
        implements GetAction<RoutingContext, CatalogueGetAction.Query, EmptyParam.NoHeaders, DocContent> {

    public record Query(String id) implements Param._QueryString {}

    private final CatalogueRegistry registry;

    public CatalogueGetAction(CatalogueRegistry registry) {
        this.registry = Objects.requireNonNull(registry, "registry");
    }

    @Override
    public ParamMarshaller._QueryString<RoutingContext, Query> queryStrMarshaller() {
        return ctx -> new Query(ctx.request().getParam("id"));
    }

    @Override
    public ParamMarshaller._Header<RoutingContext, EmptyParam.NoHeaders> headerMarshaller() {
        return ctx -> new EmptyParam.NoHeaders();
    }

    @Override
    public CompletableFuture<DocContent> execute(Query query, EmptyParam.NoHeaders headers) {
        String fqn = query.id();
        if (fqn == null || fqn.isBlank()) {
            return CompletableFuture.failedFuture(
                    notFound("id", "Required query parameter 'id' was not provided"));
        }
        Class<?> raw;
        try {
            raw = Class.forName(fqn);
        } catch (ClassNotFoundException e) {
            return CompletableFuture.failedFuture(notFound(fqn, "Class not found"));
        }
        if (!Catalogue.class.isAssignableFrom(raw)) {
            return CompletableFuture.failedFuture(notFound(fqn, "Class is not a Catalogue"));
        }
        @SuppressWarnings("unchecked")
        Class<? extends Catalogue> cls = (Class<? extends Catalogue>) raw;
        Catalogue catalogue = registry.resolve(cls);
        if (catalogue == null) {
            return CompletableFuture.failedFuture(notFound(fqn, "Catalogue not registered"));
        }
        try {
            String body = serialize(catalogue);
            return CompletableFuture.completedFuture(new DocContent(body, "application/json; charset=utf-8"));
        } catch (Exception e) {
            return CompletableFuture.failedFuture(notFound(fqn,
                    "Failed to serialise catalogue: " + e.getMessage()));
        }
    }

    String serialize(Catalogue c) {
        StringBuilder sb = new StringBuilder("{");
        sb.append("\"name\":")   .append(jstr(c.name())).append(',');
        sb.append("\"summary\":").append(jstr(c.summary())).append(',');

        // Brand — `logo` is the resolved SVG markup string (server-side read of
        // the typed SvgRef), or empty when no logo is configured. The renderer's
        // Brand() component falls back to the dot for empty strings.
        StudioBrand brand = registry.brand();
        String logoSvg = (brand.logo() != null) ? brand.logo().resolve().orElse("") : "";
        sb.append("\"brand\":{")
          .append("\"label\":")  .append(jstr(brand.label())).append(',')
          .append("\"logo\":")   .append(jstr(logoSvg)).append(',')
          .append("\"homeUrl\":").append(jstr(catalogueUrl(brand.homeApp().getName())))
          .append("},");

        // Breadcrumbs (root → leaf).
        sb.append("\"breadcrumbs\":[");
        List<Catalogue> crumbs = registry.breadcrumbs(c.getClass());
        boolean firstCrumb = true;
        for (Catalogue ck : crumbs) {
            if (!firstCrumb) sb.append(',');
            firstCrumb = false;
            String url = (ck.getClass() == c.getClass()) ? "" : catalogueUrl(ck.getClass().getName());
            sb.append("{\"name\":").append(jstr(ck.name()))
              .append(",\"url\":") .append(jstr(url))
              .append('}');
        }
        sb.append("],");

        // Entries — RFC 0005-ext2 Option A: typed sub-catalogues first
        // (rendered as catalogue cards), then leaves (Doc / App / Plan).
        // Within each group the catalogue's declared order is preserved.
        sb.append("\"entries\":[");
        boolean firstEntry = true;

        // ---- Sub-catalogues ----
        for (Catalogue child : c.subCatalogues()) {
            if (!firstEntry) sb.append(',');
            firstEntry = false;
            // Renders as a Card (uniform with Doc/Plan entries) — `category`
            // is a fixed "CATALOGUE" badge so a mixed-kind listing reads at
            // a glance which entries drill into further sub-catalogues.
            sb.append("{\"kind\":\"catalogue\",")
              .append("\"name\":")    .append(jstr(child.name())).append(',')
              .append("\"summary\":") .append(jstr(child.summary())).append(',')
              .append("\"category\":").append(jstr("CATALOGUE")).append(',')
              .append("\"url\":")     .append(jstr(catalogueUrl(child.getClass().getName())))
              .append('}');
        }

        // ---- Leaves ----
        for (Entry e : c.leaves()) {
            if (!firstEntry) sb.append(',');
            firstEntry = false;
            switch (e) {
                case Entry.OfDoc(Doc d) -> {
                    sb.append("{\"kind\":\"doc\",")
                      .append("\"title\":")   .append(jstr(d.title())).append(',')
                      .append("\"summary\":") .append(jstr(d.summary())).append(',')
                      .append("\"category\":").append(jstr(d.category())).append(',')
                      .append("\"url\":")     .append(jstr(docReaderUrl(d.uuid().toString())))
                      .append('}');
                }
                case Entry.OfApp(Navigable<?, ?> nav) -> {
                    // Navigable carries the bound (App, Params, name, summary) tuple —
                    // its url() returns the fully-formed URL with query string baked in.
                    sb.append("{\"kind\":\"app\",")
                      .append("\"name\":")    .append(jstr(nav.name())).append(',')
                      .append("\"summary\":") .append(jstr(nav.summary())).append(',')
                      .append("\"category\":").append(jstr("APP")).append(',')
                      .append("\"url\":")     .append(jstr(nav.url()))
                      .append('}');
                }
                case Entry.OfPlan(hue.captains.singapura.js.homing.studio.base.tracker.Plan plan) -> {
                    // RFC 0005-ext1: Plan tile in a catalogue listing. URL goes to
                    // the shared PlanAppHost. Renders as a Card (same shape as Doc
                    // entries) so a catalogue listing reads uniformly — `category`
                    // is the plan's `kicker()` (e.g. "RFC 0001"), or "PLAN" as
                    // fallback when no kicker is set.
                    String badge = (plan.kicker() == null || plan.kicker().isBlank())
                            ? "PLAN" : plan.kicker();
                    sb.append("{\"kind\":\"plan\",")
                      .append("\"name\":")    .append(jstr(plan.name())).append(',')
                      .append("\"summary\":") .append(jstr(plan.summary())).append(',')
                      .append("\"category\":").append(jstr(badge)).append(',')
                      .append("\"url\":")     .append(jstr(planUrl(plan.getClass().getName())))
                      .append('}');
                }
            }
        }
        sb.append("]}");
        return sb.toString();
    }

    private static String catalogueUrl(String fqn) {
        return "/app?app=catalogue&id=" + fqn;
    }

    private static String docReaderUrl(String uuid) {
        return "/app?app=doc-reader&doc=" + uuid;
    }

    private static String appUrl(String simpleName) {
        return "/app?app=" + simpleName;
    }

    private static String planUrl(String fqn) {
        return "/app?app=plan&id=" + fqn;
    }

    private static String jstr(String v) {
        if (v == null) return "null";
        StringBuilder sb = new StringBuilder("\"");
        for (int i = 0; i < v.length(); i++) {
            char c = v.charAt(i);
            switch (c) {
                case '\\' -> sb.append("\\\\");
                case '"'  -> sb.append("\\\"");
                case '\n' -> sb.append("\\n");
                case '\r' -> sb.append("\\r");
                case '\t' -> sb.append("\\t");
                default -> {
                    if (c < 0x20) sb.append(String.format("\\u%04x", (int) c));
                    else sb.append(c);
                }
            }
        }
        sb.append('"');
        return sb.toString();
    }

    private static ResourceNotFound notFound(String resource, String reason) {
        return new ResourceNotFound(
                new ResourceNotFound._InternalError(null, reason + ": " + resource),
                new ResourceNotFound._ExternalError(resource, reason)
        );
    }
}
