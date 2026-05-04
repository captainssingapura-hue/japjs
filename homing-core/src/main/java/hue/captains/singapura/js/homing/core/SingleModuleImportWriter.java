package hue.captains.singapura.js.homing.core;

import java.util.stream.Collectors;

public record SingleModuleImportWriter<M extends EsModule<M>>(
        M module, ModuleNameResolver resolver, String theme, String locale
) {

    public SingleModuleImportWriter(M module, ModuleNameResolver resolver) {
        this(module, resolver, null, null);
    }

    public String writeImports(ModuleImports<M> imports) {
        // RFC 0001 Step 11 fix: AppLink<?> entries are pure Java metadata for
        // nav generation — they don't correspond to JS-side exports. Multiple
        // targets all use the inner record name `link`, so emitting them as
        // `import { link } from "..."` would produce duplicate-identifier errors
        // in the browser. Filter them out here.
        var emittable = imports.allImports().stream()
                .filter(x -> !(x instanceof AppLink<?>))
                .toList();
        if (emittable.isEmpty()) return "";

        final String moduleName = resolver.resolve(imports.from())
                .withTheme(theme).withLocale(locale).basePath();
        return "import {"
                + emittable.stream().map(x -> x.getClass().getSimpleName()).collect(Collectors.joining(", "))
                + "} from \"" + moduleName + "\";";
    }
}
