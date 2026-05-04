// =============================================================================
// Homing studio — launcher / home (RFC 0001 Step 11 — typed nav + href manager)
// =============================================================================

function appMain(rootElement) {

    function cn() {
        var parts = [];
        for (var i = 0; i < arguments.length; i++) {
            if (arguments[i]) parts.push(css.className(arguments[i]));
        }
        return parts.join(" ");
    }

    var apps = [
        {
            link:     function() { return nav.DocBrowser(); },
            label:    "Documents",
            desc:     "Browse and read every white paper, brochure, RFC, brand artifact, and design note in the project — searchable, filterable by category, with an in-page table of contents on every doc.",
            icon:     "D",
            featured: true
        },
        {
            link:     function() { return nav.Rfc0001Plan(); },
            label:    "RFC 0001 Plan",
            desc:     "Live implementation tracker for the App Registry & Typed Navigation RFC. Every step is its own URL; progress is recorded in Java code and rendered live in the studio.",
            icon:     "R",
            featured: false
        },
        {
            link:     function() { return nav.RenamePlan(); },
            label:    "Rename Plan",
            desc:     "Migration plan for Homing → Homing. Six phases with verification gates and rollback strategies, plus four open decisions to resolve before executing. Live tracker — edit RenameSteps.java to revise.",
            icon:     "→",
            featured: false
        }
        // Future apps register themselves here as new tiles, importing their link()
        // record into StudioCatalogue.java imports().
    ];

    var html = ''
        + '<div class="' + cn(st_root) + '">'

        // header
        + '<div class="' + cn(st_header) + '">'
        + '  <a class="' + cn(st_brand) + '" ' + href.toAttr(nav.StudioCatalogue()) + '>'
        + '    <span class="' + cn(st_brand_dot) + '"></span>'
        + '    <span class="' + cn(st_brand_word) + '">Homing · studio</span>'
        + '  </a>'
        + '  <div class="' + cn(st_breadcrumbs) + '">'
        + '    <span class="' + cn(st_crumb) + '">Home</span>'
        + '  </div>'
        + '</div>'

        // main
        + '<div class="' + cn(st_main) + '">'
        + '  <div class="' + cn(st_kicker) + '">design &amp; project management</div>'
        + '  <h1 class="' + cn(st_title) + '">Studio</h1>'
        + '  <p class="' + cn(st_subtitle) + '">A workspace for the design, documentation, and project artifacts that drive Homing forward — built on Homing itself.</p>'

        + '  <div class="' + cn(st_section) + '">'
        + '    <div class="' + cn(st_section_title) + '">Apps</div>'
        + '    <div class="' + cn(st_grid) + '">';

    for (var i = 0; i < apps.length; i++) {
        var a = apps[i];
        var pillCls = a.featured ? cn(st_app_pill, st_app_pill_dark) : cn(st_app_pill);
        html += '<a class="' + pillCls + '" ' + href.toAttr(a.link()) + '>'
              +   '<div class="' + cn(st_app_pill_icon) + '">' + a.icon + '</div>'
              +   '<div>'
              +     '<div class="' + cn(st_app_pill_label) + '">' + a.label + '</div>'
              +     '<div class="' + cn(st_app_pill_desc) + '">' + a.desc + '</div>'
              +   '</div>'
              + '</a>';
    }

    html += '    </div>'
          + '  </div>'

          // footer
          + '  <div class="' + cn(st_footer) + '">'
          + '    Studio is a sibling Maven module to <code>homing-demo</code>, built entirely on Homing primitives. Add a new tile to this file and import the target\'s <code>link()</code> record into <code>StudioCatalogue.java</code>.'
          + '  </div>'
          + '</div>'

          + '</div>';

    rootElement.innerHTML = html;
}
