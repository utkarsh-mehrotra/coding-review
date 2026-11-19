import java.util.*;

public class DPTest {
    // Top-down DP recurses ~10^4 deep on some cases (e.g. checkRecord(10101)); Linux gives the main thread a small
    // stack, so run the suite in a big-stack thread and rethrow anything it throws so a failure still exits non-zero.
    public static void main(String[] a) throws Throwable {
        Throwable[] err = new Throwable[1];
        Thread t = new Thread(null, () -> { try { run(a); } catch (Throwable e) { err[0] = e; } }, "dp-tests", 1L << 28);
        t.start();
        t.join();
        if (err[0] != null) throw err[0];
    }

    static void run(String[] a) {
        // Linear
        Check.eq("climb", DPLinear.climbStairsMemo(10), 89);
        Check.eq("rob", DPLinear.rob(new int[]{2, 7, 9, 3, 1}), 12);
        Check.eq("robCirc", DPLinear.robCircular(new int[]{2, 3, 2}), 3);
        Check.eq("decode", DPLinear.numDecodings("226") + "," + DPLinear.numDecodings("06") + "," + DPLinear.numDecodings("12"), "3,0,2");
        Check.eq("kadane", DPLinear.maxSubArray(new int[]{-2, 1, -3, 4, -1, 2, 1, -5, 4}), 6);
        Check.eq("maxProd", DPLinear.maxProduct(new int[]{2, 3, -2, 4}) + "," + DPLinear.maxProduct(new int[]{-2, 0, -1}), "6,0");
        int[] p = {3, 3, 5, 0, 0, 3, 1, 4};
        Check.eq("stock1", DPLinear.maxProfitOne(new int[]{7, 1, 5, 3, 6, 4}), 5);
        Check.eq("stock2", DPLinear.maxProfitUnlimited(new int[]{7, 1, 5, 3, 6, 4}), 7);
        Check.eq("stock3", DPLinear.maxProfitK(2, p), 6);
        Check.eq("stockK", DPLinear.maxProfitK(2, new int[]{3, 2, 6, 5, 0, 3}), 7);
        Check.eq("stockCool", DPLinear.maxProfitCooldown(new int[]{1, 2, 3, 0, 2}), 3);
        Check.eq("stockFee", DPLinear.maxProfitFee(new int[]{1, 3, 2, 8, 4, 9}, 2), 8);
        Check.eq("minCostStairs", DPLinear.minCostClimbingStairs(new int[]{10, 15, 20}), 15);

        // Knapsack
        Check.eq("k01", DPKnapsack.knapsack01(new int[]{1, 3, 4, 5}, new int[]{1, 4, 5, 7}, 7), 9);
        Check.eq("kUnb", DPKnapsack.knapsackUnbounded(new int[]{2, 3}, new int[]{3, 5}, 7), 11);
        Check.eq("kBnd", DPKnapsack.knapsackBounded(new int[]{2}, new int[]{3}, new int[]{3}, 5), 6);
        Check.eq("partition", DPKnapsack.canPartition(new int[]{1, 5, 11, 5}) + "," + DPKnapsack.canPartition(new int[]{1, 2, 3, 5}), "true,false");
        Check.eq("lastStone2", DPKnapsack.lastStoneWeightII(new int[]{2, 7, 4, 1, 8, 1}), 1);
        Check.eq("targetSum", DPKnapsack.findTargetSumWays(new int[]{1, 1, 1, 1, 1}, 3), 5);
        Check.eq("coinMin", DPKnapsack.coinChangeMin(new int[]{1, 2, 5}, 11) + "," + DPKnapsack.coinChangeMin(new int[]{2}, 3), "3,-1");
        Check.eq("coinWays", DPKnapsack.coinChangeWays(5, new int[]{1, 2, 5}), 4);
        Check.eq("combSum4", DPKnapsack.combinationSum4(new int[]{1, 2, 3}, 4), 7);
        Check.eq("numSquares", DPKnapsack.numSquares(12) + "," + DPKnapsack.numSquares(13), "3,2");
        Check.eq("onesZeroes", DPKnapsack.findMaxForm(new String[]{"10", "0001", "111001", "1", "0"}, 5, 3), 4);

        // Sequences
        Check.eq("lis", DPSequences.lengthOfLIS(new int[]{10, 9, 2, 5, 3, 7, 101, 18}), 4);
        Check.eq("numLIS", DPSequences.findNumberOfLIS(new int[]{1, 3, 5, 4, 7}), 2);
        Check.eq("envelopes", DPSequences.maxEnvelopes(new int[][]{{5, 4}, {6, 4}, {6, 7}, {2, 3}}), 3);
        Check.eq("lcs", DPSequences.longestCommonSubsequence("abcde", "ace"), 3);
        Check.eq("edit", DPSequences.minDistance("horse", "ros"), 3);
        Check.eq("lps", DPSequences.longestPalindromeSubseq("bbbab"), 4);
        Check.eq("lpSub", DPSequences.longestPalindrome("babad").length(), 3);
        Check.eq("countSub", DPSequences.countSubstrings("aaa"), 6);
        Check.eq("distinct", DPSequences.numDistinct("rabbbit", "rabbit"), 3);
        Check.eq("wordBreak", DPSequences.wordBreak("leetcode", List.of("leet", "code")) + "," + DPSequences.wordBreak("catsandog", List.of("cats", "dog", "sand", "and", "cat")), "true,false");
        Check.eq("wildcard", DPSequences.isMatchWildcard("adceb", "*a*b") + "," + DPSequences.isMatchWildcard("acdcb", "a*c?b"), "true,false");
        Check.eq("regex", DPSequences.isMatchRegex("aab", "c*a*b") + "," + DPSequences.isMatchRegex("mississippi", "mis*is*p*.") + "," + DPSequences.isMatchRegex("ab", ".*"), "true,false,true");
        Check.eq("interleave", DPSequences.isInterleave("aabcc", "dbbca", "aadbbcbcac") + "," + DPSequences.isInterleave("aabcc", "dbbca", "aadbbbaccc"), "true,false");
        Check.eq("repSub", DPSequences.findLength(new int[]{1, 2, 3, 2, 1}, new int[]{3, 2, 1, 4, 7}), 3);

        // Grid / interval
        Check.eq("paths", DPGridInterval.uniquePaths(3, 7), 28);
        Check.eq("pathsObs", DPGridInterval.uniquePathsObstacles(new int[][]{{0, 0, 0}, {0, 1, 0}, {0, 0, 0}}), 2);
        Check.eq("minPath", DPGridInterval.minPathSum(new int[][]{{1, 3, 1}, {1, 5, 1}, {4, 2, 1}}), 7);
        Check.eq("triangle", DPGridInterval.minimumTotal(List.of(List.of(2), List.of(3, 4), List.of(6, 5, 7), List.of(4, 1, 8, 3))), 11);
        Check.eq("maxSquare", DPGridInterval.maximalSquare(new char[][]{"10100".toCharArray(), "10111".toCharArray(), "11111".toCharArray(), "10010".toCharArray()}), 4);
        Check.eq("dungeon", DPGridInterval.calculateMinimumHP(new int[][]{{-2, -3, 3}, {-5, -10, 1}, {10, 30, -5}}), 7);
        Check.eq("cherry2", DPGridInterval.cherryPickup2(new int[][]{{3, 1, 1}, {2, 5, 1}, {1, 5, 5}, {2, 1, 1}}), 24);
        Check.eq("burst", DPGridInterval.maxCoins(new int[]{3, 1, 5, 8}), 167);
        Check.eq("mcm", DPGridInterval.matrixChainOrder(new int[]{10, 30, 5, 60}), 4500);
        Check.eq("cutStick", DPGridInterval.minCostCutStick(7, new int[]{1, 3, 4, 5}), 16);
        Check.eq("palCut", DPGridInterval.minCutPalindrome("aab"), 1);
        Check.eq("printer", DPGridInterval.strangePrinter("aaabbb") + "," + DPGridInterval.strangePrinter("aba"), "2,2");
        Check.eq("stoneGame", DPGridInterval.stoneGameDiff(new int[]{5, 3, 4, 5}) > 0, true);

        // Bitmask / digit
        Check.eq("assign", DPBitmaskDigit.minCostAssignment(new int[][]{{9, 2, 7}, {6, 4, 3}, {5, 8, 1}}), 9);
        Check.eq("kSubsets", DPBitmaskDigit.canPartitionKSubsets(new int[]{4, 3, 2, 3, 5, 2, 1}, 4), true);
        Check.eq("arrange", DPBitmaskDigit.countArrangement(2) + "," + DPBitmaskDigit.countArrangement(4), "2,8");
        Check.eq("canIWin", DPBitmaskDigit.canIWin(10, 11) + "," + DPBitmaskDigit.canIWin(10, 0) + "," + DPBitmaskDigit.canIWin(10, 40), "false,true,false");
        Check.eq("digitOne", DPBitmaskDigit.countDigitOne(13), 6);
        Check.eq("digitSet", DPBitmaskDigit.atMostNGivenDigitSet(new String[]{"1", "3", "5", "7"}, 100) + "," + DPBitmaskDigit.atMostNGivenDigitSet(new String[]{"1", "4", "9"}, 1000000000), "20,29523");
        Check.eq("dupDigits", DPBitmaskDigit.numDupDigitsAtMostN(100), 10);

        // Combined
        Check.eq("jobs", DPCombined.jobScheduling(new int[]{1, 2, 3, 3}, new int[]{3, 4, 5, 6}, new int[]{50, 10, 40, 70}), 120);
        Check.eq("constrained", DPCombined.constrainedSubsetSum(new int[]{10, 2, -10, 5, 20}, 2), 37);
        Check.eq("jumpVI", DPCombined.maxResult(new int[]{1, -1, -2, 4, -7, 3}, 2), 7);
        Check.eq("knight", String.format("%.5f", DPCombined.knightProbability(3, 2, 0, 0)), "0.06250");
        Check.eq("new21", String.format("%.5f", DPCombined.new21Game(21, 17, 10)), "0.73278");
        Check.eq("arith", DPCombined.longestArithSeqLength(new int[]{9, 4, 7, 2, 10}), 3);
        Check.eq("lip", DPCombined.longestIncreasingPath(new int[][]{{9, 9, 4}, {6, 6, 8}, {2, 1, 1}}), 4);

        // Randomized cross-checks
        Random r = new Random(7);
        for (int t = 0; t < 300; t++) {
            int n = r.nextInt(12);
            int[] x = new int[n];
            for (int i = 0; i < n; i++) x[i] = r.nextInt(8);
            Check.eq("lisRand", DPSequences.lengthOfLIS(x), lisBrute(x));

            int items = 1 + r.nextInt(6), cap = r.nextInt(15);
            int[] w = new int[items], v = new int[items];
            for (int i = 0; i < items; i++) { w[i] = 1 + r.nextInt(6); v[i] = r.nextInt(10); }
            int best = 0;
            for (int m = 0; m < (1 << items); m++) {
                int sw = 0, sv = 0;
                for (int i = 0; i < items; i++) if ((m >> i & 1) != 0) { sw += w[i]; sv += v[i]; }
                if (sw <= cap) best = Math.max(best, sv);
            }
            Check.eq("knapRand", DPKnapsack.knapsack01(w, v, cap), best);

            int amt = r.nextInt(25);
            int[] coins = {1 + r.nextInt(4), 3 + r.nextInt(4), 6 + r.nextInt(3)};
            Check.eq("coinRand", DPKnapsack.coinChangeMin(coins, amt), coinBrute(coins, amt));

            String s1 = rnd(r, r.nextInt(8)), s2 = rnd(r, r.nextInt(8));
            Check.eq("editRand", DPSequences.minDistance(s1, s2), editBrute(s1, s2, s1.length(), s2.length()));
            Check.eq("palRand", DPSequences.countSubstrings(s1), palBrute(s1));
        }
        // critique regressions
        Check.eq("numTilings0", DPVariants.numTilings(0) + "," + DPVariants.numTilings(1) + "," + DPVariants.numTilings(3), "1,1,5");
        Check.eq("maxProdLC152", DPLinear.maxProduct(new int[]{-2, 3, -4}), 24);
        Check.done("DPTest");
    }

