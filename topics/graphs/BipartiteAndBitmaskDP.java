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

    // ---------- Held-Karp (Bitmask DP for TSP) ----------
    // dist[i][j] = cost from i to j. Returns min cost to visit all nodes starting at 0.
    static int heldKarpTSP(int[][] dist) {
        int n = dist.length;
        int[][] dp = new int[1 << n][n];
        for (int[] row : dp) Arrays.fill(row, Integer.MAX_VALUE / 2);
        dp[1][0] = 0; // start at node 0, only node 0 visited

        for (int mask = 1; mask < (1 << n); mask++) {
            for (int u = 0; u < n; u++) {
                if ((mask & (1 << u)) == 0 || dp[mask][u] == Integer.MAX_VALUE / 2) continue;
                for (int v = 0; v < n; v++) {
                    if ((mask & (1 << v)) != 0) continue; // already visited
                    int nextMask = mask | (1 << v);
                    dp[nextMask][v] = Math.min(dp[nextMask][v], dp[mask][u] + dist[u][v]);
                }
            }
        }

        int fullMask = (1 << n) - 1;
        int minCost = Integer.MAX_VALUE;
        for (int u = 1; u < n; u++) {
            if (dp[fullMask][u] != Integer.MAX_VALUE / 2) {
                minCost = Math.min(minCost, dp[fullMask][u] + dist[u][0]); // return to start
            }
        }
        return minCost;
    }

    // ---------- Bitmask DP: Shortest Path Visiting All Nodes (LeetCode 847 shape) ----------
    // Unweighted graph, BFS over (node, visitedMask) states instead of pure DP table.
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
