package hue.captains.singapura.js.homing.studio.docs.doctrines;

import hue.captains.singapura.js.homing.studio.base.ClasspathMarkdownDoc;
import hue.captains.singapura.js.homing.studio.base.DocReference;
import hue.captains.singapura.js.homing.studio.base.Reference;

import java.util.List;
import java.util.UUID;

/**
 * Doctrine — Explicit over Implicit. Public API surfaces return concrete,
 * named, materialised types ({@code List}, {@code Set}, {@code Map},
 * {@code Optional}, typed records) — never lazy/single-use proxies
 * ({@code Stream}, {@code Iterator}, {@code Iterable}, raw arrays).
 *
 * <p>The caller writing {@code var docs = graph.docsCiting(target);}
 * should see {@code Set<Doc> docs} in their IDE and immediately answer:
 * how many? what's in it? can I iterate again? can I toString it?
 * Yes to all four. Internal pipelines compose with streams freely;
 * the boundary where data leaves a typed primitive is where the
 * explicit form takes over.</p>
 *
 * <p>The doctrine pairs with Functional Objects (refused covert
 * behaviour surfaces) by refusing covert return-type surfaces. Same
 * principle, different slice: <i>types must tell the truth about what
 * they carry.</i></p>
 */
public record ExplicitOverImplicitDoc() implements ClasspathMarkdownDoc {
    private static final UUID ID = UUID.fromString("e3a7c5d9-1b4f-4e8d-9a2c-5f3b7e1d8c40");
    public static final ExplicitOverImplicitDoc INSTANCE = new ExplicitOverImplicitDoc();

    @Override public UUID   uuid()    { return ID; }
    @Override public String title()   { return "Doctrine — Explicit over Implicit"; }
    @Override public String summary() { return "Public API surfaces return concrete, materialised types (List, Set, Map, Optional, typed records) — never Stream, Iterator, Iterable, or other lazy/single-use proxies. The caller sees the type, knows the size, can iterate twice, can toString for debugging. Internal pipelines compose with streams freely; the boundary where data leaves a typed primitive is where the explicit form takes over. Pairs with Functional Objects — same principle: types must tell the truth about what they carry."; }
    @Override public String category(){ return "DOCTRINE"; }

    @Override public List<Reference> references() {
        return List.of(
                new DocReference("doc-fo",  FunctionalObjectsDoc.INSTANCE),
                new DocReference("doc-wc",  WeighedComplexityDoc.INSTANCE),
                new DocReference("doc-cc",  CatalogueContainerDoc.INSTANCE),
                new DocReference("doc-nsd", NoStealthDataDoc.INSTANCE)
        );
    }
}
