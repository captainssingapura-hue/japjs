package hue.captains.singapura.japjs.core;

import java.util.stream.Collectors;

public record SingleModuleImportWriter<M extends EsModule<M>>(
        M module, ModuleNameResolver resolver, String theme, String locale
) {

    public SingleModuleImportWriter(M module, ModuleNameResolver resolver) {
        this(module, resolver, null, null);
    }

    public String writeImports(ModuleImports<M> imports) {
        final String moduleName = resolver.resolve(imports.from())
                .withTheme(theme).withLocale(locale).basePath();
        return "import {"
                + imports.allImports().stream().map(x -> x.getClass().getSimpleName()).collect(Collectors.joining(", "))
                + "} from \"" + moduleName + "\";";
    }
}
