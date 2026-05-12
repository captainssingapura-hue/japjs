package hue.captains.singapura.js.homing.studio.docs.releases;

import hue.captains.singapura.js.homing.studio.base.ClasspathMarkdownDoc;
import hue.captains.singapura.js.homing.studio.base.DocReference;
import hue.captains.singapura.js.homing.studio.base.Reference;
import hue.captains.singapura.js.homing.studio.docs.defects.Defect0004Doc;
import hue.captains.singapura.js.homing.studio.docs.doctrines.CatalogueContainerDoc;
import hue.captains.singapura.js.homing.studio.docs.guides.ReleaseChecklistDoc;
import hue.captains.singapura.js.homing.studio.docs.releases.Release0_0_11Doc;
import hue.captains.singapura.js.homing.studio.docs.rfcs.Rfc0005Ext2Doc;
import hue.captains.singapura.js.homing.studio.docs.rfcs.Rfc0006Doc;
import hue.captains.singapura.js.homing.studio.docs.rfcs.Rfc0007Doc;
import hue.captains.singapura.js.homing.studio.docs.rfcs.Rfc0008Doc;

import java.util.List;
import java.util.UUID;

/**
 * Release notes for 0.0.100 (binary — fourth release, predecessor 0.0.11).
 *
 * <p>Two parallel headlines: the framework's <i>sensory layer</i> fills out
 * (RFC 0006 wallpaper backdrops, RFC 0007 audio cues, RFC 0008 interactive
 * theme experiences — all typed end-to-end), and the catalogue tree's shape
 * moves into the type system (RFC 0005-ext2 typed levels L0..L8 + the
 * sub-catalogue / leaf split). One defect closes (Defect 0004 — flat
 * breadcrumbs) as a direct consequence of the typed-levels work.</p>
 */
public record Release0_0_100Doc() implements ClasspathMarkdownDoc {
    private static final UUID ID = UUID.fromString("4f8b1c3d-6a2e-4d9b-8c5f-7e3a1b9d2c4e");
    public static final Release0_0_100Doc INSTANCE = new Release0_0_100Doc();

    @Override public UUID   uuid()    { return ID; }
    @Override public String title()   { return "0.0.100 — Typed Levels, Wallpapers, Audio Cues, Interactive Themes"; }
    @Override public String summary() { return "Sensory layer fills out (RFC 0006 wallpaper backdrops, RFC 0007 typed Cue ADT for theme audio, RFC 0008 interactive theme experiences — Jazz Drum Kit), and the catalogue tree's shape moves into the type system (RFC 0005-ext2 — sealed L0..L8 family, typed parent(), subCatalogues() / leaves() split). Defect 0004 (flat breadcrumbs) closes structurally as a consequence. Six small breaking changes for downstream studios — all caught by the compiler, all covered by the migrate-from-0-0-11 skill."; }
    @Override public String category(){ return "RELEASE"; }

    @Override public List<Reference> references() {
        return List.of(
                new DocReference("rfc-5-ext2",   Rfc0005Ext2Doc.INSTANCE),
                new DocReference("rfc-6",        Rfc0006Doc.INSTANCE),
                new DocReference("rfc-7",        Rfc0007Doc.INSTANCE),
                new DocReference("rfc-8",        Rfc0008Doc.INSTANCE),
                new DocReference("def-4",        Defect0004Doc.INSTANCE),
                new DocReference("doc-cc",       CatalogueContainerDoc.INSTANCE),
                new DocReference("checklist",    ReleaseChecklistDoc.INSTANCE),
                new DocReference("rel-0-0-11",   Release0_0_11Doc.INSTANCE)
        );
    }
}
