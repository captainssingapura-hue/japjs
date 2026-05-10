package hue.captains.singapura.js.homing.studio.docs.rfcs;

import hue.captains.singapura.js.homing.studio.base.ClasspathMarkdownDoc;

import java.util.UUID;

public record Rfc0002Doc() implements ClasspathMarkdownDoc {
    private static final UUID ID = UUID.fromString("39fb3476-0673-4333-802e-eb2a0e99e86a");
    public static final Rfc0002Doc INSTANCE = new Rfc0002Doc();

    @Override public UUID   uuid()    { return ID; }
    @Override public String title()   { return "RFC 0002 — Typed Themes for CssGroups"; }
    @Override public String summary() { return "Header/Impl split: typed Theme records, per-CssGroup Impl<TH> nested interfaces, registry-based resolution, no silent fallbacks."; }
    @Override public String category(){ return "RFC"; }
}
