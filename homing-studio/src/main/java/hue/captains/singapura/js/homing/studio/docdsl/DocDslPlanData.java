package hue.captains.singapura.js.homing.studio.docdsl;

import hue.captains.singapura.js.homing.studio.base.tracker.Acceptance;
import hue.captains.singapura.js.homing.studio.base.tracker.Decision;
import hue.captains.singapura.js.homing.studio.base.tracker.DecisionStatus;
import hue.captains.singapura.js.homing.studio.base.tracker.Dependency;
import hue.captains.singapura.js.homing.studio.base.tracker.Objective;
import hue.captains.singapura.js.homing.studio.base.tracker.Phase;
import hue.captains.singapura.js.homing.studio.base.tracker.PhaseStatus;
import hue.captains.singapura.js.homing.studio.base.tracker.Plan;
import hue.captains.singapura.js.homing.studio.base.tracker.Task;

import java.util.List;

/**
 * Multi-phase plan tracking the journey from {@code .md}-only doc authoring to
 * a typed Document DSL. Coexistence with {@code ClasspathMarkdownDoc}
 * throughout — no forced migration. Each phase is trigger-gated: most phases
 * enter when a real doc-authoring need can't be met by the existing markdown
 * surface.
 *
 * <p>Three sealed families ({@code Structural} / {@code Content} / {@code Role})
 * with recursive structural composition are the architectural spine. Specific
 * design questions (prose authoring shape, renderer engine, first diagram
 * type, retrofit policy) are deliberately deferred and resolved as their
 * phase entered.</p>
 */
public final class DocDslPlanData implements Plan {

    public static final DocDslPlanData INSTANCE = new DocDslPlanData();

    private DocDslPlanData() {}

    @Override public String kicker()   { return "DOC DSL"; }
    @Override public String name()     { return "Typed Document DSL"; }
    @Override public String subtitle() {
        return "Multi-phase journey replacing .md authoring for docs that have outgrown markdown. "
             + "Coexistence with ClasspathMarkdownDoc throughout; trigger-driven adoption per phase. "
             + "Architectural spine: three sealed families (Structural / Content / Role) with "
             + "recursive structural composition.";
    }
    @Override public String summary() {
        return "Replace .md with a typed DSL where structural primitives organise the doc, "
             + "content primitives carry leaf data, and semantic roles name what each piece means. "
             + "Coexists with existing markdown docs; trigger-driven adoption.";
    }

    @Override public List<Objective> objectives() {
        return List.of(
                new Objective("Type the doc layer like everything else",
                        "The framework's typed-everything stance — extended to its own documentation. Doc primitives become records; doc kinds become typed interfaces; references become typed handles."),
                new Objective("Make silly mistakes structurally impossible",
                        "Compile-time enforcement replaces ad-hoc conformance tests. Missing required sections, dangling references, malformed tables become build failures, not late-caught review findings."),
                new Objective("Express what .md cannot",
                        "Typed diagrams, 3D visualisations, computed content from framework data, typed cross-doc references — the structural ceiling that motivated this whole journey."),
                new Objective("Adopt incrementally; coexist forever",
                        "No forced migration. ClasspathMarkdownDoc and TypedDoc render through the same pipeline; authors opt into the typed surface only when they hit a real limit.")
        );
    }

