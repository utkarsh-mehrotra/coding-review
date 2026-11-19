import java.math.BigInteger;
import java.util.*;

public class DPVariantsTest {
    public static void main(String[] x) throws Throwable {
        Throwable[] err = new Throwable[1];
        Thread t = new Thread(null, () -> { try { run(x); } catch (Throwable e) { err[0] = e; } }, "dp-tests", 1L << 28);
        t.start();
        t.join();
        if (err[0] != null) throw err[0];
    }

    static void run(String[] x) {
        dp();
        Check.done("DPVariantsTest");
    }

    static Random rnd = new Random(31415926);
    static int[] arr(int n, int lo, int hi) { int[] a = new int[n]; for (int i = 0; i < n; i++) a[i] = lo + rnd.nextInt(hi - lo + 1); return a; }
    static String str(int n, int alpha) { StringBuilder sb = new StringBuilder(); for (int i = 0; i < n; i++) sb.append((char) ('a' + rnd.nextInt(alpha))); return sb.toString(); }
    static void record(BinaryTree.TreeNode n, int depth, int parent, Map<Integer, int[]> info) {
        if (n == null) return;
        info.put(n.val, new int[]{depth, parent});
        record(n.left, depth + 1, n.val, info);
        record(n.right, depth + 1, n.val, info);
    }
    static void dp() {
        Check.eq("frog1", DPVariants.canCross(new int[]{0, 1, 3, 5, 6, 8, 12, 17}), true);
        Check.eq("frog2", DPVariants.canCross(new int[]{0, 1, 2, 3, 4, 8, 9, 11}), false);
        Check.eq("stoneII1", DPVariants.stoneGameII(new int[]{2, 7, 9, 4, 4}), 10);
        Check.eq("stoneII2", DPVariants.stoneGameII(new int[]{1, 2, 3, 4, 5, 100}), 104);
        Check.eq("egg1", DPVariants.superEggDrop(1, 2), 2);
        Check.eq("egg2", DPVariants.superEggDrop(2, 6), 3);
        Check.eq("egg3", DPVariants.superEggDrop(3, 14), 4);
        Check.eq("rolls1", DPVariants.numRollsToTarget(1, 6, 3), 1);
        Check.eq("rolls2", DPVariants.numRollsToTarget(2, 6, 7), 6);
        Check.eq("rolls3", DPVariants.numRollsToTarget(30, 30, 500), 222616187);
        Check.eq("paint1", DPVariants.minCostII(new int[][]{{1, 5, 3}, {2, 9, 4}}), 5);
        Check.eq("paint2", DPVariants.minCostII(new int[][]{{1, 3}, {2, 4}}), 5);
        Check.eq("cherry1", DPVariants.cherryPickup(new int[][]{{0, 1, -1}, {1, 0, -1}, {1, 1, 1}}), 5);
        Check.eq("cherry2", DPVariants.cherryPickup(new int[][]{{1, 1, -1}, {1, -1, 1}, {-1, 1, 1}}), 0);
        Check.eq("bound1", DPVariants.findPaths(2, 2, 2, 0, 0), 6);
        Check.eq("bound2", DPVariants.findPaths(1, 3, 3, 0, 1), 12);
        int[] tiles = {1, 1, 2, 5, 11, 24, 53, 117};
        for (int n = 1; n < tiles.length; n++) Check.eq("tilings" + n, DPVariants.numTilings(n), tiles[n]);
        Check.eq("palSub1", DPVariants.countPalindromicSubsequences("bccb"), 6);
        Check.eq("palSub2", DPVariants.countPalindromicSubsequences("abcdabcdabcdabcdabcdabcdabcdabcddcbadcbadcbadcbadcbadcbadcbadcba"), 104860361);
        Check.eq("schemes1", DPVariants.profitableSchemes(5, 3, new int[]{2, 2}, new int[]{2, 3}), 2);
        Check.eq("schemes2", DPVariants.profitableSchemes(10, 5, new int[]{2, 3, 5}, new int[]{6, 7, 8}), 7);
        Check.eq("billboard1", DPVariants.tallestBillboard(new int[]{1, 2, 3, 6}), 6);
        Check.eq("billboard2", DPVariants.tallestBillboard(new int[]{1, 2, 3, 4, 5, 6}), 10);
        Check.eq("billboard3", DPVariants.tallestBillboard(new int[]{1, 2}), 0);
        Check.eq("team1", DPVariants.bestTeamScore(new int[]{1, 3, 5, 10, 15}, new int[]{1, 2, 3, 4, 5}), 34);
        Check.eq("team2", DPVariants.bestTeamScore(new int[]{4, 5, 6, 5}, new int[]{2, 1, 2, 1}), 16);
        Check.eq("team3", DPVariants.bestTeamScore(new int[]{1, 2, 3, 5}, new int[]{8, 9, 10, 1}), 6);
        Check.eq("boxes1", DPVariants.removeBoxes(new int[]{1, 3, 2, 2, 2, 3, 4, 3, 1}), 23);
        Check.eq("boxes2", DPVariants.removeBoxes(new int[]{1, 1, 1}), 9);
        Check.eq("merge1", DPVariants.mergeStones(new int[]{3, 2, 4, 1}, 2), 20);
        Check.eq("merge2", DPVariants.mergeStones(new int[]{3, 2, 4, 1}, 3), -1);
        Check.eq("merge3", DPVariants.mergeStones(new int[]{3, 5, 1, 2, 6}, 3), 25);
        Check.eq("mct1", DPVariants.mctFromLeafValues(new int[]{6, 2, 4}), 32);
        Check.eq("mct2", DPVariants.mctFromLeafValues(new int[]{4, 11}), 44);
        Check.eq("maxSum3", Check.s(DPVariants.maxSumOfThreeSubarrays(new int[]{1, 2, 1, 2, 6, 7, 5, 1}, 2)), "[0, 3, 5]");
        Check.eq("earn1", DPVariants.deleteAndEarn(new int[]{3, 4, 2}), 6);
        Check.eq("earn2", DPVariants.deleteAndEarn(new int[]{2, 2, 3, 3, 3, 4}), 9);
        Check.eq("chain1", DPVariants.longestStrChain(new String[]{"a", "b", "ba", "bca", "bda", "bdca"}), 4);
        Check.eq("chain2", DPVariants.longestStrChain(new String[]{"xbc", "pcxbcf", "xb", "cxbc", "pcxbc"}), 5);
        Check.eq("record2", DPVariants.checkRecord(2), 8);
        Check.eq("record10101", DPVariants.checkRecord(10101), 183236316);
        Check.eq("vowel5", DPVariants.countVowelPermutation(5), 68);

        for (int t = 0; t < 250; t++) {
            int[] a = arr(1 + rnd.nextInt(9), 1, 9);
            Check.eq("predict", DPVariants.predictTheWinner(a), minimax(a, 0, a.length - 1) >= 0);

            int[] piles = arr(1 + rnd.nextInt(7), 1, 9);
            Check.eq("stoneII brute", DPVariants.stoneGameII(piles), stoneBrute(piles, 0, 1));

            int eggs = 1 + rnd.nextInt(3), floors = 1 + rnd.nextInt(25);
            Check.eq("egg brute", DPVariants.superEggDrop(eggs, floors), eggBrute(eggs, floors));

            int dice = 1 + rnd.nextInt(4), faces = 1 + rnd.nextInt(6), target = rnd.nextInt(dice * faces + 2);
            Check.eq("rolls brute", DPVariants.numRollsToTarget(dice, faces, target), rollsBrute(dice, faces, target));

            int rows = 1 + rnd.nextInt(4), cols = 2 + rnd.nextInt(3);
            int[][] costs = new int[rows][cols];
            for (int[] r : costs) for (int j = 0; j < cols; j++) r[j] = 1 + rnd.nextInt(9);
            Check.eq("paint brute", DPVariants.minCostII(costs), paintBrute(costs, 0, -1));

            int gn = 1 + rnd.nextInt(4);
            int[][] grid = new int[gn][gn];
            for (int[] r : grid) for (int j = 0; j < gn; j++) r[j] = rnd.nextInt(5) == 0 ? -1 : rnd.nextInt(2);
            grid[0][0] = 0;
            grid[gn - 1][gn - 1] = 0;
            Check.eq("cherry brute", DPVariants.cherryPickup(grid), cherryBrute(grid));

            int m = 1 + rnd.nextInt(3), n = 1 + rnd.nextInt(3), mv = rnd.nextInt(5), sr = rnd.nextInt(m), sc = rnd.nextInt(n);
            Check.eq("bound brute", DPVariants.findPaths(m, n, mv, sr, sc), (int) (boundBrute(m, n, mv, sr, sc) % 1_000_000_007L));

            String ps = str(1 + rnd.nextInt(9), 3);
            Check.eq("palSub brute", DPVariants.countPalindromicSubsequences(ps), distinctPalSubseq(ps));

            int people = 1 + rnd.nextInt(6), minP = rnd.nextInt(6), crimes = 1 + rnd.nextInt(5);
            int[] gr = arr(crimes, 1, 4), pr = arr(crimes, 0, 4);
            int cnt = 0;
            for (int mask = 0; mask < 1 << crimes; mask++) { int pp = 0, prof = 0; for (int i = 0; i < crimes; i++) if ((mask >> i & 1) != 0) { pp += gr[i]; prof += pr[i]; } if (pp <= people && prof >= minP) cnt++; }
            Check.eq("schemes brute", DPVariants.profitableSchemes(people, minP, gr, pr), cnt);

            int[] rods = arr(1 + rnd.nextInt(7), 1, 8);
            int tb = 0, total = 1;
            for (int i = 0; i < rods.length; i++) total *= 3;
            for (int code = 0; code < total; code++) { int l = 0, r = 0, c = code; for (int v : rods) { int d = c % 3; c /= 3; if (d == 1) l += v; else if (d == 2) r += v; } if (l == r) tb = Math.max(tb, l); }
            Check.eq("billboard brute", DPVariants.tallestBillboard(rods), tb);

            int bn = 1 + rnd.nextInt(7);
            int[] sc2 = arr(bn, 1, 9), ag = arr(bn, 1, 5);
            int bt = 0;
            for (int mask = 0; mask < 1 << bn; mask++) {
                boolean ok = true;
                int sum = 0;
                for (int i = 0; i < bn && ok; i++) if ((mask >> i & 1) != 0) { sum += sc2[i]; for (int j = 0; j < bn; j++) if ((mask >> j & 1) != 0 && ag[i] < ag[j] && sc2[i] > sc2[j]) ok = false; }
                if (ok) bt = Math.max(bt, sum);
            }
            Check.eq("team brute", DPVariants.bestTeamScore(sc2, ag), bt);

            List<Integer> boxes = new ArrayList<>();
            for (int v : arr(1 + rnd.nextInt(6), 1, 3)) boxes.add(v);
            Check.eq("boxes brute", DPVariants.removeBoxes(boxes.stream().mapToInt(Integer::intValue).toArray()), boxesBrute(boxes));

            int sn = 1 + rnd.nextInt(7), kk = 2 + rnd.nextInt(2);
            int[] st = arr(sn, 1, 9);
            List<Integer> sl = new ArrayList<>();
            for (int v : st) sl.add(v);
            int mb = mergeBrute(sl, kk);
            Check.eq("mergeStones brute", DPVariants.mergeStones(st, kk), mb == Integer.MAX_VALUE ? -1 : mb);

            int[] leaf = arr(2 + rnd.nextInt(6), 1, 9);
            Check.eq("mct brute", DPVariants.mctFromLeafValues(leaf), mctBrute(leaf, 0, leaf.length - 1)[1]);

            String s1 = str(1 + rnd.nextInt(6), 3), s2 = str(1 + rnd.nextInt(6), 3);
            String scs = DPVariants.shortestCommonSupersequence(s1, s2);
            Check.eq("scs sub1", isSubseq(s1, scs), true);
            Check.eq("scs sub2", isSubseq(s2, scs), true);
            Check.eq("scs len", scs.length(), s1.length() + s2.length() - lcs(s1, s2));

            String pi = str(1 + rnd.nextInt(8), 3);
            Check.eq("minInsertions", DPVariants.minInsertions(pi), pi.length() - lcs(pi, new StringBuilder(pi).reverse().toString()));

            int[] three = arr(6 + rnd.nextInt(6), 0, 9);
            int k3 = 1 + rnd.nextInt(2);
            int[] w3 = new int[three.length - k3 + 1];
            for (int i = 0; i < w3.length; i++) for (int j = 0; j < k3; j++) w3[i] += three[i + j];
            int topSum = -1;
            int[] topIdx = null;
            for (int i = 0; i < w3.length; i++) for (int j = i + k3; j < w3.length; j++) for (int l = j + k3; l < w3.length; l++) if (w3[i] + w3[j] + w3[l] > topSum) { topSum = w3[i] + w3[j] + w3[l]; topIdx = new int[]{i, j, l}; }
            if (topIdx != null) Check.eq("three brute", Check.s(DPVariants.maxSumOfThreeSubarrays(three, k3)), Check.s(topIdx));

            int[] ea = arr(1 + rnd.nextInt(8), 1, 8);
            int eb = 0;
            Set<Integer> vs = new TreeSet<>();
            for (int v : ea) vs.add(v);
            List<Integer> vl = new ArrayList<>(vs);
            for (int mask = 0; mask < 1 << vl.size(); mask++) {
                boolean ok = true;
                for (int i = 0; i < vl.size() && ok; i++) for (int j = i + 1; j < vl.size(); j++) if ((mask >> i & 1) != 0 && (mask >> j & 1) != 0 && vl.get(j) - vl.get(i) == 1) ok = false;
                if (!ok) continue;
                int sum = 0;
                for (int v : ea) { int idx = vl.indexOf(v); if ((mask >> idx & 1) != 0) sum += v; }
                eb = Math.max(eb, sum);
            }
            Check.eq("earn brute", DPVariants.deleteAndEarn(ea), eb);

            int ib = 2 + rnd.nextInt(12);
            Check.eq("integerBreak", DPVariants.integerBreak(ib), breakBrute(ib));

            int[] dv = arr(1 + rnd.nextInt(8), 1, 24);
            Set<Integer> uniq = new TreeSet<>();
            for (int v : dv) uniq.add(v);
            int[] ud = uniq.stream().mapToInt(Integer::intValue).toArray();
            List<Integer> dsub = DPVariants.largestDivisibleSubset(ud.clone());
            for (int i = 0; i < dsub.size(); i++) for (int j = 0; j < dsub.size(); j++) if (i != j) Check.eq("divisible pair", dsub.get(i) % dsub.get(j) == 0 || dsub.get(j) % dsub.get(i) == 0, true);
            int bestSize = 0;
            for (int mask = 1; mask < 1 << ud.length; mask++) {
                boolean ok = true;
                for (int i = 0; i < ud.length && ok; i++) for (int j = i + 1; j < ud.length; j++) if ((mask >> i & 1) != 0 && (mask >> j & 1) != 0 && ud[j] % ud[i] != 0) ok = false;
                if (ok) bestSize = Math.max(bestSize, Integer.bitCount(mask));
            }
            Check.eq("divisible size", dsub.size(), bestSize);

            int wn = 1 + rnd.nextInt(7);
            Set<String> wset = new TreeSet<>();
            for (int i = 0; i < wn; i++) wset.add(str(1 + rnd.nextInt(4), 2));
            String[] words = wset.toArray(new String[0]);
            Check.eq("strChain", DPVariants.longestStrChain(words.clone()), chainBrute(words));

            int vn = 1 + rnd.nextInt(6);
            Check.eq("vowel brute", DPVariants.countVowelPermutation(vn), vowelBrute("", vn));

            int rn = 1 + rnd.nextInt(8);
            Check.eq("record brute", DPVariants.checkRecord(rn), recordBrute(rn, 0, 0, 0));
        }
    }
    static int minimax(int[] a, int l, int r) { return l == r ? a[l] : Math.max(a[l] - minimax(a, l + 1, r), a[r] - minimax(a, l, r - 1)); }
    static int stoneBrute(int[] p, int i, int m) {
        int n = p.length;
        if (i >= n) return 0;
        int total = 0;
        for (int k = i; k < n; k++) total += p[k];
        int best = 0;
        for (int x = 1; x <= 2 * m && i + x <= n; x++) best = Math.max(best, total - stoneBrute(p, i + x, Math.max(m, x)));
        return best;
    }
    static int eggBrute(int eggs, int floors) {
        int[][] dp = new int[eggs + 1][floors + 1];
        for (int f = 1; f <= floors; f++) dp[1][f] = f;
        for (int e = 2; e <= eggs; e++) for (int f = 1; f <= floors; f++) { dp[e][f] = Integer.MAX_VALUE; for (int x = 1; x <= f; x++) dp[e][f] = Math.min(dp[e][f], 1 + Math.max(dp[e - 1][x - 1], dp[e][f - x])); }
        return dp[eggs][floors];
    }
    static int rollsBrute(int dice, int faces, int target) { if (dice == 0) return target == 0 ? 1 : 0; int c = 0; for (int f = 1; f <= faces; f++) c += rollsBrute(dice - 1, faces, target - f); return c; }
    static int paintBrute(int[][] c, int row, int prev) { if (row == c.length) return 0; int best = Integer.MAX_VALUE; for (int j = 0; j < c[0].length; j++) if (j != prev) best = Math.min(best, c[row][j] + paintBrute(c, row + 1, j)); return best; }
    static int cherryBrute(int[][] g) { int n = g.length; return Math.max(0, cb(g, 0, 0, 0, new Integer[n][n][n])); }
    static int cb(int[][] g, int r1, int c1, int r2, Integer[][][] memo) {
        int n = g.length, c2 = r1 + c1 - r2;
        if (r1 >= n || c1 >= n || r2 >= n || c2 >= n || g[r1][c1] == -1 || g[r2][c2] == -1) return Integer.MIN_VALUE / 2;
        if (r1 == n - 1 && c1 == n - 1) return g[r1][c1];
        if (memo[r1][c1][r2] != null) return memo[r1][c1][r2];
        int gain = g[r1][c1] + (r1 == r2 && c1 == c2 ? 0 : g[r2][c2]);
        int best = Math.max(Math.max(cb(g, r1 + 1, c1, r2 + 1, memo), cb(g, r1, c1 + 1, r2, memo)), Math.max(cb(g, r1 + 1, c1, r2, memo), cb(g, r1, c1 + 1, r2 + 1, memo)));
        return memo[r1][c1][r2] = gain + best;
    }
    static long boundBrute(int m, int n, int moves, int r, int c) {
        if (r < 0 || c < 0 || r >= m || c >= n) return 1;
        if (moves == 0) return 0;
        return boundBrute(m, n, moves - 1, r + 1, c) + boundBrute(m, n, moves - 1, r - 1, c) + boundBrute(m, n, moves - 1, r, c + 1) + boundBrute(m, n, moves - 1, r, c - 1);
    }
    static int distinctPalSubseq(String s) {
        Set<String> set = new HashSet<>();
        for (int mask = 1; mask < 1 << s.length(); mask++) {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < s.length(); i++) if ((mask >> i & 1) != 0) sb.append(s.charAt(i));
            if (sb.toString().equals(sb.reverse().toString())) set.add(sb.toString());
        }
        return set.size();
    }
    static int boxesBrute(List<Integer> b) {
        if (b.isEmpty()) return 0;
        int best = 0;
        for (int i = 0; i < b.size(); ) {
            int j = i;
            while (j < b.size() && b.get(j).equals(b.get(i))) j++;
            List<Integer> rest = new ArrayList<>(b.subList(0, i));
            rest.addAll(b.subList(j, b.size()));
            best = Math.max(best, (j - i) * (j - i) + boxesBrute(rest));
            i = j;
        }
        // also allow removing a partial run: remove any contiguous equal subrun
        for (int i = 0; i < b.size(); i++) for (int j = i + 1; j <= b.size(); j++) {
            boolean same = true;
            for (int p = i; p < j; p++) if (!b.get(p).equals(b.get(i))) same = false;
            if (!same) break;
            List<Integer> rest = new ArrayList<>(b.subList(0, i));
            rest.addAll(b.subList(j, b.size()));
            best = Math.max(best, (j - i) * (j - i) + boxesBrute(rest));
        }
        return best;
    }
    static int mergeBrute(List<Integer> s, int k) {
        if (s.size() == 1) return 0;
        int best = Integer.MAX_VALUE;
        for (int i = 0; i + k <= s.size(); i++) {
            int sum = 0;
            for (int j = i; j < i + k; j++) sum += s.get(j);
            List<Integer> next = new ArrayList<>(s.subList(0, i));
            next.add(sum);
            next.addAll(s.subList(i + k, s.size()));
            int sub = mergeBrute(next, k);
            if (sub != Integer.MAX_VALUE) best = Math.min(best, sub + sum);
        }
        return best;
    }
    static int[] mctBrute(int[] a, int l, int r) { // {maxLeaf, minCost}
        if (l == r) return new int[]{a[l], 0};
        int best = Integer.MAX_VALUE, mx = 0;
        for (int i = l; i <= r; i++) mx = Math.max(mx, a[i]);
        for (int k = l; k < r; k++) { int[] L = mctBrute(a, l, k), R = mctBrute(a, k + 1, r); best = Math.min(best, L[1] + R[1] + L[0] * R[0]); }
        return new int[]{mx, best};
    }
    static boolean isSubseq(String t, String s) { int j = 0; for (int i = 0; i < s.length() && j < t.length(); i++) if (s.charAt(i) == t.charAt(j)) j++; return j == t.length(); }
    static int lcs(String a, String b) { int[][] dp = new int[a.length() + 1][b.length() + 1]; for (int i = 1; i <= a.length(); i++) for (int j = 1; j <= b.length(); j++) dp[i][j] = a.charAt(i - 1) == b.charAt(j - 1) ? dp[i - 1][j - 1] + 1 : Math.max(dp[i - 1][j], dp[i][j - 1]); return dp[a.length()][b.length()]; }
    static int breakBrute(int n) { int best = 0; for (int i = 1; i < n; i++) best = Math.max(best, Math.max(i * (n - i), i * breakBrute(n - i))); return n == 1 ? 1 : best; }
    static int chainBrute(String[] words) {
        int best = 1;
        for (String w : words) best = Math.max(best, chainFrom(w, words));
        return best;
    }
    static int chainFrom(String w, String[] words) {
        int best = 1;
        for (String p : words) if (p.length() + 1 == w.length() && isSubseq(p, w)) best = Math.max(best, 1 + chainFrom(p, words));
        return best;
    }
    static int vowelBrute(String cur, int n) {
        if (cur.length() == n) return 1;
        int c = 0;
        for (char v : "aeiou".toCharArray()) {
            if (!cur.isEmpty()) {
                char p = cur.charAt(cur.length() - 1);
                boolean ok = (p == 'a' && v == 'e') || (p == 'e' && (v == 'a' || v == 'i')) || (p == 'i' && v != 'i') || (p == 'o' && (v == 'i' || v == 'u')) || (p == 'u' && v == 'a');
                if (!ok) continue;
            }
            c += vowelBrute(cur + v, n);
        }
        return c;
    }
    static int recordBrute(int n, int pos, int absent, int lates) {
        if (pos == n) return 1;
        int c = recordBrute(n, pos + 1, absent, 0);
        if (absent == 0) c += recordBrute(n, pos + 1, 1, 0);
        if (lates < 2) c += recordBrute(n, pos + 1, absent, lates + 1);
        return c;
    }
}
