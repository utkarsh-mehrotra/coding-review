import java.util.*;

public class BacktrackingVariantsTest {
    public static void main(String[] x) {
        backtracking();
        Check.done("BacktrackingVariantsTest");
    }

    static Random rnd = new Random(20260316);
    static int[] arr(int n, int lo, int hi) { int[] a = new int[n]; for (int i = 0; i < n; i++) a[i] = lo + rnd.nextInt(hi - lo + 1); return a; }
    static String str(int n, int alpha) { StringBuilder sb = new StringBuilder(); for (int i = 0; i < n; i++) sb.append((char) ('a' + rnd.nextInt(alpha))); return sb.toString(); }
    static boolean validParens(String s) { int o = 0; for (char c : s.toCharArray()) { if (c == '(') o++; else if (c == ')' && --o < 0) return false; } return o == 0; }
    static void backtracking() {
        for (int t = 0; t < 200; t++) {
            int[] sticks = arr(4 + rnd.nextInt(4), 1, 5);
            int sum = 0;
            for (int v : sticks) sum += v;
            boolean can = false;
            if (sum % 4 == 0) {
                int total = 1;
                for (int i = 0; i < sticks.length; i++) total *= 4;
                for (int code = 0; code < total && !can; code++) {
                    int[] side = new int[4];
                    int c = code;
                    for (int v : sticks) { side[c % 4] += v; c /= 4; }
                    can = side[0] == sum / 4 && side[1] == sum / 4 && side[2] == sum / 4 && side[3] == sum / 4;
                }
            }
            Check.eq("makesquare", BacktrackingVariants.makesquare(sticks), can);

            int k = 1 + rnd.nextInt(4), n = rnd.nextInt(30) + 1, c3 = 0;
            Set<String> want = new TreeSet<>();
            for (int mask = 0; mask < 512; mask++) {
                if (Integer.bitCount(mask) != k) continue;
                int s = 0;
                List<Integer> l = new ArrayList<>();
                for (int d = 0; d < 9; d++) if ((mask >> d & 1) != 0) { s += d + 1; l.add(d + 1); }
                if (s == n) want.add(l.toString());
            }
            Set<String> got = new TreeSet<>();
            for (List<Integer> l : BacktrackingVariants.combinationSum3(k, n)) got.add(l.toString());
            Check.eq("comboSum3", got.toString(), want.toString());

            int pn = 1 + rnd.nextInt(6), pk = 1 + rnd.nextInt(fact(pn));
            List<String> perms = new ArrayList<>();
            permute("", "123456".substring(0, pn), perms);
            Collections.sort(perms);
            Check.eq("permSeq", BacktrackingVariants.getPermutation(pn, pk), perms.get(pk - 1));

            String ps = str(1 + rnd.nextInt(6), 2);
            List<String> allPerms = new ArrayList<>();
            permute("", ps, allPerms);
            Set<String> wp = new TreeSet<>();
            for (String s : allPerms) if (new StringBuilder(s).reverse().toString().equals(s)) wp.add(s);
            List<String> gp = BacktrackingVariants.generatePalindromes(ps);
            Check.eq("palPerm", new TreeSet<>(gp).toString(), wp.toString());
            Check.eq("palPermNoDup", gp.size(), wp.size());

            String lc = str(1 + rnd.nextInt(4), 2) + (rnd.nextBoolean() ? "1" : "") + str(rnd.nextInt(2), 2);
            List<String> lcr = BacktrackingVariants.letterCasePermutation(lc);
            int letters = 0;
            for (char c : lc.toCharArray()) if (Character.isLetter(c)) letters++;
            Check.eq("caseCount", lcr.size(), 1 << letters);
            Check.eq("caseDistinct", new HashSet<>(lcr).size(), lcr.size());

            StringBuilder pb = new StringBuilder();
            int pl = 1 + rnd.nextInt(8);
            for (int i = 0; i < pl; i++) pb.append("()a".charAt(rnd.nextInt(3)));
            String raw = pb.toString();
            int bestLen = -1;
            Set<String> wantInv = new TreeSet<>();
            for (int mask = 0; mask < (1 << raw.length()); mask++) {
                StringBuilder sb = new StringBuilder();
                for (int i = 0; i < raw.length(); i++) if ((mask >> i & 1) != 0) sb.append(raw.charAt(i));
                String sub = sb.toString();
                if (!validParens(sub)) continue;
                if (sub.length() > bestLen) { bestLen = sub.length(); wantInv.clear(); }
                if (sub.length() == bestLen) wantInv.add(sub);
            }
            Check.eq("removeInvalid", new TreeSet<>(BacktrackingVariants.removeInvalidParentheses(raw)).toString(), wantInv.toString());

            List<String> words = new ArrayList<>();
            for (int i = 0; i < 1 + rnd.nextInt(5); i++) words.add(str(1 + rnd.nextInt(4), 5));
            int bl = 0;
            for (int mask = 0; mask < (1 << words.size()); mask++) {
                StringBuilder sb = new StringBuilder();
                for (int i = 0; i < words.size(); i++) if ((mask >> i & 1) != 0) sb.append(words.get(i));
                if (sb.chars().distinct().count() == sb.length()) bl = Math.max(bl, sb.length());
            }
            Check.eq("uniqueConcat", BacktrackingVariants.maxLength(words), bl);

            String ab = str(1 + rnd.nextInt(6), 26);
            List<String> abbr = BacktrackingVariants.generateAbbreviations(ab);
            Check.eq("abbrCount", abbr.size(), 1 << ab.length());
            Check.eq("abbrDistinct", new HashSet<>(abbr).size(), abbr.size());
            for (String a : abbr) {
                int len = 0;
                for (int i = 0; i < a.length(); ) {
                    if (Character.isDigit(a.charAt(i))) { int j = i; while (j < a.length() && Character.isDigit(a.charAt(j))) j++; len += Integer.parseInt(a.substring(i, j)); i = j; } else { len++; i++; }
                }
                Check.eq("abbrLen", len, ab.length());
            }
        }
        // known counts of Android unlock patterns by length 1..9
        int[] byLen = {9, 56, 320, 1624, 7152, 26016, 72912, 140704, 140704};
        int total = 0;
        for (int len = 1; len <= 9; len++) { Check.eq("unlock" + len, BacktrackingVariants.numberOfPatterns(len, len), byLen[len - 1]); total += byLen[len - 1]; }
        Check.eq("unlockAll", BacktrackingVariants.numberOfPatterns(1, 9), total);
        Check.eq("unique3", BacktrackingVariants.uniquePathsIII(new int[][]{{1, 0, 0, 0}, {0, 0, 0, 0}, {0, 0, 2, -1}}), 2);
        Check.eq("unique3b", BacktrackingVariants.uniquePathsIII(new int[][]{{1, 0, 0, 0}, {0, 0, 0, 0}, {0, 0, 0, 2}}), 4);
        List<Integer> dw = BacktrackingVariants.diffWaysToCompute("2*3-4*5");
        Collections.sort(dw);
        Check.eq("diffWays", dw.toString(), "[-34, -14, -10, -10, 10]");
        Check.eq("transfers", BacktrackingVariants.minTransfers(new int[][]{{0, 1, 10}, {2, 0, 5}}), 2);
        Check.eq("transfers2", BacktrackingVariants.minTransfers(new int[][]{{0, 1, 10}, {1, 0, 1}, {1, 2, 5}, {2, 0, 5}}), 1);
        Check.eq("dagPaths", BacktrackingVariants.allPathsSourceTarget(new int[][]{{1, 2}, {3}, {3}, {}}).toString(), "[[0, 1, 3], [0, 2, 3]]");
    }
    static int fact(int n) { return n <= 1 ? 1 : n * fact(n - 1); }
    static void permute(String prefix, String rest, List<String> out) {
        if (rest.isEmpty()) { out.add(prefix); return; }
        for (int i = 0; i < rest.length(); i++) permute(prefix + rest.charAt(i), rest.substring(0, i) + rest.substring(i + 1), out);
    }
}
