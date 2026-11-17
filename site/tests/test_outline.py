import json
import sys
import unittest
from pathlib import Path

SITE = Path(__file__).resolve().parent.parent
sys.path.insert(0, str(SITE))
from java_outline import outline, anchor_id, group_id  # noqa: E402

ROOT = SITE.parent

SRC = '''import java.util.*;
public class Demo {
    // Kth largest (LC 215)
    // RECOGNIZE: k largest
    static int findKthLargest(int[] a, int k) {
        return helper(a) + k;
    }
    private static int helper(int[] a) { return a.length; }
    static class MedianFinder {
        void addNum(int x) { }
    }
}
'''

TRICKY = '''public class T {
    static String f(int x) {
        String s = "}}{";   // } closing brace in comment {
        char c = '}';
        /* block } */
        return s + c;
    }
    static int g() { return 1; }
}
'''


class OutlineTest(unittest.TestCase):
    def test_members_and_ranges(self):
        ms = outline(SRC)
        self.assertEqual([m.name for m in ms], ["findKthLargest", "helper", "MedianFinder"])
        self.assertEqual((ms[0].start, ms[0].end, ms[0].doc_start), (5, 7, 3))
        self.assertTrue(ms[1].private)
        self.assertFalse(ms[0].private)
        self.assertEqual((ms[2].kind, ms[2].start, ms[2].end), ("class", 9, 11))
        self.assertEqual(ms[1].doc_start, ms[1].start)

    def test_braces_in_strings_and_comments_ignored(self):
        ms = outline(TRICKY)
        self.assertEqual([(m.name, m.start, m.end) for m in ms], [("f", 2, 7), ("g", 8, 8)])

    def test_doc_start_steps_over_annotation_lines(self):
        src = (
            "public class A {\n"
            "    // note\n"
            "    @SuppressWarnings(\"unchecked\")\n"
            "    // more\n"
            "    @Deprecated\n"
            "    private static void h() { }\n"
            "}\n"
        )
        ms = outline(src)
        self.assertEqual([(m.name, m.doc_start, m.start, m.end) for m in ms], [("h", 2, 6, 6)])

    def test_ids(self):
        self.assertEqual(anchor_id("DSU", "union"), "m-DSU-union")
        self.assertEqual(group_id("DSU", "union"), "lc-DSU-union")

    def test_every_repo_template_parses(self):
        for tj in sorted((ROOT / "topics").glob("*/topic.json")):
            topic = json.loads(tj.read_text())
            by_file = {}
            for jp in sorted(tj.parent.glob("*.java")):
                ms = outline(jp.read_text())
                self.assertTrue(ms, jp.name)
                prev = 0
                for m in ms:
                    self.assertLessEqual(m.doc_start, m.start, jp.name)
                    self.assertGreater(m.doc_start, prev, "%s %s overlaps" % (jp.name, m.name))
                    self.assertLessEqual(m.start, m.end)
                    prev = m.end
                by_file[jp.stem] = {m.name for m in ms}
            for dec in topic["decisions"]:
                f, member = dec["template"].split("#", 1)
                self.assertIn(member, by_file.get(f, set()), dec["template"])


if __name__ == "__main__":
    unittest.main()
