package hue.captains.singapura.js.homing.demo.css;

import hue.captains.singapura.js.homing.core.CssClass;
import hue.captains.singapura.js.homing.core.CssGroup;
import hue.captains.singapura.js.homing.core.CssImportsFor;

import java.util.List;

public record PlaygroundStyles() implements CssGroup<PlaygroundStyles> {
    public static final PlaygroundStyles INSTANCE = new PlaygroundStyles();

    public record pg_title() implements CssClass<PlaygroundStyles> {
        @Override public String body() { return """
                font-size: 2rem;
                font-weight: 800;
                text-transform: uppercase;
                letter-spacing: var(--pg-title-letter-spacing);
                color: var(--pg-title-color);
                text-shadow: var(--pg-title-text-shadow);
                margin: 0 0 4px 0;
                """;
        }
    }
    public record pg_hint() implements CssClass<PlaygroundStyles> {
        @Override public String body() { return """
                color: var(--pg-hint-color);
                font-style: italic;
                font-size: 0.9rem;
                margin: 12px 0 16px 0;
                """;
        }
    }
    public record pg_controls() implements CssClass<PlaygroundStyles> {
        @Override public String body() { return """
                display: flex;
                align-items: center;
                gap: 16px;
                margin-bottom: 16px;
                flex-wrap: wrap;
                """;
        }
    }
    public record pg_size_display() implements CssClass<PlaygroundStyles> {
        @Override public String body() { return """
                font-size: 0.85rem;
                color: var(--pg-size-color);
                font-weight: 600;
                min-width: 80px;
                """;
        }
    }
    public record pg_theme_switcher() implements CssClass<PlaygroundStyles> {
        @Override public String body() { return """
                display: flex;
                align-items: center;
                gap: 8px;
                margin-bottom: 16px;
                flex-wrap: wrap;
                """;
        }
    }
    public record pg_theme_label() implements CssClass<PlaygroundStyles> {
        @Override public String body() { return """
                font-size: 0.85rem;
                font-weight: 600;
                color: var(--pg-theme-label-color);
                margin-right: 4px;
                """;
        }
    }
    public record pg_theme_btn() implements CssClass<PlaygroundStyles> {
        @Override public String body() { return """
                padding: 4px 12px;
                font-size: 0.8rem;
                font-weight: 500;
                border: var(--pg-theme-btn-border);
                border-radius: 4px;
                background: var(--pg-theme-btn-bg);
                color: var(--pg-theme-btn-color);
                cursor: pointer;
                transition: background 0.15s, border-color 0.15s, color 0.15s;
                """;
        }
    }
    public record pg_theme_btn_active() implements CssClass<PlaygroundStyles> {
        @Override public String body() { return """
                background: var(--pg-theme-btn-active-bg);
                border-color: var(--pg-theme-btn-active-border);
                color: var(--pg-theme-btn-active-color);
                cursor: default;
                """;
        }
    }
    public record pg_playground() implements CssClass<PlaygroundStyles> {
        @Override public String body() { return """
                position: relative;
                overflow: hidden;
                background: var(--pg-playground-bg);
                border: var(--pg-playground-border);
                border-radius: 8px;
                box-shadow: var(--pg-playground-shadow);
                """;
        }
    }
    public record pg_sky() implements CssClass<PlaygroundStyles> {
        @Override public String body() { return """
                position: absolute;
                top: 0;
                left: 0;
                right: 0;
                z-index: 1;
                background: var(--pg-sky-bg);
                background-repeat: var(--pg-sky-bg-repeat);
                background-position: var(--pg-sky-bg-position);
                background-size: var(--pg-sky-bg-size);
                """;
        }
    }
    public record pg_world() implements CssClass<PlaygroundStyles> {
        @Override public String body() { return """
                position: absolute;
                top: 0;
                left: 0;
                width: 100%;
                height: 100%;
                """;
        }
    }
    public record pg_animal() implements CssClass<PlaygroundStyles> {
        @Override public String body() { return """
                position: absolute;
                transform-origin: center;
                will-change: transform, left, top;
                z-index: 5;
                """;
        }
    }
    public record pg_platform() implements CssClass<PlaygroundStyles> {
        @Override public String body() { return """
                position: absolute;
                background: var(--pg-platform-bg);
                border-radius: var(--pg-platform-radius);
                border-top: var(--pg-platform-border-top);
                transition: background 0.2s, border-color 0.2s, box-shadow 0.2s;
                """;
        }
    }
    public record pg_platform_active() implements CssClass<PlaygroundStyles> {
        @Override public String body() { return """
                background: var(--pg-platform-active-bg);
                border-top-color: var(--pg-platform-active-border);
                box-shadow: var(--pg-platform-active-shadow);
                """;
        }
    }
    public record pg_lava() implements CssClass<PlaygroundStyles> {
        @Override public String body() { return """
                position: absolute;
                bottom: 0;
                left: 0;
                right: 0;
                background: var(--pg-lava-bg);
                z-index: 2;
                """;
        }
    }
    public record pg_score() implements CssClass<PlaygroundStyles> {
        @Override public String body() { return """
                position: absolute;
                top: 8px;
                right: 12px;
                font-size: 1rem;
                font-weight: 700;
                color: var(--pg-score-color);
                z-index: 3;
                """;
        }
    }
    public record pg_gameover() implements CssClass<PlaygroundStyles> {
        @Override public String body() { return """
                position: absolute;
                inset: 0;
                display: flex;
                flex-direction: column;
                align-items: center;
                justify-content: center;
                background: var(--pg-gameover-bg);
                z-index: 10;
                """;
        }
    }
    public record pg_final_score() implements CssClass<PlaygroundStyles> {
        @Override public String body() { return """
                font-size: 1.2rem;
                color: var(--pg-final-score-color);
                margin: 0 0 16px 0;
                """;
        }
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
