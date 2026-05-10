package hue.captains.singapura.js.homing.studio.base.app;

import hue.captains.singapura.js.homing.studio.base.Doc;
import hue.captains.singapura.js.homing.studio.base.DocRegistry;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

/**
 * Boot-time registry of {@link Catalogue}s and the parent index used for breadcrumb
 * derivation. Constructed once at studio startup; immutable afterwards.
 *
 * <p>Per <a href="../../../../../../../../../../docs/rfcs/Rfc0005Doc.md">RFC 0005</a>
 * §6.1, the constructor performs four boot-time validations against the explicit
 * catalogue list — fail-fast at boot, no runtime surprises:</p>
 *
 * <ol>
 *   <li><b>Strict tree</b> — each catalogue appears as an entry in at most one parent.
 *       Multi-parent throws {@link IllegalStateException} (the v1 implementation enforces
 *       strict-tree for breadcrumb determinism; the doctrine permits multi-parent in
 *       principle but a future RFC would extend the URL contract to support it).</li>
 *   <li><b>No cycles</b> — walking {@link Catalogue#entries()} from each catalogue terminates.
 *       Cycles throw.</li>
 *   <li><b>Closure completeness</b> — every {@link Entry.OfCatalogue} references a catalogue
 *       that's in the registered list. An entry pointing at an unregistered catalogue
 *       throws.</li>
 *   <li><b>Doc reachability</b> — every {@link Entry.OfDoc} references a doc that's in the
 *       supplied {@link DocRegistry}. Catalogues can't link to off-classpath / private docs.</li>
 * </ol>
 *
 * <p>{@link Entry.OfApp} entries are not validated by this registry —
 * referenced {@link hue.captains.singapura.js.homing.core.AppModule}s are
 * registered independently through {@link
 * hue.captains.singapura.js.homing.core.SimpleAppResolver}; their reachability
 * is checked there.</p>
 *
 * @since RFC 0005
 */
public final class CatalogueRegistry {

    private final StudioBrand brand;
    private final Map<Class<? extends Catalogue>, Catalogue> byClass;
    private final Map<Class<? extends Catalogue>, Catalogue> parentByChild;

    /**
     * Build a registry from the studio brand, the doc registry, and an explicit list of
     * catalogues. Performs all four §6.1 validations at construction.
     *
     * @throws IllegalStateException on any validation failure (multi-parent, cycle,
     *                               unregistered sub-catalogue, unregistered doc, blank
     *                               name, null entries)
     */
    public CatalogueRegistry(StudioBrand brand,
                             DocRegistry docRegistry,
                             Collection<? extends Catalogue> catalogues) {
        this.brand = Objects.requireNonNull(brand, "brand");
        Objects.requireNonNull(docRegistry, "docRegistry");
        Objects.requireNonNull(catalogues,  "catalogues");

        // Build the class → catalogue lookup. Each catalogue class registered at most once.
        var byClass = new LinkedHashMap<Class<? extends Catalogue>, Catalogue>();
        for (Catalogue c : catalogues) {
            requireValid(c);
            Class<? extends Catalogue> cls = c.getClass();
            Catalogue prev = byClass.put(cls, c);
            if (prev != null && prev != c) {
                throw new IllegalStateException(
                        "Catalogue class registered twice with different instances: " + cls.getName());
            }
        }

        // Validate the brand's home-app references a registered catalogue.
        if (!byClass.containsKey(brand.homeApp())) {
            throw new IllegalStateException(
                    "StudioBrand.homeApp references " + brand.homeApp().getName()
                  + " which is not in the registered catalogue list");
        }

        // Validate entries + build the parent index in one pass. Strict tree: each
        // catalogue can be a child of at most one parent.
        var parentByChild = new LinkedHashMap<Class<? extends Catalogue>, Catalogue>();
        for (Catalogue parent : byClass.values()) {
            for (Entry e : parent.entries()) {
                if (e == null) {
                    throw new IllegalStateException(
                            "Catalogue " + parent.getClass().getName()
                          + " has a null Entry in entries()");
                }
                switch (e) {
                    case Entry.OfDoc(Doc d) -> {
                        if (d == null) {
                            throw new IllegalStateException(
                                    "Catalogue " + parent.getClass().getName()
                                  + " has Entry.OfDoc with null doc");
                        }
                        UUID id = d.uuid();
                        if (id == null || docRegistry.resolve(id) == null) {
                            throw new IllegalStateException(
                                    "Catalogue " + parent.getClass().getName()
                                  + " references Doc " + d.getClass().getName()
                                  + " (uuid=" + id + ") which is not in the DocRegistry");
                        }
                    }
                    case Entry.OfCatalogue(Catalogue child) -> {
                        if (child == null) {
                            throw new IllegalStateException(
                                    "Catalogue " + parent.getClass().getName()
                                  + " has Entry.OfCatalogue with null catalogue");
                        }
                        if (!byClass.containsKey(child.getClass())) {
                            throw new IllegalStateException(
                                    "Catalogue " + parent.getClass().getName()
                                  + " references sub-catalogue " + child.getClass().getName()
                                  + " which is not in the registered catalogue list");
                        }
                        Catalogue priorParent = parentByChild.put(child.getClass(), parent);
                        if (priorParent != null && priorParent.getClass() != parent.getClass()) {
                            throw new IllegalStateException(
                                    "Catalogue " + child.getClass().getName()
                                  + " has multiple parents: " + priorParent.getClass().getName()
                                  + " and " + parent.getClass().getName()
                                  + " (RFC 0005 v1 enforces strict-tree)");
                        }
                    }
                    case Entry.OfApp(Navigable<?, ?> nav) -> {
                        if (nav == null) {
                            throw new IllegalStateException(
                                    "Catalogue " + parent.getClass().getName()
                                  + " has Entry.OfApp with null Navigable");
                        }
                        // Navigable's compact constructor already enforces app/params/name
                        // non-null; AppModule reachability is enforced via SimpleAppResolver
                        // in the framework's app registry — not validated here.
                    }
                    case Entry.OfPlan(hue.captains.singapura.js.homing.studio.base.tracker.Plan plan) -> {
                        if (plan == null) {
                            throw new IllegalStateException(
                                    "Catalogue " + parent.getClass().getName()
                                  + " has Entry.OfPlan with null plan");
                        }
                        // Plan reachability is enforced via PlanRegistry (RFC 0005-ext1)
                        // — not validated here.
                    }
                }
            }
        }

        // Cycle detection — DFS from each catalogue.
        for (Catalogue root : byClass.values()) {
            detectCycles(root, byClass, parentByChild, new HashSet<>());
        }

        this.byClass       = Map.copyOf(byClass);
        this.parentByChild = Map.copyOf(parentByChild);
    }

