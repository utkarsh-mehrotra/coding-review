import java.util.*;

public class DPSequences {

    // ---------- LIS in O(n log n) (LC 300) ----------
    // RECOGNIZE: longest chain where each element must exceed the previous (subsequence, order kept).
    // tails[i] = smallest possible tail of an increasing subsequence of length i + 1.
    static int lengthOfLIS(int[] nums) {
        int[] tails = new int[nums.length];
        int size = 0;
        for (int x : nums) {
            int lo = 0, hi = size;
            while (lo < hi) {
                int mid = (lo + hi) >>> 1;
                if (tails[mid] < x) lo = mid + 1; else hi = mid;
            }
            tails[lo] = x;
            if (lo == size) size++;
        }
        return size;
    }

    // O(n^2) reference form: dp[i] = longest increasing subsequence ending at i
    static int lengthOfLISQuadratic(int[] nums) {
        int best = 0;
        int[] dp = new int[nums.length];
        for (int i = 0; i < nums.length; i++) {
            dp[i] = 1;
            for (int j = 0; j < i; j++) if (nums[j] < nums[i]) dp[i] = Math.max(dp[i], dp[j] + 1);
            best = Math.max(best, dp[i]);
        }
        return best;
    }

    // Number of LIS (LC 673): carry a count next to each length
    static int findNumberOfLIS(int[] nums) {
        int n = nums.length, best = 0, res = 0;
        int[] len = new int[n], cnt = new int[n];
        for (int i = 0; i < n; i++) {
            len[i] = 1;
            cnt[i] = 1;
            for (int j = 0; j < i; j++) {
                if (nums[j] >= nums[i]) continue;
                if (len[j] + 1 > len[i]) { len[i] = len[j] + 1; cnt[i] = cnt[j]; }
                else if (len[j] + 1 == len[i]) cnt[i] += cnt[j];
            }
            if (len[i] > best) { best = len[i]; res = cnt[i]; }
            else if (len[i] == best) res += cnt[i];
        }
        return res;
    }

    // Russian Doll Envelopes (LC 354): sort width asc, height DESC, then LIS on heights
    static int maxEnvelopes(int[][] env) {
        Arrays.sort(env, (a, b) -> a[0] != b[0] ? Integer.compare(a[0], b[0]) : Integer.compare(b[1], a[1]));
        int[] h = new int[env.length];
        for (int i = 0; i < env.length; i++) h[i] = env[i][1];
        return lengthOfLIS(h);
    }

    // ---------- LCS (LC 1143) ----------
    // RECOGNIZE: two sequences, keep order, match characters. dp[i][j] over prefixes.
    static int longestCommonSubsequence(String a, String b) {
        int n = a.length(), m = b.length();
        int[][] dp = new int[n + 1][m + 1];
        for (int i = 1; i <= n; i++)
            for (int j = 1; j <= m; j++)
                dp[i][j] = a.charAt(i - 1) == b.charAt(j - 1) ? dp[i - 1][j - 1] + 1 : Math.max(dp[i - 1][j], dp[i][j - 1]);
        return dp[n][m];
    }

    // ---------- Edit Distance (LC 72): insert / delete / replace ----------
    static int minDistance(String a, String b) {
        int n = a.length(), m = b.length();
        int[][] dp = new int[n + 1][m + 1];
        for (int i = 0; i <= n; i++) dp[i][0] = i;
        for (int j = 0; j <= m; j++) dp[0][j] = j;
        for (int i = 1; i <= n; i++)
            for (int j = 1; j <= m; j++)
                dp[i][j] = a.charAt(i - 1) == b.charAt(j - 1)
                        ? dp[i - 1][j - 1]
                        : 1 + Math.min(dp[i - 1][j - 1], Math.min(dp[i - 1][j], dp[i][j - 1]));
        return dp[n][m];
    }

    // ---------- Palindromes ----------
    // Longest Palindromic Subsequence (LC 516): interval dp[i][j]
    static int longestPalindromeSubseq(String s) {
        int n = s.length();
        int[][] dp = new int[n][n];
        for (int i = n - 1; i >= 0; i--) {
            dp[i][i] = 1;
            for (int j = i + 1; j < n; j++)
                dp[i][j] = s.charAt(i) == s.charAt(j) ? dp[i + 1][j - 1] + 2 : Math.max(dp[i + 1][j], dp[i][j - 1]);
        }
        return n == 0 ? 0 : dp[0][n - 1];
    }

    // Longest Palindromic Substring (LC 5): expand around every center, O(n^2) time, O(1) space
    static String longestPalindrome(String s) {
        int bs = 0, be = 0;
        for (int c = 0; c < s.length(); c++) {
            for (int w = 0; w < 2; w++) { // w = 0 odd length, w = 1 even length
                int l = c, r = c + w;
                while (l >= 0 && r < s.length() && s.charAt(l) == s.charAt(r)) { l--; r++; }
                if (r - l - 1 > be - bs) { bs = l + 1; be = r; }
            }
        }
        return s.substring(bs, be);
    }

