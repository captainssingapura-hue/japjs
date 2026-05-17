# Meta-Doctrine — Ontology First

Before adding a primitive to the framework, name its ontological category. If an existing category fits, realise the primitive as an instance of that category. If no category fits, file the new ontological entry first — definition, identity, invariants, relationships, realisations — and only then implement.

## The discipline

> Implementation without prior ontological placement is the smell that catches: you are building a *thing* without naming *what kind of thing it is*.

Three permitted moves when a new framework primitive is proposed:

1. **Realise an existing ontology entry.** The new primitive is a fresh instance of a category that already exists. Implementation proceeds; no ontology change. Example: adding a new prose doc — `Doc` already exists; the new record is just one more `Doc`.
2. **Extend an existing ontology entry.** The new primitive is a new subtype of a category that already exists, requiring the category's permits list (or its enumerated realisations section) to grow. The ontology entry is amended *before* implementation. Example: adding `ProxyDoc` as a new `Doc` subtype — amend the `Doc` entry to include the new realisation, then implement.
3. **Introduce a new ontology entry.** The new primitive does not fit any existing category. The new ontology entry is filed first — definition, identity, invariants, relationships, realisations — and only then is implementation begun. Example: when `ContentTree` was introduced, it required the `ManagedTree` ontology to be named explicitly so `Catalogue` and `ContentTree` had a shared definition to inherit.

Any move outside these three is the failure mode this doctrine catches.

## The failure mode it prevents

Most OO codebases grow by free-floating class authorship. A new requirement arrives; a developer writes a new class to handle it; the class joins the codebase. Over time, hundreds of classes accumulate, none with a definitive answer to *what kind of thing it is*. New contributors cannot reason about the system because the system has no stated vocabulary. Refactoring loses traction because no two classes share a clearly-named role.

The result is what most large codebases feel like: ad hoc, drifting, comprehensible only to whoever wrote each piece.

Ontology First refuses this failure mode at the gate. A new primitive cannot enter the framework without a stated answer to "what kind of thing is this?" — and the answer must reference a named ontology entry, not be made up on the spot.

## Why this is a doctrine, not an ontology entry

Ontology entries define *what kinds of things exist*. This document does not name a category of being; it prescribes *how to act when proposing a new primitive*. That is an operational principle — a doctrine.

The framework's three foundational layers split cleanly:

| Layer | Question | Reads as |
|---|---|---|
| **Ontology** | What is there? | A taxonomy of being |
| **Doctrine** | How should you act given what is there? | An operational manual |
| **RFC** | What should change? | A historical proposal |

Ontology First sits at the doctrine layer. It governs how new things become ontology entries; it does not itself name a category. The recursion is intentional: this doctrine is *about* the Ontology layer, and that is precisely its role — to make the Ontology layer enforceable rather than merely descriptive.

It lives under `Meta`, not under `Doctrines`, because its scope is the Meta layer itself. Ordinary doctrines (operational principles for working with existing primitives) live under the Doctrines catalogue alongside their peers.

## How it works in practice

When a change is proposed that introduces or modifies a framework primitive, the review checklist asks:

- **Which ontology entry does this realise?** If the answer is "none," the next question must be either *which existing entry is being extended* or *which new entry is being filed first.* "We will figure it out later" is not a permitted answer.
- **If a new entry is being filed:** has it been written? Does it have the five required sections (definition, identity, invariants, relationships, realisations)? Has a conformance test been added or queued? An ontology entry without a conformance test is a definition without enforcement.
- **If an existing entry is being extended:** has the entry's realisations list been updated? Does the existing conformance test cover the new subtype?

The discipline is not about ceremony; it is about *naming*. Most ontology placements take a sentence. The work is the thinking, not the typing.

## What it does not require

It does not require:

- Filing an RFC for every primitive — small primitives that clearly realise existing categories just get implemented.
- Re-litigating the ontology for every change — ontology entries are durable and rarely change.
- Architectural review boards or process overhead — the doctrine is enforced by the contributor herself, supported by conformance tests at build time.
- Defining everything ontologically — most of a codebase is implementation, not new categories of existence. New ontology entries are rare events.

What it does require is *honesty*: every new framework primitive has a stated kind. Skipping the question is the failure.

## Why this matters for OO

The discipline of:

1. Naming what categories of things exist
2. Defining each category's identity, invariants, relationships
3. Only then choosing classes / interfaces / records to realise those categories

…is the difference between **ontologically grounded design** and **ad hoc class authoring**. The latter dominates the industry. Code feels arbitrary because nothing tells you what *kind of thing* any given class is — it is just a class.

When the framework instead says "everything you can register as a leaf in a tree is a *Doc*, and Doc means exactly these eight things," the next question — "should I add a `NewKindOfThing`?" — answers itself: "is `NewKindOfThing` a Doc? If yes, realise it as a Doc subtype. If no, either extend the ontology or reconsider." The ontology becomes a forcing function for design clarity.

This is what good OO actually feels like when you encounter it. Most people never do, because most projects never do the ontological work. Ontology First is the meta-doctrine that ensures Homing keeps doing it.

## Relationship to existing doctrines

Several existing doctrines have ontological residue — they partially define a type while also prescribing how to use it. After the Meta layer is established, the definitional half can lift to Ontology while the operational half stays as a doctrine. This is gradual work; the existing doctrines stand as they are until each is refined.

Until then, doctrines that straddle definition and prescription continue to do both jobs. Ontology First does not demand retroactive cleanup; it governs only what enters the framework from this point forward.
