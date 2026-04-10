package hue.captains.singapura.japjs.core;

import java.util.HashMap;
import java.util.Map;

/**
 * Declare all imports for a given EsModule
 * @param <M> the module those imports are for
 */
public final class ImportsFor<M extends EsModule>{

    private final Map<EsModule, ModuleImports<?>> allImports;

    private ImportsFor(Map<EsModule, ModuleImports<?>> allImports) {
        this.allImports = allImports;
    }

    public static <M extends EsModule> _Builder<M> builder(){
        return new _Builder<>();
    }

    public static <M extends EsModule> ImportsFor<M> noImports(){
        return new ImportsFor<>(Map.of());
    }

    public Map<EsModule, ModuleImports<?>> getAllImports() {
        return allImports;
    }

    /**
     * Builder utility to ensure the map is constructed properly.
     * @param <M>
     */
    public static final class _Builder<M extends EsModule> {
        private final HashMap<EsModule, ModuleImports<?>> tempMap = new HashMap<>();

        public <M2 extends EsModule> _Builder<M> add(ModuleImports<M2> importsFromThisModule){
            this.tempMap.put(importsFromThisModule.from(), importsFromThisModule);
            return this;
        }

        public ImportsFor<M> build(){
            return new ImportsFor<>(Map.copyOf(tempMap));
        }

    }
}
