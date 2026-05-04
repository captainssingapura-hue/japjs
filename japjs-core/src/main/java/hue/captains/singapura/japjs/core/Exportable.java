package hue.captains.singapura.japjs.core;

/**
 * An export from a particular Module or other Linkable.
 *
 * <p>The type parameter {@code M} identifies the source — typically an
 * {@link EsModule}, but as of RFC 0001 this bound has been loosened to also
 * permit {@link Linkable} sources (specifically {@link ProxyApp}s, which
 * are not EsModules but do produce navigable typed handles).</p>
 *
 * @param <M> the source — an EsModule, a Linkable, or any future
 *            export-source type
 */
public interface Exportable<M> {

    interface _Class<M extends EsModule> extends Exportable<M> {}

    interface _Constant<M extends EsModule> extends Exportable<M> {}

}
