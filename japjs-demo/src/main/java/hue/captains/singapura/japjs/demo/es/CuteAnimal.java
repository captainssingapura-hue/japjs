package hue.captains.singapura.japjs.demo.es;

import hue.captains.singapura.japjs.core.*;

import java.util.List;

public record CuteAnimal() implements SvgGroup<CuteAnimal> {

    record turtle() implements SvgBeing<CuteAnimal> {}
    record ghost() implements SvgBeing<CuteAnimal> {}
    record broom() implements SvgBeing<CuteAnimal> {}
    record penguin() implements SvgBeing<CuteAnimal> {}
    record crocodile() implements SvgBeing<CuteAnimal> {}
    record whale() implements SvgBeing<CuteAnimal> {}

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
