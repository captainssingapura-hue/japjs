package hue.captains.singapura.js.homing.studio.base.composed;

import hue.captains.singapura.js.homing.core.AppModule;
import hue.captains.singapura.js.homing.studio.base.app.ContentViewer;

/**
 * RFC 0019 — framework-default {@link ContentViewer} for
 * {@link ComposedDoc} (kind == {@code "composed"}). Binds the composed
 * kind to {@link ComposedViewer}.
 *
 * <p>Registered by the framework's default {@code Fixtures.contentViewers()}
 * list. Downstream may override the default registry to substitute a
 * custom composed viewer if needed.</p>
 *
 * @since RFC 0019 Phase 1
 */
public record ComposedContentViewer() implements ContentViewer {

    public static final ComposedContentViewer INSTANCE = new ComposedContentViewer();

    @Override public String kind() { return "composed"; }

    @Override public AppModule<?, ?> app() { return ComposedViewer.INSTANCE; }

    @Override public String urlFor(String contentId) {
        return "/app?app=composed-viewer&id=" + contentId;
    }

    @Override public String summary() {
        return "Composed-doc renderer (RFC 0019) — fetches /doc?id=<uuid> (JSON: title + toc + segments) and dispatches per segment kind (markdown via marked.js; svg via proxy fetch).";
    }
}
