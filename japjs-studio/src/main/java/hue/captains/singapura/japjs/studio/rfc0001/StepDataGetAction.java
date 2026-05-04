package hue.captains.singapura.japjs.studio.rfc0001;

import hue.captains.singapura.japjs.server.EmptyParam;
import hue.captains.singapura.japjs.server.ResourceNotFound;
import hue.captains.singapura.tao.http.action.GetAction;
import hue.captains.singapura.tao.http.action.Param;
import hue.captains.singapura.tao.http.action.ParamMarshaller;
import hue.captains.singapura.tao.http.action.TypedContent;
import io.vertx.ext.web.RoutingContext;

import java.util.concurrent.CompletableFuture;

/**
 * GET /step-data?rfc=0001              → JSON of all steps (for the plan view)
 * GET /step-data?rfc=0001&amp;id=03   → JSON of one step (for the step detail view)
 *
 * <p>Serves the live state of {@link Rfc0001Steps}. Edit the Java file,
 * recompile, refresh — the new state appears in the studio.</p>
 */
public class StepDataGetAction
        implements GetAction<RoutingContext, StepDataGetAction.Query, EmptyParam.NoHeaders, StepDataGetAction.Json> {

    public record Query(String rfc, String id) implements Param._QueryString {}

    public record Json(String body) implements TypedContent {
        @Override public String contentType() { return "application/json; charset=utf-8"; }
    }

    @Override
    public ParamMarshaller._QueryString<RoutingContext, Query> queryStrMarshaller() {
        return ctx -> new Query(ctx.request().getParam("rfc"), ctx.request().getParam("id"));
    }

    @Override
    public ParamMarshaller._Header<RoutingContext, EmptyParam.NoHeaders> headerMarshaller() {
        return ctx -> new EmptyParam.NoHeaders();
    }

    @Override
    public CompletableFuture<Json> execute(Query query, EmptyParam.NoHeaders headers) {
        if (query.rfc() == null || query.rfc().isBlank()) {
            return CompletableFuture.failedFuture(notFound("rfc", "Required query parameter 'rfc' missing"));
        }
        if (!"0001".equals(query.rfc())) {
            return CompletableFuture.failedFuture(notFound(query.rfc(), "Unknown RFC id"));
        }
        if (query.id() == null || query.id().isBlank()) {
            return CompletableFuture.completedFuture(new Json(serializeAll()));
        }
        var step = Rfc0001Steps.byId(query.id());
        if (step == null) {
            return CompletableFuture.failedFuture(notFound(query.id(), "Unknown step id"));
        }
        return CompletableFuture.completedFuture(new Json(serializeStep(step)));
    }

    // ---- minimal JSON serialization (no external dep) ----
    private static String serializeAll() {
        StringBuilder sb = new StringBuilder();
        sb.append("{\"rfc\":\"").append(Rfc0001Steps.RFC_ID).append("\",")
          .append("\"title\":").append(jstr(Rfc0001Steps.RFC_TITLE)).append(",")
          .append("\"path\":").append(jstr(Rfc0001Steps.RFC_PATH)).append(",")
          .append("\"totalProgress\":").append(Rfc0001Steps.totalProgressPercent()).append(",")
          .append("\"steps\":[");
        boolean first = true;
        for (var s : Rfc0001Steps.STEPS) {
            if (!first) sb.append(",");
            sb.append(serializeStep(s));
            first = false;
        }
        sb.append("]}");
        return sb.toString();
    }

    private static String serializeStep(Rfc0001Steps.Step s) {
        StringBuilder sb = new StringBuilder();
        sb.append("{")
          .append("\"id\":").append(jstr(s.id())).append(",")
          .append("\"label\":").append(jstr(s.label())).append(",")
          .append("\"summary\":").append(jstr(s.summary())).append(",")
          .append("\"description\":").append(jstr(s.description())).append(",")
          .append("\"status\":").append(jstr(s.status().name())).append(",")
          .append("\"statusLabel\":").append(jstr(s.status().label)).append(",")
          .append("\"statusSlug\":").append(jstr(s.status().slug)).append(",")
          .append("\"progress\":").append(s.progressPercent()).append(",")
          .append("\"tasks\":[");
        boolean firstT = true;
        for (var t : s.tasks()) {
            if (!firstT) sb.append(",");
            sb.append("{\"description\":").append(jstr(t.description()))
              .append(",\"done\":").append(t.done()).append("}");
            firstT = false;
        }
        sb.append("],\"dependsOn\":[");
        boolean firstD = true;
        for (var d : s.dependsOn()) {
            if (!firstD) sb.append(",");
            sb.append("{\"stepId\":").append(jstr(d.stepId()))
              .append(",\"reason\":").append(jstr(d.reason())).append("}");
            firstD = false;
        }
        sb.append("],")
          .append("\"acceptance\":").append(jstr(s.acceptance())).append(",")
          .append("\"effort\":").append(jstr(s.effort())).append(",")
          .append("\"rfcSection\":").append(jstr(s.rfcSection())).append(",")
          .append("\"notes\":").append(jstr(s.notes()))
          .append("}");
        return sb.toString();
    }

    private static String jstr(String v) {
        if (v == null) return "null";
        StringBuilder sb = new StringBuilder("\"");
        for (int i = 0; i < v.length(); i++) {
            char c = v.charAt(i);
            switch (c) {
                case '\\': sb.append("\\\\"); break;
                case '"':  sb.append("\\\""); break;
                case '\n': sb.append("\\n"); break;
                case '\r': sb.append("\\r"); break;
                case '\t': sb.append("\\t"); break;
                case '\b': sb.append("\\b"); break;
                case '\f': sb.append("\\f"); break;
                default:
                    if (c < 0x20) sb.append(String.format("\\u%04x", (int) c));
                    else sb.append(c);
            }
        }
        sb.append("\"");
        return sb.toString();
    }

    private static ResourceNotFound notFound(String resource, String reason) {
        return new ResourceNotFound(
                new ResourceNotFound._InternalError(null, reason + ": " + resource),
                new ResourceNotFound._ExternalError(resource, reason)
        );
    }
}
