# RFC 0010 — Multi-Studio Launcher Pattern

| Field | Value |
|---|---|
| **Status** | Accepted (reference impl shipped) |
| **Author** | Howard, with Homing |
| **Filed** | 2026-05-13 |
| **Target release** | 0.0.101 |
| **Scope** | Pattern documentation. Zero framework code. ~3 small files in `homing-demo` as reference implementation. |

---

## 1. The problem

Some shops want multiple "studios" hosted on a single process / single port. The shapes vary:

- A monorepo with several product lines, each with its own design surface (docs, RFCs, plan trackers) and identity.
- A central directory of internal tools, each rendered Homing-style.
- A reference deployment that bundles examples + skills + the framework's own studio on one URL.

The framework today already supports *multi-studio per repo* — `homing-studio`, `homing-skills`, and `homing-demo` are three separate Maven modules in this very tree, each capable of standing up its own `StudioBootstrap.start()` on its own port. What this RFC addresses is **multi-studio behind one bound port**.

## 2. Approaches considered

Four shapes, in increasing intrusiveness (full analysis was held in design discussion; summary here):

| Approach | Mechanism | Framework cost | Per-studio identity | Verdict |
|---|---|---|---|---|
| **A. Path-prefix routing** | Sub-router per studio (`/foo/...` / `/bar/...`), per-context `StudioContext` bundling `{brand, catalogueRegistry, planRegistry, docRegistry, themeRegistry}` | Significant — every action becomes context-aware, URL contracts change | Full (per-studio brand / logo / theme registry) | Right answer for **multi-tenant SaaS**. Out of scope here. |
| **B. Host-based virtual hosting** | Same as A, discriminator is `Host` header instead of path | Same | Same | Same verdict. |
| **C. Sub-catalogue model** | Each studio is just a deeper L1/L2 sub-tree of a single root catalogue | Zero | None — shared brand, shared theme picker, shared URL space | Already supported. Works when "studios" are facets of one product. |
| **D. Launcher pattern** *(this RFC)* | An umbrella L0 catalogue whose `leaves()` are `Navigable` tiles pointing at each source studio's L0 page. All source studios registered in one `CatalogueRegistry`. | Zero | Partial (shared brand, but each source L0 keeps its own catalogue tree intact) | **Adopted.** Minimal cost, demonstrable pattern. |

## 3. The design — launcher pattern

```java
public record MultiStudioHome() implements L0_Catalogue {
    public static final MultiStudioHome INSTANCE = new MultiStudioHome();

    @Override public String name()    { return "Homing Studios"; }
    @Override public String summary() { return "Three studios composed onto one server."; }
    @Override public String icon()    { return "🌐"; }   // RFC 0009 — flows into breadcrumb

    @Override public List<Entry> leaves() {
        return List.of(
                Entry.of(new Navigable<>(
                        CatalogueAppHost.INSTANCE,
                        new CatalogueAppHost.Params(DemoStudio.class.getName()),
                        "🎨 Demo",
                        "Minimal example studio.")),
                Entry.of(new Navigable<>(
                        CatalogueAppHost.INSTANCE,
                        new CatalogueAppHost.Params(SkillsHome.class.getName()),
                        "📜 Skills",
                        "Claude-Code skill bundle.")),
                Entry.of(new Navigable<>(
                        CatalogueAppHost.INSTANCE,
                        new CatalogueAppHost.Params(StudioCatalogue.class.getName()),
                        "🏠 Homing",
                        "Full Homing framework studio."))
        );
    }
}
```

The umbrella is registered as the brand's `homeApp`. The source L0s (`DemoStudio`, `SkillsHome`, `StudioCatalogue`) are also registered in the same `CatalogueRegistry`. Clicking a tile invokes `CatalogueAppHost?id=<source-L0-fqn>` which resolves locally — same process, same port, same JVM.

## 4. What the pattern uses, and what it does not

| Primitive used | Origin |
|---|---|
| `L0_Catalogue` sealed permit | RFC 0005-ext2 |
| `leaves()` with `Navigable` entries | Original RFC 0005 + RFC 0005-ext2 §11 split |
| `icon()` flowing into breadcrumb chain | RFC 0009 |
| `CatalogueAppHost.urlFor(class)` / `Params(id)` | Original RFC 0005 |
| `CatalogueRegistry` allowing multiple L0s | Existing — registry never required reachability from the brand's `homeApp` |

