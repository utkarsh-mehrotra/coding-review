import java.util.*;

public class GreedyVariantsTest {
    public static void main(String[] x) {
        Random r = new Random(21);
        // ---------- greedy ----------
        Check.eq("negations", GreedyVariants.largestSumAfterKNegations(new int[]{4, 2, 3}, 1) + "," + GreedyVariants.largestSumAfterKNegations(new int[]{2, -3, -1, 5, -4}, 2), "5,13");
        Check.eq("wiggle", GreedyVariants.wiggleMaxLength(new int[]{1, 7, 4, 9, 2, 5}) + "," + GreedyVariants.wiggleMaxLength(new int[]{1, 17, 5, 10, 13, 15, 10, 5, 16, 8}), "6,7");
        Check.eq("dupLetters", GreedyVariants.removeDuplicateLetters("bcabc") + "," + GreedyVariants.removeDuplicateLetters("cbacdcbc"), "abc,acdb");
        Check.eq("twoCity", GreedyVariants.twoCitySchedCost(new int[][]{{10, 20}, {30, 200}, {400, 50}, {30, 20}}), 110);
        Check.eq("tokens", GreedyVariants.bagOfTokensScore(new int[]{100, 200, 300, 400}, 200), 2);
        Check.eq("brokenCalc", GreedyVariants.brokenCalc(2, 3) + "," + GreedyVariants.brokenCalc(5, 8) + "," + GreedyVariants.brokenCalc(3, 10), "2,2,3");
        Check.eq("monotone", GreedyVariants.monotoneIncreasingDigits(332) + "," + GreedyVariants.monotoneIncreasingDigits(10) + "," + GreedyVariants.monotoneIncreasingDigits(1234), "299,9,1234");
        Check.eq("split659", GreedyVariants.isPossibleSplit(new int[]{1, 2, 3, 3, 4, 5}) + "," + GreedyVariants.isPossibleSplit(new int[]{1, 2, 3, 4, 4, 5}), "true,false");
        Check.eq("swap", GreedyVariants.maximumSwap(2736) + "," + GreedyVariants.maximumSwap(9973), "7236,9973");
        Check.eq("nonDecr", GreedyVariants.checkPossibility(new int[]{4, 2, 3}) + "," + GreedyVariants.checkPossibility(new int[]{4, 2, 1}), "true,false");
        Check.eq("advantage", Check.s(GreedyVariants.advantageCount(new int[]{2, 7, 11, 15}, new int[]{1, 10, 4, 11})), "[2, 11, 7, 15]");
        Check.eq("fracKnap", String.format("%.2f", GreedyVariants.fractionalKnapsack(new int[]{10, 20, 30}, new int[]{60, 100, 120}, 50)), "240.00");
        Check.eq("domino", GreedyVariants.minDominoRotations(new int[]{2, 1, 2, 4, 2, 2}, new int[]{5, 2, 6, 2, 3, 2}) + "," + GreedyVariants.minDominoRotations(new int[]{3, 5, 1, 2, 3}, new int[]{3, 6, 3, 3, 4}), "2,-1");
        Check.eq("maxNumber", Check.s(GreedyVariants.maxNumber(new int[]{3, 4, 6, 5}, new int[]{9, 1, 2, 5, 8, 3}, 5)), "[9, 8, 6, 5, 3]");
        Check.eq("maxNumber2", Check.s(GreedyVariants.maxNumber(new int[]{6, 7}, new int[]{6, 0, 4}, 5)), "[6, 7, 6, 0, 4]");
        Check.eq("minAdd", GreedyVariants.minAddToMakeValid("())") + "," + GreedyVariants.minAddToMakeValid("((("), "1,3");
        Check.eq("incSub", GreedyVariants.minNumberOperations(new int[]{1, 2, 3, 2, 1}) + "," + GreedyVariants.minNumberOperations(new int[]{3, 1, 5, 4, 2}), "3,7");
        Check.eq("pairChain", GreedyVariants.findLongestChain(new int[][]{{1, 2}, {2, 3}, {3, 4}}), 2);
        for (int t = 0; t < 300; t++) {
            int n = 1 + r.nextInt(9);
            int[] a = new int[n];
            for (int i = 0; i < n; i++) a[i] = r.nextInt(6);
            Check.eq("wiggleRand", GreedyVariants.wiggleMaxLength(a), wiggleBrute(a));
            Check.eq("nonDecrRand", GreedyVariants.checkPossibility(a), nonDecrBrute(a));
            int num = 1 + r.nextInt(100000);
            Check.eq("maxSwapRand", GreedyVariants.maximumSwap(num), maxSwapBrute(num));
            Check.eq("monotoneRand", GreedyVariants.monotoneIncreasingDigits(num), monotoneBrute(num));
            int st = 1 + r.nextInt(20), tg = 1 + r.nextInt(40);
            Check.eq("brokenRand", GreedyVariants.brokenCalc(st, tg), brokenBfs(st, tg));
            String s = "";
            for (int i = 0; i < 1 + r.nextInt(8); i++) s += (char) ('a' + r.nextInt(4));
            Check.eq("dupLettersRand", GreedyVariants.removeDuplicateLetters(s), dupLettersBrute(s));
            int[] p1 = new int[r.nextInt(5)], p2 = new int[r.nextInt(5)];
            for (int i = 0; i < p1.length; i++) p1[i] = r.nextInt(4);
            for (int i = 0; i < p2.length; i++) p2[i] = r.nextInt(4);
            int kk = r.nextInt(p1.length + p2.length + 1);
            if (kk > 0) Check.eq("maxNumberRand", Check.s(GreedyVariants.maxNumber(p1, p2, kk)), Check.s(maxNumberBrute(p1, p2, kk)));
            int c = 2 * (1 + r.nextInt(3));
            int[][] costs = new int[c][2];
            for (int[] row : costs) { row[0] = r.nextInt(50); row[1] = r.nextInt(50); }
            Check.eq("twoCityRand", GreedyVariants.twoCitySchedCost(costs), twoCityBrute(costs, 0, 0, 0));
        }

        Check.done("GreedyVariantsTest");
    }

