package hue.captains.singapura.japjs.core;

import java.util.List;

/**
 * An EsModule whose generated JavaScript interacts with the DOM.
 * <p>Only DomModules may declare CSS dependencies, since CSS
 * is only meaningful for modules that produce or manipulate
 * visible elements.</p>
 *
 * @param <M> self-type
 */
public interface DomModule<M extends DomModule<M>> extends EsModule<M> {

    /**
     * Derives CSS dependencies from this module's imports.
     * Any import source that is a {@link CssBeing} is treated as a CSS dependency.
     */
    @SuppressWarnings("unchecked")
    default List<CssBeing<?>> cssBeings() {
        return (List<CssBeing<?>>) (List<?>) imports().getAllImports().keySet().stream()
                .filter(m -> m instanceof CssBeing<?>)
                .toList();
    }
}
