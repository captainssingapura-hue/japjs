package hue.captains.singapura.js.homing.conformance;

import hue.captains.singapura.js.homing.core.AppLink;
import hue.captains.singapura.js.homing.core.DomModule;
import hue.captains.singapura.js.homing.core.EsModule;
import hue.captains.singapura.js.homing.core.ManagerInjector;
import hue.captains.singapura.js.homing.core.util.ResourceReader;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

/**
 * Conformance — no manual redeclaration of framework auto-injected identifiers.
 *
 * <p>For each {@link EsModule}, the framework prepends one
 * {@code import { <export> as <bind> } from "<manager>"} line per
 * auto-injected manager:</p>
 * <ul>
 *   <li>{@code css}  — when the module is a {@link DomModule} with a non-empty
 *       {@code cssGroups()}</li>
 *   <li>{@code href} — when the module imports any {@link AppLink}</li>
 *   <li>{@code <bind>} — for each {@link ManagerInjector} reachable through the
 *       module's direct imports (e.g. {@code docs} from a {@code DocGroup}
 *       injector)</li>
 * </ul>
 *
 * <p>If the module's {@code .js} body <i>also</i> declares
 * {@code var/let/const <bind> = …}, the concatenated module fails to parse with
 * <i>"Identifier '&lt;bind&gt;' has already been declared"</i> at first
 * navigation — a silent, browser-only failure that ships green from CI.</p>
 *
 * <p>This test pins the rule. Subclasses provide the list of root modules; the
 * scanner walks each root's import graph transitively, so listing top-level
 * AppModules automatically covers their renderers and shared element modules.</p>
 *
 * <p>The set of auto-injected identifiers is derived from the framework's own
 * injection logic (see {@code EsModuleGetAction.createWriter}) — no hardcoded
 * name list. New {@code ManagerInjector} sources are picked up automatically.</p>
 */
public abstract class ManagerInjectionConformanceTest {

    /** Roots to scan. The test walks each root's import graph transitively. */
    protected abstract List<EsModule<?>> esModules();

    /** Modules permitted to redeclare an auto-injected identifier. Override only with strong justification. */
    protected Set<Class<? extends EsModule<?>>> allowList() {
        return Set.of();
    }

    @TestFactory
    Stream<DynamicTest> noRedeclarationOfAutoInjectedIdentifiers() {
        Set<EsModule<?>> all = new LinkedHashSet<>();
        for (EsModule<?> root : esModules()) collectReachable(root, all);
        return all.stream()
                .filter(m -> !allowList().contains(m.getClass()))
                .map(m -> DynamicTest.dynamicTest(
                        m.getClass().getSimpleName() + " must not redeclare auto-injected identifiers",
                        () -> assertNoRedeclarations(m)
                ));
    }

    private void assertNoRedeclarations(EsModule<?> module) {
        Map<String, String> injected = autoInjectedBindings(module);
        if (injected.isEmpty()) return;

        String basePath = "homing/js/" + module.getClass().getCanonicalName().replace(".", "/") + ".js";
        List<String> lines;
        try {
            lines = ResourceReader.INSTANCE.getStringsFromResource(basePath);
        } catch (RuntimeException e) {
            return; // no JS resource — nothing to scan (e.g. CssGroup-only or generated modules)
        }

        List<String> stripped = HrefConformanceTest.stripComments(lines);
        List<String> violations = new ArrayList<>();
        for (var entry : injected.entrySet()) {
            String name = entry.getKey();
            // \b(var|let|const)\s+<name>(?![\w$])\s*=
            Pattern p = Pattern.compile(
                    "\\b(var|let|const)\\s+" + Pattern.quote(name) + "(?![\\w$])\\s*=");
            for (int i = 0; i < stripped.size(); i++) {
                Matcher m = p.matcher(stripped.get(i));
                if (m.find()) {
                    violations.add("  line " + (i + 1) + ": " + lines.get(i).strip()
                            + "    [identifier `" + name + "` is " + entry.getValue() + "]");
                }
            }
        }

        if (!violations.isEmpty()) {
            throw new AssertionError(
                    module.getClass().getSimpleName() + ".js redeclares framework auto-injected identifier(s).\n"
                    + "The framework prepends `import { … as <name> }` to your JS body automatically;\n"
                    + "redeclaring with `var`/`let`/`const <name> = …` produces a SyntaxError\n"
                    + "(\"Identifier '<name>' has already been declared\") at first navigation.\n"
                    + "Either remove the manual declaration, or remove the import that triggers injection.\n"
                    + "Violations:\n"
                    + String.join("\n", violations)
            );
        }
    }

    // -----------------------------------------------------------------------
    // Public helpers — testable in isolation
    // -----------------------------------------------------------------------

    /**
     * Names the framework auto-injects into the given module's JS body, mapped
     * to a short reason. Mirrors the logic in {@code EsModuleGetAction.createWriter}.
     */
    public static Map<String, String> autoInjectedBindings(EsModule<?> module) {
        Map<String, String> bindings = new LinkedHashMap<>();

        if (module instanceof DomModule<?> dom && !dom.cssGroups().isEmpty()) {
            bindings.put("css", "auto-injected because this is a DomModule with non-empty cssGroups()");
        }

        if (importsAnyAppLink(module)) {
            bindings.put("href", "auto-injected because this module imports an AppLink");
        }

        for (var key : module.imports().getAllImports().keySet()) {
            if (key instanceof ManagerInjector mi) {
                bindings.putIfAbsent(mi.managerBindName(),
                        "auto-injected by ManagerInjector " + key.getClass().getSimpleName()
                                + " (imports " + mi.managerExportName() + " as " + mi.managerBindName() + ")");
            }
        }

        return bindings;
    }

    /** True iff {@code module}'s direct imports contain any {@link AppLink}. */
    public static boolean importsAnyAppLink(EsModule<?> module) {
        return module.imports().getAllImports().values().stream()
                .anyMatch(mi -> mi.allImports().stream().anyMatch(e -> e instanceof AppLink<?>));
    }

    /** BFS — collect every EsModule reachable from {@code root} through the import graph. */
    public static void collectReachable(EsModule<?> root, Set<EsModule<?>> sink) {
        Deque<EsModule<?>> queue = new ArrayDeque<>();
        queue.add(root);
        while (!queue.isEmpty()) {
            EsModule<?> m = queue.poll();
            if (!sink.add(m)) continue;
            for (var key : m.imports().getAllImports().keySet()) {
                if (key instanceof EsModule<?> em) queue.add(em);
            }
        }
    }
}
