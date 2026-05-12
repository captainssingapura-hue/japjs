package hue.captains.singapura.js.homing.studio.es;

import hue.captains.singapura.js.homing.core.AppLink;
import hue.captains.singapura.js.homing.core.AppModule;
import hue.captains.singapura.js.homing.core.ExportsOf;
import hue.captains.singapura.js.homing.core.ImportsFor;
import hue.captains.singapura.js.homing.core.ModuleImports;
import hue.captains.singapura.js.homing.core.util.CssClassName;
import hue.captains.singapura.js.homing.studio.base.Doc;
import hue.captains.singapura.js.homing.studio.base.DocProvider;
import hue.captains.singapura.js.homing.studio.base.app.CatalogueCrumb;
import hue.captains.singapura.js.homing.studio.base.app.DocBrowserAppModule;
import hue.captains.singapura.js.homing.studio.base.app.DocBrowserData;
import hue.captains.singapura.js.homing.studio.base.app.DocBrowserEntry;
import hue.captains.singapura.js.homing.studio.base.app.DocBrowserRenderer;
import hue.captains.singapura.js.homing.studio.base.app.DocReader;
import hue.captains.singapura.js.homing.studio.base.css.StudioStyles;
import hue.captains.singapura.js.homing.studio.docs.brand.BrandReadmeDoc;
import hue.captains.singapura.js.homing.studio.docs.brand.RenameToHomingDoc;
import hue.captains.singapura.js.homing.studio.docs.comparison.HomingVsReactVueDoc;
import hue.captains.singapura.js.homing.studio.docs.defects.Defect0001Doc;
import hue.captains.singapura.js.homing.studio.docs.defects.Defect0002Doc;
import hue.captains.singapura.js.homing.studio.docs.defects.Defect0003Doc;
import hue.captains.singapura.js.homing.studio.docs.defects.Defect0004Doc;
import hue.captains.singapura.js.homing.studio.docs.doctrines.CatalogueContainerDoc;
import hue.captains.singapura.js.homing.studio.docs.doctrines.ManagedDomOpsDoc;
import hue.captains.singapura.js.homing.studio.docs.doctrines.MethodsOverPropsDoc;
import hue.captains.singapura.js.homing.studio.docs.doctrines.OwnedReferencesDoc;
import hue.captains.singapura.js.homing.studio.docs.doctrines.PlanContainerDoc;
import hue.captains.singapura.js.homing.studio.docs.doctrines.DualAudienceSkillsDoc;
import hue.captains.singapura.js.homing.studio.docs.doctrines.EncapsulatedComponentsDoc;
import hue.captains.singapura.js.homing.studio.docs.doctrines.PerceivableSurfaceDoc;
import hue.captains.singapura.js.homing.studio.docs.gotchas.Gotcha0001Doc;
import hue.captains.singapura.js.homing.studio.docs.doctrines.FirstUserDoc;
import hue.captains.singapura.js.homing.studio.docs.doctrines.PureComponentViewsDoc;
import hue.captains.singapura.js.homing.studio.docs.guides.LiveTrackerPatternDoc;
import hue.captains.singapura.js.homing.studio.docs.guides.ReleaseChecklistDoc;
import hue.captains.singapura.js.homing.studio.docs.guides.UserGuideDoc;
import hue.captains.singapura.js.homing.studio.docs.releases.Release0_0_100Doc;
import hue.captains.singapura.js.homing.studio.docs.releases.Release0_0_11Doc;
import hue.captains.singapura.js.homing.studio.docs.rename.RenameExecutionPlanDoc;
import hue.captains.singapura.js.homing.studio.docs.rfcs.Rfc0001Doc;
import hue.captains.singapura.js.homing.studio.docs.rfcs.Rfc0002Doc;
import hue.captains.singapura.js.homing.studio.docs.rfcs.Rfc0002Ext1Doc;
import hue.captains.singapura.js.homing.studio.docs.rfcs.Rfc0003Doc;
import hue.captains.singapura.js.homing.studio.docs.rfcs.Rfc0004Doc;
import hue.captains.singapura.js.homing.studio.docs.rfcs.Rfc0004Ext1Doc;
import hue.captains.singapura.js.homing.studio.docs.rfcs.Rfc0005Doc;
import hue.captains.singapura.js.homing.studio.docs.rfcs.Rfc0005Ext1Doc;
import hue.captains.singapura.js.homing.studio.docs.rfcs.Rfc0005Ext2Doc;
import hue.captains.singapura.js.homing.studio.docs.rfcs.Rfc0006Doc;
import hue.captains.singapura.js.homing.studio.docs.rfcs.Rfc0007Doc;
import hue.captains.singapura.js.homing.studio.docs.rfcs.Rfc0008Doc;
import hue.captains.singapura.js.homing.studio.docs.whitepaper.HomingShellFlexibilityWhitepaperDoc;
import hue.captains.singapura.js.homing.studio.docs.whitepaper.HomingWhitepaperDoc;

