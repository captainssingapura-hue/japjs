package hue.captains.singapura.js.homing.core;

public interface ImportsWriterResolver {
    <M extends EsModule<M>> SingleModuleImportWriter<M> resolve(M fromModule);
}
