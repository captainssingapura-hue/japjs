package hue.captains.singapura.japjs.core.util;

import hue.captains.singapura.japjs.core.EsModule;
import hue.captains.singapura.japjs.core.ModuleNameResolver;
import hue.captains.singapura.japjs.core.PartialModulePath;

public record SimplePrefixResolver(String prefix) implements ModuleNameResolver {
    @Override
    public PartialModulePath resolve(EsModule<?> module) {
        String path = prefix + module.getClass().getCanonicalName().replace(".", "/") + ".js";
        return new PartialModulePath(path, false);
    }
}
