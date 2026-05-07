package hue.captains.singapura.js.homing.studio.base.theme;

import hue.captains.singapura.js.homing.core.Theme;
import hue.captains.singapura.js.homing.core.ThemeGlobals;
import hue.captains.singapura.js.homing.core.ThemeVariables;
import hue.captains.singapura.js.homing.server.ThemeRegistry;

import java.util.List;

/**
 * RFC 0002-ext1 Phase 10 — registry of theme artifacts shipped by
 * {@code homing-studio-base}.
 *
 * <p>Every {@link Theme} the studio supports has a registered
 * {@link ThemeVariables} (the variable values, served at {@code /theme-vars})
 * and a {@link ThemeGlobals} (the raw global rules, served at
 * {@code /theme-globals}).</p>
 *
 * <p>Adding a new theme: implement {@link Theme} + nested {@code Vars} +
 * nested {@code Globals} (mirroring {@link HomingDefault}), then append all
 * three singletons to the lists below.</p>
 */
public final class StudioThemeRegistry implements ThemeRegistry {

    public static final StudioThemeRegistry INSTANCE = new StudioThemeRegistry();

    @Override public List<Theme> themes() {
        return List.of(
                HomingDefault.INSTANCE,
                HomingForest.INSTANCE,
                HomingSunset.INSTANCE
        );
    }

    @Override public List<ThemeVariables<?>> variables() {
        return List.of(
                HomingDefault.Vars.INSTANCE,
                HomingForest.Vars.INSTANCE,
                HomingSunset.Vars.INSTANCE
        );
    }

    @Override public List<ThemeGlobals<?>> globals() {
        return List.of(
                HomingDefault.Globals.INSTANCE,
                HomingForest.Globals.INSTANCE,
                HomingSunset.Globals.INSTANCE
        );
    }
}
