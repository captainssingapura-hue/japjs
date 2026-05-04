package hue.captains.singapura.js.homing.core.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Objects;

/**
 * Reads resource files from classpath or filesystem.
 * <p>
 * When {@code sourceRoot} is set, reads from the filesystem at {@code sourceRoot/path},
 * enabling live-reload during development without server restarts.
 * Set the system property {@code homing.devRoot} to activate, e.g.:
 * <pre>-Dhoming.devRoot=homing-demo/src/main/resources</pre>
 */
public record ResourceReader(Path sourceRoot) {
    public static final ResourceReader INSTANCE = new ResourceReader((Path) null);

    public static ResourceReader fromSystemProperty() {
        String devRoot = System.getProperty("homing.devRoot");
        if (devRoot != null && !devRoot.isBlank()) {
            return new ResourceReader(Path.of(devRoot));
        }
        return INSTANCE;
    }

    public List<String> getStringsFromResource(String path) {
        if (sourceRoot != null) {
            Path file = sourceRoot.resolve(path);
            if (Files.exists(file)) {
                try {
                    return Files.readAllLines(file);
                } catch (IOException e) {
                    throw new RuntimeException("Failed to read " + file, e);
                }
            }
        }
        try (InputStream in = this.getClass().getClassLoader().getResourceAsStream(path)) {
            Objects.requireNonNull(in, "cannot find " + path);
            return new BufferedReader(new InputStreamReader(in)).lines().toList();
        } catch (IOException e) {
            throw new RuntimeException("Failed to load " + path, e);
        }
    }
}
