package hue.captains.singapura.js.homing.studio.base.graph;

import hue.captains.singapura.js.homing.studio.base.app.L0_Catalogue;

/**
 * RFC 0014 — top-level diagnostic catalogue. Registered as an
 * {@code L0_Catalogue} so it has a stable URL ({@code /app?app=catalogue&id=…
 * DiagnosticsCatalogue}) and a stable identity for {@link CatalogueRegistry}.
 *
 * <p>The catalogue itself carries <em>no typed entries</em>. All tiles are
 * supplied by the framework via the {@link DiagnosticsHub} augmentation —
 * either the per-studio parent tiles (multi-studio composition) or the
 * Object Graph / Type View tiles (single-studio composition or per-studio
 * context-scoped page). This keeps the tile inventory entirely under
 * framework control, so the rules can evolve (more view kinds, per-studio
 * scoping, etc.) without changing this record.</p>
 *
 * <p>Wired by {@code Bootstrap.compose()} only when
 * {@code RuntimeParams.diagnosticsEnabled()} is true. When disabled, this
 * catalogue is absent from the union — no surface, no endpoint.</p>
 */
public record DiagnosticsCatalogue() implements L0_Catalogue<DiagnosticsCatalogue> {

    public static final DiagnosticsCatalogue INSTANCE = new DiagnosticsCatalogue();

    @Override public String name()    { return "Diagnostics"; }
    @Override public String summary() {
        return "Self-introspection surfaces for the running studio — live object graph "
             + "and type view, scoped per studio when more than one is composed. "
             + "Visible only when the diagnostics switch is on.";
    }
}
