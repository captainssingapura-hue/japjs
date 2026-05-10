package hue.captains.singapura.js.homing.studio.docs.doctrines;

import hue.captains.singapura.js.homing.studio.base.ClasspathMarkdownDoc;
import hue.captains.singapura.js.homing.studio.base.DocReference;
import hue.captains.singapura.js.homing.studio.base.Reference;
import hue.captains.singapura.js.homing.studio.docs.rfcs.Rfc0003Doc;

import java.util.List;
import java.util.UUID;

public record MethodsOverPropsDoc() implements ClasspathMarkdownDoc {
    private static final UUID ID = UUID.fromString("2feb36f0-e5cf-4b64-bfb1-53362d6a4ca4");
    public static final MethodsOverPropsDoc INSTANCE = new MethodsOverPropsDoc();

    @Override public UUID   uuid()    { return ID; }
    @Override public String title()   { return "Doctrine — Methods Over Props"; }
    @Override public String summary() { return "Components are objects, not functions of props. OO with pragmatic functional, not React's handicapped pure functional."; }
    @Override public String category(){ return "DOCTRINE"; }

    @Override public List<Reference> references() {
        return List.of(
                new DocReference("pcv",   PureComponentViewsDoc.INSTANCE),
                new DocReference("mdo",   ManagedDomOpsDoc.INSTANCE),
                new DocReference("or",    OwnedReferencesDoc.INSTANCE),
                new DocReference("rfc-3", Rfc0003Doc.INSTANCE)
        );
    }
}
