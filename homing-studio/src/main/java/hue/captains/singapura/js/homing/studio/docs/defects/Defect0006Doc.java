package hue.captains.singapura.js.homing.studio.docs.defects;

import hue.captains.singapura.js.homing.studio.base.ClasspathMarkdownDoc;
import hue.captains.singapura.js.homing.studio.base.DocReference;
import hue.captains.singapura.js.homing.studio.base.Reference;

import java.util.List;
import java.util.UUID;

public record Defect0006Doc() implements ClasspathMarkdownDoc {
    private static final UUID ID = UUID.fromString("a72e5c9d-4b18-49f3-8716-2e4a0b5d8c1f");
    public static final Defect0006Doc INSTANCE = new Defect0006Doc();

    @Override public UUID   uuid()    { return ID; }
    @Override public String title()   { return "Defect 0006 — ConformanceTest Family Lacks a Taxonomy"; }
    @Override public String summary() { return "Nine conformance tests grew organically with no top-level documentation. Each test's purpose is implicit in its name + javadoc. There's no map showing which invariants are covered, which families exist, or which gaps remain. Fix: a Building Block doc that lists, categorises, and explains every test."; }
    @Override public String category(){ return "DEFECT"; }

    @Override public List<Reference> references() {
        return List.of(
                new DocReference("def-5", Defect0005Doc.INSTANCE)
        );
    }
}
