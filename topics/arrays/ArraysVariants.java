import java.util.*;

public class ArraysVariants {

    // ---------- Maximum Sum Circular Subarray (LC 918): max(best linear, total - worst linear) ----------
    // Edge case: if every element is negative, total - min = 0 would mean an empty subarray, so return the linear max.
    static int maxSubarraySumCircular(int[] a) {
        int total = 0, curMax = 0, curMin = 0, best = a[0], worst = a[0];
        for (int x : a) {
            curMax = Math.max(x, curMax + x);
            curMin = Math.min(x, curMin + x);
            best = Math.max(best, curMax);
            worst = Math.min(worst, curMin);
            total += x;
        }
        return best < 0 ? best : Math.max(best, total - worst);
    }

    // ---------- Majority Element II (LC 229): Boyer-Moore with two candidates, then verify ----------
    static List<Integer> majorityElementII(int[] a) {
        int c1 = 0, c2 = 1, n1 = 0, n2 = 0;
        for (int x : a) {
            if (x == c1) n1++;
            else if (x == c2) n2++;
            else if (n1 == 0) { c1 = x; n1 = 1; }
            else if (n2 == 0) { c2 = x; n2 = 1; }
            else { n1--; n2--; }
        }
        n1 = n2 = 0;
        for (int x : a) { if (x == c1) n1++; else if (x == c2) n2++; }
        List<Integer> res = new ArrayList<>();
        if (n1 > a.length / 3) res.add(c1);
        if (n2 > a.length / 3) res.add(c2);
        return res;
    }

    // ---------- Find All Duplicates (LC 442): negate a[|x| - 1] as a visited mark (values in 1..n) ----------
    static List<Integer> findDuplicates(int[] a) {
        List<Integer> res = new ArrayList<>();
        for (int x : a) {
            int i = Math.abs(x) - 1;
            if (a[i] < 0) res.add(i + 1); else a[i] = -a[i];
        }
        return res;
    }

    // ---------- Merge-sort counting family: count pairs (i < j) with a relation, while merging ----------
    // RECOGNIZE: "count pairs i < j with a[i] vs a[j] condition" -> merge sort (or Fenwick over ranks).
    // Count of Range Sum (LC 327): pairs of prefix sums with lower <= p[j] - p[i] <= upper
    static int countRangeSum(int[] a, int lower, int upper) {
        long[] p = new long[a.length + 1];
        for (int i = 0; i < a.length; i++) p[i + 1] = p[i] + a[i];
        return sortCount(p, 0, p.length - 1, new long[p.length], lower, upper);
    }

    private static int sortCount(long[] p, int lo, int hi, long[] tmp, int lower, int upper) {
        if (lo >= hi) return 0;
        int mid = (lo + hi) >>> 1;
        int count = sortCount(p, lo, mid, tmp, lower, upper) + sortCount(p, mid + 1, hi, tmp, lower, upper);
        int l = mid + 1, r = mid + 1;
        for (int i = lo; i <= mid; i++) { // both halves are sorted: slide two windows over the right half
            while (l <= hi && p[l] - p[i] < lower) l++;
            while (r <= hi && p[r] - p[i] <= upper) r++;
            count += r - l;
        }
        int i = lo, j = mid + 1, k = lo;
        while (i <= mid && j <= hi) tmp[k++] = p[i] <= p[j] ? p[i++] : p[j++];
        while (i <= mid) tmp[k++] = p[i++];
        while (j <= hi) tmp[k++] = p[j++];
        System.arraycopy(tmp, lo, p, lo, hi - lo + 1);
        return count;
    }

    // Reverse Pairs (LC 493): i < j and a[i] > 2 * a[j]
    static int reversePairs(int[] a) { return rp(a.clone(), 0, a.length - 1, new int[a.length]); }

    private static int rp(int[] a, int lo, int hi, int[] tmp) {
        if (lo >= hi) return 0;
        int mid = (lo + hi) >>> 1;
        int count = rp(a, lo, mid, tmp) + rp(a, mid + 1, hi, tmp);
        int j = mid + 1;
        for (int i = lo; i <= mid; i++) {
            while (j <= hi && (long) a[i] > 2L * a[j]) j++;
            count += j - (mid + 1);
        }
        int i = lo, r = mid + 1, k = lo;
        while (i <= mid && r <= hi) tmp[k++] = a[i] <= a[r] ? a[i++] : a[r++];
        while (i <= mid) tmp[k++] = a[i++];
        while (r <= hi) tmp[k++] = a[r++];
        System.arraycopy(tmp, lo, a, lo, hi - lo + 1);
        return count;
    }

    // ---------- Max Chunks To Make Sorted (LC 769, permutation of 0..n-1): chunk ends where max == index ----------
    static int maxChunksToSorted(int[] a) {
        int chunks = 0, max = 0;
        for (int i = 0; i < a.length; i++) { max = Math.max(max, a[i]); if (max == i) chunks++; }
        return chunks;
    }

    // Max Chunks II (LC 768, arbitrary values): chunk boundary when prefix max <= suffix min
    static int maxChunksToSortedII(int[] a) {
        int n = a.length;
        int[] sufMin = new int[n + 1];
        sufMin[n] = Integer.MAX_VALUE;
        for (int i = n - 1; i >= 0; i--) sufMin[i] = Math.min(sufMin[i + 1], a[i]);
        int chunks = 0, max = Integer.MIN_VALUE;
        for (int i = 0; i < n; i++) { max = Math.max(max, a[i]); if (max <= sufMin[i + 1]) chunks++; }
        return chunks;
    }

