package hue.captains.singapura.js.homing.server;

import hue.captains.singapura.js.homing.demo.css.PlaygroundStyles;
import hue.captains.singapura.js.homing.demo.es.*;
import org.junit.jupiter.api.Test;

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
    void execute_generatesThreeJsWrapper() throws Exception {
        var query = new ModuleQuery(ThreeJs.class.getCanonicalName());
        var result = action.execute(query, new EmptyParam.NoHeaders()).get();
        String js = result.body();

        assertTrue(js.contains("from \"https://esm.sh/three@0.170.0\""),
                "Should contain CDN import from esm.sh");
        assertTrue(js.contains("export {Scene"),
                "Should export Three.js classes");
    }

    @Test
    void execute_turtleDemoImportsFromThreeJsWrapper() throws Exception {
        var query = new ModuleQuery(TurtleDemo.class.getCanonicalName());
        var result = action.execute(query, new EmptyParam.NoHeaders()).get();
        String js = result.body();

        assertTrue(js.contains("from \"/module?class=hue.captains.singapura.js.homing.demo.es.ThreeJs\""),
                "Should import from ThreeJs wrapper module");
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
