package hue.captains.singapura.japjs.core;

/**
 * An EsModule that serves as the entry point of a single-page application.
 * <p>By convention, an AppModule exports a function named {@code appMain}
 * that accepts a root DOM element. The generated HTML scaffold imports
 * and invokes this function automatically.</p>
 *
 * @param <M> self-type
 */
public interface AppModule<M extends AppModule<M>> extends DomModule<M> {

    /**
     * The title for the generated HTML page.
     */
    String title();

    /**
     * Marker for the {@code appMain} export.
     * Use the record name {@code appMain} so the JS identifier matches.
     */
    interface _AppMain<M extends AppModule<M>> extends Exportable._Constant<M> {}
}
