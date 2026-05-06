package hue.captains.singapura.js.homing.server;

import hue.captains.singapura.js.homing.core.BundledExternalModule;
import hue.captains.singapura.js.homing.core.Exportable;
import hue.captains.singapura.js.homing.core.ExportsOf;
import hue.captains.singapura.js.homing.demo.css.PlaygroundStyles;
import hue.captains.singapura.js.homing.demo.es.*;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.concurrent.ExecutionException;

import static org.junit.jupiter.api.Assertions.*;

class EsModuleGetActionTest {

    private final EsModuleGetAction action = new EsModuleGetAction(new QueryParamResolver());

    @Test
    void execute_generatesJsForAlice() throws Exception {
        var query = new ModuleQuery(Alice.class.getCanonicalName());
        var result = action.execute(query, new EmptyParam.NoHeaders()).get();

        assertNotNull(result);
        assertEquals("application/javascript", result.contentType());
        assertTrue(result.body().contains("AliceClass"));
        assertTrue(result.body().contains("export {"));
    }

    @Test
    void execute_generatesJsForSvgGroup() throws Exception {
        var query = new ModuleQuery(Wonderland.class.getCanonicalName());
        var result = action.execute(query, new EmptyParam.NoHeaders()).get();

        assertNotNull(result);
        assertEquals("application/javascript", result.contentType());
        assertTrue(result.body().contains("CheshireCat"));
        assertTrue(result.body().contains("export {"));
    }

    /** Test fixture: a BundledExternalModule pointing at a sentinel file in src/test/resources. */
    public record TestBundledModule() implements BundledExternalModule<TestBundledModule> {
        public static final TestBundledModule INSTANCE = new TestBundledModule();
        @Override public String sourceUrl()    { return "https://example.com/test-bundle@1.0.0.js"; }
        @Override public String resourcePath() { return "lib/test-bundle@1.0.0/test-bundle.js"; }
        @Override public String sha512()       { return "test-fixture-no-checksum"; }

        public record FakeBundleClass()   implements Exportable._Class<TestBundledModule> {}
        public record FakeBundleConstant() implements Exportable._Constant<TestBundledModule> {}

        @Override public ExportsOf<TestBundledModule> exports() {
            return new ExportsOf<>(INSTANCE, List.of(new FakeBundleClass(), new FakeBundleConstant()));
        }
    }

    @Test
    void execute_returnsBundledContentVerbatim() throws Exception {
        // BundledExternalModule short-circuits the writer machinery: the bundled JS
        // file's content is shipped to the browser as-is, with no imports prefix,
        // no exports suffix, and no css/href injection.
        // getName() returns the binary name (with `$` for nesting); Class.forName needs that form
        var query = new ModuleQuery(TestBundledModule.class.getName());
        var result = action.execute(query, new EmptyParam.NoHeaders()).get();

        assertNotNull(result);
        assertEquals("application/javascript", result.contentType());
        // Bundle content present verbatim
        assertTrue(result.body().contains("=== SENTINEL: this is the bundled JS file content ==="),
                "Bundled content should appear verbatim");
        assertTrue(result.body().contains("export class FakeBundleClass"),
                "Bundled JS exports should be preserved");
        // No framework-generated wrapper prefix/suffix
        assertFalse(result.body().contains("import { CssClassManagerInstance"),
                "BundledExternalModule must not get css manager injected");
        assertFalse(result.body().contains("import { HrefManagerInstance"),
                "BundledExternalModule must not get href manager injected");
        assertFalse(result.body().contains("export {"),
                "BundledExternalModule must not get a generated export block (the bundled JS has its own exports)");
    }

    @Test
    void execute_failsForUnknownClass() {
        var query = new ModuleQuery("com.nonexistent.FakeModule");
        var future = action.execute(query, new EmptyParam.NoHeaders());

        assertThrows(ExecutionException.class, future::get);
    }

    @Test
    void execute_failsForNullClassName() {
        var query = new ModuleQuery(null);
        var future = action.execute(query, new EmptyParam.NoHeaders());

        var ex = assertThrows(ExecutionException.class, future::get);
        assertInstanceOf(ResourceNotFound.class, ex.getCause());
    }

    @Test
    void execute_failsForBlankClassName() {
        var query = new ModuleQuery("   ");
        var future = action.execute(query, new EmptyParam.NoHeaders());

        var ex = assertThrows(ExecutionException.class, future::get);
        assertInstanceOf(ResourceNotFound.class, ex.getCause());
    }