import java.util.List;

/**
 * Doc browser for the homing studio. Lists every public doc the studio knows about,
 * with category filter + free-text search. Auto-generated JS via
 * {@link DocBrowserAppModule}.
 *
 * <p>Per RFC 0004, the browser is a {@link DocProvider} that contributes its full set
 * of {@link Doc}s to the studio's {@code DocRegistry}. Each {@link DocBrowserEntry}
 * references a typed Doc record; titles, summaries, and identities all flow from the
 * Doc itself.</p>
 */
public record DocBrowser() implements DocBrowserAppModule<DocBrowser>, DocProvider {

    record appMain() implements AppModule._AppMain<AppModule._None, DocBrowser> {}

    public record link() implements AppLink<DocBrowser> {}

    public static final DocBrowser INSTANCE = new DocBrowser();

    // Catalogue tile name + summary live on the wrapping Navigable
    // (RFC 0005-ext1 / v1 reframe) — see StudioCatalogue.entries().

    /** RFC 0005: studio home is the StudioCatalogue served by CatalogueAppHost. */
    @Override public String homeUrl() { return hue.captains.singapura.js.homing.studio.base.app.CatalogueAppHost.urlFor(StudioCatalogue.class); }

    /** Every doc this browser displays, in order. Doubles as the {@link DocProvider} contribution. */
    private static final List<DocBrowserEntry> ENTRIES = List.of(
            entry(HomingWhitepaperDoc.INSTANCE,                   "White Papers", StudioStyles.st_badge_whitepaper.class),
            entry(HomingShellFlexibilityWhitepaperDoc.INSTANCE,   "White Papers", StudioStyles.st_badge_whitepaper.class),

            entry(Rfc0001Doc.INSTANCE,         "RFCs", StudioStyles.st_badge_rfc.class),
            entry(Rfc0002Doc.INSTANCE,         "RFCs", StudioStyles.st_badge_rfc.class),
            entry(Rfc0002Ext1Doc.INSTANCE,     "RFCs", StudioStyles.st_badge_rfc.class),
            entry(Rfc0003Doc.INSTANCE,         "RFCs", StudioStyles.st_badge_rfc.class),
            entry(Rfc0004Doc.INSTANCE,         "RFCs", StudioStyles.st_badge_rfc.class),
            entry(Rfc0004Ext1Doc.INSTANCE,     "RFCs", StudioStyles.st_badge_rfc.class),
            entry(Rfc0005Doc.INSTANCE,         "RFCs", StudioStyles.st_badge_rfc.class),
            entry(Rfc0005Ext1Doc.INSTANCE,     "RFCs", StudioStyles.st_badge_rfc.class),
            entry(Rfc0005Ext2Doc.INSTANCE,     "RFCs", StudioStyles.st_badge_rfc.class),
            entry(Rfc0006Doc.INSTANCE,         "RFCs", StudioStyles.st_badge_rfc.class),
            entry(Rfc0007Doc.INSTANCE,         "RFCs", StudioStyles.st_badge_rfc.class),
            entry(Rfc0008Doc.INSTANCE,         "RFCs", StudioStyles.st_badge_rfc.class),

            entry(BrandReadmeDoc.INSTANCE,     "Brand", StudioStyles.st_badge_brand.class),
            entry(RenameToHomingDoc.INSTANCE,  "Brand", StudioStyles.st_badge_brand.class),

            entry(RenameExecutionPlanDoc.INSTANCE, "Rename", StudioStyles.st_badge_rename.class),

            entry(LiveTrackerPatternDoc.INSTANCE, "Guides", StudioStyles.st_badge_reference.class),
            entry(ReleaseChecklistDoc.INSTANCE,   "Guides", StudioStyles.st_badge_reference.class),

            entry(Release0_0_100Doc.INSTANCE,  "Releases", StudioStyles.st_badge_reference.class),
            entry(Release0_0_11Doc.INSTANCE,   "Releases", StudioStyles.st_badge_reference.class),

            entry(Defect0001Doc.INSTANCE,      "Defects", StudioStyles.st_badge_reference.class),
            entry(Defect0002Doc.INSTANCE,      "Defects", StudioStyles.st_badge_reference.class),
            entry(Defect0003Doc.INSTANCE,      "Defects", StudioStyles.st_badge_reference.class),
            entry(Defect0004Doc.INSTANCE,      "Defects", StudioStyles.st_badge_reference.class),

            entry(Gotcha0001Doc.INSTANCE,      "Gotchas", StudioStyles.st_badge_reference.class),

            entry(FirstUserDoc.INSTANCE,          "Doctrines", StudioStyles.st_badge_reference.class),
            entry(DualAudienceSkillsDoc.INSTANCE, "Doctrines", StudioStyles.st_badge_reference.class),
            entry(PureComponentViewsDoc.INSTANCE, "Doctrines", StudioStyles.st_badge_reference.class),
            entry(EncapsulatedComponentsDoc.INSTANCE, "Doctrines", StudioStyles.st_badge_reference.class),
            entry(PerceivableSurfaceDoc.INSTANCE,     "Doctrines", StudioStyles.st_badge_reference.class),
            entry(MethodsOverPropsDoc.INSTANCE,   "Doctrines", StudioStyles.st_badge_reference.class),
            entry(ManagedDomOpsDoc.INSTANCE,      "Doctrines", StudioStyles.st_badge_reference.class),
            entry(OwnedReferencesDoc.INSTANCE,    "Doctrines", StudioStyles.st_badge_reference.class),
            entry(CatalogueContainerDoc.INSTANCE, "Doctrines", StudioStyles.st_badge_reference.class),
            entry(PlanContainerDoc.INSTANCE,      "Doctrines", StudioStyles.st_badge_reference.class),

            entry(HomingVsReactVueDoc.INSTANCE, "Reference", StudioStyles.st_badge_reference.class),
            entry(UserGuideDoc.INSTANCE,        "Reference", StudioStyles.st_badge_reference.class)
    );

