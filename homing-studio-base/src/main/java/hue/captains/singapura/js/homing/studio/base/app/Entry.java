package hue.captains.singapura.js.homing.studio.base.app;

import hue.captains.singapura.js.homing.core.AppModule;
import hue.captains.singapura.js.homing.studio.base.Doc;
import hue.captains.singapura.js.homing.studio.base.tracker.Plan;

/**
 * Typed leaf entry inside a {@link Catalogue}. RFC 0011: parameterised by the
 * host catalogue's type {@code C}, so an {@code Entry<C>} can only appear in
 * {@code C}'s {@code leaves()} list. Misplaced entries become compile errors.
 *
 * <p>Sealed to four variants reflecting the catalogue tree's leaf shapes:</p>
 *
 * <ul>
 *   <li>{@link OfDoc} — a static {@link Doc} (markdown content per RFC 0004).</li>
 *   <li>{@link OfApp} — a living "doc" — any {@link AppModule} with its own page.</li>
 *   <li>{@link OfPlan} — a structured living plan (RFC 0005-ext1).</li>
 *   <li>{@link OfStudio} — a typed re-attachment of a source L0 catalogue as a
 *       leaf in this catalogue, via a {@link StudioProxy} (RFC 0011).</li>
 * </ul>
 *
 * <p>The generic factories — {@code Entry.of(host, target)} — take the host
 * catalogue as a type witness; the compiler infers {@code C} from the
 * {@code this} reference at the call site. The host argument is discarded
 * at runtime (the entry doesn't carry it back as data — it's known by
 * virtue of being in {@code host.leaves()}).</p>
 *
 * @param <C> the host catalogue's type
 * @since RFC 0005 (RFC 0005-ext1 OfPlan; RFC 0005-ext2 removed OfCatalogue;
 *        RFC 0011 typed by host + OfStudio variant for cross-tree composition)
 */
public sealed interface Entry<C extends Catalogue<C>> {

    /** A static Doc — markdown content shipped on the classpath. */
    record OfDoc<C extends Catalogue<C>, D extends Doc>
                 (D doc) implements Entry<C> {}

    /** A living "doc" — an AppModule bound to its typed Params with tile display data. */
    record OfApp<C extends Catalogue<C>,
                 P extends AppModule._Param,
                 M extends AppModule<P, M>>
                 (Navigable<P, M> nav) implements Entry<C> {}

    /** A structured living plan (RFC 0005-ext1). */
    record OfPlan<C extends Catalogue<C>, P extends Plan>
                  (P plan) implements Entry<C> {}

    /** RFC 0011 — a typed re-attachment of a source {@link L0_Catalogue} as a leaf
     *  in this catalogue's tree, via a {@link StudioProxy}. The proxy carries
     *  display fields plus a typed reference to the wrapped L0 INSTANCE;
     *  {@link CatalogueRegistry} uses it to augment breadcrumbs for any page
     *  reached via the source L0. */
    record OfStudio<C extends Catalogue<C>, S extends L0_Catalogue<S>>
                    (StudioProxy<S> proxy) implements Entry<C> {}

    // -----------------------------------------------------------------------
    // Convenience factories — `host` is a type witness for inference, discarded.
    // -----------------------------------------------------------------------

    static <C extends Catalogue<C>, D extends Doc>
           Entry<C> of(C host, D doc) {
        return new OfDoc<>(doc);
    }

    static <C extends Catalogue<C>,
            P extends AppModule._Param,
            M extends AppModule<P, M>>
           Entry<C> of(C host, Navigable<P, M> nav) {
        return new OfApp<>(nav);
    }

    static <C extends Catalogue<C>, P extends Plan>
           Entry<C> of(C host, P plan) {
        return new OfPlan<>(plan);
    }

    static <C extends Catalogue<C>, S extends L0_Catalogue<S>>
           Entry<C> of(C host, StudioProxy<S> proxy) {
        return new OfStudio<>(proxy);
    }
}
