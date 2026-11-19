import java.util.*;

public class TreesVariants {
    // Static counters below are reset at the start of each public entry method, so a failed or repeated call cannot leak state.
    // Uses BinaryTree.TreeNode; build test trees with BinaryTree.fromLevel(...).
    // Two shapes cover most of these: (1) post-order returns one value to the parent while a global tracks the answer,
    // (2) pre-order carries state down (a path sum, a bound, a depth) as parameters.

    // ---------- Boundary of Binary Tree (LC 545): left edge + leaves + right edge reversed ----------
    static List<Integer> boundaryOfBinaryTree(BinaryTree.TreeNode root) {
        List<Integer> res = new ArrayList<>();
        if (root == null) return res;
        if (!isLeaf(root)) res.add(root.val);
        BinaryTree.TreeNode n = root.left;
        while (n != null) { if (!isLeaf(n)) res.add(n.val); n = n.left != null ? n.left : n.right; }
        leaves(root, res);
        Deque<Integer> right = new ArrayDeque<>();
        n = root.right;
        while (n != null) { if (!isLeaf(n)) right.push(n.val); n = n.right != null ? n.right : n.left; }
        res.addAll(right);
        return res;
    }

    private static boolean isLeaf(BinaryTree.TreeNode n) { return n.left == null && n.right == null; }

    private static void leaves(BinaryTree.TreeNode n, List<Integer> out) {
        if (n == null) return;
        if (isLeaf(n)) { out.add(n.val); return; }
        leaves(n.left, out);
        leaves(n.right, out);
    }

    // ---------- Distribute Coins in Binary Tree (LC 979): each edge carries |excess of its subtree| moves ----------
    private static int moves;

    static int distributeCoins(BinaryTree.TreeNode root) { moves = 0; excess(root); return moves; }

    private static int excess(BinaryTree.TreeNode n) {
        if (n == null) return 0;
        int e = n.val - 1 + excess(n.left) + excess(n.right);
        moves += Math.abs(e);
        return e;
    }

    // ---------- Longest Univalue Path (LC 687): post-order returns the longest downward same-value arm ----------
    private static int uniBest;

    static int longestUnivaluePath(BinaryTree.TreeNode root) { uniBest = 0; arm(root); return uniBest; }

    private static int arm(BinaryTree.TreeNode n) {
        if (n == null) return 0;
        int l = arm(n.left), r = arm(n.right);
        int al = n.left != null && n.left.val == n.val ? l + 1 : 0;
        int ar = n.right != null && n.right.val == n.val ? r + 1 : 0;
        uniBest = Math.max(uniBest, al + ar);
        return Math.max(al, ar);
    }

    // ---------- Sum Root to Leaf Numbers (LC 129): pass the number down ----------
    static int sumNumbers(BinaryTree.TreeNode root) { return sumDown(root, 0); }

    private static int sumDown(BinaryTree.TreeNode n, int acc) {
        if (n == null) return 0;
        acc = acc * 10 + n.val;
        return isLeaf(n) ? acc : sumDown(n.left, acc) + sumDown(n.right, acc);
    }

    // ---------- Maximum Width of Binary Tree (LC 662): heap-style indices; subtract the level's first index to avoid overflow ----------
    static int widthOfBinaryTree(BinaryTree.TreeNode root) {
        if (root == null) return 0;
        Deque<BinaryTree.TreeNode> q = new ArrayDeque<>();
        Deque<Long> idx = new ArrayDeque<>();
        q.add(root);
        idx.add(0L);
        long best = 0;
        while (!q.isEmpty()) {
            long first = idx.peekFirst(), last = first;
            for (int size = q.size(); size > 0; size--) {
                BinaryTree.TreeNode n = q.poll();
                long i = idx.poll() - first;
                last = i;
                if (n.left != null) { q.add(n.left); idx.add(2 * i); }
                if (n.right != null) { q.add(n.right); idx.add(2 * i + 1); }
            }
            best = Math.max(best, last + 1);
        }
        return (int) best;
    }

    // ---------- Find Duplicate Subtrees (LC 652): canonical serialization -> id; report an id the second time it is seen ----------
    static List<BinaryTree.TreeNode> findDuplicateSubtrees(BinaryTree.TreeNode root) {
        List<BinaryTree.TreeNode> res = new ArrayList<>();
        dupId(root, new HashMap<>(), new HashMap<>(), res);
        return res;
    }

