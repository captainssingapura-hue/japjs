package hue.captains.singapura.js.homing.studio;

import java.util.List;

/**
 * Catalog of documents the studio knows about.
 * Each document is identified by a relative path under the configured docs root,
 * grouped by category for browsing.
 *
 * <p>Adding a new document: add a new {@link Doc} to {@link #ALL}. Path is relative
 * to the docs root configured on {@link DocContentGetAction}.</p>
 */
public final class DocRegistry {

    public enum Category {
        WHITEPAPER("White Papers"),
        BROCHURE("Brochure"),
        RFC("RFCs"),
        BRAND("Brand"),
        RENAME("Rename"),
        SESSION("Session Notes"),
        REFERENCE("Reference");

        public final String label;
        Category(String label) { this.label = label; }
    }

    public record Doc(
            String path,        // relative path under docs root, e.g. "whitepaper/japjs-whitepaper.md"
            String title,       // display title
            String summary,     // one-line description
            Category category
    ) {}

    public static final List<Doc> ALL = List.of(
            // White papers
            new Doc("whitepaper/japjs-whitepaper.md",
                    "japjs — Main White Paper",
                    "The full technical design: four-layer architecture, diagrams, positioning.",
                    Category.WHITEPAPER),
            new Doc("whitepaper/japjs-shell-flexibility-whitepaper.md",
                    "Shell Flexibility — Exploration",
                    "japjs's output is shell-agnostic. The Java backend with CLI parity is the killer feature.",
                    Category.WHITEPAPER),

            // Brochure
            new Doc("brochure/00-index.md",
                    "00 — Brochure Index",
                    "Cover sheet with reading-time tracks (3 / 10 / 20 / 45 min).",
                    Category.BROCHURE),
            new Doc("brochure/01-executive-summary.md",
                    "01 — Executive Summary",
                    "One-page pitch with the built/designed capability table.",
                    Category.BROCHURE),
            new Doc("brochure/02-business-case.md",
                    "02 — Business Case",
                    "Costs, target workloads, risks.",
                    Category.BROCHURE),
            new Doc("brochure/03-competitive-landscape.md",
                    "03 — Competitive Landscape",
                    "Side-by-side vs. React, Vaadin, Hilla, JHipster, htmx.",
                    Category.BROCHURE),
            new Doc("brochure/04-pilot-proposal.md",
                    "04 — Pilot Proposal",
                    "4–6 weeks, 4 success metrics, weekly milestones, decision gates.",
                    Category.BROCHURE),
            new Doc("brochure/05-faq.md",
                    "05 — FAQ & Objection Handling",
                    "25 honest Q&As across strategy, risk, tech, people, cost.",
                    Category.BROCHURE),
            new Doc("brochure/06-architecture-at-a-glance.md",
                    "06 — Architecture at a Glance",
                    "Visual summary with built-vs-designed status table.",
                    Category.BROCHURE),

            // RFCs
            new Doc("rfcs/0001-app-registry-and-typed-nav.md",
                    "RFC 0001 — App Registry & Typed Navigation",
                    "Friendly-name URL contract, AppLink<L>, ProxyApp, conformance enforcement.",
                    Category.RFC),

            // Brand
            new Doc("brand/README.md",
                    "Brand Guide",
                    "Logo concept, asset inventory, palette, typography, usage rules.",
                    Category.BRAND),
            // Rename
            new Doc("brand/RENAME-TO-HOMING.md",
                    "Rename Dossier — japjs → Homing",
                    "Decision context, three-layer metaphor, migration logistics.",
                    Category.RENAME),
            new Doc("rename/EXECUTION-PLAN.md",
                    "Rename Execution Plan",
                    "Six-phase migration plan with verification gates and rollback strategy.",
                    Category.RENAME),

            // Session
            new Doc("SESSION-SUMMARY-2026-04-25.md",
                    "Session Summary — 2026-04-25",
                    "Comprehensive recap of the design session that built the brochure suite.",
                    Category.SESSION),
            new Doc("ACTION-PLAN-2026-04-25.md",
                    "Action Plan — 2026-04-25",
                    "Phase-by-phase execution plan with decision gates and risk register.",
                    Category.SESSION),

            // Reference
            new Doc("comparison/japjs-vs-react-vue.md",
                    "japjs vs React / Vue",
                    "Honest comparison, fair assessment of strengths and gaps.",
                    Category.REFERENCE),
            new Doc("user-guide.md",
                    "User Guide",
                    "Getting-started reference for framework users.",
                    Category.REFERENCE)
    );

    private DocRegistry() {}
}
