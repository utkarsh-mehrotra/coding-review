import java.util.*;

public class DPLinear {

    // DP FRAMEWORK (run it on every problem)
    //   1. State      what is the least I must know to decide the future? (dp[i], dp[i][j], dp[mask])
    //   2. Choice     what can I do from this state?
    //   3. Transition dp[state] = best over choices of (choice cost + dp[next state])
    //   4. Base       states whose answer needs no recursion
    //   5. Order      memo makes it automatic: recurse, cache by state, never recompute
    //   6. Memo       cache each state's answer (null = not computed yet)

    // ---------- Climbing Stairs (LC 70): top-down memo ----------
    // RECOGNIZE: count ways / min cost where step i depends on a few earlier steps.
    // COST: O(n) time, O(n) memo + recursion stack.
    // DEPTH: recursion n deep; at n >= 1e4 run inside a big-stack thread (see SCC_Bridges_2SAT.runWithBigStack).
    static int climbStairsMemo(int n) { return ways(n, new Integer[n + 1]); }

    // state: n = steps left | choice: take 1 or 2 steps | base: n <= 1 -> 1 way
    private static int ways(int n, Integer[] memo) {
        if (n <= 1) return 1;
        if (memo[n] != null) return memo[n];
        return memo[n] = ways(n - 1, memo) + ways(n - 2, memo);
    }

    // ---------- House Robber (LC 198): take / skip state ----------
    // COST: O(n) time, O(n) memo.
    // DEPTH: recursion nums.length deep; at nums.length >= 1e4 run inside a big-stack thread (see SCC_Bridges_2SAT.runWithBigStack).
    static int rob(int[] nums) { return robFrom(0, nums.length - 1, nums, new Integer[nums.length]); }

    // state: i = first house still available | choice: rob it (jump to i+2) or skip it | base: past hi -> 0
    private static int robFrom(int i, int hi, int[] a, Integer[] memo) {
        if (i > hi) return 0;
        if (memo[i] != null) return memo[i];
        return memo[i] = Math.max(a[i] + robFrom(i + 2, hi, a, memo), robFrom(i + 1, hi, a, memo));
    }

    // House Robber II (LC 213): circle = best of "drop first" and "drop last"
    static int robCircular(int[] nums) {
        int n = nums.length;
        if (n == 1) return nums[0];
        return Math.max(robFrom(0, n - 2, nums, new Integer[n]), robFrom(1, n - 1, nums, new Integer[n]));
    }

    // ---------- Decode Ways (LC 91): state = suffix start, 1- and 2-digit choices ----------
    // COST: O(n) time, O(n) memo.
    // DEPTH: recursion s.length() deep; at s.length() >= 1e4 run inside a big-stack thread (see SCC_Bridges_2SAT.runWithBigStack).
    static int numDecodings(String s) { return decodeFrom(0, s, new Integer[s.length() + 1]); }

    // state: i = start of undecoded suffix | choice: decode 1 digit or 2 digits | base: i == n -> 1 way
    private static int decodeFrom(int i, String s, Integer[] memo) {
        int n = s.length();
        if (i == n) return 1;
        if (s.charAt(i) == '0') return 0;
        if (memo[i] != null) return memo[i];
        int r = decodeFrom(i + 1, s, memo);
        if (i + 2 <= n && Integer.parseInt(s.substring(i, i + 2)) <= 26) r += decodeFrom(i + 2, s, memo);
        return memo[i] = r;
    }

    // ---------- Kadane (LC 53): best subarray STARTING here (extend right only if the rest is positive) ----------
    // Faster: Kadane's running max, O(n) time O(1) space — not DP-memo; mention as a follow-up.
    // DEPTH: recursion nums.length deep; at nums.length >= 1e4 run inside a big-stack thread (see SCC_Bridges_2SAT.runWithBigStack).
    static int maxSubArray(int[] nums) {
        Integer[] memo = new Integer[nums.length];
        int best = Integer.MIN_VALUE;
        for (int i = 0; i < nums.length; i++) best = Math.max(best, startAt(i, nums, memo));
        return best;
    }

