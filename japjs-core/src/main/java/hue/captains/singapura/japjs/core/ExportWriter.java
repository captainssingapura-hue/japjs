package hue.captains.singapura.japjs.core;

import java.util.List;

public record ExportWriter() {
    public <M extends EsModule<M>> List<String> writeExports(ExportsOf<M> exports){
        return exports.exports().stream().map(e->"export " + e.getClass().getSimpleName() + ";").toList();
    }
}
