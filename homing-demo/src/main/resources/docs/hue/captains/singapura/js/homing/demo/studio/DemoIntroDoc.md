# Welcome to the Homing Demo Studio

A small studio running entirely on the public **`homing-studio-base`** API — the same machinery any downstream consumer gets when they depend on the artefact.

## What this is

This studio exists to **dogfood** the framework. Per the [First-User Discipline](#) doctrine over in `homing-studio`, every framework primitive must have a real consumer that isn't `homing-studio` itself. This is that consumer.

The whole studio is configured in **one file** — `DemoStudioServer.java` — with:

- one home `Catalogue` (`DemoStudio.java`),
- one typed `Doc` (this page),
- a `StudioBrand` whose logo is a typed `SvgRef` pointing at the **turtle** SvgBeing already declared by `CuteAnimal` for the SVG demos,
- the same `CatalogueAppHost` / `PlanAppHost` / `DocReader` / `DocBrowser` AppModules every downstream studio uses.

That's it. No per-app `appMain`, no hand-rolled HTML, no theme registry of its own (it inherits the four bundled themes — Default / Forest / Sunset / Bauhaus). The whole boot is a single `StudioBootstrap.start(...)` call.

## What the turtle is doing here

The brand mark in the header is the same turtle SVG the `Wonderland` and `DancingAnimals` demos use — typed via `SvgRef<>(CuteAnimal.INSTANCE, new CuteAnimal.turtle())`. Two birds, one asset:

- The demo gets a friendly mark with personality — easier to tell apart from the main studio at a glance.
- The framework's `SvgRef` primitive gets exercised against an asset *not designed for it* — a 800×800 illustrative SVG with hardcoded dimensions, originally drawn for the SVG-extruder demos. If the framework can host that as a brand logo without per-asset work, the primitive is robust.

## What's not here

- **No catalogues for the SVG/animation demos** — those still run from `WonderlandDemoServer` on its own port. They predate the typed-Catalogue pattern and their migration is a separate, larger effort. (Per the First-User Discipline, a deliberate deferral, recorded in the v1 release tracker.)
- **No plan trackers** — the demo studio doesn't track its own implementation. It's a *consumer* of the primitives, not a project artefact.

## Try it

- **Theme picker**: top-right of the header. Flip between Default / Forest / Sunset / Bauhaus and watch the chrome retint. The turtle stays the turtle (a logo is identity).
- **`/`**: redirects here.
- **`/app?app=catalogue&id=...DemoStudio`**: this catalogue page.
- **`/brand`**: JSON payload showing what the framework serves to consumer modules — note `logo` carries the full inline SVG markup.
- **`/themes`**: JSON catalogue of every registered theme with palette swatches.

## How to read the source

Three files, all under `homing-demo/src/main/java/.../demo/studio/`:

| File | What it is |
|---|---|
| `DemoStudio.java` | The home `Catalogue` — name, summary, list of entries. |
| `DemoIntroDoc.java` | This doc — UUID + title + summary + classpath markdown. |
| `DemoStudioServer.java` | `main()` — wires AppHosts + catalogues + brand + boots `StudioBootstrap`. |

Plus the markdown body you're reading now, at `homing-demo/src/main/resources/docs/.../demo/studio/DemoIntroDoc.md`.

That's the full surface area of a downstream studio. Copy these four files, rename, and you have your own.
