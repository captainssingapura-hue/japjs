package hue.captains.singapura.js.homing.studio.base;

/**
 * A {@link Doc} of kind plain text ({@code .txt}, {@code text/plain}).
 *
 * <p>Useful for license files, release notes, raw logs — anything the viewer
 * should display verbatim in a {@code <pre>} block without markdown rendering.</p>
 *
 * @param <D> the {@link DocGroup} this doc belongs to
 */
public interface PlainTextDoc<D extends DocGroup<D>> extends Doc<D> {
    @Override default String contentType()   { return "text/plain; charset=utf-8"; }
    @Override default String fileExtension() { return ".txt"; }
}
