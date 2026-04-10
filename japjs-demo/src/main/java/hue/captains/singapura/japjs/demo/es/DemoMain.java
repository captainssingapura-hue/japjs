package hue.captains.singapura.japjs.demo.es;

import hue.captains.singapura.japjs.core.EsModule;
import hue.captains.singapura.japjs.core.EsModuleWriter;
import hue.captains.singapura.japjs.core.ExportWriter;
import hue.captains.singapura.japjs.core.ModuleNameResolver;
import hue.captains.singapura.japjs.core.util.ReadContentFromResources;
import hue.captains.singapura.japjs.core.util.SimpleImportsWriterResolver;
import hue.captains.singapura.japjs.core.util.SimplePrefixResolver;
import hue.captains.singapura.japjs.core.util.SvgGroupContentProvider;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

public class DemoMain{
    public static void main(String[] args) throws IOException {
        final Path rootPath = Path.of(args[0]);
        final ModuleNameResolver nameResolver = new SimplePrefixResolver("/test/");
        for(var m : new EsModule<?>[]{BobModule.INSTANCE, Alice.INSTANCE}){
            var writer = new EsModuleWriter(m, new ReadContentFromResources(m), nameResolver, ExportWriter.INSTANCE, new SimpleImportsWriterResolver(nameResolver));
            var outputFile = rootPath.resolve(nameResolver.resolve(m).basePath());
            Files.createDirectories(outputFile.getParent());
            Files.write(outputFile, writer.writeModule());
        }
        {
            var w = Wonderland.INSTANCE;
            var writer = new EsModuleWriter<>(w, new SvgGroupContentProvider<>(w), nameResolver, ExportWriter.INSTANCE, new SimpleImportsWriterResolver(nameResolver));
            var outputFile = rootPath.resolve(nameResolver.resolve(w).basePath());
            Files.createDirectories(outputFile.getParent());
            Files.write(outputFile, writer.writeModule());
        }
        try(var html = DemoMain.class.getResourceAsStream("index.html")){
            Files.copy(html, rootPath.resolve("index.html"), StandardCopyOption.REPLACE_EXISTING);
        }
    }
}
