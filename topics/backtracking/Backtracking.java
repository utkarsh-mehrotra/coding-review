import java.util.*;

public class Backtracking {

    // ===== SKELETON =====
    //   void dfs(state) { if (complete) { record; return; } for (choice in choices) { if (!valid) continue; choose; dfs(next); undo; } }
    // Duplicates in input: sort first, then skip `i > start && a[i] == a[i-1]` at the same depth.
    // Subsets vs permutations: subsets move a START index forward; permutations use a USED array.

    // Subsets (LC 78)
    static List<List<Integer>> subsets(int[] a) {
        List<List<Integer>> res = new ArrayList<>();
        subsetsDfs(a, 0, new ArrayList<>(), res);
        return res;
    }

    private static void subsetsDfs(int[] a, int start, List<Integer> cur, List<List<Integer>> res) {
        res.add(new ArrayList<>(cur));
        for (int i = start; i < a.length; i++) {
            cur.add(a[i]);
            subsetsDfs(a, i + 1, cur, res);
            cur.remove(cur.size() - 1);
        }
    }

    // Subsets II with duplicates (LC 90)
    static List<List<Integer>> subsetsWithDup(int[] a) {
        Arrays.sort(a);
        List<List<Integer>> res = new ArrayList<>();
        dupDfs(a, 0, new ArrayList<>(), res);
        return res;
    }

    private static void dupDfs(int[] a, int start, List<Integer> cur, List<List<Integer>> res) {
        res.add(new ArrayList<>(cur));
        for (int i = start; i < a.length; i++) {
            if (i > start && a[i] == a[i - 1]) continue;
            cur.add(a[i]);
            dupDfs(a, i + 1, cur, res);
            cur.remove(cur.size() - 1);
        }
    }

    // Permutations (LC 46)
    static List<List<Integer>> permute(int[] a) {
        List<List<Integer>> res = new ArrayList<>();
        permDfs(a, new boolean[a.length], new ArrayList<>(), res);
        return res;
    }

    private static void permDfs(int[] a, boolean[] used, List<Integer> cur, List<List<Integer>> res) {
        if (cur.size() == a.length) { res.add(new ArrayList<>(cur)); return; }
        for (int i = 0; i < a.length; i++) {
            if (used[i]) continue;
            used[i] = true;
            cur.add(a[i]);
            permDfs(a, used, cur, res);
            cur.remove(cur.size() - 1);
            used[i] = false;
        }
    }

    // Permutations II with duplicates (LC 47): skip a[i] if its equal left twin is not used on this path
    static List<List<Integer>> permuteUnique(int[] a) {
        Arrays.sort(a);
        List<List<Integer>> res = new ArrayList<>();
        uniqDfs(a, new boolean[a.length], new ArrayList<>(), res);
        return res;
    }

    private static void uniqDfs(int[] a, boolean[] used, List<Integer> cur, List<List<Integer>> res) {
        if (cur.size() == a.length) { res.add(new ArrayList<>(cur)); return; }
        for (int i = 0; i < a.length; i++) {
            if (used[i] || (i > 0 && a[i] == a[i - 1] && !used[i - 1])) continue;
            used[i] = true;
            cur.add(a[i]);
            uniqDfs(a, used, cur, res);
            cur.remove(cur.size() - 1);
            used[i] = false;
        }
    }

    // Combinations (LC 77)
    static List<List<Integer>> combine(int n, int k) {
        List<List<Integer>> res = new ArrayList<>();
        combDfs(n, k, 1, new ArrayList<>(), res);
        return res;
    }

    private static void combDfs(int n, int k, int start, List<Integer> cur, List<List<Integer>> res) {
        if (cur.size() == k) { res.add(new ArrayList<>(cur)); return; }
        for (int i = start; i <= n - (k - cur.size()) + 1; i++) { // prune: enough numbers must remain
            cur.add(i);
            combDfs(n, k, i + 1, cur, res);
            cur.remove(cur.size() - 1);
        }
    }

    // Combination Sum (LC 39): reuse allowed -> recurse with the same index
    static List<List<Integer>> combinationSum(int[] c, int target) {
        Arrays.sort(c);
        List<List<Integer>> res = new ArrayList<>();
        csDfs(c, 0, target, new ArrayList<>(), res);
        return res;
    }

    private static void csDfs(int[] c, int start, int remain, List<Integer> cur, List<List<Integer>> res) {
        if (remain == 0) { res.add(new ArrayList<>(cur)); return; }
        for (int i = start; i < c.length && c[i] <= remain; i++) {
            cur.add(c[i]);
            csDfs(c, i, remain - c[i], cur, res);
            cur.remove(cur.size() - 1);
        }
    }

