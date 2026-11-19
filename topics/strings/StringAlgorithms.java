import java.util.*;

public class StringAlgorithms {

    // ---------- KMP: failure function = longest proper prefix that is also a suffix ----------
    // RECOGNIZE: find a pattern in text in O(n + m); periodic structure; shortest repeating unit.
    static int[] prefixFunction(String p) {
        int[] pi = new int[p.length()];
        for (int i = 1, k = 0; i < p.length(); i++) {
            while (k > 0 && p.charAt(i) != p.charAt(k)) k = pi[k - 1];
            if (p.charAt(i) == p.charAt(k)) k++;
            pi[i] = k;
        }
        return pi;
    }

    // first index of p in s, or -1 (LC 28)
    static int kmpSearch(String s, String p) {
        if (p.isEmpty()) return 0;
        int[] pi = prefixFunction(p);
        for (int i = 0, k = 0; i < s.length(); i++) {
            while (k > 0 && s.charAt(i) != p.charAt(k)) k = pi[k - 1];
            if (s.charAt(i) == p.charAt(k)) k++;
            if (k == p.length()) return i - k + 1;
        }
        return -1;
    }

    // Repeated Substring Pattern (LC 459): smallest period divides n
    static boolean repeatedSubstringPattern(String s) {
        int n = s.length(), p = prefixFunction(s)[n - 1];
        return p > 0 && n % (n - p) == 0;
    }

    // Shortest Palindrome (LC 214): longest palindromic prefix via KMP on s + '#' + reverse(s)
    static String shortestPalindrome(String s) {
        String t = s + "#" + new StringBuilder(s).reverse();
        int keep = prefixFunction(t)[t.length() - 1];
        return new StringBuilder(s.substring(keep)).reverse() + s;
    }

    // ---------- Z-function: z[i] = longest common prefix of s and s[i..] ----------
    static int[] zFunction(String s) {
        int n = s.length();
        int[] z = new int[n];
        for (int i = 1, l = 0, r = 0; i < n; i++) {
            if (i < r) z[i] = Math.min(r - i, z[i - l]);
            while (i + z[i] < n && s.charAt(z[i]) == s.charAt(i + z[i])) z[i]++;
            if (i + z[i] > r) { l = i; r = i + z[i]; }
        }
        return z;
    }

    // LC 2223 (partial: the core of LC 2223, which sums Z-values of every suffix; this lists all occurrences of p in s via Z)
    // All occurrences of p in s using Z on p + '#' + s
    static List<Integer> findAllZ(String s, String p) {
        String t = p + "\u0001" + s;
        int[] z = zFunction(t);
        List<Integer> res = new ArrayList<>();
        for (int i = p.length() + 1; i < t.length(); i++) if (z[i] >= p.length()) res.add(i - p.length() - 1);
        return res;
    }

    // ---------- Rabin-Karp rolling hash (double mod to dodge anti-hash tests) ----------
    static List<Integer> rabinKarp(String s, String p) {
        List<Integer> res = new ArrayList<>();
        int n = s.length(), m = p.length();
        if (m > n) return res;
        final long B = 131, M1 = 1_000_000_007L, M2 = 998_244_353L;
        long h1 = 0, h2 = 0, t1 = 0, t2 = 0, p1 = 1, p2 = 1;
        for (int i = 0; i < m; i++) {
            h1 = (h1 * B + p.charAt(i)) % M1; h2 = (h2 * B + p.charAt(i)) % M2;
            t1 = (t1 * B + s.charAt(i)) % M1; t2 = (t2 * B + s.charAt(i)) % M2;
            if (i > 0) { p1 = p1 * B % M1; p2 = p2 * B % M2; }
        }
        for (int i = 0; ; i++) {
            if (h1 == t1 && h2 == t2 && s.startsWith(p, i)) res.add(i); // verify to be safe
            if (i + m >= n) break;
            t1 = ((t1 - s.charAt(i) * p1 % M1 + M1) * B + s.charAt(i + m)) % M1;
            t2 = ((t2 - s.charAt(i) * p2 % M2 + M2) * B + s.charAt(i + m)) % M2;
        }
        return res;
    }

    // ---------- Manacher: longest palindromic substring in O(n) ----------
    static String manacher(String s) {
        int n = s.length();
        if (n == 0) return "";
        StringBuilder sb = new StringBuilder("^");
        for (char c : s.toCharArray()) sb.append('#').append(c);
        String t = sb.append("#$").toString();
        int[] p = new int[t.length()];
        int c = 0, r = 0, bestLen = 0, bestCenter = 0;
        for (int i = 1; i < t.length() - 1; i++) {
            if (i < r) p[i] = Math.min(r - i, p[2 * c - i]);
            while (t.charAt(i + 1 + p[i]) == t.charAt(i - 1 - p[i])) p[i]++;
            if (i + p[i] > r) { c = i; r = i + p[i]; }
            if (p[i] > bestLen) { bestLen = p[i]; bestCenter = i; }
        }
        int start = (bestCenter - bestLen) / 2;
        return s.substring(start, start + bestLen);
    }

