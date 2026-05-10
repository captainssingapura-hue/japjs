package hue.captains.singapura.js.homing.studio.docs.defects;

import hue.captains.singapura.js.homing.studio.base.ClasspathMarkdownDoc;

import java.util.UUID;

public record Defect0001Doc() implements ClasspathMarkdownDoc {
    private static final UUID ID = UUID.fromString("0f5c25db-e4a0-471a-8d98-7d75407fdaaa");
    public static final Defect0001Doc INSTANCE = new Defect0001Doc();

    @Override public UUID   uuid()    { return ID; }
    @Override public String title()   { return "Defect 0001 — No App-Kind Abstraction"; }
    @Override public String summary() { return "Plan trackers re-implement ~600 LoC per instance because the framework lacks a curated 'kind' kit. Resolved by the tracker kit."; }
    @Override public String category(){ return "DEFECT"; }
}
