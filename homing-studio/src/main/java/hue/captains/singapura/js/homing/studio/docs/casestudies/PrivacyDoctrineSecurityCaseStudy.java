package hue.captains.singapura.js.homing.studio.docs.casestudies;

import hue.captains.singapura.js.homing.studio.base.ClasspathMarkdownDoc;
import hue.captains.singapura.js.homing.studio.base.DocReference;
import hue.captains.singapura.js.homing.studio.base.Reference;
import hue.captains.singapura.js.homing.studio.docs.doctrines.FunctionalObjectsDoc;
import hue.captains.singapura.js.homing.studio.docs.doctrines.NoStealthDataDoc;
import hue.captains.singapura.js.homing.studio.docs.doctrines.StatelessServerDoc;

import java.util.List;
import java.util.UUID;

/**
 * Case study — the security profile of {@link NoStealthDataDoc} +
 * {@link StatelessServerDoc} together. Names what the two doctrines secure
 * structurally (entire breach classes eliminated because their preconditions
 * don't exist), what they leave on the table (per-user forensics,
 * behavioural anomaly detection), and the meta-property they achieve —
 * <i>verifiable</i> trust auditable from the user's own browser, stronger
 * than promised or audited trust.
 *
 * <p>The general principle: <i>cheap security properties fall out of
 * refusing the wrong shapes early</i>. Same pattern as the cross-studio
 * refs study (also filed under Case Studies) — what you refuse early, you
 * don't have to defend later.</p>
 */
public record PrivacyDoctrineSecurityCaseStudy() implements ClasspathMarkdownDoc {
    private static final UUID ID = UUID.fromString("d6b4e8c3-1a5f-4d7b-9e2c-8f3a1b6d5e70");
    public static final PrivacyDoctrineSecurityCaseStudy INSTANCE = new PrivacyDoctrineSecurityCaseStudy();

    @Override public UUID   uuid()    { return ID; }
    @Override public String title()   { return "Case Study — The Privacy Doctrines Have Nothing To Lose"; }
    @Override public String summary() { return "Security profile of the No Stealth Data + Stateless Server doctrines together. They eliminate entire breach classes (sessions, PII, passwords, GDPR exposure, XSS impact amplification) by refusing the precondition, at the cost of per-user forensics and behavioural anomaly detection. The meta-property — verifiable trust auditable from the user's own browser — is stronger than promised or audited trust. Cheap security properties fall out of refusing the wrong shapes early."; }
    @Override public String category(){ return "CASE_STUDY"; }

    @Override public List<Reference> references() {
        return List.of(
                new DocReference("doc-nsd",   NoStealthDataDoc.INSTANCE),
                new DocReference("doc-ss",    StatelessServerDoc.INSTANCE),
                new DocReference("doc-fo",    FunctionalObjectsDoc.INSTANCE),
                new DocReference("csref",     CrossStudioRefsCaseStudy.INSTANCE),
                new DocReference("tenant-iso", TenantIsolationDecomposedCaseStudy.INSTANCE)
        );
    }
}
