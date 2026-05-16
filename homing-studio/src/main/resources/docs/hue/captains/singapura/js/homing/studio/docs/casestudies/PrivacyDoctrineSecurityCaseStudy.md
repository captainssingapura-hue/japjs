# Case Study — The Privacy Doctrines Have Nothing To Lose

> **[No Stealth Data](#ref:doc-nsd) and [Stateless Server](#ref:doc-ss) have a striking and asymmetric security profile: they eliminate entire classes of threats by refusing the precondition (data to gather, state to retain), at the cost of two specific capabilities the framework chooses not to need (per-user forensics, behavioural anomaly detection). The system's security property is verifiable from the user's own browser — the most under-rated form of trust there is.**

Filed alongside the [Cross-Studio References](#ref:csref) study — both are *"what we got by saying no to something."* This one names what those refusals secured.

---

## The phenomenon

Zero of the framework's code mentions a session ID, a CSRF token, a fingerprint hash, a user identifier, an analytics SDK, a session store, or a user table. And yet entire classes of breach — *the* classes that fill annual breach-disclosure reports — are structurally absent.

The two privacy doctrines achieve this without adding security controls. They work by **refusing the precondition**: there is no session to hijack, no PII to leak, no password to compromise, because none of those exist in the first place.

---

## What gets eliminated structurally

These threats cannot occur in the framework as currently doctrinised, because their preconditions don't exist:

| Threat class | Why it's eliminated |
|---|---|
| **Session hijacking** | No sessions exist to hijack |
| **Session fixation** | No sessions exist to fix |
| **CSRF** | No state-changing endpoints authenticate by cookie |
| **Cookie theft → impersonation** | No identity cookies are issued |
| **Password database breach** | No passwords stored |
| **PII database breach** | No PII stored |
| **User-table breach** | No user database exists |
| **GDPR right-to-erasure breach** | Nothing to erase |
| **GDPR right-to-know miscompliance** | Nothing to know about |
| **XSS impact amplification** | No tokens or session data to exfiltrate — XSS shrinks to *"show fake content"* instead of *"impersonate the user"* |
| **Third-party SDK supply-chain compromise** | No third-party JS shipped client-side; analytics + tracker SDK class refused categorically |
| **Cross-site re-identification** | No identifiers exist to correlate |
| **Server-side cache poisoning by user-keyed entries** | Caches are content-keyed only; "user X's view of /home" doesn't exist as a cache entry |

This is the single highest-value security property a service can offer: *"we don't have what you're asking us to protect."* Every breach disclosure cites stored user data; a server that stores none has nothing to disclose. The doctrines achieve this **not by adding controls but by refusing the precondition**.

## What gets reduced

| Threat class | Effect |
|---|---|
| **XSS proper** | Still possible (any rendered content carries XSS risk). But impact reduced to UI tampering on the current page, not session theft or persistent compromise. The conformance suite's `HrefConformanceTest` + the no-`innerHTML` discipline already reduces the surface. |
| **Content tampering in transit** | TLS still mandatory. The doctrines bind the *application*, not the *network*. |
| **Response observation as side channel** | Same request → same response makes responses predictable. Generally not exploitable because no per-user data is in any response. |

## What gets left on the table

This is where the trade-off is honest:

| Threat class | Why these doctrines don't help |
|---|---|
| **DDoS / resource exhaustion** | Stateless GETs are *easier* to flood than session-gated endpoints. Can't rate-limit by user (no users to rate-limit). Have to rely on IP-level rate limiting (coarse, CDN-shieldable). |
| **Adversarial web scraping** | Undetectable as adversarial because adversarial behaviour looks identical to high-volume legitimate use. The doctrines refuse the per-user baseline that would let you detect *"this user is scraping."* |
| **Anomaly detection** | Same root cause — no per-user baseline. *"User X downloaded 10,000 docs in an hour"* is structurally indistinguishable from *"10,000 separate users downloaded 1 doc each."* |
| **Replay attacks** (against future remote functions) | If an explicit remote function is ever added under the doctrines' exception path, *"no server state"* makes naive nonce-tracking impossible. Mitigations exist (signed time-bounded tokens), but require care. |
| **Authorization for private content** | Default posture is *"everything is public."* Any private-content feature requires the doctrines' exception clauses. Until then, there is no authz layer to attack — but also none to use. |
| **Forensic audit trail** | The server cannot prove a user did X (no per-user logs). For most use-cases this is a feature (privacy); for compliance-bound deployments it's a gap. |
| **Vendor security tooling** | The doctrines refuse third-party SDKs categorically — including security SDKs that ship a JS shim (bot detection, WAF integrations). Trade-off: vendor security tooling is itself a tracking surface. The framework picks *"no tracking, no vendor security"* over *"more security with tracking cost."* |

These are real losses, not paper-over-able. Honest naming: the framework optimises for one specific security property (breach-class elimination) and accepts the loss of two specific capabilities (per-user forensics, behavioural anomaly detection).

---

## The meta-property: *verifiable* trust, not *promised* trust

The most interesting security implication is one the doctrines achieve almost as a side effect:

**Their privacy claims are auditable from the user's own browser.**

The user does not have to trust the operator's promise that *"we don't track you"* — they can open DevTools → Network and verify it. Storage tab, same thing. Cookies, same thing. Anything covert would show up; nothing is covert.

This is a strictly stronger property than the industry standard. Most services say *"we respect your privacy"* in a policy document the user is expected to trust. The framework says *"open DevTools, you'll see we don't gather anything you didn't authorise."* The audit shifts from "trust the operator's word" to "verify with browser-native tools."

| Property | What "trust" means |
|---|---|
| Promised trust | The operator says they don't gather data; user trusts the word |
| Audited trust | The operator commissions an external audit; user trusts the auditor |
| **Verifiable trust** | The user can check from their own browser without trusting anyone |

Verifiable trust > audited trust > promised trust. The doctrines give the framework the strongest of the three by construction — not because of *"we care about your privacy"* messaging but because the audit surface is the same as the user's own browser inspector.

This also pairs nicely with the framework's First User being a Claude-class agent: agents can verify the property by inspecting traffic, not by reading policy prose.

---

## Where these doctrines fit Homing's needs

The framework's actual threat model:

- Content is documentation, design notes, and code
- Content is **public by default**
- Users are the author, agents reading the codebase, and occasional human readers
- High-value asset is *the codebase being inspectable and trustworthy*, not user data
- Deployments are by individuals / small teams, not regulated enterprises

For this profile, eliminating session-related threat classes outweighs losing user-level forensics or anomaly detection. The doctrines are well-matched, and *"verifiable trust"* is the property that does most of the work.

## Real-world requirements and conscious compromises

The doctrines aren't laws of nature; they're commitments the framework makes for the use-cases it targets. A practitioner facing a different requirement isn't running into "wrong" doctrines — they're running into a requirement that asks the framework to give something up.

The honest move is to **name what would have to give**, decide whether the compromise is acceptable, and (when the cost is fatal to the framework's values) explicitly say *"not supported."* The framework's principles include the freedom to refuse a use-case that can't be met without breaking what matters.

| Real-world requirement | What it asks the framework to compromise | Verdict under Homing's values |
|---|---|---|
| **Multi-tenant SaaS with strict tenant isolation** | *Nothing*, when done as **container-per-tenant + external IdP + JWT** — the per-tenant app stays stateless, tenant identity is deploy-time config, isolation is enforced by the orchestrator. *Shared-application multi-tenant* has its own deliberate path via end-to-end encryption with the server reduced to opaque storage — see the [decomposed-isolation case study](#ref:tenant-iso). | **Supported** via the container+JWT pattern; also supported as shared-app with cryptographic isolation. The RLS-discipline shared-app form is *not supported* without explicit deviation. |
| **Authentication / accounts** | Push user identity to an external IdP; use short-lived signed tokens; the app server validates cryptographically and reads no session table. Token revocation requires either short TTLs + refresh, or an IdP-side denylist (state at the IdP, not in the app). | **Supported** via the exception clauses — the state lives outside the application boundary, is disclosed (the user logged in), and is auditable / revocable at the IdP. |
| **Cross-session memory in a per-user app** (AI assistant, personal notes) | Two paths. Client-resident storage (the user owns their history in localStorage/IndexedDB) requires *no* compromise. Server-resident storage requires the full exception path: justified / disclosed / auditable / erasable / minimal. | **Both supported.** Client-resident is preferred and doctrine-clean. Server-resident is the conscious-compromise form, only if the five clauses are met. |
| **Real-time collaboration** | Peer-to-peer (CRDT over WebRTC, e.g. Yjs / Automerge) requires *no* compromise — the framework's server never sees the shared state. Server-mediated relay requires retaining shared *content* state, which is a smaller doctrinal bend than retaining *user* state, but still a conscious deviation. | **Peer-to-peer supported.** Server-mediated requires explicit design that surfaces the retained content and the channel's audit story. |
| **Regulatory audit trails** (banking, medical, HIPAA, SOX) | Per-user request logs retained for years, indexed by user identity, queryable by regulator. This breaks "Stateless Server retains nothing about clients" structurally, and the *erasable* exception clause directly conflicts with the regulator's *retention* mandate (regulator says keep; GDPR says let user delete; the operator is stuck in the middle). | **Not supported by these doctrines.** The conflict is fundamental — verifiable user agency over their data and regulatory retention pull opposite directions. An operator that needs this should use a framework whose values include compliance retention. Homing does not. |
| **Behavioural inference** (recommendations, anti-fraud, anomaly detection) | Persistent per-user behavioural baselines; opaque models whose inputs the user can't audit; algorithmic decisions the user can't reconstruct. Even if the inputs are technically inspectable (they're not, typically), the model's interpretation of them is the black box the framework refuses to embed. | **Not supported.** The verifiable-trust property is a core value; behavioural inference is structurally non-verifiable. No exception clause path makes a black-box model auditable. The framework declines the use-case. |
| **Social-network style feeds with engagement-optimised ranking** | Stacks the previous two: per-user behavioural inference + engagement metrics retention + server-pushed updates the user can't audit. Every layer is its own compromise; together they require essentially abandoning the doctrines. | **Not supported.** A *chronological* feed of explicitly-followed accounts (no ranking, no engagement metrics, no inference) fits, but commercial social networks aren't built that shape — they require the very compromises the doctrines refuse. |
| **Server-side analytics / behavioural telemetry** | Per-user event streams. Even when "anonymised," correlation defeats anonymisation. The opt-in form (a user clicked "send me usage analytics") fits No Stealth Data, but the default-on form does not. | **Opt-in supported.** Default-on telemetry is the canonical thing No Stealth Data refuses. If a use-case requires default-on, this framework is the wrong choice. |

The verdicts above aren't fixed in stone — they're how the doctrines currently weigh the trade. An operator facing a "not supported" verdict has three paths:

1. **Use a different framework** whose values match the requirement (entirely reasonable — frameworks are opinionated, not universal).
2. **Operate outside the doctrines** for the conflicting feature, accepting that the verifiable-trust property no longer holds for that feature and disclosing that to users.
3. **Reshape the product** so the requirement isn't there (the social-network case: build a chronological-feed network, not an algorithmic one).

The framework's commitment isn't that every requirement can be met — it's that the answer is **explicit, conscious, and honest**, rather than the requirement quietly eroding the doctrine via accreted shortcuts.

---

## Subtleties — three things the doctrines don't claim

1. **"Stateless server" ≠ "private from the network."** The OS, hosting provider, ISP, and any in-path proxy can still observe the connection's metadata. The doctrines bind the application; transit privacy is TLS's job and an entirely separate concern.

2. **"Auditable in DevTools" ≠ "audited in DevTools."** Users have the *capability* to audit; most won't open DevTools. The doctrines create the property; user behaviour determines whether it's exercised. The property still matters: it's the difference between *"you can't tell"* and *"you could tell if you looked."*

3. **"No stored client data" ≠ "no observation of clients."** Server access logs (request URL + timestamp) are necessary for the server to function and are permitted under both doctrines. They aren't user-keyed, but they could in principle be correlated with other data (e.g. IP-level inference). The doctrines bind *what the application stores about clients*, not what observers outside the doctrines' scope can infer from traffic patterns.

The exception clauses in [Stateless Server](#ref:doc-ss) (justified/disclosed/auditable/erasable/minimal) take care of (1) and (3) through transparent disclosure when state has to be added. (2) is intrinsic to giving users agency rather than dictating their behaviour.

---

## Net synthesis

These two doctrines optimise specifically for **breach-class elimination via precondition refusal**. They trade away **per-user forensic capability** and **behavioural anomaly detection**. The meta-property they achieve — *verifiable trust* — is stronger than what most frameworks offer.

For Homing's threat model, the trade-off is heavily favourable. For a different threat model, the exception clauses are the principled deviation path.

The clearest one-line statement:

> **The surface a hostile party could acquire by compromising the server is empty by construction.**

That's an asset, not just an abstention.

---

## How to think about it

When considering any addition that would touch network, storage, or `navigator.*`, ask: *would adding this break the verifiable-trust property?* Specifically:

1. **Would a user inspecting DevTools see something unexpected?** Network requests they didn't initiate, storage entries they didn't authorise, cookies they didn't recognise. If yes, the addition breaks verifiable trust — refuse or redesign.
2. **Would a security incident now create disclosure obligations that don't exist today?** If yes, the addition is moving the framework into a different security posture, and the cost-benefit must be reassessed under that new posture (GDPR scope, breach notification timelines, retention regulations).
3. **Does the feature genuinely require server state?** Many features that seem to require state can be redesigned to be client-state + signed-token. The exception path exists; the bar to take it is real.

If you can't keep the verifiable-trust property, the doctrines force you to surface the deviation — which is also a feature, not a bug.

---

## Related patterns from other case studies

The cross-studio refs study notes that **cheap features fall out of saying no enough times early**. This study extends the same principle to security: *cheap security properties fall out of refusing the wrong shapes early*.

Both studies share the same shape of argument:

| Study | What was refused | What emerged |
|---|---|---|
| [Cross-Studio Refs](#ref:csref) | Location-encoded URLs, symbolic refs, per-studio sub-registries | Zero-cost cross-studio composition |
| **This study** | Client data gathering, server-side state retention | Zero-cost breach-class elimination |

The general principle: *what you refuse early, you don't have to defend later*.

---

## See also

- [No Stealth Data](#ref:doc-nsd) — the client-side doctrine analysed.
- [Stateless Server](#ref:doc-ss) — the server-side doctrine analysed.
- [Functional Objects](#ref:doc-fo) — sibling doctrine that refused covert *behaviour* surfaces. The privacy doctrines refuse covert *data* and *state* surfaces. Same principle, three slices.
- [Cross-Studio References Cost Nothing](#ref:csref) — companion case study examining a different free-property from a different set of refusals.
