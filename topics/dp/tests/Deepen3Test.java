import java.math.BigInteger;
import java.util.*;

public class Deepen3Test {
    static Random rnd = new Random(31415926);
    static int[] arr(int n, int lo, int hi) { int[] a = new int[n]; for (int i = 0; i < n; i++) a[i] = lo + rnd.nextInt(hi - lo + 1); return a; }
    static String str(int n, int alpha) { StringBuilder sb = new StringBuilder(); for (int i = 0; i < n; i++) sb.append((char) ('a' + rnd.nextInt(alpha))); return sb.toString(); }
    static List<Integer> sorted(Collection<Integer> c) { List<Integer> l = new ArrayList<>(c); Collections.sort(l); return l; }

    // Top-down DP recurses ~10^4 deep on some cases (e.g. checkRecord(10101)); Linux gives the main thread a small
    // stack, so run the suite in a big-stack thread and rethrow anything it throws so a failure still exits non-zero.
    public static void main(String[] x) throws Throwable {
        Throwable[] err = new Throwable[1];
        Thread t = new Thread(null, () -> { try { run(x); } catch (Throwable e) { err[0] = e; } }, "dp-tests", 1L << 28);
        t.start();
        t.join();
        if (err[0] != null) throw err[0];
    }

    static void run(String[] x) {
        lists();
        bits();
        trees();
        dp();
        Check.done("Deepen3Test");
    }

    // ================================================================== linked lists
    static List<Integer> toList(LinkedLists.ListNode n) { List<Integer> l = new ArrayList<>(); for (; n != null; n = n.next) l.add(n.val); return l; }
    static LinkedLists.ListNode fromList(List<Integer> l) { LinkedLists.ListNode d = new LinkedLists.ListNode(0), t = d; for (int v : l) { t.next = new LinkedLists.ListNode(v); t = t.next; } return d.next; }

