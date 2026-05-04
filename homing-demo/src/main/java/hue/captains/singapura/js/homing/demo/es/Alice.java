package hue.captains.singapura.js.homing.demo.es;

import hue.captains.singapura.js.homing.core.*;

import java.util.List;

public record Alice() implements EsModule<Alice> {

    record Alice1() implements Exportable._Constant<Alice>{}
    record Alice2() implements Exportable._Constant<Alice>{}
    record AliceClass() implements Exportable._Class<Alice>{}

    public static final Alice INSTANCE = new Alice();

    @Override
    public ImportsFor<Alice> imports() {
        return ImportsFor.<Alice>builder().build();
    }

    @Override
    public ExportsOf<Alice> exports() {
        return new ExportsOf<>(new Alice(), List.of(new Alice1(), new Alice2(), new AliceClass()));
    }

}
