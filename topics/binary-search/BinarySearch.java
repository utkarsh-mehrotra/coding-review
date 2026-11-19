import java.util.*;

public class BinarySearch {

    // ===== THE ONE TEMPLATE: find the FIRST index in [lo, hi] where ok(i) is true (ok is monotone F..F T..T) =====
    //   lo = 0, hi = n (n means "none true"); while (lo < hi) { mid = lo + (hi - lo) / 2; if (ok(mid)) hi = mid; else lo = mid + 1; }
    //   Every problem = choose the search space + write ok(). "Last true" = (first true of the negation) - 1.

    // lower bound: first index with a[i] >= x
    static int lowerBound(int[] a, int x) {
        int lo = 0, hi = a.length;
        while (lo < hi) {
            int mid = lo + (hi - lo) / 2;
            if (a[mid] >= x) hi = mid; else lo = mid + 1;
        }
        return lo;
    }

    // upper bound: first index with a[i] > x
    static int upperBound(int[] a, int x) {
        int lo = 0, hi = a.length;
        while (lo < hi) {
            int mid = lo + (hi - lo) / 2;
            if (a[mid] > x) hi = mid; else lo = mid + 1;
        }
        return lo;
    }

    // Find First and Last Position (LC 34)
    static int[] searchRange(int[] a, int target) {
        int l = lowerBound(a, target);
        if (l == a.length || a[l] != target) return new int[]{-1, -1};
        return new int[]{l, upperBound(a, target) - 1};
    }

    // Search Insert Position (LC 35)
    static int searchInsert(int[] a, int target) { return lowerBound(a, target); }

    // ---------- Rotated sorted array (LC 33, distinct): one half is always sorted ----------
    static int searchRotated(int[] a, int t) {
        int lo = 0, hi = a.length - 1;
        while (lo <= hi) {
            int mid = lo + (hi - lo) / 2;
            if (a[mid] == t) return mid;
            if (a[lo] <= a[mid]) { // left half sorted
                if (a[lo] <= t && t < a[mid]) hi = mid - 1; else lo = mid + 1;
            } else {
                if (a[mid] < t && t <= a[hi]) lo = mid + 1; else hi = mid - 1;
            }
        }
        return -1;
    }

    // Find Minimum in Rotated Sorted Array (LC 153); duplicates (LC 154) shrink hi on ties
    static int findMin(int[] a) {
        int lo = 0, hi = a.length - 1;
        while (lo < hi) {
            int mid = lo + (hi - lo) / 2;
            if (a[mid] > a[hi]) lo = mid + 1;
            else if (a[mid] < a[hi]) hi = mid;
            else hi--; // equal: cannot tell, shrink safely
        }
        return a[lo];
    }

    // Peak Element (LC 162): follow the uphill side
    static int findPeakElement(int[] a) {
        int lo = 0, hi = a.length - 1;
        while (lo < hi) {
            int mid = lo + (hi - lo) / 2;
            if (a[mid] < a[mid + 1]) lo = mid + 1; else hi = mid;
        }
        return lo;
    }

    // Search a 2D Matrix (LC 74): treat as a flat sorted array
    static boolean searchMatrix(int[][] m, int t) {
        int R = m.length, C = m[0].length, lo = 0, hi = R * C - 1;
        while (lo <= hi) {
            int mid = lo + (hi - lo) / 2, v = m[mid / C][mid % C];
            if (v == t) return true;
            if (v < t) lo = mid + 1; else hi = mid - 1;
        }
        return false;
    }

    // Search a 2D Matrix II (LC 240): staircase from the top-right, O(m + n)
    static boolean searchMatrixII(int[][] m, int t) {
        int r = 0, c = m[0].length - 1;
        while (r < m.length && c >= 0) {
            if (m[r][c] == t) return true;
            if (m[r][c] > t) c--; else r++;
        }
        return false;
    }

    // ===== BINARY SEARCH ON THE ANSWER: ok(x) = "is x enough / feasible?" =====
    // Koko Eating Bananas (LC 875): min speed finishing in h hours
    static int minEatingSpeed(int[] piles, int h) {
        int lo = 1, hi = Arrays.stream(piles).max().getAsInt();
        while (lo < hi) {
            int mid = lo + (hi - lo) / 2;
            long hours = 0;
            for (int p : piles) hours += (p + mid - 1) / mid;
            if (hours <= h) hi = mid; else lo = mid + 1;
        }
        return lo;
    }

