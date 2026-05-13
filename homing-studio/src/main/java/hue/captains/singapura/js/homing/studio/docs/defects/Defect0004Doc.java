package hue.captains.singapura.js.homing.studio.docs.defects;

import hue.captains.singapura.js.homing.studio.base.ClasspathMarkdownDoc;
import hue.captains.singapura.js.homing.studio.base.Reference;

import java.util.List;
import java.util.UUID;

public record Defect0004Doc() implements ClasspathMarkdownDoc {
    private static final UUID ID = UUID.fromString("7e1f9c2a-5b4d-4e8a-9f3c-1a2b3c4d5e6f");
    public static final Defect0004Doc INSTANCE = new Defect0004Doc();

    @Override public UUID   uuid()    { return ID; }
    @Override public String title()   { return "Defect 0004 — Flat Breadcrumbs in Multi-Level Catalogues"; }
    @Override public String summary() { return "DocReader and PlanHostRenderer rendered a single 'Home' crumb above the doc/plan title — fine for L1 docs, wrong for anything nested deeper (e.g., Doctrines → Pure Component Views landed with 'Home › Pure Component Views' instead of 'Studio › Doctrines › Pure Component Views'). Root cause: catalogue parent relations were inferred at runtime from Entry.OfCatalogue references, and the renderers never asked for the chain — they hard-coded a single brand crumb. Resolved by RFC 0005-ext2: typed L0..L8 levels encode the parent type at compile time; CatalogueRegistry exposes breadcrumbsForDoc(UUID) / breadcrumbsForPlan(Class) reverse indices; /doc-refs and /plan now carry the typed chain; renderers consume it."; }
    @Override public String category(){ return "DEFECT"; }

    @Override public List<Reference> references() { return List.of(); }
}