    @Override public List<Decision> decisions() {
        return List.of(
                new Decision("D1",
                        "How to partition doc primitives?",
                        "Three sealed families — Structural / Content / Role — at the same DocBlock level.",
                        "Three sealed families",
                        DecisionStatus.RESOLVED,
                        "Structurals organise blocks (layout). Content is leaf data (the stuff itself). Roles name semantic intent (what this block means). Each family evolves independently; renderer dispatches at three exhaustive levels.",
                        "Settled during initial design conversation, 2026-05-15."),
                new Decision("D2",
                        "How does the doc tree compose?",
                        "Section as the primary recursion point with arbitrary nested DocBlocks.",
                        "Structural is recursive; Section.children: List<DocBlock>",
                        DecisionStatus.RESOLVED,
                        "Author's mental model is hierarchical sections. Section IS the doc's argumentative tree. The DSL is built around structural composition; Content and Role attach as leaves of that tree.",
                        ""),
                new Decision("D3",
                        "Replace or coexist with existing .md docs?",
                        "Sealed Doc over (ClasspathMarkdownDoc, TypedDoc); both render through the same pipeline.",
                        "Coexist; no forced migration; trigger-driven adoption",
                        DecisionStatus.RESOLVED,
                        "Existing .md works well for most docs. Mermaid rejection was the original trigger that exposed the need; until similar limits hit, .md stays. New typed system opt-in only.",
                        ""),
                new Decision("D4",
                        "Are doc-kinds extensible by downstream?",
                        "Non-sealed interfaces let downstream define new kinds; primitives stay sealed.",
                        "Non-sealed kinds, sealed primitives",
                        DecisionStatus.RESOLVED,
                        "Open-set extensibility for kinds (Doctrine, RFC, CaseStudy, WorkshopDoc, ...); closed-shape stability for primitives. Matches the framework's open-set/closed-shape doctrine.",
                        ""),
                new Decision("D5",
                        "How are inline references authored in prose?",
                        "Pick one of: builder fluent, template substitution with ${ClassName}, plain text + separate ref nodes.",
                        null,
                        DecisionStatus.OPEN,
                        "",
                        "Resolved in Phase 1 — chosen via implementation feedback. Trade-off is ergonomic prose ease vs. authoring clarity."),
                new Decision("D6",
                        "Extend the existing marked-based pipeline or build a parallel typed-AST renderer?",
                        "Build a parallel typed-AST renderer that lives alongside the markdown one. Avoids contaminating the markdown path; lets each evolve on its own schedule.",
                        null,
                        DecisionStatus.OPEN,
                        "",
                        "Resolved in Phase 1 once the JSON wire format is settled."),
                new Decision("D7",
                        "Which diagram family ships first?",
                        "Likely Graph — most generally useful. Driven by the first triggering doc's needs.",
                        null,
                        DecisionStatus.OPEN,
                        "",
                        "Resolved in Phase 3 by the actual first doc that wants a diagram."),
                new Decision("D8",
                        "SVG vs Canvas as primary diagram rendering target?",
                        "SVG first — composes with theme CSS variables, exports cleanly, accessible. Canvas only if a specific diagram needs Canvas-only features.",
                        null,
                        DecisionStatus.OPEN,
                        "",
                        "Resolved in Phase 3."),
                new Decision("D9",
                        "Which existing doctrine becomes the first typed worked example?",
                        "Likely NoStealthDataDoc, StatelessServerDoc, or QualityWithoutSurveillanceDoc — most recent, most structured, already follow the canonical section pattern closely.",
                        null,
                        DecisionStatus.OPEN,
                        "",
                        "Resolved in Phase 2."),
                new Decision("D10",
                        "Convert remaining .md docs to typed, or coexist forever?",
                        "Coexist as the default; opportunistic retrofit only when a .md doc is being edited for content reasons anyway.",
                        null,
                        DecisionStatus.OPEN,
                        "",
                        "Deferred indefinitely; revisit in Phase 7 if a compelling reason emerges. Plausible final answer: never fully retrofit; ClasspathMarkdownDoc stays as the simple-case escape hatch.")
        );
    }

