package hue.captains.singapura.js.homing.studio.es;

import hue.captains.singapura.js.homing.studio.base.Doc;
import hue.captains.singapura.js.homing.studio.base.DocProvider;
import hue.captains.singapura.js.homing.studio.base.app.Entry;
import hue.captains.singapura.js.homing.studio.base.app.L2_Catalogue;
import hue.captains.singapura.js.homing.studio.docs.casestudies.PrivacyDoctrineSecurityCaseStudy;
import hue.captains.singapura.js.homing.studio.docs.casestudies.TenantIsolationDecomposedCaseStudy;

import java.util.List;

/**
 * L2 sub-catalogue of {@link CaseStudiesCatalogue} — case studies about
 * the framework's privacy and security posture. Typically <i>"what threats
 * did we eliminate by refusing a precondition?"</i> studies, complementing
 * the Trust doctrines (No Stealth Data, Stateless Server) that prescribe
 * the stance.
 */
public record PrivacySecurityCaseStudiesCatalogue()
        implements L2_Catalogue<CaseStudiesCatalogue, PrivacySecurityCaseStudiesCatalogue>, DocProvider {

    public static final PrivacySecurityCaseStudiesCatalogue INSTANCE = new PrivacySecurityCaseStudiesCatalogue();

    @Override public CaseStudiesCatalogue parent() { return CaseStudiesCatalogue.INSTANCE; }
    @Override public String name()    { return "Privacy & Security"; }
    @Override public String summary() { return "Case studies about privacy and security posture — usually \"what threats did we eliminate by refusing a precondition?\". Complements the Trust doctrines that prescribe the stance."; }
    @Override public String badge()   { return "CASES"; }
    @Override public String icon()    { return "🛡️"; }

    @Override public List<Entry<PrivacySecurityCaseStudiesCatalogue>> leaves() {
        return List.of(
                Entry.of(this, PrivacyDoctrineSecurityCaseStudy.INSTANCE),
                Entry.of(this, TenantIsolationDecomposedCaseStudy.INSTANCE)
        );
    }

    /** RFC 0004: the case-study docs contributed to the studio's DocRegistry. */
    @Override public List<Doc> docs() {
        return List.of(
                PrivacyDoctrineSecurityCaseStudy.INSTANCE,
                TenantIsolationDecomposedCaseStudy.INSTANCE
        );
    }
}
