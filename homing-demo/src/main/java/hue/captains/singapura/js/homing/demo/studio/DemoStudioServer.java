package hue.captains.singapura.js.homing.demo.studio;

import hue.captains.singapura.js.homing.demo.studio.multi.MultiStudio;
import hue.captains.singapura.js.homing.skills.SkillsStudio;
import hue.captains.singapura.js.homing.studio.HomingStudio;
import hue.captains.singapura.js.homing.studio.base.Bootstrap;
import hue.captains.singapura.js.homing.studio.base.DefaultFixtures;
import hue.captains.singapura.js.homing.studio.base.DefaultRuntimeParams;
import hue.captains.singapura.js.homing.studio.base.Studio;
import hue.captains.singapura.js.homing.studio.base.Umbrella;

import java.util.List;

/**
 * RFC 0012 — multi-studio demo server. Composes four typed studios under
 * one umbrella: {@link MultiStudio} (the launcher providing
 * {@code MultiStudioHome} and the three category L1s) plus three
 * contributors ({@link DemoBaseStudio}, {@link SkillsStudio},
 * {@link HomingStudio}). Brand resolution falls through to MultiStudio's
 * standalone brand — the turtle-logoed multi-studio umbrella.
 *
 * <p>Listens on port 8082 alongside the standalone single-studio servers.</p>
 */
public final class DemoStudioServer {

    private DemoStudioServer() {}

    public static void main(String[] args) {

        Umbrella<Studio<?>> umbrella = new Umbrella.Group<>(
                "Homing Multi-Studio Demo",
                "Three source studios composed onto one server, launched from a typed umbrella.",
                List.of(
                        new Umbrella.Solo<>(MultiStudio.INSTANCE),
                        new Umbrella.Solo<>(DemoBaseStudio.INSTANCE),
                        new Umbrella.Solo<>(SkillsStudio.INSTANCE),
                        new Umbrella.Solo<>(HomingStudio.INSTANCE)
                ));

        new Bootstrap<>(new DefaultFixtures<>(umbrella), new DefaultRuntimeParams(8082)).start();
    }
}
