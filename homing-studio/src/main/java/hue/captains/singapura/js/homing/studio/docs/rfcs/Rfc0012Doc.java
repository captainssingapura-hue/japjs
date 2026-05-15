package hue.captains.singapura.js.homing.studio.docs.rfcs;

import hue.captains.singapura.js.homing.studio.base.ClasspathMarkdownDoc;
import hue.captains.singapura.js.homing.studio.base.DocReference;
import hue.captains.singapura.js.homing.studio.base.Reference;
import hue.captains.singapura.js.homing.studio.docs.doctrines.CatalogueContainerDoc;
import hue.captains.singapura.js.homing.studio.docs.doctrines.FunctionalObjectsDoc;
import hue.captains.singapura.js.homing.studio.docs.doctrines.WeighedComplexityDoc;

import java.util.List;
import java.util.UUID;

/**
 * RFC 0012 — Typed Studio Composition. Replaces the procedural ten-parameter
 * {@code StudioBootstrap.start(...)} with a four-type composition primitive:
 * {@code Studio<L0>} (intrinsic studio declaration), {@code Umbrella<S>}
 * (ADT placing studios in a tree, leaves = studios, branches = grouping
 * categories), {@code Fixtures<S>} (the harness wrapping — extra apps,
 * actions, and node chrome, the downstream extensibility seam), and
 * {@code RuntimeParams} (deployment knobs). All four flow into a
 * {@code Bootstrap<S, F extends Fixtures<S>>} record with a no-arg
 * {@code start()} method.
 *
 * <p>Closes the multi-studio gap RFC 0010's launcher pattern revealed: the
 * demo server today hand-merges every studio's catalogues, apps, plans,
 * themes, and brand in {@code DemoStudioServer.main()}. With one wrong
 * register, an AppModule URL silently 404s. After this RFC, each studio
 * exposes its needs through a typed {@code Studio<L0>} record; the
 * Umbrella ADT carries composition structure; Fixtures owns harness
 * extension; the bootstrap collapses to record construction + start().</p>
 *
 * <p>Also resolves the {@code StudioBootstrap} parameter explosion + its
 * violation of the Functional Objects doctrine — every public method in
 * the new surface lives on an instance of a typed object. No public
 * static methods anywhere.</p>
 */
public record Rfc0012Doc() implements ClasspathMarkdownDoc {
    private static final UUID ID = UUID.fromString("6c2e8a4f-1d5b-4e3a-9b7c-3d6e9f5a8c20");
    public static final Rfc0012Doc INSTANCE = new Rfc0012Doc();

    @Override public UUID   uuid()    { return ID; }
    @Override public String title()   { return "RFC 0012 — Typed Studio Composition"; }
    @Override public String summary() { return "Replaces the procedural ten-parameter StudioBootstrap.start(...) with four typed primitives — Studio<L0> for the studio's intrinsic bundle, Umbrella<S> as an ADT placing studios in a navigable tree (leaves = studios, branches = grouping categories), Fixtures<S> as the harness extensibility seam (extra apps, actions, node chrome), and RuntimeParams for deployment knobs — composed into a Bootstrap<S, F> record with a no-arg start(). Closes the multi-studio composition gap RFC 0010 exposed, eliminates the bootstrap's parameter explosion, and satisfies the Functional Objects doctrine by construction."; }
    @Override public String category(){ return "RFC"; }

    @Override public List<Reference> references() {
        return List.of(
                new DocReference("rfc-5-ext2", Rfc0005Ext2Doc.INSTANCE),
                new DocReference("rfc-10",     Rfc0010Doc.INSTANCE),
                new DocReference("rfc-11",     Rfc0011Doc.INSTANCE),
                new DocReference("doc-cc",     CatalogueContainerDoc.INSTANCE),
                new DocReference("doc-fo",     FunctionalObjectsDoc.INSTANCE),
                new DocReference("wc",         WeighedComplexityDoc.INSTANCE)
        );
    }
}