    // Capacity To Ship Packages Within D Days (LC 1011)
    static int shipWithinDays(int[] w, int days) {
        int lo = 0, hi = 0;
        for (int x : w) { lo = Math.max(lo, x); hi += x; }
        while (lo < hi) {
            int mid = lo + (hi - lo) / 2, need = 1, cur = 0;
            for (int x : w) { if (cur + x > mid) { need++; cur = 0; } cur += x; }
            if (need <= days) hi = mid; else lo = mid + 1;
        }
        return lo;
    }

    // Split Array Largest Sum (LC 410): same shape as shipping
    static int splitArray(int[] a, int k) { return shipWithinDays(a, k); }

    // Minimum Days to Make m Bouquets (LC 1482)
    static int minDays(int[] bloom, int m, int k) {
        if ((long) m * k > bloom.length) return -1;
        int lo = 1, hi = Arrays.stream(bloom).max().getAsInt();
        while (lo < hi) {
            int mid = lo + (hi - lo) / 2, bouquets = 0, run = 0;
            for (int b : bloom) {
                if (b <= mid) { if (++run == k) { bouquets++; run = 0; } } else run = 0;
            }
            if (bouquets >= m) hi = mid; else lo = mid + 1;
        }
        return lo;
    }

    // Kth Smallest in Multiplication Table (LC 668) / count-less-or-equal style
    static int findKthNumber(int m, int n, int k) {
        int lo = 1, hi = m * n;
        while (lo < hi) {
            int mid = lo + (hi - lo) / 2, cnt = 0;
            for (int i = 1; i <= m; i++) cnt += Math.min(mid / i, n);
            if (cnt >= k) hi = mid; else lo = mid + 1;
        }
        return lo;
    }

    // Median of Two Sorted Arrays (LC 4): partition the smaller array, O(log min(m, n))
    static double findMedianSortedArrays(int[] a, int[] b) {
        if (a.length > b.length) return findMedianSortedArrays(b, a);
        int m = a.length, n = b.length, lo = 0, hi = m, half = (m + n + 1) / 2;
        while (lo <= hi) {
            int i = (lo + hi) / 2, j = half - i;
            int aL = i == 0 ? Integer.MIN_VALUE : a[i - 1], aR = i == m ? Integer.MAX_VALUE : a[i];
            int bL = j == 0 ? Integer.MIN_VALUE : b[j - 1], bR = j == n ? Integer.MAX_VALUE : b[j];
            if (aL <= bR && bL <= aR) {
                if ((m + n) % 2 == 1) return Math.max(aL, bL);
                return (Math.max(aL, bL) + (double) Math.min(aR, bR)) / 2;
            }
            if (aL > bR) hi = i - 1; else lo = i + 1;
        }
        throw new IllegalArgumentException();
    }

    // LC 644
    // Real-valued search: sqrt with fixed iterations (avoids epsilon traps)
    static double sqrt(double x) {
        double lo = 0, hi = Math.max(1, x);
        for (int it = 0; it < 100; it++) {
            double mid = (lo + hi) / 2;
            if (mid * mid >= x) hi = mid; else lo = mid;
        }
        return hi;
    }

    // Integer sqrt (LC 69): last x with x*x <= n
    // Same idea as BitsMathVariants.mySqrt.
    static int mySqrt(int n) {
        int lo = 0, hi = 46341; // 46341^2 overflows int, so it is the first "too big"
        while (lo < hi) {
            int mid = lo + (hi - lo) / 2;
            if ((long) mid * mid > n) hi = mid; else lo = mid + 1;
        }
        return lo - 1;
    }

    // Time Based Key-Value Store (LC 981): binary search the latest timestamp <= t
    static class TimeMap {
        private final Map<String, List<int[]>> ts = new HashMap<>(); // {timestamp, valueIndex}
        private final List<String> vals = new ArrayList<>();
        void set(String k, String v, int t) {
            vals.add(v);
            ts.computeIfAbsent(k, x -> new ArrayList<>()).add(new int[]{t, vals.size() - 1});
        }
        String get(String k, int t) {
            List<int[]> l = ts.get(k);
            if (l == null) return "";
            int lo = 0, hi = l.size();
            while (lo < hi) {
                int mid = (lo + hi) >>> 1;
                if (l.get(mid)[0] > t) hi = mid; else lo = mid + 1;
            }
            return lo == 0 ? "" : vals.get(l.get(lo - 1)[1]);
        }
    }

    // Find K Closest Elements (LC 658): binary search the left edge of the window
    static List<Integer> findClosestElements(int[] a, int k, int x) {
        int lo = 0, hi = a.length - k;
        while (lo < hi) {
            int mid = lo + (hi - lo) / 2;
            if (x - a[mid] > a[mid + k] - x) lo = mid + 1; else hi = mid;
        }
        List<Integer> res = new ArrayList<>();
        for (int i = lo; i < lo + k; i++) res.add(a[i]);
        return res;
    }
}
