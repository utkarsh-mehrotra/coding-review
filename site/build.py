#!/usr/bin/env python3
"""Generates site/index.html from topics/*/topic.json and the Java templates.

  python3 site/build.py          build the page
  python3 site/build.py --check  validate only (template-first rule), no output

Template-first rule enforced by --check:
  * every decision-tree entry links to File#member that exists in that Java file
  * every view's Java files exist, and every template file is shown in some view
  * every view fragment placeholder {{code:File.java}} resolves
  * every LeetCode problem shown on the page (tools/lc-coverage/data/problems.json, tiers A and B)
    points at File#member that exists in that topic's Java files
"""
import html
import json
import re
import sys
from pathlib import Path

ROOT = Path(__file__).resolve().parent.parent
TOPICS = ROOT / "topics"
ASSETS = ROOT / "site" / "assets"
OUT = ROOT / "site" / "index.html"
PROBLEMS = ROOT / "tools" / "lc-coverage" / "data" / "problems.json"
SHOWN_TIERS = ("A", "B")     # A: named in a template or practice map, B: specific tag match
GROUP_PREVIEW = 12           # problems shown per template before "show more"

KEYWORDS = set("""abstract assert boolean break byte case catch char class const continue default do double else enum
extends final finally float for goto if implements import instanceof int interface long native new package private
protected public return short static strictfp super switch synchronized this throw throws transient try void volatile
while var true false null record""".split())

TOKEN = re.compile(
    r"(?P<com>//[^\n]*|/\*.*?\*/)|(?P<str>\"(?:\\.|[^\"\\])*\"|'(?:\\.|[^'\\])*')|(?P<id>[A-Za-z_][A-Za-z0-9_]*)",
    re.S,
)


def highlight(src: str) -> str:
    out, pos = [], 0
    for m in TOKEN.finditer(src):
        out.append(html.escape(src[pos:m.start()], quote=False))
        text = m.group(0)
        if m.lastgroup == "com":
            out.append('<span class="tok-com">%s</span>' % html.escape(text, quote=False))
        elif m.lastgroup == "str":
            out.append('<span class="tok-str">%s</span>' % html.escape(text, quote=False))
        else:
            if text in KEYWORDS:
                out.append('<span class="tok-kw">%s</span>' % text)
            elif text[0].isupper() and not text.isupper():
                out.append('<span class="tok-type">%s</span>' % text)
            else:
                out.append(text)
        pos = m.end()
    out.append(html.escape(src[pos:], quote=False))
    return "".join(out)


def load_topics():
    topics = []
    for p in sorted(TOPICS.glob("*/topic.json")):
        t = json.loads(p.read_text())
        t["_dir"] = p.parent
        topics.append(t)
    topics.sort(key=lambda t: t.get("order", 99))
    return topics


def java_members(path: Path):
    """Method, nested class and field-ish names declared in a Java file (regex level, good enough for linking)."""
    src = path.read_text()
    names = set(re.findall(r"\bclass\s+(\w+)", src))
    names |= set(re.findall(r"\b(\w+)\s*\([^;{)]*\)\s*(?:throws [\w, ]+)?\s*\{", src))
    names -= KEYWORDS | {"if", "for", "while", "switch", "catch", "synchronized"}
    return names


def load_problems():
    """A/B problems that name a template, grouped by template key File#member."""
    if not PROBLEMS.exists():
        return {}
    by = {}
    for r in json.loads(PROBLEMS.read_text())["problems"]:
        if r["tier"] in SHOWN_TIERS and r["templates"]:
            by.setdefault(r["templates"][0], []).append(r)
    for rows in by.values():
        rows.sort(key=lambda r: (r["tier"], r["paid"], r["id"]))
    return by


