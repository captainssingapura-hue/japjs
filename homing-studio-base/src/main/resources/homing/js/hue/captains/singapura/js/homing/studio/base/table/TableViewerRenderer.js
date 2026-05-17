// =============================================================================
// TableViewerRenderer — shared renderer for TableViewer (RFC 0020).
//
// renderTable({ docId, host }) → void
//
// Fetches /doc?id=<docId> (JSON: { headers:[Cell], rows:[[Cell]] }) and
// builds a <table> in `host` using the framework's typed cell CSS.
// Slim: colspan/rowspan/badge/align only. No sort/filter/edit.
// =============================================================================

function _alignClass(align) {
    if (align === "left")   return st_td_align_left;
    if (align === "center") return st_td_align_center;
    if (align === "right")  return st_td_align_right;
    return null;
}

function _badgeClass(badge) {
    if (badge === "success") return st_td_badge_success;
    if (badge === "warning") return st_td_badge_warning;
    if (badge === "error")   return st_td_badge_error;
    return null;
}

function _applyCellAttrs(el, cell) {
    if (cell.colspan && cell.colspan > 1) el.setAttribute("colspan", String(cell.colspan));
    if (cell.rowspan && cell.rowspan > 1) el.setAttribute("rowspan", String(cell.rowspan));
    var alignCls = _alignClass(cell.align);
    if (alignCls) css.addClass(el, alignCls);
}

function _renderCellContent(el, cell) {
    var badgeCls = _badgeClass(cell.badge);
    if (badgeCls) {
        var span = document.createElement("span");
        css.addClass(span, badgeCls);
        span.textContent = cell.text || "";
        el.appendChild(span);
    } else {
        el.textContent = cell.text || "";
    }
}

function renderTable(props) {
    var docId = props.docId;
    var host  = props.host;

    if (!docId) {
        var errMsg = document.createElement("div");
        css.addClass(errMsg, st_error);
        errMsg.textContent = "No table id supplied. Use ?id=<uuid>.";
        host.replaceChildren(errMsg);
        return;
    }

    var loading = document.createElement("div");
    css.addClass(loading, st_loading);
    loading.textContent = "Loading…";
    host.replaceChildren(loading);

    fetch("/doc?id=" + encodeURIComponent(docId))
        .then(function(r) {
            if (!r.ok) throw new Error("HTTP " + r.status);
            return r.json();
        })
        .then(function(data) {
            var table = document.createElement("table");
            css.addClass(table, st_table);

            var headers = data.headers || [];
            var rows    = data.rows    || [];

            if (headers.length > 0) {
                var thead = document.createElement("thead");
                css.addClass(thead, st_thead);
                var trh = document.createElement("tr");
                for (var hi = 0; hi < headers.length; hi++) {
                    var hc = headers[hi];
                    var th = document.createElement("th");
                    css.addClass(th, st_th);
                    _applyCellAttrs(th, hc);
                    _renderCellContent(th, hc);
                    trh.appendChild(th);
                }
                thead.appendChild(trh);
                table.appendChild(thead);
            }

            var tbody = document.createElement("tbody");
            for (var ri = 0; ri < rows.length; ri++) {
                var row = rows[ri];
                var tr = document.createElement("tr");
                for (var ci = 0; ci < row.length; ci++) {
                    var dc = row[ci];
                    var td = document.createElement("td");
                    css.addClass(td, st_td);
                    _applyCellAttrs(td, dc);
                    _renderCellContent(td, dc);
                    tr.appendChild(td);
                }
                tbody.appendChild(tr);
            }
            table.appendChild(tbody);

            host.replaceChildren(table);
        })
        .catch(function(err) {
            var errEl = document.createElement("div");
            css.addClass(errEl, st_error);
            errEl.textContent = "Failed to load table: " + err.message;
            host.replaceChildren(errEl);
        });
}
