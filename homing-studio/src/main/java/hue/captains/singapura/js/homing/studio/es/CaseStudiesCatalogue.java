package hue.captains.singapura.js.homing.studio.es;

import hue.captains.singapura.js.homing.studio.base.app.L1_Catalogue;
import hue.captains.singapura.js.homing.studio.base.app.L2_Catalogue;

import java.util.List;

/**
 * Sub-catalogue collecting <i>case studies</i> — focused write-ups examining
 * specific phenomena in the framework, usually structured as "why does X
 * work for free?" or "what made Y so expensive?". Distinct from doctrines
 * (which prescribe) and RFCs (which decide); case studies <i>observe</i>.
 *
 * <p>Organised by subject area into typed L2 sub-catalogues. The studies
 * themselves are leaves of those L2s; this L1 holds only the
 * categorisation.</p>
 */
public record CaseStudiesCatalogue()
        implements L1_Catalogue<StudioCatalogue, CaseStudiesCatalogue> {

    public static final CaseStudiesCatalogue INSTANCE = new CaseStudiesCatalogue();

    @Override public StudioCatalogue parent() { return StudioCatalogue.INSTANCE; }
    @Override public String name()    { return "Case Studies"; }
    @Override public String summary() { return "Focused write-ups examining specific phenomena in the framework — typically why a property holds for free, or what made an alternative expensive. Doctrines prescribe; case studies observe."; }
    @Override public String badge()   { return "CASES"; }
    @Override public String icon()    { return "🔬"; }

    @Override public List<? extends L2_Catalogue<CaseStudiesCatalogue, ?>> subCatalogues() {
        return List.of(
                ArchitectureCaseStudiesCatalogue.INSTANCE,
                PrivacySecurityCaseStudiesCatalogue.INSTANCE
        );
    }
}
