package hue.captains.singapura.js.homing.studio.base;

import hue.captains.singapura.js.homing.core.util.ResourceReader;

/**
 * RFC 0012 — deployment knobs. Port + environment-derived defaults.
 * Independent of {@link Fixtures} — Fixtures arrives already-initialised
 * with everything downstream provides; RuntimeParams only describes how
 * this deployment <em>process</em> is wired.
 *
 * <p>Operators with env-specific fields (TLS, bind address, profiling
 * toggles) implement a richer subtype; the bootstrap only reads through
 * this base contract.</p>
 *
 * <pre>{@code
 * RuntimeParams params = new DefaultRuntimeParams(8080);
 * }</pre>
 */
public interface RuntimeParams {

    /** Port to bind. */
    int port();

    /** Resource reader for classpath / disk lookups. Default: {@link ResourceReader#fromSystemProperty()}. */
    default ResourceReader resourceReader() {
        return ResourceReader.fromSystemProperty();
    }
}
