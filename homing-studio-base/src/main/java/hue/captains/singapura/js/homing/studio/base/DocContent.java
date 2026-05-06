package hue.captains.singapura.js.homing.studio.base;

import hue.captains.singapura.tao.http.action.TypedContent;

/** A markdown document body served by {@link DocGetAction}. */
public record DocContent(String body) implements TypedContent {
    @Override public String contentType() { return "text/markdown; charset=utf-8"; }
}
