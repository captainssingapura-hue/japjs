package hue.captains.singapura.js.homing.skills;

import hue.captains.singapura.js.homing.studio.base.Bootstrap;
import hue.captains.singapura.js.homing.studio.base.DefaultFixtures;
import hue.captains.singapura.js.homing.studio.base.DefaultRuntimeParams;
import hue.captains.singapura.js.homing.studio.base.Studio;
import hue.captains.singapura.js.homing.studio.base.Umbrella;

/**
 * RFC 0012 — the Skills mini-studio entry point. Constructs a {@link Solo}
 * umbrella around {@link SkillsStudio}, wraps in {@link DefaultFixtures},
 * and starts a {@link Bootstrap}.
 *
 * <p>Per the Dual-Audience Skills doctrine, this is the human-facing mode.
 * The CLI's {@code --dump} subcommand is the agent-facing mode.</p>
 */
public final class SkillsStudioServer {

    private SkillsStudioServer() {}

    public static void main(String[] args) {
        start(8080);
    }

    public static void start(int port) {
        Umbrella<Studio<?>> umbrella = new Umbrella.Solo<>(SkillsStudio.INSTANCE);
        new Bootstrap<>(new DefaultFixtures<>(umbrella), new DefaultRuntimeParams(port)).start();
    }
}
