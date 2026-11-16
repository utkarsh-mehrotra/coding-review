public class DSU {
    int[] parent, rank_;
    int components;

    DSU(int n) {
        parent = new int[n];
        rank_ = new int[n];
        components = n;
        for (int i = 0; i < n; i++) parent[i] = i;
    }

    int find(int x) {
        if (parent[x] != x) parent[x] = find(parent[x]); // path compression
        return parent[x];
    }

    // returns false if x and y were already connected (i.e. this union closes a cycle)
    boolean union(int x, int y) {
        int rx = find(x), ry = find(y);
        if (rx == ry) return false;
        if (rank_[rx] < rank_[ry]) { int tmp = rx; rx = ry; ry = tmp; }
        parent[ry] = rx;
        if (rank_[rx] == rank_[ry]) rank_[rx]++;
        components--;
        return true;
    }

    boolean connected(int x, int y) {
        return find(x) == find(y);
    }

    // ---------- Usage: cycle detection in undirected graph ----------
    static boolean hasCycle(int n, int[][] edges) {
        DSU dsu = new DSU(n);
        for (int[] e : edges) {
            if (!dsu.union(e[0], e[1])) return true; // already connected -> cycle
        }
        return false;
    }

    // ---------- Usage: count connected components ----------
    static int countComponents(int n, int[][] edges) {
        DSU dsu = new DSU(n);
        for (int[] e : edges) dsu.union(e[0], e[1]);
        return dsu.components;
    }
}
