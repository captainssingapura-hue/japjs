package hue.captains.singapura.js.homing.server;

import hue.captains.singapura.tao.http.action.TypedContent;

public record HtmlPageContent(String body) implements TypedContent.Html {}
