package hue.captains.singapura.japjs.conformance;

import hue.captains.singapura.japjs.core.CssGroup;
import hue.captains.singapura.japjs.core.DomModule;
import hue.captains.singapura.japjs.core.util.ResourceReader;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;

import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Stream;

/**
 * Base class for CSS conformance tests.
 * <p>Scans DomModule JS files to ensure no raw CSS class operations
 * bypass the type-safe {@code css.*} API. Subclasses provide the
 * list of DomModules to check and an optional allowlist.</p>
 */
public abstract class CssConformanceTest {

    private static final List<Pattern> RAW_CSS_PATTERNS = List.of(
            Pattern.compile("\\.className\\s*="),
            Pattern.compile("\\.classList\\s*\\.\\s*add\\s*\\("),
            Pattern.compile("\\.classList\\s*\\.\\s*remove\\s*\\("),
            Pattern.compile("\\.classList\\s*\\.\\s*toggle\\s*\\("),
            Pattern.compile("\\.classList\\s*\\.\\s*replace\\s*\\("),
            Pattern.compile("\\.classList\\s*\\.\\s*contains\\s*\\(")
    );

    /**
     * All DomModule instances to scan.
     */
    protected abstract List<DomModule<?>> domModules();

    /**
     * Module classes that are allowed to use raw CSS operations.
     * Override to allowlist specific modules (e.g. shared cell factories
     * that receive class names as parameters).
     */
    protected Set<Class<? extends DomModule<?>>> allowList() {
        return Set.of();
    }

    @TestFactory
    Stream<DynamicTest> noRawCssOperationsInDomModulesWithCssImports() {
        return domModules().stream()
                .filter(m -> !allowList().contains(m.getClass()))
                .filter(m -> !m.cssGroups().isEmpty())
                .map(m -> DynamicTest.dynamicTest(
                        m.getClass().getSimpleName() + " must use css.* API",
                        () -> assertNoRawCssOperations(m)
                ));
    }

    private void assertNoRawCssOperations(DomModule<?> module) {
        String basePath = "japjs/js/" + module.getClass().getCanonicalName().replace(".", "/") + ".js";
        List<String> lines;
        try {
            lines = ResourceReader.INSTANCE.getStringsFromResource(basePath);
        } catch (RuntimeException e) {
            return; // no JS resource — nothing to check (e.g. generated-only modules)
        }

        var violations = new java.util.ArrayList<String>();
        for (int i = 0; i < lines.size(); i++) {
            String line = lines.get(i);
            for (Pattern p : RAW_CSS_PATTERNS) {
                if (p.matcher(line).find()) {
                    violations.add("  line " + (i + 1) + ": " + line.strip() + "  [" + p.pattern() + "]");
                }
            }
        }

        if (!violations.isEmpty()) {
            throw new AssertionError(
                    module.getClass().getSimpleName() + ".js contains raw CSS operations. "
                    + "Use css.setClass(), css.addClass(), css.removeClass(), css.toggleClass() instead.\n"
                    + String.join("\n", violations)
            );
        }
    }
}
