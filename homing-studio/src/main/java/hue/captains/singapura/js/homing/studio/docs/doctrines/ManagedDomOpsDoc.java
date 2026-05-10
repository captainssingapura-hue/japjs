package hue.captains.singapura.js.homing.studio.docs.doctrines;

import hue.captains.singapura.js.homing.studio.base.ClasspathMarkdownDoc;
import hue.captains.singapura.js.homing.studio.base.DocReference;
import hue.captains.singapura.js.homing.studio.base.Reference;
import hue.captains.singapura.js.homing.studio.docs.rfcs.Rfc0003Doc;

import java.util.List;
import java.util.UUID;

public record ManagedDomOpsDoc() implements ClasspathMarkdownDoc {
    private static final UUID ID = UUID.fromString("0e703e9d-e18f-468e-b460-71965447cc31");
    public static final ManagedDomOpsDoc INSTANCE = new ManagedDomOpsDoc();

    @Override public UUID   uuid()    { return ID; }
    @Override public String title()   { return "Doctrine — Managed DOM Ops (SPA scope)"; }
    @Override public String summary() { return "In SPA consumer code, every DOM mutation flows through one typed gateway. Imperative / game / animation contexts may use the DOM API directly."; }
    @Override public String category(){ return "DOCTRINE"; }

    @Override public List<Reference> references() {
        return List.of(
                new DocReference("pcv",   PureComponentViewsDoc.INSTANCE),
                new DocReference("mop",   MethodsOverPropsDoc.INSTANCE),
                new DocReference("or",    OwnedReferencesDoc.INSTANCE),
                new DocReference("rfc-3", Rfc0003Doc.INSTANCE)
        );
    }
}
