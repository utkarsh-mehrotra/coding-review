import java.util.*;

public class BinaryTree {

    static class TreeNode {
        int val;
        TreeNode left, right;
        TreeNode(int v) { val = v; }
    }

    // ---------- Traversals ----------
    // RECOGNIZE: any "visit every node" ask. Recursive is fine; know the iterative forms for follow-ups.
    static List<Integer> inorder(TreeNode root) {
        List<Integer> out = new ArrayList<>();
        Deque<TreeNode> st = new ArrayDeque<>();
        TreeNode cur = root;
        while (cur != null || !st.isEmpty()) {
            while (cur != null) { st.push(cur); cur = cur.left; }
            cur = st.pop();
            out.add(cur.val);
            cur = cur.right;
        }
        return out;
    }

    static List<Integer> preorder(TreeNode root) {
        List<Integer> out = new ArrayList<>();
        Deque<TreeNode> st = new ArrayDeque<>();
        if (root != null) st.push(root);
        while (!st.isEmpty()) {
            TreeNode n = st.pop();
            out.add(n.val);
            if (n.right != null) st.push(n.right);
            if (n.left != null) st.push(n.left);
        }
        return out;
    }

    // postorder = reverse of (root, right, left)
    static List<Integer> postorder(TreeNode root) {
        LinkedList<Integer> out = new LinkedList<>();
        Deque<TreeNode> st = new ArrayDeque<>();
        if (root != null) st.push(root);
        while (!st.isEmpty()) {
            TreeNode n = st.pop();
            out.addFirst(n.val);
            if (n.left != null) st.push(n.left);
            if (n.right != null) st.push(n.right);
        }
        return out;
    }

    // Morris inorder: O(1) extra space by threading predecessor.right -> current
    static List<Integer> inorderMorris(TreeNode root) {
        List<Integer> out = new ArrayList<>();
        TreeNode cur = root;
        while (cur != null) {
            if (cur.left == null) { out.add(cur.val); cur = cur.right; continue; }
            TreeNode pred = cur.left;
            while (pred.right != null && pred.right != cur) pred = pred.right;
            if (pred.right == null) { pred.right = cur; cur = cur.left; }
            else { pred.right = null; out.add(cur.val); cur = cur.right; }
        }
        return out;
    }

    // Level order (LC 102): one queue, snapshot the level size
    static List<List<Integer>> levelOrder(TreeNode root) {
        List<List<Integer>> res = new ArrayList<>();
        if (root == null) return res;
        Deque<TreeNode> q = new ArrayDeque<>();
        q.add(root);
        while (!q.isEmpty()) {
            int sz = q.size();
            List<Integer> level = new ArrayList<>();
            while (sz-- > 0) {
                TreeNode n = q.poll();
                level.add(n.val);
                if (n.left != null) q.add(n.left);
                if (n.right != null) q.add(n.right);
            }
            res.add(level);
        }
        return res;
    }

    // Zigzag (LC 103)
    static List<List<Integer>> zigzag(TreeNode root) {
        List<List<Integer>> res = levelOrder(root);
        for (int i = 1; i < res.size(); i += 2) Collections.reverse(res.get(i));
        return res;
    }

    // Right side view (LC 199): last node of each level
    static List<Integer> rightSideView(TreeNode root) {
        List<Integer> out = new ArrayList<>();
        for (List<Integer> lv : levelOrder(root)) out.add(lv.get(lv.size() - 1));
        return out;
    }

    // ---------- Height / balance / diameter: ONE post-order returning a value, updating a global ----------
    static int maxDepth(TreeNode n) { return n == null ? 0 : 1 + Math.max(maxDepth(n.left), maxDepth(n.right)); }

    private static int diam;

    // LC 543
    // DEPTH: recursion n deep; at n >= 5e3 run inside a big-stack thread (see SCC_Bridges_2SAT.runWithBigStack).
    static int diameter(TreeNode root) {
        diam = 0;
        depthForDiameter(root);
        return diam; // in edges
    }

    private static int depthForDiameter(TreeNode n) {
        if (n == null) return 0;
        int l = depthForDiameter(n.left), r = depthForDiameter(n.right);
        diam = Math.max(diam, l + r);
        return 1 + Math.max(l, r);
    }

    // Balanced (LC 110): -1 means "unbalanced" bubbled up
    static boolean isBalanced(TreeNode root) { return height(root) >= 0; }

    private static int height(TreeNode n) {
        if (n == null) return 0;
        int l = height(n.left);
        if (l < 0) return -1;
        int r = height(n.right);
        if (r < 0 || Math.abs(l - r) > 1) return -1;
        return 1 + Math.max(l, r);
    }

