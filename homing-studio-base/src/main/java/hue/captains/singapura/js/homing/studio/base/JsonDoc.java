package hue.captains.singapura.js.homing.studio.base;

/**
 * A {@link Doc} of kind JSON ({@code .json}, {@code application/json}).
 *
 * <p>Useful for structured fixtures, config samples, or schema documentation
 * that downstream viewers parse and pretty-print.</p>
 *
 * @param <D> the {@link DocGroup} this doc belongs to
 */
public interface JsonDoc<D extends DocGroup<D>> extends Doc<D> {
    @Override default String contentType()   { return "application/json; charset=utf-8"; }
    @Override default String fileExtension() { return ".json"; }
}
