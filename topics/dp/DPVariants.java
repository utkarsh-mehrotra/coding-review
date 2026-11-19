import java.util.*;

public class DPVariants {
    // Framework reminder: state -> choice -> transition -> base -> order -> can the state shrink?

    // ---------- Frog Jump (LC 403): memo over (stone index, last jump size) ----------
    static boolean canCross(int[] stones) {
        int n = stones.length;
        Map<Integer, Integer> at = new HashMap<>();
        for (int i = 0; i < n; i++) at.put(stones[i], i);
        return reach(0, 0, stones, at, new Boolean[n][n + 2]);
    }

    // state: (i = stone index, k = last jump) | choice: next jump k-1, k or k+1 | base: last stone reached -> true
    private static boolean reach(int i, int k, int[] stones, Map<Integer, Integer> at, Boolean[][] memo) {
        if (i == stones.length - 1) return true;
        if (memo[i][k] != null) return memo[i][k];
        boolean ok = false;
        for (int step = k - 1; step <= k + 1 && !ok; step++) {
            Integer j = step > 0 ? at.get(stones[i] + step) : null;
            if (j != null && reach(j, step, stones, at, memo)) ok = true;
        }
        return memo[i][k] = ok;
    }

    // ---------- Stone Game II (LC 1140): best(i, M) = best stones the mover can take from piles i.. with limit M ----------
    static int stoneGameII(int[] piles) {
        int n = piles.length;
        int[] suffix = new int[n + 1];
        for (int i = n - 1; i >= 0; i--) suffix[i] = suffix[i + 1] + piles[i];
        return best(0, 1, suffix, new Integer[n + 1][n + 1]);
    }

    // state: (i = first pile left, m = current limit) | choice: take x in 1..2m piles | base: 2m covers the rest -> take everything
    private static int best(int i, int m, int[] suffix, Integer[][] memo) {
        int n = suffix.length - 1;
        if (i + 2 * m >= n) return suffix[i]; // can take everything left
        if (memo[i][m] != null) return memo[i][m];
        int res = 0;
        for (int x = 1; x <= 2 * m; x++) res = Math.max(res, suffix[i] - best(i + x, Math.max(m, x), suffix, memo)); // total - opponent's best
        return memo[i][m] = res;
    }

    // ---------- Predict the Winner (LC 486): diff(l, r) = best score difference for the mover ----------
    static boolean predictTheWinner(int[] a) {
        int n = a.length;
        return diff(0, n - 1, a, new Integer[n][n]) >= 0;
    }

    // state: (l, r) = remaining a[l..r] | choice: take left or right end | base: one pile -> its value
    private static int diff(int l, int r, int[] a, Integer[][] memo) {
        if (l == r) return a[l];
        if (memo[l][r] != null) return memo[l][r];
        return memo[l][r] = Math.max(a[l] - diff(l + 1, r, a, memo), a[r] - diff(l, r - 1, a, memo));
    }

    // ---------- Super Egg Drop (LC 887): drops(k, f) = min worst-case drops with k eggs and f floors ----------
    // Faster: flip the state to moves (dp[k] += dp[k-1] + 1 per move), O(k log n) — not DP-memo; mention as a follow-up.
    static int superEggDrop(int eggs, int floors) {
        return drops(eggs, floors, new Integer[eggs + 1][floors + 1]);
    }

    // state: (k eggs, f floors) | choice: drop from floor x (binary search: breaks rises with x, survives falls) | base: 0 floors -> 0, 1 egg -> f
    private static int drops(int k, int f, Integer[][] memo) {
        if (f == 0) return 0;
        if (k == 1) return f;
        if (memo[k][f] != null) return memo[k][f];
        int lo = 1, hi = f; // smallest x where the breaking branch is at least as bad as the surviving one
        while (lo < hi) {
            int x = (lo + hi) / 2;
            if (drops(k - 1, x - 1, memo) >= drops(k, f - x, memo)) hi = x; else lo = x + 1;
        }
        int res = Math.max(drops(k - 1, lo - 1, memo), drops(k, f - lo, memo));
        if (lo > 1) res = Math.min(res, Math.max(drops(k - 1, lo - 2, memo), drops(k, f - lo + 1, memo)));
        return memo[k][f] = 1 + res;
    }

