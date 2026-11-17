import java.util.*;

public class DPGridInterval {

    // ---------- Unique Paths (LC 62) ----------
    // RECOGNIZE: grid, move right/down only, count paths or min/max path sum.
    // State (i, j) = paths from this cell to the bottom-right corner.
    static int uniquePaths(int m, int n) {
        return pathsFrom(0, 0, m, n, new Integer[m][n]);
    }

    // state: (i, j) = number of paths from (i, j) to the corner | choice: step right or down | base: last row or column -> 1
    private static int pathsFrom(int i, int j, int m, int n, Integer[][] memo) {
        if (i == m - 1 || j == n - 1) return 1;
        if (memo[i][j] != null) return memo[i][j];
        return memo[i][j] = pathsFrom(i + 1, j, m, n, memo) + pathsFrom(i, j + 1, m, n, memo);
    }

    // Unique Paths II (LC 63): obstacles
    static int uniquePathsObstacles(int[][] g) {
        return pathsAvoiding(0, 0, g, new Integer[g.length][g[0].length]);
    }

    // state: (i, j) = paths from (i, j) to the corner avoiding obstacles | choice: step right or down | base: obstacle or off-grid -> 0, corner -> 1
    private static int pathsAvoiding(int i, int j, int[][] g, Integer[][] memo) {
        if (i >= g.length || j >= g[0].length || g[i][j] == 1) return 0;
        if (i == g.length - 1 && j == g[0].length - 1) return 1;
        if (memo[i][j] != null) return memo[i][j];
        return memo[i][j] = pathsAvoiding(i + 1, j, g, memo) + pathsAvoiding(i, j + 1, g, memo);
    }

    // Minimum Path Sum (LC 64)
    static int minPathSum(int[][] g) {
        return costFrom(0, 0, g, new Integer[g.length][g[0].length]);
    }

    // state: (i, j) = cheapest path sum from (i, j) to the corner | choice: step right or down | base: corner -> its own cost
    private static int costFrom(int i, int j, int[][] g, Integer[][] memo) {
        int m = g.length, n = g[0].length;
        if (i == m - 1 && j == n - 1) return g[i][j];
        if (memo[i][j] != null) return memo[i][j];
        int down = i + 1 < m ? costFrom(i + 1, j, g, memo) : Integer.MAX_VALUE;
        int right = j + 1 < n ? costFrom(i, j + 1, g, memo) : Integer.MAX_VALUE;
        return memo[i][j] = g[i][j] + Math.min(down, right);
    }

    // Triangle (LC 120): top-down memo over (row, index)
    static int minimumTotal(List<List<Integer>> t) {
        int n = t.size();
        return n == 0 ? 0 : triFrom(0, 0, t, new Integer[n][n]);
    }

    // state: (i, j) = min path sum from t[i][j] to the last row | choice: go to index j or j + 1 in the next row | base: last row -> its value
    private static int triFrom(int i, int j, List<List<Integer>> t, Integer[][] memo) {
        if (i == t.size() - 1) return t.get(i).get(j);
        if (memo[i][j] != null) return memo[i][j];
        return memo[i][j] = t.get(i).get(j) + Math.min(triFrom(i + 1, j, t, memo), triFrom(i + 1, j + 1, t, memo));
    }

    // Maximal Square (LC 221): side(i, j) = side of largest square with bottom-right corner here
    static int maximalSquare(char[][] g) {
        int m = g.length, n = g[0].length, best = 0;
        Integer[][] memo = new Integer[m][n];
        for (int i = 0; i < m; i++)
            for (int j = 0; j < n; j++) best = Math.max(best, side(i, j, g, memo));
        return best * best;
    }

    // state: (i, j) = side of the largest all-'1' square whose bottom-right corner is (i, j) | choice: 1 + min of up, left, up-left | base: off-grid or '0' -> 0
    private static int side(int i, int j, char[][] g, Integer[][] memo) {
        if (i < 0 || j < 0 || g[i][j] != '1') return 0;
        if (memo[i][j] != null) return memo[i][j];
        return memo[i][j] = 1 + Math.min(side(i - 1, j - 1, g, memo), Math.min(side(i - 1, j, g, memo), side(i, j - 1, g, memo)));
    }

