# Doctrine — Managed DOM Ops

> **In SPA consumer code, every DOM mutation flows through a single typed gateway. No raw `document.createElement`, `setAttribute`, `appendChild`, `innerHTML`, or equivalent. Bypass must be explicit, marked, and justified.**

This is the rule that lets every DOM change in an application's view layer be auditable, testable, and crosscut-able from one place.

---

## Scope — applies to SPA consumer code only

This doctrine is **conditional on context**. It applies to:

- **SPA-style apps** with stable, long-lived DOM trees, multiple cooperating components, lifecycle concerns, accessibility surfaces, and cross-cutting visual policies (themes, RTL, focus management). The studio belongs here. Most downstream studios will too.

It does **not** apply to:

- **Imperative / animation-driven / game-loop modules.** A platformer demo that builds dozens of short-lived DOM nodes per game tick gets nothing useful from a typed gateway and pays real ceremony for it. Direct `document.createElement` is fine in those contexts.
- **Pure logic / data / sound modules.** No DOM, no doctrine.
- **Component implementations themselves.** They *are* the gateway's clients; their internal `createElement` calls would be replaced with gateway calls, but that's the framework's own structure, not consumer code.

The line is fuzzy. The heuristic: **if your code structures a UI app with stable components and lifecycle, use the gateway. If your code paints frames or runs an interactive scene, the DOM API is the right tool.**

The other three doctrines ([Pure-Component Views](#ref:pcv), [Methods Over Props](#ref:mop), [Owned References](#ref:or)) remain universal — they're cheap to honour everywhere. Managed DOM Ops is the one with a real cost in non-SPA contexts, so it carries a scope.

---

## In practice (within SPA scope)

### Banned

- `document.createElement(...)` and the entire `Document` factory family
- `element.setAttribute(...)`, `element.removeAttribute(...)` outside the gateway
- `element.appendChild(...)`, `removeChild`, `replaceChild`, `insertBefore` outside the gateway
- `element.innerHTML = "…"`, `element.outerHTML = "…"`, `element.textContent = "…"` outside the gateway
- `element.classList.add/remove/toggle` outside the gateway
- Anything else that mutates the DOM tree or any element's attributes/content

### Permitted

- **All of the above, performed through the gateway.** The gateway is a typed Java interface with a JS singleton bridge. Every Component impl declares it as a dependency and uses it for every mutation.
- **Reading the DOM**: `element.value`, `element.checked`, `element.getBoundingClientRect()`, etc. — for measurement and inspection. Mutation goes through the gateway; reading stays direct.
- **Marked bypasses** for unavoidable cases (third-party libs that need raw elements; performance-critical paths). Bypasses are explicit (annotation or named call), greppable, and audited by conformance.

### Out of scope

- **Imperative / animation-driven / game contexts** — see "Scope" above. Direct DOM API is fine there.
- **Framework code that implements the gateway.** The gateway is the only thing in the system that calls raw DOM mutation APIs; that's its job.
- **The framework bootstrap page** (HTML rendered by `AppHtmlGetAction`). The page exists before any Component runs; it's outside the doctrine.

---

## Why centralised mutation is worth it

- **Auditable.** Every DOM op flows through one place. Logging, timing, replay, and sandboxing all become possible without modifying call sites.
- **Testable.** Mock the gateway; test Components without a real DOM. No more JSDOM-juggling for unit tests.
- **Cross-cutting concerns become possible.** Accessibility checks, theme propagation, RTL handling, security filters can run inside the gateway without permeating Component code.
- **Contract over convention.** A Component that wants to do something unusual to the DOM goes through the gateway like every other Component. The gateway's interface tells you what is and isn't possible — and "anything else" needs an explicit bypass.
- **Bypass is greppable.** Every escape hatch is marked. The doctrine permits no silent escapes — only declared ones.

---

## How to think about it

The DOM is not the substrate; it is a managed runtime. Components don't *touch* the DOM; they *commission* it through the gateway. The gateway is the only party in the system that knows how the DOM works under the hood — every other piece of code talks to it in typed terms.

Same architectural shape as garbage collection vs. manual `malloc`/`free`: a single gateway owns the unsafe operations; everyone else uses the safe interface. The doctrine doesn't ask you to write less code; it asks you to write code that delegates the unsafe step to one place.

---

## See also

- [Pure-Component Views](#ref:pcv) — bans HTML strings; this doctrine bans raw DOM ops.
- [Methods Over Props](#ref:mop) — Components are objects whose methods route through this gateway.
- [Owned References](#ref:or) — the gateway's results (slots, handles) become typed references, never recovered by lookup.
- [RFC 0003](#ref:rfc-3) — will carry the implementation specifics (gateway interface, bypass mechanism, conformance test).
