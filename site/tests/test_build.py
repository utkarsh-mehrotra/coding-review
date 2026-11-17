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


if __name__ == "__main__":
    unittest.main()