    // ---------- Number of Dice Rolls With Target Sum (LC 1155) ----------
    static int numRollsToTarget(int dice, int faces, int target) {
        return ways(dice, target, faces, new Integer[dice + 1][target + 1]);
    }

    // state: (d dice left, s sum still needed) | choice: face f for this die | base: no dice -> 1 iff s == 0
    private static int ways(int d, int s, int faces, Integer[][] memo) {
        final int MOD = 1_000_000_007;
        if (d == 0) return s == 0 ? 1 : 0;
        if (memo[d][s] != null) return memo[d][s];
        int res = 0;
        for (int f = 1; f <= faces && f <= s; f++) res = (res + ways(d - 1, s - f, faces, memo)) % MOD;
        return memo[d][s] = res;
    }

    // ---------- Paint House II (LC 265): paint(i, prev) = min cost for houses i.. when house i-1 used color prev ----------
    // Faster: keep the two smallest previous costs so each row is O(k), O(nk) — not DP-memo; mention as a follow-up.
    // LC 265 has k >= 2 (with k = 1 and n >= 2 no valid painting exists and the sentinel overflows).
    static int minCostII(int[][] costs) {
        int n = costs.length, k = costs[0].length;
        return paint(0, k, costs, new Integer[n][k + 1]);
    }

    // state: (i = house, prev = previous color, k = none yet) | choice: any color != prev | base: past the last house -> 0
    private static int paint(int i, int prev, int[][] costs, Integer[][] memo) {
        int k = costs[0].length;
        if (i == costs.length) return 0;
        if (memo[i][prev] != null) return memo[i][prev];
        int res = Integer.MAX_VALUE;
        for (int c = 0; c < k; c++)
            if (c != prev) res = Math.min(res, costs[i][c] + paint(i + 1, c, costs, memo));
        return memo[i][prev] = res;
    }

    // ---------- Cherry Pickup (LC 741): two walkers go down together; state = (step, r1, r2) ----------
    static int cherryPickup(int[][] grid) {
        int n = grid.length;
        return Math.max(0, pick(0, 0, 0, grid, new Integer[2 * n - 1][n][n]));
    }

    // state: (step = r + c, r1, r2 = rows of the two walkers) | choice: each walker goes down or right | base: both at the corner -> its cherries
    private static int pick(int step, int r1, int r2, int[][] grid, Integer[][][] memo) {
        int n = grid.length, NEG = Integer.MIN_VALUE / 2;
        int c1 = step - r1, c2 = step - r2;
        if (r1 >= n || r2 >= n || c1 >= n || c2 >= n || grid[r1][c1] == -1 || grid[r2][c2] == -1) return NEG;
        if (step == 2 * (n - 1)) return grid[r1][c1];
        if (memo[step][r1][r2] != null) return memo[step][r1][r2];
        int next = Math.max(Math.max(pick(step + 1, r1, r2, grid, memo), pick(step + 1, r1 + 1, r2, grid, memo)),
                            Math.max(pick(step + 1, r1, r2 + 1, grid, memo), pick(step + 1, r1 + 1, r2 + 1, grid, memo)));
        int gain = grid[r1][c1] + (r1 == r2 ? 0 : grid[r2][c2]); // same cell: count once
        return memo[step][r1][r2] = next < 0 ? NEG : next + gain;
    }

    // ---------- Out of Boundary Paths (LC 576): exits(move, r, c) = paths leaving the grid within `move` more moves ----------
    static int findPaths(int m, int n, int maxMove, int startRow, int startCol) {
        return exits(maxMove, startRow, startCol, m, n, new Integer[maxMove + 1][m][n]);
    }

