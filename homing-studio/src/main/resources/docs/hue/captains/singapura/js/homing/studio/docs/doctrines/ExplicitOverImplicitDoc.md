# Doctrine — Explicit over Implicit

> **Public API surfaces return concrete, named, materialised types. A method that gives you data hands you a `List`, `Set`, `Map`, or `Optional` — never a `Stream`, `Iterator`, `Iterable`, or other lazy/single-use proxy. The caller sees the type, knows the size, can iterate twice, can `toString()` for debugging, can write a test against the result without consuming it. Internal pipelines may compose with streams freely; the boundary where data leaves a typed primitive is where the explicit form takes over.**

This doctrine binds the framework's authored API surface. Existing public methods that return lazy/proxy types are debt; new public APIs use concrete return types by default. The principle is small but its cumulative effect on a typed-everything codebase is substantial.

---

## What this doctrine commits to

Every public method on every framework type, where the return value carries data, returns one of:

- A concrete **`List<X>`** — when order matters
- A concrete **`Set<X>`** — when membership matters and order is incidental
- A concrete **`Map<K, V>`** — when key-value lookup is the relationship
- An **`Optional<X>`** — when zero-or-one is the contract
- A specific **record / typed primitive** carrying the result

The framework does *not* return:

- `Stream<X>` from public APIs (it's lazy, single-use, doesn't `toString` usefully, can't be sized cheaply, can't be iterated twice)
- `Iterator<X>` (one-shot, no `toString`, no size)
- `Iterable<X>` when a concrete collection is already in hand (caller has to materialise; loses size/contains semantics)
- Raw arrays from public APIs (mutable, no parameterised type witness)
- Lazy views, custom proxies, or framework-specific wrappers that hide whether the data has been computed

The caller writing `var docs = graph.docsCiting(target);` sees `Set<Doc> docs` in their IDE and can immediately answer: *how many? what's in it? can I iterate again? can I `toString()` to debug?* Yes to all four.

## What this doctrine bans

- **`Stream<X>` as a public return type** — even when the producer happens to have a stream internally. Producer materialises (`.toList()`, `.collect(toSet())`) before returning.
- **Returning `Iterator<X>` or `Enumeration<X>`** from public methods — single-use, opaque, no debugging.
- **Returning `Iterable<X>` when a concrete `Collection<X>` is what you have** — degrades the API for no gain.
- **Returning raw arrays** (`X[]`) from public methods — mutable, no generics, no useful operations.
- **Returning custom lazy proxies** (e.g., `LazyList`, `DeferredSet`) without explicit and documented laziness contract.
- **Returning `Map.Entry<K, V>` streams** as the public surface — return `Map<K, V>` and let the caller call `.entrySet()` if they want.
- **Mutable views of internal state** — `Collections.unmodifiableList(internalField)` is a compromise; preferred is `List.copyOf(internalField)`.

## What this doctrine permits

- **Concrete collections** — `List<X>`, `Set<X>`, `Map<K, V>`. Defensively immutable when possible (`List.copyOf`, `Set.copyOf`, `Map.copyOf`).
- **`Optional<X>`** — explicit zero-or-one contract.
- **Typed records** carrying multiple values together (`record Result(int count, List<X> items) {}`).
- **`Stream<X>` internally** — pipelines that compose filter/map/flatMap inside a method are fine. The materialisation happens before `return`.
- **`Stream<X>` when the contract IS explicit laziness** — e.g., a query API that says *"this returns potentially infinite or lazy data; the caller chooses how to consume"*. The Javadoc must document the laziness as part of the contract, not as a side effect.
- **`Iterable<X>` when implementing for-each-only types** — a typed primitive whose only contract is *"you can iterate over me"*. Different from masquerading as "implicit collection."
- **Builder-style intermediate types** that explicitly are stream-like — e.g., a fluent search-result query builder. These name their stream-ness in their type.

## Where this doctrine doesn't apply

- **Internal helpers and private methods** — implementation detail; use whatever composes best.
- **External library APIs** consumed by the framework — Java's `Stream` API, Vert.x's reactive types, third-party SDK shapes. Don't fight the external contract; convert at the framework boundary.
- **Truly infinite or pull-based data** — server-sent events, message streams, infinite generators. Stream/Flow is the right shape for these because the *unboundedness* is the contract.
- **Performance-critical hot paths** where measurable benefit comes from lazy composition — document the deviation and the measurement.
- **Test fixtures** — internal to test code; use whatever reads best.

