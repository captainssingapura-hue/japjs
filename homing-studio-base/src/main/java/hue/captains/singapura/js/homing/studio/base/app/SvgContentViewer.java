package hue.captains.singapura.js.homing.studio.base.app;

import hue.captains.singapura.js.homing.core.AppModule;

/**
 * RFC 0015 Phase 5 — framework-default {@link ContentViewer} for SVG
 * Docs (kind == {@code "svg"}). Binds the svg kind to {@link SvgViewer}.
 *
 * <p>Serves every Doc whose {@code kind()} returns {@code "svg"} — the
 * {@code SvgDoc} subtype wrapping a typed {@code SvgRef}. The
 * {@code contentId} is the SvgDoc's UUID; the URL resolves to
 * {@code SvgViewer} which fetches {@code /doc?id=<uuid>} and inlines
 * the SVG markup.</p>
 *
 * <p>Registered by the framework's default {@code Fixtures.contentViewers()}
 * list.</p>
 *
 * @since RFC 0016
 */
public record SvgContentViewer() implements ContentViewer {

    public static final SvgContentViewer INSTANCE = new SvgContentViewer();

    @Override public String kind() { return "svg"; }

    @Override public AppModule<?, ?> app() { return SvgViewer.INSTANCE; }

    @Override public String urlFor(String contentId) {
        return "/app?app=svg-viewer&id=" + contentId;
    }

    @Override public String summary() {
        return "Inline SVG renderer — fetches the SvgDoc's body (SVG markup) and inlines it centered on its own page.";
    }
}
