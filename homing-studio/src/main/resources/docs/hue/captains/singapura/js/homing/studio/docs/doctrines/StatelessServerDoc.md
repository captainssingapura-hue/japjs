# Doctrine — Stateless Server

> **The server is stateless by default. Its role is to provide sufficient information, code, and — when explicit — remote functions for the client to run the app on their own machine. Any client-to-server communication flows through transparent, auditable channels: a visible URL, an inspectable payload, no covert sidebands. Storing client data on the server is the exception that requires justification; the default is to operate without ever needing to retain anything about a particular user, session, or device.**

This is the server-side counterpart to [No Stealth Data](#ref:doc-nsd). That doctrine refuses covert *gathering* on the client; this one refuses covert *retention* on the server. Together they say: client data is the client's; the server's job is delivery, not memory.

The framework's current server already conforms — every endpoint is a stateless GET keyed by request content (`/doc?id=<uuid>`, `/catalogue?id=<class-fqn>`, `/plan?id=<class-fqn>`), no sessions, no cookies setting user IDs, no per-user storage. The doctrine codifies what's already true and makes regressions a violation.

---

## What this doctrine commits to

Every server response is a pure function of the request:

```
response = f(request)
```

with **no other inputs** read from server-side state, and **no other outputs** written to server-side state, beyond the four explicitly-permitted clauses below. The same request from the same client at two different times produces the same response (modulo deployed content changes — but those are server-config changes, not state changes). The same request from two different clients produces the same response.

The client gets, from the server, three things and only these three:

1. **Information** — typed JSON (Doc bodies, Catalogue trees, Plan data, theme manifests, brand). Content addressed by UUID or class-FQN, never by session.
2. **Code** — ES modules, CSS, SVG assets. Static content from the JVM classpath. The framework's `homing-libs` bundles even third-party JS (Tone.js, Marked, Three.js) so the client doesn't reach beyond the first-party origin.
3. **Optionally, remote functions** — explicit POST/GET endpoints the client *deliberately calls* to execute server-only work (e.g. a search index too large to ship). The endpoint's URL, payload, and response are inspectable in the browser's Network tab; the client controls when and whether to call.

Once the client has those, the app runs locally. The server is a content delivery point with a function-call surface, not a session-bearing service. Tabs the user opens are independent; reloads don't lose state because there's no server state to lose.

## What this doctrine bans

- **Server-side sessions of any shape** — `JSESSIONID` cookies, in-memory session maps, Redis-backed session stores, signed-cookie sessions carrying user identity. Even short-lived ones. The server may not "remember the user across requests" by any mechanism.
- **Server-set tracking cookies** — cookies the server sets that are read on later requests to correlate them. Functional cookies the client sets and chooses to send back are a client-side concern (governed by No Stealth Data).
- **Per-user database tables** — a `users`, `sessions`, `preferences`, `history`, or any table keyed by user identity. If user data has to persist, it persists *on the client* (localStorage they wrote, IndexedDB they manage); the server doesn't hold the canonical copy.
- **Server-correlated analytics** — request log lines that carry session IDs, fingerprint hashes, or user-correlation tokens. Aggregate request-shape counts ("how many `/doc?id=…` requests in the last hour") are fine; user-keyed ones aren't.
- **WebSockets / long-poll channels carrying server-state pushes** — if a feature needs real-time-feeling updates, the client polls explicit endpoints on its own schedule. The server doesn't push state; the client pulls when it wants.
- **Server-side feature flags decided per user** — feature gates that read a stored profile, an opaque cookie, or a fingerprint. Per-request flags decided from server config (e.g. release-stage rollouts) are fine; per-user-keyed flags are not.
- **Hidden RPC** — calls the client makes whose URL, payload, or purpose aren't recognisable when the user opens the Network tab. Obfuscated endpoints, opaque binary payloads carrying user data, queries embedded in headers the user wouldn't think to inspect.
- **Server-rendered responses keyed by user identity** — e.g. `/home` returning different HTML based on a session cookie. Either the response is purely a function of explicit query params (and works for any client), or it doesn't exist.
- **Auth-required endpoints whose payloads carry server-derived user state** — even when authentication is justified (rare; not in scope today), the payloads carry only what the client requested. The server doesn't *augment* responses with "things we know about this user" the client didn't ask for.

## What this doctrine permits

- **Stateless GET endpoints keyed by request content** — the entire current API surface (`/doc`, `/doc-refs`, `/catalogue`, `/plan`, `/themes`, `/brand`, `/app`, `/css-content`, etc.). Each is a function of its query string; the server reads no state, writes no state.
- **Server-side content caches keyed by request content** — caching the rendered JSON for `/doc?id=X` because doc X doesn't change between releases is fine. The cache key is request content; eviction is content-driven, not user-driven.
- **Server-side computation with no read/write** — a future `/search?q=…` endpoint that runs a pure search over an in-process index, returning ranked hits. Inputs are query-shaped, output is content-shaped, no per-user state involved.
- **Request access logs that are request-shaped** — already permitted by No Stealth Data. The server logs that *a request happened*; it does not correlate requests across time or across clients.
- **Server-driven feature flags from immutable config** — config read at boot from the classpath / environment / disk decides "feature X enabled for this release." Same answer for every request; the *server's* state, not the *user's*.
- **Explicit, audited remote functions** — a POST endpoint the client deliberately calls (e.g. an analyser too heavy to ship to the client). The URL, payload, response are all client-controlled and Network-tab-inspectable. The function may be expensive; it must not retain anything from the call after the response is returned.
- **JVM internal observability** — JFR, JMX, heap profiles. These observe the *server process*, not users. Out of scope for this doctrine.

## Where this doctrine doesn't apply

- **CDN / reverse-proxy layers** — the JVM behind a CDN delegates caching and TLS to that layer. The doctrine binds what the application server itself stores about clients; it doesn't dictate that the CDN can't have its own ops-level caches keyed by URL (which is request-shaped, conformant).
- **Server-internal scheduled jobs** — a periodic task that re-indexes content at boot, a cache-warming routine, a background SVG optimisation pipeline. These have state — their own state, about their own work, not about clients.
- **Operations data the operator needs to keep the service running** — capacity-planning aggregates, error rates, latency histograms. The doctrine binds *client-state retention*; operations metrics that are aggregated and not keyed by any client are operator concerns.
- **Future genuinely-server-mediated features** — multi-user collaboration, server-stored user content, server-side auth. If such features become necessary, the doctrine doesn't refuse them outright — it requires the retention to be:
  - **Justified** in writing (a Doc or RFC saying *why* the server holds this data),
  - **Disclosed** to the user before storage happens (consent surface),
  - **Auditable** by the user (read what the server has about them),
  - **Erasable** by the user (delete what the server has about them),
  - **Minimal** (only what the feature requires; not "while we're at it, let's store…").

The doctrine binds: **the default posture and what counts as conformant zero-state operation.** Deviations are possible but explicit.

---

## The canonical test

Three questions before adding any server-side line of code that reads or writes anything beyond the request and the static classpath:

1. **Is this state about a *client*?** (User identity, session, preferences, history, fingerprint, behaviour.) If yes, this doctrine binds — you must justify, disclose, audit, erase, minimise.
2. **Could the same response be computed from the request alone?** If yes, that's the design. Read no state, write no state.
3. **Is the channel auditable from the client's Network tab?** Every endpoint the client hits should be inspectable in DevTools: URL, method, headers (no opaque cookies), payload (no obfuscation), response (no extra fields the client didn't ask for). If a future addition would make the channel opaque, the doctrine refuses the addition.

