(function(){
  var validViews = /*VIEWS*/[];

  function showView(name) {
    if (validViews.indexOf(name) === -1) name = "overview";
    document.querySelectorAll(".view").forEach(function(v){ v.classList.remove("active"); });
    var target = document.getElementById("view-" + name);
    if (target) target.classList.add("active");
    document.querySelectorAll(".nav-item").forEach(function(n){
      n.classList.toggle("active", n.getAttribute("data-view") === name);
    });
    document.getElementById("crumb").textContent = name;
    var mainEl = document.querySelector(".main"); if (mainEl) mainEl.scrollTop = 0; window.scrollTo(0, 0);
    closeSidebarMobile();
  }

  function currentHash() {
    return (location.hash || "").replace("#", "");
  }

  window.addEventListener("hashchange", function(){ showView(currentHash()); });

  document.querySelectorAll(".nav-item").forEach(function(item){
    item.addEventListener("click", function(){
      var v = item.getAttribute("data-view");
      location.hash = v;
      showView(v);
    });
  });

  showView(currentHash());

  // ---------- Copy buttons ----------
  document.querySelectorAll(".copy-btn").forEach(function(btn){
    btn.addEventListener("click", function(){
      var card = btn.closest(".code-card");
      var codeEl = card.querySelector("pre.code-block code");
      var text = codeEl.textContent;
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
      var text = (item.textContent + " " + (item.getAttribute("data-kw") || "")).toLowerCase();
      item.classList.toggle("hidden-by-search", q.length > 0 && text.indexOf(q) === -1);
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
