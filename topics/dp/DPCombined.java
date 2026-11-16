import java.util.*;

public class DPCombined {

    // ---------- DP + binary search: Maximum Profit in Job Scheduling (LC 1235) ----------
    // dp[i] = best profit using the first i jobs (sorted by end). Binary-search the last compatible job.
    static int jobScheduling(int[] start, int[] end, int[] profit) {
        int n = start.length;
        Integer[] idx = new Integer[n];
        for (int i = 0; i < n; i++) idx[i] = i;
        Arrays.sort(idx, Comparator.comparingInt(i -> end[i]));
        int[] ends = new int[n], dp = new int[n + 1];
        for (int i = 0; i < n; i++) ends[i] = end[idx[i]];
        for (int i = 1; i <= n; i++) {
            int job = idx[i - 1];
            int lo = 0, hi = i - 1; // count of jobs among first i-1 with end <= start[job]
            while (lo < hi) {
                int mid = (lo + hi) >>> 1;
                if (ends[mid] <= start[job]) lo = mid + 1; else hi = mid;
            }
            dp[i] = Math.max(dp[i - 1], dp[lo] + profit[job]);
        }
        return dp[n];
    }

    // ---------- DP + monotonic deque: sliding-window max over dp ----------
    // Constrained Subset Sum (LC 1425): pick a subsequence, adjacent picks at most k apart
    static int constrainedSubsetSum(int[] nums, int k) {
        Deque<Integer> dq = new ArrayDeque<>(); // indices, dp values decreasing
        int[] dp = new int[nums.length];
        int best = Integer.MIN_VALUE;
        for (int i = 0; i < nums.length; i++) {
            while (!dq.isEmpty() && dq.peekFirst() < i - k) dq.pollFirst();
            dp[i] = nums[i] + (dq.isEmpty() ? 0 : Math.max(0, dp[dq.peekFirst()]));
            while (!dq.isEmpty() && dp[dq.peekLast()] <= dp[i]) dq.pollLast();
            dq.addLast(i);
            best = Math.max(best, dp[i]);
        }
        return best;
    }

    // Jump Game VI (LC 1696): max score path with jumps of 1..k
    static int maxResult(int[] nums, int k) {
        int n = nums.length;
        int[] dp = new int[n];
        dp[0] = nums[0];
        Deque<Integer> dq = new ArrayDeque<>();
        dq.addLast(0);
        for (int i = 1; i < n; i++) {
            while (dq.peekFirst() < i - k) dq.pollFirst();
            dp[i] = nums[i] + dp[dq.peekFirst()];
            while (!dq.isEmpty() && dp[dq.peekLast()] <= dp[i]) dq.pollLast();
            dq.addLast(i);
        }
        return dp[n - 1];
    }

    // ---------- Probability DP ----------
    // Knight Probability in Chessboard (LC 688)
    static double knightProbability(int n, int k, int row, int col) {
        int[][] mv = {{1, 2}, {2, 1}, {-1, 2}, {-2, 1}, {1, -2}, {2, -1}, {-1, -2}, {-2, -1}};
        double[][] cur = new double[n][n];
        cur[row][col] = 1;
        for (int s = 0; s < k; s++) {
            double[][] nx = new double[n][n];
            for (int r = 0; r < n; r++)
                for (int c = 0; c < n; c++) {
                    if (cur[r][c] == 0) continue;
                    for (int[] m : mv) {
                        int nr = r + m[0], nc = c + m[1];
                        if (nr >= 0 && nr < n && nc >= 0 && nc < n) nx[nr][nc] += cur[r][c] / 8.0;
                    }
                }
            cur = nx;
        }
        double sum = 0;
        for (double[] r : cur) for (double v : r) sum += v;
        return sum;
    }

    // New 21 Game (LC 837): dp[i] = P(reach score i), sliding window sum
    static double new21Game(int n, int k, int maxPts) {
        if (k == 0 || n >= k + maxPts) return 1.0;
        double[] dp = new double[n + 1];
        dp[0] = 1.0;
        double window = 1.0, ans = 0;
        for (int i = 1; i <= n; i++) {
            dp[i] = window / maxPts;
            if (i < k) window += dp[i]; else ans += dp[i];
            if (i - maxPts >= 0 && i - maxPts < k) window -= dp[i - maxPts];
        }
        return ans;
    }

    // ---------- Hash-map-state DP ----------
    // Longest Arithmetic Subsequence (LC 1027): dp[i][diff]
    static int longestArithSeqLength(int[] nums) {
        int n = nums.length, best = 2;
        if (n <= 2) return n;
        List<Map<Integer, Integer>> dp = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            Map<Integer, Integer> m = new HashMap<>();
            for (int j = 0; j < i; j++) {
                int diff = nums[i] - nums[j];
                int len = dp.get(j).getOrDefault(diff, 1) + 1;
                m.merge(diff, len, Math::max);
                best = Math.max(best, len);
            }
            dp.add(m);
        }
        return best;
    }

    // ---------- Memoized DFS on a DAG: Longest Increasing Path in a Matrix (LC 329) ----------
    static int longestIncreasingPath(int[][] g) {
        int m = g.length, n = g[0].length, best = 0;
        int[][] memo = new int[m][n];
        for (int i = 0; i < m; i++)
            for (int j = 0; j < n; j++) best = Math.max(best, lip(g, i, j, memo));
        return best;
    }

    private static int lip(int[][] g, int i, int j, int[][] memo) {
        if (memo[i][j] != 0) return memo[i][j];
        int best = 1;
        int[] d = {0, 1, 0, -1, 0};
        for (int k = 0; k < 4; k++) {
            int r = i + d[k], c = j + d[k + 1];
            if (r >= 0 && r < g.length && c >= 0 && c < g[0].length && g[r][c] > g[i][j])
                best = Math.max(best, 1 + lip(g, r, c, memo));
        }
        return memo[i][j] = best;
    }
}
