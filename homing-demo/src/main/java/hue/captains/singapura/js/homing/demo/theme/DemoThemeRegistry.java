package hue.captains.singapura.js.homing.demo.theme;

import hue.captains.singapura.js.homing.core.Theme;
import hue.captains.singapura.js.homing.core.ThemeGlobals;
import hue.captains.singapura.js.homing.core.ThemeVariables;
import hue.captains.singapura.js.homing.server.ThemeRegistry;

import java.util.List;

/**
 * RFC 0002-ext1 Phase 11 — registry of theme artifacts shipped by homing-demo.
 *
 * <p>Mirrors {@code StudioThemeRegistry}. Adding a new theme: implement
 * {@link Theme} + nested {@code Vars} + nested {@code Globals}, then append
 * all three singletons to the lists below.</p>
 */
public final class DemoThemeRegistry implements ThemeRegistry {

    public static final DemoThemeRegistry INSTANCE = new DemoThemeRegistry();

    @Override public List<Theme> themes() {
        return List.of(
                DemoDefault.INSTANCE,
                Beach.INSTANCE,
                Alpine.INSTANCE,
                Dracula.INSTANCE);
    }

    @Override public List<ThemeVariables<?>> variables() {
        return List.of(
                DemoDefault.Vars.INSTANCE,
                Beach.Vars.INSTANCE,
                Alpine.Vars.INSTANCE,
                Dracula.Vars.INSTANCE);
    }

    @Override public List<ThemeGlobals<?>> globals() {
        return List.of(
                DemoDefault.Globals.INSTANCE,
                Beach.Globals.INSTANCE,
                Alpine.Globals.INSTANCE,
                Dracula.Globals.INSTANCE);
    }
}