    private static int dupId(BinaryTree.TreeNode n, Map<String, Integer> ids, Map<Integer, Integer> counts, List<BinaryTree.TreeNode> res) {
        if (n == null) return 0;
        String key = dupId(n.left, ids, counts, res) + "," + n.val + "," + dupId(n.right, ids, counts, res);
        int id = ids.computeIfAbsent(key, k -> ids.size() + 1);
        if (counts.merge(id, 1, Integer::sum) == 2) res.add(n);
        return id;
    }

    // ---------- All Possible Full Binary Trees (LC 894): memoized by node count ----------
    static List<BinaryTree.TreeNode> allPossibleFBT(int n) { return fbt(n, new HashMap<>()); }

    private static List<BinaryTree.TreeNode> fbt(int n, Map<Integer, List<BinaryTree.TreeNode>> memo) {
        if (n % 2 == 0) return List.of();
        if (n == 1) return List.of(new BinaryTree.TreeNode(0));
        if (memo.containsKey(n)) return memo.get(n);
        List<BinaryTree.TreeNode> res = new ArrayList<>();
        for (int left = 1; left < n; left += 2)
            for (BinaryTree.TreeNode l : fbt(left, memo))
                for (BinaryTree.TreeNode r : fbt(n - 1 - left, memo)) { // subtrees may be shared: trees are only read afterwards
                    BinaryTree.TreeNode root = new BinaryTree.TreeNode(0);
                    root.left = l;
                    root.right = r;
                    res.add(root);
                }
        memo.put(n, res);
        return res;
    }

    // ---------- Unique Binary Search Trees II (LC 95): choose each root, combine every left x right ----------
    static List<BinaryTree.TreeNode> generateTrees(int n) { return n == 0 ? List.of() : gen(1, n); }

    private static List<BinaryTree.TreeNode> gen(int lo, int hi) {
        List<BinaryTree.TreeNode> res = new ArrayList<>();
        if (lo > hi) { res.add(null); return res; }
        for (int root = lo; root <= hi; root++)
            for (BinaryTree.TreeNode l : gen(lo, root - 1))
                for (BinaryTree.TreeNode r : gen(root + 1, hi)) {
                    BinaryTree.TreeNode t = new BinaryTree.TreeNode(root);
                    t.left = l;
                    t.right = r;
                    res.add(t);
                }
        return res;
    }

    // ---------- Construct from Inorder and Postorder (LC 106): postorder's last element is the root; build RIGHT first ----------
    private static int postIdx;

    static BinaryTree.TreeNode buildTreeInPost(int[] inorder, int[] postorder) {
        Map<Integer, Integer> pos = new HashMap<>();
        for (int i = 0; i < inorder.length; i++) pos.put(inorder[i], i);
        postIdx = postorder.length - 1;
        return buildPost(postorder, pos, 0, inorder.length - 1);
    }

    private static BinaryTree.TreeNode buildPost(int[] post, Map<Integer, Integer> pos, int lo, int hi) {
        if (lo > hi) return null;
        BinaryTree.TreeNode root = new BinaryTree.TreeNode(post[postIdx--]);
        int mid = pos.get(root.val);
        root.right = buildPost(post, pos, mid + 1, hi); // right subtree comes just before the root in postorder
        root.left = buildPost(post, pos, lo, mid - 1);
        return root;
    }

    // ---------- Construct BST from Preorder (LC 1008): pass an upper bound, consume while values fit ----------
    private static int preIdx;

    static BinaryTree.TreeNode bstFromPreorder(int[] pre) { preIdx = 0; return bstBound(pre, Integer.MAX_VALUE); }

    private static BinaryTree.TreeNode bstBound(int[] pre, int bound) {
        if (preIdx == pre.length || pre[preIdx] > bound) return null;
        BinaryTree.TreeNode root = new BinaryTree.TreeNode(pre[preIdx++]);
        root.left = bstBound(pre, root.val);
        root.right = bstBound(pre, bound);
        return root;
    }

    // ---------- Maximum Product of Splitted Binary Tree (LC 1339): total sum, then every subtree sum s gives s * (total - s) ----------
    static int maxProduct(BinaryTree.TreeNode root) {
        List<Long> sums = new ArrayList<>();
        long total = subtreeSum(root, sums);
        long best = 0;
        for (long s : sums) best = Math.max(best, s * (total - s));
        return (int) (best % 1_000_000_007L);
    }

