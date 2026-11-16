import java.util.*;

public class WindowsVariants {

    // ---------- Minimum Window Subsequence (LC 727): shortest window of s containing t as a SUBSEQUENCE ----------
    // Greedy forward to match all of t, then backward to tighten the left edge, then continue just after that left edge.
    static String minWindowSubsequence(String s, String t) {
        int n = s.length(), m = t.length(), bestLen = Integer.MAX_VALUE, bestStart = -1, i = 0;
        while (i < n) {
            int j = 0, k = i;
            while (k < n && j < m) { if (s.charAt(k) == t.charAt(j)) j++; k++; }
            if (j < m) break; // t no longer fits
            int end = k; // exclusive
            j = m - 1;
            k--;
            while (j >= 0) { if (s.charAt(k) == t.charAt(j)) j--; k--; }
            k++;
            if (end - k < bestLen) { bestLen = end - k; bestStart = k; }
            i = k + 1;
        }
        return bestStart == -1 ? "" : s.substring(bestStart, bestStart + bestLen);
    }

    // ---------- Substring with Concatenation of All Words (LC 30): window over word-sized steps, one pass per offset ----------
    static List<Integer> findSubstring(String s, String[] words) {
        List<Integer> res = new ArrayList<>();
        int w = words[0].length(), cnt = words.length, n = s.length();
        Map<String, Integer> need = new HashMap<>();
        for (String x : words) need.merge(x, 1, Integer::sum);
        for (int off = 0; off < w; off++) {
            Map<String, Integer> have = new HashMap<>();
            int l = off, matched = 0;
            for (int r = off; r + w <= n; r += w) {
                String word = s.substring(r, r + w);
                if (!need.containsKey(word)) { have.clear(); matched = 0; l = r + w; continue; }
                have.merge(word, 1, Integer::sum);
                matched++;
                while (have.get(word) > need.get(word)) { // too many of this word: shrink from the left
                    have.merge(s.substring(l, l + w), -1, Integer::sum);
                    l += w;
                    matched--;
                }
                if (matched == cnt) res.add(l);
            }
        }
        return res;
    }

    // ---------- Longest Substring with At Least K Repeating Characters (LC 395): fix the number of unique chars (1..26) ----------
    static int longestSubstringAtLeastK(String s, int k) {
        int best = 0;
        for (int uniq = 1; uniq <= 26; uniq++) {
            int[] cnt = new int[26];
            int l = 0, unique = 0, atLeastK = 0;
            for (int r = 0; r < s.length(); r++) {
                if (cnt[s.charAt(r) - 'a']++ == 0) unique++;
                if (cnt[s.charAt(r) - 'a'] == k) atLeastK++;
                while (unique > uniq) {
                    if (cnt[s.charAt(l) - 'a'] == k) atLeastK--;
                    if (--cnt[s.charAt(l) - 'a'] == 0) unique--;
                    l++;
                }
                if (unique == uniq && unique == atLeastK) best = Math.max(best, r - l + 1);
            }
        }
        return best;
    }

    // ---------- Maximum Points You Can Obtain from Cards (LC 1423): take from the ends = leave a window of n - k ----------
    static int maxScoreCards(int[] a, int k) {
        int n = a.length, total = 0, window = 0;
        for (int x : a) total += x;
        int w = n - k;
        for (int i = 0; i < w; i++) window += a[i];
        int minWindow = window;
        for (int i = w; i < n; i++) { window += a[i] - a[i - w]; minWindow = Math.min(minWindow, window); }
        return total - minWindow;
    }

    // ---------- Count Number of Nice Subarrays (LC 1248): exactly k odds = atMost(k) - atMost(k - 1) ----------
    static int numberOfSubarrays(int[] a, int k) { return atMostOdd(a, k) - atMostOdd(a, k - 1); }

    private static int atMostOdd(int[] a, int k) {
        if (k < 0) return 0;
        int l = 0, odds = 0, res = 0;
        for (int r = 0; r < a.length; r++) {
            odds += a[r] & 1;
            while (odds > k) odds -= a[l++] & 1;
            res += r - l + 1;
        }
        return res;
    }

