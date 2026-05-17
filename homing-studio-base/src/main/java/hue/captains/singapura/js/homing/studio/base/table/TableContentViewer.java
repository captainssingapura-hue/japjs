package hue.captains.singapura.js.homing.studio.base.table;

import hue.captains.singapura.js.homing.core.AppModule;
import hue.captains.singapura.js.homing.studio.base.app.ContentViewer;

/**
 * RFC 0020 — framework-default {@link ContentViewer} for {@link TableDoc}
 * (kind == {@code "table"}). Binds the table kind to {@link TableViewer}.
 *
 * <p>Registered by the framework's default {@code Fixtures.contentViewers()}
 * list.</p>
 *
 * @since RFC 0020
 */
public record TableContentViewer() implements ContentViewer {

    public static final TableContentViewer INSTANCE = new TableContentViewer();

    @Override public String kind() { return "table"; }

    @Override public AppModule<?, ?> app() { return TableViewer.INSTANCE; }

    @Override public String urlFor(String contentId) {
        return "/app?app=table-viewer&id=" + contentId;
    }

    @Override public String summary() {
        return "Typed table renderer (RFC 0020) — fetches the JSON envelope and builds the <table> with the framework's themed cell CSS. Slim: colspan/rowspan/badges/align; no sort/filter/edit.";
    }
}
