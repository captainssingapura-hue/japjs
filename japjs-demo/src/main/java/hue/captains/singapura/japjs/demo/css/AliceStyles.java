package hue.captains.singapura.japjs.demo.css;

import hue.captains.singapura.japjs.core.CssBeing;
import hue.captains.singapura.japjs.core.CssClass;
import hue.captains.singapura.japjs.core.CssImportsFor;

import java.util.List;

public record AliceStyles() implements CssBeing<AliceStyles> {
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
