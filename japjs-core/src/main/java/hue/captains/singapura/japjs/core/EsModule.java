package hue.captains.singapura.japjs.core;

/**
 * A "Declaration" only of an ES/JS module
 */
public interface EsModule<M extends EsModule<M>> {
    ImportsFor<M> imports();
    ExportsOf<M> exports();
}
