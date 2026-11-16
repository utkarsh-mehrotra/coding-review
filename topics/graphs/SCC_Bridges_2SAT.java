import java.util.*;

public class SCC_Bridges_2SAT {

    // The DFS below is recursive, which is the form interviewers expect. On a chain ~10^5 deep it
    // overflows Java's default stack. Run the whole call on a thread with a bigger stack:
    //   runWithBigStack(() -> result = tarjanSCC(n, adj));
    static void runWithBigStack(Runnable r) throws InterruptedException {
        Thread t = new Thread(null, r, "big-stack", 512L * 1024 * 1024);
        t.start();
        t.join();
    }

    // ---------- Tarjan's Algorithm (SCC, single-pass) ----------
    static int[] disc, low;
    static boolean[] onStack;
    static Deque<Integer> stack;
    static int timer;
    static List<List<Integer>> sccs;

    static List<List<Integer>> tarjanSCC(int n, List<List<Integer>> adj) {
        disc = new int[n];
        low = new int[n];
        onStack = new boolean[n];
        Arrays.fill(disc, -1);
        stack = new ArrayDeque<>();
        sccs = new ArrayList<>();
        timer = 0;

        for (int i = 0; i < n; i++) if (disc[i] == -1) tarjanDFS(i, adj);
        return sccs;
    }

    private static void tarjanDFS(int u, List<List<Integer>> adj) {
        disc[u] = low[u] = timer++;
        stack.push(u);
        onStack[u] = true;

        for (int v : adj.get(u)) {
            if (disc[v] == -1) {
                tarjanDFS(v, adj);
                low[u] = Math.min(low[u], low[v]);
            } else if (onStack[v]) {
                low[u] = Math.min(low[u], disc[v]);
            }
        }

        if (low[u] == disc[u]) { // u is root of an SCC
            List<Integer> component = new ArrayList<>();
            int v;
            do {
                v = stack.pop();
                onStack[v] = false;
                component.add(v);
            } while (v != u);
            sccs.add(component);
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
    static List<int[]> bridges;
    static Set<Integer> articulationPoints;

    static void findBridgesAndArticulation(int n, List<List<Integer>> adj) {
        disc = new int[n];
        low = new int[n];
        Arrays.fill(disc, -1);
        bridges = new ArrayList<>();
        articulationPoints = new HashSet<>();
        timer = 0;

        for (int i = 0; i < n; i++) if (disc[i] == -1) bridgeDFS(i, -1, adj);
    }

    private static void bridgeDFS(int u, int parent, List<List<Integer>> adj) {
        disc[u] = low[u] = timer++;
        int children = 0;

        for (int v : adj.get(u)) {
            if (v == parent) continue;
            if (disc[v] == -1) {
                children++;
                bridgeDFS(v, u, adj);
                low[u] = Math.min(low[u], low[v]);

                if (low[v] > disc[u]) bridges.add(new int[]{u, v}); // bridge condition

                if (parent != -1 && low[v] >= disc[u]) articulationPoints.add(u);
                if (parent == -1 && children > 1) articulationPoints.add(u); // root special case
            } else {
                low[u] = Math.min(low[u], disc[v]);
            }
        }
    }

    // ---------- 2-SAT ----------
    // n variables; each clause is {a, negA, b, negB} meaning (lit(a) OR lit(b)), where neg = 1 means the literal is NOT var.
    // Build implication graph: for clause (a OR b): ¬a -> b, ¬b -> a
    static boolean solve2SAT(int numVars, List<int[]> clauses) {
        int n = 2 * numVars; // node 2i = var i true, node 2i+1 = var i false
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

        for (int i = 0; i < numVars; i++) {
            if (compId[2 * i] == compId[2 * i + 1]) return false; // var and ¬var in same SCC -> UNSAT
        }
        return true;
    }
}
