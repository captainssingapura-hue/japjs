// =============================================================================
// Homing studio — RFC 0002 plan (Typed Themes for CssGroups)
// Source of truth: Rfc0002Steps.java. Edit, recompile, refresh.
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
        return String(s == null ? "" : s)
            .replace(/&/g, "&amp;").replace(/</g, "&lt;").replace(/>/g, "&gt;")
            .replace(/"/g, "&quot;").replace(/'/g, "&#39;");
    }

    function statusBadgeClass(slug) {
        switch (slug) {
            case "not-started": return st_status_not_started;
            case "in-progress": return st_status_in_progress;
            case "blocked":     return st_status_blocked;
            case "done":        return st_status_done;
            default:            return st_status_not_started;
        }
    }

    function decisionBadgeClass(slug) {
        return slug === "resolved" ? st_status_done : st_status_in_progress;
    }

    var shellHtml = ''
        + '<div class="' + cn(st_root) + '">'

        + '<div class="' + cn(st_header) + '">'
        + '  <a class="' + cn(st_brand) + '" ' + href.toAttr(nav.StudioCatalogue()) + '>'
        + '    <span class="' + cn(st_brand_dot) + '"></span>'
        + '    <span class="' + cn(st_brand_word) + '">Homing · studio</span>'
        + '  </a>'
        + '  <div class="' + cn(st_breadcrumbs) + '">'
        + '    <a class="' + cn(st_crumb) + '" ' + href.toAttr(nav.StudioCatalogue()) + '>Home</a>'
        + '    <span class="' + cn(st_crumb_sep) + '">/</span>'  
        + '    <a class="' + cn(st_crumb) + '" ' + href.toAttr(nav.JourneysCatalogue()) + '>Journeys</a>'
        + '    <span class="' + cn(st_crumb_sep) + '">/</span>'
        + '    <span class="' + cn(st_crumb) + '">RFC 0002 Plan</span>'
        + '  </div>'
        + '</div>'

        + '<div class="' + cn(st_main) + '">'
        + '  <div id="planRoot"><div class="' + cn(st_loading) + '">Loading RFC 0002 plan…</div></div>'
        + '</div>'

        + '</div>';

    rootElement.innerHTML = shellHtml;
    var planRoot = document.getElementById("planRoot");

    fetch("/rfc0002-data")
        .then(function(r) {
            if (!r.ok) throw new Error("HTTP " + r.status);
            return r.json();
        })
        .then(function(data) { renderPlan(data); })
        .catch(function(err) {
            planRoot.innerHTML = '<div class="' + cn(st_error) + '">Failed to load plan: ' + escape(err.message) + '</div>';
        });

    function renderPlan(data) {
        var html = ''
            + '<div class="' + cn(st_kicker) + '">RFC 0002</div>'
            + '<h1 class="' + cn(st_title) + '">Typed Themes for CssGroups</h1>'
            + '<p class="' + cn(st_subtitle) + '">Source of truth: <code>Rfc0002Steps.java</code>. Edit, recompile, refresh — phases and decisions update live. Companion document: <a ' + href.toAttr(nav.DocReader({path: data.rfcDoc})) + ' style="color:var(--st-amber-dk);">RFC 0002 (full design)</a>.</p>';

        // Overall progress bar
        html += '<div class="' + cn(st_overall_progress) + '">'
              + '  <div>'
              + '    <div style="font-size:11px;letter-spacing:3px;color:rgba(202,220,252,0.6);font-weight:700;text-transform:uppercase;">Overall</div>'
              + '    <div style="font-family:Georgia,serif;font-size:18px;margin-top:2px;">' + escape(data.phases.length) + ' phases · ' + escape(data.openDecisions) + ' open decisions</div>'
              + '  </div>'
              + '  <div class="' + cn(st_overall_bar) + '">'
              + '    <div class="' + cn(st_overall_fill) + '" style="width:' + escape(data.totalProgress) + '%;"></div>'
              + '  </div>'
              + '  <div class="' + cn(st_overall_pct) + '">' + escape(data.totalProgress) + '%</div>'
              + '</div>';

        // Decisions section (if any)
        if (data.decisions && data.decisions.length > 0) {
            html += '<div class="' + cn(st_section) + '">'
                  + '  <div class="' + cn(st_section_title) + '">Open Decisions</div>'
                  + '  <p style="font-size:14px;color:var(--st-gray-mid);margin:0 0 14px 0;font-style:italic;">Resolve these during execution. Edit <code>Rfc0002Steps.DECISIONS</code> to mark resolved with a chosen value.</p>';
            for (var d = 0; d < data.decisions.length; d++) {
                var dec = data.decisions[d];
                var chosenLine = dec.chosenValue
                    ? '<div style="margin-top:8px;font-size:13px;color:var(--st-navy);"><strong>Chosen:</strong> ' + escape(dec.chosenValue) + '</div>'
                    : '';
                html += '<div class="' + cn(st_card) + '" style="cursor:default;min-height:auto;margin-bottom:10px;">'
                      + '  <div style="display:flex;align-items:flex-start;justify-content:space-between;gap:12px;">'
                      + '    <div style="flex:1;">'
                      + '      <div style="font-family:Georgia,serif;font-style:italic;font-size:11px;color:var(--st-amber-dk);font-weight:700;letter-spacing:2px;">' + escape(dec.id) + '</div>'
                      + '      <h4 class="' + cn(st_card_title) + '" style="margin:4px 0 6px 0;">' + escape(dec.question) + '</h4>'
                      + '      <p class="' + cn(st_card_summary) + '" style="margin:6px 0;"><strong>Recommendation:</strong> ' + escape(dec.recommendation) + '</p>'
                      + '      <p style="font-size:12px;color:var(--st-gray-mid);font-style:italic;margin:4px 0 0 0;">' + escape(dec.rationale) + '</p>'
                      +        chosenLine
                      + '    </div>'
                      + '    <span class="' + cn(st_status_badge) + ' ' + cn(decisionBadgeClass(dec.statusSlug)) + '">' + escape(dec.statusLabel) + '</span>'
                      + '  </div>'
                      + '</div>';
            }
            html += '</div>';
        }

        // Phases section
        html += '<div class="' + cn(st_section) + '">'
              + '  <div class="' + cn(st_section_title) + '">Phases</div>';

        for (var i = 0; i < data.phases.length; i++) {
            var p = data.phases[i];
            var taskCount = p.tasks.length;
            var doneCount = p.tasks.filter(function(t) { return t.done; }).length;

            html += '<a class="' + cn(st_step_card) + '" ' + href.toAttr(nav.Rfc0002Step({phase: p.id})) + '>'
                  + '  <div class="' + cn(st_step_head) + '">'
                  + '    <span class="' + cn(st_step_id) + '">PHASE ' + escape(p.id) + '</span>'
                  + '    <h3 class="' + cn(st_step_label) + '">' + escape(p.label) + '</h3>'
                  + '    <span class="' + cn(st_status_badge) + ' ' + cn(statusBadgeClass(p.statusSlug)) + '">' + escape(p.statusLabel) + '</span>'
                  + '  </div>'
                  + '  <p class="' + cn(st_step_summary) + '">' + escape(p.summary) + '</p>'
                  + '  <div class="' + cn(st_step_progress) + '">'
                  + '    <div class="' + cn(st_step_progress_bar) + '">'
                  + '      <div class="' + cn(st_step_progress_fill) + '" style="width:' + escape(p.progress) + '%;"></div>'
                  + '    </div>'
                  + '    <div class="' + cn(st_step_meta) + '">'
                  +        escape(doneCount) + ' / ' + escape(taskCount) + ' tasks · ' + escape(p.effort)
                  + '    </div>'
                  + '  </div>'
                  + '</a>';
        }

        html += '</div>';

        // Footer
        html += '<div class="' + cn(st_footer) + '">'
              + '  Reference: <a ' + href.toAttr(nav.DocReader({path: data.rfcDoc})) + '>RFC 0002 — Typed Themes for CssGroups</a>'
              + '  <br/>Raw JSON endpoint: <code>/rfc0002-data</code>.'
              + '</div>';

        planRoot.innerHTML = html;
    }
}