    @Test
    void execute_autoInjectsCssManagerForDomModuleWithCss() throws Exception {
        var query = new ModuleQuery(MovingAnimal.class.getCanonicalName());
        var result = action.execute(query, new EmptyParam.NoHeaders()).get();
        String js = result.body();

        // Should import CssClassManagerInstance from the CssClassManager module
        assertTrue(js.contains("import { CssClassManagerInstance as css } from \"/module?class=hue.captains.singapura.js.homing.server.CssClassManager\""),
                "Should import CssClassManagerInstance");
        // Should import CSS classes from PlaygroundStyles
        assertTrue(js.contains("from \"/module?class=" + PlaygroundStyles.class.getCanonicalName()),
                "Should import from PlaygroundStyles");
    }

    @Test
    void execute_noCssManagerForPureEsModule() throws Exception {
        var query = new ModuleQuery(JumpPhysics.class.getCanonicalName());
        var result = action.execute(query, new EmptyParam.NoHeaders()).get();

        assertFalse(result.body().contains("CssClassManagerInstance"),
                "Should not inject CssClassManager for pure EsModules");
    }

    @Test
    void execute_servesThreeJsBundleVerbatim() throws Exception {
        // ThreeJs is now a BundledExternalModule — the action ships the actual
        // three.js bundle from the homing.js classpath. No CDN import line, no
        // framework-generated export wrapper — the bundled JS file declares its
        // own exports.
        var query = new ModuleQuery(hue.captains.singapura.js.homing.libs.ThreeJs.class.getCanonicalName());
        var result = action.execute(query, new EmptyParam.NoHeaders()).get();
        String js = result.body();

        // No CDN import survives in the served JS — it's the bundle itself
        assertFalse(js.contains("from \"https://"),
                "BundledExternalModule output must contain no CDN import lines");
        // Bundled three.js (minified) declares its own exports — esm.sh banner + `export{...}`
        assertTrue(js.contains("/* esm.sh - three@0.170.0 */"),
                "Should contain the esm.sh bundle banner");
        assertTrue(js.contains("export{"),
                "Bundled three.js should still expose its native export statements");
    }

    @Test
    void execute_turtleDemoImportsFromBundledThreeJs() throws Exception {
        var query = new ModuleQuery(TurtleDemo.class.getCanonicalName());
        var result = action.execute(query, new EmptyParam.NoHeaders()).get();
        String js = result.body();

        // The framework generates an import line that points to the BundledExternalModule's
        // homing.js URL — the browser will fetch the bundle from our server, not a CDN.
        assertTrue(js.contains("from \"/module?class=hue.captains.singapura.js.homing.libs.ThreeJs\""),
                "Should import from the bundled ThreeJs module on the homing.js classpath");
        assertTrue(js.contains("export {appMain"),
                "Should export appMain");
        assertTrue(js.contains("new Scene()"),
                "Should contain turtle scene code");
    }

    @Test
    void execute_generatedImportsUseQueryParamPaths() throws Exception {
        // BobModule imports from Alice and Wonderland — verify the generated import
        // statements use query-param URLs, not file paths
        var query = new ModuleQuery("hue.captains.singapura.js.homing.demo.es.BobModule");
        var result = action.execute(query, new EmptyParam.NoHeaders()).get();

        assertTrue(result.body().contains("from \"/module?class="),
                "Import paths should use query-param format");
        assertFalse(result.body().contains("/test/"),
                "Import paths should not use file-based prefix");
    }

    @Test
    void execute_withThemeLocale_forwardsToDomModuleImports() throws Exception {
        var query = new ModuleQuery(MovingAnimal.class.getCanonicalName(), "dark", "en");
        var result = action.execute(query, new EmptyParam.NoHeaders()).get();
        String js = result.body();

        // AnimalCell is a DomModule — should receive theme and locale
        assertTrue(js.contains("AnimalCell&theme=dark&locale=en"),
                "DomModule import should include theme and locale");

        // JumpPhysics is a plain EsModule — should NOT receive theme/locale
        assertFalse(js.contains("JumpPhysics&theme="),
                "Pure EsModule import should not include theme");

        // ToneJs is an ExternalModule — should NOT receive theme/locale
        assertFalse(js.contains("ToneJs&theme="),
                "ExternalModule import should not include theme");

        // CssClassManager is a plain EsModule — should NOT receive theme/locale
        assertFalse(js.contains("CssClassManager&theme="),
                "CssClassManager import should not include theme");
    }

    @Test
    void execute_withoutThemeLocale_noParamsAppended() throws Exception {
        var query = new ModuleQuery(MovingAnimal.class.getCanonicalName());
        var result = action.execute(query, new EmptyParam.NoHeaders()).get();
        String js = result.body();

        assertFalse(js.contains("&theme="),
                "No theme param should appear when not provided");
        assertFalse(js.contains("&locale="),
                "No locale param should appear when not provided");
    }
}
