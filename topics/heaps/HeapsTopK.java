import java.util.*;

public class HeapsTopK {

    // ---------- Kth Largest: keep a MIN-heap of size k (LC 215) ----------
    // RECOGNIZE: "k largest / smallest / most frequent / closest". Heap of size k -> O(n log k).
    static int findKthLargest(int[] a, int k) {
        PriorityQueue<Integer> pq = new PriorityQueue<>();
        for (int x : a) { pq.add(x); if (pq.size() > k) pq.poll(); }
        return pq.peek();
    }

    // Quickselect: average O(n), worst O(n^2) -- randomize the pivot
    static int quickselectKthLargest(int[] a, int k) {
        int[] b = a.clone();
        int target = b.length - k, lo = 0, hi = b.length - 1;
        Random rnd = new Random(1);
        while (lo < hi) {
            int p = b[lo + rnd.nextInt(hi - lo + 1)], i = lo, j = hi;
            while (i <= j) { // Hoare-style three-way-safe partition
                while (b[i] < p) i++;
                while (b[j] > p) j--;
                if (i <= j) { int t = b[i]; b[i] = b[j]; b[j] = t; i++; j--; }
            }
            if (target <= j) hi = j; else if (target >= i) lo = i; else return b[target];
        }
        return b[target];
    }

    // K Closest Points to Origin (LC 973): MAX-heap of size k on distance
    static int[][] kClosest(int[][] pts, int k) {
        PriorityQueue<int[]> pq = new PriorityQueue<>((a, b) -> Long.compare(d2(b), d2(a)));
        for (int[] p : pts) { pq.add(p); if (pq.size() > k) pq.poll(); }
        return pq.toArray(new int[0][]);
    }

    private static long d2(int[] p) { return (long) p[0] * p[0] + (long) p[1] * p[1]; }

    // Top K Frequent Words (LC 692): tie-break alphabetical
    static List<String> topKFrequentWords(String[] words, int k) {
        Map<String, Integer> f = new HashMap<>();
        for (String w : words) f.merge(w, 1, Integer::sum);
        PriorityQueue<String> pq = new PriorityQueue<>((a, b) -> f.get(a).equals(f.get(b)) ? b.compareTo(a) : f.get(a) - f.get(b));
        for (String w : f.keySet()) { pq.add(w); if (pq.size() > k) pq.poll(); }
        LinkedList<String> res = new LinkedList<>();
        while (!pq.isEmpty()) res.addFirst(pq.poll());
        return res;
    }

    // ---------- Two heaps: Find Median from Data Stream (LC 295) ----------
    static class MedianFinder {
        private final PriorityQueue<Integer> lo = new PriorityQueue<>(Collections.reverseOrder()); // lower half
        private final PriorityQueue<Integer> hi = new PriorityQueue<>();                           // upper half
        void addNum(int x) {
            lo.add(x);
            hi.add(lo.poll());
            if (hi.size() > lo.size()) lo.add(hi.poll());
        }
        double findMedian() { return lo.size() > hi.size() ? lo.peek() : (lo.peek() + (double) hi.peek()) / 2; }
    }

    // Sliding Window Median (LC 480): TreeMap-free version with two multiset heaps + lazy delete is long;
    // this simple O(n k) insertion version is the safe interview baseline.
    static double[] medianSlidingWindow(int[] a, int k) {
        double[] res = new double[a.length - k + 1];
        List<Integer> win = new ArrayList<>();
        for (int i = 0; i < a.length; i++) {
            int pos = Collections.binarySearch(win, a[i]);
            win.add(pos < 0 ? -pos - 1 : pos, a[i]);
            if (i >= k) win.remove(Collections.binarySearch(win, a[i - k]));
            if (i >= k - 1)
                res[i - k + 1] = k % 2 == 1 ? win.get(k / 2) : ((long) win.get(k / 2 - 1) + win.get(k / 2)) / 2.0;
        }
        return res;
    }

    // ---------- Merge K sorted lists / arrays (LC 23): heap of (value, list, index) ----------
    static List<Integer> mergeKSorted(List<int[]> lists) {
        PriorityQueue<int[]> pq = new PriorityQueue<>((x, y) -> x[0] - y[0]); // {value, listIdx, elemIdx}
        for (int i = 0; i < lists.size(); i++) if (lists.get(i).length > 0) pq.add(new int[]{lists.get(i)[0], i, 0});
        List<Integer> out = new ArrayList<>();
        while (!pq.isEmpty()) {
            int[] t = pq.poll();
            out.add(t[0]);
            int[] src = lists.get(t[1]);
            if (t[2] + 1 < src.length) pq.add(new int[]{src[t[2] + 1], t[1], t[2] + 1});
        }
        return out;
    }

