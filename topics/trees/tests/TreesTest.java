import java.util.*;

public class TreesTest {
    static BinaryTree.TreeNode T(Integer... a) { return BinaryTree.fromLevel(a); }

    public static void main(String[] args) {
        BinaryTree.TreeNode t = T(3, 9, 20, null, null, 15, 7);
        Check.eq("inorder", BinaryTree.inorder(t), "[9, 3, 15, 20, 7]");
        Check.eq("morris", BinaryTree.inorderMorris(T(3, 9, 20, null, null, 15, 7)), "[9, 3, 15, 20, 7]");
        Check.eq("preorder", BinaryTree.preorder(t), "[3, 9, 20, 15, 7]");
        Check.eq("postorder", BinaryTree.postorder(t), "[9, 15, 7, 20, 3]");
        Check.eq("level", BinaryTree.levelOrder(t), "[[3], [9, 20], [15, 7]]");
        Check.eq("zigzag", BinaryTree.zigzag(t), "[[3], [20, 9], [15, 7]]");
        Check.eq("rightView", BinaryTree.rightSideView(t), "[3, 20, 7]");
        Check.eq("depth", BinaryTree.maxDepth(t), 3);
        Check.eq("diameter", BinaryTree.diameter(T(1, 2, 3, 4, 5)), 3);
        Check.eq("balanced", BinaryTree.isBalanced(t) + "," + BinaryTree.isBalanced(T(1, 2, 2, 3, 3, null, null, 4, 4)), "true,false");
        Check.eq("maxPath", BinaryTree.maxPathSum(T(-10, 9, 20, null, null, 15, 7)), 42);
        Check.eq("symmetric", BinaryTree.isSymmetric(T(1, 2, 2, 3, 4, 4, 3)) + "," + BinaryTree.isSymmetric(T(1, 2, 2, null, 3, null, 3)), "true,false");
        Check.eq("invert", BinaryTree.levelOrder(BinaryTree.invertTree(T(4, 2, 7, 1, 3, 6, 9))), "[[4], [7, 2], [9, 6, 3, 1]]");
        Check.eq("subtree", BinaryTree.isSubtree(T(3, 4, 5, 1, 2), T(4, 1, 2)), true);
        Check.eq("hasPath", BinaryTree.hasPathSum(T(5, 4, 8, 11, null, 13, 4, 7, 2, null, null, null, 1), 22), true);
        Check.eq("pathSumIII", BinaryTree.pathSumIII(T(10, 5, -3, 3, 2, null, 11, 3, -2, null, 1), 8), 3);
        BinaryTree.TreeNode lt = T(3, 5, 1, 6, 2, 0, 8, null, null, 7, 4);
        Check.eq("lca", BinaryTree.lowestCommonAncestor(lt, lt.left, lt.right).val, 3);
        Check.eq("lca2", BinaryTree.lowestCommonAncestor(lt, lt.left, lt.left.right.right).val, 5);
        Check.eq("build", BinaryTree.levelOrder(BinaryTree.buildTree(new int[]{3, 9, 20, 15, 7}, new int[]{9, 3, 15, 20, 7})), "[[3], [9, 20], [15, 7]]");
        String ser = BinaryTree.serialize(t);
        Check.eq("serde", BinaryTree.serialize(BinaryTree.deserialize(ser)), ser);
        BinaryTree.TreeNode fl = T(1, 2, 5, 3, 4, null, 6);
        BinaryTree.flatten(fl);
        Check.eq("flatten", BinaryTree.inorder(fl), "[1, 2, 3, 4, 5, 6]");
        BinaryTree.TreeNode dk = T(3, 5, 1, 6, 2, 0, 8, null, null, 7, 4);
        List<Integer> k2 = BinaryTree.distanceK(dk, dk.left, 2);
        Collections.sort(k2);
        Check.eq("distK", k2, "[1, 4, 7]");
        Check.eq("vertical", BinaryTree.verticalTraversal(T(3, 9, 20, null, null, 15, 7)), "[[9], [3, 15], [20], [7]]");
        Check.eq("robTree", BinaryTree.robTree(T(3, 2, 3, null, 3, null, 1)), 7);
        Check.eq("cameras", BinaryTree.minCameraCover(T(0, 0, null, 0, 0)), 1);

        // BST
        BinaryTree.TreeNode b = T(5, 3, 6, 2, 4, null, 7);
        Check.eq("validBST", BST.isValidBST(b) + "," + BST.isValidBST(T(5, 1, 4, null, null, 3, 6)), "true,false");
        Check.eq("kth", BST.kthSmallest(b, 3), 4);
        Check.eq("bstLca", BST.lcaBST(b, 2, 4).val, 3);
        BinaryTree.TreeNode del = BST.delete(T(5, 3, 6, 2, 4, null, 7), 3);
        Check.eq("delete", BinaryTree.inorder(del), "[2, 4, 5, 6, 7]");
        Check.eq("insert", BinaryTree.inorder(BST.insert(b, 1)), "[1, 2, 3, 4, 5, 6, 7]");
        Check.eq("sortedToBST", BinaryTree.inorder(BST.sortedArrayToBST(new int[]{-10, -3, 0, 5, 9})), "[-10, -3, 0, 5, 9]");
        BinaryTree.TreeNode rec = T(3, 1, 4, null, null, 2);
        BST.recoverTree(rec);
        Check.eq("recover", BinaryTree.inorder(rec), "[1, 2, 3, 4]");
        BST.BSTIterator it = new BST.BSTIterator(T(7, 3, 15, null, null, 9, 20));
        StringBuilder sb = new StringBuilder();
        while (it.hasNext()) sb.append(it.next()).append(' ');
        Check.eq("iterator", sb.toString().trim(), "3 7 9 15 20");
        Check.eq("largestBST", BST.largestBSTSubtree(T(10, 5, 15, 1, 8, null, 7)), 3);
        Check.eq("floor", BST.floor(b, 5) + "," + BST.floor(b, 0), "5,null");
        Check.eq("numTrees", BST.numTrees(3) + "," + BST.numTrees(5), "5,42");

        // Trie
        Trie tr = new Trie();
        for (String w : new String[]{"apple", "app", "apt"}) tr.insert(w);
        Check.eq("trie", tr.search("app") + "," + tr.search("ap") + "," + tr.startsWith("ap") + "," + tr.countPrefix("ap"), "true,false,true,3");
        Check.eq("wild", tr.searchWildcard("a.t") + "," + tr.searchWildcard("a..le") + "," + tr.searchWildcard("b.."), "true,true,false");
        char[][] board = {"oaan".toCharArray(), "etae".toCharArray(), "ihkr".toCharArray(), "iflv".toCharArray()};
        List<String> fw = Trie.findWords(board, new String[]{"oath", "pea", "eat", "rain"});
        Collections.sort(fw);
        Check.eq("wordSearch2", fw, "[eat, oath]");
        Check.eq("maxXor", Trie.findMaximumXOR(new int[]{3, 10, 5, 25, 2, 8}), 28);
        Check.eq("replaceWords", Trie.replaceWords(List.of("cat", "bat", "rat"), "the cattle was rattled by the battery"), "the cat was rat by the bat");
        Check.eq("suggest", Trie.suggestedProducts(new String[]{"mobile", "mouse", "moneypot", "monitor", "mousepad"}, "mouse"),
                "[[mobile, moneypot, monitor], [mobile, moneypot, monitor], [mouse, mousepad], [mouse, mousepad], [mouse, mousepad]]");

        // Range structures
        int[] arr = {1, 2, 3, 4, 5};
        long[] pf = RangeStructures.prefix(arr);
        Check.eq("prefix", pf[4] - pf[1], 9);
        Check.eq("rangeAdd", Check.s(RangeStructures.rangeAdd(5, new int[][]{{1, 3, 2}, {2, 4, 3}, {0, 2, -2}})), "[-2, 0, 3, 5, 3]");
        RangeStructures.NumMatrix nm = new RangeStructures.NumMatrix(new int[][]{{3, 0, 1, 4, 2}, {5, 6, 3, 2, 1}, {1, 2, 0, 1, 5}, {4, 1, 0, 1, 7}, {1, 0, 3, 0, 5}});
        Check.eq("numMatrix", nm.sumRegion(2, 1, 4, 3), 8);
        Check.eq("countSmaller", RangeStructures.countSmaller(new int[]{5, 2, 6, 1}), "[2, 1, 1, 0]");
        Check.eq("inversions", RangeStructures.countInversions(new int[]{2, 4, 1, 3, 5}), 3);
        Check.eq("skyline", RangeStructures.getSkyline(new int[][]{{2, 9, 10}, {3, 7, 15}, {5, 12, 12}, {15, 20, 10}, {19, 24, 8}}),
                "[[2, 10], [3, 15], [7, 12], [12, 0], [15, 10], [20, 8], [24, 0]]");

        // Randomized cross-checks
        Random r = new Random(11);
        for (int rep = 0; rep < 200; rep++) {
            int n = 1 + r.nextInt(20);
            int[] a = new int[n];
            for (int i = 0; i < n; i++) a[i] = r.nextInt(21) - 10;
            RangeStructures.Fenwick fw2 = new RangeStructures.Fenwick(n);
            RangeStructures.SegTree seg = new RangeStructures.SegTree(a);
            RangeStructures.LazySeg lz = new RangeStructures.LazySeg(a);
            RangeStructures.SparseTable sp = new RangeStructures.SparseTable(a);
            long[] ref = new long[n];
            for (int i = 0; i < n; i++) { fw2.add(i, a[i]); ref[i] = a[i]; }
            for (int op = 0; op < 30; op++) {
                int l = r.nextInt(n), rr = l + r.nextInt(n - l);
                int kind = r.nextInt(3);
                if (kind == 0) { // point update
                    int idx = r.nextInt(n), v = r.nextInt(21) - 10;
                    fw2.add(idx, v - ref[idx]);
                    seg.set(idx, v);
                    lz.add(idx, idx, v - ref[idx]);
                    ref[idx] = v;
                } else if (kind == 1) { // range add for lazy only: mirror into others via point updates
                    int v = r.nextInt(11) - 5;
                    lz.add(l, rr, v);
                    for (int i = l; i <= rr; i++) { fw2.add(i, v); seg.set(i, ref[i] + v); ref[i] += v; }
                } else {
                    long want = 0;
                    for (int i = l; i <= rr; i++) want += ref[i];
                    Check.eq("fenwick", fw2.range(l, rr), want);
                    Check.eq("seg", seg.query(l, rr), want);
                    Check.eq("lazy", lz.query(l, rr), want);
                }
            }
            int l = r.nextInt(n), rr = l + r.nextInt(n - l), mn = Integer.MAX_VALUE;
            for (int i = l; i <= rr; i++) mn = Math.min(mn, a[i]);
            Check.eq("sparse", sp.min(l, rr), mn);
        }
        {
        // 1.2 skyline: overflow regression + randomized brute force incl. heights near Integer.MAX_VALUE
        Check.eq("skylineMax", RangeStructures.getSkyline(new int[][]{{0, 2, Integer.MAX_VALUE}, {2, 3, Integer.MAX_VALUE}}).toString(), "[[0, 2147483647], [3, 0]]");
        Random sk = new Random(9);
        for (int iter = 0; iter < 500; iter++) {
            int nb = 1 + sk.nextInt(5);
            int[][] bs = new int[nb][];
            for (int i = 0; i < nb; i++) {
                int l = sk.nextInt(8), rr = l + 1 + sk.nextInt(5);
                int h = sk.nextBoolean() ? Integer.MAX_VALUE - sk.nextInt(3) : 1 + sk.nextInt(5);
                if (sk.nextInt(5) == 0) h = 1;
                bs[i] = new int[]{l, rr, h};
            }
            List<List<Integer>> want = new ArrayList<>();
            int prev = 0;
            for (int x = 0; x <= 14; x++) {
                int h = 0;
                for (int[] bb : bs) if (bb[0] <= x && x < bb[1]) h = Math.max(h, bb[2]);
                if (h != prev) { want.add(List.of(x, h)); prev = h; }
            }
            Check.eq("skylineRand", RangeStructures.getSkyline(bs).toString(), want.toString());
        }
        // 1.16 largestBSTSubtree with extreme node values
        Check.eq("lbstMin", BST.largestBSTSubtree(T(Integer.MIN_VALUE, null, 5)), 2);
        Check.eq("lbstMax", BST.largestBSTSubtree(T(Integer.MAX_VALUE, 3, null)), 2);
        Check.eq("lbstBoth", BST.largestBSTSubtree(T(0, Integer.MIN_VALUE, Integer.MAX_VALUE)), 3);
        Check.eq("lbstSingleMin", BST.largestBSTSubtree(T(Integer.MIN_VALUE)), 1);
        }
        Check.eq("isSameTree equal", BinaryTree.isSameTree(T(1, 2, 3), T(1, 2, 3)), true);
        Check.eq("isSameTree shape", BinaryTree.isSameTree(T(1, 2), T(1, null, 2)), false);
        Check.eq("isSameTree values", BinaryTree.isSameTree(T(1, 2, 1), T(1, 1, 2)), false);
        Check.eq("isSameTree both null", BinaryTree.isSameTree(null, null), true);
        Check.eq("isSameTree one null", BinaryTree.isSameTree(T(1), null), false);
        lopsided();
        Check.done("TreesTest");
    }

