// =============================================================================
// Homing framework — href manager (RFC 0001 §4.0.1, Step 09)
//
// Auto-injected as `href` into any DomModule that imports an AppLink<?>.
// Sole sanctioned API for constructing or applying URLs in user JS.
//
// User code MUST use these six methods exclusively. Step 10 ships the
// conformance scanner that enforces it.
// =============================================================================

const HrefManagerInstance = (() => {

    function _str(link, where) {
        if (typeof link !== "string") {
            throw new TypeError("href." + where + ": link must be a URL string (got " + typeof link + ")");
        }
        return link;
    }

    function _attrEscape(s) {
        return String(s).replace(/&/g, "&amp;").replace(/"/g, "&quot;");
    }

    /** Returns an `href="..."` attribute fragment for safe inclusion in innerHTML. */
    function toAttr(link) {
        return 'href="' + _attrEscape(_str(link, "toAttr")) + '"';
    }

    /** Sets `el.href` to the link. Returns el for chaining. */
    function set(el, link) {
        if (!el) throw new Error("href.set: element required");
        el.setAttribute("href", _str(link, "set"));
        return el;
    }

    /** Creates a new <a> element with href set. opts: { text, className, target, rel, id }. */
    function create(link, opts) {
        const a = document.createElement("a");
        a.setAttribute("href", _str(link, "create"));
        if (opts) {
            if (opts.text != null)      a.textContent = opts.text;
            if (opts.className != null) a.className = opts.className;
            if (opts.target != null)    a.setAttribute("target", opts.target);
            if (opts.rel != null)       a.setAttribute("rel", opts.rel);
            if (opts.id != null)        a.id = opts.id;
        }
        return a;
    }

    /**
     * Opens the link in a new tab/window. opts: { windowFeatures, name }.
     * Returns the WindowProxy from window.open (may be null if blocked).
     */
    function openNew(link, opts) {
        const url = _str(link, "openNew");
        const target = (opts && opts.name) || "_blank";
        const features = opts && opts.windowFeatures;
        return window.open(url, target, features);
    }

    /**
     * Programmatic navigation. opts: { replace: true } to replace current entry
     * (no back-button entry); default is push (assign).
     */
    function navigate(link, opts) {
        const url = _str(link, "navigate");
        if (opts && opts.replace) {
            window.location.replace(url);
        } else {
            window.location.assign(url);
        }
    }

    /** `href="#<slug>"` for same-page anchors. Slug must be a string. */
    function fragment(slug) {
        if (typeof slug !== "string") {
            throw new TypeError("href.fragment: slug must be a string (got " + typeof slug + ")");
        }
        return 'href="#' + _attrEscape(slug) + '"';
    }

    return Object.freeze({
        toAttr,
        set,
        create,
        openNew,
        navigate,
        fragment,
    });
})();
