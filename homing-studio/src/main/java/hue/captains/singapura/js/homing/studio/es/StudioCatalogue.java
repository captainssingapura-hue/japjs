package hue.captains.singapura.js.homing.studio.es;

import hue.captains.singapura.js.homing.core.AppModule;
import hue.captains.singapura.js.homing.studio.base.app.Catalogue;
import hue.captains.singapura.js.homing.studio.base.app.Entry;
import hue.captains.singapura.js.homing.studio.base.app.Navigable;
import hue.captains.singapura.js.homing.studio.base.theme.ThemesIntro;

import java.util.List;

/**
 * Studio home — typed root catalogue per RFC 0005.
 *
 * <p>Lists the four sub-catalogues. No tile shapes, no URLs, no `appMain` /
 * `link` records — the catalogue is pure structure; {@code CatalogueAppHost}
 * serves it.</p>
 */
public record StudioCatalogue() implements Catalogue {

    public static final StudioCatalogue INSTANCE = new StudioCatalogue();

    @Override public String name()    { return "Studio"; }
    @Override public String summary() { return "A workspace for the design, documentation, and project artifacts that drive Homing forward — built on Homing itself."; }

    @Override public List<Entry> entries() {
        return List.of(
                Entry.of(DoctrineCatalogue.INSTANCE),
                Entry.of(new Navigable<>(
                        DocBrowser.INSTANCE,
                        AppModule._None.INSTANCE,
                        "Documents",
                        "Browse and read every white paper, RFC, brand artifact, and design note — searchable, filterable by category.")),
                Entry.of(JourneysCatalogue.INSTANCE),
                Entry.of(BuildingBlocksCatalogue.INSTANCE),
                Entry.of(new Navigable<>(
                        ThemesIntro.INSTANCE,
                        AppModule._None.INSTANCE,
                        "Themes",
                        "Palette previews and one-click activation for every registered theme. Your choice sticks across navigation.")),
                Entry.of(ReleasesCatalogue.INSTANCE)
        );
    }
}
