package hue.captains.singapura.js.homing.studio.es;

import hue.captains.singapura.js.homing.core.AppModule;
import hue.captains.singapura.js.homing.studio.base.app.Entry;
import hue.captains.singapura.js.homing.studio.base.app.L0_Catalogue;
import hue.captains.singapura.js.homing.studio.base.app.L1_Catalogue;
import hue.captains.singapura.js.homing.studio.base.app.Navigable;
import hue.captains.singapura.js.homing.studio.base.theme.ThemesIntro;

import java.util.List;

/**
 * Studio home — typed root catalogue. RFC 0005 + RFC 0005-ext2 + RFC 0011 CRTP.
 */
public record StudioCatalogue() implements L0_Catalogue<StudioCatalogue> {

    public static final StudioCatalogue INSTANCE = new StudioCatalogue();

    @Override public String name()    { return "Studio"; }
    @Override public String summary() { return "A workspace for the design, documentation, and project artifacts that drive Homing forward — built on Homing itself."; }

    @Override public List<? extends L1_Catalogue<StudioCatalogue, ?>> subCatalogues() {
        return List.of(
                MetaCatalogue.INSTANCE,
                RfcsCatalogue.INSTANCE,
                JourneysCatalogue.INSTANCE,
                CaseStudiesCatalogue.INSTANCE,
                BuildingBlocksCatalogue.INSTANCE,
                ReleasesCatalogue.INSTANCE
        );
    }

    @Override public List<Entry<StudioCatalogue>> leaves() {
        return List.of(
                Entry.of(this, new Navigable<>(
                        DocBrowser.INSTANCE,
                        AppModule._None.INSTANCE,
                        "Documents",
                        "Browse and read every white paper, RFC, brand artifact, and design note — searchable, filterable by category.")),
                Entry.of(this, new Navigable<>(
                        ThemesIntro.INSTANCE,
                        AppModule._None.INSTANCE,
                        "Themes",
                        "Palette previews and one-click activation for every registered theme. Your choice sticks across navigation."))
        );
    }
}
