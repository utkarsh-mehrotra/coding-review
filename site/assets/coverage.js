// LeetCode coverage view: summary, per-topic breakdown, searchable problem list, gaps.
// Data comes from <script type="application/json" id="coverage-data">, written by site/build.py
// from tools/lc-coverage/data/problems.json.
(function () {
  var dataEl = document.getElementById("coverage-data");
  if (!dataEl) return;
  var DATA = JSON.parse(dataEl.textContent);
  var TIERS = [
    { k: "A", name: "Verified", desc: "named in a template or practice map", c: "var(--tA)" },
    { k: "B", name: "Tag match", desc: "a specific tag points to one template", c: "var(--tB)" },
    { k: "C", name: "Likely", desc: "broad tag only, unconfirmed", c: "var(--tC)" },
    { k: "U", name: "Not covered", desc: "no template applies", c: "var(--tU)" }
  ];
  var DIFF = ["Easy", "Medium", "Hard"], DCLS = ["e", "m", "h"];
  var T = DATA.templates, TOP = DATA.topics, VIEW = DATA.templateView;
  // row: [id, title, slug, diff, paid, ac, tier, topic, tmpl, also[], practiceOnly]
  var R = DATA.rows;
  function $(id) { return document.getElementById(id); }
  function fmt(n) { return n.toLocaleString("en-US"); }
  function pct(a, b) { return b ? Math.round(100 * a / b) : 0; }
  function esc(s) { return String(s).replace(/[&<>"]/g, function (c) { return { "&": "&amp;", "<": "&lt;", ">": "&gt;", '"': "&quot;" }[c]; }); }
  function tierCounts(rows) { var c = [0, 0, 0, 0]; rows.forEach(function (r) { c[r[6]]++; }); return c; }
  function toList() { var h = $("cov-list-title"); if (h) h.scrollIntoView({ behavior: "smooth", block: "start" }); }

  var state = { q: "", tiers: { A: 1, B: 1, C: 1, U: 1 }, diffs: { 0: 1, 1: 1, 2: 1 }, topic: -2, free: false, sort: "id", dir: 1, limit: 200 };
  try { var saved = JSON.parse(localStorage.getItem("coding-review-coverage") || "null"); if (saved) state.free = !!saved.free; } catch (e) {}

  // ---- summary
  var all = tierCounts(R), n = R.length, covered = all[0] + all[1];
  $("cov-lede").textContent = fmt(covered) + " of " + fmt(n) + " LeetCode algorithm problems (" + pct(covered, n) +
    "%) map to a template on this site: " + fmt(all[0]) + " named in the template code or practice map, " + fmt(all[1]) +
    " matched by a specific LeetCode tag. Another " + fmt(all[2]) + " are likely matches that need a closer look.";
  var bar = $("cov-bigbar");
  bar.setAttribute("aria-label", TIERS.map(function (t, i) { return t.name + " " + all[i]; }).join(", "));
  TIERS.forEach(function (t, i) {
    var d = document.createElement("div"); d.style.flex = all[i]; d.style.background = t.c; d.title = t.name + ": " + fmt(all[i]); bar.appendChild(d);
    var b = document.createElement("button"); b.type = "button"; b.className = "cov-leg"; b.style.setProperty("--c", t.c);
    b.innerHTML = "<em>" + t.k + " &middot; " + t.name + "</em><b>" + fmt(all[i]) + "</b><span>" + pct(all[i], n) + "% &middot; " + t.desc + "</span>";
    b.setAttribute("aria-label", "Show only " + t.name);
    b.onclick = function () { state.tiers = {}; state.tiers[t.k] = 1; syncChips(); render(true); toList(); };
    $("cov-legend").appendChild(b);
  });
  var exN = 0, exParts = [];
  Object.keys(DATA.excluded).forEach(function (k) { exN += DATA.excluded[k]; exParts.push(DATA.excluded[k] + " " + k); });
  $("cov-excl").textContent = fmt(n) + " algorithm problems in total. Left out: " + fmt(exN) + " non-algorithm problems (" + exParts.join(", ") + ").";

  // ---- topic table + difficulty bars
  function mini(c, total) {
    return '<div class="cov-mini">' + [0, 1, 2].map(function (i) { return '<div style="flex:' + c[i] + ';background:' + TIERS[i].c + '"></div>'; }).join("") +
      '<div style="flex:' + (total - c[0] - c[1] - c[2]) + '"></div></div>';
  }
  var per = TOP.map(function (t, i) { var rows = R.filter(function (r) { return r[7] === i; }); return { i: i, name: t[1], rows: rows, c: tierCounts(rows) }; })
    .sort(function (a, b) { return (b.c[0] + b.c[1]) - (a.c[0] + a.c[1]); });
  var maxT = Math.max.apply(null, per.map(function (o) { return o.rows.length; }));
  per.forEach(function (o) {
    var tr = document.createElement("tr"); tr.className = "cov-topic"; tr.tabIndex = 0; tr.setAttribute("role", "button"); tr.setAttribute("aria-label", "Filter to " + o.name);
    tr.innerHTML = "<td>" + esc(o.name) + '</td><td class="r">' + o.c[0] + '</td><td class="r">' + o.c[1] + '</td><td class="r">' + o.c[2] +
      '</td><td class="r cov-hide-sm">' + o.rows.length + '</td><td><div style="width:' + Math.max(8, 100 * o.rows.length / maxT) + '%">' + mini(o.c, o.c[0] + o.c[1] + o.c[2]) + "</div></td>";
    function go() { state.topic = o.i; $("cov-topic").value = String(o.i); render(true); toList(); }
    tr.onclick = go; tr.onkeydown = function (e) { if (e.key === "Enter" || e.key === " ") { e.preventDefault(); go(); } };
    $("cov-topics").appendChild(tr);
  });
  var cov = [];
  DIFF.forEach(function (name, di) {
    var rows = R.filter(function (r) { return r[3] === di; }), c = tierCounts(rows);
    cov.push(pct(c[0] + c[1], rows.length));
    var row = document.createElement("div"); row.className = "cov-diffrow";
    row.innerHTML = '<span class="cov-d-' + DCLS[di] + '">' + name + "</span>" + mini(c, rows.length) + '<span class="r">' + cov[di] + "%</span>";
    $("cov-diffs").appendChild(row);
  });
  $("cov-diffnote").textContent = "Bar: share of each level in A, B and C. Number: A + B coverage. Hard problems are the best covered (" + cov[2] +
    "%) because their tags name the technique: segment tree, bitmask DP, SCC. Easy problems are the least covered (" + cov[0] + "%): mostly plain array and string loops.";

  // ---- filters
  var topicSel = $("cov-topic");
  function opt(v, label) { var o = document.createElement("option"); o.value = v; o.textContent = label; topicSel.appendChild(o); }
  opt("-2", "All topics"); TOP.forEach(function (t, i) { opt(String(i), t[1]); }); opt("-1", "No topic (not covered)");
  topicSel.onchange = function () { state.topic = +topicSel.value; render(true); };
  var free = $("cov-free"); free.checked = state.free;
  free.onchange = function () { state.free = free.checked; try { localStorage.setItem("coding-review-coverage", JSON.stringify({ free: state.free })); } catch (e) {} render(true); };
  var qTimer; $("cov-q").oninput = function (e) { clearTimeout(qTimer); qTimer = setTimeout(function () { state.q = e.target.value.trim().toLowerCase(); render(true); }, 120); };

  var tierChips = $("cov-tierchips"), diffChips = $("cov-diffchips");
  TIERS.forEach(function (t) {
    var b = document.createElement("button"); b.type = "button"; b.className = "cov-chip"; b.dataset.t = t.k; b.style.setProperty("--c", t.c);
    b.innerHTML = "<i></i>" + t.k + " &middot; " + t.name;
    b.onclick = function () { if (state.tiers[t.k]) delete state.tiers[t.k]; else state.tiers[t.k] = 1; syncChips(); render(true); };
    tierChips.appendChild(b);
  });
  DIFF.forEach(function (d, i) {
    var b = document.createElement("button"); b.type = "button"; b.className = "cov-chip"; b.dataset.d = i; b.style.setProperty("--c", "var(--" + ["easy", "med", "hard"][i] + ")");
    b.innerHTML = "<i></i>" + d;
    b.onclick = function () { if (state.diffs[i]) delete state.diffs[i]; else state.diffs[i] = 1; syncChips(); render(true); };
    diffChips.appendChild(b);
  });
  function syncChips() {
    tierChips.querySelectorAll(".cov-chip").forEach(function (b) { b.setAttribute("aria-pressed", state.tiers[b.dataset.t] ? "true" : "false"); });
    diffChips.querySelectorAll(".cov-chip").forEach(function (b) { b.setAttribute("aria-pressed", state.diffs[b.dataset.d] ? "true" : "false"); });
  }
  syncChips();

  document.querySelectorAll("#view-coverage th.cov-sort").forEach(function (th) {
    th.onclick = function () { var k = th.dataset.k; state.dir = state.sort === k ? -state.dir : (k === "ac" ? -1 : 1); state.sort = k; render(true); };
  });
  $("cov-more").onclick = function () { state.limit += 200; render(false); };

  var KEY = {
    id: function (r) { return r[0]; }, title: function (r) { return r[1].toLowerCase(); }, diff: function (r) { return r[3]; },
    tier: function (r) { return r[6]; }, tmpl: function (r) { return r[8] < 0 ? "~" : T[r[8]].toLowerCase(); }, ac: function (r) { return r[5]; }
  };
  function filtered() {
    var q = state.q, numeric = /^\d+$/.test(q);
    var out = R.filter(function (r) {
      if (!state.tiers["ABCU"[r[6]]] || !state.diffs[r[3]] || (state.free && r[4])) return false;
      if (state.topic !== -2 && r[7] !== state.topic) return false;
      if (!q) return true;
      if (numeric) return String(r[0]).indexOf(q) === 0;
      return r[1].toLowerCase().indexOf(q) !== -1 || (r[8] >= 0 && T[r[8]].toLowerCase().indexOf(q) !== -1);
    });
    var f = KEY[state.sort], d = state.dir;
    return out.sort(function (a, b) { var x = f(a), y = f(b); return x < y ? -d : x > y ? d : a[0] - b[0]; });
  }
  function tmplCell(r) {
    if (r[8] < 0) return r[10] ? '<span class="cov-also">topic: ' + esc(TOP[r[7]][1]) + "</span>" : '<span class="cov-also">none</span>';
    var key = T[r[8]], v = VIEW[key.split("#")[0]];
    var label = esc(key).replace("#", "#<wbr>");
    var main = v ? '<a class="tpl" data-goto="' + v + '">' + label + "</a>" : '<span class="tpl">' + label + "</span>";
    var also = r[9].length ? '<span class="cov-also">also: ' + r[9].map(function (i) { return esc(T[i].split("#")[1]); }).join(", ") + "</span>" : "";
    return main + also;
  }
  function render(reset) {
    if (reset) state.limit = 200;
    var rows = filtered(), body = $("cov-rows");
    document.querySelectorAll("#view-coverage th.cov-sort").forEach(function (th) {
      th.setAttribute("aria-sort", th.dataset.k === state.sort ? (state.dir > 0 ? "ascending" : "descending") : "none");
    });
    var c = tierCounts(rows);
    $("cov-count").textContent = fmt(rows.length) + " problems · " + fmt(c[0] + c[1]) + " covered";
    var more = $("cov-more");
    if (!rows.length) {
      body.innerHTML = '<tr><td colspan="6" class="cov-empty">No problems match these filters. Clear the search or turn a match level back on.</td></tr>';
      more.hidden = true; return;
    }
    body.innerHTML = rows.slice(0, state.limit).map(function (r) {
      var t = "ABCU"[r[6]];
      return '<tr><td class="r cov-muted">' + r[0] + '</td><td><a class="cov-title" href="https://leetcode.com/problems/' + esc(r[2]) + '/" target="_blank" rel="noopener">' +
        esc(r[1]) + "</a>" + (r[4] ? '<span class="lc-lock" title="Premium">P</span>' : "") + '</td><td class="cov-d-' + DCLS[r[3]] + '">' + DIFF[r[3]] +
        '</td><td><span class="cov-pill cov-p' + t + '">' + t + " &middot; " + TIERS[r[6]].name + "</span></td><td>" + tmplCell(r) +
        '</td><td class="r cov-muted cov-hide-sm">' + r[5].toFixed(0) + "%</td></tr>";
    }).join("");
    more.hidden = rows.length <= state.limit;
    more.textContent = "Show " + Math.min(200, rows.length - state.limit) + " more of " + fmt(rows.length - state.limit);
  }
  render(true);

  // ---- gaps
  $("cov-zero-note").textContent = DATA.zero.length + " of " + DATA.decisionCount + " decision-tree templates are not the main match for any problem. " +
    "Some only need a problem number in their comments (Floyd-Warshall, copy random list); others are design or theory templates with no LeetCode equivalent (rate limiter, Bloom filter, 2-SAT).";
  $("cov-zero").innerHTML = DATA.zero.map(function (t) {
    var v = VIEW[t.split("#")[0]];
    return v ? '<a class="tpl" data-goto="' + v + '">' + esc(t) + "</a>" : '<span class="tpl">' + esc(t) + "</span>";
  }).join("");
  $("cov-utags").innerHTML = DATA.utags.map(function (p) { return '<span class="cov-tag">' + esc(p[0]) + ' <span class="cov-muted">' + p[1] + "</span></span>"; }).join("");
})();
