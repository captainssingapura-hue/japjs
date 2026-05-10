package hue.captains.singapura.js.homing.studio.docs.blocks;

import hue.captains.singapura.js.homing.studio.base.ClasspathMarkdownDoc;
import hue.captains.singapura.js.homing.studio.base.DocReference;
import hue.captains.singapura.js.homing.studio.base.Reference;
import hue.captains.singapura.js.homing.studio.docs.rfcs.Rfc0004Doc;

import java.util.List;
import java.util.UUID;

/** Block 03 — DocBrowser & DocReader Kits. */
public record DocKitsDoc() implements ClasspathMarkdownDoc {
    private static final UUID ID = UUID.fromString("cfd37ce2-56ff-4666-b0e4-7dc34683de93");
    public static final DocKitsDoc INSTANCE = new DocKitsDoc();

    @Override public UUID   uuid()    { return ID; }
    @Override public String title()   { return "Block 03 — DocBrowser & DocReader Kits"; }
    @Override public String summary() { return "Searchable card grid + shared markdown reader. Pair them and your studio has a documentation surface with zero JS."; }
    @Override public String category(){ return "BLOCK"; }

    @Override public List<Reference> references() {
        return List.of(
                new DocReference("atoms",   AtomsDoc.INSTANCE),
                new DocReference("cat-kit", CatalogueKitDoc.INSTANCE),
                new DocReference("bac",     BootstrapAndConformanceDoc.INSTANCE),
                new DocReference("rfc-4",   Rfc0004Doc.INSTANCE)
        );
    }
}
