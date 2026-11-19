import java.util.*;

public class IntervalsVariantsTest {
    public static void main(String[] x) {
        Random r = new Random(21);
        // ---------- intervals ----------
        Check.eq("minInterval", Check.s(IntervalsVariants.minInterval(new int[][]{{1, 4}, {2, 4}, {3, 6}, {4, 4}}, new int[]{2, 3, 4, 5})), "[3, 3, 1, 4]");
        Check.eq("maxEvents", IntervalsVariants.maxEvents(new int[][]{{1, 2}, {2, 3}, {3, 4}, {1, 2}}), 4);
        Check.eq("mostBooked", IntervalsVariants.mostBooked(2, new int[][]{{0, 10}, {1, 5}, {2, 7}, {3, 4}}) + "," + IntervalsVariants.mostBooked(3, new int[][]{{1, 20}, {2, 10}, {3, 5}, {4, 9}, {6, 8}}), "0,1");
        Check.eq("minGroups", IntervalsVariants.minGroups(new int[][]{{5, 10}, {6, 8}, {1, 5}, {2, 3}, {1, 10}}) + "," + IntervalsVariants.minGroups(new int[][]{{1, 3}, {5, 6}, {8, 10}, {11, 13}}), "3,1");
        Check.eq("videoStitch", IntervalsVariants.videoStitching(new int[][]{{0, 2}, {4, 6}, {8, 10}, {1, 9}, {1, 5}, {5, 9}}, 10), 3);
        Check.eq("taps", IntervalsVariants.minTaps(5, new int[]{3, 4, 1, 1, 0, 0}), 1);
        Check.eq("removeInterval", IntervalsVariants.removeInterval(new int[][]{{0, 2}, {3, 4}, {5, 7}}, new int[]{1, 6}), "[[0, 1], [6, 7]]");
        IntervalsVariants.RangeModule rm = new IntervalsVariants.RangeModule();
        rm.addRange(10, 20); rm.removeRange(14, 16);
        Check.eq("rangeModule", rm.queryRange(10, 14) + "," + rm.queryRange(13, 15) + "," + rm.queryRange(16, 17), "true,false,true");
        Check.eq("rectArea", IntervalsVariants.rectangleArea(new int[][]{{0, 0, 2, 2}, {1, 0, 2, 3}, {1, 0, 3, 1}}), 6);
        Check.eq("rectArea2", IntervalsVariants.rectangleArea(new int[][]{{0, 0, 1000000000, 1000000000}}), 49);
        for (int t = 0; t < 200; t++) {
            int m = 1 + r.nextInt(6);
            int[][] iv = new int[m][2];
            for (int i = 0; i < m; i++) { int s = r.nextInt(20); iv[i] = new int[]{s, s + r.nextInt(8)}; }
            IntervalsVariants.StabbingCounter sc = new IntervalsVariants.StabbingCounter(iv);
            int p = r.nextInt(30), brute = 0;
            for (int[] v : iv) if (v[0] <= p && p <= v[1]) brute++;
            Check.eq("stabRand", sc.count(p), brute);

            // minInterval vs brute
            int[] qs = {r.nextInt(25), r.nextInt(25), r.nextInt(25)};
            int[] got = IntervalsVariants.minInterval(iv.clone(), qs);
            for (int qi = 0; qi < 3; qi++) {
                int best = -1;
                for (int[] v : iv) if (v[0] <= qs[qi] && qs[qi] <= v[1]) { int sz = v[1] - v[0] + 1; if (best == -1 || sz < best) best = sz; }
                Check.eq("minIntervalRand", got[qi], best);
            }
            // rectangle area vs grid
            int rc = 1 + r.nextInt(4);
            int[][] rects = new int[rc][];
            boolean[][] grid = new boolean[12][12];
            for (int i = 0; i < rc; i++) {
                int x1 = r.nextInt(8), y1 = r.nextInt(8);
                rects[i] = new int[]{x1, y1, x1 + 1 + r.nextInt(4), y1 + 1 + r.nextInt(4)};
                for (int a = rects[i][0]; a < rects[i][2]; a++) for (int c = rects[i][1]; c < rects[i][3]; c++) grid[a][c] = true;
            }
            int area = 0;
            for (boolean[] row : grid) for (boolean v : row) if (v) area++;
            Check.eq("rectAreaRand", IntervalsVariants.rectangleArea(rects), area);

            // RangeModule vs boolean array
            IntervalsVariants.RangeModule mod = new IntervalsVariants.RangeModule();
            boolean[] cells = new boolean[40];
            for (int op = 0; op < 20; op++) {
                int l = r.nextInt(30), rr = l + 1 + r.nextInt(8), kind = r.nextInt(3);
                if (kind == 0) { mod.addRange(l, rr); for (int c = l; c < rr; c++) cells[c] = true; }
                else if (kind == 1) { mod.removeRange(l, rr); for (int c = l; c < rr; c++) cells[c] = false; }
                else { boolean all = true; for (int c = l; c < rr; c++) all &= cells[c]; Check.eq("rangeModRand", mod.queryRange(l, rr), all); }
            }
        }

        Check.done("IntervalsVariantsTest");
    }

}
