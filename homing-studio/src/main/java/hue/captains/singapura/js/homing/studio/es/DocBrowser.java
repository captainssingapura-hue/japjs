package hue.captains.singapura.js.homing.studio.es;

import hue.captains.singapura.js.homing.core.AppLink;
import hue.captains.singapura.js.homing.core.AppModule;
import hue.captains.singapura.js.homing.core.ExportsOf;
import hue.captains.singapura.js.homing.core.ImportsFor;
import hue.captains.singapura.js.homing.core.ModuleImports;
import hue.captains.singapura.js.homing.core.util.CssClassName;
import hue.captains.singapura.js.homing.studio.base.app.CatalogueCrumb;
import hue.captains.singapura.js.homing.studio.base.app.DocBrowserAppModule;
import hue.captains.singapura.js.homing.studio.base.app.DocBrowserData;
import hue.captains.singapura.js.homing.studio.base.app.DocBrowserEntry;
import hue.captains.singapura.js.homing.studio.base.app.DocBrowserRenderer;
import hue.captains.singapura.js.homing.studio.base.app.DocReader;
import hue.captains.singapura.js.homing.studio.base.css.StudioStyles;

import java.util.List;

/**
 * Doc browser for the homing studio. Lists every doc the studio knows about,
 * with category filter + free-text search. Auto-generated JS via
 * {@link DocBrowserAppModule}.
 */
public record DocBrowser() implements DocBrowserAppModule<DocBrowser> {

    record appMain() implements AppModule._AppMain<DocBrowser> {}

    public record link() implements AppLink<DocBrowser> {}

    public static final DocBrowser INSTANCE = new DocBrowser();

    @Override public String homeAppSimpleName() { return StudioCatalogue.INSTANCE.simpleName(); }