    // Palindromic Substrings (LC 647)
    static int countSubstrings(String s) {
        int count = 0;
        for (int c = 0; c < s.length(); c++) {
            for (int w = 0; w < 2; w++) {
                int l = c, r = c + w;
                while (l >= 0 && r < s.length() && s.charAt(l) == s.charAt(r)) { count++; l--; r++; }
            }
        }
        return count;
    }

    // ---------- Distinct Subsequences (LC 115): dp[j] = ways to form t[0..j) ----------
    static int numDistinct(String s, String t) {
        int[] dp = new int[t.length() + 1];
        dp[0] = 1;
        for (char c : s.toCharArray())
            for (int j = t.length(); j >= 1; j--)
                if (c == t.charAt(j - 1)) dp[j] += dp[j - 1];
        return dp[t.length()];
    }

    // ---------- Word Break (LC 139): dp[i] = prefix of length i is breakable ----------
    static boolean wordBreak(String s, List<String> dict) {
        Set<String> set = new HashSet<>(dict);
        int maxLen = 0;
        for (String w : dict) maxLen = Math.max(maxLen, w.length());
        boolean[] dp = new boolean[s.length() + 1];
        dp[0] = true;
        for (int i = 1; i <= s.length(); i++) {
            for (int j = Math.max(0, i - maxLen); j < i; j++) {
                if (dp[j] && set.contains(s.substring(j, i))) { dp[i] = true; break; }
            }
        }
        return dp[s.length()];
    }

    // ---------- Wildcard Matching (LC 44): '?' one char, '*' any sequence ----------
    static boolean isMatchWildcard(String s, String p) {
        int n = s.length(), m = p.length();
        boolean[][] dp = new boolean[n + 1][m + 1];
        dp[0][0] = true;
        for (int j = 1; j <= m; j++) if (p.charAt(j - 1) == '*') dp[0][j] = dp[0][j - 1];
        for (int i = 1; i <= n; i++) {
            for (int j = 1; j <= m; j++) {
                char pc = p.charAt(j - 1);
                if (pc == '*') dp[i][j] = dp[i][j - 1] || dp[i - 1][j];
                else if (pc == '?' || pc == s.charAt(i - 1)) dp[i][j] = dp[i - 1][j - 1];
            }
        }
        return dp[n][m];
    }

    // ---------- Regular Expression Matching (LC 10): '.' any char, 'x*' zero or more x ----------
    static boolean isMatchRegex(String s, String p) {
        int n = s.length(), m = p.length();
        boolean[][] dp = new boolean[n + 1][m + 1];
        dp[0][0] = true;
        for (int j = 2; j <= m; j++) if (p.charAt(j - 1) == '*') dp[0][j] = dp[0][j - 2];
        for (int i = 1; i <= n; i++) {
            for (int j = 1; j <= m; j++) {
                char pc = p.charAt(j - 1);
                if (pc == '*') {
                    dp[i][j] = dp[i][j - 2]; // zero copies of the preceding element
                    char prev = p.charAt(j - 2);
                    if (prev == '.' || prev == s.charAt(i - 1)) dp[i][j] |= dp[i - 1][j]; // one more copy
                } else if (pc == '.' || pc == s.charAt(i - 1)) {
                    dp[i][j] = dp[i - 1][j - 1];
                }
            }
        }
        return dp[n][m];
    }

    // ---------- Interleaving String (LC 97) ----------
    static boolean isInterleave(String a, String b, String c) {
        int n = a.length(), m = b.length();
        if (n + m != c.length()) return false;
        boolean[] dp = new boolean[m + 1];
        for (int i = 0; i <= n; i++) {
            for (int j = 0; j <= m; j++) {
                if (i == 0 && j == 0) { dp[j] = true; continue; }
                boolean fromA = i > 0 && dp[j] && a.charAt(i - 1) == c.charAt(i + j - 1);
                boolean fromB = j > 0 && dp[j - 1] && b.charAt(j - 1) == c.charAt(i + j - 1);
                dp[j] = fromA || fromB;
            }
        }
        return dp[m];
    }

    // ---------- Longest Common SUBSTRING-style: Maximum Length of Repeated Subarray (LC 718) ----------
    static int findLength(int[] a, int[] b) {
        int[] dp = new int[b.length + 1];
        int best = 0;
        for (int i = 1; i <= a.length; i++) {
            for (int j = b.length; j >= 1; j--) {
                dp[j] = a[i - 1] == b[j - 1] ? dp[j - 1] + 1 : 0; // contiguous: reset on mismatch
                best = Math.max(best, dp[j]);
            }
        }
        return best;
    }
}
