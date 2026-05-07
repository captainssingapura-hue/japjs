package hue.captains.singapura.js.homing.demo.css;

import hue.captains.singapura.js.homing.core.CssBlock;
import hue.captains.singapura.js.homing.core.CssGroup;
import hue.captains.singapura.js.homing.core.CssClass;
import hue.captains.singapura.js.homing.core.CssGroupImpl;
import hue.captains.singapura.js.homing.core.CssImportsFor;
import hue.captains.singapura.js.homing.core.Theme;

import java.util.List;

public record SubwayStyles() implements CssGroup<SubwayStyles> {
    public static final SubwayStyles INSTANCE = new SubwayStyles();

    public record subway_title() implements CssClass<SubwayStyles> {}
    public record subway_hint() implements CssClass<SubwayStyles> {}
    public record subway_grid() implements CssClass<SubwayStyles> {}
    public record subway_cell() implements CssClass<SubwayStyles> {}

    /** Per-theme implementation contract for {@link SubwayStyles}. */
    public interface Impl<TH extends Theme> extends CssGroupImpl<SubwayStyles, TH> {
        @Override default SubwayStyles group() { return INSTANCE; }

        CssBlock<subway_title> subway_title();
        CssBlock<subway_hint> subway_hint();
        CssBlock<subway_grid> subway_grid();
        CssBlock<subway_cell> subway_cell();
    }

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
