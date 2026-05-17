# Doctrine — No Stealth Data

> **No client data gathers in stealth. The framework, and every studio following its conventions, refuses any client-side data collection the user cannot see and audit using ordinary browser tools — no telemetry beacons, no fingerprinting, no analytics SDKs, no silent error reporters, no opaque cookies, no third-party tracker embeds. Disclosed, opt-in, user-controllable collection is welcome; covert collection is banned absolutely. The keyword is *stealth* — visibility is the test.**

This is the doctrine the framework commits to. It binds going forward — new framework code, new downstream-studio code following framework conventions, and the migration of any covert data flow we happen to touch during regular work. Existing source already conforms (zero analytics, zero telemetry, zero third-party trackers); the doctrine codifies what's already true and makes regressions a violation.

---

## What this doctrine commits to

Every byte of data the framework collects about the user, their session, their device, or their behaviour is **either**:

- **Visible by inspection** — sits in a place the user can open and read using browser-native tools (DevTools → Application → Storage / Cookies / Local Storage; Network tab; about:config). The framework writes nothing the user can't audit without installing third-party software.
- **Disclosed and opt-in** — the user said yes before the byte exists, and can say no to make the byte stop existing. No "implied consent" via continued use of the site. No dark-pattern toggles that re-enable themselves after a release.

The default posture is therefore *no collection at all*. If a feature wants to collect anything, the burden is on the feature to justify it under one of the two clauses above.

## What this doctrine bans

