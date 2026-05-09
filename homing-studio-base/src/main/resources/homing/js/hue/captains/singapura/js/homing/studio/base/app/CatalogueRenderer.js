// =============================================================================
// CatalogueRenderer — shared renderer for any CatalogueAppModule.
//
// renderCatalogue({ data, brand }) → Node
//
// `data` matches CatalogueData (kicker, title, subtitle, crumbs[], sections[],
//        footer). Each section has a `tileStyle` ("PILL" or "CARD") plus a
//        list of tile records.
// `brand` is { href, label } for the studio header.
//
// Doctrine note: this is renderer code (framework). It uses createElement +
// css.* + href.*; the AppModule's auto-generated body never touches HTML
// strings or DOM API directly.
// =============================================================================

var href = HrefManagerInstance;

// Render `text` with markdown-style backtick `code` spans into a parent.
// Preserves order, returns nothing — appends to `parent`. Used only for
// catalogue footers (no other formatting).
function _appendBackticked(parent, text) {
    if (!text) return;
    var i = 0;
    var inCode = false;
    var buffer = "";
    while (i < text.length) {
        var ch = text.charAt(i);
        if (ch === "`") {
            if (buffer.length > 0) {
                if (inCode) {
                    var code = document.createElement("code");
                    code.textContent = buffer;
                    parent.appendChild(code);
                } else {
                    parent.appendChild(document.createTextNode(buffer));
                }
                buffer = "";
            }
            inCode = !inCode;
        } else {
            buffer += ch;
        }
        i++;
    }
    if (buffer.length > 0) {
        parent.appendChild(document.createTextNode(buffer));
    }
}

function renderCatalogue(props) {
    var data  = props.data;
    var brand = props.brand;

    var root = document.createElement("div");
    css.addClass(root, st_root);

    // Header — convert null hrefs to omitted (Header treats missing href as plain text).
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

    for (var si = 0; si < data.sections.length; si++) {
        var section = data.sections[si];
        var children = section.tiles.map(function(t) {
            if (section.tileStyle === "PILL") {
                return Pill({
                    href:  t.href,
                    icon:  t.icon,
                    label: t.label,
                    desc:  t.desc,
                    dark:  t.featured
                });
            }
            // CARD style
            return Card({
                href:       t.href,
                title:      t.label,
                summary:    t.desc,
                badge:      t.badge,
                badgeClass: t.badgeClass ? css.cls(t.badgeClass) : null,
                link:       "Open →"
            });
        });
        main.appendChild(Section({ title: section.title, children: children }));
    }

    if (data.footer) {
        // Build text + <code> nodes from the markdown-ish footer string,
        // snapshot the children, then wrap with StudioElements.Footer.
        var fragment = document.createDocumentFragment();
        _appendBackticked(fragment, data.footer);
        // Array.from snapshots the live NodeList — Footer will move the
        // nodes out of the fragment via appendChild, which would otherwise
        // mutate the iterator and skip every other node.
        var footerNodes = Array.from(fragment.childNodes);
        main.appendChild(Footer({ children: footerNodes }));
    }

    root.appendChild(main);
    return root;
}
