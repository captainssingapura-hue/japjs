package hue.captains.singapura.js.homing.demo.es;

import hue.captains.singapura.js.homing.core.*;

import java.util.List;

public record Wonderland() implements SvgGroup<Wonderland> {

    record CheshireCat() implements SvgBeing<Wonderland> {}

    record WhiteRabbit() implements SvgBeing<Wonderland> {}

    record MadHatter() implements SvgBeing<Wonderland> {}

    record QueenOfHearts() implements SvgBeing<Wonderland> {}

    public static final Wonderland INSTANCE = new Wonderland();

    @Override
    public List<SvgBeing<Wonderland>> svgBeings() {
        return List.of(new CheshireCat(), new WhiteRabbit(), new MadHatter(), new QueenOfHearts());
    }

    @Override
    public ImportsFor<Wonderland> imports() {
        return ImportsFor.<Wonderland>builder().build();
    }

    @Override
    public ExportsOf<Wonderland> exports() {
        return new ExportsOf<>(this, List.copyOf(svgBeings()));
    }
}
