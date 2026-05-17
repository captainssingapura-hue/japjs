package hue.captains.singapura.js.homing.studio.docs.defects;

import hue.captains.singapura.js.homing.studio.base.ClasspathMarkdownDoc;
import hue.captains.singapura.js.homing.studio.base.DocReference;
import hue.captains.singapura.js.homing.studio.base.Reference;

import java.util.List;
import java.util.UUID;

public record Defect0005Doc() implements ClasspathMarkdownDoc {
    private static final UUID ID = UUID.fromString("8f3b1d24-7e9a-4d62-9c41-3a8e5b07f9d1");
    public static final Defect0005Doc INSTANCE = new Defect0005Doc();

    @Override public UUID   uuid()    { return ID; }
    @Override public String title()   { return "Defect 0005 — Two-Source Registration Drift"; }
    @Override public String summary() { return "Plans, AppModules, and (probably) future typed kinds are registered in two parallel places — Studio.plans() + catalogue leaf for Plans; Fixtures.harnessApps() + ContentViewer.app() for AppModules. Forgetting either side surfaces as a 404 at click time. Backstopped by per-kind conformance tests; root-cause fix open."; }
    @Override public String category(){ return "DEFECT"; }

    @Override public List<Reference> references() {
        return List.of(
                new DocReference("def-6", Defect0006Doc.INSTANCE)
        );
    }
}
