import java.util.*;

public class BitsMathTest {
    public static void main(String[] x) {
        // ---------- bits / math ----------
        Check.eq("single", BitsMath.singleNumber(new int[]{4, 1, 2, 1, 2}), 4);
        Check.eq("single2", BitsMath.singleNumberII(new int[]{2, 2, 3, 2}), 3);
        int[] s3 = BitsMath.singleNumberIII(new int[]{1, 2, 1, 3, 2, 5});
        Arrays.sort(s3);
        Check.eq("single3", Check.s(s3), "[3, 5]");
        Check.eq("hamming", BitsMath.hammingWeight(11) + "," + BitsMath.hammingWeight(-1), "3,32");
        Check.eq("countBits", Check.s(BitsMath.countBits(5)), "[0, 1, 1, 2, 1, 2]");
        Check.eq("pow2", BitsMath.isPowerOfTwo(16) + "," + BitsMath.isPowerOfTwo(6) + "," + BitsMath.isPowerOfFour(16) + "," + BitsMath.isPowerOfFour(8), "true,false,true,false");
        Check.eq("reverseBits", BitsMath.reverseBits(43261596), 964176192);
        Check.eq("missing", BitsMath.missingNumber(new int[]{3, 0, 1}), 2);
        Check.eq("getSum", BitsMath.getSum(5, -3), 2);
        Check.eq("rangeAnd", BitsMath.rangeBitwiseAnd(5, 7), 4);
        Check.eq("wordLen", BitsMath.maxProduct(new String[]{"abcw", "baz", "foo", "bar", "xtfn", "abcdef"}), 16);
        Check.eq("subsetsMask", BitsMath.subsetsMask(new int[]{1, 2, 3}).size(), 8);
        Check.eq("gcd", BitsMath.gcd(48, 18) + "," + BitsMath.lcm(4, 6), "6,12");
        Check.eq("powMod", BitsMath.powMod(2, 10, 1000), 24);
        Check.eq("myPow", BitsMath.myPow(2.0, -2), 0.25);
        Check.eq("primes", BitsMath.countPrimes(10) + "," + BitsMath.countPrimes(100), "4,25");
        Check.eq("factorize", BitsMath.factorize(360), "[2, 2, 2, 3, 3, 5]");
        BitsMath.Combinatorics cb = new BitsMath.Combinatorics(100, 1_000_000_007L);
        Check.eq("nCr", cb.nCr(10, 3) + "," + cb.nCr(5, 7), "120,0");
        Check.eq("reverseInt", BitsMath.reverseInteger(123) + "," + BitsMath.reverseInteger(-123) + "," + BitsMath.reverseInteger(1534236469), "321,-321,0");
        Check.eq("excel", BitsMath.columnTitle(701) + "," + BitsMath.columnTitle(28), "ZY,AB");
        Check.eq("fraction", BitsMath.fractionToDecimal(1, 2) + "," + BitsMath.fractionToDecimal(2, 3) + "," + BitsMath.fractionToDecimal(4, 333), "0.5,0.(6),0.(012)");
        Check.eq("happy", BitsMath.isHappy(19) + "," + BitsMath.isHappy(2), "true,false");

        // ---------- randomized cross-checks ----------
        Random r = new Random(9);
        for (int t = 0; t < 300; t++) {
            int n = r.nextInt(1 << 20), m = r.nextInt(1 << 20);
            Check.eq("popcount", BitsMath.hammingWeight(n), Integer.bitCount(n));
            Check.eq("gcdRand", BitsMath.gcd(n, m), java.math.BigInteger.valueOf(n).gcd(java.math.BigInteger.valueOf(m)).longValue());
            Check.eq("sumRand", BitsMath.getSum(n - 500000, m - 500000), (n - 500000) + (m - 500000));
            Check.eq("powModRand", BitsMath.powMod(n, 5, 1_000_007L), java.math.BigInteger.valueOf(n).modPow(java.math.BigInteger.valueOf(5), java.math.BigInteger.valueOf(1_000_007L)).longValue());
        }
        // modInverse (prime modulus, Fermat)
        Check.eq("modInverse 3 mod 7", BitsMath.modInverse(3, 7), 5);
        Check.eq("modInverse 2 mod 1e9+7", BitsMath.modInverse(2, 1_000_000_007L), 500000004);
        for (long a : new long[]{1, 2, 123456, 999999999}) Check.eq("modInverse product " + a, a * BitsMath.modInverse(a, 1_000_000_007L) % 1_000_000_007L, 1);
        Check.done("BitsMathTest");
    }

}
