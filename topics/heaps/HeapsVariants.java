import java.util.*;

public class HeapsVariants {

    // ---------- Sliding Window Median, O(n log k): two TreeSets of INDICES ordered by (value, index) (LC 480) ----------
    // RECOGNIZE: need median / order statistic of a moving window with deletions. Heaps cannot delete arbitrary items; ordered sets can.
    static double[] medianSlidingWindowTwoSets(int[] a, int k) {
        Comparator<Integer> cmp = (i, j) -> a[i] != a[j] ? Integer.compare(a[i], a[j]) : Integer.compare(i, j);
        TreeSet<Integer> lo = new TreeSet<>(cmp), hi = new TreeSet<>(cmp); // lo holds the lower half (size = ceil(k/2))
        double[] res = new double[a.length - k + 1];
        for (int i = 0; i < a.length; i++) {
            if (lo.isEmpty() || cmp.compare(i, lo.last()) <= 0) lo.add(i); else hi.add(i);
            if (i >= k) { int out = i - k; if (!lo.remove(out)) hi.remove(out); }
            while (lo.size() > hi.size() + 1) hi.add(lo.pollLast());
            while (lo.size() < hi.size()) lo.add(hi.pollFirst());
            if (i >= k - 1) res[i - k + 1] = k % 2 == 1 ? a[lo.last()] : ((double) a[lo.last()] + a[hi.first()]) / 2;
        }
        return res;
    }

    // ---------- Minimum Cost to Hire K Workers (LC 857): sort by wage/quality; keep the k smallest qualities ----------
    // Paying everyone at the group's max ratio makes cost = ratio * sumQuality; minimize both with a max-heap of quality.
    static double mincostToHireWorkers(int[] quality, int[] wage, int k) {
        int n = quality.length;
        Integer[] idx = new Integer[n];
        for (int i = 0; i < n; i++) idx[i] = i;
        Arrays.sort(idx, (x, y) -> Double.compare((double) wage[x] / quality[x], (double) wage[y] / quality[y]));
        PriorityQueue<Integer> pq = new PriorityQueue<>(Collections.reverseOrder());
        long sum = 0;
        double best = Double.MAX_VALUE;
        for (int i : idx) {
            pq.add(quality[i]);
            sum += quality[i];
            if (pq.size() > k) sum -= pq.poll();
            if (pq.size() == k) best = Math.min(best, sum * ((double) wage[i] / quality[i]));
        }
        return best;
    }

    // ---------- Maximum Performance of a Team (LC 1383): sort by efficiency desc, min-heap of speeds of size k ----------
    static int maxPerformance(int n, int[] speed, int[] eff, int k) {
        Integer[] idx = new Integer[n];
        for (int i = 0; i < n; i++) idx[i] = i;
        Arrays.sort(idx, (a, b) -> eff[b] - eff[a]);
        PriorityQueue<Integer> pq = new PriorityQueue<>();
        long sum = 0, best = 0;
        for (int i : idx) {
            pq.add(speed[i]);
            sum += speed[i];
            if (pq.size() > k) sum -= pq.poll();
            best = Math.max(best, sum * eff[i]);
        }
        return (int) (best % 1_000_000_007L);
    }

    // ---------- Find K Pairs with Smallest Sums (LC 373): frontier heap over (i, j) ----------
    static List<List<Integer>> kSmallestPairs(int[] a, int[] b, int k) {
        List<List<Integer>> res = new ArrayList<>();
        if (a.length == 0 || b.length == 0) return res;
        PriorityQueue<int[]> pq = new PriorityQueue<>((x, y) -> Integer.compare(a[x[0]] + b[x[1]], a[y[0]] + b[y[1]]));
        for (int i = 0; i < Math.min(a.length, k); i++) pq.add(new int[]{i, 0});
        while (k-- > 0 && !pq.isEmpty()) {
            int[] t = pq.poll();
            res.add(List.of(a[t[0]], b[t[1]]));
            if (t[1] + 1 < b.length) pq.add(new int[]{t[0], t[1] + 1});
        }
        return res;
    }

    // ---------- K-th Smallest Prime Fraction (LC 786): frontier heap, compare fractions by cross-multiplication ----------
    static int[] kthSmallestPrimeFraction(int[] arr, int k) {
        int n = arr.length;
        PriorityQueue<int[]> pq = new PriorityQueue<>((x, y) -> Long.compare((long) arr[x[0]] * arr[y[1]], (long) arr[y[0]] * arr[x[1]]));
        for (int j = 1; j < n; j++) pq.add(new int[]{0, j}); // numerator index i, denominator index j
        while (--k > 0) {
            int[] t = pq.poll();
            if (t[0] + 1 < t[1]) pq.add(new int[]{t[0] + 1, t[1]});
        }
        int[] t = pq.peek();
        return new int[]{arr[t[0]], arr[t[1]]};
    }

    // ---------- Course Schedule III (LC 630): sort by deadline; if over time, drop the LONGEST course taken ----------
    // Exchange argument: swapping the longest taken course for a shorter new one never hurts.
    static int scheduleCourse(int[][] courses) {
        Arrays.sort(courses, (a, b) -> a[1] - b[1]);
        PriorityQueue<Integer> pq = new PriorityQueue<>(Collections.reverseOrder());
        int time = 0;
        for (int[] c : courses) {
            pq.add(c[0]);
            time += c[0];
            if (time > c[1]) time -= pq.poll();
        }
        return pq.size();
    }

