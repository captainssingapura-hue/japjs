package hue.captains.singapura.js.homing.server;

import hue.captains.singapura.js.homing.core.*;
import hue.captains.singapura.js.homing.core.util.CssClassName;

import java.util.ArrayList;
import java.util.List;

/**
 * Auto-generates JS module content for a {@link CssGroup}.
 * <p>Mirrors {@link hue.captains.singapura.js.homing.core.util.SvgGroupContentProvider}:
 * produces a header-only module that imports {@link CssClassManager},
 * loads the CSS file, and exports frozen CssClass objects.</p>
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
            lines.add("const " + recordName + " = _css.cls(\"" + cssName + "\");");
        }
        return lines;
    }
}