    // ---------- Minimum Operations to Reduce X to Zero (LC 1658): longest middle window with sum = total - x ----------
    static int minOperationsToZero(int[] a, int x) {
        long total = 0;
        for (int v : a) total += v;
        long target = total - x;
        if (target < 0) return -1;
        int l = 0, bestLen = -1;
        long sum = 0;
        for (int r = 0; r < a.length; r++) {
            sum += a[r];
            while (sum > target && l <= r) sum -= a[l++];
            if (sum == target) bestLen = Math.max(bestLen, r - l + 1);
        }
        return bestLen == -1 ? -1 : a.length - bestLen;
    }

    // ---------- Longest Turbulent Subarray (LC 978) ----------
    static int maxTurbulenceSize(int[] a) {
        int best = 1, up = 1, down = 1;
        for (int i = 1; i < a.length; i++) {
            if (a[i] > a[i - 1]) { up = down + 1; down = 1; }
            else if (a[i] < a[i - 1]) { down = up + 1; up = 1; }
            else { up = down = 1; }
            best = Math.max(best, Math.max(up, down));
        }
        return best;
    }

    // ---------- Contains Duplicate III (LC 220): window of the last k values in a TreeSet, floor / ceiling probe ----------
    static boolean containsNearbyAlmostDuplicate(int[] a, int indexDiff, int valueDiff) {
        TreeSet<Long> window = new TreeSet<>();
        for (int i = 0; i < a.length; i++) {
            long x = a[i];
            Long f = window.floor(x + valueDiff);
            if (f != null && f >= x - valueDiff) return true;
            window.add(x);
            if (i >= indexDiff) window.remove((long) a[i - indexDiff]);
        }
        return false;
    }

    // ---------- kSum generalization (LC 18 / 4Sum): sort, recurse down to the two-pointer base case ----------
    static List<List<Integer>> fourSum(int[] a, int target) {
        Arrays.sort(a);
        return kSum(a, target, 0, 4);
    }

    private static List<List<Integer>> kSum(int[] a, long target, int start, int k) {
        List<List<Integer>> res = new ArrayList<>();
        if (start == a.length) return res;
        long avg = target / k;
        if (a[start] > avg || a[a.length - 1] < avg) return res; // prune: sorted extremes cannot reach the average
        if (k == 2) {
            int l = start, r = a.length - 1;
            while (l < r) {
                long s = (long) a[l] + a[r];
                if (s < target || (l > start && a[l] == a[l - 1])) l++;
                else if (s > target || (r < a.length - 1 && a[r] == a[r + 1])) r--;
                else res.add(new ArrayList<>(List.of(a[l++], a[r--])));
            }
            return res;
        }
        for (int i = start; i < a.length; i++) {
            if (i > start && a[i] == a[i - 1]) continue;
            for (List<Integer> sub : kSum(a, target - a[i], i + 1, k - 1)) {
                sub.add(0, a[i]);
                res.add(sub);
            }
        }
        return res;
    }

    // ---------- Number of Substrings Containing All Three Characters (LC 1358): for each right end, count valid lefts ----------
    static int numberOfSubstrings(String s) {
        int[] last = {-1, -1, -1};
        int res = 0;
        for (int i = 0; i < s.length(); i++) {
            last[s.charAt(i) - 'a'] = i;
            res += 1 + Math.min(last[0], Math.min(last[1], last[2])); // lefts 0..min(last) all contain a, b and c
        }
        return res;
    }

    // ---------- Maximum Number of Vowels in a Substring of Length k (LC 1456): fixed window ----------
    static int maxVowels(String s, int k) {
        int cur = 0, best = 0;
        for (int i = 0; i < s.length(); i++) {
            if ("aeiou".indexOf(s.charAt(i)) >= 0) cur++;
            if (i >= k && "aeiou".indexOf(s.charAt(i - k)) >= 0) cur--;
            if (i >= k - 1) best = Math.max(best, cur);
        }
        return best;
    }

    // ---------- Frequency of the Most Frequent Element (LC 1838): sort, window where cost to raise all to a[r] <= k ----------
    static int maxFrequency(int[] a, int k) {
        Arrays.sort(a);
        long sum = 0;
        int l = 0, best = 1;
        for (int r = 0; r < a.length; r++) {
            sum += a[r];
            while ((long) a[r] * (r - l + 1) - sum > k) sum -= a[l++];
            best = Math.max(best, r - l + 1);
        }
        return best;
    }
}
