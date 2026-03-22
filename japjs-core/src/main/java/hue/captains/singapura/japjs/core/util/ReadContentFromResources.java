package hue.captains.singapura.japjs.core.util;

import hue.captains.singapura.japjs.core.ContentProvider;
import hue.captains.singapura.japjs.core.EsModule;

import java.io.*;
import java.util.List;

public record ReadContentFromResources<M extends EsModule>(M module) implements ContentProvider<M> {

    @Override
    public List<String> content() {
        final String path = module.getClass().getCanonicalName().replace(".", "/") + ".js";
        try(InputStream in = module.getClass().getClassLoader().getResourceAsStream(path)){
            return new BufferedReader(new InputStreamReader(in)).lines().toList();
        } catch (IOException e) {
            throw new RuntimeException("Failed to load " + module, e);
        }
    }
}
