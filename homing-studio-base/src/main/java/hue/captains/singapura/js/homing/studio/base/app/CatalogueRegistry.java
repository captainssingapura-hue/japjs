package hue.captains.singapura.js.homing.studio.base.app;

import hue.captains.singapura.js.homing.studio.base.Doc;
import hue.captains.singapura.js.homing.studio.base.DocRegistry;
import hue.captains.singapura.js.homing.studio.base.tracker.Plan;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

/**
 * Boot-time registry of {@link Catalogue}s with breadcrumb derivation via
 * typed parent() calls (RFC 0005-ext2).
 *
 * <p>Per RFC 0005-ext2, the catalogue tree's shape is now encoded in the
 * type system: each catalogue extends exactly one of L0..L8, and non-root
 * levels declare their parent's type as a generic parameter with a
 * matching {@code parent()} accessor. The registry no longer needs to
 * infer (catalogue → parent) at boot — it reads {@code parent()} directly.</p>
 *
 * <p>Boot-time validations remaining:</p>
 *
 * <ol>
 *   <li><b>Entry depth + parent-match</b> — every {@link Entry.OfCatalogue}
 *       child must declare its {@code parent()} as the containing catalogue.
 *       Mismatch throws (covers "wrong-level entry" and "stale parent
 *       reference" cases).</li>
 *   <li><b>Closure completeness</b> — every {@code Entry.OfCatalogue}
 *       references a catalogue in the registered list.</li>
 *   <li><b>Doc reachability</b> — every {@code Entry.OfDoc} references a
 *       doc in the supplied {@link DocRegistry}.</li>
 *   <li><b>Brand home-app registered</b> — {@code brand.homeApp()} is an L0
 *       catalogue in the list.</li>
 * </ol>
 *
 * <p>Cycle detection is no longer needed — the type system makes cycles
 * impossible to express. Each LN's parent() returns LN-1; the chain
 * strictly descends.</p>
 *
 * <p>Multi-parent is also impossible to express — a class can only declare
 * one parent type at compile time.</p>
 *
 * @since RFC 0005 (refactored in RFC 0005-ext2)
 */
public final class CatalogueRegistry {

    private final StudioBrand brand;
    private final Map<Class<? extends Catalogue>, Catalogue> byClass;
    /** Reverse index: doc UUID → first catalogue containing the doc.
     *  Built at construction; used for breadcrumb derivation. */
    private final Map<UUID, Catalogue> docHome;
    /** Reverse index: plan class → first catalogue containing the plan. */
    private final Map<Class<? extends Plan>, Catalogue> planHome;

