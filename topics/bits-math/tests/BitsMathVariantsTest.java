import java.math.BigInteger;
import java.util.*;

public class BitsMathVariantsTest {
    public static void main(String[] x) {
        bits();
        // matMul / matPow / extGcd known inputs
        Check.eq("matMul", Arrays.deepToString(BitsMathVariants.matMul(new long[][]{{1, 2}, {3, 4}}, new long[][]{{5, 6}, {7, 8}}, 1_000_000_007L)), "[[19, 22], [43, 50]]");
        Check.eq("matMul mod", Arrays.deepToString(BitsMathVariants.matMul(new long[][]{{1, 2}, {3, 4}}, new long[][]{{5, 6}, {7, 8}}, 10)), "[[9, 2], [3, 0]]");
        Check.eq("matPow fib10", Arrays.deepToString(BitsMathVariants.matPow(new long[][]{{1, 1}, {1, 0}}, 10, 1_000_000_007L)), "[[89, 55], [55, 34]]");
        Check.eq("matPow e=0 identity", Arrays.deepToString(BitsMathVariants.matPow(new long[][]{{1, 1}, {1, 0}}, 0, 1_000_000_007L)), "[[1, 0], [0, 1]]");
        Check.eq("matPow fib90 mod", BitsMathVariants.matPow(new long[][]{{1, 1}, {1, 0}}, 90, Long.MAX_VALUE >> 32)[0][1], 2880067194370816120L % (Long.MAX_VALUE >> 32));
        long[] eg = BitsMathVariants.extGcd(240, 46);
        Check.eq("extGcd gcd", eg[0], 2);
        Check.eq("extGcd bezout", 240 * eg[1] + 46 * eg[2], 2);
        long[] eg2 = BitsMathVariants.extGcd(17, 5);
        Check.eq("extGcd coprime", eg2[0] + "," + (17 * eg2[1] + 5 * eg2[2]), "1,1");
        Check.done("BitsMathVariantsTest");
    }

    static Random rnd = new Random(31415926);
    static int[] arr(int n, int lo, int hi) { int[] a = new int[n]; for (int i = 0; i < n; i++) a[i] = lo + rnd.nextInt(hi - lo + 1); return a; }
    static List<Integer> sorted(Collection<Integer> c) { List<Integer> l = new ArrayList<>(c); Collections.sort(l); return l; }
    static List<Integer> toList(LinkedLists.ListNode n) { List<Integer> l = new ArrayList<>(); for (; n != null; n = n.next) l.add(n.val); return l; }
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
}
