package hue.captains.singapura.japjs.core;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class ParamsWriterTest {

    // =====================================================================
    // Sample Params records
    // =====================================================================

    record StringP(String name) {}
    record IntP(int n) {}
    record LongP(long n) {}
    record DoubleP(double v) {}
    record BoolP(boolean ok) {}
    record IntegerP(Integer n) {}
    record DoubleBoxedP(Double v) {}
    record OptStringP(Optional<String> tab) {}
    record OptIntP(Optional<Integer> count) {}
    record ListStringP(List<String> tags) {}
    record ListIntP(List<Integer> ids) {}

    enum Status { ACTIVE, ARCHIVED, DRAFT }
    record EnumP(Status status) {}
    record OptEnumP(Optional<Status> status) {}
    record ListEnumP(List<Status> tags) {}

    record Combined(String productId, int quantity, Optional<String> tab, boolean expanded) {}

    record ReservedApp(String app) {}
    record ReservedTheme(String theme) {}
    record ReservedLocale(String locale) {}

    record NestedRecord(String n) {}
    record HasNested(NestedRecord nested) {}

    // =====================================================================
    // No-op cases
    // =====================================================================

    @Test
    @DisplayName("Void.class → no params block emitted")
    void voidClass_emptyOutput() {
        assertEquals(List.of(), new ParamsWriter(Void.class).write());
    }

    @Test
    @DisplayName("null paramsType → no params block emitted")
    void nullClass_emptyOutput() {
        assertEquals(List.of(), new ParamsWriter(null).write());
    }

    @Test
    @DisplayName("non-record paramsType throws")
    void nonRecord_throws() {
        var ex = assertThrows(IllegalStateException.class, () -> new ParamsWriter(String.class).write());
        assertTrue(ex.getMessage().contains("record"), ex.getMessage());
    }

    // =====================================================================
    // Scalar types
    // =====================================================================

    @Test
    @DisplayName("String component → sp.get(name) || \"\"")
    void stringComponent() {
        var joined = String.join("\n", new ParamsWriter(StringP.class).write());
        assertTrue(joined.contains("name: (sp.get(\"name\") || \"\")"), joined);
        assertTrue(joined.contains("// === japjs generated params"), joined);
        assertTrue(joined.contains("Object.freeze({"), joined);
    }

    @Test
    @DisplayName("primitive int → parseInt with 0 default")
    void primitiveInt() {
        var joined = String.join("\n", new ParamsWriter(IntP.class).write());
        assertTrue(joined.contains("parseInt(v, 10)"), joined);
        assertTrue(joined.contains("? 0 :"), joined);
    }

    @Test
    @DisplayName("primitive long → parseInt with 0 default")
    void primitiveLong() {
        var joined = String.join("\n", new ParamsWriter(LongP.class).write());
        assertTrue(joined.contains("parseInt(v, 10)"), joined);
    }

    @Test
    @DisplayName("primitive double → parseFloat with 0 default")
    void primitiveDouble() {
        var joined = String.join("\n", new ParamsWriter(DoubleP.class).write());
        assertTrue(joined.contains("parseFloat(v)"), joined);
        assertTrue(joined.contains("? 0 :"), joined);
    }

    @Test
    @DisplayName("primitive boolean → strict === \"true\" check")
    void primitiveBoolean() {
        var joined = String.join("\n", new ParamsWriter(BoolP.class).write());
        assertTrue(joined.contains("ok: (sp.get(\"ok\") === \"true\")"), joined);
    }

    @Test
    @DisplayName("boxed Integer → null when absent (vs primitive's 0)")
    void boxedInteger() {
        var joined = String.join("\n", new ParamsWriter(IntegerP.class).write());
        assertTrue(joined.contains("? null :"), joined);
        assertTrue(joined.contains("parseInt(v, 10)"), joined);
    }

    @Test
    @DisplayName("boxed Double → null when absent")
    void boxedDouble() {
        var joined = String.join("\n", new ParamsWriter(DoubleBoxedP.class).write());
        assertTrue(joined.contains("? null :"), joined);
        assertTrue(joined.contains("parseFloat(v)"), joined);
    }

    // =====================================================================
    // Optional<T>
    // =====================================================================

    @Test
    @DisplayName("Optional<String> → undefined when absent, raw string when present")
    void optionalString() {
        var joined = String.join("\n", new ParamsWriter(OptStringP.class).write());
        assertTrue(joined.contains("v == null ? undefined : v"), joined);
        assertTrue(joined.contains("sp.get(\"tab\")"), joined);
    }

    @Test
    @DisplayName("Optional<Integer> → undefined when absent, parseInt when present")
    void optionalInt() {
        var joined = String.join("\n", new ParamsWriter(OptIntP.class).write());
        assertTrue(joined.contains("v == null ? undefined : parseInt(v, 10)"), joined);
    }

    // =====================================================================
    // List<T>
    // =====================================================================

    @Test
    @DisplayName("List<String> → sp.getAll(name).map(identity)")
    void listString() {
        var joined = String.join("\n", new ParamsWriter(ListStringP.class).write());
        assertTrue(joined.contains("sp.getAll(\"tags\") || []"), joined);
        assertTrue(joined.contains(".map(function(x) { return x;"), joined);
    }

    @Test
    @DisplayName("List<Integer> → sp.getAll(name).map(parseInt)")
    void listInteger() {
        var joined = String.join("\n", new ParamsWriter(ListIntP.class).write());
        assertTrue(joined.contains("sp.getAll(\"ids\") || []"), joined);
        assertTrue(joined.contains("parseInt(x, 10)"), joined);
    }

    // =====================================================================
    // Enum
    // =====================================================================

    @Test
    @DisplayName("Enum component → _enum helper + allowed-values list")
    void enumComponent() {
        var joined = String.join("\n", new ParamsWriter(EnumP.class).write());
        assertTrue(joined.contains("function _enum(v, allowed)"), joined);
        assertTrue(joined.contains("[\"ACTIVE\", \"ARCHIVED\", \"DRAFT\"]"), joined);
        assertTrue(joined.contains("_enum(v, [\"ACTIVE\""), joined);
    }

    @Test
    @DisplayName("Optional<Enum> → undefined when absent, validated enum when present")
    void optionalEnum() {
        var joined = String.join("\n", new ParamsWriter(OptEnumP.class).write());
        assertTrue(joined.contains("function _enum"), joined);
        assertTrue(joined.contains("v == null ? undefined : _enum(v,"), joined);
    }

    @Test
    @DisplayName("List<Enum> → getAll then map through _enum")
    void listEnum() {
        var joined = String.join("\n", new ParamsWriter(ListEnumP.class).write());
        assertTrue(joined.contains("function _enum"), joined);
        assertTrue(joined.contains(".map(function(x) { return _enum(x,"), joined);
    }

    // =====================================================================
    // Combined / multiple components
    // =====================================================================

    @Test
    @DisplayName("multiple components → all emitted with proper commas")
    void combined() {
        var joined = String.join("\n", new ParamsWriter(Combined.class).write());
        assertTrue(joined.contains("productId:"), joined);
        assertTrue(joined.contains("quantity:"), joined);
        assertTrue(joined.contains("tab:"), joined);
        assertTrue(joined.contains("expanded:"), joined);

        // Trailing entry has no comma; preceding three do
        var lines = joined.lines().filter(l -> l.contains(": ") && !l.contains("//") && !l.contains("function ")).toList();
        long withComma = lines.stream().filter(l -> l.trim().endsWith(",")).count();
        long withoutComma = lines.stream().filter(l -> !l.trim().endsWith(",") && !l.trim().endsWith("{")).count();
        assertEquals(3, withComma, "got: " + lines);
        assertTrue(withoutComma >= 1, "got: " + lines);
    }

    // =====================================================================
    // Reserved keys
    // =====================================================================

    @Test
    @DisplayName("component named 'app' → reserved-key collision error")
    void reservedApp() {
        var ex = assertThrows(IllegalStateException.class, () -> new ParamsWriter(ReservedApp.class).write());
        assertTrue(ex.getMessage().contains("reserved"), ex.getMessage());
        assertTrue(ex.getMessage().contains("app"), ex.getMessage());
    }

    @Test
    @DisplayName("component named 'theme' → reserved-key collision error")
    void reservedTheme() {
        var ex = assertThrows(IllegalStateException.class, () -> new ParamsWriter(ReservedTheme.class).write());
        assertTrue(ex.getMessage().contains("theme"), ex.getMessage());
    }

    @Test
    @DisplayName("component named 'locale' → reserved-key collision error")
    void reservedLocale() {
        var ex = assertThrows(IllegalStateException.class, () -> new ParamsWriter(ReservedLocale.class).write());
        assertTrue(ex.getMessage().contains("locale"), ex.getMessage());
    }

    // =====================================================================
    // Forbidden: nested records
    // =====================================================================

    @Test
    @DisplayName("nested record component → explicit not-supported error")
    void nestedRecord_forbidden() {
        var ex = assertThrows(IllegalStateException.class, () -> new ParamsWriter(HasNested.class).write());
        assertTrue(ex.getMessage().contains("Nested"), ex.getMessage());
        assertTrue(ex.getMessage().contains("§9.2") || ex.getMessage().contains("v1"), ex.getMessage());
    }

    // =====================================================================
    // EsModuleWriter integration — params const appears in output
    // =====================================================================

    record AppWithParams() implements AppModule<AppWithParams> {
        public record Params(String productId, int quantity) {}
        static final AppWithParams INSTANCE = new AppWithParams();
        @Override public String title() { return "Sample"; }
        @Override public Class<?> paramsType() { return Params.class; }
        @Override public ImportsFor<AppWithParams> imports() { return ImportsFor.noImports(); }
        @Override public ExportsOf<AppWithParams> exports() { return new ExportsOf<>(this, List.of()); }
    }

    record AppNoParams() implements AppModule<AppNoParams> {
        static final AppNoParams INSTANCE = new AppNoParams();
        @Override public String title() { return "NoParams"; }
        @Override public ImportsFor<AppNoParams> imports() { return ImportsFor.noImports(); }
        @Override public ExportsOf<AppNoParams> exports() { return new ExportsOf<>(this, List.of()); }
    }

    @Test
    @DisplayName("EsModuleWriter emits params block for AppModule with non-Void Params")
    void writerEmitsParamsForAppWithParams() {
        var writer = new EsModuleWriter<>(
                AppWithParams.INSTANCE,
                () -> List.of("function appMain(rootEl) { /* uses params.productId, params.quantity */ }"),
                m -> new hue.captains.singapura.japjs.core.PartialModulePath("ignored", false),
                ExportWriter.INSTANCE,
                new hue.captains.singapura.japjs.core.util.SimpleImportsWriterResolver(
                        m -> new hue.captains.singapura.japjs.core.PartialModulePath("ignored", false)));
        var lines = writer.writeModule();
        var joined = String.join("\n", lines);
        assertTrue(joined.contains("// === japjs generated params"), joined);
        assertTrue(joined.contains("productId:"), joined);
        assertTrue(joined.contains("quantity:"), joined);
    }

    @Test
    @DisplayName("EsModuleWriter does NOT emit params block for AppModule with default (Void) Params")
    void writerSkipsParamsForVoid() {
        var writer = new EsModuleWriter<>(
                AppNoParams.INSTANCE,
                () -> List.of("function appMain(rootEl) {}"),
                m -> new hue.captains.singapura.japjs.core.PartialModulePath("ignored", false),
                ExportWriter.INSTANCE,
                new hue.captains.singapura.japjs.core.util.SimpleImportsWriterResolver(
                        m -> new hue.captains.singapura.japjs.core.PartialModulePath("ignored", false)));
        var joined = String.join("\n", writer.writeModule());
        assertFalse(joined.contains("japjs generated params"), joined);
    }
}