    private static long subtreeSum(BinaryTree.TreeNode n, List<Long> sums) {
        if (n == null) return 0;
        long s = n.val + subtreeSum(n.left, sums) + subtreeSum(n.right, sums);
        sums.add(s);
        return s;
    }

    // ---------- Amount of Time for Binary Tree to Be Infected (LC 2385): parent map + BFS, or one DFS returning depth / distance ----------
    private static int infected;

    static int amountOfTime(BinaryTree.TreeNode root, int start) { infected = 0; infect(root, start); return infected; }

    // returns the subtree height in NODES (null = 0, leaf = 1) if `start` is not inside; otherwise -(edges from this node to start + 1):
    // start returns -1, its parent -2, ... so a caller recovers the edge distance to start as |value|.
    private static int infect(BinaryTree.TreeNode n, int start) {
        if (n == null) return 0;
        int l = infect(n.left, start), r = infect(n.right, start);
        if (n.val == start) { infected = Math.max(l, r); return -1; }
        if (l >= 0 && r >= 0) return Math.max(l, r) + 1;
        int dist = Math.abs(l < 0 ? l : r);
        infected = Math.max(infected, dist + Math.max(l, r)); // other side's depth counts if non-negative
        return -(dist + 1);
    }

    // ---------- LCA of Deepest Leaves (LC 1123): return (depth, lca) pairs bottom-up ----------
    static BinaryTree.TreeNode lcaDeepestLeaves(BinaryTree.TreeNode root) { return deepest(root).node; }

    private static class Deep { int depth; BinaryTree.TreeNode node; Deep(int d, BinaryTree.TreeNode n) { depth = d; node = n; } }

    private static Deep deepest(BinaryTree.TreeNode n) {
        if (n == null) return new Deep(0, null);
        Deep l = deepest(n.left), r = deepest(n.right);
        if (l.depth > r.depth) return new Deep(l.depth + 1, l.node);
        if (r.depth > l.depth) return new Deep(r.depth + 1, r.node);
        return new Deep(l.depth + 1, n);
    }

    // ---------- Delete Nodes And Return Forest (LC 1110): post-order, a deleted node promotes its children to roots ----------
    static List<BinaryTree.TreeNode> delNodes(BinaryTree.TreeNode root, int[] toDelete) {
        Set<Integer> del = new HashSet<>();
        for (int d : toDelete) del.add(d);
        List<BinaryTree.TreeNode> forest = new ArrayList<>();
        if (prune(root, del, forest) != null) forest.add(root);
        return forest;
    }

    private static BinaryTree.TreeNode prune(BinaryTree.TreeNode n, Set<Integer> del, List<BinaryTree.TreeNode> forest) {
        if (n == null) return null;
        n.left = prune(n.left, del, forest);
        n.right = prune(n.right, del, forest);
        if (!del.contains(n.val)) return n;
        if (n.left != null) forest.add(n.left);
        if (n.right != null) forest.add(n.right);
        return null;
    }

    // ---------- Count Complete Tree Nodes (LC 222): compare leftmost and rightmost depth; a perfect subtree is 2^h - 1 ----------
    static int countNodes(BinaryTree.TreeNode root) {
        if (root == null) return 0;
        int l = 0, r = 0;
        for (BinaryTree.TreeNode n = root; n != null; n = n.left) l++;
        for (BinaryTree.TreeNode n = root; n != null; n = n.right) r++;
        if (l == r) return (1 << l) - 1;
        return 1 + countNodes(root.left) + countNodes(root.right);
    }

    // ---------- Cousins in Binary Tree (LC 993): same depth, different parents ----------
    static boolean isCousins(BinaryTree.TreeNode root, int x, int y) {
        Deque<BinaryTree.TreeNode> q = new ArrayDeque<>();
        q.add(root);
        while (!q.isEmpty()) {
            boolean fx = false, fy = false;
            for (int size = q.size(); size > 0; size--) {
                BinaryTree.TreeNode n = q.poll();
                if (n.left != null && n.right != null) {
                    int a = n.left.val, b = n.right.val;
                    if ((a == x && b == y) || (a == y && b == x)) return false; // siblings
                }
                for (BinaryTree.TreeNode c : new BinaryTree.TreeNode[]{n.left, n.right}) {
                    if (c == null) continue;
                    if (c.val == x) fx = true;
                    if (c.val == y) fy = true;
                    q.add(c);
                }
            }
            if (fx && fy) return true;
            if (fx || fy) return false;
        }
        return false;
    }

