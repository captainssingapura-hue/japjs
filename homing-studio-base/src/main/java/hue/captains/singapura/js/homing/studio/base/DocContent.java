package hue.captains.singapura.js.homing.studio.base;

import hue.captains.singapura.tao.http.action.TypedContent;

/**
 * The body of a {@link Doc}, served by {@link DocGetAction}.
 *
 * <p>RFC 0002-ext2: carries an explicit content type so the same action can
 * serve markdown, HTML, plain text, JSON, SVG, etc. — whichever doc kind the
 * caller's typed {@link Doc} record advertised.</p>
 */
public record DocContent(String body, String contentType) implements TypedContent {

    /** Convenience constructor — defaults to markdown for back-compat. */
    public DocContent(String body) {
        this(body, "text/markdown; charset=utf-8");
    }

    @Override public String contentType() { return contentType; }
}
