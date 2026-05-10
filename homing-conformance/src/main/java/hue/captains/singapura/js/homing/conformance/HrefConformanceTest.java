package hue.captains.singapura.js.homing.conformance;

import hue.captains.singapura.js.homing.core.DomModule;
import hue.captains.singapura.js.homing.core.util.ResourceReader;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Stream;

/**
 * Base class for href conformance tests (RFC 0001 §6.2, Amendment 3).
 *
 * <p>Enforces the single rule: <i>"the literal substring {@code href} may
 * appear in DomModule JS only as the manager identifier — i.e., immediately
 * followed by {@code .} and a recognised manager method"</i>.</p>
 *
 * <p>Comments ({@code //...} line, {@code /* ... *}{@code /} block) are stripped
 * before scanning. Strings are preserved (an HTML literal {@code '<a href="..."'}
 * inside a JS string is exactly the kind of forbidden pattern this scanner
 * targets).</p>
 *
 * <p>Subclasses provide the list of DomModules to scan. Override
 * {@link #allowList()} only with strong justification.</p>
 *
 * <p>Forbidden patterns and the one allowed pattern are documented in the
 * static field initializers below.</p>
 */
public abstract class HrefConformanceTest {

    /**
     * The forbidden patterns. Any match in user JS is a violation.
     * Each pattern's javadoc explains <i>why</i> it's forbidden — every
     * legitimate use case has a corresponding {@code href.*} method.
     */
    private static final List<Pattern> FORBIDDEN = List.of(
            // Literal href= attribute or property assignment.  Use href.toAttr() or href.set().
            Pattern.compile("\\bhref\\s*="),
            // Property access on an element. Use href.set() / href.create() / href.openNew() / href.navigate().
            Pattern.compile("\\.href\\b"),
            // The string "href" — typically setAttribute("href", ...).  Use href.set().
            Pattern.compile("[\"']href[\"']"),
            // Any window.location read or write.  Use href.navigate() — it covers assign + replace.
            Pattern.compile("\\bwindow\\.location\\b"),
            // window.open call.  Use href.openNew().
            Pattern.compile("\\bwindow\\.open\\s*\\("),
            // setAttribute with href first arg — belt-and-braces in case earlier patterns miss.  Use href.set().
            Pattern.compile("setAttribute\\s*\\(\\s*[\"']href")
    );

    /** A single scan result. */
    public record Violation(int lineNumber, String line, String pattern) {
        @Override public String toString() {
            return "  line " + lineNumber + ": " + line.strip() + "    [matched: " + pattern + "]";
        }
    }

    /** All DomModule instances to scan. */
    protected abstract List<DomModule<?>> domModules();

    /**
     * Module classes allowed to use raw href operations.
     * Override to allowlist specific modules — each entry should carry
     * an inline justification comment in the override.
     */
    protected Set<Class<? extends DomModule<?>>> allowList() {
        return Set.of();
    }

    @TestFactory
    Stream<DynamicTest> noRawHrefOperationsInDomModules() {
        return domModules().stream()
                .filter(m -> !allowList().contains(m.getClass()))
                .map(m -> DynamicTest.dynamicTest(
                        m.getClass().getSimpleName() + " must use href.* API",
                        () -> assertNoRawHrefOperations(m)
                ));
    }

    private void assertNoRawHrefOperations(DomModule<?> module) {
        String basePath = "homing/js/" + module.getClass().getCanonicalName().replace(".", "/") + ".js";
        List<String> lines;
        try {
            lines = ResourceReader.INSTANCE.getStringsFromResource(basePath);
        } catch (RuntimeException e) {
            return; // no JS resource — nothing to scan
        }

        List<Violation> violations = scan(lines);
        if (!violations.isEmpty()) {
            StringBuilder msg = new StringBuilder();
            msg.append(module.getClass().getSimpleName()).append(".js contains raw href operations.\n")
               .append("Use the injected `href` manager (RFC 0001 Step 09):\n")
               .append("  • href.toAttr(link)        — for innerHTML attribute fragments\n")
               .append("  • href.set(el, link)       — to set element href\n")
               .append("  • href.create(link, opts)  — to create <a> element\n")
               .append("  • href.openNew(link)       — to window.open in new tab\n")
               .append("  • href.navigate(link)      — for programmatic navigation\n")
               .append("  • href.fragment(slug)      — for same-page anchors\n")
               .append("Violations:\n");
            for (var v : violations) msg.append(v).append("\n");
            throw new AssertionError(msg.toString());
        }
    }

    // -----------------------------------------------------------------------
    // Public scanner — testable in isolation
    // -----------------------------------------------------------------------

    /** Scan the given JS lines for raw href operations. Comments are stripped first. */
    public static List<Violation> scan(List<String> lines) {
        List<String> stripped = stripComments(lines);
        List<Violation> violations = new ArrayList<>();
        for (int i = 0; i < stripped.size(); i++) {
            String line = stripped.get(i);
            for (Pattern p : FORBIDDEN) {
                if (p.matcher(line).find()) {
                    violations.add(new Violation(i + 1, lines.get(i), p.pattern()));
                }
            }
        }
        return violations;
    }

    /**
     * Strip {@code //} line comments and {@code /} … {@code /} block comments.
     * String literals are preserved (their contents are still scanned). Newlines
     * inside block comments are preserved so reported line numbers stay accurate.
     */
    public static List<String> stripComments(List<String> lines) {
        String joined = String.join("\n", lines);
        StringBuilder out = new StringBuilder(joined.length());

        boolean inLineComment = false;
        boolean inBlockComment = false;
        boolean inString = false;
        char stringChar = 0;

        int i = 0;
        int n = joined.length();
        while (i < n) {
            char c = joined.charAt(i);
            char next = (i + 1 < n) ? joined.charAt(i + 1) : 0;

            if (inLineComment) {
                if (c == '\n') { inLineComment = false; out.append(c); }
                i++;
                continue;
            }
            if (inBlockComment) {
                if (c == '*' && next == '/') { inBlockComment = false; i += 2; continue; }
                if (c == '\n') out.append(c);   // preserve line numbers
                i++;
                continue;
            }
            if (inString) {
                if (c == '\\' && i + 1 < n) {
                    out.append(c).append(next);
                    i += 2;
                    continue;
                }
                if (c == stringChar) {
                    inString = false;
                }
                out.append(c);
                i++;
                continue;
            }
            // Not in any special state
            if (c == '/' && next == '/') { inLineComment = true; i += 2; continue; }
            if (c == '/' && next == '*') { inBlockComment = true; i += 2; continue; }
            if (c == '"' || c == '\'' || c == '`') {
                inString = true;
                stringChar = c;
                out.append(c);
                i++;
                continue;
            }
            out.append(c);
            i++;
        }

        return Arrays.asList(out.toString().split("\n", -1));
    }
}
