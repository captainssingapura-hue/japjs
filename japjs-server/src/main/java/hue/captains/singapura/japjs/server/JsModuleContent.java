package hue.captains.singapura.japjs.server;

import hue.captains.singapura.tao.http.action.TypedContent;

public record JsModuleContent(String body) implements TypedContent.Js {}
