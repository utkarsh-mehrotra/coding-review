import java.util.*;

public class StringsVariantsTest {
    public static void main(String[] x) {
        Random r = new Random(21);
        // ---------- strings ----------
        Check.eq("suffixArray", Check.s(StringsVariants.suffixArray("banana")), "[5, 3, 1, 0, 4, 2]");
        Check.eq("lcp", Check.s(StringsVariants.lcpArray("banana", StringsVariants.suffixArray("banana"))), "[0, 1, 3, 0, 0, 2]");
        Check.eq("distinctSubs", StringsVariants.countDistinctSubstrings("banana") + "," + StringsVariants.countDistinctSubstrings("aaa"), "15,3");
        Check.eq("repeated", StringsVariants.longestRepeatedSubstring("banana") + "," + StringsVariants.longestRepeatedSubstring("abcd").isEmpty(), "ana,true");
        Check.eq("dupSub", StringsVariants.longestDupSubstring("banana") + "," + StringsVariants.longestDupSubstring("abcd").isEmpty(), "ana,true");
        StringsVariants.AhoCorasick ahoC = new StringsVariants.AhoCorasick(new String[]{"he", "she", "his", "hers"});
        Check.eq("ahoCorasick", Check.s(ahoC.countAll("ahishers")), "[1, 1, 1, 1]");
        Check.eq("wordBreak2", new TreeSet<>(StringsVariants.wordBreakAll("catsanddog", List.of("cat", "cats", "and", "sand", "dog"))), "[cat sand dog, cats and dog]");
        List<List<Integer>> pp = StringsVariants.palindromePairs(new String[]{"abcd", "dcba", "lls", "s", "sssll"});
        Check.eq("palPairs", new TreeSet<>(pp.stream().map(Object::toString).toList()), "[[0, 1], [1, 0], [2, 4], [3, 2]]");
        Check.eq("happyPrefix", StringsVariants.longestHappyPrefix("level") + "," + StringsVariants.longestHappyPrefix("ababab"), "l,abab");
        Check.eq("leastRotation", StringsVariants.leastRotation("cba") + "," + StringsVariants.leastRotation("bbaaccaadd") + "," + StringsVariants.leastRotation("aaaa"), "acb,aaccaaddbb,aaaa");
        Check.eq("justify", StringsVariants.fullJustify(new String[]{"This", "is", "an", "example", "of", "text", "justification."}, 16), "[This    is    an, example  of text, justification.  ]");
        Check.eq("isNumber", StringsVariants.isNumber("2") + "," + StringsVariants.isNumber("0089") + "," + StringsVariants.isNumber("-0.1") + "," + StringsVariants.isNumber("3.") + "," + StringsVariants.isNumber("e3") + "," + StringsVariants.isNumber("99e2.5") + "," + StringsVariants.isNumber("--6") + "," + StringsVariants.isNumber("-+3") + "," + StringsVariants.isNumber(".") + "," + StringsVariants.isNumber("4e+"), "true,true,true,true,false,false,false,false,false,false");
        Check.eq("encodeDecode", StringsVariants.decode(StringsVariants.encode(List.of("a#b", "", "12#", "xyz"))), "[a#b, , 12#, xyz]");
        Check.eq("compareVersion", StringsVariants.compareVersion("1.01", "1.001") + "," + StringsVariants.compareVersion("1.0", "1.0.0") + "," + StringsVariants.compareVersion("0.1", "1.1"), "0,0,-1");
        Check.eq("zigzag", StringsVariants.zigzag("PAYPALISHIRING", 3) + "," + StringsVariants.zigzag("PAYPALISHIRING", 4), "PAHNAPLSIIGYIR,PINALSIGYAHRPI");
        for (int t = 0; t < 300; t++) {
            StringBuilder sb = new StringBuilder();
            int len = r.nextInt(14);
            for (int i = 0; i < len; i++) sb.append((char) ('a' + r.nextInt(3)));
            String s = sb.toString();
            // suffix array vs sorting suffixes
            Integer[] idx = new Integer[len];
            for (int i = 0; i < len; i++) idx[i] = i;
            Arrays.sort(idx, (p, q) -> s.substring(p).compareTo(s.substring(q)));
            int[] want = new int[len];
            for (int i = 0; i < len; i++) want[i] = idx[i];
            Check.eq("saRand", Check.s(StringsVariants.suffixArray(s)), Check.s(want));
            Set<String> subs = new HashSet<>();
            for (int i = 0; i < len; i++) for (int j = i + 1; j <= len; j++) subs.add(s.substring(i, j));
            Check.eq("distinctRand", StringsVariants.countDistinctSubstrings(s), subs.size());
            // longest repeated substring length vs brute
            int best = 0;
            for (int L = 1; L < len; L++) { Set<String> seen = new HashSet<>(); for (int i = 0; i + L <= len; i++) if (!seen.add(s.substring(i, i + L))) { best = L; break; } }
            Check.eq("repeatedRand", StringsVariants.longestRepeatedSubstring(s).length(), best);
            Check.eq("dupSubRand", StringsVariants.longestDupSubstring(s).length(), best);
            // Aho-Corasick vs indexOf counting
            String[] pats = {rndStr(r, 1 + r.nextInt(3)), rndStr(r, 1 + r.nextInt(3)), rndStr(r, 1 + r.nextInt(2))};
            int[] got = new StringsVariants.AhoCorasick(pats).countAll(s);
            for (int i = 0; i < 3; i++) { int c = 0; for (int j = s.indexOf(pats[i]); j >= 0; j = s.indexOf(pats[i], j + 1)) c++; Check.eq("ahoRand", got[i], c); }
            // least rotation vs brute
            String minRot = s;
            for (int i = 0; i < len; i++) { String rot = s.substring(i) + s.substring(0, i); if (rot.compareTo(minRot) < 0) minRot = rot; }
            Check.eq("rotationRand", StringsVariants.leastRotation(s), minRot);
            // happy prefix vs brute
            String hp = "";
            for (int L = len - 1; L >= 1; L--) if (s.substring(0, L).equals(s.substring(len - L))) { hp = s.substring(0, L); break; }
            Check.eq("happyRand", StringsVariants.longestHappyPrefix(s), hp);
            // palindrome pairs vs brute
            int w = 2 + r.nextInt(4);
            Set<String> uniq = new LinkedHashSet<>();
            for (int i = 0; i < w; i++) uniq.add(rndStr(r, r.nextInt(4)));
            String[] words = uniq.toArray(new String[0]);
            Set<String> wantPairs = new TreeSet<>(), gotPairs = new TreeSet<>();
            for (int i = 0; i < words.length; i++) for (int j = 0; j < words.length; j++)
                if (i != j && isPalS(words[i] + words[j])) wantPairs.add("[" + i + ", " + j + "]");
            for (List<Integer> pr : StringsVariants.palindromePairs(words)) gotPairs.add(pr.toString());
            Check.eq("palPairsRand", gotPairs, wantPairs);
            // encode / decode round trip
            List<String> strs = List.of(s, rndStr(r, 3) + "#" + rndStr(r, 2), "");
            Check.eq("encodeRand", StringsVariants.decode(StringsVariants.encode(strs)), strs);
        }
        Check.done("StringsVariantsTest");
    }

    static String rndStr(Random r, int n) { StringBuilder sb = new StringBuilder(); for (int i = 0; i < n; i++) sb.append((char) ('a' + r.nextInt(3))); return sb.toString(); }
    static boolean isPalS(String s) { return new StringBuilder(s).reverse().toString().equals(s); }
}