    // Dungeon Game (LC 174): min health needed to stand at each cell, asked from the cell onward
    static int calculateMinimumHP(int[][] d) {
        return needAt(0, 0, d, new Integer[d.length][d[0].length]);
    }

    // state: (i, j) = min health on entering (i, j) to survive to the end | choice: continue to the easier of right/down | base: corner -> max(1, 1 - d)
    private static int needAt(int i, int j, int[][] d, Integer[][] memo) {
        int m = d.length, n = d[0].length;
        if (memo[i][j] != null) return memo[i][j];
        int next;
        if (i == m - 1 && j == n - 1) next = 1;
        else {
            int down = i + 1 < m ? needAt(i + 1, j, d, memo) : Integer.MAX_VALUE;
            int right = j + 1 < n ? needAt(i, j + 1, d, memo) : Integer.MAX_VALUE;
            next = Math.min(down, right);
        }
        return memo[i][j] = Math.max(1, next - d[i][j]);
    }

    // Cherry Pickup II (LC 1463): two walkers move row by row together; state = (row, col1, col2)
    static int cherryPickup2(int[][] g) {
        int m = g.length, n = g[0].length;
        return Math.max(0, pickFrom(0, 0, n - 1, g, new Integer[m][n][n]));
    }

    // state: (r, a, b) = max cherries from row r down with walkers at columns a and b | choice: each walker moves col -1/0/+1 | base: last row -> this row's cherries
    private static int pickFrom(int r, int a, int b, int[][] g, Integer[][][] memo) {
        if (memo[r][a][b] != null) return memo[r][a][b];
        int n = g[0].length, here = g[r][a] + (a == b ? 0 : g[r][b]), rest = 0;
        if (r + 1 < g.length) {
            rest = Integer.MIN_VALUE / 2;
            for (int da = -1; da <= 1; da++)
                for (int db = -1; db <= 1; db++) {
                    int na = a + da, nb = b + db;
                    if (na >= 0 && na < n && nb >= 0 && nb < n) rest = Math.max(rest, pickFrom(r + 1, na, nb, g, memo));
                }
        }
        return memo[r][a][b] = here + rest;
    }

    // ===================== INTERVAL DP =====================
    // RECOGNIZE: answer for a range [i, j] built from splitting at k. Recurse on the sub-interval, memo on (i, j).

    // Burst Balloons (LC 312): k is the LAST balloon burst in (i, j)
    static int maxCoins(int[] nums) {
        int n = nums.length;
        int[] a = new int[n + 2];
        a[0] = a[n + 1] = 1;
        for (int i = 0; i < n; i++) a[i + 1] = nums[i];
        return coinsIn(0, n + 1, a, new Integer[n + 2][n + 2]);
    }

    // state: (i, j) = max coins bursting everything strictly between i and j | choice: k = last balloon burst | base: nothing between -> 0
    private static int coinsIn(int i, int j, int[] a, Integer[][] memo) {
        if (j - i < 2) return 0;
        if (memo[i][j] != null) return memo[i][j];
        int best = 0;
        for (int k = i + 1; k < j; k++)
            best = Math.max(best, coinsIn(i, k, a, memo) + a[i] * a[k] * a[j] + coinsIn(k, j, a, memo));
        return memo[i][j] = best;
    }

    // Matrix Chain Multiplication: dims[i-1] x dims[i] is matrix i
    static int matrixChainOrder(int[] dims) {
        int n = dims.length - 1;
        return n <= 1 ? 0 : chainCost(0, n - 1, dims, new Integer[n][n]);
    }

    // state: (i, j) = min scalar multiplications for matrices i..j | choice: split point k, (i..k)(k+1..j) | base: single matrix -> 0
    private static int chainCost(int i, int j, int[] dims, Integer[][] memo) {
        if (i == j) return 0;
        if (memo[i][j] != null) return memo[i][j];
        int best = Integer.MAX_VALUE;
        for (int k = i; k < j; k++)
            best = Math.min(best, chainCost(i, k, dims, memo) + chainCost(k + 1, j, dims, memo) + dims[i] * dims[k + 1] * dims[j + 1]);
        return memo[i][j] = best;
    }

