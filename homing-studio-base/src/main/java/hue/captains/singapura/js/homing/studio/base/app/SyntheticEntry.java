package hue.captains.singapura.js.homing.studio.base.app;

import hue.captains.singapura.tao.ontology.ValueObject;

/**
 * A pre-rendered entry tile to be appended to a {@link Catalogue}'s
 * serialized JSON output, bypassing the typed {@link Entry} machinery.
 *
 * <p>Use case: the framework needs to surface a tile inside a downstream
 * studio's catalogue (e.g. an opt-in diagnostics tile injected into the
 * brand's home L0) without forcing the downstream to declare it in their
 * own {@code leaves()}. Synthetic entries are added by
 * {@link CatalogueGetAction} after iterating {@code subCatalogues()} and
 * {@code leaves()}, so they appear at the end of the tile grid.</h>
 *
 * <p>Synthetic entries are <em>not</em> validated by {@link CatalogueRegistry}
 * — their URL is opaque to the registry, the destination may or may not
 * be a registered catalogue, and the rendering layer is the only check.
 * Reserved for framework-managed augmentation (RFC 0014 diagnostics, etc.);
 * downstream studios should still go through {@link Entry} for their own
 * leaves to keep the typed validation guarantees.</p>
 *
 * <p>Shape mirrors the JSON emitted by {@link CatalogueGetAction#serialize}
 * for the four built-in {@link Entry} variants, so the frontend renderer
 * doesn't need to learn a new {@code kind}.</p>
 *
 * @param kind     one of {@code "catalogue"}, {@code "doc"}, {@code "app"},
 *                 {@code "plan"}, {@code "studio"} — must match a kind the
 *                 frontend tile renderer already understands
 * @param name     tile heading
 * @param summary  tile body text; empty string OK
 * @param category short uppercase badge text (e.g. {@code "DIAGNOSTICS"})
 * @param url      destination URL — typically {@code /app?app=...} produced
 *                 by the framework
 */
public record SyntheticEntry(
        String kind,
        String name,
        String summary,
        String category,
        String url
) implements ValueObject {}
