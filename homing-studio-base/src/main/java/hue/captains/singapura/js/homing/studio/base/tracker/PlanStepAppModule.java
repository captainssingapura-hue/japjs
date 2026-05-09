package hue.captains.singapura.js.homing.studio.base.tracker;

import hue.captains.singapura.js.homing.core.AppModule;
import hue.captains.singapura.js.homing.core.ModuleNameResolver;
import hue.captains.singapura.js.homing.core.SelfContent;

import java.util.List;

/**
 * Generic AppModule for any {@link Plan} tracker's per-phase detail page. The
 * companion to {@link PlanAppModule}. Reads {@code params.phase} (typed query
 * parameter) and delegates rendering to {@code renderStep} in
 * {@code PlanRenderer.js}.
 *
 * <p>Concrete trackers declare a {@code Params(String phase)} record, set
 * {@code paramsType()} to it, and implement {@code plan()} +
 * {@code planAppSimpleName()} + {@code stepAppSimpleName()}. Everything else
 * is inherited.</p>
 */
public interface PlanStepAppModule<M extends PlanStepAppModule<M>> extends AppModule<M>, SelfContent {

    /** The plan data this module renders one phase from. */
    Plan plan();

    /** Simple-name of the index-page AppModule (e.g. {@code "rename-plan"}). */
    String planAppSimpleName();

    /** Simple-name of THIS step-detail AppModule (e.g. {@code "rename-step"}). */
    String stepAppSimpleName();

    @Override
    default String title() {
        return "Homing · studio · " + plan().title() + " · phase";
    }

    @Override
    default List<String> selfContent(ModuleNameResolver nameResolver) {
        String json = PlanJson.of(plan());
        String stepApp = stepAppSimpleName();
        String planApp = planAppSimpleName();
        return List.of(
                "const planData = " + json + ";",
                "function appMain(rootElement) {",
                "    var planAppName = " + jstr(planApp) + ";",
                "    var stepAppName = " + jstr(stepApp) + ";",
                "    var navTargets = {",
                "        home:     nav.StudioCatalogue(),",
                "        journeys: nav.JourneysCatalogue(),",
                "        self:     \"/app?app=\" + planAppName,",
                "        step:     function(phaseId) { return \"/app?app=\" + stepAppName + \"&phase=\" + encodeURIComponent(phaseId); },",
                "        docReader: function(path) { return nav.DocReader({path: path}); }",
                "    };",
                "    rootElement.replaceChildren(renderStep({",
                "        data:    planData,",
                "        phaseId: params.phase,",
                "        nav:     navTargets,",
                "        brand:   { href: nav.StudioCatalogue(), label: \"Homing · studio\" }",
                "    }));",
                "}"
        );
    }

    private static String jstr(String s) {
        if (s == null) return "null";
        return "\"" + s.replace("\\", "\\\\").replace("\"", "\\\"") + "\"";
    }
}
