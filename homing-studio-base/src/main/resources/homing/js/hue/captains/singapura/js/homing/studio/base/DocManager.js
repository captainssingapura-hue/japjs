// =============================================================================
// DocManager — runtime for typed Doc references.
//
// User code calls: docs.path(myDoc), docs.title(myDoc), docs.summary(myDoc),
// docs.category(myDoc), docs.contentType(myDoc), docs.fileExtension(myDoc),
// docs.url(myDoc), docs.fetch(myDoc).
//
// Doc records are frozen objects produced by
//   _docs.doc(path, title, summary, category, contentType, fileExtension)
// from each DocGroup's auto-emitted module body. Internal field names are
// deliberately short ('_p','_t','_s','_c','_m','_e') to keep generated modules
// compact.
//
// RFC 0002-ext2: contentType + fileExtension travel with each Doc so consumers
// can dispatch viewers by kind (markdown render vs raw <pre> vs inline iframe).
// =============================================================================

const DocManagerInstance = (() => {

    function doc(path, title, summary, category, contentType, fileExtension) {
        return Object.freeze({
            _p: path,
            _t: title,
            _s: summary || "",
            _c: category || "",
            _m: contentType   || "text/markdown; charset=utf-8",
            _e: fileExtension || ".md"
        });
    }

    function resolve(d) {
        if (!d || typeof d._p !== "string") {
            throw new Error("Invalid Doc object: " + JSON.stringify(d));
        }
        return d;
    }

    return {
        doc,
        path(d)          { return resolve(d)._p; },
        title(d)         { return resolve(d)._t; },
        summary(d)       { return resolve(d)._s; },
        category(d)      { return resolve(d)._c; },
        contentType(d)   { return resolve(d)._m; },
        fileExtension(d) { return resolve(d)._e; },

        // Build the public reader URL for a doc.
        url(d) {
            return "/app?app=doc-reader&path=" + encodeURIComponent(resolve(d)._p);
        },

        // Fetch the doc body via DocGetAction (registered by studio-base).
        // Returns Promise<string> — works for any text-based doc kind.
        async fetch(d) {
            const r = resolve(d);
            const resp = await window.fetch("/doc?path=" + encodeURIComponent(r._p));
            if (!resp.ok) throw new Error("Failed to load doc " + r._p + ": " + resp.status);
            return await resp.text();
        }
    };
})();