    @Override public List<Phase> phases() {
        return List.of(
                new Phase("P1", "Foundations",
                        "Sealed DocBlock hierarchy + TypedDoc interface + minimum renderer + DSL helpers.",
                        "Build the minimum viable typed-doc pipeline. Define the sealed DocBlock hierarchy "
                        + "with Structural / Content / Role as three orthogonal sealed families. Add TypedDoc "
                        + "as a non-sealed interface alongside ClasspathMarkdownDoc, with Doc made sealed over "
                        + "the pair. Build the AST → JSON serializer server-side and a client-side renderer for "
                        + "the minimum primitive set. Define the static-helper DSL surface for authoring. "
                        + "Validate end-to-end with one internal proof-of-concept TypedDoc. Verify that all "
                        + "existing .md doctrines continue to render unchanged.",
                        PhaseStatus.NOT_STARTED,
                        List.of(
                                new Task("Define sealed DocBlock + Structural / Content / Role sub-families", false),
                                new Task("Define minimum primitive set per family (Section, Tagline, SeeAlso, lists, table, Prose, CodeBlock, Tensions, Note, etc.)", false),
                                new Task("Add TypedDoc interface; make Doc sealed over (ClasspathMarkdownDoc, TypedDoc)", false),
                                new Task("Build AST → JSON serializer server-side", false),
                                new Task("Build client-side renderer for the minimum primitive set", false),
                                new Task("Define static-helper DSL surface (section, prose, codeBlock, tensions, ...)", false),
                                new Task("Build one proof-of-concept TypedDoc end-to-end", false),
                                new Task("Verify all existing .md docs continue to render unchanged", false),
                                new Task("Resolve D5 (prose authoring shape) and D6 (renderer engine choice)", false)
                        ),
                        List.<Dependency>of(),
                        "POC TypedDoc renders end-to-end through the pipeline; sealed three-family enforcement compile-tested; every existing .md doc renders identically to before.",
                        "Phase 1 is purely additive — rollback is removing the new types; the existing .md pipeline keeps working.",
                        "L",
                        "Establishes the architectural foundation. Open decisions D5 and D6 resolved here through implementation feedback."),

                new Phase("P2", "First Doc Kind — Doctrine",
                        "Codify Doctrine as a typed kind; convert one existing doctrine as worked example.",
                        "Define the non-sealed Doctrine interface extending TypedDoc, with required "
                        + "Section-returning methods for each canonical section (tagline, what-this-bans, "
                        + "what-this-permits, where-this-doesnt-apply, why-the-strictness-is-worth-it, "
                        + "how-to-think-about-it, see-also). The body() default composes required sections "
                        + "in canonical order. Convert one existing doctrine end-to-end as the worked "
                        + "example, demonstrating compile-time schema enforcement (deleting a required "
                        + "section breaks the build). Lock the kind-interface authoring pattern for use "
                        + "across other kinds.",
                        PhaseStatus.NOT_STARTED,
                        List.of(
                                new Task("Define non-sealed Doctrine interface with required Section-returning methods", false),
                                new Task("Implement body() default composing required sections in canonical order", false),
                                new Task("Convert chosen first doctrine end-to-end to typed form", false),
                                new Task("Verify it renders identically to its .md predecessor", false),
                                new Task("Verify required-section enforcement (delete a section, build fails)", false),
                                new Task("Resolve D9 (which doctrine first)", false)
                        ),
                        List.of(new Dependency("P1", "Needs the sealed DocBlock + TypedDoc + renderer")),
                        "Chosen doctrine fully typed; identical rendering to its .md predecessor; deleting any required method breaks the build.",
                        "Phase 2 is per-doctrine — rollback to .md version is reverting the conversion commit.",
                        "M",
                        ""),

                new Phase("P3", "First Diagram Type",
                        "Sealed Diagram sub-family + first concrete diagram (likely Graph) + first doc using one.",
                        "Add the sealed Diagram sub-family under Content with one initial permit (likely "
                        + "Graph). Implement Graph as a typed record with GraphNode / GraphEdge; the compact "
                        + "constructor validates edge endpoints reference declared nodes. Add client-side "
                        + "rendering (SVG primary per D8). Build authoring helpers. Ship the first production "
                        + "doc that uses a typed diagram — this is the trigger that originally motivated the "
                        + "whole DSL (mermaid rejection from .md authoring).",
                        PhaseStatus.NOT_STARTED,
                        List.of(
                                new Task("Define sealed Diagram sub-family with first permit (likely Graph)", false),
                                new Task("Implement Graph record with compact-constructor validation of edge endpoints", false),
                                new Task("Client-side rendering (SVG or Canvas per D8)", false),
                                new Task("Authoring helpers (graph, node, edge, ...)", false),
                                new Task("Ship first production typed doc with a diagram", false),
                                new Task("Resolve D7 (which diagram first) and D8 (SVG vs Canvas)", false)
                        ),
                        List.of(new Dependency("P1", "Needs the sealed DocBlock hierarchy and renderer infrastructure")),
                        "Typed doc with Graph renders correctly in the browser; framework features (catalogue, breadcrumb, references) work around it; the original mermaid limitation that motivated this work is now solved.",
                        "Phase 3 is opt-in per doc — rollback is removing the Diagram permits and using .md for that doc.",
                        "M",
                        "Trigger-gated by an actual doc author needing a diagram. Likely concurrent with or shortly after P2."),

                new Phase("P4", "Doc-Kind Family Completion",
                        "RFC, CaseStudy, BuildingBlock kind interfaces — each shipped when its first typed instance is needed.",
                        "Roll out typed kind interfaces for the remaining major doc types. Each kind enters "
                        + "Phase 4 individually, gated by its first typed instance. No mass migration. RFC "
                        + "required sections: motivation, design, decisions, compatibility. CaseStudy required "
                        + "sections: phenomenon, analysis, see-also. BuildingBlock required sections: "
                        + "purpose, usage, examples.",
                        PhaseStatus.NOT_STARTED,
                        List.of(
                                new Task("Define RFC kind interface when first typed RFC is written", false),
                                new Task("Define CaseStudy kind interface when first typed case study is written", false),
                                new Task("Define BuildingBlock kind interface when first typed building block is written", false),
                                new Task("(Optional) ReleaseNote kind interface — decide if release notes benefit from typing", false)
                        ),
                        List.of(new Dependency("P2", "Pattern established by the Doctrine kind")),
                        "At least one typed instance of each of Doctrine, RFC, CaseStudy, BuildingBlock kinds exists in the framework's docs.",
                        "Phase 4 entries are individual — rollback is per-kind, mechanical (revert that kind's interface + its first instance).",
                        "M (spread across kinds)",
                        ""),

                new Phase("P5", "Computed and Auto-Generated Content",
                        "Diagrams and lists derived from typed framework data (Catalogues, Plans, ...).",
                        "Content that's derived from typed framework data, not hand-authored. CatalogueTree "
                        + "diagram auto-generated from a Catalogue<?> instance — embed "
                        + "new CatalogueTree(StudioCatalogue.INSTANCE) and the diagram regenerates as the "
                        + "catalogue tree changes. PlanGraph for Plan instances. Cross-doc queries (e.g., "
                        + "rolled-up Tensions across all doctrines, or all Compromises across all case "
                        + "studies).",
                        PhaseStatus.NOT_STARTED,
                        List.of(
                                new Task("CatalogueTree diagram type — auto-generated from any Catalogue", false),
                                new Task("PlanGraph diagram type — auto-generated from any Plan", false),
                                new Task("Cross-doc query primitives (programmatic AST traversal)", false),
                                new Task("First doc consuming computed content", false)
                        ),
                        List.of(new Dependency("P3", "Diagram infrastructure already in place")),
                        "A doc embeds computed content; the content updates automatically as its source data changes; no doc-side edit required when the source evolves.",
                        "Phase 5 is additive — rollback removes the computed-content types.",
                        "M",
                        ""),

                new Phase("P6", "3D Visualisations",
                        "Scene3D family with Three.js integration via existing homing-libs bundle.",
                        "Add the sealed Visualisation sub-family under Content with Scene3D as the first "
                        + "permit. Integrate with Three.js (already in homing-libs — no CDN dependency). "
                        + "First doc that legitimately needs 3D — probably an architecture-style doc "
                        + "visualising spatial layout or a domain-specific visualisation. Performance budget "
                        + "and accessibility fallback (non-WebGL clients) defined.",
                        PhaseStatus.NOT_STARTED,
                        List.of(
                                new Task("Define sealed Visualisation sub-family with Scene3D permit", false),
                                new Task("Three.js integration via homing-libs (no CDN load)", false),
                                new Task("Performance budget and accessibility fallback policy", false),
                                new Task("First typed doc with Scene3D content", false)
                        ),
                        List.of(new Dependency("P1", "Needs the sealed DocBlock hierarchy and Content family")),
                        "Typed doc with Scene3D renders correctly client-side; performance acceptable on target browsers; non-WebGL fallback documented and tested.",
                        "Phase 6 is additive — rollback removes the Visualisation permits.",
                        "M-L",
                        "Trigger-gated by an actual doc author needing 3D — may stay in NOT_STARTED for a long time."),

                new Phase("P7", "Retrofit (Optional)",
                        "Optional opportunistic conversion of remaining .md docs; possible ClasspathMarkdownDoc retirement.",
                        "Optional throughout. May never fully happen — coexistence is the steady state if no "
                        + "compelling reason emerges. If pursued: build a one-time .md → typed parser as a "
                        + "migration aid, convert remaining docs opportunistically (when edited for content "
                        + "reasons anyway), and eventually decide whether to retire ClasspathMarkdownDoc "
                        + "entirely. Many docs may never need conversion. Plausible final answer: "
                        + "ClasspathMarkdownDoc stays as the simple-case escape hatch indefinitely.",
                        PhaseStatus.NOT_STARTED,
                        List.of(
                                new Task("Decide retrofit scope per content type", false),
                                new Task("(Optional) Build .md → typed parser as migration aid", false),
                                new Task("Opportunistic conversion of edited docs", false),
                                new Task("Decide whether to retire ClasspathMarkdownDoc", false)
                        ),
                        List.of(new Dependency("P4", "Need all doc kinds defined for any full-retrofit option")),
                        "Phase-dependent. Could be 'no retrofit, coexistence permanent' or 'all docs typed; ClasspathMarkdownDoc retired'. Decision deferred to actual accumulated experience.",
                        "Phase 7 is optional throughout — non-action is a valid resolution. Reverting any single doc to .md is mechanical.",
                        "Variable (could be zero)",
                        "Always deferred until clear value emerges.")
        );
    }

