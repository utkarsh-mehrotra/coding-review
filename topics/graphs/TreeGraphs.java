import java.util.*;

public class TreeGraphs {

    // ---------- Tree diameter (LC 1245): BFS from anywhere, then BFS from the farthest node ----------
    static int treeDiameter(List<List<Integer>> adj) {
        int[] d0 = bfsDist(0, adj);
        int far = argMax(d0);
        int[] d1 = bfsDist(far, adj);
        return d1[argMax(d1)]; // in edges
    }

    static int[] bfsDist(int src, List<List<Integer>> adj) {
        int[] dist = new int[adj.size()];
        Arrays.fill(dist, -1);
        Queue<Integer> q = new ArrayDeque<>();
        dist[src] = 0;
        q.add(src);
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

    private static int argMax(int[] a) {
        int best = 0;
        for (int i = 1; i < a.length; i++) if (a[i] > a[best]) best = i;
        return best;
    }

    // ---------- LCA + k-th ancestor via binary lifting (LC 236 / 1483) ----------
    // up[j][v] = 2^j-th ancestor of v. Build O(N log N), each query O(log N). BFS-built: no recursion.
    static class LCA {
        final int LOG;
        final int[][] up;
        final int[] depth;

        LCA(int n, List<List<Integer>> adj, int root) {
            int lg = 1;
            while ((1 << lg) < n) lg++;
            LOG = lg;
            up = new int[LOG][n];
            depth = new int[n];
            boolean[] seen = new boolean[n];
            Queue<Integer> q = new ArrayDeque<>();
            q.add(root);
            seen[root] = true;
            up[0][root] = root; // root's ancestor is itself
            while (!q.isEmpty()) {
                int u = q.poll();
                for (int v : adj.get(u)) {
                    if (!seen[v]) {
                        seen[v] = true;
                        depth[v] = depth[u] + 1;
                        up[0][v] = u;
                        q.add(v);
                    }
                }
            }
            for (int j = 1; j < LOG; j++)
                for (int v = 0; v < n; v++)
                    up[j][v] = up[j - 1][up[j - 1][v]];
        }

        int kthAncestor(int v, int k) {
            if (k > depth[v]) return -1;
            for (int j = 0; j < LOG; j++) if (((k >> j) & 1) == 1) v = up[j][v];
            return v;
        }

        int lca(int a, int b) {
            if (depth[a] < depth[b]) { int t = a; a = b; b = t; }
            a = kthAncestor(a, depth[a] - depth[b]); // lift deeper node to the same depth
            if (a == b) return a;
            for (int j = LOG - 1; j >= 0; j--) {
                if (up[j][a] != up[j][b]) { a = up[j][a]; b = up[j][b]; }
            }
            return up[0][a];
        }

        int dist(int a, int b) { return depth[a] + depth[b] - 2 * depth[lca(a, b)]; }
    }

    // ---------- Rerooting: Sum of Distances in Tree (LC 834) ----------
    // Pass 1 (bottom-up): cnt[v] = subtree size, res[v] = sum of distances to nodes in v's subtree.
    // Pass 2 (top-down): moving the root from p to child v brings cnt[v] nodes 1 closer, the other n-cnt[v] 1 farther.
    static int[] sumOfDistancesInTree(int n, int[][] edges) {
        List<List<Integer>> adj = new ArrayList<>();
        for (int i = 0; i < n; i++) adj.add(new ArrayList<>());
        for (int[] e : edges) { adj.get(e[0]).add(e[1]); adj.get(e[1]).add(e[0]); }

        int[] parent = new int[n], order = new int[n];
        Arrays.fill(parent, -1);
        int head = 0, tail = 0;
        boolean[] seen = new boolean[n];
        order[tail++] = 0;
        seen[0] = true;
        while (head < tail) { // BFS order doubles as a traversal order for both passes
            int u = order[head++];
            for (int v : adj.get(u)) {
                if (!seen[v]) { seen[v] = true; parent[v] = u; order[tail++] = v; }
            }
        }

        long[] cnt = new long[n], res = new long[n];
        for (int i = n - 1; i >= 0; i--) { // children before parents
            int v = order[i];
            cnt[v] += 1;
            if (parent[v] != -1) {
                cnt[parent[v]] += cnt[v];
                res[parent[v]] += res[v] + cnt[v];
            }
        }
        for (int i = 1; i < n; i++) { // parents before children
            int v = order[i], p = parent[v];
            res[v] = res[p] - cnt[v] + (n - cnt[v]);
        }
        int[] out = new int[n];
        for (int i = 0; i < n; i++) out[i] = (int) res[i];
        return out;
    }

    // ---------- Minimum Height Trees (LC 310): peel leaves layer by layer; the last 1-2 nodes are the centers ----------
    static List<Integer> minHeightTrees(int n, int[][] edges) {
        if (n == 1) return Collections.singletonList(0);
        List<Set<Integer>> adj = new ArrayList<>();
        for (int i = 0; i < n; i++) adj.add(new HashSet<>());
        for (int[] e : edges) { adj.get(e[0]).add(e[1]); adj.get(e[1]).add(e[0]); }

        List<Integer> leaves = new ArrayList<>();
        for (int i = 0; i < n; i++) if (adj.get(i).size() == 1) leaves.add(i);
        int remaining = n;
        while (remaining > 2) {
            remaining -= leaves.size();
            List<Integer> next = new ArrayList<>();
            for (int leaf : leaves) {
                int nb = adj.get(leaf).iterator().next();
                adj.get(nb).remove(leaf);
                if (adj.get(nb).size() == 1) next.add(nb);
            }
            leaves = next;
        }
        return leaves;
    }
}