    // state: (moves left, r, c) | choice: 4 directions | base: outside -> 1 path, no moves left -> 0
    private static int exits(int move, int r, int c, int m, int n, Integer[][][] memo) {
        final int MOD = 1_000_000_007;
        if (r < 0 || c < 0 || r >= m || c >= n) return 1;
        if (move == 0) return 0;
        if (memo[move][r][c] != null) return memo[move][r][c];
        int[][] dirs = {{1, 0}, {-1, 0}, {0, 1}, {0, -1}};
        int res = 0;
        for (int[] d : dirs) res = (res + exits(move - 1, r + d[0], c + d[1], m, n, memo)) % MOD;
        return memo[move][r][c] = res;
    }

    // ---------- Domino and Tromino Tiling (LC 790): f(n) = 2 f(n-1) + f(n-3) ----------
    static int numTilings(int n) {
        if (n == 0) return 1; // f(0) = 1, matches the recurrence base
        if (n <= 2) return n;
        return tilings(n, new Integer[n + 1]);
    }

    // state: i = width of the board | choice: recurrence f(i) = 2 f(i-1) + f(i-3) | base: f(0)=1, f(1)=1, f(2)=2
    private static int tilings(int i, Integer[] memo) {
        final int MOD = 1_000_000_007;
        if (i <= 1) return 1;
        if (i == 2) return 2;
        if (memo[i] != null) return memo[i];
        return memo[i] = (int) ((2L * tilings(i - 1, memo) + tilings(i - 3, memo)) % MOD);
    }

    // ---------- Count Different Palindromic Subsequences (LC 730): interval DP, shrink to the outermost equal characters ----------
    static int countPalindromicSubsequences(String s) {
        int n = s.length();
        return palins(0, n - 1, s, new Integer[n][n]);
    }

    // state: (i, j) = substring s[i..j] | choice: outer chars differ -> drop one end; equal -> find same char inside | base: empty -> 0, one char -> 1
    private static int palins(int i, int j, String s, Integer[][] memo) {
        final int MOD = 1_000_000_007;
        if (i > j) return 0;
        if (i == j) return 1;
        if (memo[i][j] != null) return memo[i][j];
        int res;
        if (s.charAt(i) != s.charAt(j)) {
            res = ((palins(i + 1, j, s, memo) + palins(i, j - 1, s, memo) - palins(i + 1, j - 1, s, memo)) % MOD + MOD) % MOD;
        } else {
            int lo = i + 1, hi = j - 1;
            while (lo <= hi && s.charAt(lo) != s.charAt(i)) lo++;
            while (lo <= hi && s.charAt(hi) != s.charAt(i)) hi--;
            long inner = palins(i + 1, j - 1, s, memo);
            if (lo > hi) res = (int) ((2 * inner + 2) % MOD);          // no same char inside: a, aa, plus wrapped
            else if (lo == hi) res = (int) ((2 * inner + 1) % MOD);    // exactly one inside: "a" is already counted
            else res = (int) (((2 * inner - palins(lo + 1, hi - 1, s, memo)) % MOD + MOD) % MOD); // remove double count
        }
        return memo[i][j] = res;
    }

    // ---------- Profitable Schemes (LC 879): state (crime, people left, profit capped at minProfit); capping keeps the table small ----------
    static int profitableSchemes(int n, int minProfit, int[] group, int[] profit) {
        return schemes(0, n, 0, minProfit, group, profit, new Integer[group.length][n + 1][minProfit + 1]);
    }

    // state: (k = crime, left = people available, p = profit so far, capped) | choice: skip or commit crime k | base: all crimes seen -> 1 iff p reached minProfit
    private static int schemes(int k, int left, int p, int minProfit, int[] group, int[] profit, Integer[][][] memo) {
        final int MOD = 1_000_000_007;
        if (k == group.length) return p == minProfit ? 1 : 0;
        if (memo[k][left][p] != null) return memo[k][left][p];
        int res = schemes(k + 1, left, p, minProfit, group, profit, memo);                  // skip this crime
        if (group[k] <= left) {
            int np = Math.min(minProfit, p + profit[k]);                                   // profit above the cap collapses into the cap
            res = (res + schemes(k + 1, left - group[k], np, minProfit, group, profit, memo)) % MOD;
        }
        return memo[k][left][p] = res;
    }

