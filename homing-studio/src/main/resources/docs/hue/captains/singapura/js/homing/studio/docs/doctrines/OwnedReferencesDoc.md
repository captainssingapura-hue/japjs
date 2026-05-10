# Doctrine — Owned References

> **Every element has exactly one owner. References are obtained at creation time and passed through composition. They are never recovered by selector lookup. Action on an element is a method call on its owner — not a `getElementById` away.**

This is the rule that turns the DOM from a global namespace into an ownership tree.

---

## In practice

### Banned

- `document.getElementById(...)`
- `document.querySelector(...)`, `document.querySelectorAll(...)`
- `element.querySelector(...)`, `element.querySelectorAll(...)`
- Walking the DOM tree (`parentNode`, `childNodes`, `nextSibling`, `firstElementChild`, …) to reach an element you didn't create.
- "Find element by class / id / tag and act on it" patterns of any flavour.

### Permitted

- **Holding a Component handle** returned by its construction. A handle is the typed reference to the Component instance and the element subtree it owns.
- **Calling methods on the handle** to act on the element: `searchInput.focus()`, `card.setTitle("Hi")`, `list.append(item)`.
- **Passing handles through composition.** A parent constructs its children and stores their handles; handles can be passed to siblings via the parent so siblings can wire callbacks against each other.
- **Reading values** from the handle's typed methods (`searchInput.value()`), not from looking up the underlying DOM node and reading a property off it.

### Out of scope

- **Framework code internal to a Component impl**, where the impl naturally has the element it just created.
- **The bypass mechanism** shared with [Managed DOM Ops](#ref:mdo), for unavoidable third-party interop.

---

## Why message-passing beats lookup

- **No stringly-typed selectors.** A typo or rename is a compile-time break (Java side) or a missing-method error (JS side), not a silent runtime nothing-found.
- **Encapsulation holds.** A Component's element subtree is private to it. Other code can't reach in via `querySelector`; it must go through methods the Component chose to expose. The Component is in control of its API.
- **Ownership is traceable.** Every element has exactly one owner — the Component that constructed it. Who can act on element X is exactly who has its handle. Mystery-mutations stop happening.
- **Composition is explicit.** If a sibling needs to react to another sibling's change, the parent wires it: `search.onChange(q => list.update(...))`. The dependency is visible in the code, not hidden inside a selector pattern.
- **Actor model.** Each Component is an actor; methods are messages; the framework holds no shared mutable state addressable by string. The mental model collapses to "send messages to handles you already have."

---

## How to think about it

Imagine the DOM has no `id` attribute at all. Now imagine there's no way to traverse parents or children. The only thing you can do is *be given* something — a handle to an element, a slot to mount a child into — and call methods on it.

That world is what this doctrine constructs. Ids and selectors still *exist* in the underlying DOM; the framework just doesn't let you use them from consumer code. Every action on an element happens because someone, somewhere, was given the handle by the Component that owns it.

Selectors are a convenience for one-off scripts. In a project with typed end-to-end ownership, they are a backdoor through every encapsulation rule the rest of the framework enforces. We close the backdoor.

---

## See also

- [Pure-Component Views](#ref:pcv) — bans HTML in consumer code.
- [Methods Over Props](#ref:mop) — Components are objects; methods are the protocol.
- [Managed DOM Ops](#ref:mdo) — DOM mutations go through the gateway.
- [RFC 0003](#ref:rfc-3) — will carry the implementation specifics (slot type, handle API, conformance test).