    public CatalogueRegistry(StudioBrand brand,
                             DocRegistry docRegistry,
                             Collection<? extends Catalogue> catalogues) {
        this.brand = Objects.requireNonNull(brand, "brand");
        Objects.requireNonNull(docRegistry, "docRegistry");
        Objects.requireNonNull(catalogues,  "catalogues");

        // Build the class → catalogue lookup.
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

        // Validate the brand's home-app references a registered L0 catalogue.
        if (!byClass.containsKey(brand.homeApp())) {
            throw new IllegalStateException(
                    "StudioBrand.homeApp references " + brand.homeApp().getName()
                  + " which is not in the registered catalogue list");
        }
        Catalogue homeCatalogue = byClass.get(brand.homeApp());
        if (!(homeCatalogue instanceof L0_Catalogue)) {
            throw new IllegalStateException(
                    "StudioBrand.homeApp " + brand.homeApp().getName()
                  + " must be an L0_Catalogue (the studio's root). Got "
                  + homeCatalogue.getClass().getName() + " which is at level "
                  + levelOf(homeCatalogue));
        }

        // Validate sub-catalogues + leaves and build reverse indices in one
        // pass. RFC 0005-ext2: the typed parent() relation makes cycles and
        // multi-parent impossible at the type level; subCatalogues() narrows
        // the return type to L<N+1>, so the depth check is also compile-time.
        // The remaining runtime checks are: closure (every sub-catalogue is
        // in the registered list), parent-match (child.parent() == this
        // catalogue), doc/plan reachability, and the L8 terminal invariant.
        var docHomeMap  = new HashMap<UUID, Catalogue>();
        var planHomeMap = new HashMap<Class<? extends Plan>, Catalogue>();
        for (Catalogue parent : byClass.values()) {
            // ---- Sub-catalogue children ----
            List<? extends Catalogue> subs = parent.subCatalogues();
            if (subs == null) {
                throw new IllegalStateException(
                        "Catalogue " + parent.getClass().getName()
                      + " has null subCatalogues()");
            }
            if (parent instanceof L8_Catalogue<?> && !subs.isEmpty()) {
                throw new IllegalStateException(
                        "Catalogue " + parent.getClass().getName()
                      + " is an L8_Catalogue (terminal level) but declares "
                      + subs.size() + " sub-catalogue(s). L8 catalogues cannot"
                      + " nest further — there is no L9 type. Move children to leaves()"
                      + " or refactor the tree shallower.");
            }
            for (Catalogue child : subs) {
                if (child == null) {
                    throw new IllegalStateException(
                            "Catalogue " + parent.getClass().getName()
                          + " has a null entry in subCatalogues()");
                }
                if (!byClass.containsKey(child.getClass())) {
                    throw new IllegalStateException(
                            "Catalogue " + parent.getClass().getName()
                          + " references sub-catalogue " + child.getClass().getName()
                          + " which is not in the registered catalogue list");
                }
                // RFC 0005-ext2: the child's typed parent() must point at this
                // containing catalogue's INSTANCE. The type bound on
                // subCatalogues() already constrains the parent's *type*; this
                // runtime check covers the instance-equality case (singleton
                // convention — handles authoring slip where a non-INSTANCE
                // parent is returned).
                Catalogue declaredParent = declaredParentOf(child);
                if (declaredParent != parent) {
                    throw new IllegalStateException(
                            "Catalogue " + parent.getClass().getName()
                          + " contains " + child.getClass().getName()
                          + " in subCatalogues(), but the latter's parent() returns "
                          + (declaredParent == null ? "null" : declaredParent.getClass().getName())
                          + ". An L<N+1> child's parent() must return the containing"
                          + " catalogue's INSTANCE (RFC 0005-ext2 typed-level invariant).");
                }
            }

            // ---- Leaves ----
            List<Entry> leaves = parent.leaves();
            if (leaves == null) {
                throw new IllegalStateException(
                        "Catalogue " + parent.getClass().getName()
                      + " has null leaves()");
            }
            for (Entry e : leaves) {
                if (e == null) {
                    throw new IllegalStateException(
                            "Catalogue " + parent.getClass().getName()
                          + " has a null Entry in leaves()");
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
                        // First catalogue containing this doc wins for breadcrumbs.
                        docHomeMap.putIfAbsent(id, parent);
                    }
                    case Entry.OfApp(Navigable<?, ?> nav) -> {
                        if (nav == null) {
                            throw new IllegalStateException(
                                    "Catalogue " + parent.getClass().getName()
                                  + " has Entry.OfApp with null Navigable");
                        }
                    }
                    case Entry.OfPlan(Plan plan) -> {
                        if (plan == null) {
                            throw new IllegalStateException(
                                    "Catalogue " + parent.getClass().getName()
                                  + " has Entry.OfPlan with null plan");
                        }
                        planHomeMap.putIfAbsent(plan.getClass(), parent);
                    }
                }
            }
        }

