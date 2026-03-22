package hue.captains.singapura.japjs.core;

import java.util.List;

/**
 * Declare all exports of a given module
 * @param <M>
 */
public record ExportsOf<M extends EsModule>(M module, List<? extends Exportable<M>> exports) {
}
