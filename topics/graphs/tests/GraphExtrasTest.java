import java.util.*;

public class GraphExtrasTest {
    public static void main(String[] x) {
        int[][] h = {{1, 2, 2}, {3, 8, 2}, {5, 3, 5}};
        Check.eq("effortDSU", DSUExtended.minimumEffortPathDSU(h), 2);
        Check.eq("effortAgree", DSUExtended.minimumEffortPathDSU(h), DijkstraVariants.minimumEffortPath(h));
        Random r = new Random(3);
        for (int t = 0; t < 200; t++) {
            int m = 1 + r.nextInt(4), n = 1 + r.nextInt(4);
            int[][] g = new int[m][n];
            for (int[] row : g) for (int j = 0; j < n; j++) row[j] = r.nextInt(10);
            Check.eq("effortRand", DSUExtended.minimumEffortPathDSU(g), DijkstraVariants.minimumEffortPath(g));
        }
        List<List<String>> eq = List.of(List.of("a", "b"), List.of("b", "c"));
        double[] v = {2.0, 3.0};
        List<List<String>> q = List.of(List.of("a", "c"), List.of("b", "a"), List.of("a", "e"), List.of("a", "a"), List.of("x", "x"));
        Check.eq("calcDFS", Arrays.toString(DSUExtended.calcEquationDFS(eq, v, q)), "[6.0, 0.5, -1.0, 1.0, -1.0]");
        Check.eq("calcAgree", Arrays.toString(DSUExtended.calcEquationDFS(eq, v, q)), Arrays.toString(DSUExtended.calcEquation(eq, v, q)));
        {
        // 1.3 bridges / articulation points with multi-edges (brute force: remove each edge / vertex)
        Random br = new Random(11);
        for (int t = 0; t < 400; t++) {
            int n = 1 + br.nextInt(7), m = br.nextInt(10);
            List<int[]> es = new ArrayList<>();
            List<List<Integer>> adj = new ArrayList<>();
            for (int i = 0; i < n; i++) adj.add(new ArrayList<>());
            for (int i = 0; i < m && n > 1; i++) {
                int u = br.nextInt(n), w = br.nextInt(n);
                if (u == w) continue;
                es.add(new int[]{u, w});
                adj.get(u).add(w);
                adj.get(w).add(u);
                if (br.nextInt(4) == 0) { es.add(new int[]{u, w}); adj.get(u).add(w); adj.get(w).add(u); } // doubled edge
            }
            SCC_Bridges_2SAT.findBridgesAndArticulation(n, adj);
            Set<String> got = new TreeSet<>();
            for (int[] b : SCC_Bridges_2SAT.bridges) got.add(Math.min(b[0], b[1]) + "-" + Math.max(b[0], b[1]));
            Set<String> want = new TreeSet<>();
            int base = comps(n, es, -1, -1);
            for (int i = 0; i < es.size(); i++)
                if (comps(n, es, i, -1) > base) want.add(Math.min(es.get(i)[0], es.get(i)[1]) + "-" + Math.max(es.get(i)[0], es.get(i)[1]));
            Check.eq("bridgesRand", got.toString(), want.toString());
            Set<Integer> wantAp = new TreeSet<>();
            for (int z = 0; z < n; z++) if (comps(n, es, -1, z) > base) wantAp.add(z);
            Check.eq("apRand", new TreeSet<>(SCC_Bridges_2SAT.articulationPoints).toString(), wantAp.toString());
        }
        List<List<Integer>> dbl = List.of(List.of(1, 1), List.of(0, 0));
        SCC_Bridges_2SAT.findBridgesAndArticulation(2, dbl);
        Check.eq("doubledEdgeNotBridge", SCC_Bridges_2SAT.bridges.size(), 0);

        // 1.14 2-SAT assignment: satisfies every clause; unsat agrees with brute force
        Random sr = new Random(5);
        for (int t = 0; t < 600; t++) {
            int nv = 1 + sr.nextInt(5), mc = sr.nextInt(12);
            List<int[]> cl = new ArrayList<>();
            for (int i = 0; i < mc; i++) cl.add(new int[]{sr.nextInt(nv), sr.nextInt(2), sr.nextInt(nv), sr.nextInt(2)});
            boolean sat = false;
            for (int mask = 0; mask < (1 << nv) && !sat; mask++) sat = holds(cl, mask, nv);
            boolean[] asg = SCC_Bridges_2SAT.solve2SATAssignment(nv, cl);
            Check.eq("2satAgrees", asg != null, sat);
            Check.eq("2satBool", SCC_Bridges_2SAT.solve2SAT(nv, cl), sat);
            if (asg != null) {
                int mask = 0;
                for (int i = 0; i < nv; i++) if (asg[i]) mask |= 1 << i;
                Check.eq("2satAssignmentValid", holds(cl, mask, nv), true);
            }
        }

        // 1.9 Held-Karp single node
        Check.eq("heldKarpN1", BipartiteAndBitmaskDP.heldKarpTSP(new int[][]{{0}}), 0);
        }
        Check.done("GraphExtrasTest");
    }

    static boolean holds(List<int[]> cl, int mask, int nv) {
        for (int[] c : cl) {
            boolean a = ((mask >> c[0]) & 1) == 1, b = ((mask >> c[2]) & 1) == 1;
            if (!((c[1] == 1 ? !a : a) || (c[3] == 1 ? !b : b))) return false;
        }
        return true;
    }

    // component count, ignoring edge index skipE and vertex skipV (vertex not counted)
    static int comps(int n, List<int[]> es, int skipE, int skipV) {
        int[] p = new int[n];
        for (int i = 0; i < n; i++) p[i] = i;
        for (int i = 0; i < es.size(); i++) {
            if (i == skipE || es.get(i)[0] == skipV || es.get(i)[1] == skipV) continue;
            int a = es.get(i)[0], b = es.get(i)[1];
            while (p[a] != a) a = p[a];
            while (p[b] != b) b = p[b];
            p[a] = b;
        }
        int c = 0;
        for (int i = 0; i < n; i++) if (i != skipV && p[i] == i) c++;
        return c;
    }
}
