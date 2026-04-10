package hue.captains.singapura.japjs.server;

import hue.captains.singapura.japjs.core.*;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class QueryParamResolverTest {

    record TestModule() implements EsModule<TestModule> {
        static final TestModule INSTANCE = new TestModule();

        @Override
        public ImportsFor<TestModule> imports() {
            return ImportsFor.<TestModule>builder().build();
        }

        @Override
        public ExportsOf<TestModule> exports() {
            return new ExportsOf<>(INSTANCE, List.of());
        }
    }

    @Test
    void resolve_usesDefaultActionPath() {
        var resolver = new QueryParamResolver();
        var result = resolver.resolve(TestModule.INSTANCE);

        assertTrue(result.basePath().startsWith("/module?class="));
        assertTrue(result.basePath().contains(TestModule.class.getCanonicalName()));
        assertFalse(result.basePath().contains(".js"), "Query param resolver should not append .js");
        assertFalse(result.domAware(), "Plain EsModule should not be domAware");
    }

    @Test
    void resolve_usesCustomActionPath() {
        var resolver = new QueryParamResolver("/api/esmodule");
        var result = resolver.resolve(TestModule.INSTANCE);

        assertTrue(result.basePath().startsWith("/api/esmodule?class="));
    }

    @Test
    void resolve_generatesValidImportPath() {
        var resolver = new QueryParamResolver();
        var importWriter = new SingleModuleImportWriter<>(TestModule.INSTANCE, resolver);

        record Foo() implements Exportable._Constant<TestModule> {}

        var imports = new ModuleImports<>(List.of(new Foo()), TestModule.INSTANCE);
        String result = importWriter.writeImports(imports);

        // The import writer no longer appends .js, so the query-param URL is clean
        assertTrue(result.contains("from \"/module?class="));
        assertFalse(result.contains(".js"), "Import from query-param URL should not have .js");
    }
}
