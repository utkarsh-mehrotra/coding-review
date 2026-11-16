import java.util.*;

public class SlidingWindowPointers {

    // ===== VARIABLE WINDOW SKELETON =====
    //   for (r...) { add a[r]; while (window invalid) { remove a[l]; l++; } update answer }
    // "longest valid"  -> update after the shrink loop.
    // "shortest valid" -> update INSIDE a while (window valid) loop that shrinks.
    // "count subarrays with property" -> atMost(k) - atMost(k - 1).

    // Longest Substring Without Repeating (LC 3)
    static int lengthOfLongestSubstring(String s) {
        int[] last = new int[128];
        Arrays.fill(last, -1);
        int l = 0, best = 0;
        for (int r = 0; r < s.length(); r++) {
            char c = s.charAt(r);
            if (last[c] >= l) l = last[c] + 1;
            last[c] = r;
            best = Math.max(best, r - l + 1);
        }
        return best;
    }

    // Longest Repeating Character Replacement (LC 424): window valid iff len - maxFreq <= k (maxFreq never shrinks)
    static int characterReplacement(String s, int k) {
        int[] cnt = new int[26];
        int l = 0, maxFreq = 0, best = 0;
        for (int r = 0; r < s.length(); r++) {
            maxFreq = Math.max(maxFreq, ++cnt[s.charAt(r) - 'A']);
            if (r - l + 1 - maxFreq > k) cnt[s.charAt(l++) - 'A']--;
            best = Math.max(best, r - l + 1);
        }
        return best;
    }

    // Minimum Window Substring (LC 76): shortest window covering all of t
    static String minWindow(String s, String t) {
        int[] need = new int[128];
        for (char c : t.toCharArray()) need[c]++;
        int missing = t.length(), l = 0, bestL = 0, bestLen = Integer.MAX_VALUE;
        for (int r = 0; r < s.length(); r++) {
            if (need[s.charAt(r)]-- > 0) missing--;
            while (missing == 0) {
                if (r - l + 1 < bestLen) { bestLen = r - l + 1; bestL = l; }
                if (++need[s.charAt(l++)] > 0) missing++;
            }
        }
        return bestLen == Integer.MAX_VALUE ? "" : s.substring(bestL, bestL + bestLen);
    }

    // Minimum Size Subarray Sum (LC 209): positives only
    static int minSubArrayLen(int target, int[] a) {
        int l = 0, sum = 0, best = Integer.MAX_VALUE;
        for (int r = 0; r < a.length; r++) {
            sum += a[r];
            while (sum >= target) { best = Math.min(best, r - l + 1); sum -= a[l++]; }
        }
        return best == Integer.MAX_VALUE ? 0 : best;
    }

    // Longest Substring with At Most K Distinct (LC 340 / 159 / fruit baskets 904)
    static int lengthOfLongestSubstringKDistinct(String s, int k) {
        Map<Character, Integer> cnt = new HashMap<>();
        int l = 0, best = 0;
        for (int r = 0; r < s.length(); r++) {
            cnt.merge(s.charAt(r), 1, Integer::sum);
            while (cnt.size() > k) {
                char c = s.charAt(l++);
                if (cnt.merge(c, -1, Integer::sum) == 0) cnt.remove(c);
            }
            best = Math.max(best, r - l + 1);
        }
        return best;
    }

    // Subarrays with exactly K distinct (LC 992) = atMost(K) - atMost(K-1)
    static int subarraysWithKDistinct(int[] a, int k) { return atMostDistinct(a, k) - atMostDistinct(a, k - 1); }

    private static int atMostDistinct(int[] a, int k) {
        if (k < 0) return 0;
        Map<Integer, Integer> cnt = new HashMap<>();
        int l = 0, res = 0;
        for (int r = 0; r < a.length; r++) {
            cnt.merge(a[r], 1, Integer::sum);
            while (cnt.size() > k) {
                if (cnt.merge(a[l], -1, Integer::sum) == 0) cnt.remove(a[l]);
                l++;
            }
            res += r - l + 1; // every subarray ending at r and starting in [l, r]
        }
        return res;
    }

    // Max Consecutive Ones III (LC 1004): at most k zeros flipped
    static int longestOnes(int[] a, int k) {
        int l = 0, zeros = 0, best = 0;
        for (int r = 0; r < a.length; r++) {
            if (a[r] == 0) zeros++;
            while (zeros > k) if (a[l++] == 0) zeros--;
            best = Math.max(best, r - l + 1);
        }
        return best;
    }

