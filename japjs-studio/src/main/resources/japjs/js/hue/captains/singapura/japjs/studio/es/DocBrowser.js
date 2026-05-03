// =============================================================================
// japjs studio — Document Browser
// Lists every document the studio knows about, with search and category filter.
// Document registry comes from server-side metadata fetched at boot.
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

    // Hardcoded mirror of DocRegistry.java for v0. A future iteration can serve
    // this as JSON from a /doc-list endpoint to avoid duplication.
    var docs = [
        {path:"whitepaper/japjs-whitepaper.md",                      title:"japjs — Main White Paper",            summary:"The full technical design: four-layer architecture, diagrams, positioning.",        category:"WHITEPAPER", catLabel:"White Papers"},
        {path:"whitepaper/japjs-shell-flexibility-whitepaper.md",    title:"Shell Flexibility — Exploration",     summary:"japjs's output is shell-agnostic. The Java backend with CLI parity is the killer feature.", category:"WHITEPAPER", catLabel:"White Papers"},
        {path:"brochure/00-index.md",                                title:"00 — Brochure Index",                  summary:"Cover sheet with reading-time tracks (3 / 10 / 20 / 45 min).",                       category:"BROCHURE",   catLabel:"Brochure"},
        {path:"brochure/01-executive-summary.md",                    title:"01 — Executive Summary",               summary:"One-page pitch with the built/designed capability table.",                          category:"BROCHURE",   catLabel:"Brochure"},
        {path:"brochure/02-business-case.md",                        title:"02 — Business Case",                   summary:"Costs, target workloads, risks.",                                                    category:"BROCHURE",   catLabel:"Brochure"},
        {path:"brochure/03-competitive-landscape.md",                title:"03 — Competitive Landscape",           summary:"Side-by-side vs. React, Vaadin, Hilla, JHipster, htmx.",                            category:"BROCHURE",   catLabel:"Brochure"},
        {path:"brochure/04-pilot-proposal.md",                       title:"04 — Pilot Proposal",                  summary:"4–6 weeks, 4 success metrics, weekly milestones, decision gates.",                  category:"BROCHURE",   catLabel:"Brochure"},
        {path:"brochure/05-faq.md",                                  title:"05 — FAQ & Objection Handling",        summary:"25 honest Q&As across strategy, risk, tech, people, cost.",                         category:"BROCHURE",   catLabel:"Brochure"},
        {path:"brochure/06-architecture-at-a-glance.md",             title:"06 — Architecture at a Glance",        summary:"Visual summary with built-vs-designed status table.",                               category:"BROCHURE",   catLabel:"Brochure"},
        {path:"rfcs/0001-app-registry-and-typed-nav.md",             title:"RFC 0001 — App Registry & Typed Nav",  summary:"Friendly-name URL contract, AppLink<L>, ProxyApp, conformance enforcement.",        category:"RFC",        catLabel:"RFCs"},
        {path:"brand/README.md",                                     title:"Brand Guide",                          summary:"Logo concept, asset inventory, palette, typography, usage rules.",                  category:"BRAND",      catLabel:"Brand"},
        {path:"brand/RENAME-TO-HOMING.md",                           title:"Rename Dossier — japjs → Homing",       summary:"Decision context, three-layer metaphor, migration logistics.",                      category:"BRAND",      catLabel:"Brand"},
        {path:"SESSION-SUMMARY-2026-04-25.md",                       title:"Session Summary — 2026-04-25",         summary:"Comprehensive recap of the design session that built the brochure suite.",          category:"SESSION",    catLabel:"Session Notes"},
        {path:"ACTION-PLAN-2026-04-25.md",                           title:"Action Plan — 2026-04-25",             summary:"Phase-by-phase execution plan with decision gates and risk register.",              category:"SESSION",    catLabel:"Session Notes"},
        {path:"comparison/japjs-vs-react-vue.md",                    title:"japjs vs React / Vue",                  summary:"Honest comparison, fair assessment of strengths and gaps.",                          category:"REFERENCE",  catLabel:"Reference"},
        {path:"user-guide.md",                                        title:"User Guide",                            summary:"Getting-started reference for framework users.",                                     category:"REFERENCE",  catLabel:"Reference"}
    ];

    // Distinct categories in the order they appear
    var categories = [];
    var seen = {};
    for (var i = 0; i < docs.length; i++) {
        if (!seen[docs[i].category]) {
            seen[docs[i].category] = true;
            categories.push({key: docs[i].category, label: docs[i].catLabel});
        }
    }

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

    function readerUrl(path) {
        return "/app?class=hue.captains.singapura.japjs.studio.es.DocReader&path=" + encodeURIComponent(path);
    }

    // ---- shell render ----
    var shellHtml = ''
        + '<div class="' + cn(st_root) + '">'

        // header
        + '<div class="' + cn(st_header) + '">'
        + '  <a class="' + cn(st_brand) + '" href="/app?class=hue.captains.singapura.japjs.studio.es.StudioCatalogue">'
        + '    <span class="' + cn(st_brand_dot) + '"></span>'
        + '    <span class="' + cn(st_brand_word) + '">japjs · studio</span>'
        + '  </a>'
        + '  <div class="' + cn(st_breadcrumbs) + '">'
        + '    <a class="' + cn(st_crumb) + '" href="/app?class=hue.captains.singapura.japjs.studio.es.StudioCatalogue">Home</a>'
        + '    <span class="' + cn(st_crumb_sep) + '">/</span>'
        + '    <span class="' + cn(st_crumb) + '">Documents</span>'
        + '  </div>'
        + '</div>'

        + '<div class="' + cn(st_main) + '">'
        + '  <div class="' + cn(st_kicker) + '">documents</div>'
        + '  <h1 class="' + cn(st_title) + '">Browse</h1>'
        + '  <p class="' + cn(st_subtitle) + '">Every white paper, brochure, RFC, brand artifact, session note, and reference doc — searchable and filterable.</p>'

        + '  <div class="' + cn(st_search_wrap) + '">'
        + '    <input id="docSearch" class="' + cn(st_search) + '" type="search" placeholder="Search title or summary…" />'
        + '    <div id="docFilter" class="' + cn(st_filter) + '"></div>'
        + '  </div>'

        + '  <div id="docResults"></div>'

        + '  <div class="' + cn(st_footer) + '">'
        + '    Documents are read live from <code>docs/</code> on the server. Add a new doc by editing <code>DocRegistry.java</code> and the mirror in <code>DocBrowser.js</code>.'
        + '  </div>'
        + '</div>'
        + '</div>';

    rootElement.innerHTML = shellHtml;

    // ---- filter + search wiring ----
    var search       = document.getElementById("docSearch");
    var filterEl     = document.getElementById("docFilter");
    var resultsEl    = document.getElementById("docResults");
    var activeFilter = "ALL";
    var query        = "";

    // Render filter buttons
    var filterHtml = '';
    var filterBtns = [{key:"ALL", label:"All (" + docs.length + ")"}];
    for (var c = 0; c < categories.length; c++) {
        var count = docs.filter(function(d) { return d.category === categories[c].key; }).length;
        filterBtns.push({key: categories[c].key, label: categories[c].label + " (" + count + ")"});
    }
    for (var f = 0; f < filterBtns.length; f++) {
        var activeCls = filterBtns[f].key === activeFilter ? " " + cn(st_filter_btn_active) : "";
        filterHtml += '<button class="' + cn(st_filter_btn) + activeCls + '" data-key="' + filterBtns[f].key + '">' + escape(filterBtns[f].label) + '</button>';
    }
    filterEl.innerHTML = filterHtml;

    filterEl.addEventListener("click", function(e) {
        var t = e.target;
        if (t.tagName !== "BUTTON") return;
        activeFilter = t.getAttribute("data-key");
        // update active state
        var btns = filterEl.querySelectorAll("button");
        for (var i = 0; i < btns.length; i++) {
            css.toggleClass(btns[i], st_filter_btn_active, btns[i].getAttribute("data-key") === activeFilter);
        }
        renderResults();
    });

    search.addEventListener("input", function() {
        query = search.value.trim().toLowerCase();
        renderResults();
    });

    function renderResults() {
        var matched = docs.filter(function(d) {
            if (activeFilter !== "ALL" && d.category !== activeFilter) return false;
            if (!query) return true;
            return d.title.toLowerCase().indexOf(query) !== -1
                || d.summary.toLowerCase().indexOf(query) !== -1
                || d.path.toLowerCase().indexOf(query) !== -1;
        });

        if (matched.length === 0) {
            resultsEl.innerHTML = '<div class="' + cn(st_loading) + '">No matches.</div>';
            return;
        }

        // group by category
        var groups = {};
        var order = [];
        for (var i = 0; i < matched.length; i++) {
            var d = matched[i];
            if (!groups[d.category]) {
                groups[d.category] = {label: d.catLabel, items: []};
                order.push(d.category);
            }
            groups[d.category].items.push(d);
        }

        var html = '';
        for (var k = 0; k < order.length; k++) {
            var g = groups[order[k]];
            html += '<div class="' + cn(st_section) + '">'
                  + '  <div class="' + cn(st_section_title) + '">' + escape(g.label) + '</div>'
                  + '  <div class="' + cn(st_grid) + '">';
            for (var j = 0; j < g.items.length; j++) {
                var doc = g.items[j];
                html += '<a class="' + cn(st_card) + '" href="' + readerUrl(doc.path) + '">'
                      +   '<h3 class="' + cn(st_card_title) + '">' + escape(doc.title) + '</h3>'
                      +   '<p class="' + cn(st_card_summary) + '">' + escape(doc.summary) + '</p>'
                      +   '<div class="' + cn(st_card_meta) + '">'
                      +     '<span class="' + cn(st_badge) + ' ' + cn(badgeClassFor(doc.category)) + '">' + escape(doc.category) + '</span>'
                      +     '<span class="' + cn(st_card_link) + '">Open →</span>'
                      +   '</div>'
                      + '</a>';
            }
            html += '  </div></div>';
        }
        resultsEl.innerHTML = html;
    }

    renderResults();
}
