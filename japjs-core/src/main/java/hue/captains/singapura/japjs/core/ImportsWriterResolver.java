package hue.captains.singapura.japjs.core;

public interface ImportsWriterResolver {
    <M extends EsModule<M>> SingleModuleImportWriter<M> resolve(M fromModule);
}
