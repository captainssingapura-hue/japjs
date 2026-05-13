package hue.captains.singapura.js.homing.studio.docs.rfcs;

import hue.captains.singapura.js.homing.studio.base.ClasspathMarkdownDoc;
import hue.captains.singapura.js.homing.studio.base.DocReference;
import hue.captains.singapura.js.homing.studio.base.Reference;
import hue.captains.singapura.js.homing.studio.docs.doctrines.PerceivableSurfaceDoc;

import java.util.List;
import java.util.UUID;

public record Rfc0008Doc() implements ClasspathMarkdownDoc {
    private static final UUID ID = UUID.fromString("b6525936-e2d6-4300-8bdf-c2e694d13ea1");
    public static final Rfc0008Doc INSTANCE = new Rfc0008Doc();

    @Override public UUID   uuid()    { return ID; }
    @Override public String title()   { return "RFC 0008 — Interactive Theme Experiences"; }
    @Override public String summary() { return "Themes go beyond decoration: backdrops become instruments (jazz drum kit), keyboard input becomes opt-in play mode, per-theme preferences land in localStorage, a theme control panel surfaces volume / mute / play-mode toggles. Phase 1 ships the Jazz Drum Kit theme (click-only) using existing RFC 0007 primitives. Phase 2 adds typed KeyCombo bindings + the control panel. Phase 3 (separate RFC) adds persistent ambient state — animal-as-theme."; }
    @Override public String category(){ return "RFC"; }

    @Override public List<Reference> references() {
        return List.of(
                new DocReference("doc-surface", PerceivableSurfaceDoc.INSTANCE),
                new DocReference("rfc-7",       Rfc0007Doc.INSTANCE)
        );
    }
}
