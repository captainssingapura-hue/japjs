package hue.captains.singapura.js.homing.studio.docs.rfcs;

import hue.captains.singapura.js.homing.studio.base.ClasspathMarkdownDoc;
import hue.captains.singapura.js.homing.studio.base.DocReference;
import hue.captains.singapura.js.homing.studio.base.Reference;
import hue.captains.singapura.js.homing.studio.docs.doctrines.CatalogueContainerDoc;
import hue.captains.singapura.js.homing.studio.docs.doctrines.PlanContainerDoc;

import java.util.List;
import java.util.UUID;

public record Rfc0005Ext1Doc() implements ClasspathMarkdownDoc {
    private static final UUID ID = UUID.fromString("93605dda-c709-4d91-9a84-91d8f81c28f2");
    public static final Rfc0005Ext1Doc INSTANCE = new Rfc0005Ext1Doc();

    @Override public UUID   uuid()    { return ID; }
    @Override public String title()   { return "RFC 0005-ext1 — Typed Plan Containers"; }
    @Override public String summary() { return "Operationalises the Plans-as-Living-Containers doctrine: single PlanAppHost serves any registered Plan, three-pillar structure compiler-enforced, per-tracker boilerplate collapsed from 4 files to 2."; }
    @Override public String category(){ return "RFC"; }

    @Override public List<Reference> references() {
        return List.of(
                new DocReference("plan-doc", PlanContainerDoc.INSTANCE),
                new DocReference("cc",       CatalogueContainerDoc.INSTANCE),
                new DocReference("rfc-5",    Rfc0005Doc.INSTANCE)
        );
    }
}
