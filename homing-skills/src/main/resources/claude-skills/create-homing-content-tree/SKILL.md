---
name: create-homing-content-tree
description: Use this skill when the user wants to add a data-authored hierarchical content surface to a Homing studio — a "tree of categorised content tiles" reachable at /app?app=tree&id=<slug>. Distinct from Catalogue (typed code-authored hierarchy) — ContentTree is the right call when the structure is data-driven, when leaves wrap heterogeneous Doc kinds (SvgDoc, prose Doc, ComposedDoc, AppDoc), or when you want the same Doc to appear in multiple categorisations with different framings. Triggers — "add content tree", "new doctree", "categorise SVG assets", "tag pages", "search-results-shaped page", "tile listing of typed content", "DocTree", "tree of Docs". Skip if the structure is typed code-authored (use the L0..L8 Catalogue family — see `create-homing-studio`).
---

# Create a Homing ContentTree

A `ContentTree` (RFC 0016) is a **data-authored** hierarchical content surface. It's the sibling of `Catalogue` — both render through the standard catalogue-shaped Card UI, both feed the breadcrumb / TOC / theme chrome — but they answer different authoring questions.

| | `Catalogue` (L0..L8) | `ContentTree` |
|---|---|---|
| **Hierarchy expressed in** | Java types (`L2_Catalogue<L1_Catalogue<L0_Catalogue<...>>>`) | Java values (`TreeBranch.children` lists) |
| **Levels** | Sealed at compile time — adding a level edits the sealed permits | Open at runtime — branch trees as deep as you like |
| **Authoring model** | Code-first; every node is a `Catalogue` subclass with `parent()` | Data-first; nodes are records with `children` |
| **Right when** | Studio structure is the spec (Doctrines / RFCs / Building Blocks / Case Studies) | Structure is content data (animal categories, tag pages, search results, asset taxonomy) |
| **Wrong when** | Structure is dynamic, big, or per-author | Structure deserves a compile-time skeleton |

If you find yourself writing the same `TreeBranch(...)` shape ten times with only the slug changing, you probably wanted a `Catalogue`. If you find yourself extending the sealed `Catalogue` permits list to add an L9 just to host one branch of asset categorisations, you probably wanted a `ContentTree`.

## Surface area you'll touch

A minimal ContentTree is **2 files** (3 if you need SVG leaves):

| File | Purpose | Manual JS? |
|---|---|---|
| `MyTree.java` | the `ContentTree` constant — branches + leaves | no |
| `MyFixtures.java` | overrides `Fixtures.trees()` to register the tree | no |
| (optional) `MyTreeSvgs.java` + `<being>.svg` files | typed `SvgGroup` if any leaf is a `SvgDoc` | no |

The framework wires the rest. When `Fixtures.trees()` is non-empty, `Bootstrap.compose()` registers `TreeRegistry`, `TreeGetAction`, and `TreeAppHost` automatically. Your tree gets the URL `/app?app=tree&id=<slug>` for the root and `/app?app=tree&id=<slug>&path=<segment>/<segment>` for any branch. Click a leaf → routes to that Doc's registered viewer via the polymorphic `ContentViewer` dispatch (RFC 0015 Phase 5).

## The two node kinds

`TreeNode` is a sealed sum:

```java
public sealed interface TreeNode extends ValueObject permits TreeBranch, TreeLeaf { ... }
```

### TreeBranch — has children, bears no content

```java
public record TreeBranch(
        String         segment,    // URL-safe slug, unique among siblings
        String         name,       // display heading
        String         summary,    // display body
        String         badge,      // short uppercase badge
        String         icon,       // optional glyph
        List<TreeNode> children    // ordered branches + leaves
) implements TreeNode { ... }
```

### TreeLeaf — wraps a Doc, has no children

```java
public record TreeLeaf(
        String segment,
        String name,
        String summary,
        String badge,
        String icon,
        Doc    doc                 // ANY Doc subtype
) implements TreeNode { ... }
```

