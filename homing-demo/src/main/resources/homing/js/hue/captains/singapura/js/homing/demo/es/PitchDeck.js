// =============================================================================
// Homing — Executive Pitch Deck
//
// A semi-interactive presentation rendered by Homing itself.
// Demonstrates: type-safe CSS classes, ToneJs BGM, SvgGroup-bundled diagrams,
// keyboard + click navigation, and a full DomModule built on the framework.
// =============================================================================

function appMain(rootElement) {

    // helper to build a class-name attribute string from imported CssClass records
    function cn() {
        var parts = [];
        for (var i = 0; i < arguments.length; i++) {
            if (arguments[i]) parts.push(css.className(arguments[i]));
        }
        return parts.join(" ");
    }

    // -------------------------------------------------------------------------
    // SLIDE CONTENT — each slide is a function returning an innerHTML string.
    // Using template literals + css.className() keeps refactoring honest:
    // remove a class from PitchDeckStyles.java and the import breaks at javac.
    // -------------------------------------------------------------------------

    function slideTitle() {
        return ''
            + '<div class="' + cn(pd_hero) + '">'
            + '  <div class="' + cn(pd_kicker) + '">EXECUTIVE PITCH · 2026</div>'
            + '  <h1 class="' + cn(pd_hero_title) + '">Homing</h1>'
            + '  <p class="' + cn(pd_hero_tag) + '">A type-safe workspace framework for Java teams.</p>'
            + '  <p class="' + cn(pd_hero_sub) + '">Ship internal tools, dashboards, and workspace-style web apps without adopting a parallel Node toolchain or a stateful UI server. One language at the boundary. Compile-time guarantees end-to-end.</p>'
            + '  <div class="' + cn(pd_hero_press) + '">Press → or Space to begin · M to toggle music</div>'
            + '</div>';
    }

    function slideSituation() {
        return ''
            + '<div class="' + cn(pd_kicker) + '">The situation</div>'
            + '<h1 class="' + cn(pd_title) + '">Your Java teams are being pushed full-stack.</h1>'
            + '<div class="' + cn(pd_grid2) + '" style="margin-top:24px;">'
            + '  <div class="' + cn(pd_body) + '" style="padding-right:20px;">'
            + '    <p>Internal tools. Admin consoles. Dashboards. Devtools. Workspace-style applications.</p>'
            + '    <p>The default answer is React/Vue + TypeScript — which drags in a parallel toolchain <em>(Node, bundlers, state managers, routers)</em> and a second language boundary that your type system cannot cross.</p>'
            + '    <p style="font-size:18px;color:#1E2761;font-weight:700;margin-top:18px;">For small internal tools, the tax often exceeds the payload.</p>'
            + '  </div>'
            + '  <div style="display:flex;flex-direction:column;gap:14px;">'
            + '    <div class="' + cn(pd_stat) + '"><div class="' + cn(pd_stat_num) + '">2 – 4 wks</div><div class="' + cn(pd_stat_label) + '">typical toolchain setup before the first screen ships</div></div>'
            + '    <div class="' + cn(pd_stat) + '"><div class="' + cn(pd_stat_num) + '">2 ×</div><div class="' + cn(pd_stat_label) + '">build systems · dependency graphs · security-audit surfaces</div></div>'
            + '    <div class="' + cn(pd_stat) + '"><div class="' + cn(pd_stat_num) + '">0</div><div class="' + cn(pd_stat_label) + '">type-safety guarantees across the tier boundary</div></div>'
            + '  </div>'
            + '</div>';
    }

    function slideHiddenCost() {
        var rows = [
            ["Second build system",            "Node, npm, Webpack/Vite, lockfiles, node_modules, separate CI steps"],
            ["Second dependency graph",        "Monthly security updates for hundreds of transitive npm packages, Dependabot noise"],
            ["Second language at the boundary","Hand-maintained TypeScript types mirroring Java DTOs; every rename is a cross-repo ritual"],
            ["Second set of specialists",      "Developers who know React, Redux, Vite config — recruiting premium or contractor dependency"],
            ["Onboarding tax",                 "New Java hires spend weeks learning the JS half before shipping anything"],
            ["Coordination tax",               "Backend schema changes need frontend ticket → PR → review → deploy, even for internal tools"]
        ];
        var html = ''
            + '<div class="' + cn(pd_kicker) + '">The hidden cost</div>'
            + '<h1 class="' + cn(pd_title) + '">The real cost is rarely the component code.</h1>'
            + '<p class="' + cn(pd_subtitle) + '">It\'s the parallel toolchain that comes with it.</p>'
            + '<div style="margin-top:18px;">';
        for (var i = 0; i < rows.length; i++) {
            html += '<div class="' + cn(pd_row) + '">'
                  + '  <div class="' + cn(pd_row_head) + '">' + rows[i][0] + '</div>'
                  + '  <div class="' + cn(pd_row_body) + '">' + rows[i][1] + '</div>'
                  + '</div>';
        }
        html += '</div>'
              + '<p class="' + cn(pd_hint) + '">Justified for public consumer products. Often excessive for internal tools.</p>';
        return html;
    }

    function slideProposition() {
        return ''
            + '<div class="' + cn(pd_kicker) + '">The proposition</div>'
            + '<h1 class="' + cn(pd_cta) + '">Invert the setup.</h1>'
            + '<p style="font-size:20px;line-height:1.5;color:#CADCFC;max-width:1100px;">'
            + '  Declare the web app\'s module graph, CSS bindings, assets, and inter-tier messages in <span class="' + cn(pd_accent) + '">Java</span>. Homing generates plain ES modules and serves them through whatever HTTP server your team <span class="' + cn(pd_accent) + '">already runs</span>.'
            + '</p>'
            + '<div class="' + cn(pd_grid3) + '" style="margin-top:36px;">'
            + '  <div class="' + cn(pd_card_dark) + '"><div class="' + cn(pd_accent) + '" style="font-size:42px;font-family:Georgia,serif;">01</div><div class="' + cn(pd_card_head) + '">One toolchain</div><div class="' + cn(pd_card_body) + '">Maven only. No Node. No bundler.</div></div>'
            + '  <div class="' + cn(pd_card_dark) + '"><div class="' + cn(pd_accent) + '" style="font-size:42px;font-family:Georgia,serif;">02</div><div class="' + cn(pd_card_head) + '">One language at the boundary</div><div class="' + cn(pd_card_body) + '">Java declares. JS is generated.</div></div>'
            + '  <div class="' + cn(pd_card_dark) + '"><div class="' + cn(pd_accent) + '" style="font-size:42px;font-family:Georgia,serif;">03</div><div class="' + cn(pd_card_head) + '">Types end-to-end</div><div class="' + cn(pd_card_body) + '">Enforced at javac, across browser and server.</div></div>'
            + '</div>';
    }

    function slideDiagram(kicker, title, subtitle, svgContent) {
        return ''
            + '<div class="' + cn(pd_kicker) + '">' + kicker + '</div>'
            + '<h1 class="' + cn(pd_title) + '">' + title + '</h1>'
            + (subtitle ? '<p class="' + cn(pd_subtitle) + '">' + subtitle + '</p>' : '')
            + '<div class="' + cn(pd_diagram) + '" style="height:calc(100% - 130px);">'
            + svgContent
            + '</div>';
    }

    function slideArchitecture()  { return slideDiagram("Architecture", "The four-layer stack.",          "Each layer is useful on its own. Higher layers depend on lower.", arch);       }
    function slideOwnership()     { return slideDiagram("Key insight",  "Ownership tree vs. layout tree.","Two independent structures. Tabs are references, not owners.",     ownership);  }
    function slideDeployment()    { return slideDiagram("Deployment",   "Bring your own HTTP.",           "Slot into the Java service you already run. No Node. No bundler.", deployment); }
    function slideMessaging()     { return slideDiagram("Messaging",    "End-to-end typed channels.",     "Same Java records drive widget↔widget and widget↔server traffic.", messaging);  }

    function slideCompetitive() {
        var headers = ["", "Toolchain", "Runtime", "Types across tiers", "Server lock-in", "Workspace-ready"];
        var rows = [
            ["React + TS",       "Heavy (Node)",       "Light",              "Partial",         "None (SPA)",        "3rd-party"],
            ["Vaadin Flow",      "Medium",             "Heavy (stateful)",   "Yes",             "Vaadin-specific",   "Yes, heavy"],
            ["Vaadin Hilla",     "Heavy + Node",       "Medium",             "Yes (generated)", "Spring-centric",    "Via React"],
            ["JHipster",         "Very heavy",         "Medium",             "Partial",         "Opinionated",       "Not focused"],
            ["htmx + Spring",    "Light",              "Light",              "No",              "Any",               "No"],
            ["Homing",            "Light",              "Light (stateless)",  "Yes, end-to-end", "Any (adapter)",     "Yes, first-class"]
        ];

        var html = ''
            + '<div class="' + cn(pd_kicker) + '">Competitive landscape</div>'
            + '<h1 class="' + cn(pd_title) + '">Against the Java full-stack peer set.</h1>'
            + '<div class="' + cn(pd_table) + '" style="margin-top:18px;">';

        for (var h = 0; h < headers.length; h++) {
            html += '<div class="' + cn(pd_table_header) + '">' + headers[h] + '</div>';
        }
        for (var r = 0; r < rows.length; r++) {
            var featured = (r === rows.length - 1);
            for (var c = 0; c < rows[r].length; c++) {
                var classes = featured
                    ? cn(pd_table_row, pd_table_row_featured, pd_table_cell)
                    : cn(pd_table_row, pd_table_cell);
                html += '<div class="' + classes + '">' + rows[r][c] + '</div>';
            }
        }
        html += '</div>'
              + '<p class="' + cn(pd_hint) + '">Not competing with React for public products. Competing with Vaadin / Hilla / JHipster / htmx for the Java-team full-stack slot.</p>';
        return html;
    }

    function slideBuiltDesigned() {
        var built = [
            "homing-core — module, CSS, SVG, external-module primitives",
            "homing-server — Vert.x adapter, live reload via homing.devRoot",
            "Type-safe CSS class bindings with theme variants",
            "Type-safe SVG asset bundling",
            "Third-party library wrapping (Three.js, Tone.js)",
            "homing-conformance — CSS raw-op scanner tests",
            "This deck (running on Homing itself)"
        ];
        var designed = [
            "Spring Boot adapter (~100–200 LOC of glue)",
            "DomOpsParty integration — DOM ownership & leak detection",
            "Recursive BSP split-pane workspace shell",
            "Tab pane with drag / detach / dock",
            "Typed channel bus — local + remote transports",
            "Java-record message/channel declarations",
            "Serializable workspace layout (local & server)"
        ];

        var html = ''
            + '<div class="' + cn(pd_kicker) + '">Honest status</div>'
            + '<h1 class="' + cn(pd_title) + '">What\'s built · what\'s designed.</h1>'
            + '<div class="' + cn(pd_grid2) + '" style="margin-top:24px;">';

        html += '<div><div class="' + cn(pd_badge_built) + '">Built · running today</div>';
        for (var i = 0; i < built.length; i++) {
            html += '<div class="' + cn(pd_card) + '" style="padding:12px 16px;margin-bottom:8px;border-left-color:#059669;"><div style="font-size:13px;color:#3B4A6B;">' + built[i] + '</div></div>';
        }
        html += '</div>';

        html += '<div><div class="' + cn(pd_badge_designed) + '">Designed · buildable in a quarter</div>';
        for (var j = 0; j < designed.length; j++) {
            html += '<div class="' + cn(pd_card) + '" style="padding:12px 16px;margin-bottom:8px;"><div style="font-size:13px;color:#3B4A6B;">' + designed[j] + '</div></div>';
        }
        html += '</div></div>';

        html += '<p class="' + cn(pd_hint) + '">Pilot scope can be drawn conservatively (built layers only) or ambitiously (concurrent shipping of designed layers).</p>';
        return html;
    }

    function slideWorkloads() {
        var right = [
            ["Internal tools & admin consoles",     "Long-lived, workflow-shaped, used by employees"],
            ["Trading & monitoring dashboards",     "Many widgets, typed data, live updates"],
            ["Devtools & data exploration",         "Layout-heavy, multi-pane, keyboard-driven"],
            ["Backoffice portals",                  "CRUD + search + detail over 3–5 entities"],
            ["Java-heavy orgs going full-stack",    "Backend talent, no budget for a frontend squad"]
        ];
        var wrong = [
            ["Public consumer products with SEO",   "Next.js / Nuxt earn their keep"],
            ["Existing healthy React/Vue codebases","Don't migrate for migration's sake"],
            ["Dense fan-out reactivity (200-field forms)", "Signal-based frameworks are purpose-built"],
            ["Reactive 3D scene graphs (JSX-for-meshes)",  "Homing hosts Three.js but doesn't abstract it"],
            ["Short-notice broad-pool frontend hiring",    "React's labor market is orders larger"]
        ];

        function block(title, items, isRight) {
            var iconClass = isRight ? cn(pd_check) : cn(pd_cross);
            var iconChar  = isRight ? "✓" : "✕";
            var s = '<div><div class="' + cn(pd_card_dark) + '" style="margin-bottom:10px;text-align:center;padding:12px;">'
                  + '<div style="font-size:14px;letter-spacing:3px;font-weight:700;color:#F4B942;">' + title + '</div></div>';
            for (var k = 0; k < items.length; k++) {
                s += '<div class="' + cn(pd_card) + '" style="padding:12px 16px;margin-bottom:8px;display:flex;align-items:flex-start;">'
                   +   '<span class="' + iconClass + '" style="margin-top:2px;">' + iconChar + '</span>'
                   +   '<div style="margin-left:6px;">'
                   +     '<div style="font-weight:700;color:#1E2761;font-size:13px;">' + items[k][0] + '</div>'
                   +     '<div style="font-size:12px;color:#64748B;font-style:italic;margin-top:2px;">' + items[k][1] + '</div>'
                   +   '</div>'
                   + '</div>';
            }
            s += '</div>';
            return s;
        }

        return ''
            + '<div class="' + cn(pd_kicker) + '">Target workloads</div>'
            + '<h1 class="' + cn(pd_title) + '">Where Homing is — and isn\'t — the right choice.</h1>'
            + '<div class="' + cn(pd_grid2) + '" style="margin-top:18px;">'
            + block("✓  RIGHT CHOICE", right, true)
            + block("✕  WRONG CHOICE", wrong, false)
            + '</div>'
            + '<p class="' + cn(pd_hint) + '">Not on the wrong-choice list: large tabular datasets. Virtualized grids (AG Grid, Monaco, CodeMirror) work fine on direct-DOM frameworks.</p>';
    }

    function slidePilot() {
        var shape = [
            ["Duration", "4 – 6 weeks calendar"],
            ["Team",     "1 – 2 engineers, part-time OK"],
            ["Project",  "One upcoming internal tool"],
            ["Infra",    "Zero net-new"]
        ];
        var metrics = [
            ["≤ 0.5 ×", "time to first useful screen"],
            ["≤ 0.7 ×", "lines of code at feature parity"],
            ["≤ 0.2 ×", "toolchain setup time"],
            ["Fewer",    "tier-boundary defects"]
        ];

        var html = ''
            + '<div class="' + cn(pd_kicker) + '">The pilot</div>'
            + '<h1 class="' + cn(pd_title) + '">4–6 weeks · 1–2 engineers · one real internal tool.</h1>'
            + '<div class="' + cn(pd_grid4) + '" style="margin-top:18px;">';
        for (var i = 0; i < shape.length; i++) {
            html += '<div class="' + cn(pd_card_dark) + '">'
                  +   '<div class="' + cn(pd_kicker) + '" style="margin-bottom:8px;">' + shape[i][0] + '</div>'
                  +   '<div class="' + cn(pd_card_head) + '" style="font-size:18px;color:#FFFFFF;">' + shape[i][1] + '</div>'
                  + '</div>';
        }
        html += '</div>';

        html += '<p class="' + cn(pd_hint) + '" style="margin-top:24px;">SUCCESS METRICS · graded against the team\'s current React baseline</p>';
        html += '<div class="' + cn(pd_grid4) + '" style="margin-top:8px;">';
        for (var j = 0; j < metrics.length; j++) {
            html += '<div class="' + cn(pd_stat) + '">'
                  +   '<div class="' + cn(pd_stat_num) + '">' + metrics[j][0] + '</div>'
                  +   '<div class="' + cn(pd_stat_label) + '">' + metrics[j][1] + '</div>'
                  + '</div>';
        }
        html += '</div>';

        html += '<div class="' + cn(pd_card_dark) + '" style="margin-top:24px;">'
              +   '<div class="' + cn(pd_kicker) + '">Decision gates</div>'
              +   '<div class="' + cn(pd_grid2) + '" style="margin-top:8px;">'
              +     '<div><div class="' + cn(pd_card_head) + '" style="color:#FFFFFF;font-size:16px;">Week 2 · Go / No-go</div><div class="' + cn(pd_card_body) + '">End-to-end typed round-trip working? If not, stop and re-scope.</div></div>'
              +     '<div><div class="' + cn(pd_card_head) + '" style="color:#FFFFFF;font-size:16px;">Week 6 · Ship / Shelve</div><div class="' + cn(pd_card_body) + '">≥ 3 of 4 metrics met and team prefers continuing? Adopt for next project.</div></div>'
              +   '</div>'
              + '</div>';

        return html;
    }

    function slideAsk() {
        return ''
            + '<div class="' + cn(pd_kicker) + '">The ask</div>'
            + '<h1 class="' + cn(pd_cta) + '">Sponsor a scoped pilot.</h1>'
            + '<p style="font-size:24px;color:#CADCFC;margin-bottom:32px;">'
            +   'One upcoming internal tool. <span class="' + cn(pd_accent) + '">4–6 weeks.</span> 1–2 engineers. Zero net-new infrastructure.'
            + '</p>'
            + '<div class="' + cn(pd_grid2) + '" style="margin-top:14px;">'
            + '  <div class="' + cn(pd_card_dark) + '" style="border-color:#F4B942;">'
            + '    <div class="' + cn(pd_kicker) + '">Best case</div>'
            + '    <div class="' + cn(pd_card_body) + '" style="margin-top:8px;font-size:14px;">Adopt Homing for the class of internal tools where the Node-toolchain tax is highest. Consolidate delivery. Give Java engineers a first-class UI path.</div>'
            + '  </div>'
            + '  <div class="' + cn(pd_card_dark) + '" style="border-color:#F4B942;">'
            + '    <div class="' + cn(pd_kicker) + '">Worst case</div>'
            + '    <div class="' + cn(pd_card_body) + '" style="margin-top:8px;font-size:14px;">Ship one useful internal tool, collect data on your own pipeline, decide Homing is not for you. One team-month. Backend work is reusable.</div>'
            + '  </div>'
            + '</div>'
            + '<p class="' + cn(pd_quote) + '" style="margin-top:32px;">If your Java teams are building long-lived internal web tools and the Node toolchain feels like a tax — Homing is worth 4 weeks to find out.</p>'
            + '<div class="' + cn(pd_hero_press) + '" style="margin-top:18px;">— end · press ← to return —</div>';
    }

    // -------------------------------------------------------------------------
    // SLIDE REGISTRY — order, dark-flag, render fn
    // -------------------------------------------------------------------------
    var slides = [
        { dark: true,  render: slideTitle         },
        { dark: false, render: slideSituation     },
        { dark: false, render: slideHiddenCost    },
        { dark: true,  render: slideProposition   },
        { dark: false, render: slideArchitecture  },
        { dark: false, render: slideOwnership     },
        { dark: false, render: slideDeployment    },
        { dark: false, render: slideMessaging     },
        { dark: false, render: slideCompetitive   },
        { dark: false, render: slideBuiltDesigned },
        { dark: false, render: slideWorkloads     },
        { dark: false, render: slidePilot         },
        { dark: true,  render: slideAsk           }
    ];

    // -------------------------------------------------------------------------
    // SHELL CONSTRUCTION
    // -------------------------------------------------------------------------
    var root = document.createElement("div");
    css.setClass(root, pd_root);

    // progress bar (top)
    var progress = document.createElement("div");
    css.setClass(progress, pd_progress);
    var progressFill = document.createElement("div");
    css.setClass(progressFill, pd_progress_fill);
    progressFill.style.width = "0%";
    progress.appendChild(progressFill);
    root.appendChild(progress);

    // stage with all slides pre-rendered
    var stage = document.createElement("div");
    css.setClass(stage, pd_stage);
    var slideEls = [];
    for (var s = 0; s < slides.length; s++) {
        var slideEl = document.createElement("div");
        css.setClass(slideEl, pd_slide);
        if (slides[s].dark) css.addClass(slideEl, pd_slide_dark);
        slideEl.innerHTML = slides[s].render();
        stage.appendChild(slideEl);
        slideEls.push(slideEl);
    }
    root.appendChild(stage);

    // bottom nav bar
    var nav = document.createElement("div");
    css.setClass(nav, pd_nav);

    var prevBtn = document.createElement("button");
    css.setClass(prevBtn, pd_btn);
    prevBtn.textContent = "← Prev";

    var nextBtn = document.createElement("button");
    css.setClass(nextBtn, pd_btn);
    css.addClass(nextBtn, pd_btn_primary);
    nextBtn.textContent = "Next →";

    var counter = document.createElement("div");
    counter.style.cssText = "color:#CADCFC;font-size:12px;letter-spacing:2px;font-weight:700;margin-left:8px;";

    var dots = document.createElement("div");
    css.setClass(dots, pd_dots);
    var dotEls = [];
    for (var d = 0; d < slides.length; d++) {
        var dot = document.createElement("div");
        css.setClass(dot, pd_dot);
        (function(idx) { dot.addEventListener("click", function() { goTo(idx); }); })(d);
        dots.appendChild(dot);
        dotEls.push(dot);
    }

    var bgmBtn = document.createElement("button");
    css.setClass(bgmBtn, pd_btn);
    css.addClass(bgmBtn, pd_btn_bgm);
    bgmBtn.textContent = "Music";

    nav.appendChild(prevBtn);
    nav.appendChild(nextBtn);
    nav.appendChild(counter);
    nav.appendChild(dots);
    nav.appendChild(bgmBtn);
    root.appendChild(nav);

    // toast
    var toast = document.createElement("div");
    css.setClass(toast, pd_toast);
    root.appendChild(toast);

    rootElement.appendChild(root);

    // -------------------------------------------------------------------------
    // NAVIGATION
    // -------------------------------------------------------------------------
    var current = 0;

    function showToast(msg) {
        toast.textContent = msg;
        css.addClass(toast, pd_toast_show);
        clearTimeout(toast._t);
        toast._t = setTimeout(function() { css.removeClass(toast, pd_toast_show); }, 1200);
    }

    function render() {
        for (var i = 0; i < slideEls.length; i++) {
            css.toggleClass(slideEls[i], pd_slide_active, i === current);
            css.toggleClass(dotEls[i],   pd_dot_active,   i === current);
        }
        progressFill.style.width = ((current + 1) / slides.length * 100) + "%";
        counter.textContent = (current + 1) + " / " + slides.length;
        prevBtn.disabled = (current === 0);
        nextBtn.disabled = (current === slides.length - 1);
    }

    function goTo(idx) {
        if (idx < 0 || idx >= slides.length) return;
        current = idx;
        render();
    }
    function next() { goTo(current + 1); }
    function prev() { goTo(current - 1); }

    prevBtn.addEventListener("click", prev);
    nextBtn.addEventListener("click", next);

    document.addEventListener("keydown", function(e) {
        if (e.target && (e.target.tagName === "INPUT" || e.target.tagName === "TEXTAREA")) return;
        switch (e.key) {
            case "ArrowRight":
            case " ":
            case "PageDown":
                e.preventDefault();
                next();
                break;
            case "ArrowLeft":
            case "PageUp":
                e.preventDefault();
                prev();
                break;
            case "Home":
                e.preventDefault(); goTo(0); break;
            case "End":
                e.preventDefault(); goTo(slides.length - 1); break;
            case "m":
            case "M":
                e.preventDefault(); toggleBgm(); break;
        }
    });

    // -------------------------------------------------------------------------
    // BGM — Tone.js synth pad + melody loop, paused by default until user opts in.
    // -------------------------------------------------------------------------
    var bgm = getBgm();
    var melodySynth = null;
    var padSynth = null;
    var melodyTimer = null;
    var padTimer = null;
    var melodyIdx = 0;
    var padIdx = 0;
    var bgmOn = false;
    var toneStarted = false;

    function durationToMs(dur, bpm) {
        var beat = 60000 / bpm;
        switch (dur) {
            case "1n":  return beat * 4;
            case "2n":  return beat * 2;
            case "4n":  return beat;
            case "8n":  return beat / 2;
            case "16n": return beat / 4;
            default:    return beat;
        }
    }

    function ensureSynths() {
        if (melodySynth) return;
        melodySynth = new Synth({
            oscillator: { type: "triangle" },
            envelope:   { attack: 0.04, decay: 0.18, sustain: 0.12, release: 0.4 },
            volume:     -22
        }).toDestination();
        padSynth = new Synth({
            oscillator: { type: "sine" },
            envelope:   { attack: 0.6, decay: 0.4, sustain: 0.4, release: 1.5 },
            volume:     -32
        }).toDestination();
    }

    function melodyStep() {
        if (!bgmOn) return;
        var note = bgm.melody[melodyIdx];
        var dur  = bgm.durations[melodyIdx];
        if (note) melodySynth.triggerAttackRelease(note, dur);
        var ms = durationToMs(dur, bgm.bpm);
        melodyIdx = (melodyIdx + 1) % bgm.melody.length;
        melodyTimer = setTimeout(melodyStep, ms);
    }

    function padStep() {
        if (!bgmOn) return;
        var note = bgm.padNotes[padIdx];
        padSynth.triggerAttackRelease(note, "2n");
        padIdx = (padIdx + 1) % bgm.padNotes.length;
        padTimer = setTimeout(padStep, durationToMs("2n", bgm.bpm));
    }

    function startBgm() {
        if (bgmOn) return;
        var go = function() {
            ensureSynths();
            bgmOn = true;
            melodyIdx = 0;
            padIdx = 0;
            css.addClass(bgmBtn, pd_btn_bgm_on);
            showToast("Music on");
            melodyStep();
            padStep();
        };
        if (!toneStarted) {
            start().then(function() { toneStarted = true; go(); });
        } else {
            go();
        }
    }

    function stopBgm() {
        bgmOn = false;
        if (melodyTimer) { clearTimeout(melodyTimer); melodyTimer = null; }
        if (padTimer)    { clearTimeout(padTimer);    padTimer    = null; }
        css.removeClass(bgmBtn, pd_btn_bgm_on);
        showToast("Music off");
    }

    function toggleBgm() {
        if (bgmOn) stopBgm(); else startBgm();
    }

    bgmBtn.addEventListener("click", toggleBgm);

    // initial render
    render();
}
