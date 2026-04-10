package hue.captains.singapura.japjs.server;

import hue.captains.singapura.tao.http.action.TypedContent;

public record CssContent(String body) implements TypedContent.Css {}
