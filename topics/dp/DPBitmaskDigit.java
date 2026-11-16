import java.util.*;

public class DPBitmaskDigit {

    // ---------- Bitmask DP: dp[mask] over a subset of <= ~20 items ----------
    // RECOGNIZE: n <= 20, "assign / partition / visit every item once".

    // Minimum Cost Assignment: cost[i][j] = worker i on task j, one each
    static int minCostAssignment(int[][] cost) {
        int n = cost.length;
        int[] dp = new int[1 << n];
        Arrays.fill(dp, Integer.MAX_VALUE);
        dp[0] = 0;
        for (int mask = 0; mask < (1 << n); mask++) {
            if (dp[mask] == Integer.MAX_VALUE) continue;
            int worker = Integer.bitCount(mask); // next worker to place
            if (worker == n) continue;
            for (int t = 0; t < n; t++)
                if ((mask >> t & 1) == 0)
                    dp[mask | 1 << t] = Math.min(dp[mask | 1 << t], dp[mask] + cost[worker][t]);
        }
        return dp[(1 << n) - 1];
    }

    // Partition to K Equal Sum Subsets (LC 698): dp[mask] = sum mod target of items used, -1 if unreachable
    static boolean canPartitionKSubsets(int[] nums, int k) {
        int sum = 0;
        for (int x : nums) sum += x;
        if (sum % k != 0) return false;
        int target = sum / k, n = nums.length;
        for (int x : nums) if (x > target) return false;
        int[] fill = new int[1 << n];
        Arrays.fill(fill, -1);
        fill[0] = 0;
        for (int mask = 0; mask < (1 << n); mask++) {
            if (fill[mask] < 0) continue;
            for (int i = 0; i < n; i++) {
                if ((mask >> i & 1) != 0 || fill[mask] + nums[i] > target) continue;
                fill[mask | 1 << i] = (fill[mask] + nums[i]) % target;
            }
        }
        return fill[(1 << n) - 1] == 0;
    }

    // Beautiful Arrangement (LC 526): position = popcount(mask) + 1
    static int countArrangement(int n) {
        int[] dp = new int[1 << n];
        dp[0] = 1;
        for (int mask = 0; mask < (1 << n); mask++) {
            if (dp[mask] == 0) continue;
            int pos = Integer.bitCount(mask) + 1;
            for (int v = 1; v <= n; v++)
                if ((mask >> (v - 1) & 1) == 0 && (v % pos == 0 || pos % v == 0)) dp[mask | 1 << (v - 1)] += dp[mask];
        }
        return dp[(1 << n) - 1];
    }

    // Can I Win (LC 464): memo over the set of numbers already taken
    static boolean canIWin(int maxChoosable, int desiredTotal) {
        if (desiredTotal <= 0) return true;
        if (maxChoosable * (maxChoosable + 1) / 2 < desiredTotal) return false;
        return win(maxChoosable, desiredTotal, 0, new Boolean[1 << maxChoosable]);
    }

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

    // Number of Digit One (LC 233)
    static int countDigitOne(int n) {
        long count = 0;
        for (long p = 1; p <= n; p *= 10) {
            long high = n / (p * 10), cur = (n / p) % 10, low = n % p;
            if (cur == 0) count += high * p;
            else if (cur == 1) count += high * p + low + 1;
            else count += (high + 1) * p;
        }
        return (int) count;
    }

    // Numbers At Most N Given Digit Set (LC 902)
    static int atMostNGivenDigitSet(String[] digits, int n) {
        String s = String.valueOf(n);
        int L = s.length(), d = digits.length, res = 0;
        for (int len = 1; len < L; len++) res += (int) Math.pow(d, len);
        for (int i = 0; i < L; i++) {
            boolean sameDigit = false;
            for (String dg : digits) {
                if (dg.charAt(0) < s.charAt(i)) res += (int) Math.pow(d, L - i - 1);
                else if (dg.charAt(0) == s.charAt(i)) sameDigit = true;
            }
            if (!sameDigit) return res;
        }
        return res + 1; // n itself is buildable
    }

    // Generic digit DP: count x in [1, n] with no repeated digit (LC 1012)
    static int numDupDigitsAtMostN(int n) {
        return n - countUniqueDigits(n);
    }

    private static int countUniqueDigits(int n) {
        char[] s = String.valueOf(n).toCharArray();
        Map<String, Integer> memo = new HashMap<>();
        return dig(s, 0, 0, true, false, memo);
    }

    private static int dig(char[] s, int pos, int mask, boolean tight, boolean started, Map<String, Integer> memo) {
        if (pos == s.length) return started ? 1 : 0;
        String key = pos + "," + mask + "," + tight + "," + started;
        Integer c = memo.get(key);
        if (c != null) return c;
        int res = 0, lim = tight ? s[pos] - '0' : 9;
        for (int d = 0; d <= lim; d++) {
            boolean nt = tight && d == lim;
            if (!started && d == 0) res += dig(s, pos + 1, mask, nt, false, memo);
            else if ((mask >> d & 1) == 0) res += dig(s, pos + 1, mask | 1 << d, nt, true, memo);
        }
        memo.put(key, res);
        return res;
    }
}
