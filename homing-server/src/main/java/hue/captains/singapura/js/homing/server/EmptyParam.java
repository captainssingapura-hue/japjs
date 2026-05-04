package hue.captains.singapura.js.homing.server;

import hue.captains.singapura.tao.http.action.Param;

public final class EmptyParam {

    private EmptyParam() {}

    public record NoHeaders() implements Param._Header {}
}
