import java.util.*;

public class SCC_Bridges_2SAT {

    // The DFS below is recursive, which is the form interviewers expect. On a chain ~10^5 deep it
    // overflows Java's default stack. Run the whole call on a thread with a bigger stack:
    //   List<List<Integer>>[] out = new List[1];
    //   runWithBigStack(() -> out[0] = tarjanSCC(n, adj));
    static void runWithBigStack(Runnable r) throws InterruptedException {
        Thread t = new Thread(null, r, "big-stack", 512L * 1024 * 1024);
        t.start();
        t.join();
    }

    // ---------- Tarjan's Algorithm (SCC, single-pass) ----------
    // All DFS state lives in a per-call holder, so nothing leaks between calls or between algorithms.
    // DEPTH: recursion n deep; at n >= 1e4 run inside a big-stack thread (see SCC_Bridges_2SAT.runWithBigStack).
    private static final class TarjanState {
        int[] disc, low;
        boolean[] onStack;
        Deque<Integer> stack = new ArrayDeque<>();
        int timer;
        List<List<Integer>> sccs = new ArrayList<>();
    }

    static List<List<Integer>> tarjanSCC(int n, List<List<Integer>> adj) {
        TarjanState st = new TarjanState();
        st.disc = new int[n];
        st.low = new int[n];
        st.onStack = new boolean[n];
        Arrays.fill(st.disc, -1);
        for (int i = 0; i < n; i++) if (st.disc[i] == -1) tarjanDFS(i, adj, st);
        return st.sccs;
    }

    private static void tarjanDFS(int u, List<List<Integer>> adj, TarjanState st) {
        st.disc[u] = st.low[u] = st.timer++;
        st.stack.push(u);
        st.onStack[u] = true;

        for (int v : adj.get(u)) {
            if (st.disc[v] == -1) {
                tarjanDFS(v, adj, st);
                st.low[u] = Math.min(st.low[u], st.low[v]);
            } else if (st.onStack[v]) {
                st.low[u] = Math.min(st.low[u], st.disc[v]);
            }
        }

        if (st.low[u] == st.disc[u]) { // u is root of an SCC
            List<Integer> component = new ArrayList<>();
            int v;
            do {
                v = st.stack.pop();
                st.onStack[v] = false;
                component.add(v);
            } while (v != u);
            st.sccs.add(component);
        }
    }

    // ---------- Kosaraju's Algorithm (SCC, two-pass) ----------
    static List<List<Integer>> kosarajuSCC(int n, List<List<Integer>> adj) {
        boolean[] visited = new boolean[n];
        Deque<Integer> finishOrder = new ArrayDeque<>();
        for (int i = 0; i < n; i++) if (!visited[i]) fillOrder(i, adj, visited, finishOrder);

        List<List<Integer>> transpose = new ArrayList<>();
        for (int i = 0; i < n; i++) transpose.add(new ArrayList<>());
        for (int u = 0; u < n; u++) for (int v : adj.get(u)) transpose.get(v).add(u);

        Arrays.fill(visited, false);
        List<List<Integer>> result = new ArrayList<>();
        while (!finishOrder.isEmpty()) {
            int u = finishOrder.pop();
            if (!visited[u]) {
                List<Integer> component = new ArrayList<>();
                collectComponent(u, transpose, visited, component);
                result.add(component);
            }
        }
        return result;
    }

    private static void fillOrder(int u, List<List<Integer>> adj, boolean[] visited, Deque<Integer> finishOrder) {
        visited[u] = true;
        for (int v : adj.get(u)) if (!visited[v]) fillOrder(v, adj, visited, finishOrder);
        finishOrder.push(u);
    }

    private static void collectComponent(int u, List<List<Integer>> adj, boolean[] visited, List<Integer> component) {
        visited[u] = true;
        component.add(u);
        for (int v : adj.get(u)) if (!visited[v]) collectComponent(v, adj, visited, component);
    }

    // ---------- SCC Condensation: collapse each SCC into a single DAG node ----------
    static List<List<Integer>> condenseSCC(int n, List<List<Integer>> adj, List<List<Integer>> sccs) {
        int[] compId = new int[n];
        for (int i = 0; i < sccs.size(); i++) for (int node : sccs.get(i)) compId[node] = i;

        List<Set<Integer>> condensedSet = new ArrayList<>();
        for (int i = 0; i < sccs.size(); i++) condensedSet.add(new HashSet<>());
        for (int u = 0; u < n; u++) {
            for (int v : adj.get(u)) {
                if (compId[u] != compId[v]) condensedSet.get(compId[u]).add(compId[v]);
            }
        }
        List<List<Integer>> condensed = new ArrayList<>();
        for (Set<Integer> s : condensedSet) condensed.add(new ArrayList<>(s));
        return condensed; // this is now a DAG — run topo sort / DP on it
    }

    // ---------- Bridges & Articulation Points (undirected graph) ----------
    // Results of the last findBridgesAndArticulation call (read by tests); the DFS state itself is local.
    // Multi-edges: the parent is skipped by EDGE id, not by vertex, so a doubled edge is not a bridge.
    // Edge ids are built internally: each undirected edge u-v must appear once in adj[u] and once in adj[v];
    // the k-th occurrence of v in adj[u] is paired with the k-th occurrence of u in adj[v].
    // DEPTH: recursion n deep; at n >= 1e4 run inside a big-stack thread (see SCC_Bridges_2SAT.runWithBigStack).
    static List<int[]> bridges;
    static Set<Integer> articulationPoints;

