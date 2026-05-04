package hue.captains.singapura.js.homing.core;

/**
 * An EsModule that serves as the entry point of a single-page application.
 * <p>By convention, an AppModule exports a function named {@code appMain}
 * that accepts a root DOM element. The generated HTML scaffold imports
 * and invokes this function automatically.</p>
 *
 * <p>As of RFC 0001, AppModule is also a {@link Linkable} — every AppModule
 * has a {@link #simpleName()} (defaulting to a kebab-case derivation of its
 * class name) usable as the {@code ?app=} URL identifier, and may declare
 * a typed {@link #paramsType()} for query parameters.</p>
 *
 * @param <M> self-type
 */
public non-sealed interface AppModule<M extends AppModule<M>> extends DomModule<M>, Linkable {

    /**
     * The title for the generated HTML page.
     */
    String title();

    /**
     * Public URL identifier for this app. Defaults to a kebab-case
     * derivation of the simple class name (e.g., {@code PitchDeck} →
     * {@code "pitch-deck"}). Override to lock the URL contract
     * independently of the Java class name.
     *
     * <p>Introduced as part of RFC 0001 Step 02. Step 01 ships the
     * default; Step 02 will broaden its usage across the kernel.</p>
     */
    @Override
    default String simpleName() {
        return Linkable.defaultSimpleName(this.getClass());
    }

    /**
     * Java record describing this app's typed query parameters, or
     * {@code Void.class} for parameter-less apps (the default).
     *
     * <p>Step 02 of RFC 0001 will broaden this; Step 06 wires it through
     * the writer to generate a {@code params} const in the compiled JS.</p>
     */
    @Override
    default Class<?> paramsType() {
        return Void.class;
    }

    /**
     * Marker for the {@code appMain} export.
     * Use the record name {@code appMain} so the JS identifier matches.
     */
    interface _AppMain<M extends AppModule<M>> extends Exportable._Constant<M> {}
}
