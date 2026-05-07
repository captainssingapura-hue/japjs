package hue.captains.singapura.js.homing.server;

import hue.captains.singapura.js.homing.core.*;
import hue.captains.singapura.js.homing.core.util.CssClassName;

import java.util.ArrayList;
import java.util.List;

/**
 * Auto-generates JS module content for a {@link CssGroup}.
 *
 * <p>Mirrors {@link hue.captains.singapura.js.homing.core.util.SvgGroupContentProvider}:
 * produces a header-only module that imports {@link CssClassManager},
 * loads the CSS file, and exports per-class JS handles.</p>
 *
 * <p>RFC 0002-ext1: per-class handles are emitted via {@code _css.cls(...)} which
 * returns either a {@code CssClass} or a {@code CssUtility} ES6 instance. For each
 * base, the {@link CssClass#variants()} set drives the variant map: each declared
 * state {@code s} contributes a {@code s: "<s>-<kebab>"} entry, which becomes a
 * dedicated variant property on the JS-side handle ({@code base.hover},
 * {@code base.focus}, etc.). No separate variant records are required — the
 * framework synthesizes everything from the base + its declared states.</p>
 */
public record CssGroupContentProvider<C extends CssGroup<C>>(
        C cssGroup, String theme, ModuleNameResolver nameResolver
) implements ContentProvider<C> {

    @Override
    public List<String> content() {
        List<String> lines = new ArrayList<>();
        String managerPath = nameResolver.resolve(CssClassManager.INSTANCE).basePath();
        String groupName = cssGroup.getClass().getCanonicalName();
        String themeArg = theme != null ? ", \"" + theme + "\"" : "";

        lines.add("import { CssClassManagerInstance as _css } from \"" + managerPath + "\";");
        lines.add("await _css.loadCss(\"" + groupName + "\"" + themeArg + ");");

        for (CssClass<C> cls : cssGroup.cssClasses()) {
            String recordName = cls.getClass().getSimpleName();
            String cssName = CssClassName.toCssName(cls.getClass());
            var variants = cls.variants();
            if (variants.isEmpty()) {
                lines.add("const " + recordName + " = _css.cls(\"" + cssName + "\");");
            } else {
                StringBuilder sb = new StringBuilder();
                sb.append("const ").append(recordName).append(" = _css.cls(\"")
                  .append(cssName).append("\", {");
                boolean first = true;
                for (String state : variants) {
                    if (!first) sb.append(",");
                    first = false;
                    sb.append(" ").append(state).append(": \"")
                      .append(state).append("-").append(cssName).append("\"");
                }
                sb.append(" });");
                lines.add(sb.toString());
            }
        }
        return lines;
    }
}
