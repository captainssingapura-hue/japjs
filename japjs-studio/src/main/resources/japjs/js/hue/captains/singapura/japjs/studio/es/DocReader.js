// =============================================================================
// japjs studio — Document Reader
// Fetches a markdown doc from /doc-content?path=..., renders with marked.js,
// builds a TOC sidebar from the document's headings.
// =============================================================================

function appMain(rootElement) {

    function cn() {
        var parts = [];
        for (var i = 0; i < arguments.length; i++) {
            if (arguments[i]) parts.push(css.className(arguments[i]));
        }
        return parts.join(" ");
    }

    function escape(s) {
        return String(s)
            .replace(/&/g, "&amp;").replace(/</g, "&lt;").replace(/>/g, "&gt;")
            .replace(/"/g, "&quot;").replace(/'/g, "&#39;");
    }

    function slugify(text) {
        return String(text)
            .toLowerCase()
            .replace(/[^\w\s-]/g, "")
            .replace(/\s+/g, "-")
            .replace(/-+/g, "-")
            .replace(/^-|-$/g, "");
    }

    // Read the doc path from query string
    var docPath = new URLSearchParams(window.location.search).get("path") || "";

    // Mirror of DocRegistry — used to display the title and category.
    // (Kept brief; the browser app has the full mirror.)
    var docMeta = (function() {
        var entries = {
            "whitepaper/japjs-whitepaper.md":                  {title:"japjs — Main White Paper",            category:"WHITEPAPER"},
            "whitepaper/japjs-shell-flexibility-whitepaper.md":{title:"Shell Flexibility — Exploration",     category:"WHITEPAPER"},
            "brochure/00-index.md":                            {title:"00 — Brochure Index",                 category:"BROCHURE"},
            "brochure/01-executive-summary.md":                {title:"01 — Executive Summary",              category:"BROCHURE"},
            "brochure/02-business-case.md":                    {title:"02 — Business Case",                  category:"BROCHURE"},
            "brochure/03-competitive-landscape.md":            {title:"03 — Competitive Landscape",          category:"BROCHURE"},
            "brochure/04-pilot-proposal.md":                   {title:"04 — Pilot Proposal",                 category:"BROCHURE"},
            "brochure/05-faq.md":                              {title:"05 — FAQ & Objection Handling",       category:"BROCHURE"},
            "brochure/06-architecture-at-a-glance.md":         {title:"06 — Architecture at a Glance",       category:"BROCHURE"},
            "rfcs/0001-app-registry-and-typed-nav.md":         {title:"RFC 0001 — App Registry & Typed Nav", category:"RFC"},
            "brand/README.md":                                 {title:"Brand Guide",                         category:"BRAND"},
            "brand/RENAME-TO-HOMING.md":                       {title:"Rename Dossier — japjs → Homing",     category:"BRAND"},
            "SESSION-SUMMARY-2026-04-25.md":                   {title:"Session Summary — 2026-04-25",        category:"SESSION"},
            "ACTION-PLAN-2026-04-25.md":                       {title:"Action Plan — 2026-04-25",            category:"SESSION"},
            "comparison/japjs-vs-react-vue.md":                {title:"japjs vs React / Vue",                category:"REFERENCE"},
            "user-guide.md":                                   {title:"User Guide",                          category:"REFERENCE"}
        };
        return entries[docPath] || {title: docPath || "(no document)", category: "REFERENCE"};
    })();

    function badgeClassFor(cat) {
        switch (cat) {
            case "WHITEPAPER": return st_badge_whitepaper;
            case "BROCHURE":   return st_badge_brochure;
            case "RFC":        return st_badge_rfc;
            case "BRAND":      return st_badge_brand;
            case "SESSION":    return st_badge_session;
            default:           return st_badge_reference;
        }
    }

    // ---- shell ----
    var shellHtml = ''
        + '<div class="' + cn(st_root) + '">'

        + '<div class="' + cn(st_header) + '">'
        + '  <a class="' + cn(st_brand) + '" href="/app?class=hue.captains.singapura.japjs.studio.es.StudioCatalogue">'
        + '    <span class="' + cn(st_brand_dot) + '"></span>'
        + '    <span class="' + cn(st_brand_word) + '">japjs · studio</span>'
        + '  </a>'
        + '  <div class="' + cn(st_breadcrumbs) + '">'
        + '    <a class="' + cn(st_crumb) + '" href="/app?class=hue.captains.singapura.japjs.studio.es.StudioCatalogue">Home</a>'
        + '    <span class="' + cn(st_crumb_sep) + '">/</span>'
        + '    <a class="' + cn(st_crumb) + '" href="/app?class=hue.captains.singapura.japjs.studio.es.DocBrowser">Documents</a>'
        + '    <span class="' + cn(st_crumb_sep) + '">/</span>'
        + '    <span class="' + cn(st_crumb) + '">' + escape(docMeta.title) + '</span>'
        + '  </div>'
        + '</div>'

        + '<div class="' + cn(st_main) + '">'

        + '  <div class="' + cn(st_doc_meta) + '">'
        + '    <span class="' + cn(st_badge) + ' ' + cn(badgeClassFor(docMeta.category)) + '">' + escape(docMeta.category) + '</span>'
        + '    <code style="font-size:11px;color:#64748B;">' + escape(docPath || "—") + '</code>'
        + '  </div>'

        + '  <div class="' + cn(st_layout) + '">'
        + '    <aside class="' + cn(st_sidebar) + '">'
        + '      <div class="' + cn(st_sidebar_title) + '">In this document</div>'
        + '      <nav id="docToc" class="' + cn(st_toc) + '"></nav>'
        + '    </aside>'
        + '    <article id="docBody" class="' + cn(st_doc) + '">'
        + '      <div class="' + cn(st_loading) + '">Loading…</div>'
        + '    </article>'
        + '  </div>'

        + '</div>'
        + '</div>';

    rootElement.innerHTML = shellHtml;

    var bodyEl = document.getElementById("docBody");
    var tocEl  = document.getElementById("docToc");

    if (!docPath) {
        bodyEl.innerHTML = '<div class="' + cn(st_error) + '">No document specified. Use <code>?path=…</code>.</div>';
        return;
    }

    // ---- fetch + render ----
    fetch("/doc-content?path=" + encodeURIComponent(docPath))
        .then(function(r) {
            if (!r.ok) throw new Error("HTTP " + r.status);
            return r.text();
        })
        .then(function(md) {
            renderDoc(md);
        })
        .catch(function(err) {
            bodyEl.innerHTML = '<div class="' + cn(st_error) + '">Failed to load <code>' + escape(docPath) + '</code>: ' + escape(err.message) + '</div>';
        });

    function renderDoc(md) {
        // marked: configure modest options
        if (marked && marked.use) {
            marked.use({ gfm: true, breaks: false });
        }

        var html = marked.parse(md);
        bodyEl.innerHTML = html;

        // Walk headings, assign ids, build TOC
        var headings = bodyEl.querySelectorAll("h1, h2, h3");
        var slugs = {};
        var tocItems = [];

        headings.forEach(function(h) {
            var text = h.textContent || "";
            var base = slugify(text) || "section";
            var slug = base;
            var n = 2;
            while (slugs[slug]) { slug = base + "-" + n; n++; }
            slugs[slug] = true;
            h.id = slug;
            tocItems.push({level: h.tagName.toLowerCase(), text: text, slug: slug});
        });

        if (tocItems.length === 0) {
            tocEl.innerHTML = '<div class="' + cn(st_loading) + '" style="padding:8px 12px;">No headings.</div>';
        } else {
            var tocHtml = '';
            for (var i = 0; i < tocItems.length; i++) {
                var item = tocItems[i];
                var levelCls = item.level === "h1" ? cn(st_toc_h1)
                              : item.level === "h2" ? cn(st_toc_h2)
                              : cn(st_toc_h3);
                tocHtml += '<a class="' + cn(st_toc_item) + ' ' + levelCls + '" href="#' + item.slug + '" data-slug="' + item.slug + '">' + escape(item.text) + '</a>';
            }
            tocEl.innerHTML = tocHtml;
        }

        // Active TOC tracking via IntersectionObserver
        if (typeof IntersectionObserver === "function" && tocItems.length > 0) {
            var tocLinks = tocEl.querySelectorAll("a[data-slug]");
            var bySlug = {};
            tocLinks.forEach(function(a) { bySlug[a.getAttribute("data-slug")] = a; });

            var observer = new IntersectionObserver(function(entries) {
                entries.forEach(function(e) {
                    if (e.isIntersecting) {
                        // clear all
                        tocLinks.forEach(function(a) { css.removeClass(a, st_toc_active); });
                        var link = bySlug[e.target.id];
                        if (link) css.addClass(link, st_toc_active);
                    }
                });
            }, { rootMargin: "0px 0px -70% 0px", threshold: 0 });

            headings.forEach(function(h) { observer.observe(h); });
        }
    }
}
