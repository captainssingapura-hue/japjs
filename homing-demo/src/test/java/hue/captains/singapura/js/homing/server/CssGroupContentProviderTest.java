package hue.captains.singapura.js.homing.server;

import hue.captains.singapura.js.homing.core.CssClass;
import hue.captains.singapura.js.homing.core.CssGroup;
import hue.captains.singapura.js.homing.core.CssImportsFor;
import hue.captains.singapura.js.homing.core.UtilityCssClass;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * RFC 0002-ext1 Phase 03 (revised) — exercises the JS-side ES6 emission
 * for CssGroups, including the auto-generated variant-as-property shape
 * driven by {@link CssClass#variants()}.
 */
class CssGroupContentProviderTest {

    public record FixtureStyles() implements CssGroup<FixtureStyles> {
        public static final FixtureStyles INSTANCE = new FixtureStyles();

        // Plain class — no variants.
        public record fix_root() implements CssClass<FixtureStyles> {}

        // Utility base — explicit subset of states (hover + focus, NOT active).
        public record bg_accent() implements CssClass<FixtureStyles> {
            @Override public Set<String> variants() { return Set.of("hover", "focus"); }
        }

        // Utility base via the convenience marker — gets all three states.
        public record p_4() implements UtilityCssClass<FixtureStyles> {}

        @Override public List<CssClass<FixtureStyles>> cssClasses() {
            return List.of(new fix_root(), new bg_accent(), new p_4());
        }

        @Override public CssImportsFor<FixtureStyles> cssImports() {
            return CssImportsFor.none(this);
        }
    }

    private final CssGroupContentProvider<FixtureStyles> provider =
            new CssGroupContentProvider<>(FixtureStyles.INSTANCE, null, new QueryParamResolver());

    @Test
    void emits_plainCssClass_asBareCls() {
        String js = String.join("\n", provider.content());
        assertTrue(js.contains("const fix_root = _css.cls(\"fix-root\");"),
                "plain class — bare cls(). js:\n" + js);
    }

    @Test
    void emits_baseWithExplicitVariants_includesEachStateInMap() {
        String js = String.join("\n", provider.content());

        // bg_accent declares hover + focus (NOT active). Variant kebab is
        // <state>-<base> per framework convention.
        assertTrue(js.contains("hover: \"hover-bg-accent\""), "hover entry. js:\n" + js);
        assertTrue(js.contains("focus: \"focus-bg-accent\""), "focus entry. js:\n" + js);
        assertFalse(js.contains("active: \"active-bg-accent\""),
                "active not declared — must not appear. js:\n" + js);
    }

    @Test
    void emits_utilityCssClass_marker_includesAllThreeStates() {
        String js = String.join("\n", provider.content());

        assertTrue(js.contains("hover: \"hover-p-4\""),   "marker: hover entry. js:\n" + js);
        assertTrue(js.contains("focus: \"focus-p-4\""),   "marker: focus entry. js:\n" + js);
        assertTrue(js.contains("active: \"active-p-4\""), "marker: active entry. js:\n" + js);
    }

    @Test
    void preservesBootstrap_loadCssAndManagerImport() {
        String js = String.join("\n", provider.content());

        assertTrue(js.contains("import { CssClassManagerInstance as _css }"));
        assertTrue(js.contains("await _css.loadCss(\"" + FixtureStyles.class.getCanonicalName() + "\");"));
    }
}