    // Max path sum (LC 124): return the best DOWNWARD gain, update answer with the bent path through node
    // DEPTH: recursion n deep; at n >= 5e3 run inside a big-stack thread (see SCC_Bridges_2SAT.runWithBigStack).
    private static int bestPath;

    static int maxPathSum(TreeNode root) {
        bestPath = Integer.MIN_VALUE;
        gain(root);
        return bestPath;
    }

    private static int gain(TreeNode n) {
        if (n == null) return 0;
        int l = Math.max(0, gain(n.left)), r = Math.max(0, gain(n.right));
        bestPath = Math.max(bestPath, n.val + l + r);
        return n.val + Math.max(l, r);
    }

    // ---------- Structure checks ----------
    static boolean isSameTree(TreeNode a, TreeNode b) {
        if (a == null || b == null) return a == b;
        return a.val == b.val && isSameTree(a.left, b.left) && isSameTree(a.right, b.right);
    }

    static boolean isSymmetric(TreeNode root) { return root == null || mirror(root.left, root.right); }

    private static boolean mirror(TreeNode a, TreeNode b) {
        if (a == null || b == null) return a == b;
        return a.val == b.val && mirror(a.left, b.right) && mirror(a.right, b.left);
    }

    static TreeNode invertTree(TreeNode n) {
        if (n == null) return null;
        TreeNode t = n.left;
        n.left = invertTree(n.right);
        n.right = invertTree(t);
        return n;
    }

    // Subtree of another tree (LC 572)
    static boolean isSubtree(TreeNode root, TreeNode sub) {
        if (root == null) return sub == null;
        return isSameTree(root, sub) || isSubtree(root.left, sub) || isSubtree(root.right, sub);
    }

    // ---------- Path sums ----------
    static boolean hasPathSum(TreeNode n, int target) {
        if (n == null) return false;
        if (n.left == null && n.right == null) return n.val == target;
        return hasPathSum(n.left, target - n.val) || hasPathSum(n.right, target - n.val);
    }

    // Path Sum III (LC 437): prefix-sum map along the root-to-node path
    static int pathSumIII(TreeNode root, int target) {
        Map<Long, Integer> seen = new HashMap<>();
        seen.put(0L, 1);
        return pathSumDfs(root, 0, target, seen);
    }

    private static int pathSumDfs(TreeNode n, long sum, int target, Map<Long, Integer> seen) {
        if (n == null) return 0;
        sum += n.val;
        int count = seen.getOrDefault(sum - target, 0);
        seen.merge(sum, 1, Integer::sum);
        count += pathSumDfs(n.left, sum, target, seen) + pathSumDfs(n.right, sum, target, seen);
        seen.merge(sum, -1, Integer::sum); // backtrack
        return count;
    }

    // ---------- LCA (binary tree) (LC 236) ----------
    // Both p and q must exist in the tree (LC 236 guarantees it); otherwise it can return the one that is present.
    // DEPTH: recursion n deep; at n >= 5e3 run inside a big-stack thread (see SCC_Bridges_2SAT.runWithBigStack).
    static TreeNode lowestCommonAncestor(TreeNode root, TreeNode p, TreeNode q) {
        if (root == null || root == p || root == q) return root;
        TreeNode l = lowestCommonAncestor(root.left, p, q), r = lowestCommonAncestor(root.right, p, q);
        return l != null && r != null ? root : l != null ? l : r;
    }

    // ---------- Build from traversals ----------
    // Preorder + inorder (LC 105)
    static TreeNode buildTree(int[] pre, int[] in) {
        Map<Integer, Integer> pos = new HashMap<>();
        for (int i = 0; i < in.length; i++) pos.put(in[i], i);
        return build(pre, new int[]{0}, 0, in.length - 1, pos);
    }

    private static TreeNode build(int[] pre, int[] pi, int lo, int hi, Map<Integer, Integer> pos) {
        if (lo > hi) return null;
        TreeNode root = new TreeNode(pre[pi[0]++]);
        int mid = pos.get(root.val);
        root.left = build(pre, pi, lo, mid - 1, pos);
        root.right = build(pre, pi, mid + 1, hi, pos);
        return root;
    }

    // ---------- Serialize / deserialize (LC 297): preorder with null markers ----------
    // DEPTH: recursion n deep; at n >= 5e3 run inside a big-stack thread (see SCC_Bridges_2SAT.runWithBigStack).
    static String serialize(TreeNode root) {
        StringBuilder sb = new StringBuilder();
        ser(root, sb);
        return sb.toString();
    }

    private static void ser(TreeNode n, StringBuilder sb) {
        if (n == null) { sb.append("#,"); return; }
        sb.append(n.val).append(',');
        ser(n.left, sb);
        ser(n.right, sb);
    }

    static TreeNode deserialize(String data) {
        return des(new ArrayDeque<>(Arrays.asList(data.split(","))));
    }

