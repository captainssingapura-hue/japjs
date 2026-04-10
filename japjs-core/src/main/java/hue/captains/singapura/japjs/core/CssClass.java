package hue.captains.singapura.japjs.core;

/**
 * A CSS class declared within a {@link CssBeing}.
 * <p>Mirrors {@link SvgBeing} for {@link SvgGroup}: each implementing
 * record is an exportable constant whose simple name (snake_case)
 * maps 1:1 to a kebab-case CSS class name.</p>
 *
 * @param <C> the CssBeing this class belongs to
 */
public interface CssClass<C extends CssBeing<C>> extends Exportable._Constant<C> {
}
