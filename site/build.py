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

sys.path.insert(0, str(Path(__file__).resolve().parent))
from java_outline import outline, anchor_id, group_id  # noqa: E402

ROOT = Path(__file__).resolve().parent.parent
TOPICS = ROOT / "topics"
ASSETS = ROOT / "site" / "assets"
OUT = ROOT / "site" / "index.html"
COVERAGE_OUT = ROOT / "site" / "coverage.json"
FAVICON_OUT = ROOT / "site" / "favicon.svg"
NICHE_FILE = TOPICS / "niche.json"


def load_niche():
    """Decision templates marked niche for an SDE2/SDE3 round (topics/niche.json); hidden by the Hide niche toggle."""
    if not NICHE_FILE.exists():
        return set()
    return set(json.loads(NICHE_FILE.read_text())["templates"])
LISTS_FILE = ROOT / "tools" / "lc-coverage" / "data" / "lists.json"
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
    """A/B problems that name a template, grouped by template key File#member.
    A verified (tier A) problem is listed under every template that cites it; a tag match (B) under its first."""
    if not PROBLEMS.exists():
        return {}
    by = {}
    for r in json.loads(PROBLEMS.read_text())["problems"]:
        if r["tier"] in SHOWN_TIERS and r["templates"]:
            for key in (r["templates"] if r["tier"] == "A" else r["templates"][:1]):
                by.setdefault(key, []).append(r)
    for rows in by.values():
        rows.sort(key=lambda r: (r["tier"], r["paid"], r["id"]))
    return by


def count_problems(problems):
    """Number of distinct LeetCode problems in a {template: [problem]} map."""
    return len({r["id"] for rows in problems.values() for r in rows})


def coverage_data(topics, problems):
    """Compact data for the coverage view: every algorithm problem, all tiers."""
    if not PROBLEMS.exists():
        return None
    import collections
    raw = json.loads(PROBLEMS.read_text())
    rows_in, names = raw["problems"], raw["topics"]
    topic_ids = sorted(names, key=lambda t: names[t])
    tp = {t: i for i, t in enumerate(topic_ids)}
    tmpl = sorted({k for r in rows_in for k in r["templates"] + r["also"]})
    ti = {k: i for i, k in enumerate(tmpl)}
    diff = ["Easy", "Medium", "Hard"]
    rows = [[r["id"], r["title"], r["slug"], diff.index(r["difficulty"]), int(r["paid"]), r["ac"], "ABCU".index(r["tier"]),
             tp.get(r["topic"], -1), ti[r["templates"][0]] if r["templates"] else -1, [ti[k] for k in r["also"]],
             1 if r["how"] == "listed in practice map" else 0,
             sum(1 << i for i, (key, _, _) in enumerate(LIST_BADGES) if key in r.get("lists", []))] for r in rows_in]
    view_of = {}
    for t in topics:
        view_of.update(view_of_file(t))
    decision_tpls = sorted({d["template"] for t in topics for d in t["decisions"]})
    flagged = {d["template"] for t in topics for d in t["decisions"] if d.get("noLeetCode")}
    utags = collections.Counter(tag for r in rows_in if r["tier"] == "U" for tag in r["tags"])
    lists_raw = json.loads(LISTS_FILE.read_text())
    lists = [{"key": key, "name": lists_raw[key]["name"], "bit": 1 << i, "size": len(lists_raw[key]["ids"])}
             for i, (key, _, _) in enumerate(LIST_BADGES)]
    return {"lists": lists, "topics": [[t, names[t]] for t in topic_ids], "templates": tmpl, "rows": rows, "excluded": raw["excluded"],
            "templateView": {f: v for f, v in view_of.items()}, "decisionCount": len(decision_tpls),
            "zero": [k for k in decision_tpls if not problems.get(k) and k not in flagged],
            "noLc": [k for k in decision_tpls if k in flagged], "utags": utags.most_common(10)}


