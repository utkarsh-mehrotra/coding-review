# Graph Algorithms — Complete FAANG SDE2/3 Reference

DFS/BFS are table stakes. This covers everything that actually shows up at SDE2/3 bar-raiser level, organized by graph property so you can pattern-match a problem to its algorithm fast.

---

## 0. Traversal Primitives (the base every pattern builds on)

| Pattern | Use Case | Complexity |
|---|---|---|
| **DFS (recursive/iterative)** | Connectivity, cycle detection, path existence, backtracking | O(V+E) |
| **BFS** | Shortest path (unweighted), level-order, multi-source spread | O(V+E) |
| **Multi-source BFS** | "Nearest X from any source" — rotting oranges, walls-and-gates, 01 matrix | O(V+E) |
| **0-1 BFS (deque-based)** | Shortest path when edge weights are only 0 or 1 | O(V+E) |
| **Dial's Algorithm** | Shortest path with small bounded integer weights (bucket queue) | O(V + E + maxW) |

**Interview signal:** if weights are capped/small, interviewers expect you to *not* reach for Dijkstra by default — 0-1 BFS or Dial's is the "you actually understand the tradeoffs" answer.

---

## 1. Shortest Path Algorithms

| Algorithm | Core Idea | Constraint | Complexity |
|---|---|---|---|
| **Dijkstra** | Greedy, min-heap, relax nearest unvisited node | Non-negative weights only | O((V+E) log V) |
| **Bellman-Ford** | Relax all edges V−1 times | Handles negative weights; detects negative cycles | O(V·E) |
| **SPFA** | Queue-based Bellman-Ford optimization | Same as Bellman-Ford, faster in practice, worst-case same | O(V·E) worst case |
| **Floyd-Warshall** | DP over all-pairs, `dp[k][i][j]` | All-Pairs Shortest Path (APSP), works with negative edges (no negative cycles) | O(V³) |
| **Johnson's Algorithm** | Bellman-Ford reweighting + Dijkstra from every node | APSP on **sparse** graphs, beats Floyd-Warshall when E << V² | O(V² log V + VE) |
| **A\* Search** | Dijkstra + heuristic `f(n)=g(n)+h(n)` | Needs admissible heuristic; used in pathfinding/spatial/games | Same worst case as Dijkstra, faster in practice |

**Follow-up interviewers ask:** "your graph has 10^5 nodes but is sparse — why not Floyd-Warshall?" → Johnson's is the answer that shows depth.

---

## 2. Topological Sort & DAG Patterns

Works only on **Directed Acyclic Graphs**.

* **Kahn's Algorithm (BFS, in-degree based):** queue of zero-in-degree nodes; if fewer than V nodes processed at the end → cycle exists. Natural for "detect cycle in directed graph" too.
* **DFS-based Topo Sort:** post-order push onto a stack; reverse at the end.
* **Applications:** course scheduling, build systems, dependency resolution, longest path in a DAG (relax edges in topo order — O(V+E), no need for Dijkstra since DAG has no cycles).

**DP-on-DAG pattern:** once topologically sorted, many problems reduce to simple DP over the order (longest/shortest path, number of paths, critical path scheduling).

---

## 3. Disjoint Set Union (DSU) / Union-Find

Manages **partitions**, not edge traversal.

* **Operations:** `find()` with path compression, `union()` by rank/size → O(α(N)) amortized (effectively constant).
* **Primary uses:**
  - Cycle detection in undirected graphs
  - Connected components / dynamic connectivity queries
  - Kruskal's MST
  - Offline queries (process queries sorted, union as you go — classic for "number of islands over time" style problems)
  - Accounts-merge / equivalence-class problems

**Interview tell:** any problem with "connect/merge" operations interleaved with "are X and Y connected?" queries → DSU, not BFS/DFS per query (which would be too slow).

---

## 4. Minimum Spanning Tree (MST)

