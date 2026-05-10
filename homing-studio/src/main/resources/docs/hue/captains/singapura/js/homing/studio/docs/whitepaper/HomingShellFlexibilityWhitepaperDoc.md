# Homing On Any WebView — A Shell-Flexibility Exploration

**Exploratory paper, draft 1 · 2026-04-25**

> *Homing generates plain ES modules, plain CSS, and standard browser-compatible assets. Any host capable of running a modern webview can serve as a shell. This document explores what that property opens up — without committing to build any of it.*

---

## Status

This is a **vision document**, not a roadmap. It explores a direction the architecture happens to enable; it does not propose work, allocate effort, or modify the action plan. The companion paper `homing-whitepaper.md` describes what Homing *is* and what is being built. This one describes what Homing *could be* if a future direction makes sense.

Read this paper if you want to understand the architectural breadth. Skip it if you want the build plan.

---

## 1. Premise

Homing is, by construction, **shell-agnostic on the front end**. The kernel produces:

- ES modules — native to every modern browser engine.
- CSS — native to every browser engine.
- SVG assets — native to every browser engine.
- Optionally, a typed bus client — JavaScript, with the transport pluggable.

It consumes:

- An HTTP host that serves four stateless endpoints (or, equivalently, a way to *fetch* those resources from any source).
- Optionally, a transport for the bus.

Notice what is *not* in either list: any framework-specific runtime, any DOM-binding library, any browser-engine-specific feature beyond ES modules and standard CSS. The output is unusually portable, almost by accident.

This means Homing apps can run — without modification — inside any host that wraps a modern webview. The only question is which shell makes sense for which audience.

---

## 2. The architectural property that enables this

Three design choices in Homing combine to produce shell flexibility:

1. **Generation, not runtime.** The Java side generates artifacts; it doesn't intermediate at runtime. There's no "Homing container" running in the browser that the shell has to accommodate. There are just files.
2. **Standards-only output.** No bundler, no transpiler, no framework-specific module format. The shell loads what every browser already knows how to load.
3. **Pluggable transport for the bus.** Bus messages are Java records; the wire format is JSON; the transport can be WebSocket, IPC, named pipes, or any byte-stream mechanism the shell provides. The same Java records work in every transport.

These properties are individually small. Together, they make shell choice an integration question, not a framework question.

---

## 3. The shell landscape

Every option here can host Homing output today, in principle. The differences are about **distribution model**, **binary size**, **OS integration**, and **target audience** — not about framework compatibility.

### 3.1 Web shells

| Shell | Engine | Distribution | Binary cost | Notable |
|---|---|---|---|---|
| **Browser (any modern)** | Native (Blink/Gecko/WebKit) | URL | 0 (uses installed browser) | The default. SaaS-style delivery. |
| **PWA** | Native | URL with `manifest.json` + service worker | 0 + cache | Installable from a browser; works offline; no app-store dependency. |

### 3.2 Desktop shells

| Shell | Engine | Distribution | Approx. binary | Notable |
|---|---|---|---|---|
| **Electron** | Bundled Chromium + Node | Native installer per OS | 80–120 MB | Industry standard; heavy; full Node access. |
| **Tauri** | System webview (WebView2 / WKWebView / WebKitGTK) | Native installer per OS | 5–15 MB | Rust core; small; modern; growing ecosystem. |
| **Wails** | System webview | Native installer per OS | 8–20 MB | Go core; similar tradeoffs to Tauri. |
| **WebView2** (Microsoft) | Edge Chromium runtime | Windows-only installer; or part of WinUI3 / WPF / WinForms host | 5–10 MB + WebView2 runtime (preinstalled on Win11) | First-class Windows integration; can host inside any .NET app. |
| **WinUI 3 / WPF + WebView2** | WebView2 | Native Windows installer / MSIX | 10–25 MB | When the desktop app needs heavy native Win features alongside the web UI. |
| **JavaFX WebView** | WebKit (older snapshot) | JRE-bundled or jpackage | 50–80 MB | Pure-JVM option; engine is older; works for internal tools where modern CSS isn't critical. |
| **Neutralino / NW.js / others** | Various | Various | Various | Niche; included for completeness. |

