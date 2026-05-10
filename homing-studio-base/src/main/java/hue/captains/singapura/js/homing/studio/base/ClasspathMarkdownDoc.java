package hue.captains.singapura.js.homing.studio.base;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;

/**
 * A markdown {@link Doc} whose {@code .md} file lives next to its record class on the
 * classpath, under the conventional {@code docs/} resource prefix.
 *
 * <p>The default {@link #resourcePath()} is derived from the record's fully-qualified
 * class name. A Doc record at:</p>
 *
 * <pre>com.example.studio.docs.blocks.AtomsDoc</pre>
 *
 * <p>expects its companion file at:</p>
 *
 * <pre>resources/docs/com/example/studio/docs/blocks/AtomsDoc.md</pre>
 *
 * <p>Renaming or moving the record renames or moves the file in lock-step (most IDEs offer
 * to refactor the matching resource when the resource sits in the conventional layout).</p>
 *
 * <p>Override {@link #resourcePath()} only when the convention doesn't fit; for that case,
 * {@link ResourceMarkdownDoc} is usually the right interface to implement instead — it makes
 * the explicit-path nature visible at the type level.</p>
 *
 * @since RFC 0004
 */
public interface ClasspathMarkdownDoc extends Doc {

    /**
     * Default classpath path for this Doc's bytes: {@code docs/<package-as-path>/<SimpleName>.md}.
     * Override only if the file genuinely cannot live in the conventional location.
     */
    default String resourcePath() {
        return "docs/" + getClass().getName().replace('.', '/') + fileExtension();
    }

    @Override
    default String contents() {
        String path = resourcePath();
        ClassLoader loader = getClass().getClassLoader();
        try (var in = loader.getResourceAsStream(path)) {
            if (in == null) {
                throw new IllegalStateException(
                        "Doc " + getClass().getName() + " missing classpath resource: " + path);
            }
            return new String(in.readAllBytes(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new UncheckedIOException("Failed to read " + path, e);
        }
    }
}
