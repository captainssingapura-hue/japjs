package hue.captains.singapura.js.homing.core.util;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

class ResourceReaderTest {

    @Test
    void getStringsFromResource_readsFromFilesystem(@TempDir Path tempDir) throws IOException {
        Path file = tempDir.resolve("test/hello.txt");
        Files.createDirectories(file.getParent());
        Files.writeString(file, "line1\nline2\nline3");

        var reader = new ResourceReader(tempDir);
        var lines = reader.getStringsFromResource("test/hello.txt");

        assertEquals(3, lines.size());
        assertEquals("line1", lines.get(0));
        assertEquals("line2", lines.get(1));
        assertEquals("line3", lines.get(2));
    }

    @Test
    void getStringsFromResource_throwsForMissingFile() {
        var reader = new ResourceReader((Path) null);
        assertThrows(NullPointerException.class, () ->
                reader.getStringsFromResource("nonexistent/file.txt")
        );
    }

    @Test
    void getStringsFromResource_prefersFilesystemOverClasspath(@TempDir Path tempDir) throws IOException {
        // Create a file on filesystem that shadows any classpath resource
        Path file = tempDir.resolve("shadow.txt");
        Files.writeString(file, "from-filesystem");

        var reader = new ResourceReader(tempDir);
        var lines = reader.getStringsFromResource("shadow.txt");

        assertEquals(1, lines.size());
        assertEquals("from-filesystem", lines.getFirst());
    }

    @Test
    void fromSystemProperty_returnsDefaultWhenNotSet() {
        // System property japjs.devRoot should not be set in test env
        String prev = System.getProperty("homing.devRoot");
        try {
            System.clearProperty("homing.devRoot");
            var reader = ResourceReader.fromSystemProperty();
            assertNull(reader.sourceRoot());
        } finally {
            if (prev != null) System.setProperty("homing.devRoot", prev);
        }
    }
}