    static void lists() {
        for (int t = 0; t < 400; t++) {
            List<Integer> vals = new ArrayList<>();
            for (int v : arr(1 + rnd.nextInt(9), -3, 3)) vals.add(v);

            List<Integer> res = toList(LinkedListsVariants.removeZeroSumSublists(fromList(vals)));
            boolean zero = false;
            for (int i = 0; i < res.size(); i++) { int s = 0; for (int j = i; j < res.size(); j++) { s += res.get(j); if (s == 0) zero = true; } }
            Check.eq("zeroSumNone", zero, false);
            int total = 0, resTotal = 0;
            for (int v : vals) total += v;
            for (int v : res) resTotal += v;
            Check.eq("zeroSumKeepsTotal", resTotal, total);
            int p = 0;
            for (int v : vals) if (p < res.size() && res.get(p) == v) p++;
            Check.eq("zeroSumSubsequence", p, res.size());

            int[] nl = LinkedListsVariants.nextLargerNodes(fromList(vals));
            int[] want = new int[vals.size()];
            for (int i = 0; i < want.length; i++) for (int j = i + 1; j < want.length; j++) if (vals.get(j) > vals.get(i)) { want[i] = vals.get(j); break; }
            Check.eq("nextLarger", Check.s(nl), Check.s(want));

            int k = 1 + rnd.nextInt(6);
            LinkedLists.ListNode[] parts = LinkedListsVariants.splitListToParts(fromList(vals), k);
            List<Integer> concat = new ArrayList<>();
            int prevSize = Integer.MAX_VALUE, minSize = Integer.MAX_VALUE, maxSize = 0;
            for (LinkedLists.ListNode part : parts) { List<Integer> pl = toList(part); concat.addAll(pl); Check.eq("splitNonIncreasing", pl.size() <= prevSize, true); prevSize = pl.size(); minSize = Math.min(minSize, pl.size()); maxSize = Math.max(maxSize, pl.size()); }
            Check.eq("splitConcat", concat, vals);
            Check.eq("splitBalanced", maxSize - minSize <= 1, true);
            Check.eq("splitCount", parts.length, k);

            List<Integer> digits = new ArrayList<>();
            for (int d : arr(1 + rnd.nextInt(6), 0, 9)) digits.add(d);
            if (digits.get(0) == 0 && digits.size() > 1) digits.set(0, 1);
            if (rnd.nextInt(3) == 0) for (int i = 0; i < digits.size(); i++) digits.set(i, 9);
            StringBuilder sb = new StringBuilder();
            for (int d : digits) sb.append(d);
            String wantPlus = new BigInteger(sb.toString()).add(BigInteger.ONE).toString();
            StringBuilder got = new StringBuilder();
            for (int d : toList(LinkedListsVariants.plusOne(fromList(digits)))) got.append(d);
            Check.eq("plusOne", got.toString(), wantPlus);

            int sk = 1 + rnd.nextInt(vals.size());
            List<Integer> sw = new ArrayList<>(vals);
            Collections.swap(sw, sk - 1, sw.size() - sk);
            Check.eq("swapNodes", toList(LinkedListsVariants.swapNodes(fromList(vals), sk)), sw);

            List<Integer> dm = new ArrayList<>(vals);
            dm.remove(vals.size() / 2);
            Check.eq("deleteMiddle", toList(LinkedListsVariants.deleteMiddle(fromList(vals))), dm);

            if (vals.size() % 2 == 0) {
                int best = 0;
                for (int i = 0; i < vals.size() / 2; i++) best = Math.max(best, vals.get(i) + vals.get(vals.size() - 1 - i));
                Check.eq("twinSum", LinkedListsVariants.pairSum(fromList(vals)), best);
            }

            List<Integer> bz = new ArrayList<>();
            bz.add(0);
            List<Integer> mergedWant = new ArrayList<>();
            int run = 0;
            for (int g = 0; g < 1 + rnd.nextInt(4); g++) { int sum = 0; for (int i = 0; i < 1 + rnd.nextInt(3); i++) { int v = 1 + rnd.nextInt(5); bz.add(v); sum += v; } bz.add(0); mergedWant.add(sum); }
            Check.eq("mergeZeros", toList(LinkedListsVariants.mergeNodes(fromList(bz))), mergedWant);

            List<Integer> l1 = new ArrayList<>(), l2 = new ArrayList<>();
            for (int i = 0; i < 6; i++) l1.add(i);
            for (int i = 0; i < 1 + rnd.nextInt(3); i++) l2.add(100 + i);
            int a = 1 + rnd.nextInt(3), b = a + rnd.nextInt(3);
            List<Integer> mw = new ArrayList<>(l1.subList(0, a));
            mw.addAll(l2);
            mw.addAll(l1.subList(b + 1, l1.size()));
            Check.eq("mergeInBetween", toList(LinkedListsVariants.mergeInBetween(fromList(l1), a, b, fromList(l2))), mw);

            List<Integer> grp = new ArrayList<>();
            for (int i = 0; i < 1 + rnd.nextInt(14); i++) grp.add(i);
            List<Integer> gw = new ArrayList<>();
            for (int i = 0, size = 1; i < grp.size(); size++) {
                int end = Math.min(grp.size(), i + size);
                List<Integer> g = new ArrayList<>(grp.subList(i, end));
                if (g.size() % 2 == 0) Collections.reverse(g);
                gw.addAll(g);
                i = end;
            }
            Check.eq("evenGroups", toList(LinkedListsVariants.reverseEvenLengthGroups(fromList(grp))), gw);

            List<Integer> srt = new ArrayList<>(vals);
            Collections.sort(srt);
            Check.eq("insertionSort", toList(LinkedListsVariants.insertionSortList(fromList(vals))), srt);

            List<Integer> bits = new ArrayList<>();
            int dec = 0;
            for (int i = 0; i < 1 + rnd.nextInt(10); i++) { int bit = rnd.nextInt(2); bits.add(bit); dec = dec * 2 + bit; }
            Check.eq("decimal", LinkedListsVariants.getDecimalValue(fromList(bits)), dec);

            int rv = rnd.nextInt(7) - 3;
            List<Integer> rm = new ArrayList<>(vals);
            rm.removeIf(v -> v == rv);
            Check.eq("removeElements", toList(LinkedListsVariants.removeElements(fromList(vals), rv)), rm);

            // circular insert: sorted values, head at a random rotation
            List<Integer> cv = new ArrayList<>();
            for (int v : arr(rnd.nextInt(6), 0, 8)) cv.add(v);
            Collections.sort(cv);
            int ins = rnd.nextInt(10) - 1;
            LinkedLists.ListNode head = null;
            if (!cv.isEmpty()) {
                LinkedLists.ListNode[] nodes = new LinkedLists.ListNode[cv.size()];
                for (int i = 0; i < nodes.length; i++) nodes[i] = new LinkedLists.ListNode(cv.get(i));
                for (int i = 0; i < nodes.length; i++) nodes[i].next = nodes[(i + 1) % nodes.length];
                head = nodes[rnd.nextInt(nodes.length)];
            }
            LinkedLists.ListNode h2 = LinkedListsVariants.insertCircular(head, ins);
            List<Integer> around = new ArrayList<>();
            LinkedLists.ListNode c = h2;
            do { around.add(c.val); c = c.next; } while (c != h2 && around.size() < 50);
            List<Integer> expect = new ArrayList<>(cv);
            expect.add(ins);
            Check.eq("circularMultiset", sorted(around), sorted(expect));
            int descents = 0;
            for (int i = 0; i < around.size(); i++) if (around.get(i) > around.get((i + 1) % around.size())) descents++;
            Check.eq("circularSorted", descents <= 1, true);

            int tail = 1 + rnd.nextInt(5), cyc = rnd.nextInt(6);
            LinkedLists.ListNode[] chain = new LinkedLists.ListNode[tail + cyc];
            for (int i = 0; i < chain.length; i++) chain[i] = new LinkedLists.ListNode(i);
            for (int i = 0; i + 1 < chain.length; i++) chain[i].next = chain[i + 1];
            if (cyc > 0) chain[chain.length - 1].next = chain[tail];
            Check.eq("cycleLength", LinkedListsVariants.cycleLength(chain[0]), cyc);
        }
        Check.eq("plusOneEx", LinkedLists.str(LinkedListsVariants.plusOne(LinkedLists.of(1, 2, 9))), "[1,3,0]");
        // flatten multilevel: 1-2-3-4-5-6, 3.child = 7-8-9-10, 8.child = 11-12
        LinkedListsVariants.DNode[] top = dn(1, 2, 3, 4, 5, 6), mid = dn(7, 8, 9, 10), low = dn(11, 12);
        top[2].child = mid[0];
        mid[1].child = low[0];
        LinkedListsVariants.DNode f = LinkedListsVariants.flatten(top[0]);
        List<Integer> flat = new ArrayList<>();
        LinkedListsVariants.DNode last = null;
        for (LinkedListsVariants.DNode n = f; n != null; n = n.next) {
            flat.add(n.val);
            Check.eq("flattenPrev", n.prev == last, true);
            Check.eq("flattenChild", n.child == null, true);
            last = n;
        }
        Check.eq("flatten", flat.toString(), "[1, 2, 3, 7, 8, 11, 12, 9, 10, 4, 5, 6]");
    }

