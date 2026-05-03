// =============================================================================
// japjs studio — RFC 0001 implementation plan (master view)
// Fetches /step-data?rfc=0001 and renders all steps with progress bars.
// Source of truth: Rfc0001Steps.java. Edit there, recompile, refresh.
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

    function stepUrl(id) {
        return "/app?class=hue.captains.singapura.japjs.studio.rfc0001.Rfc0001Step&id=" + encodeURIComponent(id);
    }

    function rfcDocUrl(path) {
        return "/app?class=hue.captains.singapura.japjs.studio.es.DocReader&path=" + encodeURIComponent(path);
    }

    var shellHtml = ''
        + '<div class="' + cn(st_root) + '">'

        + '<div class="' + cn(st_header) + '">'
        + '  <a class="' + cn(st_brand) + '" href="/app?class=hue.captains.singapura.japjs.studio.es.StudioCatalogue">'
        + '    <span class="' + cn(st_brand_dot) + '"></span>'
        + '    <span class="' + cn(st_brand_word) + '">japjs · studio</span>'
        + '  </a>'
        + '  <div class="' + cn(st_breadcrumbs) + '">'
        + '    <a class="' + cn(st_crumb) + '" href="/app?class=hue.captains.singapura.japjs.studio.es.StudioCatalogue">Home</a>'
        + '    <span class="' + cn(st_crumb_sep) + '">/</span>'
        + '    <span class="' + cn(st_crumb) + '">RFC 0001 Plan</span>'
        + '  </div>'
        + '</div>'

        + '<div class="' + cn(st_main) + '">'
        + '  <div id="planRoot"><div class="' + cn(st_loading) + '">Loading plan…</div></div>'
        + '</div>'

        + '</div>';

    rootElement.innerHTML = shellHtml;
    var planRoot = document.getElementById("planRoot");

    fetch("/step-data?rfc=0001")
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
            + '<div class="' + cn(st_kicker) + '">implementation plan</div>'
            + '<h1 class="' + cn(st_title) + '">' + escape(data.title) + '</h1>'
            + '<p class="' + cn(st_subtitle) + '">Each step is its own URL. Edit <code>Rfc0001Steps.java</code> to record progress; refresh to see updated state. The Java code is the single source of truth — git history is the change log.</p>'

            + '<div class="' + cn(st_overall_progress) + '">'
            + '  <div>'
            + '    <div style="font-size:11px;letter-spacing:3px;color:rgba(202,220,252,0.6);font-weight:700;text-transform:uppercase;">Overall</div>'
            + '    <div style="font-family:Georgia,serif;font-size:18px;margin-top:2px;">' + escape(data.steps.length) + ' steps</div>'
            + '  </div>'
            + '  <div class="' + cn(st_overall_bar) + '">'
            + '    <div class="' + cn(st_overall_fill) + '" style="width:' + escape(data.totalProgress) + '%;"></div>'
            + '  </div>'
            + '  <div class="' + cn(st_overall_pct) + '">' + escape(data.totalProgress) + '%</div>'
            + '</div>';

        for (var i = 0; i < data.steps.length; i++) {
            var s = data.steps[i];
            var taskCount = s.tasks.length;
            var doneCount = s.tasks.filter(function(t) { return t.done; }).length;

            html += '<a class="' + cn(st_step_card) + '" href="' + stepUrl(s.id) + '">'
                  + '  <div class="' + cn(st_step_head) + '">'
                  + '    <span class="' + cn(st_step_id) + '">STEP ' + escape(s.id) + '</span>'
                  + '    <h3 class="' + cn(st_step_label) + '">' + escape(s.label) + '</h3>'
                  + '    <span class="' + cn(st_status_badge) + ' ' + cn(statusBadgeClass(s.statusSlug)) + '">' + escape(s.statusLabel) + '</span>'
                  + '  </div>'
                  + '  <p class="' + cn(st_step_summary) + '">' + escape(s.summary) + '</p>'
                  + '  <div class="' + cn(st_step_progress) + '">'
                  + '    <div class="' + cn(st_step_progress_bar) + '">'
                  + '      <div class="' + cn(st_step_progress_fill) + '" style="width:' + escape(s.progress) + '%;"></div>'
                  + '    </div>'
                  + '    <div class="' + cn(st_step_meta) + '">'
                  +        escape(doneCount) + ' / ' + escape(taskCount) + ' tasks · ' + escape(s.effort)
                  + '    </div>'
                  + '  </div>'
                  + '</a>';
        }

        html += '<div class="' + cn(st_section) + '">'
              + '  <div class="' + cn(st_section_title) + '">References</div>'
              + '  <p style="font-size:14px;line-height:1.6;">'
              + '    <a href="' + rfcDocUrl(data.path) + '" style="color:var(--st-amber-dk);text-decoration:underline;">Read the RFC</a>'
              + '    &nbsp;·&nbsp; '
              + '    <a href="/step-data?rfc=0001" style="color:var(--st-gray-mid);text-decoration:underline;font-family:monospace;font-size:12px;">/step-data?rfc=0001</a> (raw JSON)'
              + '  </p>'
              + '</div>';

        planRoot.innerHTML = html;
    }
}