    // state: i = subarray must start at i | choice: stop here or extend to i+1 | base: last element -> itself
    private static int startAt(int i, int[] a, Integer[] memo) {
        if (i == a.length - 1) return a[i];
        if (memo[i] != null) return memo[i];
        return memo[i] = a[i] + Math.max(0, startAt(i + 1, a, memo));
    }

    // ---------- Maximum Product Subarray (LC 152): track max AND min (negatives flip them) ----------
    // COST: O(n) time, O(n) memo.
    // Int products are safe: LC 152 guarantees every product fits in a 32-bit integer.
    // DEPTH: recursion nums.length deep; at nums.length >= 1e4 run inside a big-stack thread (see SCC_Bridges_2SAT.runWithBigStack).
    static int maxProduct(int[] nums) {
        int n = nums.length;
        Integer[] hi = new Integer[n], lo = new Integer[n];
        int best = Integer.MIN_VALUE;
        for (int i = 0; i < n; i++) best = Math.max(best, prodHi(i, nums, hi, lo));
        return best;
    }

    // state: i = subarray starts at i | choice: stop at i or extend with the best/worst product from i+1 | base: last element
    private static int prodHi(int i, int[] a, Integer[] hi, Integer[] lo) {
        if (i == a.length - 1) return a[i];
        if (hi[i] != null) return hi[i];
        int x = a[i];
        return hi[i] = Math.max(x, Math.max(x * prodHi(i + 1, a, hi, lo), x * prodLo(i + 1, a, hi, lo)));
    }

    // state: i = subarray starts at i | choice: as prodHi but keeps the minimum | base: last element
    private static int prodLo(int i, int[] a, Integer[] hi, Integer[] lo) {
        if (i == a.length - 1) return a[i];
        if (lo[i] != null) return lo[i];
        int x = a[i];
        return lo[i] = Math.min(x, Math.min(x * prodHi(i + 1, a, hi, lo), x * prodLo(i + 1, a, hi, lo)));
    }

    // ---------- Stock family: state machine (holding / not holding, transactions used) ----------
    // I (LC 121): one transaction. COST: O(n) time, O(n) memo.
    // DEPTH: recursion prices.length deep; at prices.length >= 1e4 run inside a big-stack thread (see SCC_Bridges_2SAT.runWithBigStack).
    static int maxProfitOne(int[] p) { return oneFrom(0, 0, p, new Integer[p.length][2]); }

    // state: (day i, hold 0/1) | choice: buy / sell (sell ends the only transaction) / rest | base: i == n -> 0
    private static int oneFrom(int i, int hold, int[] p, Integer[][] memo) {
        if (i == p.length) return 0;
        if (memo[i][hold] != null) return memo[i][hold];
        int rest = oneFrom(i + 1, hold, p, memo);
        int act = hold == 0 ? -p[i] + oneFrom(i + 1, 1, p, memo) : p[i];
        return memo[i][hold] = Math.max(rest, act);
    }

    // II (LC 122): unlimited transactions. COST: O(n) time, O(n) memo.
    // DEPTH: recursion prices.length deep; at prices.length >= 1e4 run inside a big-stack thread (see SCC_Bridges_2SAT.runWithBigStack).
    static int maxProfitUnlimited(int[] p) { return unlimFrom(0, 0, p, new Integer[p.length][2]); }

    // state: (day i, hold 0/1) | choice: buy / sell / rest | base: i == n -> 0
    private static int unlimFrom(int i, int hold, int[] p, Integer[][] memo) {
        if (i == p.length) return 0;
        if (memo[i][hold] != null) return memo[i][hold];
        int rest = unlimFrom(i + 1, hold, p, memo);
        int act = hold == 0 ? -p[i] + unlimFrom(i + 1, 1, p, memo) : p[i] + unlimFrom(i + 1, 0, p, memo);
        return memo[i][hold] = Math.max(rest, act);
    }

