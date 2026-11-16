import java.util.*;

public class BitsMathVariants {

    // ---------- Sum of All Subset XOR Totals (LC 1863): every bit set in the OR of all elements appears in half the subsets ----------
    static int subsetXORSum(int[] a) {
        int or = 0;
        for (int x : a) or |= x;
        return or << (a.length - 1);
    }

    // ---------- Count Triplets That Can Form Two Arrays of Equal XOR (LC 1442): a == b  <=>  xor(i..k) == 0 ----------
    static int countTriplets(int[] a) {
        int count = 0;
        for (int i = 0; i < a.length; i++) {
            int x = 0;
            for (int k = i; k < a.length; k++) {
                x ^= a[k];
                if (x == 0 && k > i) count += k - i; // any split j in (i, k] works
            }
        }
        return count;
    }

    // ---------- Minimum Flips to Make a OR b Equal to c (LC 1318): decide bit by bit ----------
    static int minFlips(int a, int b, int c) {
        int flips = 0;
        for (int bit = 0; bit < 31; bit++) {
            int x = a >> bit & 1, y = b >> bit & 1, z = c >> bit & 1;
            if (z == 1) { if ((x | y) == 0) flips++; }
            else flips += x + y;
        }
        return flips;
    }

    // ---------- Gray code (LC 89) and its inverse ----------
    static List<Integer> grayCode(int n) {
        List<Integer> res = new ArrayList<>();
        for (int i = 0; i < 1 << n; i++) res.add(i ^ (i >> 1));
        return res;
    }

    static int grayToBinary(int g) {
        int b = 0;
        for (; g != 0; g >>= 1) b ^= g;
        return b;
    }

    // ---------- Minimum One Bit Operations to Make Integers Zero (LC 1611): it is grayToBinary ----------
    static int minimumOneBitOperations(int n) { return grayToBinary(n); }

    // ---------- Total Hamming Distance (LC 477): per bit, ones * zeros ----------
    static int totalHammingDistance(int[] a) {
        int total = 0;
        for (int bit = 0; bit < 32; bit++) {
            int ones = 0;
            for (int x : a) ones += x >> bit & 1;
            total += ones * (a.length - ones);
        }
        return total;
    }

    // ---------- Number Complement (LC 476): flip only the bits below the highest set bit ----------
    static int findComplement(int n) {
        int mask = n;
        mask |= mask >> 1; mask |= mask >> 2; mask |= mask >> 4; mask |= mask >> 8; mask |= mask >> 16; // fill ones under the top bit
        return ~n & mask;
    }

    // ---------- Divide Two Integers (LC 29): subtract shifted divisors, no * / % ----------
    static int divide(int dividend, int divisor) {
        if (dividend == Integer.MIN_VALUE && divisor == -1) return Integer.MAX_VALUE;
        boolean neg = (dividend < 0) ^ (divisor < 0);
        long a = Math.abs((long) dividend), b = Math.abs((long) divisor), q = 0;
        for (int shift = 31; shift >= 0; shift--) {
            if ((a >> shift) >= b) { a -= b << shift; q += 1L << shift; }
        }
        return (int) (neg ? -q : q);
    }

    // ---------- Swap odd and even bits ----------
    static int swapOddEvenBits(int n) { return ((n & 0x55555555) << 1) | ((n >>> 1) & 0x55555555); }

    // ---------- Sqrt(x) (LC 69) and Valid Perfect Square (LC 367): binary search on the answer, long to avoid overflow ----------
    static int mySqrt(int x) {
        long lo = 0, hi = x;
        while (lo < hi) {
            long mid = (lo + hi + 1) / 2;
            if (mid * mid <= x) lo = mid; else hi = mid - 1;
        }
        return (int) lo;
    }

    static boolean isPerfectSquare(int x) { long r = mySqrt(x); return r * r == x; }

    // ---------- Super Pow (LC 372): a^[b0 b1 ... bk] mod 1337, one digit at a time ----------
    static int superPow(int a, int[] b) {
        final int MOD = 1337;
        int result = 1;
        a %= MOD;
        for (int d : b) result = (int) (powMod(result, 10, MOD) * powMod(a, d, MOD) % MOD);
        return result;
    }

    static long powMod(long base, long exp, long mod) {
        long r = 1;
        base %= mod;
        for (; exp > 0; exp >>= 1, base = base * base % mod) if ((exp & 1) == 1) r = r * base % mod;
        return r;
    }

    // ---------- Ugly Number II (LC 264): three pointers merge the sequences x2, x3, x5 ----------
    static int nthUglyNumber(int n) {
        int[] u = new int[n];
        u[0] = 1;
        int i2 = 0, i3 = 0, i5 = 0;
        for (int i = 1; i < n; i++) {
            int next = Math.min(u[i2] * 2, Math.min(u[i3] * 3, u[i5] * 5));
            u[i] = next;
            if (next == u[i2] * 2) i2++;
            if (next == u[i3] * 3) i3++; // separate ifs, not else-if: this skips duplicates like 6 = 2*3 = 3*2
            if (next == u[i5] * 5) i5++;
        }
        return u[n - 1];
    }

    // ---------- Consecutive Numbers Sum (LC 829): n = k*x + k(k-1)/2 for k terms; count k with integer x >= 1 ----------
    static int consecutiveNumbersSum(int n) {
        int count = 0;
        for (long k = 1; k * (k + 1) / 2 <= n; k++) if ((n - k * (k - 1) / 2) % k == 0) count++;
        return count;
    }

    // ---------- Bulb Switcher (LC 319): bulb i ends on iff i has an odd number of divisors, i.e. is a perfect square ----------
    static int bulbSwitch(int n) { return (int) Math.sqrt(n); }