    private static final class BridgeState {
        int[] disc, low;
        int timer;
        int[][] eid; // eid[u][i] = id of the i-th adjacency entry of u
        List<int[]> bridges = new ArrayList<>();
        Set<Integer> aps = new HashSet<>();
    }

    // LC 1568 (articulation points on the land-cell graph)
    static void findBridgesAndArticulation(int n, List<List<Integer>> adj) {
        BridgeState st = new BridgeState();
        st.disc = new int[n];
        st.low = new int[n];
        Arrays.fill(st.disc, -1);
        st.eid = new int[n][];
        for (int u = 0; u < n; u++) st.eid[u] = new int[adj.get(u).size()];
        // pair the k-th u->v entry with the k-th v->u entry (u < v), self-loops get their own id
        Map<Long, Deque<Integer>> pending = new HashMap<>(); // key (u,v) with u<v -> unmatched ids from u's side
        int nextId = 0;
        for (int u = 0; u < n; u++) {
            List<Integer> nb = adj.get(u);
            for (int i = 0; i < nb.size(); i++) {
                int v = nb.get(i);
                if (u == v) { st.eid[u][i] = nextId++; continue; }
                if (u < v) {
                    int id = nextId++;
                    st.eid[u][i] = id;
                    pending.computeIfAbsent((long) u * n + v, k -> new ArrayDeque<>()).add(id);
                } else {
                    Deque<Integer> q = pending.get((long) v * n + u);
                    st.eid[u][i] = (q == null || q.isEmpty()) ? nextId++ : q.poll();
                }
            }
        }
        for (int i = 0; i < n; i++) if (st.disc[i] == -1) bridgeDFS(i, -1, adj, st);
        bridges = st.bridges;
        articulationPoints = st.aps;
    }

    private static void bridgeDFS(int u, int parentEdge, List<List<Integer>> adj, BridgeState st) {
        st.disc[u] = st.low[u] = st.timer++;
        int children = 0;
        List<Integer> nb = adj.get(u);

        for (int i = 0; i < nb.size(); i++) {
            int v = nb.get(i), id = st.eid[u][i];
            if (id == parentEdge) continue;
            if (st.disc[v] == -1) {
                children++;
                bridgeDFS(v, id, adj, st);
                st.low[u] = Math.min(st.low[u], st.low[v]);

                if (st.low[v] > st.disc[u]) st.bridges.add(new int[]{u, v}); // bridge condition

                if (parentEdge != -1 && st.low[v] >= st.disc[u]) st.aps.add(u);
                if (parentEdge == -1 && children > 1) st.aps.add(u); // root special case
            } else {
                st.low[u] = Math.min(st.low[u], st.disc[v]);
            }
        }
    }

    // ---------- 2-SAT ----------
    // n variables; each clause is {a, negA, b, negB} meaning (lit(a) OR lit(b)), where neg = 1 means the literal is NOT var.
    // Build implication graph: for clause (a OR b): ¬a -> b, ¬b -> a
    // DEPTH: recursion 2*numVars deep (Tarjan); at >= 1e4 variables run inside a big-stack thread.
    static boolean solve2SAT(int numVars, List<int[]> clauses) {
        return solve2SATAssignment(numVars, clauses) != null;
    }

    // Returns a satisfying assignment (result[i] = value of variable i), or null if unsatisfiable.
    // Tarjan emits SCCs in reverse topological order, so a smaller component index means "later in the
    // implication DAG": set var true iff comp[true node] < comp[false node].
    static boolean[] solve2SATAssignment(int numVars, List<int[]> clauses) {
        int n = 2 * numVars; // node 2i = var i true literal, node 2i+1 = var i false literal
        List<List<Integer>> adj = new ArrayList<>();
        for (int i = 0; i < n; i++) adj.add(new ArrayList<>());

        for (int[] clause : clauses) {
            int a = clause[0], negA = clause[1]; // a: variable index, negA: 1 if negated
            int b = clause[2], negB = clause[3];
            int nodeA = 2 * a + negA, notNodeA = 2 * a + (1 - negA);
            int nodeB = 2 * b + negB, notNodeB = 2 * b + (1 - negB);
            adj.get(notNodeA).add(nodeB); // ¬a -> b
            adj.get(notNodeB).add(nodeA); // ¬b -> a
        }

        List<List<Integer>> sccList = tarjanSCC(n, adj);
        int[] compId = new int[n];
        for (int i = 0; i < sccList.size(); i++) for (int node : sccList.get(i)) compId[node] = i;

        boolean[] val = new boolean[numVars];
        for (int i = 0; i < numVars; i++) {
            if (compId[2 * i] == compId[2 * i + 1]) return null; // var and ¬var in same SCC -> UNSAT
            // node 2i is the literal "x_i with negA=0", i.e. x_i itself; pick it when it comes later in topo order
            val[i] = compId[2 * i] < compId[2 * i + 1];
        }
        return val;
    }
}
