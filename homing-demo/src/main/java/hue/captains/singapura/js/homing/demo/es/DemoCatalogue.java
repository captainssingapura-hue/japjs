package hue.captains.singapura.js.homing.demo.es;

import hue.captains.singapura.js.homing.core.*;
import hue.captains.singapura.js.homing.demo.css.CatalogueStyles;

import java.util.List;

public record DemoCatalogue() implements AppModule<DemoCatalogue> {

    record appMain() implements AppModule._AppMain<DemoCatalogue> {}

    public record link() implements AppLink<DemoCatalogue> {}

    public static final DemoCatalogue INSTANCE = new DemoCatalogue();

    @Override
    public String title() {
        return "Homing · demos";
    }

    @Override
    public ImportsFor<DemoCatalogue> imports() {
        return ImportsFor.<DemoCatalogue>builder()
                // Navigation targets — every linkable demo, brought in via the
                // typed AppLink<?> machinery (RFC 0001 Step 11). Each link()
                // import causes the writer to add an entry to the generated
                // `nav` const, AND causes the resolver to register the target
                // app transitively at server boot.
                .add(new ModuleImports<>(List.of(new WonderlandDemo.link()),    WonderlandDemo.INSTANCE))
                .add(new ModuleImports<>(List.of(new DancingAnimals.link()),    DancingAnimals.INSTANCE))
                .add(new ModuleImports<>(List.of(new SpinningAnimals.link()),   SpinningAnimals.INSTANCE))
                .add(new ModuleImports<>(List.of(new MovingAnimal.link()),      MovingAnimal.INSTANCE))
                .add(new ModuleImports<>(List.of(new TurtleDemo.link()),        TurtleDemo.INSTANCE))
                .add(new ModuleImports<>(List.of(new ExtrudedTurtleDemo.link()),ExtrudedTurtleDemo.INSTANCE))
                .add(new ModuleImports<>(List.of(new DecomposedSvgDemo.link()), DecomposedSvgDemo.INSTANCE))
                .add(new ModuleImports<>(List.of(new ExtrudedSvgDemo.link()),   ExtrudedSvgDemo.INSTANCE))
                .add(new ModuleImports<>(List.of(new DemoCatalogue.link()),     DemoCatalogue.INSTANCE))
                // CSS imports.
                .add(new ModuleImports<>(List.of(
                        new CatalogueStyles.cat_root(),
                        new CatalogueStyles.cat_header(),
                        new CatalogueStyles.cat_kicker(),
                        new CatalogueStyles.cat_title(),
                        new CatalogueStyles.cat_subtitle(),
                        new CatalogueStyles.cat_section(),
                        new CatalogueStyles.cat_section_title(),
                        new CatalogueStyles.cat_grid(),
                        new CatalogueStyles.cat_card(),
                        new CatalogueStyles.cat_card_featured(),
                        new CatalogueStyles.cat_card_head(),
                        new CatalogueStyles.cat_card_title(),
                        new CatalogueStyles.cat_card_desc(),
                        new CatalogueStyles.cat_card_meta(),
                        new CatalogueStyles.cat_card_link(),
                        new CatalogueStyles.cat_badge(),
                        new CatalogueStyles.cat_badge_pitch(),
                        new CatalogueStyles.cat_badge_3d(),
                        new CatalogueStyles.cat_badge_anim(),
                        new CatalogueStyles.cat_badge_basic(),
                        new CatalogueStyles.cat_footer(),
                        new CatalogueStyles.cat_mono()
                ), CatalogueStyles.INSTANCE))
                .build();
    }

    @Override
    public ExportsOf<DemoCatalogue> exports() {
        return new ExportsOf<>(INSTANCE, List.of(new appMain()));
    }
}
