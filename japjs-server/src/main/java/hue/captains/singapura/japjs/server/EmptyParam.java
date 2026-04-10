package hue.captains.singapura.japjs.server;

import hue.captains.singapura.tao.http.action.Param;

public final class EmptyParam {

    private EmptyParam() {}

    public record NoHeaders() implements Param._Header {}
}
