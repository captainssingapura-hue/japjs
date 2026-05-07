package hue.captains.singapura.js.homing.demo.theme;

import hue.captains.singapura.js.homing.core.CssGroupImpl;

import java.util.List;

/**
 * Manual registry of every {@link CssGroupImpl} the homing-demo ships.
 *
 * <p>Mirrors the studio-base {@code CssGroupImplRegistry}. The framework's
 * {@code CssContentGetAction} consults this list to resolve which impl to
 * render for a given {@code (CssGroup canonical name, Theme.slug)} pair.</p>
 */
public final class DemoCssGroupImplRegistry {

    /** Every CssGroup × Theme implementation shipped by homing-demo. */
    public static final List<CssGroupImpl<?, ?>> ALL = List.of(
            CatalogueStylesDemoDefault.INSTANCE,
            PitchDeckStylesDemoDefault.INSTANCE,
            PlaygroundStylesDemoDefault.INSTANCE,
            PlaygroundStylesAlpine.INSTANCE,
            PlaygroundStylesBeach.INSTANCE,
            PlaygroundStylesDracula.INSTANCE,
            SpinningStylesDemoDefault.INSTANCE,
            SpinningStylesBeach.INSTANCE,
            SubwayStylesDemoDefault.INSTANCE,
            SubwayStylesBeach.INSTANCE
    );

    private DemoCssGroupImplRegistry() {}
}
