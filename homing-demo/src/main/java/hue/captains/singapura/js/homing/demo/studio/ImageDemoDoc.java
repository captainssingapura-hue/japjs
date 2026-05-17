package hue.captains.singapura.js.homing.demo.studio;

import hue.captains.singapura.js.homing.studio.base.image.ImageDoc;

import java.util.Optional;

/**
 * RFC 0020 demo — an {@link ImageDoc} pointing at a tiny PNG shipped in
 * {@code homing-demo}'s classpath. Demonstrates the Raw-tier image kind:
 * the raster is delivered as a base64 data URL inside a JSON envelope,
 * rendered by the framework's registered {@code ImageViewer}.
 *
 * <p>Production studios would point at brand artwork, screenshots, or
 * architecture diagrams. For the demo we ship a small in-image label
 * so the proof is visible at a glance.</p>
 */
public final class ImageDemoDoc {

    private ImageDemoDoc() {}

    public static final ImageDoc INSTANCE = new ImageDoc(
            "homing-demo/img/demo.png",
            "image/png",
            "ImageDoc demo placeholder — a 240×120 label rendered by System.Drawing for the RFC 0020 PoC.",
            "Figure — the ImageDoc kind, in action. Bytes shipped on the classpath, served as a base64 data URL.",
            Optional.of(240),
            Optional.of(120));
}
