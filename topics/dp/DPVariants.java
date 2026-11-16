import java.util.*;

public class DPVariants {
    // Framework reminder: state -> choice -> transition -> base -> order -> can the state shrink?

    // ---------- Frog Jump (LC 403): state = (stone, last jump); set of reachable jump sizes per stone ----------
    static boolean canCross(int[] stones) {
        Map<Integer, Set<Integer>> jumps = new HashMap<>();
        for (int s : stones) jumps.put(s, new HashSet<>());
        jumps.get(0).add(0);
        for (int s : stones)
            for (int k : jumps.get(s))
                for (int step = k - 1; step <= k + 1; step++)
                    if (step > 0 && jumps.containsKey(s + step)) jumps.get(s + step).add(step);
        return !jumps.get(stones[stones.length - 1]).isEmpty();
    }

    // ---------- Stone Game II (LC 1140): dp[i][M] = best stones the mover can take from piles i.. with limit M ----------
    static int stoneGameII(int[] piles) {
        int n = piles.length;
        int[] suffix = new int[n + 1];
        for (int i = n - 1; i >= 0; i--) suffix[i] = suffix[i + 1] + piles[i];
        int[][] dp = new int[n + 1][n + 1];
        for (int i = n - 1; i >= 0; i--)
            for (int m = 1; m <= n; m++) {
                if (i + 2 * m >= n) { dp[i][m] = suffix[i]; continue; } // can take everything left
                int best = 0;
                for (int x = 1; x <= 2 * m; x++) best = Math.max(best, suffix[i] - dp[i + x][Math.max(m, x)]); // total - opponent's best
                dp[i][m] = best;
            }
        return dp[0][1];
    }

    // ---------- Predict the Winner (LC 486): dp[l][r] = best score difference for the mover ----------
    static boolean predictTheWinner(int[] a) {
        int n = a.length;
        int[][] dp = new int[n][n];
        for (int i = 0; i < n; i++) dp[i][i] = a[i];
        for (int len = 2; len <= n; len++)
            for (int l = 0; l + len - 1 < n; l++) {
                int r = l + len - 1;
                dp[l][r] = Math.max(a[l] - dp[l + 1][r], a[r] - dp[l][r - 1]);
            }
        return dp[0][n - 1] >= 0;
    }

    // ---------- Super Egg Drop (LC 887): dp[m][k] = max floors checkable with m moves and k eggs ----------
    static int superEggDrop(int eggs, int floors) {
        int[] dp = new int[eggs + 1];
        int moves = 0;
        while (dp[eggs] < floors) {
            moves++;
            for (int k = eggs; k >= 1; k--) dp[k] = dp[k] + dp[k - 1] + 1; // egg breaks (k-1) + survives (k) + this floor
        }
        return moves;
    }

    // ---------- Number of Dice Rolls With Target Sum (LC 1155) ----------
    static int numRollsToTarget(int dice, int faces, int target) {
        final int MOD = 1_000_000_007;
        int[] dp = new int[target + 1];
        dp[0] = 1;
        for (int d = 1; d <= dice; d++) {
            int[] next = new int[target + 1];
            for (int s = 1; s <= target; s++)
                for (int f = 1; f <= faces && f <= s; f++) next[s] = (next[s] + dp[s - f]) % MOD;
            dp = next;
        }
        return dp[target];
    }

    // ---------- Paint House II (LC 265): keep the two smallest previous costs so each row is O(k) ----------
    static int minCostII(int[][] costs) {
        int n = costs.length, k = costs[0].length;
        int min1 = 0, min2 = 0, idx1 = -1;
        for (int i = 0; i < n; i++) {
            int nm1 = Integer.MAX_VALUE, nm2 = Integer.MAX_VALUE, ni = -1;
            for (int c = 0; c < k; c++) {
                int cost = costs[i][c] + (c == idx1 ? min2 : min1);
                if (cost < nm1) { nm2 = nm1; nm1 = cost; ni = c; }
                else if (cost < nm2) nm2 = cost;
            }
            min1 = nm1; min2 = nm2; idx1 = ni;
        }
        return min1;
    }

