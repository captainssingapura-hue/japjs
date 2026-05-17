package hue.captains.singapura.js.homing.studio.base.image;

import hue.captains.singapura.js.homing.core.DomModule;
import hue.captains.singapura.js.homing.core.Exportable;
import hue.captains.singapura.js.homing.core.ExportsOf;
import hue.captains.singapura.js.homing.core.ImportsFor;
import hue.captains.singapura.js.homing.core.ModuleImports;
import hue.captains.singapura.js.homing.studio.base.css.StudioStyles;

import java.util.List;

/**
 * RFC 0020 — shared JS renderer for {@link ImageViewer}. Fetches the
 * image envelope JSON via {@code /doc?id=<uuid>} and renders a
 * {@code <figure>} with the image (data URL) and optional caption.
 *
 * @since RFC 0020
 */
public record ImageViewerRenderer() implements DomModule<ImageViewerRenderer> {

    public record renderImage() implements Exportable._Constant<ImageViewerRenderer> {}

    public static final ImageViewerRenderer INSTANCE = new ImageViewerRenderer();

    @Override
    public ImportsFor<ImageViewerRenderer> imports() {
        return ImportsFor.<ImageViewerRenderer>builder()
                .add(new ModuleImports<>(List.of(
                        new StudioStyles.st_image_figure(),
                        new StudioStyles.st_image_img(),
                        new StudioStyles.st_image_caption(),
                        new StudioStyles.st_loading(),
                        new StudioStyles.st_error()
                ), StudioStyles.INSTANCE))
                .build();
    }

    @Override
    public ExportsOf<ImageViewerRenderer> exports() {
        return new ExportsOf<>(INSTANCE, List.of(new renderImage()));
    }
}
