// =============================================================================
// japjs studio — launcher / home
// Lists the available studio apps. Pre-RFC-0001, links use the legacy ?class=
// URL contract; will migrate to nav.X(...) when typed nav lands.
// =============================================================================

function appMain(rootElement) {

    function cn() {
        var parts = [];
        for (var i = 0; i < arguments.length; i++) {
            if (arguments[i]) parts.push(css.className(arguments[i]));
        }
        return parts.join(" ");
    }

    var PKG = "hue.captains.singapura.japjs.studio.es.";
    var url = function(cls) { return "/app?class=" + PKG + cls; };
    var rfcUrl = function(cls) { return "/app?class=hue.captains.singapura.japjs.studio.rfc0001." + cls; };

    var apps = [
        {
            href:     url("DocBrowser"),
            label:    "Documents",
            desc:     "Browse and read every white paper, brochure, RFC, brand artifact, and design note in the project — searchable, filterable by category, with an in-page table of contents on every doc.",
            icon:     "D",
            featured: true
        },
        {
            href:     rfcUrl("Rfc0001Plan"),
            label:    "RFC 0001 Plan",
            desc:     "Live implementation tracker for the App Registry & Typed Navigation RFC. Every step is its own URL; progress is recorded in Java code and rendered live in the studio.",
            icon:     "R",
            featured: false
        }
        // Future apps register themselves here as new tiles:
        //   { href: "...", label: "Decisions", desc: "...", icon: "L" }
        //   { href: "...", label: "Plan",      desc: "...", icon: "P" }
    ];

    var html = ''
        + '<div class="' + cn(st_root) + '">'

        // header
        + '<div class="' + cn(st_header) + '">'
        + '  <a class="' + cn(st_brand) + '" href="/app?class=hue.captains.singapura.japjs.studio.es.StudioCatalogue">'
        + '    <span class="' + cn(st_brand_dot) + '"></span>'
        + '    <span class="' + cn(st_brand_word) + '">japjs · studio</span>'
        + '  </a>'
        + '  <div class="' + cn(st_breadcrumbs) + '">'
        + '    <span class="' + cn(st_crumb) + '">Home</span>'
        + '  </div>'
        + '</div>'

        // main
        + '<div class="' + cn(st_main) + '">'
        + '  <div class="' + cn(st_kicker) + '">design &amp; project management</div>'
        + '  <h1 class="' + cn(st_title) + '">Studio</h1>'
        + '  <p class="' + cn(st_subtitle) + '">A workspace for the design, documentation, and project artifacts that drive japjs forward — built on japjs itself.</p>'

        + '  <div class="' + cn(st_section) + '">'
        + '    <div class="' + cn(st_section_title) + '">Apps</div>'
        + '    <div class="' + cn(st_grid) + '">';

    for (var i = 0; i < apps.length; i++) {
        var a = apps[i];
        var pillCls = a.featured ? cn(st_app_pill, st_app_pill_dark) : cn(st_app_pill);
        html += '<a class="' + pillCls + '" href="' + a.href + '">'
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
          + '    Studio is a sibling Maven module to <code>japjs-demo</code>, built entirely on japjs primitives. Add new tiles to <code>StudioCatalogue.js</code>; new apps register themselves as siblings under <code>...studio.es.*</code>.'
          + '  </div>'
          + '</div>'

          + '</div>';

    rootElement.innerHTML = html;
}
