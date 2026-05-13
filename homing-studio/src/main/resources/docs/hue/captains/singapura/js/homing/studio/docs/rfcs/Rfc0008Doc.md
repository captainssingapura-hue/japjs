# RFC 0008 — Interactive Theme Experiences

| Field | Value |
|---|---|
| **Status** | **Phase 1 + Phase 2 Shipped (2026-05-12). Phase 3 explicitly out of scope** — see §10 for the scope decision. |
| **Author** | Howard, with Homing |
| **Created** | 2026-05-12 |
| **Targets** | `homing-core` (primitives, Phase 2) + `homing-studio-base` (Jazz Drum Kit theme + control panel UI). |
| **Builds on** | [Themes as Perceivable Surface doctrine](#ref:doc-surface), [RFC 0007](#ref:rfc-7) audio cues. |
| **Adds** | A new theme kind: **instrument** themes — backdrops you can play. Plus the framework UX for opt-in interactive modes. |

---

## 1. The motivating insight

[RFC 0007](#ref:rfc-7) shipped typed audio cues — clicks on `ClickTarget`s play synthesised sounds. Maple Bridge's temple bell and Retro 90s' Win95 click both work. The framework reached "themes are ambient atmosphere with sonic accents."

But the primitives can go further with almost no additional code. A backdrop SVG with 7 classed drum elements + 7 typed cue bindings = a **playable jazz drum kit**. The framework already does the work; the theme just composes it.

Recognising this as a *kind* of theme — not a one-off — opens a meaningful realm:

| Theme kind | Backdrop is… | Interactivity |
|---|---|---|
| **Minimal** | Absent | None |
| **Atmospheric** | Scenery | Optional hover-grow on accents (Maple Bridge moon) |
| **Instrument** | A playable thing | Click + (opt-in) keyboard fires cues |
| **Ambient game** | A persistent character/world | Click + keyboard interact with state that persists across navigation |

The Jazz Drum Kit is the worked example of "instrument." This RFC ships it (Phase 1, today) and specifies the framework support needed to make instrument themes first-class (Phase 2, follow-up implementation).

Phase 3 — ambient game themes, e.g. the moving-animal demo as a backdrop — lives in a separate RFC because it requires persistent ambient state, a new primitive. Phase 3 is enabled by but not blocked on this RFC.

---

## 2. The doctrine extension

Per [Themes as Perceivable Surface](#ref:doc-surface), themes vary perceivable surface — sight + sound + opt-in interaction + ambient state — never page semantics. This RFC pins down the **opt-in interaction** dimension:

> Themes MAY offer interactive modes — keyboard play, customised cue overrides, gameplay toggles — that the user explicitly enables through a control panel. Enabled modes persist per-theme in localStorage. Disabled modes leave the theme behaving as a pure perceivable-surface theme (clicks on accents, decorative animation, no global event interception).

The bright lines remain:

- Theme keyboard mode is OFF by default. Letter keys go to text inputs, search boxes, doc readers normally until the user opts in.
- Theme keyboard mode never intercepts Tab, Enter, Escape, or arrow keys (assistive tech relies on them).
- Per-theme state lives in the `homing-theme:<slug>:*` localStorage namespace; nothing outside it.
- Themes still don't touch page semantics or navigation logic.

---

## 3. Phase 1 — Jazz Drum Kit (ships in this RFC)

### 3.1 What ships

A new theme `HomingJazzDrums` (`?theme=jazz-drums`) with:

- **Warm-dark palette** — stage-red surface, cream text, brass accents.
- **Drum kit backdrop SVG** — bass drum, snare, two toms, floor tom, hi-hat (closed), ride cymbal, crash cymbal — 8 classed click targets, jazz-kit configuration.
- **Typed cue presets** added to `Cues` stdlib — `KICK`, `SNARE`, `HIHAT_CLOSED`, `TOM_HIGH`, `TOM_LOW`, `FLOOR_TOM`, `RIDE`, `CRASH`. Each is a typed `Cue` (`MembraneCue` for drums, `NoiseCue` for cymbals + hi-hat).
- **Typed audio spec** — `JazzDrumsAudio` interface declaring 8 cue-returning methods, `StandardAudio` impl picking from the new presets.
- **Translucent reading panes** — `.st-main` slab uses `color-mix(... 88%, transparent)` (same trick as Maple Bridge) so the kit is partially visible behind the column on doc pages.

### 3.2 What this exercises

Phase 1 uses **only existing RFC 0007 primitives**. No new framework code. The kit:

- Reuses `ThemeAudio<TH>`, `ClickTarget<TH>`, `Cue` sealed hierarchy.
- Reuses the inline-SVG backdrop pattern from Maple Bridge / Retro 90s.
- Reuses the audio runtime's delegated click listener and Tone.js bridge.
- Reuses the mute toggle UI.

If Phase 1 works without modifying any framework code, RFC 0007's primitives are validated as sufficient for instrument themes. If it doesn't, we learn precisely what's missing before designing Phase 2.

### 3.3 Drum sound design

Cue parameters informed by the existing demo synthesis (`MovingAnimal.js`) and standard drum-synthesis recipes:

| Drum | Cue type | Tone.js voice | Pitch | Envelope (A/D/S/R) | Notes |
|---|---|---|---|---|---|
| **Kick** | `MembraneCue` | MembraneSynth | C2 | 0.001 / 0.4 / 0 / 0.1 | Deep, fast pitch sweep |
| **Snare** | `NoiseCue` | NoiseSynth | (un-pitched) | 0.001 / 0.15 / 0 / 0.05 | White noise burst, short |
| **Hi-hat closed** | `NoiseCue` | NoiseSynth | (un-pitched) | 0.001 / 0.04 / 0 / 0.02 | Tight, bright noise |
| **Tom high** | `MembraneCue` | MembraneSynth | G3 | 0.001 / 0.2 / 0 / 0.08 | Mid pitch, quick decay |
| **Tom low** | `MembraneCue` | MembraneSynth | E3 | 0.001 / 0.22 / 0 / 0.1 | Slightly lower, slightly longer |
| **Floor tom** | `MembraneCue` | MembraneSynth | C3 | 0.001 / 0.28 / 0 / 0.12 | Low, long-ish |
| **Ride** | `NoiseCue` | NoiseSynth | (un-pitched) | 0.002 / 0.5 / 0 / 0.5 | Long noise tail (cymbal ring) |
| **Crash** | `NoiseCue` | NoiseSynth | (un-pitched) | 0.001 / 1.2 / 0 / 0.8 | Even longer, brighter |

Volumes calibrated to typical drum-kit mix: kick -8 dB, snare -10 dB, hi-hat -16 dB, toms -12 dB, cymbals -14 dB.

### 3.4 What Phase 1 does NOT include

- **No keyboard input.** Click-only. Letters typed at the page go to the address bar / doc reader as normal.
- **No control panel.** The existing single-button mute toggle from RFC 0007 is the only audio control.
- **No play-state persistence.** The kit is stateless — each click is its own event.
- **No visual play feedback animation.** Hover-grow via existing CSS pattern is enough for Phase 1.

These are Phase 2 work — specced below, deferred to a follow-up commit.

---

## 4. Phase 2 — Keyboard play + theme control panel (spec; implementation deferred)

### 4.1 Typed keyboard bindings

Extend `ThemeAudio<TH>` with a new optional method:

```java
public interface ThemeAudio<TH extends Theme> {
    TH theme();
    Map<ClickTarget<TH>, Cue> bindings();

    /** Optional — typed keyboard bindings. Default empty. */
    default Map<KeyCombo, ClickTarget<TH>> keyBindings() { return Map.of(); }
}
```

A new `KeyCombo` enum in `homing-core`:

```java
public enum KeyCombo {
    KEY_A, KEY_B, KEY_C, /* … through Z */,
    KEY_0, KEY_1, /* … through 9 */,
    SPACE, RETURN, BACKSPACE;
    
    public String eventCode();  // "KeyA", "Digit0", "Space", etc.
    public String displayLabel(); // "A", "0", "Space" — for the panel
}
```

Why an enum and not free-form strings: same reason as `Note` and `NoteDuration` — the runtime walks the typed values at JS-gen time, never parsing magic strings. The set of supported keys is bounded (letters, digits, a few specials); enum captures the bound.

**Excluded** from `KeyCombo` deliberately: Tab, Escape, arrow keys, function keys. These are reserved for assistive tech and browser navigation. Themes cannot map them.

### 4.2 Play mode — explicit user opt-in

When `keyBindings()` returns a non-empty map, the framework displays a **"Play mode"** toggle in the control panel. Off by default. When on:

- The framework's runtime attaches a `keydown` listener on `document.body`.
- For each keydown, look up the matching `KeyCombo` in the active theme's bindings.
- If a binding exists, the runtime fires the bound `ClickTarget`'s cue (same code path as click).
- The runtime calls `preventDefault()` on the keydown so the key doesn't also reach text inputs.
- The toggle is **prominent and visible at all times when on** — a small "▶ PLAY" indicator in the header so users know their typing isn't going where they expect.

The toggle's state persists to `localStorage["homing-theme:<slug>:play-mode"]`. Themes never auto-enable.

### 4.3 Theme control panel UI

Replaces the existing single-button mute toggle. Surfaces beside the theme picker:

```
┌─────────────────────────────────────┐
│ Theme: Jazz Drums ▼            [⚙] │  ← gear button opens panel
└─────────────────────────────────────┘
                                  │
                                  ▼
                  ┌────────────────────────┐
                  │ Audio          [●─────]│  ← master mute (boolean)
                  │ Volume         [──●───]│  ← 0–100 (number)
                  │ Play mode      [─────●]│  ← only when theme has keyBindings
                  │                        │
                  │ ─── Bindings ───       │  ← collapsible, shown when play mode is on
                  │ A   Kick               │
                  │ S   Snare              │
                  │ D   Hi-hat             │
                  │ F   Tom (high)         │
                  │ G   Tom (low)          │
                  │ H   Floor tom          │
                  │ J   Ride               │
                  │ K   Crash              │
                  │                        │
                  │ ─── Reset preferences ─│
                  │ Clear all for this theme│
                  └────────────────────────┘
```

The panel is **theme-aware**: rows only appear when the active theme has the corresponding capability. A theme without audio shows no audio controls. A theme without keyboard bindings shows no play-mode toggle and no bindings list.

### 4.4 Per-theme localStorage prefs

Namespaced keys:

```
homing-theme:<slug>:muted          → "0" | "1"
homing-theme:<slug>:volume         → "0".."100"
homing-theme:<slug>:play-mode      → "0" | "1"
homing-theme:<slug>:keymap-custom  → JSON object (Phase 2.5, optional)
```

The runtime reads these on init. The control panel writes them on change. Switching themes preserves each theme's independent preferences — useful for users who want drums LOUD but reading-themes muted.

### 4.5 Visual play feedback

When a cue fires (click or keyboard), the runtime adds a transient class `played` to the matched `ClickTarget` element for 180ms:

```css
.drum-kick.played      { animation: drum-hit 180ms ease-out; }
.drum-snare.played     { animation: drum-hit 180ms ease-out; }
.hihat-closed.played   { animation: cymbal-shimmer 240ms ease-out; }
/* … */

@keyframes drum-hit       { 50% { transform: scale(1.06) translateY(2px); } }
@keyframes cymbal-shimmer { 50% { transform: scale(1.04) rotate(2deg); } }
```

Themes provide the per-target animation CSS. The runtime owns the class-add/remove. The animation is theme-territory (visual response); the trigger is framework-territory (event dispatch).

### 4.6 Theme.kind() classification (proposed)

Add an optional method on `Theme`:

```java
public enum Kind { MINIMAL, ATMOSPHERIC, INSTRUMENT, AMBIENT_GAME }

public interface Theme {
    // … existing …
    default Kind kind() { return Kind.MINIMAL; }
}
```

Used by the theme picker to group themes by kind. Helps users understand what they're picking before they pick it.

---

## 5. Phase 3 — Persistent ambient state (out of scope; future RFC 0009)

Themes like the proposed "Pixel Animal walking across pages" need **state that survives navigation**. The animal's x-coordinate persists; on every page load in the same theme, the animal resumes from where it left off.

This requires:

- `ThemeState<TH>` typed primitive — defaults, mutable state map, optional tick callback.
- Theme lifecycle hooks — init on theme activation, dispose on theme change.
- `requestAnimationFrame` driven tick loop with tab-visibility throttling.
- Reduced-motion respect.

This is Phase 3 work, large enough for its own RFC (0009 when committed). Phase 2 (control panel + keyboard) does not block Phase 3 — but Phase 3 builds on Phase 2's prefs infrastructure.

---

## 6. Worked example — the Jazz Drum Kit theme

This is what Phase 1 actually ships. Code sketches; full source in the commit.

```java
public record HomingJazzDrums() implements Theme {

    public static final HomingJazzDrums INSTANCE = new HomingJazzDrums();
    @Override public String slug()  { return "jazz-drums"; }
    @Override public String label() { return "Jazz Drum Kit"; }

    @Override public SvgRef<?> backdrop() {
        return new SvgRef<>(HomingJazzDrumsBg.INSTANCE, new HomingJazzDrumsBg.kit());
    }
    @Override public ThemeAudio<?> audio() { return StandardAudio.INSTANCE; }

    public sealed interface DrumTarget extends ClickTarget<HomingJazzDrums>
            permits Kick, Snare, HihatClosed, TomHigh, TomLow, FloorTom, Ride, Crash {}

    public record Kick()          implements DrumTarget { public String classToken() { return "drum-kick"; } }
    public record Snare()         implements DrumTarget { public String classToken() { return "drum-snare"; } }
    public record HihatClosed()   implements DrumTarget { public String classToken() { return "drum-hihat"; } }
    public record TomHigh()       implements DrumTarget { public String classToken() { return "drum-tom-high"; } }
    public record TomLow()        implements DrumTarget { public String classToken() { return "drum-tom-low"; } }
    public record FloorTom()      implements DrumTarget { public String classToken() { return "drum-floor"; } }
    public record Ride()          implements DrumTarget { public String classToken() { return "drum-ride"; } }
    public record Crash()         implements DrumTarget { public String classToken() { return "drum-crash"; } }

    public interface JazzDrumsAudio extends ThemeAudio<HomingJazzDrums> {
        Cue kick();
        Cue snare();
        Cue hihatClosed();
        Cue tomHigh();
        Cue tomLow();
        Cue floorTom();
        Cue ride();
        Cue crash();

        @Override default HomingJazzDrums theme() { return HomingJazzDrums.INSTANCE; }
        @Override default java.util.Map<ClickTarget<HomingJazzDrums>, Cue> bindings() {
            return java.util.Map.ofEntries(
                java.util.Map.entry(new Kick(),        kick()),
                java.util.Map.entry(new Snare(),       snare()),
                java.util.Map.entry(new HihatClosed(), hihatClosed()),
                java.util.Map.entry(new TomHigh(),     tomHigh()),
                java.util.Map.entry(new TomLow(),      tomLow()),
                java.util.Map.entry(new FloorTom(),    floorTom()),
                java.util.Map.entry(new Ride(),        ride()),
                java.util.Map.entry(new Crash(),       crash())
            );
        }
    }

    public record StandardAudio() implements JazzDrumsAudio {
        public static final StandardAudio INSTANCE = new StandardAudio();
        @Override public Cue kick()         { return Cues.KICK; }
        @Override public Cue snare()        { return Cues.SNARE; }
        @Override public Cue hihatClosed()  { return Cues.HIHAT_CLOSED; }
        @Override public Cue tomHigh()      { return Cues.TOM_HIGH; }
        @Override public Cue tomLow()       { return Cues.TOM_LOW; }
        @Override public Cue floorTom()     { return Cues.FLOOR_TOM; }
        @Override public Cue ride()         { return Cues.RIDE; }
        @Override public Cue crash()        { return Cues.CRASH; }
    }
}
```

Total Java: ~70 lines. Plus the SVG (~200 lines hand-drawn drum kit), the 8 new Cue constants (~80 lines), the palette + globals (~80 lines), and registry registration (~5 lines). All composing existing primitives.

---

## 7. Open questions (decide before Phase 2 implementation)

1. **Multiple drum cues for one target?** A kick drum sometimes plays a "heel-toe" double on the same beat. Could a single `ClickTarget` bind to MULTIPLE cues with random selection or sequence? **Recommendation:** ship Phase 1 with one cue per target; revisit if drumming feels stiff.

2. **Velocity / pressure.** Drums sound different when hit hard vs soft. Pointer events have `pressure`; keyboard doesn't. **Recommendation:** fixed velocity in Phase 1-2; velocity-aware as Phase 4 if we still care.

3. **Customisable keymaps?** Users might want to remap which key plays which drum. **Recommendation:** ship Phase 2 with fixed keymaps; customisation as Phase 2.5 if needed (a JSON serialised to `keymap-custom` prefs key).

4. **Combo keys / sequences?** Press A+S together for a flam. **Recommendation:** out of scope for Phase 2. Single-key only.

5. **Latency as a non-goal.** Web audio one-shot latency is 20-80ms typical. This is fine for a toy; useless for music production. **Recommendation:** document the non-goal explicitly. Frame the drum kit as "atmospheric / playful," not "production tool."

6. **Mobile / touch.** Touch events behave differently from clicks; some browsers' tap-to-zoom defaults interfere. **Recommendation:** test on mobile; add `touch-action: manipulation` to bound elements if needed.

---

## 8. Out of scope (this RFC)

- **Recording / playback** — "record your jam, play it back." Large feature, separate RFC if pursued.
- **Tempo / metronome** — themes don't have a clock concept. Phase 3 territory.
- **Multi-user collaboration** — out of doctrine: themes are per-user, never shared state.
- **MIDI output / input** — Web MIDI is interesting but a different sandbox.
- **Generative AI drum patterns** — wildly out of scope.

---

## 9. Implementation order

**Phase 1 (this commit):**

1. `Cues.KICK` + 7 sibling drum/cymbal constants — append to `Cues` class in `homing-studio-base`.
2. `HomingJazzDrumsBg` SvgGroup + `kit.svg` resource.
3. `HomingJazzDrums` theme record + nested `DrumTarget` permits + `JazzDrumsAudio` spec + `StandardAudio` impl.
4. `StudioThemeRegistry` registers `HomingJazzDrums.INSTANCE` + `Vars` + `Globals`.
5. Smoke test: theme picker shows "Jazz Drum Kit"; clicking each drum on the page plays its cue.

**Phase 2 (follow-up commit, after Phase 1 stabilises):**

6. `KeyCombo` enum in `homing-core`.
7. `ThemeAudio.keyBindings()` default method.
8. Theme control panel UI (vanilla JS, injected into header slot).
9. Per-theme localStorage prefs namespace.
10. Runtime keyboard listener with play-mode gate.
11. Migrate existing mute toggle into the control panel.
12. Jazz Drums theme adopts `keyBindings()` — A–K mapped to the 8 drums.
13. Visual `.played` class injection on cue fire.
14. (Optional) `Theme.kind()` classification for the picker.

---

## 10. Decision

**Phase 1 + Phase 2 both shipped (same session)**. The drum kit landed as the original worked example; Phase 2's framework support (KeyCombo, keyBindings, control panel, per-theme prefs, visual feedback) shipped immediately after. Phase 3 (ambient game state — animal-as-theme) is **explicitly not pursued** for the studio use case — see §13 for the scope decision.

---

## 11. What shipped — Phase 1 (Jazz Drum Kit)

The drum-kit theme is live at `?theme=jazz-drums`. As built:

- **8 typed drum/cymbal targets** — `Kick`, `Snare`, `HihatClosed`, `TomHigh`, `TomLow`, `FloorTom`, `Ride`, `Crash`. Sealed permits in `HomingJazzDrums.DrumTarget`; each carries its visual classToken.
- **8 typed cue presets** in the stdlib — `Cues.KICK` through `Cues.CRASH`. `MembraneCue` for pitched drums, `NoiseCue` for cymbals + hi-hat. Tuned for jazz-kit character.
- **Inline-DOM SVG backdrop** — `HomingJazzDrumsBg.kit` renders the full kit with hover-grow on every drum + cymbal, drum-strike + cymbal-shimmer animations on the `.played` class.
- **Warm-stage palette** — chamber-red surface, cream text, brass accents.
- **Translucent doc-reader slab** — same trick as Maple Bridge so the kit shows partially behind the column on doc pages.

Zero new framework code was needed for Phase 1 — the existing RFC 0007 primitives (sealed `Cue`, `ClickTarget<TH>`, `ThemeAudio<TH>`) carried it through. Validation of "themes as instruments" using only the existing contract.

---

## 12. What shipped — Phase 2 (Interactive modes + Control Panel)

Phase 2 specified four pillars (typed `KeyCombo`, keyboard play, control panel, per-theme prefs). All four shipped, plus several additions that emerged during the build:

### Originally specified

| Pillar | Shape |
|---|---|
| **`KeyCombo` enum** | 37 values — A-Z, 0-9, SPACE. Excludes Tab/Escape/arrows/F-keys at the type level. `eventCode()` maps to `KeyboardEvent.code`; `displayLabel()` for the panel. |
| **`ThemeAudio.keyBindings()`** | Optional default-empty map of `KeyCombo → ClickTarget<TH>`. Themes opt in by declaring entries. |
| **Theme control panel** | Two-button strip next to the theme picker: 🔊/🔇 mute toggle (always present when theme has audio) + ▷/▶ play-mode toggle (only when theme has keyBindings). Play button shows the binding map as a tooltip. |
| **Per-theme localStorage** | Keys namespaced as `homing-theme:<slug>:muted` and `homing-theme:<slug>:play-mode`. Each theme's prefs are independent. |
| **Visual `.played` animation** | Framework default + theme overrides. Drum-strike (non-uniform compress) for drums; cymbal-shimmer (scale + brightness) for cymbals. |

### Added during the build (not in the original Phase 2 spec)

| Addition | Why it landed |
|---|---|
| **`ThemeAudio.hoverBindings()`** | Hover cues turned out to be a natural sibling to click cues. Same typed shape, different event. Shipped alongside the play-mode work. |
| **Per-element WeakMap hover debounce** | Fast cursor sweeps were silently dropping the 2nd-onward cards' sounds. Global throttle replaced with per-element debounce. |
| **`PaletteMode` enum + `VocalPalette`** | Each card needed its own pitch from a shared C3–B5 palette, hash-stable per element. Replaces a boolean `useVocalPalette` flag. |
| **`ChordPalette` + chord hover cues** | Cards play diatonic triads rather than single notes — richer harmonic texture across a catalogue page. `HOVER_CHORD_CLEAN` for Maple Bridge, `HOVER_CHORD_RETRO` for Retro 90s. |
| **2D spatial keyboard layout for drums** | Upper row = top of kit (cymbals + mounted toms); home row = bottom of kit (hi-hat + snare + kick + floor tom). Keyboard mirrors the visible kit. |
| **`distortion` field on `Cue`** + **electric-clean guitar chords** | A second instrument layer in Jazz Drums — keyboard-only `PowerLow / PowerMid / PowerHigh` targets mapped to `Cues.ELECTRIC_CLEAN_LOW / MID / HIGH` (triangle wave, clean, soft envelope, glass-shimmer top partial). U/I/O keys, right-hand upper row. |
| **Render-once-play-many runtime** | Per-trigger synth allocation replaced with `Tone.Offline` bake + `AudioBufferSourceNode` replay. Eliminates polyphonic collision errors, enables rapid retriggers, bounds memory. (Details in [RFC 0007 §11](#ref:rfc-7).) |
| **Inline SVG favicon + brand-aware upgrade** | Silences the `/favicon.ico` 404; client-side fetches `/brand` to upgrade to the studio's logo. |
| **Named-placeholder template substitution** | Replaces `.formatted()` with `.replace()` for the audio-runtime template — `%` characters in JS/CSS no longer require escape. Documented as Gotcha 0001. |

---

## 13. Phase 3 — explicitly out of scope (scope decision)

The original RFC sketched Phase 3 as "ambient game state — themes carry persistent state across navigation, e.g. animal-walks-across-pages." After Phase 2 shipped, we deliberated and decided **not to pursue** ambient-game themes for the studio use case.

The reasoning, captured at scope-decision time:

> Ambient game is doctrinally fine — perceivable surface includes interactive ambient experiences. The drum kit is the worked example. But ambient *game* themes don't fit the **studio use case**: a studio is for reading docs / browsing catalogues / tracking plans. An animal walking across every page competes with the content the user came for. The drum kit works because it's a *bounded* interaction the user opts into (click a drum, press a key). An animal that moves continuously is *unbounded ambient motion* the user has to ignore.

The shape distinction:

| Shape | User opts in via | Fits a studio? |
|---|---|---|
| **Bounded instrument** (drum kit, future piano) | Explicit click / play-mode toggle | ✅ — interaction is on-demand |
| **Ambient game** (animal walking, scrolling characters) | Nothing — it happens regardless | ❌ — competes with content |

The doctrine [Themes as Perceivable Surface](#ref:doc-surface) still permits both shapes architecturally. **Scope of the studio's curated theme catalogue** is the layer where the decision lives.

If a future use case wanted ambient game themes (a meditation app, a children's site, an entertainment site), the framework would support it — `Theme.audio()` + `Theme.backdrop()` + `Theme.kind()` + a hypothetical `ThemeState<TH>` primitive could carry the work. The decision here is *Homing's studio doesn't ship one*, not *the framework forbids them*.