    // Skewed trees: 50-node chains leaning left and right (values 1..50 from the root down) catch base-case and
    // left/right mix-ups that balanced inputs hide. Small enough to run on any stack.
    static void lopsided() {
        for (boolean left : new boolean[]{true, false}) {
            String side = left ? "left" : "right";
            BinaryTree.TreeNode root = new BinaryTree.TreeNode(1), cur = root, at25 = null, at50 = null;
            for (int v = 2; v <= 50; v++) {
                BinaryTree.TreeNode n = new BinaryTree.TreeNode(v);
                if (left) cur.left = n; else cur.right = n;
                cur = n;
                if (v == 25) at25 = n;
                if (v == 50) at50 = n;
            }
            Check.eq("chain-" + side + " maxDepth", BinaryTree.maxDepth(root), 50);
            Check.eq("chain-" + side + " diameter", BinaryTree.diameter(root), 49);
            Check.eq("chain-" + side + " isBalanced", BinaryTree.isBalanced(root), false);
            Check.eq("chain-" + side + " maxPathSum", BinaryTree.maxPathSum(root), 1275);   // 1 + 2 + ... + 50
            Check.eq("chain-" + side + " robTree", BinaryTree.robTree(root), 650);          // 2 + 4 + ... + 50
            Check.eq("chain-" + side + " levelOrder size", BinaryTree.levelOrder(root).size(), 50);
            List<Integer> in = BinaryTree.inorder(root);
            Check.eq("chain-" + side + " inorder first", in.get(0), left ? 50 : 1);
            Check.eq("chain-" + side + " inorder last", in.get(49), left ? 1 : 50);
            Check.eq("chain-" + side + " lca(25,50)", BinaryTree.lowestCommonAncestor(root, at25, at50).val, 25);
            Check.eq("chain-" + side + " serialize roundtrip", BinaryTree.isSameTree(root, BinaryTree.deserialize(BinaryTree.serialize(root))), true);
        }
    }
}
