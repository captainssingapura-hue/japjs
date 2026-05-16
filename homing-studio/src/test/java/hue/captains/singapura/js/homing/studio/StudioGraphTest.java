package hue.captains.singapura.js.homing.studio;

import hue.captains.singapura.js.homing.studio.base.Bootstrap;
import hue.captains.singapura.js.homing.studio.base.DefaultFixtures;
import hue.captains.singapura.js.homing.studio.base.DefaultRuntimeParams;
import hue.captains.singapura.js.homing.studio.base.Doc;
import hue.captains.singapura.js.homing.studio.base.Studio;
import hue.captains.singapura.js.homing.studio.base.Umbrella;
import hue.captains.singapura.js.homing.studio.base.app.Catalogue;
import hue.captains.singapura.js.homing.studio.base.graph.StudioGraph;
import hue.captains.singapura.js.homing.studio.base.tracker.Phase;
import hue.captains.singapura.js.homing.studio.base.tracker.Plan;
import hue.captains.singapura.js.homing.studio.docs.doctrines.FunctionalObjectsDoc;
import hue.captains.singapura.js.homing.studio.docs.doctrines.NoStealthDataDoc;
import hue.captains.singapura.js.homing.studio.es.StudioCatalogue;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * RFC 0014 Phase 1 — verifies {@code Bootstrap.graph()} produces a typed
 * in-memory object graph with the expected structural properties. Per the
 * Explicit-over-Implicit doctrine, all graph query methods return concrete
 * collections (Set), so assertions use {@code .size()} and
 * {@code .contains()} directly without consuming a stream.
 */
class StudioGraphTest {

    private StudioGraph buildHomingGraph() {
        Umbrella<Studio<?>> umbrella = new Umbrella.Solo<>(HomingStudio.INSTANCE);
        var bootstrap = new Bootstrap<>(new DefaultFixtures<>(umbrella), new DefaultRuntimeParams(8080));
        return bootstrap.graph();
    }

    @Test
    void graphBuildsAndIsNonEmpty() {
        var graph = buildHomingGraph();
        assertNotNull(graph);
        assertFalse(graph.vertices().isEmpty(), "graph should have vertices");
        assertFalse(graph.edges().isEmpty(),    "graph should have edges");
    }

    @Test
    void bootstrapIsAVertex() {
        var graph = buildHomingGraph();
        assertEquals(1, graph.verticesOfType(Bootstrap.class).size(),
                "exactly one Bootstrap vertex expected");
    }

    @Test
    void homingStudioReachableAsVertex() {
        var graph = buildHomingGraph();
        assertTrue(graph.vertices().contains(HomingStudio.INSTANCE),
                "HomingStudio.INSTANCE should be in the graph");
    }

    @Test
    void studioCatalogueReachableAsVertex() {
        var graph = buildHomingGraph();
        assertTrue(graph.vertices().contains(StudioCatalogue.INSTANCE),
                "StudioCatalogue.INSTANCE should be in the graph");
    }

    @Test
    void everyDoctrineDocReachable() {
        var graph = buildHomingGraph();
        assertTrue(graph.vertices().contains(FunctionalObjectsDoc.INSTANCE),
                "FunctionalObjectsDoc should be reachable");
        assertTrue(graph.vertices().contains(NoStealthDataDoc.INSTANCE),
                "NoStealthDataDoc should be reachable");
    }

    @Test
    void verticesOfTypeFiltersByJavaType() {
        var graph = buildHomingGraph();
        int catalogueCount = graph.verticesOfType(Catalogue.class).size();
        int docCount       = graph.verticesOfType(Doc.class).size();
        int planCount      = graph.verticesOfType(Plan.class).size();
        int phaseCount     = graph.verticesOfType(Phase.class).size();
        // At least: StudioCatalogue + 5 L1s + many L2s; lots of docs; 11 plans
        assertTrue(catalogueCount > 10, "expected at least 10 catalogues, got " + catalogueCount);
        assertTrue(docCount       > 30, "expected at least 30 docs, got "       + docCount);
        assertTrue(planCount      > 5,  "expected at least 5 plans, got "       + planCount);
        assertTrue(phaseCount     > 5,  "expected at least 5 phases, got "      + phaseCount);
    }

    @Test
    void catalogueChildrenRoundTrip() {
        var graph = buildHomingGraph();
        var children = graph.children(StudioCatalogue.INSTANCE);
        assertTrue(children.size() >= 5,
                "StudioCatalogue should have at least 5 children, got " + children.size());

        // Each child should list StudioCatalogue as a parent (via CONTAINS).
        for (var child : children) {
            assertTrue(graph.parents(child).contains(StudioCatalogue.INSTANCE),
                    "child " + child + " should list StudioCatalogue as parent");
        }
    }

    @Test
    void docReferencesAppearAsReferenceEdges() {
        var graph = buildHomingGraph();
        // FunctionalObjectsDoc references CatalogueContainerDoc and WeighedComplexityDoc, etc.
        var forwardRefs = graph.referencesFrom(FunctionalObjectsDoc.INSTANCE);
        assertFalse(forwardRefs.isEmpty(),
                "FunctionalObjectsDoc should have at least one reference edge");
    }

    @Test
    void reverseReferenceWalkWorks() {
        var graph = buildHomingGraph();
        // FunctionalObjectsDoc is cited by NoStealthDataDoc, StatelessServerDoc, etc.
        var reverseRefs = graph.referencedBy(FunctionalObjectsDoc.INSTANCE);
        assertFalse(reverseRefs.isEmpty(),
                "FunctionalObjectsDoc should be referenced by at least one doc");
    }

    @Test
    void outgoingReferenceEdgesCarryLabels() {
        var graph = buildHomingGraph();
        // Reference edges carry the doc-anchor name as their label.
        var refEdges = graph.outgoingReferenceEdges(FunctionalObjectsDoc.INSTANCE);
        assertFalse(refEdges.isEmpty(), "should have at least one outgoing reference edge");
        boolean anyHasLabel = refEdges.stream().anyMatch(e -> !e.label().isEmpty());
        assertTrue(anyHasLabel, "at least one outgoing reference edge should carry a label");
    }

    @Test
    void dumpRendersTextTree() {
        var graph = buildHomingGraph();
        String dump = graph.dump(StudioCatalogue.INSTANCE);
        assertNotNull(dump);
        assertTrue(dump.contains("StudioCatalogue"), "dump should mention the root by class name");
        assertTrue(dump.contains("DoctrineCatalogue"),
                "dump should descend into child catalogues like DoctrineCatalogue");
        assertTrue(dump.length() > 100, "dump should be substantial, got " + dump.length() + " chars");
    }
}