# Logo: a code-brace pair with a check mark on a rounded tile ("templates, reviewed").
# One drawing, two renderings: themed (topbar, colors from CSS tokens) and fixed (favicon).
LOGO_SHAPES = (
    '<rect x="1" y="1" width="30" height="30" rx="8" {tile}/>'
    '<path d="M12.6 8.5c-2.3 0-2.9 1.2-2.9 2.9v2.4c0 1.5-.9 2.2-2.4 2.2 1.5 0 2.4.7 2.4 2.2v2.4c0 1.7.6 2.9 2.9 2.9" {ink}/>'
    '<path d="M19.4 8.5c2.3 0 2.9 1.2 2.9 2.9v2.4c0 1.5.9 2.2 2.4 2.2-1.5 0-2.4.7-2.4 2.2v2.4c0 1.7-.6 2.9-2.9 2.9" {ink}/>'
    '<path d="M13.1 16.3l2.1 2.3 3.7-4.5" {ink}/>'
)
LOGO_STROKE = 'fill="none" stroke-width="2.3" stroke-linecap="round" stroke-linejoin="round"'


def logo_svg(themed=True, size=None):
    """The site logo as SVG markup. themed=True uses CSS classes (styled with tokens in style.css);
    themed=False uses fixed colors, for the favicon file."""
    if themed:
        body = LOGO_SHAPES.format(tile='class="logo-tile"', ink='class="logo-ink" ' + LOGO_STROKE)
        dims = ' width="%d" height="%d"' % (size, size) if size else ""
        return '<svg class="logo" viewBox="0 0 32 32"%s aria-hidden="true" focusable="false">%s</svg>' % (dims, body)
    body = LOGO_SHAPES.format(tile='fill="#0E7C7B"', ink='stroke="#FFFFFF" ' + LOGO_STROKE)
    return '<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 32 32">%s</svg>\n' % body


def coverage_json(topics, problems):
    """Compact JSON text of the coverage payload, served as site/coverage.json; None when there is no problem data."""
    return dump_coverage(coverage_data(topics, problems))


def dump_coverage(data):
    return json.dumps(data, separators=(",", ":"), ensure_ascii=False) if data else None


def coverage_version(text):
    """Short content hash; index.html fetches coverage.json?v=<hash> so a new deploy never pairs with an old cached file."""
    import hashlib
    return hashlib.sha1(text.encode("utf-8")).hexdigest()[:12]


