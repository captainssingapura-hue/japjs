package hue.captains.singapura.js.homing.studio.es;

import hue.captains.singapura.js.homing.studio.base.Doc;
import hue.captains.singapura.js.homing.studio.base.DocProvider;
import hue.captains.singapura.js.homing.studio.base.app.Entry;
import hue.captains.singapura.js.homing.studio.base.app.L2_Catalogue;
import hue.captains.singapura.js.homing.studio.docs.casestudies.CostOfIterationCaseStudy;
import hue.captains.singapura.js.homing.studio.docs.casestudies.CrossStudioRefsCaseStudy;
import hue.captains.singapura.js.homing.studio.docs.casestudies.WhyWeDitchedHtmlCaseStudy;

import java.util.List;

/**
 * L2 sub-catalogue of {@link CaseStudiesCatalogue} — case studies about
 * architectural and compositional properties of the framework. Typically
 * <i>"why does X work for free?"</i> studies where the answer points at
 * specific identity, composition, or wire-surface choices made elsewhere.
 */
public record ArchitectureCaseStudiesCatalogue()
        implements L2_Catalogue<CaseStudiesCatalogue, ArchitectureCaseStudiesCatalogue>, DocProvider {

    public static final ArchitectureCaseStudiesCatalogue INSTANCE = new ArchitectureCaseStudiesCatalogue();

    @Override public CaseStudiesCatalogue parent() { return CaseStudiesCatalogue.INSTANCE; }
    @Override public String name()    { return "Architecture"; }
    @Override public String summary() { return "Case studies about architectural and compositional properties — usually \"why does X work for free?\", with the answer pointing at specific identity, composition, or wire-surface choices."; }
    @Override public String badge()   { return "CASES"; }
    @Override public String icon()    { return "🏗️"; }

    @Override public List<Entry<ArchitectureCaseStudiesCatalogue>> leaves() {
        return List.of(
                Entry.of(this, CrossStudioRefsCaseStudy.INSTANCE),
                Entry.of(this, CostOfIterationCaseStudy.INSTANCE),
                // RFC 0019 Phase 5 self-proof — written entirely in the
                // typed-content vocabulary as a ComposedDoc.
                Entry.of(this, WhyWeDitchedHtmlCaseStudy.INSTANCE)
        );
    }

    /**
     * RFC 0004: the case-study docs contributed to the studio's DocRegistry.
     *
     * <p>For the {@link WhyWeDitchedHtmlCaseStudy}, the ComposedDoc itself
     * lands in the registry via {@code DocRegistry.harvestSyntheticFromLeaves}
     * (it appears in {@link #leaves()}); the SvgDoc and TableDoc its
     * segments proxy at must be explicitly registered here because they're
     * not catalogue leaves themselves (only referenced from inside the
     * ComposedDoc's segments).</p>
     */
    @Override public List<Doc> docs() {
        return List.of(
                CrossStudioRefsCaseStudy.INSTANCE,
                CostOfIterationCaseStudy.INSTANCE,
                WhyWeDitchedHtmlCaseStudy.DIAGRAM_DOC,
                WhyWeDitchedHtmlCaseStudy.COMPARISON_DOC
        );
    }
}
