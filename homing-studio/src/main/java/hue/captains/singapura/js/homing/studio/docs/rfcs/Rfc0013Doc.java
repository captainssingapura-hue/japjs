package hue.captains.singapura.js.homing.studio.docs.rfcs;

import hue.captains.singapura.js.homing.studio.base.ClasspathMarkdownDoc;
import hue.captains.singapura.js.homing.studio.base.DocReference;
import hue.captains.singapura.js.homing.studio.base.ExternalReference;
import hue.captains.singapura.js.homing.studio.base.Reference;
import hue.captains.singapura.js.homing.studio.docs.doctrines.CatalogueContainerDoc;
import hue.captains.singapura.js.homing.studio.docs.doctrines.FunctionalObjectsDoc;
import hue.captains.singapura.js.homing.studio.docs.doctrines.WeighedComplexityDoc;

import java.util.List;
import java.util.UUID;

/**
 * RFC 0013 — jOntology Integration. Adopts the sibling
 * <a href="https://github.com/captainssingapura-hue/jOntology">jOntology</a>
 * marker-interface library across Homing's spine, promoting the Functional
 * Objects doctrine from convention to compile-and-runtime mechanism.
 *
 * <p>The classification overlap is essentially exact: jOntology's
 * {@code StatelessFunctionalObject} is what the doctrine names as
 * "stateless functional object reached via INSTANCE";
 * {@code FunctionalObject} is the "dependency-holding object";
 * {@code ValueObject} is the framework's typed-data records;
 * {@code Mutable} is the explicit acknowledgement of state-bearing types
 * the doctrine generally refuses.</p>
 *
 * <p>Integration shape: add jOntology as a {@code homing-core} dependency,
 * mark the framework's spine interfaces ({@code Doc}, {@code Catalogue},
 * {@code Plan}, {@code Studio}, {@code AppModule}) as
 * {@code StatelessFunctionalObject} so all ~100 implementing records
 * inherit the marker automatically, mark the ~10 field-bearing types
 * ({@code Bootstrap}, {@code Umbrella}, {@code Entry}, ...) individually,
 * and wire the jOntology enforcer as a build-time conformance test joining
 * the existing {@code StudioDocConformanceTest} family. Existing API
 * surfaces unchanged; markers are additive.</p>
 *
 * <p>The framework asserts that {@code Doc}, {@code Catalogue},
 * {@code Plan}, {@code Studio}, and {@code AppModule} are inherently
 * stateless — there is no scenario in which any of them legitimately
 * grows mutable state. The interface-level marker enforces this
 * categorically: a downstream record that tries to add a field to a
 * {@code Doc} fails the enforcer at build time.</p>
 *
 * <p>Total work: ~15 edits to framework code, one new conformance test,
 * one paragraph of doctrine prose. The benefit is the doctrine's
 * discipline becomes mechanically true rather than authored true,
 * automatically extended to every downstream implementation.</p>
 */
public record Rfc0013Doc() implements ClasspathMarkdownDoc {
    private static final UUID ID = UUID.fromString("d8e5c2a1-3b4f-4e6d-7a8c-9f1d6b3e8d50");
    public static final Rfc0013Doc INSTANCE = new Rfc0013Doc();

    @Override public UUID   uuid()    { return ID; }
    @Override public String title()   { return "RFC 0013 — jOntology Integration"; }
    @Override public String summary() { return "Adopt the sibling jOntology marker-interface library across Homing's spine. Promotes the Functional Objects doctrine from convention to compile-and-runtime mechanism. Mark framework spine interfaces (Doc, Catalogue, Plan, Studio, AppModule) as StatelessFunctionalObject — implementations inherit automatically. Mark the ~10 field-bearing types individually. Five interface edits + ten record edits = full-framework classification; jOntology enforcer runs at build time as a conformance test catching any contract violation. Existing APIs unchanged; downstream gets the discipline automatically."; }
    @Override public String category(){ return "RFC"; }

    @Override public List<Reference> references() {
        return List.of(
                new DocReference("doc-fo", FunctionalObjectsDoc.INSTANCE),
                new DocReference("doc-wc", WeighedComplexityDoc.INSTANCE),
                new DocReference("doc-cc", CatalogueContainerDoc.INSTANCE),
                new ExternalReference("jontology",
                        "https://github.com/captainssingapura-hue/jOntology",
                        "jOntology",
                        "Sibling Java library providing marker interfaces for object classification (Mutable, Immutable, Stateless, ValueObject, FunctionalObject, StatelessFunctionalObject) plus a runtime enforcer. Same author group; MIT licensed; published to Maven Central.")
        );
    }
}
