import java.util.*;

public class GraphExtrasTest {
    public static void main(String[] x) {
        int[][] h = {{1, 2, 2}, {3, 8, 2}, {5, 3, 5}};
        Check.eq("effortDSU", DSUExtended.minimumEffortPathDSU(h), 2);
        Check.eq("effortAgree", DSUExtended.minimumEffortPathDSU(h), DijkstraVariants.minimumEffortPath(h));
        Random r = new Random(3);
        for (int t = 0; t < 200; t++) {
            int m = 1 + r.nextInt(4), n = 1 + r.nextInt(4);
            int[][] g = new int[m][n];
            for (int[] row : g) for (int j = 0; j < n; j++) row[j] = r.nextInt(10);
            Check.eq("effortRand", DSUExtended.minimumEffortPathDSU(g), DijkstraVariants.minimumEffortPath(g));
        }
        List<List<String>> eq = List.of(List.of("a", "b"), List.of("b", "c"));
        double[] v = {2.0, 3.0};
        List<List<String>> q = List.of(List.of("a", "c"), List.of("b", "a"), List.of("a", "e"), List.of("a", "a"), List.of("x", "x"));
        Check.eq("calcDFS", Arrays.toString(DSUExtended.calcEquationDFS(eq, v, q)), "[6.0, 0.5, -1.0, 1.0, -1.0]");
        Check.eq("calcAgree", Arrays.toString(DSUExtended.calcEquationDFS(eq, v, q)), Arrays.toString(DSUExtended.calcEquation(eq, v, q)));
        Check.done("GraphExtrasTest");
    }
}
