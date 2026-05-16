package hue.captains.singapura.js.homing.studio.docs.casestudies;

import hue.captains.singapura.js.homing.studio.base.ClasspathMarkdownDoc;
import hue.captains.singapura.js.homing.studio.base.DocReference;
import hue.captains.singapura.js.homing.studio.base.Reference;
import hue.captains.singapura.js.homing.studio.docs.doctrines.FirstUserDoc;
import hue.captains.singapura.js.homing.studio.docs.doctrines.FunctionalObjectsDoc;
import hue.captains.singapura.js.homing.studio.docs.doctrines.NoStealthDataDoc;
import hue.captains.singapura.js.homing.studio.docs.doctrines.QualityWithoutSurveillanceDoc;
import hue.captains.singapura.js.homing.studio.docs.doctrines.StatelessServerDoc;
import hue.captains.singapura.js.homing.studio.docs.doctrines.WeighedComplexityDoc;

import java.util.List;
import java.util.UUID;

/**
 * Case study — cost-of-iteration shapes architecture. The Homing framework's
 * distinctive property (internal consistency under principled commitment) is
 * achievable because one author iterating with AI assistance has a
 * cost-of-iteration measured in hours; an MNC IT department iterating across
 * teams + review boards has it measured in weeks. The result is not a 10×
 * difference in delivery speed — it is a structural difference in what
 * design coherence is reachable at all.
 *
 * <p>Two angles examined: (1) what an MNC IT department would have to spend
 * to produce the same nominal output (~$8-14M, 18-30 months; bank version
 * $12-20M, 30-48 months); (2) what downstream developers gain by building
 * on Homing — particularly with AI coding-agent assistance — turning
 * week-scale documentation-and-data application work into day-scale work.</p>
 *
 * <p>The general lesson: <i>the value of a framework, in the AI-agent era,
 * is increasingly measured by how much it shrinks the agent's decision tree,
 * not by how much it adds to the developer's toolbox.</i> Homing's typed
 * everything + agent-first authoring + opinionated refusal of common
 * patterns produces compounding leverage for both authors and downstream
 * consumers.</p>
 */
public record CostOfIterationCaseStudy() implements ClasspathMarkdownDoc {
    private static final UUID ID = UUID.fromString("a3f5e7c9-2b4d-4a8e-9c1f-5e3d7b9a2c80");
    public static final CostOfIterationCaseStudy INSTANCE = new CostOfIterationCaseStudy();

    @Override public UUID   uuid()    { return ID; }
    @Override public String title()   { return "Case Study — Cost-Of-Iteration Shapes Architecture"; }
    @Override public String summary() { return "What the Homing framework would cost an MNC IT department to deliver (~$8-14M, 18-30 months) versus what one author + AI assistance produced. The economic comparison is striking, but the deeper insight is structural: design coherence under principled commitment is reachable at small-team scale and unreachable at MNC scale, regardless of budget. The downstream amplifier — applications built on Homing with AI assistance run 10-25× cheaper than the MNC-built equivalent — generalises the lesson: in the AI-agent era, framework value is measured by how much it shrinks the agent's decision tree."; }
    @Override public String category(){ return "CASE_STUDY"; }

    @Override public List<Reference> references() {
        return List.of(
                new DocReference("doc-fu",   FirstUserDoc.INSTANCE),
                new DocReference("doc-fo",   FunctionalObjectsDoc.INSTANCE),
                new DocReference("doc-nsd",  NoStealthDataDoc.INSTANCE),
                new DocReference("doc-ss",   StatelessServerDoc.INSTANCE),
                new DocReference("doc-qws",  QualityWithoutSurveillanceDoc.INSTANCE),
                new DocReference("doc-wc",   WeighedComplexityDoc.INSTANCE),
                new DocReference("csref",    CrossStudioRefsCaseStudy.INSTANCE),
                new DocReference("priv-sec", PrivacyDoctrineSecurityCaseStudy.INSTANCE)
        );
    }
}
