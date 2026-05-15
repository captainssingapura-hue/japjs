package hue.captains.singapura.js.homing.studio.base;

import hue.captains.singapura.js.homing.core.AppModule;
import hue.captains.singapura.js.homing.core.Theme;
import hue.captains.singapura.js.homing.studio.base.app.Catalogue;
import hue.captains.singapura.js.homing.studio.base.app.L0_Catalogue;
import hue.captains.singapura.js.homing.studio.base.app.StudioBrand;
import hue.captains.singapura.js.homing.studio.base.tracker.Plan;

import java.util.List;

/**
 * RFC 0012 — a studio's intrinsic typed declaration of what it brings to a
 * server. Plain interface (no Standard / Extended split); every field
 * defaults except {@link #home()}. The studio's author owns this
 * implementation; the harness owns wrapping (see {@link Fixtures}).
 *
 * <p>A studio is a record whose body is six accessors. {@link #home()} is the
 * one required field — every other field has a sensible empty default. A
 * studio whose catalogue tree is wholly reachable from {@code home()} (via
 * {@link Catalogue#subCatalogues()}) needs to override nothing else but its
 * apps / plans / themes / standalone brand.</p>
 *
 * <pre>{@code
 * public record HomingStudio() implements Studio<StudioCatalogue> {
 *     public static final HomingStudio INSTANCE = new HomingStudio();
 *
 *     @Override public StudioCatalogue home()           { return StudioCatalogue.INSTANCE; }
 *     @Override public List<AppModule<?,?>> apps()      { return List.of(DocBrowser.INSTANCE); }
 *     @Override public List<Plan> plans()               { return List.of(Rfc0001PlanData.INSTANCE); }
 *     @Override public StudioBrand standaloneBrand()    {
 *         return new StudioBrand("Homing · studio", StudioCatalogue.class,
 *                                new SvgRef<>(StudioLogo.INSTANCE, new StudioLogo.logo()));
 *     }
 * }
 * }</pre>
 *
 * @param <L0> the studio's root L0 catalogue type
 */
public interface Studio<L0 extends L0_Catalogue<L0>> {

    /** The studio's root L0 catalogue. The only required field. */
    L0 home();

    /**
     * Full catalogue closure. Default: BFS from {@link #home()} via
     * {@link Catalogue#subCatalogues()}. Override only to include orphan
     * catalogues not reachable from the home tree.
     */
    default List<? extends Catalogue<?>> catalogues() {
        return CatalogueClosure.INSTANCE.walk(home());
    }

    /**
     * AppModules this studio depends on — custom server-side apps the studio
     * itself ships (e.g. a doc browser, a per-studio dashboard). Excludes
     * the harness apps ({@code CatalogueAppHost}, {@code PlanAppHost},
     * {@code DocReader}, {@code ThemesIntro}) — those live on
     * {@link Fixtures#harnessApps()}.
     */
    default List<AppModule<?, ?>> apps() { return List.of(); }

    /** Plans this studio ships. */
    default List<Plan> plans() { return List.of(); }

    /** Themes this studio contributes — merged into the server's ThemeRegistry. */
    default List<Theme> themes() { return List.of(); }

    /**
     * Brand for standalone deploy. Ignored when composed under an umbrella —
     * the harness's brand wins (see {@link Fixtures#brand()}). {@code null}
     * is valid for studios that only ever run under an umbrella.
     */
    default StudioBrand standaloneBrand() { return null; }
}