### 3.3 Mobile and embedded

| Shell | Engine | Distribution | Notable |
|---|---|---|---|
| **Android WebView** | Chromium-derived | APK | First-class on Android; performance and modern-CSS support depend on Android version. |
| **iOS WKWebView** | WebKit | App Store IPA | First-class on iOS; restricted to WebKit by Apple policy. |
| **Capacitor / Cordova** | Native WebView wrappers | App Store / Play Store | Adds a plugin ecosystem on top of native WebView. |
| **Embedded / kiosk** (Raspberry Pi, industrial panels) | WebKitGTK / Chromium kiosk | OS image | Single-purpose displays; trading desks, manufacturing HMIs, lobby screens. |

### 3.4 IDE / extension hosts

| Shell | Engine | Distribution | Notable |
|---|---|---|---|
| **VSCode webview / extension** | Embedded Electron Chromium | VSIX | Plugins for VSCode that need rich UI. |
| **JetBrains JCEF** | Chromium Embedded | JetBrains plugin | IntelliJ-family plugins with web UIs. |
| **Browser extension** (Chrome / Firefox / Safari) | Browser-native | Web Store | Side panels, devtools, options pages. |

---

## 4. Three runtime architectures for non-browser shells

How Homing participates at runtime in a non-browser shell admits three distinct architectures, each with different tradeoffs.

### Architecture A — Bundled JVM (server-in-the-box)

The shell embeds a JRE; a Vert.x Homing server starts when the app launches; the webview points at a local port.

```
   [Tauri / WebView2 / Electron shell]
          │
          ▼
   [Bundled JRE]
          │
          ▼
   [Vert.x Homing server on localhost:NNNN]
          │
          ▼
   [Webview loads /app?class=...]
```

- **Binary size:** 60–100 MB (JRE dominates).
- **Cold start:** Slow — JVM warmup is noticeable as first-launch UX.
- **What's preserved:** Everything. Live module generation, theme query params, runtime CSS resolution, dev-mode hot reload.
- **What's lost:** The size advantage over Electron.
- **When it makes sense:** Internal tools where binary size is irrelevant and developer experience matters more than user experience. Shipping to operators on managed laptops, not consumers.

### Architecture B — GraalVM native-image server + small shell

The Homing server is compiled to a native binary via GraalVM `native-image`. Shell launches the binary; webview connects to its localhost port.

```
   [Tauri / WebView2 / Electron shell]
          │
          ▼
   [homing-server.exe — native, ~20 MB]
          │
          ▼
   [Webview loads /app?class=...]
```

- **Binary size:** 15–30 MB total.
- **Cold start:** Fast.
- **What's preserved:** Most server-side runtime features, depending on what survives `native-image` constraints.
- **Risk:** GraalVM compatibility is its own engineering investment. Vert.x is mostly compatible. Homing's own code uses records, sealed interfaces, no obvious reflection — likely compatible but unproven. Until tested, "likely" is doing a lot of work.
- **When it makes sense:** When the runtime advantages of Homing (live generation, theming) are valuable but binary size matters. Best balance of features and footprint, *if* it compiles cleanly.

### Architecture C — Build-time only (no Homing at runtime)

Homing runs in the dev pipeline, generates a static `dist/` folder, and disappears. The shell ships only the static assets plus its own native code.

```
   [Build time]
   Java sources → homing-core → dist/ (static ES modules, CSS, SVG)

   [Runtime — shipped binary]
   [Tauri / WebView2 / native shell] ── custom-protocol ──▶ [dist/ assets]
                  │
                  ▼
   [Native ↔ webview IPC] ◀──▶ [Java-record-derived typed bus client (JS)]
```

