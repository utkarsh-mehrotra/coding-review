# Critique Review: Verdicts and Correction Plan

Source: the "Coding Interview Review: inconsistency log" doc (both tabs), written against commit `9af45fa`, which is the current `main`. Every claim below was checked on branch `critique-review` on 2026-11-19: **Run** means reproduced with a harness, **Read** means confirmed in the source or `problems.json`.

**Bottom line:** corrections are required. Of the items in the tables below: 26 need a code, data or test fix; 27 need a comment change only; 2 are best answered with a documentation note; 2 conflict with the memo-only DP rule and get a documented answer instead of the suggested bottom-up rewrite; 3 are refuted or partly refuted; 1 (`maxEnvelopes`) and 2 scope calls (S1, S2) need your decision; 1 is optional.

## Standing rules that decide some verdicts
- DP templates stay top-down memoization (user rule, 2026-11-17). Fixes that say "bottom-up table", "rolling counters", "Kadane loop" or "stop running DP tests in a big-stack thread" are not applied as written.
- The site is a quick-revision reference, not a practice tool.
- Fixes stay inside LeetCode's stated constraints unless the template claims more.

## 1. Correctness bugs

| # | Template | Claim | Check | Verdict | Fix |
|---|---|---|---|---|---|
| 1.1 | `DPSequences.maxEnvelopes` | Comment says patience sorting is required at n = 1e5, code calls the O(n²) memo LIS | Read | **Decision for you** | Memo-only rule means no second implementation. Either reword the comment to "this memo version times out at LC 354's limits; the pass needs patience sorting" (recommended), or allow one non-DP exception here |
| 1.2 | `RangeStructures.getSkyline` | Comparator `p[1]-q[1]` overflows at heights near 2³¹−1 | Run: `[[0,2,MAX],[2,3,MAX]]` gives 4 points instead of `[[0,MAX],[3,0]]`. LC 218 allows these heights | **Fix** | `Integer.compare`; add the case to TreesTest. (Tab 1 marked this refuted after random tests that never reached large heights; the fix list is right) |
| 1.3 | `SCC_Bridges_2SAT.bridgeDFS` | Skips the parent by vertex, so a doubled edge is reported as a bridge | Run: edges 0=1 twice returns bridge `[0,1]` | **Fix** | Skip the parent by edge index (adjacency stores `{to, edgeId}`), randomized multi-edge test against brute force |
| 1.4 | `DPCombined` LC 1425 / 1696 | O(n·k) memo times out at n = k = 1e5 | Read (user accepted O(n·k) with a note) | **Comment only** | Reword both notes to say "REQUIRED at LC limits", the same wording as 1.1 |
| 1.5 | `RangeStructures.countInversions` | Labelled `// LC 493` but counts inversions, not reverse pairs | Run: returns 5 on `[2,4,3,5,1]`; `ArraysVariants.reversePairs` returns 3 | **Fix** | Remove the LC 493 label (counting inversions is LC 315's sibling), then `make problems` |
| 1.6 | `Intervals.maxOverlap` | `[[5,5]]` throws | Run: `ArrayIndexOutOfBoundsException` | **Fix** | Guard the `ends[e]` scan with `e < n` |
| 1.7 | `DPLinear.maxProduct` | `[50000,50000]` returns 50000 | Run. Outside LC 152's 32-bit guarantee | **Comment only** | Note the LC bound; no long arithmetic needed |
| 1.8a | `numTilings(0)` | Returns 0, comment says f(0)=1 | Run. LC 790 has n ≥ 1 | **Fix (tiny)** | Return 1 for n = 0 so code matches the comment |
| 1.8b | `powMod(5,0,1)` | Returns 1, should be 0 | Run | **Fix (tiny)** | Start from `1 % m` |
| 1.8c | `minCostII` with k = 1, n ≥ 2 | Overflows | Run: returns −2147483648. LC 265 has k ≥ 2 | **Comment only** | State k ≥ 2 |
| 1.8d | `numTrees` | Overflows at n ≥ 20 | Run: n = 19 fits (LC 96 limit), n = 20 overflows | **Comment only** | State n ≤ 19 |
| 1.9 | `heldKarpTSP` | n = 1 returns MAX_VALUE; `// LC 943` sits on TSP | Run + Read | **Fix** | Return 0 for n = 1; reword the label to "LC 943 reduces to this TSP over overlap costs" |
| 1.10 | `StacksVariants.minRemoveToMakeValid` | `'*'` sentinel removes real `*` | Run: `"a*b(c"` gives `"abc"`. LC 1249 input has no `*` | **Fix** | `boolean[] drop`; cheap and removes a trap |
| 1.11 | `WeightedDSU` | Contradictory ratio silently ignored | Run: a/b = 2 then 3 still gives 2.0. LC 399 has none | **Comment only** | Note that conflicts are not detected; optional `boolean union` |
| 1.12 | `alienOrder` | `""` for both invalid and valid-empty | Run | **Comment only** | LC 269 returns `""` for invalid by spec; note it |
| 1.13 | `MyCalendarThree` | O(n) per book | Run: 20,000 books in 3.3 s, but LC 732's 400 calls take 2 ms | **Comment only** | State the cost; a lazy segment tree is the follow-up |
| 1.14 | `solve2SAT`, shared static `disc/low/timer` | No assignment; shared state across tarjan, bridges and 2-SAT | Read | **Fix** | Return the assignment from SCC order; make state local to each call |
| 1.15 | `bellmanFord`, `johnsons` | Detects only cycles reachable from start; Johnson's needs edges and adj | Read | **Comment only** | Document both |
| 1.16 | `BST.largestBSTSubtree` | MIN/MAX sentinel collision; header promises ceiling | Read | **Fix** | Long sentinels; drop "ceiling" from the header |
| 1.17 | `Trie.findMaximumXOR` | Non-negative only, large allocation | Read | **Comment only** | State the bound |
| 1.18 | `TreesVariants` LCA | Wrong when a node is not under the root | Read. LC 236 guarantees both exist | **Comment only** | State the precondition |
| 1.19 | `LazySeg` n = 0, design guards (`MinHeap.pop` empty, `BloomFilter(0)`, `IndexedMinPQ`, `PeekingIterator`) | Crash or corrupt on degenerate input | Read | **Optional** | One-line guards; outside LC limits (scope call S1) |
| 1.20 | `quickselectKthLargest`, `medianSlidingWindow` | Fixed `Random(1)`; O(nk) baseline unlabeled | Read | **Fix** | Unseeded `Random`; label the baseline's cost |

## 2. Stack depth at LeetCode limits

| # | Claim | Verdict | Plan |
|---|---|---|---|
| 2.1 | DP memo templates overflow a 1 MB stack at LC limits: `coinChangeMin`, `numSquares`, `jobScheduling`, `maxResult`, `constrainedSubsetSum`, `maxProduct`, `maxSubArray`, `maxProfit*`, `countVowelPermutation`, `checkRecord`, `new21Game` | **Conflicts with the memo-only rule** | Keep memo. Add a one-line `// DEPTH: n frames; run in a big-stack thread at n ≥ 1e4` to each of these (the DP pitfall already says it once) |
| 2.2 | "Stop running DP tests in a big-stack thread" | **Conflicts with the memo-only rule** | Keep the big-stack test thread. Add a test that checks each listed template overflows only above about 1e4 frames and passes in a big-stack thread at the LC limit |
| 2.3 | Tree recursion overflows at a 1e4-node chain: `diameter`, `serialize`, `robTree`, `maxPathSum`, `LCA` | **Document** | Recursive tree code is the interview norm and LC tree tests are balanced or small; add the same DEPTH note. No iterative rewrite unless you want it |

## 3. Comments that contradict code (all Read, all confirmed; comment-only fixes)

`MST.java:48` "04_DSU.java" · `MatchingColoringFunctional.java:7` "08_NetworkFlow" · `TopoSortDAG` "result.size() < n" vs throw · `NetworkFlow` `{to, capIndex}` and unused `cap` · `StringAlgorithms` LC 12 header and "expand around center" in DPSequences (now memo) · `BST` `{isBST,size,min,max}` order · `TreesVariants.infect` off-by-one · `EulerianPath` `totalEdges` · SCC header example that does not compile · `HeapsTopK` trailing comment with no code · `Trie.findWords` "prune" · partial `// LC 2223` and LC 496 labels · `sumSubarrayMins` inverted comment.

## 4. Duplicates, input mutation, shared state

| # | Claim | Verdict | Plan |
|---|---|---|---|
| 4.1 | Duplicates (`DSU` vs `MST.DSU`, `sortedArrayToBST`, `powMod`, `grayCode`, `mySqrt`, shuffle, reservoir) | **Fix, low** | Keep one of each, point the other at it. Each file must still compile alone with `javac`, so `MST.DSU` stays inline with a "same as DSU.java" comment |
| 4.2 | Input mutation (`isPalindrome`, `mergeKLists`, `pairSum`, `Intervals.merge`, Kruskal `edges`, grid functions, `firstMissingPositive`, `largestDivisibleSubset`) | **Comment only** | One-line "mutates input" note where it does; LC allows mutation |
| 4.3 | Static state (`calculate3.pos`, `BinaryTree`, `TreesVariants` globals) | **Fix** | Reset at each entry call (already true for `pos`); move globals into a small result holder where a test can leak state |
| 4.4 | About 21 comparators use subtraction | **Fix** | Replace with `Integer.compare` where values can be large; matches the site's own pitfall note |

## 5. LeetCode mapping

| # | Problem | Now | Verdict | Plan |
|---|---|---|---|---|
| 5.1 | 12 | `romanToInt` | **Fix** | Remove LC 12 from the comment (no `intToRoman`) |
| 5.2 | 1947 | `isBipartite` (practice map) | **Fix** | Move to bitmask DP in the practice map; cite on `minCostAssignment` |
| 5.3 | 1515, 470, 478, 519 | `Reservoir` | **Fix** | Tighten the `randomized` tag rule in `match.py` to reservoir and shuffle titles only |
| 5.4 | 488, 2291, 641, 3956 | wrong tag matches | **Fix** | Remove "zuma" from the interval rule; add title exclusions |
| 5.5 | 847, 286, 1162, 307, 1349, 1568 | practice-map topic picks the wrong template | **Fix** | Add `// LC` citations on the right members (`shortestPathVisitingAllNodes`, `multiSourceBFS`, `SegTree`, `minCostAssignment`) |
| 5.6 | 54, 73, 50, 113 | first template wrong | **Fix** | Move the `// LC` to `spiralOrder`, `setZeroes`, `myPow`, `pathSumII` |
| 5.7 | 108, 1044, 783, 938 | multi-LC comment attaches every id to one member | **Fix** | Split the `TreesVariants.java:316` comment; `inventory.py` attaches only an `LC n` that starts a comment or is the method's own label |
| 5.8 | 943, 644 | loose but deliberate | **Comment only** | Add "reduces to" notes |
| 5.9 | Tier B about 4% wrong, 30% loose | **Document** | Say on the Coverage page that tier B means "nearest template", not "solves it" |

## 6. Tests and layout

| # | Claim | Verdict | Plan |
|---|---|---|---|
| 6.1 | 9 of 15 topics have no tests | **Partly refuted** | Those topics are covered by `Deepen1-3Test`, `PatternsTest` and `StructuresTest` under other folders. Fix the naming instead (6.2) |
| 6.2 | `Deepen*Test` and `StructuresTest` sit in the wrong folders | **Fix** | Rename by coverage and move next to the code; update the Makefile |
| 6.3 | Graph tests define their own `check()` | **Fix** | Switch to `common/Check.java` |
| 6.4 | 30 of 729 methods never named in a test | **Fix** | Add known-input checks for the listed ones |
| 6.5 | No LC-limit inputs; regressions for the Run rows | **Fix** | One regression test per section-1 Run row; LC-limit cases for DP and tree templates |

## 7. Docs and site

| # | Claim | Verdict | Plan |
|---|---|---|---|
| 7.1 | README counts stale (2,196 / 522 / 1,674) | **Fix** | Generate the line from `problems.json` at build time, or update by hand after section 5 |
| 7.2 | ConsistentHash page says O(1) | **Refuted** | No O(1) claim on the template or its decision entry |
| 7.3 | View HTML files are about 1 KB stubs | **Refuted** | They are intentional page fragments (intro, complexity table, `{{code:…}}` slots) |

## Scope calls for you
- **S1 Edge-case guards outside LeetCode's constraints** (1.19 and the doc's "low-priority crashes": empty grids, k = 0, null roots). Recommended: skip; they add lines to templates you read for revision.
- **S2 Niche items** (about 75 templates the doc suggests cutting from the revision path). Recommended: keep the code; add a "niche" tag that the Cheat Sheet and sidebar can hide behind a toggle.

## Execution order (each step: tests first, `make all`, commit)
1. Correctness fixes 1.2, 1.3, 1.5, 1.6, 1.8a, 1.8b, 1.9, 1.10, 1.14, 1.16, 1.20 with regression tests.
2. Comment-only fixes: section 1 comment rows, all of section 3, 2.1/2.3 DEPTH notes, 4.2.
3. Mapping: 5.1 to 5.8, re-run `make problems`, check the counts move as expected.
4. Shared state and comparators: 4.3, 4.4; duplicates 4.1.
5. Tests and layout: 6.2 to 6.5, plus the 2.2 depth test.
6. Docs: 7.1, the tier B note (5.9).
7. Answer the doc's threads: mark each item fixed, documented, or declined with the reason.
