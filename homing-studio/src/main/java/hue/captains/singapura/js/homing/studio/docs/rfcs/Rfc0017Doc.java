package hue.captains.singapura.js.homing.studio.docs.rfcs;

import hue.captains.singapura.js.homing.studio.base.ClasspathMarkdownDoc;
import hue.captains.singapura.js.homing.studio.base.DocReference;
import hue.captains.singapura.js.homing.studio.base.Reference;
import hue.captains.singapura.js.homing.studio.docs.doctrines.FunctionalObjectsDoc;
import hue.captains.singapura.js.homing.studio.docs.doctrines.StatelessServerDoc;
import hue.captains.singapura.js.homing.studio.docs.doctrines.WeighedComplexityDoc;
import hue.captains.singapura.js.homing.studio.docs.ontology.ViewerOntologyDoc;

import java.util.List;
import java.util.UUID;

/**
 * RFC 0017 — Themable Content. The next release's star feature: extend
 * the framework's existing CSS-custom-properties theme system through
 * inline visual content (SVG, HTML, and future visual kinds).
 *
 * <p>Until now, theme support stops at the chrome boundary — page
 * header, tile borders, breadcrumbs, prose all theme correctly, but
 * SVG / HTML bodies render with whatever colors the author hardcoded.
 * This RFC names the contract that lets content bodies opt into the
 * theme via {@code currentColor} and {@code var(--color-*)} references,
 * codifies a small stable token set, and adds the corresponding viewer
 * ontology axiom (V12 — Theme Scope Promise).</p>
 *
 * <p>Almost no new framework code — recognizes that the existing theme
 * machinery already cascades into inline content via CSS, and makes the
 * authoring discipline explicit. The compounding effect: every future
 * visual content kind inherits theme support by construction, not by
 * per-kind retrofit.</p>
 *
 * <p>Companion to {@link Rfc0015Doc} (polymorphic doc viewer) and
 * {@link Rfc0016Doc} (content trees) — together they form the
 * "content has reached parity with chrome" arc that this release
 * delivers.</p>
 */
public record Rfc0017Doc() implements ClasspathMarkdownDoc {
    private static final UUID ID = UUID.fromString("c5d6e7f8-9012-4d56-8045-7d8e9f102233");
    public static final Rfc0017Doc INSTANCE = new Rfc0017Doc();

    @Override public UUID   uuid()    { return ID; }
    @Override public String title()   { return "RFC 0017 — Themable Content"; }
    @Override public String summary() { return "Extend the framework's CSS-custom-properties theme system through inline visual content (SVG, HTML, future kinds). Until now, theme stops at the chrome boundary: chrome themes; SVG / HTML bodies render with hardcoded colors. This RFC names the contract — content opts in via currentColor and var(--color-*) — codifies a stable ~12-token set, adds Viewer ontology V12 (Theme Scope Promise), and files the Themable Tokens doctrine. Almost no new framework code; the existing theme cascade already extends to inline content via CSS. The star feature of 0.0.103: every future visual content kind inherits theme support by construction."; }
    @Override public String category(){ return "RFC"; }

    @Override public List<Reference> references() {
        return List.of(
                new DocReference("viewer-ontology", ViewerOntologyDoc.INSTANCE),
                new DocReference("rfc-7",           Rfc0007Doc.INSTANCE),
                new DocReference("rfc-14",          Rfc0014Doc.INSTANCE),
                new DocReference("rfc-15",          Rfc0015Doc.INSTANCE),
                new DocReference("rfc-16",          Rfc0016Doc.INSTANCE),
                new DocReference("doc-wc",          WeighedComplexityDoc.INSTANCE),
                new DocReference("doc-ss",          StatelessServerDoc.INSTANCE),
                new DocReference("doc-fo",          FunctionalObjectsDoc.INSTANCE)
        );
    }
}
