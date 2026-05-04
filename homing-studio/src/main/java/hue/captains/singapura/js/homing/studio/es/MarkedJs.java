package hue.captains.singapura.js.homing.studio.es;

import hue.captains.singapura.js.homing.core.ExternalModule;
import hue.captains.singapura.js.homing.core.Exportable;
import hue.captains.singapura.js.homing.core.ExportsOf;

import java.util.List;

/** marked — markdown to HTML renderer. Loaded from esm.sh as a native ES module. */
public record MarkedJs() implements ExternalModule<MarkedJs> {

    public static final MarkedJs INSTANCE = new MarkedJs();

    public record marked() implements Exportable._Constant<MarkedJs> {}

    @Override
    public ExportsOf<MarkedJs> exports() {
        return new ExportsOf<>(INSTANCE, List.of(new marked()));
    }
}
