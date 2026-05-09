package hue.captains.singapura.js.homing.studio.rfc0002ext1;

import hue.captains.singapura.js.homing.core.AppLink;
import hue.captains.singapura.js.homing.core.AppModule;
import hue.captains.singapura.js.homing.core.ExportsOf;
import hue.captains.singapura.js.homing.core.ImportsFor;
import hue.captains.singapura.js.homing.core.ModuleImports;
import hue.captains.singapura.js.homing.studio.base.app.DocReader;
import hue.captains.singapura.js.homing.studio.es.JourneysCatalogue;
import hue.captains.singapura.js.homing.studio.es.StudioCatalogue;
import hue.captains.singapura.js.homing.studio.base.tracker.Plan;
import hue.captains.singapura.js.homing.studio.base.tracker.PlanAppModule;
import hue.captains.singapura.js.homing.studio.base.tracker.PlanRenderer;

import java.util.List;

/** Index page for the RFC 0002-ext1 (Utility-First + Semantic Tokens) tracker. */
public record Rfc0002Ext1Plan() implements PlanAppModule<Rfc0002Ext1Plan> {

    record appMain() implements AppModule._AppMain<Rfc0002Ext1Plan> {}

    public record link() implements AppLink<Rfc0002Ext1Plan> {}

    public static final Rfc0002Ext1Plan INSTANCE = new Rfc0002Ext1Plan();

    @Override public Plan   plan()              { return Rfc0002Ext1PlanData.INSTANCE; }
    @Override public String stepAppSimpleName() { return Rfc0002Ext1Step.INSTANCE.simpleName(); }

    @Override
    public ImportsFor<Rfc0002Ext1Plan> imports() {
        return ImportsFor.<Rfc0002Ext1Plan>builder()
                .add(new ModuleImports<>(List.of(new StudioCatalogue.link()),    StudioCatalogue.INSTANCE))
                .add(new ModuleImports<>(List.of(new JourneysCatalogue.link()),  JourneysCatalogue.INSTANCE))
                .add(new ModuleImports<>(List.of(new Rfc0002Ext1Step.link()),    Rfc0002Ext1Step.INSTANCE))
                .add(new ModuleImports<>(List.of(new DocReader.link()),          DocReader.INSTANCE))
                .add(new ModuleImports<>(List.of(new PlanRenderer.renderPlan()), PlanRenderer.INSTANCE))
                .build();
    }

    @Override
    public ExportsOf<Rfc0002Ext1Plan> exports() {
        return new ExportsOf<>(INSTANCE, List.of(new appMain()));
    }
}
