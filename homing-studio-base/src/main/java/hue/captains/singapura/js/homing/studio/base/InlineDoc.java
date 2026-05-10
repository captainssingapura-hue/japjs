package hue.captains.singapura.js.homing.studio.base;

/**
 * A {@link Doc} whose contents are provided directly — typically a Java text block —
 * rather than loaded from a classpath resource. No companion {@code .md} file.
 *
 * <p>Useful for changelog snippets, status notices, fixtures, and generated content. The
 * implementer is required to override {@link #contents()}; everything else inherits from
 * {@link Doc}'s defaults.</p>
 *
 * @since RFC 0004
 */
public interface InlineDoc extends Doc {

    @Override
    String contents();
}
