// =============================================================================
// japjs demo catalogue
// Lists every available demo with one-click navigation.
// Hand-edit the `demos` array below when adding a new demo to the project.
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

    var PKG = "hue.captains.singapura.japjs.demo.es.";
    var url = function(cls) { return "/app?class=" + PKG + cls; };

    // -------------------------------------------------------------------------
    // DEMO REGISTRY — edit when adding/removing demos
    // -------------------------------------------------------------------------
    var demos = {
        featured: {
            cls:      "PitchDeck",
            title:    "Executive Pitch Deck",
            desc:     "A 13-slide interactive presentation of japjs's design and positioning, with keyboard navigation and ambient BGM. The deck pitches japjs by being built on japjs.",
            badge:    "PITCH",
            badgeCls: "pitch"
        },
        sections: [
            {
                title: "Examples · Modules and assets",
                items: [
                    {
                        cls:      "WonderlandDemo",
                        title:    "Wonderland",
                        desc:     "The minimal example. Imports from another EsModule and an SvgGroup; demonstrates module wiring and SVG bundling.",
                        badge:    "BASIC",
                        badgeCls: "basic"
                    }
                ]
            },
            {
                title: "Animations · DomModule + CSS",
                items: [
                    {
                        cls:      "DancingAnimals",
                        title:    "Dancing Animals",
                        desc:     "A 5×5 grid of animals controlled by keyboard. Demonstrates shared components (AnimalCell), CSS class bindings, and event handling.",
                        badge:    "ANIM",
                        badgeCls: "anim"
                    },
                    {
                        cls:      "SpinningAnimals",
                        title:    "Spinning Animals",
                        desc:     "Animated grid with pause/resume controls. Same shared AnimalCell component composed differently.",
                        badge:    "ANIM",
                        badgeCls: "anim"
                    },
                    {
                        cls:      "MovingAnimal",
                        title:    "Moving Animal · Platformer",
                        desc:     "A small platformer with physics, sound (Tone.js), and theme switching across five themes (light, dark, beach, dracula, alpine).",
                        badge:    "ANIM",
                        badgeCls: "anim"
                    }
                ]
            },
            {
                title: "3D and SVG · Three.js + asset bundling",
                items: [
                    {
                        cls:      "TurtleDemo",
                        title:    "3D Turtle",
                        desc:     "A simple 3D turtle scene rendered with Three.js. Demonstrates that japjs hosts WebGL libraries cleanly via ExternalModule.",
                        badge:    "3D",
                        badgeCls: "3d"
                    },
                    {
                        cls:      "ExtrudedTurtleDemo",
                        title:    "Extruded Turtle",
                        desc:     "An SVG turtle extruded into 3D geometry. Combines SvgGroup, ThreeJsSvgLoader, and SvgExtruder modules.",
                        badge:    "3D",
                        badgeCls: "3d"
                    },
                    {
                        cls:      "DecomposedSvgDemo",
                        title:    "Decomposed SVG",
                        desc:     "Decomposes an SVG into its constituent paths and renders each as a separate Three.js mesh.",
                        badge:    "3D",
                        badgeCls: "3d"
                    },
                    {
                        cls:      "ExtrudedSvgDemo",
                        title:    "Extruded SVG",
                        desc:     "Generic SVG extrusion with depth control. Reuses the SVG decomposition machinery on arbitrary inputs.",
                        badge:    "3D",
                        badgeCls: "3d"
                    }
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
        return '<a class="' + cn(cat_card, cat_card_featured) + '" href="' + url(d.cls) + '">'
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
        return '<a class="' + cn(cat_card) + '" href="' + url(d.cls) + '">'
             + '  <div class="' + cn(cat_card_head) + '">'
             + '    <h3 class="' + cn(cat_card_title) + '">' + escape(d.title) + '</h3>'
             + '    <span class="' + cn(cat_badge) + ' ' + cn(badgeClassFor(d.badgeCls)) + '">' + escape(d.badge) + '</span>'
             + '  </div>'
             + '  <p class="' + cn(cat_card_desc) + '">' + escape(d.desc) + '</p>'
             + '  <div class="' + cn(cat_card_meta) + '">'
             + '    <span class="' + cn(cat_mono) + '">' + escape(d.cls) + '</span>'
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
          + '  <div class="' + cn(cat_kicker) + '">japjs · demo catalogue</div>'
          + '  <h1 class="' + cn(cat_title) + '">Demos</h1>'
          + '  <p class="' + cn(cat_subtitle) + '">Every demo is a working japjs <span class="' + cn(cat_mono) + '">AppModule</span>. Click any card to open. The full canonical class name is shown on each card so you can paste it into a <span class="' + cn(cat_mono) + '">/app?class=…</span> URL elsewhere.</p>'
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
          + '  All demos are AppModules under <code>' + escape(PKG.replace(/\.$/, "")) + '</code>. '
          + 'Add a new entry to <code>DemoCatalogue.js</code> when shipping a new demo. '
          + '<br/>Backend route: <code>/app?class=&lt;canonical&gt;</code>.'
          + '</div>';

    html += '</div>';

    rootElement.innerHTML = html;
}