    private static DocBrowserEntry entry(Doc doc, String catLabel,
                                         Class<? extends hue.captains.singapura.js.homing.core.CssClass> badgeClass) {
        return new DocBrowserEntry(doc, catLabel, CssClassName.toCssName(badgeClass));
    }

    @Override
    public List<Doc> docs() {
        return ENTRIES.stream().map(DocBrowserEntry::doc).toList();
    }

    @Override
    public DocBrowserData docBrowserData() {
        return new DocBrowserData(
                "documents",
                "Browse",
                "Every white paper, RFC, brand artifact, doctrine, and reference doc — searchable and filterable.",
                List.of(
                        new CatalogueCrumb("Home",      hue.captains.singapura.js.homing.studio.base.app.CatalogueAppHost.urlFor(StudioCatalogue.class)),
                        new CatalogueCrumb("Documents", null)
                ),
                DocReader.INSTANCE.simpleName(),
                ENTRIES,
                "Documents are typed Java records (RFC 0004). Each Doc carries a UUID; URLs survive renames and file moves. Add a new doc by creating a record under hue.captains.singapura.js.homing.studio.docs.<area>.<NameDoc> with its companion .md next to it on the classpath."
        );
    }

    @Override
    public ImportsFor<DocBrowser> imports() {
        return ImportsFor.<DocBrowser>builder()
                .add(new ModuleImports<>(List.of(new DocReader.link()),       DocReader.INSTANCE))
                .add(new ModuleImports<>(List.of(new DocBrowserRenderer.renderDocBrowser()), DocBrowserRenderer.INSTANCE))
                .build();
    }

    @Override
    public ExportsOf<DocBrowser> exports() {
        return new ExportsOf<>(INSTANCE, List.of(new appMain()));
    }
}
