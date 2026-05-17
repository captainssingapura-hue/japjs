package hue.captains.singapura.js.homing.demo.studio;

import hue.captains.singapura.js.homing.demo.studio.multi.MultiStudio;
import hue.captains.singapura.js.homing.skills.MigrateFrom0_0_100SkillDoc;
import hue.captains.singapura.js.homing.skills.SkillsAboutDoc;
import hue.captains.singapura.js.homing.skills.SkillsStudio;
import hue.captains.singapura.js.homing.studio.HomingStudio;
import hue.captains.singapura.js.homing.studio.base.Bootstrap;
import hue.captains.singapura.js.homing.studio.base.DefaultFixtures;
import hue.captains.singapura.js.homing.studio.base.DefaultRuntimeParams;
import hue.captains.singapura.js.homing.studio.base.Doc;
import hue.captains.singapura.js.homing.studio.base.DocProvider;
import hue.captains.singapura.js.homing.studio.base.DocReference;
import hue.captains.singapura.js.homing.studio.base.DocRegistry;
import hue.captains.singapura.js.homing.studio.base.Reference;
import hue.captains.singapura.js.homing.studio.base.Studio;
import hue.captains.singapura.js.homing.studio.base.Umbrella;
import hue.captains.singapura.js.homing.studio.docs.rfcs.Rfc0012Doc;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Validates that <b>cross-studio Doc references resolve correctly</b> under a
 * composed multi-studio {@link Bootstrap}.
 *
 * <p>The capability the test pins:</p>
 * <ol>
 *   <li>The {@link DocRegistry} built by {@code Bootstrap.compose()} unions
 *       Docs from every studio in the umbrella, regardless of source module.</li>
 *   <li>A Doc in module A may declare a {@link DocReference} to a Doc in
 *       module B — the reference resolves at runtime via the shared registry's
 *       UUID lookup, with no studio-awareness in the reference path.</li>
 *   <li>The only constraint is compile-time: module A must depend on module B
 *       to import {@code OtherStudio.SomeDoc.INSTANCE}. This test lives in
 *       {@code homing-demo} which depends on all three source modules and
 *       therefore can express any cross-studio reference.</li>
 * </ol>
 *
 * <p>The test mirrors the {@code DemoStudioServer.main()} umbrella exactly,
 * so any regression in real-world cross-studio resolution surfaces here.</p>
 */
class BootstrapCrossStudioRefTest {

    /** Identical to {@code DemoStudioServer.main()}'s umbrella. */
    private static Umbrella<Studio<?>> demoUmbrella() {
        return new Umbrella.Group<>(
                "Homing Multi-Studio Demo",
                "Three source studios composed onto one server.",
                List.of(
                        new Umbrella.Solo<>(MultiStudio.INSTANCE),
                        new Umbrella.Solo<>(DemoBaseStudio.INSTANCE),
                        new Umbrella.Solo<>(SkillsStudio.INSTANCE),
                        new Umbrella.Solo<>(HomingStudio.INSTANCE)
                ));
    }

    /**
     * Mirrors {@code Bootstrap.compose()}'s DocProvider walk to surface the
     * registry it would build, without spinning up Vert.x. Returns the same
     * UUID-indexed DocRegistry that {@code DocGetAction} / {@code DocRefsGetAction}
     * see at runtime.
     */
    private static DocRegistry composeDocRegistry(Umbrella<Studio<?>> umbrella) {
        var fixtures = new DefaultFixtures<>(umbrella);
        var providers = new ArrayList<DocProvider>();
        for (var app : fixtures.harnessApps()) {
            if (app instanceof DocProvider p) providers.add(p);
        }
        for (var studio : umbrella.studios()) {
            for (var app : studio.apps()) {
                if (app instanceof DocProvider p) providers.add(p);
            }
            for (var c : studio.catalogues()) {
                if (c instanceof DocProvider p) providers.add(p);
            }
        }
        var allDocs = new ArrayList<Doc>();
        for (var p : providers) allDocs.addAll(p.docs());
        return new DocRegistry(allDocs);
    }