    // Kth Smallest in Sorted Matrix (LC 378)
    static int kthSmallest(int[][] m, int k) {
        int n = m.length;
        PriorityQueue<int[]> pq = new PriorityQueue<>((a, b) -> a[0] - b[0]);
        for (int i = 0; i < Math.min(n, k); i++) pq.add(new int[]{m[i][0], i, 0});
        int v = 0;
        while (k-- > 0) {
            int[] t = pq.poll();
            v = t[0];
            if (t[2] + 1 < n) pq.add(new int[]{m[t[1]][t[2] + 1], t[1], t[2] + 1});
        }
        return v;
    }

    // Smallest Range Covering Elements from K Lists (LC 632)
    static int[] smallestRange(List<List<Integer>> nums) {
        PriorityQueue<int[]> pq = new PriorityQueue<>((a, b) -> a[0] - b[0]);
        int max = Integer.MIN_VALUE;
        for (int i = 0; i < nums.size(); i++) { pq.add(new int[]{nums.get(i).get(0), i, 0}); max = Math.max(max, nums.get(i).get(0)); }
        int[] best = {pq.peek()[0], max};
        while (true) {
            int[] t = pq.poll();
            if (max - t[0] < best[1] - best[0]) best = new int[]{t[0], max};
            if (t[2] + 1 == nums.get(t[1]).size()) break;
            int nv = nums.get(t[1]).get(t[2] + 1);
            max = Math.max(max, nv);
            pq.add(new int[]{nv, t[1], t[2] + 1});
        }
        return best;
    }

    // ---------- Greedy with heap ----------
    // Task Scheduler (LC 621): closed form
    static int leastInterval(char[] tasks, int n) {
        int[] c = new int[26];
        int max = 0, maxCount = 0;
        for (char t : tasks) max = Math.max(max, ++c[t - 'A']);
        for (int x : c) if (x == max) maxCount++;
        return Math.max(tasks.length, (max - 1) * (n + 1) + maxCount);
    }

    // Reorganize String (LC 767): always place the most frequent char that is not the previous one
    static String reorganizeString(String s) {
        int[] c = new int[26];
        for (char ch : s.toCharArray()) c[ch - 'a']++;
        PriorityQueue<int[]> pq = new PriorityQueue<>((a, b) -> b[1] - a[1]);
        for (int i = 0; i < 26; i++) if (c[i] > 0) pq.add(new int[]{i, c[i]});
        StringBuilder sb = new StringBuilder();
        int[] prev = null;
        while (!pq.isEmpty()) {
            int[] cur = pq.poll();
            sb.append((char) ('a' + cur[0]));
            if (prev != null && prev[1] > 0) pq.add(prev);
            cur[1]--;
            prev = cur;
        }
        return sb.length() == s.length() ? sb.toString() : "";
    }

    // Meeting Rooms II (LC 253): min-heap of end times
    static int minMeetingRooms(int[][] iv) {
        Arrays.sort(iv, (a, b) -> a[0] - b[0]);
        PriorityQueue<Integer> ends = new PriorityQueue<>();
        for (int[] i : iv) {
            if (!ends.isEmpty() && ends.peek() <= i[0]) ends.poll();
            ends.add(i[1]);
        }
        return ends.size();
    }

    // IPO (LC 502): sort by capital, max-heap of affordable profits
    static int findMaximizedCapital(int k, int w, int[] profits, int[] capital) {
        int n = profits.length;
        Integer[] idx = new Integer[n];
        for (int i = 0; i < n; i++) idx[i] = i;
        Arrays.sort(idx, Comparator.comparingInt(i -> capital[i]));
        PriorityQueue<Integer> pq = new PriorityQueue<>(Collections.reverseOrder());
        int p = 0;
        while (k-- > 0) {
            while (p < n && capital[idx[p]] <= w) pq.add(profits[idx[p++]]);
            if (pq.isEmpty()) break;
            w += pq.poll();
        }
        return w;
    }

    // Furthest Building You Can Reach (LC 1642): spend ladders on the biggest gaps (min-heap of ladder gaps)
    static int furthestBuilding(int[] h, int bricks, int ladders) {
        PriorityQueue<Integer> used = new PriorityQueue<>();
        for (int i = 0; i + 1 < h.length; i++) {
            int d = h[i + 1] - h[i];
            if (d <= 0) continue;
            used.add(d);
            if (used.size() > ladders) bricks -= used.poll();
            if (bricks < 0) return i;
        }
        return h.length - 1;
    }

    // Ugly Number II (LC 264): three pointers (heap-free)
    static int nthUglyNumber(int n) {
        int[] u = new int[n];
        u[0] = 1;
        int i2 = 0, i3 = 0, i5 = 0;
        for (int i = 1; i < n; i++) {
            int next = Math.min(u[i2] * 2, Math.min(u[i3] * 3, u[i5] * 5));
            u[i] = next;
            if (next == u[i2] * 2) i2++;
            if (next == u[i3] * 3) i3++;
            if (next == u[i5] * 5) i5++;
        }
        return u[n - 1];
    }

    // Lazy-deletion heap pattern: remember what is deleted in a map, discard stale tops when peeking.
    // (see Dijkstra "if (d > dist[u]) continue" in the graphs topic -- same idea)
}
