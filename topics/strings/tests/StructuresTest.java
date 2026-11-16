import java.util.*;

// Covers linked-lists, strings, bits-math, design.
public class StructuresTest {
    public static void main(String[] x) {
        // ---------- linked lists ----------
        Check.eq("reverse", LinkedLists.str(LinkedLists.reverse(LinkedLists.of(1, 2, 3, 4, 5))), "[5,4,3,2,1]");
        Check.eq("reverseBetween", LinkedLists.str(LinkedLists.reverseBetween(LinkedLists.of(1, 2, 3, 4, 5), 2, 4)), "[1,4,3,2,5]");
        Check.eq("reverseK", LinkedLists.str(LinkedLists.reverseKGroup(LinkedLists.of(1, 2, 3, 4, 5), 2)), "[2,1,4,3,5]");
        Check.eq("swapPairs", LinkedLists.str(LinkedLists.swapPairs(LinkedLists.of(1, 2, 3, 4))), "[2,1,4,3]");
        Check.eq("middle", LinkedLists.middle(LinkedLists.of(1, 2, 3, 4, 5)).val + "," + LinkedLists.middle(LinkedLists.of(1, 2, 3, 4)).val, "3,3");
        LinkedLists.ListNode cyc = LinkedLists.of(3, 2, 0, -4);
        LinkedLists.ListNode tail = cyc;
        while (tail.next != null) tail = tail.next;
        tail.next = cyc.next;
        Check.eq("cycle", LinkedLists.detectCycle(cyc).val, 2);
        Check.eq("noCycle", LinkedLists.detectCycle(LinkedLists.of(1, 2)) == null, true);
        Check.eq("removeNth", LinkedLists.str(LinkedLists.removeNthFromEnd(LinkedLists.of(1, 2, 3, 4, 5), 2)), "[1,2,3,5]");
        Check.eq("removeNthHead", LinkedLists.str(LinkedLists.removeNthFromEnd(LinkedLists.of(1), 1)), "[]");
        Check.eq("mergeTwo", LinkedLists.str(LinkedLists.mergeTwo(LinkedLists.of(1, 2, 4), LinkedLists.of(1, 3, 4))), "[1,1,2,3,4,4]");
        Check.eq("mergeK", LinkedLists.str(LinkedLists.mergeKLists(new LinkedLists.ListNode[]{LinkedLists.of(1, 4, 5), LinkedLists.of(1, 3, 4), LinkedLists.of(2, 6)})), "[1,1,2,3,4,4,5,6]");
        Check.eq("sortList", LinkedLists.str(LinkedLists.sortList(LinkedLists.of(4, 2, 1, 3))), "[1,2,3,4]");
        LinkedLists.ListNode ro = LinkedLists.of(1, 2, 3, 4, 5);
        LinkedLists.reorderList(ro);
        Check.eq("reorder", LinkedLists.str(ro), "[1,5,2,4,3]");
        Check.eq("palList", LinkedLists.isPalindrome(LinkedLists.of(1, 2, 2, 1)) + "," + LinkedLists.isPalindrome(LinkedLists.of(1, 2)), "true,false");
        Check.eq("addNumbers", LinkedLists.str(LinkedLists.addTwoNumbers(LinkedLists.of(2, 4, 3), LinkedLists.of(5, 6, 4))), "[7,0,8]");
        LinkedLists.ListNode shared = LinkedLists.of(8, 4, 5), a = LinkedLists.of(4, 1), b = LinkedLists.of(5, 6, 1);
        a.next.next = shared;
        b.next.next.next = shared;
        Check.eq("intersect", LinkedLists.getIntersectionNode(a, b).val, 8);
        Check.eq("dedupe2", LinkedLists.str(LinkedLists.deleteDuplicates(LinkedLists.of(1, 2, 3, 3, 4, 4, 5))), "[1,2,5]");
        Check.eq("partition", LinkedLists.str(LinkedLists.partition(LinkedLists.of(1, 4, 3, 2, 5, 2), 3)), "[1,2,2,4,3,5]");
        Check.eq("rotateList", LinkedLists.str(LinkedLists.rotateRight(LinkedLists.of(1, 2, 3, 4, 5), 2)), "[4,5,1,2,3]");
        Check.eq("oddEven", LinkedLists.str(LinkedLists.oddEvenList(LinkedLists.of(1, 2, 3, 4, 5))), "[1,3,5,2,4]");
        LinkedLists.RNode r1 = new LinkedLists.RNode(1), r2 = new LinkedLists.RNode(2);
        r1.next = r2; r1.random = r2; r2.random = r2;
        LinkedLists.RNode cp = LinkedLists.copyRandomList(r1);
        Check.eq("copyRandom", (cp != r1) + "," + (cp.random == cp.next) + "," + (cp.next.random == cp.next) + "," + (r1.next == r2), "true,true,true,true");

        // ---------- strings ----------
        Check.eq("kmp", StringAlgorithms.kmpSearch("sadbutsad", "sad") + "," + StringAlgorithms.kmpSearch("leetcode", "leeto"), "0,-1");
        Check.eq("period", StringAlgorithms.repeatedSubstringPattern("abab") + "," + StringAlgorithms.repeatedSubstringPattern("aba"), "true,false");
        Check.eq("shortPal", StringAlgorithms.shortestPalindrome("aacecaaa") + "," + StringAlgorithms.shortestPalindrome("abcd"), "aaacecaaa,dcbabcd");
        Check.eq("z", Check.s(StringAlgorithms.zFunction("aabxaab")), "[0, 1, 0, 0, 3, 1, 0]");
        Check.eq("zAll", StringAlgorithms.findAllZ("abababa", "aba"), "[0, 2, 4]");
        Check.eq("rk", StringAlgorithms.rabinKarp("abababa", "aba"), "[0, 2, 4]");
        Check.eq("manacher", StringAlgorithms.manacher("babad").length() + "," + StringAlgorithms.manacher("cbbd"), "3,bb");
        Check.eq("anagram", StringAlgorithms.isAnagram("anagram", "nagaram") + "," + StringAlgorithms.isAnagram("rat", "car"), "true,false");
        Check.eq("isomorphic", StringAlgorithms.isIsomorphic("egg", "add") + "," + StringAlgorithms.isIsomorphic("foo", "bar"), "true,false");
        Check.eq("lcp", StringAlgorithms.longestCommonPrefix(new String[]{"flower", "flow", "flight"}), "fl");
        char[] cc = "aabbccc".toCharArray();
        Check.eq("compress", StringAlgorithms.compress(cc) + "," + new String(cc, 0, 6), "6,a2b2c3");
        Check.eq("reverseWords", StringAlgorithms.reverseWords("  the sky  is blue "), "blue is sky the");
        Check.eq("multiply", StringAlgorithms.multiply("123", "456"), "56088");
        Check.eq("addStrings", StringAlgorithms.addStrings("456", "77"), "533");
        Check.eq("atoi", StringAlgorithms.myAtoi("  -42") + "," + StringAlgorithms.myAtoi("4193 with words") + "," + StringAlgorithms.myAtoi("-91283472332"), "-42,4193,-2147483648");
        Check.eq("roman", StringAlgorithms.romanToInt("MCMXCIV"), 1994);
        Check.eq("minRot", StringAlgorithms.minRotation("cba"), "acb");

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

        // ---------- design ----------
        DesignStructures.LRUCache lru = new DesignStructures.LRUCache(2);
        lru.put(1, 1); lru.put(2, 2);
        int g1 = lru.get(1);
        lru.put(3, 3);
        int g2 = lru.get(2);
        lru.put(4, 4);
        Check.eq("lru", g1 + "," + g2 + "," + lru.get(1) + "," + lru.get(3) + "," + lru.get(4), "1,-1,-1,3,4");
        DesignStructures.LRUCacheLHM lhm = new DesignStructures.LRUCacheLHM(2);
        lhm.put(1, 1); lhm.put(2, 2); lhm.get(1); lhm.put(3, 3);
        Check.eq("lruLHM", lhm.containsKey(2) + "," + lhm.containsKey(1), "false,true");
        DesignStructures.LFUCache lfu = new DesignStructures.LFUCache(2);
        lfu.put(1, 1); lfu.put(2, 2);
        int f1 = lfu.get(1);
        lfu.put(3, 3);
        int f2 = lfu.get(2), f3 = lfu.get(3);
        lfu.put(4, 4);
        Check.eq("lfu", f1 + "," + f2 + "," + f3 + "," + lfu.get(1) + "," + lfu.get(3) + "," + lfu.get(4), "1,-1,3,-1,3,4");
        DesignStructures.RandomizedSet rs = new DesignStructures.RandomizedSet();
        Check.eq("randSet", rs.insert(1) + "," + rs.remove(2) + "," + rs.insert(2) + "," + rs.remove(1) + "," + rs.insert(2) + "," + rs.getRandom(), "true,false,true,true,false,2");
        DesignStructures.MaxStack mxs = new DesignStructures.MaxStack();
        mxs.push(5); mxs.push(1); mxs.push(5);
        Check.eq("maxStack", mxs.top() + "," + mxs.popMax() + "," + mxs.top() + "," + mxs.peekMax() + "," + mxs.pop() + "," + mxs.top(), "5,5,1,5,1,5");
        DesignStructures.Twitter tw = new DesignStructures.Twitter();
        tw.postTweet(1, 5);
        tw.follow(1, 2);
        tw.postTweet(2, 6);
        Check.eq("feed", tw.getNewsFeed(1), "[6, 5]");
        tw.unfollow(1, 2);
        Check.eq("feedAfterUnfollow", tw.getNewsFeed(1), "[5]");
        DesignStructures.RateLimiter rl = new DesignStructures.RateLimiter(2, 1000);
        Check.eq("rateLimit", rl.allow(0) + "," + rl.allow(100) + "," + rl.allow(200) + "," + rl.allow(1000) + "," + rl.allow(1100), "true,true,false,true,true");
        DesignStructures.TokenBucket tb = new DesignStructures.TokenBucket(2, 1, 0);
        Check.eq("tokenBucket", tb.allow(0) + "," + tb.allow(0) + "," + tb.allow(0) + "," + tb.allow(1000), "true,true,false,true");
        DesignStructures.SnapshotArray sa = new DesignStructures.SnapshotArray(3);
        sa.set(0, 5);
        int sid = sa.snap();
        sa.set(0, 6);
        Check.eq("snapshot", sa.get(0, sid), 5);
        DesignStructures.HitCounter hc = new DesignStructures.HitCounter();
        hc.hit(1); hc.hit(2); hc.hit(3);
        Check.eq("hits", hc.getHits(4) + "," + hc.getHits(300) + "," + hc.getHits(301), "3,3,2");
        DesignStructures.PeekingIterator pi = new DesignStructures.PeekingIterator(List.of(1, 2, 3).iterator());
        Check.eq("peek", pi.next() + "," + pi.peek() + "," + pi.next() + "," + pi.next() + "," + pi.hasNext(), "1,2,2,3,false");
        DesignStructures.ConsistentHash ch = new DesignStructures.ConsistentHash(20);
        ch.addNode("A"); ch.addNode("B"); ch.addNode("C");
        String owner = ch.route("user-42");
        ch.addNode("D");
        String after = ch.route("user-42");
        Check.eq("consistentHash", owner.equals(after) || after.equals("D"), true);

        // ---------- randomized cross-checks ----------
        Random r = new Random(9);
        for (int t = 0; t < 300; t++) {
            String text = rnd(r, r.nextInt(30)), pat = rnd(r, 1 + r.nextInt(3));
            List<Integer> naive = new ArrayList<>();
            for (int i = text.indexOf(pat); i >= 0; i = text.indexOf(pat, i + 1)) naive.add(i);
            Check.eq("kmpRand", StringAlgorithms.kmpSearch(text, pat), text.indexOf(pat));
            Check.eq("zRand", StringAlgorithms.findAllZ(text, pat), naive);
            Check.eq("rkRand", StringAlgorithms.rabinKarp(text, pat), naive);
            Check.eq("manacherRand", StringAlgorithms.manacher(text).length(), bruteLongestPal(text));
            int n = r.nextInt(1 << 20), m = r.nextInt(1 << 20);
            Check.eq("popcount", BitsMath.hammingWeight(n), Integer.bitCount(n));
            Check.eq("gcdRand", BitsMath.gcd(n, m), java.math.BigInteger.valueOf(n).gcd(java.math.BigInteger.valueOf(m)).longValue());
            Check.eq("sumRand", BitsMath.getSum(n - 500000, m - 500000), (n - 500000) + (m - 500000));
            Check.eq("multiplyRand", StringAlgorithms.multiply(String.valueOf(n), String.valueOf(m)), String.valueOf((long) n * m));
            Check.eq("powModRand", BitsMath.powMod(n, 5, 1_000_007L), java.math.BigInteger.valueOf(n).modPow(java.math.BigInteger.valueOf(5), java.math.BigInteger.valueOf(1_000_007L)).longValue());

            // list reverse / sort vs array ops
            int len = r.nextInt(10);
            int[] arr = new int[len];
            for (int i = 0; i < len; i++) arr[i] = r.nextInt(10);
            int[] sorted = arr.clone();
            Arrays.sort(sorted);
            Check.eq("sortListRand", LinkedLists.str(LinkedLists.sortList(LinkedLists.of(arr))), LinkedLists.str(LinkedLists.of(sorted)));
        }
        // LRU vs LinkedHashMap oracle
        for (int t = 0; t < 50; t++) {
            int cap = 1 + r.nextInt(4);
            DesignStructures.LRUCache mine = new DesignStructures.LRUCache(cap);
            DesignStructures.LRUCacheLHM oracle = new DesignStructures.LRUCacheLHM(cap);
            for (int op = 0; op < 60; op++) {
                int k = r.nextInt(6);
                if (r.nextBoolean()) { int v = r.nextInt(100); mine.put(k, v); oracle.put(k, v); }
                else { Integer want = oracle.get(k); Check.eq("lruRand", mine.get(k), want == null ? -1 : want); }
            }
        }
        Check.done("StructuresTest");
    }

    static String rnd(Random r, int n) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < n; i++) sb.append((char) ('a' + r.nextInt(2)));
        return sb.toString();
    }

    static int bruteLongestPal(String s) {
        int best = 0;
        for (int i = 0; i < s.length(); i++)
            for (int j = i + 1; j <= s.length(); j++) {
                String t = s.substring(i, j);
                if (new StringBuilder(t).reverse().toString().equals(t)) best = Math.max(best, t.length());
            }
        return best;
    }
}