    // ---------- Binary Tree Paths (LC 257) and Path Sum II (LC 113): backtracking on a shared path list ----------
    static List<String> binaryTreePaths(BinaryTree.TreeNode root) {
        List<String> res = new ArrayList<>();
        paths(root, new StringBuilder(), res);
        return res;
    }

    private static void paths(BinaryTree.TreeNode n, StringBuilder cur, List<String> res) {
        if (n == null) return;
        int len = cur.length();
        cur.append(len == 0 ? "" : "->").append(n.val);
        if (isLeaf(n)) res.add(cur.toString());
        else { paths(n.left, cur, res); paths(n.right, cur, res); }
        cur.setLength(len);
    }

    static List<List<Integer>> pathSumII(BinaryTree.TreeNode root, int target) {
        List<List<Integer>> res = new ArrayList<>();
        pathSum(root, target, new ArrayList<>(), res);
        return res;
    }

    private static void pathSum(BinaryTree.TreeNode n, int remain, List<Integer> path, List<List<Integer>> res) {
        if (n == null) return;
        path.add(n.val);
        if (isLeaf(n) && remain == n.val) res.add(new ArrayList<>(path));
        pathSum(n.left, remain - n.val, path, res);
        pathSum(n.right, remain - n.val, path, res);
        path.remove(path.size() - 1);
    }

    // ---------- BST family: Convert BST to Greater Tree (LC 538) = reverse inorder with a running sum ----------
    private static int running;

    static BinaryTree.TreeNode convertBST(BinaryTree.TreeNode root) { running = 0; greater(root); return root; }

    private static void greater(BinaryTree.TreeNode n) {
        if (n == null) return;
        greater(n.right);
        running += n.val;
        n.val = running;
        greater(n.left);
    }

    // Trim a BST (LC 669), Sorted Array to BST (LC 108), Minimum Difference in BST (LC 783), Range Sum of BST (LC 938)
    static BinaryTree.TreeNode trimBST(BinaryTree.TreeNode n, int lo, int hi) {
        if (n == null) return null;
        if (n.val < lo) return trimBST(n.right, lo, hi);
        if (n.val > hi) return trimBST(n.left, lo, hi);
        n.left = trimBST(n.left, lo, hi);
        n.right = trimBST(n.right, lo, hi);
        return n;
    }

    // Same as BST.sortedArrayToBST; kept in both so each file compiles alone.
    static BinaryTree.TreeNode sortedArrayToBST(int[] a) { return sortedBuild(a, 0, a.length - 1); }

    private static BinaryTree.TreeNode sortedBuild(int[] a, int lo, int hi) {
        if (lo > hi) return null;
        int mid = (lo + hi) >>> 1;
        BinaryTree.TreeNode n = new BinaryTree.TreeNode(a[mid]);
        n.left = sortedBuild(a, lo, mid - 1);
        n.right = sortedBuild(a, mid + 1, hi);
        return n;
    }

    static int minDiffInBST(BinaryTree.TreeNode root) {
        int best = Integer.MAX_VALUE;
        Deque<BinaryTree.TreeNode> st = new ArrayDeque<>();
        BinaryTree.TreeNode cur = root, prev = null;
        while (cur != null || !st.isEmpty()) {
            while (cur != null) { st.push(cur); cur = cur.left; }
            cur = st.pop();
            if (prev != null) best = Math.min(best, cur.val - prev.val);
            prev = cur;
            cur = cur.right;
        }
        return best;
    }

    static int rangeSumBST(BinaryTree.TreeNode n, int lo, int hi) {
        if (n == null) return 0;
        if (n.val < lo) return rangeSumBST(n.right, lo, hi);
        if (n.val > hi) return rangeSumBST(n.left, lo, hi);
        return n.val + rangeSumBST(n.left, lo, hi) + rangeSumBST(n.right, lo, hi);
    }

    // ---------- Inorder Successor in BST (LC 285): last ancestor where we went left ----------
    static BinaryTree.TreeNode inorderSuccessor(BinaryTree.TreeNode root, int p) {
        BinaryTree.TreeNode succ = null;
        while (root != null) {
            if (p < root.val) { succ = root; root = root.left; } else root = root.right;
        }
        return succ;
    }
}
