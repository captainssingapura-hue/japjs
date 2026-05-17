package hue.captains.singapura.js.homing.studio.base.app;

import hue.captains.singapura.js.homing.core.AppModule;
import hue.captains.singapura.tao.ontology.StatelessFunctionalObject;

/**
 * RFC 0015 Phase 5 — typed binding between a content kind and the
 * AppModule that renders Docs of that kind. The framework's only
 * mechanism for declaring "this kind of content opens with this app."
 *
 * <p>Realises the Viewer ontology (V1–V10): identity by kind (V1–V2),
 * kind exclusivity (V3) — registered via
 * {@code Fixtures.contentViewers()}, with duplicate kinds rejected at
 * boot; Doc routing through kind (V4); URL composition (V6);
 * stateless-functional-object record shape (V10).</p>
 *
 * <p>Registration is via {@code Fixtures.contentViewers()}. Framework
 * defaults ship for the three built-in Doc kinds (prose, plan, app);
 * downstream studios add Viewers for additional kinds (diagrams, code
 * surfaces, 3D graph views, etc.) by registering more.</p>
 *
 * <p><b>Phase 5 status.</b> The protocol is defined and the framework
 * ships its three default Viewers. Bootstrap-side dispatch (e.g.
 * "validate every Doc's kind has a Viewer registered") and the
 * conformance test (per Viewer ontology axioms) land in follow-ups.
 * Today's framework primarily routes via {@link
 * hue.captains.singapura.js.homing.studio.base.Doc#url() Doc.url()}
 * polymorphism; ContentViewer is the registry surface for any future
 * code that needs the kind → viewer mapping at runtime.</p>
 *
 * @since RFC 0015 Phase 5
 */
public interface ContentViewer extends StatelessFunctionalObject {

    /**
     * The content-kind discriminator this viewer serves. Must match the
     * {@code kind()} returned by Docs that route through this viewer
     * (e.g. {@code "doc"}, {@code "plan"}, {@code "app"}). Unique
     * across the registered viewer set in a single deployment.
     */
    String kind();

    /**
     * The AppModule that renders Docs of {@link #kind()}. The framework
     * uses this to look up the renderer when dispatching content URLs.
     * Same AppModule serves every Doc of this kind (V5: one viewer,
     * many Docs).
     */
    AppModule<?, ?> app();

    /**
     * Compose the canonical URL for a Doc of this viewer's kind given
     * its identifier. The identifier shape is kind-specific (UUID for
     * prose, class FQN for plan, etc.); the viewer encapsulates the
     * URL grammar so consumers don't hard-code it.
     *
     * <p>Realises Viewer ontology V6 — canonical URL composition.</p>
     *
     * @param contentId opaque identifier; kind-specific format
     * @return absolute path including {@code ?app=…} and any required
     *         id params
     */
    String urlFor(String contentId);

    /** Optional one-line description for introspection / picker UI. */
    default String summary() { return ""; }
}
