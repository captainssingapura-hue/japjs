package hue.captains.singapura.japjs.core;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.SequencedSet;

/**
 * Transitively resolves CSS dependencies for a list of {@link CssBeing}s.
 * Returns a flat list in dependency order (dependencies before dependents, no duplicates).
 */
public final class CssBeingResolver {

    private CssBeingResolver() {}

    public static List<CssBeing<?>> resolve(List<CssBeing<?>> roots) {
        SequencedSet<CssBeing<?>> resolved = new LinkedHashSet<>();
        for (CssBeing<?> root : roots) {
            walk(root, resolved);
        }
        return new ArrayList<>(resolved);
    }

    private static void walk(CssBeing<?> current, SequencedSet<CssBeing<?>> resolved) {
        if (resolved.contains(current)) {
            return;
        }
        for (CssBeing<?> dep : current.cssImports().imports()) {
            walk(dep, resolved);
        }
        resolved.add(current);
    }
}