    // ---------- Minimum Cost to Connect Sticks (LC 1167) / Huffman: always merge the two smallest ----------
    static int connectSticks(int[] sticks) {
        PriorityQueue<Integer> pq = new PriorityQueue<>();
        for (int s : sticks) pq.add(s);
        int cost = 0;
        while (pq.size() > 1) {
            int m = pq.poll() + pq.poll();
            cost += m;
            pq.add(m);
        }
        return cost;
    }

    // ---------- Trapping Rain Water II (LC 407): flood from the lowest boundary cell inward ----------
    static int trapRainWater(int[][] h) {
        int m = h.length, n = h[0].length;
        if (m < 3 || n < 3) return 0;
        boolean[][] seen = new boolean[m][n];
        PriorityQueue<int[]> pq = new PriorityQueue<>((a, b) -> a[0] - b[0]); // {height, r, c}
        for (int i = 0; i < m; i++)
            for (int j = 0; j < n; j++)
                if (i == 0 || j == 0 || i == m - 1 || j == n - 1) { pq.add(new int[]{h[i][j], i, j}); seen[i][j] = true; }
        int water = 0;
        int[] d = {0, 1, 0, -1, 0};
        while (!pq.isEmpty()) {
            int[] t = pq.poll();
            for (int k = 0; k < 4; k++) {
                int r = t[1] + d[k], c = t[2] + d[k + 1];
                if (r < 0 || c < 0 || r >= m || c >= n || seen[r][c]) continue;
                seen[r][c] = true;
                water += Math.max(0, t[0] - h[r][c]);
                pq.add(new int[]{Math.max(t[0], h[r][c]), r, c}); // level rises to the wall
            }
        }
        return water;
    }

    // ---------- Single-Threaded CPU (LC 1834): sort by enqueue time, heap of available by (processing time, index) ----------
    static int[] getOrder(int[][] tasks) {
        int n = tasks.length;
        Integer[] byStart = new Integer[n];
        for (int i = 0; i < n; i++) byStart[i] = i;
        Arrays.sort(byStart, (a, b) -> tasks[a][0] - tasks[b][0]);
        PriorityQueue<Integer> ready = new PriorityQueue<>((a, b) -> tasks[a][1] != tasks[b][1] ? tasks[a][1] - tasks[b][1] : a - b);
        int[] order = new int[n];
        long time = 0;
        int p = 0, o = 0;
        while (o < n) {
            if (ready.isEmpty() && time < tasks[byStart[p]][0]) time = tasks[byStart[p]][0]; // CPU idle: jump ahead
            while (p < n && tasks[byStart[p]][0] <= time) ready.add(byStart[p++]);
            int t = ready.poll();
            order[o++] = t;
            time += tasks[t][1];
        }
        return order;
    }

    // ---------- Minimize Deviation in Array (LC 1675): odds can only double, evens can only halve ----------
    // Double every odd once (now only halving remains), then repeatedly halve the max while it is even.
    static int minimumDeviation(int[] nums) {
        PriorityQueue<Integer> pq = new PriorityQueue<>(Collections.reverseOrder());
        int min = Integer.MAX_VALUE;
        for (int x : nums) {
            if (x % 2 == 1) x *= 2;
            pq.add(x);
            min = Math.min(min, x);
        }
        int best = pq.peek() - min;
        while (pq.peek() % 2 == 0) {
            int mx = pq.poll() / 2;
            pq.add(mx);
            min = Math.min(min, mx);
            best = Math.min(best, pq.peek() - min);
        }
        return best;
    }

    // ---------- Kth Largest Element in a Stream (LC 703) ----------
    static class KthLargest {
        private final PriorityQueue<Integer> pq = new PriorityQueue<>();
        private final int k;
        KthLargest(int k, int[] nums) { this.k = k; for (int x : nums) add(x); }
        int add(int v) { pq.add(v); if (pq.size() > k) pq.poll(); return pq.peek(); }
    }

    // ---------- Indexed min-priority-queue with decrease-key ----------
    // RECOGNIZE: Dijkstra / Prim written with a true decrease-key instead of "push duplicates and skip stale".
    static class IndexedMinPQ {
        private final int[] heap, pos; // heap[i] = item id; pos[id] = index in heap or -1
        private final long[] key;
        private int size;
        IndexedMinPQ(int capacity) {
            heap = new int[capacity];
            pos = new int[capacity];
            key = new long[capacity];
            Arrays.fill(pos, -1);
        }
        boolean contains(int id) { return pos[id] != -1; }
        boolean isEmpty() { return size == 0; }
        void insert(int id, long k) { key[id] = k; heap[size] = id; pos[id] = size; up(size++); }
        void decreaseKey(int id, long k) { if (k < key[id]) { key[id] = k; up(pos[id]); } }
        int extractMin() {
            int top = heap[0];
            swap(0, --size);
            pos[top] = -1;
            if (size > 0) down(0);
            return top;
        }
        long keyOf(int id) { return key[id]; }
        private void up(int i) { while (i > 0 && key[heap[i]] < key[heap[(i - 1) / 2]]) { swap(i, (i - 1) / 2); i = (i - 1) / 2; } }
        private void down(int i) {
            while (true) {
                int l = 2 * i + 1, r = l + 1, s = i;
                if (l < size && key[heap[l]] < key[heap[s]]) s = l;
                if (r < size && key[heap[r]] < key[heap[s]]) s = r;
                if (s == i) return;
                swap(i, s);
                i = s;
            }
        }
        private void swap(int i, int j) {
            int t = heap[i]; heap[i] = heap[j]; heap[j] = t;
            pos[heap[i]] = i;
            pos[heap[j]] = j;
        }
    }
}