    // Combination Sum II (LC 40): each number once, duplicates present
    static List<List<Integer>> combinationSum2(int[] c, int target) {
        Arrays.sort(c);
        List<List<Integer>> res = new ArrayList<>();
        cs2Dfs(c, 0, target, new ArrayList<>(), res);
        return res;
    }

    private static void cs2Dfs(int[] c, int start, int remain, List<Integer> cur, List<List<Integer>> res) {
        if (remain == 0) { res.add(new ArrayList<>(cur)); return; }
        for (int i = start; i < c.length && c[i] <= remain; i++) {
            if (i > start && c[i] == c[i - 1]) continue;
            cur.add(c[i]);
            cs2Dfs(c, i + 1, remain - c[i], cur, res);
            cur.remove(cur.size() - 1);
        }
    }

    // Generate Parentheses (LC 22): track open / close counts, never let close exceed open
    static List<String> generateParenthesis(int n) {
        List<String> res = new ArrayList<>();
        genDfs(n, 0, 0, new StringBuilder(), res);
        return res;
    }

    private static void genDfs(int n, int open, int close, StringBuilder sb, List<String> res) {
        if (sb.length() == 2 * n) { res.add(sb.toString()); return; }
        if (open < n) { sb.append('('); genDfs(n, open + 1, close, sb, res); sb.setLength(sb.length() - 1); }
        if (close < open) { sb.append(')'); genDfs(n, open, close + 1, sb, res); sb.setLength(sb.length() - 1); }
    }

    // Letter Combinations of a Phone Number (LC 17)
    static List<String> letterCombinations(String digits) {
        List<String> res = new ArrayList<>();
        if (digits.isEmpty()) return res;
        String[] map = {"", "", "abc", "def", "ghi", "jkl", "mno", "pqrs", "tuv", "wxyz"};
        phoneDfs(digits, 0, map, new StringBuilder(), res);
        return res;
    }

    private static void phoneDfs(String d, int i, String[] map, StringBuilder sb, List<String> res) {
        if (i == d.length()) { res.add(sb.toString()); return; }
        for (char c : map[d.charAt(i) - '0'].toCharArray()) {
            sb.append(c);
            phoneDfs(d, i + 1, map, sb, res);
            sb.setLength(sb.length() - 1);
        }
    }

    // Palindrome Partitioning (LC 131)
    static List<List<String>> partition(String s) {
        List<List<String>> res = new ArrayList<>();
        palDfs(s, 0, new ArrayList<>(), res);
        return res;
    }

    private static void palDfs(String s, int start, List<String> cur, List<List<String>> res) {
        if (start == s.length()) { res.add(new ArrayList<>(cur)); return; }
        for (int end = start + 1; end <= s.length(); end++) {
            if (!isPal(s, start, end - 1)) continue;
            cur.add(s.substring(start, end));
            palDfs(s, end, cur, res);
            cur.remove(cur.size() - 1);
        }
    }

    private static boolean isPal(String s, int l, int r) {
        while (l < r) if (s.charAt(l++) != s.charAt(r--)) return false;
        return true;
    }

    // Word Search (LC 79): mark visited in place, restore on the way out
    static boolean exist(char[][] b, String w) {
        for (int i = 0; i < b.length; i++)
            for (int j = 0; j < b[0].length; j++) if (wsDfs(b, w, 0, i, j)) return true;
        return false;
    }

    private static boolean wsDfs(char[][] b, String w, int k, int i, int j) {
        if (k == w.length()) return true;
        if (i < 0 || j < 0 || i >= b.length || j >= b[0].length || b[i][j] != w.charAt(k)) return false;
        char c = b[i][j];
        b[i][j] = '#';
        boolean ok = wsDfs(b, w, k + 1, i + 1, j) || wsDfs(b, w, k + 1, i - 1, j) || wsDfs(b, w, k + 1, i, j + 1) || wsDfs(b, w, k + 1, i, j - 1);
        b[i][j] = c;
        return ok;
    }

    // N-Queens (LC 51): column / diagonal / anti-diagonal occupancy sets -> O(1) validity
    static List<List<String>> solveNQueens(int n) {
        List<List<String>> res = new ArrayList<>();
        queens(n, 0, new int[n], new boolean[n], new boolean[2 * n], new boolean[2 * n], res);
        return res;
    }

