# Doctrine — First-User Discipline

> **We must be the first user of whatever we are building. Every framework primitive ships alongside its first in-tree consumer; speculative primitives without a real use site are deferred until one exists. When a chokepoint emerges — a single endpoint, registry, or contract that future modules will benefit from — every existing user retrofits to it immediately, in the same change. The studio dogfoods itself: the framework hosts the [doctrines](#ref:pcv) that govern it, runs the conformance that polices it, and tracks its own development with the same [plan](#ref:pc) and [catalogue](#ref:cc) trackers it ships. If we wouldn't use it ourselves, we don't ship it.**

This is the meta-doctrine — the rule that governs which other rules we're allowed to introduce, and how. It exists because the framework's worst failure mode is **building beautiful primitives no one uses, including us.**

---

## What this doctrine commits to

Five sentences, five commitments:

1. **First-user first.** Every framework primitive lands alongside a real in-tree consumer in the same change. No speculative abstractions ahead of demand.
2. **Retrofit on chokepoints.** When a chokepoint emerges (a single endpoint, single registry, single typed contract), retrofit *every* existing in-tree user to it immediately. Half-migrated frameworks are worse than un-migrated ones.
3. **Recursion-proof.** The framework demonstrates each capability by using it on itself. Doctrines live as Docs; release plans live as Plan trackers; conformance polices the studio's own modules.
4. **Honest deprecation.** When a path becomes legacy (e.g. an AppModule predates a registry it should now use), it's flagged as deprecated *and migrated*, not just commented over. The studio tree must not contain "we'll get to it" code paths.
5. **Speculative is opt-in.** Experimental primitives that don't yet have a real consumer live in a clearly-marked sandbox or branch, never in `main`'s default code path.

---

## Why this matters

Frameworks die by accreting **un-used capability**. Each unused primitive:

- Forces every other primitive to integrate with it ("just in case someone uses it"),
- Carries documentation that drifts because no one reads it,
- Surfaces edge cases at the wrong time — first real use, in production.

By being our own first user, we close every loop:

- The capability is **observed under load** — the studio actually exercises it.
- The documentation is **de-facto verified** — if our own pages stop working, the doc is wrong.
- The migration cost of breaking changes is **paid by us first** — we feel what downstream feels.

---

## What this doctrine bans

- **No speculative APIs.** We don't ship `Foo.advanced()` "for downstream that might need it later." If no in-tree consumer needs `advanced()`, the method doesn't exist.
- **No partial migrations to a new chokepoint.** When `/brand` lands as the single source of truth for the brand, every doc-side AppModule migrates in the same change. Not "next version."
- **No "internal-only" code paths in the same tree.** The framework's own studio uses the same public API every downstream uses. If we need to escape that API, the API is incomplete — fix the API, not the studio.
- **No theoretical doctrines.** Every doctrine in this catalogue must reflect a real choice the studio actually made. We don't speculate.

---

## What this doctrine permits

- **Iterative shipping.** A primitive can be added with a minimal first user, then expanded as more use sites appear. The first user doesn't have to be the *complete* user.
- **Refactoring under pressure.** When the architecture is wrong, retrofit. The point isn't "don't change things"; it's "don't ship un-used things."
- **Studio-internal helpers.** A utility class used only by one studio module is fine — that's still a real consumer. The ban is on capability without a consumer, not on narrow consumers.
- **Explicit deferrals.** When we deliberately defer a migration (e.g. "doc-side modules update in v1.x"), the deferral is recorded in the relevant tracker's open decisions and re-surfaced when the related feature ships next.

---

## How we know we're following it

The studio itself is the proof. Specific markers:

- **`StudioCatalogue`, `DoctrineCatalogue`, `JourneysCatalogue`, `BuildingBlocksCatalogue`** — typed catalogues, served by `CatalogueAppHost` from `homing-studio-base`. The framework's own catalogue UI is built with the catalogue primitive.
- **`Rfc0001..Rfc0005Ext1PlanData`, `V1PlanData`** — typed plan trackers, served by `PlanAppHost`. The framework's RFC/release tracking uses the same plan kit any downstream installation gets.
- **`StudioCatalogueConstructsTest`, `StudioPlanConstructsTest`, the conformance suite** — every conformance base shipped from `homing-conformance` is extended by the studio. We test ourselves with our own tests.
- **The doctrines themselves** — including this one — are typed `Doc` records served by `DocReader` through `DocRegistry`. The framework's documentation runs on the framework.
- **`StudioBrand` with the `SvgRef` logo** — the framework's brand uses the typed-SVG primitive that downstream apps will use.

When a future feature lands and you can't point at "the studio's own use of this," the feature isn't done.

---

## How to apply it day-to-day

Before shipping a new framework primitive, ask:

1. **Who in this tree uses this today?** If "no one yet," stop — go find or create the first user.
2. **What existing path will this replace?** If the answer is "future code," be honest: it's speculative. If the answer is "this specific module," migrate that module in the same change.
3. **If a chokepoint is forming, list every existing consumer.** All of them migrate in this PR, or the chokepoint isn't really a chokepoint — it's an alternate path that adds entropy.
4. **Write the conformance / construct test alongside the primitive.** Not as a follow-up. The test is the most enduring form of "first user."

---

## Where this doctrine doesn't apply

- **Hard-to-test foundational primitives** (the kernel's bytecode-level mechanics) sometimes have no other consumer beyond the immediate framework wiring. The discipline still applies in spirit — but their "first user" is the framework code itself, and that's acceptable.
- **External integrations** with third-party libraries — we may build the integration before any internal code uses it, because the *external* consumer (the third party's own library) is real. This is the only acceptable form of "ship without an in-tree consumer."

---

## See also

- [Pure-Component Views](#ref:pcv) — the doctrine that all studio views obey, and that the studio's own pages are the proof of.
- [Catalogues as Containers](#ref:cc) — the catalogue primitive whose first users are the studio's own catalogues.
- [Plans as Living Containers](#ref:pc) — the plan primitive whose first users are the studio's own RFC trackers.
