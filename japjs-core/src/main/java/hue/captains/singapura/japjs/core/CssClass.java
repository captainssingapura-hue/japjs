package hue.captains.singapura.japjs.core;

/**
 * A CSS class declared within a {@link CssGroup}.
 * <p>Mirrors {@link SvgBeing} for {@link SvgGroup}: each implementing
 * record is an exportable constant whose simple name (snake_case)
 * maps 1:1 to a kebab-case CSS class name.</p>
 *
 * @param <C> the CssGroup this class belongs to
 */
public interface CssClass<C extends CssGroup<C>> extends Exportable._Constant<C> {
}
