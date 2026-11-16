import java.util.*;

public class RangeStructures {

    // Pick: prefix sums (static), Fenwick (point update + prefix/range sum), segment tree (any monoid, range ops),
    //       lazy segment tree (range update), sparse table (static range min/max in O(1)).

    // ---------- Prefix sum / difference array ----------
    static long[] prefix(int[] a) {
        long[] p = new long[a.length + 1];
        for (int i = 0; i < a.length; i++) p[i + 1] = p[i] + a[i];
        return p; // sum [l, r] = p[r + 1] - p[l]
    }

    // Range add via difference array (LC 370 / 1109)
    static int[] rangeAdd(int n, int[][] updates) { // {l, r, v}
        int[] d = new int[n + 1];
        for (int[] u : updates) { d[u[0]] += u[2]; d[u[1] + 1] -= u[2]; }
        int[] res = new int[n];
        int run = 0;
        for (int i = 0; i < n; i++) { run += d[i]; res[i] = run; }
        return res;
    }

    // 2D prefix sum (LC 304)
    static class NumMatrix {
        private final int[][] p;
        NumMatrix(int[][] m) {
            p = new int[m.length + 1][m[0].length + 1];
            for (int i = 0; i < m.length; i++)
                for (int j = 0; j < m[0].length; j++) p[i + 1][j + 1] = m[i][j] + p[i][j + 1] + p[i + 1][j] - p[i][j];
        }
        int sumRegion(int r1, int c1, int r2, int c2) { return p[r2 + 1][c2 + 1] - p[r1][c2 + 1] - p[r2 + 1][c1] + p[r1][c1]; }
    }

    // ---------- Fenwick tree (BIT), 1-indexed internally ----------
    static class Fenwick {
        private final long[] t;
        Fenwick(int n) { t = new long[n + 1]; }
        void add(int i, long delta) { for (i++; i < t.length; i += i & -i) t[i] += delta; }       // 0-indexed API
        long prefix(int i) { long s = 0; for (i++; i > 0; i -= i & -i) s += t[i]; return s; }     // sum [0, i]
        long range(int l, int r) { return prefix(r) - (l > 0 ? prefix(l - 1) : 0); }
    }

    // Count of Smaller Numbers After Self (LC 315): compress values, Fenwick over ranks, scan right to left
    static List<Integer> countSmaller(int[] nums) {
        int n = nums.length;
        int[] sorted = Arrays.stream(nums).distinct().sorted().toArray();
        Fenwick f = new Fenwick(sorted.length);
        Integer[] res = new Integer[n];
        for (int i = n - 1; i >= 0; i--) {
            int rank = Arrays.binarySearch(sorted, nums[i]);
            res[i] = rank == 0 ? 0 : (int) f.prefix(rank - 1);
            f.add(rank, 1);
        }
        return Arrays.asList(res);
    }

    // Count inversions with Fenwick
    static long countInversions(int[] a) {
        int[] sorted = Arrays.stream(a).distinct().sorted().toArray();
        Fenwick f = new Fenwick(sorted.length);
        long inv = 0;
        for (int i = a.length - 1; i >= 0; i--) {
            int rank = Arrays.binarySearch(sorted, a[i]);
            if (rank > 0) inv += f.prefix(rank - 1);
            f.add(rank, 1);
        }
        return inv;
    }

    // ---------- Segment tree (iterative, bottom-up): point update, range query (sum; swap combine for min/max) ----------
    static class SegTree {
        private final int n;
        private final long[] t;
        SegTree(int[] a) {
            n = a.length;
            t = new long[2 * n];
            for (int i = 0; i < n; i++) t[n + i] = a[i];
            for (int i = n - 1; i > 0; i--) t[i] = t[2 * i] + t[2 * i + 1];
        }
        void set(int i, long v) {
            for (t[i += n] = v; i > 1; i >>= 1) t[i >> 1] = t[i] + t[i ^ 1];
        }
        long query(int l, int r) { // inclusive
            long res = 0;
            for (l += n, r += n + 1; l < r; l >>= 1, r >>= 1) {
                if ((l & 1) == 1) res += t[l++];
                if ((r & 1) == 1) res += t[--r];
            }
            return res;
        }
    }

