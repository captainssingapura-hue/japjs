package hue.captains.singapura.js.homing.studio.docs.rfcs;

import hue.captains.singapura.js.homing.studio.base.ClasspathMarkdownDoc;
import hue.captains.singapura.js.homing.studio.base.DocReference;
import hue.captains.singapura.js.homing.studio.base.Reference;
import hue.captains.singapura.js.homing.studio.docs.defects.Defect0002Doc;
import hue.captains.singapura.js.homing.studio.docs.doctrines.EncapsulatedComponentsDoc;
import hue.captains.singapura.js.homing.studio.docs.gotchas.Gotcha0001Doc;

import java.util.List;
import java.util.UUID;

public record Rfc0007Doc() implements ClasspathMarkdownDoc {
    private static final UUID ID = UUID.fromString("40b7f59d-e17d-4b8d-9541-de56ce539279");
    public static final Rfc0007Doc INSTANCE = new Rfc0007Doc();

    @Override public UUID   uuid()    { return ID; }
    @Override public String title()   { return "RFC 0007 — Theme Audio Cues (Typed, Synthesised)"; }
    @Override public String summary() { return "A fourth theme dimension orthogonal to palette / writing-medium / wallpaper: click-triggered audio cues, fully typed end-to-end. Themes declare ClickTargets (typed records) and bind them to Cues (typed Synth/Membrane/Noise records). The framework synthesises audio via Tone.js — zero audio files, zero new HTTP endpoints. Doctrine extends: themes vary perceivable surface (sight + sound), not control logic."; }
    @Override public String category(){ return "RFC"; }

    @Override public List<Reference> references() {
        return List.of(
                new DocReference("def-2",                Defect0002Doc.INSTANCE),
                new DocReference("doc-encapsulated",     EncapsulatedComponentsDoc.INSTANCE),
                new DocReference("rfc-6",                Rfc0006Doc.INSTANCE),
                new DocReference("gotcha-0001-formatted", Gotcha0001Doc.INSTANCE)
        );
    }
}
