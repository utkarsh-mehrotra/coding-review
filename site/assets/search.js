// Search helpers for the sidebar filter and the jump box. Plain ES5.
var CRSearch = (function(){
  var KIND_ORDER = { view: 0, template: 1, problem: 2 };

  // Digits only: match a whole number token. Otherwise every word must appear.
  function matchesSidebar(text, q) {
    q = String(q).trim().toLowerCase();
    if (!q) return true;
    text = String(text).toLowerCase();
    if (/^\d+$/.test(q)) return new RegExp("(^|\\D)" + q + "(\\D|$)").test(text);
    var words = q.split(/\s+/);
    for (var i = 0; i < words.length; i++) {
      var w = words[i];
      if (/^\d+$/.test(w)) {
        if (!new RegExp("(^|\\D)" + w + "(\\D|$)").test(text)) return false;
      } else if (text.indexOf(w) === -1) return false;
    }
    return true;
  }

  function wordStart(s, q) {
    var parts = s.split(/[^a-z0-9]+/);
    for (var i = 0; i < parts.length; i++) if (parts[i] && parts[i].indexOf(q) === 0) return true;
    return false;
  }

  // Lower score is better; -1 means no match.
  function score(entry, q, isNum) {
    var label = entry[0].toLowerCase();
    var hay = label + " " + entry[1].toLowerCase();
    if (isNum) {
      if (entry[4] === "problem" && label.split(" ")[0] === q) return 0;
      return new RegExp("(^|\\D)" + q + "(\\D|$)").test(hay) ? 4 : -1;
    }
    if (label.indexOf(q) === 0) return 1;
    if (wordStart(label, q)) return 2;
    if (hay.indexOf(q) !== -1) return 3;
    return -1;
  }

  function rank(index, q, limit) {
    q = String(q).trim().toLowerCase();
    if (!q) return [];
    if (limit === undefined) limit = 12;
    var isNum = /^\d+$/.test(q);
    var scored = [];
    for (var i = 0; i < index.length; i++) {
      var words = isNum ? [q] : q.split(/\s+/);
      var total = 0, ok = true;
      for (var j = 0; j < words.length; j++) {
        var s = score(index[i], words[j], /^\d+$/.test(words[j]));
        if (s < 0) { ok = false; break; }
        if (s > total) total = s;
      }
      if (ok) scored.push({ e: index[i], s: total });
    }
    scored.sort(function(a, b){
      if (a.s !== b.s) return a.s - b.s;
      var ka = KIND_ORDER[a.e[4]], kb = KIND_ORDER[b.e[4]];
      if (ka !== kb) return ka - kb;
      return a.e[0] < b.e[0] ? -1 : (a.e[0] > b.e[0] ? 1 : 0);
    });
    return scored.slice(0, limit).map(function(x){ return x.e; });
  }

  return { matchesSidebar: matchesSidebar, rank: rank };
})();
if (typeof module !== "undefined" && module.exports) module.exports = CRSearch; else window.CRSearch = CRSearch;