- **Analytics SDKs** — Google Analytics, Segment, Mixpanel, Plausible, Fathom, Matomo, any analytics-shaped third-party script. Even the "privacy-friendly" ones. They are by definition tracking the user, and the user cannot inspect the third-party server's storage.
- **Telemetry beacons** — silent `fetch()` / `navigator.sendBeacon()` calls to first- or third-party endpoints carrying user-shaped data (`?session=…`, `?visitor=…`, `?path=/visited`, error stacks with file paths, performance timings tied to a session ID).
- **Fingerprinting** — Canvas / WebGL fingerprints, font-list probes, screen-dimension probes, `navigator.*` probes whose purpose is identity inference rather than feature detection.
- **Silent error reporting** — Sentry, Bugsnag, Rollbar, LogRocket, FullStory, any uncaught-exception-phones-home library. Errors stay in the browser console; the user (or a paste-the-stack-trace channel) is the report mechanism.
- **Session replay** — recording mouse movements, scroll positions, form inputs, anything that reconstructs the user's session for off-device replay.
- **Opaque cookies / storage** — cookies whose names, values, or purposes are not documented somewhere the user can read; storage entries that aren't either app-state-the-user-set (e.g. theme choice) or transient cache the user can clear.
- **Third-party embeds that phone home** — YouTube `<iframe>`s, Twitter widgets, Disqus comments, Stripe checkout overlays. Each is a tracker by transitive load even if the embedded service "doesn't track." First-party reimplementations are preferred; if a third-party embed is unavoidable, it's gated behind an explicit opt-in click.
- **A/B testing infrastructure that observes the user** — feature flags decided client-side based on user attributes the server hasn't disclosed. (Server-side feature flags decided per *request*, with no user-identifying state, are fine — see "What this doctrine permits.")
- **CDN-served scripts whose load tracks the user** — the [Cdn-Free conformance test](#ref:rfc-1) already prevents this structurally; this doctrine names the reason.

## What this doctrine permits

- **Server access logs of the request** — the user already knows they made the request; logging the URL + timestamp on the server is request-shaped, not user-shaped, and is necessary for the server to function. Logging the request body, headers carrying client-identifying data, or correlating across requests via a user ID is not permitted under this clause.
- **`localStorage` entries the user set** — the framework writes `theme=jazz-drum-kit` when the user clicks a theme. The user can open DevTools → Application → Local Storage and see exactly that. Permitted.
- **`localStorage` entries the framework needs for the page to function** — e.g. a `last-visited-doc-uuid` for "back" navigation — *as long as the value is documented and inspectable*. Anything the user can't recognise on inspection ("`x7k2_ab=…opaque base64…`") is not permitted under this clause.
- **Errors in `console.error`** — the browser's own console is a user-inspectable surface. The framework logs errors there with full stack traces; the user (or a developer) can copy the stack and report it through a human channel. The error never leaves the browser without explicit user action.
- **Opt-in feedback** — a *"Report a bug"* link that opens a `mailto:` or a clearly-labelled form is fine. The user pasted the stack into a form they opened deliberately; the act is the consent.
- **Per-request feature flags from server config** — the server decides "render with feature X enabled" based on its own config (e.g. release-stage gating), not based on observed user attributes. The decision is in the response, not a client probe.
- **CSS / SVG / font loads from first-party origin** — these are content delivery, not data gathering, and the request is *request-shaped* (same logging concerns as any other GET).

## Where this doctrine doesn't apply

- **Browser-native diagnostics the user controls** — DevTools, browser performance recordings, browser-builtin telemetry the user agreed to with the browser vendor. The browser is its own privacy boundary; what Chrome / Firefox / Safari does with the user's data is between them and the user.
- **The user's own choice to embed Homing content elsewhere** — if a downstream user saves an MHTML and emails it, or screenshots the page, that's outside the framework's control surface. The doctrine binds the framework's *authoring*, not the user's *agency*.
- **JVM-side observability of the server** — JFR, JMX, heap dumps, slow-query logs. These observe the server process, not the user. Permitted as standard operations.
- **Server-side analytics on aggregated request shape** — counting "how many requests for `/doc?id=…` in the last hour" with no user correlation is permitted as server operations. Once the count starts being keyed by user, session, IP, fingerprint, or correlation token, this doctrine binds.

The doctrine binds: **client-shaped data gathering**. Server-internal observability and user-disclosed mechanisms are out of scope.

---

## The canonical test

When considering adding any data-gathering line of code, ask:

1. **Can the user see this data exists?** Open DevTools, inspect storage, watch the Network tab — would the user notice? If no, it's stealth.
2. **Did the user agree this data should exist?** Either by acting (clicking the theme, saving a preference) or by an explicit opt-in. If no — and the data isn't request-shaped server operation — it's stealth.
3. **Can the user make it stop?** Clear storage, revoke consent, log out. If the data persists after the user's reasonable action to stop it, it's stealth.

Three yeses → permitted. One or more no → the doctrine refuses the addition.

---

## Why the strictness is worth it

- **User trust is non-recoverable.** A single discovered stealth-tracking incident, once public, costs more credibility than years of careful authoring earns. The doctrine refuses to gamble that any specific instance of stealth gathering won't be the one discovered.
- **The framework is read by agents.** Homing's documentation is consumed by Claude-class agents reasoning over the codebase. A doctrine that says "no stealth gathering" is a positive signal: any code an agent might generate or refactor stays inside the boundary, because the boundary is part of the read-time context.
- **Aggregation hazards compound.** A "harmless" cookie + a "harmless" canvas probe + a "harmless" font list + a "harmless" timing measurement is, in combination, a fingerprint. The doctrine refuses each piece individually so the combination can't accumulate.
- **The doctrine is self-enforcing at the architectural layer.** [Cdn-Free conformance](#ref:rfc-1) already refuses external scripts. [Pure-Component Views](#ref:doc-pcv) already refuses side channels in render. [Owned References](#ref:doc-or) already refuses unowned DOM mutation. This doctrine names the *user-data* slice of what those other doctrines structurally prevent.
- **What looks like a cost is a non-cost.** Most teams never make a deliberate "let's add stealth tracking" decision; tracking accretes accidentally — a vendor SDK shipped to enable a feature happens to send beacons; an A/B framework gets adopted and instruments everything by default; a "lightweight" performance library phones home. The doctrine costs *one rejected dependency every few months*, not "build your own analytics."
- **Pairs with [Functional Objects](#ref:doc-fo).** That doctrine refused covert behaviour-bearing surfaces (public statics). This doctrine refuses covert data-gathering surfaces. Same principle, different slice.

---

## How to think about it

Two checks before adding any client-side code that touches the network, storage, or `navigator.*`:

1. *Is anything about the user, their session, or their device being recorded somewhere they cannot inspect?* → not permitted under this doctrine.
2. *If they could inspect it, would they recognise it as something they agreed to?* → if no, opt-in is required before the byte exists.

Then write the code such that the answer to both is "yes."

For dependency additions, the same questions apply at the dependency level. A library whose own README mentions "telemetry," "anonymous usage data," "anonymous statistics," "to help us improve," or "opt-out" is a doctrine violation — opt-out is the dark pattern of stealth gathering. Reject the dependency or use a fork with the telemetry path stripped.

---

## See also

- [Stateless Server](#ref:doc-ss) — the server-side counterpart. Client refuses covert *gathering*; server refuses covert *retention*. Together: client data is the client's.
- [Quality Without Surveillance](#ref:doc-qws) — sharpens this doctrine in one direction. This doctrine permits disclosed opt-in collection; that one asks whether disclosed *"help us improve"* collection should even be reached for. The answer there is no — improve the engineering instead.
- [Functional Objects](#ref:doc-fo) — refused covert *behaviour* surfaces; this doctrine refuses covert *data* surfaces. Same shape of "say no early to avoid the cost forever."
- [Pure-Component Views](#ref:doc-pcv) — the rendering function is its inputs; no side channels. This doctrine extends the no-side-channels principle to user-data.
- [Owned References](#ref:doc-or) — every DOM element has an owner; nothing operates on the DOM in stealth. This doctrine extends the visibility principle to data.
- [RFC 0001 — App Registry & Typed Nav](#ref:rfc-1) — the [Cdn-Free conformance test](#ref:rfc-1) family lives here; refuses external scripts structurally, which removes the most common stealth-tracking surface before it can be added.