    // Permutation in String (LC 567) / Find All Anagrams (LC 438): fixed window, compare counts
    static List<Integer> findAnagrams(String s, String p) {
        List<Integer> res = new ArrayList<>();
        int m = p.length();
        if (s.length() < m) return res;
        int[] diff = new int[26];
        for (int i = 0; i < m; i++) { diff[p.charAt(i) - 'a']--; diff[s.charAt(i) - 'a']++; }
        int nonZero = 0;
        for (int d : diff) if (d != 0) nonZero++;
        if (nonZero == 0) res.add(0);
        for (int r = m; r < s.length(); r++) {
            nonZero += adjust(diff, s.charAt(r) - 'a', 1);
            nonZero += adjust(diff, s.charAt(r - m) - 'a', -1);
            if (nonZero == 0) res.add(r - m + 1);
        }
        return res;
    }

    private static int adjust(int[] d, int i, int delta) {
        int before = d[i] != 0 ? 1 : 0;
        d[i] += delta;
        return (d[i] != 0 ? 1 : 0) - before;
    }

    // Maximum Average Subarray I (LC 643): fixed window sum
    static double findMaxAverage(int[] a, int k) {
        long sum = 0, best;
        for (int i = 0; i < k; i++) sum += a[i];
        best = sum;
        for (int i = k; i < a.length; i++) { sum += a[i] - a[i - k]; best = Math.max(best, sum); }
        return (double) best / k;
    }

    // ===================== TWO POINTERS =====================

    // Two Sum II sorted (LC 167)
    static int[] twoSumSorted(int[] a, int target) {
        int l = 0, r = a.length - 1;
        while (l < r) {
            int s = a[l] + a[r];
            if (s == target) return new int[]{l + 1, r + 1};
            if (s < target) l++; else r--;
        }
        return new int[0];
    }

    // 3Sum (LC 15): sort, fix i, two pointers, skip duplicates
    static List<List<Integer>> threeSum(int[] a) {
        Arrays.sort(a);
        List<List<Integer>> res = new ArrayList<>();
        for (int i = 0; i < a.length - 2; i++) {
            if (i > 0 && a[i] == a[i - 1]) continue;
            int l = i + 1, r = a.length - 1;
            while (l < r) {
                int s = a[i] + a[l] + a[r];
                if (s < 0) l++;
                else if (s > 0) r--;
                else {
                    res.add(List.of(a[i], a[l], a[r]));
                    l++; r--;
                    while (l < r && a[l] == a[l - 1]) l++;
                    while (l < r && a[r] == a[r + 1]) r--;
                }
            }
        }
        return res;
    }

    // 3Sum Closest (LC 16)
    static int threeSumClosest(int[] a, int target) {
        Arrays.sort(a);
        int best = a[0] + a[1] + a[2];
        for (int i = 0; i < a.length - 2; i++) {
            int l = i + 1, r = a.length - 1;
            while (l < r) {
                int s = a[i] + a[l] + a[r];
                if (Math.abs(s - target) < Math.abs(best - target)) best = s;
                if (s < target) l++; else if (s > target) r--; else return s;
            }
        }
        return best;
    }

    // Container With Most Water (LC 11): move the shorter wall
    static int maxArea(int[] h) {
        int l = 0, r = h.length - 1, best = 0;
        while (l < r) {
            best = Math.max(best, Math.min(h[l], h[r]) * (r - l));
            if (h[l] < h[r]) l++; else r--;
        }
        return best;
    }

    // Remove Duplicates from Sorted Array (LC 26): read/write pointers
    static int removeDuplicates(int[] a) {
        if (a.length == 0) return 0;
        int w = 1;
        for (int r = 1; r < a.length; r++) if (a[r] != a[w - 1]) a[w++] = a[r];
        return w;
    }

    // Move Zeroes (LC 283)
    static void moveZeroes(int[] a) {
        int w = 0;
        for (int x : a) if (x != 0) a[w++] = x;
        while (w < a.length) a[w++] = 0;
    }

    // Valid Palindrome (LC 125)
    static boolean isPalindrome(String s) {
        int l = 0, r = s.length() - 1;
        while (l < r) {
            while (l < r && !Character.isLetterOrDigit(s.charAt(l))) l++;
            while (l < r && !Character.isLetterOrDigit(s.charAt(r))) r--;
            if (Character.toLowerCase(s.charAt(l++)) != Character.toLowerCase(s.charAt(r--))) return false;
        }
        return true;
    }

    // Merge sorted arrays in place from the back (LC 88)
    static void merge(int[] a, int m, int[] b, int n) {
        int i = m - 1, j = n - 1, k = m + n - 1;
        while (j >= 0) a[k--] = i >= 0 && a[i] > b[j] ? a[i--] : b[j--];
    }

    // Subarray Product Less Than K (LC 713)
    static int numSubarrayProductLessThanK(int[] a, int k) {
        if (k <= 1) return 0;
        int l = 0, res = 0;
        long prod = 1;
        for (int r = 0; r < a.length; r++) {
            prod *= a[r];
            while (prod >= k) prod /= a[l++];
            res += r - l + 1;
        }
        return res;
    }
}
