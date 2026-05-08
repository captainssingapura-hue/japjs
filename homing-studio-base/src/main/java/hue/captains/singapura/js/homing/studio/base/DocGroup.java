package hue.captains.singapura.js.homing.studio.base;

import hue.captains.singapura.js.homing.core.*;

import java.util.ArrayList;
import java.util.List;

/**
 * A logical grouping of {@link Doc} records. Each implementing class is an
 * {@link EsModule} so user JS can {@code import { MyDoc } from <DocGroup>} and
 * receive a typed reference to the document.
 *
 * <h2>Mirrors {@code CssGroup}, with one difference</h2>
 *
 * <p>{@code CssGroup} corresponds to one physical {@code .css} file (all classes
 * declared in it share that file). {@code DocGroup} is a <i>logical</i> grouping
 * of multiple {@code .md} files, one per declared {@link Doc}. The asymmetry
 * comes from the underlying assets: CSS naturally bundles, markdown doesn't.</p>
 *
 * <h2>Self-providing JS body — no separate ContentProvider</h2>
 *
 * <p>Implements {@link SelfContent} so the framework dispatches directly to
 * {@link #selfContent(ModuleNameResolver)} when serving the module. No external
 * ContentProvider class is involved.</p>
 *
 * <h2>Auto-injects DocManager into consumers</h2>
 *
 * <p>Implements {@link ManagerInjector} so consumers that import any Doc from a
 * DocGroup automatically receive {@code import { DocManagerInstance as docs } from "…"}
 * at the top of their served body. The consumer can then call
 * {@code docs.path(myDoc)}, {@code docs.url(myDoc)}, {@code docs.fetch(myDoc)}.</p>
 *
 * @param <D> self-type
 */
public interface DocGroup<D extends DocGroup<D>>
        extends EsModule<D>, SelfContent, ManagerInjector {

    /** Every doc declared in this group. The order is preserved for browsers / lists. */
    List<Doc<D>> docs();

    @Override
    default ImportsFor<D> imports() {
        return ImportsFor.noImports();
    }

    @SuppressWarnings("unchecked")
    @Override
    default ExportsOf<D> exports() {
        return new ExportsOf<>((D) this, List.copyOf(docs()));
    }

    // -------------------------------------------------------------------------
    // SelfContent — emit one `const X = _docs.doc(...)` per declared Doc.
    // ExportWriter appends the trailing `export {…}` block from exports().
    // -------------------------------------------------------------------------

    @Override
    default List<String> selfContent(ModuleNameResolver nameResolver) {
        String managerPath = nameResolver.resolve(DocManager.INSTANCE).basePath();
        List<String> lines = new ArrayList<>();
        lines.add("import { DocManagerInstance as _docs } from \"" + managerPath + "\";");
        for (Doc<D> doc : docs()) {
            // RFC 0002-ext2: contentType + fileExtension travel with the JS
            // handle so DocManager can dispatch viewers by kind (markdown
            // renderer for .md, raw <pre> for .txt, inline iframe for .html, etc.)
            lines.add("const " + doc.getClass().getSimpleName() + " = _docs.doc("
                    + jstr(doc.path())          + ", "
                    + jstr(doc.title())         + ", "
                    + jstr(doc.summary())       + ", "
                    + jstr(doc.category())      + ", "
                    + jstr(doc.contentType())   + ", "
                    + jstr(doc.fileExtension()) + ");");
        }
        return lines;
    }

    // -------------------------------------------------------------------------
    // ManagerInjector — auto-import DocManager into consumers.
    // -------------------------------------------------------------------------

    @Override default EsModule<?> manager()             { return DocManager.INSTANCE; }
    @Override default String      managerBindName()     { return "docs"; }
    @Override default String      managerExportName()   { return "DocManagerInstance"; }

    // -------------------------------------------------------------------------
    // Internal: RFC 8259 string escape (single-line; no newlines in metadata).
    // -------------------------------------------------------------------------

    private static String jstr(String v) {
        if (v == null) return "null";
        StringBuilder sb = new StringBuilder("\"");
        for (int i = 0; i < v.length(); i++) {
            char c = v.charAt(i);
            switch (c) {
                case '\\' -> sb.append("\\\\");
                case '"'  -> sb.append("\\\"");
                case '\n' -> sb.append("\\n");
                case '\r' -> sb.append("\\r");
                case '\t' -> sb.append("\\t");
                default -> {
                    if (c < 0x20) sb.append(String.format("\\u%04x", (int) c));
                    else sb.append(c);
                }
            }
        }
        sb.append("\"");
        return sb.toString();
    }
}
