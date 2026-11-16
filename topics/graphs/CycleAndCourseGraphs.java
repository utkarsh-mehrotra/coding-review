import java.util.*;

public class CycleAndCourseGraphs {

    // ---------- Directed cycle detection: 3-color DFS ----------
    // white (0) = unvisited, gray (1) = on the current DFS path, black (2) = fully explored.
    // An edge into a gray node is a back edge, i.e. a cycle.
    static boolean hasCycleDirected(int n, List<List<Integer>> adj) {
        int[] color = new int[n];
        for (int i = 0; i < n; i++) {
            if (color[i] == 0 && dfsCycle(i, adj, color)) return true;
        }
        return false;
    }

    private static boolean dfsCycle(int u, List<List<Integer>> adj, int[] color) {
        color[u] = 1;
        for (int v : adj.get(u)) {
            if (color[v] == 1) return true;
            if (color[v] == 0 && dfsCycle(v, adj, color)) return true;
        }
        color[u] = 2;
        return false;
    }

    // ---------- Eventual Safe States (LC 802): nodes that cannot reach a cycle ----------
    static List<Integer> eventualSafeNodes(int[][] graph) {
        int n = graph.length;
        int[] color = new int[n];
        List<Integer> res = new ArrayList<>();
        for (int i = 0; i < n; i++) if (isSafe(i, graph, color)) res.add(i);
        return res;
    }

    private static boolean isSafe(int u, int[][] g, int[] color) {
        if (color[u] != 0) return color[u] == 2; // gray here means we re-entered the path: unsafe
        color[u] = 1;
        for (int v : g[u]) if (!isSafe(v, g, color)) return false;
        color[u] = 2;
        return true;
    }

    // ---------- Course Schedule I/II (LC 207/210): Kahn's. prerequisites[i] = {course, prereq} ----------
    static int[] courseOrder(int numCourses, int[][] prerequisites) {
        List<List<Integer>> adj = new ArrayList<>();
        for (int i = 0; i < numCourses; i++) adj.add(new ArrayList<>());
        int[] indeg = new int[numCourses];
        for (int[] p : prerequisites) {
            adj.get(p[1]).add(p[0]);
            indeg[p[0]]++;
        }
        Queue<Integer> q = new ArrayDeque<>();
        for (int i = 0; i < numCourses; i++) if (indeg[i] == 0) q.add(i);
        int[] order = new int[numCourses];
        int idx = 0;
        while (!q.isEmpty()) {
            int u = q.poll();
            order[idx++] = u;
            for (int v : adj.get(u)) if (--indeg[v] == 0) q.add(v);
        }
        return idx == numCourses ? order : new int[0]; // empty = cycle, impossible
    }

    // ---------- Parallel Courses (LC 1136): Kahn's by level = number of semesters ----------
    // Courses are 1..n; relations[i] = {prev, next}.
    static int minimumSemesters(int n, int[][] relations) {
        List<List<Integer>> adj = new ArrayList<>();
        for (int i = 0; i < n; i++) adj.add(new ArrayList<>());
        int[] indeg = new int[n];
        for (int[] r : relations) {
            adj.get(r[0] - 1).add(r[1] - 1);
            indeg[r[1] - 1]++;
        }
        Queue<Integer> q = new ArrayDeque<>();
        for (int i = 0; i < n; i++) if (indeg[i] == 0) q.add(i);
        int semesters = 0, taken = 0;
        while (!q.isEmpty()) {
            semesters++;
            for (int sz = q.size(); sz > 0; sz--) { // everything available this semester
                int u = q.poll();
                taken++;
                for (int v : adj.get(u)) if (--indeg[v] == 0) q.add(v);
            }
        }
        return taken == n ? semesters : -1;
    }

    // ---------- Alien Dictionary (LC 269): build the graph from adjacent words, then Kahn's ----------
    static String alienOrder(String[] words) {
        Map<Character, Set<Character>> adj = new HashMap<>();
        Map<Character, Integer> indeg = new HashMap<>();
        for (String w : words) {
            for (char c : w.toCharArray()) {
                adj.putIfAbsent(c, new HashSet<>());
                indeg.putIfAbsent(c, 0);
            }
        }
        for (int i = 0; i + 1 < words.length; i++) {
            String a = words[i], b = words[i + 1];
            if (a.length() > b.length() && a.startsWith(b)) return ""; // "abc" before "ab" is invalid
            for (int j = 0; j < Math.min(a.length(), b.length()); j++) {
                if (a.charAt(j) != b.charAt(j)) {
                    if (adj.get(a.charAt(j)).add(b.charAt(j))) indeg.merge(b.charAt(j), 1, Integer::sum);
                    break; // only the first differing char gives information
                }
            }
        }
        Queue<Character> q = new ArrayDeque<>();
        for (Map.Entry<Character, Integer> e : indeg.entrySet()) if (e.getValue() == 0) q.add(e.getKey());
        StringBuilder sb = new StringBuilder();
        while (!q.isEmpty()) {
            char c = q.poll();
            sb.append(c);
            for (char nb : adj.get(c)) if (indeg.merge(nb, -1, Integer::sum) == 0) q.add(nb);
        }
        return sb.length() == indeg.size() ? sb.toString() : ""; // shorter = cycle
    }

    // ---------- Clone Graph (LC 133): old node -> new node map doubles as the visited set ----------
    static class Node {
        int val;
        List<Node> neighbors = new ArrayList<>();
        Node(int val) { this.val = val; }
    }

    static Node cloneGraph(Node start) {
        if (start == null) return null;
        Map<Node, Node> copy = new HashMap<>();
        Queue<Node> q = new ArrayDeque<>();
        copy.put(start, new Node(start.val));
        q.add(start);
        while (!q.isEmpty()) {
            Node cur = q.poll();
            for (Node nb : cur.neighbors) {
                if (!copy.containsKey(nb)) {
                    copy.put(nb, new Node(nb.val));
                    q.add(nb);
                }
                copy.get(cur).neighbors.add(copy.get(nb));
            }
        }
        return copy.get(start);
    }
}
