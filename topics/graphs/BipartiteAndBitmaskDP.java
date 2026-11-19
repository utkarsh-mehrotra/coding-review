import java.util.*;

public class BipartiteAndBitmaskDP {

    // ---------- Bipartite Check (2-coloring via BFS) ----------
    static boolean isBipartite(int n, List<List<Integer>> adj) {
        int[] color = new int[n];
        Arrays.fill(color, -1);

        for (int start = 0; start < n; start++) {
            if (color[start] != -1) continue;
            color[start] = 0;
            Queue<Integer> q = new ArrayDeque<>();
            q.add(start);
            while (!q.isEmpty()) {
                int u = q.poll();
                for (int v : adj.get(u)) {
                    if (color[v] == -1) {
                        color[v] = 1 - color[u];
                        q.add(v);
                    } else if (color[v] == color[u]) {
                        return false; // conflict -> not bipartite
                    }
                }
            }
        }
        return true;
    }

    // LC 943 (Shortest Superstring reduces to this TSP over overlap costs)
    // ---------- Held-Karp (Bitmask DP for TSP) ----------
    // dist[i][j] = cost from i to j. Returns min cost to visit all nodes starting at 0.
    static int heldKarpTSP(int[][] dist) {
        int n = dist.length;
        if (n == 1) return 0; // single node: already at the start, nothing to visit
        return tourFrom(1, 0, dist, new Integer[1 << n][n]); // O(2^n * n^2)
    }

    // state: (mask visited, last node) | choice: next unvisited node v | base: all visited -> dist[last][0] (return to start)
    private static int tourFrom(int mask, int last, int[][] dist, Integer[][] memo) {
        int n = dist.length;
        if (mask == (1 << n) - 1) return dist[last][0];
        if (memo[mask][last] != null) return memo[mask][last];
        int best = Integer.MAX_VALUE;
        for (int v = 0; v < n; v++) {
            if ((mask & (1 << v)) != 0) continue;
            best = Math.min(best, dist[last][v] + tourFrom(mask | 1 << v, v, dist, memo));
        }
        return memo[mask][last] = best;
    }

    // ---------- Bitmask DP: Shortest Path Visiting All Nodes (LeetCode 847 shape) ----------
    // Unweighted graph, BFS over (node, visitedMask) states instead of pure DP table.
    // LC 847
    static int shortestPathVisitingAllNodes(List<List<Integer>> adj) {
        int n = adj.size();
        int fullMask = (1 << n) - 1;
        Queue<int[]> q = new ArrayDeque<>(); // {node, mask, steps}
        boolean[][] visited = new boolean[n][1 << n];

        for (int i = 0; i < n; i++) {
            q.add(new int[]{i, 1 << i, 0});
            visited[i][1 << i] = true;
        }

        while (!q.isEmpty()) {
            int[] cur = q.poll();
            int u = cur[0], mask = cur[1], steps = cur[2];
            if (mask == fullMask) return steps;
            for (int v : adj.get(u)) {
                int nextMask = mask | (1 << v);
                if (!visited[v][nextMask]) {
                    visited[v][nextMask] = true;
                    q.add(new int[]{v, nextMask, steps + 1});
                }
            }
        }
        return -1;
    }
}