    // ---------- Anagram / counting utilities ----------
    static boolean isAnagram(String a, String b) {
        if (a.length() != b.length()) return false;
        int[] c = new int[26];
        for (int i = 0; i < a.length(); i++) { c[a.charAt(i) - 'a']++; c[b.charAt(i) - 'a']--; }
        for (int x : c) if (x != 0) return false;
        return true;
    }

    // Isomorphic Strings (LC 205): bidirectional mapping
    static boolean isIsomorphic(String s, String t) {
        int[] a = new int[256], b = new int[256];
        for (int i = 0; i < s.length(); i++) {
            if (a[s.charAt(i)] != b[t.charAt(i)]) return false;
            a[s.charAt(i)] = b[t.charAt(i)] = i + 1;
        }
        return true;
    }

    // Longest Common Prefix (LC 14)
    static String longestCommonPrefix(String[] strs) {
        String p = strs[0];
        for (String s : strs) while (!s.startsWith(p)) p = p.substring(0, p.length() - 1);
        return p;
    }

    // String Compression (LC 443): two pointers in place
    static int compress(char[] chars) {
        int w = 0, i = 0;
        while (i < chars.length) {
            int j = i;
            while (j < chars.length && chars[j] == chars[i]) j++;
            chars[w++] = chars[i];
            if (j - i > 1) for (char d : String.valueOf(j - i).toCharArray()) chars[w++] = d;
            i = j;
        }
        return w;
    }

    // Reverse Words (LC 151)
    static String reverseWords(String s) {
        String[] w = s.trim().split("\\s+");
        Collections.reverse(Arrays.asList(w));
        return String.join(" ", w);
    }

    // Multiply Strings (LC 43)
    static String multiply(String a, String b) {
        if (a.equals("0") || b.equals("0")) return "0";
        int[] r = new int[a.length() + b.length()];
        for (int i = a.length() - 1; i >= 0; i--)
            for (int j = b.length() - 1; j >= 0; j--) {
                int p = (a.charAt(i) - '0') * (b.charAt(j) - '0') + r[i + j + 1];
                r[i + j + 1] = p % 10;
                r[i + j] += p / 10;
            }
        StringBuilder sb = new StringBuilder();
        for (int d : r) if (!(sb.length() == 0 && d == 0)) sb.append(d);
        return sb.toString();
    }

    // Add Strings (LC 415)
    static String addStrings(String a, String b) {
        StringBuilder sb = new StringBuilder();
        int i = a.length() - 1, j = b.length() - 1, carry = 0;
        while (i >= 0 || j >= 0 || carry > 0) {
            int s = carry + (i >= 0 ? a.charAt(i--) - '0' : 0) + (j >= 0 ? b.charAt(j--) - '0' : 0);
            sb.append(s % 10);
            carry = s / 10;
        }
        return sb.reverse().toString();
    }

    // String to Integer / atoi (LC 8)
    static int myAtoi(String s) {
        int i = 0, n = s.length(), sign = 1;
        long v = 0;
        while (i < n && s.charAt(i) == ' ') i++;
        if (i < n && (s.charAt(i) == '+' || s.charAt(i) == '-')) sign = s.charAt(i++) == '-' ? -1 : 1;
        while (i < n && Character.isDigit(s.charAt(i))) {
            v = v * 10 + (s.charAt(i++) - '0');
            if (v * sign > Integer.MAX_VALUE) return Integer.MAX_VALUE;
            if (v * sign < Integer.MIN_VALUE) return Integer.MIN_VALUE;
        }
        return (int) (v * sign);
    }

    // Roman to Integer (LC 13)
    static int romanToInt(String s) {
        Map<Character, Integer> m = Map.of('I', 1, 'V', 5, 'X', 10, 'L', 50, 'C', 100, 'D', 500, 'M', 1000);
        int res = 0;
        for (int i = 0; i < s.length(); i++) {
            int v = m.get(s.charAt(i));
            res += i + 1 < s.length() && v < m.get(s.charAt(i + 1)) ? -v : v;
        }
        return res;
    }

    // Longest Duplicate Substring (LC 1044) idea: binary search on length + rolling hash is the standard answer;
    // see rabinKarp above for the hash and BinarySearch template for the outer loop.

    // Smallest rotation / Lyndon: minimal string rotation via doubling (simple O(n^2) baseline kept short)
    static String minRotation(String s) {
        String best = s;
        for (int i = 1; i < s.length(); i++) {
            String r = s.substring(i) + s.substring(0, i);
            if (r.compareTo(best) < 0) best = r;
        }
        return best;
    }

    // Longest Palindromic Substring is in DPSequences (expand around center) -- Manacher above is the O(n) version.
}
