package hue.captains.singapura.japjs.core;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class LinkableTest {

    // ---- defaultSimpleName ------------------------------------------------

    @Test
    @DisplayName("simple class name → kebab-case")
    void simpleSplit() {
        assertEquals("pitch-deck",       Linkable.defaultSimpleName(SampleClasses.PitchDeck.class));
        assertEquals("wonderland-demo",  Linkable.defaultSimpleName(SampleClasses.WonderlandDemo.class));
        assertEquals("doc-browser",      Linkable.defaultSimpleName(SampleClasses.DocBrowser.class));
        assertEquals("rfc0001-step",     Linkable.defaultSimpleName(SampleClasses.Rfc0001Step.class));
    }

    @Test
    @DisplayName("single-letter and short class names")
    void shortNames() {
        assertEquals("a", Linkable.defaultSimpleName(SampleClasses.A.class));
        assertEquals("ab", Linkable.defaultSimpleName(SampleClasses.Ab.class));
    }

    @Test
    @DisplayName("acronyms followed by a normal word")
    void acronymBoundary() {
        assertEquals("http-handler",   Linkable.defaultSimpleName(SampleClasses.HTTPHandler.class));
        assertEquals("xml-parser",     Linkable.defaultSimpleName(SampleClasses.XMLParser.class));
        assertEquals("sql-query",      Linkable.defaultSimpleName(SampleClasses.SQLQuery.class));
    }

    @Test
    @DisplayName("trailing acronym is one segment")
    void trailingAcronym() {
        assertEquals("user-id",  Linkable.defaultSimpleName(SampleClasses.UserID.class));
        assertEquals("dns-ttl",  Linkable.defaultSimpleName(SampleClasses.DnsTTL.class));
    }

    @Test
    @DisplayName("all-uppercase class name remains a single segment")
    void allUpper() {
        assertEquals("api",  Linkable.defaultSimpleName(SampleClasses.API.class));
        assertEquals("rfc",  Linkable.defaultSimpleName(SampleClasses.RFC.class));
    }

    @Test
    @DisplayName("digits are treated as continuation of the current word")
    void digits() {
        assertEquals("step01",         Linkable.defaultSimpleName(SampleClasses.Step01.class));
        assertEquals("api2",           Linkable.defaultSimpleName(SampleClasses.API2.class));
        assertEquals("v2-handler",     Linkable.defaultSimpleName(SampleClasses.V2Handler.class));
    }

    // ---- AppModule simpleName / paramsType defaults -----------------------

    @Test
    @DisplayName("AppModule.simpleName() defaults via Linkable.defaultSimpleName")
    void appModuleDefaultSimpleName() {
        assertEquals("sample-app", new SampleClasses.SampleApp().simpleName());
    }

    @Test
    @DisplayName("AppModule.paramsType() defaults to Void.class")
    void appModuleDefaultParamsType() {
        assertEquals(Void.class, new SampleClasses.SampleApp().paramsType());
    }

    @Test
    @DisplayName("AppModule simpleName override wins")
    void appModuleSimpleNameOverride() {
        assertEquals("custom", new SampleClasses.RenamedApp().simpleName());
    }

    // ---- AppLink basic shape ----------------------------------------------

    @Test
    @DisplayName("AppLink<L> may be implemented by an inner record on a Linkable")
    void appLinkImplementable() {
        AppLink<SampleClasses.SampleApp> link = new SampleClasses.SampleApp.link();
        assertNotNull(link);
        assertTrue(link instanceof Exportable);
    }

    // ---- shared sample classes for the tests above ------------------------

    private static final class SampleClasses {
        static class PitchDeck {}
        static class WonderlandDemo {}
        static class DocBrowser {}
        static class Rfc0001Step {}
        static class A {}
        static class Ab {}
        static class HTTPHandler {}
        static class XMLParser {}
        static class SQLQuery {}
        static class UserID {}
        static class DnsTTL {}
        static class API {}
        static class RFC {}
        static class Step01 {}
        static class API2 {}
        static class V2Handler {}

        public record SampleApp() implements AppModule<SampleApp> {
            public record link() implements AppLink<SampleApp> {}
            @Override public String title() { return "Sample"; }
            @Override public ImportsFor<SampleApp> imports() { return ImportsFor.noImports(); }
            @Override public ExportsOf<SampleApp> exports() { return new ExportsOf<>(this, java.util.List.of()); }
        }

        public record RenamedApp() implements AppModule<RenamedApp> {
            @Override public String simpleName() { return "custom"; }
            @Override public String title() { return "Renamed"; }
            @Override public ImportsFor<RenamedApp> imports() { return ImportsFor.noImports(); }
            @Override public ExportsOf<RenamedApp> exports() { return new ExportsOf<>(this, java.util.List.of()); }
        }
    }
}
