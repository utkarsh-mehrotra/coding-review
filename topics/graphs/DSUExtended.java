import java.util.*;

public class DSUExtended {

    // ---------- DSU tracking component sizes (iterative find = no recursion) ----------
    static class DSUSize {
        int[] parent, size;
        int components, maxSize = 1;

        DSUSize(int n) {
            parent = new int[n];
            size = new int[n];
            components = n;
            for (int i = 0; i < n; i++) { parent[i] = i; size[i] = 1; }
        }

        int find(int x) { // path halving
            while (parent[x] != x) {
                parent[x] = parent[parent[x]];
                x = parent[x];
            }
            return x;
        }

        boolean union(int a, int b) {
            int ra = find(a), rb = find(b);
            if (ra == rb) return false;
            if (size[ra] < size[rb]) { int t = ra; ra = rb; rb = t; }
            parent[rb] = ra;
            size[ra] += size[rb];
            maxSize = Math.max(maxSize, size[ra]);
            components--;
            return true;
        }

        int sizeOf(int x) { return size[find(x)]; }
    }

    // ---------- DSU on a grid, online: Number of Islands II (LC 305) ----------
    // Cell (r, c) -> id r*n + c. Each new land cell adds 1 island, each successful union removes 1.
    static List<Integer> numIslands2(int m, int n, int[][] positions) {
        DSUSize dsu = new DSUSize(m * n);
        boolean[] land = new boolean[m * n];
        int[][] dirs = {{1, 0}, {-1, 0}, {0, 1}, {0, -1}};
        int islands = 0;
        List<Integer> res = new ArrayList<>();
        for (int[] p : positions) {
            int id = p[0] * n + p[1];
            if (!land[id]) {
                land[id] = true;
                islands++;
                for (int[] d : dirs) {
                    int nr = p[0] + d[0], nc = p[1] + d[1];
                    if (nr < 0 || nr >= m || nc < 0 || nc >= n) continue;
                    int nid = nr * n + nc;
                    if (land[nid] && dsu.union(id, nid)) islands--;
                }
            }
            res.add(islands);
        }
        return res;
    }

    // ---------- String keys: Accounts Merge (LC 721) ----------
    // DSU over account indices; the first account that owns an email becomes its representative.
    static List<List<String>> accountsMerge(List<List<String>> accounts) {
        int n = accounts.size();
        DSUSize dsu = new DSUSize(n);
        Map<String, Integer> emailOwner = new HashMap<>();
        for (int i = 0; i < n; i++) {
            for (int j = 1; j < accounts.get(i).size(); j++) {
                Integer prev = emailOwner.putIfAbsent(accounts.get(i).get(j), i);
                if (prev != null) dsu.union(i, prev);
            }
        }
        Map<Integer, TreeSet<String>> groups = new HashMap<>();
        for (Map.Entry<String, Integer> en : emailOwner.entrySet()) {
            groups.computeIfAbsent(dsu.find(en.getValue()), k -> new TreeSet<>()).add(en.getKey());
        }
        List<List<String>> res = new ArrayList<>();
        for (Map.Entry<Integer, TreeSet<String>> g : groups.entrySet()) {
            List<String> row = new ArrayList<>();
            row.add(accounts.get(g.getKey()).get(0)); // name
            row.addAll(g.getValue());                 // sorted emails
            res.add(row);
        }
        return res;
    }

    // ---------- Redundant Connection (LC 684): the edge whose union fails closes the cycle ----------
    static int[] findRedundantConnection(int[][] edges) {
        DSUSize dsu = new DSUSize(edges.length + 1);
        for (int[] e : edges) if (!dsu.union(e[0], e[1])) return e;
        return new int[0];
    }

    // ---------- Weighted DSU: Evaluate Division (LC 399) ----------
    // w[x] = value(x) / value(parent[x]); after path compression w[x] = value(x) / value(root).
    static class WeightedDSU {
        int[] parent;
        double[] w;

        WeightedDSU(int n) {
            parent = new int[n];
            w = new double[n];
            for (int i = 0; i < n; i++) { parent[i] = i; w[i] = 1.0; }
        }