    static int wiggleBrute(int[] a) {
        int n = a.length, best = 1;
        for (int m = 1; m < (1 << n); m++) {
            List<Integer> sub = new ArrayList<>();
            for (int i = 0; i < n; i++) if ((m >> i & 1) != 0) sub.add(a[i]);
            boolean ok = true;
            for (int i = 1; i < sub.size(); i++) {
                int d = sub.get(i) - sub.get(i - 1);
                if (d == 0 || (i > 1 && (d > 0) == (sub.get(i - 1) - sub.get(i - 2) > 0))) { ok = false; break; }
            }
            if (ok) best = Math.max(best, sub.size());
        }
        return best;
    }
    static boolean nonDecrBrute(int[] a) {
        if (sorted(a)) return true;
        for (int i = 0; i < a.length; i++) {
            int[] b = a.clone();
            for (int v = -1; v <= 7; v++) { b[i] = v; if (sorted(b)) return true; }
        }
        return false;
    }
    static boolean sorted(int[] a) { for (int i = 1; i < a.length; i++) if (a[i - 1] > a[i]) return false; return true; }
    static int maxSwapBrute(int num) {
        char[] d = String.valueOf(num).toCharArray();
        int best = num;
        for (int i = 0; i < d.length; i++) for (int j = i + 1; j < d.length; j++) {
            char[] c = d.clone();
            char t = c[i]; c[i] = c[j]; c[j] = t;
            best = Math.max(best, Integer.parseInt(new String(c)));
        }
        return best;
    }
    static int monotoneBrute(int n) {
        for (int v = n; v >= 0; v--) {
            String s = String.valueOf(v);
            boolean ok = true;
            for (int i = 1; i < s.length(); i++) if (s.charAt(i - 1) > s.charAt(i)) ok = false;
            if (ok) return v;
        }
        return 0;
    }
    static int brokenBfs(int start, int target) {
        Map<Integer, Integer> dist = new HashMap<>();
        Deque<Integer> q = new ArrayDeque<>();
        dist.put(start, 0);
        q.add(start);
        while (!q.isEmpty()) {
            int v = q.poll();
            if (v == target) return dist.get(v);
            for (int nx : new int[]{v * 2, v - 1}) if (nx >= 1 && nx <= 200 && !dist.containsKey(nx)) { dist.put(nx, dist.get(v) + 1); q.add(nx); }
        }
        return -1;
    }
    static String dupLettersBrute(String s) {
        Set<Character> need = new HashSet<>();
        for (char c : s.toCharArray()) need.add(c);
        String best = null;
        for (int m = 1; m < (1 << s.length()); m++) {
            StringBuilder sb = new StringBuilder();
            Set<Character> seen = new HashSet<>();
            boolean ok = true;
            for (int i = 0; i < s.length(); i++) if ((m >> i & 1) != 0) { if (!seen.add(s.charAt(i))) { ok = false; break; } sb.append(s.charAt(i)); }
            if (ok && seen.equals(need) && (best == null || sb.toString().compareTo(best) < 0)) best = sb.toString();
        }
        return best;
    }
    static int[] maxNumberBrute(int[] a, int[] b, int k) {
        int[] best = null;
        for (int ma = 0; ma < (1 << a.length); ma++) for (int mb = 0; mb < (1 << b.length); mb++) {
            if (Integer.bitCount(ma) + Integer.bitCount(mb) != k) continue;
            List<Integer> sa = new ArrayList<>(), sb = new ArrayList<>();
            for (int i = 0; i < a.length; i++) if ((ma >> i & 1) != 0) sa.add(a[i]);
            for (int i = 0; i < b.length; i++) if ((mb >> i & 1) != 0) sb.add(b[i]);
            // all interleavings
            List<int[]> all = new ArrayList<>();
            interleave(sa, 0, sb, 0, new ArrayList<>(), all);
            for (int[] c : all) if (best == null || cmp(c, best) > 0) best = c;
        }
        return best;
    }
    static void interleave(List<Integer> a, int i, List<Integer> b, int j, List<Integer> cur, List<int[]> out) {
        if (i == a.size() && j == b.size()) { out.add(cur.stream().mapToInt(Integer::intValue).toArray()); return; }
        if (i < a.size()) { cur.add(a.get(i)); interleave(a, i + 1, b, j, cur, out); cur.remove(cur.size() - 1); }
        if (j < b.size()) { cur.add(b.get(j)); interleave(a, i, b, j + 1, cur, out); cur.remove(cur.size() - 1); }
    }
    static int cmp(int[] x, int[] y) { for (int i = 0; i < x.length; i++) if (x[i] != y[i]) return Integer.compare(x[i], y[i]); return 0; }
    static int twoCityBrute(int[][] c, int i, int a, int b) {
        if (i == c.length) return a == b ? 0 : 1_000_000;
        return Math.min(c[i][0] + twoCityBrute(c, i + 1, a + 1, b), c[i][1] + twoCityBrute(c, i + 1, a, b + 1));
    }
}
