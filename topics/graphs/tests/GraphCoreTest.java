import java.util.*;

public class GraphCoreTest {
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
        Check.eq("bfs", Arrays.toString(Traversal.bfs(0, adj(4, new int[][]{{0,1},{1,2},{2,3}}, false))), "[0, 1, 2, 3]");
        Check.eq("01bfs", Traversal.zeroOneBFS(0, wadj(3, new int[][]{{0,1,1},{0,2,0},{2,1,0}}))[1], 0);
        Check.eq("dial", Traversal.dial(0, wadj(3, new int[][]{{0,1,2},{0,2,1},{2,1,1}}), 2)[1], 2);
        // Shortest path
        int[][] se = {{0,1,4},{0,2,1},{2,1,2},{1,3,1}};
        Check.eq("dijkstra", ShortestPath.dijkstra(0, wadj(4, se))[3], 4);
        Check.eq("bellman", ShortestPath.bellmanFord(3, 0, new int[][]{{0,1,3},{1,2,-2},{0,2,2}})[2], 1);
        int INF = Integer.MAX_VALUE;
        int[][] fw = {{0,3,INF},{INF,0,-2},{INF,INF,0}};
        Check.eq("floyd", ShortestPath.floydWarshall(fw)[0][2], 1);
        int[][] je = {{0,1,3},{1,2,-2},{0,2,2}};
        int[][] jr = ShortestPath.johnsons(3, je, wadj(3, je));
        Check.eq("johnson", jr[0][2], 1);
        Check.eq("johnson-unreach", jr[2][0], INF);
        Check.eq("astar", ShortestPath.aStar(0, 3, wadj(4, se), x -> 0), 4);
        // Topo
        Check.eq("kahn", TopoSortDAG.kahnsTopoSort(3, adj(3, new int[][]{{0,1},{1,2}}, false)), "[0, 1, 2]");
        Check.eq("dfstopo", TopoSortDAG.dfsTopoSort(3, adj(3, new int[][]{{0,1},{1,2}}, false)), "[0, 1, 2]");
        Check.eq("countpaths", Arrays.toString(TopoSortDAG.countPathsDAG(4, adj(4, new int[][]{{0,1},{0,2},{1,3},{2,3}}, false), 0)), "[1, 1, 1, 2]");
        // DSU
        Check.eq("dsu-cycle", DSU.hasCycle(3, new int[][]{{0,1},{1,2},{2,0}}), true);
        Check.eq("dsu-comp", DSU.countComponents(5, new int[][]{{0,1},{2,3}}), 3);
        // MST
        int[][] me = {{0,1,1},{1,2,2},{0,2,3},{2,3,4}};
        Check.eq("kruskal", MST.kruskalsMST(4, me.clone()), 7);
        List<int[]>[] pa = wadj(4, new int[][]{{0,1,1},{1,0,1},{1,2,2},{2,1,2},{0,2,3},{2,0,3},{2,3,4},{3,2,4}});
        Check.eq("prim", MST.primsMST(4, pa), 7);
        // SCC
        List<List<Integer>> g = adj(4, new int[][]{{0,1},{1,2},{2,0},{2,3}}, false);
        Check.eq("tarjan-count", SCC_Bridges_2SAT.tarjanSCC(4, g).size(), 2);
        Check.eq("kosaraju-count", SCC_Bridges_2SAT.kosarajuSCC(4, g).size(), 2);
        List<List<Integer>> sccs = SCC_Bridges_2SAT.kosarajuSCC(4, g);
        Check.eq("condense-nodes", SCC_Bridges_2SAT.condenseSCC(4, g, sccs).size(), 2);
        SCC_Bridges_2SAT.findBridgesAndArticulation(4, adj(4, new int[][]{{0,1},{1,2},{2,0},{2,3}}, true));
        Check.eq("bridges", SCC_Bridges_2SAT.bridges.size(), 1);
        Check.eq("artic", SCC_Bridges_2SAT.articulationPoints, "[2]");
        List<int[]> unsat = Arrays.asList(new int[]{0,0,1,0}, new int[]{0,1,1,0}, new int[]{0,0,1,1}, new int[]{0,1,1,1});
        Check.eq("2sat-unsat", SCC_Bridges_2SAT.solve2SAT(2, unsat), false);
        Check.eq("2sat-sat", SCC_Bridges_2SAT.solve2SAT(2, Arrays.asList(new int[]{0,0,1,0})), true);
        // Eulerian
        List<List<String>> tix = Arrays.asList(Arrays.asList("MUC","LHR"), Arrays.asList("JFK","MUC"), Arrays.asList("SFO","SJC"), Arrays.asList("LHR","SFO"));
        Check.eq("itinerary", EulerianPath.findItinerary(tix), "[JFK, MUC, LHR, SFO, SJC]");
        Map<Integer, Deque<Integer>> em = new HashMap<>();
        em.computeIfAbsent(0, k -> new ArrayDeque<>()).add(1);
        em.computeIfAbsent(1, k -> new ArrayDeque<>()).add(2);
        em.computeIfAbsent(2, k -> new ArrayDeque<>()).add(0);
        Check.eq("hierholzer", EulerianPath.hierholzer(3, em, 0), "[0, 1, 2, 0]");
        // Flow
        int[][] cap = new int[4][4];
        cap[0][1] = 3; cap[0][2] = 2; cap[1][2] = 1; cap[1][3] = 2; cap[2][3] = 3;
        Check.eq("edmonds", NetworkFlow.edmondsKarp(4, cap, 0, 3), 5);
        NetworkFlow.Dinic d = new NetworkFlow.Dinic(4);
        d.addEdge(0,1,3); d.addEdge(0,2,2); d.addEdge(1,2,1); d.addEdge(1,3,2); d.addEdge(2,3,3);
        Check.eq("dinic", d.maxFlow(0, 3), 5);
        NetworkFlow.HopcroftKarp hk = new NetworkFlow.HopcroftKarp(3, 3);
        hk.addEdge(0,0); hk.addEdge(0,1); hk.addEdge(1,0); hk.addEdge(2,1);
        Check.eq("hopcroft", hk.maxMatching(), 2);
        // Bipartite / bitmask
        Check.eq("bip-odd", BipartiteAndBitmaskDP.isBipartite(3, adj(3, new int[][]{{0,1},{1,2},{2,0}}, true)), false);
        Check.eq("bip-even", BipartiteAndBitmaskDP.isBipartite(4, adj(4, new int[][]{{0,1},{1,2},{2,3},{3,0}}, true)), true);
        int[][] tsp = {{0,10,15,20},{10,0,35,25},{15,35,0,30},{20,25,30,0}};
        Check.eq("heldkarp", BipartiteAndBitmaskDP.heldKarpTSP(tsp), 80);
        Check.eq("lc847", BipartiteAndBitmaskDP.shortestPathVisitingAllNodes(adj(4, new int[][]{{0,1},{0,2},{0,3}}, true)), 4);
        // never-tested before: DSU, DSUExtended, Traversal dfs/dfsRec/multiSourceBFS, TreeGraphs.bfsDist
        DSU dsu0 = new DSU(5);
        Check.eq("dsu union new", dsu0.union(0, 1) + "," + dsu0.union(1, 2), "true,true");
        Check.eq("dsu union cycle", dsu0.union(0, 2), false);
        Check.eq("dsu find same", dsu0.find(0) == dsu0.find(2), true);
        Check.eq("dsu find apart", dsu0.find(3) == dsu0.find(0), false);
        Check.eq("dsu components", dsu0.components, 3);
        DSUExtended.DSUSize ds = new DSUExtended.DSUSize(6);
        Check.eq("dsuSize union", ds.union(0, 1) + "," + ds.union(2, 3) + "," + ds.union(1, 3) + "," + ds.union(0, 2), "true,true,true,false");
        Check.eq("dsuSize sizeOf", ds.sizeOf(3) + "," + ds.sizeOf(4), "4,1");
        Check.eq("dsuSize find", ds.find(0) == ds.find(3) && ds.find(4) != ds.find(0), true);
        DSUExtended.WeightedDSU wd = new DSUExtended.WeightedDSU(4);
        wd.union(0, 1, 2.0);
        wd.union(1, 2, 3.0);
        Check.eq("weightedDSU query", wd.query(0, 2) + "," + wd.query(2, 0) + "," + wd.query(0, 3), "6.0,0.16666666666666666,-1.0");
        List<List<Integer>> g5 = adj(4, new int[][]{{0, 1}, {1, 2}}, true);
        boolean[] seen = new boolean[4];
        Traversal.dfsRec(0, g5, seen);
        Check.eq("dfsRec visited", Arrays.toString(seen), "[true, true, true, false]");
        Traversal.dfs(0, adj(3, new int[][]{{0, 1}, {1, 2}, {2, 0}}, true)); // terminates on a cycle
        Check.eq("dfs cycle terminates", true, true);
        Check.eq("multiSourceBFS", Arrays.toString(Traversal.multiSourceBFS(Arrays.asList(0, 3), adj(5, new int[][]{{0, 1}, {1, 2}, {2, 3}, {3, 4}}, true), 5)), "[0, 1, 1, 0, 1]");
        Check.eq("bfsDist", Arrays.toString(TreeGraphs.bfsDist(0, adj(6, new int[][]{{0, 1}, {1, 2}, {2, 3}, {1, 4}}, true))), "[0, 1, 2, 3, 2, -1]");
        Check.done("GraphCoreTest");
    }
}