    static String rnd(Random r, int n) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < n; i++) sb.append((char) ('a' + r.nextInt(3)));
        return sb.toString();
    }

    static int coinBrute(int[] coins, int amt) {
        if (amt == 0) return 0;
        int best = -1;
        for (int c : coins) if (c <= amt) {
            int sub = coinBrute(coins, amt - c);
            if (sub >= 0 && (best < 0 || sub + 1 < best)) best = sub + 1;
        }
        return best;
    }

    static int editBrute(String a, String b, int i, int j) {
        if (i == 0) return j;
        if (j == 0) return i;
        if (a.charAt(i - 1) == b.charAt(j - 1)) return editBrute(a, b, i - 1, j - 1);
        return 1 + Math.min(editBrute(a, b, i - 1, j - 1), Math.min(editBrute(a, b, i - 1, j), editBrute(a, b, i, j - 1)));
    }

    static int palBrute(String s) {
        int c = 0;
        for (int i = 0; i < s.length(); i++)
            for (int j = i + 1; j <= s.length(); j++) {
                String t = s.substring(i, j);
                if (new StringBuilder(t).reverse().toString().equals(t)) c++;
            }
        return c;
    }

    // Independent check for LIS: every subset of indices, keep the longest strictly increasing one (small n only).
    static int lisBrute(int[] a) {
        int n = a.length, best = 0;
        for (int mask = 0; mask < (1 << n); mask++) {
            int prev = Integer.MIN_VALUE, len = 0; boolean ok = true;
            for (int i = 0; i < n && ok; i++) if ((mask >> i & 1) == 1) { if (a[i] <= prev) ok = false; else { prev = a[i]; len++; } }
            if (ok) best = Math.max(best, len);
        }
        return best;
    }
}
