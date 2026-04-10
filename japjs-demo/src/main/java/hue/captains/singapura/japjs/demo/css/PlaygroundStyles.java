package hue.captains.singapura.japjs.demo.css;

import hue.captains.singapura.japjs.core.CssBeing;
import hue.captains.singapura.japjs.core.CssClass;
import hue.captains.singapura.japjs.core.CssImportsFor;

import java.util.List;

public record PlaygroundStyles() implements CssBeing<PlaygroundStyles> {
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
