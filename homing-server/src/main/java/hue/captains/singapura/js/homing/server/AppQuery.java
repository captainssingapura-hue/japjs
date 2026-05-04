package hue.captains.singapura.js.homing.server;

import hue.captains.singapura.tao.http.action.Param;

/**
 * Query parameters accepted by {@link AppHtmlGetAction} as of RFC 0001 Step 07.
 *
 * <p>Supports both:
 * <ul>
 *   <li>{@code ?app=&lt;simpleName&gt;} — the new contract; resolved via
 *       {@link hue.captains.singapura.js.homing.core.SimpleAppResolver};</li>
 *   <li>{@code ?class=&lt;canonicalClassName&gt;} — the legacy contract;
 *       used by code paths not yet migrated by Step 11.</li>
 * </ul>
 *
 * <p>If both are present, {@code app} wins. If neither is present, dispatch fails
 * with a 404.</p>
 *
 * @param simpleName the value of {@code ?app=} (preferred)
 * @param className  the value of {@code ?class=} (legacy fallback)
 * @param theme      optional theme propagated to the loaded module
 * @param locale     optional locale propagated to the loaded module
 */
public record AppQuery(String simpleName, String className, String theme, String locale)
        implements Param._QueryString {

    public boolean hasSimpleName() {
        return simpleName != null && !simpleName.isBlank();
    }

    public boolean hasClassName() {
        return className != null && !className.isBlank();
    }
}