def check(topics, problems=None):
    errors, files_shown = [], set()
    all_members = {}
    for t in topics:
        d = t["_dir"]
        java = {p.name: p for p in d.glob("*.java")}
        members = {n: java_members(p) for n, p in java.items()}
        for n, ms in members.items():
            all_members[n[:-5]] = ms
        for v in t["views"]:
            for f in v["files"]:
                if f["file"] not in java:
                    errors.append("%s: view %s lists missing file %s" % (t["id"], v["id"], f["file"]))
                files_shown.add((t["id"], f["file"]))
            if v.get("fragment"):
                frag = d / v["fragment"]
                if not frag.exists():
                    errors.append("%s: missing fragment %s" % (t["id"], v["fragment"]))
                else:
                    for ref in re.findall(r"\{\{code:([^}]+)\}\}", frag.read_text()):
                        if ref not in java:
                            errors.append("%s: fragment %s references unknown file %s" % (t["id"], v["fragment"], ref))
        for dec in t["decisions"]:
            tpl = dec.get("template", "")
            if "#" not in tpl:
                errors.append("%s: decision without File#member template: %s" % (t["id"], re.sub("<.*?>", "", dec["q"])[:60]))
                continue
            fname, member = tpl.split("#", 1)
            if fname + ".java" not in java:
                errors.append("%s: template file %s.java not found (%s)" % (t["id"], fname, tpl))
            elif member not in members[fname + ".java"]:
                errors.append("%s: member %s not found in %s.java" % (t["id"], member, fname))
        for name in java:
            if (t["id"], name) not in files_shown:
                errors.append("%s: template %s is not shown in any view" % (t["id"], name))
    for key, rows in (problems or {}).items():
        fname, member = key.split("#", 1)
        if fname not in all_members:
            errors.append("problems.json: %d problems point at missing file %s.java (e.g. LC %s)" % (len(rows), fname, rows[0]["id"]))
        elif member not in all_members[fname]:
            errors.append("problems.json: %d problems point at missing member %s (e.g. LC %s)" % (len(rows), key, rows[0]["id"]))
    return errors


DIFF_CLASS = {"Easy": "e", "Medium": "m", "Hard": "h"}


def problem_pill(r):
    return '<a class="lc-pill lc-%s lc-%s" href="https://leetcode.com/problems/%s/" target="_blank" rel="noopener" title="%s · %s%s">' \
           '<span class="lc-id">%s</span>%s%s</a>' % (
               DIFF_CLASS[r["difficulty"]], r["tier"].lower(), html.escape(r["slug"]),
               r["difficulty"], "verified: named in the template" if r["tier"] == "A" else "matched by LeetCode tag",
               " · premium" if r["paid"] else "", r["id"], html.escape(r["title"]),
               '<span class="lc-lock">P</span>' if r["paid"] else "")


def problems_section(topic, view, problems):
    """LeetCode problems grouped by template member, for the files shown in this view."""
    groups = []
    for f in view["files"]:
        fname = f["file"][:-5]
        src = (topic["_dir"] / f["file"]).read_text()
        keys = [k for k in problems if k.split("#", 1)[0] == fname]
        # order members as they appear in the file
        keys.sort(key=lambda k: (src.find(k.split("#", 1)[1]) if k.split("#", 1)[1] in src else 1 << 30, k))
        groups += keys
    if not groups:
        return ""
    total = sum(len(problems[k]) for k in groups)
    n_a = sum(1 for k in groups for r in problems[k] if r["tier"] == "A")
    out = ['<h2 class="section-title">LeetCode problems <span class="lc-count">%d</span></h2>' % total,
           '<p class="lc-legend"><span class="lc-key lc-a">solid</span> %d named in the template &nbsp; '
           '<span class="lc-key lc-b">dashed</span> %d matched by LeetCode tag &nbsp; '
           '<span class="lc-dot lc-e"></span>Easy <span class="lc-dot lc-m"></span>Medium <span class="lc-dot lc-h"></span>Hard &nbsp; '
           '<span class="lc-lock">P</span> premium</p>' % (n_a, total - n_a)]
    for k in groups:
        rows = problems[k]
        head, rest = rows[:GROUP_PREVIEW], rows[GROUP_PREVIEW:]
        pills = "".join(problem_pill(r) for r in head)
        if rest:
            pills += '<details class="lc-more"><summary>show %d more</summary><div class="lc-list">%s</div></details>' % (
                len(rest), "".join(problem_pill(r) for r in rest))
        out.append('<div class="lc-group"><div class="lc-head"><span class="tpl">%s</span><span class="lc-n">%d</span></div>'
                   '<div class="lc-list">%s</div></div>' % (html.escape(k), len(rows), pills))
    return "\n".join(out)


