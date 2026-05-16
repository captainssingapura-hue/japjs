package hue.captains.singapura.js.homing.studio.es;

import hue.captains.singapura.js.homing.studio.base.Doc;
import hue.captains.singapura.js.homing.studio.base.DocProvider;
import hue.captains.singapura.js.homing.studio.base.app.Entry;
import hue.captains.singapura.js.homing.studio.base.app.L2_Catalogue;
import hue.captains.singapura.js.homing.studio.docs.casestudies.CostOfIterationCaseStudy;
import hue.captains.singapura.js.homing.studio.docs.casestudies.CrossStudioRefsCaseStudy;

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
                Entry.of(this, CostOfIterationCaseStudy.INSTANCE)
        );
    }

    /** RFC 0004: the case-study docs contributed to the studio's DocRegistry. */
    @Override public List<Doc> docs() {
        return List.of(
                CrossStudioRefsCaseStudy.INSTANCE,
                CostOfIterationCaseStudy.INSTANCE
        );
    }
}
