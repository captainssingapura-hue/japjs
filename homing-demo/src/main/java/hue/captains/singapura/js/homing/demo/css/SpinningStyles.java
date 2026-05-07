package hue.captains.singapura.js.homing.demo.css;

import hue.captains.singapura.js.homing.core.CssClass;
import hue.captains.singapura.js.homing.core.CssGroup;
import hue.captains.singapura.js.homing.core.CssImportsFor;

import java.util.List;

public record SpinningStyles() implements CssGroup<SpinningStyles> {
    public static final SpinningStyles INSTANCE = new SpinningStyles();

    public record spin_title() implements CssClass<SpinningStyles> {
        @Override public String body() { return """
                font-size: 2rem;
                font-weight: 800;
                text-transform: uppercase;
                letter-spacing: 4px;
                color: var(--spin-title-color);
                text-shadow: var(--spin-title-text-shadow);
                margin: 0 0 4px 0;
                """;
        }
    }
    public record spin_hint() implements CssClass<SpinningStyles> {
        @Override public String body() { return """
                color: var(--spin-hint-color);
                font-style: italic;
                font-size: 0.9rem;
                margin: 12px 0 16px 0;
                """;
        }
    }
    public record spin_controls() implements CssClass<SpinningStyles> {
        @Override public String body() { return """
                display: flex;
                align-items: center;
                gap: 16px;
                margin-bottom: 16px;
                flex-wrap: wrap;
                """;
        }
    }
    public record spin_grid() implements CssClass<SpinningStyles> {
        @Override public String body() { return """
                display: grid;
                grid-template-columns: repeat(4, 1fr);
                gap: 12px;
                max-width: 560px;
                """;
        }
    }
    public record spin_cell() implements CssClass<SpinningStyles> {
        @Override public String body() { return """
                display: flex;
                align-items: center;
                justify-content: center;
                background: var(--spin-cell-bg);
                border: var(--spin-cell-border);
                border-radius: 50%;
                aspect-ratio: 1;
                cursor: pointer;
                transition: border-color 0.3s, box-shadow 0.3s;
                """;
        }
    }
    public record paused() implements CssClass<SpinningStyles> {
        @Override public String body() { return "opacity: 0.4;"; }
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
