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


if __name__ == "__main__":
    unittest.main()
