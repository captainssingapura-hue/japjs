package hue.captains.singapura.js.homing.core;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.SequencedSet;

/**
 * Transitively resolves CSS dependencies for a list of {@link CssGroup}s.
 * Returns a flat list in dependency order (dependencies before dependents, no duplicates).
 */
public final class CssGroupResolver {

    private CssGroupResolver() {}

    public static List<CssGroup<?>> resolve(List<CssGroup<?>> roots) {
        SequencedSet<CssGroup<?>> resolved = new LinkedHashSet<>();
        for (CssGroup<?> root : roots) {
            walk(root, resolved);
        }
        return new ArrayList<>(resolved);
    }

    private static void walk(CssGroup<?> current, SequencedSet<CssGroup<?>> resolved) {
        if (resolved.contains(current)) {
            return;
        }
        for (CssGroup<?> dep : current.cssImports().imports()) {
            walk(dep, resolved);
        }
        resolved.add(current);
    }
}
