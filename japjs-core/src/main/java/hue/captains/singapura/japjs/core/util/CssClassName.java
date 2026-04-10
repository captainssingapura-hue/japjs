package hue.captains.singapura.japjs.core.util;

import hue.captains.singapura.japjs.core.CssClass;

/**
 * Converts a {@link CssClass} record's snake_case simple name
 * to its kebab-case CSS class name.
 */
public final class CssClassName {

    private CssClassName() {}

    /**
     * Converts snake_case record name to kebab-case CSS class name.
     * <p>Example: {@code pg_theme_btn} → {@code "pg-theme-btn"}</p>
     */
    @SuppressWarnings("rawtypes")
    public static String toCssName(Class<? extends CssClass> clazz) {
        return clazz.getSimpleName().replace('_', '-');
    }
}
