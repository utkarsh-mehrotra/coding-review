import java.util.*;

public class EulerianPath {

    // ---------- Hierholzer's Algorithm ----------
    // Directed graph, adjacency as multiset (use Deque so edges can be removed as used).
    // Precondition for Eulerian circuit: every node has in-degree == out-degree, graph connected.
    // Precondition for Eulerian path: exactly one node has out-degree = in-degree+1 (start),
    //                                  exactly one has in-degree = out-degree+1 (end), rest balanced.
    static List<Integer> hierholzer(int n, Map<Integer, Deque<Integer>> adj, int start) {
        Deque<Integer> pathStack = new ArrayDeque<>();
        List<Integer> circuit = new ArrayList<>();
        pathStack.push(start);

        while (!pathStack.isEmpty()) {
            int u = pathStack.peek();
            Deque<Integer> neighbors = adj.get(u);
            if (neighbors != null && !neighbors.isEmpty()) {
                int v = neighbors.poll(); // consume edge u->v
                pathStack.push(v);
            } else {
                circuit.add(pathStack.pop()); // no more edges out of u, finalize it
            }
        }
        Collections.reverse(circuit);
        return circuit; // caller checks: if size != (total edge count)+1, the graph had no valid Eulerian path. `n` is unused (kept for the signature).
    }

    // ---------- Example: Reconstruct Itinerary (LeetCode 332) shape ----------
    // tickets = [["JFK","SFO"], ["SFO","ATL"], ...] -> lexicographically smallest valid itinerary
    static List<String> findItinerary(List<List<String>> tickets) {
        Map<String, PriorityQueue<String>> graph = new HashMap<>();
        for (List<String> t : tickets) {
            graph.computeIfAbsent(t.get(0), k -> new PriorityQueue<>()).add(t.get(1));
        }
        List<String> route = new ArrayList<>();
        Deque<String> stack = new ArrayDeque<>();
        stack.push("JFK");

        while (!stack.isEmpty()) {
            String airport = stack.peek();
            PriorityQueue<String> destinations = graph.get(airport);
            if (destinations != null && !destinations.isEmpty()) {
                stack.push(destinations.poll());
            } else {
                route.add(stack.pop());
            }
        }
        Collections.reverse(route);
        return route;
    }
}
