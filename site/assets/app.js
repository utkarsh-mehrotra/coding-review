(function(){
  var validViews = /*VIEWS*/[];

  var shownView = null;

  function showView(name) {
    if (validViews.indexOf(name) === -1) name = "overview";
    shownView = name;
    document.querySelectorAll(".view").forEach(function(v){ v.classList.remove("active"); });
    var target = document.getElementById("view-" + name);
    if (target) target.classList.add("active");
    document.querySelectorAll(".nav-item").forEach(function(n){
      n.classList.toggle("active", n.getAttribute("data-view") === name);
    });
    document.getElementById("crumb").textContent = name;
    var mainEl = document.querySelector(".main"); if (mainEl) mainEl.scrollTop = 0; window.scrollTo(0, 0);
    closeSidebarMobile();
    var ev = document.createEvent("CustomEvent");
    ev.initCustomEvent("cr:view", false, false, { name: name });
    document.dispatchEvent(ev);
  }

  // Jump to a view, then to an anchor inside it (e.g. crGoto("union-find", "m-DSU-union")).
  window.crGoto = function(view, anchorId) {
    location.hash = view;
    showView(view);
    var el = anchorId ? document.getElementById(anchorId) : null;
    if (el && el.scrollIntoView) el.scrollIntoView();
  };

  function currentHash() {
    return (location.hash || "").replace("#", "");
  }

  window.addEventListener("hashchange", function(){
    var name = currentHash();
    if (name !== shownView) showView(name);   // crGoto already showed this view and scrolled
  });

  document.querySelectorAll(".nav-item").forEach(function(item){
    item.addEventListener("click", function(){
      var v = item.getAttribute("data-view");
      location.hash = v;
      showView(v);
    });
  });

  showView(currentHash());

  var printBtn = document.getElementById("printCheatsheet");
  if (printBtn) printBtn.addEventListener("click", function(){ window.print(); });

  // ---------- Copy buttons ----------
  // Text of a code element without the fold marks, so a copy is always the full file.
  function copyText(codeEl) {
    var clone = codeEl.cloneNode(true);
    clone.querySelectorAll(".fold-mark").forEach(function(m){ m.parentNode.removeChild(m); });
    return clone.textContent;
  }

  document.querySelectorAll(".copy-btn").forEach(function(btn){
    btn.addEventListener("click", function(){
      var card = btn.closest(".code-card");
      var codeEl = card.querySelector("pre.code-block code");
      var text = copyText(codeEl);
      function markCopied(){
        var original = btn.innerHTML;
        btn.classList.add("copied");
        btn.innerHTML = "✓ Copied";
        setTimeout(function(){ btn.classList.remove("copied"); btn.innerHTML = original; }, 1400);
      }
      if (navigator.clipboard && navigator.clipboard.writeText) {
        navigator.clipboard.writeText(text).then(markCopied).catch(function(){ fallbackCopy(text, markCopied); });
      } else {
        fallbackCopy(text, markCopied);
      }
    });
  });

  function fallbackCopy(text, cb) {
    var ta = document.createElement("textarea");
    ta.value = text;
    ta.style.position = "fixed";
    ta.style.opacity = "0";
    document.body.appendChild(ta);
    ta.select();
    try { document.execCommand("copy"); } catch(e) {}
    document.body.removeChild(ta);
    cb();
  }

  // ---------- Search filter ----------
  document.getElementById("searchInput").addEventListener("input", function(e){
    var q = e.target.value.trim().toLowerCase();
    document.querySelectorAll(".nav-item").forEach(function(item){
      var text = item.textContent + " " + (item.getAttribute("data-kw") || "");
      item.classList.toggle("hidden-by-search", q.length > 0 && !CRSearch.matchesSidebar(text, q));
    });
  });

  // ---------- Theme toggle (cycles auto -> light -> dark) ----------
  var root = document.documentElement;
  var themeState = "auto";
  var icon = document.getElementById("themeIcon");
  var label = document.getElementById("themeLabel");
  function applyTheme(){
    if (themeState === "auto") { root.removeAttribute("data-theme"); icon.textContent = "◐"; label.textContent = "Auto"; }
    else if (themeState === "light") { root.setAttribute("data-theme","light"); icon.textContent = "○"; label.textContent = "Light"; }
    else { root.setAttribute("data-theme","dark"); icon.textContent = "●"; label.textContent = "Dark"; }
  }
  try {
    var saved = localStorage.getItem("coding-review-theme");
    if (saved) themeState = saved;
  } catch(e) {}
  applyTheme();
  document.getElementById("themeToggle").addEventListener("click", function(){
    themeState = themeState === "auto" ? "light" : (themeState === "light" ? "dark" : "auto");
    applyTheme();
    try { localStorage.setItem("coding-review-theme", themeState); } catch(e) {}
  });

  // ---------- Essentials toggle (hides imports and private helpers) ----------
  var essBtn = document.getElementById("essentialsToggle");
  function applyEssentials(on){
    root.classList.toggle("essentials", on);
    essBtn.setAttribute("aria-pressed", on ? "true" : "false");
    essBtn.textContent = on ? "Show full code" : "Show essentials";
  }
  var essOn = false;
  try { essOn = localStorage.getItem("coding-review-essentials") === "1"; } catch(e) {}
  applyEssentials(essOn);
  essBtn.addEventListener("click", function(){
    essOn = !essOn;
    applyEssentials(essOn);
    try { localStorage.setItem("coding-review-essentials", essOn ? "1" : "0"); } catch(e) {}
  });

  // ---------- Jump box ----------
  var jumpIndex = [];
  try { jumpIndex = JSON.parse(document.getElementById("jump-index").textContent); } catch(e) {}
  var jumpBackdrop = document.getElementById("jumpBackdrop");
  var jumpInput = document.getElementById("jumpInput");
  var jumpList = document.getElementById("jumpList");
  var jumpStatus = document.getElementById("jumpStatus");
  var jumpButton = document.getElementById("jumpButton");
  var jumpResults = [], jumpActive = 0, jumpOpener = null;

  function jumpIsOpen() { return !jumpBackdrop.hidden; }

  function jumpSetActive(i, scroll) {
    var rows = jumpList.children;
    if (!rows.length) { jumpActive = 0; jumpInput.setAttribute("aria-activedescendant", ""); return; }
    jumpActive = (i + rows.length) % rows.length;
    for (var k = 0; k < rows.length; k++) {
      var on = k === jumpActive;
      rows[k].classList.toggle("active", on);
      rows[k].setAttribute("aria-selected", on ? "true" : "false");
    }
    jumpInput.setAttribute("aria-activedescendant", rows[jumpActive].id);
    if (scroll && rows[jumpActive].scrollIntoView) rows[jumpActive].scrollIntoView({ block: "nearest" });
  }

  function jumpRender() {
    var q = jumpInput.value;
    jumpResults = CRSearch.rank(jumpIndex, q, 12);
    jumpList.textContent = "";
    jumpInput.setAttribute("aria-expanded", jumpResults.length ? "true" : "false");
    jumpStatus.textContent = (!jumpResults.length && q.trim()) ? "No matches." : "";
    if (!jumpResults.length) { jumpSetActive(0); return; }
    jumpResults.forEach(function(r, i){
      var li = document.createElement("li");
      li.className = "jump-row";
      li.setAttribute("role", "option");
      li.id = "jump-opt-" + i;
      var a = document.createElement("span"); a.className = "jump-label"; a.textContent = r[0];
      var b = document.createElement("span"); b.className = "jump-sub"; b.textContent = r[1];
      var c = document.createElement("span"); c.className = "jump-kind"; c.textContent = r[4];
      li.appendChild(a); li.appendChild(b); li.appendChild(c);
      li.addEventListener("mousemove", function(){ if (jumpActive !== i) jumpSetActive(i); });
      li.addEventListener("click", function(){ jumpGo(i); });
      jumpList.appendChild(li);
    });
    jumpSetActive(0);
  }

  function jumpOpen() {
    if (jumpIsOpen()) { jumpInput.focus(); return; }
    jumpOpener = document.activeElement;
    jumpBackdrop.hidden = false;
    jumpInput.value = "";
    jumpRender();
    jumpInput.focus();
  }

  function jumpClose() {
    if (!jumpIsOpen()) return;
    jumpBackdrop.hidden = true;
    jumpInput.setAttribute("aria-expanded", "false");
    var o = jumpOpener; jumpOpener = null;
    if (o && o.focus) o.focus();
  }

  function jumpGo(i) {
    var r = jumpResults[i];
    if (!r) return;
    jumpClose();
    window.crGoto(r[2], r[3]);
  }

  jumpButton.addEventListener("click", jumpOpen);
  jumpInput.addEventListener("input", jumpRender);
  jumpBackdrop.addEventListener("mousedown", function(e){
    if (e.target === jumpBackdrop) { e.preventDefault(); jumpClose(); }
  });
  jumpBackdrop.addEventListener("keydown", function(e){
    if (e.key === "Escape") { e.preventDefault(); jumpClose(); }
    else if (e.key === "ArrowDown") { e.preventDefault(); jumpSetActive(jumpActive + 1, true); }
    else if (e.key === "ArrowUp") { e.preventDefault(); jumpSetActive(jumpActive - 1, true); }
    else if (e.key === "Enter") {
      if (e.isComposing || e.keyCode === 229) return; e.preventDefault(); jumpGo(jumpActive); }
    else if (e.key === "Tab") { e.preventDefault(); jumpInput.focus(); }
  });
  document.addEventListener("keydown", function(e){
    if ((e.metaKey || e.ctrlKey) && !e.altKey && (e.key === "k" || e.key === "K")) {
      e.preventDefault();
      jumpOpen();
      return;
    }
    if (e.key === "/" && !e.metaKey && !e.ctrlKey && !e.altKey && !jumpIsOpen()) {
      var a = document.activeElement, tag = a && a.tagName;
      if (tag === "INPUT" || tag === "TEXTAREA" || tag === "SELECT" || (a && a.isContentEditable)) return;
      e.preventDefault();
      jumpOpen();
    }
  });

  // ---------- Mobile sidebar ----------
  var sidebar = document.getElementById("sidebar");
  var overlay = document.getElementById("sidebarOverlay");
  document.getElementById("menuToggle").addEventListener("click", function(){
    sidebar.classList.toggle("open");
    overlay.classList.toggle("show");
  });
  overlay.addEventListener("click", function(){
    sidebar.classList.remove("open");
    overlay.classList.remove("show");
  });
  function closeSidebarMobile(){
    if (sidebar && overlay && window.innerWidth <= 860) {
      sidebar.classList.remove("open");
      overlay.classList.remove("show");
    }
  }
})();
