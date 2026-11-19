import java.util.*;

public class DPKnapsack {

    // KEY RULE: the recursive call decides reuse.
    //   move to i+1 after taking = each item at most once (0/1)      stay on i after taking = reusable (unbounded)
    // KEY RULE 2 (counting): coins OUTER (index state) = combinations (order ignored); amount-only state = sequences (order matters).

    // ---------- 0/1 knapsack ----------
    // RECOGNIZE: pick a subset under a capacity, maximize value / feasibility / count.
    // COST: O(n*cap) time and memo.
    static int knapsack01(int[] wt, int[] val, int cap) {
        return best01(0, cap, wt, val, new Integer[wt.length][cap + 1]);
    }

    // state: (item i, remaining capacity c) | choice: skip item i or take it once (move to i+1) | base: i == n -> 0
    private static int best01(int i, int c, int[] wt, int[] val, Integer[][] memo) {
        if (i == wt.length) return 0;
        if (memo[i][c] != null) return memo[i][c];
        int best = best01(i + 1, c, wt, val, memo);
        if (wt[i] <= c) best = Math.max(best, val[i] + best01(i + 1, c - wt[i], wt, val, memo));
        return memo[i][c] = best;
    }

    // ---------- Unbounded knapsack ----------
    // COST: O(n*cap) time and memo.
    static int knapsackUnbounded(int[] wt, int[] val, int cap) {
        return bestUnb(0, cap, wt, val, new Integer[wt.length][cap + 1]);
    }

    // state: (item i, remaining capacity c) | choice: skip item i for good, or take it and STAY on i | base: i == n -> 0
    private static int bestUnb(int i, int c, int[] wt, int[] val, Integer[][] memo) {
        if (i == wt.length) return 0;
        if (memo[i][c] != null) return memo[i][c];
        int best = bestUnb(i + 1, c, wt, val, memo);
        if (wt[i] <= c) best = Math.max(best, val[i] + bestUnb(i, c - wt[i], wt, val, memo));
        return memo[i][c] = best;
    }

    // ---------- Bounded knapsack: at item i choose how many copies (0..cnt[i]) ----------
    // COST: O(cap * sum(cnt)) time, O(n*cap) memo.
    static int knapsackBounded(int[] wt, int[] val, int[] cnt, int cap) {
        return bestBnd(0, cap, wt, val, cnt, new Integer[wt.length][cap + 1]);
    }

    // state: (item i, remaining capacity c) | choice: take k copies, 0 <= k <= cnt[i], then move to i+1 | base: i == n -> 0
    private static int bestBnd(int i, int c, int[] wt, int[] val, int[] cnt, Integer[][] memo) {
        if (i == wt.length) return 0;
        if (memo[i][c] != null) return memo[i][c];
        int best = 0;
        for (int k = 0; k <= cnt[i] && (long) k * wt[i] <= c; k++)
            best = Math.max(best, k * val[i] + bestBnd(i + 1, c - k * wt[i], wt, val, cnt, memo));
        return memo[i][c] = best;
    }

    // ---------- Subset sum: can some subset reach target? ----------
    // COST: O(n*target) time and memo.
    static boolean subsetSum(int[] nums, int target) {
        if (target < 0) return false;
        return canMake(0, target, nums, new Boolean[nums.length][target + 1]);
    }

    // state: (item i, remaining sum rem) | choice: skip or take nums[i] | base: rem == 0 -> true, i == n -> false
    private static boolean canMake(int i, int rem, int[] nums, Boolean[][] memo) {
        if (rem == 0) return true;
        if (i == nums.length) return false;
        if (memo[i][rem] != null) return memo[i][rem];
        boolean ok = canMake(i + 1, rem, nums, memo)
                || (nums[i] <= rem && canMake(i + 1, rem - nums[i], nums, memo));
        return memo[i][rem] = ok;
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
        int half = total / 2;
        return splitFrom(0, half, half, total, stones, new Integer[stones.length][half + 1]);
    }

    // state: (stone i, remaining room rem in the smaller pile) | choice: put stone in the small pile or the big one | base: i == n -> total - 2*used
    private static int splitFrom(int i, int rem, int half, int total, int[] stones, Integer[][] memo) {
        if (i == stones.length) return total - 2 * (half - rem);
        if (memo[i][rem] != null) return memo[i][rem];
        int best = splitFrom(i + 1, rem, half, total, stones, memo);
        if (stones[i] <= rem) best = Math.min(best, splitFrom(i + 1, rem - stones[i], half, total, stones, memo));
        return memo[i][rem] = best;
    }

    // ---------- Target Sum (LC 494): +/- signs = count subsets summing to (total + target) / 2 ----------
    // COST: O(n*s) time and memo.
    static int findTargetSumWays(int[] nums, int target) {
        int total = 0;
        for (int x : nums) total += x;
        if (Math.abs(target) > total || (total + target) % 2 != 0) return 0;
        int s = (total + target) / 2;
        return countSubsets(0, s, nums, new Integer[nums.length][s + 1]);
    }

