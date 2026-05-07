package hue.captains.singapura.js.homing.server;

import hue.captains.singapura.js.homing.core.CssBlock;
import hue.captains.singapura.js.homing.core.CssClass;
import hue.captains.singapura.js.homing.core.CssGroup;
import hue.captains.singapura.js.homing.core.CssGroupImpl;
import hue.captains.singapura.js.homing.core.CssImportsFor;
import hue.captains.singapura.js.homing.core.Theme;
import hue.captains.singapura.js.homing.core.UtilityCssClass;
import org.junit.jupiter.api.Test;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * RFC 0002-ext1 Phase 01 (revised) — exercises {@code pseudoState()},
 * {@code variants()} auto-generation, and the two-layer token emission.
 *
 * <p>Test fixtures defined inline so the assertions are decoupled from any
 * production CssGroup.</p>
 */
class CssContentGetActionTest {

    record TestTheme() implements Theme {
        static final TestTheme INSTANCE = new TestTheme();
        @Override public String slug() { return "test"; }
    }

    public record TestStyles() implements CssGroup<TestStyles> {
        public static final TestStyles INSTANCE = new TestStyles();

        // Bare class — no pseudo-state, no variants.
        public record bare_label() implements CssClass<TestStyles> {}

        // Plain pseudo-state — no variants, just a :hover suffix on its own rule.
        public record bare_link_hover() implements CssClass<TestStyles> {
            @Override public String pseudoState() { return ":hover"; }
        }

        // Utility base with explicit variant set (only hover + focus, NOT active).
        public record bg_accent() implements CssClass<TestStyles> {
            @Override public Set<String> variants() { return Set.of("hover", "focus"); }
        }

        // Utility base via the convenience marker — gets all three states.
        public record p_4() implements UtilityCssClass<TestStyles> {}

        // RFC 0002-ext1 Phase 05: inline-bodied class. The body lives ON the
        // record; the impl has NO method for it. Variants still auto-synthesize
        // and reuse the inline body.
        public record inline_bodied() implements CssClass<TestStyles> {
            @Override public String body() { return "color: limegreen;"; }
            @Override public Set<String> variants() { return Set.of("hover"); }
        }

        public interface Impl<TH extends Theme> extends CssGroupImpl<TestStyles, TH> {
            @Override default TestStyles group() { return INSTANCE; }
            CssBlock<bare_label>      bare_label();
            CssBlock<bare_link_hover> bare_link_hover();
            CssBlock<bg_accent>       bg_accent();
            CssBlock<p_4>             p_4();
            // NOTE: no method for inline_bodied — the renderer uses cls.body() directly.
        }

        @Override public List<CssClass<TestStyles>> cssClasses() {
            return List.of(new bare_label(), new bare_link_hover(), new bg_accent(), new p_4(), new inline_bodied());
        }

        @Override public CssImportsFor<TestStyles> cssImports() {
            return CssImportsFor.none(this);
        }
    }

    public record TestStylesTheme() implements TestStyles.Impl<TestTheme> {
        public static final TestStylesTheme INSTANCE = new TestStylesTheme();
        @Override public TestTheme theme() { return TestTheme.INSTANCE; }

        @Override public Map<String, String> cssVariables() {
            var m = new LinkedHashMap<String, String>();
            m.put("--prim-amber", "#F4B942");
            m.put("--prim-navy",  "#1E2761");
            return m;
        }

        @Override public Map<String, String> semanticTokens() {
            var m = new LinkedHashMap<String, String>();
            m.put("--color-accent", "var(--prim-amber)");
            m.put("--color-link",   "var(--prim-navy)");
            return m;
        }

        @Override public CssBlock<TestStyles.bare_label>      bare_label()      { return CssBlock.of("font-weight: 700;"); }
        @Override public CssBlock<TestStyles.bare_link_hover> bare_link_hover() { return CssBlock.of("color: red;"); }
        @Override public CssBlock<TestStyles.bg_accent>       bg_accent()       { return CssBlock.of("background: amber;"); }
        @Override public CssBlock<TestStyles.p_4>             p_4()             { return CssBlock.of("padding: 16px;"); }
    }

    private final CssContentGetAction action = new CssContentGetAction(
            List.of(TestStylesTheme.INSTANCE),
            TestTheme.INSTANCE
    );

    @Test
    void renders_bareCssClass_asPlainSelector() throws Exception {
        var query = new ModuleQuery(TestStyles.class.getName(), null, null);
        var css   = action.execute(query, new EmptyParam.NoHeaders()).get().body();

        assertTrue(css.contains(".bare-label {"),    "bare class. css:\n" + css);
        assertTrue(css.contains("font-weight: 700;"), "body emitted. css:\n" + css);
    }

