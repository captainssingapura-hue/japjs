package hue.captains.singapura.japjs.demo.es;

import hue.captains.singapura.japjs.core.*;
import hue.captains.singapura.japjs.core.util.ReadContentFromResources;

import java.util.List;

public record Alice() implements EsModule<Alice> {

    record AliceConst() implements Exportable._Constant<Alice>{}
    record AliceClass() implements Exportable._Class<Alice>{}

    public static final Alice INSTANCE = new Alice();

    @Override
    public ImportsFor<Alice> imports() {
        return ImportsFor.<Alice>builder().build();
    }

    @Override
    public ExportsOf<Alice> exports() {
        return new ExportsOf<>(new Alice(), List.of(new AliceConst(), new AliceClass()));
    }


}