    // ---------- Tallest Billboard (LC 956): state = (rod, difference between the two towers); value = extra height of the shorter tower ----------
    static int tallestBillboard(int[] rods) {
        int sum = 0;
        for (int r : rods) sum += r;
        return Math.max(0, tower(0, 0, rods, new Integer[rods.length][sum + 1]));
    }

    // state: (i = rod, diff = taller - shorter) | choice: skip, add to taller, add to shorter | base: no rods -> 0 iff diff == 0 else impossible
    private static int tower(int i, int diff, int[] rods, Integer[][] memo) {
        final int NEG = Integer.MIN_VALUE / 2;
        if (i == rods.length) return diff == 0 ? 0 : NEG;
        if (memo[i][diff] != null) return memo[i][diff];
        int r = rods[i];
        int res = tower(i + 1, diff, rods, memo);                                                              // skip
        res = Math.max(res, tower(i + 1, diff + r, rods, memo));                                               // add to the taller tower
        res = Math.max(res, Math.min(diff, r) + tower(i + 1, Math.abs(diff - r), rods, memo));                 // add to the shorter tower
        return memo[i][diff] = Math.max(res, NEG);
    }

    // ---------- Best Team With No Conflicts (LC 1626): sort by (age, score), LIS on score allowing equal ----------
    static int bestTeamScore(int[] scores, int[] ages) {
        int n = scores.length;
        Integer[] idx = new Integer[n];
        for (int i = 0; i < n; i++) idx[i] = i;
        Arrays.sort(idx, (a, b) -> ages[a] != ages[b] ? Integer.compare(ages[a], ages[b]) : Integer.compare(scores[a], scores[b]));
        int[] sorted = new int[n];
        for (int i = 0; i < n; i++) sorted[i] = scores[idx[i]];
        Integer[] memo = new Integer[n];
        int best = 0;
        for (int i = 0; i < n; i++) best = Math.max(best, teamEndingAt(i, sorted, memo));
        return best;
    }

    // state: i = last player in sorted order | choice: previous player j with score <= ours | base: no previous player -> own score
    private static int teamEndingAt(int i, int[] sorted, Integer[] memo) {
        if (memo[i] != null) return memo[i];
        int res = sorted[i];
        for (int j = 0; j < i; j++) if (sorted[j] <= sorted[i]) res = Math.max(res, teamEndingAt(j, sorted, memo) + sorted[i]);
        return memo[i] = res;
    }

    // ---------- Remove Boxes (LC 546): memo over (l, r, k) with k equal boxes attached to the left of l ----------
    static int removeBoxes(int[] boxes) {
        int n = boxes.length;
        return rb(boxes, 0, n - 1, 0, new Integer[n][n][n]);
    }

    // state: (l, r, k = equal boxes glued to the left of l) | choice: remove b[l] now, or clear a middle then merge with a later equal box | base: empty range -> 0
    private static int rb(int[] b, int l, int r, int k, Integer[][][] memo) {
        if (l > r) return 0;
        if (memo[l][r][k] != null) return memo[l][r][k];
        int l0 = l, k0 = k;                             // cache under the key we were called with
        while (l < r && b[l + 1] == b[l]) { l++; k++; } // absorb equal neighbours into k
        int best = (k + 1) * (k + 1) + rb(b, l + 1, r, 0, memo); // remove b[l] with its k friends now
        for (int m = l + 1; m <= r; m++)
            if (b[m] == b[l]) best = Math.max(best, rb(b, l + 1, m - 1, 0, memo) + rb(b, m, r, k + 1, memo)); // clear the middle first, then merge
        return memo[l0][r][k0] = best;
    }

