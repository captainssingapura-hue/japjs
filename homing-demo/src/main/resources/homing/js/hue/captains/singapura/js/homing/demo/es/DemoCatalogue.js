// =============================================================================
// Homing demo catalogue (RFC 0001 Step 11 — migrated to typed nav + href)
// Lists every available demo with one-click navigation.
// Each entry references its target via the generated `nav` object.
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

    // -------------------------------------------------------------------------
    // DEMO REGISTRY — each entry's `link` is a function that returns the URL.
    // -------------------------------------------------------------------------
    var demos = {
        featured: {
            link:     function() { return nav.PitchDeck(); },
            simple:   "pitch-deck",
            title:    "Executive Pitch Deck",
            desc:     "A 13-slide interactive presentation of Homing's design and positioning, with keyboard navigation and ambient BGM. The deck pitches Homing by being built on Homing.",
            badge:    "PITCH",
            badgeCls: "pitch"
        },
        sections: [
            {
                title: "Examples · Modules and assets",
                items: [
                    { link: function() { return nav.WonderlandDemo(); },    simple: "wonderland-demo",
                      title: "Wonderland", badge: "BASIC", badgeCls: "basic",
                      desc: "The minimal example. Imports from another EsModule and an SvgGroup; demonstrates module wiring and SVG bundling." }
                ]
            },
            {
                title: "Animations · DomModule + CSS",
                items: [
                    { link: function() { return nav.DancingAnimals(); },    simple: "dancing-animals",
                      title: "Dancing Animals", badge: "ANIM", badgeCls: "anim",
                      desc: "A 5×5 grid of animals controlled by keyboard. Demonstrates shared components (AnimalCell), CSS class bindings, and event handling." },
                    { link: function() { return nav.SpinningAnimals(); },   simple: "spinning-animals",
                      title: "Spinning Animals", badge: "ANIM", badgeCls: "anim",
                      desc: "Animated grid with pause/resume controls. Same shared AnimalCell component composed differently." },
                    { link: function() { return nav.MovingAnimal(); },      simple: "moving-animal",
                      title: "Moving Animal · Platformer", badge: "ANIM", badgeCls: "anim",
                      desc: "A small platformer with physics, sound (Tone.js), and theme switching across five themes." }
                ]
            },
            {
                title: "3D and SVG · Three.js + asset bundling",
                items: [
                    { link: function() { return nav.TurtleDemo(); },        simple: "turtle-demo",
                      title: "3D Turtle", badge: "3D", badgeCls: "3d",
                      desc: "A simple 3D turtle scene rendered with Three.js. Demonstrates that Homing hosts WebGL libraries cleanly via ExternalModule." },
                    { link: function() { return nav.ExtrudedTurtleDemo(); },simple: "extruded-turtle-demo",
                      title: "Extruded Turtle", badge: "3D", badgeCls: "3d",
                      desc: "An SVG turtle extruded into 3D geometry. Combines SvgGroup, ThreeJsSvgLoader, and SvgExtruder modules." },
                    { link: function() { return nav.DecomposedSvgDemo(); }, simple: "decomposed-svg-demo",
                      title: "Decomposed SVG", badge: "3D", badgeCls: "3d",
                      desc: "Decomposes an SVG into its constituent paths and renders each as a separate Three.js mesh." },
                    { link: function() { return nav.ExtrudedSvgDemo(); },   simple: "extruded-svg-demo",
                      title: "Extruded SVG", badge: "3D", badgeCls: "3d",
                      desc: "Generic SVG extrusion with depth control. Reuses the SVG decomposition machinery on arbitrary inputs." }
                ]
            }
        ]
    };

    // -------------------------------------------------------------------------
    // RENDER HELPERS
    // -------------------------------------------------------------------------
    function badgeClassFor(badgeCls) {
        switch (badgeCls) {
            case "pitch": return cat_badge_pitch;
            case "3d":    return cat_badge_3d;
            case "anim":  return cat_badge_anim;
            case "basic": return cat_badge_basic;
            default:      return cat_badge_basic;
        }
    }

    function renderFeatured(d) {
        return '<a class="' + cn(cat_card, cat_card_featured) + '" ' + href.toAttr(d.link()) + '>'
             + '  <div class="' + cn(cat_card_head) + '">'
             + '    <div>'
             + '      <h2 class="' + cn(cat_card_title) + '">' + escape(d.title) + '</h2>'
             + '      <p class="' + cn(cat_card_desc) + '">' + escape(d.desc) + '</p>'
             + '    </div>'
             + '  </div>'
             + '  <div class="' + cn(cat_card_meta) + '">'
             + '    <span class="' + cn(cat_badge) + ' ' + cn(badgeClassFor(d.badgeCls)) + '">' + escape(d.badge) + '</span>'
             + '    <span class="' + cn(cat_card_link) + '">Open →</span>'
             + '  </div>'
             + '</a>';
    }

    function renderCard(d) {
        return '<a class="' + cn(cat_card) + '" ' + href.toAttr(d.link()) + '>'
             + '  <div class="' + cn(cat_card_head) + '">'
             + '    <h3 class="' + cn(cat_card_title) + '">' + escape(d.title) + '</h3>'
             + '    <span class="' + cn(cat_badge) + ' ' + cn(badgeClassFor(d.badgeCls)) + '">' + escape(d.badge) + '</span>'
             + '  </div>'
             + '  <p class="' + cn(cat_card_desc) + '">' + escape(d.desc) + '</p>'
             + '  <div class="' + cn(cat_card_meta) + '">'
             + '    <span class="' + cn(cat_mono) + '">' + escape(d.simple) + '</span>'
             + '    <span class="' + cn(cat_card_link) + '">Open →</span>'
             + '  </div>'
             + '</a>';
    }

    function renderSection(section) {
        var html = '<div class="' + cn(cat_section) + '">'
                 + '  <div class="' + cn(cat_section_title) + '">' + escape(section.title) + '</div>'
                 + '  <div class="' + cn(cat_grid) + '">';
        for (var i = 0; i < section.items.length; i++) {
            html += renderCard(section.items[i]);
        }
        html += '  </div></div>';
        return html;
    }

    // -------------------------------------------------------------------------
    // RENDER
    // -------------------------------------------------------------------------
    var html = '<div class="' + cn(cat_root) + '">';

    // header
    html += '<div class="' + cn(cat_header) + '">'
          + '  <div class="' + cn(cat_kicker) + '">Homing · demo catalogue</div>'
          + '  <h1 class="' + cn(cat_title) + '">Demos</h1>'
          + '  <p class="' + cn(cat_subtitle) + '">Every demo is a working Homing <span class="' + cn(cat_mono) + '">AppModule</span>. Click any card to open. The simple-name shown on each card is the public URL identifier — paste it into a <span class="' + cn(cat_mono) + '">/app?app=…</span> URL elsewhere.</p>'
          + '</div>';

    // featured
    html += '<div class="' + cn(cat_section) + '">'
          + '  <div class="' + cn(cat_section_title) + '">Featured</div>'
          + '  <div class="' + cn(cat_grid) + '">'
          + renderFeatured(demos.featured)
          + '  </div>'
          + '</div>';

    // sections
    for (var s = 0; s < demos.sections.length; s++) {
        html += renderSection(demos.sections[s]);
    }

    // footer
    html += '<div class="' + cn(cat_footer) + '">'
          + '  All demos are AppModules under <code>hue.captains.singapura.js.homing.demo.es</code>. '
          + 'Add a new entry to <code>DemoCatalogue.js</code> when shipping a new demo, and import its <code>link()</code> record into <code>DemoCatalogue.java</code>. '
          + '<br/>Backend route: <code>/app?app=&lt;simple-name&gt;</code>.'
          + '</div>';

    html += '</div>';

    rootElement.innerHTML = html;
}
