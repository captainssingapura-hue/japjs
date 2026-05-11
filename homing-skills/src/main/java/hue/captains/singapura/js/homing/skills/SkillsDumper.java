package hue.captains.singapura.js.homing.skills;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;

/**
 * Writes every shipped {@code SKILL.md} from the jar's classpath to a user-
 * chosen target directory, byte-identical to the source. Per the Dual-Audience
 * Skills doctrine the source is read from the same classpath path the studio
 * mode reads — single source of truth, no transformation.
 */
public final class SkillsDumper {

    private final Path target;

    public SkillsDumper(Path target) {
        this.target = Objects.requireNonNull(target, "target");
    }

    /** Returns the number of skills written. */
    public int dump() throws IOException {
        Files.createDirectories(target);
        ClassLoader cl = SkillsDumper.class.getClassLoader();

        int count = 0;
        for (SkillsManifest.Entry entry : SkillsManifest.ALL) {
            String classpath = entry.classpathPath();
            try (InputStream in = cl.getResourceAsStream(classpath)) {
                if (in == null) {
                    throw new IOException(
                            "Skill resource missing on classpath: " + classpath
                          + " (referenced by " + entry.slug() + ")");
                }
                Path dest = target.resolve(entry.slug()).resolve("SKILL.md");
                Files.createDirectories(dest.getParent());
                Files.write(dest, in.readAllBytes());
                System.out.printf("  ✓ %-30s → %s%n", entry.slug(), dest);
                count++;
            }
        }
        return count;
    }
}