    // ---------- Cherry Pickup (LC 741): two walkers go down together; state = (step, r1, r2) ----------
    static int cherryPickup(int[][] grid) {
        int n = grid.length, NEG = Integer.MIN_VALUE / 2;
        int[][] dp = new int[n][n];
        for (int[] row : dp) Arrays.fill(row, NEG);
        dp[0][0] = grid[0][0];
        for (int step = 1; step <= 2 * (n - 1); step++) {
            int[][] next = new int[n][n];
            for (int[] row : next) Arrays.fill(row, NEG);
            for (int r1 = Math.max(0, step - (n - 1)); r1 <= Math.min(n - 1, step); r1++)
                for (int r2 = r1; r2 <= Math.min(n - 1, step); r2++) {
                    int c1 = step - r1, c2 = step - r2;
                    if (grid[r1][c1] == -1 || grid[r2][c2] == -1) continue;
                    int best = Math.max(Math.max(dp[r1][r2], r1 > 0 ? dp[r1 - 1][r2] : NEG), Math.max(r2 > 0 ? dp[r1][r2 - 1] : NEG, r1 > 0 && r2 > 0 ? dp[r1 - 1][r2 - 1] : NEG));
                    if (best < 0) continue;
                    int gain = grid[r1][c1] + (r1 == r2 ? 0 : grid[r2][c2]); // same cell: count once
                    next[r1][r2] = best + gain;
                }
            dp = next;
        }
        return Math.max(0, dp[n - 1][n - 1]);
    }

    // ---------- Out of Boundary Paths (LC 576): dp over moves ----------
    static int findPaths(int m, int n, int maxMove, int startRow, int startCol) {
        final int MOD = 1_000_000_007;
        int[][] dp = new int[m][n];
        dp[startRow][startCol] = 1;
        int out = 0;
        int[][] dirs = {{1, 0}, {-1, 0}, {0, 1}, {0, -1}};
        for (int move = 0; move < maxMove; move++) {
            int[][] next = new int[m][n];
            for (int r = 0; r < m; r++)
                for (int c = 0; c < n; c++) {
                    if (dp[r][c] == 0) continue;
                    for (int[] d : dirs) {
                        int nr = r + d[0], nc = c + d[1];
                        if (nr < 0 || nc < 0 || nr >= m || nc >= n) out = (out + dp[r][c]) % MOD;
                        else next[nr][nc] = (next[nr][nc] + dp[r][c]) % MOD;
                    }
                }
            dp = next;
        }
        return out;
    }

    // ---------- Domino and Tromino Tiling (LC 790): f(n) = 2 f(n-1) + f(n-3) ----------
    static int numTilings(int n) {
        final int MOD = 1_000_000_007;
        if (n <= 2) return n;
        long a = 1, b = 1, c = 2; // f(0), f(1), f(2)
        for (int i = 3; i <= n; i++) { long d = (2 * c + a) % MOD; a = b; b = c; c = d; }
        return (int) c;
    }

    // ---------- Count Different Palindromic Subsequences (LC 730): interval DP, shrink to the outermost equal characters ----------
    static int countPalindromicSubsequences(String s) {
        final int MOD = 1_000_000_007;
        int n = s.length();
        int[][] dp = new int[n][n];
        for (int i = 0; i < n; i++) dp[i][i] = 1;
        for (int len = 2; len <= n; len++)
            for (int i = 0; i + len - 1 < n; i++) {
                int j = i + len - 1;
                if (s.charAt(i) != s.charAt(j)) {
                    dp[i][j] = ((dp[i + 1][j] + dp[i][j - 1] - dp[i + 1][j - 1]) % MOD + MOD) % MOD;
                } else {
                    int lo = i + 1, hi = j - 1;
                    while (lo <= hi && s.charAt(lo) != s.charAt(i)) lo++;
                    while (lo <= hi && s.charAt(hi) != s.charAt(i)) hi--;
                    long inner = dp[i + 1][j - 1];
                    if (lo > hi) dp[i][j] = (int) ((2 * inner + 2) % MOD);          // no same char inside: a, aa, plus wrapped
                    else if (lo == hi) dp[i][j] = (int) ((2 * inner + 1) % MOD);    // exactly one inside: "a" is already counted
                    else dp[i][j] = (int) (((2 * inner - dp[lo + 1][hi - 1]) % MOD + MOD) % MOD); // remove double count
                }
            }
        return dp[0][n - 1];
    }