    // ---------- Minimum Cost to Merge Stones (LC 1000): cost(i, j, m) = min cost to turn stones i..j into m piles ----------
    static int mergeStones(int[] stones, int k) {
        int n = stones.length;
        if ((n - 1) % (k - 1) != 0) return -1;
        int[] prefix = new int[n + 1];
        for (int i = 0; i < n; i++) prefix[i + 1] = prefix[i] + stones[i];
        return cost(0, n - 1, 1, k, prefix, new Integer[n][n][k + 1]);
    }

    // state: (i, j, m piles wanted) | choice: split point where the first pile ends | base: one stone -> 0 cost for 1 pile, impossible otherwise
    private static int cost(int i, int j, int m, int k, int[] prefix, Integer[][][] memo) {
        final int INF = Integer.MAX_VALUE / 2;
        if (i == j) return m == 1 ? 0 : INF;
        if (memo[i][j][m] != null) return memo[i][j][m];
        int res = INF;
        if (m == 1) res = Math.min(INF, cost(i, j, k, k, prefix, memo) + prefix[j + 1] - prefix[i]); // merge k piles into one
        else
            for (int mid = i; mid < j; mid += k - 1)
                res = Math.min(res, cost(i, mid, 1, k, prefix, memo) + cost(mid + 1, j, m - 1, k, prefix, memo));
        return memo[i][j][m] = res;
    }

    // ---------- Minimum Cost Tree From Leaf Values (LC 1130): interval DP, or the greedy monotone stack ----------
    // Faster: greedy monotone stack, O(n) — not DP-memo; mention as a follow-up.
    static int mctFromLeafValues(int[] arr) {
        int n = arr.length;
        int[][] mx = new int[n][n]; // range maximum lookup
        for (int l = 0; l < n; l++) { mx[l][l] = arr[l]; for (int r = l + 1; r < n; r++) mx[l][r] = Math.max(mx[l][r - 1], arr[r]); }
        return tree(0, n - 1, mx, new Integer[n][n]);
    }

    // state: (l, r) = leaves l..r | choice: split point m between the two subtrees | base: one leaf -> 0
    private static int tree(int l, int r, int[][] mx, Integer[][] memo) {
        if (l == r) return 0;
        if (memo[l][r] != null) return memo[l][r];
        int res = Integer.MAX_VALUE;
        for (int m = l; m < r; m++) res = Math.min(res, tree(l, m, mx, memo) + tree(m + 1, r, mx, memo) + mx[l][m] * mx[m + 1][r]);
        return memo[l][r] = res;
    }

    // ---------- Shortest Common Supersequence (LC 1092): memo the LCS length, then walk back merging ----------
    static String shortestCommonSupersequence(String a, String b) {
        int n = a.length(), m = b.length();
        Integer[][] memo = new Integer[n + 1][m + 1];
        StringBuilder sb = new StringBuilder();
        int i = n, j = m;
        while (i > 0 && j > 0) {
            if (a.charAt(i - 1) == b.charAt(j - 1)) { sb.append(a.charAt(i - 1)); i--; j--; }
            else if (lcsTo(i - 1, j, a, b, memo) >= lcsTo(i, j - 1, a, b, memo)) sb.append(a.charAt(--i));
            else sb.append(b.charAt(--j));
        }
        while (i > 0) sb.append(a.charAt(--i));
        while (j > 0) sb.append(b.charAt(--j));
        return sb.reverse().toString();
    }

    // state: (i, j) = LCS of the prefixes a[0..i) and b[0..j) | choice: match the last chars, or drop one | base: an empty prefix -> 0
    private static int lcsTo(int i, int j, String a, String b, Integer[][] memo) {
        if (i == 0 || j == 0) return 0;
        if (memo[i][j] != null) return memo[i][j];
        return memo[i][j] = a.charAt(i - 1) == b.charAt(j - 1) ? lcsTo(i - 1, j - 1, a, b, memo) + 1
                : Math.max(lcsTo(i - 1, j, a, b, memo), lcsTo(i, j - 1, a, b, memo));
    }

    // ---------- Minimum Insertion Steps to Make a String Palindrome (LC 1312): n - LPS ----------
    static int minInsertions(String s) {
        int n = s.length();
        return inserts(0, n - 1, s, new Integer[n][n]);
    }

