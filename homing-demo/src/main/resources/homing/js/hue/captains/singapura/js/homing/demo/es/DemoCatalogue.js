// =============================================================================
// Homing demo catalogue (RFC 0001 Step 11 — typed nav + href).
//
// Doctrine compliance:
//   - Pure-Component Views: no HTML tag literals, no innerHTML writes. Every
//     UI element is built via document.createElement (until Components ship).
//   - Owned References: the catalogue creates its DOM top-down and never
//     looks anything up by id/class.
//   - Managed DOM Ops: pending — DemoCatalogue is SPA-shaped, so this file
//     will eventually route DOM ops through DomOpsParty (RFC 0003 v2). Until
//     the gateway exists, the imperative DOM API is the only path.
// =============================================================================

function appMain(rootElement) {

    // ---- helpers ----
    function el(tag) {
        return document.createElement(tag);
    }

    function setClass(node) {
        for (var i = 1; i < arguments.length; i++) {
            if (arguments[i]) css.addClass(node, arguments[i]);
        }
        return node;
    }

    function appendAll(parent /* , ...children */) {
        for (var i = 1; i < arguments.length; i++) {
            if (arguments[i] != null) parent.appendChild(arguments[i]);
        }
        return parent;
    }

    function badgeClassFor(badgeCls) {
        switch (badgeCls) {
            case "pitch": return cat_badge_pitch;
            case "3d":    return cat_badge_3d;
            case "anim":  return cat_badge_anim;
            case "basic": return cat_badge_basic;
            default:      return cat_badge_basic;
        }
    }

    // -------------------------------------------------------------------------
    // DEMO REGISTRY — each entry's `link` is a function that returns the URL.
    // -------------------------------------------------------------------------
    var demos = {
        featured: {
            link:     function() { return nav.MovingAnimal(); },
            simple:   "moving-animal",
            title:    "Moving Animal · Military Platformer",
            desc:     "A side-scrolling platformer where the animal hops across military vehicles. Three themes — Navy / Air Force / Army — vary the vehicle silhouettes (carrier / destroyer / submarine; A-10 / fighter / Apache; tank / armoured truck / Humvee), the sky, and the BGM. Theme switch reshapes the world, not just its colours.",
            badge:    "ANIM",
            badgeCls: "anim"
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
                      desc: "Animated grid with pause/resume controls. Same shared AnimalCell component composed differently." }
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
    // RENDER HELPERS — each returns a Node, never an HTML string.
    // -------------------------------------------------------------------------
    function buildBadge(d) {
        var span = setClass(el("span"), cat_badge, badgeClassFor(d.badgeCls));
        span.textContent = d.badge;
        return span;
    }

    function buildOpenLink() {
        var span = setClass(el("span"), cat_card_link);
        span.textContent = "Open →";
        return span;
    }

    function buildFeatured(d) {
        var a = setClass(el("a"), cat_card, cat_card_featured);
        href.set(a, d.link());

        var head = setClass(el("div"), cat_card_head);
        var inner = el("div");
        var title = setClass(el("h2"), cat_card_title);
        title.textContent = d.title;
        var desc = setClass(el("p"), cat_card_desc);
        desc.textContent = d.desc;
        appendAll(inner, title, desc);
        head.appendChild(inner);

        var meta = setClass(el("div"), cat_card_meta);
        appendAll(meta, buildBadge(d), buildOpenLink());

        return appendAll(a, head, meta);
    }

    function buildCard(d) {
        var a = setClass(el("a"), cat_card);
        href.set(a, d.link());

        var head = setClass(el("div"), cat_card_head);
        var title = setClass(el("h3"), cat_card_title);
        title.textContent = d.title;
        appendAll(head, title, buildBadge(d));

        var desc = setClass(el("p"), cat_card_desc);
        desc.textContent = d.desc;

        var meta = setClass(el("div"), cat_card_meta);
        var mono = setClass(el("span"), cat_mono);
        mono.textContent = d.simple;
        appendAll(meta, mono, buildOpenLink());

        return appendAll(a, head, desc, meta);
    }

    function buildSection(title, items, isFeaturedSlot) {
        var section = setClass(el("div"), cat_section);
        var label = setClass(el("div"), cat_section_title);
        label.textContent = title;
        section.appendChild(label);

        var grid = setClass(el("div"), cat_grid);
        for (var i = 0; i < items.length; i++) {
            grid.appendChild(isFeaturedSlot ? buildFeatured(items[i]) : buildCard(items[i]));
        }
        section.appendChild(grid);
        return section;
    }

    function buildHeader() {
        var header = setClass(el("div"), cat_header);

        var kicker = setClass(el("div"), cat_kicker);
        kicker.textContent = "Homing · demo catalogue";

        var title = setClass(el("h1"), cat_title);
        title.textContent = "Demos";

        var subtitle = setClass(el("p"), cat_subtitle);
        var mono1 = setClass(el("span"), cat_mono); mono1.textContent = "AppModule";
        var mono2 = setClass(el("span"), cat_mono); mono2.textContent = "/app?app=…";
        subtitle.appendChild(document.createTextNode("Every demo is a working Homing "));
        subtitle.appendChild(mono1);
        subtitle.appendChild(document.createTextNode(". Click any card to open. The simple-name shown on each card is the public URL identifier — paste it into a "));
        subtitle.appendChild(mono2);
        subtitle.appendChild(document.createTextNode(" URL elsewhere."));

        return appendAll(header, kicker, title, subtitle);
    }

    function buildFooter() {
        var footer = setClass(el("div"), cat_footer);
        var c1 = el("code"); c1.textContent = "hue.captains.singapura.js.homing.demo.es";
        var c2 = el("code"); c2.textContent = "DemoCatalogue.js";
        var c3 = el("code"); c3.textContent = "link()";
        var c4 = el("code"); c4.textContent = "DemoCatalogue.java";
        var c5 = el("code"); c5.textContent = "/app?app=<simple-name>";

        footer.appendChild(document.createTextNode("All demos are AppModules under "));
        footer.appendChild(c1);
        footer.appendChild(document.createTextNode(". Add a new entry to "));
        footer.appendChild(c2);
        footer.appendChild(document.createTextNode(" when shipping a new demo, and import its "));
        footer.appendChild(c3);
        footer.appendChild(document.createTextNode(" record into "));
        footer.appendChild(c4);
        footer.appendChild(document.createTextNode("."));
        footer.appendChild(el("br"));
        footer.appendChild(document.createTextNode("Backend route: "));
        footer.appendChild(c5);
        footer.appendChild(document.createTextNode("."));
        return footer;
    }

    // -------------------------------------------------------------------------
    // RENDER (top-down composition; rootElement gets exactly one child)
    // -------------------------------------------------------------------------
    var root = setClass(el("div"), cat_root);

    root.appendChild(buildHeader());
    root.appendChild(buildSection("Featured", [demos.featured], true));
    for (var s = 0; s < demos.sections.length; s++) {
        root.appendChild(buildSection(demos.sections[s].title, demos.sections[s].items, false));
    }
    root.appendChild(buildFooter());

    rootElement.replaceChildren(root);
}
