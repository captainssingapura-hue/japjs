// =============================================================================
// DocBrowserRenderer — shared renderer for any DocBrowserAppModule.
//
// renderDocBrowser({ data, brand }) → Node
//
// `data` matches DocBrowserData (kicker, title, subtitle, crumbs[],
//        readerAppSimpleName, docs[], footer).
// `brand` is { href, label } for the studio header.
//
// Builds the search input + category filters + grouped card grid; updates
// results live as the search query / active filter changes. References to
// the search input, filter buttons, and results container are held from
// creation (Owned References doctrine).
// =============================================================================

var href = HrefManagerInstance;

function renderDocBrowser(props) {
    var data  = props.data;
    var brand = props.brand;

    var docs = data.docs;
    function readerUrl(path) {
        return "/app?app=" + data.readerAppSimpleName + "&path=" + encodeURIComponent(path);
    }

    var root = document.createElement("div");
    css.addClass(root, st_root);

    root.appendChild(Header({ brand: brand, crumbs: data.crumbs.map(function(c) {
        return c.href ? { text: c.text, href: c.href } : { text: c.text };
    })}));

    var main = document.createElement("div");
    css.addClass(main, st_main);

    var kicker = document.createElement("div");
    css.addClass(kicker, st_kicker);
    kicker.textContent = data.kicker;
    main.appendChild(kicker);

    var title = document.createElement("h1");
    css.addClass(title, st_title);
    title.textContent = data.title;
    main.appendChild(title);

    var subtitle = document.createElement("p");
    css.addClass(subtitle, st_subtitle);
    subtitle.textContent = data.subtitle;
    main.appendChild(subtitle);

    // Distinct categories preserving insertion order.
    var catOrder = [];
    var catLabel = {};
    for (var i = 0; i < docs.length; i++) {
        if (!catLabel[docs[i].category]) {
            catLabel[docs[i].category] = docs[i].catLabel;
            catOrder.push(docs[i].category);
        }
    }

    // Search + filter row.
    var searchWrap = document.createElement("div");
    css.addClass(searchWrap, st_search_wrap);

    var searchInput = document.createElement("input");
    css.addClass(searchInput, st_search);
    searchInput.type = "search";
    searchInput.placeholder = "Search title or path…";
    searchWrap.appendChild(searchInput);

    var filterContainer = document.createElement("div");
    css.addClass(filterContainer, st_filter);
    searchWrap.appendChild(filterContainer);

    main.appendChild(searchWrap);

    // Results container.
    var resultsEl = document.createElement("div");
    main.appendChild(resultsEl);

    if (data.footer) {
        var footer = document.createElement("p");
        footer.style.cssText = "margin: 24px 0 0 0;";
        footer.textContent = data.footer;
        main.appendChild(Footer({ children: [footer] }));
    }

    root.appendChild(main);

    // ---- filter buttons ----
    var activeFilter = "ALL";
    var query        = "";
    var filterButtons = [];

    var allBtnSpec = { key: "ALL", label: "All (" + docs.length + ")" };
    var btnSpecs = [allBtnSpec];
    for (var c = 0; c < catOrder.length; c++) {
        var key = catOrder[c];
        var count = docs.filter(function(d) { return d.category === key; }).length;
        btnSpecs.push({ key: key, label: catLabel[key] + " (" + count + ")" });
    }
    for (var f = 0; f < btnSpecs.length; f++) {
        (function(spec) {
            var btn = document.createElement("button");
            css.addClass(btn, st_filter_btn);
            if (spec.key === activeFilter) css.addClass(btn, st_filter_btn_active);
            btn.textContent = spec.label;
            btn.addEventListener("click", function() {
                activeFilter = spec.key;
                for (var bi = 0; bi < filterButtons.length; bi++) {
                    var fb = filterButtons[bi];
                    css.toggleClass(fb.button, st_filter_btn_active, fb.key === activeFilter);
                }
                renderResults();
            });
            filterButtons.push({ key: spec.key, button: btn });
            filterContainer.appendChild(btn);
        })(btnSpecs[f]);
    }

    searchInput.addEventListener("input", function() {
        query = searchInput.value.trim().toLowerCase();
        renderResults();
    });

    function renderResults() {
        var matched = docs.filter(function(d) {
            if (activeFilter !== "ALL" && d.category !== activeFilter) return false;
            if (!query) return true;
            return d.title.toLowerCase().indexOf(query) !== -1
                || (d.summary && d.summary.toLowerCase().indexOf(query) !== -1)
                || d.path.toLowerCase().indexOf(query) !== -1;
        });

        if (matched.length === 0) {
            var empty = document.createElement("div");
            css.addClass(empty, st_loading);
            empty.textContent = "No matches.";
            resultsEl.replaceChildren(empty);
            return;
        }

        var groups = {};
        var order  = [];
        for (var i = 0; i < matched.length; i++) {
            var d = matched[i];
            if (!groups[d.category]) {
                groups[d.category] = { label: d.catLabel, items: [] };
                order.push(d.category);
            }
            groups[d.category].items.push(d);
        }

        var sections = [];
        for (var k = 0; k < order.length; k++) {
            var g = groups[order[k]];
            var cards = g.items.map(function(doc) {
                return Card({
                    href:       readerUrl(doc.path),
                    title:      doc.title,
                    summary:    doc.summary,
                    badge:      doc.category,
                    badgeClass: doc.badgeClass ? css.cls(doc.badgeClass) : null,
                    link:       "Open →"
                });
            });
            sections.push(Section({ title: g.label, children: cards }));
        }
        resultsEl.replaceChildren.apply(resultsEl, sections);
    }

    renderResults();
    return root;
}
