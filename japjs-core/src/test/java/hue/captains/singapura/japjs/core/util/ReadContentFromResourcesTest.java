package hue.captains.singapura.japjs.core.util;

import hue.captains.singapura.japjs.core.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ReadContentFromResourcesTest {

    record TestMod() implements EsModule<TestMod> {
        static final TestMod INSTANCE = new TestMod();
        record x() implements Exportable._Constant<TestMod> {}

        @Override public ImportsFor<TestMod> imports() { return ImportsFor.noImports(); }
        @Override public ExportsOf<TestMod> exports() {
            return new ExportsOf<>(INSTANCE, List.of(new x()));
        }
    }

    @Test
    void content_readsDefaultFile(@TempDir Path tempDir) throws IOException {
        // Create the expected resource path
        String basePath = "japjs/js/" + TestMod.class.getCanonicalName().replace(".", "/");
        Path jsFile = tempDir.resolve(basePath + ".js");
        Files.createDirectories(jsFile.getParent());
        Files.writeString(jsFile, "const x = 42;");

        var reader = new ResourceReader(tempDir);
        var provider = new ReadContentFromResources<>(TestMod.INSTANCE, null, reader);
        var content = provider.content();

        assertEquals(List.of("const x = 42;"), content);
    }

    @Test
    void content_readsThemedFile(@TempDir Path tempDir) throws IOException {
        String basePath = "japjs/js/" + TestMod.class.getCanonicalName().replace(".", "/");
        Path defaultFile = tempDir.resolve(basePath + ".js");
        Path darkFile = tempDir.resolve(basePath + ".dark.js");
        Files.createDirectories(defaultFile.getParent());
        Files.writeString(defaultFile, "const x = 'light';");
        Files.writeString(darkFile, "const x = 'dark';");

        var reader = new ResourceReader(tempDir);
        var provider = new ReadContentFromResources<>(TestMod.INSTANCE, "dark", reader);
        var content = provider.content();

        assertEquals(List.of("const x = 'dark';"), content);
    }

    @Test
    void content_fallsBackToDefaultWhenThemeNotFound(@TempDir Path tempDir) throws IOException {
        String basePath = "japjs/js/" + TestMod.class.getCanonicalName().replace(".", "/");
        Path defaultFile = tempDir.resolve(basePath + ".js");
        Files.createDirectories(defaultFile.getParent());
        Files.writeString(defaultFile, "const x = 'default';");

        var reader = new ResourceReader(tempDir);
        var provider = new ReadContentFromResources<>(TestMod.INSTANCE, "nonexistent", reader);
        var content = provider.content();

        assertEquals(List.of("const x = 'default';"), content);
    }
}
