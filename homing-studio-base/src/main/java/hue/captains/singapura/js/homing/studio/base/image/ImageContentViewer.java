package hue.captains.singapura.js.homing.studio.base.image;

import hue.captains.singapura.js.homing.core.AppModule;
import hue.captains.singapura.js.homing.studio.base.app.ContentViewer;

/**
 * RFC 0020 — framework-default {@link ContentViewer} for {@link ImageDoc}
 * (kind == {@code "image"}). Binds the image kind to {@link ImageViewer}.
 *
 * <p>Registered by the framework's default {@code Fixtures.contentViewers()}
 * list.</p>
 *
 * @since RFC 0020
 */
public record ImageContentViewer() implements ContentViewer {

    public static final ImageContentViewer INSTANCE = new ImageContentViewer();

    @Override public String kind() { return "image"; }

    @Override public AppModule<?, ?> app() { return ImageViewer.INSTANCE; }

    @Override public String urlFor(String contentId) {
        return "/app?app=image-viewer&id=" + contentId;
    }

    @Override public String summary() {
        return "Raster image renderer (RFC 0020) — fetches the JSON envelope and renders a <figure> with the data-URL image and optional caption. Raw tier per RFC 0017 — no theming on the raster.";
    }
}
