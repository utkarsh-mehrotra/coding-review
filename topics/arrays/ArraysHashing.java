import java.util.*;

public class ArraysHashing {

    // ---------- Two Sum (LC 1): value -> index map, one pass ----------
    static int[] twoSum(int[] a, int target) {
        Map<Integer, Integer> seen = new HashMap<>();
        for (int i = 0; i < a.length; i++) {
            Integer j = seen.get(target - a[i]);
            if (j != null) return new int[]{j, i};
            seen.put(a[i], i);
        }
        return new int[0];
    }

    // ---------- Subarray Sum Equals K (LC 560): prefix-sum frequency map ----------
    // RECOGNIZE: count/exists contiguous subarray with a sum (or sum mod, or balance) condition. Works with negatives.
    static int subarraySum(int[] a, int k) {
        Map<Integer, Integer> freq = new HashMap<>();
        freq.put(0, 1);
        int sum = 0, count = 0;
        for (int x : a) {
            sum += x;
            count += freq.getOrDefault(sum - k, 0);
            freq.merge(sum, 1, Integer::sum);
        }
        return count;
    }

    // Subarray Sums Divisible by K (LC 974): key = prefix mod k, normalized to non-negative
    static int subarraysDivByK(int[] a, int k) {
        int[] cnt = new int[k];
        cnt[0] = 1;
        int sum = 0, res = 0;
        for (int x : a) {
            sum = ((sum + x) % k + k) % k;
            res += cnt[sum]++;
        }
        return res;
    }

    // Contiguous Array (LC 525): map 0 -> -1, longest subarray with sum 0 via first-seen index
    static int findMaxLength(int[] a) {
        Map<Integer, Integer> first = new HashMap<>();
        first.put(0, -1);
        int bal = 0, best = 0;
        for (int i = 0; i < a.length; i++) {
            bal += a[i] == 1 ? 1 : -1;
            Integer j = first.putIfAbsent(bal, i);
            if (j != null) best = Math.max(best, i - j);
        }
        return best;
    }

    // ---------- Product of Array Except Self (LC 238): left pass, then right pass ----------
    static int[] productExceptSelf(int[] a) {
        int n = a.length;
        int[] res = new int[n];
        res[0] = 1;
        for (int i = 1; i < n; i++) res[i] = res[i - 1] * a[i - 1];
        int right = 1;
        for (int i = n - 1; i >= 0; i--) { res[i] *= right; right *= a[i]; }
        return res;
    }

    // ---------- Group Anagrams (LC 49): canonical key = sorted chars (or 26-count signature) ----------
    static List<List<String>> groupAnagrams(String[] strs) {
        Map<String, List<String>> g = new HashMap<>();
        for (String s : strs) {
            char[] c = s.toCharArray();
            Arrays.sort(c);
            g.computeIfAbsent(new String(c), k -> new ArrayList<>()).add(s);
        }
        return new ArrayList<>(g.values());
    }

    // ---------- Longest Consecutive Sequence (LC 128): only start counting at sequence heads ----------
    static int longestConsecutive(int[] a) {
        Set<Integer> set = new HashSet<>();
        for (int x : a) set.add(x);
        int best = 0;
        for (int x : set) {
            if (set.contains(x - 1)) continue;
            int len = 1;
            while (set.contains(x + len)) len++;
            best = Math.max(best, len);
        }
        return best;
    }

    // ---------- Top K Frequent (LC 347): bucket by frequency, O(n) ----------
    static int[] topKFrequent(int[] a, int k) {
        Map<Integer, Integer> f = new HashMap<>();
        for (int x : a) f.merge(x, 1, Integer::sum);
        List<Integer>[] bucket = new List[a.length + 1];
        for (Map.Entry<Integer, Integer> e : f.entrySet()) {
            if (bucket[e.getValue()] == null) bucket[e.getValue()] = new ArrayList<>();
            bucket[e.getValue()].add(e.getKey());
        }
        int[] res = new int[k];
        int p = 0;
        for (int c = a.length; c >= 1 && p < k; c--)
            if (bucket[c] != null) for (int x : bucket[c]) if (p < k) res[p++] = x;
        return res;
    }

    // ---------- Cyclic sort / index-as-hash: First Missing Positive (LC 41), values in 1..n go to index v-1 ----------
    static int firstMissingPositive(int[] a) {
        int n = a.length;
        for (int i = 0; i < n; i++)
            while (a[i] > 0 && a[i] <= n && a[a[i] - 1] != a[i]) {
                int t = a[a[i] - 1]; a[a[i] - 1] = a[i]; a[i] = t;
            }
        for (int i = 0; i < n; i++) if (a[i] != i + 1) return i + 1;
        return n + 1;
    }

