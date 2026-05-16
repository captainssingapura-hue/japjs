package hue.captains.singapura.js.homing.studio.base.graph;

import hue.captains.singapura.tao.http.action.TypedContent;
import hue.captains.singapura.tao.ontology.ValueObject;

/**
 * RFC 0014 — typed HTTP response body carrying a markdown document.
 * Sets {@code Content-Type: text/markdown; charset=utf-8} so client-side
 * fetchers can branch on content-type when needed.
 *
 * <p>Used today by {@code StudioGraphMarkdownAction} to serve the live
 * {@code StudioGraph} dump as markdown that the front-end renders via
 * the bundled marked.js. Available for any future action that needs to
 * serve raw markdown (the typed-doc DSL might use it later for
 * computed-content endpoints).</p>
 */
public record MarkdownContent(String body) implements TypedContent, ValueObject {

    @Override
    public String contentType() {
        return "text/markdown; charset=utf-8";
    }
}
