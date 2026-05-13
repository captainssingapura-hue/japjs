---
name: migrate-from-0-0-11
description: Use this skill when the user is upgrading a Homing studio from release 0.0.11 to 0.0.100. The bulk of the work is a mechanical migration of every Catalogue declaration to the new typed-level system (RFC 0005-ext2) — sealed L0..L8 family with typed parent(), split subCatalogues() / leaves() accessors, and a slimmer Entry sum. Triggers — "upgrade homing", "migrate from 0.0.11", "Catalogue is sealed", "entries() doesn't compile", "Entry.OfCatalogue gone", "implements Catalogue can't extend sealed class". Skip if the studio is already on 0.0.100 or later.
---

# Migrate a Homing Studio from 0.0.11 to 0.0.100

0.0.100 lands [RFC 0005-ext2 — Typed Catalogue Levels](https://github.com/captainssingapura-hue/homing/blob/main/homing-studio/src/main/resources/docs/hue/captains/singapura/js/homing/studio/docs/rfcs/Rfc0005Ext2Doc.md). Every downstream studio that authored a `Catalogue` record needs a small mechanical migration. There are six changes; each is local to one record at a time.

## TL;DR — the six changes

| # | Was (0.0.11) | Is (0.0.100) |
|---|---|---|
| 1 | `implements Catalogue` | `implements L0_Catalogue` (root) **or** `implements L<N>_Catalogue<ParentClass>` |
| 2 | `entries(): List<Entry>` | `subCatalogues(): List<L<N+1>_Catalogue<Self>>` + `leaves(): List<Entry>` |
| 3 | `Entry.of(MyCatalogue.INSTANCE)` | Moved out of `entries()` — sub-catalogues live in `subCatalogues()` |
| 4 | Non-root catalogues had nothing | Non-root catalogues **must** declare `public ParentClass parent()` returning the parent INSTANCE |
| 5 | Multiple parents allowed (runtime check) | A class can only declare one parent type — multi-parent is a compile error |
| 6 | Interleaved entries rendered in author order | Sub-catalogues always render *before* leaves (Option A) — author-controlled interleave is gone |

Compile errors in your studio will be the migration guide. Java's sealed-class diagnostic is precise: *"class X is not allowed to extend sealed class: Catalogue (as it is not listed in its 'permits' clause)"* → that's change #1 staring you in the face.

## Step 1 — Identify your tree shape

Decide each catalogue's level before touching code. Map the tree on paper.

- **Root catalogues** (the one passed to `StudioBrand`) → **L0**. Exactly one per studio.
- **Direct children of L0** → **L1**.
- **Children of L1** → **L2**. Etc.

Maximum depth supported today is **L8**. Most studios are L0 with one or two L1 sub-catalogues.

Example for a studio with `MyHomeCatalogue` (root) containing `MyDoctrinesCatalogue` and `MyJourneysCatalogue`:

```
MyHomeCatalogue        → L0
├── MyDoctrinesCatalogue   → L1<MyHomeCatalogue>
└── MyJourneysCatalogue    → L1<MyHomeCatalogue>
```

## Step 2 — Migrate each catalogue record

For the **root** (`L0`):

```java
// Before
public record MyHomeCatalogue() implements Catalogue, DocProvider {
    public static final MyHomeCatalogue INSTANCE = new MyHomeCatalogue();

    @Override public String name()    { return "My Studio"; }
    @Override public String summary() { return "..."; }

    @Override public List<Entry> entries() {
        return List.of(
                Entry.of(MyDoctrinesCatalogue.INSTANCE),    // sub-catalogue
                Entry.of(MyIntroDoc.INSTANCE),              // doc leaf
                Entry.of(new Navigable<>(...))              // app leaf
        );
    }

    @Override public List<Doc> docs() { return List.of(MyIntroDoc.INSTANCE); }
}
```

```java
// After
public record MyHomeCatalogue() implements L0_Catalogue, DocProvider {
    public static final MyHomeCatalogue INSTANCE = new MyHomeCatalogue();

    @Override public String name()    { return "My Studio"; }
    @Override public String summary() { return "..."; }

    // Sub-catalogues — typed by the next level down.
    @Override public List<L1_Catalogue<MyHomeCatalogue>> subCatalogues() {
        return List.of(MyDoctrinesCatalogue.INSTANCE);
    }

    // Everything else — Docs, Plans, AppModules.
    @Override public List<Entry> leaves() {
        return List.of(
                Entry.of(MyIntroDoc.INSTANCE),
                Entry.of(new Navigable<>(...))
        );
    }

    @Override public List<Doc> docs() { return List.of(MyIntroDoc.INSTANCE); }
}
```

For a **non-root** (`L1`, `L2`, …):

```java
// Before
public record MyDoctrinesCatalogue() implements Catalogue {
    public static final MyDoctrinesCatalogue INSTANCE = new MyDoctrinesCatalogue();

    @Override public String name() { return "Doctrines"; }
    @Override public List<Entry> entries() {
        return List.of(Entry.of(MyDoctrineDoc.INSTANCE));
    }
}
```

```java
// After
public record MyDoctrinesCatalogue() implements L1_Catalogue<MyHomeCatalogue> {
    public static final MyDoctrinesCatalogue INSTANCE = new MyDoctrinesCatalogue();

    // Required for every non-root level: the typed parent INSTANCE.
    @Override public MyHomeCatalogue parent() { return MyHomeCatalogue.INSTANCE; }

    @Override public String name() { return "Doctrines"; }

    // No sub-catalogues here — inherit the empty default from L1_Catalogue.
    // Only declare subCatalogues() if you have L2 children.

    @Override public List<Entry> leaves() {
        return List.of(Entry.of(MyDoctrineDoc.INSTANCE));
    }
}
```

The mechanical part:

1. `implements Catalogue` → `implements L<N>_Catalogue<...>` (drop `Catalogue` import, add the new level interface import).
2. Rename `entries()` → `leaves()`, removing any `Entry.of(<sub-catalogue>)` calls from it.
3. For each removed sub-catalogue entry, add it to a new `subCatalogues()` method typed `List<L<N+1>_Catalogue<ThisClass>>`.
4. Non-root only: add `@Override public ParentClass parent() { return ParentClass.INSTANCE; }`.
5. Delete imports of `Catalogue`, `Entry.OfCatalogue` if you had them — neither exists anymore.

## Step 3 — Audit your registration

The `catalogues` list passed to `StudioBootstrap.start(...)` still works the same way — every catalogue in the tree must be in that list. Boot validation now also checks:

- The brand's `homeApp()` must be an `L0_Catalogue`. If you previously wired a non-root catalogue as the home-app, you'll get a clear runtime error pointing at the level mismatch.
- Each sub-catalogue's `parent()` must return the containing catalogue's INSTANCE. If you forget the `parent()` override or return the wrong instance, boot fails with the offending class names.

## Step 4 — Verify

```bash
mvn install
```

Once it compiles, the studio runs. The only visible behavioural change is the render order in catalogues that mixed sub-catalogues and leaves: sub-catalogues now appear first, leaves second. If a particular catalogue needs the old interleaved order, restructure the data — promote a leaf into its own one-doc sub-catalogue, or vice versa.

Breadcrumbs above any Doc or Plan now derive from the typed `parent()` chain automatically — you'll see the full path (e.g. `My Studio › Doctrines › Pure Component Views`) instead of the old flat `Home › Pure Component Views`. No renderer or URL changes are required to get this — it's a free upgrade once the catalogues are typed.

## What this skill does NOT cover

- **RFC 0006 (Wallpaper Backdrops)** — additive, no migration required. Use the new `Theme.backdrop()` mechanism in new themes; old themes work unchanged.
- **RFC 0007 (Theme Audio Cues)** — additive. Themes that want audio cues opt in via the new `Cue` ADT; themes without cues compile and run unchanged.
- **RFC 0008 (Interactive Theme Experiences)** — additive. Existing themes don't need to change.

The only breaking changes in 0.0.100 are the six listed at the top — all from RFC 0005-ext2.

## Anti-patterns

- **Don't try to `@SuppressWarnings` your way past a sealed-class error.** The compiler is correctly forbidding `implements Catalogue` — there's no flag that re-enables it. Migrate the record.
- **Don't keep `Entry.OfCatalogue` references in legacy switch statements.** The variant no longer exists. Switches over `Entry` are now exhaustive over three cases (`OfDoc`, `OfApp`, `OfPlan`).
- **Don't declare `subCatalogues()` to return `List<? extends L<N+1>_Catalogue<?>>` if you can write the narrow `List<L<N+1>_Catalogue<ThisClass>>` instead.** The narrow type buys compile-time enforcement that children's `parent()` types match this catalogue. Wildcards weaken the guarantee.
- **Don't add `parent()` to L0 catalogues.** L0 has no parent — the interface doesn't even declare the method. Trying to add it produces "method does not override or implement a method from a supertype."

## Reference reading

After the migration, browse these in a running studio:

- `Studio › RFCs › Architecture › RFC 0005-ext2 — Typed Catalogue Levels` — the design doc.
- `Studio › Defects › Defect 0004 — Flat Breadcrumbs` — the UX bug this RFC closes.
- `Studio › Releases › 0.0.100` — the release notes covering everything in this skill (plus the additive RFCs 0006/0007/0008).