    @Test
    void renders_pseudoState_appendsToSelector() throws Exception {
        var query = new ModuleQuery(TestStyles.class.getName(), null, null);
        var css   = action.execute(query, new EmptyParam.NoHeaders()).get().body();

        assertTrue(css.contains(".bare-link-hover:hover {"),
                "pseudoState() appends ':hover'. css:\n" + css);
        assertFalse(css.contains(".bare-link-hover {\n"),
                "no bare selector when pseudoState set. css:\n" + css);
    }

    @Test
    void renders_primitivesAndSemanticTokens_inOneRootBlock_primitivesFirst() throws Exception {
        var query = new ModuleQuery(TestStyles.class.getName(), null, null);
        var css   = action.execute(query, new EmptyParam.NoHeaders()).get().body();

        int rootStart = css.indexOf(":root {");
        int rootEnd   = css.indexOf("}", rootStart);
        assertTrue(rootStart >= 0, "must emit :root. css:\n" + css);
        String rootBody = css.substring(rootStart, rootEnd);

        assertTrue(rootBody.contains("--prim-amber: #F4B942;"),                "primitive emitted");
        assertTrue(rootBody.contains("--color-accent: var(--prim-amber);"),    "semantic emitted");

        int primIdx = rootBody.indexOf("--prim-amber");
        int semIdx  = rootBody.indexOf("--color-accent");
        assertTrue(primIdx < semIdx, "primitives precede semantic tokens");

        int secondRoot = css.indexOf(":root {", rootStart + 1);
        assertTrue(secondRoot < 0 || secondRoot > rootEnd,
                "primitives + semantic share one :root block");
    }

    @Test
    void renders_variants_autoGeneratedFromBase_withSameBody() throws Exception {
        var query = new ModuleQuery(TestStyles.class.getName(), null, null);
        var css   = action.execute(query, new EmptyParam.NoHeaders()).get().body();

        // Base utility rule.
        assertTrue(css.contains(".bg-accent {"), "base rule. css:\n" + css);

        // Auto-generated variants — only hover + focus (per the base's variants()),
        // both with the SAME body as the base.
        assertTrue(css.contains(".hover-bg-accent:hover {"), "hover variant rule. css:\n" + css);
        assertTrue(css.contains(".focus-bg-accent:focus {"), "focus variant rule. css:\n" + css);
        assertFalse(css.contains(".active-bg-accent:active {"),
                "active NOT in bg_accent.variants() — must not be emitted. css:\n" + css);

        // Each variant rule reuses the base body.
        int hoverStart = css.indexOf(".hover-bg-accent:hover {");
        int hoverEnd   = css.indexOf("}", hoverStart);
        assertTrue(css.substring(hoverStart, hoverEnd).contains("background: amber;"),
                "variant body must equal base body");
    }

    @Test
    void renders_utilityCssClass_marker_givesAllThreeStates() throws Exception {
        var query = new ModuleQuery(TestStyles.class.getName(), null, null);
        var css   = action.execute(query, new EmptyParam.NoHeaders()).get().body();

        // p_4 implements UtilityCssClass — all three default states should appear.
        assertTrue(css.contains(".hover-p-4:hover {"),   "marker: hover. css:\n" + css);
        assertTrue(css.contains(".focus-p-4:focus {"),   "marker: focus. css:\n" + css);
        assertTrue(css.contains(".active-p-4:active {"), "marker: active. css:\n" + css);
    }

    @Test
    void renders_inlineBodied_withoutImplMethod() throws Exception {
        var query = new ModuleQuery(TestStyles.class.getName(), null, null);
        var css   = action.execute(query, new EmptyParam.NoHeaders()).get().body();

        // inline_bodied has no impl method — the renderer must use cls.body() directly.
        assertTrue(css.contains(".inline-bodied {"),
                "inline-bodied class rule must be emitted. css:\n" + css);
        assertTrue(css.contains("color: limegreen;"),
                "inline body must be used. css:\n" + css);
        // The renderer must not emit a "no method" error comment for this class.
        assertFalse(css.contains("/* render error: no method inline_bodied"),
                "must not fall through to impl-method dispatch. css:\n" + css);
    }

    @Test
    void renders_inlineBodied_variantsReuseInlineBody() throws Exception {
        var query = new ModuleQuery(TestStyles.class.getName(), null, null);
        var css   = action.execute(query, new EmptyParam.NoHeaders()).get().body();

        // inline_bodied declares variants() = {"hover"}. The hover variant
        // must auto-synthesize and reuse the inline body.
        assertTrue(css.contains(".hover-inline-bodied:hover {"),
                "hover variant of inline-bodied must auto-synthesize. css:\n" + css);

        int hoverStart = css.indexOf(".hover-inline-bodied:hover {");
        int hoverEnd   = css.indexOf("}", hoverStart);
        String hoverRule = css.substring(hoverStart, hoverEnd);
        assertTrue(hoverRule.contains("color: limegreen;"),
                "variant rule must reuse the inline body. variant rule:\n" + hoverRule);
    }
}