def code_card(topic, fname, desc, badge):
    src = (topic["_dir"] / fname).read_text().rstrip() + "\n"
    return (
        '<div class="code-card">\n  <div class="code-card-head">\n    <div>\n'
        '      <div class="cc-title">%s</div>\n      <div class="cc-desc">%s</div>\n    </div>\n'
        '    <div class="cc-meta">\n      <span class="cc-badge">%s</span>\n'
        '      <button class="copy-btn">&#10697; Copy</button>\n    </div>\n  </div>\n'
        '  <pre class="code-block"><code>%s</code></pre>\n</div>'
    ) % (fname, html.escape(desc, quote=False) if "<" not in desc else desc, badge, highlight(src))


def build_view(topic, view, number, problems=None):
    d = topic["_dir"]
    meta = {f["file"]: f for f in view["files"]}
    kicker = "%02d &mdash; %s" % (number, " + ".join(f["file"].upper() for f in view["files"]))
    parts = ['<div class="page-kicker">%s</div>' % kicker]
    if view.get("fragment"):
        frag = (d / view["fragment"]).read_text()
        frag = re.sub(
            r"\{\{code:([^}]+)\}\}",
            lambda m: code_card(topic, m.group(1), meta[m.group(1)]["desc"], meta[m.group(1)]["badge"]),
            frag,
        )
        parts.append(frag)
    else:
        parts.append('<h1 class="page-title">%s</h1>' % html.escape(view["title"], quote=False))
        parts.append('<p class="page-intro">%s</p>' % html.escape(view["intro"], quote=False))
        if view.get("table"):
            rows = ['<tr><th>Pattern</th><th>Use case</th><th>Complexity</th></tr>']
            for a, b, c in view["table"]:
                rows.append('<tr><td>%s</td><td>%s</td><td class="complexity">%s</td></tr>' % (
                    html.escape(a, quote=False), html.escape(b, quote=False), html.escape(c, quote=False)))
            parts.append('<table class="ref-table">\n%s\n</table>' % "\n".join(rows))
        for f in view["files"]:
            parts.append(code_card(topic, f["file"], f["desc"], f["badge"]))
        if view.get("pitfalls"):
            parts.append('<h2 class="section-title">Pitfalls</h2>\n<ol class="decision-list">\n%s\n</ol>' % "\n".join(
                "<li>%s</li>" % html.escape(p, quote=False).replace("`", "") for p in view["pitfalls"]))
    section = problems_section(topic, view, problems or {})
    if section:
        parts.append(section)
    return '<div class="view" id="view-%s">\n%s\n</div>' % (view["id"], "\n".join(parts))


def view_of_file(topic):
    m = {}
    for v in topic["views"]:
        for f in v["files"]:
            m[f["file"][:-5]] = v["id"]
    return m


