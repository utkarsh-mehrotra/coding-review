import java.util.*;

public class DijkstraVariants {

    static final int[][] DIRS4 = {{1, 0}, {-1, 0}, {0, 1}, {0, -1}};

    // ---------- Cheapest Flights Within K Stops (LC 787) ----------
    // Version A: Dijkstra over augmented state (node, edgesUsed). "At most k stops" = at most k+1 edges.
    @SuppressWarnings("unchecked")
    static int cheapestFlightsDijkstraState(int n, int[][] flights, int src, int dst, int k) {
        List<int[]>[] adj = new List[n];
        for (int i = 0; i < n; i++) adj[i] = new ArrayList<>();
        for (int[] f : flights) adj[f[0]].add(new int[]{f[1], f[2]});

        int maxEdges = k + 1;
        int[][] best = new int[n][maxEdges + 1];
        for (int[] row : best) Arrays.fill(row, Integer.MAX_VALUE);
        PriorityQueue<int[]> pq = new PriorityQueue<>((a, b) -> Integer.compare(a[0], b[0])); // {cost, node, edgesUsed}
        pq.add(new int[]{0, src, 0});
        best[src][0] = 0;
        while (!pq.isEmpty()) {
            int[] cur = pq.poll();
            int cost = cur[0], u = cur[1], used = cur[2];
            if (u == dst) return cost;
            if (cost > best[u][used] || used == maxEdges) continue;
            for (int[] e : adj[u]) {
                int nc = cost + e[1];
                if (nc < best[e[0]][used + 1]) {
                    best[e[0]][used + 1] = nc;
                    pq.add(new int[]{nc, e[0], used + 1});
                }
            }
        }
        return -1;
    }

    // Version B: Bellman-Ford limited to K+1 rounds. Relax from a COPY so one round adds at most one edge.
    static int cheapestFlightsBellmanFord(int n, int[][] flights, int src, int dst, int k) {
        final int INF = Integer.MAX_VALUE / 2;
        int[] dist = new int[n];
        Arrays.fill(dist, INF);
        dist[src] = 0;
        for (int round = 0; round <= k; round++) {
            int[] next = dist.clone();
            for (int[] f : flights) {
                if (dist[f[0]] < INF && dist[f[0]] + f[2] < next[f[1]]) next[f[1]] = dist[f[0]] + f[2];
            }
            dist = next;
        }
        return dist[dst] >= INF ? -1 : dist[dst];
    }

    // ---------- Minimax path: Path With Minimum Effort (LC 1631) ----------
    // Cost of a path = MAX edge on it, so combine with max() instead of +. Dijkstra still works.
    static int minimumEffortPath(int[][] h) {
        int m = h.length, n = h[0].length;
        int[][] eff = new int[m][n];
        for (int[] row : eff) Arrays.fill(row, Integer.MAX_VALUE);
        eff[0][0] = 0;
        PriorityQueue<int[]> pq = new PriorityQueue<>((a, b) -> Integer.compare(a[0], b[0])); // {effort, r, c}
        pq.add(new int[]{0, 0, 0});
        while (!pq.isEmpty()) {
            int[] cur = pq.poll();
            int e = cur[0], r = cur[1], c = cur[2];
            if (r == m - 1 && c == n - 1) return e;
            if (e > eff[r][c]) continue;
            for (int[] d : DIRS4) {
                int nr = r + d[0], nc = c + d[1];
                if (nr < 0 || nr >= m || nc < 0 || nc >= n) continue;
                int ne = Math.max(e, Math.abs(h[nr][nc] - h[r][c]));
                if (ne < eff[nr][nc]) {
                    eff[nr][nc] = ne;
                    pq.add(new int[]{ne, nr, nc});
                }
            }
        }
        return 0;
    }

    // ---------- Path reconstruction: keep prev[] ----------
    static List<Integer> dijkstraPath(int src, int dst, List<int[]>[] adj) {
        int n = adj.length;
        int[] dist = new int[n], prev = new int[n];
        Arrays.fill(dist, Integer.MAX_VALUE);
        Arrays.fill(prev, -1);
        dist[src] = 0;
        PriorityQueue<int[]> pq = new PriorityQueue<>((a, b) -> Integer.compare(a[1], b[1])); // {node, dist}
        pq.add(new int[]{src, 0});
        while (!pq.isEmpty()) {
            int[] cur = pq.poll();
            int u = cur[0];
            if (cur[1] > dist[u]) continue;
            if (u == dst) break;
            for (int[] e : adj[u]) {
                int v = e[0], nd = dist[u] + e[1];
                if (nd < dist[v]) {
                    dist[v] = nd;
                    prev[v] = u;
                    pq.add(new int[]{v, nd});
                }
            }
        }
        if (dist[dst] == Integer.MAX_VALUE) return Collections.emptyList();
        LinkedList<Integer> path = new LinkedList<>();
        for (int v = dst; v != -1; v = prev[v]) path.addFirst(v);
        return path;
    }

    // ---------- Count shortest paths: Number of Ways to Arrive at Destination (LC 1976) ----------
    // ways[v] = ways[u] on strict improvement, += ways[u] on a tie. Needs strictly positive weights.
    static int countShortestPaths(int n, List<int[]>[] adj, int src, int dst) {
        final int MOD = 1_000_000_007;
        long[] dist = new long[n];
        Arrays.fill(dist, Long.MAX_VALUE);
        long[] ways = new long[n];
        dist[src] = 0;
        ways[src] = 1;
        PriorityQueue<long[]> pq = new PriorityQueue<>((a, b) -> Long.compare(a[1], b[1])); // {node, dist}
        pq.add(new long[]{src, 0});
        while (!pq.isEmpty()) {
            long[] cur = pq.poll();
            int u = (int) cur[0];
            if (cur[1] > dist[u]) continue;
            for (int[] e : adj[u]) {
                int v = e[0];
                long nd = dist[u] + e[1];
                if (nd < dist[v]) {
                    dist[v] = nd;
                    ways[v] = ways[u];
                    pq.add(new long[]{v, nd});
                } else if (nd == dist[v]) {
                    ways[v] = (ways[v] + ways[u]) % MOD;
                }
            }
        }
        return (int) ways[dst];
    }

    // ---------- BFS over (cell, removalsUsed): Shortest Path with Obstacle Elimination (LC 1293) ----------
    static int shortestPathWithObstacleElimination(int[][] grid, int k) {
        int m = grid.length, n = grid[0].length;
        if (k >= m + n - 2) return m + n - 2; // can walk straight through everything
        boolean[][][] seen = new boolean[m][n][k + 1];
        Queue<int[]> q = new ArrayDeque<>(); // {r, c, used}
        q.add(new int[]{0, 0, 0});
        seen[0][0][0] = true;
        int steps = 0;
        while (!q.isEmpty()) {
            for (int sz = q.size(); sz > 0; sz--) {
                int[] cur = q.poll();
                if (cur[0] == m - 1 && cur[1] == n - 1) return steps;
                for (int[] d : DIRS4) {
                    int nr = cur[0] + d[0], nc = cur[1] + d[1];
                    if (nr < 0 || nr >= m || nc < 0 || nc >= n) continue;
                    int used = cur[2] + grid[nr][nc];
                    if (used <= k && !seen[nr][nc][used]) {
                        seen[nr][nc][used] = true;
                        q.add(new int[]{nr, nc, used});
                    }
                }
            }
            steps++;
        }
        return -1;
    }
}
