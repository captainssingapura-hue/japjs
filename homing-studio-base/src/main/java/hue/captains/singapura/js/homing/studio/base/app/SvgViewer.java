package hue.captains.singapura.js.homing.studio.base.app;

import hue.captains.singapura.js.homing.core.AppLink;
import hue.captains.singapura.js.homing.core.AppModule;
import hue.captains.singapura.js.homing.core.Importable;
import hue.captains.singapura.js.homing.core.ModuleImports;

import java.util.List;

/**
 * RFC 0015 Phase 5 + RFC 0016 — viewer AppModule for SVG content Docs.
 * URL contract:
 *
 * <pre>/app?app=svg-viewer&id=&lt;svgdoc-uuid&gt;</pre>
 *
 * <p>Extends {@link DocViewer} so the framework's standard chrome
 * (Header + brand + breadcrumb + theme picker + audio runtime + root
 * layout) is composed automatically. The {@link DocViewer} base's
 * {@code selfContent} and {@code imports} are {@code final}; this class
 * supplies only the kind-specific bits: the body JS that populates
 * {@code main} with the inline SVG, plus the typed appMain / params /
 * simpleName declarations.</p>
 *
 * <p>The body fetches {@code /doc?id=<uuid>} (the SvgDoc's body — raw
 * SVG markup served by the polymorphic doc viewer) and inlines it
 * centered in {@code main}. The browser renders inline SVG natively;
 * no SVG library needed.</p>
 *
 * @since RFC 0016
 */
public final class SvgViewer extends DocViewer<SvgViewer.Params, SvgViewer> {

    public static final SvgViewer INSTANCE = new SvgViewer();

    private SvgViewer() {}  // singleton via INSTANCE

    /** @param id UUID of the SvgDoc to render (resolved via the doc registry). */
    public record Params(String id) implements AppModule._Param {}

    private record appMain() implements AppModule._AppMain<Params, SvgViewer> {}
    public record link() implements AppLink<SvgViewer> {}

    @Override public String simpleName() { return "svg-viewer"; }
    @Override public Class<Params> paramsType() { return Params.class; }
    @Override public String title() { return "svg"; }

    @Override
    protected AppModule._AppMain<Params, SvgViewer> appMain() {
        return new appMain();
    }

    @Override
    protected List<ModuleImports<? extends Importable>> bodyImports() {
        // Framework chrome covers st_root / st_main / st_loading / st_error
        // — body uses only those.
        return List.of();
    }

    @Override
    protected List<String> bodyJs() {
        return List.of(
                "    main.style.cssText = 'display:flex;align-items:center;justify-content:center;min-height:60vh;padding:40px;';",
                "    var loading = document.createElement('div');",
                "    css.addClass(loading, st_loading);",
                "    loading.textContent = 'Loading\\u2026';",
                "    main.appendChild(loading);",
                "",
                "    if (!params.id) {",
                "        var errMsg = document.createElement('div');",
                "        css.addClass(errMsg, st_error);",
                "        errMsg.textContent = 'No SVG id supplied. Use ?id=<uuid>.';",
                "        main.replaceChildren(errMsg);",
                "        return;",
                "    }",
                "",
                "    fetch('/doc?id=' + encodeURIComponent(params.id))",
                "        .then(function(r){",
                "            if (!r.ok) throw new Error('HTTP ' + r.status);",
                "            return r.text();",
                "        })",
                "        .then(function(svg){",
                "            var host = document.createElement('div');",
                "            host.style.cssText = 'max-width:80vw;max-height:80vh;display:flex;';",
                "            var range = document.createRange();",
                "            range.selectNodeContents(host);",
                "            host.appendChild(range.createContextualFragment(svg));",
                "            var svgEl = host.querySelector('svg');",
                "            if (svgEl) {",
                "                svgEl.style.width = '100%';",
                "                svgEl.style.height = '100%';",
                "                svgEl.style.maxWidth = '600px';",
                "                svgEl.style.maxHeight = '600px';",
                "            }",
                "            main.replaceChildren(host);",
                "        })",
                "        .catch(function(err){",
                "            var errEl = document.createElement('div');",
                "            css.addClass(errEl, st_error);",
                "            errEl.textContent = 'Failed to load SVG: ' + err.message;",
                "            main.replaceChildren(errEl);",
                "        });"
        );
    }
}
