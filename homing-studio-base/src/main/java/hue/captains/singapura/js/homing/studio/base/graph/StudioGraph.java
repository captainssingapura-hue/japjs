package hue.captains.singapura.js.homing.studio.base.graph;

import hue.captains.singapura.tao.ontology.FunctionalObject;
import hue.captains.singapura.tao.ontology.Immutable;
import hue.captains.singapura.tao.ontology.Mutable;
import hue.captains.singapura.tao.ontology.Stateless;
import hue.captains.singapura.tao.ontology.StatelessFunctionalObject;
import hue.captains.singapura.tao.ontology.ValueObject;

import java.util.LinkedHashSet;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * RFC 0014 Phase 1 — the typed in-memory object graph built from a composed
 * {@code Bootstrap}. Two relationship kinds:
 *
 * <ul>
 *   <li><b>{@code CONTAINS}</b> — structural parent-child. The Bootstrap
 *       contains its Fixtures; a Studio contains its catalogues; a Catalogue
 *       contains its sub-catalogues and Entry leaves; a Plan contains its
 *       phases; etc. This forms the spine tree.</li>
 *   <li><b>{@code REFERENCES}</b> — cross-references between vertices that
 *       are <em>not</em> in a parent-child relationship. A Doc references
 *       another Doc; a Phase depends on another Phase; a hosting catalogue
 *       has a typed cross-tree reverse-reference to a source studio's L0.</li>
 * </ul>
 *
 * <p>Vertices are just objects. No wrapper records, no synthetic vertex IDs —
 * the actual framework objects (Doc instances, Catalogue instances, Plan
 * instances, etc.) are the vertices themselves. Identity is whatever Java's
 * {@code equals}/{@code hashCode} produces: singleton records (which most
 * framework primitives are) compare by reference; value records (Phase,
 * Task, Decision, ...) compare by their fields.</p>
 *
 * <p>The graph is built eagerly by {@link StudioGraphBuilder} and is
 * immutable thereafter (defensively-copied collections in the compact
 * constructor). Queries return concrete {@link Set} collections per the
 * Explicit-over-Implicit doctrine — caller sees size, can iterate twice,
 * can toString-debug, can convert to Stream with {@code .stream()} if a
 * pipeline is needed. Performance for Phase 1 is O(edges) per query;
 * indices can be added later when profiling shows the need.</p>
 *
 * @param vertices the deduplicated set of objects reachable from the Bootstrap root
 * @param edges    the typed edges; each edge has a kind and an optional label
 *
 * @since RFC 0014
 */
