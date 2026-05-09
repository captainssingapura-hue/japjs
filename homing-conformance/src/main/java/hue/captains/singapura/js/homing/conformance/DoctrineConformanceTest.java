package hue.captains.singapura.js.homing.conformance;

import hue.captains.singapura.js.homing.core.EsModule;
import hue.captains.singapura.js.homing.core.util.ResourceReader;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

/**
 * Base class for view-doctrine conformance tests. Enforces the two
 * <b>universal</b> view doctrines on consumer JS:
 *
 * <ul>
 *   <li>{@code Pure-Component Views} — no HTML tag literals
 *       ({@code '<div>}, {@code "<a href=}, …); no {@code innerHTML} /
 *       {@code outerHTML} writes (except clearing to {@code ""}).</li>
 *   <li>{@code Owned References} — no DOM lookups
 *       ({@code document.getElementById}, {@code .querySelector},
 *       {@code .querySelectorAll}).</li>
 * </ul>
 *
 * <p>The third view doctrine, {@code Managed DOM Ops}, is <b>not</b> enforced
 * here because it is SPA-scoped (see
 * {@code docs/doctrines/managed-dom-ops.md}); imperative / animation /
 * game-loop modules use the DOM API directly, and the framework has no
 * portable signal yet for "this module is SPA-shaped." When the
 * {@code DomOpsParty} primitive lands and SPA modules carry a marker, this
 * test will gain a third pattern set scoped to those modules only.</p>
 *
 * <p>The fourth doctrine, {@code Methods Over Props}, is structural and not
 * statically detectable.</p>
 *
 * <p>Subclasses provide the list of modules to scan and an optional
 * allowlist. Comments (lines beginning with {@code //} or {@code *}) are
 * skipped so doctrine references in JSDoc and commentary don't false-trigger.</p>
 */
public abstract class DoctrineConformanceTest {

    /** {@code 'X} or {@code "X} where X is an HTML tag start character — bans
     *  HTML string authoring of any kind in consumer code. */
    private static final List<Pattern> HTML_LITERAL_PATTERNS = List.of(
            Pattern.compile("['\"]<[a-zA-Z!]")
    );

    /** {@code .innerHTML = …} except when assigning an empty string ({@code = ""} or {@code = ''}),
     *  which is the doctrine-permitted way to clear a node's content. */
    private static final List<Pattern> INNER_HTML_WRITE_PATTERNS = List.of(
            Pattern.compile("\\.innerHTML\\s*=\\s*(?![\"']\\s*[\"'])"),
            Pattern.compile("\\.outerHTML\\s*=\\s*(?![\"']\\s*[\"'])")
    );

    /** All forms of stringly-typed DOM lookup — banned by Owned References. */
    private static final List<Pattern> LOOKUP_PATTERNS = List.of(
            Pattern.compile("document\\s*\\.\\s*getElementById\\s*\\("),
            Pattern.compile("\\.querySelector\\s*\\("),
            Pattern.compile("\\.querySelectorAll\\s*\\(")
    );

    /** All EsModule instances to scan. */
    protected abstract List<EsModule<?>> esModules();

    /**
     * Modules exempt from the doctrine. Override sparingly — typical reasons
     * are framework-internal helpers that install typed assets, or modules
     * still pending migration with a tracking ticket linked in commentary.
     */
    protected Set<Class<? extends EsModule<?>>> allowList() {
        return Set.of();
    }

    @TestFactory
    Stream<DynamicTest> compliesWithUniversalViewDoctrines() {
        return esModules().stream()
                .filter(m -> !allowList().contains(m.getClass()))
                .map(m -> DynamicTest.dynamicTest(
                        m.getClass().getSimpleName() + " complies with view doctrines",
                        () -> assertCompliance(m)
                ));
    }

    private void assertCompliance(EsModule<?> module) {
        String basePath = "homing/js/" + module.getClass().getCanonicalName().replace(".", "/") + ".js";
        List<String> lines;
        try {
            lines = ResourceReader.INSTANCE.getStringsFromResource(basePath);
        } catch (RuntimeException e) {
            return; // no JS resource — generated-only or data-only module; nothing to check
        }

        List<String> violations = new ArrayList<>();
        for (int i = 0; i < lines.size(); i++) {
            String line   = lines.get(i);
            String trimmed = line.strip();
            // Skip JS line and JSDoc comment lines so doctrine references in
            // commentary don't false-trigger. Multi-line block comments are
            // not perfectly handled but are rare in our codebase.
            if (trimmed.startsWith("//") || trimmed.startsWith("*")) continue;

            check(line, i, HTML_LITERAL_PATTERNS,    "Pure-Component Views — HTML tag literal",        violations);
            check(line, i, INNER_HTML_WRITE_PATTERNS, "Pure-Component Views — innerHTML/outerHTML write", violations);
            check(line, i, LOOKUP_PATTERNS,          "Owned References — DOM lookup",                   violations);
        }

        if (!violations.isEmpty()) {
            throw new AssertionError(
                    module.getClass().getSimpleName() + ".js violates view doctrines:\n"
                  + String.join("\n", violations)
                  + "\n\nSee docs/doctrines/pure-component-views.md and"
                  + "\n    docs/doctrines/owned-references.md"
                  + "\nfor the rules, the rationale, and the permitted alternatives."
            );
        }
    }

    private void check(String line, int lineIndex, List<Pattern> patterns, String label, List<String> sink) {
        for (Pattern p : patterns) {
            Matcher m = p.matcher(line);
            if (m.find()) {
                sink.add("  line " + (lineIndex + 1) + " [" + label + "]: " + line.strip());
            }
        }
    }
}
