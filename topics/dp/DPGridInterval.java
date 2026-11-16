import java.util.*;

public class DPGridInterval {

    // ---------- Unique Paths (LC 62) ----------
    // RECOGNIZE: grid, move right/down only, count paths or min/max path sum.
    static int uniquePaths(int m, int n) {
        int[] dp = new int[n];
        Arrays.fill(dp, 1);
        for (int i = 1; i < m; i++)
            for (int j = 1; j < n; j++) dp[j] += dp[j - 1];
        return dp[n - 1];
    }

    // Unique Paths II (LC 63): obstacles
    static int uniquePathsObstacles(int[][] g) {
        int n = g[0].length;
        int[] dp = new int[n];
        dp[0] = g[0][0] == 1 ? 0 : 1;
        for (int[] row : g)
            for (int j = 0; j < n; j++) {
                if (row[j] == 1) dp[j] = 0;
                else if (j > 0) dp[j] += dp[j - 1];
            }
        return dp[n - 1];
    }

    // Minimum Path Sum (LC 64)
    static int minPathSum(int[][] g) {
        int m = g.length, n = g[0].length;
        int[] dp = new int[n];
        for (int i = 0; i < m; i++)
            for (int j = 0; j < n; j++) {
                if (i == 0 && j == 0) dp[j] = g[0][0];
                else if (i == 0) dp[j] = dp[j - 1] + g[i][j];
                else if (j == 0) dp[j] = dp[j] + g[i][j];
                else dp[j] = Math.min(dp[j], dp[j - 1]) + g[i][j];
            }
        return dp[n - 1];
    }

    // Triangle (LC 120): bottom-up, one row
    static int minimumTotal(List<List<Integer>> t) {
        int n = t.size();
        int[] dp = new int[n + 1];
        for (int i = n - 1; i >= 0; i--)
            for (int j = 0; j <= i; j++) dp[j] = Math.min(dp[j], dp[j + 1]) + t.get(i).get(j);
        return dp[0];
    }

    // Maximal Square (LC 221): dp = side of largest square with bottom-right corner here
    static int maximalSquare(char[][] g) {
        int m = g.length, n = g[0].length, best = 0;
        int[][] dp = new int[m + 1][n + 1];
        for (int i = 1; i <= m; i++)
            for (int j = 1; j <= n; j++)
                if (g[i - 1][j - 1] == '1') {
                    dp[i][j] = 1 + Math.min(dp[i - 1][j - 1], Math.min(dp[i - 1][j], dp[i][j - 1]));
                    best = Math.max(best, dp[i][j]);
                }
        return best * best;
    }

    // Dungeon Game (LC 174): run BACKWARD, min health needed to stand at each cell
    static int calculateMinimumHP(int[][] d) {
        int m = d.length, n = d[0].length;
        int[][] dp = new int[m + 1][n + 1];
        for (int[] r : dp) Arrays.fill(r, Integer.MAX_VALUE);
        dp[m][n - 1] = dp[m - 1][n] = 1;
        for (int i = m - 1; i >= 0; i--)
            for (int j = n - 1; j >= 0; j--)
                dp[i][j] = Math.max(1, Math.min(dp[i + 1][j], dp[i][j + 1]) - d[i][j]);
        return dp[0][0];
    }

    // Cherry Pickup II (LC 1463): two walkers move row by row together; state = (col1, col2)
    static int cherryPickup2(int[][] g) {
        int m = g.length, n = g[0].length;
        int[][] dp = new int[n][n];
        for (int[] r : dp) Arrays.fill(r, Integer.MIN_VALUE / 2);
        dp[0][n - 1] = g[0][0] + g[0][n - 1];
        for (int i = 1; i < m; i++) {
            int[][] nx = new int[n][n];
            for (int[] r : nx) Arrays.fill(r, Integer.MIN_VALUE / 2);
            for (int a = 0; a < n; a++)
                for (int b = 0; b < n; b++) {
                    int best = Integer.MIN_VALUE / 2;
                    for (int da = -1; da <= 1; da++)
                        for (int db = -1; db <= 1; db++) {
                            int pa = a + da, pb = b + db;
                            if (pa >= 0 && pa < n && pb >= 0 && pb < n) best = Math.max(best, dp[pa][pb]);
                        }
                    nx[a][b] = best + g[i][a] + (a == b ? 0 : g[i][b]);
                }
            dp = nx;
        }
        int ans = 0;
        for (int[] r : dp) for (int v : r) ans = Math.max(ans, v);
        return ans;
    }