    // ---------- Profitable Schemes (LC 879): dp[people used][profit capped at minProfit]; capping keeps the table small ----------
    static int profitableSchemes(int n, int minProfit, int[] group, int[] profit) {
        final int MOD = 1_000_000_007;
        int[][] dp = new int[n + 1][minProfit + 1];
        dp[0][0] = 1;
        for (int k = 0; k < group.length; k++) {
            int[][] next = new int[n + 1][minProfit + 1];
            for (int people = 0; people <= n; people++)
                for (int p = 0; p <= minProfit; p++) {
                    if (dp[people][p] == 0) continue;
                    next[people][p] = (next[people][p] + dp[people][p]) % MOD;           // skip this crime
                    if (people + group[k] <= n) {
                        int np = Math.min(minProfit, p + profit[k]);                       // profit above the cap collapses into the cap
                        next[people + group[k]][np] = (next[people + group[k]][np] + dp[people][p]) % MOD;
                    }
                }
            dp = next;
        }
        int total = 0;
        for (int people = 0; people <= n; people++) total = (total + dp[people][minProfit]) % MOD;
        return total;
    }

    // ---------- Tallest Billboard (LC 956): dp[difference between the two towers] = tallest shorter tower ----------
    static int tallestBillboard(int[] rods) {
        Map<Integer, Integer> dp = new HashMap<>();
        dp.put(0, 0);
        for (int r : rods) {
            Map<Integer, Integer> next = new HashMap<>(dp);
            for (var e : dp.entrySet()) {
                int diff = e.getKey(), shorter = e.getValue();
                next.merge(diff + r, shorter, Math::max);                              // add to the taller tower
                next.merge(Math.abs(diff - r), shorter + Math.min(diff, r), Math::max); // add to the shorter tower
            }
            dp = next;
        }
        return dp.get(0);
    }

    // ---------- Best Team With No Conflicts (LC 1626): sort by (age, score), LIS on score allowing equal ----------
    static int bestTeamScore(int[] scores, int[] ages) {
        int n = scores.length;
        Integer[] idx = new Integer[n];
        for (int i = 0; i < n; i++) idx[i] = i;
        Arrays.sort(idx, (a, b) -> ages[a] != ages[b] ? ages[a] - ages[b] : scores[a] - scores[b]);
        int[] dp = new int[n];
        int best = 0;
        for (int i = 0; i < n; i++) {
            dp[i] = scores[idx[i]];
            for (int j = 0; j < i; j++) if (scores[idx[j]] <= scores[idx[i]]) dp[i] = Math.max(dp[i], dp[j] + scores[idx[i]]);
            best = Math.max(best, dp[i]);
        }
        return best;
    }

    // ---------- Remove Boxes (LC 546): dp[l][r][k] with k equal boxes attached to the left of l ----------
    static int removeBoxes(int[] boxes) {
        int n = boxes.length;
        return rb(boxes, 0, n - 1, 0, new int[n][n][n]);
    }

    private static int rb(int[] b, int l, int r, int k, int[][][] memo) {
        if (l > r) return 0;
        if (memo[l][r][k] != 0) return memo[l][r][k];
        while (l < r && b[l + 1] == b[l]) { l++; k++; } // absorb equal neighbours into k
        int best = (k + 1) * (k + 1) + rb(b, l + 1, r, 0, memo); // remove b[l] with its k friends now
        for (int m = l + 1; m <= r; m++)
            if (b[m] == b[l]) best = Math.max(best, rb(b, l + 1, m - 1, 0, memo) + rb(b, m, r, k + 1, memo)); // clear the middle first, then merge
        return memo[l][r][k] = best;
    }

