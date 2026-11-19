import java.util.*;

public class HeapsVariantsTest {
    public static void main(String[] x) {
        Random r = new Random(21);
        // ---------- heaps ----------
        Check.eq("medianSets", Arrays.toString(HeapsVariants.medianSlidingWindowTwoSets(new int[]{1, 3, -1, -3, 5, 3, 6, 7}, 3)), "[1.0, -1.0, -1.0, 3.0, 5.0, 6.0]");
        Check.eq("hireWorkers", String.format("%.5f", HeapsVariants.mincostToHireWorkers(new int[]{10, 20, 5}, new int[]{70, 50, 30}, 2)), "105.00000");
        Check.eq("perf", HeapsVariants.maxPerformance(6, new int[]{2, 10, 3, 1, 5, 8}, new int[]{5, 4, 3, 9, 7, 2}, 2), 60);
        Check.eq("kPairs", HeapsVariants.kSmallestPairs(new int[]{1, 7, 11}, new int[]{2, 4, 6}, 3), "[[1, 2], [1, 4], [1, 6]]");
        Check.eq("primeFraction", Check.s(HeapsVariants.kthSmallestPrimeFraction(new int[]{1, 2, 3, 5}, 3)), "[2, 5]");
        Check.eq("courseIII", HeapsVariants.scheduleCourse(new int[][]{{100, 200}, {200, 1300}, {1000, 1250}, {2000, 3200}}), 3);
        Check.eq("sticks", HeapsVariants.connectSticks(new int[]{2, 4, 3}) + "," + HeapsVariants.connectSticks(new int[]{1, 8, 3, 5}), "14,30");
        Check.eq("rain2", HeapsVariants.trapRainWater(new int[][]{{1, 4, 3, 1, 3, 2}, {3, 2, 1, 3, 2, 4}, {2, 3, 3, 2, 3, 1}}), 4);
        Check.eq("cpu", Check.s(HeapsVariants.getOrder(new int[][]{{1, 2}, {2, 4}, {3, 2}, {4, 1}})), "[0, 2, 3, 1]");
        Check.eq("deviation", HeapsVariants.minimumDeviation(new int[]{1, 2, 3, 4}) + "," + HeapsVariants.minimumDeviation(new int[]{4, 1, 5, 20, 3}), "1,3");
        HeapsVariants.KthLargest kl = new HeapsVariants.KthLargest(3, new int[]{4, 5, 8, 2});
        Check.eq("kthStream", kl.add(3) + "," + kl.add(5) + "," + kl.add(10) + "," + kl.add(9) + "," + kl.add(4), "4,5,5,8,8");
        for (int t = 0; t < 300; t++) {
            int n = 1 + r.nextInt(14);
            int[] a = new int[n];
            for (int i = 0; i < n; i++) a[i] = r.nextInt(15) - 7;
            int k = 1 + r.nextInt(n);
            double[] got = HeapsVariants.medianSlidingWindowTwoSets(a, k);
            for (int i = 0; i + k <= n; i++) {
                int[] w = Arrays.copyOfRange(a, i, i + k);
                Arrays.sort(w);
                double want = k % 2 == 1 ? w[k / 2] : ((double) w[k / 2 - 1] + w[k / 2]) / 2;
                Check.eq("medianRand", got[i], want);
            }
            // indexed PQ vs sorted order
            HeapsVariants.IndexedMinPQ pq = new HeapsVariants.IndexedMinPQ(n);
            long[] keys = new long[n];
            for (int i = 0; i < n; i++) { keys[i] = r.nextInt(100); pq.insert(i, keys[i]); }
            for (int i = 0; i < n; i++) if (r.nextBoolean()) { long nk = keys[i] - r.nextInt(50); pq.decreaseKey(i, nk); keys[i] = Math.min(keys[i], nk); }
            long prev = Long.MIN_VALUE;
            boolean ok = true;
            for (int i = 0; i < n; i++) { int id = pq.extractMin(); if (keys[id] < prev) ok = false; prev = keys[id]; }
            Check.eq("indexedPQ", ok, true);
            // k pairs vs brute
            int[] b = new int[1 + r.nextInt(5)];
            for (int i = 0; i < b.length; i++) b[i] = r.nextInt(10);
            int[] sa = a.clone(), sb = b.clone();
            Arrays.sort(sa); Arrays.sort(sb);
            int kk = 1 + r.nextInt(6);
            List<Integer> brute = new ArrayList<>();
            for (int p : sa) for (int q : sb) brute.add(p + q);
            Collections.sort(brute);
            List<Integer> mine = new ArrayList<>();
            for (List<Integer> pr : HeapsVariants.kSmallestPairs(sa, sb, kk)) mine.add(pr.get(0) + pr.get(1));
            Check.eq("kPairsRand", mine, brute.subList(0, Math.min(kk, brute.size())));
        }

        Check.done("HeapsVariantsTest");
    }

    static boolean sorted(int[] a) { for (int i = 1; i < a.length; i++) if (a[i - 1] > a[i]) return false; return true; }
}
