package hue.captains.singapura.js.homing.studio.docs.doctrines;

import hue.captains.singapura.js.homing.studio.base.ClasspathMarkdownDoc;
import hue.captains.singapura.js.homing.studio.base.DocReference;
import hue.captains.singapura.js.homing.studio.base.ExternalReference;
import hue.captains.singapura.js.homing.studio.base.Reference;
import hue.captains.singapura.js.homing.studio.docs.rfcs.Rfc0013Doc;

import java.util.List;
import java.util.UUID;

// Same package — direct references, no import needed for siblings.

/**
 * Doctrine — Functional Objects. No public static methods, anywhere in the
 * framework. Behaviour (orchestration, IO, composition, even pure functions)
 * is carried by methods on immutable typed objects. A "stateless functional
 * object" is the framework's universal substitute for the public static
 * method — same call-site ergonomics, none of static's problems.
 *
 * <p>Filed while reshaping {@link hue.captains.singapura.js.homing.studio.base.StudioBootstrap}
 * for RFC 0012 — the parameter-explosion symptom was downstream of the deeper
 * structural problem of having public statics carrying orchestration at all.
 * The doctrine names the principle so the fix becomes generalisable: any
 * future public static method anywhere is the same anti-pattern.</p>
 */
public record FunctionalObjectsDoc() implements ClasspathMarkdownDoc {
    private static final UUID ID = UUID.fromString("e5a8d2c1-7f4b-4e6a-9d3c-1b8e5f2a4d70");
    public static final FunctionalObjectsDoc INSTANCE = new FunctionalObjectsDoc();

    @Override public UUID   uuid()    { return ID; }
    @Override public String title()   { return "Doctrine — Functional Objects"; }
    @Override public String summary() { return "No public static methods, anywhere. Behaviour belongs on methods of immutable typed objects — stateless functional objects when the behaviour is pure, dependency-holding objects when it isn't. The static-method shape carries problems (no polymorphism, no constructor injection, parameter explosion when deps multiply, friction for tests); the framework refuses them entirely."; }
    @Override public String category(){ return "DOCTRINE"; }

    @Override public List<Reference> references() {
        return List.of(
                new DocReference("doc-cc", CatalogueContainerDoc.INSTANCE),
                new DocReference("wc",     WeighedComplexityDoc.INSTANCE),
                new DocReference("rfc-13", Rfc0013Doc.INSTANCE),
                new ExternalReference("jontology",
                        "https://github.com/captainssingapura-hue/jOntology",
                        "jOntology",
                        "Sibling marker-interface library for object classification (Mutable, Immutable, Stateless, ValueObject, FunctionalObject, StatelessFunctionalObject) with a runtime enforcer. Same author group; integrated as the mechanical realisation of this doctrine via RFC 0013.")
        );
    }
}