        this.byClass  = Map.copyOf(byClass);
        this.docHome  = Map.copyOf(docHomeMap);
        this.planHome = Map.copyOf(planHomeMap);
    }

    private static void requireValid(Catalogue c) {
        Objects.requireNonNull(c, "catalogue must not be null");
        if (c.name() == null || c.name().isBlank()) {
            throw new IllegalStateException(
                    "Catalogue " + c.getClass().getName() + " has null/blank name()");
        }
        // subCatalogues() / leaves() null-checks happen in the validation pass.
    }

    /**
     * The declared parent of a catalogue — extracted from the typed
     * {@code parent()} method via a sealed-exhaustive switch. Returns
     * null for L0 catalogues (which have no parent).
     */
    private static Catalogue declaredParentOf(Catalogue c) {
        return switch (c) {
            case L0_Catalogue l0 -> null;
            case L1_Catalogue<?> l1 -> l1.parent();
            case L2_Catalogue<?> l2 -> l2.parent();
            case L3_Catalogue<?> l3 -> l3.parent();
            case L4_Catalogue<?> l4 -> l4.parent();
            case L5_Catalogue<?> l5 -> l5.parent();
            case L6_Catalogue<?> l6 -> l6.parent();
            case L7_Catalogue<?> l7 -> l7.parent();
            case L8_Catalogue<?> l8 -> l8.parent();
        };
    }

    /** The numeric level (0..8) of a catalogue, derived from its sealed type. */
    public static int levelOf(Catalogue c) {
        return switch (c) {
            case L0_Catalogue l0 -> 0;
            case L1_Catalogue<?> l1 -> 1;
            case L2_Catalogue<?> l2 -> 2;
            case L3_Catalogue<?> l3 -> 3;
            case L4_Catalogue<?> l4 -> 4;
            case L5_Catalogue<?> l5 -> 5;
            case L6_Catalogue<?> l6 -> 6;
            case L7_Catalogue<?> l7 -> 7;
            case L8_Catalogue<?> l8 -> 8;
        };
    }

    // -----------------------------------------------------------------------
    // Lookups
    // -----------------------------------------------------------------------

    public StudioBrand brand() { return brand; }

    public Catalogue resolve(Class<? extends Catalogue> cls) {
        return byClass.get(cls);
    }

    /** Parent of the given catalogue, or null if it's an L0 root (or not registered). */
    public Catalogue parentOf(Class<? extends Catalogue> cls) {
        Catalogue at = byClass.get(cls);
        return at == null ? null : declaredParentOf(at);
    }

    /**
     * Breadcrumb chain from the root (L0) down to the given catalogue, inclusive.
     * The first element is the root; the last is the supplied catalogue. Returns
     * empty if the catalogue isn't registered.
     *
     * <p>RFC 0005-ext2: derived by walking {@code parent()} via the sealed-switch
     * recursion, not by map lookup.</p>
     */
    public List<Catalogue> breadcrumbs(Class<? extends Catalogue> cls) {
        Catalogue at = byClass.get(cls);
        if (at == null) return List.of();
        return breadcrumbs(at);
    }

    /** Same as {@link #breadcrumbs(Class)} but takes the instance directly. */
    public List<Catalogue> breadcrumbs(Catalogue at) {
        List<Catalogue> chain = new ArrayList<>();
        Catalogue cursor = at;
        while (cursor != null) {
            chain.add(cursor);
            cursor = declaredParentOf(cursor);
        }
        Collections.reverse(chain);
        return List.copyOf(chain);
    }

    /**
     * Breadcrumb chain for a {@link Doc} by its UUID — root → containing
     * catalogue → (caller appends the doc's title separately). Returns the
     * empty list if the doc isn't referenced by any registered catalogue
     * (such docs are typically reachable only via {@code DocBrowser}, where
     * the breadcrumb falls back to the DocBrowser Navigable's catalogue path).
     */
    public List<Catalogue> breadcrumbsForDoc(UUID docId) {
        Catalogue home = docHome.get(docId);
        return home == null ? List.of() : breadcrumbs(home);
    }

    /**
     * Breadcrumb chain for a {@link Plan} by its class — root → containing
     * catalogue (typically Journeys).
     */
    public List<Catalogue> breadcrumbsForPlan(Class<? extends Plan> cls) {
        Catalogue home = planHome.get(cls);
        return home == null ? List.of() : breadcrumbs(home);
    }

    public Collection<Catalogue> all() {
        return Collections.unmodifiableCollection(byClass.values());
    }

    public int size() {
        return byClass.size();
    }
}
