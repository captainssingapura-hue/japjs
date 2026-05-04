package hue.captains.singapura.japjs.core.proxies;

import hue.captains.singapura.japjs.core.AppLink;
import hue.captains.singapura.japjs.core.ProxyApp;

import java.util.Optional;

/**
 * Built-in {@link ProxyApp} for the {@code mailto:} URL scheme.
 *
 * <p>Usage from JS (via the generated {@code nav}):</p>
 * <pre>{@code
 * href.toAttr(nav.Mailto({to: "user@example.com"}))
 * href.toAttr(nav.Mailto({to: "user@example.com", subject: "Hi", body: "Hello"}))
 * }</pre>
 *
 * <p>For multiple recipients in {@code cc} or {@code bcc}, pass a comma-separated
 * string (RFC 6068 mailto syntax).</p>
 *
 * <p><b>Known limitation:</b> when an optional field is absent, the URL still
 * contains its empty key (e.g. {@code mailto:to?subject=&body=}). This is a
 * v1 simplification — query-string templating with conditional segments is
 * deferred (see RFC §3.8). Mail clients accept the empty fields without issue;
 * the URL just looks slightly verbose.</p>
 *
 * <p>Introduced in RFC 0001 Step 08.</p>
 */
public record Mailto() implements ProxyApp<Mailto> {

    public static final Mailto INSTANCE = new Mailto();

    public record Params(
            String to,
            Optional<String> subject,
            Optional<String> body,
            Optional<String> cc,
            Optional<String> bcc) {}

    public record link() implements AppLink<Mailto> {}

    @Override public String simpleName()  { return "mailto"; }
    @Override public Class<?> paramsType() { return Params.class; }
    @Override public String urlTemplate() {
        return "mailto:{to}?subject={subject?}&body={body?}&cc={cc?}&bcc={bcc?}";
    }
}
