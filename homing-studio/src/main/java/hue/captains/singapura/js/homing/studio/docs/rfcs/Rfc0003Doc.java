package hue.captains.singapura.js.homing.studio.docs.rfcs;

import hue.captains.singapura.js.homing.studio.base.ClasspathMarkdownDoc;
import hue.captains.singapura.js.homing.studio.base.DocReference;
import hue.captains.singapura.js.homing.studio.base.Reference;
import hue.captains.singapura.js.homing.studio.docs.defects.Defect0001Doc;
import hue.captains.singapura.js.homing.studio.docs.defects.Defect0002Doc;
import hue.captains.singapura.js.homing.studio.docs.doctrines.ManagedDomOpsDoc;
import hue.captains.singapura.js.homing.studio.docs.doctrines.MethodsOverPropsDoc;
import hue.captains.singapura.js.homing.studio.docs.doctrines.OwnedReferencesDoc;
import hue.captains.singapura.js.homing.studio.docs.doctrines.PureComponentViewsDoc;

import java.util.List;
import java.util.UUID;

public record Rfc0003Doc() implements ClasspathMarkdownDoc {
    private static final UUID ID = UUID.fromString("739f4c1c-45df-4f05-bf21-853790927d04");
    public static final Rfc0003Doc INSTANCE = new Rfc0003Doc();

    @Override public UUID   uuid()    { return ID; }
    @Override public String title()   { return "RFC 0003 — Themeable Form & Component Primitive"; }
    @Override public String summary() { return "Add Component<C> + ComponentImpl<C, TH> so themes can vary form, not just paint. Two render modes, asset management, F2 scope."; }
    @Override public String category(){ return "RFC"; }

    @Override public List<Reference> references() {
        return List.of(
                new DocReference("def-1", Defect0001Doc.INSTANCE),
                new DocReference("def-2", Defect0002Doc.INSTANCE),
                new DocReference("pcv",   PureComponentViewsDoc.INSTANCE),
                new DocReference("mop",   MethodsOverPropsDoc.INSTANCE),
                new DocReference("mdo",   ManagedDomOpsDoc.INSTANCE),
                new DocReference("or",    OwnedReferencesDoc.INSTANCE)
        );
    }
}
