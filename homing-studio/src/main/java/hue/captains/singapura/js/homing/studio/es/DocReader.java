package hue.captains.singapura.js.homing.studio.es;

import hue.captains.singapura.js.homing.core.*;
import hue.captains.singapura.js.homing.libs.MarkedJs;
import hue.captains.singapura.js.homing.studio.base.css.StudioStyles;

import java.util.List;

public record DocReader() implements AppModule<DocReader> {

    record appMain() implements AppModule._AppMain<DocReader> {}

    public record link() implements AppLink<DocReader> {}

    /** Typed query parameter — the relative path to the markdown doc to render. */
    public record Params(String path) {}

    public static final DocReader INSTANCE = new DocReader();

    @Override
    public String title() {
        return "Homing · studio · doc";
    }

    @Override
    public Class<?> paramsType() {
        return Params.class;
    }

    @Override
    public ImportsFor<DocReader> imports() {
        return ImportsFor.<DocReader>builder()
                // Navigation targets — RFC 0001 Step 11.
                .add(new ModuleImports<>(List.of(new StudioCatalogue.link()), StudioCatalogue.INSTANCE))
                .add(new ModuleImports<>(List.of(new DocBrowser.link()),      DocBrowser.INSTANCE))
                // External library.
                .add(new ModuleImports<>(List.of(new MarkedJs.marked()), MarkedJs.INSTANCE))
                // CSS imports.
                .add(new ModuleImports<>(List.of(
                        new StudioStyles.st_root(),
                        new StudioStyles.st_header(),
                        new StudioStyles.st_brand(),
                        new StudioStyles.st_brand_dot(),
                        new StudioStyles.st_brand_word(),
                        new StudioStyles.st_breadcrumbs(),
                        new StudioStyles.st_crumb(),
                        new StudioStyles.st_crumb_sep(),
                        new StudioStyles.st_main(),
                        new StudioStyles.st_layout(),
                        new StudioStyles.st_sidebar(),
                        new StudioStyles.st_sidebar_title(),
                        new StudioStyles.st_toc(),
                        new StudioStyles.st_toc_item(),
                        new StudioStyles.st_toc_h1(),
                        new StudioStyles.st_toc_h2(),
                        new StudioStyles.st_toc_h3(),
                        new StudioStyles.st_toc_active(),
                        new StudioStyles.st_doc(),
                        new StudioStyles.st_doc_meta(),
                        new StudioStyles.st_badge(),
                        new StudioStyles.st_badge_whitepaper(),
                        new StudioStyles.st_badge_brochure(),
                        new StudioStyles.st_badge_rfc(),
                        new StudioStyles.st_badge_brand(),
                        new StudioStyles.st_badge_session(),
                        new StudioStyles.st_badge_reference(),
                        new StudioStyles.st_loading(),
                        new StudioStyles.st_error()
                ), StudioStyles.INSTANCE))
                .build();
    }

    @Override
    public ExportsOf<DocReader> exports() {
        return new ExportsOf<>(INSTANCE, List.of(new appMain()));
    }
}
