package hue.captains.singapura.js.homing.conformance;

import hue.captains.singapura.js.homing.core.BundledExternalModule;
import hue.captains.singapura.js.homing.core.EsModule;
import hue.captains.singapura.js.homing.core.util.ResourceReader;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

/**
 * Base class for "no CDN at runtime" conformance tests.
 *
 * <p>Single rule: <i>"no JS module's import statement may target an
 * {@code http://} or {@code https://} URL"</i>. Every 3rd-party library must
 * be served from the homing.js classpath via a {@link BundledExternalModule},
 * so the resulting application is fully self-contained — no runtime CDN call,
 * no breakage behind corporate proxy / firewall / air-gap.</p>
 *
 * <p>Subclasses provide the list of modules to scan (typically the same list
 * used by {@code CssConformanceTest} and {@code HrefConformanceTest}).</p>
 *
 * <p>{@code BundledExternalModule} instances are skipped automatically — their
 * "content" is the bundled library bytes, not a hand-written wrapper, and the
 * library's own internal imports (if any survived bundling) are not subject
 * to this rule.</p>
 *
 * <p>The legitimate exception — a 3rd-party library that genuinely cannot be
 * bundled (e.g., something runtime-resolved by a CDN-only worker) — is to use
 * {@link hue.captains.singapura.js.homing.core.ExternalModule} with a
 * hand-written wrapper JS file. Such cases require an explicit allow-list
 * entry with inline justification.</p>
 */
public abstract class CdnFreeConformanceTest {

    /** Matches {@code import ... from "https://..."} or {@code import("https://...")}. */
    private static final Pattern CDN_IMPORT = Pattern.compile(
            "(?:from|import)\\s*\\(?\\s*[\"']https?://"
    );

    public record Violation(int lineNumber, String line) {
        @Override public String toString() {
            return "  line " + lineNumber + ": " + line.strip();
        }
    }

    /** Modules whose JS resource files should be scanned. */
    protected abstract List<EsModule<?>> esModules();

    /**
     * Modules allowed to keep CDN imports in their wrapper JS file (rare).
     * Override only with strong justification — every entry should have an
     * inline comment explaining why the library can't be bundled.
     */
    protected java.util.Set<Class<? extends EsModule<?>>> allowList() {
        return java.util.Set.of();
    }

    @TestFactory
    Stream<DynamicTest> noCdnImportsInEsModules() {
        return esModules().stream()
                .filter(m -> !(m instanceof BundledExternalModule<?>))   // bundled libs are exempt — they ARE the library
                .filter(m -> !allowList().contains(m.getClass()))
                .map(m -> DynamicTest.dynamicTest(
                        m.getClass().getSimpleName() + " must have no CDN imports",
                        () -> assertNoCdnImports(m)
                ));
    }

    private void assertNoCdnImports(EsModule<?> module) {
        String basePath = "homing/js/" + module.getClass().getCanonicalName().replace(".", "/") + ".js";
        List<String> lines;
        try {
            lines = ResourceReader.INSTANCE.getStringsFromResource(basePath);
        } catch (RuntimeException e) {
            return; // no JS resource file — nothing to scan
        }

        List<Violation> violations = scan(lines);
        if (!violations.isEmpty()) {
            StringBuilder msg = new StringBuilder();
            msg.append(module.getClass().getSimpleName()).append(".js imports from a CDN.\n")
               .append("Wrap the library as a BundledExternalModule in homing-libs (or your project's libs module):\n")
               .append("  • declare sourceUrl(), resourcePath(), sha512()\n")
               .append("  • register in HomingLibsRegistry.ALL\n")
               .append("  • LibDownloader will fetch + verify + ship it on the classpath\n")
               .append("  • see homing-libs.MarkedJs for the canonical example\n")
               .append("Violations:\n");
            for (var v : violations) msg.append(v).append("\n");
            throw new AssertionError(msg.toString());
        }
    }

    /** Scan the given JS lines for CDN import statements. */
    public static List<Violation> scan(List<String> lines) {
        List<Violation> violations = new ArrayList<>();
        for (int i = 0; i < lines.size(); i++) {
            Matcher m = CDN_IMPORT.matcher(lines.get(i));
            if (m.find()) {
                violations.add(new Violation(i + 1, lines.get(i)));
            }
        }
        return violations;
    }
}
