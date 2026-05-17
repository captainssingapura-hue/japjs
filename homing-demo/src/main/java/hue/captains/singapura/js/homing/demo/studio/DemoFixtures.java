package hue.captains.singapura.js.homing.demo.studio;

import hue.captains.singapura.js.homing.core.AppModule;
import hue.captains.singapura.js.homing.studio.base.DefaultFixtures;
import hue.captains.singapura.js.homing.studio.base.Fixtures;
import hue.captains.singapura.js.homing.studio.base.Studio;
import hue.captains.singapura.js.homing.studio.base.Umbrella;
import hue.captains.singapura.js.homing.studio.base.app.tree.ContentTree;
import hue.captains.singapura.tao.ontology.ValueObject;

import java.util.List;
import java.util.Objects;

/**
 * RFC 0016 — homing-demo's Fixtures override; delegates to
 * {@link DefaultFixtures} for everything except {@link #trees()}, which
 * surfaces the {@link AnimalsTree} demo ContentTree.
 *
 * <p>Demonstrates the seam: downstream studios register data-authored
 * trees via {@code Fixtures.trees()}; Bootstrap wires the
 * {@code TreeRegistry}, {@code TreeGetAction}, and {@code TreeAppHost}
 * automatically when the list is non-empty.</p>
 */
public record DemoFixtures<S extends Studio<?>>(Umbrella<S> umbrella)
        implements Fixtures<S>, ValueObject {

    public DemoFixtures {
        Objects.requireNonNull(umbrella);
    }

    @Override public List<AppModule<?, ?>> harnessApps() {
        return new DefaultFixtures<>(umbrella).harnessApps();
    }

    @Override public NodeChrome chromeFor(Umbrella<S> node) {
        return new DefaultFixtures<>(umbrella).chromeFor(node);
    }

    @Override public List<ContentTree> trees() {
        return List.of(AnimalsTree.INSTANCE);
    }
}
