package hue.captains.singapura.js.homing.studio.docs.doctrines;

import hue.captains.singapura.js.homing.studio.base.ClasspathMarkdownDoc;
import hue.captains.singapura.js.homing.studio.base.DocReference;
import hue.captains.singapura.js.homing.studio.base.Reference;

import java.util.List;
import java.util.UUID;

public record DualAudienceSkillsDoc() implements ClasspathMarkdownDoc {
    private static final UUID ID = UUID.fromString("7e9c4d2a-1f8b-4a3c-95d7-2b6e4f8c9d50");
    public static final DualAudienceSkillsDoc INSTANCE = new DualAudienceSkillsDoc();

    @Override public UUID   uuid()    { return ID; }
    @Override public String title()   { return "Doctrine — Dual-Audience Skills"; }
    @Override public String summary() { return "Skills are not only for agents. Every skill serves two readers — the agent that follows the SKILL.md to execute a task, and the human that reads it to understand. One source of truth, two delivery modes; if either mode silently rots, the skill is broken."; }
    @Override public String category(){ return "DOCTRINE"; }

    @Override public List<Reference> references() {
        return List.of(
                new DocReference("first-user", FirstUserDoc.INSTANCE),
                new DocReference("cc",         CatalogueContainerDoc.INSTANCE),
                new DocReference("pc",         PlanContainerDoc.INSTANCE)
        );
    }
}
