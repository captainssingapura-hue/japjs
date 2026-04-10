package hue.captains.singapura.japjs.demo.es;

import hue.captains.singapura.japjs.core.*;

import java.util.List;

public record CuteAnimal() implements SvgGroup<CuteAnimal> {

    record turtle() implements SvgBeing<CuteAnimal> {}

    public static final CuteAnimal INSTANCE = new CuteAnimal();

    @Override
    public List<SvgBeing<CuteAnimal>> svgBeings() {
        return List.of(new turtle());
    }

    @Override
    public ExportsOf<CuteAnimal> exports() {
        return new ExportsOf<>(this, List.copyOf(svgBeings()));
    }
}
