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

import java.util.List;

/**
 * Studio home — three top-level launcher tiles. Auto-generated JS via
 * {@link CatalogueAppModule}; no hand-written {@code .js}.
 */
public record StudioCatalogue() implements CatalogueAppModule<StudioCatalogue> {

    record appMain() implements AppModule._AppMain<StudioCatalogue> {}

    public record link() implements AppLink<StudioCatalogue> {}

    public static final StudioCatalogue INSTANCE = new StudioCatalogue();

    @Override
    public CatalogueData catalogueData() {
        var tiles = List.of(
                CatalogueTile.pill(
                        "/app?app=" + DoctrineCatalogue.INSTANCE.simpleName(),
                        "!", "Doctrines",
                        "Required reading. The rules that hold the design together — anyone working on this project should read every doctrine here before touching the code. Brainwashing intentional.",
                        true),
                CatalogueTile.pill(
                        "/app?app=" + DocBrowser.INSTANCE.simpleName(),
                        "D", "Documents",
                        "Browse and read every white paper, brochure, RFC, brand artifact, and design note in the project — searchable, filterable by category, with an in-page table of contents on every doc.",
                        true),
                CatalogueTile.pill(
                        "/app?app=" + JourneysCatalogue.INSTANCE.simpleName(),
                        "J", "Journeys",
                        "Live trackers for every multi-phase plan — RFC implementations, migrations, audits. Each plan's source of truth is a *Steps.java file; the studio renders the latest state on every page load.",
                        true),
                CatalogueTile.pill(
                        "/app?app=" + BuildingBlocksCatalogue.INSTANCE.simpleName(),
                        "B", "Building Blocks",
                        "Every reusable kit, atom, and primitive in homing-studio-base. The promise: no JS to write for the common case. Composed from the same kits it documents — recursive proof that the kit covers its own surface.",
                        true)
        );

        return new CatalogueData(
                "design & project management",
                "Studio",
                "A workspace for the design, documentation, and project artifacts that drive Homing forward — built on Homing itself.",
                List.of(new CatalogueCrumb("Home", null)),
                List.of(new CatalogueSection("Apps", CatalogueSection.TileStyle.PILL, tiles)),
                "Studio is a sibling Maven module to `homing-demo`, built entirely on Homing primitives. Add a new tile to this file and import the target's `link()` record into `StudioCatalogue.java`."
        );
    }

    @Override
    public ImportsFor<StudioCatalogue> imports() {
        return ImportsFor.<StudioCatalogue>builder()
                // Navigation targets — every app the catalogue links to.
                .add(new ModuleImports<>(List.of(new StudioCatalogue.link()),    StudioCatalogue.INSTANCE))
                .add(new ModuleImports<>(List.of(new DoctrineCatalogue.link()),  DoctrineCatalogue.INSTANCE))
                .add(new ModuleImports<>(List.of(new DocBrowser.link()),         DocBrowser.INSTANCE))
                .add(new ModuleImports<>(List.of(new JourneysCatalogue.link()),  JourneysCatalogue.INSTANCE))
                .add(new ModuleImports<>(List.of(new BuildingBlocksCatalogue.link()), BuildingBlocksCatalogue.INSTANCE))
                // Renderer.
                .add(new ModuleImports<>(List.of(new CatalogueRenderer.renderCatalogue()), CatalogueRenderer.INSTANCE))
                .build();
    }

    @Override
    public ExportsOf<StudioCatalogue> exports() {
        return new ExportsOf<>(INSTANCE, List.of(new appMain()));
    }
}