def build_coverage_view(data):
    version = coverage_version(dump_coverage(data))
    return f"""<div class="view" id="view-coverage" data-coverage-version="{version}">
<div class="page-kicker">LC &mdash; LEETCODE COVERAGE</div>
<h1 class="page-title">LeetCode Template Coverage</h1>
<p class="cov-note" id="cov-status">Loading the problem list&hellip;</p>
<p class="cov-offline" id="cov-offline" hidden>The problem list loads from coverage.json, which browsers block when the page is opened as a file. Run <code>make serve</code> and open <code>http://localhost:8000</code>.</p>
<p class="cov-offline" id="cov-failed" hidden>The problem list didn't load. Check your connection, then open this view again.</p>
<p class="cov-offline" id="cov-render" hidden>The problem list couldn't be displayed. Reload the page to try again.</p>
<div id="cov-body" hidden>
<p class="page-intro" id="cov-lede"></p>
<div class="cov-panel">
  <div class="cov-bigbar" id="cov-bigbar" role="img"></div>
  <div class="cov-legend" id="cov-legend"></div>
  <p class="cov-note" id="cov-excl"></p>
  <p class="cov-note" id="cov-lists"></p>
</div>

<h2 class="section-title">Coverage by topic</h2>
<div class="cov-grid2">
  <div class="cov-panel cov-scroll">
    <table class="cov-table">
      <thead><tr><th>Topic</th><th class="r">A</th><th class="r">B</th><th class="r">C</th><th class="r cov-hide-sm">Total</th><th style="width:34%">Mix</th></tr></thead>
      <tbody id="cov-topics"></tbody>
    </table>
    <p class="cov-note">Click a topic to filter the problem list.</p>
  </div>
  <div class="cov-panel">
    <div class="cov-eyebrow">By difficulty</div>
    <div id="cov-diffs"></div>
    <p class="cov-note" id="cov-diffnote"></p>
  </div>
</div>

<h2 class="section-title" id="cov-list-title">Problems</h2>
<div class="cov-panel cov-stack">
  <div class="cov-filters">
    <input type="search" id="cov-q" placeholder="Search number, title or template (e.g. 743, islands, DSU)" aria-label="Search problems" autocomplete="off">
    <select id="cov-topic" aria-label="Topic"></select>
    <select id="cov-list" aria-label="List"><option value="0">All problems</option></select>
    <label class="cov-check"><input type="checkbox" id="cov-free"> Hide premium</label>
  </div>
  <div class="cov-filters">
    <div class="cov-chips" id="cov-tierchips" aria-label="Match level"></div>
    <div class="cov-chips" id="cov-diffchips" aria-label="Difficulty"></div>
    <span class="cov-count" id="cov-count"></span>
  </div>
  <div class="cov-scroll">
    <table class="cov-table cov-list">
      <thead><tr>
        <th class="cov-sort r" data-k="id">#</th><th class="cov-sort" data-k="title">Problem</th>
        <th class="cov-sort" data-k="diff">Level</th><th class="cov-sort" data-k="tier">Match</th>
        <th class="cov-sort" data-k="tmpl">Template</th><th class="cov-sort r cov-hide-sm" data-k="ac">Accept</th>
      </tr></thead>
      <tbody id="cov-rows"></tbody>
    </table>
  </div>
  <button type="button" class="cov-more" id="cov-more" hidden>Show 200 more</button>
</div>

<h2 class="section-title">Gaps</h2>
<div class="cov-grid2 cov-even">
  <div class="cov-panel">
    <div class="cov-eyebrow">Templates with no problem assigned</div>
    <p class="cov-note" id="cov-zero-note"></p>
    <div class="cov-taglist" id="cov-zero"></div>
    <div class="cov-eyebrow">No LeetCode equivalent</div>
    <p class="cov-note" id="cov-nolc-note"></p>
    <div class="cov-taglist" id="cov-nolc"></div>
  </div>
  <div class="cov-panel">
    <div class="cov-eyebrow">What the uncovered problems are</div>
    <p class="cov-note">Most common LeetCode tags among uncovered problems: simulation, enumeration and one-off math, which a template would not help with.</p>
    <div class="cov-taglist" id="cov-utags"></div>
  </div>
</div>

<h2 class="section-title">How matches were made</h2>
<dl class="cov-method">
  <dt>A &middot; Verified</dt><dd>The problem number appears in a template comment (<code>// LC 215</code>) or in a topic's practice map. Every one of these numbers was checked against LeetCode's title.</dd>
  <dt>B &middot; Tag match</dt><dd>A specific LeetCode tag or title keyword points at one template, for example <code>union-find</code> &rarr; DSU, <code>monotonic-stack</code> &rarr; nextGreater. Rules run from most specific to least. It means "nearest template", not "solves it": in an 80-problem sample, 53 fit the technique, 24 were loose and 3 were wrong.</dd>
  <dt>C &middot; Likely</dt><dd>Only broad tags matched (dynamic programming, greedy, hash table, sorting). The technique family is covered, but the specific trick may not be. Not listed under the templates.</dd>
  <dt>Not covered</dt><dd>No rule matched. Mostly simulation, string formatting and one-off math.</dd>
  <dt>Source</dt><dd>LeetCode's public problem list, cached in <code>tools/lc-coverage/data</code>. Database, shell, concurrency, pandas and JavaScript problems are excluded. Rebuild with <code>make problems</code>; refresh the list with <code>make fetch-lc</code>.</dd>
</dl>
</div>
</div>"""


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
            if dec.get("noLeetCode") and (problems or {}).get(tpl):
                errors.append("%s: %s is flagged noLeetCode but %d problems point at it; remove noLeetCode" % (t["id"], tpl, len(problems[tpl])))
            if fname + ".java" not in java:
                errors.append("%s: template file %s.java not found (%s)" % (t["id"], fname, tpl))
            elif member not in members[fname + ".java"]:
                errors.append("%s: member %s not found in %s.java" % (t["id"], member, fname))
            elif member not in {m.name for m in outline(java[fname + ".java"].read_text())}:
                errors.append("%s: member %s in %s.java has no outline anchor, so its cue cannot link" % (t["id"], member, fname))
        for name in java:
            if (t["id"], name) not in files_shown:
                errors.append("%s: template %s is not shown in any view" % (t["id"], name))
    for key, rows in (problems or {}).items():
        fname, member = key.split("#", 1)
        if fname not in all_members:
            errors.append("problems.json: %d problems point at missing file %s.java (e.g. LC %s)" % (len(rows), fname, rows[0]["id"]))
        elif member not in all_members[fname]:
            errors.append("problems.json: %d problems point at missing member %s (e.g. LC %s)" % (len(rows), key, rows[0]["id"]))
    decision_templates = {d["template"] for t in topics for d in t["decisions"]}
    for tpl in sorted(load_niche() - decision_templates):
        errors.append("niche.json: %s is not a decision-tree template" % tpl)
    return errors


