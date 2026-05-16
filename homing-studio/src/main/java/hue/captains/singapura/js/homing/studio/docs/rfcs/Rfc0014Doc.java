package hue.captains.singapura.js.homing.studio.docs.rfcs;

import hue.captains.singapura.js.homing.studio.base.ClasspathMarkdownDoc;
import hue.captains.singapura.js.homing.studio.base.DocReference;
import hue.captains.singapura.js.homing.studio.base.Reference;
import hue.captains.singapura.js.homing.studio.docs.casestudies.CrossStudioRefsCaseStudy;
import hue.captains.singapura.js.homing.studio.docs.doctrines.CatalogueContainerDoc;
import hue.captains.singapura.js.homing.studio.docs.doctrines.FunctionalObjectsDoc;
import hue.captains.singapura.js.homing.studio.docs.doctrines.WeighedComplexityDoc;

import java.util.List;
import java.util.UUID;

/**
 * RFC 0014 — Typed Studio Graph. Expose the in-memory object graph composed
 * by {@code Bootstrap} as a typed, queryable {@code StudioGraph<S, F>}
 * record. Reuses the registries already built at boot
 * ({@code CatalogueRegistry}, {@code DocRegistry}, {@code PlanRegistry},
 * {@code StudioProxyManager}) as the graph's underlying state; adds typed
 * query primitives over them.
 *
 * <p>Becomes the foundation for: auto-generated diagrams (RFC 0013 §5
 * deferred work — {@code CatalogueTree}, {@code PlanGraph}, etc.);
 * cross-doc programmability (rolled-up Tensions, Compromises across
 * doctrines / case studies); reverse-citation queries; live conformance /
 * observability beyond build-time tests; search index / sitemap
 * generation; and the typed doc DSL's computed-content phase.</p>
 *
 * <p>Small, additive, no breaking change. The data already exists in the
 * four registries — what was missing is the typed query API. ~200-line
 * foundation that unlocks multiple downstream features without each having
 * to re-implement registry-walk plumbing.</p>
 */
public record Rfc0014Doc() implements ClasspathMarkdownDoc {
    private static final UUID ID = UUID.fromString("f1e2d3c4-5b6a-4789-9b0c-1d2e3f4a5b70");
    public static final Rfc0014Doc INSTANCE = new Rfc0014Doc();

    @Override public UUID   uuid()    { return ID; }
    @Override public String title()   { return "RFC 0014 — Typed Studio Graph"; }
    @Override public String summary() { return "Expose the in-memory object graph composed by Bootstrap as a typed, queryable StudioGraph<S, F> record. Reuses the four registries already built at boot (Catalogue, Doc, Plan, StudioProxy) as underlying state; adds Stream-based query primitives. Foundation for auto-generated diagrams, cross-doc programmability, reverse-citation queries, live conformance, search index, and the typed doc DSL's computed-content phase. Small, additive, no breaking change."; }
    @Override public String category(){ return "RFC"; }

    @Override public List<Reference> references() {
        return List.of(
                new DocReference("rfc-11",          Rfc0011Doc.INSTANCE),
                new DocReference("rfc-12",          Rfc0012Doc.INSTANCE),
                new DocReference("rfc-13",          Rfc0013Doc.INSTANCE),
                new DocReference("doc-fo",          FunctionalObjectsDoc.INSTANCE),
                new DocReference("doc-wc",          WeighedComplexityDoc.INSTANCE),
                new DocReference("doc-cc",          CatalogueContainerDoc.INSTANCE),
                new DocReference("csref",           CrossStudioRefsCaseStudy.INSTANCE)
        );
    }
}
