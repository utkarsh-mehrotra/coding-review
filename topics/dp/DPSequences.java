import java.util.*;

public class DPSequences {

    // ---------- LIS (LC 300) ----------
    // RECOGNIZE: longest chain where each element must exceed the previous (subsequence, order kept).
    // COST: O(n^2) time, O(n) memo (top-down over "LIS starting at i").
    // Faster: patience sorting (tails[] + binary search), O(n log n) — not DP-memo; mention as a follow-up.
    static int lengthOfLIS(int[] nums) {
        Integer[] memo = new Integer[nums.length];
        int best = 0;
        for (int i = 0; i < nums.length; i++) best = Math.max(best, lisFrom(i, nums, memo));
        return best;
    }

    // state: i = LIS starting exactly at nums[i] | choice: next element j > i with nums[j] > nums[i] | base: no valid j -> 1
    private static int lisFrom(int i, int[] nums, Integer[] memo) {
        if (memo[i] != null) return memo[i];
        int best = 1;
        for (int j = i + 1; j < nums.length; j++)
            if (nums[j] > nums[i]) best = Math.max(best, 1 + lisFrom(j, nums, memo));
        return memo[i] = best;
    }

    // Number of LIS (LC 673): carry a count next to each length
    static int findNumberOfLIS(int[] nums) {
        int n = nums.length, best = 0, res = 0;
        Integer[] len = new Integer[n], cnt = new Integer[n];
        for (int i = 0; i < n; i++) {
            int l = lisEnd(i, nums, len);
            if (l > best) { best = l; res = lisCount(i, nums, len, cnt); }
            else if (l == best) res += lisCount(i, nums, len, cnt);
        }
        return res;
    }

    // state: i = LIS length ending exactly at nums[i] | choice: previous element j < i with nums[j] < nums[i] | base: none -> 1
    private static int lisEnd(int i, int[] nums, Integer[] len) {
        if (len[i] != null) return len[i];
        int best = 1;
        for (int j = 0; j < i; j++)
            if (nums[j] < nums[i]) best = Math.max(best, 1 + lisEnd(j, nums, len));
        return len[i] = best;
    }

    // state: i = number of LIS ending at nums[i] | choice: sum counts of predecessors j that give length lisEnd(i) - 1 | base: length 1 -> 1
    private static int lisCount(int i, int[] nums, Integer[] len, Integer[] cnt) {
        if (cnt[i] != null) return cnt[i];
        int l = lisEnd(i, nums, len), total = 0;
        if (l == 1) total = 1;
        else for (int j = 0; j < i; j++)
            if (nums[j] < nums[i] && lisEnd(j, nums, len) + 1 == l) total += lisCount(j, nums, len, cnt);
        return cnt[i] = total;
    }

    // Russian Doll Envelopes (LC 354): sort width asc, height DESC, then LIS on heights
    // Faster: This memo version is O(n^2) and times out at LC 354's limits (n = 1e5); passing needs patience sorting on heights, O(n log n) — not DP-memo.
    static int maxEnvelopes(int[][] env) {
        Arrays.sort(env, (a, b) -> a[0] != b[0] ? Integer.compare(a[0], b[0]) : Integer.compare(b[1], a[1]));
        int[] h = new int[env.length];
        for (int i = 0; i < env.length; i++) h[i] = env[i][1];
        return lengthOfLIS(h);
    }

    // ---------- LCS (LC 1143) ----------
    // RECOGNIZE: two sequences, keep order, match characters. State (i, j) = suffixes a[i..], b[j..].
    static int longestCommonSubsequence(String a, String b) {
        return lcsFrom(0, 0, a, b, new Integer[a.length() + 1][b.length() + 1]);
    }

    // state: (i, j) = LCS of a[i..] and b[j..] | choice: match both, or skip a[i] / skip b[j] | base: either string used up -> 0
    private static int lcsFrom(int i, int j, String a, String b, Integer[][] memo) {
        if (i == a.length() || j == b.length()) return 0;
        if (memo[i][j] != null) return memo[i][j];
        int r = a.charAt(i) == b.charAt(j)
                ? 1 + lcsFrom(i + 1, j + 1, a, b, memo)
                : Math.max(lcsFrom(i + 1, j, a, b, memo), lcsFrom(i, j + 1, a, b, memo));
        return memo[i][j] = r;
    }

    // ---------- Edit Distance (LC 72): insert / delete / replace ----------
    static int minDistance(String a, String b) {
        return editFrom(0, 0, a, b, new Integer[a.length() + 1][b.length() + 1]);
    }

