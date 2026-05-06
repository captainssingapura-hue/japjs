// =============================================================================
// DocManager — runtime for typed Doc references.
//
// User code calls: docs.path(myDoc), docs.title(myDoc), docs.summary(myDoc),
// docs.category(myDoc), docs.url(myDoc), docs.fetch(myDoc).
//
// Doc records are frozen objects produced by `_docs.doc(path, title, summary,
// category)` from each DocGroup's auto-emitted module body. Internal field
// names are deliberately short ('_p','_t','_s','_c') to keep generated
// modules compact.
// =============================================================================

const DocManagerInstance = (() => {

    function doc(path, title, summary, category) {
        return Object.freeze({ _p: path, _t: title, _s: summary || "", _c: category || "" });
    }

    function resolve(d) {
        if (!d || typeof d._p !== "string") {
            throw new Error("Invalid Doc object: " + JSON.stringify(d));
        }
        return d;
    }

    return {
        doc,
        path(d)     { return resolve(d)._p; },
        title(d)    { return resolve(d)._t; },
        summary(d)  { return resolve(d)._s; },
        category(d) { return resolve(d)._c; },

        // Build the public reader URL for a doc.
        url(d) {
            return "/app?app=doc-reader&path=" + encodeURIComponent(resolve(d)._p);
        },

        // Fetch the markdown body via DocGetAction (registered by studio-base).
        // Returns Promise<string>.
        async fetch(d) {
            const r = resolve(d);
            const resp = await window.fetch("/doc?path=" + encodeURIComponent(r._p));
            if (!resp.ok) throw new Error("Failed to load doc " + r._p + ": " + resp.status);
            return await resp.text();
        }
    };
})();
