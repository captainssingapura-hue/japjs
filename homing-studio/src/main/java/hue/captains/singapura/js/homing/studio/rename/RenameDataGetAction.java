package hue.captains.singapura.js.homing.studio.rename;

import hue.captains.singapura.js.homing.server.EmptyParam;
import hue.captains.singapura.js.homing.server.ResourceNotFound;
import hue.captains.singapura.tao.http.action.GetAction;
import hue.captains.singapura.tao.http.action.Param;
import hue.captains.singapura.tao.http.action.ParamMarshaller;
import hue.captains.singapura.tao.http.action.TypedContent;
import io.vertx.ext.web.RoutingContext;

import java.util.concurrent.CompletableFuture;

/**
 * GET /rename-data            → JSON of decisions + all phases
 * GET /rename-data?phase=03   → JSON of one phase
 *
 * <p>Serves the live state of {@link RenameSteps}. Edit that file, recompile,
 * refresh — the new state appears in the studio.</p>
 */
public class RenameDataGetAction
        implements GetAction<RoutingContext, RenameDataGetAction.Query, EmptyParam.NoHeaders, RenameDataGetAction.Json> {

    public record Query(String phaseId) implements Param._QueryString {}

    public record Json(String body) implements TypedContent {
        @Override public String contentType() { return "application/json; charset=utf-8"; }
    }

    @Override
    public ParamMarshaller._QueryString<RoutingContext, Query> queryStrMarshaller() {
        return ctx -> new Query(ctx.request().getParam("phase"));
    }

    @Override
    public ParamMarshaller._Header<RoutingContext, EmptyParam.NoHeaders> headerMarshaller() {
        return ctx -> new EmptyParam.NoHeaders();
    }

    @Override
    public CompletableFuture<Json> execute(Query query, EmptyParam.NoHeaders headers) {
        if (query.phaseId() == null || query.phaseId().isBlank()) {
            return CompletableFuture.completedFuture(new Json(serializeAll()));
        }
        var phase = RenameSteps.phaseById(query.phaseId());
        if (phase == null) {
            return CompletableFuture.failedFuture(notFound(query.phaseId(), "Unknown phase id"));
        }
        return CompletableFuture.completedFuture(new Json(serializePhase(phase)));
    }

    // ---- minimal JSON serialization (no external dep) --------------------

    private static String serializeAll() {
        StringBuilder sb = new StringBuilder();
        sb.append("{\"oldName\":").append(jstr(RenameSteps.OLD_NAME)).append(",")
          .append("\"newName\":").append(jstr(RenameSteps.NEW_NAME)).append(",")
          .append("\"executionDoc\":").append(jstr(RenameSteps.EXECUTION_DOC)).append(",")
          .append("\"dossierDoc\":").append(jstr(RenameSteps.DOSSIER_DOC)).append(",")
          .append("\"totalProgress\":").append(RenameSteps.totalProgressPercent()).append(",")
          .append("\"openDecisions\":").append(RenameSteps.openDecisionsCount()).append(",")
          .append("\"decisions\":[");
        boolean firstD = true;
        for (var d : RenameSteps.DECISIONS) {
            if (!firstD) sb.append(","); firstD = false;
            sb.append(serializeDecision(d));
        }
        sb.append("],\"phases\":[");
        boolean firstP = true;
        for (var p : RenameSteps.PHASES) {
            if (!firstP) sb.append(","); firstP = false;
            sb.append(serializePhase(p));
        }
        sb.append("]}");
        return sb.toString();
    }

    private static String serializeDecision(RenameSteps.Decision d) {
        return "{"
             + "\"id\":" + jstr(d.id()) + ","
             + "\"question\":" + jstr(d.question()) + ","
             + "\"recommendation\":" + jstr(d.recommendation()) + ","
             + "\"chosenValue\":" + jstr(d.chosenValue()) + ","
             + "\"status\":" + jstr(d.status().name()) + ","
             + "\"statusLabel\":" + jstr(d.status().label) + ","
             + "\"statusSlug\":" + jstr(d.status().slug) + ","
             + "\"rationale\":" + jstr(d.rationale()) + ","
             + "\"notes\":" + jstr(d.notes())
             + "}";
    }

    private static String serializePhase(RenameSteps.Phase p) {
        StringBuilder sb = new StringBuilder();
        sb.append("{")
          .append("\"id\":").append(jstr(p.id())).append(",")
          .append("\"label\":").append(jstr(p.label())).append(",")
          .append("\"summary\":").append(jstr(p.summary())).append(",")
          .append("\"description\":").append(jstr(p.description())).append(",")
          .append("\"status\":").append(jstr(p.status().name())).append(",")
          .append("\"statusLabel\":").append(jstr(p.status().label)).append(",")
          .append("\"statusSlug\":").append(jstr(p.status().slug)).append(",")
          .append("\"progress\":").append(p.progressPercent()).append(",")
          .append("\"tasks\":[");
        boolean firstT = true;
        for (var t : p.tasks()) {
            if (!firstT) sb.append(","); firstT = false;
            sb.append("{\"description\":").append(jstr(t.description()))
              .append(",\"done\":").append(t.done()).append("}");
        }
        sb.append("],\"dependsOn\":[");
        boolean firstD = true;
        for (var d : p.dependsOn()) {
            if (!firstD) sb.append(","); firstD = false;
            sb.append("{\"phaseId\":").append(jstr(d.phaseId()))
              .append(",\"reason\":").append(jstr(d.reason())).append("}");
        }
        sb.append("],")
          .append("\"verification\":").append(jstr(p.verification())).append(",")
          .append("\"rollback\":").append(jstr(p.rollback())).append(",")
          .append("\"effort\":").append(jstr(p.effort())).append(",")
          .append("\"notes\":").append(jstr(p.notes()))
          .append("}");
        return sb.toString();
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
                case '\b' -> sb.append("\\b");
                case '\f' -> sb.append("\\f");
                default -> {
                    if (c < 0x20) sb.append(String.format("\\u%04x", (int) c));
                    else sb.append(c);
                }
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
