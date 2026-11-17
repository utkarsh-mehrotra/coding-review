import java.util.*;

public class DPCombined {

    // ---------- DP + binary search: Maximum Profit in Job Scheduling (LC 1235) ----------
    // memo over job index (jobs sorted by start); binary-search the first job starting at or after this one ends. O(n log n)
    static int jobScheduling(int[] start, int[] end, int[] profit) {
        int n = start.length;
        Integer[] idx = new Integer[n];
        for (int i = 0; i < n; i++) idx[i] = i;
        Arrays.sort(idx, Comparator.comparingInt(i -> start[i]));
        int[] st = new int[n], en = new int[n], pr = new int[n];
        for (int i = 0; i < n; i++) { st[i] = start[idx[i]]; en[i] = end[idx[i]]; pr[i] = profit[idx[i]]; }
        return bestJobs(0, st, en, pr, new Integer[n + 1]);
    }

    // state: i = first job still allowed | choice: skip job i, or take it and jump to the next compatible job | base: i == n -> 0
    private static int bestJobs(int i, int[] st, int[] en, int[] pr, Integer[] memo) {
        int n = st.length;
        if (i >= n) return 0;
        if (memo[i] != null) return memo[i];
        int lo = i + 1, hi = n; // first job with start >= en[i]
        while (lo < hi) {
            int mid = (lo + hi) >>> 1;
            if (st[mid] >= en[i]) hi = mid; else lo = mid + 1;
        }
        return memo[i] = Math.max(bestJobs(i + 1, st, en, pr, memo), pr[i] + bestJobs(lo, st, en, pr, memo));
    }

    // ---------- Window-max DP: memo over index, scan the next k ----------
    // Constrained Subset Sum (LC 1425): pick a subsequence, adjacent picks at most k apart
    // Faster: monotonic deque for the window max, O(n) — not DP-memo; mention as a follow-up.
    static int constrainedSubsetSum(int[] nums, int k) {
        Integer[] memo = new Integer[nums.length];
        int best = Integer.MIN_VALUE;
        for (int i = 0; i < nums.length; i++) best = Math.max(best, sumFrom(i, nums, k, memo));
        return best;
    }

    // state: i = first picked index | choice: next pick j in i+1..i+k, or stop (take 0) | base: none, falls out of the loop. O(n*k)
    private static int sumFrom(int i, int[] nums, int k, Integer[] memo) {
        if (memo[i] != null) return memo[i];
        int next = 0;
        for (int j = i + 1; j <= Math.min(nums.length - 1, i + k); j++) next = Math.max(next, sumFrom(j, nums, k, memo));
        return memo[i] = nums[i] + next;
    }

    // Jump Game VI (LC 1696): max score path with jumps of 1..k
    // Faster: monotonic deque for the window max, O(n) — not DP-memo; mention as a follow-up.
    static int maxResult(int[] nums, int k) {
        return scoreFrom(0, nums, k, new Integer[nums.length]);
    }

    // state: i = current index | choice: jump j in i+1..i+k | base: i == n-1 -> nums[i]. O(n*k)
    private static int scoreFrom(int i, int[] nums, int k, Integer[] memo) {
        if (i == nums.length - 1) return nums[i];
        if (memo[i] != null) return memo[i];
        int best = Integer.MIN_VALUE;
        for (int j = i + 1; j <= Math.min(nums.length - 1, i + k); j++) best = Math.max(best, scoreFrom(j, nums, k, memo));
        return memo[i] = nums[i] + best;
    }

    // ---------- Probability DP ----------
    // Knight Probability in Chessboard (LC 688). O(k * n^2 * 8)
    static double knightProbability(int n, int k, int row, int col) {
        return stay(k, row, col, n, new Double[k + 1][n][n]);
    }