| Primitive **not** used / not invented |
|---|
| Any framework-side multi-context dispatching |
| Path-prefix routing |
| Per-studio `StudioContext` typed bundle |
| Per-studio theme registries / brand overrides |

The pattern is *purely compositional* — it relies on the framework already supporting multiple L0s in one registry, which it does, and on `CatalogueAppHost` accepting any registered L0 as a target, which it does.

## 5. Trade-offs accepted

| Trade-off | Why acceptable |
|---|---|
| Single shared brand label in `<title>` and header. | The umbrella's brand IS the deployment's brand; per-source-studio branding can be communicated in the tile's name + summary + icon. |
| Breadcrumb chain does **not** span the umbrella → source-studio boundary. Inside Homing's RFC 0009 page the breadcrumb starts at `🏠 Homing`, not at `🌐 Homing Studios › 🏠 Homing`. | Each source L0 has no `parent()` — the typed-levels chain rightly stops at the L0 root. The brand link in the header bridges the gap (always returns to the umbrella in one click). Alternatives — re-typing every source L0 as L1 under the umbrella — cascade through their whole sub-trees and break the source studios' standalone-ability. |
| `CatalogueRegistry` holds N L0s instead of 1. | Already permitted by the registry. Validation only requires *referenced* catalogues to be registered, not that all registered catalogues be reachable from the brand's `homeApp`. |
| Plans + Docs from all source studios share one flat registry, so UUID collisions across studios would manifest at boot. | Already enforced by `DocRegistry` / `PlanRegistry`. Source studios authored against their own UUIDs — collisions in practice are vanishingly unlikely. |

## 6. Reference implementation

Lives in `homing-demo` — three small files:

| File | Purpose |
|---|---|
| `homing-demo/src/main/java/.../studio/multi/MultiStudioHome.java` | The umbrella L0 catalogue. Defines 3 `Navigable` tiles. |
| `homing-demo/src/main/java/.../studio/DemoStudioServer.java` | Updated `main()` — registers all 4 L0s + Homing's full sub-tree + Homing's plans + every studio's Docs (via `DocProvider` flow). Brand is the umbrella. |
| `homing-demo/pom.xml` | Adds `homing-studio` + `homing-skills` as dependencies so the demo module sees all three source L0s. |

Reactor build + test stays green. The original `DemoStudio` L0 record is unchanged — still standalone-capable if someone wants to boot just it. The original `homing-studio` and `homing-skills` modules are also unchanged.

## 7. When to escalate to Approach A (path-prefix routing)

The launcher pattern is sufficient when:

- One shared brand identity is acceptable.
- Source studios' own breadcrumbs are self-explanatory (the user knows what studio they're in from its name / icon).
- UUIDs / plan classes / catalogue classes don't collide across source studios.

It becomes insufficient when:

- Different studios need genuinely different brand chrome (different label / logo / theme registry).
- Different studios are different tenants with isolation requirements (e.g., search must not cross-pollinate; one studio's docs must not be findable from another's flat browser).
- Breadcrumbs must show "you are inside Studio X within the multi-studio deployment" — i.e., the umbrella context must persist through navigation.

When any of those become real, RFC 0011 (or whatever it ends up numbered) takes up Approach A: typed `StudioContext`, per-context registries, path-prefix routing, the works. The launcher pattern at that point becomes a deprecated stepping stone — but not before there's a real need.

## 8. Decision

Adopt the launcher pattern as the documented multi-studio approach for the current framework generation. Defer Approach A until a concrete need arises that the launcher can't serve.

## 9. Implementation status

Done. Reference impl lands in 0.0.101 alongside this RFC.

## 10. Why this is the right time

RFC 0009 just landed `icon()` — without it, the umbrella's `🌐` glyph wouldn't flow into the breadcrumb chain, and the launcher pattern would feel structurally identical to any other catalogue. The icon makes the umbrella's role legible at a glance. Together, RFC 0009 + RFC 0010 give the framework's first compositional multi-studio story without any registry-level changes.
