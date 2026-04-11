package hue.captains.singapura.japjs.core;

import org.junit.jupiter.api.Test;

import hue.captains.singapura.japjs.core.util.SimpleImportsWriterResolver;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class EsModuleWriterTest {

    record Source() implements EsModule<Source> {
        static final Source INSTANCE = new Source();
        record Greet() implements Exportable._Constant<Source> {}

        @Override public ImportsFor<Source> imports() { return ImportsFor.noImports(); }
        @Override public ExportsOf<Source> exports() {
            return new ExportsOf<>(INSTANCE, List.of(new Greet()));
        }
    }

    record Consumer() implements EsModule<Consumer> {
        static final Consumer INSTANCE = new Consumer();
        record main() implements Exportable._Constant<Consumer> {}

        @Override public ImportsFor<Consumer> imports() {
            return ImportsFor.<Consumer>builder()
                    .add(new ModuleImports<>(List.of(new Source.Greet()), Source.INSTANCE))
                    .build();
        }

        @Override public ExportsOf<Consumer> exports() {
            return new ExportsOf<>(INSTANCE, List.of(new main()));
        }
    }

    record NoExports() implements EsModule<NoExports> {
        static final NoExports INSTANCE = new NoExports();
        @Override public ImportsFor<NoExports> imports() { return ImportsFor.noImports(); }
        @Override public ExportsOf<NoExports> exports() { return new ExportsOf<>(INSTANCE, List.of()); }
    }

    private final ModuleNameResolver resolver = m -> new PartialModulePath("/mod?class=" + m.getClass().getCanonicalName(), false);
    private final SimpleImportsWriterResolver importsResolver = new SimpleImportsWriterResolver(resolver);

    @Test
    void writeModule_noImportsNoExports() {
        ContentProvider<NoExports> content = () -> List.of("console.log('hello');");
        var writer = new EsModuleWriter<>(NoExports.INSTANCE, content, resolver, ExportWriter.INSTANCE, importsResolver);

        var lines = writer.writeModule();
        assertEquals(List.of("console.log('hello');"), lines);
    }

    @Test
    void writeModule_withImportsContentAndExports() {
        ContentProvider<Consumer> content = () -> List.of("const main = () => Greet;");
        var writer = new EsModuleWriter<>(Consumer.INSTANCE, content, resolver, ExportWriter.INSTANCE, importsResolver);

        var lines = writer.writeModule();

        assertEquals(3, lines.size());
        assertTrue(lines.get(0).startsWith("import {Greet} from"));
        assertEquals("const main = () => Greet;", lines.get(1));
        assertEquals("export {main};", lines.get(2));
    }

    @Test
    void writeModule_contentOnly() {
        ContentProvider<Source> content = () -> List.of("const Greet = 'hello';");
        var writer = new EsModuleWriter<>(Source.INSTANCE, content, resolver, ExportWriter.INSTANCE, importsResolver);

        var lines = writer.writeModule();
        assertEquals(2, lines.size());
        assertEquals("const Greet = 'hello';", lines.get(0));
        assertEquals("export {Greet};", lines.get(1));
    }

    @Test
    void writeModule_multiLineContent() {
        ContentProvider<Source> content = () -> List.of(
                "const Greet = () => {",
                "  return 'hi';",
                "};"
        );
        var writer = new EsModuleWriter<>(Source.INSTANCE, content, resolver, ExportWriter.INSTANCE, importsResolver);

        var lines = writer.writeModule();
        assertEquals(4, lines.size());
        assertEquals("export {Greet};", lines.getLast());
    }
}
