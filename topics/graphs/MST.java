import java.util.*;

public class MST {

    // ---------- Kruskal's Algorithm ----------
    // edges[i] = {u, v, weight}. Best for sparse graphs / edge-list input.
    // Note: sorts the caller's `edges` array in place (mutates input).
    static int kruskalsMST(int n, int[][] edges) {
        Arrays.sort(edges, (a, b) -> Integer.compare(a[2], b[2])); // sort by weight
        DSU dsu = new DSU(n);
        int totalWeight = 0, edgesUsed = 0;

        for (int[] e : edges) {
            int u = e[0], v = e[1], w = e[2];
            if (dsu.union(u, v)) {
                totalWeight += w;
                edgesUsed++;
                if (edgesUsed == n - 1) break;
            }
        }
        if (edgesUsed != n - 1) throw new IllegalStateException("Graph is not connected — no spanning tree");
        return totalWeight;
    }

    // ---------- Prim's Algorithm ----------
    // adj[u] = list of {v, weight}. Best for dense graphs / adjacency-list input.
    static int primsMST(int n, List<int[]>[] adj) {
        boolean[] inMST = new boolean[n];
        PriorityQueue<int[]> pq = new PriorityQueue<>((a, b) -> Integer.compare(a[1], b[1])); // {node, weight}
        pq.add(new int[]{0, 0}); // start from node 0
        int totalWeight = 0, nodesAdded = 0;

        while (!pq.isEmpty() && nodesAdded < n) {
            int[] cur = pq.poll();
            int u = cur[0], w = cur[1];
            if (inMST[u]) continue;
            inMST[u] = true;
            totalWeight += w;
            nodesAdded++;
            for (int[] edge : adj[u]) {
                int v = edge[0], vw = edge[1];
                if (!inMST[v]) pq.add(new int[]{v, vw});
            }
        }
        if (nodesAdded != n) throw new IllegalStateException("Graph is not connected — no spanning tree");
        return totalWeight;
    }

    // Same as DSU.java, kept inline so this file compiles alone (see DSU.java for the full version with cycle/component helpers).
    static class DSU {
        int[] parent, rank_;
        DSU(int n) {
            parent = new int[n];
            rank_ = new int[n];
            for (int i = 0; i < n; i++) parent[i] = i;
        }
        int find(int x) {
            if (parent[x] != x) parent[x] = find(parent[x]);
            return parent[x];
        }
        boolean union(int x, int y) {
            int rx = find(x), ry = find(y);
            if (rx == ry) return false;
            if (rank_[rx] < rank_[ry]) { int t = rx; rx = ry; ry = t; }
            parent[ry] = rx;
            if (rank_[rx] == rank_[ry]) rank_[rx]++;
            return true;
        }
    }
}