    // ---------- Lazy segment tree: range add + range sum (recursive) ----------
    static class LazySeg {
        private final long[] sum, lazy;
        private final int n;
        LazySeg(int[] a) {
            n = a.length;
            sum = new long[4 * n];
            lazy = new long[4 * n];
            build(1, 0, n - 1, a);
        }
        private void build(int o, int l, int r, int[] a) {
            if (l == r) { sum[o] = a[l]; return; }
            int m = (l + r) >>> 1;
            build(2 * o, l, m, a);
            build(2 * o + 1, m + 1, r, a);
            sum[o] = sum[2 * o] + sum[2 * o + 1];
        }
        private void push(int o, int l, int r) {
            if (lazy[o] == 0) return;
            int m = (l + r) >>> 1;
            apply(2 * o, l, m, lazy[o]);
            apply(2 * o + 1, m + 1, r, lazy[o]);
            lazy[o] = 0;
        }
        private void apply(int o, int l, int r, long v) { sum[o] += v * (r - l + 1); lazy[o] += v; }
        void add(int ql, int qr, long v) { add(1, 0, n - 1, ql, qr, v); }
        private void add(int o, int l, int r, int ql, int qr, long v) {
            if (qr < l || r < ql) return;
            if (ql <= l && r <= qr) { apply(o, l, r, v); return; }
            push(o, l, r);
            int m = (l + r) >>> 1;
            add(2 * o, l, m, ql, qr, v);
            add(2 * o + 1, m + 1, r, ql, qr, v);
            sum[o] = sum[2 * o] + sum[2 * o + 1];
        }
        long query(int ql, int qr) { return query(1, 0, n - 1, ql, qr); }
        private long query(int o, int l, int r, int ql, int qr) {
            if (qr < l || r < ql) return 0;
            if (ql <= l && r <= qr) return sum[o];
            push(o, l, r);
            int m = (l + r) >>> 1;
            return query(2 * o, l, m, ql, qr) + query(2 * o + 1, m + 1, r, ql, qr);
        }
    }

    // ---------- Sparse table: static range minimum in O(1) after O(n log n) build ----------
    static class SparseTable {
        private final int[][] st;
        private final int[] log;
        SparseTable(int[] a) {
            int n = a.length, K = 1;
            while ((1 << K) <= n) K++;
            st = new int[K][n];
            log = new int[n + 2];
            for (int i = 2; i <= n + 1; i++) log[i] = log[i / 2] + 1;
            st[0] = a.clone();
            for (int k = 1; k < K; k++)
                for (int i = 0; i + (1 << k) <= n; i++) st[k][i] = Math.min(st[k - 1][i], st[k - 1][i + (1 << (k - 1))]);
        }
        int min(int l, int r) {
            int k = log[r - l + 1];
            return Math.min(st[k][l], st[k][r - (1 << k) + 1]);
        }
    }

    // ---------- Skyline (LC 218): sweep + max-heap with lazy deletion ----------
    static List<List<Integer>> getSkyline(int[][] b) {
        List<int[]> ev = new ArrayList<>();
        for (int[] x : b) { ev.add(new int[]{x[0], -x[2]}); ev.add(new int[]{x[1], x[2]}); }
        ev.sort((p, q) -> p[0] != q[0] ? p[0] - q[0] : p[1] - q[1]); // starts (negative) before ends at equal x, taller start first
        TreeMap<Integer, Integer> heights = new TreeMap<>();
        heights.put(0, 1);
        int prev = 0;
        List<List<Integer>> res = new ArrayList<>();
        for (int[] e : ev) {
            if (e[1] < 0) heights.merge(-e[1], 1, Integer::sum);
            else heights.merge(e[1], -1, (a, c) -> a + c == 0 ? null : a + c);
            int cur = heights.lastKey();
            if (cur != prev) { res.add(List.of(e[0], cur)); prev = cur; }
        }
        return res;
    }
}
