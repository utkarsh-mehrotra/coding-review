import java.util.*;

public class NetworkFlow {

    // ---------- Ford-Fulkerson / Edmonds-Karp (BFS augmenting path) ----------
    static int edmondsKarp(int n, int[][] capacity, int source, int sink) {
        int[][] residual = new int[n][n];
        for (int i = 0; i < n; i++) residual[i] = capacity[i].clone();

        int maxFlow = 0;
        while (true) {
            int[] parent = bfsFindAugmentingPath(residual, n, source, sink);
            if (parent == null) break; // no augmenting path left

            // find bottleneck capacity along the path
            int pathFlow = Integer.MAX_VALUE;
            for (int v = sink; v != source; v = parent[v]) {
                int u = parent[v];
                pathFlow = Math.min(pathFlow, residual[u][v]);
            }
            // update residual capacities
            for (int v = sink; v != source; v = parent[v]) {
                int u = parent[v];
                residual[u][v] -= pathFlow;
                residual[v][u] += pathFlow;
            }
            maxFlow += pathFlow;
        }
        return maxFlow;
    }

    private static int[] bfsFindAugmentingPath(int[][] residual, int n, int source, int sink) {
        int[] parent = new int[n];
        Arrays.fill(parent, -1);
        parent[source] = source;
        Queue<Integer> q = new ArrayDeque<>();
        q.add(source);
        while (!q.isEmpty()) {
            int u = q.poll();
            for (int v = 0; v < n; v++) {
                if (parent[v] == -1 && residual[u][v] > 0) {
                    parent[v] = u;
                    if (v == sink) return parent;
                    q.add(v);
                }
            }
        }
        return null;
    }

    // ---------- Dinic's Algorithm (faster max flow: level graph + blocking flow) ----------
    static class Dinic {
        int n;
        List<int[]> edgeList = new ArrayList<>(); // {to}; the capacity of edge i lives in capList.get(i)
        List<Integer>[] graph;
        int[] level, it;

        Dinic(int n) {
            this.n = n;
            graph = new List[n];
            for (int i = 0; i < n; i++) graph[i] = new ArrayList<>();
        }

        void addEdge(int u, int v, int c) {
            graph[u].add(edgeList.size());
            edgeList.add(new int[]{v});
            graph[v].add(edgeList.size());
            edgeList.add(new int[]{u});
            capList.add(c);
            capList.add(0); // reverse edge starts with 0 capacity
        }
        List<Integer> capList = new ArrayList<>();

        boolean bfs(int s, int t) {
            level = new int[n];
            Arrays.fill(level, -1);
            level[s] = 0;
            Queue<Integer> q = new ArrayDeque<>();
            q.add(s);
            while (!q.isEmpty()) {
                int u = q.poll();
                for (int id : graph[u]) {
                    int v = edgeList.get(id)[0];
                    if (capList.get(id) > 0 && level[v] < 0) {
                        level[v] = level[u] + 1;
                        q.add(v);
                    }
                }
            }
            return level[t] >= 0;
        }

        int dfs(int u, int t, int f) {
            if (u == t) return f;
            for (; it[u] < graph[u].size(); it[u]++) {
                int id = graph[u].get(it[u]);
                int v = edgeList.get(id)[0];
                if (capList.get(id) > 0 && level[v] == level[u] + 1) {
                    int d = dfs(v, t, Math.min(f, capList.get(id)));
                    if (d > 0) {
                        capList.set(id, capList.get(id) - d);
                        capList.set(id ^ 1, capList.get(id ^ 1) + d);
                        return d;
                    }
                }
            }
            return 0;
        }

        int maxFlow(int s, int t) {
            int flow = 0;
            while (bfs(s, t)) {
                it = new int[n];
                int f;
                while ((f = dfs(s, t, Integer.MAX_VALUE)) > 0) flow += f;
            }
            return flow;
        }
    }

    // ---------- Hopcroft-Karp (Maximum Bipartite Matching) ----------
    static class HopcroftKarp {
        int leftSize, rightSize;
        List<Integer>[] adj; // adj[left node] -> list of right nodes
        int[] matchLeft, matchRight, dist;
        static final int NIL = -1, INF = Integer.MAX_VALUE;

        HopcroftKarp(int leftSize, int rightSize) {
            this.leftSize = leftSize;
            this.rightSize = rightSize;
            adj = new List[leftSize];
            for (int i = 0; i < leftSize; i++) adj[i] = new ArrayList<>();
            matchLeft = new int[leftSize];
            matchRight = new int[rightSize];
            Arrays.fill(matchLeft, NIL);
            Arrays.fill(matchRight, NIL);
        }

        void addEdge(int u, int v) { adj[u].add(v); }

        boolean bfs() {
            dist = new int[leftSize];
            Queue<Integer> q = new ArrayDeque<>();
            for (int u = 0; u < leftSize; u++) {
                if (matchLeft[u] == NIL) { dist[u] = 0; q.add(u); }
                else dist[u] = INF;
            }
            boolean foundAugmentingPath = false;
            while (!q.isEmpty()) {
                int u = q.poll();
                for (int v : adj[u]) {
                    int w = matchRight[v];
                    if (w == NIL) foundAugmentingPath = true;
                    else if (dist[w] == INF) {
                        dist[w] = dist[u] + 1;
                        q.add(w);
                    }
                }
            }
            return foundAugmentingPath;
        }

        boolean dfs(int u) {
            for (int v : adj[u]) {
                int w = matchRight[v];
                if (w == NIL || (dist[w] == dist[u] + 1 && dfs(w))) {
                    matchLeft[u] = v;
                    matchRight[v] = u;
                    return true;
                }
            }
            dist[u] = INF;
            return false;
        }

        int maxMatching() {
            int matching = 0;
            while (bfs()) {
                for (int u = 0; u < leftSize; u++) {
                    if (matchLeft[u] == NIL && dfs(u)) matching++;
                }
            }
            return matching;
        }
    }
}
