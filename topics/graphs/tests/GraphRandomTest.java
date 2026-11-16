import java.util.*;

// Randomized cross-checks for the graph templates that are easy to get subtly wrong:
// max-flow (vs brute-force min cut), Tarjan / Kosaraju SCC (vs transitive closure), 2-SAT (vs enumerating assignments).
public class GraphRandomTest {
    static Random rnd = new Random(271828);

    public static void main(String[] x) {
        flow();
        scc();
        twoSat();
        Check.done("GraphRandomTest");
    }

    static void flow() {
        for (int t = 0; t < 400; t++) {
            int n = 2 + rnd.nextInt(6);
            int[][] cap = new int[n][n];
            for (int i = 0; i < n; i++) for (int j = 0; j < n; j++) if (i != j && rnd.nextInt(3) > 0) cap[i][j] = rnd.nextInt(8);
            int s = 0, tt = n - 1, minCut = Integer.MAX_VALUE;
            for (int mask = 0; mask < 1 << n; mask++) {
                if ((mask & 1) == 0 || (mask >> tt & 1) != 0) continue; // source in S, sink out of S
                int cut = 0;
                for (int i = 0; i < n; i++) for (int j = 0; j < n; j++) if ((mask >> i & 1) != 0 && (mask >> j & 1) == 0) cut += cap[i][j];
                minCut = Math.min(minCut, cut);
            }
            Check.eq("maxflow = mincut", NetworkFlow.edmondsKarp(n, cap, s, tt), minCut);
        }
    }

    static List<List<Integer>> graph(int n, double p) {
        List<List<Integer>> adj = new ArrayList<>();
        for (int i = 0; i < n; i++) adj.add(new ArrayList<>());
        for (int i = 0; i < n; i++) for (int j = 0; j < n; j++) if (i != j && rnd.nextDouble() < p) adj.get(i).add(j);
        return adj;
    }

    static String partition(List<List<Integer>> sccs) {
        List<String> parts = new ArrayList<>();
        for (List<Integer> c : sccs) { List<Integer> cc = new ArrayList<>(c); Collections.sort(cc); parts.add(cc.toString()); }
        Collections.sort(parts);
        return parts.toString();
    }

    static void scc() {
        for (int t = 0; t < 400; t++) {
            int n = 1 + rnd.nextInt(8);
            List<List<Integer>> adj = graph(n, 0.05 + rnd.nextDouble() * 0.3);
            boolean[][] reach = new boolean[n][n];
            for (int i = 0; i < n; i++) { reach[i][i] = true; for (int j : adj.get(i)) reach[i][j] = true; }
            for (int k = 0; k < n; k++) for (int i = 0; i < n; i++) for (int j = 0; j < n; j++) if (reach[i][k] && reach[k][j]) reach[i][j] = true;
            boolean[] done = new boolean[n];
            List<List<Integer>> want = new ArrayList<>();
            for (int i = 0; i < n; i++) {
                if (done[i]) continue;
                List<Integer> comp = new ArrayList<>();
                for (int j = 0; j < n; j++) if (reach[i][j] && reach[j][i]) { comp.add(j); done[j] = true; }
                want.add(comp);
            }
            Check.eq("tarjan", partition(SCC_Bridges_2SAT.tarjanSCC(n, adj)), partition(want));
            Check.eq("kosaraju", partition(SCC_Bridges_2SAT.kosarajuSCC(n, adj)), partition(want));
        }
    }

    static void twoSat() {
        for (int t = 0; t < 600; t++) {
            int vars = 1 + rnd.nextInt(5), m = rnd.nextInt(10);
            List<int[]> clauses = new ArrayList<>();
            for (int i = 0; i < m; i++) clauses.add(new int[]{rnd.nextInt(vars), rnd.nextInt(2), rnd.nextInt(vars), rnd.nextInt(2)});
            boolean sat = false;
            for (int assign = 0; assign < 1 << vars && !sat; assign++) {
                boolean ok = true;
                for (int[] c : clauses) {
                    boolean a = ((assign >> c[0] & 1) == 1) != (c[1] == 1); // negA = 1 means literal is NOT var
                    boolean b = ((assign >> c[2] & 1) == 1) != (c[3] == 1);
                    if (!(a || b)) { ok = false; break; }
                }
                sat = ok;
            }
            Check.eq("2sat", SCC_Bridges_2SAT.solve2SAT(vars, clauses), sat);
        }
    }
}