    // state: (item i, remaining sum rem) | choice: skip or take nums[i] | base: i == n -> 1 if rem == 0 else 0
    private static int countSubsets(int i, int rem, int[] nums, Integer[][] memo) {
        if (i == nums.length) return rem == 0 ? 1 : 0;
        if (memo[i][rem] != null) return memo[i][rem];
        int r = countSubsets(i + 1, rem, nums, memo);
        if (nums[i] <= rem) r += countSubsets(i + 1, rem - nums[i], nums, memo);
        return memo[i][rem] = r;
    }

    // ---------- Coin Change (LC 322): fewest coins, unbounded ----------
    // COST: O(amount * coins) time, O(amount) memo.
    // DEPTH: recursion amount deep; at amount >= 1e4 run inside a big-stack thread (see SCC_Bridges_2SAT.runWithBigStack).
    static int coinChangeMin(int[] coins, int amount) {
        int r = fewestCoins(amount, coins, new Integer[amount + 1]);
        return r >= Integer.MAX_VALUE / 2 ? -1 : r;
    }

    // state: rem = amount left | choice: which coin to use next | base: rem == 0 -> 0; unreachable -> INF
    private static int fewestCoins(int rem, int[] coins, Integer[] memo) {
        if (rem == 0) return 0;
        if (memo[rem] != null) return memo[rem];
        int best = Integer.MAX_VALUE / 2;
        for (int c : coins)
            if (c <= rem) best = Math.min(best, 1 + fewestCoins(rem - c, coins, memo));
        return memo[rem] = best;
    }

    // Coin Change II (LC 518): number of COMBINATIONS, state carries the coin index
    // COST: O(amount * coins) time and memo.
    static int coinChangeWays(int amount, int[] coins) {
        return combos(0, amount, coins, new Integer[coins.length][amount + 1]);
    }

    // state: (coin i, remaining rem) | choice: skip coin i for good, or use it again (stay on i) | base: rem == 0 -> 1, i == n -> 0
    private static int combos(int i, int rem, int[] coins, Integer[][] memo) {
        if (rem == 0) return 1;
        if (i == coins.length) return 0;
        if (memo[i][rem] != null) return memo[i][rem];
        int r = combos(i + 1, rem, coins, memo);
        if (coins[i] <= rem) r += combos(i, rem - coins[i], coins, memo);
        return memo[i][rem] = r;
    }

    // Combination Sum IV (LC 377): number of ordered SEQUENCES, state is only the remaining target
    // COST: O(target * nums) time, O(target) memo.
    static int combinationSum4(int[] nums, int target) {
        return (int) sequences(target, nums, new Long[target + 1]);
    }

    // state: rem = target left | choice: which number comes next (order matters) | base: rem == 0 -> 1 sequence
    private static long sequences(int rem, int[] nums, Long[] memo) {
        if (rem == 0) return 1;
        if (memo[rem] != null) return memo[rem];
        long r = 0;
        for (int x : nums) if (x <= rem) r += sequences(rem - x, nums, memo);
        return memo[rem] = r;
    }

    // Perfect Squares (LC 279): unbounded, min count
    // COST: O(n * sqrt(n)) time, O(n) memo.
    // DEPTH: recursion n deep; at n >= 1e4 run inside a big-stack thread (see SCC_Bridges_2SAT.runWithBigStack).
    static int numSquares(int n) { return fewestSquares(n, new Integer[n + 1]); }

    // state: rem = value left | choice: which square s*s to subtract | base: rem == 0 -> 0
    private static int fewestSquares(int rem, Integer[] memo) {
        if (rem == 0) return 0;
        if (memo[rem] != null) return memo[rem];
        int best = rem; // all 1s
        for (int s = 1; s * s <= rem; s++) best = Math.min(best, 1 + fewestSquares(rem - s * s, memo));
        return memo[rem] = best;
    }

    // ---------- Ones and Zeroes (LC 474): two capacities = 2D 0/1 knapsack ----------
    // COST: O(len * m * n) time and memo.
    static int findMaxForm(String[] strs, int m, int n) {
        int len = strs.length;
        int[] zeros = new int[len], ones = new int[len];
        for (int i = 0; i < len; i++)
            for (char ch : strs[i].toCharArray()) if (ch == '0') zeros[i]++; else ones[i]++;
        return formFrom(0, m, n, zeros, ones, new Integer[len][m + 1][n + 1]);
    }

    // state: (string i, zeros left a, ones left b) | choice: skip string i or take it | base: i == len -> 0
    private static int formFrom(int i, int a, int b, int[] zeros, int[] ones, Integer[][][] memo) {
        if (i == zeros.length) return 0;
        if (memo[i][a][b] != null) return memo[i][a][b];
        int best = formFrom(i + 1, a, b, zeros, ones, memo);
        if (zeros[i] <= a && ones[i] <= b) best = Math.max(best, 1 + formFrom(i + 1, a - zeros[i], b - ones[i], zeros, ones, memo));
        return memo[i][a][b] = best;
    }
}
