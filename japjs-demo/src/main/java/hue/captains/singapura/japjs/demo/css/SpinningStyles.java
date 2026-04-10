package hue.captains.singapura.japjs.demo.css;

import hue.captains.singapura.japjs.core.CssBeing;
import hue.captains.singapura.japjs.core.CssClass;
import hue.captains.singapura.japjs.core.CssImportsFor;

import java.util.List;

public record SpinningStyles() implements CssBeing<SpinningStyles> {
    public static final SpinningStyles INSTANCE = new SpinningStyles();

    public record spin_title() implements CssClass<SpinningStyles> {}
    public record spin_hint() implements CssClass<SpinningStyles> {}
    public record spin_controls() implements CssClass<SpinningStyles> {}
    public record spin_grid() implements CssClass<SpinningStyles> {}
    public record spin_cell() implements CssClass<SpinningStyles> {}
    public record paused() implements CssClass<SpinningStyles> {}

    @Override
    public List<CssClass<SpinningStyles>> cssClasses() {
        return List.of(new spin_title(), new spin_hint(), new spin_controls(),
                new spin_grid(), new spin_cell(), new paused());
    }

    @Override
    public CssImportsFor<SpinningStyles> cssImports() {
        return CssImportsFor.none(this);
    }
}