    // Minimum Cost to Cut a Stick (LC 1547)
    static int minCostCutStick(int len, int[] cuts) {
        int m = cuts.length;
        int[] c = new int[m + 2];
        int[] sorted = cuts.clone();
        Arrays.sort(sorted);
        System.arraycopy(sorted, 0, c, 1, m);
        c[m + 1] = len;
        return cutCost(0, m + 1, c, new Integer[m + 2][m + 2]);
    }

    // state: (i, j) = min cost to cut the piece between c[i] and c[j] | choice: k = first cut made (cost = piece length) | base: no cut inside -> 0
    private static int cutCost(int i, int j, int[] c, Integer[][] memo) {
        if (j - i < 2) return 0;
        if (memo[i][j] != null) return memo[i][j];
        int best = Integer.MAX_VALUE;
        for (int k = i + 1; k < j; k++) best = Math.min(best, cutCost(i, k, c, memo) + cutCost(k, j, c, memo) + c[j] - c[i]);
        return memo[i][j] = best;
    }

    // Palindrome Partitioning II (LC 132): min cuts
    static int minCutPalindrome(String s) {
        int n = s.length();
        return n == 0 ? 0 : cutsFrom(0, s, new Boolean[n][n], new Integer[n]);
    }

    // state: (i, j) = is s[i..j] a palindrome | choice: ends equal and inner is a palindrome | base: i >= j -> true
    private static boolean isPalin(int i, int j, String s, Boolean[][] pal) {
        if (i >= j) return true;
        if (pal[i][j] != null) return pal[i][j];
        return pal[i][j] = s.charAt(i) == s.charAt(j) && isPalin(i + 1, j - 1, s, pal);
    }

    // state: i = min cuts so s[i..] splits into palindromes | choice: first piece s[i..j] palindrome, then 1 cut + cutsFrom(j + 1) | base: s[i..] itself palindrome -> 0
    private static int cutsFrom(int i, String s, Boolean[][] pal, Integer[] memo) {
        int n = s.length();
        if (isPalin(i, n - 1, s, pal)) return 0;
        if (memo[i] != null) return memo[i];
        int best = n;
        for (int j = i; j < n - 1; j++)
            if (isPalin(i, j, s, pal)) best = Math.min(best, 1 + cutsFrom(j + 1, s, pal, memo));
        return memo[i] = best;
    }

    // Strange Printer (LC 664)
    static int strangePrinter(String s) {
        int n = s.length();
        if (n == 0) return 0;
        return turns(0, n - 1, s, new Integer[n][n]);
    }

    // state: (i, j) = min print turns for s[i..j] | choice: print s[j] alone, or share a turn with an earlier equal s[k] | base: i == j -> 1
    private static int turns(int i, int j, String s, Integer[][] memo) {
        if (i == j) return 1;
        if (memo[i][j] != null) return memo[i][j];
        int best = turns(i, j - 1, s, memo) + 1;
        for (int k = i; k < j; k++)
            if (s.charAt(k) == s.charAt(j))
                best = Math.min(best, turns(i, k, s, memo) + (k + 1 <= j - 1 ? turns(k + 1, j - 1, s, memo) : 0));
        return memo[i][j] = best;
    }

    // Stone Game (LC 877 family): diff(i, j) = best score DIFFERENCE the mover can force on piles i..j
    static int stoneGameDiff(int[] p) {
        int n = p.length;
        return n == 0 ? 0 : diff(0, n - 1, p, new Integer[n][n]);
    }

    // state: (i, j) = best (mover - opponent) score on piles i..j | choice: take left or right pile, opponent then moves | base: i == j -> p[i]
    private static int diff(int i, int j, int[] p, Integer[][] memo) {
        if (i == j) return p[i];
        if (memo[i][j] != null) return memo[i][j];
        return memo[i][j] = Math.max(p[i] - diff(i + 1, j, p, memo), p[j] - diff(i, j - 1, p, memo));
    }
}
