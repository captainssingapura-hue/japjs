package hue.captains.singapura.js.homing.studio.docs.blocks;

import hue.captains.singapura.js.homing.studio.base.ClasspathMarkdownDoc;
import hue.captains.singapura.js.homing.studio.base.DocReference;
import hue.captains.singapura.js.homing.studio.base.Reference;
import hue.captains.singapura.js.homing.studio.docs.doctrines.PureComponentViewsDoc;

import java.util.List;
import java.util.UUID;

/** Block 01 — the 13 atoms in {@code StudioElements}. */
public record AtomsDoc() implements ClasspathMarkdownDoc {
    private static final UUID ID = UUID.fromString("06b0acc6-5c3a-4d65-bf3e-0831e7528025");
    public static final AtomsDoc INSTANCE = new AtomsDoc();

    @Override public UUID   uuid()    { return ID; }
    @Override public String title()   { return "Block 01 — Atoms (StudioElements)"; }
    @Override public String summary() { return "13 visual builders: Header, Card, Pill, Section, Footer, StatusBadge, OverallProgress, StepCard, DecisionCard, TodoList, MetricsTable, Panel, Brand."; }
    @Override public String category(){ return "BLOCK"; }

    @Override public List<Reference> references() {
        return List.of(
                new DocReference("cat-kit",  CatalogueKitDoc.INSTANCE),
                new DocReference("doc-kits", DocKitsDoc.INSTANCE),
                new DocReference("trk-kit",  TrackerKitDoc.INSTANCE),
                new DocReference("pcv",      PureComponentViewsDoc.INSTANCE)
        );
    }
}
