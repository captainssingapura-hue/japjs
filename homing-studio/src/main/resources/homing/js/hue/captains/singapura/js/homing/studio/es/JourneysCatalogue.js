// =============================================================================
// Homing studio — Journeys (sub-catalogue listing every plan tracker)
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
            link:     function() { return nav.Rfc0001Plan(); },
            label:    "RFC 0001 Plan",
            desc:     "Live implementation tracker for the App Registry & Typed Navigation RFC. Every step is its own URL; progress is recorded in Java code and rendered live in the studio.",
            icon:     "R",
            featured: false
        },
        {
            link:     function() { return nav.Rfc0002Plan(); },
            label:    "RFC 0002 Plan",
            desc:     "Live tracker for the Typed Themes for CssGroups RFC. Seven phases plus four open design questions; the third worked example of the live-tracker pattern.",
            icon:     "T",
            featured: false
        },
        {
            link:     function() { return nav.Rfc0002Ext1Plan(); },
            label:    "RFC 0002-ext1 Plan",
            desc:     "Live tracker for the Utility-First Composition + Two-Layer Semantic Tokens extension to RFC 0002. Seven phases that layer utility classes and semantic tokens onto the typed-theme foundation.",
            icon:     "U",
            featured: false
        },
        {
            link:     function() { return nav.RenamePlan(); },
            label:    "Rename Plan",
            desc:     "Migration plan for Homing → Homing. Six phases with verification gates and rollback strategies, plus four open decisions to resolve before executing. Live tracker — edit RenameSteps.java to revise.",
            icon:     "→",
            featured: false
        }
        // Future plan trackers register themselves here as new tiles, importing
        // their link() record into JourneysCatalogue.java imports().
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
        + '    <a class="' + cn(st_crumb) + '" ' + href.toAttr(nav.StudioCatalogue()) + '>Home</a>'
        + '    <span class="' + cn(st_crumb_sep) + '">/</span>'
        + '    <span class="' + cn(st_crumb) + '">Journeys</span>'
        + '  </div>'
        + '</div>'

        // main
        + '<div class="' + cn(st_main) + '">'
        + '  <div class="' + cn(st_kicker) + '">multi-phase plans</div>'
        + '  <h1 class="' + cn(st_title) + '">Journeys</h1>'
        + '  <p class="' + cn(st_subtitle) + '">Live trackers for every multi-phase plan in this project. Source of truth: the corresponding <code>*Steps.java</code> in each tracker package — edit, recompile, refresh and the state updates here.</p>'

        + '  <div class="' + cn(st_section) + '">'
        + '    <div class="' + cn(st_section_title) + '">Plans</div>'
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
          + '    Pattern documented at <a ' + href.toAttr(nav.DocReader({path: "guides/live-tracker-pattern.md"})) + '>Live Tracker Pattern</a>. Add a new journey by writing a *Steps.java + DataGetAction + Plan/Step AppModule pair and registering them here + in <code>JourneysCatalogue.java</code>.'
          + '  </div>'
          + '</div>'

          + '</div>';

    rootElement.innerHTML = html;
}
