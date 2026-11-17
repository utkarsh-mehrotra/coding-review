import html
import re
import sys
import unittest
from pathlib import Path

SITE = Path(__file__).resolve().parent.parent
sys.path.insert(0, str(SITE))
import build  # noqa: E402


class BuildTest(unittest.TestCase):
    @classmethod
    def setUpClass(cls):
        cls.page = build.render_page()

    def test_member_anchor_and_group_id(self):
        self.assertIn('id="m-DSU-union"', self.page)
        self.assertIn('id="lc-DSU-union"', self.page)

    def test_anchor_ids_unique(self):
        ids = re.findall(r'<span class="m-anchor" id="([^"]+)"></span>', self.page)
        self.assertTrue(ids)
        self.assertEqual(len(ids), len(set(ids)))

    def test_render_page_does_not_write(self):
        before = build.OUT.stat().st_mtime_ns
        build.render_page()
        self.assertEqual(before, build.OUT.stat().st_mtime_ns)

    def _folds(self, card, kind):
        """Text of each fold of this kind inside card html."""
        from html.parser import HTMLParser

        class P(HTMLParser):
            def __init__(self):
                super().__init__()
                self.depth, self.cur, self.out = 0, None, []

            def handle_starttag(self, tag, attrs):
                if self.cur is not None:
                    self.depth += 1
                elif tag == "span" and ("data-fold", kind) in attrs:
                    self.cur, self.depth = [], 1

            def handle_endtag(self, tag):
                if self.cur is not None:
                    self.depth -= 1
                    if self.depth == 0:
                        self.out.append("".join(self.cur))
                        self.cur = None

            def handle_data(self, data):
                if self.cur is not None:
                    self.cur.append(data)

        p = P()
        p.feed(card)
        return p.out

    def _heaps_card(self):
        i = self.page.index('<div class="cc-title">HeapsTopK.java')
        j = self.page.index("</pre>", i)
        return self.page[i:j]

    def test_private_helpers_wrapped(self):
        card = self._heaps_card()
        folds = self._folds(card, "helper")
        self.assertTrue(any("static long d2(" in f for f in folds))
        self.assertFalse(any("findKthLargest(" in f for f in folds))
        self.assertIn("// \u2026 d2(): helper hidden", card)

    def test_imports_wrapped(self):
        card = self._heaps_card()
        folds = self._folds(card, "import")
        self.assertEqual(len(folds), 1)
        self.assertIn("import java.util.*;", folds[0])
        self.assertNotIn("class", folds[0])
        self.assertIn("// \u2026 imports hidden", card)

    def test_copy_text_excludes_fold_marks(self):
        cards = re.findall(
            r'<div class="code-card">.*?<div class="cc-title">([^<]+)</div>.*?<pre class="code-block"><code>(.*?)</code></pre>',
            self.page, re.S)
        self.assertTrue(cards)
        by_name = {}
        for t in build.load_topics():
            for jp in t["_dir"].glob("*.java"):
                by_name.setdefault(jp.name, []).append(jp)
        for name, code in cards:
            code = re.sub(r'<span class="fold-mark">.*?</span>', "", code, flags=re.S)
            text = html.unescape(re.sub(r"<[^>]+>", "", code))
            expected = [p.read_text().rstrip() + "\n" for p in by_name[name]]
            self.assertIn(text, expected, name)

    def test_fold_html_is_well_nested(self):
        from html.parser import HTMLParser

        class P(HTMLParser):
            def __init__(self):
                super().__init__()
                self.stack, self.bad = [], []

            def handle_starttag(self, tag, attrs):
                if tag not in ("br", "meta", "link", "input"):
                    self.stack.append(tag)

            def handle_startendtag(self, tag, attrs):
                pass

            def handle_endtag(self, tag):
                if not self.stack or self.stack.pop() != tag:
                    self.bad.append(tag)

        p = P()
        p.feed(self.page)
        self.assertEqual(p.bad, [])

    def test_toggle_button_present(self):
        self.assertIn('id="essentialsToggle"', self.page)
        self.assertIn("Show essentials", self.page)
        self.assertIn("function copyText", self.page)

    def test_cues_from_decisions(self):
        t = next(t for t in build.load_topics() if (t["_dir"] / "HeapsTopK.java").exists())
        src = (t["_dir"] / "HeapsTopK.java").read_text()
        cues = build.cues_for_file(t, "HeapsTopK", src)
        hit = [c for c, m in cues if m == "findKthLargest"]
        self.assertTrue(any("K largest / smallest / closest / most frequent?" in c for c in hit), cues)
        self.assertTrue(all("<" not in c for c, _ in cues))
        self.assertIn("\u2192", hit[0])

    def test_cues_from_recognize_without_decision(self):
        src = (
            "public class Demo {\n"
            "    // Kth largest\n"
            "    // RECOGNIZE: k largest -> heap\n"
            "    static int a() { return 1; }\n"
            "    // RECOGNIZE: shadowed by a decision\n"
            "    static int b() { return 2; }\n"
            "}\n")
        topic = {"decisions": [{"q": "<strong>Q &amp; B?</strong>", "a": "use b", "template": "Demo#b"}]}
        cues = build.cues_for_file(topic, "Demo", src)
        self.assertEqual(cues, [("k largest -> heap", "a"), ("Q & B? \u2192 use b", "b")])

    def test_cue_links_resolve(self):
        anchors = re.findall(r'data-anchor="([^"]+)"', self.page)
        self.assertTrue(anchors)
        ids = set(re.findall(r'\bid="([^"]+)"', self.page))
        for a in anchors:
            self.assertIn(a, ids)

    def test_cues_markup_above_pre_and_outside_code(self):
        card = self._heaps_card()
        self.assertLess(card.index('<ul class="cues">'), card.index('<pre class="code-block">'))

    def test_cues_collapse_after_six(self):
        self.assertRegex(self.page, r"<summary>\d+ more</summary>")
        for ul in re.findall(r'<ul class="cues">(.*?)</ul>', self.page, re.S):
            self.assertLessEqual(len(re.findall(r"<li>", ul.split("<li><details>")[0])), 6)
            self.assertNotRegex(ul, r"</li>\s*<details>")   # details sits inside an li, not directly in the ul
        self.assertRegex(self.page, r"<li><details><summary>\d+ more</summary><ul>")

    def _cheatsheet(self):
        m = re.search(r'<div class="view" id="view-cheatsheet">(.*?)\n</div>\n', self.page, re.S)
        self.assertTrue(m)
        return m.group(1)

    def test_cheatsheet_has_every_decision(self):
        topics = build.load_topics()
        want = sum(len(t["decisions"]) for t in topics)
        cs = self._cheatsheet()
        rows = re.findall(r'<tr class="sig-row">', cs)
        self.assertEqual(len(rows), want)

    def test_cheatsheet_topic_order(self):
        topics = build.load_topics()
        cs = self._cheatsheet()
        titles = re.findall(r'<h3 class="group-title">(.*?)</h3>', cs)
        self.assertEqual(titles, [html.escape(t["title"], quote=False) for t in topics])

    def test_cheatsheet_links_resolve(self):
        cs = self._cheatsheet()
        anchors = re.findall(r'data-anchor="([^"]+)"', cs)
        self.assertTrue(anchors)
        ids = set(re.findall(r'\bid="([^"]+)"', self.page))
        for a in anchors:
            self.assertIn(a, ids)
        gotos = re.findall(r'data-goto="([^"]+)"', cs)
        self.assertTrue(gotos)
        for g in gotos:
            self.assertIn('id="view-%s"' % g, self.page)

    def test_nav_has_cheatsheet_after_overview(self):
        ov = self.page.index('data-view="overview"><span class="fname">00</span>')
        cs = self.page.index('<div class="nav-item" data-view="cheatsheet"><span class="fname">CS</span>Cheat Sheet</div>')
        lc = self.page.index('<span class="fname">LC</span>')
        self.assertLess(ov, cs)
        self.assertLess(cs, lc)

    def test_cheatsheet_print_button_and_css(self):
        self.assertIn('id="printCheatsheet"', self._cheatsheet())
        css = (SITE / "assets" / "style.css").read_text()
        i = css.index("@media print")
        depth, j = 0, css.index("{", i)
        start = j
        while True:
            if css[j] == "{":
                depth += 1
            elif css[j] == "}":
                depth -= 1
                if depth == 0:
                    break
            j += 1
        block = css[start:j]
        self.assertRegex(block, r"\.topbar[^{]*\{[^}]*display:\s*none")
        self.assertRegex(block, r"\.sidebar[^{]*\{[^}]*display:\s*none")
        self.assertIn(block.strip("{} \n"), self.page)
        self.assertIn("window.print()", self.page)
        # dark and auto-dark themes must print on light tokens
        self.assertIn("color-scheme: light", block)
        self.assertRegex(block, r"--bg:\s*#F6F7F9")
        self.assertRegex(block, r"--text:\s*#1C2127")
        self.assertIn('data-theme="dark"', block)
        self.assertIn("columns: 2", block)

    def _fixture(self, views):
        return {"title": "Fix <T>", "_dir": None, "decisions": [
            {"q": "<b>q1</b>", "a": "a1", "template": "F#m"}], "views": views}

    def test_cheatsheet_merges_tables_and_pitfalls(self):
        f = {"file": "F.java"}
        v1 = {"id": "v1", "files": [f], "table": [["P1", "U1", "O(1)"], ["P2", "U<2", "O(n)"]],
              "pitfalls": ["use `a` < b"]}
        v2 = {"id": "v2", "files": [f], "table": [["P2", "U<2", "O(n)"], ["P3", "U3", "O(2)"]],
              "pitfalls": ["second"]}
        out = build.build_cheatsheet_view([self._fixture([v1, v2])])
        pats = re.findall(r'<td>(P\d)</td>', out)
        self.assertEqual(pats, ["P1", "P2", "P3"])
        self.assertIn("<td>U&lt;2</td>", out)
        self.assertEqual(re.findall(r"<li>(.*?)</li>", out), ["use a &lt; b", "second"])
        self.assertNotIn("`", out)
        self.assertEqual(out.count("<th>Pattern</th>"), 1)
        self.assertIn("Fix <T>".replace("<", "&lt;").replace(">", "&gt;"), out)

    def test_cheatsheet_omits_empty_tables_and_pitfalls(self):
        out = build.build_cheatsheet_view([self._fixture([{"id": "v1", "files": [{"file": "F.java"}]}])])
        self.assertNotIn("<th>Pattern</th>", out)
        self.assertNotIn("cs-pitfalls", out)
        self.assertEqual(out.count('class="sig-row"'), 1)

    def test_jump_index_embedded_and_unique(self):
        import json
        m = re.search(r'<script type="application/json" id="jump-index">(.*?)</script>', self.page, re.S)
        self.assertTrue(m)
        idx = build.expand_jump(json.loads(m.group(1)))
        kinds = {e[4] for e in idx}
        self.assertEqual(kinds, {"view", "template", "problem"})
        ids = [e[0].split(" ", 1)[0] for e in idx if e[4] == "problem"]
        self.assertEqual(len(ids), len(set(ids)))
        self.assertIn("743 Network Delay Time", [e[0] for e in idx])

    def test_jump_index_targets_exist(self):
        import json
        m = re.search(r'<script type="application/json" id="jump-index">(.*?)</script>', self.page, re.S)
        for label, sub, view, anchor, kind in build.expand_jump(json.loads(m.group(1))):
            self.assertIn('id="view-%s"' % view, self.page, label)
            if anchor:
                self.assertIn('id="%s"' % anchor, self.page, label)

    def test_jump_modal_and_button(self):
        self.assertIn('id="jumpButton"', self.page)
        self.assertIn('aria-haspopup="dialog"', self.page)
        self.assertIn('role="dialog"', self.page)
        self.assertIn('id="jumpInput"', self.page)
        self.assertIn('role="listbox"', self.page)
        self.assertLess(self.page.index("window.CRSearch"), self.page.index("window.crGoto"))
        self.assertNotIn("text.indexOf(q)", self.page)

    def test_jump_css_uses_tokens(self):
        css = (SITE / "assets" / "style.css").read_text()
        block = css[css.index(".jump-modal"):]
        self.assertNotRegex(block, r"#[0-9A-Fa-f]{3,6}\b")


    def test_jump_problem_uses_own_first_template(self):
        t = {"id": "t", "title": "T", "_dir": None, "decisions": [],
             "views": [{"id": "v1", "title": "V1", "files": [{"file": "A.java"}]},
                       {"id": "v2", "title": "V2", "files": [{"file": "B.java"}]}]}
        row = {"id": 7, "title": "Seven", "templates": ["B#b", "A#a"]}
        # dict order lists the secondary template first
        idx = build.build_jump_index([t], {"A#a": [row], "B#b": [row]}, [])
        probs = [e for e in idx if e[4] == "problem"]
        self.assertEqual(len(probs), 1)
        self.assertEqual(probs[0][2:4], ["v2", build.group_id("B", "b")])

    def test_jump_index_compact_roundtrip(self):
        import json
        t = {"id": "t", "title": "T", "_dir": None, "decisions": [],
             "views": [{"id": "v1", "title": "V1", "files": [{"file": "A.java"}]},
                       {"id": "v2", "title": "V2", "files": [{"file": "B.java"}]}]}
        rows = [{"id": 7, "title": "Seven </script>", "templates": ["B#b"]},
                {"id": 8, "title": "Eight", "templates": ["B#b"]},
                {"id": 9, "title": "Nine", "templates": ["A#a"]}]
        full = build.build_jump_index([t], {"B#b": rows[:2], "A#a": rows[2:]}, [("v1", "V1", "T")])
        compact = build.compact_jump_index(full)
        self.assertEqual(len(compact["t"]), 2)
        self.assertEqual(build.expand_jump(json.loads(build.jump_json(compact))), full)
        self.assertNotIn("<", build.jump_json(compact))

    def test_jump_json_cannot_close_script(self):
        out = build.jump_json([["1 </script><b>x", "", "v", "", "problem"]])
        self.assertNotIn("<", out)
        import json
        self.assertEqual(json.loads(out)[0][0], "1 </script><b>x")

    def test_goto_links_have_href(self):
        tags = re.findall(r"<a\b[^>]*data-goto=[^>]*>", self.page)
        self.assertTrue(tags)
        for tag in tags:
            self.assertIn("href=", tag)
        js = (SITE / "assets" / "coverage.js").read_text()
        for tag in re.findall(r"<a class=\"tpl\"[^']*data-goto=", js):
            self.assertIn("href=", tag)

    def test_jump_hint_platform_and_focus(self):
        js = (SITE / "assets" / "app.js").read_text()
        self.assertIn("Ctrl K", js)
        self.assertIn("&#8984;K", self.page)
        self.assertIn("preventScroll", js)
        self.assertNotIn('"aria-activedescendant", ""', js)
        self.assertNotIn('aria-activedescendant=""', self.page)

    def test_jump_a11y_markup(self):
        self.assertIn('role="combobox"', self.page)
        self.assertIn('aria-activedescendant', self.page)
        self.assertIn('id="jumpStatus"', self.page)
        self.assertNotIn('.jump-btn { display: none', (SITE / "assets" / "style.css").read_text())

    def test_topbar_fits_phone(self):
        css = (SITE / "assets" / "style.css").read_text()
        i = css.index("@media (max-width: 520px) {\n  .topbar")
        block = css[i:css.index("\n}\n", i)]
        for rule in (".topbar { gap: 8px; }", ".topbar .brand strong, .topbar .brand span.sub { display: none; }", "min-width: 0",
                     ".ess-long { display: none; }", "#themeLabel { position: absolute", ".jump-hint { display: none; }"):
            self.assertIn(rule, block)
        self.assertIn('class="ess-short"', self.page)

    def test_logo_in_topbar_links_home(self):
        m = re.search(r'<a class="brand" href="#overview" data-goto="overview"[^>]*>(.*?)</a>', self.page, re.S)
        self.assertIsNotNone(m)
        self.assertIn('class="logo"', m.group(1))
        self.assertIn('class="logo-tile"', m.group(1))
        self.assertIn('aria-hidden="true"', m.group(1))

    def test_logo_css_uses_tokens(self):
        css = (SITE / "assets" / "style.css").read_text()
        self.assertIn(".logo-tile { fill: var(--accent); }", css)
        self.assertIn(".logo-ink { stroke: var(--panel); }", css)

    def test_favicon_links_and_files(self):
        for href in ('href="favicon.svg" type="image/svg+xml"', 'href="favicon-32.png" sizes="32x32"',
                     'rel="apple-touch-icon" href="apple-touch-icon.png"'):
            self.assertIn(href, self.page)
        fav = (SITE / "favicon.svg").read_text()
        self.assertEqual(fav, build.logo_svg(themed=False))
        self.assertIn('xmlns="http://www.w3.org/2000/svg"', fav)
        self.assertNotIn("var(", fav)
        for png in ("favicon-32.png", "apple-touch-icon.png"):
            data = (SITE / png).read_bytes()
            self.assertEqual(data[:8], b"\x89PNG\r\n\x1a\n", png)

    def test_jump_keys_handled_on_document(self):
        js = (SITE / "assets" / "app.js").read_text()
        self.assertNotIn("jumpBackdrop.addEventListener(\"keydown\"", js)

    def test_verified_problem_under_all_cited_templates(self):
        probs = build.load_problems()
        for key in ("Backtracking#subsets", "BitsMath#subsetsMask"):
            self.assertIn(78, [r["id"] for r in probs[key]], key)

    def test_totals_count_each_problem_once(self):
        probs = build.load_problems()
        n = len({r["id"] for rows in probs.values() for r in rows})
        self.assertLess(n, sum(map(len, probs.values())))  # some problems sit under two templates
        self.assertEqual(build.count_problems(probs), n)
        self.assertIn("%s LeetCode problems mapped" % format(n, ","), self.page)
        self.assertIn("<strong>LeetCode coverage:</strong> %s problems map" % format(n, ","), self.page)

    def test_no_zero_templates(self):
        topics = build.load_topics()
        cov = build.coverage_data(topics, build.load_problems())
        self.assertEqual(cov["zero"], [])
        self.assertTrue(cov["noLc"])
        self.assertNotIn("BinarySearch#sqrt", cov["noLc"])
        self.assertIn("DesignVariants#BloomFilter", cov["noLc"])

    def test_noleetcode_conflict_is_error(self):
        topics = build.load_topics()
        probs = build.load_problems()
        key = "Backtracking#subsets"
        dec = next(d for t in topics for d in t["decisions"] if d["template"] == key)
        dec["noLeetCode"] = True
        try:
            errs = build.check(topics, probs)
        finally:
            del dec["noLeetCode"]
        self.assertTrue(any("remove noLeetCode" in e and key in e for e in errs), errs)


