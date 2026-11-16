import java.util.*;

public class BacktrackingVariants {

    // ---------- Matchsticks to Square (LC 473): fill 4 sides; sort descending and skip equal-state siblings ----------
    static boolean makesquare(int[] sticks) {
        int sum = 0;
        for (int x : sticks) sum += x;
        if (sum % 4 != 0 || sticks.length < 4) return false;
        Integer[] s = Arrays.stream(sticks).boxed().toArray(Integer[]::new);
        Arrays.sort(s, Collections.reverseOrder());
        return fill(s, 0, new int[4], sum / 4);
    }

    private static boolean fill(Integer[] s, int i, int[] side, int target) {
        if (i == s.length) return true;
        for (int j = 0; j < 4; j++) {
            if (side[j] + s[i] > target) continue;
            boolean dup = false;
            for (int p = 0; p < j; p++) if (side[p] == side[j]) dup = true; // same load as an earlier side: symmetric branch
            if (dup) continue;
            side[j] += s[i];
            if (fill(s, i + 1, side, target)) return true;
            side[j] -= s[i];
        }
        return false;
    }

    // ---------- Unique Paths III (LC 980): visit every empty cell exactly once ----------
    static int uniquePathsIII(int[][] g) {
        int m = g.length, n = g[0].length, empty = 1, sr = 0, sc = 0;
        for (int i = 0; i < m; i++)
            for (int j = 0; j < n; j++) {
                if (g[i][j] == 0) empty++;
                else if (g[i][j] == 1) { sr = i; sc = j; }
            }
        return walk(g, sr, sc, empty);
    }

    private static int walk(int[][] g, int r, int c, int remain) {
        if (r < 0 || c < 0 || r >= g.length || c >= g[0].length || g[r][c] == -1) return 0;
        if (g[r][c] == 2) return remain == 0 ? 1 : 0; // every empty cell and the start were consumed
        int saved = g[r][c];
        g[r][c] = -1;
        int paths = walk(g, r + 1, c, remain - 1) + walk(g, r - 1, c, remain - 1) + walk(g, r, c + 1, remain - 1) + walk(g, r, c - 1, remain - 1);
        g[r][c] = saved;
        return paths;
    }

    // ---------- Combination Sum III (LC 216): k distinct digits 1..9 summing to n ----------
    static List<List<Integer>> combinationSum3(int k, int n) {
        List<List<Integer>> res = new ArrayList<>();
        cs3(k, n, 1, new ArrayList<>(), res);
        return res;
    }

    private static void cs3(int k, int remain, int start, List<Integer> cur, List<List<Integer>> res) {
        if (cur.size() == k) { if (remain == 0) res.add(new ArrayList<>(cur)); return; }
        for (int d = start; d <= 9 && d <= remain; d++) {
            cur.add(d);
            cs3(k, remain - d, d + 1, cur, res);
            cur.remove(cur.size() - 1);
        }
    }

    // ---------- Permutation Sequence (LC 60): no search at all; factorial number system picks each digit directly ----------
    static String getPermutation(int n, int k) {
        List<Integer> digits = new ArrayList<>();
        int[] fact = new int[n + 1];
        fact[0] = 1;
        for (int i = 1; i <= n; i++) { fact[i] = fact[i - 1] * i; digits.add(i); }
        k--;
        StringBuilder sb = new StringBuilder();
        for (int i = n; i >= 1; i--) {
            int idx = k / fact[i - 1];
            sb.append(digits.remove(idx));
            k %= fact[i - 1];
        }
        return sb.toString();
    }

    // ---------- Palindrome Permutation II (LC 267): build the left half from half the counts, mirror it ----------
    static List<String> generatePalindromes(String s) {
        int[] cnt = new int[128];
        for (char c : s.toCharArray()) cnt[c]++;
        char mid = 0;
        int odd = 0;
        StringBuilder half = new StringBuilder();
        for (int c = 0; c < 128; c++) {
            if (cnt[c] % 2 == 1) { odd++; mid = (char) c; }
            for (int i = 0; i < cnt[c] / 2; i++) half.append((char) c);
        }
        List<String> res = new ArrayList<>();
        if (odd > 1) return res;
        char[] h = half.toString().toCharArray();
        Arrays.sort(h);
        permuteHalf(h, new boolean[h.length], new StringBuilder(), mid, res);
        return res;
    }