- **Binary size:** Tauri-class (5–15 MB). Whatever the shell would normally be.
- **Cold start:** Native-fast.
- **What's preserved:** All the type-safety Homing provides at *build* time — module graph, CSS classes, SVG bundles, typed channels, generated bindings. The discipline travels through the static export.
- **What's lost:** Live module generation, theme query params at runtime, server-driven module resolution. For a shipped desktop app, you don't need most of these — modules are fixed at release time, themes can be CSS-toggled, no server is involved.
- **When it makes sense:** Whenever the shipped binary's size and startup time matter. Probably most desktop / mobile / embedded scenarios.

This is the architecture that opens up the broadest set of shells. It's also the one that requires the most thought — the typed bus has to compile to *two* target languages (JS for the webview, native for the shell side: Rust for Tauri, C# for WebView2/WinUI3, Go for Wails, etc.). Both come from the same Java record, but each transport needs a code generator.

### Choosing among A / B / C

| Criterion | A (JVM bundled) | B (GraalVM native) | C (build-time only) |
|---|:---:|:---:|:---:|
| Binary size | Heavy | Medium | Light |
| Cold start | Slow | Fast | Fastest |
| Implementation effort | Low | Medium-high | Medium |
| Homing runtime features preserved | All | Most | Build-time only |
| Risk of unsolved unknowns | Low | High (GraalVM) | Medium (multi-language codegen) |
| Audience fit | Internal tools, ops | General desktop | General desktop, mobile, embedded |

For this exploration, **Architecture C is the most architecturally interesting** because it requires the smallest change to Homing while opening the widest range of shells. Architectures A and B are useful primarily when the runtime features are non-negotiable.

---

## 5. What the typed bus contributes in non-browser shells

The typed bus is Homing's strongest move in this space, and the reason "type-safe Tauri" is a sentence worth caring about.

In every non-browser shell that exists today, the renderer↔native boundary is **untyped by default**.

| Shell | Native side | IPC mechanism | Type story |
|---|---|---|---|
| Electron | Node main process | `ipcMain.handle` / `ipcRenderer.invoke` | Strings; users hand-write TS bindings. |
| Tauri | Rust core | `#[tauri::command]` + `invoke()` | Generated TS from Rust, decent but Rust-flavored. |
| WebView2 | C# / C++ host | `addHostObjectToScript` / postMessage | Untyped JSON; users hand-write bindings. |
| Wails | Go binding | Generated TS from Go | Decent but Go-flavored. |
| Capacitor | Native plugin code | Plugin interface | Untyped at the boundary. |

In every case, the user maintains type definitions on **both sides** of the boundary, and a refactor on one side requires manual reconciliation on the other. This is the same pain point Homing's bus addresses for browser↔server traffic — and the fix is identical:

> **One Java record. Code-generated bindings on both sides. Renaming a field is a `javac` error on the side that needs to change.**

If Homing's bus generators can target multiple host languages — JS for webview, Rust for Tauri, C# for WebView2, etc. — then Homing becomes **the most type-safe IPC story available** for any of these shells. That is a defensible niche, regardless of which shell wins.

The Java records become the single source of truth. Every shell-specific binding generator is a small, swappable adapter.

---

## 6. The Java backend advantage — the actual killer feature

The previous sections describe shell flexibility in terms of *where the UI runs*. This section is about *what runs alongside it*. In any non-browser architecture that retains the JVM at runtime (A or B above, **not** C), Homing apps ship with a property no Electron or Tauri app can match: **a full, mature backend runtime co-located with the UI in the same shipped binary.**

This is not a small detail. It is the strongest single differentiator Homing brings to the desktop market and the dominant reason a Java team would choose it over Electron.

### 6.1 Heavy lifting belongs in the backend

Electron's architecture forces non-trivial logic into JavaScript. Even with Node in the main process, you are still in the JS ecosystem — with its ecosystem-shaped strengths and weaknesses. For workloads that are CPU-bound, data-heavy, or library-intensive, this is a structural constraint:

| Workload class | Java strength | Node-side reality |
|---|---|---|
| Data processing | Apache Commons, Jackson, Apache POI, PDFBox, Arrow, mature columnar libs | Capable but thinner; many gaps |
| Scientific / ML | DJL, Tribuo, Smile, Apache Commons Math, Spark/Flink integration | Limited; mostly thin wrappers over native |
| Cryptography & security | Bouncy Castle, FIPS-validated JCE providers, mature key management | Smaller; some gaps in regulated contexts |
| Enterprise integration | JDBC, JMS, JTA, Camel, mature SOAP/gRPC, every legacy protocol | Often via shell-out to Java or native helpers |
| Concurrency | Virtual threads (Java 21), structured concurrency, ForkJoinPool | Single-event-loop bottleneck for sustained CPU work |
| Native interop | JNI, Project Panama | Possible via N-API but harder to maintain |
| Profiling & observability | JFR, async-profiler, JMX, decades of mature tools | Decent for I/O; weaker for CPU-bound work |

In an Electron app these workloads either run in JavaScript with the attendant pain, or invoke a separate native helper process the developer must write, build, sign, and distribute themselves. In a Homing desktop app with the JVM present, **they just run in the same process**, in idiomatic Java, with Java's debuggers, profilers, and library ecosystem.

For data-heavy, scientific, enterprise-integration, or regulated-domain applications, this is not a marginal advantage. It is the dominant reason to consider Homing over Electron.

### 6.2 The UI layer can stay thin

A consequence of pushing logic into the backend: the UI layer doesn't need to do much. It renders and dispatches input. Bus channels carry typed messages to the backend; the backend does the work; bus channels carry typed results back.

This pattern has been valuable for decades on the server (REST APIs with thin web clients), but Electron actively pushes against it by making the renderer and the main process *both* JavaScript and *both* convenient places to put logic. Logic creeps into the renderer because nothing structural prevents it — and the renderer is exactly where logic is hardest to test, slowest to run, and most painful to maintain.

Homing's tier separation is **structural**, not cultural. The wire is between Java and JS. Crossing it requires a typed channel. Logic stays where it belongs because the alternative is uncomfortable. That is a feature, not a constraint.

### 6.3 CLI parity comes for free

This is the feature Electron cannot match without rewriting.

A Homing desktop app that ships with a JVM (or a GraalVM native-image binary) can expose **the same backend logic via a CLI**, without launching the UI at all:

```
$ my-app --list-projects
project-a
project-b
project-c

$ my-app export --project project-a --format csv > data.csv

$ my-app sync --remote staging --dry-run

$ my-app                              # default: launch the desktop UI
```

The single shipped binary contains:

- **UI mode** (default or `--ui`): launches the webview shell, starts the typed bus, presents the workspace.
- **CLI mode** (any other args): dispatches to a [Picocli](#ref:picocli) or Spring Shell command tree.
- **The same backend services**, the same Java code, the same business logic — invoked through different entry points.

This pattern is well-loved in tools that have it: `git`, `docker`, `kubectl`, `gh`, `terraform`, JetBrains' command-line tools, AWS CLI. It is **structurally impossible in Electron** without bundling a separate CLI binary written separately. It is **awkward in Tauri/Wails** because Rust/Go CLI sharing requires careful workspace structure and most teams won't do it. In Java with Homing, it is the natural shape — Java has had first-class CLI frameworks for two decades.

What this enables:

- **Scripting and automation.** Internal tools become CI/CD-integrable. Ops scripts hit the same logic the UI exposes.
- **Headless servers.** The same binary runs as a service when a UI is unwanted (cloud, containers, scheduled batches, embedded systems with no display).
- **Power users.** Users who prefer the terminal get the terminal. Users who prefer the UI get the UI. **Same app.**
- **Testability.** Logic exposed via CLI is trivially shellable for integration tests. The UI doesn't have to be in the test loop.
- **Reproducibility.** A workflow performed in the UI can be captured as a CLI invocation, scripted, version-controlled, and replayed. This is gold for audit-heavy domains.
- **Composability.** The CLI can be piped through Unix tools, scheduled with cron, invoked from Make/Bazel, embedded in higher-level scripts. The UI is one front-end among several.

### 6.4 What this means for the architecture choice

Section 4 identified Architecture C (build-time only, no JVM at runtime) as the cleanest path to small binaries and broad shell support. **The Java-backend advantage is the explicit tradeoff Architecture C makes.** Without the JVM at runtime, you don't get heavy-lifting parity, you don't get CLI mode, and you don't get the Java backend ecosystem. You get a small, fast, type-safe webview app and nothing else.

For some applications that's the right tradeoff. For others — and probably for most of Homing's natural Java-shop audience — it is not.

This sharpens the architecture choice considerably:

| Need | Best architecture | Notes |
|---|---|---|
| Smallest binary, fastest startup, simple shipped UI | **C** — build-time only | Trade backend, gain footprint |
| Heavy lifting, mature library access, CLI parity | **A** — JVM bundled | Trade footprint, gain everything |
| Both small *and* full backend | **B** — GraalVM native-image | Best case if it compiles cleanly |

Architecture B does the most work in this table. It is the only one that gives the full Java backend (including CLI mode) at desktop-class binary size. It is also the riskiest because GraalVM compatibility for the full Homing + Vert.x + chosen-CLI-framework stack is unproven. Picocli is GraalVM-friendly; Spring Shell partially. Vert.x has GraalVM support but care is required.

A future desktop spike, if ever undertaken, should probably target **B as the goal, A as the fallback, C as a separate option** for apps that genuinely don't need the backend.

### 6.5 Where this lands the desktop pitch

The strongest version of Homing's desktop pitch is not "Electron alternative." It is:

> **"A single binary that ships your domain logic with two front ends — a rich workspace UI and a complete CLI — both driven by the same typed Java services."**

Electron cannot say this sentence. Tauri cannot say it in any Java-shop-friendly way. JavaFX-based desktop frameworks can say half of it but lose the modern web UI. **This is the actual differentiator.** Type-safe IPC is the second-best feature; the Java backend with CLI parity is the first.

For Java teams that already build CLI tools, internal services, and occasionally desktop apps — and currently use *three different stacks* for those — Homing offers the prospect of **one stack that covers all three with the same code**. That is a much bigger claim than "small binary" or "type-safe IPC," and a much harder one for any incumbent to match.

---

## 7. Shell-by-shell notes

Selected shells with concrete commentary on Homing fit.

### Browser (the default)

The case the framework is currently built for. Already works. Reference point for everything else.

### Tauri

Probably the cleanest fit for Architecture C. Tauri's design — small Rust core, system webview, IPC via JSON — composes naturally with Homing's static-export model. Tauri commands are typed Rust functions; pairing them with Homing's typed bus would require a Rust binding generator parallel to the existing JS one. This is the most straightforward "Homing goes desktop" path.

### Electron

The biggest market, but the highest-mass shell. Architecture A or C both work; Architecture C makes more sense (you'd be shipping Electron + Homing static output, no JVM). The interesting move here is **typed IPC** — Homing's bus offering type safety on top of Electron's `ipcMain`/`ipcRenderer` would be a real upgrade for that ecosystem.

### WebView2 (and WinUI 3 / WPF hosting it)

Underrated and probably the right answer for **Windows-first enterprise desktop apps**. WebView2 is preinstalled on Windows 11, the runtime is Edge Chromium (modern CSS support), and hosting it inside a WPF or WinUI 3 app gives full Windows integration (tray, jump lists, MSIX deployment, code signing, group policy). For Java-heavy enterprises shipping internal Windows tools, this combination — WPF/WinUI3 host + WebView2 + Homing static assets + typed bus over WebView2 host objects — is genuinely interesting and *more enterprise-shaped* than Tauri.

### JavaFX WebView

The pure-JVM path. Same JVM that compiles your Java sources hosts the webview. Distribution is `jpackage`. Engine is an older WebKit snapshot, so modern CSS features can be patchy. Useful for internal tools where staying in the JVM matters more than UI polish.

### VSCode / JetBrains plugins

Both IDEs allow rich web-based panels in extensions. Homing static output loads cleanly. The interesting angle is plugins authored by Java teams for IDE extensibility — currently a frustrating space because most plugin SDKs assume JavaScript. Homing lets the team stay in Java, generate the panel UI as static assets, ship inside the plugin package.

### Mobile via WKWebView / Android WebView

Plausible for line-of-business mobile tools. The static-export model fits the App Store / Play Store distribution model. Performance on mid-range Android devices is the open question. Capacitor or a thin native shell would be the integration path. Probably the lowest priority of the non-browser shells.

### Embedded / kiosk / industrial

A surprising sweet spot. Trading floor displays, manufacturing HMIs, retail kiosks, lobby info screens — these are typically:

- Long-running (DomOpsParty's leak detection is gold).
- Layout-heavy (workspace primitives apply).
- Distributed by IT image, not app store (no signing/notarization mess).
- Often Java-backed already.

A small Linux box, kiosk-mode browser, Homing static export served from a local Vert.x process or even from `file://`. Low-friction, real value.

---

## 8. Strategic implications

If this direction were ever pursued, it would change Homing's market positioning in four ways.

**(a) The pitch leads with the Java backend, not with shell flexibility.** The strongest claim is "single binary, domain logic + workspace UI + complete CLI, all type-safe end-to-end." Shell flexibility is the *property that makes this possible*; the Java backend with CLI parity is the *feature users care about*. Lead with the feature.

**(b) Three audiences, not one.** Today's pitch targets "Java teams shipping web internal tools." A shell-flexible Homing additionally reaches:

- Java teams shipping desktop tools with backend logic (currently a vacuum — Electron forces JS, Tauri forces Rust).
- Java teams shipping mobile line-of-business apps with shared backend code.
- Java teams shipping embedded / kiosk / industrial UIs.
- Java teams that already ship CLIs *and* web tools and want one stack instead of two.

Each is a distinct audience with distinct pains. The last one is larger than it sounds — many enterprise Java shops maintain parallel CLI tools, web admin consoles, and occasional desktop helpers, all with overlapping logic and zero code sharing.

**(c) "Type-safe IPC" becomes a top-line feature in any non-browser shell.** In every shell that exists today, the renderer↔native boundary is weakly typed. Homing's bus genuinely improves on what Electron, Tauri, Wails, and WebView2 offer out of the box. This is a stronger differentiator in the desktop market than in the SaaS market — because in desktop, every shell already has *some* answer; none of them are good.

**(d) The pitch evolves from "framework" to "platform with two faces."** With Architecture A or B, Homing is no longer just a UI framework. It is a **runtime that ships your domain logic with switchable front-ends** — webview today, CLI tomorrow, headless service the day after, all from the same code. Architecture C reframes Homing as a *compiler* (type-safe build pipeline producing shell-agnostic UI artifacts) — also durable, but a different position. **The action plan's eventual desktop spike should pick which framing to commit to**; trying to be both at once dilutes the message.

---

## 9. Open questions

Honest list of things this paper does not resolve.

- **Does GraalVM native-image actually compile Homing cleanly?** Untested. Reflection use is light, but Vert.x interactions and any classpath-scanning code paths need verification.
- **What does the multi-language bus generator look like?** JS is built. Rust, C#, Go, Swift would each be a real generator. Code-generation framework choice matters. Possibilities: Java-record annotations + a generator service, or a shared schema (e.g. records → JSON Schema → per-language codegen).
- **How are static assets best served from a shell?** Custom protocols (Tauri's `tauri://`, WebView2's virtual host name mapping, file:// with permissive CORS) all work, each with quirks. Picking a default is a real design decision.
- **What happens to live reload?** Build-time Homing gives up the file-watching dev-mode story. Re-creating it inside a desktop dev shell is doable but not free.
- **Bundle size for the static export.** A typical workspace app's `dist/` could be 1–5 MB of JS+CSS+SVG. Acceptable, but worth measuring.
- **Code signing, notarization, auto-update.** Each shell has its own answer. None are Homing's job to solve, but examples should exist for adopters.
- **Plugin / extension model for shells.** Some shells (VSCode, JetBrains) require specific packaging conventions. Translating Homing output into those formats is non-trivial.
- **Offline-first patterns.** Service workers, local storage, optimistic mutations — all are application-level concerns, but Homing should provide an opinion or at least an example.
- **CLI / UI dispatch and shared service initialization.** A single binary that switches modes based on argv needs careful startup design — the UI mode wants the full Vert.x stack; the CLI mode wants minimal init. Spring Boot solves this with profiles; pure-Vert.x apps need bespoke wiring. Worth a recipe.
- **GraalVM compatibility for the chosen CLI framework.** Picocli is GraalVM-friendly; Spring Shell is partial. The CLI framework choice constrains Architecture B feasibility.
- **Distribution of dual-mode binaries.** Windows users may not expect a `.exe` to also be a CLI; some installers strip CLI affordances. Naming conventions and packaging defaults need thought.

---

## 10. What this paper deliberately does not do

- **Does not commit to building any of this.** No engineering effort is allocated; no phase is added to the action plan; no deliverables are promised.
- **Does not claim Homing is an Electron killer.** The space is crowded. Differentiation is plausible but unproven. Marketing claims would be premature.
- **Does not specify which shell wins.** Different audiences want different shells. The architectural property is that Homing doesn't have to choose.
- **Does not evaluate against specific competitors at feature level.** Tauri vs Wails vs Neutralino is a separate, deeper analysis. This paper assumes shell choice is downstream of audience choice.
- **Does not describe what users should adopt today.** Browser is the only currently-supported shell. Everything else is exploration.

---

## 11. Summary

Homing's output is, by accident of standards-compliance, **shell-flexible**. The kernel produces native ES modules, CSS, and SVG; the bus produces typed messaging contracts in multiple target languages. Anything that hosts a modern webview can host Homing apps.

But the genuinely powerful feature is not shell flexibility. It is what shell flexibility enables when paired with a JVM at runtime:

> **A single binary that ships your domain logic with two front-ends — a rich workspace UI and a complete CLI — both driven by the same typed Java services.**

This is the sentence Electron cannot say. It is the sentence Tauri cannot say to a Java-shop audience. It is the sentence that opens a defensible position in a crowded space.

The architecture choice is sharper than this paper originally framed it:

- **Architecture A** (JVM bundled) — easiest to build, full backend, full CLI parity, ~80 MB binary. **Right for internal tools where size doesn't matter.**
- **Architecture B** (GraalVM native-image) — full backend, full CLI parity, ~20 MB binary. **Best case if it compiles cleanly. The target.**
- **Architecture C** (build-time only) — smallest binary (~10 MB), but trades away the Java backend and CLI mode. **Right for thin-shell apps that don't need backend logic.**

A and B preserve the killer feature. C is a different positioning altogether — Homing as compiler, not as runtime.

The market positions this could open, *theoretically, on a long horizon, contingent on real engineering work:*

1. **The single-stack Java desktop** — domain logic, workspace UI, and CLI in one binary. The most differentiated position.
2. **Type-safe IPC for any webview shell** — Electron, Tauri, WebView2, Wails. A genuine improvement over each.
3. **Java teams' first-class path to embedded / kiosk / industrial UIs** — small market, low competition, real fit.

This is a natural extension of the design. It is not a commitment. And it is one of the strongest extensions the architecture supports.

---

**Companion documents:**
- `homing-whitepaper.md` — the main technical white paper (built and designed surfaces).
- `docs/ACTION-PLAN-2026-04-25.md` — the build sequence (this paper does not modify it).
- `docs/SESSION-SUMMARY-2026-04-25.md` — context for how this idea arose.