The `doc` field accepts every Doc kind the framework supports: prose `ClasspathMarkdownDoc`, `SvgDoc`, `TableDoc`, `ImageDoc`, `ComposedDoc`, `PlanDoc`, `AppDoc`, `ProxyDoc`. Click routes via the Doc's `kind()` to the registered viewer.

The leaf's metadata (`name`, `summary`, `badge`, `icon`) **overrides** the wrapped Doc's display fields at serialization time. Empty strings fall through to the Doc's own values. This is what makes the same Doc appearing in two trees with two different framings legal and lossless.

## Step-by-step

### 1. Decide the tree id and structure

Pick a URL-safe slug for the tree id (e.g. `"animals"`, `"tags"`, `"by-status"`). Sketch the branch / leaf shape on paper before writing code. Branches are bare scaffolding; leaves carry the real content.

### 2. (Optional) Author SVG resources if leaves need them

If any leaf is a `SvgDoc`, you need a typed `SvgGroup`:

```java
public record MyTreeSvgs() implements SvgGroup<MyTreeSvgs> {
    public record someThing() implements SvgBeing<MyTreeSvgs> {}
    public record otherThing() implements SvgBeing<MyTreeSvgs> {}

    public static final MyTreeSvgs INSTANCE = new MyTreeSvgs();

    @Override public List<SvgBeing<MyTreeSvgs>> svgBeings() {
        return List.of(new someThing(), new otherThing());
    }
    @Override public ExportsOf<MyTreeSvgs> exports() {
        return new ExportsOf<>(this, List.copyOf(svgBeings()));
    }
}
```

SVG files go at `src/main/resources/homing/svg/<package-path>/MyTreeSvgs/someThing.svg` — the framework's classpath convention.

Themable per RFC 0017: use `var(--color-*)` tokens with literal fallbacks so the SVG inherits the active theme.

### 3. Build the tree

The canonical shape: build the tree as `public static final ContentTree INSTANCE` in a final non-instantiable class. One factory per tree.

```java
public final class MyTree {

    private MyTree() {}

    public static final ContentTree INSTANCE = build();

    private static ContentTree build() {
        var categoryA = new TreeBranch(
                "category-a", "Category A",
                "Optional summary shown on the branch tile.",
                "CATEGORY", "🅰️",
                List.<TreeNode>of(
                        leaf("alpha",  "Alpha",  "Alpha summary.",  new MyTreeSvgs.alpha()),
                        leaf("beta",   "Beta",   "Beta summary.",   new MyTreeSvgs.beta())
                ));

        var categoryB = new TreeBranch(
                "category-b", "Category B",
                "Optional summary.",
                "CATEGORY", "🅱️",
                List.<TreeNode>of(
                        leaf("gamma", "Gamma", "Gamma summary.", new MyTreeSvgs.gamma())
                ));

        var root = new TreeBranch(
                "", "My Tree",
                "Root summary — shown when the tree's root page loads.",
                "TREE", "🌳",
                List.of(categoryA, categoryB));

        return new ContentTree("my-tree", root);
    }

    private static <G extends SvgGroup<G>> TreeLeaf leaf(
            String slug, String name, String summary, SvgBeing<G> being) {
        @SuppressWarnings("unchecked")
        SvgRef<G> ref = new SvgRef<>((G) MyTreeSvgs.INSTANCE, being);
        var doc = new SvgDoc<>(ref, name, summary);
        return new TreeLeaf(slug, name, summary, "ANIMAL", "", doc);
    }
}
```

The root branch's `segment` is `""` — the tree's URL slug supplies the root segment, so the root's own segment field is unused.

### 4. Register via Fixtures.trees()

Trees flow through `Fixtures.trees()`, not through `Studio`. Override the default fixtures:

```java
public record MyFixtures<S extends Studio<?>>(Umbrella<S> umbrella)
        implements Fixtures<S>, ValueObject {

    public MyFixtures { Objects.requireNonNull(umbrella); }

    @Override public List<AppModule<?, ?>> harnessApps() {
        return new DefaultFixtures<>(umbrella).harnessApps();
    }
    @Override public NodeChrome chromeFor(Umbrella<S> node) {
        return new DefaultFixtures<>(umbrella).chromeFor(node);
    }
    @Override public List<ContentTree> trees() {
        return List.of(MyTree.INSTANCE);
    }
}
```

