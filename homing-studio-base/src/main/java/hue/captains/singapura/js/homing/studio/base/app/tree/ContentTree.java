package hue.captains.singapura.js.homing.studio.base.app.tree;

import hue.captains.singapura.tao.ontology.ValueObject;

import java.util.Objects;

/**
 * RFC 0016 — a data-authored hierarchical structure of named nodes whose
 * leaves bear typed content. Parallel to {@code Catalogue} (typed
 * code-authored hierarchy); both render through the same UI surface.
 *
 * <p>Realises the DocTree ontology (T1–T10) for data-authored trees.
 * Each tree has its own identity (URL slug); every node is addressable
 * by {@code (tree-id, path)}; the root is itself a {@link TreeBranch}
 * (no separate "root" wrapper — the tree's metadata IS the root).</p>
 *
 * <p>Phase 1 of RFC 0016: static trees only (boot-registered via
 * {@code Fixtures.trees()}). Dynamic trees ({@code TreeProvider}) and
 * cross-tree references via tree-root reference leaves are deferred.</p>
 *
 * @param id   URL-safe identity slug (e.g. "animals"); unique across the
 *             registered tree set
 * @param root the root branch; carries the tree's display name + structure
 *
 * @since RFC 0016
 */
public record ContentTree(String id, TreeBranch root) implements ValueObject {
    public ContentTree {
        Objects.requireNonNull(id,   "ContentTree.id");
        Objects.requireNonNull(root, "ContentTree.root");
        if (id.isBlank()) {
            throw new IllegalArgumentException("ContentTree.id must not be blank");
        }
        if (!id.matches("[a-z0-9-]+")) {
            throw new IllegalArgumentException(
                    "ContentTree.id must be URL-safe slug ([a-z0-9-]+); got: " + id);
        }
    }

    /** Convenience — derive the slug id from the root's name. */
    public ContentTree(TreeBranch root) {
        this(TreeBranch.slugify(root.name()), root);
    }
}