public record StudioGraph(Set<Object> vertices, Set<Edge> edges)
        implements ValueObject {

    public StudioGraph {
        vertices = Set.copyOf(vertices);
        edges = Set.copyOf(edges);
    }

    /** A typed edge between two vertices, with a {@link Kind} and optional label. */
    public record Edge(Object from, Object to, Kind kind, String label) implements ValueObject {
        public Edge(Object from, Object to, Kind kind) { this(from, to, kind, ""); }
    }

    /** The two edge relationships modelled in Phase 1. */
    public enum Kind { CONTAINS, REFERENCES }

    // -------------------------------------------------------------------------
    // Query primitives (Phase 1 — collection-returning per Explicit over Implicit)
    // -------------------------------------------------------------------------

    /** Children reachable from {@code parent} via {@link Kind#CONTAINS} edges. */
    public Set<Object> children(Object parent) {
        return edges.stream()
                .filter(e -> e.kind == Kind.CONTAINS)
                .filter(e -> e.from.equals(parent))
                .map(Edge::to)
                .collect(Collectors.toUnmodifiableSet());
    }

    /** Parents reachable from {@code child} via reverse {@link Kind#CONTAINS} edges. */
    public Set<Object> parents(Object child) {
        return edges.stream()
                .filter(e -> e.kind == Kind.CONTAINS)
                .filter(e -> e.to.equals(child))
                .map(Edge::from)
                .collect(Collectors.toUnmodifiableSet());
    }

    /** Vertices {@code source} references via {@link Kind#REFERENCES} edges. */
    public Set<Object> referencesFrom(Object source) {
        return edges.stream()
                .filter(e -> e.kind == Kind.REFERENCES)
                .filter(e -> e.from.equals(source))
                .map(Edge::to)
                .collect(Collectors.toUnmodifiableSet());
    }

    /** Vertices that reference {@code target} via {@link Kind#REFERENCES} edges. */
    public Set<Object> referencedBy(Object target) {
        return edges.stream()
                .filter(e -> e.kind == Kind.REFERENCES)
                .filter(e -> e.to.equals(target))
                .map(Edge::from)
                .collect(Collectors.toUnmodifiableSet());
    }

    /** All vertices that are instances of the given type. */
    @SuppressWarnings("unchecked")
    public <T> Set<T> verticesOfType(Class<T> type) {
        return vertices.stream()
                .filter(type::isInstance)
                .map(v -> (T) v)
                .collect(Collectors.toUnmodifiableSet());
    }

    /** All edges where {@code parent} is the source of a CONTAINS edge — full edge info. */
    public Set<Edge> outgoingContainsEdges(Object parent) {
        return edges.stream()
                .filter(e -> e.kind == Kind.CONTAINS)
                .filter(e -> e.from.equals(parent))
                .collect(Collectors.toUnmodifiableSet());
    }

    /** All edges where {@code source} is the source of a REFERENCES edge — full edge info (carries label). */
    public Set<Edge> outgoingReferenceEdges(Object source) {
        return edges.stream()
                .filter(e -> e.kind == Kind.REFERENCES)
                .filter(e -> e.from.equals(source))
                .collect(Collectors.toUnmodifiableSet());
    }

    // -------------------------------------------------------------------------
    // Diagnostic dump — quick text-tree view for spot-checking the graph
    // -------------------------------------------------------------------------

    /**
     * Render the graph as an indented text tree rooted at the given vertex.
     * For diagnostic / spot-check use (printing in tests, console exploration).
     * Not a UI; just a debuggable view.
     */
    public String dump(Object root) {
        var sb = new StringBuilder();
        dumpInto(sb, root, 0, new java.util.HashSet<>());
        return sb.toString();
    }

    /**
     * Render the graph as a Markdown document — header + stats + ontology
     * legend + the indented text tree wrapped in a fenced code block.
     *
     * <p>Suitable for serving over HTTP and rendering on the front-end with
     * any standard markdown library (the framework's bundled marked.js, for
     * example). Each part of the document is small and self-contained so a
     * client can render incrementally if needed.</p>
     *
     * <p>The fenced code block preserves the tree's alignment; the emoji
     * prefixes are inlined rather than expanded to image tags, so the
     * rendered output reads identically to the plain {@link #dump(Object)}
     * output once the markdown processor finishes.</p>
     */
    public String dumpMarkdown(Object root) {
        var sb = new StringBuilder();
        sb.append("# Studio Graph\n\n");
        sb.append("**Root:** `").append(root.getClass().getName()).append("`  \n");
        sb.append("**Vertices:** ").append(vertices.size())
          .append(" &middot; **Edges:** ").append(edges.size()).append("  \n");
        sb.append("**Generated:** ").append(java.time.Instant.now()).append("\n\n");

        sb.append("## Ontology legend\n\n");
        sb.append("| Emoji | jOntology marker |\n");
        sb.append("|---|---|\n");
        sb.append("| ⚡ | `StatelessFunctionalObject` — pure functions, no state |\n");
        sb.append("| ⚙️ | `FunctionalObject` — functions with collaborator state |\n");
        sb.append("| 💎 | `ValueObject` — value-equal data record |\n");
        sb.append("| 🪶 | `Stateless` — pure marker, no behaviour |\n");
        sb.append("| 🧊 | `Immutable` — broader immutable type |\n");
        sb.append("| 🔄 | `Mutable` — explicit mutable state |\n");
        sb.append("| ❓ | unmarked |\n\n");

        sb.append("## Tree\n\n");
        sb.append("```\n");
        sb.append(dump(root));
        sb.append("```\n");
        return sb.toString();
    }

    /**
     * Render a type-only view of the graph — one row per distinct vertex class
     * with its ontology classification, package, instance count and a sample
     * {@code toString}. Intended as a code-quality gauge: rows classified ❓
     * (unmarked) float to the top of the table so missing ontology markers are
     * the first thing the reader sees.
     *
     * <p>Sort order: ❓ first (smells), then by instance count descending, then
     * by class name ascending for stable output across runs.</p>
     */
    public String dumpTypesMarkdown() {
        // Group vertices by their concrete class; count instances; keep one sample
        // per class for the toString preview column.
        var byClass = new java.util.LinkedHashMap<Class<?>, java.util.List<Object>>();
        for (Object v : vertices) {
            if (v == null) continue;
            byClass.computeIfAbsent(v.getClass(), k -> new java.util.ArrayList<>()).add(v);
        }

        record Row(String emoji, Class<?> cls, int count, String sample) {}
        var rows = new java.util.ArrayList<Row>();
        for (var e : byClass.entrySet()) {
            var cls = e.getKey();
            var instances = e.getValue();
            var emoji = ontologyEmoji(instances.get(0));
            var sample = instances.get(0).toString();
            if (sample.length() > 60) sample = sample.substring(0, 57) + "…";
            rows.add(new Row(emoji, cls, instances.size(), sample));
        }
        // ❓ first (smells surface), then count desc, then class name asc.
        rows.sort((a, b) -> {
            boolean au = "❓".equals(a.emoji), bu = "❓".equals(b.emoji);
            if (au != bu) return au ? -1 : 1;
            if (a.count != b.count) return Integer.compare(b.count, a.count);
            return a.cls.getName().compareTo(b.cls.getName());
        });

        long unmarked = rows.stream().filter(r -> "❓".equals(r.emoji)).count();

        var sb = new StringBuilder();
        sb.append("# Studio Graph — Type View\n\n");
        sb.append("**Distinct types:** ").append(rows.size())
          .append(" &middot; **Unmarked (❓):** ").append(unmarked)
          .append(" &middot; **Vertices:** ").append(vertices.size())
          .append("  \n");
        sb.append("**Generated:** ").append(java.time.Instant.now()).append("\n\n");

        sb.append("> Each row is one concrete vertex class. ❓ rows are types that ")
          .append("declare no jOntology marker — every ❓ is a smell, either on the ")
          .append("type (mark it) or on the graph builder (don't walk into it).\n\n");

        sb.append("| | Type | Package | # | Sample |\n");
        sb.append("|---|---|---|---:|---|\n");
        for (var r : rows) {
            String pkg = r.cls.getPackageName();
            if (pkg == null || pkg.isEmpty()) pkg = "(default)";
            String simple = r.cls.getSimpleName();
            if (simple.isBlank()) simple = r.cls.getName();
            // Escape pipe characters in the sample so the markdown table doesn't
            // get split by toStrings that happen to contain "|".
            String sample = r.sample.replace("|", "\\|");
            sb.append("| ").append(r.emoji)
              .append(" | `").append(simple).append("`")
              .append(" | `").append(pkg).append("`")
              .append(" | ").append(r.count)
              .append(" | ").append(sample)
              .append(" |\n");
        }
        return sb.toString();
    }

    private void dumpInto(StringBuilder sb, Object v, int indent, Set<Object> seen) {
        for (int i = 0; i < indent; i++) sb.append("  ");
        sb.append(ontologyEmoji(v)).append(" ").append(describe(v));
        if (!seen.add(v)) {
            sb.append("  (already shown above)\n");
            return;
        }
        sb.append("\n");
        for (Object child : children(v)) {
            dumpInto(sb, child, indent + 1, seen);
        }
        for (Edge ref : outgoingReferenceEdges(v)) {
            for (int i = 0; i <= indent; i++) sb.append("  ");
            sb.append("→ ").append(ontologyEmoji(ref.to())).append(" ").append(describe(ref.to()));
            if (!ref.label().isEmpty()) sb.append("  [").append(ref.label()).append("]");
            sb.append("\n");
        }
    }

    private String describe(Object v) {
        var cls = v.getClass().getSimpleName();
        return cls.isBlank() ? v.toString() : cls;
    }

    /**
     * Maps a vertex's jOntology classification to a single emoji glyph.
     *
     * <p>Order of checks goes from most-specific to least-specific so a
     * {@code StatelessFunctionalObject} (which is also a {@code FunctionalObject},
     * {@code Stateless}, and {@code Immutable}) gets the most-specific glyph.</p>
     *
     * <ul>
     *   <li>⚡ {@code StatelessFunctionalObject} — pure functions, no state</li>
     *   <li>⚙️ {@code FunctionalObject} — functions with collaborator state</li>
     *   <li>💎 {@code ValueObject} — value-equal data record</li>
     *   <li>🪶 {@code Stateless} — pure marker, no behaviour</li>
     *   <li>🧊 {@code Immutable} — broader immutable type</li>
     *   <li>🔄 {@code Mutable} — explicit mutable state</li>
     *   <li>❓ unmarked — no ontology classification</li>
     * </ul>
     */
    public static String ontologyEmoji(Object v) {
        if (v == null) return "❓";
        Class<?> c = v.getClass();
        if (StatelessFunctionalObject.class.isAssignableFrom(c)) return "⚡";
        if (FunctionalObject.class.isAssignableFrom(c))          return "⚙️";
        if (ValueObject.class.isAssignableFrom(c))               return "💎";
        if (Stateless.class.isAssignableFrom(c))                 return "🪶";
        if (Immutable.class.isAssignableFrom(c))                 return "🧊";
        if (Mutable.class.isAssignableFrom(c))                   return "🔄";
        return "❓";
    }

    // -------------------------------------------------------------------------
    // Construction helper — builders use this to assemble the graph
    // -------------------------------------------------------------------------

    /** Mutable builder used internally by {@link StudioGraphBuilder} during traversal. */
    public static final class Mutable {
        private final Set<Object> vertices = new LinkedHashSet<>();
        private final Set<Edge> edges = new LinkedHashSet<>();
        /** Tracks vertices whose outgoing edges have been walked, distinct from the
         *  vertex set itself (which is populated by both vertex() and edge addition).
         *  Used to prevent re-walking on revisit while still allowing edges to be added. */
        private final Set<Object> walked = new java.util.HashSet<>();

        public Mutable vertex(Object v) {
            if (v != null) vertices.add(v);
            return this;
        }

        public Mutable contains(Object parent, Object child) {
            if (parent == null || child == null) return this;
            vertices.add(parent);
            vertices.add(child);
            edges.add(new Edge(parent, child, Kind.CONTAINS));
            return this;
        }

        public Mutable references(Object from, Object to, String label) {
            if (from == null || to == null) return this;
            vertices.add(from);
            vertices.add(to);
            edges.add(new Edge(from, to, Kind.REFERENCES, label == null ? "" : label));
            return this;
        }

        /** Mark a vertex as walked. Returns {@code true} if this was the first call
         *  (caller should proceed with the walk); {@code false} if already walked
         *  (caller should skip). Allows the builder to add edges referring to a
         *  vertex without preventing later walks of the same vertex. */
        public boolean markWalked(Object v) {
            return walked.add(v);
        }

        public StudioGraph build() {
            return new StudioGraph(vertices, edges);
        }
    }
}
