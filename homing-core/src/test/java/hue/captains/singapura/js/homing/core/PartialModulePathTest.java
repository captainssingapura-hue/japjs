package hue.captains.singapura.js.homing.core;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PartialModulePathTest {

    @Test
    void withTheme_appendsWhenDomAware() {
        var path = new PartialModulePath("/module?class=X", true);
        var result = path.withTheme("dark");

        assertEquals("/module?class=X&theme=dark", result.basePath());
        assertTrue(result.domAware());
    }

    @Test
    void withTheme_noOpWhenNotDomAware() {
        var path = new PartialModulePath("/module?class=X", false);
        var result = path.withTheme("dark");

        assertSame(path, result, "Should return same instance when not domAware");
    }

    @Test
    void withTheme_noOpWhenNull() {
        var path = new PartialModulePath("/module?class=X", true);
        var result = path.withTheme(null);

        assertSame(path, result, "Should return same instance when theme is null");
    }

    @Test
    void withLocale_appendsWhenDomAware() {
        var path = new PartialModulePath("/module?class=X", true);
        var result = path.withLocale("fr");

        assertEquals("/module?class=X&locale=fr", result.basePath());
    }

    @Test
    void withLocale_noOpWhenNotDomAware() {
        var path = new PartialModulePath("/module?class=X", false);
        var result = path.withLocale("fr");

        assertSame(path, result);
    }

    @Test
    void chaining_themeAndLocale() {
        var path = new PartialModulePath("/module?class=X", true);
        var result = path.withTheme("dark").withLocale("en");

        assertEquals("/module?class=X&theme=dark&locale=en", result.basePath());
    }

    @Test
    void chaining_noOpWhenNotDomAware() {
        var path = new PartialModulePath("/test/module.js", false);
        var result = path.withTheme("dark").withLocale("en");

        assertEquals("/test/module.js", result.basePath());
        assertSame(path, result);
    }

    @Test
    void toString_returnsBasePath() {
        var path = new PartialModulePath("/module?class=X", true);
        assertEquals("/module?class=X", path.toString());
    }
}