    @Override public List<Acceptance> acceptance() {
        return List.of(
                new Acceptance("Foundation pipeline operational",
                        "TypedDoc renders end-to-end through the JSON serializer + client-side renderer; sealed three-family DocBlock compile-tested; all existing .md docs continue to render identically.",
                        false),
                new Acceptance("Doctrine kind shipped as typed",
                        "At least one existing doctrine fully converted to TypedDoc form; required-section enforcement compile-tested via deletion experiment; the kind-interface authoring pattern locked.",
                        false),
                new Acceptance("First diagram-bearing doc",
                        "A typed doc embeds at least one diagram — the trigger that originally motivated this DSL (mermaid rejection); renders correctly client-side; framework features work around it.",
                        false),
                new Acceptance("Four primary doc kinds typed",
                        "Doctrine, RFC, CaseStudy, and BuildingBlock kind interfaces each have at least one typed instance in the framework's docs.",
                        false),
                new Acceptance("Auto-generated content shipped",
                        "At least one typed doc embeds computed content (e.g., CatalogueTree from a Catalogue, PlanGraph from a Plan); the content updates automatically as its source data changes.",
                        false),
                new Acceptance("3D content shipped or deferred",
                        "A typed doc with Scene3D ships, OR the framework explicitly decides 3D visualisations are deferred indefinitely with documented rationale.",
                        false)
        );
    }
}
