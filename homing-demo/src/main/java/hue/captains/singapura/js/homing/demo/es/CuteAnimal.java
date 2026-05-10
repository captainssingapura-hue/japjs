package hue.captains.singapura.js.homing.demo.es;

import hue.captains.singapura.js.homing.core.*;

import java.util.List;

public record CuteAnimal() implements SvgGroup<CuteAnimal> {

    public record turtle()    implements SvgBeing<CuteAnimal> {}
    public record ghost()     implements SvgBeing<CuteAnimal> {}
    public record broom()     implements SvgBeing<CuteAnimal> {}
    public record penguin()   implements SvgBeing<CuteAnimal> {}
    public record crocodile() implements SvgBeing<CuteAnimal> {}
    public record whale()     implements SvgBeing<CuteAnimal> {}

    public static final CuteAnimal INSTANCE = new CuteAnimal();

    @Override
    public List<SvgBeing<CuteAnimal>> svgBeings() {
        return List.of(new turtle(), new ghost(), new broom(), new penguin(), new crocodile(), new whale());
    }

    @Override
    public ExportsOf<CuteAnimal> exports() {
        return new ExportsOf<>(this, List.copyOf(svgBeings()));
    }
}