    static LinkedListsVariants.DNode[] dn(int... v) {
        LinkedListsVariants.DNode[] a = new LinkedListsVariants.DNode[v.length];
        for (int i = 0; i < v.length; i++) a[i] = new LinkedListsVariants.DNode(v[i]);
        for (int i = 0; i + 1 < v.length; i++) { a[i].next = a[i + 1]; a[i + 1].prev = a[i]; }
        return a;
    }

    // ================================================================== bits and math
    static void bits() {
        for (int t = 0; t < 400; t++) {
            int[] a = arr(1 + rnd.nextInt(7), 0, 20);
            int sx = 0;
            for (int mask = 0; mask < 1 << a.length; mask++) { int v = 0; for (int i = 0; i < a.length; i++) if ((mask >> i & 1) != 0) v ^= a[i]; sx += v; }
            Check.eq("subsetXor", BitsMathVariants.subsetXORSum(a), sx);

            int tc = 0;
            for (int i = 0; i < a.length; i++) for (int j = i + 1; j < a.length; j++) for (int k = j; k < a.length; k++) {
                int l = 0, r = 0;
                for (int p = i; p < j; p++) l ^= a[p];
                for (int p = j; p <= k; p++) r ^= a[p];
                if (l == r) tc++;
            }
            Check.eq("triplets", BitsMathVariants.countTriplets(a), tc);

            int fa = rnd.nextInt(16), fb = rnd.nextInt(16), fc = rnd.nextInt(16), bf = Integer.MAX_VALUE;
            for (int a2 = 0; a2 < 16; a2++) for (int b2 = 0; b2 < 16; b2++) if ((a2 | b2) == fc) bf = Math.min(bf, Integer.bitCount(a2 ^ fa) + Integer.bitCount(b2 ^ fb));
            Check.eq("minFlips", BitsMathVariants.minFlips(fa, fb, fc), bf);

            int ha = 1 + rnd.nextInt(8);
            int[] hv = arr(ha, 0, 100);
            int hd = 0;
            for (int i = 0; i < ha; i++) for (int j = i + 1; j < ha; j++) hd += Integer.bitCount(hv[i] ^ hv[j]);
            Check.eq("hamming", BitsMathVariants.totalHammingDistance(hv), hd);

            int n = 1 + rnd.nextInt(1_000_000);
            int mask = (Integer.highestOneBit(n) << 1) - 1;
            Check.eq("complement", BitsMathVariants.findComplement(n), ~n & mask);

            int dv = rnd.nextInt() / (1 + rnd.nextInt(1000)), ds = (rnd.nextBoolean() ? 1 : -1) * (1 + rnd.nextInt(100000));
            Check.eq("divide", BitsMathVariants.divide(dv, ds), dv / ds);

            int sw = rnd.nextInt();
            int swWant = 0;
            for (int bit = 0; bit < 32; bit += 2) { swWant |= (sw >> bit & 1) << (bit + 1); swWant |= (sw >> (bit + 1) & 1) << bit; }
            Check.eq("swapBits", BitsMathVariants.swapOddEvenBits(sw), swWant);

            int sq = rnd.nextInt(Integer.MAX_VALUE);
            Check.eq("sqrt", BitsMathVariants.mySqrt(sq), (int) Math.sqrt(sq));
            int rs = rnd.nextInt(40000);
            Check.eq("perfectSquare", BitsMathVariants.isPerfectSquare(rs * rs), true);
            Check.eq("notPerfectSquare", BitsMathVariants.isPerfectSquare(rs * rs + 1), rs == 0);

            int base = 1 + rnd.nextInt(200);
            int[] digs = arr(1 + rnd.nextInt(4), 0, 9);
            StringBuilder sb = new StringBuilder();
            for (int d : digs) sb.append(d);
            Check.eq("superPow", BitsMathVariants.superPow(base, digs), BigInteger.valueOf(base).modPow(new BigInteger(sb.toString()), BigInteger.valueOf(1337)).intValue());

            int cn = 1 + rnd.nextInt(300), cnt = 0;
            for (int k = 1; k <= cn; k++) for (int s = 1; s * k + k * (k - 1) / 2 <= cn; s++) if (s * k + k * (k - 1) / 2 == cn) cnt++;
            Check.eq("consecutiveSum", BitsMathVariants.consecutiveNumbersSum(cn), cnt);

            int bn = rnd.nextInt(200);
            boolean[] on = new boolean[bn + 1];
            for (int round = 1; round <= bn; round++) for (int i = round; i <= bn; i += round) on[i] = !on[i];
            int bc = 0;
            for (boolean v : on) if (v) bc++;
            Check.eq("bulbs", BitsMathVariants.bulbSwitch(bn), bc);

            int jn = 1 + rnd.nextInt(30), jk = 1 + rnd.nextInt(10);
            List<Integer> circle = new ArrayList<>();
            for (int i = 1; i <= jn; i++) circle.add(i);
            int pos = 0;
            while (circle.size() > 1) { pos = (pos + jk - 1) % circle.size(); circle.remove(pos); }
            Check.eq("josephus", BitsMathVariants.findTheWinner(jn, jk), circle.get(0));

            int tn = 1 + rnd.nextInt(500), tw = 0;
            for (int i = 1; i <= tn; i++) if (BigInteger.valueOf(i).gcd(BigInteger.valueOf(tn)).equals(BigInteger.ONE)) tw++;
            Check.eq("totient", BitsMathVariants.totient(tn), tw);

            long mod = 2 + rnd.nextInt(60), av = 1 + rnd.nextInt(100);
            long inv = BitsMathVariants.modInverseAny(av, mod), bruteInv = -1;
            for (long y = 1; y < mod; y++) if (av * y % mod == 1) { bruteInv = y; break; }
            if (mod == 1) bruteInv = 0;
            Check.eq("modInverseAny", inv, bruteInv);

            long fn = rnd.nextInt(90);
            long f0 = 0, f1 = 1;
            for (long i = 0; i < fn; i++) { long nx = (f0 + f1) % 1_000_000_007L; f0 = f1; f1 = nx; }
            Check.eq("fibDoubling", BitsMathVariants.fibPair(fn, 1_000_000_007L)[0], f0);
        }
        int[] spf = BitsMathVariants.spfSieve(5000);
        for (int v = 2; v <= 5000; v++) { long prod = 1; for (int p : BitsMathVariants.factorizeWithSpf(v, spf)) prod *= p; Check.eq("spf", prod, (long) v); }
        Check.eq("bitOps3", BitsMathVariants.minimumOneBitOperations(3), 2);
        Check.eq("bitOps6", BitsMathVariants.minimumOneBitOperations(6), 4);
        Check.eq("bitOps9", BitsMathVariants.minimumOneBitOperations(9), 14);
        Check.eq("bitOps333", BitsMathVariants.minimumOneBitOperations(333), 393);
        for (int n = 0; n <= 6; n++) {
            List<Integer> g = BitsMathVariants.grayCode(n);
            Check.eq("graySize", g.size(), 1 << n);
            Check.eq("grayDistinct", new HashSet<>(g).size(), 1 << n);
            for (int i = 0; i < g.size(); i++) {
                Check.eq("grayAdjacent", Integer.bitCount(g.get(i) ^ g.get((i + 1) % g.size())) <= 1, true);
                Check.eq("grayInverse", BitsMathVariants.grayToBinary(g.get(i)), i);
            }
        }
        int[] ug = {1, 2, 3, 4, 5, 6, 8, 9, 10, 12};
        for (int i = 0; i < ug.length; i++) Check.eq("ugly", BitsMathVariants.nthUglyNumber(i + 1), ug[i]);
        Check.eq("ugly1690", BitsMathVariants.nthUglyNumber(1690), 2123366400);
        Check.eq("divideMin", BitsMathVariants.divide(Integer.MIN_VALUE, -1), Integer.MAX_VALUE);
        Check.eq("divideMin2", BitsMathVariants.divide(Integer.MIN_VALUE, 1), Integer.MIN_VALUE);
        Check.eq("sqrtMax", BitsMathVariants.mySqrt(Integer.MAX_VALUE), 46340);
        for (int n = 1; n <= 40; n++) Check.eq("vowelMatrix", BitsMathVariants.countVowelPermutationMatrix(n), DPVariants.countVowelPermutation(n));
        Check.eq("vowel5", BitsMathVariants.countVowelPermutationMatrix(5), 68);

        // randomness: coarse statistical sanity with a fixed seed (deterministic)
        int[] hits = new int[5];
        for (int trial = 0; trial < 20000; trial++) {
            BitsMathVariants.Reservoir r = new BitsMathVariants.Reservoir(trial * 7919L + 1);
            for (int v = 0; v < 5; v++) r.offer(v);
            hits[r.pick()]++;
        }
        for (int h : hits) Check.eq("reservoir ~uniform", h > 3400 && h < 4600, true);
        int[] first = new int[4];
        Random sr = new Random(5);
        for (int trial = 0; trial < 20000; trial++) {
            int[] s = {0, 1, 2, 3};
            BitsMathVariants.shuffle(s, sr);
            first[s[0]]++;
            Check.eq("shufflePerm", sorted(Arrays.stream(s).boxed().toList()).toString(), "[0, 1, 2, 3]");
        }
        for (int h : first) Check.eq("shuffle ~uniform", h > 4400 && h < 5600, true);
        BitsMathVariants.WeightedPicker wp = new BitsMathVariants.WeightedPicker(new int[]{1, 3}, 1);
        Check.eq("weighted1", wp.indexFor(1), 0);
        Check.eq("weighted2", wp.indexFor(2), 1);
        Check.eq("weighted4", wp.indexFor(4), 1);
        int[] cnt = new int[2];
        for (int i = 0; i < 20000; i++) cnt[wp.pick()]++;
        Check.eq("weightedRatio", cnt[1] > cnt[0] * 2.6 && cnt[1] < cnt[0] * 3.4, true);
    }

