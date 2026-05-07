package hue.captains.singapura.js.homing.core;

import java.util.Set;

/**
 * Convenience parent for {@link CssClass} that opts a utility into the three
 * common pseudo-state variants — {@code :hover}, {@code :focus}, {@code :active} —
 * with one declaration. The framework auto-generates the variant rules and
 * JS handles. No separate variant records needed.
 *
 * <pre>
 *   public record bg_accent() implements UtilityCssClass&lt;Util&gt; {}
 * </pre>
 *
 * <p>Renders to:</p>
 * <pre>
 *   .bg-accent              { &lt;body&gt; }
 *   .hover-bg-accent:hover  { &lt;same body&gt; }
 *   .focus-bg-accent:focus  { &lt;same body&gt; }
 *   .active-bg-accent:active { &lt;same body&gt; }
 * </pre>
 *
 * <p>Override {@link #variants()} to opt out of states or add custom ones.</p>
 *
 * @param <G> the CssGroup this utility belongs to
 */
public interface UtilityCssClass<G extends CssGroup<G>> extends CssClass<G> {
    @Override default Set<String> variants() { return Set.of("hover", "focus", "active"); }
}
