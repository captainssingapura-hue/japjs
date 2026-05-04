package hue.captains.singapura.js.homing.core;

import java.util.List;

/**
 * Imports from a single source.
 *
 * <p>The source is typed as {@link Importable} — typically an
 * {@link EsModule} (the historical case, emits a JS {@code import} statement)
 * or a {@link Linkable} (specifically {@link ProxyApp}, contributes a
 * {@code nav} entry but no JS import). The type parameter is bounded
 * to {@code Importable} as of RFC 0001 Step 04.</p>
 *
 * @param allImports objects to be imported from {@code from}
 * @param from       the source — an EsModule or a Linkable
 * @param <M>        the source type
 */
public record ModuleImports<M extends Importable>(List<? extends Exportable<M>> allImports, M from) {}
