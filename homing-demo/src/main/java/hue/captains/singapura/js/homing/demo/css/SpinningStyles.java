package hue.captains.singapura.js.homing.demo.css;

import hue.captains.singapura.js.homing.core.CssBlock;
import hue.captains.singapura.js.homing.core.CssGroup;
import hue.captains.singapura.js.homing.core.CssClass;
import hue.captains.singapura.js.homing.core.CssGroupImpl;
import hue.captains.singapura.js.homing.core.CssImportsFor;
import hue.captains.singapura.js.homing.core.Theme;

import java.util.List;

public record SpinningStyles() implements CssGroup<SpinningStyles> {
    public static final SpinningStyles INSTANCE = new SpinningStyles();

    public record spin_title() implements CssClass<SpinningStyles> {}
    public record spin_hint() implements CssClass<SpinningStyles> {}
    public record spin_controls() implements CssClass<SpinningStyles> {}
    public record spin_grid() implements CssClass<SpinningStyles> {}
    public record spin_cell() implements CssClass<SpinningStyles> {}
    public record paused() implements CssClass<SpinningStyles> {}

    /** Per-theme implementation contract for {@link SpinningStyles}. */
    public interface Impl<TH extends Theme> extends CssGroupImpl<SpinningStyles, TH> {
        @Override default SpinningStyles group() { return INSTANCE; }

        CssBlock<spin_title> spin_title();
        CssBlock<spin_hint> spin_hint();
        CssBlock<spin_controls> spin_controls();
        CssBlock<spin_grid> spin_grid();
        CssBlock<spin_cell> spin_cell();
        CssBlock<paused> paused();
    }

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