        int find(int x) {
            if (parent[x] != x) {
                int p = parent[x];
                parent[x] = find(p);
                w[x] *= w[p]; // w[p] is now value(p)/value(root)
            }
            return parent[x];
        }

        // Records value(a) / value(b) = ratio.
        void union(int a, int b, double ratio) {
            int ra = find(a), rb = find(b);
            if (ra == rb) return;
            parent[ra] = rb;
            w[ra] = ratio * w[b] / w[a];
        }

        // value(a) / value(b), or -1 if the two are not connected.
        double query(int a, int b) {
            if (find(a) != find(b)) return -1.0;
            return w[a] / w[b];
        }
    }

    static double[] calcEquation(List<List<String>> equations, double[] values, List<List<String>> queries) {
        Map<String, Integer> id = new HashMap<>();
        for (List<String> eq : equations) for (String s : eq) id.putIfAbsent(s, id.size());
        WeightedDSU dsu = new WeightedDSU(id.size());
        for (int i = 0; i < equations.size(); i++) {
            dsu.union(id.get(equations.get(i).get(0)), id.get(equations.get(i).get(1)), values[i]);
        }
        double[] res = new double[queries.size()];
        for (int i = 0; i < queries.size(); i++) {
            String a = queries.get(i).get(0), b = queries.get(i).get(1);
            res[i] = (id.containsKey(a) && id.containsKey(b)) ? dsu.query(id.get(a), id.get(b)) : -1.0;
        }
        return res;
    }

    // ---------- Path With Minimum Effort (LC 1631), DSU form: sort cells' edges by weight, union until start ~ end ----------
    // Same answer as the Dijkstra variant in DijkstraVariants; use this when the question is "smallest bottleneck".
    static int minimumEffortPathDSU(int[][] h) {
        int m = h.length, n = h[0].length;
        if (m * n == 1) return 0;
        List<int[]> edges = new ArrayList<>(); // {weight, a, b}
        for (int i = 0; i < m; i++)
            for (int j = 0; j < n; j++) {
                if (i + 1 < m) edges.add(new int[]{Math.abs(h[i][j] - h[i + 1][j]), i * n + j, (i + 1) * n + j});
                if (j + 1 < n) edges.add(new int[]{Math.abs(h[i][j] - h[i][j + 1]), i * n + j, i * n + j + 1});
            }
        edges.sort((a, b) -> a[0] - b[0]);
        int[] parent = new int[m * n];
        for (int i = 0; i < parent.length; i++) parent[i] = i;
        for (int[] e : edges) {
            int a = root(parent, e[1]), b = root(parent, e[2]);
            if (a != b) parent[a] = b;
            if (root(parent, 0) == root(parent, m * n - 1)) return e[0];
        }
        return 0;
    }

    private static int root(int[] p, int x) {
        while (p[x] != x) { p[x] = p[p[x]]; x = p[x]; }
        return x;
    }

    // ---------- Evaluate Division (LC 399), DFS form: build a weighted graph, answer each query with a search ----------
    static double[] calcEquationDFS(List<List<String>> eq, double[] vals, List<List<String>> queries) {
        Map<String, Map<String, Double>> g = new HashMap<>();
        for (int i = 0; i < eq.size(); i++) {
            String a = eq.get(i).get(0), b = eq.get(i).get(1);
            g.computeIfAbsent(a, k -> new HashMap<>()).put(b, vals[i]);
            g.computeIfAbsent(b, k -> new HashMap<>()).put(a, 1.0 / vals[i]);
        }
        double[] res = new double[queries.size()];
        for (int i = 0; i < res.length; i++) {
            String a = queries.get(i).get(0), b = queries.get(i).get(1);
            res[i] = !g.containsKey(a) || !g.containsKey(b) ? -1.0 : evalDfs(g, a, b, new HashSet<>());
        }
        return res;
    }

    private static double evalDfs(Map<String, Map<String, Double>> g, String cur, String target, Set<String> seen) {
        if (cur.equals(target)) return 1.0;
        seen.add(cur);
        for (Map.Entry<String, Double> e : g.get(cur).entrySet()) {
            if (seen.contains(e.getKey())) continue;
            double sub = evalDfs(g, e.getKey(), target, seen);
            if (sub >= 0) return e.getValue() * sub;
        }
        return -1.0;
    }
}