    private static void permuteHalf(char[] h, boolean[] used, StringBuilder cur, char mid, List<String> res) {
        if (cur.length() == h.length) {
            String left = cur.toString();
            res.add(left + (mid == 0 ? "" : String.valueOf(mid)) + new StringBuilder(left).reverse());
            return;
        }
        for (int i = 0; i < h.length; i++) {
            if (used[i] || (i > 0 && h[i] == h[i - 1] && !used[i - 1])) continue;
            used[i] = true;
            cur.append(h[i]);
            permuteHalf(h, used, cur, mid, res);
            cur.setLength(cur.length() - 1);
            used[i] = false;
        }
    }

    // ---------- Letter Case Permutation (LC 784) ----------
    static List<String> letterCasePermutation(String s) {
        List<String> res = new ArrayList<>();
        lcp(s.toCharArray(), 0, res);
        return res;
    }

    private static void lcp(char[] c, int i, List<String> res) {
        if (i == c.length) { res.add(new String(c)); return; }
        lcp(c, i + 1, res);
        if (Character.isLetter(c[i])) {
            c[i] ^= 32; // flip the case bit
            lcp(c, i + 1, res);
            c[i] ^= 32;
        }
    }

    // ---------- Different Ways to Add Parentheses (LC 241): split at every operator, combine left x right results (memoized) ----------
    static List<Integer> diffWaysToCompute(String expr) { return ways(expr, new HashMap<>()); }

    private static List<Integer> ways(String e, Map<String, List<Integer>> memo) {
        if (memo.containsKey(e)) return memo.get(e);
        List<Integer> res = new ArrayList<>();
        for (int i = 0; i < e.length(); i++) {
            char c = e.charAt(i);
            if (c != '+' && c != '-' && c != '*') continue;
            for (int l : ways(e.substring(0, i), memo))
                for (int r : ways(e.substring(i + 1), memo)) res.add(c == '+' ? l + r : c == '-' ? l - r : l * r);
        }
        if (res.isEmpty()) res.add(Integer.parseInt(e));
        memo.put(e, res);
        return res;
    }

    // ---------- Remove Invalid Parentheses (LC 301): BFS by number of removals, first level with a valid string wins ----------
    static List<String> removeInvalidParentheses(String s) {
        List<String> res = new ArrayList<>();
        Set<String> seen = new HashSet<>(List.of(s));
        Deque<String> q = new ArrayDeque<>(List.of(s));
        boolean found = false;
        while (!q.isEmpty() && !found) {
            for (int size = q.size(); size > 0; size--) {
                String cur = q.poll();
                if (validParens(cur)) { res.add(cur); found = true; }
                if (found) continue;
                for (int i = 0; i < cur.length(); i++) {
                    if (cur.charAt(i) != '(' && cur.charAt(i) != ')') continue;
                    String next = cur.substring(0, i) + cur.substring(i + 1);
                    if (seen.add(next)) q.add(next);
                }
            }
        }
        return res;
    }

    private static boolean validParens(String s) {
        int open = 0;
        for (char c : s.toCharArray()) {
            if (c == '(') open++;
            else if (c == ')' && --open < 0) return false;
        }
        return open == 0;
    }

    // ---------- Maximum Length of a Concatenated String with Unique Characters (LC 1239): bitmask per word ----------
    static int maxLength(List<String> arr) {
        List<Integer> masks = new ArrayList<>();
        List<Integer> lens = new ArrayList<>();
        for (String w : arr) {
            int m = 0;
            boolean ok = true;
            for (char c : w.toCharArray()) { if ((m >> (c - 'a') & 1) != 0) { ok = false; break; } m |= 1 << (c - 'a'); }
            if (ok) { masks.add(m); lens.add(w.length()); }
        }
        return pickMax(masks, lens, 0, 0);
    }

    private static int pickMax(List<Integer> masks, List<Integer> lens, int i, int used) {
        if (i == masks.size()) return 0;
        int best = pickMax(masks, lens, i + 1, used);
        if ((masks.get(i) & used) == 0) best = Math.max(best, lens.get(i) + pickMax(masks, lens, i + 1, used | masks.get(i)));
        return best;
    }