    private static TreeNode des(Deque<String> q) {
        String s = q.poll();
        if (s == null || s.equals("#")) return null;
        TreeNode n = new TreeNode(Integer.parseInt(s));
        n.left = des(q);
        n.right = des(q);
        return n;
    }

    // ---------- Flatten to linked list (LC 114) ----------
    static void flatten(TreeNode root) {
        TreeNode cur = root;
        while (cur != null) {
            if (cur.left != null) {
                TreeNode p = cur.left;
                while (p.right != null) p = p.right;
                p.right = cur.right;
                cur.right = cur.left;
                cur.left = null;
            }
            cur = cur.right;
        }
    }

    // ---------- Distance K from a node (LC 863): parent map + BFS ----------
    static List<Integer> distanceK(TreeNode root, TreeNode target, int k) {
        Map<TreeNode, TreeNode> parent = new HashMap<>();
        Deque<TreeNode> q = new ArrayDeque<>();
        q.add(root);
        while (!q.isEmpty()) {
            TreeNode n = q.poll();
            for (TreeNode c : new TreeNode[]{n.left, n.right})
                if (c != null) { parent.put(c, n); q.add(c); }
        }
        Set<TreeNode> seen = new HashSet<>(List.of(target));
        q.add(target);
        while (!q.isEmpty() && k-- > 0) {
            for (int s = q.size(); s > 0; s--) {
                TreeNode n = q.poll();
                for (TreeNode nb : new TreeNode[]{n.left, n.right, parent.get(n)})
                    if (nb != null && seen.add(nb)) q.add(nb);
            }
        }
        List<Integer> out = new ArrayList<>();
        for (TreeNode n : q) out.add(n.val);
        return out;
    }

    // ---------- Vertical order (LC 987): sort by column, then row, then value ----------
    static List<List<Integer>> verticalTraversal(TreeNode root) {
        List<int[]> cells = new ArrayList<>(); // col, row, val
        Deque<Object[]> q = new ArrayDeque<>();
        q.add(new Object[]{root, 0, 0});
        while (!q.isEmpty()) {
            Object[] t = q.poll();
            TreeNode n = (TreeNode) t[0];
            if (n == null) continue;
            int col = (int) t[1], row = (int) t[2];
            cells.add(new int[]{col, row, n.val});
            q.add(new Object[]{n.left, col - 1, row + 1});
            q.add(new Object[]{n.right, col + 1, row + 1});
        }
        cells.sort((a, b) -> a[0] != b[0] ? Integer.compare(a[0], b[0]) : a[1] != b[1] ? Integer.compare(a[1], b[1]) : Integer.compare(a[2], b[2]));
        List<List<Integer>> res = new ArrayList<>();
        int prev = Integer.MIN_VALUE;
        for (int[] c : cells) {
            if (c[0] != prev) { res.add(new ArrayList<>()); prev = c[0]; }
            res.get(res.size() - 1).add(c[2]);
        }
        return res;
    }

    // ---------- Tree DP: House Robber III (LC 337) -> {rob this, skip this} ----------
    // DEPTH: recursion n deep; at n >= 5e3 run inside a big-stack thread (see SCC_Bridges_2SAT.runWithBigStack).
    static int robTree(TreeNode root) {
        int[] r = robDfs(root);
        return Math.max(r[0], r[1]);
    }

    private static int[] robDfs(TreeNode n) {
        if (n == null) return new int[]{0, 0};
        int[] l = robDfs(n.left), r = robDfs(n.right);
        return new int[]{n.val + l[1] + r[1], Math.max(l[0], l[1]) + Math.max(r[0], r[1])};
    }

    // Binary Tree Cameras (LC 968): states 0 = needs cover, 1 = has camera, 2 = covered
    private static int cams;

    static int minCameraCover(TreeNode root) {
        cams = 0;
        return (cover(root) == 0 ? 1 : 0) + cams;
    }

    private static int cover(TreeNode n) {
        if (n == null) return 2;
        int l = cover(n.left), r = cover(n.right);
        if (l == 0 || r == 0) { cams++; return 1; }
        return l == 1 || r == 1 ? 2 : 0;
    }

    // Helper for tests: build from level-order array, Integer null = absent
    static TreeNode fromLevel(Integer... a) {
        if (a.length == 0 || a[0] == null) return null;
        TreeNode root = new TreeNode(a[0]);
        Deque<TreeNode> q = new ArrayDeque<>();
        q.add(root);
        int i = 1;
        while (!q.isEmpty() && i < a.length) {
            TreeNode n = q.poll();
            if (i < a.length && a[i] != null) { n.left = new TreeNode(a[i]); q.add(n.left); }
            i++;
            if (i < a.length && a[i] != null) { n.right = new TreeNode(a[i]); q.add(n.right); }
            i++;
        }
        return root;
    }
}
