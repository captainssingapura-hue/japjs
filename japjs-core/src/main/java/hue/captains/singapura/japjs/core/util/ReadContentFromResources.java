package hue.captains.singapura.japjs.core.util;

import hue.captains.singapura.japjs.core.ContentProvider;
import hue.captains.singapura.japjs.core.EsModule;

import java.util.List;

public record ReadContentFromResources<M extends EsModule>(M module, String theme, ResourceReader resourceReader) implements ContentProvider<M> {

    public ReadContentFromResources(M module) {
        this(module, null, ResourceReader.INSTANCE);
    }

    public ReadContentFromResources(M module, ResourceReader resourceReader) {
        this(module, null, resourceReader);
    }

    @Override
    public List<String> content() {
        final String basePath = "japjs/js/" + module.getClass().getCanonicalName().replace(".", "/");
        if (theme != null) {
            try {
                return resourceReader.getStringsFromResource(basePath + "." + theme + ".js");
            } catch (Exception e) {
                // Fall back to default if themed variant not found
            }
        }
        return resourceReader.getStringsFromResource(basePath + ".js");
    }
}