    // Find the Duplicate Number (LC 287): Floyd cycle detection on i -> a[i]
    static int findDuplicate(int[] a) {
        int slow = a[0], fast = a[a[0]];
        while (slow != fast) { slow = a[slow]; fast = a[a[fast]]; }
        slow = 0;
        while (slow != fast) { slow = a[slow]; fast = a[fast]; }
        return slow;
    }

    // ---------- Boyer-Moore majority vote (LC 169) ----------
    static int majorityElement(int[] a) {
        int cand = 0, cnt = 0;
        for (int x : a) {
            if (cnt == 0) cand = x;
            cnt += x == cand ? 1 : -1;
        }
        return cand;
    }

    // ---------- Dutch National Flag (LC 75) ----------
    static void sortColors(int[] a) {
        int lo = 0, mid = 0, hi = a.length - 1;
        while (mid <= hi) {
            if (a[mid] == 0) swap(a, lo++, mid++);
            else if (a[mid] == 2) swap(a, mid, hi--);
            else mid++;
        }
    }

    private static void swap(int[] a, int i, int j) { int t = a[i]; a[i] = a[j]; a[j] = t; }

    // ---------- Next Permutation (LC 31) ----------
    static void nextPermutation(int[] a) {
        int i = a.length - 2;
        while (i >= 0 && a[i] >= a[i + 1]) i--;
        if (i >= 0) {
            int j = a.length - 1;
            while (a[j] <= a[i]) j--;
            swap(a, i, j);
        }
        for (int l = i + 1, r = a.length - 1; l < r; l++, r--) swap(a, l, r);
    }

    // ---------- Rotate Array (LC 189): three reversals ----------
    static void rotate(int[] a, int k) {
        int n = a.length;
        k %= n;
        rev(a, 0, n - 1); rev(a, 0, k - 1); rev(a, k, n - 1);
    }

    private static void rev(int[] a, int l, int r) { while (l < r) swap(a, l++, r--); }

    // ---------- Matrix: rotate 90 clockwise (LC 48), spiral (LC 54), set zeroes O(1) (LC 73) ----------
    static void rotateMatrix(int[][] m) {
        int n = m.length;
        for (int i = 0; i < n; i++) for (int j = i + 1; j < n; j++) { int t = m[i][j]; m[i][j] = m[j][i]; m[j][i] = t; }
        for (int[] row : m) rev(row, 0, n - 1);
    }

    static List<Integer> spiralOrder(int[][] m) {
        List<Integer> out = new ArrayList<>();
        int top = 0, bot = m.length - 1, left = 0, right = m[0].length - 1;
        while (top <= bot && left <= right) {
            for (int j = left; j <= right; j++) out.add(m[top][j]);
            top++;
            for (int i = top; i <= bot; i++) out.add(m[i][right]);
            right--;
            if (top <= bot) { for (int j = right; j >= left; j--) out.add(m[bot][j]); bot--; }
            if (left <= right) { for (int i = bot; i >= top; i--) out.add(m[i][left]); left++; }
        }
        return out;
    }

    static void setZeroes(int[][] m) {
        int R = m.length, C = m[0].length;
        boolean row0 = false, col0 = false;
        for (int j = 0; j < C; j++) if (m[0][j] == 0) row0 = true;
        for (int i = 0; i < R; i++) if (m[i][0] == 0) col0 = true;
        for (int i = 1; i < R; i++) for (int j = 1; j < C; j++) if (m[i][j] == 0) { m[i][0] = 0; m[0][j] = 0; }
        for (int i = 1; i < R; i++) for (int j = 1; j < C; j++) if (m[i][0] == 0 || m[0][j] == 0) m[i][j] = 0;
        if (row0) for (int j = 0; j < C; j++) m[0][j] = 0;
        if (col0) for (int i = 0; i < R; i++) m[i][0] = 0;
    }

    // ---------- Reservoir sampling (uniform pick from a stream of unknown length) ----------
    static int reservoirPick(int[] stream, Random rnd) {
        int pick = -1;
        for (int i = 0; i < stream.length; i++) if (rnd.nextInt(i + 1) == 0) pick = stream[i];
        return pick;
    }

    // Fisher-Yates shuffle
    static void shuffle(int[] a, Random rnd) {
        for (int i = a.length - 1; i > 0; i--) swap(a, i, rnd.nextInt(i + 1));
    }

    // ---------- Trapping Rain Water (LC 42): two pointers on the lower wall ----------
    static int trap(int[] h) {
        int l = 0, r = h.length - 1, lm = 0, rm = 0, water = 0;
        while (l < r) {
            if (h[l] < h[r]) { lm = Math.max(lm, h[l]); water += lm - h[l]; l++; }
            else { rm = Math.max(rm, h[r]); water += rm - h[r]; r--; }
        }
        return water;
    }
}
