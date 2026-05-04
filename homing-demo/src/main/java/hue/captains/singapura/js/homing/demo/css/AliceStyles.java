package hue.captains.singapura.js.homing.demo.css;

import hue.captains.singapura.js.homing.core.CssGroup;
import hue.captains.singapura.js.homing.core.CssClass;
import hue.captains.singapura.js.homing.core.CssImportsFor;

import java.util.List;

public record AliceStyles() implements CssGroup<AliceStyles> {
    public static final AliceStyles INSTANCE = new AliceStyles();

    @Override
    public List<CssClass<AliceStyles>> cssClasses() {
        return List.of();
    }

    @Override
    public CssImportsFor<AliceStyles> cssImports() {
        return new CssImportsFor<>(this, List.of(BaseStyles.INSTANCE));
    }
}
