package hue.captains.singapura.japjs.core.util;

import hue.captains.singapura.japjs.core.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class SvgGroupContentProviderTest {

    record Animals() implements SvgGroup<Animals> {
        static final Animals INSTANCE = new Animals();
        record Cat() implements SvgBeing<Animals> {}
        record Dog() implements SvgBeing<Animals> {}

        @Override
        public List<SvgBeing<Animals>> svgBeings() {
            return List.of(new Cat(), new Dog());
        }

        @Override
        public ExportsOf<Animals> exports() {
            return new ExportsOf<>(INSTANCE, List.copyOf(svgBeings()));
        }
    }

    @Test
    void content_generatesConstDeclarationsForEachSvg(@TempDir Path tempDir) throws IOException {
        String dirPath = "japjs/svg/" + Animals.class.getCanonicalName().replace(".", "/");
        Path svgDir = tempDir.resolve(dirPath);
        Files.createDirectories(svgDir);
        Files.writeString(svgDir.resolve("Cat.svg"), "<svg>cat</svg>");
        Files.writeString(svgDir.resolve("Dog.svg"), "<svg>dog</svg>");

        var reader = new ResourceReader(tempDir);
        var provider = new SvgGroupContentProvider<>(Animals.INSTANCE, reader);
        var content = provider.content();

        assertTrue(content.contains("const Cat = `"));
        assertTrue(content.contains("<svg>cat</svg>"));
        assertTrue(content.contains("const Dog = `"));
        assertTrue(content.contains("<svg>dog</svg>"));
        assertEquals(2, content.stream().filter(l -> l.equals("`;")).count());
    }

    record SingleSvg() implements SvgGroup<SingleSvg> {
        static final SingleSvg INSTANCE = new SingleSvg();
        record Circle() implements SvgBeing<SingleSvg> {}

        @Override public List<SvgBeing<SingleSvg>> svgBeings() { return List.of(new Circle()); }
        @Override public ExportsOf<SingleSvg> exports() {
            return new ExportsOf<>(INSTANCE, List.copyOf(svgBeings()));
        }
    }

    @Test
    void content_singleSvg(@TempDir Path tempDir) throws IOException {
        String dirPath = "japjs/svg/" + SingleSvg.class.getCanonicalName().replace(".", "/");
        Path svgDir = tempDir.resolve(dirPath);
        Files.createDirectories(svgDir);
        Files.writeString(svgDir.resolve("Circle.svg"), "<circle r=\"5\"/>");

        var reader = new ResourceReader(tempDir);
        var provider = new SvgGroupContentProvider<>(SingleSvg.INSTANCE, reader);
        var content = provider.content();

        assertEquals(3, content.size());
        assertEquals("const Circle = `", content.get(0));
        assertEquals("<circle r=\"5\"/>", content.get(1));
        assertEquals("`;", content.get(2));
    }
}
