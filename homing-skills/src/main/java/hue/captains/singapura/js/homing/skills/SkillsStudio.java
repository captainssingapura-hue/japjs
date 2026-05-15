package hue.captains.singapura.js.homing.skills;

import hue.captains.singapura.js.homing.studio.base.Studio;
import hue.captains.singapura.js.homing.studio.base.app.StudioBrand;

/**
 * RFC 0012 — the Skills studio's typed bundle. Home is {@link SkillsHome};
 * no intrinsic apps (the harness covers everything), no plans, no themes.
 */
public record SkillsStudio() implements Studio<SkillsHome> {

    public static final SkillsStudio INSTANCE = new SkillsStudio();

    @Override
    public SkillsHome home() { return SkillsHome.INSTANCE; }

    @Override
    public StudioBrand standaloneBrand() {
        return new StudioBrand("Homing · skills", SkillsHome.class);
    }
}
