package hue.captains.singapura.js.homing.studio.base.app;

import java.util.List;

/**
 * A typed, ordered container of {@link Entry}s — Docs, sub-Catalogues, or other
 * navigable apps. Per the
 * <a href="../../../../../../../../../../docs/doctrines/CatalogueContainerDoc.md">
 * Catalogues as Containers</a> doctrine, a catalogue is structure only — it carries
 * no URLs, no presentation directives, no per-entry decoration. Identity is the
 * implementing Java class itself; presentation is the renderer's responsibility.
 *
 * <p>Per <a href="../../../../../../../../../../docs/rfcs/Rfc0005Doc.md">RFC 0005</a>,
 * a catalogue declaration is canonically a stateless record:</p>
 *
 * <pre>{@code
 * public record DoctrineCatalogue() implements Catalogue {
 *     public static final DoctrineCatalogue INSTANCE = new DoctrineCatalogue();
 *
 *     @Override public String name()    { return "Doctrines"; }
 *     @Override public String summary() { return "The rules that hold the design together."; }
 *
 *     @Override public List<Entry> entries() {
 *         return List.of(
 *                 Entry.of(PureComponentViewsDoc.INSTANCE),
 *                 Entry.of(MethodsOverPropsDoc.INSTANCE),
 *                 Entry.of(ManagedDomOpsDoc.INSTANCE),
 *                 Entry.of(OwnedReferencesDoc.INSTANCE)
 *         );
 *     }
 * }
 * }</pre>
 *
 * <p>The class — {@code DoctrineCatalogue} — is the catalogue's identity. Other
 * artifacts reference this catalogue by importing the class and using
 * {@code DoctrineCatalogue.INSTANCE}. URLs are derived by the framework at render
 * time; no catalogue ever constructs a URL.</p>
 *
 * @since RFC 0005
 */
public interface Catalogue {

    /**
     * Human-readable label identifying this catalogue. Used as the page heading and
     * in tile listings by parent catalogues. Not presentation — this is the
     * catalogue's name; how it's styled is the renderer's call.
     */
    String name();

    /**
     * Optional one-line summary shown in parent-catalogue tile listings. Default
     * empty (renderer omits the summary line).
     */
    default String summary() { return ""; }

    /** Ordered children — Docs, sub-Catalogues, or other navigable apps, intermixed. */
    List<Entry> entries();
}
