package hue.captains.singapura.js.homing.demo.studio;

import hue.captains.singapura.js.homing.core.AppModule;
import hue.captains.singapura.js.homing.studio.base.Doc;
import hue.captains.singapura.js.homing.studio.base.DocProvider;
import hue.captains.singapura.js.homing.studio.base.app.Entry;
import hue.captains.singapura.js.homing.studio.base.app.L0_Catalogue;
import hue.captains.singapura.js.homing.studio.base.app.Navigable;
import hue.captains.singapura.js.homing.studio.base.app.tree.TreeAppHost;
import hue.captains.singapura.js.homing.studio.base.theme.ThemesIntro;

import java.util.List;

/**
 * Home catalogue for the demo studio. Stateless record per RFC 0005 — the
 * shared {@code CatalogueAppHost} renders it. Also implements {@link DocProvider}
 * so {@link DemoIntroDoc} is reachable through the studio's {@code DocRegistry}
 * (required for {@code Entry.OfDoc} reachability validation at boot).
 */
public record DemoStudio() implements L0_Catalogue<DemoStudio>, DocProvider {

    public static final DemoStudio INSTANCE = new DemoStudio();

    @Override public String name()    { return "Demo Studio"; }
    @Override public String summary() { return "A tiny dogfood studio for homing-studio-base — branded with the turtle, running on its own port, configured in one file."; }

    @Override public List<Entry<DemoStudio>> leaves() {
        return List.of(
                Entry.of(this, DemoIntroDoc.INSTANCE),
                Entry.of(this, ComposedDemoDoc.INSTANCE),
                Entry.of(this, TableDemoDoc.INSTANCE),
                Entry.of(this, ImageDemoDoc.INSTANCE),
                Entry.of(this, new Navigable<>(
                        ThemesIntro.INSTANCE,
                        AppModule._None.INSTANCE,
                        "Themes",
                        "Palette previews and one-click activation for Default / Forest / Sunset / Bauhaus.")),
                Entry.of(this, new Navigable<>(
                        TreeAppHost.INSTANCE,
                        new TreeAppHost.Params("animals", null),
                        "Animals & Halloween",
                        "RFC 0016 ContentTree demo — cute SVG critters categorised into two branches."))
        );
    }

    @Override public List<Doc> docs() {
        return List.of(DemoIntroDoc.INSTANCE);
    }
}