    // state: (i, j) = edits to turn a[i..] into b[j..] | choice: match free, else 1 + replace/delete/insert | base: one side empty -> remaining length of the other
    private static int editFrom(int i, int j, String a, String b, Integer[][] memo) {
        if (i == a.length()) return b.length() - j;
        if (j == b.length()) return a.length() - i;
        if (memo[i][j] != null) return memo[i][j];
        int r = a.charAt(i) == b.charAt(j)
                ? editFrom(i + 1, j + 1, a, b, memo)
                : 1 + Math.min(editFrom(i + 1, j + 1, a, b, memo), Math.min(editFrom(i + 1, j, a, b, memo), editFrom(i, j + 1, a, b, memo)));
        return memo[i][j] = r;
    }

    // ---------- Palindromes ----------
    // Longest Palindromic Subsequence (LC 516): interval state (i, j)
    static int longestPalindromeSubseq(String s) {
        int n = s.length();
        return n == 0 ? 0 : palSubseq(0, n - 1, s, new Integer[n][n]);
    }

    // state: (i, j) = longest palindromic subsequence of s[i..j] | choice: ends match -> 2 + inner, else drop one end | base: i == j -> 1, i > j -> 0
    private static int palSubseq(int i, int j, String s, Integer[][] memo) {
        if (i > j) return 0;
        if (i == j) return 1;
        if (memo[i][j] != null) return memo[i][j];
        int r = s.charAt(i) == s.charAt(j)
                ? palSubseq(i + 1, j - 1, s, memo) + 2
                : Math.max(palSubseq(i + 1, j, s, memo), palSubseq(i, j - 1, s, memo));
        return memo[i][j] = r;
    }

    // state: (i, j) = is s[i..j] a palindrome | choice: ends equal and inner is a palindrome | base: i >= j -> true
    private static boolean isPal(int i, int j, String s, Boolean[][] memo) {
        if (i >= j) return true;
        if (memo[i][j] != null) return memo[i][j];
        return memo[i][j] = s.charAt(i) == s.charAt(j) && isPal(i + 1, j - 1, s, memo);
    }

    // Longest Palindromic Substring (LC 5): memo isPal(i, j), scan every (i, j), O(n^2) time and memo
    // This is the memo isPal version; expand-around-center (O(n^2) time, O(1) space) is the follow-up, not DP-memo.
    static String longestPalindrome(String s) {
        int n = s.length(), bs = 0, be = 0;
        Boolean[][] memo = new Boolean[n][n];
        for (int i = 0; i < n; i++)
            for (int j = i; j < n; j++)
                if (j + 1 - i > be - bs && isPal(i, j, s, memo)) { bs = i; be = j + 1; }
        return s.substring(bs, be);
    }

    // Palindromic Substrings (LC 647): count every (i, j) where memo isPal holds
    static int countSubstrings(String s) {
        int n = s.length(), count = 0;
        Boolean[][] memo = new Boolean[n][n];
        for (int i = 0; i < n; i++)
            for (int j = i; j < n; j++)
                if (isPal(i, j, s, memo)) count++;
        return count;
    }

    // ---------- Distinct Subsequences (LC 115): ways s[i..] contains t[j..] ----------
    static int numDistinct(String s, String t) {
        return waysFormed(0, 0, s, t, new Integer[s.length() + 1][t.length() + 1]);
    }

    // state: (i, j) = ways to form t[j..] from s[i..] | choice: skip s[i], or use it when s[i] == t[j] | base: j == t.length -> 1, i == s.length -> 0
    private static int waysFormed(int i, int j, String s, String t, Integer[][] memo) {
        if (j == t.length()) return 1;
        if (i == s.length()) return 0;
        if (memo[i][j] != null) return memo[i][j];
        int r = waysFormed(i + 1, j, s, t, memo);
        if (s.charAt(i) == t.charAt(j)) r += waysFormed(i + 1, j + 1, s, t, memo);
        return memo[i][j] = r;
    }

    // ---------- Word Break (LC 139): can suffix s[i..] be segmented ----------
    static boolean wordBreak(String s, List<String> dict) {
        Set<String> set = new HashSet<>(dict);
        int maxLen = 0;
        for (String w : dict) maxLen = Math.max(maxLen, w.length());
        return breakFrom(0, s, set, maxLen, new Boolean[s.length() + 1]);
    }

