package hue.captains.singapura.js.homing.studio.docs.blocks;

import hue.captains.singapura.js.homing.studio.base.ClasspathMarkdownDoc;
import hue.captains.singapura.js.homing.studio.base.DocReference;
import hue.captains.singapura.js.homing.studio.base.Reference;

import java.util.List;
import java.util.UUID;

/** Top-level index of every reusable kit / atom / primitive in homing-studio-base. */
public record BlocksIndexDoc() implements ClasspathMarkdownDoc {
    private static final UUID ID = UUID.fromString("600a37ee-d3ec-4fd0-9a52-924bfd69dd93");
    public static final BlocksIndexDoc INSTANCE = new BlocksIndexDoc();

    @Override public UUID   uuid()    { return ID; }
    @Override public String title()   { return "Building Blocks — Index"; }
    @Override public String summary() { return "Top-level index of every reusable kit, atom, and primitive in homing-studio-base. Promise-per-goal table."; }
    @Override public String category(){ return "BLOCK"; }

    @Override public List<Reference> references() {
        return List.of(
                new DocReference("atoms",    AtomsDoc.INSTANCE),
                new DocReference("cat-kit",  CatalogueKitDoc.INSTANCE),
                new DocReference("doc-kits", DocKitsDoc.INSTANCE),
                new DocReference("trk-kit",  TrackerKitDoc.INSTANCE),
                new DocReference("bac",      BootstrapAndConformanceDoc.INSTANCE)
        );
    }
}
