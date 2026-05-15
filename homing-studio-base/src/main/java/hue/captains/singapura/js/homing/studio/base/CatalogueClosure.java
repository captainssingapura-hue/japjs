package hue.captains.singapura.js.homing.studio.base;

import hue.captains.singapura.js.homing.studio.base.app.Catalogue;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * RFC 0012 — stateless functional object that walks the closure of a
 * catalogue tree. Used as the default implementation of
 * {@link Studio#catalogues()}.
 *
 * <p>Per the Functional Objects doctrine, this is reached via the singleton
 * {@link #INSTANCE} handle; the framework has no public static methods.</p>
 *
 * <pre>{@code
 * List<Catalogue<?>> all = CatalogueClosure.INSTANCE.walk(StudioCatalogue.INSTANCE);
 * }</pre>
 */
public record CatalogueClosure() {

    public static final CatalogueClosure INSTANCE = new CatalogueClosure();

    /**
     * BFS walk from a root catalogue, visiting every reachable sub-catalogue
     * via {@link Catalogue#subCatalogues()}. Returns the closure with the
     * root first. Dedup by class identity — two instances of the same
     * catalogue class collapse to one.
     */
    public List<Catalogue<?>> walk(Catalogue<?> root) {
        List<Catalogue<?>> out = new ArrayList<>();
        Set<Class<?>> seen = new HashSet<>();
        Deque<Catalogue<?>> q = new ArrayDeque<>();
        q.add(root);
        while (!q.isEmpty()) {
            Catalogue<?> c = q.poll();
            if (!seen.add(c.getClass())) continue;
            out.add(c);
            for (Catalogue<?> sub : c.subCatalogues()) {
                q.add(sub);
            }
        }
        return List.copyOf(out);
    }
}
