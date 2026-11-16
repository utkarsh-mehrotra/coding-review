import java.util.*;

public class IntervalsVariants {

    // ---------- Minimum Interval to Include Each Query (LC 1851): offline, sort queries, heap by (size, end) ----------
    // RECOGNIZE: many point queries against many intervals, answers need not be online -> sort both and sweep.
    static int[] minInterval(int[][] intervals, int[] queries) {
        Arrays.sort(intervals, (a, b) -> a[0] - b[0]);
        int q = queries.length;
        Integer[] order = new Integer[q];
        for (int i = 0; i < q; i++) order[i] = i;
        Arrays.sort(order, (a, b) -> queries[a] - queries[b]);
        PriorityQueue<int[]> pq = new PriorityQueue<>((a, b) -> a[0] != b[0] ? a[0] - b[0] : a[1] - b[1]); // {size, end}
        int[] ans = new int[q];
        int p = 0;
        for (int qi : order) {
            int x = queries[qi];
            while (p < intervals.length && intervals[p][0] <= x) { pq.add(new int[]{intervals[p][1] - intervals[p][0] + 1, intervals[p][1]}); p++; }
            while (!pq.isEmpty() && pq.peek()[1] < x) pq.poll(); // lazily drop intervals that already ended
            ans[qi] = pq.isEmpty() ? -1 : pq.peek()[0];
        }
        return ans;
    }

    // ---------- Maximum Number of Events You Can Attend (LC 1353): each day attend the event that ends soonest ----------
    static int maxEvents(int[][] events) {
        Arrays.sort(events, (a, b) -> a[0] - b[0]);
        PriorityQueue<Integer> ends = new PriorityQueue<>();
        int i = 0, day = 0, count = 0, n = events.length;
        while (i < n || !ends.isEmpty()) {
            if (ends.isEmpty()) day = events[i][0];
            while (i < n && events[i][0] <= day) ends.add(events[i++][1]);
            while (!ends.isEmpty() && ends.peek() < day) ends.poll();
            if (!ends.isEmpty()) { ends.poll(); count++; }
            day++;
        }
        return count;
    }

    // ---------- Meeting Rooms III (LC 2402): free-room heap + busy heap (end, room) ----------
    static int mostBooked(int n, int[][] meetings) {
        Arrays.sort(meetings, (a, b) -> a[0] - b[0]);
        PriorityQueue<Integer> free = new PriorityQueue<>();
        for (int i = 0; i < n; i++) free.add(i);
        PriorityQueue<long[]> busy = new PriorityQueue<>((a, b) -> a[0] != b[0] ? Long.compare(a[0], b[0]) : Long.compare(a[1], b[1]));
        int[] used = new int[n];
        for (int[] m : meetings) {
            while (!busy.isEmpty() && busy.peek()[0] <= m[0]) free.add((int) busy.poll()[1]);
            if (!free.isEmpty()) {
                int r = free.poll();
                used[r]++;
                busy.add(new long[]{m[1], r});
            } else {
                long[] t = busy.poll(); // wait for the earliest room, meeting keeps its duration
                used[(int) t[1]]++;
                busy.add(new long[]{t[0] + (m[1] - m[0]), t[1]});
            }
        }
        int best = 0;
        for (int i = 1; i < n; i++) if (used[i] > used[best]) best = i;
        return best;
    }

    // ---------- Divide Intervals Into Minimum Number of Groups (LC 2406) ----------
    static int minGroups(int[][] intervals) {
        Arrays.sort(intervals, (a, b) -> a[0] - b[0]);
        PriorityQueue<Integer> ends = new PriorityQueue<>();
        for (int[] iv : intervals) {
            if (!ends.isEmpty() && ends.peek() < iv[0]) ends.poll(); // closed intervals: touching at a point conflicts
            ends.add(iv[1]);
        }
        return ends.size();
    }

    // ---------- Video Stitching (LC 1024) / Minimum Taps (LC 1326): jump-game on intervals ----------
    // Normalize to "furthest right end reachable from each left start", then run Jump Game II.
    static int videoStitching(int[][] clips, int time) {
        int[] far = new int[time + 1];
        for (int[] c : clips) if (c[0] <= time) far[c[0]] = Math.max(far[c[0]], Math.min(c[1], time));
        int count = 0, curEnd = 0, reach = 0;
        for (int i = 0; i < time; i++) {
            reach = Math.max(reach, far[i]);
            if (i == curEnd) {
                if (reach <= i) return -1;
                count++;
                curEnd = reach;
            }
        }
        return count;
    }

