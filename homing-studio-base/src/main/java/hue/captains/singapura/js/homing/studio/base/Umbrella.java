package hue.captains.singapura.js.homing.studio.base;

import java.util.List;
import java.util.Objects;

/**
 * RFC 0012 — a typed tree placing studios at the leaves and pure
 * organisational categories at the branches. Pure structure — no display
 * chrome, no behaviour beyond the closure walk. Visual chrome is supplied
 * by {@link Fixtures#chromeFor(Umbrella)}.
 *
 * <p>The deployer constructs an Umbrella at composition time. A standalone
 * deploy is a {@link Solo}; a multi-studio deploy is a {@link Group} with
 * {@code Solo} leaves (or further nested {@code Group}s for richer
 * categorisation).</p>
 *
 * <pre>{@code
 * // Standalone.
 * Umbrella<Studio<?>> u = new Umbrella.Solo<>(HomingStudio.INSTANCE);
 *
 * // Multi-studio.
 * Umbrella<Studio<?>> u = new Umbrella.Group<>("Homing Demo", "Three studios, one server.",
 *         List.of(
 *                 new Umbrella.Group<>("Learning", "Demos and tutorials.",
 *                         List.of(new Umbrella.Solo<>(DemoBaseStudio.INSTANCE))),
 *                 new Umbrella.Group<>("Tooling",  "Skills & utilities.",
 *                         List.of(new Umbrella.Solo<>(SkillsStudio.INSTANCE))),
 *                 new Umbrella.Group<>("Core",     "Framework reference.",
 *                         List.of(new Umbrella.Solo<>(HomingStudio.INSTANCE)))));
 * }</pre>
 *
 * @param <S> the studio type captured at the leaves; usually {@code Studio<?>}
 */
public sealed interface Umbrella<S extends Studio<?>>
        permits Umbrella.Group, Umbrella.Solo {

    /** A grouping category — pure organisational node. Carries content (name +
     *  summary) only; display chrome (badge, icon) is supplied by Fixtures. */
    record Group<S extends Studio<?>>(
            String name,
            String summary,
            List<Umbrella<S>> children) implements Umbrella<S> {
        public Group {
            Objects.requireNonNull(name);
            Objects.requireNonNull(summary);
            children = List.copyOf(children);
        }
    }

    /** A leaf — a single studio. */
    record Solo<S extends Studio<?>>(S studio) implements Umbrella<S> {
        public Solo {
            Objects.requireNonNull(studio);
        }
    }

    /** All studios in the umbrella, in tree-order. Sealed-switch dispatch. */
    default List<S> studios() {
        return switch (this) {
            case Solo<S> s   -> List.of(s.studio());
            case Group<S> g  -> g.children().stream()
                                  .flatMap(u -> u.studios().stream())
                                  .toList();
        };
    }
}
