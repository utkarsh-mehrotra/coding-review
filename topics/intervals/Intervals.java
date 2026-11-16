import java.util.*;

public class Intervals {

    // ALWAYS first decide: sort by START (merge, insert, overlaps) or by END (max non-overlapping, min arrows).

    // Merge Intervals (LC 56)
    static int[][] merge(int[][] iv) {
        Arrays.sort(iv, (a, b) -> Integer.compare(a[0], b[0]));
        List<int[]> out = new ArrayList<>();
        for (int[] cur : iv) {
            if (out.isEmpty() || out.get(out.size() - 1)[1] < cur[0]) out.add(cur.clone());
            else out.get(out.size() - 1)[1] = Math.max(out.get(out.size() - 1)[1], cur[1]);
        }
        return out.toArray(new int[0][]);
    }

    // Insert Interval (LC 57): before / overlapping / after, one pass
    static int[][] insert(int[][] iv, int[] add) {
        List<int[]> out = new ArrayList<>();
        int i = 0, n = iv.length;
        while (i < n && iv[i][1] < add[0]) out.add(iv[i++]);
        int[] cur = add.clone();
        while (i < n && iv[i][0] <= cur[1]) { cur[0] = Math.min(cur[0], iv[i][0]); cur[1] = Math.max(cur[1], iv[i][1]); i++; }
        out.add(cur);
        while (i < n) out.add(iv[i++]);
        return out.toArray(new int[0][]);
    }

    // Non-overlapping Intervals (LC 435): keep the interval with the EARLIEST END; count removals
    static int eraseOverlapIntervals(int[][] iv) {
        Arrays.sort(iv, (a, b) -> Integer.compare(a[1], b[1]));
        int removed = 0, end = Integer.MIN_VALUE;
        for (int[] x : iv) {
            if (x[0] >= end) end = x[1]; else removed++;
        }
        return removed;
    }

    // Minimum Arrows to Burst Balloons (LC 452): sort by end, one arrow per cluster
    static int findMinArrowShots(int[][] pts) {
        Arrays.sort(pts, (a, b) -> Integer.compare(a[1], b[1]));
        int arrows = 0;
        long end = Long.MIN_VALUE;
        for (int[] p : pts) if (p[0] > end) { arrows++; end = p[1]; }
        return arrows;
    }

    // Interval List Intersections (LC 986): two pointers, advance the one that ends first
    static int[][] intervalIntersection(int[][] a, int[][] b) {
        List<int[]> out = new ArrayList<>();
        int i = 0, j = 0;
        while (i < a.length && j < b.length) {
            int lo = Math.max(a[i][0], b[j][0]), hi = Math.min(a[i][1], b[j][1]);
            if (lo <= hi) out.add(new int[]{lo, hi});
            if (a[i][1] < b[j][1]) i++; else j++;
        }
        return out.toArray(new int[0][]);
    }

    // Meeting Rooms (LC 252): any overlap?
    static boolean canAttendMeetings(int[][] iv) {
        Arrays.sort(iv, (a, b) -> a[0] - b[0]);
        for (int i = 1; i < iv.length; i++) if (iv[i][0] < iv[i - 1][1]) return false;
        return true;
    }

    // ---------- Sweep line with events: max concurrent overlap (rooms) ----------
    // Ends sort BEFORE starts at the same time when [s, e) is half-open.
    static int maxOverlap(int[][] iv) {
        int[] starts = new int[iv.length], ends = new int[iv.length];
        for (int i = 0; i < iv.length; i++) { starts[i] = iv[i][0]; ends[i] = iv[i][1]; }
        Arrays.sort(starts);
        Arrays.sort(ends);
        int e = 0, cur = 0, best = 0;
        for (int s : starts) {
            while (ends[e] <= s) { e++; cur--; }
            cur++;
            best = Math.max(best, cur);
        }
        return best;
    }

    // Car Pooling (LC 1094): difference array over positions
    static boolean carPooling(int[][] trips, int capacity) {
        int[] diff = new int[1002];
        for (int[] t : trips) { diff[t[1]] += t[0]; diff[t[2]] -= t[0]; }
        int cur = 0;
        for (int d : diff) { cur += d; if (cur > capacity) return false; }
        return true;
    }

    // My Calendar I (LC 729): TreeMap floor / ceiling check
    static class MyCalendar {
        private final TreeMap<Integer, Integer> m = new TreeMap<>(); // start -> end
        boolean book(int s, int e) {
            Integer prev = m.floorKey(s), next = m.ceilingKey(s);
            if ((prev != null && m.get(prev) > s) || (next != null && next < e)) return false;
            m.put(s, e);
            return true;
        }
    }

    // My Calendar III (LC 732): max k-booking via TreeMap of +1 / -1 deltas
    static class MyCalendarThree {
        private final TreeMap<Integer, Integer> delta = new TreeMap<>();
        int book(int s, int e) {
            delta.merge(s, 1, Integer::sum);
            delta.merge(e, -1, Integer::sum);
            int cur = 0, best = 0;
            for (int d : delta.values()) { cur += d; best = Math.max(best, cur); }
            return best;
        }
    }

    // Employee Free Time (LC 759): merge all busy intervals, gaps are free
    static List<int[]> employeeFreeTime(List<List<int[]>> schedule) {
        List<int[]> all = new ArrayList<>();
        for (List<int[]> s : schedule) all.addAll(s);
        all.sort((a, b) -> a[0] - b[0]);
        List<int[]> free = new ArrayList<>();
        int end = all.get(0)[1];
        for (int[] x : all) {
            if (x[0] > end) free.add(new int[]{end, x[0]});
            end = Math.max(end, x[1]);
        }
        return free;
    }

    // Remove Covered Intervals (LC 1288)
    static int removeCoveredIntervals(int[][] iv) {
        Arrays.sort(iv, (a, b) -> a[0] != b[0] ? a[0] - b[0] : b[1] - a[1]);
        int count = 0, maxEnd = 0;
        for (int[] x : iv) if (x[1] > maxEnd) { count++; maxEnd = x[1]; }
        return count;
    }

    // Data Stream as Disjoint Intervals (LC 352)
    static class SummaryRanges {
        private final TreeMap<Integer, Integer> m = new TreeMap<>(); // start -> end
        void addNum(int v) {
            Integer lo = m.floorKey(v);
            if (lo != null && m.get(lo) >= v) return;
            boolean joinLeft = lo != null && m.get(lo) == v - 1, joinRight = m.containsKey(v + 1);
            if (joinLeft && joinRight) { m.put(lo, m.remove(v + 1)); }
            else if (joinLeft) m.put(lo, v);
            else if (joinRight) { m.put(v, m.remove(v + 1)); }
            else m.put(v, v);
        }
        int[][] getIntervals() {
            int[][] r = new int[m.size()][];
            int i = 0;
            for (Map.Entry<Integer, Integer> e : m.entrySet()) r[i++] = new int[]{e.getKey(), e.getValue()};
            return r;
        }
    }
}
