// =============================================================================
// Homing studio — RFC 0001 single-step detail view
// Reads the step id from ?id=NN, fetches /step-data?rfc=0001&id=NN, renders.
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

    function planUrl() {
        return nav.Rfc0001Plan();
    }
    function stepUrl(id) {
        return nav.Rfc0001Step({id: id});
    }
    function rfcDocUrl(path) {
        // Note: anchor support deferred — would need a separate ProxyApp or kernel
        // support for fragment-via-nav. For now, callers can append #anchor outside.
        return nav.DocReader({path: path});
    }

    // RFC 0001 Step 06+11: typed `params` const generated from Rfc0001Step.Params (Java).
    var stepId = params.id || "";

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
        + '    <a class="' + cn(st_crumb) + '" ' + href.toAttr(planUrl()) + '>RFC 0001 Plan</a>'
        + '    <span class="' + cn(st_crumb_sep) + '">/</span>'
        + '    <span class="' + cn(st_crumb) + '">Step ' + escape(stepId) + '</span>'
        + '  </div>'
        + '</div>'

        + '<div class="' + cn(st_main) + '">'
        + '  <div id="stepRoot"><div class="' + cn(st_loading) + '">Loading step…</div></div>'
        + '</div>'

        + '</div>';

    rootElement.innerHTML = shellHtml;
    var stepRoot = document.getElementById("stepRoot");

    if (!stepId) {
        stepRoot.innerHTML = '<div class="' + cn(st_error) + '">No step id specified. Use <code>?id=…</code>.</div>';
        return;
    }

    fetch("/step-data?rfc=0001&id=" + encodeURIComponent(stepId))
        .then(function(r) {
            if (!r.ok) throw new Error("HTTP " + r.status);
            return r.json();
        })
        .then(function(s) { renderStep(s); })
        .catch(function(err) {
            stepRoot.innerHTML = '<div class="' + cn(st_error) + '">Failed to load step <code>' + escape(stepId) + '</code>: ' + escape(err.message) + '</div>';
        });

    function renderStep(s) {
        // Render description as markdown if marked is loaded
        var descHtml = (typeof marked !== "undefined" && marked.parse)
            ? marked.parse(s.description || "")
            : "<p>" + escape(s.description || "") + "</p>";

        var notesHtml = s.notes && s.notes.length
            ? ((typeof marked !== "undefined" && marked.parse) ? marked.parse(s.notes) : "<p>" + escape(s.notes) + "</p>")
            : '<p style="color:var(--st-gray-mid);font-style:italic;">(no notes yet)</p>';

        var tasksHtml = s.tasks.length
            ? '<ul class="' + cn(st_task_list) + '">'
              + s.tasks.map(function(t) {
                    var doneCls = t.done ? " " + cn(st_task_done) : "";
                    var box = '<span class="' + cn(st_task_box) + '">' + (t.done ? "✓" : "") + '</span>';
                    return '<li class="' + cn(st_task_item) + doneCls + '">' + box + '<span>' + escape(t.description) + '</span></li>';
                }).join("")
              + '</ul>'
            : '<p style="color:var(--st-gray-mid);font-style:italic;">(no tasks declared)</p>';

        var depsHtml = s.dependsOn.length
            ? s.dependsOn.map(function(d) {
                    return '<a class="' + cn(st_dep) + '" ' + href.toAttr(stepUrl(d.stepId)) + ' title="' + escape(d.reason) + '">Step ' + escape(d.stepId) + '</a>';
                }).join("")
            : '<span style="color:var(--st-gray-mid);font-style:italic;font-size:13px;">(no dependencies — independent)</span>';

        var html = ''
            + '<div class="' + cn(st_kicker) + '">RFC 0001 · Step ' + escape(s.id) + '</div>'
            + '<h1 class="' + cn(st_title) + '">' + escape(s.label) + '</h1>'
            + '<p class="' + cn(st_subtitle) + '">' + escape(s.summary) + '</p>'

            // status row
            + '<div style="display:flex;align-items:center;gap:14px;margin:16px 0 24px;">'
            + '  <span class="' + cn(st_status_badge) + ' ' + cn(statusBadgeClass(s.statusSlug)) + '" style="font-size:11px;padding:4px 10px;">' + escape(s.statusLabel) + '</span>'
            + '  <div style="flex:1;display:flex;align-items:center;gap:10px;">'
            + '    <div class="' + cn(st_step_progress_bar) + '" style="flex:1;height:8px;">'
            + '      <div class="' + cn(st_step_progress_fill) + '" style="width:' + escape(s.progress) + '%;"></div>'
            + '    </div>'
            + '    <span style="font-family:Georgia,serif;font-weight:700;color:var(--st-navy);">' + escape(s.progress) + '%</span>'
            + '  </div>'
            + '  <span class="' + cn(st_effort) + '">est. ' + escape(s.effort) + '</span>'
            + '</div>'

            // description panel
            + '<div class="' + cn(st_panel) + '">'
            + '  <div class="' + cn(st_panel_title) + '">Description</div>'
            + '  <div class="' + cn(st_doc) + '">' + descHtml + '</div>'
            + '</div>'

            // tasks panel
            + '<div class="' + cn(st_panel) + '">'
            + '  <div class="' + cn(st_panel_title) + '">Tasks</div>'
            +    tasksHtml
            + '</div>'

            // dependencies panel
            + '<div class="' + cn(st_panel) + '">'
            + '  <div class="' + cn(st_panel_title) + '">Depends on</div>'
            +    depsHtml
            + '</div>'

            // acceptance panel
            + '<div class="' + cn(st_panel) + '">'
            + '  <div class="' + cn(st_panel_title) + '">Acceptance criteria</div>'
            + '  <div class="' + cn(st_acceptance) + '">' + escape(s.acceptance) + '</div>'
            + '</div>'

            // rfc + notes
            + '<div class="' + cn(st_panel) + '">'
            + '  <div class="' + cn(st_panel_title) + '">RFC reference</div>'
            + '  <p style="margin:0;font-size:14px;">'
            + '    <a ' + href.toAttr(rfcDocUrl("rfcs/0001-app-registry-and-typed-nav.md")) + ' style="color:var(--st-amber-dk);text-decoration:underline;">RFC 0001</a>'
            +      ' &nbsp;·&nbsp; sections <code>' + escape(s.rfcSection) + '</code>'
            + '  </p>'
            + '</div>'

            + '<div class="' + cn(st_panel) + '">'
            + '  <div class="' + cn(st_panel_title) + '">Notes</div>'
            + '  <div class="' + cn(st_doc) + '">' + notesHtml + '</div>'
            + '</div>';

        // prev / next navigation
        var idNum = parseInt(s.id, 10);
        if (!isNaN(idNum)) {
            var prevId = idNum > 1 ? String(idNum - 1).padStart(2, "0") : null;
            var nextId = String(idNum + 1).padStart(2, "0");
            html += '<div style="display:flex;justify-content:space-between;margin-top:24px;font-size:13px;">'
                  + '  <span>'
                  +    (prevId ? '<a ' + href.toAttr(stepUrl(prevId)) + ' style="color:var(--st-amber-dk);text-decoration:underline;">← Step ' + prevId + '</a>' : '')
                  + '  </span>'
                  + '  <a ' + href.toAttr(planUrl()) + ' style="color:var(--st-gray-mid);text-decoration:underline;">All steps</a>'
                  + '  <span>'
                  + '    <a ' + href.toAttr(stepUrl(nextId)) + ' style="color:var(--st-amber-dk);text-decoration:underline;">Step ' + nextId + ' →</a>'
                  + '  </span>'
                  + '</div>';
        }

        stepRoot.innerHTML = html;
    }
}
