package hue.captains.singapura.js.homing.studio.docs.doctrines;

import hue.captains.singapura.js.homing.studio.base.ClasspathMarkdownDoc;
import hue.captains.singapura.js.homing.studio.base.DocReference;
import hue.captains.singapura.js.homing.studio.base.Reference;
import hue.captains.singapura.js.homing.studio.docs.casestudies.PrivacyDoctrineSecurityCaseStudy;
import hue.captains.singapura.js.homing.studio.docs.rfcs.Rfc0001Doc;

import java.util.List;
import java.util.UUID;

/**
 * Doctrine — Quality Without Surveillance. A well-designed,
 * correctly-implemented, thoroughly-tested application does not need to
 * collect data from clients to make itself better. The engineering team
 * accepts the burden of quality through their own discipline (design,
 * testing, code review, internal usability work) and does not delegate
 * that burden onto users by extracting behavioural data dressed up as
 * "product improvement." If users want to give feedback, they give it
 * explicitly — through a form they filled out, a bug report they wrote,
 * a public post they chose to make. The application's quality is the
 * engineers' problem, not a tax on user attention.
 *
 * <p>Sharpens {@link NoStealthDataDoc} in one specific direction: that
 * doctrine refused <i>covert</i> gathering and permitted disclosed opt-in
 * collection; this doctrine asks whether disclosed opt-in
 * <i>"help us improve"</i> collection should even be reached for. The
 * answer is <b>no</b> — improve the engineering instead.</p>
 *
 * <p>Names the industry's most common cover story (<i>"to make the product
 * better"</i>) and refuses it categorically. Pairs with No Stealth Data
 * and Stateless Server under Privacy &amp; Trust.</p>
 */
public record QualityWithoutSurveillanceDoc() implements ClasspathMarkdownDoc {
    private static final UUID ID = UUID.fromString("f1a2b3c4-5e6d-4a7b-8c9f-1d2e3f4a5b60");
    public static final QualityWithoutSurveillanceDoc INSTANCE = new QualityWithoutSurveillanceDoc();

    @Override public UUID   uuid()    { return ID; }
    @Override public String title()   { return "Doctrine — Quality Without Surveillance"; }
    @Override public String summary() { return "A well-designed, correctly-implemented, thoroughly-tested application does not need to collect data from clients to make itself better. Engineering accepts the burden of quality through their own discipline; users give feedback only when they choose to. Sharpens No Stealth Data by refusing even disclosed opt-in \"help us improve\" telemetry — the canonical cover for substituting user attention for engineering work. Improve the engineering instead."; }
    @Override public String category(){ return "DOCTRINE"; }

    @Override public List<Reference> references() {
        return List.of(
                new DocReference("doc-nsd",  NoStealthDataDoc.INSTANCE),
                new DocReference("doc-ss",   StatelessServerDoc.INSTANCE),
                new DocReference("doc-fo",   FunctionalObjectsDoc.INSTANCE),
                new DocReference("priv-sec", PrivacyDoctrineSecurityCaseStudy.INSTANCE),
                new DocReference("rfc-1",    Rfc0001Doc.INSTANCE)
        );
    }
}