    // state: (moves left, r, c) | choice: 8 knight moves, each 1/8 | base: off board -> 0, no moves left -> 1
    private static double stay(int left, int r, int c, int n, Double[][][] memo) {
        if (r < 0 || r >= n || c < 0 || c >= n) return 0;
        if (left == 0) return 1;
        if (memo[left][r][c] != null) return memo[left][r][c];
        int[][] mv = {{1, 2}, {2, 1}, {-1, 2}, {-2, 1}, {1, -2}, {2, -1}, {-1, -2}, {-2, -1}};
        double sum = 0;
        for (int[] m : mv) sum += stay(left - 1, r + m[0], c + m[1], n, memo) / 8.0;
        return memo[left][r][c] = sum;
    }

    // New 21 Game (LC 837): memo over current points, each draw 1..maxPts equally likely. O(k * maxPts)
    // Faster: sliding-window sum over the next maxPts values, O(n + maxPts) — not DP-memo; mention as a follow-up.
    static double new21Game(int n, int k, int maxPts) {
        if (k == 0 || n >= k + maxPts) return 1.0;
        return ending(0, n, k, maxPts, new Double[k]);
    }

    // state: pts = current score | choice: draw x in 1..maxPts | base: pts >= k stops -> 1 if pts <= n else 0
    private static double ending(int pts, int n, int k, int maxPts, Double[] memo) {
        if (pts >= k) return pts <= n ? 1.0 : 0.0;
        if (memo[pts] != null) return memo[pts];
        double sum = 0;
        for (int x = 1; x <= maxPts; x++) sum += ending(pts + x, n, k, maxPts, memo);
        return memo[pts] = sum / maxPts;
    }

    // ---------- Hash-map-state DP ----------
    // Longest Arithmetic Subsequence (LC 1027): memo over (i, diff), one HashMap per index.
    // COST: O(n^2 log n) — each (i, diff) state finds its previous index by binary search.
    static int longestArithSeqLength(int[] nums) {
        int n = nums.length, best = 2;
        if (n <= 2) return n;
        Map<Integer, List<Integer>> positions = new HashMap<>();          // value -> indices, ascending
        for (int i = 0; i < n; i++) positions.computeIfAbsent(nums[i], v -> new ArrayList<>()).add(i);
        List<Map<Integer, Integer>> memo = new ArrayList<>();
        for (int i = 0; i < n; i++) memo.add(new HashMap<>());
        for (int i = 0; i < n; i++)
            for (int j = 0; j < i; j++) best = Math.max(best, arithEnding(i, nums[i] - nums[j], nums, positions, memo));
        return best;
    }

    // state: (i, diff) = longest arithmetic run ending at i with this diff | choice: latest j < i with nums[j] == nums[i] - diff | base: no such j -> 1
    private static int arithEnding(int i, int diff, int[] nums, Map<Integer, List<Integer>> positions, List<Map<Integer, Integer>> memo) {
        Integer c = memo.get(i).get(diff);
        if (c != null) return c;
        int res = 1;
        List<Integer> at = positions.get(nums[i] - diff);
        if (at != null) {
            int lo = 0, hi = at.size() - 1, j = -1;                         // largest index in `at` that is < i
            while (lo <= hi) { int mid = (lo + hi) >>> 1; if (at.get(mid) < i) { j = at.get(mid); lo = mid + 1; } else hi = mid - 1; }
            if (j >= 0) res = 1 + arithEnding(j, diff, nums, positions, memo);
        }
        memo.get(i).put(diff, res);
        return res;
    }

    // ---------- Memoized DFS on a DAG: Longest Increasing Path in a Matrix (LC 329) ----------
    static int longestIncreasingPath(int[][] g) {
        int m = g.length, n = g[0].length, best = 0;
        Integer[][] memo = new Integer[m][n];
        for (int i = 0; i < m; i++)
            for (int j = 0; j < n; j++) best = Math.max(best, lip(g, i, j, memo));
        return best;
    }

    // state: (i, j) = longest increasing path starting at this cell | choice: step to a larger neighbour | base: no larger neighbour -> 1
    private static int lip(int[][] g, int i, int j, Integer[][] memo) {
        if (memo[i][j] != null) return memo[i][j];
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