    // state: (i, j) = substring s[i..j] | choice: ends equal -> shrink both; else insert at one end | base: i >= j -> 0
    private static int inserts(int i, int j, String s, Integer[][] memo) {
        if (i >= j) return 0;
        if (memo[i][j] != null) return memo[i][j];
        return memo[i][j] = s.charAt(i) == s.charAt(j) ? inserts(i + 1, j - 1, s, memo)
                : 1 + Math.min(inserts(i + 1, j, s, memo), inserts(i, j - 1, s, memo));
    }

    // ---------- Maximum Sum of 3 Non-Overlapping Subarrays (LC 689): memo best total over (start, windows left), then rebuild the indices ----------
    static int[] maxSumOfThreeSubarrays(int[] a, int k) {
        int n = a.length, m = n - k + 1;
        int[] w = new int[m];
        int sum = 0;
        for (int i = 0; i < n; i++) { sum += a[i]; if (i >= k) sum -= a[i - k]; if (i >= k - 1) w[i - k + 1] = sum; }
        Integer[][] memo = new Integer[m][4];
        int[] res = new int[3];
        int i = 0;
        for (int c = 3; c >= 1; c--) {
            // prefer taking window i when it ties with skipping: the earliest index wins lexicographically
            while (i + k * (c - 1) < m && w[i] + windows(i + k, c - 1, w, k, memo) < windows(i + 1, c, w, k, memo)) i++;
            res[3 - c] = i;
            i += k;
        }
        return res;
    }

    // state: (i = first allowed window start, c = windows still to place) | choice: take window i or skip it | base: c == 0 -> 0, ran out of windows -> impossible
    private static int windows(int i, int c, int[] w, int k, Integer[][] memo) {
        final int NEG = Integer.MIN_VALUE / 2;
        if (c == 0) return 0;
        if (i >= w.length) return NEG;
        if (memo[i][c] != null) return memo[i][c];
        return memo[i][c] = Math.max(Math.max(NEG, w[i] + windows(i + k, c - 1, w, k, memo)), windows(i + 1, c, w, k, memo));
    }

    // ---------- Delete and Earn (LC 740): bucket by value, then House Robber on the buckets ----------
    static int deleteAndEarn(int[] nums) {
        int max = 0;
        for (int x : nums) max = Math.max(max, x);
        int[] gain = new int[max + 1];
        for (int x : nums) gain[x] += x;
        return earn(0, gain, new Integer[max + 1]);
    }

    // state: v = smallest value still allowed | choice: take all copies of v (skips v+1) or skip v | base: past the max value -> 0
    private static int earn(int v, int[] gain, Integer[] memo) {
        if (v >= gain.length) return 0;
        if (memo[v] != null) return memo[v];
        return memo[v] = Math.max(gain[v] + earn(v + 2, gain, memo), earn(v + 1, gain, memo));
    }

    // ---------- Integer Break (LC 343) ----------
    static int integerBreak(int n) {
        return brk(n, new Integer[n + 1]);
    }

    // state: i = number to split | choice: first part j, rest stays whole or is split again | base: 1 -> 1
    private static int brk(int i, Integer[] memo) {
        if (i == 1) return 1;
        if (memo[i] != null) return memo[i];
        int res = 0;
        for (int j = 1; j < i; j++) res = Math.max(res, Math.max(j * (i - j), j * brk(i - j, memo)));
        return memo[i] = res;
    }

    // ---------- Largest Divisible Subset (LC 368): sort, LIS with the divisibility relation, rebuild by walking the memo ----------
    // Note: sorts the input array in place (LC allows it).
    static List<Integer> largestDivisibleSubset(int[] a) {
        Arrays.sort(a);
        int n = a.length, bestEnd = 0;
        LinkedList<Integer> res = new LinkedList<>();
        if (n == 0) return res;
        Integer[] memo = new Integer[n];
        for (int i = 0; i < n; i++) if (chainEnding(i, a, memo) > chainEnding(bestEnd, a, memo)) bestEnd = i;
        int i = bestEnd;
        while (i != -1) {
            res.addFirst(a[i]);
            int prev = -1;
            for (int j = 0; j < i && prev == -1; j++) if (a[i] % a[j] == 0 && chainEnding(j, a, memo) + 1 == chainEnding(i, a, memo)) prev = j;
            i = prev;
        }
        return res;
    }