    private static void queens(int n, int r, int[] col, boolean[] c, boolean[] d1, boolean[] d2, List<List<String>> res) {
        if (r == n) {
            List<String> board = new ArrayList<>();
            for (int i = 0; i < n; i++) {
                char[] row = new char[n];
                Arrays.fill(row, '.');
                row[col[i]] = 'Q';
                board.add(new String(row));
            }
            res.add(board);
            return;
        }
        for (int j = 0; j < n; j++) {
            if (c[j] || d1[r + j] || d2[r - j + n]) continue;
            c[j] = d1[r + j] = d2[r - j + n] = true;
            col[r] = j;
            queens(n, r + 1, col, c, d1, d2, res);
            c[j] = d1[r + j] = d2[r - j + n] = false;
        }
    }

    // N-Queens count only, bitmask form (fast)
    static int totalNQueens(int n) { return bits(n, 0, 0, 0, 0); }

    private static int bits(int n, int row, int cols, int d1, int d2) {
        if (row == n) return 1;
        int count = 0, avail = ((1 << n) - 1) & ~(cols | d1 | d2);
        while (avail != 0) {
            int bit = avail & -avail;
            avail ^= bit;
            count += bits(n, row + 1, cols | bit, (d1 | bit) << 1, (d2 | bit) >> 1);
        }
        return count;
    }

    // Sudoku Solver (LC 37): fill the first empty cell, try 1..9 with row/col/box bitmasks
    static boolean solveSudoku(char[][] b) {
        int[] row = new int[9], col = new int[9], box = new int[9];
        for (int i = 0; i < 9; i++)
            for (int j = 0; j < 9; j++)
                if (b[i][j] != '.') {
                    int bit = 1 << (b[i][j] - '1');
                    row[i] |= bit; col[j] |= bit; box[i / 3 * 3 + j / 3] |= bit;
                }
        return sudoku(b, 0, row, col, box);
    }

    private static boolean sudoku(char[][] b, int pos, int[] row, int[] col, int[] box) {
        while (pos < 81 && b[pos / 9][pos % 9] != '.') pos++;
        if (pos == 81) return true;
        int i = pos / 9, j = pos % 9, k = i / 3 * 3 + j / 3;
        for (int d = 0; d < 9; d++) {
            int bit = 1 << d;
            if (((row[i] | col[j] | box[k]) & bit) != 0) continue;
            row[i] |= bit; col[j] |= bit; box[k] |= bit;
            b[i][j] = (char) ('1' + d);
            if (sudoku(b, pos + 1, row, col, box)) return true;
            b[i][j] = '.';
            row[i] ^= bit; col[j] ^= bit; box[k] ^= bit;
        }
        return false;
    }

    // Restore IP Addresses (LC 93)
    static List<String> restoreIpAddresses(String s) {
        List<String> res = new ArrayList<>();
        ipDfs(s, 0, new ArrayList<>(), res);
        return res;
    }

    private static void ipDfs(String s, int i, List<String> parts, List<String> res) {
        if (parts.size() == 4) { if (i == s.length()) res.add(String.join(".", parts)); return; }
        for (int len = 1; len <= 3 && i + len <= s.length(); len++) {
            String p = s.substring(i, i + len);
            if ((p.length() > 1 && p.charAt(0) == '0') || Integer.parseInt(p) > 255) continue;
            parts.add(p);
            ipDfs(s, i + len, parts, res);
            parts.remove(parts.size() - 1);
        }
    }

    // Expression Add Operators (LC 282): carry the last multiplied term so '*' can undo the previous add
    static List<String> addOperators(String num, int target) {
        List<String> res = new ArrayList<>();
        opsDfs(num, target, 0, 0, 0, "", res);
        return res;
    }

    private static void opsDfs(String s, int target, int i, long val, long last, String expr, List<String> res) {
        if (i == s.length()) { if (val == target) res.add(expr); return; }
        for (int j = i; j < s.length(); j++) {
            if (j > i && s.charAt(i) == '0') break;
            long cur = Long.parseLong(s.substring(i, j + 1));
            String t = s.substring(i, j + 1);
            if (i == 0) opsDfs(s, target, j + 1, cur, cur, t, res);
            else {
                opsDfs(s, target, j + 1, val + cur, cur, expr + "+" + t, res);
                opsDfs(s, target, j + 1, val - cur, -cur, expr + "-" + t, res);
                opsDfs(s, target, j + 1, val - last + last * cur, last * cur, expr + "*" + t, res);
            }
        }
    }

    // Gray Code (LC 89): i ^ (i >> 1)
    static List<Integer> grayCode(int n) {
        List<Integer> res = new ArrayList<>();
        for (int i = 0; i < 1 << n; i++) res.add(i ^ (i >> 1));
        return res;
    }
}
