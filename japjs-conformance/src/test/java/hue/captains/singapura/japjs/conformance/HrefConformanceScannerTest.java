package hue.captains.singapura.japjs.conformance;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/** Unit tests for the {@link HrefConformanceTest} static scanner. */
class HrefConformanceScannerTest {

    // =====================================================================
    // The one allowed pattern — never produces violations
    // =====================================================================

    @Test
    @DisplayName("href.toAttr(...) — allowed, no violation")
    void allowed_toAttr() {
        var v = HrefConformanceTest.scan(List.of(
                "var a = href.toAttr(nav.PitchDeck());"));
        assertEquals(List.of(), v);
    }

    @Test
    @DisplayName("href.set / create / openNew / navigate / fragment — all allowed")
    void allowed_allSixMethods() {
        var lines = List.of(
                "href.set(el, nav.X());",
                "var a = href.create(nav.X(), {text: \"hi\"});",
                "href.openNew(nav.X());",
                "href.navigate(nav.X());",
                "var f = href.fragment(\"section-2\");"
        );
        assertEquals(List.of(), HrefConformanceTest.scan(lines));
    }

    // =====================================================================
    // Forbidden — literal href= (HTML attribute or property assign)
    // =====================================================================

    @Test
    @DisplayName("'<a href=\"...\">' literal in JS string — forbidden")
    void forbidden_literalHrefAttr() {
        var v = HrefConformanceTest.scan(List.of(
                "elem.innerHTML = '<a href=\"/foo\">x</a>';"));
        assertEquals(1, v.size());
        assertTrue(v.get(0).pattern().contains("href"), v.get(0).pattern());
    }

    @Test
    @DisplayName("el.href = '...' assignment — forbidden (matches both \\bhref\\s*= and \\.href\\b)")
    void forbidden_propertyAssign() {
        var v = HrefConformanceTest.scan(List.of(
                "anchor.href = \"/somewhere\";"));
        assertTrue(v.size() >= 1, "got: " + v);
    }

    // =====================================================================
    // Forbidden — .href property read or write
    // =====================================================================

    @Test
    @DisplayName(".href property access — forbidden")
    void forbidden_propertyAccess() {
        var v = HrefConformanceTest.scan(List.of(
                "console.log(myAnchor.href);"));
        assertEquals(1, v.size());
    }

    // =====================================================================
    // Forbidden — "href" string (e.g. setAttribute)
    // =====================================================================

    @Test
    @DisplayName("setAttribute(\"href\", ...) — forbidden")
    void forbidden_setAttributeHref() {
        var v = HrefConformanceTest.scan(List.of(
                "el.setAttribute(\"href\", url);"));
        // Two patterns may both match — the "href" string AND the setAttribute pattern
        assertTrue(v.size() >= 1);
        assertTrue(v.stream().anyMatch(x -> x.pattern().contains("setAttribute")
                                          || x.pattern().contains("\"]href")), v.toString());
    }

    @Test
    @DisplayName("setAttribute('href', ...) with single quotes — forbidden")
    void forbidden_setAttributeHrefSingleQuoted() {
        var v = HrefConformanceTest.scan(List.of(
                "el.setAttribute('href', url);"));
        assertTrue(v.size() >= 1);
    }

    // =====================================================================
    // Forbidden — window.location.* and window.open
    // =====================================================================

    @Test
    @DisplayName("window.location.href = ... — forbidden")
    void forbidden_windowLocationHref() {
        var v = HrefConformanceTest.scan(List.of(
                "window.location.href = \"/somewhere\";"));
        // Multiple patterns will match: window.location AND .href AND href=
        assertTrue(v.size() >= 1);
    }

    @Test
    @DisplayName("window.location.assign(...) — forbidden")
    void forbidden_windowLocationAssign() {
        var v = HrefConformanceTest.scan(List.of(
                "window.location.assign(url);"));
        // pattern source contains literal `window\.location` — match by `window` substring
        assertTrue(v.stream().anyMatch(x -> x.pattern().contains("window")), v.toString());
    }

    @Test
    @DisplayName("window.location.replace(...) — forbidden")
    void forbidden_windowLocationReplace() {
        var v = HrefConformanceTest.scan(List.of(
                "window.location.replace(url);"));
        assertTrue(v.stream().anyMatch(x -> x.pattern().contains("window")), v.toString());
    }

    @Test
    @DisplayName("window.open(...) — forbidden")
    void forbidden_windowOpen() {
        var v = HrefConformanceTest.scan(List.of(
                "window.open(\"https://example.com\");"));
        assertTrue(v.stream().anyMatch(x -> x.pattern().contains("window")), v.toString());
    }

    // =====================================================================
    // Forbidden — even when the right-hand side comes from nav (the strict rule)
    // =====================================================================

    @Test
    @DisplayName("'href=' + nav.X() concatenation — STILL forbidden under the strict rule")
    void forbidden_concatenationWithNav() {
        var v = HrefConformanceTest.scan(List.of(
                "var a = '<a href=\"' + nav.PitchDeck() + '\">';"));
        assertEquals(1, v.size());
        // Migration: use href.toAttr(nav.PitchDeck()) instead.
    }

    @Test
    @DisplayName("template literal `href=\"${nav.X()}\"` — STILL forbidden")
    void forbidden_templateLiteralWithNav() {
        var v = HrefConformanceTest.scan(List.of(
                "var a = `<a href=\"${nav.PitchDeck()}\">`;"));
        assertEquals(1, v.size());
    }

