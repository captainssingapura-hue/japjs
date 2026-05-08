package hue.captains.singapura.js.homing.studio.base;

/**
 * A {@link Doc} of kind SVG ({@code .svg}, {@code image/svg+xml}).
 *
 * <p>Useful for diagrams, icons, illustrations the studio embeds inline. SVG
 * is text-based, so it travels through the same string-based serving pipeline
 * as markdown / HTML / plain text — no binary plumbing required.</p>
 *
 * @param <D> the {@link DocGroup} this doc belongs to
 */
public interface SvgDoc<D extends DocGroup<D>> extends Doc<D> {
    @Override default String contentType()   { return "image/svg+xml; charset=utf-8"; }
    @Override default String fileExtension() { return ".svg"; }
}