For connected, weighted, undirected graphs.

* **Kruskal's:** sort edges by weight, add via DSU if no cycle formed. O(E log E). Best when edge list is naturally available / graph is sparse.
* **Prim's:** grow tree from a start vertex, priority queue picks cheapest crossing edge. O(E log V). Best when graph is dense or given as adjacency matrix/list and you want to grow incrementally.
* **Follow-up:** "minimum cost to connect all points" (LeetCode 1584) is Prim's/Kruskal's on an implicit complete graph — recognizing the implicit graph is the actual skill being tested.

---

## 5. Strongly Connected Components & Directed Graph Structure

| Algorithm | Solves | Key Concept |
|---|---|---|
| **Tarjan's** | SCCs, single-pass | DFS discovery time + low-link values |
| **Kosaraju's** | SCCs, two-pass | DFS + transpose graph + DFS again in finish-time order |
| **SCC Condensation** | Collapse each SCC into one node | Produces a DAG — the *actual* follow-up half of most SCC questions (e.g., "min edges to add so whole graph is one SCC") |
| **2-SAT** | Boolean constraint satisfaction (`a OR b` clauses) | Reduces to implication graph + SCC; satisfiable iff no variable and its negation are in the same SCC |

**Bridges & Articulation Points**
* DFS discovery/low-link technique (same backbone as Tarjan's).
* Solves: critical connections in a network, single points of failure.
* LeetCode 1192 "Critical Connections in a Network" is the canonical version.

**Eulerian Path/Circuit — Hierholzer's Algorithm**
* Traverses every **edge** exactly once (vs. Hamiltonian which is every *node* — NP-hard, don't confuse them in an interview).
* Post-order DFS with edge removal; reconstruct path by reversing the recursion stack.
* Classic problem: "Reconstruct Itinerary" (LeetCode 332).

---

## 6. Network Flow & Matching

| Algorithm | Solves | Key Concept |
|---|---|---|
| **Ford-Fulkerson / Edmonds-Karp** | Max flow / min cut | Augmenting paths via BFS (Edmonds-Karp is Ford-Fulkerson + BFS, guarantees O(VE²)) |
| **Dinic's Algorithm** | Max flow, faster | Level graphs + blocking flow, O(V²E) general, O(E√V) on unit-capacity/bipartite |
| **Hopcroft-Karp** | Maximum Bipartite Matching | Repeated BFS+DFS augmenting path search, O(E√V) |
| **Hungarian Algorithm** | Min-cost bipartite matching / assignment problem | O(V³) |

**Interview relevance at SDE2/3:** full max-flow implementations are rare asks, but recognizing a problem as a **flow/matching problem in disguise** is the actual signal:
- "Job scheduling with constraints" / "task assignment" → bipartite matching
- "Max number of non-overlapping X" with capacity constraints → flow
- Ford-Fulkerson/Edmonds-Karp is the one implementation worth knowing cold; Dinic's/Hopcroft-Karp are "know the name and complexity, mention as optimization" level for SDE2/3 (more of a Staff-level implementation ask).

---

## 7. Graph Coloring & Bipartiteness

* **Bipartite Check:** 2-coloring via BFS/DFS — assign alternating colors, conflict = not bipartite. O(V+E).
* **Graph Coloring (k-coloring):** NP-hard in general; interview versions are usually small-k backtracking (e.g., "can this graph be colored with k colors" for k ≤ 4).

---

## 8. Bitmask DP on Graphs

* **Held-Karp (TSP DP):** `dp[mask][i]` = shortest path visiting node set `mask`, ending at `i`. O(2^N · N²).
* Comes up disguised as "visit all X" problems — "shortest path visiting all nodes" (LeetCode 847) is Held-Karp variant on an unweighted graph.
* Recognize by: N ≤ ~15-20 and "visit all/some subset" phrasing — that bound is the tell that it's bitmask DP, not a pure graph traversal.

---

## Decision Tree — "Which algorithm do I reach for?"

1. **Unweighted, shortest path?** → BFS (multi-source if multiple starts; 0-1 BFS if weights are 0/1)
2. **Weighted, non-negative, single source?** → Dijkstra
3. **Weighted, negative edges possible, single source?** → Bellman-Ford (also tells you if there's a negative cycle)
4. **All-pairs shortest path, dense graph?** → Floyd-Warshall
5. **All-pairs shortest path, sparse graph?** → Johnson's
6. **DAG + need ordering/dependency resolution?** → Topological sort (Kahn's or DFS)
7. **DAG + need longest/shortest/count paths?** → DP over topo order
8. **Undirected + dynamic connectivity/merge queries?** → DSU
9. **Connect all nodes, minimize total edge weight?** → Kruskal's (sparse/edge-list) or Prim's (dense/adjacency-matrix)
10. **Directed graph, need to find tightly-coupled clusters?** → Tarjan's/Kosaraju's (SCC), then condense to DAG if there's a follow-up
11. **Need critical/fragile points in a network?** → Bridges & articulation points
12. **Need to traverse every edge exactly once?** → Hierholzer's (Eulerian path/circuit)
13. **Boolean 2-variable clause satisfaction?** → 2-SAT via implication graph + SCC
14. **Assignment/matching with capacity constraints?** → Max flow (Ford-Fulkerson/Edmonds-Karp) or bipartite matching (Hopcroft-Karp)
15. **Small N (≤20), must visit all nodes/subsets?** → Bitmask DP (Held-Karp style)

---

## SDE2 vs SDE3 Expectation Calibration

- **SDE2 bar:** BFS/DFS variants, Dijkstra, Union-Find, Kruskal's/Prim's, topological sort, bipartite check — implement cleanly, discuss complexity, handle edge cases (disconnected components, self-loops, multi-edges).
- **SDE3 bar:** everything above cold, plus recognizing *which* algorithm applies to a disguised/real-world framed problem, discussing tradeoffs (Johnson's vs Floyd-Warshall, Dinic's vs Edmonds-Karp), SCC condensation as a two-step technique, and reasoning about complexity on dense vs. sparse graphs without being told which.
- **Staff bar (beyond SDE3, for context):** full flow/matching implementations, 2-SAT reductions, amortized analysis of DSU from first principles, choosing/justifying approximations for NP-hard graph coloring instances.

---

## Practice Problem Map (LeetCode-anchored)

| Pattern | Problems |
|---|---|
| Multi-source BFS | 994 Rotting Oranges, 286 Walls and Gates, 1162 As Far from Land as Possible |
| Dijkstra | 743 Network Delay Time, 1631 Path With Minimum Effort, 787 Cheapest Flights Within K Stops |
| Bellman-Ford | 787 Cheapest Flights Within K Stops (K-constrained variant) |
| Topological Sort | 207/210 Course Schedule I/II, 269 Alien Dictionary, 1136 Parallel Courses |
| DSU | 547 Number of Provinces, 684 Redundant Connection, 721 Accounts Merge, 1319 Number of Operations to Make Network Connected |
| MST | 1584 Min Cost to Connect All Points, 1135 Connecting Cities With Minimum Cost |
| SCC | 1192 Critical Connections in a Network (bridges), 1568 Minimum Number of Days to Disconnect Island |
| Eulerian Path | 332 Reconstruct Itinerary |
| Bipartite | 785 Is Graph Bipartite?, 886 Possible Bipartition |
| Bitmask DP | 847 Shortest Path Visiting All Nodes, 943 Find the Shortest Superstring |
| Flow/Matching | 1349 Max Students Taking Exam (bitmask+bipartite hybrid), 1947 Maximum Compatibility Score Sum |

---

*Companion to your existing interview prep — pairs with the system design and STAR-answer prep already in progress.*
