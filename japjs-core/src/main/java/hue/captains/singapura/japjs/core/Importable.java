package hue.captains.singapura.japjs.core;

/**
 * Common supertype for things that can be the SOURCE of an import in
 * {@link ImportsFor}. Permits {@link EsModule} (the historical case — an
 * import that emits a JS {@code import} statement) and {@link Linkable}
 * (a navigation target — an {@link AppLink} import that emits a {@code nav}
 * entry but not an {@code import} statement).
 *
 * <p>Introduced in RFC 0001 Step 04 to let the import graph carry both
 * kinds without forcing {@link ProxyApp} to pretend to be an {@link EsModule}.</p>
 */
public sealed interface Importable permits EsModule, Linkable {
}
