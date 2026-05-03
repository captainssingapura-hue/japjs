package hue.captains.singapura.japjs.core;

/**
 * A typed link to a {@link Linkable} target — exported by the target,
 * imported by any module that wishes to navigate to it.
 *
 * <p>Each {@link Linkable} declares an inner record by the convention
 * {@code public record link() implements AppLink&lt;Self&gt; {}}.
 * Other modules import that record via the standard {@code ImportsFor}
 * mechanism, and the generated {@code nav} object in their compiled JS
 * gains an entry pointing at the imported target.</p>
 *
 * <p>Introduced in RFC 0001. The convention {@code record link()} is
 * fixed — the writer derives JS-side identifiers from the target's class
 * metadata, not from this record's name.</p>
 *
 * <p><b>Not sealed:</b> each {@link Linkable} implements its own inner
 * {@code record link()} per the convention. Sealing is impractical because
 * the set of implementations is open. The RFC originally specified a sealed
 * interface; that proved infeasible (self-permitting marker interfaces don't
 * compile) and the design uses a regular marker instead. Discipline at the
 * use-site is enforced by convention (record name {@code link}) and by the
 * writer reading the importing module's metadata.</p>
 *
 * @param <L> the target Linkable being pointed at
 */
public interface AppLink<L extends Linkable> extends Exportable<L> {
}
