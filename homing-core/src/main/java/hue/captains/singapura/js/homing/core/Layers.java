package hue.captains.singapura.js.homing.core;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Layer ordering + helpers. Single source of truth for the cascade ladder
 * the framework's CSS bundles ride on.
 *
 * <p>The {@link #ASCENDING} list controls two things in lockstep:</p>
 * <ol>
 *   <li>The serving order of CSS chunks — earlier layers' rules are emitted
 *       first, later layers' rules emitted last (so cascade resolves to
 *       last-loaded-wins, which is also later-in-this-list-wins).</li>
 *   <li>The {@code @layer reset, layout, component, prose, state, media, theme;}
 *       declaration emitted at the top of every served bundle. This makes
 *       the cascade deterministic <i>regardless</i> of bundle load order —
 *       browsers honour {@code @layer} declarations independently of which
 *       stylesheet arrives first.</li>
 * </ol>
 *
 * <p>Both belts and braces. The Java ordering protects authors who don't
 * know about {@code @layer}; the {@code @layer} declaration protects
 * against future load-order shuffles the framework can't predict.</p>
 */
public final class Layers {

    /** Layer ladder — earlier wins less, later wins more. */
    public static final List<Class<? extends Layer>> ASCENDING = List.of(
            Reset.class,
            Layout.class,
            Component.class,
            Prose.class,
            State.class,
            MediaGated.class,
            ThemeOverlay.class
    );

    /** CSS @layer name (lowercase, used in {@code @layer X { … }} wrappers). */
    public static final Map<Class<? extends Layer>, String> CSS_NAME = Map.of(
            Reset.class,        "reset",
            Layout.class,       "layout",
            Component.class,    "component",
            Prose.class,        "prose",
            State.class,        "state",
            MediaGated.class,   "media",
            ThemeOverlay.class, "theme"
    );

    /**
     * Emit the {@code @layer …;} declaration that establishes the cascade
     * order at the top of a stylesheet bundle. Must appear before any
     * {@code @layer X { … }} wrapper.
     */
    public static String declaration() {
        String names = ASCENDING.stream()
                .map(CSS_NAME::get)
                .collect(Collectors.joining(", "));
        return "@layer " + names + ";";
    }

    /**
     * Recover the typed {@link Layer} an object opted into via
     * {@link InLayer}. Returns {@link Component} when no {@code InLayer}
     * marker is declared — that's the implicit default for the bulk case.
     *
     * <p>Reads the implementor's generic interface declarations via
     * reflection; the type argument to {@code InLayer<L>} is a class token
     * the compiler preserved on the supertype list (Java keeps generic
     * supertype info even after type erasure).</p>
     */
    @SuppressWarnings("unchecked")
    public static Class<? extends Layer> ofImplementor(Object o) {
        Class<?> klass = o.getClass();
        while (klass != null && klass != Object.class) {
            for (Type t : klass.getGenericInterfaces()) {
                if (t instanceof ParameterizedType pt && pt.getRawType() == InLayer.class) {
                    Type arg = pt.getActualTypeArguments()[0];
                    if (arg instanceof Class<?> cls && Layer.class.isAssignableFrom(cls)) {
                        return (Class<? extends Layer>) cls;
                    }
                }
            }
            klass = klass.getSuperclass();
        }
        return Component.class;
    }

    private Layers() {}
}
