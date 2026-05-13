# RFC 0007 — Theme Audio Cues (Typed, Synthesised)

| Field | Value |
|---|---|
| **Status** | **Shipped + Extended** (2026-05-12). Original design landed; subsequent sessions added significant extensions captured in §10–§11 below. The core contract (typed `Cue`, `ThemeAudio<TH>`, `ClickTarget<TH>`) is unchanged; the runtime architecture and the supported palette/event surface have grown substantially. |
| **Author** | Howard, with Homing |
| **Created** | 2026-05-12 |
| **Targets** | `homing-core` (primitives) + `homing-studio-base` (theme integrations + runtime) |
| **Builds on** | [Defect 0002](#ref:def-2) reframe (themes vary perceivable surface, not control logic), [Encapsulated Components doctrine](#ref:doc-encapsulated), [RFC 0006](#ref:rfc-6) (writing-media textures — same orthogonal-dimension shape) |
| **Adds** | A fourth theme dimension alongside palette / writing-medium / wallpaper: click-triggered audio cues, fully typed end-to-end, synthesised via Tone.js — no audio files, no new HTTP endpoints. |

---

## 1. The motivating insight

Themes already vary three perceivable dimensions: **palette** (colour tokens), **writing medium** (texture under text — RFC 0006), and **wallpaper** (atmospheric backdrop — `Theme.backdrop()`). The fourth, missing dimension is **sound**.

Maple Bridge has a temple. Clicking it should ring a bell — that's the kind of moment that takes a theme from *aesthetic surface* to *atmospheric environment*. The framework has Tone.js bundled (already proven in the moving-animal demo with synth-driven move/jump/death cues), so the synthesis pipeline is solved. What's missing is the typed contract that lets a theme declare *"my temple makes a bell sound when clicked"* without ever touching a string selector or a magic note name.

This RFC adds that contract.

---

## 2. Doctrine extension — what audio is, and what it isn't

Defect 0002's resolution drew a sharp line:

> Themes vary **paint + shape** (CSS-only). Themes do **not** vary **behavior** (DOM, JS, events — view-layer territory).

Audio cues feel like behavior — they're event-driven side effects. But on inspection they're not. Refined doctrine:

> Themes vary **perceivable surface** — what the user **sees** and **hears**. Themes do **not** vary **control logic** — what *happens* when something is interacted with.

The control logic for a temple click is fixed: "user gesture detected on a typed click target → framework dispatches the bound audio cue." That's framework-owned, constant across themes. *Which* cue is bound to *which* target is theme-owned, variable — exactly parallel to "what colour is `--color-accent`" or "which SVG renders as the backdrop."

| Layer | What varies per theme | Owner |
|---|---|---|
| DOM structure of clickable elements | Nothing | Framework / theme's SVG |
| Click event handler | Nothing | Framework (delegated) |
| Audio playback machinery (Tone.js synths, envelopes, scheduling) | Nothing | Framework (generic runtime) |
| **Which cue plays on which target** | Per theme | **Theme** |
| **Cue parameters** (synth voice, envelope, notes, volume) | Per cue | **Theme** (picks from `Cues` stdlib or constructs literal) |

Audio sits cleanly on the "perceivable surface" side of the line. The doctrine extends; it does not bend.

---

## 3. The typed loop — zero string operations end-to-end

The defining constraint: **no string concatenation, no string parsing, no magic-string keys at the API surface**. The DOM still has class attributes (strings by spec, immutable), and the served JS still contains string literals (code, by definition). What goes away is:

- Free-form selector strings as map keys (`Map<String, ...>` → `Map<ClickTarget, ...>`)
- Magic note/duration strings (`"C4"`, `"16n"` → `Note.C4`, `Duration.SIXTEENTH` enums)
- String discriminators for synth types (`"sine"` → `OscType.SINE` enum)
- Stringly-typed JS dispatch (string switch → sealed-interface switch expression at JS-gen time)

The class tokens that have to land in the DOM (e.g. `"mb-temple"`) live as constants on the typed `ClickTarget` records — encapsulated, referenced once at framework JS-gen time, never concatenated or parsed.

---

## 4. Architecture

### 4.1 Core primitives (`homing-core`)

```java
public sealed interface ClickTarget<TH extends Theme> {
    /** Stable DOM class token. Encapsulated constant; theme authors
     *  reference the implementing record, never this string. */
    String classToken();
}

public sealed interface Cue permits OscCue, MembraneCue, NoiseCue {}

public record OscCue(OscType type, Envelope env, double volumeDb, List<NoteHit> notes) implements Cue {}
public record MembraneCue(double pitchDecay, int octaves, Envelope env, double volumeDb, List<NoteHit> notes) implements Cue {}
public record NoiseCue(Envelope env, double volumeDb, List<NoteHit> notes) implements Cue {}

public enum OscType { SINE, TRIANGLE, SQUARE, SAWTOOTH }
public enum Note { /* C0 … B8 — 108 enum constants covering MIDI 12–119 */ }
public enum Duration { WHOLE, HALF, QUARTER, EIGHTH, SIXTEENTH, THIRTY_SECOND, SIXTY_FOURTH }

public record Envelope(double attack, double decay, double sustain, double release) {}
public record NoteHit(Note note, Duration duration, int offsetMs) {}

public sealed interface ThemeAudio<TH extends Theme> {
    TH theme();
    Map<ClickTarget<TH>, Cue> bindings();
}
```

`Theme` gets one new method:

```java
public interface Theme {
    // … existing surface …
    default Optional<ThemeAudio<?>> audio() { return Optional.empty(); }
}
```

### 4.2 Theme-specific audio interfaces — the spec

Each theme that opts into audio **declares its own audio interface** — a spec listing exactly which click targets the theme requires cues for. The interface acts as a single source of truth for the theme's audio surface area:

```java
public record HomingMapleBridge() implements Theme {

    /** Sealed permits — the complete list of clickable elements
     *  on Maple Bridge's surface. Adding a new one is a typed action. */
    public sealed interface MbTarget extends ClickTarget<HomingMapleBridge>
            permits Temple, Moon, TempleWindow {}

    public record Temple()       implements MbTarget { public String classToken() { return "mb-temple"; } }
    public record Moon()         implements MbTarget { public String classToken() { return "mb-moon"; } }
    public record TempleWindow() implements MbTarget { public String classToken() { return "mb-temple-window"; } }

    /** Maple Bridge's audio SPEC. Every implementor must supply a cue for
     *  each method. Java's "implements interface but missing method" error
     *  catches forgetting one. */
    public interface MbAudio extends ThemeAudio<HomingMapleBridge> {
        Cue temple();
        Cue moon();
        Cue templeWindow();

        @Override default Map<ClickTarget<HomingMapleBridge>, Cue> bindings() {
            return Map.of(
                new Temple(),       temple(),
                new Moon(),         moon(),
                new TempleWindow(), templeWindow()
            );
        }
        @Override default HomingMapleBridge theme() { return HomingMapleBridge.INSTANCE; }
    }

    /** Standard implementation — picks from the shared Cue stdlib. */
    public record StandardAudio() implements MbAudio {
        public static final StandardAudio INSTANCE = new StandardAudio();
        @Override public Cue temple()       { return Cues.TEMPLE_BELL; }
        @Override public Cue moon()         { return Cues.SOFT_CHIME; }
        @Override public Cue templeWindow() { return Cues.LAMP_CRACKLE; }
    }
}
```

Three properties emerge for free:

1. **Compiler enforces completeness.** Adding a new permit (`TempleBell`) without adding a corresponding method to `MbAudio` is a compile error in the default `bindings()`.
2. **Multiple implementations are natural.** `StandardAudio` is one impl; a downstream `LoudAudio` or `AccessibilityAudio` would be another. The interface IS the extension point.
3. **The spec reads as documentation.** Opening `MbAudio` immediately tells you exactly what Maple Bridge's clickable surface is.

### 4.3 Reusable cue stdlib

```java
// homing-core / homing-studio-base — TBD location during impl
public final class Cues {
    /** A bronze temple bell — three partials at 1.0/1.5/2.5 ratios with
     *  the fourth detuned by 80 ms for the bell's characteristic shimmer. */
    public static final OscCue TEMPLE_BELL = new OscCue(
        OscType.SINE,
        new Envelope(0.002, 3.5, 0.0, 2.5),
        -10.0,
        List.of(
            new NoteHit(Note.C4, Duration.HALF, 0),
            new NoteHit(Note.G4, Duration.HALF, 0),
            new NoteHit(Note.E5, Duration.HALF, 0),
            new NoteHit(Note.C5, Duration.HALF, 80)
        )
    );

    public static final OscCue  SOFT_CHIME    = /* … */;
    public static final OscCue  LAMP_CRACKLE  = /* … */;
    public static final OscCue  WIN95_CLICK   = /* … */;
    public static final MembraneCue CARD_THUD = /* … */;
    public static final OscCue  TICK         = /* … */;

    private Cues() {}
}
```

Themes pick from this library or construct fresh `OscCue`/`MembraneCue`/`NoiseCue` literals. Either way, fully typed.

### 4.4 JS generation — typed switch, no string parsing

The framework's audio module emitter walks each Theme's `audio()` Optional:

```java
public static String emit(Cue cue) {
    return switch (cue) {
        case OscCue o      -> emitOsc(o);
        case MembraneCue m -> emitMembrane(m);
        case NoiseCue n    -> emitNoise(n);
    };
}
```

Exhaustive over the sealed `Cue` hierarchy. Each emit method maps record components to JS object literal fields with known names — no reflection, no string-key lookup. `Note.C4` serialises to `"C4"` (Tone.js's native note format); `Duration.HALF` serialises to `"2n"` via a small enum-keyed table.

**The string concatenation that happens here is GENERATING JS source code**, not operating on data. Same as how `StudioStyles.css()` emits CSS bodies, or how the existing ES-module-from-Java pipeline emits import/export statements. The runtime path is data-typed end-to-end.

### 4.5 Runtime delivery — three options

1. **Inline JSON in bootstrap HTML.** `AppHtmlGetAction` injects `<script id="theme-audio" type="application/json">{...}</script>` when the active theme has audio. Runtime reads it on startup. Simplest. No new endpoint, no new module.

2. **Generated ES module per theme.** A `theme-audio-<slug>.js` module containing the cue map as a JS object literal, imported via the existing `ModuleImports` pipeline. Most consistent with Homing's "everything is a typed module" rhythm.

3. **New `/theme-audio?theme=<slug>` action returning JSON.** Smallest infrastructure change to the existing pattern. But the user has explicitly excluded "actions which will make things heavy" — a JSON action is *not* heavy, but the line should be drawn somewhere.

**Recommendation:** **(2) — generated ES module.** Matches `StudioStyles.js`, theme-vars.css, etc. Zero new actions. The audio runtime imports the generated cue module + Tone.js, and uses the typed cue data directly. Build-time validation can run statically against the same module.

### 4.6 The runtime JS shim

~80 lines, in `homing-studio-base/src/main/resources/homing/js/.../theme-audio-runtime.js`:

```javascript
import { Synth, MembraneSynth, NoiseSynth, start as toneStart } from "/lib/tone";
import { CUE_BINDINGS } from "/theme-audio";  // generated per-theme module

let audioReady = false;
const inFlightUntil = new Map();   // cue → timestamp; debounce
const muted = () => localStorage.getItem("homing-audio-muted") === "1";

// Pre-instantiate synth voices, one per binding (recycled on each fire).
const synths = new Map();
for (const [classToken, cue] of Object.entries(CUE_BINDINGS)) {
    synths.set(classToken, makeSynth(cue));
}

// One delegated click listener for everything.
document.body.addEventListener("click", async (e) => {
    if (muted()) return;
    const target = e.target.closest("[class*='-']");  // any classed element
    if (!target) return;
    for (const [token, synth] of synths) {
        if (target.classList.contains(token)) {
            await ensureAudio();
            playCue(synth, CUE_BINDINGS[token]);
            return;
        }
    }
});

async function ensureAudio() {
    if (!audioReady) { await toneStart(); audioReady = true; }
}

function makeSynth(cue) { /* sealed-kind switch → Synth | MembraneSynth | NoiseSynth */ }
function playCue(synth, cue) { /* trigger each NoteHit at its offset */ }
```

Mute UI is a single button (`<button class="st-audio-toggle">🔊/🔇</button>`) added to the studio header, toggling the `localStorage` flag and the visible icon.

### 4.7 What auto-imports when

A theme that returns `Optional.empty()` from `audio()` triggers no new imports — Tone.js stays out of that page's bundle. A theme that returns a `ThemeAudio` triggers:

- `ToneJs` import (already a `BundledExternalModule`, ~50KB gzipped)
- `theme-audio-runtime.js` import (~2KB)
- `theme-audio-<slug>.js` generated module (~500 bytes per theme)

Total weight added on audio-enabled pages: ~52KB. Total weight on non-audio pages: zero.

---

## 5. Worked examples

### 5.1 Maple Bridge — temple bell + soft moon chime + window lamp crackle

Three click targets, three cues from the stdlib:

```java
public record StandardAudio() implements HomingMapleBridge.MbAudio {
    @Override public Cue temple()       { return Cues.TEMPLE_BELL; }
    @Override public Cue moon()         { return Cues.SOFT_CHIME; }
    @Override public Cue templeWindow() { return Cues.LAMP_CRACKLE; }
}
```

Total theme-author code: 4 lines. Total bytes added to page weight: ~500 (the cue manifest module).

### 5.2 Retro 90s — Win95 system click on icons, soft thud on cards

```java
public record HomingRetro90s() implements Theme {
    public sealed interface R90sTarget extends ClickTarget<HomingRetro90s>
            permits MyComputer, MyDocuments, Network, RecycleBin, Card {}

    public record MyComputer()  implements R90sTarget { public String classToken() { return "w95-icon-mycomputer"; } }
    public record MyDocuments() implements R90sTarget { public String classToken() { return "w95-icon-documents"; } }
    public record Network()     implements R90sTarget { public String classToken() { return "w95-icon-network"; } }
    public record RecycleBin()  implements R90sTarget { public String classToken() { return "w95-icon-recycle"; } }
    public record Card()        implements R90sTarget { public String classToken() { return "st-card"; } }

    public interface R90sAudio extends ThemeAudio<HomingRetro90s> {
        Cue myComputer();
        Cue myDocuments();
        Cue network();
        Cue recycleBin();
        Cue card();

        @Override default Map<ClickTarget<HomingRetro90s>, Cue> bindings() {
            return Map.of(
                new MyComputer(),  myComputer(),
                new MyDocuments(), myDocuments(),
                new Network(),     network(),
                new RecycleBin(),  recycleBin(),
                new Card(),        card()
            );
        }
        @Override default HomingRetro90s theme() { return HomingRetro90s.INSTANCE; }
    }

    public record StandardAudio() implements R90sAudio {
        public static final StandardAudio INSTANCE = new StandardAudio();
        @Override public Cue myComputer()  { return Cues.WIN95_CLICK; }
        @Override public Cue myDocuments() { return Cues.WIN95_CLICK; }
        @Override public Cue network()     { return Cues.WIN95_CLICK; }
        @Override public Cue recycleBin()  { return Cues.WIN95_DING; }    // bin gets a different sound
        @Override public Cue card()        { return Cues.CARD_THUD; }
    }
}
```

---

## 6. Build-time validation

The conformance suite gets new checks:

1. **Every `ClickTarget`'s `classToken()` must appear in the bound theme's backdrop SVG content.** A scan: `if !svg.contains("class=\"" + target.classToken() + "\"")` → fail with "Typed ClickTarget Temple has no `mb-temple` element in nocturne.svg." Catches drift between hand-authored SVG and typed targets.
2. **Every sealed `ClickTarget` permit must have a binding.** The default `bindings()` enforces this at compile time via the explicit Map literal; the test asserts no permit is missing.
3. **Every `Cue` reference must resolve to an `OscCue` / `MembraneCue` / `NoiseCue`** — i.e., the sealed switch exhaustivity holds. Compiler enforces.
4. **Note + Duration enums must round-trip** through the generator → JS → Tone.js. A small smoke test that emitted JS imports and runs without throwing.

---

## 7. Accessibility — muted-by-default vs sounds-by-default

The trade-off is genuine; both have real costs.

| Default | Pro | Con |
|---|---|---|
| **Sounds on** | Discoverable; users hear the theme's character on first interaction | Public-space surprise; auditory-sensitivity users; first-click surprise |
| **Sounds off** | Safe in public/shared spaces; respects sensitivity | Most users will never enable; the theme's audio is effectively invisible |

**Recommendation for v1: sounds ON, with a prominent persistent mute toggle (🔊/🔇) in the studio header next to the theme picker.** Tone.js's autoplay restriction already means the *first* user interaction has to happen before any audio can play — so the literal first click on the temple is the moment audio unlocks AND plays. After that, the toggle is the user's lever.

Persistence: `localStorage.homing-audio-muted = "1" | "0"`. Defaults to unmuted (absent key). Per-domain, persists across visits.

---

## 8. Out of scope (v1)

- **Hover-triggered audio.** Fatiguing on long-form pages, unpredictable as cursor crosses elements. Click-only for v1. v2 could add carefully rate-limited hover cues.
- **Background music / ambient pads.** Different concern (autoplay restrictions, ambient design, longer assets). The framework already has a worked BGM pattern in the moving-animal demo, but that's an *app* feature, not a *theme* feature.
- **Per-Doc / per-page cue overrides.** Themes apply uniformly. A future RFC could let docs declare their own click targets, but that crosses the doctrine line.
- **Speech synthesis / screen reader integration.** Separate concern — accessibility tooling, not theme audio.
- **Generative / live-coded audio.** Tone.js can synthesise on the fly; the demo proves it. But it's overkill for one-shot UI cues. v2.

---

## 9. Implementation order

1. **Core types** — `Cue` sealed interface + permits, `OscCue`/`MembraneCue`/`NoiseCue`, `OscType`/`Note`/`Duration` enums, `Envelope`/`NoteHit` records, `ClickTarget<TH>` sealed interface, `ThemeAudio<TH>` sealed interface, `Theme.audio()` default method.
2. **Cues stdlib** — `Cues.TEMPLE_BELL`, `SOFT_CHIME`, `LAMP_CRACKLE`, `WIN95_CLICK`, `WIN95_DING`, `CARD_THUD`, `TICK` — initial set covering the two existing themes' needs.
3. **Maple Bridge wiring** — `MbTarget` permits + `MbAudio` interface + `StandardAudio` impl + `audio()` override.
4. **Retro 90s wiring** — same pattern, `R90sTarget` + `R90sAudio` + `StandardAudio`.
5. **JS-gen module emitter** — generates `theme-audio-<slug>.js` per theme with audio; emits the typed cue map as a JS object literal via the sealed-switch dispatch.
6. **Auto-imports** — when a theme has audio, the page bundle includes `ToneJs` + the runtime module + the generated cue module.
7. **Runtime shim** — `theme-audio-runtime.js`: delegated click listener, Tone.js synth instantiation, debounce, mute check.
8. **Mute UI** — small `<button class="st-audio-toggle">` injected by the header renderer; toggles `localStorage` and the icon.
9. **Build-time validation** — conformance test that every classToken appears in the bound SVG.

---

## 10. Decision

**Accepted.** Implementation starts immediately after this RFC merges, in the order listed in §9. Estimated effort: 2–3 hours of focused work.

---

## 11. Architecture evolution — the render-once-play-many pivot

The original RFC proposed: "on each trigger, instantiate a Tone.js synth, fire `triggerAttackRelease`, dispose 5 seconds later." This worked for one-shot cues but had two problems:

1. **Polyphonic collision** — Tone.js's `Synth` is monophonic. A cue stacking multiple partials at the same offset (the Maple Bridge temple bell's 4 simultaneous sine partials) throws *"Start time must be strictly greater than previous start time."* Fixed initially by creating one synth per note hit; this scaled badly under rapid retriggers.

2. **Per-trigger allocation cost** — 30 rapid drum clicks = 30 synth-node graph constructions + 30 disposals. Audible glitches under load; expensive on lower-end devices.

**The pivot:** render each cue ONCE at first audio gesture via `Tone.Offline`, store the result as an `AudioBuffer`, replay through `AudioBufferSourceNode` on each trigger. Architecture:

```js
// At first user gesture (once):
const buffer = await tone.Offline(() => {
    for (const hit of cue.notes) {
        const synth = makeSynth(cue, tone);
        synth.triggerAttackRelease(hit.note, hit.duration, hit.offsetMs / 1000);
    }
}, cueRenderDuration(cue));
// buffer is now an AudioBuffer; cache it.

// At trigger time (any number of times, fast):
const source = audioContext.createBufferSource();
source.buffer = buffer;
source.connect(audioContext.destination);
source.start();
// source auto-disposes when playback ends; ~zero allocation overhead.
```

**Properties this gives us:**

- **Zero synth allocation per trigger** — `AudioBufferSourceNode` is the natural Web Audio polyphony primitive; constructing one is essentially a buffer-pointer copy.
- **Bake-time is cheap** — Tone.Offline runs in an offline context, takes milliseconds per cue; all cues bake in parallel via `Promise.all`.
- **Memory is bounded** — each baked AudioBuffer is ~30 KB for a 0.18 s hover cue, ~200 KB for a long-decay bell. Total per theme well under 1 MB.
- **Polyphonic collision is gone** — the offline render takes care of summing all partials cleanly; the runtime just plays a pre-mixed buffer.
- **Rapid retriggers overlap** — calling `source.start()` 30× in a second creates 30 source nodes, all summing at the destination. The drum kit's keyboard play feels natural.

This pivot was informed by an existing audio demo at `js-demos/audio` that used a similar pre-render pattern with Howler.js + procedural synthesis. We adopted the *pattern* (render once, play many) but stayed on Tone.js (already bundled, richer synth vocabulary, native `Tone.Offline`). Captured in the **Real Instrument Audio Engine** journey as the resolution of that journey's first three decisions.

---

## 12. Extensions shipped beyond the original spec

| Extension | What it does | Where it lives |
|---|---|---|
| **`ThemeAudio.hoverBindings()`** | Optional second binding map, triggered by `mouseover` entry instead of click. Same typed `ClickTarget` records can appear in both maps. | `homing-core/ThemeAudio.java` |
| **Per-element WeakMap hover debounce** | Different elements never throttle each other; same element gets an 80 ms safety window. Fast cursor sweeps now play every card crossed. | `AppHtmlGetAction.renderAudioRuntime` |
| **`PaletteMode` enum** | Replaces the original `useVocalPalette: boolean` with a tri-state `NONE | VOCAL | CHORD`. Future palette modes (rhythm? scale?) extend by adding enum values, not by adding boolean fields. | `homing-core/PaletteMode.java` |
| **`VocalPalette`** | 11 typed notes spanning C3–B5, G4 centred. Cues with `paletteMode: VOCAL` are baked once per pitch. | `homing-core/VocalPalette.java` |
| **`ChordPalette`** | 6 diatonic triads in C major (I, ii, iii, IV, V, vi). Cues with `paletteMode: CHORD` are baked once per chord (all chord notes simultaneously in offline render). Each card hovered plays a full chord. | `homing-core/ChordPalette.java` |
| **Identity-stable hover selection** | Hover trigger picks a variant via `hash(element.textContent + sessionSalt)` — same card always plays the same pitch/chord within a session; refresh re-rolls the salt. | runtime `hashIndex()` helper |
| **Random click selection** | Click trigger with multi-variant cues picks randomly — humanization, like a real drummer not hitting the same way twice. | runtime `playCue()` selection branch |
| **`distortion` field on `Cue`** | Numeric 0.0–1.0 distortion amount. Runtime routes the synth through `tone.Distortion(amount)` before destination when `> 0`. Enables the Jazz Drums electric-guitar power-chord layer. | `Cue` interface default + `OscCue` record field |
| **Visual `.played` animation** | Transient class added to the matched element on cue trigger; framework provides a default compress-bounce keyframe; themes override per-class (e.g., drum-strike, cymbal-shimmer). | runtime `pulse()` + injected CSS |
| **Inline favicon (and brand-aware upgrade)** | SVG H favicon in the bootstrap `<head>`; client-side fetch of `/brand` upgrades it to the studio's logo when present. | `AppHtmlGetAction.execute` |
| **Named-placeholder runtime template** | The audio runtime's `<script>` template now uses `__NAME__` placeholders + `String.replace()` instead of `%s` + `.formatted()`. Eliminates `%`-escape paper-cuts (see [Gotcha 0001](#ref:gotcha-0001-formatted)). | `AppHtmlGetAction.renderAudioRuntime` |

These extensions all sit on the original typed-Cue contract — `OscCue`/`MembraneCue`/`NoiseCue` records, `ClickTarget<TH>` interface, `ThemeAudio<TH>` interface. None of them required new sealed permits or breaking changes. The shape RFC 0007 set up turned out to scale further than originally specified.

---

## 13. What this means for the doctrine

The [Themes as Perceivable Surface doctrine](#ref:doc-encapsulated) lists "Sound" as one of the five theme dimensions. The realized surface for that dimension is:

- **Click cues** (RFC 0007 original) — single-buffer typed `Cue` per `ClickTarget`.
- **Keyboard cues** (RFC 0008 Phase 2) — same `Cue` reachable via `KeyCombo` mapping, opt-in via play mode.
- **Hover cues** (this RFC §12) — same `Cue` reachable via `mouseover` entry, throttled per-element.
- **Multi-buffer pitch-varied cues** (this RFC §12) — `paletteMode: VOCAL` for single-note variants; `paletteMode: CHORD` for triadic variants.

All four trigger paths share one typed contract. Theme authors write cue records, bind them to click targets (and optionally key combos and hover targets), and choose palette modes. The framework handles the rest — render, dispatch, throttle, visual feedback.
