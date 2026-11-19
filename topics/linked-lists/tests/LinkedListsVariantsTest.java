import java.math.BigInteger;
import java.util.*;

public class LinkedListsVariantsTest {
    public static void main(String[] x) {
        lists();
        Check.done("LinkedListsVariantsTest");
    }

    static Random rnd = new Random(31415926);
    static int[] arr(int n, int lo, int hi) { int[] a = new int[n]; for (int i = 0; i < n; i++) a[i] = lo + rnd.nextInt(hi - lo + 1); return a; }
    static String str(int n, int alpha) { StringBuilder sb = new StringBuilder(); for (int i = 0; i < n; i++) sb.append((char) ('a' + rnd.nextInt(alpha))); return sb.toString(); }
    static List<Integer> sorted(Collection<Integer> c) { List<Integer> l = new ArrayList<>(c); Collections.sort(l); return l; }
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
    static int chain(BinaryTree.TreeNode n, int v) { return n == null || n.val != v ? 0 : 1 + Math.max(chain(n.left, v), chain(n.right, v)); }
}
