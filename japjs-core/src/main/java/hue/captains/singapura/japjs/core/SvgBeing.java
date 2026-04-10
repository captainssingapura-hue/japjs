package hue.captains.singapura.japjs.core;

/**
 * A "Declaration" of an SVG resource within a SvgGroup.
 * Acts as an exportable constant from the group's generated ES module.
 * @param <G> the SvgGroup this being belongs to
 */
public interface SvgBeing<G extends SvgGroup<G>> extends Exportable._Constant<G> {
}
