package hue.captains.singapura.js.homing.studio.base;

/**
 * A {@link Doc} of kind markdown ({@code .md}, {@code text/markdown}).
 *
 * <p>The default {@code Doc} interface already targets markdown, so this
 * sub-interface is sugar — it makes the doc kind explicit at declaration
 * sites without requiring overrides.</p>
 *
 * @param <D> the {@link DocGroup} this doc belongs to
 */
public interface MarkdownDoc<D extends DocGroup<D>> extends Doc<D> {
    // Defaults inherited from Doc — already text/markdown + .md.
}