DIFF_CLASS = {"Easy": "e", "Medium": "m", "Hard": "h"}


LIST_BADGES = (("top-interview-150", "Top Interview 150", "TI"), ("leetcode-75", "LeetCode 75", "75"))


def list_badges(r):
    return "".join('<span class="lc-badge" title="%s">%s</span>' % (name, short)
                   for key, name, short in LIST_BADGES if key in r.get("lists", []))


def problem_pill(r):
    return '<a class="lc-pill lc-%s lc-%s" href="https://leetcode.com/problems/%s/" target="_blank" rel="noopener" title="%s · %s%s">' \
           '<span class="lc-id">%s</span>%s%s%s</a>' % (
               DIFF_CLASS[r["difficulty"]], r["tier"].lower(), html.escape(r["slug"]),
               r["difficulty"], "verified: named in the template" if r["tier"] == "A" else "matched by LeetCode tag",
               " · premium" if r["paid"] else "", r["id"], html.escape(r["title"]), list_badges(r),
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
    uniq = {r["id"]: r for k in groups for r in problems[k]}
    total = len(uniq)
    n_a = sum(1 for r in uniq.values() if r["tier"] == "A")
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
        out.append('<div class="lc-group" id="%s"><div class="lc-head"><span class="tpl">%s</span><span class="lc-n">%d</span></div>'
                   '<div class="lc-list">%s</div></div>' % (html.escape(group_id(*k.split("#", 1))), html.escape(k), len(rows), pills))
    return "\n".join(out)


def fold_segments(src):
    """Split src's lines into (first, last, kind) runs (0-based, half-open) covering every line.

    kind is "import" for the leading import block, "helper" for each private member (comment block
    and annotations included) and None for everything else.
    """
    n = len(src.split("\n")) - 1          # src ends with a newline
    lines = src.split("\n")
    folds = []
    imports = [i for i, l in enumerate(lines[:n]) if l.startswith("import ")]
    if imports:
        folds.append((imports[0], imports[-1] + 1, "import", ""))
    for m in outline(src):
        if m.private:
            folds.append((m.doc_start - 1, m.end, "helper", m.name))
    folds.sort()
    segs, pos = [], 0
    for a, b, kind, name in folds:
        if a > pos:
            segs.append((pos, a, None, ""))
        segs.append((a, b, kind, name))
        pos = b
    if pos < n:
        segs.append((pos, n, None, ""))
    return segs


def highlight_with_anchors(src, stem):
    """Highlight each fold segment on its own, so fold spans never cross token spans.

    Puts an empty anchor span at the start of each member's doc_start line, and wraps imports and
    private helpers in <span class="fold"> preceded by a <span class="fold-mark"> that the copy button drops.
    """
    lines = src.split("\n")
    anchors, seen = {}, set()
    for m in outline(src):
        if m.name in seen:      # overloads: keep ids unique, first one wins
            continue
        seen.add(m.name)
        anchors[m.doc_start - 1] = '<span class="m-anchor" id="%s"></span>' % html.escape(anchor_id(stem, m.name))
    out = []
    for a, b, kind, name in fold_segments(src):
        seg = highlight("\n".join(lines[a:b]) + "\n").split("\n")
        for i in range(b - a):
            if a + i in anchors:
                seg[i] = anchors[a + i] + seg[i]
        body = "\n".join(seg)
        if kind is None:
            out.append(body)
            continue
        indent = lines[a][:len(lines[a]) - len(lines[a].lstrip())]
        text = "// \u2026 imports hidden" if kind == "import" else "// \u2026 %s(): helper hidden" % name
        out.append('<span class="fold-mark">%s%s\n</span><span class="fold" data-fold="%s">%s</span>'
                   % (indent, html.escape(text, quote=False), kind, body))
    return "".join(out)


CUE_VISIBLE = 6              # cues shown above a card before "N more"
RECOGNIZE = re.compile(r"//\s*RECOGNIZE:\s*(.*\S)")


def _plain(text):
    return html.unescape(re.sub(r"<[^>]*>", "", text)).strip()


def cues_for_file(topic, file_stem, src):
    """(cue_text, member) pairs for one template file, in member order.

    A cue comes from each decision whose template is file_stem#member, plus the RECOGNIZE: comment
    in a member's doc block when no decision names that member. Text is plain (tags stripped,
    entities decoded); callers escape it once.
    """
    members = outline(src)
    first = {}
    for m in members:
        first.setdefault(m.name, m)
    by_member = {}
    for dec in topic.get("decisions", []):
        fname, _, member = dec.get("template", "").partition("#")
        if fname == file_stem and member in first:
            by_member.setdefault(member, []).append("%s \u2192 %s" % (_plain(dec["q"]), _plain(dec["a"])))
    lines = src.split("\n")
    cues = []
    for name, m in sorted(first.items(), key=lambda kv: kv[1].start):
        if name in by_member:
            cues.extend((text, name) for text in by_member[name])
            continue
        for line in lines[m.doc_start - 1:m.start - 1]:
            r = RECOGNIZE.search(line)
            if r:
                cues.append((r.group(1).strip(), name))
    return cues


def cues_html(topic, fname):
    stem = fname[:-5]
    src = (topic["_dir"] / fname).read_text().rstrip() + "\n"
    cues = cues_for_file(topic, stem, src)
    if not cues:
        return ""
    view = view_of_file(topic)[stem]
    items = ['<li>%s <a class="tpl" href="#%s" data-goto="%s" data-anchor="%s">%s</a></li>' % (
        html.escape(text), html.escape(view), html.escape(view), html.escape(anchor_id(stem, member)), html.escape(member))
        for text, member in cues]
    head, rest = items[:CUE_VISIBLE], items[CUE_VISIBLE:]
    more = ""
    if rest:
        more = '<li><details><summary>%d more</summary><ul>%s</ul></details></li>' % (len(rest), "".join(rest))
    return '  <ul class="cues">%s%s</ul>\n' % ("".join(head), more)


def cue_report(topics):
    """(members_with_cue, total_non_private, [uncovered 'File#member', ...])."""
    total, have, missing = 0, 0, []
    for t in topics:
        for jp in sorted(t["_dir"].glob("*.java")):
            src = jp.read_text().rstrip() + "\n"
            cued = {m for _, m in cues_for_file(t, jp.stem, src)}
            seen = set()
            for m in outline(src):
                if m.private or m.name in seen:
                    continue
                seen.add(m.name)
                total += 1
                if m.name in cued:
                    have += 1
                else:
                    missing.append("%s#%s" % (jp.stem, m.name))
    return have, total, missing


def code_card(topic, fname, desc, badge):
    src = (topic["_dir"] / fname).read_text().rstrip() + "\n"
    return (
        '<div class="code-card">\n  <div class="code-card-head">\n    <div>\n'
        '      <div class="cc-title">%s</div>\n      <div class="cc-desc">%s</div>\n    </div>\n'
        '    <div class="cc-meta">\n      <span class="cc-badge">%s</span>\n'
        '      <button class="copy-btn">&#10697; Copy</button>\n    </div>\n  </div>\n'
        '%s  <pre class="code-block"><code>%s</code></pre>\n</div>'
    ) % (fname, html.escape(desc, quote=False) if "<" not in desc else desc, badge,
         cues_html(topic, fname), highlight_with_anchors(src, fname[:-5]))


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


def build_jump_index(topics, problems, view_ids_titles):
    """Entries [label, sub, view, anchor, kind] for the jump box: views, decision templates, problems."""
    idx = [[title, sub, vid, "", "view"] for vid, title, sub in view_ids_titles]
    seen = set()
    for t in topics:
        vf = view_of_file(t)
        for d in t["decisions"]:
            key = d["template"]
            if key in seen:
                continue
            seen.add(key)
            stem, member = key.split("#", 1)
            idx.append([key, _plain(d["q"]), vf[stem], anchor_id(stem, member), "template"])
    vmap = {}
    for t in topics:
        vmap.update(view_of_file(t))
    done = set()
    for rows in problems.values():
        for r in rows:
            if r["id"] in done or not r["templates"]:
                continue
            key = r["templates"][0]
            stem, member = key.split("#", 1)
            if stem not in vmap:
                continue
            done.add(r["id"])
            idx.append(["%s %s" % (r["id"], r["title"]), key, vmap[stem], group_id(stem, member), "problem"])
    return idx


def compact_jump_index(index):
    """Shrinks the jump index for embedding: {"e": views and templates, "t": [[template, view, anchor]],
    "p": [[id, title, template index]]}. expand_jump (and CRSearch.expand in JS) reverse it exactly."""
    entries, tpls, tpl_idx, probs = [], [], {}, []
    for e in index:
        if e[4] != "problem":
            entries.append(e)
            continue
        key = (e[1], e[2], e[3])
        if key not in tpl_idx:
            tpl_idx[key] = len(tpls)
            tpls.append(list(key))
        pid, title = e[0].split(" ", 1)
        probs.append([int(pid), title, tpl_idx[key]])
    return {"e": entries, "t": tpls, "p": probs}


def expand_jump(data):
    out = list(data["e"])
    for pid, title, i in data["p"]:
        t = data["t"][i]
        out.append(["%d %s" % (pid, title), t[0], t[1], t[2], "problem"])
    return out


def jump_json(index):
    """JSON safe to embed in a script element."""
    return json.dumps(index, separators=(",", ":")).replace("<", "\\u003c")


JUMP_MODAL = """<div class="jump-backdrop" id="jumpBackdrop" hidden>
  <div class="jump-modal" role="dialog" aria-modal="true" aria-label="Jump to a topic, template or problem">
    <input id="jumpInput" class="jump-input" type="text" role="combobox" aria-label="Jump to a view, template or problem" aria-expanded="false" aria-controls="jumpList" aria-autocomplete="list" placeholder="Jump to a topic, template or problem number&hellip;" autocomplete="off" spellcheck="false">
    <ul class="jump-list" id="jumpList" role="listbox" aria-label="Jump results"></ul>
    <div class="jump-empty" id="jumpStatus" role="status" aria-live="polite"></div>
    <div class="jump-foot">Up and down to choose, Enter to go, Esc to close</div>
  </div>
</div>"""


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
        rows = list({r["id"]: r for v in problems.values() for r in v}.values())
        n_a = sum(1 for r in rows if r["tier"] == "A")
        p.append('<div class="calib-block"><strong>LeetCode coverage:</strong> %s problems map to a template here: %s named in the template code or practice map, '
                 '%s matched by a specific LeetCode tag. Each template view lists its problems under the code. '
                 'Type a problem number or title in the filter box to find its template, or open the '
                 '<a class="tpl" href="#coverage" data-goto="coverage">LeetCode Coverage</a> view for the full list.</div>' % (
                     format(len(rows), ","), format(n_a, ","), format(len(rows) - n_a, ",")))
    p.append('<h2 class="section-title">Topics</h2>\n<div class="topic-grid">')
    for t in topics:
        first = t["views"][0]["id"]
        p.append('<a class="topic-card" href="#%s" data-goto="%s"><strong>%s</strong><div class="tc-meta">%d %s &middot; %d %s &middot; %d decisions</div></a>' % (
            first, first, html.escape(t["title"]), len(t["views"]), "view" if len(t["views"]) == 1 else "views",
            counts[t["id"]], "template file" if counts[t["id"]] == 1 else "template files", len(t["decisions"])))
    p.append('</div>')
    niche = load_niche()
    p.append('<h2 class="section-title">Decision Tree &mdash; &ldquo;which pattern do I reach for?&rdquo;'
             '<button type="button" class="theme-toggle niche-toggle" aria-pressed="false">Hide niche</button></h2>')
    for t in topics:
        vf = view_of_file(t)
        p.append('<h3 class="group-title">%s</h3>\n<ol class="decision-list">' % html.escape(t["title"]))
        for dec in t["decisions"]:
            fname, member = dec["template"].split("#", 1)
            p.append('<li%s><strong>%s</strong> <span class="arrow">&rarr;</span> %s<a class="tpl" href="#%s" data-goto="%s">%s</a></li>' % (
                ' class="niche"' if dec["template"] in niche else "",
                re.sub(r"^<strong>|</strong>$", "", dec["q"]).replace("<strong>", "").replace("</strong>", ""),
                dec["a"], vf[fname], vf[fname], html.escape(dec["template"])))
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


def build_cheatsheet_view(topics, problems=None):
    """One printable page: per topic, signals, merged pattern tables and pitfalls."""
    esc = lambda t: html.escape(t, quote=False)
    niche = load_niche()
    p = ['<div class="page-kicker">CS &mdash; CHEAT SHEET</div>',
         '<div class="cs-head"><h1 class="page-title">Cheat Sheet</h1>'
         '<div class="cs-actions"><button type="button" class="theme-toggle niche-toggle" id="hideNiche" aria-pressed="false">Hide niche</button>'
         '<button type="button" class="theme-toggle" id="printCheatsheet">Print</button></div></div>',
         '<p class="page-intro">Every signal, pattern and pitfall in one place. Print it, or save it as a PDF.</p>']
    for t in topics:
        vmap = view_of_file(t)
        p.append('<h3 class="group-title">%s</h3>' % esc(t["title"]))
        rows = ['<tr><th>Signal</th><th>Approach</th><th>Template</th></tr>']
        for d in t["decisions"]:
            file_stem, member = d["template"].split("#", 1)
            link = '<a class="tpl" href="#%s" data-goto="%s" data-anchor="%s">%s</a>' % (
                html.escape(vmap[file_stem]), html.escape(vmap[file_stem]), html.escape(anchor_id(file_stem, member)), html.escape(d["template"]))
            is_niche = d["template"] in niche
            rows.append('<tr class="sig-row%s"><td>%s%s</td><td>%s</td><td>%s</td></tr>' % (
                " niche" if is_niche else "", esc(_plain(d["q"])),
                ' <span class="niche-tag">niche</span>' if is_niche else "", esc(_plain(d["a"])), link))
        p.append('<table class="ref-table">\n%s\n</table>' % "\n".join(rows))
        merged, seen = [], set()
        for v in t["views"]:
            for row in v.get("table") or []:
                if tuple(row) not in seen:
                    seen.add(tuple(row))
                    merged.append(row)
        if merged:
            rows = ['<tr><th>Pattern</th><th>Use case</th><th>Complexity</th></tr>']
            for a, b, c in merged:
                rows.append('<tr><td>%s</td><td>%s</td><td class="complexity">%s</td></tr>' % (esc(a), esc(b), esc(c)))
            p.append('<table class="ref-table">\n%s\n</table>' % "\n".join(rows))
        pits = [x for v in t["views"] for x in v.get("pitfalls") or []]
        if pits:
            p.append('<ul class="cs-pitfalls">\n%s\n</ul>' % "\n".join(
                "<li>%s</li>" % esc(x).replace("`", "") for x in pits))
    return '<div class="view" id="view-cheatsheet">\n%s\n</div>' % "\n".join(p)


def render_page(topics=None, problems=None):
    """Returns the whole page as a string; writes nothing."""
    topics = topics if topics is not None else load_topics()
    problems = problems if problems is not None else load_problems()
    n_dec = sum(len(t["decisions"]) for t in topics)
    n_files = sum(len(list(t["_dir"].glob("*.java"))) for t in topics)
    counts = {t["id"]: len(list(t["_dir"].glob("*.java"))) for t in topics}
    n_lc = count_problems(problems)
    nav, views, view_ids, number = [], [], ["overview"], 0
    nav.append('<div class="sidebar-label">Reference</div>')
    nav.append('<div class="nav-item" data-view="overview"><span class="fname">00</span>Overview &amp; Decision Tree</div>')
    nav.append('<div class="nav-item" data-view="cheatsheet"><span class="fname">CS</span>Cheat Sheet</div>')
    view_ids.append("cheatsheet")
    cov = coverage_data(topics, problems)
    if cov:
        nav.append('<div class="nav-item" data-view="coverage" data-kw="leetcode coverage problems all tiers gaps">'
                   '<span class="fname">LC</span>LeetCode Coverage</div>')
        view_ids.append("coverage")
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
    ref_views = [("overview", "Overview & Decision Tree", "Reference"), ("cheatsheet", "Cheat Sheet", "Reference")]
    if cov:
        ref_views.append(("coverage", "LeetCode Coverage", "Reference"))
    topic_views = [(v["id"], v["title"], t["title"]) for t in topics for v in t["views"]]
    jump_data = jump_json(compact_jump_index(build_jump_index(topics, problems, ref_views + topic_views)))

    css = (ASSETS / "style.css").read_text()
    js = (ASSETS / "search.js").read_text() + "\n" + (ASSETS / "app.js").read_text().replace("/*VIEWS*/[]", json.dumps(view_ids))
    if cov:
        js += "\n" + (ASSETS / "coverage.js").read_text()
    js += """
document.addEventListener("click", function(e){
  var g = e.target.closest("[data-goto]");
  if (!g) return;
  if (e.metaKey || e.ctrlKey || e.shiftKey || e.altKey || (e.button || 0) !== 0) return;   // let the browser open a new tab
  e.preventDefault();
  var a = g.getAttribute("data-anchor");
  if (a && window.crGoto) { window.crGoto(g.getAttribute("data-goto"), a); return; }
  location.hash = g.getAttribute("data-goto");
});
"""
    page = f"""<!DOCTYPE html>
<html lang="en">
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1, viewport-fit=cover">
<title>Coding Review &mdash; Java Templates</title>
<link rel="icon" href="favicon.svg" type="image/svg+xml">
<link rel="icon" href="favicon-32.png" sizes="32x32" type="image/png">
<link rel="apple-touch-icon" href="apple-touch-icon.png">
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
    <a class="brand" href="#overview" data-goto="overview" aria-label="coding-review: go to the overview">{logo_svg(size=22)}<strong>coding-review</strong><span class="sub">&nbsp;&mdash; Java templates, SDE2 / SDE3 / Staff</span></a>
    <div class="search-wrap">
      <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><circle cx="11" cy="11" r="7"/><line x1="21" y1="21" x2="16.65" y2="16.65"/></svg>
      <input id="searchInput" type="text" placeholder="Filter: topic, algorithm, method&hellip;" autocomplete="off">
    </div>
    <button class="theme-toggle ess-toggle" id="essentialsToggle" aria-pressed="false"><span class="ess-long">Show essentials</span><span class="ess-short">Essentials</span></button>
    <button class="theme-toggle jump-btn" id="jumpButton" type="button" aria-haspopup="dialog" aria-label="Jump to a view, template or problem"><span>Jump</span><span class="jump-hint">&#8984;K</span></button>
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
{build_cheatsheet_view(topics, problems)}
{build_coverage_view(cov) if cov else ""}
{chr(10).join(views)}
        <footer class="app-footer">Coding review &mdash; {len(topics)} topics, {n_files} tested Java template files, {n_dec} decision-tree entries, each linked to a template; {n_lc:,} LeetCode problems mapped to templates.</footer>
      </div>
    </main>
  </div>
</div>
{JUMP_MODAL}
<script type="application/json" id="jump-index">{jump_data}</script>
<script>
{js}
</script>
</body>
</html>
"""
    return page


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
                  len(topics), n_files, n_dec, count_problems(problems), len(problems)))
        have, total, missing = cue_report(topics)
        print("cues: %d of %d non-private members have a cue" % (have, total))
        if missing:
            print("  without a cue (first 10 of %d): %s" % (len(missing), ", ".join(missing[:10])))
        return
    page = render_page(topics, problems)
    OUT.write_text(page)
    FAVICON_OUT.write_text(logo_svg(themed=False))
    cov = coverage_json(topics, problems)
    if cov:
        COVERAGE_OUT.write_text(cov)
        print("wrote %s (%d KB)" % (COVERAGE_OUT.relative_to(ROOT), len(cov.encode()) // 1024))
    print("wrote %s (%d KB): %d topics, %d templates, %d decisions" % (OUT.relative_to(ROOT), len(page) // 1024, len(topics), n_files, n_dec))


if __name__ == "__main__":
    main()
