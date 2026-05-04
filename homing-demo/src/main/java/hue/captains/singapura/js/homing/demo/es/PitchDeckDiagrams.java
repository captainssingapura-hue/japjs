package hue.captains.singapura.js.homing.demo.es;

import hue.captains.singapura.js.homing.core.*;

import java.util.List;

public record PitchDeckDiagrams() implements SvgGroup<PitchDeckDiagrams> {

    public record arch() implements SvgBeing<PitchDeckDiagrams> {}
    public record ownership() implements SvgBeing<PitchDeckDiagrams> {}
    public record deployment() implements SvgBeing<PitchDeckDiagrams> {}
    public record messaging() implements SvgBeing<PitchDeckDiagrams> {}

    public static final PitchDeckDiagrams INSTANCE = new PitchDeckDiagrams();

    @Override
    public List<SvgBeing<PitchDeckDiagrams>> svgBeings() {
        return List.of(new arch(), new ownership(), new deployment(), new messaging());
    }

    @Override
    public ExportsOf<PitchDeckDiagrams> exports() {
        return new ExportsOf<>(this, List.copyOf(svgBeings()));
    }
}
