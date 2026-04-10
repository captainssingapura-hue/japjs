package hue.captains.singapura.japjs.server;

import hue.captains.singapura.japjs.core.*;
import hue.captains.singapura.japjs.core.util.CssClassName;

import java.util.ArrayList;
import java.util.List;

/**
 * Auto-generates JS module content for a {@link CssBeing}.
 * <p>Mirrors {@link hue.captains.singapura.japjs.core.util.SvgGroupContentProvider}:
 * produces a header-only module that imports {@link CssClassManager},
 * loads the CSS file, and exports frozen CssClass objects.</p>
 */
public record CssBeingContentProvider<C extends CssBeing<C>>(
        C cssBeing, String theme, ModuleNameResolver nameResolver
) implements ContentProvider<C> {

    @Override
    public List<String> content() {
        List<String> lines = new ArrayList<>();
        String managerPath = nameResolver.resolve(CssClassManager.INSTANCE).basePath();
        String beingName = cssBeing.getClass().getCanonicalName();
        String themeArg = theme != null ? ", \"" + theme + "\"" : "";

        lines.add("import { CssClassManagerInstance as _css } from \"" + managerPath + "\";");
        lines.add("await _css.loadCss(\"" + beingName + "\"" + themeArg + ");");

        for (CssClass<C> cls : cssBeing.cssClasses()) {
            String recordName = cls.getClass().getSimpleName();
            String cssName = CssClassName.toCssName(cls.getClass());
            lines.add("const " + recordName + " = _css.cls(\"" + cssName + "\");");
        }
        return lines;
    }
}