    // ---------- Android Unlock Patterns (LC 351): symmetry cuts the search to 3 starts (corner x4, edge x4, center) ----------
    static int numberOfPatterns(int m, int n) {
        int[][] skip = new int[10][10];
        skip[1][3] = skip[3][1] = 2; skip[4][6] = skip[6][4] = 5; skip[7][9] = skip[9][7] = 8;
        skip[1][7] = skip[7][1] = 4; skip[2][8] = skip[8][2] = 5; skip[3][9] = skip[9][3] = 6;
        skip[1][9] = skip[9][1] = skip[3][7] = skip[7][3] = skip[2][8] = skip[8][2] = skip[4][6] = skip[6][4] = 5;
        skip[2][8] = skip[8][2] = 5;
        boolean[] seen = new boolean[10];
        int res = 0;
        for (int len = m; len <= n; len++) {
            res += 4 * dfsPattern(skip, seen, 1, len - 1);
            res += 4 * dfsPattern(skip, seen, 2, len - 1);
            res += dfsPattern(skip, seen, 5, len - 1);
        }
        return res;
    }

    private static int dfsPattern(int[][] skip, boolean[] seen, int cur, int remain) {
        if (remain < 0) return 0;
        if (remain == 0) return 1;
        seen[cur] = true;
        int count = 0;
        for (int next = 1; next <= 9; next++)
            if (!seen[next] && (skip[cur][next] == 0 || seen[skip[cur][next]])) count += dfsPattern(skip, seen, next, remain - 1);
        seen[cur] = false;
        return count;
    }

    // ---------- Generalized Abbreviation (LC 320): each letter is kept or merged into a running count ----------
    static List<String> generateAbbreviations(String word) {
        List<String> res = new ArrayList<>();
        abbr(word, 0, 0, new StringBuilder(), res);
        return res;
    }

    private static void abbr(String w, int i, int run, StringBuilder cur, List<String> res) {
        int len = cur.length();
        if (i == w.length()) {
            if (run > 0) cur.append(run);
            res.add(cur.toString());
        } else {
            abbr(w, i + 1, run + 1, cur, res); // abbreviate this letter
            if (run > 0) cur.append(run);
            cur.append(w.charAt(i));
            abbr(w, i + 1, 0, cur, res);       // keep this letter
        }
        cur.setLength(len);
    }

    // ---------- Optimal Account Balancing (LC 465): net balances, then backtrack settling the first non-zero debt ----------
    static int minTransfers(int[][] transactions) {
        Map<Integer, Integer> net = new HashMap<>();
        for (int[] t : transactions) { net.merge(t[0], -t[2], Integer::sum); net.merge(t[1], t[2], Integer::sum); }
        List<Integer> debts = new ArrayList<>();
        for (int v : net.values()) if (v != 0) debts.add(v);
        return settle(debts, 0);
    }

    private static int settle(List<Integer> d, int i) {
        while (i < d.size() && d.get(i) == 0) i++;
        if (i == d.size()) return 0;
        int best = Integer.MAX_VALUE;
        for (int j = i + 1; j < d.size(); j++) {
            if ((long) d.get(i) * d.get(j) >= 0) continue; // only settle against the opposite sign
            d.set(j, d.get(j) + d.get(i));
            best = Math.min(best, 1 + settle(d, i + 1));
            d.set(j, d.get(j) - d.get(i));
        }
        return best;
    }

    // ---------- All Paths From Source to Target in a DAG (LC 797) ----------
    static List<List<Integer>> allPathsSourceTarget(int[][] graph) {
        List<List<Integer>> res = new ArrayList<>();
        List<Integer> path = new ArrayList<>(List.of(0));
        paths(graph, 0, path, res);
        return res;
    }

    private static void paths(int[][] g, int u, List<Integer> path, List<List<Integer>> res) {
        if (u == g.length - 1) { res.add(new ArrayList<>(path)); return; }
        for (int v : g[u]) {
            path.add(v);
            paths(g, v, path, res);
            path.remove(path.size() - 1);
        }
    }
}
