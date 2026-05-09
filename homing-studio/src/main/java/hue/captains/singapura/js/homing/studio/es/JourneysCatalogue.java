package hue.captains.singapura.js.homing.studio.es;

import hue.captains.singapura.js.homing.core.AppLink;
import hue.captains.singapura.js.homing.core.AppModule;
import hue.captains.singapura.js.homing.core.ExportsOf;
import hue.captains.singapura.js.homing.core.ImportsFor;
import hue.captains.singapura.js.homing.core.ModuleImports;
import hue.captains.singapura.js.homing.studio.base.app.CatalogueAppModule;
import hue.captains.singapura.js.homing.studio.base.app.CatalogueCrumb;
import hue.captains.singapura.js.homing.studio.base.app.CatalogueData;
import hue.captains.singapura.js.homing.studio.base.app.CatalogueRenderer;
import hue.captains.singapura.js.homing.studio.base.app.CatalogueSection;
import hue.captains.singapura.js.homing.studio.base.app.CatalogueTile;
import hue.captains.singapura.js.homing.studio.rename.RenamePlan;
import hue.captains.singapura.js.homing.studio.rfc0001.Rfc0001Plan;
import hue.captains.singapura.js.homing.studio.rfc0002.Rfc0002Plan;
import hue.captains.singapura.js.homing.studio.rfc0002ext1.Rfc0002Ext1Plan;

import java.util.List;

/** Sub-catalogue listing every plan tracker. */
public record JourneysCatalogue() implements CatalogueAppModule<JourneysCatalogue> {

    record appMain() implements AppModule._AppMain<JourneysCatalogue> {}

    public record link() implements AppLink<JourneysCatalogue> {}

    public static final JourneysCatalogue INSTANCE = new JourneysCatalogue();

    @Override public String homeAppSimpleName() { return StudioCatalogue.INSTANCE.simpleName(); }

    @Override
    public CatalogueData catalogueData() {
        var tiles = List.of(
                CatalogueTile.pill("/app?app=" + Rfc0001Plan.INSTANCE.simpleName(), "R", "RFC 0001 Plan",
                        "Live implementation tracker for the App Registry & Typed Navigation RFC. Every step is its own URL; progress is recorded in Java code and rendered live in the studio.", false),
                CatalogueTile.pill("/app?app=" + Rfc0002Plan.INSTANCE.simpleName(), "T", "RFC 0002 Plan",
                        "Live tracker for the Typed Themes for CssGroups RFC. Seven phases plus four open design questions; the third worked example of the live-tracker pattern.", false),
                CatalogueTile.pill("/app?app=" + Rfc0002Ext1Plan.INSTANCE.simpleName(), "U", "RFC 0002-ext1 Plan",
                        "Live tracker for the Utility-First Composition + Two-Layer Semantic Tokens extension to RFC 0002. Seven phases that layer utility classes and semantic tokens onto the typed-theme foundation.", false),
                CatalogueTile.pill("/app?app=" + RenamePlan.INSTANCE.simpleName(), "→", "Rename Plan",
                        "Migration plan for japjs → Homing. Six phases with verification gates and rollback strategies, plus four open decisions to resolve before executing. Live tracker — edit RenameSteps.java to revise.", false)
        );

        return new CatalogueData(
                "multi-phase plans",
                "Journeys",
                "Live trackers for every multi-phase plan in this project. Source of truth: the corresponding *Steps.java in each tracker package — edit, recompile, refresh and the state updates here.",
                List.of(
                        new CatalogueCrumb("Home",     "/app?app=" + StudioCatalogue.INSTANCE.simpleName()),
                        new CatalogueCrumb("Journeys", null)
                ),
                List.of(new CatalogueSection("Plans", CatalogueSection.TileStyle.PILL, tiles)),
                "Pattern documented in `docs/guides/live-tracker-pattern.md` (legacy) and `docs/defects/0001-no-app-kind-abstraction.md` §8 (current kit). Add a new journey by writing a `*Steps.java` + `*PlanData.java` adapter + `*Plan.java` + `*Step.java` and registering them here + in `JourneysCatalogue.java`."
        );
    }

    @Override
    public ImportsFor<JourneysCatalogue> imports() {
        return ImportsFor.<JourneysCatalogue>builder()
                .add(new ModuleImports<>(List.of(new StudioCatalogue.link()), StudioCatalogue.INSTANCE))
                .add(new ModuleImports<>(List.of(new Rfc0001Plan.link()),     Rfc0001Plan.INSTANCE))
                .add(new ModuleImports<>(List.of(new Rfc0002Plan.link()),     Rfc0002Plan.INSTANCE))
                .add(new ModuleImports<>(List.of(new Rfc0002Ext1Plan.link()), Rfc0002Ext1Plan.INSTANCE))
                .add(new ModuleImports<>(List.of(new RenamePlan.link()),      RenamePlan.INSTANCE))
                .add(new ModuleImports<>(List.of(new CatalogueRenderer.renderCatalogue()), CatalogueRenderer.INSTANCE))
                .build();
    }

    @Override
    public ExportsOf<JourneysCatalogue> exports() {
        return new ExportsOf<>(INSTANCE, List.of(new appMain()));
    }
}
