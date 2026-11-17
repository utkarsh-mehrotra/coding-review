import java.util.*;

public class DPLinear {

    // DP FRAMEWORK (run it on every problem)
    //   1. State      what is the least I must know to decide the future? (dp[i], dp[i][j], dp[mask])
    //   2. Choice     what can I do from this state?
    //   3. Transition dp[state] = best over choices of (choice cost + dp[next state])
    //   4. Base       states whose answer needs no recursion
    //   5. Order      evaluate so every dependency is ready (memo: automatic; table: loop direction)
    //   6. Shrink     keep only the rows or values later states still read

    // ---------- Climbing Stairs (LC 70): one problem, three forms ----------
    // RECOGNIZE: count ways / min cost where step i depends on a few earlier steps.
    static int climbStairsMemo(int n) { return climb(n, new int[n + 1]); }

    private static int climb(int n, int[] memo) {
        if (n <= 1) return 1;
        if (memo[n] != 0) return memo[n];
        return memo[n] = climb(n - 1, memo) + climb(n - 2, memo);
    }

    static int climbStairsTab(int n) {
        int[] dp = new int[n + 2];
        dp[0] = 1; dp[1] = 1;
        for (int i = 2; i <= n; i++) dp[i] = dp[i - 1] + dp[i - 2];
        return dp[n];
    }

    // LC 70
    static int climbStairsO1(int n) {
        int prev = 1, cur = 1;
        for (int i = 2; i <= n; i++) { int next = prev + cur; prev = cur; cur = next; }
        return cur;
    }

    // ---------- House Robber (LC 198): take / skip state ----------
    static int rob(int[] nums) {
        int take = 0, skip = 0; // best total if the previous house was robbed / not robbed
        for (int x : nums) {
            int newTake = skip + x;
            skip = Math.max(skip, take);
            take = newTake;
        }
        return Math.max(take, skip);
    }

    // House Robber II (LC 213): circle = best of "drop first" and "drop last"
    static int robCircular(int[] nums) {
        int n = nums.length;
        if (n == 1) return nums[0];
        return Math.max(robRange(nums, 0, n - 2), robRange(nums, 1, n - 1));
    }

    private static int robRange(int[] a, int lo, int hi) {
        int take = 0, skip = 0;
        for (int i = lo; i <= hi; i++) {
            int newTake = skip + a[i];
            skip = Math.max(skip, take);
            take = newTake;
        }
        return Math.max(take, skip);
    }

    // ---------- Decode Ways (LC 91): dp over prefix length, 1- and 2-digit choices ----------
    static int numDecodings(String s) {
        int n = s.length();
        int prev2 = 1, prev1 = s.charAt(0) == '0' ? 0 : 1; // ways for prefix lengths i-2 and i-1
        for (int i = 2; i <= n; i++) {
            int cur = 0;
            if (s.charAt(i - 1) != '0') cur += prev1;
            int two = Integer.parseInt(s.substring(i - 2, i));
            if (two >= 10 && two <= 26) cur += prev2;
            prev2 = prev1;
            prev1 = cur;
        }
        return prev1;
    }

    // ---------- Kadane (LC 53): best subarray ending here ----------
    static int maxSubArray(int[] nums) {
        int best = nums[0], cur = nums[0];
        for (int i = 1; i < nums.length; i++) {
            cur = Math.max(nums[i], cur + nums[i]);
            best = Math.max(best, cur);
        }
        return best;
    }

    // ---------- Maximum Product Subarray (LC 152): track max AND min (negatives flip them) ----------
    static int maxProduct(int[] nums) {
        int hi = nums[0], lo = nums[0], best = nums[0];
        for (int i = 1; i < nums.length; i++) {
            int x = nums[i];
            if (x < 0) { int t = hi; hi = lo; lo = t; }
            hi = Math.max(x, hi * x);
            lo = Math.min(x, lo * x);
            best = Math.max(best, hi);
        }
        return best;
    }

    // ---------- Stock family: state machine (holding / not holding, transactions used) ----------
    // I (LC 121): one transaction
    static int maxProfitOne(int[] p) {
        int min = Integer.MAX_VALUE, best = 0;
        for (int x : p) { min = Math.min(min, x); best = Math.max(best, x - min); }
        return best;
    }

    // II (LC 122): unlimited transactions = sum of every uphill step
    static int maxProfitUnlimited(int[] p) {
        int sum = 0;
        for (int i = 1; i < p.length; i++) sum += Math.max(0, p[i] - p[i - 1]);
        return sum;
    }

    // IV (LC 188): at most k transactions. hold[t] / sold[t] = best cash while holding / after finishing transaction t
    static int maxProfitK(int k, int[] p) {
        int n = p.length;
        if (n < 2 || k == 0) return 0;
        if (k >= n / 2) return maxProfitUnlimited(p);
        int[] hold = new int[k + 1], sold = new int[k + 1];
        Arrays.fill(hold, Integer.MIN_VALUE / 2);
        for (int x : p) {
            for (int t = 1; t <= k; t++) {
                hold[t] = Math.max(hold[t], sold[t - 1] - x); // buy: open transaction t
                sold[t] = Math.max(sold[t], hold[t] + x);     // sell: close transaction t
            }
        }
        return sold[k];
    }

    // with cooldown (LC 309): hold / sold today / rest (free to buy)
    static int maxProfitCooldown(int[] p) {
        int hold = Integer.MIN_VALUE / 2, sold = 0, rest = 0;
        for (int x : p) {
            int prevSold = sold;
            sold = hold + x;
            hold = Math.max(hold, rest - x);
            rest = Math.max(rest, prevSold);
        }
        return Math.max(sold, rest);
    }

    // with fee (LC 714)
    static int maxProfitFee(int[] p, int fee) {
        int cash = 0, hold = -p[0];
        for (int i = 1; i < p.length; i++) {
            cash = Math.max(cash, hold + p[i] - fee);
            hold = Math.max(hold, cash - p[i]);
        }
        return cash;
    }

    // ---------- Min Cost Climbing Stairs (LC 746) ----------
    static int minCostClimbingStairs(int[] cost) {
        int a = 0, b = 0; // cost to stand on step i-2 and i-1
        for (int i = 2; i <= cost.length; i++) {
            int c = Math.min(b + cost[i - 1], a + cost[i - 2]);
            a = b;
            b = c;
        }
        return b;
    }
}
