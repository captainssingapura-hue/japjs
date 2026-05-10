# Doctrine — Methods Over Props

> **Components are objects with identity, state, and methods. They are not functions of props. Composition is by instantiation and method calls. The framework is OO with pragmatic functional — not React's pure functional with handicapped state.**

This is the doctrine that defines what a Component **is** in this project.

---

## In practice

### Banned

- **"Props bag" as the primary interface.** A flat record of attributes pushed at every render is not how Components are configured beyond initial setup.
- **Functional-component style.** A Component implemented as a function `(props) → DOM`, with no instance, no state, no methods.
- **Re-render-on-prop-change as the default mutation model.** Re-rendering an entire subtree to change one piece of text is a workaround for missing methods, not a mechanism.
- **Ephemeral identity.** Constructing a "new" Component to convey new state. A Component instance has a lifetime; identity is preserved across changes.

### Permitted

- **Constructor arguments** for initial setup — a label, a target slot, an initial value. These are *not* React-style "props"; they are constructor args, frozen after construction.
- **Methods** for ongoing interaction: `searchInput.setValue("hi")`, `card.setBadge("RFC")`, `progressBar.advance(10)`.
- **Callbacks registered via methods**: `searchInput.onChange((q) => ...)`. Not as constructor args; not as a re-render trigger.
- **Pure functions** for transforms, formatters, predicates, derivations. Pure functions are the right tool for stateless data transforms; the doctrine is against *Components* being pure functions, not against pure functions in general.

### Out of scope

- A Component's **internal implementation** may use functional helpers, immutable values, etc. The doctrine governs the **interface** between Components and their consumers, not the implementation idiom inside.

---

## Why we reject React's model

React established a culture of pure-functional components driven by props. That model has costs this project refuses to pay:

- **Encapsulation lost.** A function-of-props has no private state, no identity, no methods. Hooks were invented to bolt these back on, awkwardly, with rules that fight composition (don't call hooks conditionally, don't call them outside components, …).
- **Re-render churn.** "Change one prop, re-render the subtree." The framework is then forced to memoise, diff, key, and reconcile — all to compensate for the data flow being wrong in the first place.
- **Identity confusion.** A Component instance and the next render of "the same component" aren't the same thing. Anything tied to identity (focus, animation state, scroll position, third-party wrappers) needs explicit machinery (refs, keys, effects) to survive.
- **Anti-OO ideology.** "Class components are bad" is a position, not a finding. OO has tools — encapsulation, identity, methods, lifecycle — that are the right shape for stateful UI elements. Discarding them as a tribal stance is what we mean by **handicapped**.

The framework's stance is the inverse: **OO is the natural fit for UI elements; pure functional is the right tool for data transforms; the two coexist pragmatically.** A Component is an object. A formatter is a function. Don't conflate them.

---

## What "pragmatic functional" looks like here

- Data transforms (`filterDocs(query, items)`, `formatDate(t)`, `slugify(s)`) are pure functions.
- Predicates (`isPlanReady(plan)`, `isInternalLink(url)`) are pure functions.
- Reducers, projections, mappers are pure functions.
- The functions are *used by* Components; they are not *Components themselves*.

This is unremarkable when stated plainly. We name it because the React culture has trained a generation to hide ordinary OO behind functional veneer. We don't.

---

## How to think about it

When you reach for a Component, ask: "Is this a thing or a calculation?"

- A *thing* — has state, identity, an element on the page, methods you call on it over time → Component.
- A *calculation* — takes inputs, returns a value, no state, no identity → pure function.

If the answer is "thing", you're declaring an OO Component with constructor + methods. If "calculation", you're writing a function. Don't blur the line.

---

## See also

- [Pure-Component Views](#ref:pcv) — the foundational rule that views are Component compositions.
- [Managed DOM Ops](#ref:mdo) — how Components mutate the DOM (through a gateway).
- [Owned References](#ref:or) — how Components are referenced (handles, not lookups).
- [RFC 0003](#ref:rfc-3) — the design draft that will incorporate these doctrines.
