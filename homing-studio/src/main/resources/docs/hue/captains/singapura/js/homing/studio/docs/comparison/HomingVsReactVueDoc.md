# Fair Comparison: Homing vs React vs Vue

## Upfront honesty: not the same category

React and Vue are **client-side UI frameworks** with component models, reactive state, and virtual DOM / reactivity systems. Homing is a **Java-side build/wiring tool** that generates ES modules and serves plain JS that talks to the DOM directly. Comparing them head-to-head is somewhat apples-to-oranges — but the comparison is still useful to understand where Homing fits.

## Feature matrix

| Dimension | React | Vue | Homing |
|---|---|---|---|
| **Primary role** | UI rendering library | Progressive UI framework | ES module graph generator + asset server (Java-authored) |
| **Rendering model** | Virtual DOM + diffing | Reactive proxies + template compiler | None — you write raw DOM code in `.js` |
| **Component model** | Function components + hooks | SFCs (`.vue`), Composition/Options API | No components; JS files + a bootstrap `appMain(rootElement)` |
| **State management** | useState / Context / Redux / Zustand / … | `ref` / `reactive` / Pinia | None built-in; roll your own |
| **Type safety** | TS (opt-in, on JS side) | TS (opt-in, on JS side) | Java compile-time for module wiring, CSS classes, SVG refs |
| **Templating** | JSX | SFC templates or JSX | Manual `document.createElement` / innerHTML |
| **Build step** | Vite/Webpack/etc. | Vite | Java/Maven; no bundler — served as native ES modules |
| **Hot reload** | Fast Refresh | HMR | File-based `homing.devRoot` (refresh, no restart) |
| **Ecosystem** | Massive (npm) | Large (npm) | None — pre-alpha, single author |
| **Routing / SSR / forms / i18n** | Rich 3rd-party | Rich 3rd-party (official router/Pinia) | Not provided |
| **Maturity** | 2013, battle-tested | 2014, battle-tested | Early stage, demos only |
| **Target audience** | JS/TS devs | JS/TS devs | Java devs who want to ship small SPAs without leaving the JVM toolchain |

## Where Homing is genuinely different (and sometimes better)

- **Compile-time guarantees on the module graph.** Renaming an export in Java refactors every importer. React/Vue get this only if you adopt TypeScript *and* only within the JS side.
- **Type-safe CSS class bindings.** `CssClass` records catch typos at `javac` time. React/Vue rely on CSS Modules, Tailwind, or runtime checks.
- **No bundler.** Browsers load native ES modules served by Vert.x. Simple mental model, tiny toolchain.
- **Single language toolchain for backend-heavy teams.** If your team already lives in Java/Maven, Homing removes the Node/npm/webpack layer.

## Where React/Vue are clearly ahead

- **Actual UI work.** Any non-trivial UI (lists, forms, reactive state, conditional rendering) is trivial in React/Vue and manual DOM work in Homing.
- **Ecosystem.** Routers, state stores, UI kits, testing libraries, devtools — Homing has essentially none of these.
- **Community, hiring, docs, Stack Overflow.** Enormous vs. none.
- **Production track record.** React and Vue run a large fraction of the web; Homing has not shipped production apps publicly.
- **Performance primitives.** VDOM diffing / fine-grained reactivity handle large dynamic UIs; Homing leaves perf entirely to you.
- **Browser DX.** React DevTools / Vue DevTools have no Homing equivalent.

## Honest verdict

Homing is **not a replacement** for React or Vue. It's a different tool solving a different problem: "I'm a Java developer who wants to generate and serve a small, type-safe ES module graph without a JS build system." For that niche it's pleasant and coherent. For building a typical production SPA with lists, forms, routing, and shared state, React or Vue are the pragmatic choice by a wide margin.

A useful analogy: React/Vue are to Homing roughly what Spring MVC is to a hand-rolled Servlet + static HTML server — one is a full framework, the other is a thin, opinionated scaffold.
