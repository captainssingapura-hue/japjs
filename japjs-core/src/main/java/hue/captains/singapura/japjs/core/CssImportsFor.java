package hue.captains.singapura.japjs.core;

import java.util.List;

/**
 * Declare all CSS imports for a given CssBeing
 * @param <C> the CSS resource those imports are for
 */
public record CssImportsFor<C extends CssBeing<C>>(
    C cssBeing,
    List<CssBeing<?>> imports
) {
    public static <C extends CssBeing<C>> CssImportsFor<C> none(C cssBeing) {
        return new CssImportsFor<>(cssBeing, List.of());
    }
}