    // state: i = last element of the chain (array sorted) | choice: previous element j dividing a[i] | base: no divisor before it -> 1
    private static int chainEnding(int i, int[] a, Integer[] memo) {
        if (memo[i] != null) return memo[i];
        int res = 1;
        for (int j = 0; j < i; j++) if (a[i] % a[j] == 0) res = Math.max(res, chainEnding(j, a, memo) + 1);
        return memo[i] = res;
    }

    // ---------- Longest String Chain (LC 1048): longest chain ending at a word; delete one char to find its predecessors ----------
    static int longestStrChain(String[] words) {
        Set<String> present = new HashSet<>(Arrays.asList(words));
        Map<String, Integer> memo = new HashMap<>();
        int res = 1;
        for (String w : words) res = Math.max(res, chain(w, present, memo));
        return res;
    }

    // state: w = last word of the chain | choice: delete one char to get a predecessor in the list | base: no predecessor -> 1
    private static int chain(String w, Set<String> present, Map<String, Integer> memo) {
        Integer known = memo.get(w);
        if (known != null) return known;
        int cur = 1;
        for (int i = 0; i < w.length(); i++) {
            String prev = w.substring(0, i) + w.substring(i + 1);
            if (present.contains(prev)) cur = Math.max(cur, chain(prev, present, memo) + 1);
        }
        memo.put(w, cur);
        return cur;
    }

    // ---------- Count Vowels Permutation (LC 1220) ----------
    // DEPTH: recursion n deep; at n >= 5e3 run inside a big-stack thread (see SCC_Bridges_2SAT.runWithBigStack).
    static int countVowelPermutation(int n) {
        final long MOD = 1_000_000_007L;
        Integer[][] memo = new Integer[n][5];
        long total = 0;
        for (int v = 0; v < 5; v++) total = (total + vowels(n - 1, v, memo)) % MOD;
        return (int) total;
    }

    // state: (left = letters still to append, v = last vowel a,e,i,o,u = 0..4) | choice: next vowel allowed after v | base: nothing left -> 1
    private static int vowels(int left, int v, Integer[][] memo) {
        final long MOD = 1_000_000_007L;
        int[][] next = {{1}, {0, 2}, {0, 1, 3, 4}, {2, 4}, {0}}; // a->e; e->a,i; i->a,e,o,u; o->i,u; u->a
        if (left == 0) return 1;
        if (memo[left][v] != null) return memo[left][v];
        long res = 0;
        for (int w : next[v]) res = (res + vowels(left - 1, w, memo)) % MOD;
        return memo[left][v] = (int) res;
    }

    // ---------- Student Attendance Record II (LC 552): state = (days left, absences 0..1, trailing lates 0..2) ----------
    // DEPTH: recursion n deep; at n >= 5e3 run inside a big-stack thread (see SCC_Bridges_2SAT.runWithBigStack).
    static int checkRecord(int n) {
        return records(n, 0, 0, new Integer[n + 1][2][3]);
    }

    // state: (left, a = absences so far, l = trailing lates) | choice: present, absent, late | base: no days left -> 1
    private static int records(int left, int a, int l, Integer[][][] memo) {
        final long MOD = 1_000_000_007L;
        if (left == 0) return 1;
        if (memo[left][a][l] != null) return memo[left][a][l];
        long res = records(left - 1, a, 0, memo);                          // present
        if (a == 0) res += records(left - 1, 1, 0, memo);                  // absent
        if (l < 2) res += records(left - 1, a, l + 1, memo);               // late
        return memo[left][a][l] = (int) (res % MOD);
    }
}
