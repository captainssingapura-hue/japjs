# Doctrine — Weighed Complexity

> **Lines of code are not equal. Cost has five dimensions: cognitive density, blast radius, reversibility, perpetual authoring tax, and failure mode. LOC is at best a signal for the first; it lies about the others. Size a change by its worst-cost dimension, not by its line count.**

This is the doctrine the framework commits to whenever a design proposal is being weighed for adoption. Filed retrospectively after an RFC was nearly adopted on the strength of a "~355 lines, mostly mechanical" estimate that hid a perpetual authoring tax across every catalogue record forever — a cost LOC doesn't measure.

---

## The five dimensions

### 1. Cognitive density

How much a reader must hold in their head to understand the line.

| Lower | Higher |
|---|---|
| `var foo = bar();` | `interface L1_Catalogue<P extends L0_Catalogue<P>, Self extends L1_Catalogue<P, Self>>` |
| Imperative branch | CRTP self-bound generic |
| New file with no public API | Type signature in a sealed interface |

One CRTP-bounded type signature can be costlier than fifty straight-line method bodies. Reading is what authors and reviewers do far more often than writing; the per-read cost compounds.

### 2. Blast radius

How many call sites must change *now*, and how many lock in *forever* once the change ships.

| Lower | Higher |
|---|---|
| Adding a new variant to an existing sealed sum (exhaustive switches break, find-and-fix) | Adding a type parameter to a widely-implemented sealed interface (every implementor migrates) |
| Adding an optional field to a record (record constructor pattern matching widens) | Removing a public method (every caller, downstream included, breaks) |
| Internal-only refactor | API change visible to downstream studios |

A 5-line change to a sealed interface can require hundreds of changes downstream. A 100-line new file with no public API changes nothing about existing code.

### 3. Reversibility

How hard it is to undo if the design turns out to be wrong.

| Lower | Higher |
|---|---|
| New behavior behind a feature flag | New type parameter on a published API |
| Internal map structure | Sealed permits clause |
| Renderer change shipping in next release | Wire-stable UUID assignment |
| Boot-time validation | Compile-time invariant in a public interface |

Boot-time runtime checks are far more reversible than compile-time invariants: you can soften the check without changing any caller's source. A type parameter once added to a public sealed interface is approximately irreversible — every downstream user must edit to remove it.

### 4. Perpetual authoring tax

How much every future author pays for this change, every time they touch the affected surface, *forever*.

| Lower | Higher |
|---|---|
| One-time migration of N existing records | One extra generic argument every new record must declare from now on |
| Setup boilerplate in one bootstrap file | Cognitive overhead reading the framework's API |
| Internal complexity hidden behind a typed factory | Type signature complexity visible at every call site |

The tax is paid not just by the framework's own catalogues, but by every downstream studio's catalogues, every test fixture, every example in every skill — *for as long as the framework lives*. Migration cost ends; authoring tax compounds.

### 5. Failure mode

How and when a defect surfaces.

| Better | Worse |
|---|---|
| Compile error at the line of mistake | Runtime exception with stack trace |
| Boot-time error with clear diagnostic | Silent misbehaviour with corrupted output |
| Single failure | Cascade |
| Loud (test failure, red bar) | Quiet (subtle wrong rendering, never tested) |

A compile error at the right line is the best failure mode. A runtime crash with a clear message is acceptable. A silent miscomputation that ships to users is the worst. Designs should be evaluated on the failure mode of their mistakes, not just the existence of the mistake class.

---

## What this doctrine commits to

1. **Design estimates name dimensions, not just lines.** *"~350 lines"* is not a complete estimate. *"~350 lines, of which 80 retype every catalogue record forever, blast radius across 27 files, reversibility ≈ 0"* is.
2. **The worst-cost dimension governs the verdict.** A design that's cheap on four dimensions and catastrophic on one is the catastrophic design.
3. **Lines that buy nothing don't get to count toward type safety.** Boilerplate retypes that exist only to make the type system happy carry full cost and provide marginal value. Weigh accordingly.
4. **Reversible designs are preferred over irreversible ones when behavior is identical.** A boot-time check should win over a compile-time check unless the bug class is frequent enough that catching it minutes earlier matters.

## What this doctrine bans

- **"It's only N lines" as a green-light.** Line count alone is never sufficient justification.
- **Hand-waving the perpetual cost.** *"Mechanical migration"* is honest about write-time; it's silent about every future author paying the tax. Both must be quoted.
- **Adding type-system machinery without weighing what bug class it actually prevents.** Catching a bug that never happens at zero cost is fine. Catching it at high perpetual cost is over-engineering.

## What this doctrine permits

- **LOC as a signal**, when the dimensions are uniformly cheap (additive features, isolated changes, mechanical text edits in non-public code).
- **Heavy designs**, when the dimensions are weighed and the bug class being prevented is verified — through actual incident history, not speculation — to be frequent and high-severity.
- **The same design adopted at different costs for different parts of a system.** A pattern that's worth perpetual authoring tax in a 5-record framework module is not necessarily worth it in a 50-record downstream catalogue tree.

---

## Where this doctrine doesn't apply

A tactical change with zero public-API surface, contained to a single internal file, with a clear rollback path — *just write it, ship it, judge by result*. The dimensions are uniformly cheap; LOC is roughly proportional to effort. Mandating dimension analysis for every line edit is itself a perpetual tax.

The doctrine binds **design-phase estimates of changes that cross any of**: public API, sealed interface permits, type parameter shape of a widely-implemented type, framework conventions every downstream is expected to follow. These are the places where lines lie about cost.

---

## Why the strictness is worth it

- **LOC counts mislead at the design phase, when reversibility is still high.** Once the heavy design ships, the cheap design has been forgone — possibly forever, if the heavy one is irreversible.
- **Locked-in type signatures last forever; the authoring tax compounds across every downstream use, every test fixture, every example.** Compound cost is invisible in any single-PR review.
- **Honest weighing catches over-engineering before it ships.** Catching it later means either living with it or paying the irreversible-revert cost — neither outcome is good.
- **The framework's own typed-everything pattern requires this discipline.** Each pattern lift (Layer ladder, KeyCombo, typed catalogue levels, badge/icon) was a deliberate choice with explicit cost evaluation. The doctrine names what made those decisions correct *and* what would have made them wrong.

---

## How to think about it

For any non-trivial design proposal, write down — *before* defending the design — a five-column table:

| Component | Cognitive density | Blast radius | Reversibility | Authoring tax | Failure mode |
|---|---|---|---|---|---|
| `…` | low / med / high | N files | high / low / nil | none / one-time / perpetual | compile / boot / runtime / silent |

If any component is **high cognitive density + high blast radius + low reversibility + perpetual authoring tax**, the design is on probation. It can still ship — but it needs to be paying for *something* commensurate.

When in doubt, ask: *"How many people will pay this cost, how often, and how irreversibly?"* If the answer surprises you, the line count was lying.

---

## See also

- [RFC 0011](#ref:rfc-11) — the worked example. An earlier draft of RFC 0011 was nearly adopted on a 355-line estimate; re-costing against this doctrine reduced the design to ~120 framework lines + zero migration + one localised `@SuppressWarnings`, with the same user-visible outcome.
- [First-User Discipline](#ref:fu) — companion principle. The first user pays the design's costs concretely; weighing those costs honestly is upstream of choosing the design they'll use.
- [Catalogue-as-Container](#ref:cc) — example of a doctrine that *is* worth its perpetual authoring tax. Every catalogue declaration commits to a typed structural shape, every author pays for it, and the cost is justified by the navigation correctness it buys at compile time.
