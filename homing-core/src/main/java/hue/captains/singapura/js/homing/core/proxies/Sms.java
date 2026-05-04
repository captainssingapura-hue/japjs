package hue.captains.singapura.js.homing.core.proxies;

import hue.captains.singapura.js.homing.core.AppLink;
import hue.captains.singapura.js.homing.core.ProxyApp;

import java.util.Optional;

/**
 * Built-in {@link ProxyApp} for the {@code sms:} URL scheme (RFC 5724).
 *
 * <p>Usage from JS (via the generated {@code nav}):</p>
 * <pre>{@code
 * href.toAttr(nav.Sms({number: "+15555551234"}))
 * href.toAttr(nav.Sms({number: "+15555551234", body: "Hello!"}))
 * }</pre>
 *
 * <p>International format ({@code +<country><number>}) is recommended.</p>
 *
 * <p>Introduced in RFC 0001 Step 08.</p>
 */
public record Sms() implements ProxyApp<Sms> {

    public static final Sms INSTANCE = new Sms();

    public record Params(String number, Optional<String> body) {}
    public record link() implements AppLink<Sms> {}

    @Override public String simpleName()  { return "sms"; }
    @Override public Class<?> paramsType() { return Params.class; }
    @Override public String urlTemplate() { return "sms:{number}?body={body?}"; }
}
