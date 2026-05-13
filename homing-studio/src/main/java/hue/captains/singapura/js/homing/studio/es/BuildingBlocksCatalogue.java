package hue.captains.singapura.js.homing.studio.es;

import hue.captains.singapura.js.homing.studio.base.Doc;
import hue.captains.singapura.js.homing.studio.base.DocProvider;
import hue.captains.singapura.js.homing.studio.base.app.Entry;
import hue.captains.singapura.js.homing.studio.base.app.L1_Catalogue;
import hue.captains.singapura.js.homing.studio.docs.blocks.AtomsDoc;
import hue.captains.singapura.js.homing.studio.docs.blocks.BlocksIndexDoc;
import hue.captains.singapura.js.homing.studio.docs.blocks.BootstrapAndConformanceDoc;
import hue.captains.singapura.js.homing.studio.docs.blocks.CatalogueKitDoc;
import hue.captains.singapura.js.homing.studio.docs.blocks.DocKitsDoc;
import hue.captains.singapura.js.homing.studio.docs.blocks.PlanKitDoc;
import hue.captains.singapura.js.homing.studio.docs.blocks.TrackerKitDoc;

import java.util.List;

/**
 * Sub-catalogue listing every reusable building block in {@code homing-studio-base}.
 * Per RFC 0005-ext2 an L1 catalogue under {@link StudioCatalogue}; per RFC 0004
 * also a {@link DocProvider} contributing every block doc + the index to the
 * studio's {@code DocRegistry}.
 */
public record BuildingBlocksCatalogue()
        implements L1_Catalogue<StudioCatalogue>, DocProvider {

    public static final BuildingBlocksCatalogue INSTANCE = new BuildingBlocksCatalogue();

    @Override public StudioCatalogue parent() { return StudioCatalogue.INSTANCE; }
    @Override public String name()    { return "Building Blocks"; }
    @Override public String summary() { return "Every reusable kit, atom, and primitive in homing-studio-base. The promise: no JS to write for the common case."; }
    @Override public String badge()   { return "BLOCKS"; }
    @Override public String icon()    { return "🧱"; }

    @Override public List<Entry> leaves() {
        return List.of(
                Entry.of(AtomsDoc.INSTANCE),
                Entry.of(CatalogueKitDoc.INSTANCE),
                Entry.of(PlanKitDoc.INSTANCE),
                Entry.of(DocKitsDoc.INSTANCE),
                Entry.of(TrackerKitDoc.INSTANCE),
                Entry.of(BootstrapAndConformanceDoc.INSTANCE)
        );
    }

    /** RFC 0004: the blocks doc set + the index, contributed to the studio's DocRegistry. */
    @Override public List<Doc> docs() {
        return List.of(
                BlocksIndexDoc.INSTANCE,
                AtomsDoc.INSTANCE,
                CatalogueKitDoc.INSTANCE,
                PlanKitDoc.INSTANCE,
                DocKitsDoc.INSTANCE,
                TrackerKitDoc.INSTANCE,
                BootstrapAndConformanceDoc.INSTANCE
        );
    }
}