    static int minTaps(int n, int[] ranges) {
        int[] far = new int[n + 1];
        for (int i = 0; i <= n; i++) {
            int l = Math.max(0, i - ranges[i]), r = Math.min(n, i + ranges[i]);
            far[l] = Math.max(far[l], r);
        }
        int taps = 0, curEnd = 0, reach = 0;
        for (int i = 0; i < n; i++) {
            reach = Math.max(reach, far[i]);
            if (i == curEnd) {
                if (reach <= i) return -1;
                taps++;
                curEnd = reach;
            }
        }
        return taps;
    }

    // ---------- Remove Interval (LC 1272): subtract [lo, hi) from a sorted disjoint list ----------
    static List<List<Integer>> removeInterval(int[][] iv, int[] rm) {
        List<List<Integer>> res = new ArrayList<>();
        for (int[] x : iv) {
            if (x[1] <= rm[0] || x[0] >= rm[1]) res.add(List.of(x[0], x[1]));
            else {
                if (x[0] < rm[0]) res.add(List.of(x[0], rm[0]));
                if (x[1] > rm[1]) res.add(List.of(rm[1], x[1]));
            }
        }
        return res;
    }

    // ---------- Range Module (LC 715): TreeMap of disjoint half-open ranges start -> end ----------
    static class RangeModule {
        private final TreeMap<Integer, Integer> m = new TreeMap<>();

        void addRange(int l, int r) {
            Map.Entry<Integer, Integer> e = m.floorEntry(l);
            if (e != null && e.getValue() >= l) { l = e.getKey(); r = Math.max(r, e.getValue()); } // touching ranges merge
            while (true) {
                Map.Entry<Integer, Integer> n = m.ceilingEntry(l);
                if (n == null || n.getKey() > r) break;
                r = Math.max(r, n.getValue());
                m.remove(n.getKey());
            }
            m.put(l, r);
        }

        boolean queryRange(int l, int r) {
            Map.Entry<Integer, Integer> e = m.floorEntry(l);
            return e != null && e.getValue() >= r;
        }

        void removeRange(int l, int r) {
            Map.Entry<Integer, Integer> e = m.lowerEntry(l);
            if (e != null && e.getValue() > l) { // left piece survives
                int end = e.getValue();
                m.put(e.getKey(), l);
                if (end > r) m.put(r, end);
            }
            while (true) {
                Map.Entry<Integer, Integer> n = m.ceilingEntry(l);
                if (n == null || n.getKey() >= r) break;
                m.remove(n.getKey());
                if (n.getValue() > r) m.put(r, n.getValue());
            }
        }
    }

    // ---------- Rectangle Area II (LC 850): area of the union, sweep x with coordinate-compressed y coverage ----------
    static int rectangleArea(int[][] rects) {
        final long MOD = 1_000_000_007L;
        TreeSet<Integer> ys = new TreeSet<>();
        List<int[]> ev = new ArrayList<>(); // {x, +1/-1, y1, y2}
        for (int[] r : rects) {
            ys.add(r[1]); ys.add(r[3]);
            ev.add(new int[]{r[0], 1, r[1], r[3]});
            ev.add(new int[]{r[2], -1, r[1], r[3]});
        }
        ev.sort((a, b) -> a[0] - b[0]);
        Integer[] yv = ys.toArray(new Integer[0]);
        Map<Integer, Integer> id = new HashMap<>();
        for (int i = 0; i < yv.length; i++) id.put(yv[i], i);
        int[] cover = new int[yv.length];
        long area = 0;
        int prevX = ev.get(0)[0];
        for (int[] e : ev) {
            long covered = 0; // total covered y-length with the current multiset of active rectangles
            for (int i = 0; i + 1 < yv.length; i++) if (cover[i] > 0) covered += yv[i + 1] - yv[i];
            area = (area + covered % MOD * (e[0] - prevX)) % MOD;
            for (int i = id.get(e[2]); i < id.get(e[3]); i++) cover[i] += e[1];
            prevX = e[0];
        }
        return (int) area;
    }

    // ---------- Stabbing counts: how many intervals contain point p? Two sorted arrays, no tree needed ----------
    // count(start <= p) - count(end < p). Use for static data with many queries.
    static class StabbingCounter {
        private final int[] starts, ends;
        StabbingCounter(int[][] iv) {
            starts = new int[iv.length];
            ends = new int[iv.length];
            for (int i = 0; i < iv.length; i++) { starts[i] = iv[i][0]; ends[i] = iv[i][1]; }
            Arrays.sort(starts);
            Arrays.sort(ends);
        }
        int count(int p) { return upper(starts, p) - lower(ends, p); }
        private static int upper(int[] a, int x) { int lo = 0, hi = a.length; while (lo < hi) { int m = (lo + hi) >>> 1; if (a[m] > x) hi = m; else lo = m + 1; } return lo; }
        private static int lower(int[] a, int x) { int lo = 0, hi = a.length; while (lo < hi) { int m = (lo + hi) >>> 1; if (a[m] >= x) hi = m; else lo = m + 1; } return lo; }
    }
}
