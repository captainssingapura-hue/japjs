package hue.captains.singapura.japjs.server;

import hue.captains.singapura.japjs.core.SimpleAppResolver;
import hue.captains.singapura.japjs.demo.es.Alice;
import hue.captains.singapura.japjs.demo.es.WonderlandDemo;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.concurrent.ExecutionException;

import static org.junit.jupiter.api.Assertions.*;

class AppHtmlGetActionTest {

    private final AppHtmlGetAction action = new AppHtmlGetAction(new QueryParamResolver());

    private static AppQuery byClass(String className) {
        return new AppQuery(null, className, null, null);
    }

    private static AppQuery byClass(String className, String theme, String locale) {
        return new AppQuery(null, className, theme, locale);
    }

    private static AppQuery byApp(String simpleName) {
        return new AppQuery(simpleName, null, null, null);
    }

    @Test
    void execute_generatesHtmlForAppModule() throws Exception {
        var query = byClass(WonderlandDemo.class.getCanonicalName());
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
        var query = byClass(WonderlandDemo.class.getCanonicalName(), "dark", null);
        var result = action.execute(query, new EmptyParam.NoHeaders()).get();

        assertTrue(result.body().contains("\"dark\""), "Should include theme override in bootstrap");
    }

    @Test
    void execute_respectsLocaleOverride() throws Exception {
        var query = byClass(WonderlandDemo.class.getCanonicalName(), null, "fr");
        var result = action.execute(query, new EmptyParam.NoHeaders()).get();

        assertTrue(result.body().contains("\"fr\""), "Should include locale override in bootstrap");
    }

    @Test
    void execute_failsForNonAppModule() {
        // Alice is an EsModule but not an AppModule
        var query = byClass(Alice.class.getCanonicalName());
        var future = action.execute(query, new EmptyParam.NoHeaders());

        var ex = assertThrows(ExecutionException.class, future::get);
        assertInstanceOf(ResourceNotFound.class, ex.getCause());
    }

    @Test
    void execute_failsForNullClassName() {
        var query = byClass(null);
        var future = action.execute(query, new EmptyParam.NoHeaders());

        var ex = assertThrows(ExecutionException.class, future::get);
        assertInstanceOf(ResourceNotFound.class, ex.getCause());
    }

    @Test
    void execute_failsForUnknownClass() {
        var query = byClass("com.nonexistent.FakeApp");
        var future = action.execute(query, new EmptyParam.NoHeaders());

        assertThrows(ExecutionException.class, future::get);
    }

    // ---- RFC 0001 Step 07: ?app= dispatch ---------------------------------

    @Test
    void execute_resolvesByAppSimpleName() throws Exception {
        var resolver = new SimpleAppResolver(List.of(WonderlandDemo.INSTANCE));
        var actionWithResolver = new AppHtmlGetAction(new QueryParamResolver(), resolver);
        var query = byApp(WonderlandDemo.INSTANCE.simpleName());
        var result = actionWithResolver.execute(query, new EmptyParam.NoHeaders()).get();

        assertTrue(result.body().contains("<title>Wonderland Demo</title>"));
        assertTrue(result.body().contains("/module?class=" + WonderlandDemo.class.getCanonicalName()));
    }

    @Test
    void execute_returnsNotFoundForUnknownAppName() {
        var resolver = new SimpleAppResolver(List.of(WonderlandDemo.INSTANCE));
        var actionWithResolver = new AppHtmlGetAction(new QueryParamResolver(), resolver);
        var query = byApp("definitely-not-registered");
        var future = actionWithResolver.execute(query, new EmptyParam.NoHeaders());

        var ex = assertThrows(ExecutionException.class, future::get);
        assertInstanceOf(ResourceNotFound.class, ex.getCause());
    }

    @Test
    void execute_failsCleanlyWhenAppParamUsedWithoutResolver() {
        // Action constructed without a resolver — ?app= cannot work.
        var query = byApp("anything");
        var future = action.execute(query, new EmptyParam.NoHeaders());

        var ex = assertThrows(ExecutionException.class, future::get);
        assertInstanceOf(ResourceNotFound.class, ex.getCause());
    }

    @Test
    void execute_appWinsOverClassWhenBothProvided() throws Exception {
        var resolver = new SimpleAppResolver(List.of(WonderlandDemo.INSTANCE));
        var actionWithResolver = new AppHtmlGetAction(new QueryParamResolver(), resolver);
        // ?app=wonderland-demo &class=Alice — app wins, Alice is ignored.
        var query = new AppQuery(WonderlandDemo.INSTANCE.simpleName(),
                                 Alice.class.getCanonicalName(), null, null);
        var result = actionWithResolver.execute(query, new EmptyParam.NoHeaders()).get();

        assertTrue(result.body().contains("<title>Wonderland Demo</title>"));
    }
}
