package hue.captains.singapura.japjs.demo.css;

import hue.captains.singapura.japjs.core.CssBeing;
import hue.captains.singapura.japjs.core.CssClass;
import hue.captains.singapura.japjs.core.CssImportsFor;

import java.util.List;

public record BaseStyles() implements CssBeing<BaseStyles> {

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
