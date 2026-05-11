package hue.captains.singapura.js.homing.studio.docs.defects;

import hue.captains.singapura.js.homing.studio.base.ClasspathMarkdownDoc;
import hue.captains.singapura.js.homing.studio.base.Reference;

import java.util.List;
import java.util.UUID;

public record Defect0003Doc() implements ClasspathMarkdownDoc {
    private static final UUID ID = UUID.fromString("8c2e4f7a-1b3d-4a9e-bf85-2c6d7e9a4f30");
    public static final Defect0003Doc INSTANCE = new Defect0003Doc();

    @Override public UUID   uuid()    { return ID; }
    @Override public String title()   { return "Defect 0003 — Two-Bundle CSS Cascade"; }
    @Override public String summary() { return "homing-studio-base serves CSS from two independent stylesheet bundles (StudioStyles via /css-content, Theme.Globals via /theme-globals). Both target the same .st-* class names; when rules collided at equal specificity, last-loaded won and load order wasn't deterministic. Resolved: typed Layer ladder (Reset → Layout → Component → Prose → State → MediaGated → ThemeOverlay) carried on CssClass via InLayer<L> + ThemeGlobals.chunks(), served wrapped in CSS @layer blocks. Cascade is now deterministic regardless of bundle load order; !important workarounds removed."; }
    @Override public String category(){ return "DEFECT"; }

    @Override public List<Reference> references() { return List.of(); }
}