    // ================================================================== trees
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

    // ================================================================== dp
    static void dp() {
        Check.eq("frog1", DPVariants.canCross(new int[]{0, 1, 3, 5, 6, 8, 12, 17}), true);
        Check.eq("frog2", DPVariants.canCross(new int[]{0, 1, 2, 3, 4, 8, 9, 11}), false);
        Check.eq("stoneII1", DPVariants.stoneGameII(new int[]{2, 7, 9, 4, 4}), 10);
        Check.eq("stoneII2", DPVariants.stoneGameII(new int[]{1, 2, 3, 4, 5, 100}), 104);
        Check.eq("egg1", DPVariants.superEggDrop(1, 2), 2);
        Check.eq("egg2", DPVariants.superEggDrop(2, 6), 3);
        Check.eq("egg3", DPVariants.superEggDrop(3, 14), 4);
        Check.eq("rolls1", DPVariants.numRollsToTarget(1, 6, 3), 1);
        Check.eq("rolls2", DPVariants.numRollsToTarget(2, 6, 7), 6);
        Check.eq("rolls3", DPVariants.numRollsToTarget(30, 30, 500), 222616187);
        Check.eq("paint1", DPVariants.minCostII(new int[][]{{1, 5, 3}, {2, 9, 4}}), 5);
        Check.eq("paint2", DPVariants.minCostII(new int[][]{{1, 3}, {2, 4}}), 5);
        Check.eq("cherry1", DPVariants.cherryPickup(new int[][]{{0, 1, -1}, {1, 0, -1}, {1, 1, 1}}), 5);
        Check.eq("cherry2", DPVariants.cherryPickup(new int[][]{{1, 1, -1}, {1, -1, 1}, {-1, 1, 1}}), 0);
        Check.eq("bound1", DPVariants.findPaths(2, 2, 2, 0, 0), 6);
        Check.eq("bound2", DPVariants.findPaths(1, 3, 3, 0, 1), 12);
        int[] tiles = {1, 1, 2, 5, 11, 24, 53, 117};
        for (int n = 1; n < tiles.length; n++) Check.eq("tilings" + n, DPVariants.numTilings(n), tiles[n]);
        Check.eq("palSub1", DPVariants.countPalindromicSubsequences("bccb"), 6);
        Check.eq("palSub2", DPVariants.countPalindromicSubsequences("abcdabcdabcdabcdabcdabcdabcdabcddcbadcbadcbadcbadcbadcbadcbadcba"), 104860361);
        Check.eq("schemes1", DPVariants.profitableSchemes(5, 3, new int[]{2, 2}, new int[]{2, 3}), 2);
        Check.eq("schemes2", DPVariants.profitableSchemes(10, 5, new int[]{2, 3, 5}, new int[]{6, 7, 8}), 7);
        Check.eq("billboard1", DPVariants.tallestBillboard(new int[]{1, 2, 3, 6}), 6);
        Check.eq("billboard2", DPVariants.tallestBillboard(new int[]{1, 2, 3, 4, 5, 6}), 10);
        Check.eq("billboard3", DPVariants.tallestBillboard(new int[]{1, 2}), 0);
        Check.eq("team1", DPVariants.bestTeamScore(new int[]{1, 3, 5, 10, 15}, new int[]{1, 2, 3, 4, 5}), 34);
        Check.eq("team2", DPVariants.bestTeamScore(new int[]{4, 5, 6, 5}, new int[]{2, 1, 2, 1}), 16);
        Check.eq("team3", DPVariants.bestTeamScore(new int[]{1, 2, 3, 5}, new int[]{8, 9, 10, 1}), 6);
        Check.eq("boxes1", DPVariants.removeBoxes(new int[]{1, 3, 2, 2, 2, 3, 4, 3, 1}), 23);
        Check.eq("boxes2", DPVariants.removeBoxes(new int[]{1, 1, 1}), 9);
        Check.eq("merge1", DPVariants.mergeStones(new int[]{3, 2, 4, 1}, 2), 20);
        Check.eq("merge2", DPVariants.mergeStones(new int[]{3, 2, 4, 1}, 3), -1);
        Check.eq("merge3", DPVariants.mergeStones(new int[]{3, 5, 1, 2, 6}, 3), 25);
        Check.eq("mct1", DPVariants.mctFromLeafValues(new int[]{6, 2, 4}), 32);
        Check.eq("mct2", DPVariants.mctFromLeafValues(new int[]{4, 11}), 44);
        Check.eq("maxSum3", Check.s(DPVariants.maxSumOfThreeSubarrays(new int[]{1, 2, 1, 2, 6, 7, 5, 1}, 2)), "[0, 3, 5]");
        Check.eq("earn1", DPVariants.deleteAndEarn(new int[]{3, 4, 2}), 6);
        Check.eq("earn2", DPVariants.deleteAndEarn(new int[]{2, 2, 3, 3, 3, 4}), 9);
        Check.eq("chain1", DPVariants.longestStrChain(new String[]{"a", "b", "ba", "bca", "bda", "bdca"}), 4);
        Check.eq("chain2", DPVariants.longestStrChain(new String[]{"xbc", "pcxbcf", "xb", "cxbc", "pcxbc"}), 5);
        Check.eq("record2", DPVariants.checkRecord(2), 8);
        Check.eq("record10101", DPVariants.checkRecord(10101), 183236316);
        Check.eq("vowel5", DPVariants.countVowelPermutation(5), 68);

        for (int t = 0; t < 250; t++) {
            int[] a = arr(1 + rnd.nextInt(9), 1, 9);
            Check.eq("predict", DPVariants.predictTheWinner(a), minimax(a, 0, a.length - 1) >= 0);

            int[] piles = arr(1 + rnd.nextInt(7), 1, 9);
            Check.eq("stoneII brute", DPVariants.stoneGameII(piles), stoneBrute(piles, 0, 1));

            int eggs = 1 + rnd.nextInt(3), floors = 1 + rnd.nextInt(25);
            Check.eq("egg brute", DPVariants.superEggDrop(eggs, floors), eggBrute(eggs, floors));

            int dice = 1 + rnd.nextInt(4), faces = 1 + rnd.nextInt(6), target = rnd.nextInt(dice * faces + 2);
            Check.eq("rolls brute", DPVariants.numRollsToTarget(dice, faces, target), rollsBrute(dice, faces, target));

            int rows = 1 + rnd.nextInt(4), cols = 2 + rnd.nextInt(3);
            int[][] costs = new int[rows][cols];
            for (int[] r : costs) for (int j = 0; j < cols; j++) r[j] = 1 + rnd.nextInt(9);
            Check.eq("paint brute", DPVariants.minCostII(costs), paintBrute(costs, 0, -1));

            int gn = 1 + rnd.nextInt(4);
            int[][] grid = new int[gn][gn];
            for (int[] r : grid) for (int j = 0; j < gn; j++) r[j] = rnd.nextInt(5) == 0 ? -1 : rnd.nextInt(2);
            grid[0][0] = 0;
            grid[gn - 1][gn - 1] = 0;
            Check.eq("cherry brute", DPVariants.cherryPickup(grid), cherryBrute(grid));

            int m = 1 + rnd.nextInt(3), n = 1 + rnd.nextInt(3), mv = rnd.nextInt(5), sr = rnd.nextInt(m), sc = rnd.nextInt(n);
            Check.eq("bound brute", DPVariants.findPaths(m, n, mv, sr, sc), (int) (boundBrute(m, n, mv, sr, sc) % 1_000_000_007L));

            String ps = str(1 + rnd.nextInt(9), 3);
            Check.eq("palSub brute", DPVariants.countPalindromicSubsequences(ps), distinctPalSubseq(ps));

            int people = 1 + rnd.nextInt(6), minP = rnd.nextInt(6), crimes = 1 + rnd.nextInt(5);
            int[] gr = arr(crimes, 1, 4), pr = arr(crimes, 0, 4);
            int cnt = 0;
            for (int mask = 0; mask < 1 << crimes; mask++) { int pp = 0, prof = 0; for (int i = 0; i < crimes; i++) if ((mask >> i & 1) != 0) { pp += gr[i]; prof += pr[i]; } if (pp <= people && prof >= minP) cnt++; }
            Check.eq("schemes brute", DPVariants.profitableSchemes(people, minP, gr, pr), cnt);

            int[] rods = arr(1 + rnd.nextInt(7), 1, 8);
            int tb = 0, total = 1;
            for (int i = 0; i < rods.length; i++) total *= 3;
            for (int code = 0; code < total; code++) { int l = 0, r = 0, c = code; for (int v : rods) { int d = c % 3; c /= 3; if (d == 1) l += v; else if (d == 2) r += v; } if (l == r) tb = Math.max(tb, l); }
            Check.eq("billboard brute", DPVariants.tallestBillboard(rods), tb);

            int bn = 1 + rnd.nextInt(7);
            int[] sc2 = arr(bn, 1, 9), ag = arr(bn, 1, 5);
            int bt = 0;
            for (int mask = 0; mask < 1 << bn; mask++) {
                boolean ok = true;
                int sum = 0;
                for (int i = 0; i < bn && ok; i++) if ((mask >> i & 1) != 0) { sum += sc2[i]; for (int j = 0; j < bn; j++) if ((mask >> j & 1) != 0 && ag[i] < ag[j] && sc2[i] > sc2[j]) ok = false; }
                if (ok) bt = Math.max(bt, sum);
            }
            Check.eq("team brute", DPVariants.bestTeamScore(sc2, ag), bt);

            List<Integer> boxes = new ArrayList<>();
            for (int v : arr(1 + rnd.nextInt(6), 1, 3)) boxes.add(v);
            Check.eq("boxes brute", DPVariants.removeBoxes(boxes.stream().mapToInt(Integer::intValue).toArray()), boxesBrute(boxes));

            int sn = 1 + rnd.nextInt(7), kk = 2 + rnd.nextInt(2);
            int[] st = arr(sn, 1, 9);
            List<Integer> sl = new ArrayList<>();
            for (int v : st) sl.add(v);
            int mb = mergeBrute(sl, kk);
            Check.eq("mergeStones brute", DPVariants.mergeStones(st, kk), mb == Integer.MAX_VALUE ? -1 : mb);

            int[] leaf = arr(2 + rnd.nextInt(6), 1, 9);
            Check.eq("mct brute", DPVariants.mctFromLeafValues(leaf), mctBrute(leaf, 0, leaf.length - 1)[1]);

            String s1 = str(1 + rnd.nextInt(6), 3), s2 = str(1 + rnd.nextInt(6), 3);
            String scs = DPVariants.shortestCommonSupersequence(s1, s2);
            Check.eq("scs sub1", isSubseq(s1, scs), true);
            Check.eq("scs sub2", isSubseq(s2, scs), true);
            Check.eq("scs len", scs.length(), s1.length() + s2.length() - lcs(s1, s2));

            String pi = str(1 + rnd.nextInt(8), 3);
            Check.eq("minInsertions", DPVariants.minInsertions(pi), pi.length() - lcs(pi, new StringBuilder(pi).reverse().toString()));

            int[] three = arr(6 + rnd.nextInt(6), 0, 9);
            int k3 = 1 + rnd.nextInt(2);
            int[] w3 = new int[three.length - k3 + 1];
            for (int i = 0; i < w3.length; i++) for (int j = 0; j < k3; j++) w3[i] += three[i + j];
            int topSum = -1;
            int[] topIdx = null;
            for (int i = 0; i < w3.length; i++) for (int j = i + k3; j < w3.length; j++) for (int l = j + k3; l < w3.length; l++) if (w3[i] + w3[j] + w3[l] > topSum) { topSum = w3[i] + w3[j] + w3[l]; topIdx = new int[]{i, j, l}; }
            if (topIdx != null) Check.eq("three brute", Check.s(DPVariants.maxSumOfThreeSubarrays(three, k3)), Check.s(topIdx));

            int[] ea = arr(1 + rnd.nextInt(8), 1, 8);
            int eb = 0;
            Set<Integer> vs = new TreeSet<>();
            for (int v : ea) vs.add(v);
            List<Integer> vl = new ArrayList<>(vs);
            for (int mask = 0; mask < 1 << vl.size(); mask++) {
                boolean ok = true;
                for (int i = 0; i < vl.size() && ok; i++) for (int j = i + 1; j < vl.size(); j++) if ((mask >> i & 1) != 0 && (mask >> j & 1) != 0 && vl.get(j) - vl.get(i) == 1) ok = false;
                if (!ok) continue;
                int sum = 0;
                for (int v : ea) { int idx = vl.indexOf(v); if ((mask >> idx & 1) != 0) sum += v; }
                eb = Math.max(eb, sum);
            }
            Check.eq("earn brute", DPVariants.deleteAndEarn(ea), eb);

            int ib = 2 + rnd.nextInt(12);
            Check.eq("integerBreak", DPVariants.integerBreak(ib), breakBrute(ib));

            int[] dv = arr(1 + rnd.nextInt(8), 1, 24);
            Set<Integer> uniq = new TreeSet<>();
            for (int v : dv) uniq.add(v);
            int[] ud = uniq.stream().mapToInt(Integer::intValue).toArray();
            List<Integer> dsub = DPVariants.largestDivisibleSubset(ud.clone());
            for (int i = 0; i < dsub.size(); i++) for (int j = 0; j < dsub.size(); j++) if (i != j) Check.eq("divisible pair", dsub.get(i) % dsub.get(j) == 0 || dsub.get(j) % dsub.get(i) == 0, true);
            int bestSize = 0;
            for (int mask = 1; mask < 1 << ud.length; mask++) {
                boolean ok = true;
                for (int i = 0; i < ud.length && ok; i++) for (int j = i + 1; j < ud.length; j++) if ((mask >> i & 1) != 0 && (mask >> j & 1) != 0 && ud[j] % ud[i] != 0) ok = false;
                if (ok) bestSize = Math.max(bestSize, Integer.bitCount(mask));
            }
            Check.eq("divisible size", dsub.size(), bestSize);

            int wn = 1 + rnd.nextInt(7);
            Set<String> wset = new TreeSet<>();
            for (int i = 0; i < wn; i++) wset.add(str(1 + rnd.nextInt(4), 2));
            String[] words = wset.toArray(new String[0]);
            Check.eq("strChain", DPVariants.longestStrChain(words.clone()), chainBrute(words));

            int vn = 1 + rnd.nextInt(6);
            Check.eq("vowel brute", DPVariants.countVowelPermutation(vn), vowelBrute("", vn));

            int rn = 1 + rnd.nextInt(8);
            Check.eq("record brute", DPVariants.checkRecord(rn), recordBrute(rn, 0, 0, 0));
        }
    }