    // ---------- Minimum Cost to Merge Stones (LC 1000): dp[i][j][m] = min cost to turn stones i..j into m piles ----------
    static int mergeStones(int[] stones, int k) {
        int n = stones.length;
        if ((n - 1) % (k - 1) != 0) return -1;
        int[] prefix = new int[n + 1];
        for (int i = 0; i < n; i++) prefix[i + 1] = prefix[i] + stones[i];
        int INF = Integer.MAX_VALUE / 2;
        int[][][] dp = new int[n][n][k + 1];
        for (int[][] a : dp) for (int[] b : a) Arrays.fill(b, INF);
        for (int i = 0; i < n; i++) dp[i][i][1] = 0;
        for (int len = 2; len <= n; len++)
            for (int i = 0; i + len - 1 < n; i++) {
                int j = i + len - 1;
                for (int m = 2; m <= k; m++)
                    for (int mid = i; mid < j; mid += k - 1)
                        dp[i][j][m] = Math.min(dp[i][j][m], dp[i][mid][1] + dp[mid + 1][j][m - 1]);
                dp[i][j][1] = Math.min(dp[i][j][1], dp[i][j][k] + prefix[j + 1] - prefix[i]);
            }
        return dp[0][n - 1][1];
    }

    // ---------- Minimum Cost Tree From Leaf Values (LC 1130): interval DP, or the greedy monotone stack ----------
    static int mctFromLeafValues(int[] arr) {
        Deque<Integer> st = new ArrayDeque<>();
        st.push(Integer.MAX_VALUE);
        int cost = 0;
        for (int x : arr) {
            while (st.peek() <= x) { int mid = st.pop(); cost += mid * Math.min(st.peek(), x); } // smallest leaf merges with its smaller neighbour
            st.push(x);
        }
        while (st.size() > 2) cost += st.pop() * st.peek();
        return cost;
    }

    // ---------- Shortest Common Supersequence (LC 1092): LCS table, then walk back merging ----------
    static String shortestCommonSupersequence(String a, String b) {
        int n = a.length(), m = b.length();
        int[][] dp = new int[n + 1][m + 1];
        for (int i = 1; i <= n; i++)
            for (int j = 1; j <= m; j++)
                dp[i][j] = a.charAt(i - 1) == b.charAt(j - 1) ? dp[i - 1][j - 1] + 1 : Math.max(dp[i - 1][j], dp[i][j - 1]);
        StringBuilder sb = new StringBuilder();
        int i = n, j = m;
        while (i > 0 && j > 0) {
            if (a.charAt(i - 1) == b.charAt(j - 1)) { sb.append(a.charAt(i - 1)); i--; j--; }
            else if (dp[i - 1][j] >= dp[i][j - 1]) sb.append(a.charAt(--i));
            else sb.append(b.charAt(--j));
        }
        while (i > 0) sb.append(a.charAt(--i));
        while (j > 0) sb.append(b.charAt(--j));
        return sb.reverse().toString();
    }

    // ---------- Minimum Insertion Steps to Make a String Palindrome (LC 1312): n - LPS ----------
    static int minInsertions(String s) {
        int n = s.length();
        int[][] dp = new int[n][n];
        for (int len = 2; len <= n; len++)
            for (int i = 0; i + len - 1 < n; i++) {
                int j = i + len - 1;
                dp[i][j] = s.charAt(i) == s.charAt(j) ? dp[i + 1][j - 1] : 1 + Math.min(dp[i + 1][j], dp[i][j - 1]);
            }
        return dp[0][n - 1];
    }

    // ---------- Maximum Sum of 3 Non-Overlapping Subarrays (LC 689): best-left, best-right, try every middle ----------
    static int[] maxSumOfThreeSubarrays(int[] a, int k) {
        int n = a.length, m = n - k + 1;
        int[] w = new int[m];
        int sum = 0;
        for (int i = 0; i < n; i++) { sum += a[i]; if (i >= k) sum -= a[i - k]; if (i >= k - 1) w[i - k + 1] = sum; }
        int[] left = new int[m], right = new int[m];
        int best = 0;
        for (int i = 0; i < m; i++) { if (w[i] > w[best]) best = i; left[i] = best; }
        best = m - 1;
        for (int i = m - 1; i >= 0; i--) { if (w[i] >= w[best]) best = i; right[i] = best; } // >= keeps the lexicographically smallest index
        int[] res = {-1, -1, -1};
        int top = -1;
        for (int mid = k; mid < m - k; mid++) {
            int l = left[mid - k], r = right[mid + k], total = w[l] + w[mid] + w[r];
            if (total > top) { top = total; res = new int[]{l, mid, r}; }
        }
        return res;
    }

