import java.util.*;

public class BitsMath {

    // ===== BIT CHEAT SHEET =====
    //   x & (x - 1)   clears the lowest set bit        x & -x   isolates the lowest set bit
    //   x ^ x = 0, x ^ 0 = x                           (x >> k) & 1   reads bit k
    //   x | (1 << k) sets   x & ~(1 << k) clears   x ^ (1 << k) toggles
    //   use >>> for logical shift of negatives; use long literals 1L << k above bit 30

    // Single Number (LC 136): xor cancels pairs
    static int singleNumber(int[] a) { int x = 0; for (int v : a) x ^= v; return x; }

    // Single Number II (LC 137): count each bit mod 3
    static int singleNumberII(int[] a) {
        int ones = 0, twos = 0;
        for (int x : a) { ones = (ones ^ x) & ~twos; twos = (twos ^ x) & ~ones; }
        return ones;
    }

    // Single Number III (LC 260): split by the lowest set bit of xor of the two uniques
    static int[] singleNumberIII(int[] a) {
        int x = 0;
        for (int v : a) x ^= v;
        int low = x & -x, p = 0;
        for (int v : a) if ((v & low) != 0) p ^= v;
        return new int[]{p, x ^ p};
    }

    // Number of 1 Bits (LC 191)
    static int hammingWeight(int n) { int c = 0; while (n != 0) { n &= n - 1; c++; } return c; }

    // Counting Bits (LC 338): dp[i] = dp[i >> 1] + (i & 1)
    static int[] countBits(int n) {
        int[] d = new int[n + 1];
        for (int i = 1; i <= n; i++) d[i] = d[i >> 1] + (i & 1);
        return d;
    }

    // Power of Two / Four
    static boolean isPowerOfTwo(int n) { return n > 0 && (n & (n - 1)) == 0; }
    static boolean isPowerOfFour(int n) { return isPowerOfTwo(n) && (n & 0x55555555) != 0; }

    // Reverse Bits (LC 190)
    static int reverseBits(int n) {
        int r = 0;
        for (int i = 0; i < 32; i++) { r = (r << 1) | (n & 1); n >>>= 1; }
        return r;
    }

    // Missing Number (LC 268)
    static int missingNumber(int[] a) {
        int x = a.length;
        for (int i = 0; i < a.length; i++) x ^= i ^ a[i];
        return x;
    }

    // Sum of Two Integers without + (LC 371)
    static int getSum(int a, int b) {
        while (b != 0) { int carry = (a & b) << 1; a ^= b; b = carry; }
        return a;
    }

    // Bitwise AND of Range (LC 201): common prefix of m and n
    static int rangeBitwiseAnd(int m, int n) {
        int shift = 0;
        while (m != n) { m >>= 1; n >>= 1; shift++; }
        return m << shift;
    }

    // Maximum Product of Word Lengths (LC 318): letters as bitmask
    static int maxProduct(String[] words) {
        int n = words.length, best = 0;
        int[] mask = new int[n];
        for (int i = 0; i < n; i++) for (char c : words[i].toCharArray()) mask[i] |= 1 << (c - 'a');
        for (int i = 0; i < n; i++)
            for (int j = i + 1; j < n; j++)
                if ((mask[i] & mask[j]) == 0) best = Math.max(best, words[i].length() * words[j].length());
        return best;
    }

    // Subsets via bitmask enumeration
    static List<List<Integer>> subsetsMask(int[] a) {
        List<List<Integer>> res = new ArrayList<>();
        for (int m = 0; m < 1 << a.length; m++) {
            List<Integer> s = new ArrayList<>();
            for (int i = 0; i < a.length; i++) if ((m >> i & 1) != 0) s.add(a[i]);
            res.add(s);
        }
        return res;
    }

    // ===== NUMBER THEORY =====
    static long gcd(long a, long b) { return b == 0 ? a : gcd(b, a % b); }
    static long lcm(long a, long b) { return a / gcd(a, b) * b; }

