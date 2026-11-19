import java.math.BigInteger;
import java.util.*;

public class TreesVariantsTest {
    public static void main(String[] x) {
        trees();
        Check.done("TreesVariantsTest");
    }

    static Random rnd = new Random(31415926);
    static List<Integer> sorted(Collection<Integer> c) { List<Integer> l = new ArrayList<>(c); Collections.sort(l); return l; }
    static BinaryTree.TreeNode T(Integer... a) { return BinaryTree.fromLevel(a); }
    static String S(BinaryTree.TreeNode n) { return BinaryTree.serialize(n); }
    static void inorderList(BinaryTree.TreeNode n, List<Integer> out) { if (n == null) return; inorderList(n.left, out); out.add(n.val); inorderList(n.right, out); }
    static void postList(BinaryTree.TreeNode n, List<Integer> out) { if (n == null) return; postList(n.left, out); postList(n.right, out); out.add(n.val); }
    static void collect(BinaryTree.TreeNode n, List<BinaryTree.TreeNode> out) { if (n == null) return; out.add(n); collect(n.left, out); collect(n.right, out); }
    static BinaryTree.TreeNode bstInsert(BinaryTree.TreeNode root, int v) {
        if (root == null) return new BinaryTree.TreeNode(v);
        if (v < root.val) root.left = bstInsert(root.left, v); else root.right = bstInsert(root.right, v);
        return root;
    }
    static BinaryTree.TreeNode randomTree(int maxNodes) { // random shape, distinct values
        List<Integer> vals = new ArrayList<>();
        for (int i = 0; i < maxNodes; i++) vals.add(i + 1);
        Collections.shuffle(vals, rnd);
        int[] idx = {0};
        return grow(vals, idx, 0, 5);
    }
    static BinaryTree.TreeNode grow(List<Integer> vals, int[] idx, int depth, int maxDepth) {
        if (idx[0] >= vals.size() || depth >= maxDepth || (depth > 0 && rnd.nextInt(4) == 0)) return null;
        BinaryTree.TreeNode n = new BinaryTree.TreeNode(vals.get(idx[0]++));
        n.left = grow(vals, idx, depth + 1, maxDepth);
        n.right = grow(vals, idx, depth + 1, maxDepth);
        return n;
    }
    static int depthOf(BinaryTree.TreeNode n) { return n == null ? 0 : 1 + Math.max(depthOf(n.left), depthOf(n.right)); }
    static void trees() {
        for (int t = 0; t < 300; t++) {
            BinaryTree.TreeNode tr = randomTree(1 + rnd.nextInt(14));

            // sumNumbers cross-checked against binaryTreePaths (single digit values only)
            BinaryTree.TreeNode dg = digitTree(1 + rnd.nextInt(10));
            int viaPaths = 0;
            for (String p : TreesVariants.binaryTreePaths(dg)) viaPaths += Integer.parseInt(p.replace("->", ""));
            Check.eq("sumNumbers", TreesVariants.sumNumbers(dg), viaPaths);

            // width via explicit index map
            Map<Integer, long[]> span = new HashMap<>();
            widthDfs(tr, 0, 0, span);
            long wBest = 0;
            for (long[] s : span.values()) wBest = Math.max(wBest, s[1] - s[0] + 1);
            Check.eq("width", TreesVariants.widthOfBinaryTree(tr), (int) wBest);

            // longest univalue path with an alternative formulation
            Check.eq("univalue", TreesVariants.longestUnivaluePath(valueSquash(tr)), uniBrute(valueSquash(tr)));

            // countNodes on a complete tree
            int cn = 1 + rnd.nextInt(40);
            Integer[] full = new Integer[cn];
            for (int i = 0; i < cn; i++) full[i] = i + 1;
            Check.eq("countNodes", TreesVariants.countNodes(T(full)), cn);

            // cousins
            List<BinaryTree.TreeNode> all = new ArrayList<>();
            collect(tr, all);
            if (all.size() >= 2) {
                int xv = all.get(rnd.nextInt(all.size())).val, yv = all.get(rnd.nextInt(all.size())).val;
                if (xv != yv) Check.eq("cousins", TreesVariants.isCousins(tr, xv, yv), cousinsBrute(tr, xv, yv));
            }

            // deepest-leaves LCA
            Check.eq("lcaDeepest", TreesVariants.lcaDeepestLeaves(tr).val, lcaDeepBrute(tr));

            // buildTreeInPost
            List<Integer> in = new ArrayList<>(), post = new ArrayList<>();
            inorderList(tr, in);
            postList(tr, post);
            Check.eq("inPost", S(TreesVariants.buildTreeInPost(in.stream().mapToInt(Integer::intValue).toArray(), post.stream().mapToInt(Integer::intValue).toArray())), S(tr));

            // BST family
            List<Integer> vals = new ArrayList<>();
            for (int i = 0; i < 1 + rnd.nextInt(10); i++) vals.add(rnd.nextInt(30));
            Set<Integer> dedup = new LinkedHashSet<>(vals);
            vals = new ArrayList<>(dedup);
            BinaryTree.TreeNode bst = null;
            for (int v : vals) bst = bstInsert(bst, v);
            int[] preorder = new int[vals.size()];
            List<Integer> pre = new ArrayList<>();
            preList(bst, pre);
            for (int i = 0; i < pre.size(); i++) preorder[i] = pre.get(i);
            Check.eq("bstFromPreorder", S(TreesVariants.bstFromPreorder(preorder)), S(bst));

            List<Integer> sortedVals = sorted(vals);
            int lo = rnd.nextInt(30), hi = lo + rnd.nextInt(15);
            int rangeWant = 0;
            for (int v : vals) if (v >= lo && v <= hi) rangeWant += v;
            Check.eq("rangeSum", TreesVariants.rangeSumBST(bst, lo, hi), rangeWant);
            if (vals.size() >= 2) {
                int md = Integer.MAX_VALUE;
                for (int i = 1; i < sortedVals.size(); i++) md = Math.min(md, sortedVals.get(i) - sortedVals.get(i - 1));
                Check.eq("minDiff", TreesVariants.minDiffInBST(bst), md);
            }
            int probe = rnd.nextInt(30);
            Integer succ = null;
            for (int v : sortedVals) if (v > probe) { succ = v; break; }
            BinaryTree.TreeNode sn = TreesVariants.inorderSuccessor(bst, probe);
            Check.eq("successor", sn == null ? null : sn.val, succ);

            BinaryTree.TreeNode trimmed = TreesVariants.trimBST(copy(bst), lo, hi);
            List<Integer> tl = new ArrayList<>();
            inorderList(trimmed, tl);
            List<Integer> trimWant = new ArrayList<>();
            for (int v : sortedVals) if (v >= lo && v <= hi) trimWant.add(v);
            Check.eq("trim", tl, trimWant);

            BinaryTree.TreeNode conv = TreesVariants.convertBST(copy(bst));
            List<Integer> convGot = new ArrayList<>();
            inorderList(conv, convGot);
            List<Integer> convWant = new ArrayList<>();
            for (int v : sortedVals) { int s = 0; for (int u : sortedVals) if (u >= v) s += u; convWant.add(s); }
            Check.eq("greaterTree", convGot, convWant);

            int[] sa = sortedVals.stream().mapToInt(Integer::intValue).toArray();
            BinaryTree.TreeNode bal = TreesVariants.sortedArrayToBST(sa);
            List<Integer> bl = new ArrayList<>();
            inorderList(bal, bl);
            Check.eq("sortedToBst inorder", bl, sortedVals);
            Check.eq("sortedToBst balanced", balanced(bal), true);
        }
        Check.eq("boundary1", TreesVariants.boundaryOfBinaryTree(T(1, null, 2, 3, 4)).toString(), "[1, 3, 4, 2]");
        Check.eq("boundary2", TreesVariants.boundaryOfBinaryTree(T(1, 2, 3, 4, 5, 6, null, null, null, 7, 8, 9, 10)).toString(), "[1, 2, 4, 7, 8, 9, 10, 6, 3]");
        Check.eq("boundary3", TreesVariants.boundaryOfBinaryTree(T(1)).toString(), "[1]");
        Check.eq("coins1", TreesVariants.distributeCoins(T(3, 0, 0)), 2);
        Check.eq("coins2", TreesVariants.distributeCoins(T(0, 3, 0)), 3);
        Check.eq("univalue1", TreesVariants.longestUnivaluePath(T(5, 4, 5, 1, 1, null, 5)), 2);
        Check.eq("univalue2", TreesVariants.longestUnivaluePath(T(1, 4, 5, 4, 4, null, 5)), 2);
        Check.eq("sumNumbers1", TreesVariants.sumNumbers(T(1, 2, 3)), 25);
        Check.eq("sumNumbers2", TreesVariants.sumNumbers(T(4, 9, 0, 5, 1)), 1026);
        Check.eq("width1", TreesVariants.widthOfBinaryTree(T(1, 3, 2, 5, 3, null, 9)), 4);
        Check.eq("width2", TreesVariants.widthOfBinaryTree(T(1, 3, 2, 5, null, null, 9, 6, null, 7)), 7);
        List<Integer> dups = new ArrayList<>();
        for (BinaryTree.TreeNode n : TreesVariants.findDuplicateSubtrees(T(1, 2, 3, 4, null, 2, 4, null, null, 4))) dups.add(n.val);
        Check.eq("dupSubtrees", sorted(dups).toString(), "[2, 4]");
        Check.eq("fbt3", TreesVariants.allPossibleFBT(3).size(), 1);
        Check.eq("fbt7", TreesVariants.allPossibleFBT(7).size(), 5);
        Check.eq("fbt9", TreesVariants.allPossibleFBT(9).size(), 14);
        Check.eq("fbtEven", TreesVariants.allPossibleFBT(4).size(), 0);
        int[] catalan = {1, 1, 2, 5, 14, 42};
        for (int n = 1; n <= 5; n++) {
            List<BinaryTree.TreeNode> gt = TreesVariants.generateTrees(n);
            Check.eq("generateTrees count", gt.size(), catalan[n]);
            Set<String> seen = new HashSet<>();
            for (BinaryTree.TreeNode tr : gt) {
                List<Integer> il = new ArrayList<>();
                inorderList(tr, il);
                Check.eq("generateTrees inorder", il.size() == n && il.equals(sorted(il)), true);
                seen.add(S(tr));
            }
            Check.eq("generateTrees distinct", seen.size(), gt.size());
        }
        Check.eq("inPostEx", S(TreesVariants.buildTreeInPost(new int[]{9, 3, 15, 20, 7}, new int[]{9, 15, 7, 20, 3})), S(T(3, 9, 20, null, null, 15, 7)));
        Check.eq("maxProduct1", TreesVariants.maxProduct(T(1, 2, 3, 4, 5, 6)), 110);
        Check.eq("maxProduct2", TreesVariants.maxProduct(T(1, null, 2, 3, 4, null, null, 5, 6)), 90);
        Check.eq("infect1", TreesVariants.amountOfTime(T(1, 5, 3, null, 4, 10, 6, 9, 2), 3), 4);
        Check.eq("infect2", TreesVariants.amountOfTime(T(1), 1), 0);
        Check.eq("infect3", TreesVariants.amountOfTime(T(1, 2, null, 3, null, 4, null, 5), 3), 2);
        Check.eq("lcaDeep1", TreesVariants.lcaDeepestLeaves(T(3, 5, 1, 6, 2, 0, 8, null, null, 7, 4)).val, 2);
        List<Integer> roots = new ArrayList<>();
        for (BinaryTree.TreeNode n : TreesVariants.delNodes(T(1, 2, 3, 4, 5, 6, 7), new int[]{3, 5})) roots.add(n.val);
        Check.eq("delNodes", sorted(roots).toString(), "[1, 6, 7]");
        Check.eq("cousins1", TreesVariants.isCousins(T(1, 2, 3, 4), 4, 3), false);
        Check.eq("cousins2", TreesVariants.isCousins(T(1, 2, 3, null, 4, null, 5), 5, 4), true);
        Check.eq("cousins3", TreesVariants.isCousins(T(1, 2, 3, null, 4), 2, 3), false);
        Check.eq("paths", TreesVariants.binaryTreePaths(T(1, 2, 3, null, 5)).toString(), "[1->2->5, 1->3]");
        Check.eq("pathSumII", TreesVariants.pathSumII(T(5, 4, 8, 11, null, 13, 4, 7, 2, null, null, 5, 1), 22).toString(), "[[5, 4, 11, 2], [5, 8, 4, 5]]");
    }
    static BinaryTree.TreeNode digitTree(int n) {
        List<Integer> vals = new ArrayList<>();
        for (int i = 0; i < n; i++) vals.add(rnd.nextInt(10));
        int[] idx = {0};
        return growSame(vals, idx, 0);
    }
    static BinaryTree.TreeNode growSame(List<Integer> vals, int[] idx, int depth) {
        if (idx[0] >= vals.size() || depth >= 4 || (depth > 0 && rnd.nextInt(4) == 0)) return null;
        BinaryTree.TreeNode n = new BinaryTree.TreeNode(vals.get(idx[0]++));
        n.left = growSame(vals, idx, depth + 1);
        n.right = growSame(vals, idx, depth + 1);
        return n;
    }
    static BinaryTree.TreeNode valueSquash(BinaryTree.TreeNode n) { // copy with values mod 3 so equal neighbours are common
        if (n == null) return null;
        BinaryTree.TreeNode c = new BinaryTree.TreeNode(n.val % 2);
        c.left = valueSquash(n.left);
        c.right = valueSquash(n.right);
        return c;
    }
    static int chain(BinaryTree.TreeNode n, int v) { return n == null || n.val != v ? 0 : 1 + Math.max(chain(n.left, v), chain(n.right, v)); }
    static int uniBrute(BinaryTree.TreeNode root) {
        List<BinaryTree.TreeNode> all = new ArrayList<>();
        collect(root, all);
        int best = 0;
        for (BinaryTree.TreeNode n : all) best = Math.max(best, chain(n.left, n.val) + chain(n.right, n.val));
        return best;
    }
    static void widthDfs(BinaryTree.TreeNode n, int depth, long idx, Map<Integer, long[]> span) {
        if (n == null) return;
        long[] s = span.computeIfAbsent(depth, d -> new long[]{Long.MAX_VALUE, Long.MIN_VALUE});
        s[0] = Math.min(s[0], idx);
        s[1] = Math.max(s[1], idx);
        widthDfs(n.left, depth + 1, 2 * idx, span);
        widthDfs(n.right, depth + 1, 2 * idx + 1, span);
    }
    static boolean cousinsBrute(BinaryTree.TreeNode root, int x, int y) {
        Map<Integer, int[]> info = new HashMap<>(); // val -> {depth, parent}
        record(root, 0, -1, info);
        return info.get(x)[0] == info.get(y)[0] && info.get(x)[1] != info.get(y)[1];
    }
    static void record(BinaryTree.TreeNode n, int depth, int parent, Map<Integer, int[]> info) {
        if (n == null) return;
        info.put(n.val, new int[]{depth, parent});
        record(n.left, depth + 1, n.val, info);
        record(n.right, depth + 1, n.val, info);
    }
    static int lcaDeepBrute(BinaryTree.TreeNode root) {
        int maxDepth = depthOf(root);
        List<BinaryTree.TreeNode> all = new ArrayList<>();
        collect(root, all);
        Map<BinaryTree.TreeNode, Integer> depth = new HashMap<>();
        depthMap(root, 1, depth);
        Set<BinaryTree.TreeNode> deepest = new HashSet<>();
        for (var e : depth.entrySet()) if (e.getValue() == maxDepth) deepest.add(e.getKey());
        BinaryTree.TreeNode best = null;
        int bestDepth = -1;
        for (BinaryTree.TreeNode n : all) {
            List<BinaryTree.TreeNode> sub = new ArrayList<>();
            collect(n, sub);
            if (sub.containsAll(deepest) && depth.get(n) > bestDepth) { best = n; bestDepth = depth.get(n); }
        }
        return best.val;
    }
    static void depthMap(BinaryTree.TreeNode n, int d, Map<BinaryTree.TreeNode, Integer> m) { if (n == null) return; m.put(n, d); depthMap(n.left, d + 1, m); depthMap(n.right, d + 1, m); }
    static void preList(BinaryTree.TreeNode n, List<Integer> out) { if (n == null) return; out.add(n.val); preList(n.left, out); preList(n.right, out); }
    static BinaryTree.TreeNode copy(BinaryTree.TreeNode n) { if (n == null) return null; BinaryTree.TreeNode c = new BinaryTree.TreeNode(n.val); c.left = copy(n.left); c.right = copy(n.right); return c; }
    static boolean balanced(BinaryTree.TreeNode n) { return n == null || (Math.abs(depthOf(n.left) - depthOf(n.right)) <= 1 && balanced(n.left) && balanced(n.right)); }
}
