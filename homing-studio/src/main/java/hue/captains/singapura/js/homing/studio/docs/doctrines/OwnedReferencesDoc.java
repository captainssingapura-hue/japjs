package hue.captains.singapura.js.homing.studio.docs.doctrines;

import hue.captains.singapura.js.homing.studio.base.ClasspathMarkdownDoc;
import hue.captains.singapura.js.homing.studio.base.DocReference;
import hue.captains.singapura.js.homing.studio.base.Reference;
import hue.captains.singapura.js.homing.studio.docs.rfcs.Rfc0003Doc;

import java.util.List;
import java.util.UUID;

public record OwnedReferencesDoc() implements ClasspathMarkdownDoc {
    private static final UUID ID = UUID.fromString("6056a0d6-8ba4-4406-a095-da293a69a499");
    public static final OwnedReferencesDoc INSTANCE = new OwnedReferencesDoc();

    @Override public UUID   uuid()    { return ID; }
    @Override public String title()   { return "Doctrine — Owned References"; }
    @Override public String summary() { return "Every element has exactly one owner. No getElementById / querySelector. Act via method calls on handles, not lookups."; }
    @Override public String category(){ return "DOCTRINE"; }

    @Override public List<Reference> references() {
        return List.of(
                new DocReference("pcv",   PureComponentViewsDoc.INSTANCE),
                new DocReference("mop",   MethodsOverPropsDoc.INSTANCE),
                new DocReference("mdo",   ManagedDomOpsDoc.INSTANCE),
                new DocReference("rfc-3", Rfc0003Doc.INSTANCE)
        );
    }
}
