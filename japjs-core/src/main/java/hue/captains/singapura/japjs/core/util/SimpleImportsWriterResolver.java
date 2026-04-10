package hue.captains.singapura.japjs.core.util;

import hue.captains.singapura.japjs.core.EsModule;
import hue.captains.singapura.japjs.core.ImportsWriterResolver;
import hue.captains.singapura.japjs.core.ModuleNameResolver;
import hue.captains.singapura.japjs.core.SingleModuleImportWriter;

public record SimpleImportsWriterResolver(
        ModuleNameResolver nameResolver, String theme, String locale
) implements ImportsWriterResolver {

    public SimpleImportsWriterResolver(ModuleNameResolver nameResolver) {
        this(nameResolver, null, null);
    }

    @Override
    public <M extends EsModule<M>> SingleModuleImportWriter<M> resolve(M fromModule) {
        return new SingleModuleImportWriter<>(fromModule, nameResolver, theme, locale);
    }
}