The doctrine binds the **authored public surface of the framework's typed primitives**. Implementation details, external boundaries, and genuinely-streaming protocols are out of scope.

---

## The canonical test

Three questions before adding any public method that returns data:

1. **Can the caller read the size cheaply?** `set.size()`, `list.size()` are O(1). `stream.count()` consumes the stream. If the caller would naturally want size, return a collection.
2. **Can the caller iterate twice?** Collections support repeated iteration; streams are single-use. If iteration is a normal operation, return a collection.
3. **Does `toString()` show useful contents?** `[Doc[uuid=…], Doc[uuid=…]]` is readable; `java.util.stream.ReferencePipeline$Head@1f17ae12` is not. If debuggability matters, return a collection.

Three yeses → return a collection. Otherwise the laziness is part of the contract and must be documented as such.

---

## Why the strictness is worth it

- **Type-system honesty.** `Set<Doc>` says exactly what it is. `Stream<Doc>` says *"the caller will get something Doc-shaped, eventually, once."* The framework's typed-everything stance loses force when the most-important detail (what is this collection?) is implicit.
- **Debugging cost compounds.** A method returning `Stream<X>` that someone tried to iterate twice silently fails on the second call. The same method returning `Set<X>` works correctly. Multiply across hundreds of call sites; the lazy-return ergonomic loss is greater than any pipeline-composition gain.
- **Test friction.** `assertEquals(3, graph.children(parent).count())` consumes the stream; the next assertion that tries to inspect contents fails. `assertEquals(3, graph.children(parent).size())` doesn't. Tests with collection-returning APIs are simpler and more diagnostic.
- **AI-agent friendliness.** Agents reading typed code generate better completions when the type tells the truth. `Set<Doc>` carries more usable information than `Stream<Doc>` — the agent doesn't have to reason about laziness, terminal operations, or single-use semantics.
- **Caller flexibility, not producer ergonomics.** If a caller wants stream semantics, `set.stream()` is a free conversion. The reverse (`stream.collect(toSet())`) imposes cost on every consumer. The producer eats the conversion cost once.
- **Pairs with [Functional Objects](#ref:doc-fo).** That doctrine refused covert *behaviour* surfaces. This doctrine refuses covert *return-type* surfaces. Both name the principle: *make types tell the truth about what's there.*

---

## How to think about it

When designing a public method that returns multiple values, draw the consumer side first:

```java
// What would a caller naturally do?
var docs = graph.docsCiting(target);
if (docs.isEmpty()) { ... }            // ← needs collection
for (var d : docs) { ... render ... }   // ← needs iterable
var count = docs.size();                // ← needs sized
docs.toString();                        // ← needs debugger-friendly
```

If any of those reads as natural for the API, the return type is a collection. The pipeline composition (`docs.stream().filter(...)`) is one keystroke away if the caller actually needs it.

If you find yourself writing `.toList()` at every callsite of an internal method you're considering promoting to public, that's the doctrine telling you to materialise on the producer side.

---

## On the `Stream` exception path

There are legitimate cases where a public method returns `Stream<X>`:

- **The data is genuinely infinite or open-ended** — `Stream.generate(...)` is the right shape.
- **Backpressure / lazy evaluation is the explicit contract** — the laziness is part of why this method exists.
- **The producer would otherwise materialise an extremely large collection in memory** — and the caller is documented to consume incrementally.

When one of these holds, the method's Javadoc must:
1. State that the return value is a lazy stream.
2. Document the consumption discipline (single-use; not re-iterable; terminal operation required).
3. Explain why a collection isn't suitable.

Absent those three commitments, the doctrine refuses the `Stream` return type.

---

## See also

- [Functional Objects](#ref:doc-fo) — refused covert *behaviour* surfaces (no public statics). This doctrine refuses covert *return-type* surfaces. Same shape of *"types must tell the truth."*
- [Weighed Complexity](#ref:doc-wc) — the doctrine that justifies why a small per-method discipline (materialise before returning) is worth bearing across the framework.
- [Catalogue-as-Container](#ref:doc-cc) — *"structure-only data, no presentation directives carried."* Same general principle: be explicit about what your typed primitive carries.
- [No Stealth Data](#ref:doc-nsd) — *"visibility is the test."* This doctrine extends that principle to API return types.