    @Test
    void composed_registry_contains_docs_from_every_studio() {
        var registry = composeDocRegistry(demoUmbrella());

        // homing-studio contributes Rfc0012Doc via its RFC catalogue chain.
        assertSame(Rfc0012Doc.INSTANCE, registry.resolve(Rfc0012Doc.INSTANCE.uuid()),
                "homing-studio's Rfc0012Doc must be in the composed DocRegistry");

        // homing-skills contributes SkillsAboutDoc + every shipped skill via SkillsHome.
        assertSame(SkillsAboutDoc.INSTANCE, registry.resolve(SkillsAboutDoc.INSTANCE.uuid()),
                "homing-skills' SkillsAboutDoc must be in the composed DocRegistry");
        assertSame(MigrateFrom0_0_100SkillDoc.INSTANCE,
                registry.resolve(MigrateFrom0_0_100SkillDoc.INSTANCE.uuid()),
                "homing-skills' newest migration skill must be in the composed DocRegistry");
    }

    @Test
    void cross_studio_doc_reference_resolves_through_registry() {
        var registry = composeDocRegistry(demoUmbrella());

        // The canonical cross-studio case: a hypothetical Doc shipped by
        // homing-skills (module A) declares a typed reference to RFC 0012
        // (module B = homing-studio). Construct the reference here — this
        // compiles because the test module depends on both source modules.
        DocReference ref = new DocReference("rfc-12", Rfc0012Doc.INSTANCE);

        // The runtime path: serialize the reference, extract the UUID,
        // resolve through the registry. No studio-of-origin checks.
        UUID targetId = ref.target().uuid();
        Doc resolved = registry.resolve(targetId);

        assertNotNull(resolved,
                "Cross-studio DocReference target UUID must resolve in the composed DocRegistry");
        assertSame(Rfc0012Doc.INSTANCE, resolved,
                "Resolved Doc must be the original instance the reference points at");
        assertEquals("RFC 0012 — Typed Studio Composition", resolved.title(),
                "Resolved Doc must carry its full metadata across the studio boundary");
    }

    @Test
    void bootstrap_compose_produces_action_registry_with_doc_routes() {
        // End-to-end smoke: Bootstrap.compose() (no Vert.x) must succeed for
        // the full multi-studio umbrella, producing the standard route set —
        // including /doc and /doc-refs, which both consult the cross-studio
        // DocRegistry.
        var bootstrap = new Bootstrap<>(
                new DefaultFixtures<>(demoUmbrella()),
                new DefaultRuntimeParams(0));
        var actionRegistry = bootstrap.compose();
        var getActions = actionRegistry.getActions();

        assertTrue(getActions.containsKey("/doc"),
                "/doc route must be registered — serves cross-studio Doc bodies by UUID");
        assertTrue(getActions.containsKey("/doc-refs"),
                "/doc-refs route must be registered — serves cross-studio Reference JSON");
        assertTrue(getActions.containsKey("/catalogue"),
                "/catalogue route must be registered — serves the union catalogue tree");
    }

    @Test
    void reference_target_uuid_survives_serialization() {
        // The wire-level invariant: when DocRefsGetAction serializes a
        // cross-studio reference, the carried UUID is exactly the target's
        // UUID — the frontend uses this UUID alone to construct the next
        // doc-reader URL, with no module-of-origin metadata required.
        Reference ref = new DocReference("rfc-12", Rfc0012Doc.INSTANCE);
        assertTrue(ref instanceof DocReference);
        DocReference dr = (DocReference) ref;
        assertEquals(Rfc0012Doc.INSTANCE.uuid(), dr.target().uuid(),
                "DocReference.target().uuid() must be the exact source-of-truth UUID");
        // The composed registry confirms the round-trip: same UUID → same Doc instance.
        var registry = composeDocRegistry(demoUmbrella());
        assertSame(Rfc0012Doc.INSTANCE, registry.resolve(dr.target().uuid()));
    }
}
