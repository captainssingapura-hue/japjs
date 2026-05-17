package hue.captains.singapura.js.homing.studio;

import hue.captains.singapura.js.homing.core.AppModule;
import hue.captains.singapura.js.homing.studio.base.Bootstrap;
import hue.captains.singapura.js.homing.studio.base.CatalogueClosure;
import hue.captains.singapura.js.homing.studio.base.DefaultRuntimeParams;
import hue.captains.singapura.js.homing.studio.base.Doc;
import hue.captains.singapura.js.homing.studio.base.Studio;
import hue.captains.singapura.js.homing.studio.base.Umbrella;
import hue.captains.singapura.js.homing.studio.base.graph.StudioGraphBuilder;
import hue.captains.singapura.js.homing.studio.base.app.Catalogue;
import hue.captains.singapura.js.homing.studio.base.tracker.Acceptance;
import hue.captains.singapura.js.homing.studio.base.tracker.Decision;
import hue.captains.singapura.js.homing.studio.base.tracker.Dependency;
import hue.captains.singapura.js.homing.studio.base.tracker.Metric;
import hue.captains.singapura.js.homing.studio.base.tracker.Objective;
import hue.captains.singapura.js.homing.studio.base.tracker.Plan;
import hue.captains.singapura.js.homing.studio.base.tracker.Task;
import hue.captains.singapura.js.homing.studio.docs.casestudies.CrossStudioRefsCaseStudy;
import hue.captains.singapura.js.homing.studio.docs.casestudies.PrivacyDoctrineSecurityCaseStudy;
import hue.captains.singapura.js.homing.studio.docs.casestudies.TenantIsolationDecomposedCaseStudy;
import hue.captains.singapura.js.homing.studio.docs.doctrines.CatalogueContainerDoc;
import hue.captains.singapura.js.homing.studio.docs.doctrines.DualAudienceSkillsDoc;
import hue.captains.singapura.js.homing.studio.docs.doctrines.FirstUserDoc;
import hue.captains.singapura.js.homing.studio.docs.doctrines.FunctionalObjectsDoc;
import hue.captains.singapura.js.homing.studio.docs.doctrines.ManagedDomOpsDoc;
import hue.captains.singapura.js.homing.studio.docs.doctrines.MethodsOverPropsDoc;
import hue.captains.singapura.js.homing.studio.docs.doctrines.NoStealthDataDoc;
import hue.captains.singapura.js.homing.studio.docs.doctrines.OwnedReferencesDoc;
import hue.captains.singapura.js.homing.studio.docs.doctrines.PlanContainerDoc;
import hue.captains.singapura.js.homing.studio.docs.doctrines.PureComponentViewsDoc;
import hue.captains.singapura.js.homing.studio.docs.doctrines.QualityWithoutSurveillanceDoc;
import hue.captains.singapura.js.homing.studio.docs.doctrines.StatelessServerDoc;
import hue.captains.singapura.js.homing.studio.docs.doctrines.WeighedComplexityDoc;
import hue.captains.singapura.js.homing.studio.docs.rfcs.Rfc0001Doc;
import hue.captains.singapura.js.homing.studio.docs.rfcs.Rfc0009Doc;
import hue.captains.singapura.js.homing.studio.docs.rfcs.Rfc0010Doc;
import hue.captains.singapura.js.homing.studio.docs.rfcs.Rfc0011Doc;
import hue.captains.singapura.js.homing.studio.docs.rfcs.Rfc0012Doc;
import hue.captains.singapura.js.homing.studio.docs.rfcs.Rfc0013Doc;
import hue.captains.singapura.js.homing.studio.es.ArchitectureRfcsCatalogue;
import hue.captains.singapura.js.homing.studio.es.AudienceDoctrinesCatalogue;
import hue.captains.singapura.js.homing.studio.es.CaseStudiesCatalogue;
import hue.captains.singapura.js.homing.studio.es.CodeDoctrinesCatalogue;
import hue.captains.singapura.js.homing.studio.es.ContainerDoctrinesCatalogue;
import hue.captains.singapura.js.homing.studio.es.DoctrineCatalogue;
import hue.captains.singapura.js.homing.studio.es.JourneysCatalogue;
import hue.captains.singapura.js.homing.studio.es.RfcsCatalogue;
import hue.captains.singapura.js.homing.studio.es.StudioCatalogue;
import hue.captains.singapura.js.homing.studio.es.TrustDoctrinesCatalogue;
import hue.captains.singapura.js.homing.studio.es.ViewDoctrinesCatalogue;
import hue.captains.singapura.js.homing.studio.HomingStudio;
import hue.captains.singapura.tao.ontology.enforcer.ContractViolation;
import hue.captains.singapura.tao.ontology.enforcer.OntologyEnforcer;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * RFC 0013 — runs the jOntology {@link OntologyEnforcer} over a curated list of
 * the framework's marked types. Verifies two things:
 *
 * <ol>
 *   <li>Each directly-marked spine interface and field-bearing type satisfies
 *       its declared classification's contract.</li>
 *   <li>Sample implementations transitively inherit their spine interface's
 *       marker — verifying the interface-level marking design works.</li>
 * </ol>
 *
 * <h2>Why a curated list, not a package scan</h2>
 *
 * <p>The enforcer flags <i>unmarked</i> classes as violations
 * (<i>"Who are you, where do you come from..."</i>), so a blanket package scan
 * would fail until every class in the framework is classified. We start with a
 * curated list and grow it as more types are classified and known-good.</p>
 *
 * <h2>Known follow-ups (excluded from this list pending fixes)</h2>
 *
 * <p>jOntology's {@code Immutable} contract is stricter than Homing's
 * Functional Objects doctrine in two specific ways:</p>
 *
 * <ol>
 *   <li><b>All static methods are forbidden</b>, including private ones the
 *       Functional Objects doctrine permits as "internal organisation."
 *       Affects: {@code DocBrowser.entry(...)} (called from a {@code static final
 *       List<...>} initialiser, hard to refactor), {@code Rfc0001PlanData} etc.
 *       adapter helpers (mechanical refactor possible).</li>
 *   <li><b>{@code java.util.List/Map/Set} aren't in the known-immutable set</b>,
 *       so any record with a {@code List<...>} field fails the transitive
 *       immutable check (even when the constructor does {@code List.copyOf}).
 *       Affects: {@code Umbrella.Group} (children list), {@code DefaultFixtures}
 *       transitively, {@code StudioBrand} via {@code SvgRef} via further chain.
 *       Possible fixes: custom Immutable-marked list wrapper, contribution to
 *       jOntology, or accept these as classification debt.</li>
 *   <li>{@code StudioBrand.logo} field type {@code SvgRef<?>} not yet marked
 *       Immutable. Marking SvgRef requires marking {@code SvgGroup} +
 *       {@code SvgBeing} + {@code EsModule} chain in homing-core.</li>
 *   <li>{@code StudioGraph} (RFC 0014 Phase 1) has {@code Set<Object>} and
 *       {@code Set<Edge>} fields — {@code java.util.Set} not in jOntology's
 *       known-immutable set, same root cause as item 2 above. The record is
 *       marked {@code ValueObject} (correct intent) but excluded from this
 *       test until the collection-immutability story is resolved.</li>
 * </ol>
 *
 * <p>None of these are framework correctness bugs — they're tensions between
 * jOntology's stricter contract and the framework's existing posture.</p>
 */
