package hue.captains.singapura.japjs.server;

import hue.captains.singapura.tao.http.action.Param;

public record ModuleQuery(String className, String theme, String locale) implements Param._QueryString {

    public ModuleQuery(String className) {
        this(className, null, null);
    }
}
