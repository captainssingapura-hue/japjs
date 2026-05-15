package hue.captains.singapura.js.homing.studio;

import hue.captains.singapura.js.homing.studio.base.Bootstrap;
import hue.captains.singapura.js.homing.studio.base.DefaultFixtures;
import hue.captains.singapura.js.homing.studio.base.DefaultRuntimeParams;
import hue.captains.singapura.js.homing.studio.base.Studio;
import hue.captains.singapura.js.homing.studio.base.Umbrella;

/**
 * RFC 0012 entry point for the Homing studio. Constructs an {@link Umbrella}
 * holding the single {@link HomingStudio}, wraps it in
 * {@link DefaultFixtures}, and starts a {@link Bootstrap}.
 */
public final class StudioServer {

    private StudioServer() {}

    public static void main(String[] args) {
        Umbrella<Studio<?>> umbrella = new Umbrella.Solo<>(HomingStudio.INSTANCE);
        new Bootstrap<>(new DefaultFixtures<>(umbrella), new DefaultRuntimeParams(8080)).start();
    }
}