def build_overview(topics, counts, problems=None):
    p = []
    p.append('<div class="page-kicker">00 &mdash; START HERE</div>')
    p.append('<h1 class="page-title">Coding Interview Review &mdash; FAANG SDE2 / SDE3 / Staff (Java)</h1>')
    p.append('<p class="page-intro">Pattern-first review for coding rounds only. Every entry below ends in a tested, paste-ready Java template; '
             'pick the topic, read the decision tree, copy the template, adapt the state.</p>')
    p.append('<div class="rule-block"><strong>Template-first rule:</strong> nothing is listed here without a Java template behind it. '
             'The page is generated from the same files that the test suite compiles and runs, so what you copy is what passed the tests.</div>')
    problems = problems or {}
    if problems:
        rows = [r for v in problems.values() for r in v]
        n_a = sum(1 for r in rows if r["tier"] == "A")
        p.append('<div class="calib-block"><strong>LeetCode coverage:</strong> %s problems map to a template here: %s named in the template code or practice map, '
                 '%s matched by a specific LeetCode tag. Each template view lists its problems under the code. '
                 'Type a problem number or title in the filter box to find its template.</div>' % (
                     format(len(rows), ","), format(n_a, ","), format(len(rows) - n_a, ",")))
    p.append('<h2 class="section-title">Topics</h2>\n<div class="topic-grid">')
    for t in topics:
        first = t["views"][0]["id"]
        p.append('<a class="topic-card" data-goto="%s"><strong>%s</strong><div class="tc-meta">%d %s &middot; %d %s &middot; %d decisions</div></a>' % (
            first, html.escape(t["title"]), len(t["views"]), "view" if len(t["views"]) == 1 else "views",
            counts[t["id"]], "template file" if counts[t["id"]] == 1 else "template files", len(t["decisions"])))
    p.append('</div>')
    p.append('<h2 class="section-title">Decision Tree &mdash; &ldquo;which pattern do I reach for?&rdquo;</h2>')
    for t in topics:
        vf = view_of_file(t)
        p.append('<h3 class="group-title">%s</h3>\n<ol class="decision-list">' % html.escape(t["title"]))
        for dec in t["decisions"]:
            fname, member = dec["template"].split("#", 1)
            p.append('<li><strong>%s</strong> <span class="arrow">&rarr;</span> %s<a class="tpl" data-goto="%s">%s</a></li>' % (
                re.sub(r"^<strong>|</strong>$", "", dec["q"]).replace("<strong>", "").replace("</strong>", ""),
                dec["a"], vf[fname], html.escape(dec["template"])))
        p.append('</ol>')
    p.append('<h2 class="section-title">Level Calibration</h2>')
    for t in topics:
        p.append('<h3 class="group-title">%s</h3>' % html.escape(t["title"]))
        for c in t["calibration"]:
            p.append('<div class="calib-block">%s</div>' % c)
    p.append('<h2 class="section-title">Practice Problem Map</h2>\n<table class="ref-table">')
    for t in topics:
        p.append('<tr><th colspan="2">%s</th></tr>' % html.escape(t["title"]))
        for pat, probs in t["practice"]:
            p.append('<tr><td>%s</td><td>%s</td></tr>' % (pat, "".join('<span class="problem-pill">%s</span>' % x for x in probs)))
    p.append('</table>')
    return '<div class="view" id="view-overview">\n%s\n</div>' % "\n".join(p)


