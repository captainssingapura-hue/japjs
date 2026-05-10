package hue.captains.singapura.js.homing.studio.docs.doctrines;

import hue.captains.singapura.js.homing.studio.base.ClasspathMarkdownDoc;
import hue.captains.singapura.js.homing.studio.base.DocReference;
import hue.captains.singapura.js.homing.studio.base.Reference;

import java.util.List;
import java.util.UUID;

public record FirstUserDoc() implements ClasspathMarkdownDoc {
    private static final UUID ID = UUID.fromString("5b7c4d2e-8f3a-4e9b-92c1-7d6a4f8e3b50");
    public static final FirstUserDoc INSTANCE = new FirstUserDoc();

    @Override public UUID   uuid()    { return ID; }
    @Override public String title()   { return "Doctrine — First-User Discipline"; }
    @Override public String summary() { return "We must be the first user of whatever we build. Every framework primitive ships alongside its first in-tree consumer; new chokepoints retrofit every existing user; the studio dogfoods itself — the framework hosts the doctrines that govern it, runs the conformance that polices it, tracks itself with the trackers it ships."; }
    @Override public String category(){ return "DOCTRINE"; }

    @Override public List<Reference> references() {
        return List.of(
                new DocReference("pcv", PureComponentViewsDoc.INSTANCE),
                new DocReference("cc",  CatalogueContainerDoc.INSTANCE),
                new DocReference("pc",  PlanContainerDoc.INSTANCE)
        );
    }
}
