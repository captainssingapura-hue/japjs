package hue.captains.singapura.japjs.core;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class SingleModuleImportWriterTest {

    record TestModule() implements EsModule<TestModule> {
        record Foo() implements Exportable._Constant<TestModule> {}
        record Bar() implements Exportable._Class<TestModule> {}

        static final TestModule INSTANCE = new TestModule();

        @Override
        public ImportsFor<TestModule> imports() {
            return ImportsFor.<TestModule>builder().build();
        }

        @Override
        public ExportsOf<TestModule> exports() {
            return new ExportsOf<>(INSTANCE, List.of(new Foo(), new Bar()));
        }
    }

    @Test
    void writeImports_doesNotAppendJsSuffix() {
        ModuleNameResolver resolver = m -> new PartialModulePath("/modules/" + m.getClass().getSimpleName() + ".js", false);
        var writer = new SingleModuleImportWriter<>(TestModule.INSTANCE, resolver);

        var imports = new ModuleImports<>(List.of(new TestModule.Foo(), new TestModule.Bar()), TestModule.INSTANCE);
        String result = writer.writeImports(imports);

        assertEquals("import {Foo, Bar} from \"/modules/TestModule.js\";", result);
        // Verify no double .js
        assertFalse(result.contains(".js.js"), "Should not double-append .js");
    }

    @Test
    void writeImports_worksWithQueryParamResolver() {
        ModuleNameResolver resolver = m -> new PartialModulePath("/module?class=" + m.getClass().getCanonicalName(), false);
        var writer = new SingleModuleImportWriter<>(TestModule.INSTANCE, resolver);

        var imports = new ModuleImports<>(List.of(new TestModule.Foo()), TestModule.INSTANCE);
        String result = writer.writeImports(imports);

        assertTrue(result.startsWith("import {Foo} from \"/module?class="));
        assertTrue(result.endsWith("\";"));
        assertFalse(result.contains(".js"), "Query-param URLs should not have .js");
    }
}
