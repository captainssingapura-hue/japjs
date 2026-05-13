package hue.captains.singapura.js.homing.studio.docs.doctrines;

import hue.captains.singapura.js.homing.studio.base.ClasspathMarkdownDoc;
import hue.captains.singapura.js.homing.studio.base.DocReference;
import hue.captains.singapura.js.homing.studio.base.Reference;
import hue.captains.singapura.js.homing.studio.docs.rfcs.Rfc0011Doc;

import java.util.List;
import java.util.UUID;

/**
 * Doctrine — Weighed Complexity. Lines of code are not equal. Estimate
 * change scope by the worst-cost dimension, not the line count.
 *
 * <p>Five dimensions to name when sizing a design: cognitive density per
 * read, blast radius across the codebase, reversibility once shipped,
 * perpetual authoring tax paid by every author thereafter, and failure
 * mode (compile vs boot vs runtime vs silent). LOC is at best a signal
 * for the first; it lies about all four others.</p>
 *
 * <p>Filed retrospectively while sizing {@code Rfc0011Doc} — an earlier
 * draft was priced honestly at ~355 lines and only flagged as a bad trade
 * after recognising that each line carried different cost. The doctrine
 * names the principle so future design-phase scoping doesn't repeat the
 * mistake.</p>
 */
public record WeighedComplexityDoc() implements ClasspathMarkdownDoc {
    private static final UUID ID = UUID.fromString("c9d4f2a7-3e8b-4f1a-bc6d-7e5a2b8c1d4f");
    public static final WeighedComplexityDoc INSTANCE = new WeighedComplexityDoc();

    @Override public UUID   uuid()    { return ID; }
    @Override public String title()   { return "Doctrine — Weighed Complexity"; }
    @Override public String summary() { return "Lines of code are not equal. Cost has five dimensions: cognitive density, blast radius, reversibility, perpetual authoring tax, failure mode. LOC is at best a signal for the first and lies about the others. Estimate scope by the worst-cost dimension."; }
    @Override public String category(){ return "DOCTRINE"; }

    @Override public List<Reference> references() {
        return List.of(
                new DocReference("rfc-11", Rfc0011Doc.INSTANCE),
                new DocReference("fu",     FirstUserDoc.INSTANCE),
                new DocReference("cc",     CatalogueContainerDoc.INSTANCE)
        );
    }
}
