package hue.captains.singapura.js.homing.studio.base.app;

import hue.captains.singapura.js.homing.core.AppModule;
import hue.captains.singapura.js.homing.studio.base.Doc;
import hue.captains.singapura.js.homing.studio.base.tracker.Plan;

/**
 * Typed wrapper for {@link Catalogue} entries. Sealed to four kinds reflecting
 * the catalogue tree's structural model:
 *
 * <ul>
 *   <li>{@link OfDoc} — a static {@link Doc} (markdown content shipped per RFC 0004).</li>
 *   <li>{@link OfCatalogue} — a sub-tree (another {@link Catalogue}).</li>
 *   <li>{@link OfApp} — a living "doc" — any {@link AppModule} with its own page.</li>
 *   <li>{@link OfPlan} — a structured living plan (RFC 0005-ext1) — questions, phased actions, acceptance.</li>
 * </ul>
 *
 * <p>The conceptual model: "doc" spans a spectrum from static (markdown) to living
 * (Plan trackers, custom apps). {@link OfDoc}, {@link OfApp}, and {@link OfPlan}
 * are three implementation kinds of the same conceptual "content the user navigates
 * to"; {@link OfCatalogue} is the sub-tree case. The renderer pattern-matches
 * exhaustively over the four sealed subtypes; the compile-time {@code switch}
 * exhaustiveness check guarantees complete coverage.</p>
 *
 * <p><b>Why {@code OfApp} accepts any {@link AppModule} directly</b>: every
 * AppModule is, by design, navigable — it has a {@code simpleName()} URL token,
 * a {@code title()} for display, and (post-v1) {@code name()} + {@code summary()}
 * defaulted methods for tile listings. There is no separate marker interface to
 * opt in. The catalogue author chooses what to list; type-level gating against
 * "infrastructure" apps (e.g. {@code CatalogueAppHost} which needs an {@code id}
 * param to be useful) is a content concern, not a type concern.</p>
 *
 * <p>Convenience factories keep call sites clean.</p>
 *
 * @since RFC 0005 (extended in RFC 0005-ext1 to add OfPlan; v1 unifies OfApp on AppModule directly)
 */
public sealed interface Entry {

    /** A static Doc — markdown content shipped on the classpath. */
    record OfDoc(Doc doc) implements Entry {}

    /** A sub-tree — another catalogue. */
    record OfCatalogue(Catalogue catalogue) implements Entry {}

    /** A living "doc" — an AppModule bound to its typed Params with tile display data. */
    record OfApp(Navigable<?, ?> nav) implements Entry {}

    /** A structured living plan (RFC 0005-ext1) — questions, phased actions, acceptance. */
    record OfPlan(Plan plan) implements Entry {}

    // -----------------------------------------------------------------------
    // Convenience factories
    // -----------------------------------------------------------------------

    static Entry of(Doc doc)               { return new OfDoc(doc); }
    static Entry of(Catalogue catalogue)   { return new OfCatalogue(catalogue); }
    static Entry of(Navigable<?, ?> nav)   { return new OfApp(nav); }
    static Entry of(Plan plan)             { return new OfPlan(plan); }
}
