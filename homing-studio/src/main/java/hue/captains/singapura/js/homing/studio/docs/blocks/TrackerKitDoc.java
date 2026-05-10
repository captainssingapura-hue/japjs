package hue.captains.singapura.js.homing.studio.docs.blocks;

import hue.captains.singapura.js.homing.studio.base.ClasspathMarkdownDoc;
import hue.captains.singapura.js.homing.studio.base.DocReference;
import hue.captains.singapura.js.homing.studio.base.Reference;
import hue.captains.singapura.js.homing.studio.docs.defects.Defect0001Doc;

import java.util.List;
import java.util.UUID;

/** Block 04 — Tracker Kit. */
public record TrackerKitDoc() implements ClasspathMarkdownDoc {
    private static final UUID ID = UUID.fromString("224b0edb-2b71-44c7-8744-86b887053215");
    public static final TrackerKitDoc INSTANCE = new TrackerKitDoc();

    @Override public UUID   uuid()    { return ID; }
    @Override public String title()   { return "Block 04 — Tracker Kit"; }
    @Override public String summary() { return "PlanAppModule + PlanStepAppModule. Two-page tracker for any multi-phase plan. Implement Plan, get a working tracker."; }
    @Override public String category(){ return "BLOCK"; }

    @Override public List<Reference> references() {
        return List.of(
                new DocReference("atoms", AtomsDoc.INSTANCE),
                new DocReference("def-1", Defect0001Doc.INSTANCE),
                new DocReference("bac",   BootstrapAndConformanceDoc.INSTANCE)
        );
    }
}
