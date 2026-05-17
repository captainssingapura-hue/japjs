package hue.captains.singapura.js.homing.studio.base.app;

import hue.captains.singapura.js.homing.core.AppModule;

/**
 * RFC 0015 Phase 5 — framework-default {@link ContentViewer} for
 * AppModule-bound content (kind == {@code "app"}). The forwarding case:
 * "app" content is rendered by whatever AppModule the wrapped
 * {@code Navigable} is bound to, not by a single shared viewer.
 *
 * <p>This makes the {@link #app()} method semantically meaningless for
 * the "app" kind — every {@code AppDoc} brings its own renderer. The
 * viewer is here for registry completeness (every kind has a registered
 * Viewer, per Viewer ontology V3) and for URL composition (an opaque
 * contentId could be parsed back, but in practice {@code AppDoc.url()}
 * delegates to {@link Navigable#url()} for the typed-Params
 * composition).</p>
 *
 * <p>{@link #app()} returns null as a deliberate sentinel — callers
 * looking up "the app for kind=app" must use the Doc's own routing
 * ({@code doc.url()} or unwrap to {@code AppDoc.nav().app()}). Calling
 * {@code app()} here is a structural error.</p>
 *
 * <p>Registered by {@code DefaultFixtures.contentViewers()}.</p>
 *
 * @since RFC 0015 Phase 5
 */
public record AppContentViewer() implements ContentViewer {

    public static final AppContentViewer INSTANCE = new AppContentViewer();

    @Override public String kind() { return "app"; }

    /**
     * Returns {@code null} — the "app" kind has no single AppModule;
     * each AppDoc carries its own. Use {@code AppDoc.nav().app()} to
     * reach the per-content app instance.
     */
    @Override public AppModule<?, ?> app() { return null; }

    @Override public String urlFor(String contentId) {
        // contentId for AppDoc is opaque (the framework uses the typed
        // Navigable.url() directly via Doc.url()). For external callers
        // who pass an AppModule simpleName + raw param string, the URL
        // shape is /app?app=<contentId>.
        return "/app?app=" + contentId;
    }

    @Override public String summary() {
        return "Per-Doc AppModule dispatch — each AppDoc carries its own renderer; this entry exists for registry completeness.";
    }
}