    private static final List<DocBrowserEntry> DOCS = List.of(
            doc("whitepaper/homing-whitepaper.md",                       "Homing — Main White Paper",                  "The full technical design: four-layer architecture, diagrams, positioning.",                                                                       "WHITEPAPER", "White Papers", StudioStyles.st_badge_whitepaper.class),
            doc("whitepaper/homing-shell-flexibility-whitepaper.md",     "Shell Flexibility — Exploration",           "Homing's output is shell-agnostic. The Java backend with CLI parity is the killer feature.",                                                       "WHITEPAPER", "White Papers", StudioStyles.st_badge_whitepaper.class),
            doc("brochure/00-index.md",                                  "00 — Brochure Index",                        "Cover sheet with reading-time tracks (3 / 10 / 20 / 45 min).",                                                                                      "BROCHURE",   "Brochure",     StudioStyles.st_badge_brochure.class),
            doc("brochure/01-executive-summary.md",                      "01 — Executive Summary",                     "One-page pitch with the built/designed capability table.",                                                                                          "BROCHURE",   "Brochure",     StudioStyles.st_badge_brochure.class),
            doc("brochure/02-business-case.md",                          "02 — Business Case",                         "Costs, target workloads, risks.",                                                                                                                    "BROCHURE",   "Brochure",     StudioStyles.st_badge_brochure.class),
            doc("brochure/03-competitive-landscape.md",                  "03 — Competitive Landscape",                 "Side-by-side vs. React, Vaadin, Hilla, JHipster, htmx.",                                                                                            "BROCHURE",   "Brochure",     StudioStyles.st_badge_brochure.class),
            doc("brochure/04-pilot-proposal.md",                         "04 — Pilot Proposal",                        "4–6 weeks, 4 success metrics, weekly milestones, decision gates.",                                                                                  "BROCHURE",   "Brochure",     StudioStyles.st_badge_brochure.class),
            doc("brochure/05-faq.md",                                    "05 — FAQ & Objection Handling",              "25 honest Q&As across strategy, risk, tech, people, cost.",                                                                                         "BROCHURE",   "Brochure",     StudioStyles.st_badge_brochure.class),
            doc("brochure/06-architecture-at-a-glance.md",               "06 — Architecture at a Glance",              "Visual summary with built-vs-designed status table.",                                                                                               "BROCHURE",   "Brochure",     StudioStyles.st_badge_brochure.class),
            doc("rfcs/0001-app-registry-and-typed-nav.md",               "RFC 0001 — App Registry & Typed Nav",        "Friendly-name URL contract, AppLink<L>, ProxyApp, conformance enforcement.",                                                                        "RFC",        "RFCs",         StudioStyles.st_badge_rfc.class),
            doc("rfcs/0002-typed-themes-for-cssgroups.md",               "RFC 0002 — Typed Themes for CssGroups",      "Header/Impl split: typed Theme records, per-CssGroup Impl<TH> nested interfaces, registry-based resolution, no silent fallbacks.",                  "RFC",        "RFCs",         StudioStyles.st_badge_rfc.class),
            doc("rfcs/0003-themeable-form-and-components.md",            "RFC 0003 — Themeable Form & Component Primitive", "Add Component<C> + ComponentImpl<C, TH> so themes can vary form, not just paint. Two render modes, asset management, F2 scope.",               "RFC",        "RFCs",         StudioStyles.st_badge_rfc.class),
            doc("brand/README.md",                                       "Brand Guide",                                "Logo concept, asset inventory, palette, typography, usage rules.",                                                                                  "BRAND",      "Brand",        StudioStyles.st_badge_brand.class),
            doc("brand/RENAME-TO-HOMING.md",                             "Rename Dossier — japjs → Homing",            "Decision context, three-layer metaphor, migration logistics.",                                                                                      "BRAND",      "Brand",        StudioStyles.st_badge_brand.class),
            doc("rename/EXECUTION-PLAN.md",                              "Rename Execution Plan",                      "Six-phase migration plan with verification gates and rollback strategy.",                                                                          "RENAME",     "Rename",       StudioStyles.st_badge_rename.class),
            doc("blocks/index.md",                                       "Building Blocks — Index",                    "Top-level index of every reusable kit, atom, and primitive in homing-studio-base. Promise-per-goal table.",                                       "BLOCK",      "Building Blocks", StudioStyles.st_badge_reference.class),
            doc("blocks/atoms.md",                                       "Block 01 — Atoms (StudioElements)",          "13 visual builders: Header, Card, Pill, Section, Footer, StatusBadge, OverallProgress, StepCard, DecisionCard, TodoList, MetricsTable, Panel, Brand.", "BLOCK",      "Building Blocks", StudioStyles.st_badge_reference.class),
            doc("blocks/catalogue-kit.md",                               "Block 02 — Catalogue Kit",                   "CatalogueAppModule — auto-generates a launcher / sub-catalogue / index page from typed Java data.",                                                "BLOCK",      "Building Blocks", StudioStyles.st_badge_reference.class),
            doc("blocks/doc-kits.md",                                    "Block 03 — DocBrowser & DocReader Kits",     "Searchable card grid + shared markdown reader. Pair them and your studio has a documentation surface with zero JS.",                              "BLOCK",      "Building Blocks", StudioStyles.st_badge_reference.class),
            doc("blocks/tracker-kit.md",                                 "Block 04 — Tracker Kit",                     "PlanAppModule + PlanStepAppModule. Two-page tracker for any multi-phase plan. Implement Plan, get a working tracker.",                            "BLOCK",      "Building Blocks", StudioStyles.st_badge_reference.class),
            doc("blocks/bootstrap-and-conformance.md",                   "Block 05 — Bootstrap & Conformance",         "StudioBootstrap.start(...) one-call server entrypoint + the five conformance test bases.",                                                         "BLOCK",      "Building Blocks", StudioStyles.st_badge_reference.class),
            doc("guides/live-tracker-pattern.md",                        "Live Tracker Pattern",                       "Legacy guide for the per-plan tracker pattern. Superseded by the tracker kit (PlanAppModule + PlanRenderer).",                                     "GUIDE",      "Guides",       StudioStyles.st_badge_reference.class),
            doc("defects/0001-no-app-kind-abstraction.md",               "Defect 0001 — No App-Kind Abstraction",      "Plan trackers re-implement ~600 LoC per instance because the framework lacks a curated 'kind' kit. Resolved by the tracker kit.",                  "DEFECT",     "Defects",      StudioStyles.st_badge_reference.class),
            doc("defects/0002-themes-cannot-vary-form.md",               "Defect 0002 — Themes Vary Paint, Not Form",  "Theme primitives only vary colours/sizes. No way to reshape a card or platform per theme. Resolution drafted in RFC 0003.",                       "DEFECT",     "Defects",      StudioStyles.st_badge_reference.class),
            doc("doctrines/pure-component-views.md",                     "Doctrine — Pure-Component Views",            "No HTML in consumer code. Every UI element is a Component invocation. Required reading.",                                                            "DOCTRINE",   "Doctrines",    StudioStyles.st_badge_reference.class),
            doc("doctrines/methods-over-props.md",                       "Doctrine — Methods Over Props",              "Components are objects, not functions of props. OO with pragmatic functional, not React's handicapped pure functional.",                            "DOCTRINE",   "Doctrines",    StudioStyles.st_badge_reference.class),
            doc("doctrines/managed-dom-ops.md",                          "Doctrine — Managed DOM Ops (SPA scope)",     "In SPA consumer code, every DOM mutation flows through one typed gateway. Imperative / game / animation contexts may use the DOM API directly.",   "DOCTRINE",   "Doctrines",    StudioStyles.st_badge_reference.class),
            doc("doctrines/owned-references.md",                         "Doctrine — Owned References",                "Every element has exactly one owner. No getElementById / querySelector. Act via method calls on handles, not lookups.",                              "DOCTRINE",   "Doctrines",    StudioStyles.st_badge_reference.class),
            doc("SESSION-SUMMARY-2026-04-25.md",                         "Session Summary — 2026-04-25",               "Comprehensive recap of the design session that built the brochure suite.",                                                                          "SESSION",    "Session Notes", StudioStyles.st_badge_session.class),
            doc("ACTION-PLAN-2026-04-25.md",                             "Action Plan — 2026-04-25",                   "Phase-by-phase execution plan with decision gates and risk register.",                                                                              "SESSION",    "Session Notes", StudioStyles.st_badge_session.class),
            doc("comparison/homing-vs-react-vue.md",                     "Homing vs React / Vue",                      "Honest comparison, fair assessment of strengths and gaps.",                                                                                         "REFERENCE",  "Reference",    StudioStyles.st_badge_reference.class),
            doc("user-guide.md",                                         "User Guide",                                 "Getting-started reference for framework users.",                                                                                                    "REFERENCE",  "Reference",    StudioStyles.st_badge_reference.class)
    );

