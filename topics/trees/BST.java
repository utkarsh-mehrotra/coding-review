import java.util.*;

public class BST {
    // Uses BinaryTree.TreeNode (same package, default package).

    // ---------- Validate (LC 98): carry open bounds down ----------
    static boolean isValidBST(BinaryTree.TreeNode n) { return valid(n, null, null); }

    private static boolean valid(BinaryTree.TreeNode n, Integer lo, Integer hi) {
        if (n == null) return true;
        if ((lo != null && n.val <= lo) || (hi != null && n.val >= hi)) return false;
        return valid(n.left, lo, n.val) && valid(n.right, n.val, hi);
    }

    // ---------- Search / insert / delete ----------
    static BinaryTree.TreeNode search(BinaryTree.TreeNode n, int key) {
        while (n != null && n.val != key) n = key < n.val ? n.left : n.right;
        return n;
    }

    static BinaryTree.TreeNode insert(BinaryTree.TreeNode n, int key) {
        if (n == null) return new BinaryTree.TreeNode(key);
        if (key < n.val) n.left = insert(n.left, key); else n.right = insert(n.right, key);
        return n;
    }

    // Delete (LC 450): 0 or 1 child -> splice; 2 children -> replace with inorder successor
    static BinaryTree.TreeNode delete(BinaryTree.TreeNode n, int key) {
        if (n == null) return null;
        if (key < n.val) n.left = delete(n.left, key);
        else if (key > n.val) n.right = delete(n.right, key);
        else {
            if (n.left == null) return n.right;
            if (n.right == null) return n.left;
            BinaryTree.TreeNode s = n.right;
            while (s.left != null) s = s.left;
            n.val = s.val;
            n.right = delete(n.right, s.val);
        }
        return n;
    }

    // ---------- Kth smallest (LC 230): iterative inorder, stop early ----------
    static int kthSmallest(BinaryTree.TreeNode root, int k) {
        Deque<BinaryTree.TreeNode> st = new ArrayDeque<>();
        BinaryTree.TreeNode cur = root;
        while (cur != null || !st.isEmpty()) {
            while (cur != null) { st.push(cur); cur = cur.left; }
            cur = st.pop();
            if (--k == 0) return cur.val;
            cur = cur.right;
        }
        return -1;
    }

    // ---------- LCA in BST (LC 235): walk down, no recursion needed ----------
    static BinaryTree.TreeNode lcaBST(BinaryTree.TreeNode root, int p, int q) {
        while (root != null) {
            if (p < root.val && q < root.val) root = root.left;
            else if (p > root.val && q > root.val) root = root.right;
            else return root;
        }
        return null;
    }

    // ---------- Sorted array -> height-balanced BST (LC 108) ----------
    static BinaryTree.TreeNode sortedArrayToBST(int[] a) { return arr(a, 0, a.length - 1); }

    private static BinaryTree.TreeNode arr(int[] a, int lo, int hi) {
        if (lo > hi) return null;
        int mid = (lo + hi) >>> 1;
        BinaryTree.TreeNode n = new BinaryTree.TreeNode(a[mid]);
        n.left = arr(a, lo, mid - 1);
        n.right = arr(a, mid + 1, hi);
        return n;
    }

    // ---------- Recover BST (LC 99): two inversions in the inorder sequence ----------
    static void recoverTree(BinaryTree.TreeNode root) {
        BinaryTree.TreeNode first = null, second = null, prev = null, cur = root;
        Deque<BinaryTree.TreeNode> st = new ArrayDeque<>();
        while (cur != null || !st.isEmpty()) {
            while (cur != null) { st.push(cur); cur = cur.left; }
            cur = st.pop();
            if (prev != null && prev.val > cur.val) {
                if (first == null) first = prev;
                second = cur;
            }
            prev = cur;
            cur = cur.right;
        }
        int t = first.val; first.val = second.val; second.val = t;
    }

    // ---------- BST Iterator (LC 173): stack of left spine, O(h) space, amortized O(1) next ----------
    static class BSTIterator {
        private final Deque<BinaryTree.TreeNode> st = new ArrayDeque<>();
        BSTIterator(BinaryTree.TreeNode root) { push(root); }
        private void push(BinaryTree.TreeNode n) { while (n != null) { st.push(n); n = n.left; } }
        int next() { BinaryTree.TreeNode n = st.pop(); push(n.right); return n.val; }
        boolean hasNext() { return !st.isEmpty(); }
    }

    // ---------- Largest BST subtree (LC 333): post-order returns {isBST, size, min, max} ----------
    static int largestBSTSubtree(BinaryTree.TreeNode root) { return lb(root)[3]; }

    // returns {min, max, isBST(1/0), bestSize / size}
    private static int[] lb(BinaryTree.TreeNode n) {
        if (n == null) return new int[]{Integer.MAX_VALUE, Integer.MIN_VALUE, 1, 0};
        int[] l = lb(n.left), r = lb(n.right);
        if (l[2] == 1 && r[2] == 1 && l[1] < n.val && n.val < r[0]) {
            int size = l[3] + r[3] + 1;
            return new int[]{Math.min(l[0], n.val), Math.max(r[1], n.val), 1, size};
        }
        return new int[]{0, 0, 0, Math.max(l[3], r[3])};
    }

    // ---------- Floor / ceiling ----------
    static Integer floor(BinaryTree.TreeNode n, int x) {
        Integer best = null;
        while (n != null) {
            if (n.val == x) return x;
            if (n.val < x) { best = n.val; n = n.right; } else n = n.left;
        }
        return best;
    }

    // ---------- Unique BSTs count (LC 96): Catalan ----------
    static int numTrees(int n) {
        int[] dp = new int[n + 1];
        dp[0] = dp[1] = 1;
        for (int i = 2; i <= n; i++)
            for (int j = 0; j < i; j++) dp[i] += dp[j] * dp[i - 1 - j];
        return dp[n];
    }

    // TreeMap cheat sheet (when an ordered structure is needed in an interview):
    //   floorKey / ceilingKey / lowerKey / higherKey / firstKey / lastKey / pollFirstEntry / headMap / tailMap -- all O(log n)
}
