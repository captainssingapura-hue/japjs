package hue.captains.singapura.js.homing.demo.studio;

import hue.captains.singapura.js.homing.core.SvgBeing;
import hue.captains.singapura.js.homing.core.SvgGroup;
import hue.captains.singapura.js.homing.core.SvgRef;
import hue.captains.singapura.js.homing.demo.es.CuteAnimal;
import hue.captains.singapura.js.homing.studio.base.SvgDoc;
import hue.captains.singapura.js.homing.studio.base.app.tree.ContentTree;
import hue.captains.singapura.js.homing.studio.base.app.tree.TreeBranch;
import hue.captains.singapura.js.homing.studio.base.app.tree.TreeLeaf;
import hue.captains.singapura.js.homing.studio.base.app.tree.TreeNode;

import java.util.List;

/**
 * RFC 0016 demo — categorises the {@link CuteAnimal} SVG beings into two
 * sub-branches ("Animals" and "Halloween") under one ContentTree.
 *
 * <p>Each leaf wraps a {@link SvgDoc} (RFC 0015 Phase 3 Doc subtype) so
 * clicking a tile opens the SVG full-page via the framework's registered
 * {@code SvgViewer} (RFC 0015 Phase 5 ContentViewer). The tree page itself
 * renders as standard catalogue Cards — same chrome, same hover/audio
 * behaviour as every other tile in the framework.</p>
 *
 * <p>Tree shape:</p>
 * <pre>
 *   animals (tree id; root)
 *   ├── animals  (TreeBranch)
 *   │   ├── turtle    (TreeLeaf → SvgDoc)
 *   │   ├── penguin   (TreeLeaf → SvgDoc)
 *   │   ├── crocodile (TreeLeaf → SvgDoc)
 *   │   └── whale     (TreeLeaf → SvgDoc)
 *   └── halloween (TreeBranch)
 *       ├── ghost (TreeLeaf → SvgDoc)
 *       └── broom (TreeLeaf → SvgDoc)
 * </pre>
 */
public final class AnimalsTree {

    private AnimalsTree() {}

    public static final ContentTree INSTANCE = build();

    private static ContentTree build() {
        var animalsBranch = new TreeBranch(
                "animals", "Animals",
                "Cute critters from around the world.",
                "CATEGORY", "🐾",
                List.<TreeNode>of(
                        svgLeaf("turtle",    "Turtle",    "Slow, steady, ancient.",        new CuteAnimal.turtle()),
                        svgLeaf("penguin",   "Penguin",   "Cold-weather waddler.",         new CuteAnimal.penguin()),
                        svgLeaf("crocodile", "Crocodile", "Patient, toothy, prehistoric.", new CuteAnimal.crocodile()),
                        svgLeaf("whale",     "Whale",     "Largest animal alive.",         new CuteAnimal.whale())
                ));

        var halloweenBranch = new TreeBranch(
                "halloween", "Halloween",
                "Spooky companions for the season.",
                "CATEGORY", "🎃",
                List.<TreeNode>of(
                        svgLeaf("ghost", "Ghost", "Boo.",          new CuteAnimal.ghost()),
                        svgLeaf("broom", "Broom", "Witch's ride.", new CuteAnimal.broom())
                ));

        var root = new TreeBranch(
                "", "Animals & Halloween",
                "Cute SVG critters, categorised. A demo of RFC 0016 ContentTree "
              + "with RFC 0015 polymorphic doc viewer routing — each tile is a Doc "
              + "of kind \"svg\", rendered by the framework's registered SvgViewer.",
                "TREE", "🌳",
                List.of(animalsBranch, halloweenBranch));

        return new ContentTree("animals", root);
    }

    /** Build a tree leaf wrapping an {@link SvgDoc} that points at the given
     *  {@link SvgBeing}. The leaf's metadata is used for the tile display; the
     *  SvgDoc's URL routes to {@code SvgViewer} via the polymorphic doc viewer. */
    private static <G extends SvgGroup<G>> TreeLeaf svgLeaf(
            String slug, String name, String summary, SvgBeing<G> being) {
        @SuppressWarnings("unchecked")
        SvgRef<G> ref = new SvgRef<>((G) CuteAnimal.INSTANCE, being);
        var doc = new SvgDoc<>(ref, name, summary);
        return new TreeLeaf(slug, name, summary, "ANIMAL", "", doc);
    }
}
