package hue.captains.singapura.japjs.core;

import java.util.List;

/**
 * @param allImports objects to be imported
 * @param from module to import from
 * @param <M>
 */
public record ModuleImports<M extends EsModule>(List<? extends Exportable<M>> allImports, M from) {}
