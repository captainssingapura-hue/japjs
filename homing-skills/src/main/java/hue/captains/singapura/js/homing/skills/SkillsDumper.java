package hue.captains.singapura.js.homing.skills;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;

/**
 * Writes every shipped {@code SKILL.md} from the typed Doc records to a user-
 * chosen target directory. Per the Dual-Audience Skills doctrine the source
 * is the typed {@link hue.captains.singapura.js.homing.studio.base.Doc}'s
 * {@code contents()} method — single source of truth.
 *
 * <p>Static-content skills (most of them) implement {@code ClasspathMarkdownDoc}
 * whose default {@code contents()} reads UTF-8 bytes from a fixed classpath
 * path. Dynamic-content skills (the index TOC) override {@code contents()}
 * to build markdown at call time. The dumper doesn't care — both shapes
 * resolve to a string it writes as UTF-8 to disk.</p>
 */
public final class SkillsDumper {

    private final Path target;

    public SkillsDumper(Path target) {
        this.target = Objects.requireNonNull(target, "target");
    }

    /** Returns the number of skills written. */
    public int dump() throws IOException {
        Files.createDirectories(target);

        int count = 0;
        for (SkillsManifest.Entry entry : SkillsManifest.ALL) {
            String body = entry.doc().contents();
            if (body == null) {
                throw new IOException(
                        "Skill " + entry.slug() + " returned null contents() — "
                      + "either the classpath resource is missing or a dynamic "
                      + "contents() override misbehaved");
            }
            Path dest = target.resolve(entry.slug()).resolve("SKILL.md");
            Files.createDirectories(dest.getParent());
            Files.write(dest, body.getBytes(StandardCharsets.UTF_8));
            System.out.printf("  ✓ %-30s → %s%n", entry.slug(), dest);
            count++;
        }
        return count;
    }
}
