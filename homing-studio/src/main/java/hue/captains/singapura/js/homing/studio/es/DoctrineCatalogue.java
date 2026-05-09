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

/** Top-level catalogue for foundational doctrines. */
public record DoctrineCatalogue() implements CatalogueAppModule<DoctrineCatalogue> {

    record appMain() implements AppModule._AppMain<DoctrineCatalogue> {}

    public record link() implements AppLink<DoctrineCatalogue> {}

    public static final DoctrineCatalogue INSTANCE = new DoctrineCatalogue();

    @Override public String homeAppSimpleName() { return StudioCatalogue.INSTANCE.simpleName(); }

    private record Doctrine(String number, String title, String tagline, String path) {}

    private static final List<Doctrine> DOCTRINES = List.of(
            new Doctrine("01", "Pure-Component Views",
                    "No HTML in consumer code. Every UI element is a Component invocation. The only HTML is the framework's mount-point div.",
                    "doctrines/pure-component-views.md"),
            new Doctrine("02", "Methods Over Props",
                    "Components are objects with identity, state, and methods. Not functions of props. OO with pragmatic functional — not React's pure functional with handicapped state.",
                    "doctrines/methods-over-props.md"),
            new Doctrine("03", "Managed DOM Ops (in SPA contexts)",
                    "In SPA consumer code, every DOM mutation flows through one typed gateway. Imperative / game / animation contexts may use the DOM API directly.",
                    "doctrines/managed-dom-ops.md"),
            new Doctrine("04", "Owned References",
                    "Every element has exactly one owner. References are obtained at construction, never by selector. Action is a method call on the owner — message passing, not lookup.",
                    "doctrines/owned-references.md")
    );

    @Override
    public CatalogueData catalogueData() {
        var badgeClass = CssClassName.toCssName(StudioStyles.st_badge_reference.class);
        var tiles = DOCTRINES.stream().map(d -> CatalogueTile.card(
                "/app?app=" + DocReader.INSTANCE.simpleName() + "&path=" + URLEncoder.encode(d.path(), StandardCharsets.UTF_8),
                d.title(),
                d.tagline(),
                "DOCTRINE " + d.number(),
                badgeClass
        )).toList();

        return new CatalogueData(
                "required reading",
                "Doctrines",
                "The rules that hold the design together. Defects surface gaps; RFCs propose answers; doctrines codify the rules that survive both. Anyone working on this project should read every doctrine here before touching the code.",
                List.of(
                        new CatalogueCrumb("Home",      "/app?app=" + StudioCatalogue.INSTANCE.simpleName()),
                        new CatalogueCrumb("Doctrines", null)
                ),
                List.of(new CatalogueSection("All doctrines", CatalogueSection.TileStyle.CARD, tiles)),
                "Doctrines live in `docs/doctrines/`. Add one by writing the markdown, then registering it in `DoctrineCatalogue.java`."
        );
    }

    @Override
    public ImportsFor<DoctrineCatalogue> imports() {
        return ImportsFor.<DoctrineCatalogue>builder()
                .add(new ModuleImports<>(List.of(new StudioCatalogue.link()), StudioCatalogue.INSTANCE))
                .add(new ModuleImports<>(List.of(new DocReader.link()),       DocReader.INSTANCE))
                .add(new ModuleImports<>(List.of(new CatalogueRenderer.renderCatalogue()), CatalogueRenderer.INSTANCE))
                .build();
    }

    @Override
    public ExportsOf<DoctrineCatalogue> exports() {
        return new ExportsOf<>(INSTANCE, List.of(new appMain()));
    }
}