    // ===================== INTERVAL DP =====================
    // RECOGNIZE: answer for a range [i, j] built from splitting at k. Loop by LENGTH (or i descending).

    // Burst Balloons (LC 312): k is the LAST balloon burst in (i, j)
    static int maxCoins(int[] nums) {
        int n = nums.length;
        int[] a = new int[n + 2];
        a[0] = a[n + 1] = 1;
        for (int i = 0; i < n; i++) a[i + 1] = nums[i];
        int[][] dp = new int[n + 2][n + 2];
        for (int len = 2; len <= n + 1; len++)
            for (int i = 0; i + len <= n + 1; i++) {
                int j = i + len;
                for (int k = i + 1; k < j; k++)
                    dp[i][j] = Math.max(dp[i][j], dp[i][k] + a[i] * a[k] * a[j] + dp[k][j]);
            }
        return dp[0][n + 1];
    }

    // Matrix Chain Multiplication: dims[i-1] x dims[i] is matrix i
    static int matrixChainOrder(int[] dims) {
        int n = dims.length - 1;
        int[][] dp = new int[n][n];
        for (int len = 2; len <= n; len++)
            for (int i = 0; i + len - 1 < n; i++) {
                int j = i + len - 1;
                dp[i][j] = Integer.MAX_VALUE;
                for (int k = i; k < j; k++)
                    dp[i][j] = Math.min(dp[i][j], dp[i][k] + dp[k + 1][j] + dims[i] * dims[k + 1] * dims[j + 1]);
            }
        return n <= 1 ? 0 : dp[0][n - 1];
    }

    // Minimum Cost to Cut a Stick (LC 1547)
    static int minCostCutStick(int len, int[] cuts) {
        int m = cuts.length;
        int[] c = new int[m + 2];
        int[] sorted = cuts.clone();
        Arrays.sort(sorted);
        System.arraycopy(sorted, 0, c, 1, m);
        c[m + 1] = len;
        int[][] dp = new int[m + 2][m + 2];
        for (int d = 2; d <= m + 1; d++)
            for (int i = 0; i + d <= m + 1; i++) {
                int j = i + d;
                dp[i][j] = Integer.MAX_VALUE;
                for (int k = i + 1; k < j; k++) dp[i][j] = Math.min(dp[i][j], dp[i][k] + dp[k][j] + c[j] - c[i]);
            }
        return dp[0][m + 1];
    }

    // Palindrome Partitioning II (LC 132): min cuts
    static int minCutPalindrome(String s) {
        int n = s.length();
        boolean[][] pal = new boolean[n][n];
        for (int i = n - 1; i >= 0; i--)
            for (int j = i; j < n; j++) pal[i][j] = s.charAt(i) == s.charAt(j) && (j - i < 2 || pal[i + 1][j - 1]);
        int[] cut = new int[n];
        for (int j = 0; j < n; j++) {
            if (pal[0][j]) { cut[j] = 0; continue; }
            cut[j] = j;
            for (int i = 1; i <= j; i++) if (pal[i][j]) cut[j] = Math.min(cut[j], cut[i - 1] + 1);
        }
        return n == 0 ? 0 : cut[n - 1];
    }

    // Strange Printer (LC 664)
    static int strangePrinter(String s) {
        int n = s.length();
        if (n == 0) return 0;
        int[][] dp = new int[n][n];
        for (int i = n - 1; i >= 0; i--) {
            dp[i][i] = 1;
            for (int j = i + 1; j < n; j++) {
                dp[i][j] = dp[i][j - 1] + 1;
                for (int k = i; k < j; k++)
                    if (s.charAt(k) == s.charAt(j)) dp[i][j] = Math.min(dp[i][j], dp[i][k] + (k + 1 <= j - 1 ? dp[k + 1][j - 1] : 0));
            }
        }
        return dp[0][n - 1];
    }

    // Stone Game (LC 877 family): dp[i][j] = best score DIFFERENCE the mover can force on piles i..j
    static int stoneGameDiff(int[] p) {
        int n = p.length;
        int[] dp = new int[n];
        for (int i = n - 1; i >= 0; i--) {
            dp[i] = p[i];
            for (int j = i + 1; j < n; j++) dp[j] = Math.max(p[i] - dp[j], p[j] - dp[j - 1]);
        }
        return dp[n - 1];
    }
}
