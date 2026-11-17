import java.util.*;

public class ShortestPath {

    // ---------- Dijkstra ----------
    // adj[u] = list of {v, weight}. Non-negative weights only.
    static int[] dijkstra(int start, List<int[]>[] adj) {
        int n = adj.length;
        int[] dist = new int[n];
        Arrays.fill(dist, Integer.MAX_VALUE);
        dist[start] = 0;
        PriorityQueue<int[]> pq = new PriorityQueue<>((a, b) -> a[1] - b[1]); // {node, dist}
        pq.add(new int[]{start, 0});
        while (!pq.isEmpty()) {
            int[] cur = pq.poll();
            int u = cur[0], d = cur[1];
            if (d > dist[u]) continue; // stale
            for (int[] edge : adj[u]) {
                int v = edge[0], w = edge[1];
                if (dist[u] + w < dist[v]) {
                    dist[v] = dist[u] + w;
                    pq.add(new int[]{v, dist[v]});
                }
            }
        }
        return dist;
    }

    // LC 743
    // ---------- Bellman-Ford ----------
    // edges[i] = {u, v, weight}. Handles negative weights, detects negative cycles.
    static int[] bellmanFord(int n, int start, int[][] edges) {
        int[] dist = new int[n];
        Arrays.fill(dist, Integer.MAX_VALUE);
        dist[start] = 0;
        for (int i = 0; i < n - 1; i++) {
            for (int[] e : edges) {
                int u = e[0], v = e[1], w = e[2];
                if (dist[u] != Integer.MAX_VALUE && dist[u] + w < dist[v]) {
                    dist[v] = dist[u] + w;
                }
            }
        }
        // Vth pass: if any edge still relaxes, there's a negative cycle
        for (int[] e : edges) {
            int u = e[0], v = e[1], w = e[2];
            if (dist[u] != Integer.MAX_VALUE && dist[u] + w < dist[v]) {
                throw new IllegalStateException("Negative weight cycle detected");
            }
        }
        return dist;
    }

    // LC 1334
    // ---------- Floyd-Warshall (All-Pairs Shortest Path) ----------
    // dist[i][j] = direct edge weight or Integer.MAX_VALUE/INF if none; dist[i][i] = 0
    static int[][] floydWarshall(int[][] dist) {
        int n = dist.length;
        for (int k = 0; k < n; k++) {
            for (int i = 0; i < n; i++) {
                if (dist[i][k] == Integer.MAX_VALUE) continue;
                for (int j = 0; j < n; j++) {
                    if (dist[k][j] == Integer.MAX_VALUE) continue;
                    if (dist[i][k] + dist[k][j] < dist[i][j]) {
                        dist[i][j] = dist[i][k] + dist[k][j];
                    }
                }
            }
        }
        // optional: check dist[i][i] < 0 for any i to detect negative cycle
        return dist;
    }

    // ---------- Johnson's Algorithm (APSP on sparse graphs) ----------
    // edges[i] = {u, v, weight}, adj built separately for Dijkstra phase
    static int[][] johnsons(int n, int[][] edges, List<int[]>[] adj) {
        // Step 1: add virtual node connected to all with 0-weight edges, run Bellman-Ford
        int[][] extendedEdges = new int[edges.length + n][3];
        System.arraycopy(edges, 0, extendedEdges, 0, edges.length);
        for (int i = 0; i < n; i++) extendedEdges[edges.length + i] = new int[]{n, i, 0};
        int[] h = bellmanFord(n + 1, n, extendedEdges); // h[i] = reweighting potential

        // Step 2: reweight all edges: w'(u,v) = w(u,v) + h[u] - h[v]  (guaranteed >= 0)
        List<int[]>[] reweighted = new List[n];
        for (int i = 0; i < n; i++) reweighted[i] = new ArrayList<>();
        for (int u = 0; u < n; u++) {
            for (int[] edge : adj[u]) {
                int v = edge[0], w = edge[1];
                reweighted[u].add(new int[]{v, w + h[u] - h[v]});
            }
        }

        // Step 3: run Dijkstra from every node on reweighted graph, then un-reweight
        int[][] result = new int[n][n];
        for (int src = 0; src < n; src++) {
            int[] d = dijkstra(src, reweighted);
            for (int v = 0; v < n; v++) {
                result[src][v] = (d[v] == Integer.MAX_VALUE) ? Integer.MAX_VALUE : d[v] - h[src] + h[v];
            }
        }
        return result;
    }

    // ---------- A* Search ----------
    // heuristic: admissible estimate of remaining distance to target (e.g. Manhattan/Euclidean)
    interface Heuristic { int estimate(int node); }

    static int aStar(int start, int target, List<int[]>[] adj, Heuristic h) {
        int n = adj.length;
        int[] gScore = new int[n];
        Arrays.fill(gScore, Integer.MAX_VALUE);
        gScore[start] = 0;
        PriorityQueue<int[]> pq = new PriorityQueue<>((a, b) -> a[1] - b[1]); // {node, fScore}
        pq.add(new int[]{start, h.estimate(start)});
        while (!pq.isEmpty()) {
            int[] cur = pq.poll();
            int u = cur[0];
            if (u == target) return gScore[u];
            for (int[] edge : adj[u]) {
                int v = edge[0], w = edge[1];
                int tentative = gScore[u] + w;
                if (tentative < gScore[v]) {
                    gScore[v] = tentative;
                    pq.add(new int[]{v, tentative + h.estimate(v)});
                }
            }
        }
        return -1; // unreachable
    }
}