    static int minimax(int[] a, int l, int r) { return l == r ? a[l] : Math.max(a[l] - minimax(a, l + 1, r), a[r] - minimax(a, l, r - 1)); }
    static int stoneBrute(int[] p, int i, int m) {
        int n = p.length;
        if (i >= n) return 0;
        int total = 0;
        for (int k = i; k < n; k++) total += p[k];
        int best = 0;
        for (int x = 1; x <= 2 * m && i + x <= n; x++) best = Math.max(best, total - stoneBrute(p, i + x, Math.max(m, x)));
        return best;
    }
    static int eggBrute(int eggs, int floors) {
        int[][] dp = new int[eggs + 1][floors + 1];
        for (int f = 1; f <= floors; f++) dp[1][f] = f;
        for (int e = 2; e <= eggs; e++) for (int f = 1; f <= floors; f++) { dp[e][f] = Integer.MAX_VALUE; for (int x = 1; x <= f; x++) dp[e][f] = Math.min(dp[e][f], 1 + Math.max(dp[e - 1][x - 1], dp[e][f - x])); }
        return dp[eggs][floors];
    }
    static int rollsBrute(int dice, int faces, int target) { if (dice == 0) return target == 0 ? 1 : 0; int c = 0; for (int f = 1; f <= faces; f++) c += rollsBrute(dice - 1, faces, target - f); return c; }
    static int paintBrute(int[][] c, int row, int prev) { if (row == c.length) return 0; int best = Integer.MAX_VALUE; for (int j = 0; j < c[0].length; j++) if (j != prev) best = Math.min(best, c[row][j] + paintBrute(c, row + 1, j)); return best; }
    static int cherryBrute(int[][] g) { int n = g.length; return Math.max(0, cb(g, 0, 0, 0, new Integer[n][n][n])); }
    static int cb(int[][] g, int r1, int c1, int r2, Integer[][][] memo) {
        int n = g.length, c2 = r1 + c1 - r2;
        if (r1 >= n || c1 >= n || r2 >= n || c2 >= n || g[r1][c1] == -1 || g[r2][c2] == -1) return Integer.MIN_VALUE / 2;
        if (r1 == n - 1 && c1 == n - 1) return g[r1][c1];
        if (memo[r1][c1][r2] != null) return memo[r1][c1][r2];
        int gain = g[r1][c1] + (r1 == r2 && c1 == c2 ? 0 : g[r2][c2]);
        int best = Math.max(Math.max(cb(g, r1 + 1, c1, r2 + 1, memo), cb(g, r1, c1 + 1, r2, memo)), Math.max(cb(g, r1 + 1, c1, r2, memo), cb(g, r1, c1 + 1, r2 + 1, memo)));
        return memo[r1][c1][r2] = gain + best;
    }
    static long boundBrute(int m, int n, int moves, int r, int c) {
        if (r < 0 || c < 0 || r >= m || c >= n) return 1;
        if (moves == 0) return 0;
        return boundBrute(m, n, moves - 1, r + 1, c) + boundBrute(m, n, moves - 1, r - 1, c) + boundBrute(m, n, moves - 1, r, c + 1) + boundBrute(m, n, moves - 1, r, c - 1);
    }
    static int distinctPalSubseq(String s) {
        Set<String> set = new HashSet<>();
        for (int mask = 1; mask < 1 << s.length(); mask++) {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < s.length(); i++) if ((mask >> i & 1) != 0) sb.append(s.charAt(i));
            if (sb.toString().equals(sb.reverse().toString())) set.add(sb.toString());
        }
        return set.size();
    }
    static int boxesBrute(List<Integer> b) {
        if (b.isEmpty()) return 0;
        int best = 0;
        for (int i = 0; i < b.size(); ) {
            int j = i;
            while (j < b.size() && b.get(j).equals(b.get(i))) j++;
            List<Integer> rest = new ArrayList<>(b.subList(0, i));
            rest.addAll(b.subList(j, b.size()));
            best = Math.max(best, (j - i) * (j - i) + boxesBrute(rest));
            i = j;
        }
        // also allow removing a partial run: remove any contiguous equal subrun
        for (int i = 0; i < b.size(); i++) for (int j = i + 1; j <= b.size(); j++) {
            boolean same = true;
            for (int p = i; p < j; p++) if (!b.get(p).equals(b.get(i))) same = false;
            if (!same) break;
            List<Integer> rest = new ArrayList<>(b.subList(0, i));
            rest.addAll(b.subList(j, b.size()));
            best = Math.max(best, (j - i) * (j - i) + boxesBrute(rest));
        }
        return best;
    }
    static int mergeBrute(List<Integer> s, int k) {
        if (s.size() == 1) return 0;
        int best = Integer.MAX_VALUE;
        for (int i = 0; i + k <= s.size(); i++) {
            int sum = 0;
            for (int j = i; j < i + k; j++) sum += s.get(j);
            List<Integer> next = new ArrayList<>(s.subList(0, i));
            next.add(sum);
            next.addAll(s.subList(i + k, s.size()));
            int sub = mergeBrute(next, k);
            if (sub != Integer.MAX_VALUE) best = Math.min(best, sub + sum);
        }
        return best;
    }
    static int[] mctBrute(int[] a, int l, int r) { // {maxLeaf, minCost}
        if (l == r) return new int[]{a[l], 0};
        int best = Integer.MAX_VALUE, mx = 0;
        for (int i = l; i <= r; i++) mx = Math.max(mx, a[i]);
        for (int k = l; k < r; k++) { int[] L = mctBrute(a, l, k), R = mctBrute(a, k + 1, r); best = Math.min(best, L[1] + R[1] + L[0] * R[0]); }
        return new int[]{mx, best};
    }
    static boolean isSubseq(String t, String s) { int j = 0; for (int i = 0; i < s.length() && j < t.length(); i++) if (s.charAt(i) == t.charAt(j)) j++; return j == t.length(); }
    static int lcs(String a, String b) { int[][] dp = new int[a.length() + 1][b.length() + 1]; for (int i = 1; i <= a.length(); i++) for (int j = 1; j <= b.length(); j++) dp[i][j] = a.charAt(i - 1) == b.charAt(j - 1) ? dp[i - 1][j - 1] + 1 : Math.max(dp[i - 1][j], dp[i][j - 1]); return dp[a.length()][b.length()]; }
    static int breakBrute(int n) { int best = 0; for (int i = 1; i < n; i++) best = Math.max(best, Math.max(i * (n - i), i * breakBrute(n - i))); return n == 1 ? 1 : best; }
    static int chainBrute(String[] words) {
        int best = 1;
        for (String w : words) best = Math.max(best, chainFrom(w, words));
        return best;
    }
    static int chainFrom(String w, String[] words) {
        int best = 1;
        for (String p : words) if (p.length() + 1 == w.length() && isSubseq(p, w)) best = Math.max(best, 1 + chainFrom(p, words));
        return best;
    }
    static int vowelBrute(String cur, int n) {
        if (cur.length() == n) return 1;
        int c = 0;
        for (char v : "aeiou".toCharArray()) {
            if (!cur.isEmpty()) {
                char p = cur.charAt(cur.length() - 1);
                boolean ok = (p == 'a' && v == 'e') || (p == 'e' && (v == 'a' || v == 'i')) || (p == 'i' && v != 'i') || (p == 'o' && (v == 'i' || v == 'u')) || (p == 'u' && v == 'a');
                if (!ok) continue;
            }
            c += vowelBrute(cur + v, n);
        }
        return c;
    }
    static int recordBrute(int n, int pos, int absent, int lates) {
        if (pos == n) return 1;
        int c = recordBrute(n, pos + 1, absent, 0);
        if (absent == 0) c += recordBrute(n, pos + 1, 1, 0);
        if (lates < 2) c += recordBrute(n, pos + 1, absent, lates + 1);
        return c;
    }
}