    // Fast power mod (LC 50 for doubles, here modular)
    static long powMod(long b, long e, long m) {
        long r = 1;
        b %= m;
        while (e > 0) { if ((e & 1) == 1) r = r * b % m; b = b * b % m; e >>= 1; }
        return r;
    }

    static double myPow(double x, long n) {
        if (n < 0) { x = 1 / x; n = -n; }
        double r = 1;
        while (n > 0) { if ((n & 1) == 1) r *= x; x *= x; n >>= 1; }
        return r;
    }

    // Sieve of Eratosthenes (LC 204): count primes < n
    static int countPrimes(int n) {
        if (n < 3) return 0;
        boolean[] comp = new boolean[n];
        int count = 0;
        for (int i = 2; i < n; i++) {
            if (comp[i]) continue;
            count++;
            for (long j = (long) i * i; j < n; j += i) comp[(int) j] = true;
        }
        return count;
    }

    // Prime factorization by trial division, O(sqrt n)
    static List<Long> factorize(long n) {
        List<Long> f = new ArrayList<>();
        for (long p = 2; p * p <= n; p++) while (n % p == 0) { f.add(p); n /= p; }
        if (n > 1) f.add(n);
        return f;
    }

    // Modular inverse via Fermat (m prime) and nCr mod prime with factorial tables
    static long modInverse(long a, long p) { return powMod(a, p - 2, p); }

    static class Combinatorics {
        final long MOD;
        final long[] fact, inv;
        Combinatorics(int n, long mod) {
            MOD = mod;
            fact = new long[n + 1];
            inv = new long[n + 1];
            fact[0] = 1;
            for (int i = 1; i <= n; i++) fact[i] = fact[i - 1] * i % mod;
            inv[n] = modInverse(fact[n], mod);
            for (int i = n; i > 0; i--) inv[i - 1] = inv[i] * i % mod;
        }
        long nCr(int n, int r) { return r < 0 || r > n ? 0 : fact[n] * inv[r] % MOD * inv[n - r] % MOD; }
    }

    // Integer overflow guards: use long for products, Math.multiplyExact to detect, and compare via division.
    // Reverse Integer (LC 7)
    static int reverseInteger(int x) {
        int r = 0;
        while (x != 0) {
            int d = x % 10;
            if (r > Integer.MAX_VALUE / 10 || (r == Integer.MAX_VALUE / 10 && d > 7)) return 0;
            if (r < Integer.MIN_VALUE / 10 || (r == Integer.MIN_VALUE / 10 && d < -8)) return 0;
            r = r * 10 + d;
            x /= 10;
        }
        return r;
    }

    // Excel Sheet Column Number / Title (LC 168): bijective base 26
    static String columnTitle(int n) {
        StringBuilder sb = new StringBuilder();
        while (n > 0) { n--; sb.append((char) ('A' + n % 26)); n /= 26; }
        return sb.reverse().toString();
    }

    // Fraction to Recurring Decimal (LC 166): remainder -> position map detects the cycle
    static String fractionToDecimal(long num, long den) {
        if (num == 0) return "0";
        StringBuilder sb = new StringBuilder();
        if ((num < 0) ^ (den < 0)) sb.append('-');
        num = Math.abs(num);
        den = Math.abs(den);
        sb.append(num / den);
        long rem = num % den;
        if (rem == 0) return sb.toString();
        sb.append('.');
        Map<Long, Integer> pos = new HashMap<>();
        while (rem != 0) {
            if (pos.containsKey(rem)) { sb.insert(pos.get(rem), "(").append(')'); break; }
            pos.put(rem, sb.length());
            rem *= 10;
            sb.append(rem / den);
            rem %= den;
        }
        return sb.toString();
    }

    // Happy Number (LC 202): Floyd on digit-square-sum
    static boolean isHappy(int n) {
        int slow = n, fast = next(n);
        while (fast != 1 && slow != fast) { slow = next(slow); fast = next(next(fast)); }
        return fast == 1;
    }

    private static int next(int n) { int s = 0; while (n > 0) { int d = n % 10; s += d * d; n /= 10; } return s; }

    // Pow(x, n) with BigInteger-free big arithmetic is in StringAlgorithms (multiply / addStrings).
}
