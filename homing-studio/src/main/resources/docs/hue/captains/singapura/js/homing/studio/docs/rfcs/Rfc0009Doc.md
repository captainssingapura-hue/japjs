# RFC 0009 — Catalogue Badge + Icon Primitives

| Field | Value |
|---|---|
| **Status** | Proposed |
| **Author** | Howard, with Homing |
| **Filed** | 2026-05-13 |
| **Target release** | 0.0.101 |
| **Scope** | Two optional default methods on `Catalogue`. ~6 lines of framework code. |

---

## 1. The motivating problem

In `CatalogueGetAction.serialize()`, the per-entry JSON for a sub-catalogue card hardcodes:

```java
.append("\"category\":").append(jstr("CATALOGUE")).append(',')
```

Every sub-catalogue tile in every studio renders with the same badge, regardless of what kind of catalogue it is. Compare to Docs (which use `doc.category()` — values like `"DOCTRINE"`, `"RFC"`, `"DEFECT"`) and Plans (which use `plan.kicker()`). Catalogues are the only kind without instance-level control over the badge.

The visual cost: in any multi-section catalogue (the studio's own home: Doctrines, RFCs, Journeys, Building Blocks, Releases), every section's tile reads `"CATALOGUE"`. The user can't tell from a glance which section opens into doctrines vs releases vs RFCs — only by reading the name. Acceptable for 5 sections; cluttered as soon as a Studios sub-tree adds more.

The breadcrumb has a similar issue at lower intensity: the chain is `Acme · studio › Studios › Frontend › Trading UI`, all four crumbs visually identical. No way to read at a glance that *Studios* is the org-level node vs an actual studio.

## 2. The design — two optional default methods

```java
public sealed interface Catalogue permits L0_Catalogue, ..., L8_Catalogue {
    String name();
    default String summary() { return ""; }
    default String badge()   { return "CATALOGUE"; }   // NEW — card badge label
    default String icon()    { return ""; }            // NEW — breadcrumb glyph
    default List<? extends Catalogue> subCatalogues() { return List.of(); }
    default List<Entry> leaves() { return List.of(); }
}
```

**`badge()`** — text label flowing into the card's `category` JSON field. The renderer maps it to a CSS badge class the same way it does today for `"CATALOGUE"`. Existing catalogues keep the default — backward compatible.

**`icon()`** — short string (typically a single emoji) prefixed into the breadcrumb crumb text. Empty by default; opt-in.

Both are orthogonal — a catalogue can override one, both, or neither. A studio catalogue would commonly override both (`badge() = "STUDIO"`, `icon() = "🎬"`).

## 3. Where the primitives flow

| Surface | Field | Change |
|---|---|---|
| `CatalogueGetAction.serialize()` — sub-catalogue card | `"category"` | `jstr("CATALOGUE")` → `jstr(child.badge())` |
| `CatalogueGetAction.serialize()` — breadcrumb chain | `"name"` | `c.name()` → `c.icon().isEmpty() ? c.name() : c.icon() + " " + c.name()` |
| `DocRefsGetAction.serializeBreadcrumbs()` | `"text"` | Same icon-prefix concat |
| `PlanGetAction.serialize()` — breadcrumbs | `"text"` | Same icon-prefix concat |

A small helper (`crumbTextOf(Catalogue c)`) in each action keeps the icon-prefix concat in one named place.

## 4. What's new vs what stays

| | Before | After |
|---|---|---|
| Sub-catalogue card badge | Hardcoded "CATALOGUE" | Per-instance `badge()` (default unchanged) |
| Breadcrumb crumb text | Catalogue name only | Optional icon prefix + name |
| JSON shape for breadcrumbs | `{text, href}` | `{text, href}` (unchanged — icon baked into `text`) |
| Renderer JS | No change required | No change required |
| Backward compatibility | n/a | Full — both methods have safe defaults |

## 5. Migration

**None.** Purely additive. Every existing catalogue keeps the framework default (`badge() = "CATALOGUE"`, `icon() = ""`). Downstream studios opt in per-catalogue.

The framework's own `homing-studio` opts in immediately as the worked example:

| Catalogue | `badge()` | `icon()` |
|---|---|---|
| `DoctrineCatalogue` | `"DOCTRINE"` | `📚` |
| `RfcsCatalogue` | `"RFC"` | `📐` |
| `JourneysCatalogue` | `"JOURNEY"` | `🛤️` |
| `BuildingBlocksCatalogue` | `"BLOCKS"` | `🧱` |
| `ReleasesCatalogue` | `"RELEASE"` | `🏷️` |

Studios that later organize under a Studios catalogue (see §7) use `badge() = "STUDIO"`, `icon() = "🎬"` or similar.

## 6. Accessibility

Screen readers read emoji literally. `"📚 Doctrines"` becomes *"books Doctrines"*. Usually fine — many emoji read naturally enough that the spoken form remains parseable. Studios that care strongly leave `icon()` empty; the framework default ensures no accessibility regression for existing trees.

Worth a brief note in the [Catalogue-as-Container doctrine](#ref:doc-cc) under *What this doctrine permits* — the icon is a *navigation aid*, not a *meaning carrier*. Don't encode information that exists nowhere else in the icon glyph alone.

## 7. Open questions

1. **Should the framework predefine a vocabulary of `badge()` strings?** Today docs use freeform category strings ("DOCTRINE", "RFC", "GOTCHA", …) — no typed enum. Catalogue badges follow the same pattern: any string. Renderers may have CSS for unknown badges fall back to a generic style. **Decision: freeform string**, matches existing precedent.
2. **Should there be a `Studio` typed marker that defaults `badge()` to `"STUDIO"`?** Tempting — but the entire point of this RFC is to *avoid* inventing extra types for use cases that the primitive serves. **Decision: no marker**. A studio is just a catalogue that overrides `badge()`. RFC for a Studios-catalogue *pattern* (not framework type) can be a downstream design note, not framework code.
3. **Should `icon()` accept richer values — e.g., an `SvgRef<?>`?** Out of scope. Emoji are universal, theme-neutral, and require zero rendering scaffolding. SVG-icon support is a separate RFC if ever needed.

## 8. Decision

Adopt. Land in 0.0.101 ahead of any Studios-catalogue work — the primitives are independently useful framework-wide, and the framework's own `homing-studio` benefits the moment they ship.

## 9. Implementation order

1. **Catalogue.java** — add `badge()` and `icon()` default methods.
2. **`CatalogueGetAction.serialize()`** — emit `child.badge()`; prefix breadcrumb crumb text with `parent.icon()` when non-empty.
3. **`DocRefsGetAction.serializeBreadcrumbs()`** — same icon-prefix concat.
4. **`PlanGetAction.serialize()`** — same icon-prefix concat in the breadcrumbs loop.
5. **`homing-studio` catalogues** — five `icon()` + `badge()` overrides as the worked example.
6. **Doctrine update** — one-paragraph note in Catalogue-as-Container on icon-as-navigation-aid.
7. **Build green** — full reactor `mvn test`.

Estimated total work: under an hour, including the doctrine update and registration.

## 10. Why this is the right time

The previous release (0.0.100) just landed RFC 0005-ext2's typed catalogue levels and the sub-catalogue / leaf split. With the tree shape now compile-time correct, the next layer is *visual differentiation of nodes*. The framework can either ship that as a small primitive every catalogue benefits from (this RFC), or punt and let the Studios-catalogue RFC invent it ad hoc.

Shipping the primitive first means the multi-studio organization work targeted at the next release inherits a working badge/icon system on day one — no scope creep, no last-minute design.