Three yeses to "is this stateless" (or alternatively: three justifications for each exception) → permitted. Anything else → the doctrine refuses.

---

## Why the strictness is worth it

- **Statelessness is the cheapest scaling property.** A stateless server scales horizontally with zero coordination — any instance handles any request. Adding session affinity, sticky load-balancing, session replication, or distributed caches *all* arise from holding client state. The doctrine refuses the precondition.
- **No data to leak is no data to leak.** The single highest-trust property a service can offer is *"we don't have what you're asking us to protect."* Every breach disclosure cites stored user data. A server that stores none has nothing to lose.
- **No retention is no retention obligation.** GDPR right-to-erasure, CCPA right-to-know, sectoral data-protection regimes — every one binds when there's stored client data. A stateless server has nothing to erase and nothing to disclose, because there's nothing to begin with.
- **Local-first execution gives users sovereignty.** Once the client has the info + code, the user can save the page (MHTML works, per the 0.0.100 work), share it, archive it, run it offline. The app isn't gated by a server it doesn't control.
- **Reasoning shrinks.** A stateless server has *one* failure mode per endpoint (the function diverged) instead of *N* (state corruption, cache desync, session expiry mid-request, replication lag, partition split-brain). The framework's mental model stays small.
- **Pairs structurally with [No Stealth Data](#ref:doc-nsd).** That doctrine made the client side privacy-respecting; this one makes the server side *unable to violate* even by accident, because there's nothing to violate. Compounds, doesn't add.

## The "ideally" admission

The doctrine says "stateless as much as possible," not "stateless absolutely." This is honest:

- **Authentication is a real need for some deployments.** A multi-tenant studio with login can't be fully stateless without offloading identity to a third party (which is itself a doctrine question — likely a worse one). The doctrine accommodates: when auth is required, use a stateless token format (signed JWT or equivalent) — the server validates each request against the token's claims without storing session state. Storage is on the client (the token); the server reads, never writes.
- **Real-time collaboration is a real need for some applications.** Multi-user editing of a shared document genuinely requires some server-mediated state. The doctrine doesn't refuse it; it refuses *covert* server state. If a feature needs state, the design must surface it: documented in an RFC, disclosed in the UI, auditable through a "what does the server know about me" endpoint.

The "ideally" is the doctrine acknowledging real-world constraints. The *default* is statelessness; the *exception path* requires explicit justification through the five clauses above (justified / disclosed / auditable / erasable / minimal).

---

## How to think about it

When designing a new endpoint, draw the function signature:

```
response = endpoint(request)
```

If you can't write the signature without adding a third input — `(request, sessionState)`, `(request, userPreferences)`, `(request, history)` — the doctrine asks: *can the third input come from the request itself?* (A query param, a body field, a token the client supplied.) If yes, rewrite. If genuinely no, the endpoint requires the doctrine's five-clause justification.

When considering server-side storage, draw the lifecycle:

```
write → read → delete
```

If "delete" is "when the user deletes their account" or "after 90 days" or "never," the storage is client-state and this doctrine binds — the user must be able to inspect, request deletion of, and stop the writing of this data. If "delete" is "after the response is sent" or "after eviction by content-key LRU," the storage is server-internal and the doctrine doesn't apply.

When considering a real-time / push feature, draw the data flow:

```
client poll → server respond
```

vs

```
server push → client receives
```

The doctrine prefers the first. Polling the client controls is auditable in Network; server push is by nature an open channel carrying server-decided data the client didn't ask for. Polling at one-second intervals is *not actually slower* than push for most UI patterns; it is enormously simpler to reason about.

---

## See also

- [No Stealth Data](#ref:doc-nsd) — the client-side counterpart. Client refuses covert *gathering*; server refuses covert *retention*. Together: client data is the client's.
- [Quality Without Surveillance](#ref:doc-qws) — the engineering-discipline counterpart. The server is stateless because the application doesn't need user state to be good; the engineering produces quality without extracting it from users.
- [Functional Objects](#ref:doc-fo) — refused covert *behaviour* surfaces (statics with bodies). This doctrine refuses covert *state* surfaces. Same principle, third slice.
- [Pure-Component Views](#ref:doc-pcv) — views are pure functions of their inputs. The server is the same idea one level up: responses are pure functions of their requests.
- [Owned References](#ref:doc-or) — the framework's broader stance that handles + data belong to clearly-named owners. The server, under this doctrine, owns no client data — so the question of stewardship doesn't arise.
