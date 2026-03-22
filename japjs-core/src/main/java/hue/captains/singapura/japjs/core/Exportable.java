package hue.captains.singapura.japjs.core;

/**
 * An export from a particular Module
 * @param <M>
 */
public interface Exportable<M extends EsModule>{

     interface _Class<M extends EsModule> extends Exportable<M>{}

     interface _Constant<M extends EsModule> extends Exportable<M>{}

}
