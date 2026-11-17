"""Step 3: map every LeetCode algorithm problem to templates with a confidence tier.
Usage: python3 tools/lc-coverage/match.py build/lc-inventory.json tools/lc-coverage/data/lc_all.json tools/lc-coverage/data/problems.json
Tiers: A verified (cited in code / practice map), B specific tag, C broad tag + keyword, U uncovered.
"""
import json, re, sys, collections

inv = json.load(open(sys.argv[1]))
lc = json.load(open(sys.argv[2]))
out = sys.argv[3]
T = inv['templates']
TOPIC_OF = {k: v['topic'] for k, v in T.items()}


def has(tags, *xs): return any(x in tags for x in xs)


# (tier, template, predicate(tags, title)) -- ordered most specific first
RULES = [
    # ---- graphs, specific algorithms
    ('B', 'MST#kruskalsMST', lambda t, s: has(t, 'minimum-spanning-tree')),
    ('B', 'SCC_Bridges_2SAT#tarjanSCC', lambda t, s: has(t, 'strongly-connected-component', 'kosarajus-algorithm')),
    ('B', 'SCC_Bridges_2SAT#findBridgesAndArticulation', lambda t, s: has(t, 'biconnected-component')),
    ('B', 'EulerianPath#hierholzer', lambda t, s: has(t, 'eulerian-circuit')),
    ('B', 'NetworkFlow#Dinic', lambda t, s: has(t, 'maximum-flow') or 'max flow' in s),
    ('B', 'BipartiteAndBitmaskDP#isBipartite', lambda t, s: has(t, 'bipartite-graph')),
    ('B', 'TopoSortDAG#kahnsTopoSort', lambda t, s: has(t, 'topological-sort')),
    ('B', 'DSUExtended#calcEquation', lambda t, s: has(t, 'union-find') and 'equation' in s),
    ('B', 'DSU#union', lambda t, s: has(t, 'union-find')),
    ('B', 'DijkstraVariants#cheapestFlightsDijkstraState', lambda t, s: has(t, 'shortest-path', 'dijkstra') and ('stops' in s or 'at most k' in s)),
    ('B', 'ShortestPath#dijkstra', lambda t, s: has(t, 'dijkstra', 'shortest-path')),
    ('B', 'TopoSortDAG#longestPathDAG', lambda t, s: has(t, 'directed-acyclic-graph')),
    ('B', 'TreeGraphs#LCA', lambda t, s: has(t, 'binary-lifting') or (has(t, 'lowest-common-ancestor') and not has(t, 'binary-tree', 'binary-search-tree'))),
    ('B', 'TreeGraphs#sumOfDistancesInTree', lambda t, s: has(t, 'dp-on-trees') and not has(t, 'binary-tree')),
    # ---- strings
    ('B', 'StringsVariants#suffixArray', lambda t, s: has(t, 'suffix-array')),
    ('B', 'StringAlgorithms#zFunction', lambda t, s: has(t, 'z-algorithm')),
    ('B', 'StringAlgorithms#kmpSearch', lambda t, s: has(t, 'knuth-morris-pratt-algorithm')),
    ('B', 'StringAlgorithms#rabinKarp', lambda t, s: has(t, 'rolling-hash')),
    ('B', 'StringAlgorithms#manacher', lambda t, s: 'palindrom' in s and has(t, 'string') and not has(t, 'dynamic-programming') and 'longest' in s),
    ('B', 'StringAlgorithms#kmpSearch', lambda t, s: has(t, 'string-matching')),
    ('B', 'Trie#findMaximumXOR', lambda t, s: has(t, 'trie') and has(t, 'bit-manipulation')),
    ('B', 'Trie#insert', lambda t, s: has(t, 'trie')),
    # ---- range structures
    ('B', 'RangeStructures#LazySeg', lambda t, s: has(t, 'segment-tree') and ('range' in s or 'update' in s)),
    ('B', 'RangeStructures#SegTree', lambda t, s: has(t, 'segment-tree')),
    ('B', 'RangeStructures#countSmaller', lambda t, s: has(t, 'binary-indexed-tree') and ('smaller' in s or 'inversion' in s or 'pairs' in s)),
    ('B', 'RangeStructures#Fenwick', lambda t, s: has(t, 'binary-indexed-tree')),
    # ---- DP, specific shapes
    ('B', 'DPSequences#lengthOfLIS', lambda t, s: has(t, 'longest-increasing-subsequence') or ('increasing subsequence' in s)),
    ('B', 'DPKnapsack#knapsack01', lambda t, s: has(t, 'knapsack-problem', '0-1-knapsack')),
    ('B', 'DPBitmaskDigit#minCostAssignment', lambda t, s: has(t, 'bitmask') and has(t, 'dynamic-programming', 'memoization')),
    ('B', 'DPBitmaskDigit#countDigitOne', lambda t, s: has(t, 'dynamic-programming') and ('digit' in s) and has(t, 'math')),
    ('B', 'DPLinear#maxProfitK', lambda t, s: 'stock' in s and has(t, 'dynamic-programming', 'greedy', 'array')),
    ('B', 'DPLinear#rob', lambda t, s: 'house robber' in s),
    ('B', 'DPGridInterval#uniquePaths', lambda t, s: has(t, 'dynamic-programming') and has(t, 'matrix')),
    ('B', 'DPSequences#longestPalindromeSubseq', lambda t, s: has(t, 'dynamic-programming') and 'palindrom' in s),
    ('B', 'DPSequences#longestCommonSubsequence', lambda t, s: has(t, 'dynamic-programming') and has(t, 'string') and ('subsequence' in s or 'edit' in s or 'distance' in s or 'common' in s)),
    ('B', 'DPGridInterval#matrixChainOrder', lambda t, s: has(t, 'dynamic-programming') and re.search(r'burst|merge stones|cut.*stick|triangulation|polygon|strange printer|remove boxes|zuma', s)),
    ('B', 'DPVariants#predictTheWinner', lambda t, s: has(t, 'game-theory', 'minimax-algorithm') and has(t, 'dynamic-programming', 'memoization')),
    ('B', 'ArraysHashing#nextPermutation', lambda t, s: 'next permutation' in s or 'next greater element iii' in s or 'next smaller' in s and has(t, 'math')),
    # ---- stacks / queues
    ('B', 'MonotonicStructures#maxSlidingWindow', lambda t, s: has(t, 'monotonic-queue')),
    ('B', 'MonotonicStructures#largestRectangleArea', lambda t, s: has(t, 'monotonic-stack') and ('rectangle' in s or 'histogram' in s)),
    ('B', 'MonotonicStructures#sumSubarrayMins', lambda t, s: has(t, 'monotonic-stack') and ('sum of' in s or 'subarray' in s)),
    ('B', 'MonotonicStructures#nextGreater', lambda t, s: has(t, 'monotonic-stack')),
    ('B', 'MonotonicStructures#isValid', lambda t, s: has(t, 'bracket-sequences') or (has(t, 'stack') and 'parenthes' in s)),
    ('B', 'MonotonicStructures#calculate', lambda t, s: has(t, 'stack') and re.search(r'calculator|expression|pars|formula|atoms|decode string', s)),
    # ---- design
    ('B', 'DesignStructures#LRUCache', lambda t, s: (has(t, 'doubly-linked-list') and has(t, 'design')) or 'lru' in s or 'lfu' in s),
    ('B', 'HeapsTopK#MedianFinder', lambda t, s: 'median' in s and has(t, 'heap-priority-queue')),
    # ---- heaps
    ('B', 'HeapsTopK#quickselectKthLargest', lambda t, s: has(t, 'quickselect')),
    ('B', 'HeapsTopK#mergeKSorted', lambda t, s: has(t, 'heap-priority-queue') and 'merge' in s and 'sorted' in s),
    ('B', 'HeapsTopK#minMeetingRooms', lambda t, s: has(t, 'heap-priority-queue') and ('meeting' in s or 'room' in s or 'interval' in s)),
    ('B', 'HeapsTopK#findKthLargest', lambda t, s: has(t, 'heap-priority-queue') and ('kth' in s or 'k ' in s or 'top' in s or 'closest' in s or 'frequent' in s)),
    # ---- intervals
    ('B', 'Intervals#maxOverlap', lambda t, s: has(t, 'line-sweep', 'sweep-line')),
    ('B', 'Intervals#merge', lambda t, s: 'interval' in s and has(t, 'sorting', 'array')),
    # ---- binary search
    ('B', 'BinarySearch#minEatingSpeed', lambda t, s: has(t, 'binary-search') and re.search(r'\b(minimi[sz]e|maximi[sz]e|minimum|maximum|least|smallest|largest)\b', s) and not has(t, 'binary-search-tree')),
    ('B', 'BinarySearch#searchRotated', lambda t, s: has(t, 'binary-search') and 'rotated' in s),
    ('B', 'BinarySearch#lowerBound', lambda t, s: has(t, 'binary-search') and not has(t, 'binary-search-tree', 'tree')),
    # ---- sliding window / two pointers
    ('B', 'SlidingWindowPointers#lengthOfLongestSubstring', lambda t, s: has(t, 'sliding-window')),
    ('B', 'SlidingWindowPointers#threeSum', lambda t, s: has(t, 'two-pointers') and re.search(r'\b(3sum|4sum|two sum|three|triplet|quadruplet)', s)),
    ('B', 'SlidingWindowPointers#twoSumSorted', lambda t, s: has(t, 'two-pointers') and re.search(r'\bsum\b|pair|container|palindrom|remove duplicates|move zeroes|merge sorted|squares|boats', s)),
    # ---- trees
    ('B', 'BST#BSTIterator', lambda t, s: has(t, 'binary-search-tree') and 'iterator' in s),
    ('B', 'BST#isValidBST', lambda t, s: has(t, 'binary-search-tree')),
    ('B', 'BinaryTree#lowestCommonAncestor', lambda t, s: has(t, 'lowest-common-ancestor')),
    ('B', 'BinaryTree#serialize', lambda t, s: 'serializ' in s or 'construct' in s and has(t, 'binary-tree')),
    ('B', 'BinaryTree#maxPathSum', lambda t, s: has(t, 'binary-tree') and has(t, 'dynamic-programming', 'dp-on-trees')),
    ('B', 'BinaryTree#levelOrder', lambda t, s: has(t, 'binary-tree') and has(t, 'breadth-first-search') and re.search(r'level|row|width|zigzag|right side|left|complete|cousin|even odd', s)),
    ('B', 'BinaryTree#maxDepth', lambda t, s: has(t, 'binary-tree')),
    ('B', 'BinaryTree#levelOrder', lambda t, s: has(t, 'tree') and has(t, 'breadth-first-search') and 'level' in s),
    ('B', 'TreeGraphs#treeDiameter', lambda t, s: has(t, 'tree') and re.search(r'diameter|distance|longest path|farthest|height', s)),
    ('B', 'TreeGraphs#minHeightTrees', lambda t, s: has(t, 'tree') and 'root' in s and has(t, 'graph')),
    ('B', 'Traversal#dfs', lambda t, s: has(t, 'tree') and has(t, 'graph', 'depth-first-search', 'breadth-first-search')),
    # ---- linked lists
    ('B', 'LinkedLists#detectCycle', lambda t, s: has(t, 'linked-list') and 'cycle' in s),
    ('B', 'LinkedLists#reverse', lambda t, s: has(t, 'linked-list')),
    # ---- grids & graphs, generic traversal
    ('B', 'ImplicitGraphs#wordLadder', lambda t, s: has(t, 'breadth-first-search') and ('ladder' in s or 'lock' in s or 'puzzle' in s or 'mutation' in s)),
    ('B', 'ImplicitGraphs#numIslands', lambda t, s: has(t, 'matrix') and has(t, 'breadth-first-search', 'depth-first-search')),
    ('B', 'Traversal#multiSourceBFS', lambda t, s: has(t, 'breadth-first-search') and ('nearest' in s or 'distance' in s or 'rotting' in s)),
    ('B', 'Traversal#bfs', lambda t, s: has(t, 'graph') or (has(t, 'breadth-first-search', 'depth-first-search') and not has(t, 'tree', 'string', 'stack'))),
    # ---- backtracking
    ('B', 'Backtracking#solveNQueens', lambda t, s: 'queens' in s),
    ('B', 'Backtracking#permute', lambda t, s: has(t, 'backtracking') and 'permutation' in s),
    ('B', 'Backtracking#combinationSum', lambda t, s: has(t, 'backtracking') and ('combination' in s or 'sum' in s)),
    ('B', 'Backtracking#subsets', lambda t, s: has(t, 'backtracking')),
    # ---- math & bits (specific)
    ('B', 'BitsMath#countPrimes', lambda t, s: has(t, 'sieve-theory') or 'prime' in s and has(t, 'math', 'number-theory')),
    ('B', 'BitsMath#factorize', lambda t, s: has(t, 'prime-factorization')),
    ('B', 'BitsMath#gcd', lambda t, s: has(t, 'greatest-common-divisor', 'euclidean-algorithm') or re.search(r'\b(gcd|lcm)\b', s)),
    ('B', 'BitsMath#modInverse', lambda t, s: has(t, 'fermats-little-theorem')),
    ('B', 'BitsMath#Combinatorics', lambda t, s: has(t, 'combinatorics')),
    ('B', 'BitsMath#powMod', lambda t, s: re.search(r'\bpow\b|exponentiation|super pow', s) and has(t, 'math')),
    ('B', 'BitsMathVariants#Reservoir', lambda t, s: has(t, 'reservoir-sampling', 'randomized')),
    ('B', 'BitsMath#subsetsMask', lambda t, s: has(t, 'bitmask')),
    ('B', 'BitsMath#singleNumber', lambda t, s: has(t, 'bit-manipulation') and re.search(r'xor|bit|single|hamming|power of|binary|complement', s)),
    ('B', 'ArraysHashing#subarraySum', lambda t, s: has(t, 'prefix-sum') and not has(t, 'matrix')),
    ('B', 'RangeStructures#NumMatrix', lambda t, s: has(t, 'prefix-sum') and has(t, 'matrix')),
    ('B', 'HeapsTopK#findKthLargest', lambda t, s: has(t, 'heap-priority-queue')),
    # ---- broad: tier C (technique family is covered; specific trick may not be)
    ('C', 'DPVariants#predictTheWinner', lambda t, s: has(t, 'game-theory')),
    ('C', 'SlidingWindowPointers#twoSumSorted', lambda t, s: has(t, 'two-pointers')),
    ('C', 'BitsMath#singleNumber', lambda t, s: has(t, 'bit-manipulation')),
    ('C', 'DPSequences#wordBreak', lambda t, s: has(t, 'dynamic-programming', 'memoization') and has(t, 'string')),
    ('C', 'DPLinear#rob', lambda t, s: has(t, 'dynamic-programming', 'memoization')),
    ('C', 'DesignStructures#LRUCache', lambda t, s: has(t, 'design', 'data-stream', 'iterator')),
    ('C', 'MonotonicStructures#isValid', lambda t, s: has(t, 'stack')),
    ('C', 'Traversal#bfs', lambda t, s: has(t, 'queue')),
    ('C', 'Greedy#canJump', lambda t, s: has(t, 'greedy')),
    ('C', 'Intervals#MyCalendar', lambda t, s: has(t, 'ordered-set')),
    ('C', 'ArraysVariants#reversePairs', lambda t, s: has(t, 'merge-sort', 'divide-and-conquer')),
    ('C', 'ArraysHashing#twoSum', lambda t, s: has(t, 'hash-table', 'counting')),
    ('C', 'ArraysHashing#rotateMatrix', lambda t, s: has(t, 'matrix') and not has(t, 'simulation')),
    ('C', 'BitsMath#gcd', lambda t, s: has(t, 'number-theory')),
    ('C', 'ArraysVariants#countingSort', lambda t, s: has(t, 'sorting', 'counting-sort', 'bucket-sort', 'radix-sort')),
]
for _, k, _ in RULES:
    assert k in T, f'rule names unknown template {k}'

