// =============================================================================
// Homing studio — rename phase detail view
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

    var phaseId = params.phase || "";

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
        + '    <a class="' + cn(st_crumb) + '" ' + href.toAttr(nav.RenamePlan()) + '>Rename Plan</a>'
        + '    <span class="' + cn(st_crumb_sep) + '">/</span>'
        + '    <span class="' + cn(st_crumb) + '">Phase ' + escape(phaseId) + '</span>'
        + '  </div>'
        + '</div>'

        + '<div class="' + cn(st_main) + '">'
        + '  <div id="phaseRoot"><div class="' + cn(st_loading) + '">Loading phase…</div></div>'
        + '</div>'

        + '</div>';

    rootElement.innerHTML = shellHtml;
    var phaseRoot = document.getElementById("phaseRoot");

    if (!phaseId) {
        phaseRoot.innerHTML = '<div class="' + cn(st_error) + '">No phase id specified. Use <code>?phase=…</code>.</div>';
        return;
    }

    fetch("/rename-data?phase=" + encodeURIComponent(phaseId))
        .then(function(r) {
            if (!r.ok) throw new Error("HTTP " + r.status);
            return r.json();
        })
        .then(function(p) { renderPhase(p); })
        .catch(function(err) {
            phaseRoot.innerHTML = '<div class="' + cn(st_error) + '">Failed to load phase <code>' + escape(phaseId) + '</code>: ' + escape(err.message) + '</div>';
        });

    function renderPhase(p) {
        var descHtml = (typeof marked !== "undefined" && marked.parse)
            ? marked.parse(p.description || "")
            : "<p>" + escape(p.description || "") + "</p>";

        var notesHtml = p.notes && p.notes.length
            ? ((typeof marked !== "undefined" && marked.parse) ? marked.parse(p.notes) : "<p>" + escape(p.notes) + "</p>")
            : '<p style="color:var(--st-gray-mid);font-style:italic;">(no notes yet)</p>';

        var tasksHtml = p.tasks.length
            ? '<ul class="' + cn(st_task_list) + '">'
              + p.tasks.map(function(t) {
                    var doneCls = t.done ? " " + cn(st_task_done) : "";
                    var box = '<span class="' + cn(st_task_box) + '">' + (t.done ? "✓" : "") + '</span>';
                    return '<li class="' + cn(st_task_item) + doneCls + '">' + box + '<span>' + escape(t.description) + '</span></li>';
                }).join("")
              + '</ul>'
            : '<p style="color:var(--st-gray-mid);font-style:italic;">(no tasks declared)</p>';

        var depsHtml = p.dependsOn.length
            ? p.dependsOn.map(function(d) {
                    return '<a class="' + cn(st_dep) + '" ' + href.toAttr(nav.RenameStep({phase: d.phaseId})) + ' title="' + escape(d.reason) + '">Phase ' + escape(d.phaseId) + '</a>';
                }).join("")
            : '<span style="color:var(--st-gray-mid);font-style:italic;font-size:13px;">(no dependencies — independent)</span>';

        var html = ''
            + '<div class="' + cn(st_kicker) + '">Rename · Phase ' + escape(p.id) + '</div>'
            + '<h1 class="' + cn(st_title) + '">' + escape(p.label) + '</h1>'
            + '<p class="' + cn(st_subtitle) + '">' + escape(p.summary) + '</p>'

            // Status row
            + '<div style="display:flex;align-items:center;gap:14px;margin:16px 0 24px;">'
            + '  <span class="' + cn(st_status_badge) + ' ' + cn(statusBadgeClass(p.statusSlug)) + '" style="font-size:11px;padding:4px 10px;">' + escape(p.statusLabel) + '</span>'
            + '  <div style="flex:1;display:flex;align-items:center;gap:10px;">'
            + '    <div class="' + cn(st_step_progress_bar) + '" style="flex:1;height:8px;">'
            + '      <div class="' + cn(st_step_progress_fill) + '" style="width:' + escape(p.progress) + '%;"></div>'
            + '    </div>'
            + '    <span style="font-family:Georgia,serif;font-weight:700;color:var(--st-navy);">' + escape(p.progress) + '%</span>'
            + '  </div>'
            + '  <span class="' + cn(st_effort) + '">est. ' + escape(p.effort) + '</span>'
            + '</div>'

            // Description
            + '<div class="' + cn(st_panel) + '">'
            + '  <div class="' + cn(st_panel_title) + '">Description</div>'
            + '  <div class="' + cn(st_doc) + '">' + descHtml + '</div>'
            + '</div>'

            // Tasks
            + '<div class="' + cn(st_panel) + '">'
            + '  <div class="' + cn(st_panel_title) + '">Tasks</div>'
            +    tasksHtml
            + '</div>'

            // Dependencies
            + '<div class="' + cn(st_panel) + '">'
            + '  <div class="' + cn(st_panel_title) + '">Depends on</div>'
            +    depsHtml
            + '</div>'

            // Verification
            + '<div class="' + cn(st_panel) + '">'
            + '  <div class="' + cn(st_panel_title) + '">Verification gate</div>'
            + '  <div class="' + cn(st_acceptance) + '">' + escape(p.verification) + '</div>'
            + '</div>'

            // Rollback
            + '<div class="' + cn(st_panel) + '">'
            + '  <div class="' + cn(st_panel_title) + '">Rollback</div>'
            + '  <div class="' + cn(st_acceptance) + '">' + escape(p.rollback) + '</div>'
            + '</div>'

            // Notes
            + '<div class="' + cn(st_panel) + '">'
            + '  <div class="' + cn(st_panel_title) + '">Notes</div>'
            + '  <div class="' + cn(st_doc) + '">' + notesHtml + '</div>'
            + '</div>';

        // Prev / next navigation
        var idNum = parseInt(p.id, 10);
        if (!isNaN(idNum)) {
            var prevId = idNum > 1 ? String(idNum - 1).padStart(2, "0") : null;
            var nextId = idNum < 6 ? String(idNum + 1).padStart(2, "0") : null;
            html += '<div style="display:flex;justify-content:space-between;margin-top:24px;font-size:13px;">'
                  + '  <span>'
                  +    (prevId ? '<a ' + href.toAttr(nav.RenameStep({phase: prevId})) + ' style="color:var(--st-amber-dk);text-decoration:underline;">← Phase ' + prevId + '</a>' : '')
                  + '  </span>'
                  + '  <a ' + href.toAttr(nav.RenamePlan()) + ' style="color:var(--st-gray-mid);text-decoration:underline;">All phases</a>'
                  + '  <span>'
                  +    (nextId ? '<a ' + href.toAttr(nav.RenameStep({phase: nextId})) + ' style="color:var(--st-amber-dk);text-decoration:underline;">Phase ' + nextId + ' →</a>' : '')
                  + '  </span>'
                  + '</div>';
        }

        phaseRoot.innerHTML = html;
    }
}
