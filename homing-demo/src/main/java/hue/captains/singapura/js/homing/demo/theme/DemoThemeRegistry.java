package hue.captains.singapura.js.homing.demo.theme;

import hue.captains.singapura.js.homing.core.Theme;
import hue.captains.singapura.js.homing.core.ThemeGlobals;
import hue.captains.singapura.js.homing.core.ThemeVariables;
import hue.captains.singapura.js.homing.server.ThemeRegistry;

import java.util.List;

/**
 * Registry of theme artifacts shipped by homing-demo. Three military themes,
 * each turning the moving-animal demo's platforms into a different vehicle
 * silhouette set. Navy is the default (first in the list).
 */
public final class DemoThemeRegistry implements ThemeRegistry {

    public static final DemoThemeRegistry INSTANCE = new DemoThemeRegistry();

    @Override public List<Theme> themes() {
        return List.of(
                Navy.INSTANCE,
                AirForce.INSTANCE,
                Army.INSTANCE);
    }

    @Override public List<ThemeVariables<?>> variables() {
        return List.of(
                Navy.Vars.INSTANCE,
                AirForce.Vars.INSTANCE,
                Army.Vars.INSTANCE);
    }

    @Override public List<ThemeGlobals<?>> globals() {
        return List.of(
                Navy.Globals.INSTANCE,
                AirForce.Globals.INSTANCE,
                Army.Globals.INSTANCE);
    }
}
