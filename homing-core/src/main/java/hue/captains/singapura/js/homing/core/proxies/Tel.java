package hue.captains.singapura.js.homing.core.proxies;

import hue.captains.singapura.js.homing.core.AppLink;
import hue.captains.singapura.js.homing.core.ProxyApp;

/**
 * Built-in {@link ProxyApp} for the {@code tel:} URL scheme (RFC 3966).
 *
 * <p>Usage from JS (via the generated {@code nav}):</p>
 * <pre>{@code
 * href.toAttr(nav.Tel({number: "+15555551234"}))
 * }</pre>
 *
 * <p>International format ({@code +<country><number>}) is recommended for
 * cross-device compatibility.</p>
 *
 * <p>Introduced in RFC 0001 Step 08.</p>
 */
public record Tel() implements ProxyApp<Tel> {

    public static final Tel INSTANCE = new Tel();

    public record Params(String number) {}
    public record link() implements AppLink<Tel> {}

    @Override public String simpleName()  { return "tel"; }
    @Override public Class<?> paramsType() { return Params.class; }
    @Override public String urlTemplate() { return "tel:{number}"; }
}
