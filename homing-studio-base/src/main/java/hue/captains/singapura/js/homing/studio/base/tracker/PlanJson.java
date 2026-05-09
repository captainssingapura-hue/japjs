package hue.captains.singapura.js.homing.studio.base.tracker;

/**
 * Serialises a {@link Plan} to a JSON literal embeddable in JavaScript. The
 * output's field names match what {@code PlanRenderer.js} expects.
 *
 * <p>Hand-rolled rather than Jackson-based to keep {@code homing-studio} free
 * of a JSON dependency. The shape is small and stable, so the cost is low.</p>
 */
public final class PlanJson {

    private PlanJson() {}

    public static String of(Plan plan) {
        StringBuilder sb = new StringBuilder("{");
        sb.append("\"kicker\":")        .append(jstr(plan.kicker())).append(",");
        sb.append("\"title\":")         .append(jstr(plan.title())).append(",");
        sb.append("\"subtitle\":")      .append(jstr(plan.subtitle())).append(",");
        sb.append("\"totalProgress\":") .append(plan.totalProgress()).append(",");
        sb.append("\"openDecisions\":") .append(plan.openDecisions()).append(",");
        sb.append("\"executionDoc\":")  .append(jstr(plan.executionDoc())).append(",");
        sb.append("\"dossierDoc\":")    .append(jstr(plan.dossierDoc())).append(",");
        sb.append("\"decisions\":[");
        boolean firstD = true;
        for (Decision d : plan.decisions()) {
            if (!firstD) sb.append(",");
            firstD = false;
            sb.append(decision(d));
        }
        sb.append("],");
        sb.append("\"phases\":[");
        boolean firstP = true;
        for (Phase p : plan.phases()) {
            if (!firstP) sb.append(",");
            firstP = false;
            sb.append(phase(p));
        }
        sb.append("]}");
        return sb.toString();
    }

    private static String phase(Phase p) {
        StringBuilder sb = new StringBuilder("{");
        sb.append("\"id\":")          .append(jstr(p.id())).append(",");
        sb.append("\"label\":")       .append(jstr(p.label())).append(",");
        sb.append("\"summary\":")     .append(jstr(p.summary())).append(",");
        sb.append("\"description\":") .append(jstr(p.description())).append(",");
        sb.append("\"statusSlug\":")  .append(jstr(p.status().slug)).append(",");
        sb.append("\"statusLabel\":") .append(jstr(p.status().label)).append(",");
        sb.append("\"progress\":")    .append(p.progressPercent()).append(",");
        sb.append("\"effort\":")      .append(jstr(p.effort())).append(",");
        sb.append("\"verification\":").append(jstr(p.verification())).append(",");
        sb.append("\"rollback\":")    .append(jstr(p.rollback())).append(",");
        sb.append("\"notes\":")       .append(jstr(p.notes())).append(",");
        sb.append("\"tasks\":[");
        boolean firstT = true;
        for (Task t : p.tasks()) {
            if (!firstT) sb.append(",");
            firstT = false;
            sb.append("{\"description\":").append(jstr(t.description()))
              .append(",\"done\":").append(t.done()).append("}");
        }
        sb.append("],");
        sb.append("\"dependsOn\":[");
        boolean firstD = true;
        for (Dependency d : p.dependsOn()) {
            if (!firstD) sb.append(",");
            firstD = false;
            sb.append("{\"phaseId\":").append(jstr(d.phaseId()))
              .append(",\"reason\":").append(jstr(d.reason())).append("}");
        }
        sb.append("],");
        sb.append("\"metrics\":[");
        boolean firstM = true;
        for (Metric m : p.metrics()) {
            if (!firstM) sb.append(",");
            firstM = false;
            sb.append("{\"label\":") .append(jstr(m.label()))
              .append(",\"before\":").append(jstr(m.before()))
              .append(",\"after\":") .append(jstr(m.after()))
              .append(",\"delta\":") .append(jstr(m.delta())).append("}");
        }
        sb.append("]}");
        return sb.toString();
    }

    private static String decision(Decision d) {
        StringBuilder sb = new StringBuilder("{");
        sb.append("\"id\":")             .append(jstr(d.id())).append(",");
        sb.append("\"question\":")       .append(jstr(d.question())).append(",");
        sb.append("\"recommendation\":") .append(jstr(d.recommendation())).append(",");
        sb.append("\"chosenValue\":")    .append(jstr(d.chosenValue())).append(",");
        sb.append("\"statusSlug\":")     .append(jstr(d.status().slug)).append(",");
        sb.append("\"statusLabel\":")    .append(jstr(d.status().label)).append(",");
        sb.append("\"rationale\":")      .append(jstr(d.rationale())).append(",");
        sb.append("\"notes\":")          .append(jstr(d.notes()));
        sb.append("}");
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
}
