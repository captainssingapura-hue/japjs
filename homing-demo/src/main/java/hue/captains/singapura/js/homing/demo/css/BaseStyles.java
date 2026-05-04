package hue.captains.singapura.js.homing.demo.css;

import hue.captains.singapura.js.homing.core.CssGroup;
import hue.captains.singapura.js.homing.core.CssClass;
import hue.captains.singapura.js.homing.core.CssImportsFor;

import java.util.List;

public record BaseStyles() implements CssGroup<BaseStyles> {

    public static final BaseStyles INSTANCE = new BaseStyles();

    @Override
    public List<CssClass<BaseStyles>> cssClasses() {
        return List.of();
    }

    @Override
    public CssImportsFor<BaseStyles> cssImports() {
        return CssImportsFor.none(this);
    }
}