Then hand `MyFixtures` to `Bootstrap` in your server's `main()` instead of `DefaultFixtures`.

### 5. (Optional) Surface as a Catalogue tile

The tree is reachable by URL once registered. To put a tile on your home catalogue that links into it, use `TreeAppHost` as a `Navigable`:

```java
@Override public List<Entry<MyHomeCatalogue>> leaves() {
    return List.of(
            Entry.of(this, new Navigable<>(
                    TreeAppHost.INSTANCE,
                    new TreeAppHost.Params("my-tree", null),
                    "My Tree",
                    "Optional tile-level summary."))
    );
}
```

`new TreeAppHost.Params("my-tree", null)` — the first arg is the tree id; the second is the optional path segment (null lands on the root).

## What you get for free

Once `MyTree.INSTANCE` is registered:

- **Root page** at `/app?app=tree&id=my-tree` — Card grid of top-level children
- **Branch pages** at `/app?app=tree&id=my-tree&path=category-a` — Card grid of that branch's children
- **Leaf click** — routes to the wrapped Doc's registered viewer (SvgViewer / ComposedViewer / TableViewer / ImageViewer / DocReader / PlanAppHost / per-Doc AppModule)
- **Breadcrumbs** — `My Studio › My Tree › Category A › Alpha` built from segment names
- **Theme chrome** — same Header + Footer + theme picker + audio runtime as every other studio page
- **Doc registry harvest** — wrapped Docs flow into `DocRegistry` automatically (`DocRegistry.harvestFromTrees(...)` is called by `Bootstrap`); they're directly resolvable via `/doc?id=<uuid>` and citable from anywhere

## Common pitfalls

| Pitfall | Symptom | Fix |
|---|---|---|
| Tree id contains uppercase / spaces / underscores | `IllegalArgumentException` at construction: "must be URL-safe slug" | Use `[a-z0-9-]+` only |
| Two leaves under the same branch share a `segment` slug | URL collisions; `TreeRegistry` boot validation rejects | Make sibling slugs unique |
| Wrapping a `SvgDoc` with different metadata than another tree wraps the same `(ref)` | `DocRegistry` collision: same UUID, different `.equals()` | Use identical title/summary across uses, OR add the SvgDoc to a `DocProvider.docs()` once and let leaves reuse it |
| Forgetting to register the tree in `Fixtures.trees()` | 404 at `/app?app=tree&id=…` even though `MyTree.INSTANCE` exists | Add to `Fixtures.trees()` AND use a custom Fixtures (not `DefaultFixtures` directly) |
| Trying to put a `Plan` as a leaf via `TreeLeaf(... plan)` directly | Type error: `Plan` isn't a `Doc` | Wrap via `new PlanDoc(MyPlan.INSTANCE)` |
| Building the tree at class-init time and the leaves reference each other | `NoClassDefFoundError` / static-init cycle | Each tree should be self-contained; cross-tree refs are deferred (RFC 0016 future work) |

## When to use the typed segment kinds instead

If your "tree" is really *one composed document with structured sections* — markdown + tables + diagrams — you want a `ComposedDoc` with `TextSegment` / `TableSegment` / `SvgSegment` / `ImageSegment`, not a `ContentTree`. The doc-vs-tree decision:

- **ContentTree** — every leaf is its own clickable page; navigation is the point.
- **ComposedDoc** — every segment is part of the same document, reachable at one URL; reading top-to-bottom is the point.

A *tree of ComposedDocs* is fine and common — each leaf is a fully-typed composed page (see `MdadKitDoc` in `homing-studio` for a working example).

## Worked reference

The framework's own demo studio ships **AnimalsTree** in `homing-demo/.../AnimalsTree.java` — two branches (Animals + Halloween), six SVG leaves, registered via `DemoFixtures.trees()`. Read it for the exact shape; copy / adapt for your tree.
