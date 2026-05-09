// =============================================================================
// DocReaderRenderer — shared renderer for DocReader.
//
// renderDocReader({ docPath, brand, crumbsAbove }) → Node
//
// Fetches /doc?path=<docPath>, parses with marked.js, installs via
// Range.createContextualFragment (no innerHTML write), walks headings to
// build a TOC sidebar with IntersectionObserver scroll-spy.
// =============================================================================

var href = HrefManagerInstance;

function _slugify(text) {
    return String(text)
        .toLowerCase()
        .replace(/[^\w\s-]/g, "")
        .replace(/\s+/g, "-")
        .replace(/-+/g, "-")
        .replace(/^-|-$/g, "");
}

function _collectHeadings(rootEl) {
    var out = [];
    function visit(el) {
        if (!el || el.nodeType !== 1) return;
        var tag = el.tagName;
        if (tag === "H1" || tag === "H2" || tag === "H3") out.push(el);
        var children = el.children;
        for (var i = 0; i < children.length; i++) visit(children[i]);
    }
    for (var i = 0; i < rootEl.children.length; i++) visit(rootEl.children[i]);
    return out;
}

function renderDocReader(props) {
    var docPath     = props.docPath;
    var brand       = props.brand;
    var crumbsAbove = props.crumbsAbove || [];   // breadcrumbs preceding the doc path crumb

    var pathTitle = docPath ? docPath.split("/").pop() : "(no document)";

    var root = document.createElement("div");
    css.addClass(root, st_root);

    var crumbs = [];
    for (var i = 0; i < crumbsAbove.length; i++) crumbs.push(crumbsAbove[i]);
    crumbs.push({ text: pathTitle });

    root.appendChild(Header({ brand: brand, crumbs: crumbs }));

    var main = document.createElement("div");
    css.addClass(main, st_main);

    var meta = document.createElement("div");
    css.addClass(meta, st_doc_meta);
    var pathCode = document.createElement("code");
    pathCode.style.cssText = "font-size:11px; color: var(--st-gray-mid);";
    pathCode.textContent = docPath || "—";
    meta.appendChild(pathCode);
    main.appendChild(meta);

    var layout = document.createElement("div");
    css.addClass(layout, st_layout);

    var sidebar = document.createElement("aside");
    css.addClass(sidebar, st_sidebar);
    var sidebarTitle = document.createElement("div");
    css.addClass(sidebarTitle, st_sidebar_title);
    sidebarTitle.textContent = "In this document";
    var tocEl = document.createElement("nav");
    css.addClass(tocEl, st_toc);
    sidebar.appendChild(sidebarTitle);
    sidebar.appendChild(tocEl);

    var bodyEl = document.createElement("article");
    css.addClass(bodyEl, st_doc);
    var loading = document.createElement("div");
    css.addClass(loading, st_loading);
    loading.textContent = "Loading…";
    bodyEl.appendChild(loading);

    layout.appendChild(sidebar);
    layout.appendChild(bodyEl);
    main.appendChild(layout);

    root.appendChild(main);

    if (!docPath) {
        var errMsg = document.createElement("div");
        css.addClass(errMsg, st_error);
        errMsg.appendChild(document.createTextNode("No document specified. Use "));
        var errCode = document.createElement("code"); errCode.textContent = "?path=…";
        errMsg.appendChild(errCode);
        errMsg.appendChild(document.createTextNode("."));
        bodyEl.replaceChildren(errMsg);
        return root;
    }

    fetch("/doc?path=" + encodeURIComponent(docPath))
        .then(function(r) {
            if (!r.ok) throw new Error("HTTP " + r.status);
            return r.text();
        })
        .then(function(md) { _renderDoc(md, bodyEl, tocEl); })
        .catch(function(err) {
            var errEl = document.createElement("div");
            css.addClass(errEl, st_error);
            errEl.appendChild(document.createTextNode("Failed to load "));
            var c = document.createElement("code"); c.textContent = docPath;
            errEl.appendChild(c);
            errEl.appendChild(document.createTextNode(": " + err.message));
            bodyEl.replaceChildren(errEl);
        });

    return root;
}

function _renderDoc(md, bodyEl, tocEl) {
    if (marked && marked.use) marked.use({ gfm: true, breaks: false });

    var range = document.createRange();
    range.selectNodeContents(bodyEl);
    var fragment = range.createContextualFragment(marked.parse(md));
    bodyEl.replaceChildren(fragment);

    var headings = _collectHeadings(bodyEl);
    var slugs = {};
    var tocItems = [];

    for (var hi = 0; hi < headings.length; hi++) {
        var h = headings[hi];
        var text = h.textContent || "";
        var base = _slugify(text) || "section";
        var slug = base;
        var n = 2;
        while (slugs[slug]) { slug = base + "-" + n; n++; }
        slugs[slug] = true;
        h.id = slug;
        tocItems.push({ level: h.tagName.toLowerCase(), text: text, slug: slug });
    }

    if (tocItems.length === 0) {
        var empty = document.createElement("div");
        css.addClass(empty, st_loading);
        empty.style.cssText = "padding:8px 12px;";
        empty.textContent = "No headings.";
        tocEl.replaceChildren(empty);
        return;
    }

    var tocLinks = [];
    for (var i = 0; i < tocItems.length; i++) {
        var item = tocItems[i];
        var levelCls = item.level === "h1" ? st_toc_h1
                      : item.level === "h2" ? st_toc_h2
                      : st_toc_h3;
        var a = document.createElement("a");
        css.addClass(a, st_toc_item);
        css.addClass(a, levelCls);
        href.set(a, "#" + item.slug);
        a.setAttribute("data-slug", item.slug);
        a.textContent = item.text;
        tocLinks.push(a);
    }
    tocEl.replaceChildren.apply(tocEl, tocLinks);

    if (typeof IntersectionObserver === "function") {
        var bySlug = {};
        for (var li = 0; li < tocLinks.length; li++) {
            bySlug[tocLinks[li].getAttribute("data-slug")] = tocLinks[li];
        }
        var observer = new IntersectionObserver(function(entries) {
            entries.forEach(function(e) {
                if (e.isIntersecting) {
                    for (var li = 0; li < tocLinks.length; li++) {
                        css.removeClass(tocLinks[li], st_toc_active);
                    }
                    var link = bySlug[e.target.id];
                    if (link) css.addClass(link, st_toc_active);
                }
            });
        }, { rootMargin: "0px 0px -70% 0px", threshold: 0 });
        for (var hi2 = 0; hi2 < headings.length; hi2++) observer.observe(headings[hi2]);
    }
}
