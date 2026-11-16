import java.util.*;

public class GraphCoreTest {
    static int pass = 0, fail = 0;
    static void check(String name, Object got, Object want) {
        boolean ok = Objects.equals(String.valueOf(got), String.valueOf(want));
        if (ok) pass++; else { fail++; System.out.println("FAIL " + name + ": got " + got + " want " + want); }
    }
    @SuppressWarnings("unchecked")
    static List<int[]>[] wadj(int n, int[][] e) {
        List<int[]>[] a = new List[n];
        for (int i = 0; i < n; i++) a[i] = new ArrayList<>();
        for (int[] x : e) a[x[0]].add(new int[]{x[1], x[2]});
        return a;
    }
    static List<List<Integer>> adj(int n, int[][] e, boolean undirected) {
        List<List<Integer>> a = new ArrayList<>();
        for (int i = 0; i < n; i++) a.add(new ArrayList<>());
        for (int[] x : e) { a.get(x[0]).add(x[1]); if (undirected) a.get(x[1]).add(x[0]); }
        return a;
    }
    public static void main(String[] args) {
        // Traversal
        check("bfs", Arrays.toString(Traversal.bfs(0, adj(4, new int[][]{{0,1},{1,2},{2,3}}, false))), "[0, 1, 2, 3]");
        check("01bfs", Traversal.zeroOneBFS(0, wadj(3, new int[][]{{0,1,1},{0,2,0},{2,1,0}}))[1], 0);
        check("dial", Traversal.dial(0, wadj(3, new int[][]{{0,1,2},{0,2,1},{2,1,1}}), 2)[1], 2);
        // Shortest path
        int[][] se = {{0,1,4},{0,2,1},{2,1,2},{1,3,1}};
        check("dijkstra", ShortestPath.dijkstra(0, wadj(4, se))[3], 4);
        check("bellman", ShortestPath.bellmanFord(3, 0, new int[][]{{0,1,3},{1,2,-2},{0,2,2}})[2], 1);
        int INF = Integer.MAX_VALUE;
        int[][] fw = {{0,3,INF},{INF,0,-2},{INF,INF,0}};
        check("floyd", ShortestPath.floydWarshall(fw)[0][2], 1);
        int[][] je = {{0,1,3},{1,2,-2},{0,2,2}};
        int[][] jr = ShortestPath.johnsons(3, je, wadj(3, je));
        check("johnson", jr[0][2], 1);
        check("johnson-unreach", jr[2][0], INF);
        check("astar", ShortestPath.aStar(0, 3, wadj(4, se), x -> 0), 4);
        // Topo
        check("kahn", TopoSortDAG.kahnsTopoSort(3, adj(3, new int[][]{{0,1},{1,2}}, false)), "[0, 1, 2]");
        check("dfstopo", TopoSortDAG.dfsTopoSort(3, adj(3, new int[][]{{0,1},{1,2}}, false)), "[0, 1, 2]");
        check("countpaths", Arrays.toString(TopoSortDAG.countPathsDAG(4, adj(4, new int[][]{{0,1},{0,2},{1,3},{2,3}}, false), 0)), "[1, 1, 1, 2]");
        // DSU
        check("dsu-cycle", DSU.hasCycle(3, new int[][]{{0,1},{1,2},{2,0}}), true);
        check("dsu-comp", DSU.countComponents(5, new int[][]{{0,1},{2,3}}), 3);
        // MST
        int[][] me = {{0,1,1},{1,2,2},{0,2,3},{2,3,4}};
        check("kruskal", MST.kruskalsMST(4, me.clone()), 7);
        List<int[]>[] pa = wadj(4, new int[][]{{0,1,1},{1,0,1},{1,2,2},{2,1,2},{0,2,3},{2,0,3},{2,3,4},{3,2,4}});
        check("prim", MST.primsMST(4, pa), 7);
        // SCC
        List<List<Integer>> g = adj(4, new int[][]{{0,1},{1,2},{2,0},{2,3}}, false);
        check("tarjan-count", SCC_Bridges_2SAT.tarjanSCC(4, g).size(), 2);
        check("kosaraju-count", SCC_Bridges_2SAT.kosarajuSCC(4, g).size(), 2);
        List<List<Integer>> sccs = SCC_Bridges_2SAT.kosarajuSCC(4, g);
        check("condense-nodes", SCC_Bridges_2SAT.condenseSCC(4, g, sccs).size(), 2);
        SCC_Bridges_2SAT.findBridgesAndArticulation(4, adj(4, new int[][]{{0,1},{1,2},{2,0},{2,3}}, true));
        check("bridges", SCC_Bridges_2SAT.bridges.size(), 1);
        check("artic", SCC_Bridges_2SAT.articulationPoints, "[2]");
        List<int[]> unsat = Arrays.asList(new int[]{0,0,1,0}, new int[]{0,1,1,0}, new int[]{0,0,1,1}, new int[]{0,1,1,1});
        check("2sat-unsat", SCC_Bridges_2SAT.solve2SAT(2, unsat), false);
        check("2sat-sat", SCC_Bridges_2SAT.solve2SAT(2, Arrays.asList(new int[]{0,0,1,0})), true);
        // Eulerian
        List<List<String>> tix = Arrays.asList(Arrays.asList("MUC","LHR"), Arrays.asList("JFK","MUC"), Arrays.asList("SFO","SJC"), Arrays.asList("LHR","SFO"));
        check("itinerary", EulerianPath.findItinerary(tix), "[JFK, MUC, LHR, SFO, SJC]");
        Map<Integer, Deque<Integer>> em = new HashMap<>();
        em.computeIfAbsent(0, k -> new ArrayDeque<>()).add(1);
        em.computeIfAbsent(1, k -> new ArrayDeque<>()).add(2);
        em.computeIfAbsent(2, k -> new ArrayDeque<>()).add(0);
        check("hierholzer", EulerianPath.hierholzer(3, em, 0), "[0, 1, 2, 0]");
        // Flow
        int[][] cap = new int[4][4];
        cap[0][1] = 3; cap[0][2] = 2; cap[1][2] = 1; cap[1][3] = 2; cap[2][3] = 3;
        check("edmonds", NetworkFlow.edmondsKarp(4, cap, 0, 3), 5);
        NetworkFlow.Dinic d = new NetworkFlow.Dinic(4);
        d.addEdge(0,1,3); d.addEdge(0,2,2); d.addEdge(1,2,1); d.addEdge(1,3,2); d.addEdge(2,3,3);
        check("dinic", d.maxFlow(0, 3), 5);
        NetworkFlow.HopcroftKarp hk = new NetworkFlow.HopcroftKarp(3, 3);
        hk.addEdge(0,0); hk.addEdge(0,1); hk.addEdge(1,0); hk.addEdge(2,1);
        check("hopcroft", hk.maxMatching(), 2);
        // Bipartite / bitmask
        check("bip-odd", BipartiteAndBitmaskDP.isBipartite(3, adj(3, new int[][]{{0,1},{1,2},{2,0}}, true)), false);
        check("bip-even", BipartiteAndBitmaskDP.isBipartite(4, adj(4, new int[][]{{0,1},{1,2},{2,3},{3,0}}, true)), true);
        int[][] tsp = {{0,10,15,20},{10,0,35,25},{15,35,0,30},{20,25,30,0}};
        check("heldkarp", BipartiteAndBitmaskDP.heldKarpTSP(tsp), 80);
        check("lc847", BipartiteAndBitmaskDP.shortestPathVisitingAllNodes(adj(4, new int[][]{{0,1},{0,2},{0,3}}, true)), 4);
        System.out.println("pass=" + pass + " fail=" + fail);
        if (fail > 0) System.exit(1);
    }
}