    // ---------- Josephus / Find the Winner of the Circular Game (LC 1823): f(n) = (f(n-1) + k) mod n ----------
    static int findTheWinner(int n, int k) {
        int winner = 0;
        for (int i = 2; i <= n; i++) winner = (winner + k) % i;
        return winner + 1;
    }

    // ---------- Euler's totient via factorization ----------
    static int totient(int n) {
        int result = n;
        for (int p = 2; (long) p * p <= n; p++) {
            if (n % p == 0) { while (n % p == 0) n /= p; result -= result / p; }
        }
        if (n > 1) result -= result / n;
        return result;
    }

    // ---------- Smallest-prime-factor sieve: factorize in O(log n) after O(n log log n) setup ----------
    static int[] spfSieve(int n) {
        int[] spf = new int[n + 1];
        for (int i = 2; i <= n; i++) if (spf[i] == 0) for (int j = i; j <= n; j += i) if (spf[j] == 0) spf[j] = i;
        return spf;
    }

    static List<Integer> factorizeWithSpf(int x, int[] spf) {
        List<Integer> f = new ArrayList<>();
        while (x > 1) { f.add(spf[x]); x /= spf[x]; }
        return f;
    }

    // ---------- Extended Euclid and modular inverse for ANY coprime modulus (Fermat only works for prime moduli) ----------
    static long[] extGcd(long a, long b) { // returns {g, x, y} with a*x + b*y = g
        if (b == 0) return new long[]{a, 1, 0};
        long[] r = extGcd(b, a % b);
        return new long[]{r[0], r[2], r[1] - (a / b) * r[2]};
    }

    static long modInverseAny(long a, long m) {
        long[] r = extGcd(((a % m) + m) % m, m);
        return r[0] != 1 ? -1 : ((r[1] % m) + m) % m;
    }

    // ---------- Fibonacci by fast doubling: F(2k) = F(k) * (2F(k+1) - F(k)), F(2k+1) = F(k)^2 + F(k+1)^2 ----------
    static long[] fibPair(long n, long mod) { // returns {F(n), F(n+1)} mod `mod`
        if (n == 0) return new long[]{0, 1 % mod};
        long[] h = fibPair(n / 2, mod);
        long c = h[0] * ((2 * h[1] % mod - h[0] + mod) % mod) % mod;
        long d = (h[0] * h[0] + h[1] * h[1]) % mod;
        return n % 2 == 0 ? new long[]{c, d} : new long[]{d, (c + d) % mod};
    }

    // ---------- Matrix exponentiation: linear recurrence in O(k^3 log n) ----------
    static long[][] matMul(long[][] a, long[][] b, long mod) {
        int n = a.length;
        long[][] c = new long[n][n];
        for (int i = 0; i < n; i++) for (int k = 0; k < n; k++) if (a[i][k] != 0) for (int j = 0; j < n; j++) c[i][j] = (c[i][j] + a[i][k] * b[k][j]) % mod;
        return c;
    }

    static long[][] matPow(long[][] m, long e, long mod) {
        int n = m.length;
        long[][] r = new long[n][n];
        for (int i = 0; i < n; i++) r[i][i] = 1;
        for (; e > 0; e >>= 1, m = matMul(m, m, mod)) if ((e & 1) == 1) r = matMul(r, m, mod);
        return r;
    }

    // ---------- Count Vowels Permutation, the matrix-power version of the DP (LC 1220) ----------
    static int countVowelPermutationMatrix(int n) {
        final long MOD = 1_000_000_007L;
        // a->e, e->a|i, i->a|e|o|u, o->i|u, u->a  (columns = from, rows = to)
        long[][] t = {
                {0, 1, 1, 0, 1},
                {1, 0, 1, 0, 0},
                {0, 1, 0, 1, 0},
                {0, 0, 1, 0, 0},
                {0, 0, 1, 1, 0}};
        long[][] p = matPow(t, n - 1, MOD);
        long total = 0;
        for (long[] row : p) for (long v : row) total = (total + v) % MOD;
        return (int) total;
    }

    // ---------- Reservoir sampling (uniform pick from a stream of unknown length) ----------
    static class Reservoir {
        private final Random rnd;
        private int chosen, seen;
        Reservoir(long seed) { rnd = new Random(seed); }
        void offer(int x) { if (rnd.nextInt(++seen) == 0) chosen = x; } // replace with probability 1/seen
        int pick() { return chosen; }
    }

    // ---------- Fisher-Yates shuffle (uniform): swap each position with a random one at or after it ----------
    static void shuffle(int[] a, Random rnd) {
        for (int i = 0; i < a.length - 1; i++) {
            int j = i + rnd.nextInt(a.length - i);
            int t = a[i]; a[i] = a[j]; a[j] = t;
        }
    }

    // ---------- Random Pick with Weight (LC 528): prefix sums + binary search ----------
    static class WeightedPicker {
        private final long[] prefix;
        private final Random rnd;
        WeightedPicker(int[] w, long seed) {
            prefix = new long[w.length];
            long s = 0;
            for (int i = 0; i < w.length; i++) { s += w[i]; prefix[i] = s; }
            rnd = new Random(seed);
        }
        int pick() { return indexFor(1 + (long) (rnd.nextDouble() * prefix[prefix.length - 1])); }
        int indexFor(long target) { // first prefix >= target, target in [1, total]
            int lo = 0, hi = prefix.length - 1;
            while (lo < hi) { int mid = (lo + hi) >>> 1; if (prefix[mid] >= target) hi = mid; else lo = mid + 1; }
            return lo;
        }
    }
}
