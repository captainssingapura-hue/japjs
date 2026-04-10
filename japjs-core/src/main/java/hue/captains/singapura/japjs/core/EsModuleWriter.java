package hue.captains.singapura.japjs.core;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Stream;

public record EsModuleWriter<M extends EsModule<M>>(M module, ContentProvider<M> contentProvider, ModuleNameResolver nameResolver, ExportWriter exportWriter, ImportsWriterResolver importsWriterResolver) {
    public List<String> writeModule(){
        return Stream.of(
                module.imports().getAllImports().entrySet().stream()
                        .map(e->importsWriterResolver.resolve(e.getKey()).writeImports(e.getValue())),
                contentProvider.content().stream(),
                exportWriter.writeExports(module.exports()).stream()
        ).flatMap(Function.identity()).toList();
    }
}
