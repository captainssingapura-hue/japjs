package hue.captains.singapura.js.homing.libs;

import hue.captains.singapura.js.homing.core.BundledExternalModule;
import hue.captains.singapura.js.homing.core.Exportable;
import hue.captains.singapura.js.homing.core.ExportsOf;

import java.util.List;

/**
 * marked — markdown-to-HTML renderer. Bundled at build time from esm.sh and
 * served from the homing.js classpath. No runtime CDN call.
 *
 * @see <a href="https://github.com/markedjs/marked">markedjs/marked</a>
 */
public record MarkedJs() implements BundledExternalModule<MarkedJs> {

    public static final MarkedJs INSTANCE = new MarkedJs();

    @Override public String sourceUrl()    { return "https://esm.sh/marked@14.1.4?bundle"; }
    @Override public String resourcePath() { return "lib/marked@14.1.4/marked.module.js"; }
    @Override public String sha512()       {
        return "f929ac6b4219ca208667404e0bd26739e786dc3f290654a67e872a090493432b"
             + "0a0cddfd05065abbc219f4d70f43ec397824e95c578b19e3236f7cb363b28d71";
    }

    public record marked() implements Exportable._Constant<MarkedJs> {}

    @Override
    public ExportsOf<MarkedJs> exports() {
        return new ExportsOf<>(INSTANCE, List.of(new marked()));
    }
}