def main():
    topics = load_topics()
    problems = load_problems()
    errors = check(topics, problems)
    if errors:
        print("CHECK FAILED (%d):" % len(errors))
        for e in errors:
            print("  -", e)
        sys.exit(1)
    n_dec = sum(len(t["decisions"]) for t in topics)
    n_files = sum(len(list(t["_dir"].glob("*.java"))) for t in topics)
    if "--check" in sys.argv:
        print("check ok: %d topics, %d template files, %d decision entries all link to real members; "
              "%d LeetCode problems link to %d real template members" % (
                  len(topics), n_files, n_dec, sum(map(len, problems.values())), len(problems)))
        return

    counts = {t["id"]: len(list(t["_dir"].glob("*.java"))) for t in topics}
    n_lc = sum(map(len, problems.values()))
    nav, views, view_ids, number = [], [], ["overview"], 0
    nav.append('<div class="sidebar-label">Reference</div>')
    nav.append('<div class="nav-item" data-view="overview"><span class="fname">00</span>Overview &amp; Decision Tree</div>')
    last_group = None
    for t in topics:
        for v in t["views"]:
            number += 1
            group = (t.get("groups", {}).get(v["id"])) or v.get("group") or t["title"]
            if group != last_group:
                nav.append('<div class="sidebar-label">%s</div>' % html.escape(group))
                last_group = group
            kws = [t["title"], v["title"], v.get("intro", "")]
            for f in v["files"]:
                kws.append(f["file"])
                kws.extend(sorted(java_members(t["_dir"] / f["file"])))
            for dec in t["decisions"]:
                if dec["template"].split("#")[0] + ".java" in {f["file"] for f in v["files"]}:
                    kws.append(re.sub("<.*?>", "", dec["q"]))
            view_files = {f["file"][:-5] for f in v["files"]}
            for key, rows in problems.items():
                if key.split("#", 1)[0] in view_files:
                    kws.extend("%s %s" % (r["id"], r["title"]) for r in rows)
            kw = html.escape(" ".join(kws), quote=True)
            nav.append('<div class="nav-item" data-view="%s" data-kw="%s"><span class="fname">%02d</span>%s</div>' % (
                v["id"], kw, number, html.escape(v["title"], quote=False)))
            views.append(build_view(t, v, number, problems))
            view_ids.append(v["id"])
    overview = build_overview(topics, counts, problems)

    css = (ASSETS / "style.css").read_text()
    js = (ASSETS / "app.js").read_text().replace("/*VIEWS*/[]", json.dumps(view_ids))
    js += """
document.addEventListener("click", function(e){
  var g = e.target.closest("[data-goto]");
  if (!g) return;
  location.hash = g.getAttribute("data-goto");
});
"""
    page = f"""<!DOCTYPE html>
<html lang="en">
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1, viewport-fit=cover">
<title>Coding Review &mdash; Java Templates</title>
<style>
{css}
</style>
</head>
<body>
<div class="app">

  <div class="topbar">
    <button class="menu-toggle" id="menuToggle" aria-label="Toggle navigation">
      <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><line x1="3" y1="6" x2="21" y2="6"/><line x1="3" y1="12" x2="21" y2="12"/><line x1="3" y1="18" x2="21" y2="18"/></svg>
    </button>
    <div class="brand"><span class="dot">&#9679;</span><strong>coding-review</strong><span class="sub">&nbsp;&mdash; Java templates, SDE2 / SDE3 / Staff</span></div>
    <div class="search-wrap">
      <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><circle cx="11" cy="11" r="7"/><line x1="21" y1="21" x2="16.65" y2="16.65"/></svg>
      <input id="searchInput" type="text" placeholder="Filter: topic, algorithm, method&hellip;" autocomplete="off">
    </div>
    <button class="theme-toggle" id="themeToggle"><span id="themeIcon">&#9680;</span><span id="themeLabel">Auto</span></button>
  </div>

  <div class="sidebar-overlay" id="sidebarOverlay"></div>

  <div class="body-row">
    <nav class="sidebar" id="sidebar">
      {chr(10).join(nav)}
    </nav>

    <main class="main">
      <div class="tabbar"><div class="crumb" id="crumb">overview</div></div>
      <div class="content">

{overview}
{chr(10).join(views)}
        <footer class="app-footer">Coding review &mdash; {len(topics)} topics, {n_files} tested Java template files, {n_dec} decision-tree entries, each linked to a template; {n_lc:,} LeetCode problems mapped to templates.</footer>
      </div>
    </main>
  </div>
</div>
<script>
{js}
</script>
</body>
</html>
"""
    OUT.write_text(page)
    print("wrote %s (%d KB): %d views, %d templates, %d decisions" % (OUT.relative_to(ROOT), len(page) // 1024, len(view_ids), n_files, n_dec))


if __name__ == "__main__":
    main()