class ListBadgesTest(unittest.TestCase):
    DATA = build.PROBLEMS.parent

    def _lists(self):
        import json
        return json.loads((self.DATA / "lists.json").read_text())

    def test_lists_file_shape(self):
        import json
        lists = self._lists()
        all_ids = {int(q["questionFrontendId"]) for q in json.loads((self.DATA / "lc_all.json").read_text())}
        for key, n in (("top-interview-150", 150), ("leetcode-75", 75)):
            ids = lists[key]["ids"]
            self.assertEqual(len(ids), n)
            self.assertEqual(len(set(ids)), n)
            self.assertEqual(ids, sorted(ids))
            self.assertTrue(lists[key]["name"])
            self.assertLessEqual(set(ids), all_ids)

    def test_problems_have_lists_field(self):
        import json
        lists = self._lists()
        rows = json.loads(build.PROBLEMS.read_text())["problems"]
        for r in rows:
            self.assertIsInstance(r["lists"], list)
        by_id = {r["id"]: r for r in rows}
        for key in ("top-interview-150", "leetcode-75"):
            for i in lists[key]["ids"]:
                if i in by_id:
                    self.assertIn(key, by_id[i]["lists"])

    def test_pill_badge(self):
        import json
        lists = self._lists()
        rows = json.loads(build.PROBLEMS.read_text())["problems"]
        for r in rows:
            pill = build.problem_pill(r)
            self.assertEqual('<span class="lc-badge" title="Top Interview 150">TI</span>' in pill,
                             r["id"] in lists["top-interview-150"]["ids"])
            self.assertEqual('<span class="lc-badge" title="LeetCode 75">75</span>' in pill,
                             r["id"] in lists["leetcode-75"]["ids"])
        two_sum = next(r for r in rows if r["id"] == 1)
        self.assertEqual(("TI" in build.problem_pill(two_sum)), 1 in lists["top-interview-150"]["ids"])

    def test_coverage_row_bitmask(self):
        import json
        lists = self._lists()
        data = build.coverage_data(build.load_topics(), build.load_problems())
        for row in data["rows"]:
            mask = (1 if row[0] in lists["top-interview-150"]["ids"] else 0) | (2 if row[0] in lists["leetcode-75"]["ids"] else 0)
            self.assertEqual(row[11], mask)
        self.assertIn('id="cov-list"', build.render_page())

    def test_page_has_no_inline_coverage_data(self):
        page = build.render_page()
        self.assertNotIn('id="coverage-data"', page)
        self.assertIn('id="view-coverage"', page)

    def test_coverage_json_text(self):
        import json
        text = build.coverage_json(build.load_topics(), build.load_problems())
        self.assertNotIn(": ", text[:200])
        data = json.loads(text)
        self.assertEqual(len(data["rows"]), 3696)
        self.assertEqual([(l["key"], l["bit"], l["size"]) for l in data["lists"]],
                         [("top-interview-150", 1, 150), ("leetcode-75", 2, 75)])

    def test_coverage_json_written(self):
        import json
        path = build.OUT.parent / "coverage.json"
        self.assertTrue(path.exists())
        self.assertEqual(path.read_text(), build.coverage_json(build.load_topics(), build.load_problems()))
        self.assertEqual(len(json.loads(path.read_text())["rows"]), 3696)

    def test_coverage_version_matches_json(self):
        import hashlib
        text = build.coverage_json(build.load_topics(), build.load_problems())
        v = hashlib.sha1(text.encode("utf-8")).hexdigest()[:12]
        self.assertIn('data-coverage-version="%s"' % v, build.render_page())

    def test_coverage_view_has_failure_message(self):
        self.assertIn("The problem list didn't load. Check your connection, then open this view again.", build.render_page())

    def test_coverage_view_has_render_message(self):
        self.assertIn("The problem list couldn't be displayed. Reload the page to try again.", build.render_page())

    def test_coverage_view_has_offline_message(self):
        page = build.render_page()
        self.assertIn("The problem list loads from coverage.json, which browsers block when the page is opened as a file. "
                      "Run <code>make serve</code> and open <code>http://localhost:8000</code>.", page)
        self.assertIn("Loading the problem list", page)


if __name__ == "__main__":
    unittest.main()
