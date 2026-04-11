package hue.captains.singapura.japjs.core;

import java.util.List;

/**
 * Declare all CSS imports for a given CssGroup
 * @param <C> the CSS resource those imports are for
 */
public record CssImportsFor<C extends CssGroup<C>>(
    C cssGroup,
    List<CssGroup<?>> imports
) {
    public static <C extends CssGroup<C>> CssImportsFor<C> none(C cssGroup) {
        return new CssImportsFor<>(cssGroup, List.of());
    }
}
