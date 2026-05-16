# Case Study — Tenant Isolation, Decomposed

> **"Tenant isolation" is not one property — it decomposes into seven. Container-per-tenant deployment gives all seven for free, at the cost of container overhead. Shared-application deployment can achieve three of them (data, identity, cryptographic) to a stronger standard than containers — via client-side encryption with the server reduced to opaque storage — but structurally cannot achieve the other four. The honest engineering question isn't "can we share an app server across tenants?" but "which isolation flavours does our use case need, and which compromise are we consciously taking?"**

Filed after the [companion privacy-doctrines case study](#ref:priv-sec) glibly listed *"multi-tenant SaaS"* as a case where the doctrines "didn't fit," then the analysis caught itself: container-per-tenant fits cleanly, and even *shared-application* multi-tenant has a stronger pattern available than the industry default. This study decomposes the question properly.

---

## The question

When you say *"true tenant isolation,"* what specifically are you asking for? The answer determines whether shared-application multi-tenancy is engineering negligence or a deliberate, well-supported architectural choice.

Most discussions conflate seven distinct properties. Separating them is the first move.

---

## The seven flavours of isolation

| Flavour | What it means | Container-per-tenant | Shared application (best effort) |
|---|---|---|---|
| **Data isolation** | Tenant A's bytes cannot be read by tenant B's request handler. | Free (separate filesystems / DBs) | **Achievable cryptographically** — see pattern below |
| **Identity isolation** | Tenant A cannot impersonate tenant B at the auth surface; tokens for one don't validate for the other. | Free (separate signing keys per container) | Achievable via per-tenant-keyed JWT + strict claim discipline |
| **Cryptographic isolation** | Tenant A's encryption keys are never co-mingled with tenant B's; a compromise of one tenant's key doesn't compromise others. | Achievable with per-container HSM integration | **Achievable** with per-tenant key derivation; arguably stronger if keys are user-derived |
| **Compute isolation** | Tenant A's request load (CPU, memory, I/O) cannot impact tenant B's latency or availability. | Strong (cgroups, kubernetes resource limits, OS scheduling) | **Not achievable** — same JVM heap, same CPU cores, same disk I/O. Best you get is *accountancy* (per-tenant rate limits, request quotas), which is policy not enforcement. |
| **Failure isolation** | A crash, panic, OOM, or deadlock in tenant A's request path cannot affect tenant B. | Free (separate OS processes — A's segfault doesn't touch B) | **Not achievable** — shared JVM means shared OOM kill, shared GC pause, shared deadlock. Best-effort exception handling helps with logical crashes but not resource exhaustion. |
| **Side-channel isolation** | Tenant A cannot infer tenant B's data via timing, memory pressure, cache state, or other indirect observation. | Partial — process boundary helps but Spectre-class attacks cross containers in some configs | **Not achievable in shared-process model** — same address space, same L1/L2/L3 caches. Mitigations (constant-time crypto, response padding) are partial. |
| **Operational isolation** | Operator actions (debug, profiling, upgrade, rollback) can target one tenant without affecting others. | Free (per-container deploy unit) | **Not achievable** — shared deployment means a rollback affects everyone, a debug session sees all tenants' traffic. |

**Four of the seven are structurally impossible in a shared-app deployment.** This is the honest baseline. Anyone claiming "we have shared-application multi-tenancy with full isolation" is either redefining "isolation" or selling you something.

The interesting question becomes: *given that compute, failure, side-channel, and operational isolation are off the table for shared-app, can we make the remaining three (data, identity, cryptographic) strong enough that the trade is acceptable?*

The answer is yes, with one specific pattern.

---

## The pattern: server as cryptographic storage

The strongest form of shared-app data isolation **reduces the server's role to opaque ciphertext storage**. All meaningful tenant boundary enforcement happens through cryptography held client-side, not through application logic.

### Architecture

1. **Client-derived master keys.** Each tenant's master encryption key is derived from their authentication credentials (password via a KDF like Argon2, or a hardware token). The key exists only in the client's memory during an active session and is *never sent to the server in cleartext*.

2. **All tenant data encrypted client-side before upload.** When a tenant uploads a doc, image, or any payload, the client encrypts it under the tenant's key using authenticated encryption (AES-GCM, XChaCha20-Poly1305). The server receives ciphertext indexed by an opaque content ID.

3. **The server cannot decrypt — even if it tries.** The server holds only ciphertext at rest and ciphertext in transit. A bug, compromise, malicious insider, or subpoena reveals only encrypted blobs the attacker cannot read.

4. **Identity via cryptographic proof, not lookup.** Authentication is *"prove you hold the key for tenant T"* via signature challenge, not *"look up tenant T's row in the users table."* The server validates signatures; the user table (if it exists at all) can be public hashes or per-user encrypted blobs.

5. **Metadata minimisation.** Whatever cleartext the server needs (timestamps, encrypted-blob sizes, content type) is held to the absolute minimum. Sometimes this means accepting that storage size leaks information; sometimes it means padding payloads to uniform sizes to defeat shape-based inference.

6. **Cross-tenant features via explicit key exchange.** If tenant A wants to share a doc with tenant B, the *client* performs the key exchange: encrypt the doc's key with B's public key, store the wrapped key on the server. The server stores another wrapped key but still cannot read the doc itself.

7. **Audit logs encrypted to the user.** Whatever logging the server performs about a user's actions is itself encrypted under the user's key. The user can decrypt and audit their own log; nobody else can — including the operator.

### What this gives up

The cost is real:

| Capability lost | Trade-off |
|---|---|
| **Server-side search** | Only over ciphertext. Encrypted search indexes exist (CryptDB, oblivious indexes) but are limited and expensive. Most implementations push search client-side after fetch. |
| **Server-side aggregation** | Impossible on encrypted data. *"How many docs do my users have?"* requires either client-reported counts (trust) or homomorphic encryption (expensive). |
| **Server-side rendering of user content** | Impossible. Client must render. (Aligned with Homing's existing client-rendered architecture.) |
| **Password recovery** | Lost password = lost data, unless designed for recovery codes, hardware tokens, or social recovery — every option carries its own UX + security cost. |
| **Server-side feature flags per user** | The server doesn't know who you are beyond your cryptographic claim. Per-user flags become client-resident. |
| **Server-side anti-abuse** | The server can't read traffic content; abuse detection limits itself to metadata (rate, timing). |
| **Latency** | Client-side encrypt/decrypt adds CPU. WebCrypto is fast on modern devices but not free, and round trips for key exchange add real ms. |

Each is a real engineering cost. None is fatal; all are negotiable per feature.

### What this is called in practice

This pattern is **end-to-end encryption (E2EE) with the server as zero-knowledge storage**. Production implementations:

- **Signal** — messaging protocol
- **ProtonMail / Proton Drive** — email + storage
- **Tutanota** — email
- **Bitwarden** — password manager
- **1Password** — password manager
- **Cryptee** — document store
- **Tresorit** — file sync

It's not new. It's not exotic. It's been deployable for over a decade. And yet:

---

## The industry observation

**Most multi-tenant SaaS does not use this pattern.** The default-in-practice is the third row of the comparison from the companion case study:

| Approach | What enforces isolation | Industry adoption |
|---|---|---|
| Container-per-tenant | Operating system process boundary | Common in regulated / high-revenue B2B |
| **Shared app + E2E crypto** | **Mathematics** | **Rare — concentrated in privacy-positioned products** |
| Shared app + RLS / `WHERE tenant_id = ?` discipline | Application code correctness | Default — used by most general-purpose SaaS |

The third pattern's enforcement is *"the developers remembered to put the `WHERE` clause everywhere, the code-reviewer caught the cases they didn't, the ORM never had a bug, the cache layer respected tenancy."* It is enforcement by hope.

This is also why breach disclosure reports regularly cite:

- *"A query missed a tenant filter, exposing N customers' data"*
- *"A caching layer served tenant A's response to tenant B"*
- *"A background job iterated all tenants but logged to a shared file"*
- *"An admin tool failed to scope to the requesting operator's tenant"*

Each is a code-correctness failure of a kind that **the E2E pattern makes structurally impossible**. The server can't leak what it can't read.

So the honest observation: **the industry's default multi-tenant pattern provides "soft isolation" — strong enough for many use-cases, but with regular failures that the cryptographic pattern would have prevented by construction.** Few SaaS companies have made the deliberate choice to adopt cryptographic isolation, because:

- It's authoring-expensive (client-side cryptography, key management, lost-password UX)
- It forbids the server-side features that most products want (search, analytics, ML, support tooling)
- It requires operators to give up a capability they're used to having ("we can see customer data to help them")

The pattern's adopters share a specific value: *"we sell the property that we can't see your data."* For products where that property isn't the differentiator, the cost-benefit favours the discipline-based approach — with the regular breach-disclosure tax that brings.

---

## Why this matters under the framework's doctrines

The framework's *verifiable trust* meta-property comes into focus here. Compare what "tenant isolation is real" means under each pattern:

| Pattern | The user has to trust... | Verifiability |
|---|---|---|
| Shared app + RLS discipline | Every developer who has touched the codebase, every code review, every ORM version, every cache change | **Not verifiable from outside the engineering org** |
| Container-per-tenant | The operator's deployment automation is correct, the orchestrator's isolation guarantees hold | **Partially verifiable** — network topology can be inspected, but only the operator can show the deploy manifest |
| **Shared app + E2E crypto** | **The cryptography is sound (peer-reviewed primitives) and the client-side code is what it claims to be (auditable via source / extensions like Code Verify)** | **Cryptographically verifiable — strongest form available** |

The third row's trust requirement *collapses* to a publicly-auditable mathematical property. The user does not have to trust the operator's competence, the developers' attention, the orchestrator's bug-freeness, or the auditors' integrity. They have to trust *the cryptography*, which is the property the open-source community has spent decades hardening.

This is **stronger** than container-per-tenant for the data/identity/cryptographic flavours of isolation. The trade is *weaker for the compute/failure/side-channel/operational flavours that shared-app can't achieve at all*.

From the [No Stealth Data](#ref:doc-nsd) and [Stateless Server](#ref:doc-ss) doctrines' perspective:

- **The server has no decryptable state about clients** — Stateless Server's strongest possible expression.
- **There is nothing the server could covertly gather** — No Stealth Data's strongest possible expression. The server gathering ciphertext is gathering nothing of interpretive value.
- **Audit is mathematical, not policy-based** — verifiable trust at its strongest.

The cryptographic shared-app pattern is *not just doctrine-conformant* — it's the **doctrine's strongest available expression** for the multi-tenant case.

---

## Decision framing — when to use what

Three questions, in order:

1. **What isolation flavours does the use case actually require?**
   - If compute / failure / operational isolation is required (regulated workloads, noisy-neighbour-sensitive apps, per-tenant SLAs): container-per-tenant is required. Shared-app cannot deliver these.
   - If only data / identity / cryptographic isolation matters: either pattern works; choose by other dimensions.

2. **Is the operator a trusted custodian of cleartext data, or does the product promise "we can't see your data"?**
   - Trusted custodian (most enterprise SaaS): RLS discipline or container-per-tenant, depending on isolation requirements above.
   - Zero-knowledge promise (privacy-positioned products, sensitive verticals): cryptographic shared-app is mandatory.

3. **What's the cost of the trade-offs the chosen pattern imposes?**
   - Container overhead vs. shared-app efficiency
   - Cleartext server features (search, analytics, ML) vs. cryptographic protection
   - Operator support workflows that can see customer data vs. those that cannot

There's no universal right answer. There's only the **deliberate** answer for a specific product's values and threat model.

---

## The general lesson

The framing many multi-tenant discussions take — *"shared-app or container?"* — is the wrong granularity. *"Which isolation flavours do we need, and what pattern delivers them at acceptable cost?"* is the right one.

Once decomposed:

- **Container-per-tenant** is the *operationally simple* path: all seven flavours by default, pay container overhead, trust operator-correctness.
- **Cryptographic shared-app** is the *operationally complex* path: three flavours by mathematics (strongly), four flavours forgone (consciously), trust the cryptography, gain efficiency.
- **RLS-discipline shared-app** is the *operationally convenient* path: three flavours by code-correctness, four flavours forgone (also consciously, if honestly), pay periodic breach disclosures.

The third option is the most common precisely because it's the most convenient. It's also where most multi-tenant security incidents come from. The framework's values — verifiable trust, no covert state — push toward the first two and away from the third, which is why this case study exists: to name the cryptographic path as the deliberate-only-pattern when shared-app multi-tenancy is required.

---

## See also

- [The Privacy Doctrines Have Nothing To Lose](#ref:priv-sec) — the companion case study that motivated this one. Lists multi-tenant SaaS in its compromise table; this study unpacks what that row actually entails.
- [Stateless Server](#ref:doc-ss) — the doctrine the cryptographic pattern strengthens (server holds nothing decryptable).
- [No Stealth Data](#ref:doc-nsd) — the doctrine the cryptographic pattern extends (server cannot gather what it cannot read).
- [Cross-Studio References Cost Nothing](#ref:csref) — a sibling architectural-emergent-property case study filed under the Architecture L2. Same shape of argument: *what we got for free by refusing the wrong shape early*.
