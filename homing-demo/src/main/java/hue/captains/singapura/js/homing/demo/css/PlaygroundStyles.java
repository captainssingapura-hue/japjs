package hue.captains.singapura.js.homing.demo.css;

import hue.captains.singapura.js.homing.core.CssBlock;
import hue.captains.singapura.js.homing.core.CssGroup;
import hue.captains.singapura.js.homing.core.CssClass;
import hue.captains.singapura.js.homing.core.CssGroupImpl;
import hue.captains.singapura.js.homing.core.CssImportsFor;
import hue.captains.singapura.js.homing.core.Theme;

import java.util.List;

public record PlaygroundStyles() implements CssGroup<PlaygroundStyles> {
    public static final PlaygroundStyles INSTANCE = new PlaygroundStyles();

    public record pg_title() implements CssClass<PlaygroundStyles> {}
    public record pg_hint() implements CssClass<PlaygroundStyles> {}
    public record pg_controls() implements CssClass<PlaygroundStyles> {}
    public record pg_size_display() implements CssClass<PlaygroundStyles> {}
    public record pg_theme_switcher() implements CssClass<PlaygroundStyles> {}
    public record pg_theme_label() implements CssClass<PlaygroundStyles> {}
    public record pg_theme_btn() implements CssClass<PlaygroundStyles> {}
    public record pg_theme_btn_active() implements CssClass<PlaygroundStyles> {}
    public record pg_playground() implements CssClass<PlaygroundStyles> {}
    public record pg_sky() implements CssClass<PlaygroundStyles> {}
    public record pg_world() implements CssClass<PlaygroundStyles> {}
    public record pg_animal() implements CssClass<PlaygroundStyles> {}
    public record pg_platform() implements CssClass<PlaygroundStyles> {}
    public record pg_platform_active() implements CssClass<PlaygroundStyles> {}
    public record pg_lava() implements CssClass<PlaygroundStyles> {}
    public record pg_score() implements CssClass<PlaygroundStyles> {}
    public record pg_gameover() implements CssClass<PlaygroundStyles> {}
    public record pg_final_score() implements CssClass<PlaygroundStyles> {}

    /** Per-theme implementation contract for {@link PlaygroundStyles}. */
    public interface Impl<TH extends Theme> extends CssGroupImpl<PlaygroundStyles, TH> {
        @Override default PlaygroundStyles group() { return INSTANCE; }

        CssBlock<pg_title> pg_title();
        CssBlock<pg_hint> pg_hint();
        CssBlock<pg_controls> pg_controls();
        CssBlock<pg_size_display> pg_size_display();
        CssBlock<pg_theme_switcher> pg_theme_switcher();
        CssBlock<pg_theme_label> pg_theme_label();
        CssBlock<pg_theme_btn> pg_theme_btn();
        CssBlock<pg_theme_btn_active> pg_theme_btn_active();
        CssBlock<pg_playground> pg_playground();
        CssBlock<pg_sky> pg_sky();
        CssBlock<pg_world> pg_world();
        CssBlock<pg_animal> pg_animal();
        CssBlock<pg_platform> pg_platform();
        CssBlock<pg_platform_active> pg_platform_active();
        CssBlock<pg_lava> pg_lava();
        CssBlock<pg_score> pg_score();
        CssBlock<pg_gameover> pg_gameover();
        CssBlock<pg_final_score> pg_final_score();
    }

    @Override
    public List<CssClass<PlaygroundStyles>> cssClasses() {
        return List.of(
                new pg_title(), new pg_hint(), new pg_controls(), new pg_size_display(),
                new pg_theme_switcher(), new pg_theme_label(), new pg_theme_btn(), new pg_theme_btn_active(),
                new pg_playground(), new pg_sky(), new pg_world(), new pg_animal(),
                new pg_platform(), new pg_platform_active(), new pg_lava(), new pg_score(),
                new pg_gameover(), new pg_final_score()
        );
    }

    @Override
    public CssImportsFor<PlaygroundStyles> cssImports() {
        return CssImportsFor.none(this);
    }
}
