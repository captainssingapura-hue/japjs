package hue.captains.singapura.js.homing.studio.docs.guides;

import hue.captains.singapura.js.homing.studio.base.ClasspathMarkdownDoc;
import hue.captains.singapura.js.homing.studio.base.DocReference;
import hue.captains.singapura.js.homing.studio.base.Reference;
import hue.captains.singapura.js.homing.studio.docs.defects.Defect0001Doc;
import hue.captains.singapura.js.homing.studio.docs.rfcs.Rfc0002Ext1Doc;

import java.util.List;
import java.util.UUID;

public record LiveTrackerPatternDoc() implements ClasspathMarkdownDoc {
    private static final UUID ID = UUID.fromString("5c3cc9d4-8982-4980-8dd4-25c87850fee8");
    public static final LiveTrackerPatternDoc INSTANCE = new LiveTrackerPatternDoc();

    @Override public UUID   uuid()    { return ID; }
    @Override public String title()   { return "Live Tracker Pattern"; }
    @Override public String summary() { return "Legacy guide for the per-plan tracker pattern. Superseded by the tracker kit (PlanAppModule + PlanRenderer)."; }
    @Override public String category(){ return "GUIDE"; }

    @Override public List<Reference> references() {
        return List.of(
                new DocReference("def-1",      Defect0001Doc.INSTANCE),
                new DocReference("rfc-2-ext1", Rfc0002Ext1Doc.INSTANCE)
        );
    }
}