class OntologyConformanceTest {

    /** Curated list of marked types known to satisfy their contract. Grow as more are classified. */
    private static final List<Class<?>> CLEAN_TYPES = List.of(
            // ----- Spine interfaces (directly marked StatelessFunctionalObject) -----
            AppModule.class,
            Doc.class,
            Catalogue.class,
            Plan.class,
            Studio.class,

            // ----- Field-bearing types (clean cases — all field types are Immutable) -----
            DefaultRuntimeParams.class,    // record(int port)
            CatalogueClosure.class,        // record() with INSTANCE pattern
            StudioGraphBuilder.class,      // record() with INSTANCE pattern (RFC 0014 Phase 1)
            Bootstrap.class,               // record(F fixtures, RuntimeParams params) — both Immutable
            Umbrella.Solo.class,           // record(S studio) — S extends Studio (Immutable)

            // ----- Sealed interface markers (Immutable) -----
            Umbrella.class,                // sealed Immutable; permits Solo + Group
            // Entry.class — has static `of(...)` factory methods (RFC 0011 typed factories).
            // Refactoring would require the host catalogue's `leaves()` to construct entries
            // directly via constructors. Deferred — Entry stays marked Immutable so its record
            // permits inherit it; the parent itself isn't conformance-tested yet.

            // ----- Sample spine-interface implementations (verify inheritance propagates) -----
            HomingStudio.class,            // inherits Studio → StatelessFunctionalObject

            // ----- Catalogue records (inherit Catalogue → StatelessFunctionalObject) -----
            StudioCatalogue.class,
            DoctrineCatalogue.class,
            AudienceDoctrinesCatalogue.class,
            ViewDoctrinesCatalogue.class,
            ContainerDoctrinesCatalogue.class,
            CodeDoctrinesCatalogue.class,
            TrustDoctrinesCatalogue.class,
            RfcsCatalogue.class,
            ArchitectureRfcsCatalogue.class,
            JourneysCatalogue.class,
            CaseStudiesCatalogue.class,

            // ----- All 13 doctrine docs (inherit Doc → StatelessFunctionalObject) -----
            FirstUserDoc.class,
            DualAudienceSkillsDoc.class,
            PureComponentViewsDoc.class,
            MethodsOverPropsDoc.class,
            ManagedDomOpsDoc.class,
            OwnedReferencesDoc.class,
            CatalogueContainerDoc.class,
            PlanContainerDoc.class,
            WeighedComplexityDoc.class,
            FunctionalObjectsDoc.class,
            NoStealthDataDoc.class,
            StatelessServerDoc.class,
            QualityWithoutSurveillanceDoc.class,

            // ----- Sample RFC docs (inherit Doc → StatelessFunctionalObject) -----
            Rfc0001Doc.class,
            Rfc0009Doc.class,
            Rfc0010Doc.class,
            Rfc0011Doc.class,
            Rfc0012Doc.class,
            Rfc0013Doc.class,

            // ----- All case study docs (inherit Doc → StatelessFunctionalObject) -----
            CrossStudioRefsCaseStudy.class,
            PrivacyDoctrineSecurityCaseStudy.class,
            TenantIsolationDecomposedCaseStudy.class,

            // ----- Plan tracker records (directly marked ValueObject) -----
            // Phase is excluded — has List<Task> / List<Dependency> / List<Metric> fields
            // that fail the transitive immutable check (List not in jOntology's known set).
            Task.class,
            Acceptance.class,
            Objective.class,
            Dependency.class,
            Metric.class,
            Decision.class
    );

    @Test
    void everyMarkedTypeHonoursItsContract() {
        var enforcer = new OntologyEnforcer();
        var allViolations = new ArrayList<String>();

        for (var type : CLEAN_TYPES) {
            List<ContractViolation> violations = enforcer.enforce(type);
            for (var v : violations) {
                allViolations.add(v.message());
            }
        }

        assertTrue(allViolations.isEmpty(),
                "jOntology contract violations:\n  - " + String.join("\n  - ", allViolations));
    }
}