# verified: template refs from code; practice map: topic only
code_refs = collections.defaultdict(list)
for i, key, how in inv['refs']:
    if key not in code_refs[i]:
        code_refs[i].append(key)
practice = collections.defaultdict(list)
for p in inv['practice']:
    practice[p['id']].append(p)

rows, cats = [], collections.Counter()
for q in lc:
    if q['categoryTitle'] != 'Algorithms':
        cats[q['categoryTitle']] += 1
        continue
    pid = int(q['questionFrontendId']) if q['questionFrontendId'].isdigit() else None
    tags = {x['slug'] for x in q['topicTags']}
    title = q['title'].lower()
    hits = []
    for tier, key, pred in RULES:
        try:
            ok = pred(tags, title)
        except Exception:
            ok = False
        if ok and key not in [h[1] for h in hits]:
            hits.append((tier, key))
    tier, templates, topic, how = 'U', [], None, ''
    if pid in code_refs:
        tier, templates, how = 'A', list(code_refs[pid]), 'named in template code'
    elif pid in practice:
        tier, how = 'A', 'listed in practice map'
        ptopics = {p['topic'] for p in practice[pid]}
        inside = [k for _, k in hits if TOPIC_OF[k] in ptopics]
        templates = inside[:1]
        topic = sorted(ptopics)[0]
    elif hits:
        tier = hits[0][0]
        how = 'tag rule' if tier == 'B' else 'broad tag'
        templates = [hits[0][1]]
    others = [k for _, k in hits if k not in templates][:3]
    if templates:
        topic = TOPIC_OF[templates[0]]
    rows.append({'id': pid, 'title': q['title'], 'slug': q['titleSlug'], 'difficulty': q['difficulty'],
                 'paid': q['isPaidOnly'], 'ac': round(q['acRate'], 1), 'tags': sorted(tags),
                 'tier': tier, 'how': how, 'topic': topic, 'templates': templates, 'also': others})

# one problem per line so diffs stay readable
with open(out, 'w') as fh:
    fh.write('{"topics": %s,\n "excluded": %s,\n "problems": [\n' % (json.dumps(inv['topics'], sort_keys=True), json.dumps(dict(cats), sort_keys=True)))
    fh.write(',\n'.join(json.dumps(r, separators=(',', ':')) for r in rows))
    fh.write('\n]}\n')

c = collections.Counter(r['tier'] for r in rows)
print('excluded non-algorithm:', dict(cats), sum(cats.values()))
print('algorithm problems:', len(rows), dict(sorted(c.items())))
free = [r for r in rows if not r['paid']]
print('free only:', len(free), dict(sorted(collections.Counter(r['tier'] for r in free).items())))
print('by difficulty x tier:')
for d in ('Easy', 'Medium', 'Hard'):
    print(' ', d, dict(sorted(collections.Counter(r['tier'] for r in rows if r['difficulty'] == d).items())))
print('A with no template resolved:', sum(1 for r in rows if r['tier'] == 'A' and not r['templates']))
verified_ids = set(code_refs) | set(practice)
print('verified ids not found in LeetCode list:', sorted(verified_ids - {r['id'] for r in rows}))