    private static void requireValid(Catalogue c) {
        Objects.requireNonNull(c, "catalogue must not be null");
        if (c.name() == null || c.name().isBlank()) {
            throw new IllegalStateException(
                    "Catalogue " + c.getClass().getName() + " has null/blank name()");
        }
        if (c.entries() == null) {
            throw new IllegalStateException(
                    "Catalogue " + c.getClass().getName() + " has null entries()");
        }
    }

    private static void detectCycles(Catalogue at,
                                     Map<Class<? extends Catalogue>, Catalogue> byClass,
                                     Map<Class<? extends Catalogue>, Catalogue> parentByChild,
                                     Set<Class<? extends Catalogue>> visiting) {
        Class<? extends Catalogue> cls = at.getClass();
        if (!visiting.add(cls)) {
            throw new IllegalStateException("Cycle detected in catalogue tree at " + cls.getName());
        }
        for (Entry e : at.entries()) {
            if (e instanceof Entry.OfCatalogue(Catalogue child)) {
                detectCycles(child, byClass, parentByChild, visiting);
            }
        }
        visiting.remove(cls);
    }

    // -----------------------------------------------------------------------
    // Lookups
    // -----------------------------------------------------------------------

    /** Brand configuration provided at construction. */
    public StudioBrand brand() {
        return brand;
    }

    /** Resolve a Catalogue by its implementing class, or null if not registered. */
    public Catalogue resolve(Class<? extends Catalogue> cls) {
        return byClass.get(cls);
    }

    /**
     * Parent of the given catalogue, or null if the catalogue is a tree root (or not
     * registered).
     */
    public Catalogue parentOf(Class<? extends Catalogue> cls) {
        return parentByChild.get(cls);
    }

    /**
     * Breadcrumb chain from the brand's home-app down to the given catalogue (inclusive).
     * Returns a list whose first element is the home-app catalogue and whose last element
     * is the supplied catalogue. Returns an empty list if the catalogue isn't registered.
     */
    public List<Catalogue> breadcrumbs(Class<? extends Catalogue> cls) {
        Catalogue at = byClass.get(cls);
        if (at == null) return List.of();
        List<Catalogue> chain = new ArrayList<>();
        chain.add(at);
        Class<? extends Catalogue> cursor = cls;
        while (true) {
            Catalogue parent = parentByChild.get(cursor);
            if (parent == null) break;
            chain.add(parent);
            cursor = parent.getClass();
        }
        Collections.reverse(chain);
        return List.copyOf(chain);
    }

    /** All registered catalogues in registration order. */
    public Collection<Catalogue> all() {
        return Collections.unmodifiableCollection(byClass.values());
    }

    /** Number of registered catalogues. */
    public int size() {
        return byClass.size();
    }
}
