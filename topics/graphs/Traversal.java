import java.util.*;

public class Traversal {

    // ---------- DFS (iterative, avoids stack overflow on deep graphs) ----------
    static void dfs(int start, List<List<Integer>> adj) {
        boolean[] visited = new boolean[adj.size()];
        Deque<Integer> stack = new ArrayDeque<>();
        stack.push(start);
        while (!stack.isEmpty()) {
            int u = stack.pop();
            if (visited[u]) continue;
            visited[u] = true;
            // process u here
            for (int v : adj.get(u)) if (!visited[v]) stack.push(v);
        }
    }

    // ---------- DFS (recursive) ----------
    static void dfsRec(int u, List<List<Integer>> adj, boolean[] visited) {
        visited[u] = true;
        // process u here
        for (int v : adj.get(u)) if (!visited[v]) dfsRec(v, adj, visited);
    }

    // ---------- BFS ----------
    static int[] bfs(int start, List<List<Integer>> adj) {
        int n = adj.size();
        int[] dist = new int[n];
        Arrays.fill(dist, -1);
        Queue<Integer> q = new ArrayDeque<>();
        dist[start] = 0;
        q.add(start);
        while (!q.isEmpty()) {
            int u = q.poll();
            for (int v : adj.get(u)) {
                if (dist[v] == -1) {
                    dist[v] = dist[u] + 1;
                    q.add(v);
                }
            }
        }
        return dist;
    }

    // ---------- Multi-source BFS ----------
    // e.g. rotting oranges / walls-and-gates: seed queue with all sources at dist 0
    // LC 286, LC 1162
    static int[] multiSourceBFS(List<Integer> sources, List<List<Integer>> adj, int n) {
        int[] dist = new int[n];
        Arrays.fill(dist, -1);
        Queue<Integer> q = new ArrayDeque<>();
        for (int s : sources) {
            dist[s] = 0;
            q.add(s);
        }
        while (!q.isEmpty()) {
            int u = q.poll();
            for (int v : adj.get(u)) {
                if (dist[v] == -1) {
                    dist[v] = dist[u] + 1;
                    q.add(v);
                }
            }
        }
        return dist;
    }

    // ---------- 0-1 BFS (deque) ----------
    // edge weights only 0 or 1: push to front on weight-0 edge, back on weight-1 edge
    static int[] zeroOneBFS(int start, List<int[]>[] adj) { // adj[u] = list of {v, weight}
        int n = adj.length;
        int[] dist = new int[n];
        Arrays.fill(dist, Integer.MAX_VALUE);
        dist[start] = 0;
        Deque<Integer> deque = new ArrayDeque<>();
        deque.addFirst(start);
        while (!deque.isEmpty()) {
            int u = deque.pollFirst();
            for (int[] edge : adj[u]) {
                int v = edge[0], w = edge[1];
                if (dist[u] + w < dist[v]) {
                    dist[v] = dist[u] + w;
                    if (w == 0) deque.addFirst(v);
                    else deque.addLast(v);
                }
            }
        }
        return dist;
    }

    // ---------- Dial's Algorithm (bucket queue, small bounded integer weights) ----------
    static int[] dial(int start, List<int[]>[] adj, int maxWeight) { // adj[u] = {v, weight}
        int n = adj.length;
        int[] dist = new int[n];
        Arrays.fill(dist, Integer.MAX_VALUE);
        dist[start] = 0;
        // Circular buckets: every live tentative distance lies in [d, d + maxWeight],
        // so maxWeight + 1 buckets suffice. Memory is O(maxWeight), not O(V * maxWeight).
        int B = maxWeight + 1;
        List<List<Integer>> buckets = new ArrayList<>();
        for (int i = 0; i < B; i++) buckets.add(new ArrayList<>());
        buckets.get(0).add(start);
        int remaining = 1; // entries still sitting in buckets (stale ones included)

        for (int d = 0; remaining > 0; d++) {
            List<Integer> bucket = buckets.get(d % B);
            for (int i = 0; i < bucket.size(); i++) { // index loop: 0-weight edges append to this bucket mid-scan
                int u = bucket.get(i);
                remaining--;
                if (dist[u] != d) continue; // stale entry
                for (int[] edge : adj[u]) {
                    int v = edge[0], w = edge[1];
                    if (dist[u] + w < dist[v]) {
                        dist[v] = dist[u] + w;
                        buckets.get(dist[v] % B).add(v);
                        remaining++;
                    }
                }
            }
            bucket.clear();
        }
        return dist;
    }
}
