package hue.captains.singapura.japjs.core;

import java.util.HashMap;
import java.util.Map;

/**
 * Declare all imports for a given EsModule.
 *
 * <p>The map is keyed on {@link Importable} (RFC 0001 Step 04) so that
 * both EsModule sources (emit JS imports) and {@link Linkable} sources
 * (specifically {@link ProxyApp}, emit nav entries only) coexist in
 * one container.</p>
 *
 * @param <M> the importing module — always an EsModule
 */
public final class ImportsFor<M extends EsModule>{

    private final Map<Importable, ModuleImports<?>> allImports;

    private ImportsFor(Map<Importable, ModuleImports<?>> allImports) {
        this.allImports = allImports;
    }

    public static <M extends EsModule> _Builder<M> builder(){
        return new _Builder<>();
    }

    public static <M extends EsModule> ImportsFor<M> noImports(){
        return new ImportsFor<>(Map.of());
    }

    public Map<Importable, ModuleImports<?>> getAllImports() {
        return allImports;
    }

    /**
     * Builder utility to ensure the map is constructed properly.
     * @param <M> the importing module
     */
    public static final class _Builder<M extends EsModule> {
        private final HashMap<Importable, ModuleImports<?>> tempMap = new HashMap<>();

        public <M2 extends Importable> _Builder<M> add(ModuleImports<M2> importsFromThisModule){
            this.tempMap.put(importsFromThisModule.from(), importsFromThisModule);
            return this;
        }

        public ImportsFor<M> build(){
            return new ImportsFor<>(Map.copyOf(tempMap));
        }

    }
}