    // ---------- Longest Mountain in Array (LC 845): one pass, up-run then down-run ----------
    static int longestMountain(int[] a) {
        int best = 0, i = 1, n = a.length;
        while (i < n) {
            while (i < n && a[i] == a[i - 1]) i++;
            int up = 0, down = 0;
            while (i < n && a[i] > a[i - 1]) { up++; i++; }
            while (i < n && a[i] < a[i - 1]) { down++; i++; }
            if (up > 0 && down > 0) best = Math.max(best, up + down + 1);
        }
        return best;
    }

    // ---------- Best Sightseeing Pair (LC 1014): maximize (a[i] + i) + (a[j] - j) with i < j ----------
    static int maxScoreSightseeingPair(int[] a) {
        int bestLeft = a[0], best = 0;
        for (int j = 1; j < a.length; j++) {
            best = Math.max(best, bestLeft + a[j] - j);
            bestLeft = Math.max(bestLeft, a[j] + j);
        }
        return best;
    }

    // ---------- Subarrays with Bounded Maximum (LC 795): count with max in [left, right] ----------
    static int numSubarrayBoundedMax(int[] a, int left, int right) {
        int res = 0, lastBad = -1, lastGood = -1; // lastBad: last index > right; lastGood: last index in range
        for (int i = 0; i < a.length; i++) {
            if (a[i] > right) lastBad = i;
            if (a[i] >= left) lastGood = i;
            res += Math.max(0, lastGood - lastBad);
        }
        return res;
    }

    // ---------- Shortest Unsorted Continuous Subarray (LC 581): compare with sorted, or scan both ends ----------
    static int findUnsortedSubarray(int[] a) {
        int n = a.length, end = -1, start = 0, max = a[0], min = a[n - 1];
        for (int i = 1; i < n; i++) {
            max = Math.max(max, a[i]);
            if (a[i] < max) end = i;
            min = Math.min(min, a[n - 1 - i]);
            if (a[n - 1 - i] > min) start = n - 1 - i;
        }
        return end == -1 ? 0 : end - start + 1;
    }

    // ---------- Maximum Width Ramp (LC 962): decreasing stack of left candidates, sweep right to left ----------
    static int maxWidthRamp(int[] a) {
        Deque<Integer> st = new ArrayDeque<>();
        for (int i = 0; i < a.length; i++) if (st.isEmpty() || a[st.peek()] > a[i]) st.push(i);
        int best = 0;
        for (int j = a.length - 1; j >= 0 && !st.isEmpty(); j--)
            while (!st.isEmpty() && a[st.peek()] <= a[j]) best = Math.max(best, j - st.pop());
        return best;
    }

    // ---------- Maximum Sum of Two Non-Overlapping Subarrays (LC 1031) of lengths L and M ----------
    static int maxSumTwoNoOverlap(int[] a, int L, int M) { return Math.max(twoLen(a, L, M), twoLen(a, M, L)); }

    private static int twoLen(int[] a, int first, int second) {
        int n = a.length;
        int[] p = new int[n + 1];
        for (int i = 0; i < n; i++) p[i + 1] = p[i] + a[i];
        int bestFirst = 0, best = 0;
        for (int i = first + second; i <= n; i++) {
            bestFirst = Math.max(bestFirst, p[i - second] - p[i - second - first]); // best "first" window ending before the second
            best = Math.max(best, bestFirst + p[i] - p[i - second]);
        }
        return best;
    }

    // ---------- Radix sort (LSD, base 10, non-negative ints): when n is large and the key range is bounded ----------
    static void radixSort(int[] a) {
        int max = 0;
        for (int x : a) max = Math.max(max, x);
        int[] out = new int[a.length];
        for (long exp = 1; max / exp > 0; exp *= 10) {
            int[] cnt = new int[10];
            for (int x : a) cnt[(int) (x / exp % 10)]++;
            for (int d = 1; d < 10; d++) cnt[d] += cnt[d - 1];
            for (int i = a.length - 1; i >= 0; i--) out[--cnt[(int) (a[i] / exp % 10)]] = a[i]; // backward keeps it stable
            System.arraycopy(out, 0, a, 0, a.length);
        }
    }

    // ---------- Counting sort for a small value range ----------
    static void countingSort(int[] a, int maxValue) {
        int[] cnt = new int[maxValue + 1];
        for (int x : a) cnt[x]++;
        int k = 0;
        for (int v = 0; v <= maxValue; v++) while (cnt[v]-- > 0) a[k++] = v;
    }

    // ---------- Quick sort with three-way partition (handles many duplicates) ----------
    static void quickSort3(int[] a) { qs(a, 0, a.length - 1, new Random(7)); }

    private static void qs(int[] a, int lo, int hi, Random rnd) {
        if (lo >= hi) return;
        int pivot = a[lo + rnd.nextInt(hi - lo + 1)], lt = lo, i = lo, gt = hi;
        while (i <= gt) {
            if (a[i] < pivot) swap(a, lt++, i++);
            else if (a[i] > pivot) swap(a, i, gt--);
            else i++;
        }
        qs(a, lo, lt - 1, rnd);
        qs(a, gt + 1, hi, rnd);
    }

    private static void swap(int[] a, int i, int j) { int t = a[i]; a[i] = a[j]; a[j] = t; }
}
