// =============================================================================
// StudioGraphInspectorRenderer — RFC 0014 frontend renderer for StudioGraphInspector.
//
// renderStudioGraphInspector() → Node
//
// Reads optional ?root=<class-FQN> from the page URL, fetches /graph-md (or
// /graph-md?root=<fqn>), parses the resulting markdown with the bundled
// marked.js and inserts the HTML into the page. Pure front-end render —
// the only server data is the raw markdown document.
//
// Mirrors the DocReaderRenderer fetch-and-render pattern; intentionally
// simpler — no TOC, no scroll-spy, no references section.
// =============================================================================

function renderStudioGraphInspector() {
    var root = document.createElement("div");
    css.addClass(root, st_root);

    var main = document.createElement("div");
    css.addClass(main, st_main);

    var bodyEl = document.createElement("article");
    css.addClass(bodyEl, st_doc);

    var loading = document.createElement("div");
    css.addClass(loading, st_loading);
    loading.textContent = "Loading graph…";
    bodyEl.appendChild(loading);

    main.appendChild(bodyEl);
    root.appendChild(main);

    // Read optional ?root=<fqn> and ?view=<name> from the page URL — passed
    // straight through to /graph-md so the server-side action picks the
    // tree dump (default) or the type-only table (view=types).
    var url = "/graph-md";
    try {
        var qs = new URLSearchParams(window.location.search);
        var subroot = qs.get("root");
        var view = qs.get("view");
        var parts = [];
        if (subroot && subroot.length > 0) parts.push("root=" + encodeURIComponent(subroot));
        if (view    && view.length    > 0) parts.push("view=" + encodeURIComponent(view));
        if (parts.length > 0) url = "/graph-md?" + parts.join("&");
    } catch (e) { /* URLSearchParams unavailable — fetch the default root. */ }

    fetch(url)
        .then(function(r) {
            if (!r.ok) throw new Error("HTTP " + r.status);
            return r.text();
        })
        .then(function(md) {
            if (marked && marked.use) marked.use({ gfm: true, breaks: false });
            var range = document.createRange();
            range.selectNodeContents(bodyEl);
            var fragment = range.createContextualFragment(marked.parse(md));
            bodyEl.replaceChildren(fragment);
        })
        .catch(function(err) {
            var errEl = document.createElement("div");
            css.addClass(errEl, st_error);
            errEl.appendChild(document.createTextNode("Failed to load graph: " + err.message));
            bodyEl.replaceChildren(errEl);
        });

    return root;
}
