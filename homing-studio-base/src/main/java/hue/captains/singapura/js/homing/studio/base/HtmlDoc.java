package hue.captains.singapura.js.homing.studio.base;

/**
 * A {@link Doc} of kind HTML ({@code .html}, {@code text/html}).
 *
 * <p>Useful for pre-rendered docs that the studio embeds directly into a viewer
 * pane without re-parsing markdown — exported reports, generated reference
 * pages, etc.</p>
 *
 * @param <D> the {@link DocGroup} this doc belongs to
 */
public interface HtmlDoc<D extends DocGroup<D>> extends Doc<D> {
    @Override default String contentType()   { return "text/html; charset=utf-8"; }
    @Override default String fileExtension() { return ".html"; }
}
