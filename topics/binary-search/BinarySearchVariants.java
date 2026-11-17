import java.util.*;

public class BinarySearchVariants {

    // All of these are the same loop: pick the search space, write ok(x), find the first x where ok is true.

    // ---------- Find K-th Smallest Pair Distance (LC 719): count pairs with distance <= d using two pointers ----------
    static int smallestDistancePair(int[] a, int k) {
        Arrays.sort(a);
        int lo = 0, hi = a[a.length - 1] - a[0];
        while (lo < hi) {
            int mid = lo + (hi - lo) / 2, count = 0, l = 0;
            for (int r = 0; r < a.length; r++) {
                while (a[r] - a[l] > mid) l++;
                count += r - l;
            }
            if (count >= k) hi = mid; else lo = mid + 1;
        }
        return lo;
    }

    // ---------- Smallest Divisor Given a Threshold (LC 1283) ----------
    static int smallestDivisor(int[] a, int threshold) {
        int lo = 1, hi = Arrays.stream(a).max().getAsInt();
        while (lo < hi) {
            int mid = lo + (hi - lo) / 2;
            long sum = 0;
            for (int x : a) sum += (x + mid - 1) / mid;
            if (sum <= threshold) hi = mid; else lo = mid + 1;
        }
        return lo;
    }

    // ---------- Magnetic Force Between Two Balls / Aggressive Cows (LC 1552): maximize the minimum gap ----------
    // "Maximize the minimum": ok(d) = can we place m items at least d apart? ok is true then false, so search the LAST true.
    static int maxDistance(int[] position, int m) {
        Arrays.sort(position);
        int lo = 1, hi = position[position.length - 1] - position[0];
        while (lo < hi) {
            int mid = lo + (hi - lo + 1) / 2; // round up so the loop always shrinks
            int placed = 1, last = position[0];
            for (int p : position) if (p - last >= mid) { placed++; last = p; }
            if (placed >= m) lo = mid; else hi = mid - 1;
        }
        return lo;
    }

    // ---------- Minimize Max Distance to Gas Station (LC 774): real-valued search with a fixed iteration count ----------
    static double minmaxGasDist(int[] stations, int k) {
        double lo = 0, hi = stations[stations.length - 1] - stations[0];
        for (int it = 0; it < 100; it++) {
            double mid = (lo + hi) / 2;
            long need = 0;
            for (int i = 1; i < stations.length; i++) need += (long) Math.ceil((stations[i] - stations[i - 1]) / mid) - 1;
            if (need <= k) hi = mid; else lo = mid;
        }
        return hi;
    }

    // ---------- Kth Missing Positive Number (LC 1539): missing count before index i is a[i] - (i + 1), monotone ----------
    static int findKthPositive(int[] a, int k) {
        int lo = 0, hi = a.length;
        while (lo < hi) {
            int mid = lo + (hi - lo) / 2;
            if (a[mid] - (mid + 1) >= k) hi = mid; else lo = mid + 1;
        }
        return lo + k;
    }

    // ---------- Single Element in a Sorted Array (LC 540): pairs start at even indices until the single element ----------
    static int singleNonDuplicate(int[] a) {
        int lo = 0, hi = a.length - 1;
        while (lo < hi) {
            int mid = lo + (hi - lo) / 2;
            if (mid % 2 == 1) mid--; // align to an even index
            if (a[mid] == a[mid + 1]) lo = mid + 2; else hi = mid;
        }
        return a[lo];
    }

    // ---------- Maximum Value at a Given Index in a Bounded Array (LC 1802): sum of a triangle shape is monotone in the peak ----------
    static int maxValue(int n, int index, int maxSum) {
        int lo = 1, hi = maxSum;
        while (lo < hi) {
            int mid = lo + (hi - lo + 1) / 2;
            if (minSum(mid, index, n) <= maxSum) lo = mid; else hi = mid - 1;
        }
        return lo;
    }

    private static long minSum(long peak, int index, int n) {
        return side(peak, index + 1) + side(peak, n - index) - peak; // peak counted twice
    }

