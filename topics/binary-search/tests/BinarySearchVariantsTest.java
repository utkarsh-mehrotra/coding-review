import java.util.*;

public class BinarySearchVariantsTest {
    public static void main(String[] x) {
        binarySearch();
        Check.done("BinarySearchVariantsTest");
    }

    static Random rnd = new Random(20260316);
    static int[] arr(int n, int lo, int hi) { int[] a = new int[n]; for (int i = 0; i < n; i++) a[i] = lo + rnd.nextInt(hi - lo + 1); return a; }
    static void binarySearch() {
        for (int t = 0; t < 300; t++) {
            int[] a = arr(2 + rnd.nextInt(8), 0, 20);
            List<Integer> dists = new ArrayList<>();
            for (int i = 0; i < a.length; i++) for (int j = i + 1; j < a.length; j++) dists.add(Math.abs(a[i] - a[j]));
            Collections.sort(dists);
            int k = 1 + rnd.nextInt(dists.size());
            Check.eq("pairDistance", BinarySearchVariants.smallestDistancePair(a.clone(), k), dists.get(k - 1));

            int[] dv = arr(1 + rnd.nextInt(6), 1, 30);
            int th = dv.length + rnd.nextInt(40), div = 1;
            while (true) { long s = 0; for (int v : dv) s += (v + div - 1) / div; if (s <= th) break; div++; }
            Check.eq("divisor", BinarySearchVariants.smallestDivisor(dv, th), div);

            int[] pos = new int[2 + rnd.nextInt(7)];
            Set<Integer> used = new TreeSet<>();
            while (used.size() < pos.length) used.add(rnd.nextInt(40));
            int pi = 0;
            for (int v : used) pos[pi++] = v;
            int m = 2 + rnd.nextInt(pos.length - 1), bestD = 0;
            for (int d = 1; d <= 40; d++) { int placed = 1, last = pos[0]; for (int p : pos) if (p - last >= d) { placed++; last = p; } if (placed >= m) bestD = d; }
            Check.eq("aggressive", BinarySearchVariants.maxDistance(pos.clone(), m), bestD);

            int[] miss = new int[1 + rnd.nextInt(7)];
            Set<Integer> ms = new TreeSet<>();
            while (ms.size() < miss.length) ms.add(1 + rnd.nextInt(15));
            int mi = 0;
            for (int v : ms) miss[mi++] = v;
            int kth = 1 + rnd.nextInt(8), cnt = 0, cand = 0;
            while (cnt < kth) { cand++; if (!ms.contains(cand)) cnt++; }
            Check.eq("kthMissing", BinarySearchVariants.findKthPositive(miss, kth), cand);

            int pairs = rnd.nextInt(5), spos = rnd.nextInt(pairs + 1);
            List<Integer> arrL = new ArrayList<>();
            for (int i = 0; i < pairs; i++) { arrL.add(10 + i * 3); arrL.add(10 + i * 3); }
            arrL.add(10 + spos * 3 - 1);
            Collections.sort(arrL);
            Check.eq("single", BinarySearchVariants.singleNonDuplicate(arrL.stream().mapToInt(Integer::intValue).toArray()), 10 + spos * 3 - 1);

            int n = 1 + rnd.nextInt(8), index = rnd.nextInt(n), maxSum = n + rnd.nextInt(30), bv = 1;
            for (int v = 1; v <= maxSum; v++) { long s = 0; for (int i = 0; i < n; i++) s += Math.max(v - Math.abs(i - index), 1); if (s <= maxSum) bv = v; }
            Check.eq("boundedIndex", BinarySearchVariants.maxValue(n, index, maxSum), bv);

            int[] tm = arr(1 + rnd.nextInt(4), 1, 6);
            int trips = 1 + rnd.nextInt(12);
            long tt = 1;
            while (true) { long c = 0; for (int v : tm) c += tt / v; if (c >= trips) break; tt++; }
            Check.eq("trips", BinarySearchVariants.minimumTime(tm, trips), tt);

            int[] mm = arr(2 + rnd.nextInt(6), 0, 15);
            int p = rnd.nextInt(mm.length / 2 + 1);
            Check.eq("minimizeMax", BinarySearchVariants.minimizeMax(mm.clone(), p), bruteMinMax(mm, p));

            int[] ci = arr(1 + rnd.nextInt(8), 0, 9);
            Arrays.sort(ci);
            int hb = 0;
            for (int h = 0; h <= ci.length; h++) { int c = 0; for (int v : ci) if (v >= h) c++; if (c >= h) hb = h; }
            Check.eq("hIndex", BinarySearchVariants.hIndex(ci), hb);

            int in = 1 + rnd.nextInt(6);
            int[][] iv = new int[in][2];
            for (int i = 0; i < in; i++) { iv[i][0] = i * 3 + rnd.nextInt(3) + (rnd.nextInt(4) == 0 ? 0 : 0); iv[i][1] = iv[i][0] + rnd.nextInt(8); }
            Set<Integer> starts = new HashSet<>();
            boolean uniq = true;
            for (int[] e : iv) uniq &= starts.add(e[0]);
            if (uniq) {
                int[] want = new int[in];
                for (int i = 0; i < in; i++) { want[i] = -1; int bs = Integer.MAX_VALUE; for (int j = 0; j < in; j++) if (iv[j][0] >= iv[i][1] && iv[j][0] < bs) { bs = iv[j][0]; want[i] = j; } }
                Check.eq("rightInterval", Check.s(BinarySearchVariants.findRightInterval(iv)), Check.s(want));
            }

            int R = 1 + rnd.nextInt(3), C = 1 + rnd.nextInt(3);
            int[][] mat = new int[R][C];
            for (int[] row : mat) for (int j = 0; j < C; j++) row[j] = rnd.nextInt(11) - 5;
            int kk = rnd.nextInt(8) - 2, mb = Integer.MIN_VALUE;
            for (int r1 = 0; r1 < R; r1++) for (int r2 = r1; r2 < R; r2++) for (int c1 = 0; c1 < C; c1++) for (int c2 = c1; c2 < C; c2++) {
                int s = 0;
                for (int r = r1; r <= r2; r++) for (int c = c1; c <= c2; c++) s += mat[r][c];
                if (s <= kk) mb = Math.max(mb, s);
            }
            if (mb != Integer.MIN_VALUE) Check.eq("submatrix", BinarySearchVariants.maxSumSubmatrix(mat, kk), mb);

            int up = 1 + rnd.nextInt(5), down = 1 + rnd.nextInt(5);
            List<Integer> pool = new ArrayList<>();
            for (int i = 0; i < up + down + 1; i++) pool.add(i * 2);
            Collections.shuffle(pool, rnd);
            List<Integer> upPart = new ArrayList<>(pool.subList(0, up)), downPart = new ArrayList<>(pool.subList(up + 1, pool.size()));
            int peak = Collections.max(pool);
            upPart.removeIf(v -> v == peak);
            downPart.removeIf(v -> v == peak);
            Collections.sort(upPart);
            downPart.sort(Collections.reverseOrder());
            List<Integer> mtn = new ArrayList<>(upPart);
            mtn.add(peak);
            mtn.addAll(downPart);
            int[] ma = mtn.stream().mapToInt(Integer::intValue).toArray();
            for (int target = -1; target <= peak + 1; target++) {
                int w = -1;
                for (int i = 0; i < ma.length; i++) if (ma[i] == target) { w = i; break; }
                Check.eq("mountainSearch", BinarySearchVariants.searchMountain(ma, target), w);
            }
        }
        Check.eq("gas", Math.round(BinarySearchVariants.minmaxGasDist(new int[]{1, 2, 3, 4, 5, 6, 7, 8, 9, 10}, 9) * 1e5) / 1e5, 0.5);
        Check.eq("gas2", Math.round(BinarySearchVariants.minmaxGasDist(new int[]{23, 24, 36, 39, 46, 56, 57, 65, 84, 98}, 1) * 1e5) / 1e5, 14.0);
    }
    static int bruteMinMax(int[] a, int p) {
        int[] s = a.clone();
        Arrays.sort(s);
        return pairUp(s, 0, p, 0);
    }
    // choose p disjoint pairs minimizing the largest difference: lowest unused element is either skipped or paired
    static int pairUp(int[] s, int mask, int p, int curMax) {
        if (p == 0) return curMax;
        int i = 0;
        while (i < s.length && (mask >> i & 1) != 0) i++;
        if (i == s.length) return Integer.MAX_VALUE;
        int best = pairUp(s, mask | 1 << i, p, curMax);
        for (int j = i + 1; j < s.length; j++)
            if ((mask >> j & 1) == 0) best = Math.min(best, pairUp(s, mask | 1 << i | 1 << j, p - 1, Math.max(curMax, Math.abs(s[i] - s[j]))));
        return best;
    }
}
