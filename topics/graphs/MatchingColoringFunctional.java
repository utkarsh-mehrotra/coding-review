import java.util.*;

public class MatchingColoringFunctional {

    // ---------- Kuhn's algorithm: bipartite matching by repeated augmenting DFS ----------
    // O(V*E). Short enough to write live; reach for Hopcroft-Karp (08_NetworkFlow) only on large inputs.
    // adj.get(u) = right-side nodes that left node u can match.
    static int kuhnMatching(int left, int right, List<List<Integer>> adj) {
        int[] matchRight = new int[right];
        Arrays.fill(matchRight, -1);
        int matching = 0;
        for (int u = 0; u < left; u++) {
            if (tryAugment(u, adj, new boolean[right], matchRight)) matching++;
        }
        return matching;
    }

    private static boolean tryAugment(int u, List<List<Integer>> adj, boolean[] seen, int[] matchRight) {
        for (int v : adj.get(u)) {
            if (seen[v]) continue;
            seen[v] = true;
            // v is free, or its current partner can be re-routed to another right node
            if (matchRight[v] == -1 || tryAugment(matchRight[v], adj, seen, matchRight)) {
                matchRight[v] = u;
                return true;
            }
        }
        return false;
    }

    // ---------- k-coloring by backtracking (NP-hard in general; interview versions have small k / small n) ----------
    static boolean kColorable(List<List<Integer>> adj, int k) {
        return colorFrom(0, adj, k, new int[adj.size()]);
    }

    private static boolean colorFrom(int u, List<List<Integer>> adj, int k, int[] color) {
        if (u == adj.size()) return true;
        for (int c = 1; c <= k; c++) {
            boolean ok = true;
            for (int v : adj.get(u)) {
                if (color[v] == c) { ok = false; break; }
            }
            if (!ok) continue;
            color[u] = c;
            if (colorFrom(u + 1, adj, k, color)) return true;
            color[u] = 0; // backtrack
        }
        return false;
    }

    // ---------- Functional graph (every node has exactly one outgoing edge) ----------
    // Floyd's tortoise and hare: Find the Duplicate Number (LC 287). nums[i] is the "next" pointer of i.
    static int findDuplicate(int[] nums) {
        int slow = nums[0], fast = nums[0];
        do {
            slow = nums[slow];
            fast = nums[nums[fast]];
        } while (slow != fast);
        slow = nums[0];
        while (slow != fast) { // phase 2: distance from start to cycle entry equals distance from meeting point to entry
            slow = nums[slow];
            fast = nums[fast];
        }
        return slow;
    }

    // Same idea on a next[] array where -1 means "no next". Returns the first node on the cycle, or -1.
    static int cycleStart(int[] next, int start) {
        int slow = start, fast = start;
        while (true) {
            if (fast == -1 || next[fast] == -1) return -1;
            slow = next[slow];
            fast = next[next[fast]];
            if (fast == -1) return -1;
            if (slow == fast) break;
        }
        slow = start;
        while (slow != fast) {
            slow = next[slow];
            fast = next[fast];
        }
        return slow;
    }

    // ---------- Longest Cycle in a Graph (LC 2360): timestamp walk, O(N) ----------
    // Walk from each unvisited node; hitting a node stamped during THIS walk closes a cycle of length (time - stamp).
    static int longestCycle(int[] edges) {
        int n = edges.length;
        int[] stamp = new int[n]; // 0 = unvisited
        int time = 1, best = -1;
        for (int i = 0; i < n; i++) {
            if (stamp[i] != 0) continue;
            int walkStart = time;
            int u = i;
            while (u != -1 && stamp[u] == 0) {
                stamp[u] = time++;
                u = edges[u];
            }
            if (u != -1 && stamp[u] >= walkStart) best = Math.max(best, time - stamp[u]);
        }
        return best;
    }
}
