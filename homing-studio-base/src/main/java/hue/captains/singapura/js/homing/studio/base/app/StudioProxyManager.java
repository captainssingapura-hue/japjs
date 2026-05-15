package hue.captains.singapura.js.homing.studio.base.app;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * Typed reverse-ref: source {@link L0_Catalogue} class → its umbrella
 * {@link Hosting} (the proxy + the catalogue hosting it). RFC 0011.
 *
 * <p>Built at boot by {@link #scan(Collection)} which walks every registered
 * catalogue's {@code leaves()} for {@link Entry.OfStudio} entries.
 * {@link CatalogueRegistry} consults it when computing breadcrumbs for any
 * page whose source L0 root is hosted — the umbrella's chain prepends
 * automatically.</p>
 *
 * <p>Each source L0 has at most one hosting. Duplicate registrations (the
 * same source L0 listed as an {@code OfStudio} leaf in two catalogues) are
 * rejected at scan time with a clear boot-time error.</p>
 */
public final class StudioProxyManager {

    /**
     * Typed pair: the proxy + the catalogue whose {@code leaves()} contains it.
     *
     * @param <S> the wrapped L0's type
     */
    public record Hosting<S extends L0_Catalogue<S>>(
            StudioProxy<S> proxy,
            Catalogue<?> host) {}

    /** Empty manager — for studios that never compose. */
    public static final StudioProxyManager EMPTY = new StudioProxyManager(Map.of());

    private final Map<Class<? extends L0_Catalogue<?>>, Hosting<?>> hostings;

    private StudioProxyManager(Map<Class<? extends L0_Catalogue<?>>, Hosting<?>> hostings) {
        this.hostings = hostings;
    }

    /**
     * Build the reverse-ref by scanning every registered catalogue's leaves.
     * Throws on duplicate hostings.
     */
    @SuppressWarnings({"rawtypes", "unchecked"})
    public static StudioProxyManager scan(Collection<? extends Catalogue<?>> catalogues) {
        Map<Class<? extends L0_Catalogue<?>>, Hosting<?>> map = new HashMap<>();
        for (Catalogue<?> host : catalogues) {
            for (Entry<?> e : host.leaves()) {
                if (e instanceof Entry.OfStudio(StudioProxy<?> proxy)) {
                    Class<? extends L0_Catalogue<?>> sourceClass =
                            (Class<? extends L0_Catalogue<?>>) proxy.source().getClass();
                    Hosting<?> hosting = new Hosting(proxy, host);
                    Hosting<?> prev = map.put(sourceClass, hosting);
                    if (prev != null) {
                        throw new IllegalStateException(
                                "Source L0 " + sourceClass.getName()
                              + " is wrapped by two catalogues: "
                              + prev.host().getClass().getName() + " and "
                              + host.getClass().getName()
                              + ". A source L0 may have at most one hosting (RFC 0011 §3.3).");
                    }
                }
            }
        }
        return new StudioProxyManager(Map.copyOf(map));
    }

    /**
     * Typed lookup. The {@code @SuppressWarnings} cast is safe by registration
     * invariant — entries are stored under {@code Class<S>} keys derived from
     * the proxy's own type witness, so retrieval under the same {@code Class<S>}
     * recovers the same {@code Hosting<S>}.
     */
    @SuppressWarnings("unchecked")
    public <S extends L0_Catalogue<S>> Hosting<S> hostingFor(Class<S> sourceClass) {
        return (Hosting<S>) hostings.get(sourceClass);
    }

    public boolean isHosted(Class<? extends L0_Catalogue<?>> sourceClass) {
        return hostings.containsKey(sourceClass);
    }

    /** Raw lookup — returns the host catalogue (no typed proxy). Used internally
     *  by {@link CatalogueRegistry} for breadcrumb augmentation, where the host's
     *  identity is what matters, not the typed proxy payload. */
    public Catalogue<?> hostFor(Class<? extends L0_Catalogue<?>> sourceClass) {
        Hosting<?> h = hostings.get(sourceClass);
        return h == null ? null : h.host();
    }

    public Collection<Hosting<?>> all() { return hostings.values(); }
    public int size() { return hostings.size(); }
}
