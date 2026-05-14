package hue.captains.singapura.js.homing.core;

import java.util.List;
import java.util.Map;

/**
 * Per-theme audio binding — maps typed {@link ClickTarget}s to typed
 * {@link Cue}s. The framework's runtime walks this map at JS-gen time
 * and produces a small generated module the audio runtime imports.
 *
 * <p>Themes that opt into audio typically declare their own
 * {@code <Name>Audio} interface extending this one, listing one
 * cue-returning method per click target — that interface acts as the
 * audio SPEC for the theme. Implementations satisfy the spec by
 * picking from the {@code Cues} stdlib or constructing fresh record
 * literals.</p>
 *
 * @param <TH> the theme this audio binding belongs to
 */
public interface ThemeAudio<TH extends Theme> {

    /** Identity: which theme this audio binding is bound to. */
    TH theme();

    /**
     * Typed bindings — Map key is a {@link ClickTarget} record (value-
     * equal by record semantics), value is a {@link Cue}. Zero string
     * operations on the runtime path.
     */
    Map<ClickTarget<TH>, Cue> bindings();

    /**
     * Optional hover cues — same typed {@link ClickTarget} records as
     * {@link #bindings()}, but triggered by {@code mouseover} (cursor entry)
     * instead of click. Default empty.
     *
     * <p>Use for subtle chrome feedback: a soft chime when the cursor enters
     * a card, a quiet click when it enters an interactive list item. The
     * runtime globally throttles hover sounds (≤1 every 400 ms across the
     * page) so cursor sweeps don't trigger a barrage. The runtime also
     * filters {@code mouseover} events caused by movement within an
     * already-entered element — only true element-entry fires the cue.</p>
     *
     * <p>Doctrine note: hover sounds are subject to the same mute toggle as
     * click cues. No separate "chrome sounds" toggle in v1 — users who
     * find hover sounds intrusive mute the theme.</p>
     */
    default Map<ClickTarget<TH>, Cue> hoverBindings() { return Map.of(); }

    /**
     * Optional typed keyboard bindings (RFC 0008 Phase 2). Default empty.
     *
     * <p>When non-empty, the framework's runtime renders a "play mode"
     * toggle in the theme control panel. Play mode is OFF by default —
     * the user opts in via the toggle, persisted to per-theme
     * {@code localStorage} under {@code homing-theme:<slug>:play-mode}.</p>
     *
     * <p>When play mode is on, keydown events matching one of these
     * {@link KeyCombo}s dispatch the bound {@link ClickTarget}'s cue —
     * the same code path as a click on that target. Per the doctrine
     * (perceivable surface, opt-in interactivity), keyboard interception
     * is explicit and reversible.</p>
     */
    default Map<KeyCombo, ClickTarget<TH>> keyBindings() { return Map.of(); }

    /**
     * Optional chord progressions (RFC 0008 extension) — when non-empty AND
     * {@link #progressionVoice()} is non-null, the framework's runtime renders
     * an "auto-play guitar" UI surface (clicking the theme's guitar element
     * toggles loop playback) and a "Key" slider for transposing the progressions
     * across diatonic root pitches.
     *
     * <p>Each {@link Progression} is a sequence of indices into
     * {@link ChordPalette#CHORDS}. The runtime loops indefinitely while
     * auto-play is on, picking a new random progression each cycle.
     * Persisted to per-theme {@code localStorage}:
     * {@code homing-theme:<slug>:autoplay} (toggle state),
     * {@code homing-theme:<slug>:root-offset} (semitone offset).</p>
     *
     * <p>Honour mute as global authority: when muted, the scheduler skips
     * chord playback but the toggle / slider state remain authoritative.</p>
     */
    default List<Progression> progressions() { return List.of(); }

    /**
     * Voice used to render each chord in the progressions. Should declare
     * {@code paletteMode = PaletteMode.CHORD} so the framework bakes one
     * buffer per chord in {@link ChordPalette#CHORDS}. Default null = no
     * auto-play even if {@link #progressions()} is non-empty.
     */
    default Cue progressionVoice() { return null; }
}
