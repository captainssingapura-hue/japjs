package hue.captains.singapura.japjs.server;

import hue.captains.singapura.japjs.core.*;
import hue.captains.singapura.japjs.core.util.ReadContentFromResources;
import hue.captains.singapura.japjs.core.util.ResourceReader;
import hue.captains.singapura.japjs.core.util.SimpleImportsWriterResolver;
import hue.captains.singapura.japjs.core.util.SvgGroupContentProvider;
import hue.captains.singapura.tao.http.action.GetAction;
import hue.captains.singapura.tao.http.action.ParamMarshaller;
import io.vertx.ext.web.RoutingContext;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class EsModuleGetAction
        implements GetAction<RoutingContext, ModuleQuery, EmptyParam.NoHeaders, JsModuleContent> {

    private final ModuleNameResolver nameResolver;
    private final ResourceReader resourceReader;

    public EsModuleGetAction(ModuleNameResolver nameResolver) {
        this(nameResolver, ResourceReader.INSTANCE);
    }

    public EsModuleGetAction(ModuleNameResolver nameResolver, ResourceReader resourceReader) {
        this.nameResolver = nameResolver;
        this.resourceReader = resourceReader;
    }

    @Override
    public ParamMarshaller._QueryString<RoutingContext, ModuleQuery> queryStrMarshaller() {
        return ctx -> new ModuleQuery(
                ctx.request().getParam("class"),
                ctx.request().getParam("theme"),
                ctx.request().getParam("locale")
        );
    }

    @Override
    public ParamMarshaller._Header<RoutingContext, EmptyParam.NoHeaders> headerMarshaller() {
        return ctx -> new EmptyParam.NoHeaders();
    }

    @Override
    public CompletableFuture<JsModuleContent> execute(ModuleQuery query, EmptyParam.NoHeaders headers) {
        if (query.className() == null || query.className().isBlank()) {
            return CompletableFuture.failedFuture(
                    new IllegalArgumentException("Missing 'class' query parameter"));
        }
        try {
            EsModuleWriter<?> writer = createWriter(query.className(), query.theme(), query.locale());
            String js = String.join("\n", writer.writeModule());
            return CompletableFuture.completedFuture(new JsModuleContent(js));
        } catch (Exception e) {
            return CompletableFuture.failedFuture(e);
        }
    }

    @SuppressWarnings("unchecked")
    private <M extends EsModule<M>> EsModuleWriter<M> createWriter(
            String className, String theme, String locale
    ) throws Exception {
        Class<?> clazz = Class.forName(className);
        M module;
        try {
            var instanceField = clazz.getField("INSTANCE");
            module = (M) instanceField.get(null);
        } catch (NoSuchFieldException e) {
            module = (M) clazz.getDeclaredConstructor().newInstance();
        }

        ContentProvider<M> contentProvider;
        if (module instanceof SvgGroup) {
            @SuppressWarnings("rawtypes")
            SvgGroup svg = (SvgGroup) module;
            contentProvider = (ContentProvider<M>) new SvgGroupContentProvider<>(svg, resourceReader);
        } else if (module instanceof CssGroup) {
            @SuppressWarnings("rawtypes")
            CssGroup css = (CssGroup) module;
            contentProvider = (ContentProvider<M>) new CssGroupContentProvider<>(css, theme, nameResolver);
        } else {
            contentProvider = new ReadContentFromResources<>(module, theme, resourceReader);
        }

        if (module instanceof DomModule<?> dom && !dom.cssGroups().isEmpty()) {
            contentProvider = withCssManager(contentProvider);
        }

        return new EsModuleWriter<>(module, contentProvider, nameResolver,
                ExportWriter.INSTANCE, new SimpleImportsWriterResolver(nameResolver, theme, locale));
    }

    private <M extends EsModule<M>> ContentProvider<M> withCssManager(ContentProvider<M> delegate) {
        String managerPath = nameResolver.resolve(CssClassManager.INSTANCE).basePath();
        String importLine = "import { CssClassManagerInstance as css } from \"" + managerPath + "\";";
        return () -> {
            List<String> combined = new ArrayList<>();
            combined.add(importLine);
            combined.add("");
            combined.addAll(delegate.content());
            return combined;
        };
    }
}
