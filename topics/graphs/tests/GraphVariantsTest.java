import java.util.*;

public class GraphVariantsTest {
    static int pass = 0, fail = 0;
    static void check(String name, Object got, Object want) {
        if (Objects.equals(String.valueOf(got), String.valueOf(want))) pass++;
        else { fail++; System.out.println("FAIL " + name + ": got " + got + " want " + want); }
    }
    @SuppressWarnings("unchecked")
    static List<int[]>[] wadj(int n, int[][] e, boolean undirected) {
        List<int[]>[] a = new List[n];
        for (int i = 0; i < n; i++) a[i] = new ArrayList<>();
        for (int[] x : e) { a[x[0]].add(new int[]{x[1], x[2]}); if (undirected) a[x[1]].add(new int[]{x[0], x[2]}); }
        return a;
    }
    static List<List<Integer>> adj(int n, int[][] e, boolean undirected) {
        List<List<Integer>> a = new ArrayList<>();
        for (int i = 0; i < n; i++) a.add(new ArrayList<>());
        for (int[] x : e) { a.get(x[0]).add(x[1]); if (undirected) a.get(x[1]).add(x[0]); }
        return a;
    }
    static List<String> L(String... s) { return Arrays.asList(s); }

    public static void main(String[] args) {
        // --- modified older templates ---
        check("dial0w", Traversal.dial(0, wadj(3, new int[][]{{0,1,0},{1,2,2},{0,2,3}}, false), 3)[2], 2);
        check("dial", Traversal.dial(0, wadj(3, new int[][]{{0,1,2},{0,2,1},{2,1,1}}, false), 2)[1], 2);
        check("longestDAG", TopoSortDAG.longestPathDAG(4, wadj(4, new int[][]{{0,1,3},{0,2,2},{1,3,4},{2,3,1}}, false), 0)[3], 7);

        // --- 10 implicit graphs ---
        check("islands", ImplicitGraphs.numIslands(new char[][]{"11000".toCharArray(),"11000".toCharArray(),"00100".toCharArray(),"00011".toCharArray()}), 3);
        check("maxArea", ImplicitGraphs.maxAreaOfIsland(new int[][]{{0,1,1},{0,1,0},{1,0,0}}), 3);
        check("binmat1", ImplicitGraphs.shortestPathBinaryMatrix(new int[][]{{0,1},{1,0}}), 2);
        check("binmat2", ImplicitGraphs.shortestPathBinaryMatrix(new int[][]{{0,0,0},{1,1,0},{1,1,0}}), 4);
        check("rot1", ImplicitGraphs.orangesRotting(new int[][]{{2,1,1},{1,1,0},{0,1,1}}), 4);
        check("rot2", ImplicitGraphs.orangesRotting(new int[][]{{2,1,1},{0,1,1},{1,0,1}}), -1);
        int[][] h = {{1,2,2,3,5},{3,2,3,4,4},{2,4,5,3,1},{6,7,1,4,5},{5,1,1,2,4}};
        check("pacatl", ImplicitGraphs.pacificAtlantic(h).size(), 7);
        List<String> wl = L("hot","dot","dog","lot","log","cog");
        check("ladder", ImplicitGraphs.wordLadder("hit", "cog", wl), 5);
        check("ladderBi", ImplicitGraphs.wordLadderBidirectional("hit", "cog", wl), 5);
        check("ladder0", ImplicitGraphs.wordLadder("hit", "cog", L("hot","dot","dog","lot","log")), 0);
        check("ladderBi0", ImplicitGraphs.wordLadderBidirectional("hit", "cog", L("hot","dot","dog","lot","log")), 0);
        check("lock6", ImplicitGraphs.openLock(new String[]{"0201","0101","0102","1212","2002"}, "0202"), 6);
        check("lock1", ImplicitGraphs.openLock(new String[]{"8888"}, "0009"), 1);
        check("lock-1", ImplicitGraphs.openLock(new String[]{"8887","8889","8878","8898","8788","8988","7888","9888"}, "8888"), -1);
        check("slide1", ImplicitGraphs.slidingPuzzle(new int[][]{{1,2,3},{4,0,5}}), 1);
        check("slide-1", ImplicitGraphs.slidingPuzzle(new int[][]{{1,2,3},{5,4,0}}), -1);
        check("slide5", ImplicitGraphs.slidingPuzzle(new int[][]{{4,1,2},{5,0,3}}), 5);

        // --- 11 dijkstra variants ---
        int[][] fl = {{0,1,100},{1,2,100},{2,0,100},{1,3,600},{2,3,200}};
        check("fl700A", DijkstraVariants.cheapestFlightsDijkstraState(4, fl, 0, 3, 1), 700);
        check("fl700B", DijkstraVariants.cheapestFlightsBellmanFord(4, fl, 0, 3, 1), 700);
        int[][] fl2 = {{0,1,100},{1,2,100},{0,2,500}};
        check("fl200A", DijkstraVariants.cheapestFlightsDijkstraState(3, fl2, 0, 2, 1), 200);
        check("fl200B", DijkstraVariants.cheapestFlightsBellmanFord(3, fl2, 0, 2, 1), 200);
        check("fl500A", DijkstraVariants.cheapestFlightsDijkstraState(3, fl2, 0, 2, 0), 500);
        check("fl500B", DijkstraVariants.cheapestFlightsBellmanFord(3, fl2, 0, 2, 0), 500);
        check("effort2", DijkstraVariants.minimumEffortPath(new int[][]{{1,2,2},{3,8,2},{5,3,5}}), 2);
        check("effort1", DijkstraVariants.minimumEffortPath(new int[][]{{1,2,3},{3,8,4},{5,3,5}}), 1);
        check("path", DijkstraVariants.dijkstraPath(0, 3, wadj(4, new int[][]{{0,1,4},{0,2,1},{2,1,2},{1,3,1}}, false)), "[0, 2, 1, 3]");
        int[][] roads = {{0,6,7},{0,1,2},{1,2,3},{1,3,3},{6,3,3},{3,5,1},{6,5,1},{2,5,1},{0,4,5},{4,6,2}};
        check("count4", DijkstraVariants.countShortestPaths(7, wadj(7, roads, true), 0, 6), 4);
        check("count1", DijkstraVariants.countShortestPaths(2, wadj(2, new int[][]{{1,0,10}}, true), 0, 1), 1);
        int[][] og = {{0,0,0},{1,1,0},{0,0,0},{0,1,1},{0,0,0}};
        check("obst6", DijkstraVariants.shortestPathWithObstacleElimination(og, 1), 6);
        check("obst-1", DijkstraVariants.shortestPathWithObstacleElimination(new int[][]{{0,1,1},{1,1,1},{1,0,0}}, 1), -1);

        // --- 12 cycles & courses ---
        check("cyc-true", CycleAndCourseGraphs.hasCycleDirected(3, adj(3, new int[][]{{0,1},{1,2},{2,0}}, false)), true);
        check("cyc-false", CycleAndCourseGraphs.hasCycleDirected(3, adj(3, new int[][]{{0,1},{1,2}}, false)), false);
        check("safe", CycleAndCourseGraphs.eventualSafeNodes(new int[][]{{1,2},{2,3},{5},{0},{5},{},{}}), "[2, 4, 5, 6]");
        check("order", Arrays.toString(CycleAndCourseGraphs.courseOrder(2, new int[][]{{1,0}})), "[0, 1]");
        check("order-cycle", CycleAndCourseGraphs.courseOrder(2, new int[][]{{1,0},{0,1}}).length, 0);
        check("sem2", CycleAndCourseGraphs.minimumSemesters(3, new int[][]{{1,3},{2,3}}), 2);
        check("sem-1", CycleAndCourseGraphs.minimumSemesters(3, new int[][]{{1,2},{2,3},{3,1}}), -1);
        check("alien", CycleAndCourseGraphs.alienOrder(new String[]{"wrt","wrf","er","ett","rftt"}), "wertf");
        check("alien-cycle", CycleAndCourseGraphs.alienOrder(new String[]{"z","x","z"}), "");
        check("alien-prefix", CycleAndCourseGraphs.alienOrder(new String[]{"abc","ab"}), "");
        CycleAndCourseGraphs.Node a = new CycleAndCourseGraphs.Node(1), b = new CycleAndCourseGraphs.Node(2), c = new CycleAndCourseGraphs.Node(3);
        a.neighbors.add(b); a.neighbors.add(c); b.neighbors.add(a); b.neighbors.add(c); c.neighbors.add(a); c.neighbors.add(b);
        CycleAndCourseGraphs.Node ca = CycleAndCourseGraphs.cloneGraph(a);
        check("clone-new", ca != a && ca.neighbors.get(0) != b && ca.neighbors.get(1) != c, true);
        check("clone-shape", ca.val + ":" + ca.neighbors.get(0).val + ":" + ca.neighbors.get(1).val + ":" + ca.neighbors.get(0).neighbors.get(1).val, "1:2:3:3");
        check("clone-shared", ca.neighbors.get(0).neighbors.get(0) == ca, true);

        // --- 13 DSU extended ---
        check("islands2", DSUExtended.numIslands2(3, 3, new int[][]{{0,0},{0,1},{1,2},{2,1}}), "[1, 1, 2, 3]");
        List<List<String>> acc = Arrays.asList(L("John","johnsmith@mail.com","john_newyork@mail.com"), L("John","johnsmith@mail.com","john00@mail.com"), L("Mary","mary@mail.com"), L("John","johnnybravo@mail.com"));
        List<List<String>> merged = DSUExtended.accountsMerge(acc);
        int maxRow = 0; for (List<String> r : merged) maxRow = Math.max(maxRow, r.size());
        check("accounts-count", merged.size(), 3);
        check("accounts-merged-size", maxRow, 4);
        check("redundant", Arrays.toString(DSUExtended.findRedundantConnection(new int[][]{{1,2},{1,3},{2,3}})), "[2, 3]");
        double[] ce = DSUExtended.calcEquation(Arrays.asList(L("a","b"), L("b","c")), new double[]{2.0, 3.0},
            Arrays.asList(L("a","c"), L("b","a"), L("a","e"), L("a","a"), L("x","x")));
        check("calcEq", Arrays.toString(ce), "[6.0, 0.5, -1.0, 1.0, -1.0]");

        // --- 14 trees ---
        List<List<Integer>> tr = adj(6, new int[][]{{0,1},{0,2},{1,3},{1,4},{2,5}}, true);
        check("diameter", TreeGraphs.treeDiameter(tr), 4);
        TreeGraphs.LCA lca = new TreeGraphs.LCA(6, tr, 0);
        check("lca34", lca.lca(3, 4), 1);
        check("lca35", lca.lca(3, 5), 0);
        check("dist35", lca.dist(3, 5), 4);
        check("kth", lca.kthAncestor(3, 2), 0);
        check("kth-1", lca.kthAncestor(3, 3), -1);
        check("sumdist", Arrays.toString(TreeGraphs.sumOfDistancesInTree(6, new int[][]{{0,1},{0,2},{2,3},{2,4},{2,5}})), "[8, 12, 6, 10, 10, 10]");
        check("mht1", TreeGraphs.minHeightTrees(4, new int[][]{{1,0},{1,2},{1,3}}), "[1]");
        List<Integer> m2 = new ArrayList<>(TreeGraphs.minHeightTrees(6, new int[][]{{3,0},{3,1},{3,2},{3,4},{5,4}})); Collections.sort(m2);
        check("mht2", m2, "[3, 4]");

        // --- 15 matching / coloring / functional ---
        List<List<Integer>> bp = new ArrayList<>();
        bp.add(Arrays.asList(0, 1)); bp.add(Arrays.asList(0)); bp.add(Arrays.asList(1));
        check("kuhn", MatchingColoringFunctional.kuhnMatching(3, 3, bp), 2);
        List<List<Integer>> tri = adj(3, new int[][]{{0,1},{1,2},{2,0}}, true);
        check("color-tri2", MatchingColoringFunctional.kColorable(tri, 2), false);
        check("color-tri3", MatchingColoringFunctional.kColorable(tri, 3), true);
        check("color-c4", MatchingColoringFunctional.kColorable(adj(4, new int[][]{{0,1},{1,2},{2,3},{3,0}}, true), 2), true);
        check("dup1", MatchingColoringFunctional.findDuplicate(new int[]{1,3,4,2,2}), 2);
        check("dup2", MatchingColoringFunctional.findDuplicate(new int[]{3,1,3,4,2}), 3);
        check("cycStart", MatchingColoringFunctional.cycleStart(new int[]{1,2,3,1}, 0), 1);
        check("cycNone", MatchingColoringFunctional.cycleStart(new int[]{1,2,-1}, 0), -1);
        check("longCyc3", MatchingColoringFunctional.longestCycle(new int[]{3,3,4,2,3}), 3);
        check("longCyc-1", MatchingColoringFunctional.longestCycle(new int[]{2,-1,3,1}), -1);

        System.out.println("pass=" + pass + " fail=" + fail);
        if (fail > 0) System.exit(1);
    }
}
