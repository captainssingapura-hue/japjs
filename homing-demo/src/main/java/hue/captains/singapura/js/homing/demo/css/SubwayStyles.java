package hue.captains.singapura.js.homing.demo.css;

import hue.captains.singapura.js.homing.core.CssGroup;
import hue.captains.singapura.js.homing.core.CssClass;
import hue.captains.singapura.js.homing.core.CssImportsFor;

import java.util.List;

public record SubwayStyles() implements CssGroup<SubwayStyles> {
    public static final SubwayStyles INSTANCE = new SubwayStyles();

    public record subway_title() implements CssClass<SubwayStyles> {}
    public record subway_hint() implements CssClass<SubwayStyles> {}
    public record subway_grid() implements CssClass<SubwayStyles> {}
    public record subway_cell() implements CssClass<SubwayStyles> {}

    @Override
    public List<CssClass<SubwayStyles>> cssClasses() {
        return List.of(new subway_title(), new subway_hint(),
                new subway_grid(), new subway_cell());
    }

    @Override
    public CssImportsFor<SubwayStyles> cssImports() {
        return CssImportsFor.none(this);
    }
}
