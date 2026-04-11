package hue.captains.singapura.japjs.core.util;

import hue.captains.singapura.japjs.core.*;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class SimpleImportsWriterResolverTest {

    record Dep() implements EsModule<Dep> {
        static final Dep INSTANCE = new Dep();
        record Val() implements Exportable._Constant<Dep> {}
        @Override public ImportsFor<Dep> imports() { return ImportsFor.noImports(); }
        @Override public ExportsOf<Dep> exports() { return new ExportsOf<>(INSTANCE, List.of(new Val())); }
    }

    private final ModuleNameResolver nameResolver = m -> new PartialModulePath("/mod?class=" + m.getClass().getCanonicalName(), false);

    @Test
    void resolve_passesThemeAndLocale() {
        var resolver = new SimpleImportsWriterResolver(nameResolver, "dark", "fr");
        var writer = resolver.resolve(Dep.INSTANCE);

        var imports = new ModuleImports<>(List.of(new Dep.Val()), Dep.INSTANCE);
        String result = writer.writeImports(imports);

        assertTrue(result.contains("Val"));
        assertTrue(result.contains("from"));
    }

    @Test
    void resolve_nullThemeAndLocale() {
        var resolver = new SimpleImportsWriterResolver(nameResolver, null, null);
        var writer = resolver.resolve(Dep.INSTANCE);

        var imports = new ModuleImports<>(List.of(new Dep.Val()), Dep.INSTANCE);
        String result = writer.writeImports(imports);

        assertFalse(result.contains("theme"));
        assertFalse(result.contains("locale"));
    }
}
