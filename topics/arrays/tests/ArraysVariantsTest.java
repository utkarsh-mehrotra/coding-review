import java.util.*;

public class ArraysVariantsTest {
    public static void main(String[] x) {
        arrays();
        Check.done("ArraysVariantsTest");
    }

    static Random rnd = new Random(20260316);
    static int[] arr(int n, int lo, int hi) { int[] a = new int[n]; for (int i = 0; i < n; i++) a[i] = lo + rnd.nextInt(hi - lo + 1); return a; }
    static List<Integer> sorted(Collection<Integer> c) { List<Integer> l = new ArrayList<>(c); Collections.sort(l); return l; }
    static void arrays() {
        for (int t = 0; t < 400; t++) {
            int n = 1 + rnd.nextInt(9);
            int[] a = arr(n, -6, 6);
            int best = Integer.MIN_VALUE;
            for (int s = 0; s < n; s++) { int sum = 0; for (int len = 1; len <= n; len++) { sum += a[(s + len - 1) % n]; best = Math.max(best, sum); } }
            Check.eq("circular", ArraysVariants.maxSubarraySumCircular(a), best);

            int[] m = arr(1 + rnd.nextInt(12), 0, 3);
            Map<Integer, Integer> cnt = new HashMap<>();
            for (int v : m) cnt.merge(v, 1, Integer::sum);
            List<Integer> want = new ArrayList<>();
            for (var e : cnt.entrySet()) if (e.getValue() > m.length / 3) want.add(e.getKey());
            Check.eq("majorityII", sorted(ArraysVariants.majorityElementII(m)), sorted(want));

            int dn = 1 + rnd.nextInt(8);
            int[] d = new int[dn];
            for (int i = 0; i < dn; i++) d[i] = 1 + rnd.nextInt(dn);
            Map<Integer, Integer> dc = new HashMap<>();
            for (int v : d) dc.merge(v, 1, Integer::sum);
            List<Integer> dw = new ArrayList<>();
            for (var e : dc.entrySet()) if (e.getValue() == 2) dw.add(e.getKey());
            boolean allLe2 = dc.values().stream().allMatch(c -> c <= 2);
            if (allLe2) Check.eq("findDup", sorted(ArraysVariants.findDuplicates(d.clone())), sorted(dw));

            int[] r = arr(1 + rnd.nextInt(10), -8, 8);
            int lo = -5 + rnd.nextInt(5), hi = lo + rnd.nextInt(8);
            long[] p = new long[r.length + 1];
            for (int i = 0; i < r.length; i++) p[i + 1] = p[i] + r[i];
            int c1 = 0, c2 = 0;
            for (int i = 0; i < p.length; i++) for (int j = i + 1; j < p.length; j++) if (p[j] - p[i] >= lo && p[j] - p[i] <= hi) c1++;
            Check.eq("rangeSum", ArraysVariants.countRangeSum(r, lo, hi), c1);
            for (int i = 0; i < r.length; i++) for (int j = i + 1; j < r.length; j++) if ((long) r[i] > 2L * r[j]) c2++;
            Check.eq("reversePairs", ArraysVariants.reversePairs(r), c2);

            int pn = 1 + rnd.nextInt(8);
            int[] perm = new int[pn];
            for (int i = 0; i < pn; i++) perm[i] = i;
            for (int i = pn - 1; i > 0; i--) { int j = rnd.nextInt(i + 1); int tmp = perm[i]; perm[i] = perm[j]; perm[j] = tmp; }
            int ch = 0;
            for (int i = 0; i < pn; i++) { boolean ok = true; for (int k = 0; k <= i; k++) if (perm[k] > i) ok = false; if (ok) ch++; }
            Check.eq("chunks", ArraysVariants.maxChunksToSorted(perm), ch);

            int[] g = arr(1 + rnd.nextInt(9), 0, 6);
            int chunks = 1;
            for (int i = 0; i + 1 < g.length; i++) {
                int mx = Integer.MIN_VALUE, mn = Integer.MAX_VALUE;
                for (int k = 0; k <= i; k++) mx = Math.max(mx, g[k]);
                for (int k = i + 1; k < g.length; k++) mn = Math.min(mn, g[k]);
                if (mx <= mn) chunks++;
            }
            Check.eq("chunksII", ArraysVariants.maxChunksToSortedII(g), chunks);

            int[] mt = arr(1 + rnd.nextInt(10), 0, 4);
            int mbest = 0;
            for (int pk = 1; pk + 1 < mt.length; pk++) {
                int l = pk, rr = pk;
                while (l > 0 && mt[l - 1] < mt[l]) l--;
                while (rr + 1 < mt.length && mt[rr + 1] < mt[rr]) rr++;
                if (l < pk && rr > pk) mbest = Math.max(mbest, rr - l + 1);
            }
            Check.eq("mountain", ArraysVariants.longestMountain(mt), mbest);

            int[] sp = arr(2 + rnd.nextInt(8), 1, 9);
            int sb = Integer.MIN_VALUE;
            for (int i = 0; i < sp.length; i++) for (int j = i + 1; j < sp.length; j++) sb = Math.max(sb, sp[i] + sp[j] + i - j);
            Check.eq("sightseeing", ArraysVariants.maxScoreSightseeingPair(sp), sb);

            int[] bm = arr(1 + rnd.nextInt(9), 0, 6);
            int bl = rnd.nextInt(4), br = bl + rnd.nextInt(4), bc = 0;
            for (int i = 0; i < bm.length; i++) { int mx = -1; for (int j = i; j < bm.length; j++) { mx = Math.max(mx, bm[j]); if (mx >= bl && mx <= br) bc++; } }
            Check.eq("boundedMax", ArraysVariants.numSubarrayBoundedMax(bm, bl, br), bc);

            int[] us = arr(1 + rnd.nextInt(9), 0, 6);
            int[] srt = us.clone();
            Arrays.sort(srt);
            int s0 = 0, e0 = us.length - 1;
            while (s0 < us.length && us[s0] == srt[s0]) s0++;
            while (e0 >= 0 && us[e0] == srt[e0]) e0--;
            Check.eq("unsorted", ArraysVariants.findUnsortedSubarray(us), s0 >= us.length ? 0 : e0 - s0 + 1);

            int[] rp = arr(1 + rnd.nextInt(10), 0, 7);
            int rw = 0;
            for (int i = 0; i < rp.length; i++) for (int j = i; j < rp.length; j++) if (rp[i] <= rp[j]) rw = Math.max(rw, j - i);
            Check.eq("ramp", ArraysVariants.maxWidthRamp(rp), rw);

            int[] tw = arr(3 + rnd.nextInt(9), 0, 9);
            int L = 1 + rnd.nextInt(3), M = 1 + rnd.nextInt(3);
            if (L + M <= tw.length) {
                int tb = 0;
                for (int i = 0; i + L <= tw.length; i++) for (int j = 0; j + M <= tw.length; j++) {
                    if (i + L <= j || j + M <= i) {
                        int sum = 0;
                        for (int k = 0; k < L; k++) sum += tw[i + k];
                        for (int k = 0; k < M; k++) sum += tw[j + k];
                        tb = Math.max(tb, sum);
                    }
                }
                Check.eq("twoNoOverlap", ArraysVariants.maxSumTwoNoOverlap(tw, L, M), tb);
            }

            int[] so = arr(rnd.nextInt(30), 0, 5000);
            int[] w1 = so.clone(), w2 = so.clone(), w3 = so.clone(), want0 = so.clone();
            Arrays.sort(want0);
            ArraysVariants.radixSort(w1);
            Check.eq("radix", Check.s(w1), Check.s(want0));
            int[] small = arr(rnd.nextInt(30), 0, 9);
            int[] sw = small.clone(), sw2 = small.clone();
            Arrays.sort(sw2);
            ArraysVariants.countingSort(sw, 9);
            Check.eq("countingSort", Check.s(sw), Check.s(sw2));
            ArraysVariants.quickSort3(w3);
            Check.eq("quick3", Check.s(w3), Check.s(want0));
        }
        Check.eq("circularAllNeg", ArraysVariants.maxSubarraySumCircular(new int[]{-3, -2, -3}), -2);
        Check.eq("rangeSumEx", ArraysVariants.countRangeSum(new int[]{-2, 5, -1}, -2, 2), 3);
        Check.eq("reverseEx", ArraysVariants.reversePairs(new int[]{1, 3, 2, 3, 1}), 2);
    }
}
