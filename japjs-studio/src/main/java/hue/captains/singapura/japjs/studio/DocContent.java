package hue.captains.singapura.japjs.studio;

import hue.captains.singapura.tao.http.action.TypedContent;

/** A markdown document body served by {@link DocContentGetAction}. */
public record DocContent(String body) implements TypedContent {
    @Override public String contentType() { return "text/markdown; charset=utf-8"; }
}