    // ---------- Delete and Earn (LC 740): bucket by value, then House Robber on the buckets ----------
    static int deleteAndEarn(int[] nums) {
        int max = 0;
        for (int x : nums) max = Math.max(max, x);
        int[] gain = new int[max + 1];
        for (int x : nums) gain[x] += x;
        int take = 0, skip = 0;
        for (int v = 0; v <= max; v++) { int nt = skip + gain[v], ns = Math.max(skip, take); take = nt; skip = ns; }
        return Math.max(take, skip);
    }

    // ---------- Integer Break (LC 343) ----------
    static int integerBreak(int n) {
        int[] dp = new int[n + 1];
        dp[1] = 1;
        for (int i = 2; i <= n; i++)
            for (int j = 1; j < i; j++) dp[i] = Math.max(dp[i], Math.max(j * (i - j), j * dp[i - j]));
        return dp[n];
    }

    // ---------- Largest Divisible Subset (LC 368): sort, LIS with the divisibility relation, rebuild by parent ----------
    static List<Integer> largestDivisibleSubset(int[] a) {
        Arrays.sort(a);
        int n = a.length, bestEnd = 0;
        int[] len = new int[n], parent = new int[n];
        Arrays.fill(len, 1);
        Arrays.fill(parent, -1);
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < i; j++) if (a[i] % a[j] == 0 && len[j] + 1 > len[i]) { len[i] = len[j] + 1; parent[i] = j; }
            if (len[i] > len[bestEnd]) bestEnd = i;
        }
        LinkedList<Integer> res = new LinkedList<>();
        for (int i = bestEnd; i != -1; i = parent[i]) res.addFirst(a[i]);
        return res;
    }

    // ---------- Longest String Chain (LC 1048): process words by length, delete one char to find predecessors ----------
    static int longestStrChain(String[] words) {
        Arrays.sort(words, Comparator.comparingInt(String::length));
        Map<String, Integer> best = new HashMap<>();
        int res = 1;
        for (String w : words) {
            int cur = 1;
            for (int i = 0; i < w.length(); i++) {
                Integer prev = best.get(w.substring(0, i) + w.substring(i + 1));
                if (prev != null) cur = Math.max(cur, prev + 1);
            }
            best.put(w, cur);
            res = Math.max(res, cur);
        }
        return res;
    }

    // ---------- Count Vowels Permutation (LC 1220) ----------
    static int countVowelPermutation(int n) {
        final long MOD = 1_000_000_007L;
        long a = 1, e = 1, i = 1, o = 1, u = 1;
        for (int step = 1; step < n; step++) {
            long na = (e + i + u) % MOD, ne = (a + i) % MOD, ni = (e + o) % MOD, no = i % MOD, nu = (i + o) % MOD;
            a = na; e = ne; i = ni; o = no; u = nu;
        }
        return (int) ((a + e + i + o + u) % MOD);
    }

    // ---------- Student Attendance Record II (LC 552): state = (absences 0..1, trailing lates 0..2) ----------
    static int checkRecord(int n) {
        final long MOD = 1_000_000_007L;
        long[][] dp = new long[2][3];
        dp[0][0] = 1;
        for (int day = 0; day < n; day++) {
            long[][] next = new long[2][3];
            for (int a = 0; a < 2; a++)
                for (int l = 0; l < 3; l++) {
                    long v = dp[a][l];
                    if (v == 0) continue;
                    next[a][0] = (next[a][0] + v) % MOD;                     // present
                    if (a == 0) next[1][0] = (next[1][0] + v) % MOD;         // absent
                    if (l < 2) next[a][l + 1] = (next[a][l + 1] + v) % MOD;  // late
                }
            dp = next;
        }
        long total = 0;
        for (long[] row : dp) for (long v : row) total = (total + v) % MOD;
        return (int) total;
    }
}
