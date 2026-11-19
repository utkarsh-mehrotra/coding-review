import java.util.*;

public class TopoSortDAG {

    // ---------- Kahn's Algorithm (BFS, in-degree based) ----------
    // Also detects cycles: if fewer than n nodes get emitted the graph has a cycle, and this throws IllegalStateException.
    static List<Integer> kahnsTopoSort(int n, List<List<Integer>> adj) {
        int[] inDegree = new int[n];
        for (List<Integer> neighbors : adj) {
            for (int v : neighbors) inDegree[v]++;
        }
        Queue<Integer> q = new ArrayDeque<>();
        for (int i = 0; i < n; i++) if (inDegree[i] == 0) q.add(i);

        List<Integer> order = new ArrayList<>();
        while (!q.isEmpty()) {
            int u = q.poll();
            order.add(u);
            for (int v : adj.get(u)) {
                if (--inDegree[v] == 0) q.add(v);
            }
        }
        if (order.size() != n) throw new IllegalStateException("Cycle detected — not a DAG");
        return order;
    }

    // ---------- DFS-based Topo Sort ----------
    static List<Integer> dfsTopoSort(int n, List<List<Integer>> adj) {
        boolean[] visited = new boolean[n];
        boolean[] onStack = new boolean[n]; // for cycle detection
        Deque<Integer> stack = new ArrayDeque<>();

        for (int i = 0; i < n; i++) {
            if (!visited[i]) dfs(i, adj, visited, onStack, stack);
        }
        List<Integer> order = new ArrayList<>();
        while (!stack.isEmpty()) order.add(stack.pop());
        return order;
    }

    private static void dfs(int u, List<List<Integer>> adj, boolean[] visited, boolean[] onStack, Deque<Integer> stack) {
        visited[u] = true;
        onStack[u] = true;
        for (int v : adj.get(u)) {
            if (onStack[v]) throw new IllegalStateException("Cycle detected — not a DAG");
            if (!visited[v]) dfs(v, adj, visited, onStack, stack);
        }
        onStack[u] = false;
        stack.push(u);
    }

    // ---------- DAG DP: Longest Path (relax edges in topo order, no Dijkstra needed) ----------
    // adjW[u] = list of {v, weight}. O(V+E), no V x V matrix.
    static int[] longestPathDAG(int n, List<int[]>[] adjW, int start) {
        List<List<Integer>> adj = new ArrayList<>();
        for (int u = 0; u < n; u++) {
            List<Integer> nbrs = new ArrayList<>();
            for (int[] e : adjW[u]) nbrs.add(e[0]);
            adj.add(nbrs);
        }
        List<Integer> topo = kahnsTopoSort(n, adj); // throws if the graph has a cycle
        int[] dist = new int[n];
        Arrays.fill(dist, Integer.MIN_VALUE);
        dist[start] = 0;
        for (int u : topo) {
            if (dist[u] == Integer.MIN_VALUE) continue; // unreachable from start
            for (int[] e : adjW[u]) {
                dist[e[0]] = Math.max(dist[e[0]], dist[u] + e[1]);
            }
        }
        return dist;
    }

    // ---------- DAG DP: Count Paths from source to each node ----------
    static long[] countPathsDAG(int n, List<List<Integer>> adj, int start) {
        List<Integer> topo = kahnsTopoSort(n, adj);
        long[] ways = new long[n];
        ways[start] = 1;
        for (int u : topo) {
            if (ways[u] == 0) continue;
            for (int v : adj.get(u)) ways[v] += ways[u];
        }
        return ways;
    }
}