    // state: i = s[i..] can be split into dictionary words | choice: next word s[i..j) in the set, then recurse on j | base: i == s.length -> true
    private static boolean breakFrom(int i, String s, Set<String> set, int maxLen, Boolean[] memo) {
        if (i == s.length()) return true;
        if (memo[i] != null) return memo[i];
        boolean ok = false;
        for (int j = i + 1; j <= Math.min(s.length(), i + maxLen) && !ok; j++)
            if (set.contains(s.substring(i, j)) && breakFrom(j, s, set, maxLen, memo)) ok = true;
        return memo[i] = ok;
    }

    // ---------- Wildcard Matching (LC 44): '?' one char, '*' any sequence ----------
    static boolean isMatchWildcard(String s, String p) {
        return wildFrom(0, 0, s, p, new Boolean[s.length() + 1][p.length() + 1]);
    }

    // state: (i, j) = s[i..] matches p[j..] | choice: '*' eats nothing or one char; '?'/equal char eats one | base: p used up -> s used up
    private static boolean wildFrom(int i, int j, String s, String p, Boolean[][] memo) {
        if (j == p.length()) return i == s.length();
        if (memo[i][j] != null) return memo[i][j];
        char pc = p.charAt(j);
        boolean r;
        if (pc == '*') r = wildFrom(i, j + 1, s, p, memo) || (i < s.length() && wildFrom(i + 1, j, s, p, memo));
        else r = i < s.length() && (pc == '?' || pc == s.charAt(i)) && wildFrom(i + 1, j + 1, s, p, memo);
        return memo[i][j] = r;
    }

    // ---------- Regular Expression Matching (LC 10): '.' any char, 'x*' zero or more x ----------
    static boolean isMatchRegex(String s, String p) {
        return regexFrom(0, 0, s, p, new Boolean[s.length() + 1][p.length() + 1]);
    }

    // state: (i, j) = s[i..] matches p[j..] | choice: 'x*' takes zero copies or one more copy; plain char/'.' eats one | base: p used up -> s used up
    private static boolean regexFrom(int i, int j, String s, String p, Boolean[][] memo) {
        if (j == p.length()) return i == s.length();
        if (memo[i][j] != null) return memo[i][j];
        boolean first = i < s.length() && (p.charAt(j) == '.' || p.charAt(j) == s.charAt(i));
        boolean r;
        if (j + 1 < p.length() && p.charAt(j + 1) == '*')
            r = regexFrom(i, j + 2, s, p, memo) || (first && regexFrom(i + 1, j, s, p, memo));
        else r = first && regexFrom(i + 1, j + 1, s, p, memo);
        return memo[i][j] = r;
    }

    // ---------- Interleaving String (LC 97) ----------
    static boolean isInterleave(String a, String b, String c) {
        if (a.length() + b.length() != c.length()) return false;
        return interleaveFrom(0, 0, a, b, c, new Boolean[a.length() + 1][b.length() + 1]);
    }

    // state: (i, j) = a[i..] and b[j..] interleave into c[i+j..] | choice: next char of c comes from a or from b | base: both used up -> true
    private static boolean interleaveFrom(int i, int j, String a, String b, String c, Boolean[][] memo) {
        if (i == a.length() && j == b.length()) return true;
        if (memo[i][j] != null) return memo[i][j];
        char ch = c.charAt(i + j);
        boolean r = (i < a.length() && a.charAt(i) == ch && interleaveFrom(i + 1, j, a, b, c, memo))
                || (j < b.length() && b.charAt(j) == ch && interleaveFrom(i, j + 1, a, b, c, memo));
        return memo[i][j] = r;
    }

    // ---------- Longest Common SUBSTRING-style: Maximum Length of Repeated Subarray (LC 718) ----------
    static int findLength(int[] a, int[] b) {
        Integer[][] memo = new Integer[a.length + 1][b.length + 1];
        int best = 0;
        for (int i = 0; i < a.length; i++)
            for (int j = 0; j < b.length; j++) best = Math.max(best, commonRun(i, j, a, b, memo));
        return best;
    }

    // state: (i, j) = length of the common run starting at a[i], b[j] | choice: contiguous, so extend only on a match | base: mismatch or end -> 0
    private static int commonRun(int i, int j, int[] a, int[] b, Integer[][] memo) {
        if (i == a.length || j == b.length || a[i] != b[j]) return 0;
        if (memo[i][j] != null) return memo[i][j];
        return memo[i][j] = 1 + commonRun(i + 1, j + 1, a, b, memo);
    }
}
