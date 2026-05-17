package hue.captains.singapura.js.homing.studio.docs.casestudies;

import hue.captains.singapura.js.homing.studio.base.ClasspathMarkdownDoc;
import hue.captains.singapura.js.homing.studio.base.DocReference;
import hue.captains.singapura.js.homing.studio.base.Reference;
import hue.captains.singapura.js.homing.studio.docs.doctrines.NoStealthDataDoc;
import hue.captains.singapura.js.homing.studio.docs.doctrines.StatelessServerDoc;

import java.util.List;
import java.util.UUID;

/**
 * Case study — what "tenant isolation" actually decomposes into, and why
 * shared-application multi-tenancy is a deliberate engineering choice
 * rather than negligence when implemented via the cryptographic
 * end-to-end pattern.
 *
 * <p>Seven flavours of isolation: data, identity, cryptographic, compute,
 * failure, side-channel, operational. Container-per-tenant gives all seven
 * for free; shared-app can achieve three of them (data, identity,
 * cryptographic) to a <i>stronger</i> standard than containers via
 * client-side encryption with the server reduced to opaque storage; and
 * structurally cannot achieve the other four.</p>
 *
 * <p>The framework's <i>verifiable trust</i> meta-property is strengthened
 * by the cryptographic pattern: the user no longer has to trust the
 * operator's competence — only the cryptography, which is publicly
 * auditable. This makes the cryptographic shared-app pattern the
 * doctrines' strongest available expression for multi-tenant SaaS.</p>
 *
 * <p>Industry observation: most SaaS uses neither pattern, defaulting to
 * RLS-discipline shared-app where isolation is enforced by application
 * code correctness. This is the source of most multi-tenant breach
 * disclosures.</p>
 */
public record TenantIsolationDecomposedCaseStudy() implements ClasspathMarkdownDoc {
    private static final UUID ID = UUID.fromString("e7c5d9a1-3b4f-4e6d-8a2c-9f1d6b3e8c50");
    public static final TenantIsolationDecomposedCaseStudy INSTANCE = new TenantIsolationDecomposedCaseStudy();

    @Override public UUID   uuid()    { return ID; }
    @Override public String title()   { return "Case Study — Tenant Isolation, Decomposed"; }
    @Override public String summary() { return "\"Tenant isolation\" isn't one property — it decomposes into seven (data, identity, cryptographic, compute, failure, side-channel, operational). Container-per-tenant gives all seven for free. Shared-app can achieve the first three to a stronger standard via end-to-end encryption with the server reduced to opaque storage — but structurally cannot achieve the other four. Most SaaS uses neither pattern and lives with the resulting periodic breach disclosures. The framework's verifiable-trust property favours the cryptographic pattern when shared-app multi-tenancy is required."; }
    @Override public String category(){ return "CASE_STUDY"; }

    @Override public List<Reference> references() {
        return List.of(
                new DocReference("priv-sec", PrivacyDoctrineSecurityCaseStudy.INSTANCE),
                new DocReference("doc-ss",   StatelessServerDoc.INSTANCE),
                new DocReference("doc-nsd",  NoStealthDataDoc.INSTANCE),
                new DocReference("csref",    CrossStudioRefsCaseStudy.INSTANCE)
        );
    }
}
