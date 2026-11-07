// Ballots page (csmNN/ballots.md): loads the CSV named by the table's data-src and shows every ballot, filterable.
(function () {
  var rows = [];
  var searchText = [];
  var filtered = [];
  var table = document.getElementById("ballots-table");
  var status = document.getElementById("ballots-status");

  function parseCsv(text) {
    var result = [], row = [], field = "", quoted = false;
    for (var i = 0; i < text.length; i++) {
      var c = text[i];
      if (quoted) {
        if (c === '"' && text[i + 1] === '"') { field += '"'; i++; }
        else if (c === '"') { quoted = false; }
        else { field += c; }
      } else if (c === '"') { quoted = true; }
      else if (c === ",") { row.push(field); field = ""; }
      else if (c === "\n") { row.push(field); result.push(row); row = []; field = ""; }
      else { field += c; }
    }
    if (field !== "" || row.length) { row.push(field); result.push(row); }
    return result;
  }

  function cell(tag, text, right) {
    var el = document.createElement(tag);
    el.textContent = text;
    if (right) el.className = "ballots-count";
    return el;
  }

  function render() {
    var body = table.tBodies[0];
    var fragment = document.createDocumentFragment();
    filtered.forEach(function (r) {
      var tr = document.createElement("tr");
      r.forEach(function (v, i) { tr.appendChild(cell("td", i === 0 ? Number(v).toLocaleString("en-US") : v, i === 0)); });
      fragment.appendChild(tr);
    });
    body.innerHTML = "";
    body.appendChild(fragment);
    status.textContent = filtered.length.toLocaleString("en-US") + " ballots shown";
  }

  fetch(table.dataset.src).then(function (r) { return r.text(); }).then(function (text) {
    var parsed = parseCsv(text);
    var header = parsed.shift();
    var tr = document.createElement("tr");
    header.forEach(function (h) { tr.appendChild(cell("th", h)); });
    table.tHead.appendChild(tr);
    rows = parsed.filter(function (r) { return r.length > 1; });
    // Lowercased candidate names per row, skipping the ballot count. Newlines keep terms from matching across names.
    searchText = rows.map(function (r) { return r.slice(1).join("\n").toLowerCase(); });
    filtered = rows;
    render();
  });

  var FILTER_DELAY_MS = 300;
  var filterTimer = null;

  function applyFilter(value) {
    // Every space-separated term must match some candidate on the ballot (AND), not necessarily the same one.
    var terms = value.toLowerCase().split(/\s+/).filter(function (t) { return t !== ""; });
    filtered = terms.length ? rows.filter(function (r, i) {
      return terms.every(function (t) { return searchText[i].indexOf(t) !== -1; });
    }) : rows;
    render();
  }

  // Rendering up to ~20,000 rows is slow, so wait until typing pauses.
  document.getElementById("ballots-filter").addEventListener("input", function (e) {
    clearTimeout(filterTimer);
    var value = e.target.value;
    filterTimer = setTimeout(function () { applyFilter(value); }, FILTER_DELAY_MS);
  });
})();
