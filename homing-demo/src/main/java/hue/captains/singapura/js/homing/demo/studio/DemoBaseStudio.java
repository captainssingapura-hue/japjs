package hue.captains.singapura.js.homing.demo.studio;

import hue.captains.singapura.js.homing.studio.base.Studio;
import hue.captains.singapura.js.homing.studio.base.app.StudioBrand;

/**
 * RFC 0012 — the Demo studio's typed bundle. Home is {@link DemoStudio};
 * no intrinsic apps, no plans, no themes. Standalone brand is
 * {@code DemoStudio.class} labelled "Homing · demo".
 */
public record DemoBaseStudio() implements Studio<DemoStudio> {

    public static final DemoBaseStudio INSTANCE = new DemoBaseStudio();

    @Override
    public DemoStudio home() { return DemoStudio.INSTANCE; }

    @Override
    public StudioBrand standaloneBrand() {
        return new StudioBrand("Homing · demo", DemoStudio.class);
    }
}
