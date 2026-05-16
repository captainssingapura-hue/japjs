# Doctrine — Quality Without Surveillance

> **A well-designed, correctly-implemented, thoroughly-tested application does not need to collect data from clients to make itself better. The engineering team accepts the burden of quality through their own discipline — design, testing, code review, internal usability work — and does not delegate that burden onto users by extracting behavioural data dressed up as "product improvement." If a user wants to give feedback, they give it explicitly: through a form they filled out, a bug report they wrote, a public post they chose to make. The application's quality is the engineers' problem, not a tax on the user's attention.**

This doctrine binds going forward — new framework code and new downstream-studio code following framework conventions. It sharpens [No Stealth Data](#ref:doc-nsd) in one specific direction: that doctrine refused *covert* gathering and permitted disclosed opt-in collection; this doctrine asks whether disclosed opt-in *"help us improve"* collection should even be reached for. The answer, under this doctrine, is **no — improve the engineering instead.**

---

## What this doctrine commits to

Every line of code added to the framework, every feature shipped, every design decision made, every bug fixed is the **engineering organisation's work to figure out without extracting data from users**. The mechanisms available to the engineering team are:

- **Design** — careful, deliberate, peer-reviewed before code is written.
- **Testing** — unit, integration, end-to-end, browser-matrix, regression. The framework's conformance test family is the canonical worked example.
- **Code review** — humans reading humans' work before merge.
- **Internal usability work** — engineers using their own product, paid consenting testers running through flows, dedicated internal QA passes.
- **Beta programs with explicit disclosure** — opted-in users who *know* they're providing telemetry, with full visibility into what's being collected, and the right to opt out without losing functionality.
- **Public channels** — bug reports, GitHub issues, forum posts, app-store reviews. The user *chose* to participate; the framework is allowed to read public posts about itself.
- **Explicit feedback forms** — a "Report a bug" link the user clicks, an email address they write to, a contact form they fill out. The user composes the message and decides what to include.

What is **not** available: silent telemetry, automatic crash reporters that send context the user didn't curate, "anonymous usage statistics" toggles, A/B test arms that record user behaviour, engagement metrics, funnel analytics, heatmaps, session replay, behaviour-trained ML features. None of these are needed. None are permitted as a default.

## What this doctrine bans

- **"Anonymous usage data" toggles** — including ones defaulting to off. The very existence of the toggle is a confession that the application *wants* to gather and is asking permission. The doctrine asks instead: *why does the engineering need this data, and why can't the engineering produce that quality without it?*
- **A/B testing with passive observation** — A/B testing where the variants are deployed and the system silently records which variant produced more engagement. (A/B testing with *consenting beta users who are told they're in a comparison and shown the results* is a different shape — that's an explicit research collaboration.)
- **Crash reporters that auto-send** — Sentry, Bugsnag, Rollbar, Datadog RUM, anything in this category in its default configuration. Crashes are logged to `console.error`; the user (or developer reading the user's console) chooses what to do with them.
- **Session replay tools** — LogRocket, FullStory, Hotjar's session-replay product. Already banned by No Stealth Data; reinforced here because the *"to understand user behaviour and improve UX"* framing is exactly what this doctrine refuses.
- **Heatmaps** — recording where users click, scroll, hover for "design improvement" purposes. The doctrine asks: *what's missing from the engineering's design discipline that this heatmap would fix? Now go fix the design discipline.*
- **Funnel analytics** — measuring user drop-off across multi-step flows. If a step has UX problems, that's a design failure to investigate by design means.
- **Engagement metrics tracked per-user** — DAU/MAU/retention by user identity. (Aggregate uptime, request counts, error rates not tied to user identity are operational concerns, permitted under Stateless Server.)
- **"Smart" features that learn from your behaviour** — UI that adapts based on your past actions, *if* the adaptation requires the system to retain or transmit your behaviour. (A pure local-storage preference the user can clear is fine.)
- **Feature flag systems that record outcomes** — flag platforms that observe whether variant A or variant B caused more engagement. Flags that decide per-request from server config are fine; flags that observe per-user and learn are not.
- **"Beta" badges as a license to surveil** — calling a feature *"beta"* doesn't make implicit telemetry OK. Beta means *"this may break"*; it does not mean *"we will silently watch you use it."*
- **The framing "to make the product better"** — when paired with any of the above. The doctrine asks the engineering team to do the work that produces the quality, not extract from users to substitute for it.

## What this doctrine permits

- **Explicit feedback the user composed and submitted** — a "Report a bug" form they filled out, a comment they wrote, a feature request they submitted. The content is whatever the user typed; nothing else is gathered.
- **Beta programs with full disclosure** — *"This is a preview release; we collect the following exact data; here's how to see it; here's how to opt out without losing access."* The user knows, agrees, and can leave.
- **Internal usability work** — engineers using their own software, hired UX participants in observed sessions, dedicated QA passes. The participants consented to being observed in person, on the clock, for a specific session.
- **Public posts about the framework** — forum threads, GitHub issues, blog posts, app-store reviews. The user posted publicly; the framework is allowed to read what was posted publicly. (Aggregating these into a dashboard for product decisions is fine; that's what they exist for.)
- **Server-side aggregate operational metrics** — uptime, request rate, error rate, all without user keying. Permitted under Stateless Server and reaffirmed here.
- **Browser-native diagnostics the user controls** — DevTools, browser performance recordings. Out of scope; the browser is its own privacy boundary.
- **Open-source community signals** — PRs, issues, stars, forks. These are explicit acts of contribution; the contributor knows their participation is public.
- **App-store / website published reviews** — the user wrote a review publicly; reading it isn't surveillance.

The pattern in everything permitted: the user *initiated* the data flow, *composed* its content, and could have chosen not to. The framework receives what the user gave; it does not gather what the user didn't.

## Where this doctrine doesn't apply

- **Engineering's own internal testing** — automated tests, dogfooding, internal staging environments. The engineering team is using their own product against themselves; no user has been observed.
- **Server-internal observability about the server process** — JFR, JMX, heap profiles, server logs at the operational level. These observe the server, not users.
- **Product analytics that depend on no user identity at all** — *"how many times was the homepage requested last week"* with no session correlation, no per-user keying. Aggregates without per-user discrimination are operational, not behavioural.
- **The user's own behaviour visible to themselves** — a "your recent activity" panel that reads local data. The user is observing themselves; nobody else is.
- **Compliance-mandated logging in regulated deployments** — where the regulator's mandate creates an exception that the doctrine cannot resolve. Such deployments operate outside this doctrine and accept the cost; see [the privacy-doctrines case study](#ref:priv-sec) for the broader treatment.

The doctrine binds: **product-improvement data extraction from users**. Engineering's internal work and operator-server diagnostics are out of scope.

---

## The canonical test

Three questions before adding any data collection, any analytics SDK, any feedback mechanism that gathers without the user composing the message:

1. **Is the engineering organisation asking the user to substitute for engineering discipline?** *"We don't know if this feature is good — let's measure how users react"* is exactly this substitution. The honest move is to **do the design work** that lets you ship a feature you're confident in, then ship it.

2. **Did the user compose and submit the message?** If the data flow is *"the user did a thing; the system recorded the thing,"* it's surveillance regardless of how it's framed. If the data flow is *"the user wrote a message; the user clicked send,"* it's feedback.

3. **Could improving the engineering process eliminate the perceived need?** If the answer is *"yes, with more testing / better design review / a dogfooding pass / a beta program with disclosure,"* the engineering process is the place to invest, not the data collection.

Three yeses → the doctrine refuses the addition and points to the engineering work that should happen instead.

---

## Why the strictness is worth it

- **Quality is the engineer's job, not the user's tax.** Every minute a user spends being observed is a minute of attention the engineering team is extracting in lieu of their own work. The doctrine refuses to make users pay for the engineering team's inadequacy.
- **The "improvement" framing is the most common cover for monetisation.** *"We collect anonymous usage data to make the product better"* is the publicly-defensible sentence behind which most behavioural-data businesses operate. By refusing the collection categorically, the doctrine refuses the cover story too — including for itself. If the framework ever wanted to monetise user data, it couldn't reach for *"to improve the product"* as justification because that justification is already disallowed.
- **Surveillance accretion is path-dependent.** Once the engineering org has the capability *"see what users do,"* the next feature, the next pivot, the next acquisition will find new uses for the capability. The doctrine refuses the *capability*, which is the only durable defense.
- **Engineering discipline strengthens by being load-bearing.** When the engineering team can't fall back on *"let's just ship it and measure,"* design reviews get sharper, test coverage gets broader, beta programs get more deliberate, dogfooding becomes serious. The doctrine forces the discipline that produces high-quality software by other means.
- **Pairs with the framework's existing stance.** [Cdn-Free conformance](#ref:rfc-1) already refuses external scripts (which is how most analytics arrives). [No Stealth Data](#ref:doc-nsd) refused covert gathering. [Stateless Server](#ref:doc-ss) refused server-side retention. This doctrine refuses the most common *justification* for the patterns those doctrines structurally prevent. Same posture, named at the conceptual level.
- **The honest test of an application is whether it works without watching anyone use it.** A well-engineered application's value is intrinsic — it's good for the user whether anyone is observing or not. An application that requires observation to be good is one whose engineering hasn't done the work.

---

## The honest meta-claim

This doctrine is sharper than its siblings because it names something the industry doesn't usually say out loud:

> **Most "we collect data to improve the product" framing is, on examination, a request that users substitute for engineering discipline.** The discipline is harder, slower, and more expensive than instrumentation. Telemetry is the path of least resistance. The doctrine refuses the path of least resistance because the cost — user attention extracted as ongoing tax for the engineer's convenience — is paid by the wrong party.

When a team feels the urge to add improvement-telemetry, the doctrine asks one question: *what design work, what test coverage, what beta program, what dogfooding session, what user-research collaboration would tell us the same thing — and why are we not doing those instead?* The answer is almost always *"because they're more work."* The doctrine answers: **yes. That's the work. Do it.**

---

## How to think about it

When considering any data collection, draw the data flow:

```
user does thing → system records thing → engineering reads recording
```

vs.

```
user composes message → user clicks send → engineering reads message
```

The first is surveillance dressed as improvement. The second is feedback. The user has to be the *active* party in the data's existence, not the passive observed party. If you can't redraw your proposed feature as the second flow, the doctrine refuses it.

When the perceived need is *"we genuinely can't predict X without real-world data"* — typically a rare browser bug, a corner-case interaction, a workflow nobody on the team tried — the answer is:

- **A beta program with explicit disclosure** — users who chose to participate know exactly what data is collected and can leave. *"We're collecting X, Y, Z. You can see them at this URL. You can opt out here at any time without losing access."*
- **Public bug reporting channels** — when a user hits the bug, they have a documented place to describe it.
- **Direct user research** — schedule sessions with paid, consenting participants.

All three preserve the user's agency over their data; all three deliver the engineering signal you wanted. The path is longer; the engineering is real.

---

## See also

- [No Stealth Data](#ref:doc-nsd) — refused covert client-side gathering. This doctrine asks whether even *disclosed* improvement gathering should be reached for.
- [Stateless Server](#ref:doc-ss) — refused covert server-side retention. Same posture from the server side.
- [Functional Objects](#ref:doc-fo) — refused covert *behaviour* surfaces (public statics). The privacy + this doctrine refuse covert *data* and *labour-substitution* surfaces. Same principle, different slices.
- [The Privacy Doctrines Have Nothing To Lose](#ref:priv-sec) — case study examining the security and trust implications of the broader privacy stance this doctrine completes.
- [RFC 0001 — App Registry & Typed Nav](#ref:rfc-1) — the Cdn-Free conformance family lives here and structurally refuses the external scripts most analytics arrive through.