    private static DocBrowserEntry doc(String path, String title, String summary,
                                       String category, String catLabel,
                                       Class<? extends hue.captains.singapura.js.homing.core.CssClass> badgeClass) {
        return new DocBrowserEntry(path, title, summary, category, catLabel,
                CssClassName.toCssName(badgeClass));
    }

    @Override
    public DocBrowserData docBrowserData() {
        return new DocBrowserData(
                "documents",
                "Browse",
                "Every white paper, brochure, RFC, brand artifact, session note, and reference doc — searchable and filterable.",
                List.of(
                        new CatalogueCrumb("Home",      "/app?app=" + StudioCatalogue.INSTANCE.simpleName()),
                        new CatalogueCrumb("Documents", null)
                ),
                DocReader.INSTANCE.simpleName(),
                DOCS,
                "Documents are read live from docs/ on the server. Add a new doc by editing the docs[] array in DocBrowser.java."
        );
    }

    @Override
    public ImportsFor<DocBrowser> imports() {
        return ImportsFor.<DocBrowser>builder()
                .add(new ModuleImports<>(List.of(new StudioCatalogue.link()), StudioCatalogue.INSTANCE))
                .add(new ModuleImports<>(List.of(new DocReader.link()),       DocReader.INSTANCE))
                .add(new ModuleImports<>(List.of(new DocBrowserRenderer.renderDocBrowser()), DocBrowserRenderer.INSTANCE))
                .build();
    }

    @Override
    public ExportsOf<DocBrowser> exports() {
        return new ExportsOf<>(INSTANCE, List.of(new appMain()));
    }
}
