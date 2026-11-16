import java.util.*;

public class DPKnapsack {

    // KEY RULE: loop direction over capacity decides reuse.
    //   downward = each item at most once (0/1)      upward = items reusable (unbounded)
    // KEY RULE 2 (counting): coins OUTER = combinations (order ignored); amount OUTER = sequences (order matters).

    // ---------- 0/1 knapsack ----------
    // RECOGNIZE: pick a subset under a capacity, maximize value / feasibility / count.
    static int knapsack01(int[] wt, int[] val, int cap) {
        int[] dp = new int[cap + 1];
        for (int i = 0; i < wt.length; i++)
            for (int c = cap; c >= wt[i]; c--) dp[c] = Math.max(dp[c], dp[c - wt[i]] + val[i]);
        return dp[cap];
    }

    // ---------- Unbounded knapsack ----------
    static int knapsackUnbounded(int[] wt, int[] val, int cap) {
        int[] dp = new int[cap + 1];
        for (int i = 0; i < wt.length; i++)
            for (int c = wt[i]; c <= cap; c++) dp[c] = Math.max(dp[c], dp[c - wt[i]] + val[i]);
        return dp[cap];
    }

    // ---------- Bounded knapsack: split count k into chunks 1,2,4,... then run 0/1 ----------
    static int knapsackBounded(int[] wt, int[] val, int[] cnt, int cap) {
        List<int[]> items = new ArrayList<>();
        for (int i = 0; i < wt.length; i++) {
            int k = cnt[i];
            for (int chunk = 1; k > 0; chunk <<= 1) {
                int take = Math.min(chunk, k);
                items.add(new int[]{wt[i] * take, val[i] * take});
                k -= take;
            }
        }
        int[] dp = new int[cap + 1];
        for (int[] it : items)
            for (int c = cap; c >= it[0]; c--) dp[c] = Math.max(dp[c], dp[c - it[0]] + it[1]);
        return dp[cap];
    }

    // ---------- Subset sum: can some subset reach target? ----------
    static boolean subsetSum(int[] nums, int target) {
        if (target < 0) return false;
        boolean[] dp = new boolean[target + 1];
        dp[0] = true;
        for (int x : nums)
            for (int s = target; s >= x; s--) dp[s] |= dp[s - x];
        return dp[target];
    }

    // Partition Equal Subset Sum (LC 416)
    static boolean canPartition(int[] nums) {
        int sum = 0;
        for (int x : nums) sum += x;
        return sum % 2 == 0 && subsetSum(nums, sum / 2);
    }

    // Last Stone Weight II (LC 1049): split into two piles with the smallest difference
    static int lastStoneWeightII(int[] stones) {
        int total = 0;
        for (int x : stones) total += x;
        boolean[] dp = new boolean[total / 2 + 1];
        dp[0] = true;
        for (int x : stones)
            for (int s = total / 2; s >= x; s--) dp[s] |= dp[s - x];
        for (int s = total / 2; s >= 0; s--) if (dp[s]) return total - 2 * s;
        return 0;
    }

    // ---------- Target Sum (LC 494): +/- signs = count subsets summing to (total + target) / 2 ----------
    static int findTargetSumWays(int[] nums, int target) {
        int total = 0;
        for (int x : nums) total += x;
        if (Math.abs(target) > total || (total + target) % 2 != 0) return 0;
        int s = (total + target) / 2;
        int[] dp = new int[s + 1];
        dp[0] = 1;
        for (int x : nums)
            for (int c = s; c >= x; c--) dp[c] += dp[c - x];
        return dp[s];
    }

    // ---------- Coin Change (LC 322): fewest coins, unbounded ----------
    static int coinChangeMin(int[] coins, int amount) {
        int INF = amount + 1;
        int[] dp = new int[amount + 1];
        Arrays.fill(dp, INF);
        dp[0] = 0;
        for (int a = 1; a <= amount; a++)
            for (int c : coins) if (c <= a) dp[a] = Math.min(dp[a], dp[a - c] + 1);
        return dp[amount] >= INF ? -1 : dp[amount];
    }

    // Coin Change II (LC 518): number of COMBINATIONS, coins outer
    static int coinChangeWays(int amount, int[] coins) {
        int[] dp = new int[amount + 1];
        dp[0] = 1;
        for (int c : coins)
            for (int a = c; a <= amount; a++) dp[a] += dp[a - c];
        return dp[amount];
    }

    // Combination Sum IV (LC 377): number of ordered SEQUENCES, amount outer
    static int combinationSum4(int[] nums, int target) {
        long[] dp = new long[target + 1];
        dp[0] = 1;
        for (int t = 1; t <= target; t++)
            for (int x : nums) if (x <= t) dp[t] += dp[t - x];
        return (int) dp[target];
    }

    // Perfect Squares (LC 279): unbounded, min count
    static int numSquares(int n) {
        int[] dp = new int[n + 1];
        Arrays.fill(dp, n);
        dp[0] = 0;
        for (int i = 1; i <= n; i++)
            for (int s = 1; s * s <= i; s++) dp[i] = Math.min(dp[i], dp[i - s * s] + 1);
        return dp[n];
    }

    // ---------- Ones and Zeroes (LC 474): two capacities = 2D 0/1 knapsack ----------
    static int findMaxForm(String[] strs, int m, int n) {
        int[][] dp = new int[m + 1][n + 1];
        for (String s : strs) {
            int zeros = 0, ones = 0;
            for (char ch : s.toCharArray()) if (ch == '0') zeros++; else ones++;
            for (int i = m; i >= zeros; i--)
                for (int j = n; j >= ones; j--) dp[i][j] = Math.max(dp[i][j], dp[i - zeros][j - ones] + 1);
        }
        return dp[m][n];
    }
}