    // IV (LC 188): at most k transactions. COST: O(n*k) time and memo.
    // DEPTH: recursion prices.length deep; at prices.length >= 1e4 run inside a big-stack thread (see SCC_Bridges_2SAT.runWithBigStack).
    static int maxProfitK(int k, int[] p) {
        int n = p.length;
        if (n < 2 || k == 0) return 0;
        if (k >= n / 2) return maxProfitUnlimited(p);
        return kFrom(0, 0, k, p, new Integer[n][2][k + 1]);
    }

    // state: (day i, hold 0/1, t = buys left) | choice: buy (uses one t) / sell / rest | base: i == n -> 0
    private static int kFrom(int i, int hold, int t, int[] p, Integer[][][] memo) {
        if (i == p.length) return 0;
        if (memo[i][hold][t] != null) return memo[i][hold][t];
        int best = kFrom(i + 1, hold, t, p, memo);
        if (hold == 0 && t > 0) best = Math.max(best, -p[i] + kFrom(i + 1, 1, t - 1, p, memo));
        if (hold == 1) best = Math.max(best, p[i] + kFrom(i + 1, 0, t, p, memo));
        return memo[i][hold][t] = best;
    }

    // with cooldown (LC 309): selling forces a one-day rest. COST: O(n) time, O(n) memo.
    // DEPTH: recursion prices.length deep; at prices.length >= 1e4 run inside a big-stack thread (see SCC_Bridges_2SAT.runWithBigStack).
    static int maxProfitCooldown(int[] p) { return coolFrom(0, 0, p, new Integer[p.length][2]); }

    // state: (day i, hold 0/1) | choice: buy / sell (then jump to i+2) / rest | base: i >= n -> 0
    private static int coolFrom(int i, int hold, int[] p, Integer[][] memo) {
        if (i >= p.length) return 0;
        if (memo[i][hold] != null) return memo[i][hold];
        int rest = coolFrom(i + 1, hold, p, memo);
        int act = hold == 0 ? -p[i] + coolFrom(i + 1, 1, p, memo) : p[i] + coolFrom(i + 2, 0, p, memo);
        return memo[i][hold] = Math.max(rest, act);
    }

    // with fee (LC 714): fee paid on each sale. COST: O(n) time, O(n) memo.
    // DEPTH: recursion prices.length deep; at prices.length >= 1e4 run inside a big-stack thread (see SCC_Bridges_2SAT.runWithBigStack).
    static int maxProfitFee(int[] p, int fee) { return feeFrom(0, 0, p, fee, new Integer[p.length][2]); }

    // state: (day i, hold 0/1) | choice: buy / sell (pay fee) / rest | base: i == n -> 0
    private static int feeFrom(int i, int hold, int[] p, int fee, Integer[][] memo) {
        if (i == p.length) return 0;
        if (memo[i][hold] != null) return memo[i][hold];
        int rest = feeFrom(i + 1, hold, p, fee, memo);
        int act = hold == 0 ? -p[i] + feeFrom(i + 1, 1, p, fee, memo) : p[i] - fee + feeFrom(i + 1, 0, p, fee, memo);
        return memo[i][hold] = Math.max(rest, act);
    }

    // ---------- Min Cost Climbing Stairs (LC 746) ----------
    // COST: O(n) time, O(n) memo.
    static int minCostClimbingStairs(int[] cost) {
        Integer[] memo = new Integer[cost.length];
        return Math.min(costFrom(0, cost, memo), costFrom(1, cost, memo));
    }

    // state: i = step stood on (cost not yet paid) | choice: climb 1 or 2 | base: i >= n -> past the top, 0
    private static int costFrom(int i, int[] cost, Integer[] memo) {
        if (i >= cost.length) return 0;
        if (memo[i] != null) return memo[i];
        return memo[i] = cost[i] + Math.min(costFrom(i + 1, cost, memo), costFrom(i + 2, cost, memo));
    }
}
