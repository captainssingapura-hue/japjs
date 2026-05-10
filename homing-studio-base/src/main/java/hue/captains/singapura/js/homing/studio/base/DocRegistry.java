package hue.captains.singapura.js.homing.studio.base;

import hue.captains.singapura.js.homing.core.AppModule;
import hue.captains.singapura.js.homing.core.SimpleAppResolver;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Boot-time UUID-indexed registry of {@link Doc}s reachable from a set of
 * {@link DocProvider}s. Mirrors {@link SimpleAppResolver}'s role for {@link AppModule}s.
 *
 * <p>Constructed once at studio startup; immutable afterwards. UUID collisions throw
 * {@link IllegalStateException} at construction time. Path / extension validation
 * also runs at construction so any developer mistake surfaces at boot rather than at
 * the first request.</p>
 *
 * <p>Per <a href="../../../../../../../../../../docs/rfcs/0004-typed-docs-and-doc-visibility.md">
 * RFC 0004</a>, this is the single source of truth the {@link DocGetAction} consults to
 * resolve {@code /doc?id=<uuid>} requests.</p>
 *
 * @since RFC 0004
 */
public final class DocRegistry {

    private final Map<UUID, Doc> byUuid;

    /**
     * Build a registry from an explicit collection of docs. Validates uniqueness of UUIDs.
     *
     * @throws IllegalStateException on UUID collision or null/blank UUID
     */
    public DocRegistry(Collection<? extends Doc> docs) {
        var byUuid = new LinkedHashMap<UUID, Doc>();
        for (Doc d : docs) {
            UUID id = d.uuid();
            if (id == null) {
                throw new IllegalStateException(
                        "Doc " + d.getClass().getName() + " has null uuid()");
            }
            Doc prev = byUuid.put(id, d);
            if (prev != null && prev != d) {
                throw new IllegalStateException(
                        "Doc UUID collision: " + id + " is used by both "
                      + prev.getClass().getName() + " and " + d.getClass().getName());
            }
        }
        this.byUuid = Map.copyOf(byUuid);
    }

    /**
     * Build a registry by walking a {@link SimpleAppResolver}'s app closure for
     * {@link DocProvider} implementors and unioning every contributor's {@link DocProvider#docs()}.
     */
    public static DocRegistry from(SimpleAppResolver appResolver) {
        List<Doc> all = new ArrayList<>();
        for (AppModule<?, ?> app : appResolver.apps()) {
            if (app instanceof DocProvider provider) {
                all.addAll(provider.docs());
            }
        }
        return new DocRegistry(all);
    }

    /** Resolve a Doc by UUID, or null if no Doc with that UUID is registered. */
    public Doc resolve(UUID id) {
        return byUuid.get(id);
    }

    /** All Docs in registration order. */
    public Collection<Doc> all() {
        return Collections.unmodifiableCollection(byUuid.values());
    }

    /** Number of registered Docs. */
    public int size() {
        return byUuid.size();
    }
}
