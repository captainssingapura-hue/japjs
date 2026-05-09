package hue.captains.singapura.js.homing.studio.es;

import hue.captains.singapura.js.homing.core.AppLink;
import hue.captains.singapura.js.homing.core.AppModule;
import hue.captains.singapura.js.homing.core.ExportsOf;
import hue.captains.singapura.js.homing.core.ImportsFor;
import hue.captains.singapura.js.homing.core.ModuleImports;
import hue.captains.singapura.js.homing.core.util.CssClassName;
import hue.captains.singapura.js.homing.studio.base.app.CatalogueAppModule;
import hue.captains.singapura.js.homing.studio.base.app.CatalogueCrumb;
import hue.captains.singapura.js.homing.studio.base.app.CatalogueData;
import hue.captains.singapura.js.homing.studio.base.app.CatalogueRenderer;
import hue.captains.singapura.js.homing.studio.base.app.CatalogueSection;
import hue.captains.singapura.js.homing.studio.base.app.CatalogueTile;
import hue.captains.singapura.js.homing.studio.base.app.DocReader;
import hue.captains.singapura.js.homing.studio.base.css.StudioStyles;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * Top-level catalogue listing every reusable building block in
 * {@code homing-studio-base} — atoms, kits, primitives, bootstrap, conformance.
 *
 * <p>This catalogue is itself built from existing kits: it implements
 * {@link CatalogueAppModule} (no per-page JS), each card links to a
 * markdown doc rendered by the shared {@link DocReader}. The recursion is
 * the proof — if the kit can document its own surface, the kit covers it.</p>
 */
public record BuildingBlocksCatalogue() implements CatalogueAppModule<BuildingBlocksCatalogue> {

    record appMain() implements AppModule._AppMain<BuildingBlocksCatalogue> {}

    public record link() implements AppLink<BuildingBlocksCatalogue> {}

    public static final BuildingBlocksCatalogue INSTANCE = new BuildingBlocksCatalogue();

    @Override public String homeAppSimpleName() { return StudioCatalogue.INSTANCE.simpleName(); }

    private record Block(String number, String title, String tagline, String path) {}

    private static final List<Block> BLOCKS = List.of(
            new Block("01", "Atoms — StudioElements",
                    "13 visual builders (Header, Card, Pill, Section, Footer, StatusBadge, OverallProgress, StepCard, DecisionCard, TodoList, MetricsTable, Panel, Brand). Building blocks for everything above.",
                    "blocks/atoms.md"),
            new Block("02", "Catalogue Kit",
                    "CatalogueAppModule — auto-generates a launcher / sub-catalogue / index page from typed Java data. Used by Studio's home, Journeys, Doctrines, this very page.",
                    "blocks/catalogue-kit.md"),
            new Block("03", "DocBrowser & DocReader Kits",
                    "Searchable card grid + shared markdown reader. Pair them and your studio has a documentation surface with zero JS.",
                    "blocks/doc-kits.md"),
            new Block("04", "Tracker Kit",
                    "PlanAppModule + PlanStepAppModule. Two-page tracker for any multi-phase plan. Implement Plan, get a working tracker. Includes Metric for before/after measurement display.",
                    "blocks/tracker-kit.md"),
            new Block("05", "Bootstrap & Conformance",
                    "StudioBootstrap.start(...) — one-call server entrypoint. Plus the five conformance test bases (Doctrine, CdnFree, Css, Href, CssGroupImplConsistency).",
                    "blocks/bootstrap-and-conformance.md")
    );

    @Override
    public CatalogueData catalogueData() {
        var badgeClass = CssClassName.toCssName(StudioStyles.st_badge_reference.class);
        var tiles = BLOCKS.stream().map(b -> CatalogueTile.card(
                "/app?app=" + DocReader.INSTANCE.simpleName() + "&path=" + URLEncoder.encode(b.path(), StandardCharsets.UTF_8),
                b.title(),
                b.tagline(),
                "BLOCK " + b.number(),
                badgeClass
        )).toList();

        return new CatalogueData(
                "what's available to compose a studio",
                "Building Blocks",
                "Everything reusable in homing-studio-base. The promise: no JS to write for the common case. Every page (catalogue, doc browser, doc reader, plan tracker) is a kit; you provide typed Java data, the framework auto-generates the served JS. For richer surfaces, drop down to the atoms.",
                List.of(
                        new CatalogueCrumb("Home",            "/app?app=" + StudioCatalogue.INSTANCE.simpleName()),
                        new CatalogueCrumb("Building Blocks", null)
                ),
                List.of(new CatalogueSection("All blocks", CatalogueSection.TileStyle.CARD, tiles)),
                "Each block above links to a spec doc with API, data shapes, and a worked example. The list is editable in `BuildingBlocksCatalogue.java`; the docs themselves live in `docs/blocks/`."
        );
    }

    @Override
    public ImportsFor<BuildingBlocksCatalogue> imports() {
        return ImportsFor.<BuildingBlocksCatalogue>builder()
                .add(new ModuleImports<>(List.of(new StudioCatalogue.link()), StudioCatalogue.INSTANCE))
                .add(new ModuleImports<>(List.of(new DocReader.link()),       DocReader.INSTANCE))
                .add(new ModuleImports<>(List.of(new CatalogueRenderer.renderCatalogue()), CatalogueRenderer.INSTANCE))
                .build();
    }

    @Override
    public ExportsOf<BuildingBlocksCatalogue> exports() {
        return new ExportsOf<>(INSTANCE, List.of(new appMain()));
    }
}
