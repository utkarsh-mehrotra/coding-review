import java.util.*;

public class DPBitmaskDigit {

    // ---------- Bitmask DP: memo f(mask) over a subset of <= ~20 items ----------
    // RECOGNIZE: n <= 20, "assign / partition / visit every item once".

    // Minimum Cost Assignment: cost[i][j] = worker i on task j, one each. O(2^n * n) time, O(2^n) memo
    static int minCostAssignment(int[][] cost) {
        int n = cost.length;
        return assignFrom(0, cost, new Integer[1 << n]);
    }

    // state: mask = tasks taken (worker = popcount(mask) is next) | choice: which free task t | base: all tasks taken -> 0
    private static int assignFrom(int mask, int[][] cost, Integer[] memo) {
        int n = cost.length;
        if (mask == (1 << n) - 1) return 0;
        if (memo[mask] != null) return memo[mask];
        int worker = Integer.bitCount(mask), best = Integer.MAX_VALUE;
        for (int t = 0; t < n; t++)
            if ((mask >> t & 1) == 0)
                best = Math.min(best, cost[worker][t] + assignFrom(mask | 1 << t, cost, memo));
        return memo[mask] = best;
    }

    // Partition to K Equal Sum Subsets (LC 698): memo over mask of used items; fill = their sum mod target. O(2^n * n)
    static boolean canPartitionKSubsets(int[] nums, int k) {
        int sum = 0;
        for (int x : nums) sum += x;
        if (sum % k != 0) return false;
        int target = sum / k, n = nums.length;
        for (int x : nums) if (x > target) return false;
        return canFill(0, 0, nums, target, new Boolean[1 << n]);
    }

    // state: mask = items used, fill = sum of current bucket (determined by mask) | choice: next unused item | base: all used -> true
    private static boolean canFill(int mask, int fill, int[] nums, int target, Boolean[] memo) {
        if (mask == (1 << nums.length) - 1) return true;
        if (memo[mask] != null) return memo[mask];
        for (int i = 0; i < nums.length; i++) {
            if ((mask >> i & 1) != 0 || fill + nums[i] > target) continue;
            if (canFill(mask | 1 << i, (fill + nums[i]) % target, nums, target, memo)) return memo[mask] = true;
        }
        return memo[mask] = false;
    }

    // Beautiful Arrangement (LC 526): position = popcount(mask) + 1. O(2^n * n)
    static int countArrangement(int n) {
        return arrange(0, n, new Integer[1 << n]);
    }

    // state: mask = values already placed | choice: value v for position popcount+1 if divisible either way | base: all placed -> 1
    private static int arrange(int mask, int n, Integer[] memo) {
        if (mask == (1 << n) - 1) return 1;
        if (memo[mask] != null) return memo[mask];
        int pos = Integer.bitCount(mask) + 1, res = 0;
        for (int v = 1; v <= n; v++)
            if ((mask >> (v - 1) & 1) == 0 && (v % pos == 0 || pos % v == 0)) res += arrange(mask | 1 << (v - 1), n, memo);
        return memo[mask] = res;
    }

    // Can I Win (LC 464): memo over the set of numbers already taken
    static boolean canIWin(int maxChoosable, int desiredTotal) {
        if (desiredTotal <= 0) return true;
        if (maxChoosable * (maxChoosable + 1) / 2 < desiredTotal) return false;
        return win(maxChoosable, desiredTotal, 0, new Boolean[1 << maxChoosable]);
    }

    // state: used = numbers taken (remain is determined by used) | choice: pick i; win if i >= remain or opponent loses | base: no winning pick -> false
    private static boolean win(int max, int remain, int used, Boolean[] memo) {
        if (memo[used] != null) return memo[used];
        for (int i = 1; i <= max; i++) {
            if ((used >> (i - 1) & 1) != 0) continue;
            if (i >= remain || !win(max, remain - i, used | 1 << (i - 1), memo)) return memo[used] = true;
        }
        return memo[used] = false;
    }

    // Subset enumeration trick: for (sub = mask; sub > 0; sub = (sub - 1) & mask) -> total 3^n over all masks

    // ---------- Digit DP ----------
    // RECOGNIZE: "count numbers in [0, N] with a digit property". State = (pos, tight, started, property).

    // Number of Digit One (LC 233): total count of digit 1 over [0, n]. O(len^2 * 10)
    static int countDigitOne(int n) {
        char[] s = String.valueOf(n).toCharArray();
        return (int) onesFrom(0, true, 0, s, new Long[s.length + 1][2][s.length + 1]);
    }

    // state: (pos, tight, ones so far) | choice: digit d in 0..limit | base: pos == len -> ones so far
    private static long onesFrom(int pos, boolean tight, int ones, char[] s, Long[][][] memo) {
        if (pos == s.length) return ones;
        int t = tight ? 1 : 0;
        if (memo[pos][t][ones] != null) return memo[pos][t][ones];
        int lim = tight ? s[pos] - '0' : 9;
        long res = 0;
        for (int d = 0; d <= lim; d++) res += onesFrom(pos + 1, tight && d == lim, ones + (d == 1 ? 1 : 0), s, memo);
        return memo[pos][t][ones] = res;
    }

    // Numbers At Most N Given Digit Set (LC 902): digit DP over (pos, tight, started)
    static int atMostNGivenDigitSet(String[] digits, int n) {
        char[] s = String.valueOf(n).toCharArray();
        return buildable(0, true, false, digits, s, new Integer[s.length][2][2]);
    }

    // state: (pos, tight, started) | choice: skip a leading position, or place a digit from the set (<= s[pos] if tight) | base: pos == len -> started ? 1 : 0
    private static int buildable(int pos, boolean tight, boolean started, String[] digits, char[] s, Integer[][][] memo) {
        if (pos == s.length) return started ? 1 : 0;
        int t = tight ? 1 : 0, st = started ? 1 : 0;
        if (memo[pos][t][st] != null) return memo[pos][t][st];
        int res = 0;
        if (!started) res += buildable(pos + 1, false, false, digits, s, memo); // shorter number: leave this slot empty
        for (String dg : digits) {
            char c = dg.charAt(0);
            if (tight && c > s[pos]) continue;
            res += buildable(pos + 1, tight && c == s[pos], true, digits, s, memo);
        }
        return memo[pos][t][st] = res;
    }

    // Generic digit DP: count x in [1, n] with no repeated digit (LC 1012)
    static int numDupDigitsAtMostN(int n) {
        return n - countUniqueDigits(n);
    }

    private static int countUniqueDigits(int n) {
        char[] s = String.valueOf(n).toCharArray();
        return dig(s, 0, 0, true, false, new Integer[s.length][1 << 10][2][2]);
    }

    // state: (pos, digit-mask used, tight, started) | choice: digit d <= limit not already used | base: pos == len -> started ? 1 : 0
    private static int dig(char[] s, int pos, int mask, boolean tight, boolean started, Integer[][][][] memo) {
        if (pos == s.length) return started ? 1 : 0;
        int t = tight ? 1 : 0, st = started ? 1 : 0;
        if (memo[pos][mask][t][st] != null) return memo[pos][mask][t][st];
        int res = 0, lim = tight ? s[pos] - '0' : 9;
        for (int d = 0; d <= lim; d++) {
            boolean nt = tight && d == lim;
            if (!started && d == 0) res += dig(s, pos + 1, mask, nt, false, memo);
            else if ((mask >> d & 1) == 0) res += dig(s, pos + 1, mask | 1 << d, nt, true, memo);
        }
        return memo[pos][mask][t][st] = res;
    }
}
