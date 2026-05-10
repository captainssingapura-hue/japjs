package hue.captains.singapura.js.homing.studio.docs.rfcs;

import hue.captains.singapura.js.homing.studio.base.ClasspathMarkdownDoc;

import java.util.UUID;

public record Rfc0002Ext1Doc() implements ClasspathMarkdownDoc {
    private static final UUID ID = UUID.fromString("33f77201-1bc5-4557-a944-a86da2827cbd");
    public static final Rfc0002Ext1Doc INSTANCE = new Rfc0002Ext1Doc();

    @Override public UUID   uuid()    { return ID; }
    @Override public String title()   { return "RFC 0002 ext1 — Utility-First & Semantic Tokens"; }
    @Override public String summary() { return "Add semantic tokens on top of typed themes — utility-first composition without losing type safety."; }
    @Override public String category(){ return "RFC"; }
}
