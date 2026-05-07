package hue.captains.singapura.js.homing.demo.css;

import hue.captains.singapura.js.homing.core.CssClass;
import hue.captains.singapura.js.homing.core.CssGroup;
import hue.captains.singapura.js.homing.core.CssImportsFor;

import java.util.List;

public record SubwayStyles() implements CssGroup<SubwayStyles> {
    public static final SubwayStyles INSTANCE = new SubwayStyles();

    public record subway_title() implements CssClass<SubwayStyles> {
        @Override public String body() { return """
                font-size: 2rem;
                font-weight: 800;
                text-transform: uppercase;
                letter-spacing: 4px;
                color: var(--subway-title-color);
                text-shadow: var(--subway-title-text-shadow);
                margin: 0 0 4px 0;
                position: relative;
                """;
        }
    }
    public record subway_hint() implements CssClass<SubwayStyles> {
        @Override public String body() { return """
                color: var(--subway-hint-color);
                font-style: italic;
                font-size: 0.9rem;
                margin: 12px 0 16px 0;
                """;
        }
    }
    public record subway_grid() implements CssClass<SubwayStyles> {
        @Override public String body() { return """
                display: grid;
                grid-template-columns: repeat(5, 1fr);
                gap: 6px;
                max-width: 620px;
                background: var(--subway-grid-bg);
                border: var(--subway-grid-border);
                border-radius: 8px;
                padding: 12px;
                box-shadow: var(--subway-grid-shadow);
                """;
        }
    }
    public record subway_cell() implements CssClass<SubwayStyles> {
        @Override public String body() { return """
                display: flex;
                align-items: center;
                justify-content: center;
                transition: transform 0.3s ease;
                background: linear-gradient(
                    145deg,
                    var(--cell-sheen) 0%,
                    transparent 100%
                );
                border-radius: 4px;
                padding: 4px;
                """;
        }
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
