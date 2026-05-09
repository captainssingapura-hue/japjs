package hue.captains.singapura.js.homing.studio.base.app;

import hue.captains.singapura.js.homing.core.DomModule;
import hue.captains.singapura.js.homing.core.Exportable;
import hue.captains.singapura.js.homing.core.ExportsOf;
import hue.captains.singapura.js.homing.core.ImportsFor;
import hue.captains.singapura.js.homing.core.ModuleImports;
import hue.captains.singapura.js.homing.server.HrefManager;
import hue.captains.singapura.js.homing.studio.base.css.StudioStyles;
import hue.captains.singapura.js.homing.studio.base.ui.StudioElements;

import java.util.List;

/** Shared JS renderer for {@link DocBrowserAppModule}. */
public record DocBrowserRenderer() implements DomModule<DocBrowserRenderer> {

    public record renderDocBrowser() implements Exportable._Constant<DocBrowserRenderer> {}

    public static final DocBrowserRenderer INSTANCE = new DocBrowserRenderer();

    @Override
    public ImportsFor<DocBrowserRenderer> imports() {
        return ImportsFor.<DocBrowserRenderer>builder()
                .add(new ModuleImports<>(List.of(new HrefManager.HrefManagerInstance()),
                        HrefManager.INSTANCE))
                .add(new ModuleImports<>(List.of(
                        new StudioElements.Header(),
                        new StudioElements.Card(),
                        new StudioElements.Section(),
                        new StudioElements.Footer()
                ), StudioElements.INSTANCE))
                .add(new ModuleImports<>(List.of(
                        new StudioStyles.st_root(),
                        new StudioStyles.st_main(),
                        new StudioStyles.st_kicker(),
                        new StudioStyles.st_title(),
                        new StudioStyles.st_subtitle(),
                        new StudioStyles.st_search_wrap(),
                        new StudioStyles.st_search(),
                        new StudioStyles.st_filter(),
                        new StudioStyles.st_filter_btn(),
                        new StudioStyles.st_filter_btn_active(),
                        new StudioStyles.st_loading()
                ), StudioStyles.INSTANCE))
                .build();
    }

    @Override
    public ExportsOf<DocBrowserRenderer> exports() {
        return new ExportsOf<>(INSTANCE, List.of(new renderDocBrowser()));
    }
}
