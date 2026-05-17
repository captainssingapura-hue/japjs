# Case Study — Cross-Studio References Cost Nothing

> **Cross-studio Doc references resolve correctly under a composed multi-studio Bootstrap without a single line of "cross-studio" code in the framework. Five design properties converge — identity by UUID, references as typed objects, open-set / closed-shape composition, one registry per Bootstrap, location-free wire surfaces. The property emerges; nothing implements it.**

Filed after [RFC 0012](#ref:rfc-12) shipped and the user observed: *"I suspect cross-studio reference should just work."* Four-test validation confirmed the suspicion. This study reverse-engineers why.

---

## The phenomenon

Zero framework code knows what a "cross-studio reference" is. There is no `CrossStudioResolver`, no `StudioOfOrigin` field, no `if (studio != currentStudio)` branch anywhere in the codebase. And yet:

- A `Doc` in module A may declare a `DocReference` to a `Doc` in module B.
- Under a composed multi-studio `Bootstrap`, the reference resolves correctly: clicking it in the browser navigates to the target doc, breadcrumbs render the full cross-tree chain, the title-card renders the target's title and summary.
- The only constraint is compile-time: module A's `pom.xml` must depend on module B's artifact to import `OtherStudio.SomeDoc.INSTANCE`.

The property holds because five design properties — each filed for an unrelated reason — line up.

---

## Property 1 — Identity by UUID, not by location

Every `Doc` has a `UUID`. The UUID is the *only* handle anything downstream cares about. The `DocRegistry`'s public surface is one method:

```java
public Doc resolve(UUID id) { return byUuid.get(id); }
```

Look at the wire format: `/doc?id=<uuid>`, `/doc-refs?id=<uuid>`. No `studio=` segment. No `module=` prefix. No URL fragment that encodes *"this lives in homing-studio"*. The URL is **location-free** — the doc could come from anywhere, and the URL doesn't care.

**Counterfactual.** If the URL were `/homing-studio/doc/<uuid>`, every cross-studio reference would need a *"where does this UUID actually live?"* lookup, plus URL rewriting per deploy. The frontend would need a studio→URL-prefix map propagated from boot.

## Property 2 — Reference is a typed object, not a symbolic string

```java
public record DocReference(String name, Doc target) implements Reference {}
```

`target` is **the Doc itself**, not a string `"hue.captains.singapura.js.homing.studio.docs.rfcs.Rfc0012Doc"` to be resolved at boot. The JVM's classloader has already done the cross-module work: `Rfc0012Doc.INSTANCE` is the same singleton in any module that imports it.

The reference doesn't *need* resolution — it already **is** its target. The framework just reads `ref.target().uuid()` and hands it to the registry.

This is the [Owned References](#ref:doc-or) doctrine applied one level up: *identity is the handle; everything else derives*. The catalogue/doc layer extends the principle the DOM-element layer established.

**Counterfactual.** If references were `new DocReference("rfc-12", "homing-studio:rfc-12")` (symbolic strings), every reference would need a symbol-table lookup at boot, with namespace handling, collision rules, and the very real *"what if module B isn't loaded yet?"* problem. The framework would have grown a `DocResolver` interface, configurations declaring resolution order, and a class of "missing reference at boot" runtime errors.

## Property 3 — Open-set / closed-shape composition

Per the [Catalogue-as-Container](#ref:doc-cc) doctrine: *the set of catalogues is open; the shape every catalogue satisfies is closed*. The same law governs `Doc`s — `DocProvider.docs()` is just `List<Doc>`.

Composition is therefore **set union, not transformation**:

```java
for (var studio : studios) for (var p : providers) allDocs.addAll(p.docs());
return new DocRegistry(allDocs);
```

That's it. Three lines. No merging logic, no per-source transformation, no studio-aware wrapping. Adding a studio adds Docs; the registry shape is unchanged.

**Counterfactual.** If studios contributed Docs through different mechanisms (one returns `List<Doc>`, another returns `Map<String, Doc>`, a third uses a `DocLoader` factory), the union step would need to dispatch on source type — a known anti-pattern that grows quadratically with the number of studio kinds, and that no amount of polymorphism quite tames.

## Property 4 — One registry per Bootstrap, never per studio

The `DocRegistry` is constructed **once**, at composition time, from the union of all studios. After that, every consumer (`DocGetAction`, `DocRefsGetAction`, `DocReaderRenderer`) talks only to *the* registry. Studios stop being separately-addressable the moment Bootstrap runs.

This is the [Functional Objects](#ref:doc-fo) doctrine applied to system composition: the registry is a stateless object with one `resolve` method, instantiated once with all its data, no INSTANCE-of-X-studio thing to discriminate.

**Counterfactual.** If each studio kept its own `DocRegistry`, every cross-studio resolution would need either a *registry-of-registries* (one extra indirection per lookup) or an explicit *"ask each registry until one answers"* loop (linear cost per lookup). Both are slower **and** more code. The framework also picks up an "ambiguous reference" failure mode (two studios shipping the same UUID is now resolvable by deployer-defined order — a new configuration surface for an essentially-bug condition).

## Property 5 — The renderer is the studio-blindest layer

Front-end code, in `DocReaderRenderer._renderReferences`:

```js
titleLink.href = "/app?app=doc-reader&doc=" + encodeURIComponent(r.uuid);
```

The frontend takes the JSON's `uuid` field, builds a URL, sets `href`. That's the entire navigation logic. No client-side studio-of-origin lookup, no special-case for *"this UUID is from a different studio."*

**Counterfactual.** If the frontend had to know which studio served which doc (e.g. to build `/studio/homing/doc/<uuid>`), the JSON envelope would need a `module` field, the frontend would need a studio→URL-prefix map propagated at boot, and "two studios with overlapping URL-prefix routing" would become a deploy-time concern.

---

## What the framework gave up to get this

Three trade-offs are accepted in exchange:

1. **UUIDs must be globally unique.** Two studios that ship the same UUID (perhaps because one fork-copied the other's Doc) will collide at boot — the `DocRegistry` constructor throws hard. This is the price of flat-namespace identity. Mitigated by the framework's "use `uuidgen` once, never change it" discipline and by `StudioDocConformanceTest`'s uniqueness check.

2. **The referring module must depend on the target module at Maven level.** `new DocReference("rfc-12", Rfc0012Doc.INSTANCE)` requires `Rfc0012Doc` to be on the classpath. The framework can't make this cost zero — Java's import system doesn't have a *"soft reference to a class loaded by some other module"* feature. **But the framework also doesn't make it harder than necessary**: the dep is a normal `<dependency>` block, no special "framework-aware" indirection. The constraint is paid in the build system, not at runtime.

3. **No symbolic refs.** You can't write `new DocReference("rfc-12", "homing-studio:Rfc0012")` to defer the dependency. Typed refs are the only shape. This eliminates an entire class of "missing reference at boot" runtime errors, at the cost of forbidding loose coupling at the reference layer.

The trade-offs are uniform across the framework's identity-handling — they're not specific to cross-studio reference; they fall out of the broader identity discipline.

---

## Doctrinal lineage

This works because three doctrines, each filed for unrelated reasons, line up:

- **[Catalogue-as-Container](#ref:doc-cc)** — *identity is intrinsic, the set is open, the shape is closed*. The doc-registry is just a different surface of the same pattern: an open set of typed `Doc`s, a closed shape (`uuid()`, `title()`, `references()`).
- **[Functional Objects](#ref:doc-fo)** — *no public statics; behaviour belongs on `INSTANCE`-handled records*. Every `Doc` has one canonical instance per JVM, so cross-module identity is JVM identity.
- **[Owned References](#ref:doc-or)** — *identity is the handle; URLs derive from identity*. Already established the principle the doc-registry leans on at the DOM-element layer; the doc layer is its natural extension.

None of these doctrines mention cross-studio composition. The property emerges because each doctrine independently refuses location-coupled identity.

---

## The general lesson

The cheap features in a framework are the ones that result from saying **no** enough times early. This framework said no to:

- location-encoded URLs
- symbolic-string references
- per-studio sub-registries
- studio-of-origin metadata in wire formats
- factory-shaped Doc construction

…each for its own reason. Cross-studio reference resolution is what's left after those refusals. It costs nothing because there's nothing to do — the system never picked up the cost in the first place.

Filed retrospectively, the principle is:

> **Identity-by-content, composition-by-union, location-free at every wire surface.**

If a future feature wants to be similarly cheap, those are the three properties to preserve. If a future addition would compromise any of them — adding a studio segment to URLs "for clarity", adding a `module` field to a wire format "for debuggability", introducing a per-studio sub-registry "for performance" — the cost-free composition properties unravel one at a time.

---

## How to think about it

When designing a new framework-level primitive, ask three questions:

1. **What identifies it?** If the answer is a path, a namespace, or a location, you've coupled identity to location and lost compositional freedom. Pick an intrinsic identifier (UUID, class, INSTANCE handle) and let URLs derive.
2. **How do references to it work?** If the answer is a string that must be resolved at boot, you've shipped a resolver + a class of boot-time runtime errors. Make the reference a typed handle; let the language's import system do the work.
3. **How does it compose with peers?** If the answer involves transforming or wrapping per source, you've spent the union cost up-front. Make composition flat set-union; let the consumer not care.

Pass all three and the cross-studio property — or whatever the next analogous property is — falls out for free.

---

## See also

- [RFC 0012 — Typed Studio Composition](#ref:rfc-12) — the RFC that produced the multi-studio composition path the case study examines.
- [Catalogue-as-Container doctrine](#ref:doc-cc) — the open-set / closed-shape pattern at the catalogue layer.
- [Functional Objects doctrine](#ref:doc-fo) — singleton-`INSTANCE` records as the canonical handle.
- [Owned References doctrine](#ref:doc-or) — identity-as-handle at the DOM-element layer; the principle this study sees one level up.
