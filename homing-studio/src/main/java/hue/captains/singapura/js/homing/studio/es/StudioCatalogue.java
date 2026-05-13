package hue.captains.singapura.js.homing.studio.es;

import hue.captains.singapura.js.homing.core.AppModule;
import hue.captains.singapura.js.homing.studio.base.app.Entry;
import hue.captains.singapura.js.homing.studio.base.app.L0_Catalogue;
import hue.captains.singapura.js.homing.studio.base.app.L1_Catalogue;
import hue.captains.singapura.js.homing.studio.base.app.Navigable;
import hue.captains.singapura.js.homing.studio.base.theme.ThemesIntro;

import java.util.List;

/**
 * Studio home — typed root catalogue per RFC 0005, with sub-catalogue /
 * leaf split per RFC 0005-ext2.
 *
 * <p>{@link #subCatalogues()} lists the four typed L1 sub-trees; {@link #leaves()}
 * lists the two leaf nav apps (DocBrowser, Themes). The renderer surfaces
 * sub-catalogues first, leaves second (RFC 0005-ext2 Option A).</p>
 */
public record StudioCatalogue() implements L0_Catalogue {

    public static final StudioCatalogue INSTANCE = new StudioCatalogue();

    @Override public String name()    { return "Studio"; }
    @Override public String summary() { return "A workspace for the design, documentation, and project artifacts that drive Homing forward — built on Homing itself."; }

    @Override public List<L1_Catalogue<StudioCatalogue>> subCatalogues() {
        return List.of(
                DoctrineCatalogue.INSTANCE,
                RfcsCatalogue.INSTANCE,
                JourneysCatalogue.INSTANCE,
                BuildingBlocksCatalogue.INSTANCE,
                ReleasesCatalogue.INSTANCE
        );
    }

    @Override public List<Entry> leaves() {
        return List.of(
                Entry.of(new Navigable<>(
                        DocBrowser.INSTANCE,
                        AppModule._None.INSTANCE,
                        "Documents",
                        "Browse and read every white paper, RFC, brand artifact, and design note — searchable, filterable by category.")),
                Entry.of(new Navigable<>(
                        ThemesIntro.INSTANCE,
                        AppModule._None.INSTANCE,
                        "Themes",
                        "Palette previews and one-click activation for every registered theme. Your choice sticks across navigation."))
        );
    }
}