    private static long side(long peak, int len) { // peak, peak-1, ... down to 1 then ones
        if (peak >= len) return (peak + peak - len + 1) * len / 2;
        return peak * (peak + 1) / 2 + (len - peak);
    }

    // ---------- Minimum Time to Complete Trips (LC 2187) ----------
    static long minimumTime(int[] time, int totalTrips) {
        long lo = 1, hi = (long) Arrays.stream(time).min().getAsInt() * totalTrips;
        while (lo < hi) {
            long mid = lo + (hi - lo) / 2, trips = 0;
            for (int t : time) trips += mid / t;
            if (trips >= totalTrips) hi = mid; else lo = mid + 1;
        }
        return lo;
    }

    // ---------- Minimize the Maximum Difference of Pairs (LC 2616): greedy pairing check inside the binary search ----------
    static int minimizeMax(int[] a, int p) {
        Arrays.sort(a);
        int lo = 0, hi = a[a.length - 1] - a[0];
        while (lo < hi) {
            int mid = lo + (hi - lo) / 2, pairs = 0;
            for (int i = 1; i < a.length; i++) if (a[i] - a[i - 1] <= mid) { pairs++; i++; } // pair adjacent values greedily
            if (pairs >= p) hi = mid; else lo = mid + 1;
        }
        return lo;
    }

    // ---------- H-Index II (LC 275): citations sorted ascending ----------
    static int hIndex(int[] c) {
        int n = c.length, lo = 0, hi = n;
        while (lo < hi) {
            int mid = lo + (hi - lo) / 2;
            if (c[mid] >= n - mid) hi = mid; else lo = mid + 1;
        }
        return n - lo;
    }

    // ---------- Find Right Interval (LC 436): sort starts, lower-bound each end ----------
    static int[] findRightInterval(int[][] iv) {
        int n = iv.length;
        Integer[] byStart = new Integer[n];
        for (int i = 0; i < n; i++) byStart[i] = i;
        Arrays.sort(byStart, (a, b) -> iv[a][0] - iv[b][0]);
        int[] res = new int[n];
        for (int i = 0; i < n; i++) {
            int lo = 0, hi = n;
            while (lo < hi) {
                int mid = (lo + hi) >>> 1;
                if (iv[byStart[mid]][0] >= iv[i][1]) hi = mid; else lo = mid + 1;
            }
            res[i] = lo == n ? -1 : byStart[lo];
        }
        return res;
    }

    // ---------- Max Sum of Rectangle No Larger Than K (LC 363): fix column pair, compress to 1D, prefix sums + TreeSet.ceiling ----------
    static int maxSumSubmatrix(int[][] m, int k) {
        int R = m.length, C = m[0].length, best = Integer.MIN_VALUE;
        for (int c1 = 0; c1 < C; c1++) {
            int[] rowSum = new int[R];
            for (int c2 = c1; c2 < C; c2++) {
                for (int r = 0; r < R; r++) rowSum[r] += m[r][c2];
                TreeSet<Integer> prefixes = new TreeSet<>();
                prefixes.add(0);
                int pre = 0;
                for (int r = 0; r < R; r++) {
                    pre += rowSum[r];
                    Integer need = prefixes.ceiling(pre - k); // smallest earlier prefix with pre - prefix <= k
                    if (need != null) best = Math.max(best, pre - need);
                    prefixes.add(pre);
                }
            }
        }
        return best;
    }

    // LC 852 / 1095
    // ---------- Search in a Bitonic / Mountain array: find the peak, then search each side ----------
    static int searchMountain(int[] a, int target) {
        int lo = 0, hi = a.length - 1;
        while (lo < hi) { int mid = (lo + hi) >>> 1; if (a[mid] < a[mid + 1]) lo = mid + 1; else hi = mid; }
        int peak = lo;
        int l = 0, r = peak;
        while (l <= r) { int mid = (l + r) >>> 1; if (a[mid] == target) return mid; if (a[mid] < target) l = mid + 1; else r = mid - 1; }
        l = peak + 1; r = a.length - 1;
        while (l <= r) { int mid = (l + r) >>> 1; if (a[mid] == target) return mid; if (a[mid] > target) l = mid + 1; else r = mid - 1; }
        return -1;
    }
}
