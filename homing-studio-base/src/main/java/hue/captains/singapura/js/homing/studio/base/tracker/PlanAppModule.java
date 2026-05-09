package hue.captains.singapura.js.homing.studio.base.tracker;

import hue.captains.singapura.js.homing.core.AppModule;
import hue.captains.singapura.js.homing.core.ModuleNameResolver;
import hue.captains.singapura.js.homing.core.SelfContent;

import java.util.List;

/**
 * Generic AppModule for any {@link Plan} tracker's index page. Each concrete
 * tracker (RenamePlan, Rfc0001Plan, …) implements this interface, supplies
 * its {@link #plan()} data and step-app simple name, and inherits the
 * auto-generated JS body — no per-tracker {@code .js} resource needed.
 *
 * <p>Concrete trackers still declare their own {@code appMain} / {@code link}
 * marker records, {@code INSTANCE} field, and {@code imports()} / {@code exports()}
 * because those references are typed by the concrete self-type. The shared
 * surface is everything else.</p>
 */
public interface PlanAppModule<M extends PlanAppModule<M>> extends AppModule<M>, SelfContent {

    /** The plan data this module renders. */
    Plan plan();

    /**
     * Simple-name (kebab-case URL identifier) of the companion step-detail
     * AppModule — e.g. {@code "rename-step"} for {@link Plan} "rename".
     * Phase cards on the index link to {@code /app?app=<stepAppSimpleName>&phase=<id>}.
     */
    String stepAppSimpleName();

    @Override
    default String title() {
        return "Homing · studio · " + plan().title();
    }

    /**
     * Auto-generated JS body. Embeds the plan data as a JSON literal, sets
     * up nav-target URL builders, and calls {@code renderPlan} on the
     * rootElement. Browser caches the served JS by URL; restart-to-refresh
     * is the freshness model (the typical edit-recompile-restart loop).
     */
    @Override
    default List<String> selfContent(ModuleNameResolver nameResolver) {
        String json = PlanJson.of(plan());
        String stepApp = stepAppSimpleName();
        return List.of(
                "const planData = " + json + ";",
                "function appMain(rootElement) {",
                "    var stepAppName = " + jstr(stepApp) + ";",
                "    var navTargets = {",
                "        home:     nav.StudioCatalogue(),",
                "        journeys: nav.JourneysCatalogue(),",
                "        step:     function(phaseId) { return \"/app?app=\" + stepAppName + \"&phase=\" + encodeURIComponent(phaseId); },",
                "        docReader: function(path) { return nav.DocReader({path: path}); }",
                "    };",
                "    rootElement.replaceChildren(renderPlan({",
                "        data:  planData,",
                "        nav:   navTargets,",
                "        brand: { href: nav.StudioCatalogue(), label: \"Homing · studio\" }",
                "    }));",
                "}"
        );
    }

    private static String jstr(String s) {
        if (s == null) return "null";
        return "\"" + s.replace("\\", "\\\\").replace("\"", "\\\"") + "\"";
    }
}
