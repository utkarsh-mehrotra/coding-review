import java.util.*;

public class StringAlgorithmsTest {
    public static void main(String[] x) {
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
            Check.eq("multiplyRand", StringAlgorithms.multiply(String.valueOf(n), String.valueOf(m)), String.valueOf((long) n * m));
        }
        // prefixFunction known inputs
        Check.eq("prefixFunction abcabcd", Check.s(StringAlgorithms.prefixFunction("abcabcd")), "[0, 0, 0, 1, 2, 3, 0]");
        Check.eq("prefixFunction aabaaab", Check.s(StringAlgorithms.prefixFunction("aabaaab")), "[0, 1, 0, 1, 2, 2, 3]");
        Check.eq("prefixFunction aaaa", Check.s(StringAlgorithms.prefixFunction("aaaa")), "[0, 1, 2, 3]");
        Check.done("StringAlgorithmsTest");
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