    @Test
    @DisplayName("el.href = nav.X() assignment — STILL forbidden under the strict rule")
    void forbidden_assignmentFromNav() {
        var v = HrefConformanceTest.scan(List.of(
                "el.href = nav.PitchDeck();"));
        assertTrue(v.size() >= 1);
    }

    @Test
    @DisplayName("window.open(nav.X()) — STILL forbidden under the strict rule")
    void forbidden_openNewWithNav() {
        var v = HrefConformanceTest.scan(List.of(
                "window.open(nav.GitHubProxy({repo: \"x\"}));"));
        assertTrue(v.stream().anyMatch(x -> x.pattern().contains("window")), v.toString());
    }

    @Test
    @DisplayName("same-page fragment '<a href=\"#x\">' — STILL forbidden under the strict rule")
    void forbidden_fragmentLiteral() {
        var v = HrefConformanceTest.scan(List.of(
                "el.innerHTML = '<a href=\"#section-2\">';"));
        assertEquals(1, v.size());
        // Migration: use href.fragment("section-2") instead.
    }

    // =====================================================================
    // Allowed — clean modules with no href substring at all
    // =====================================================================

    @Test
    @DisplayName("clean module with no href usage — empty result")
    void allowed_noHrefAtAll() {
        var lines = List.of(
                "function appMain(rootEl) {",
                "  var div = document.createElement(\"div\");",
                "  css.setClass(div, btn);",
                "  rootEl.appendChild(div);",
                "}");
        assertEquals(List.of(), HrefConformanceTest.scan(lines));
    }

    // =====================================================================
    // Comment handling
    // =====================================================================

    @Test
    @DisplayName("// line comment containing 'href=' — ignored")
    void comment_lineComment() {
        var v = HrefConformanceTest.scan(List.of(
                "// example: <a href=\"foo\">"));
        assertEquals(List.of(), v);
    }

    @Test
    @DisplayName("/* block comment */ containing 'href=' — ignored")
    void comment_singleLineBlockComment() {
        var v = HrefConformanceTest.scan(List.of(
                "/* href=\"x\" */"));
        assertEquals(List.of(), v);
    }

    @Test
    @DisplayName("multi-line block comment — ignored, line numbers preserved")
    void comment_multiLineBlockComment() {
        var v = HrefConformanceTest.scan(List.of(
                "/*",
                " * Don't write href=\"foo\" — use href.toAttr().",
                " */",
                "var a = href.toAttr(nav.X());"));
        assertEquals(List.of(), v);
    }

    @Test
    @DisplayName("string literal containing '//' — does NOT begin a line comment")
    void comment_doubleSlashInsideString() {
        // Without proper string tracking, the // would be treated as a comment start
        // and the trailing href= would be missed. With tracking, the entire string
        // (including href=) is preserved and the violation is caught.
        var v = HrefConformanceTest.scan(List.of(
                "var msg = \"//\" + '<a href=\"x\">' + \"//\";"));
        assertEquals(1, v.size(), "expected 1 violation for the inner href=, got: " + v);
    }

    @Test
    @DisplayName("violation in real code AND inside comment — only real code reported")
    void comment_violationOnlyOutsideComment() {
        var v = HrefConformanceTest.scan(List.of(
                "// el.href = bad;     // this is documentation",
                "el.href = \"/real-violation\";"));
        // Line 1 has nothing (it's a comment). Line 2 has 2 matches (\bhref\s*= and \.href\b).
        assertTrue(v.size() >= 1, "expected at least 1 violation, got " + v);
        assertTrue(v.stream().allMatch(vv -> vv.lineNumber() == 2),
                "all violations should be on line 2, got " + v);
    }

    @Test
    @DisplayName("line numbers reported correctly across multi-line input")
    void lineNumbersAccurate() {
        var v = HrefConformanceTest.scan(List.of(
                "function x() {",
                "  var ok = href.toAttr(nav.X());",
                "  el.href = bad;",
                "  return ok;",
                "}"));
        // Line 3 matches both \bhref\s*= and \.href\b — 2 violations expected.
        assertTrue(v.size() >= 1, "got: " + v);
        assertTrue(v.stream().allMatch(vv -> vv.lineNumber() == 3),
                "all violations should be on line 3, got " + v);
    }

    // =====================================================================
    // Multiple violations in one scan
    // =====================================================================

    @Test
    @DisplayName("multiple distinct violations — all reported")
    void multipleViolations() {
        var v = HrefConformanceTest.scan(List.of(
                "el.href = \"/a\";",
                "window.open(\"/b\");",
                "el.setAttribute(\"href\", \"/c\");",
                "console.log(el.href);"));
        // Each line has at least one violation; some lines trigger multiple patterns.
        assertTrue(v.size() >= 4, "expected >= 4 violations, got " + v.size() + ": " + v);
    }

    // =====================================================================
    // Comment stripping helper — direct test
    // =====================================================================

    @Test
    @DisplayName("stripComments removes line comments but preserves line breaks")
    void stripComments_lineCommentRemoved() {
        var stripped = HrefConformanceTest.stripComments(List.of(
                "var a = 1; // comment",
                "var b = 2;"));
        assertEquals(2, stripped.size());
        assertTrue(stripped.get(0).startsWith("var a = 1;"));
        assertEquals("var b = 2;", stripped.get(1));
    }

    @Test
    @DisplayName("stripComments preserves string literals verbatim")
    void stripComments_stringPreserved() {
        var stripped = HrefConformanceTest.stripComments(List.of(
                "var s = \"// not a comment\";"));
        assertEquals(1, stripped.size());
        assertTrue(stripped.get(0).contains("\"// not a comment\""));
    }
}
