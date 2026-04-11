package hue.captains.singapura.japjs.server;

import hue.captains.singapura.japjs.demo.es.Alice;
import hue.captains.singapura.japjs.demo.es.WonderlandDemo;
import org.junit.jupiter.api.Test;

import java.util.concurrent.ExecutionException;

import static org.junit.jupiter.api.Assertions.*;

class AppHtmlGetActionTest {

    private final AppHtmlGetAction action = new AppHtmlGetAction(new QueryParamResolver());

    @Test
    void execute_generatesHtmlForAppModule() throws Exception {
        var query = new ModuleQuery(WonderlandDemo.class.getCanonicalName());
        var result = action.execute(query, new EmptyParam.NoHeaders()).get();

        assertEquals("text/html", result.contentType());
        assertTrue(result.body().contains("<title>Wonderland Demo</title>"));
        assertTrue(result.body().contains("/module?class=" + WonderlandDemo.class.getCanonicalName()));
        assertTrue(result.body().contains("await import(moduleUrl)"));
        assertTrue(result.body().contains("appMain(document.getElementById(\"app\"))"));
        assertTrue(result.body().contains("colorScheme"), "Should set color-scheme from theme");
        assertTrue(result.body().contains("matchMedia"), "Should detect system theme preference");
    }

    @Test
    void execute_respectsThemeOverride() throws Exception {
        var query = new ModuleQuery(WonderlandDemo.class.getCanonicalName(), "dark", null);
        var result = action.execute(query, new EmptyParam.NoHeaders()).get();

        assertTrue(result.body().contains("\"dark\""), "Should include theme override in bootstrap");
    }

    @Test
    void execute_respectsLocaleOverride() throws Exception {
        var query = new ModuleQuery(WonderlandDemo.class.getCanonicalName(), null, "fr");
        var result = action.execute(query, new EmptyParam.NoHeaders()).get();

        assertTrue(result.body().contains("\"fr\""), "Should include locale override in bootstrap");
    }

    @Test
    void execute_failsForNonAppModule() {
        // Alice is an EsModule but not an AppModule
        var query = new ModuleQuery(Alice.class.getCanonicalName());
        var future = action.execute(query, new EmptyParam.NoHeaders());

        var ex = assertThrows(ExecutionException.class, future::get);
        assertInstanceOf(ResourceNotFound.class, ex.getCause());
    }

    @Test
    void execute_failsForNullClassName() {
        var query = new ModuleQuery(null);
        var future = action.execute(query, new EmptyParam.NoHeaders());

        var ex = assertThrows(ExecutionException.class, future::get);
        assertInstanceOf(ResourceNotFound.class, ex.getCause());
    }

    @Test
    void execute_failsForUnknownClass() {
        var query = new ModuleQuery("com.nonexistent.FakeApp");
        var future = action.execute(query, new EmptyParam.NoHeaders());

        assertThrows(ExecutionException.class, future::get);
    }
}
