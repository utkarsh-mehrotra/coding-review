import java.util.*;

public class StringsVariants {

    // ---------- Suffix array, O(n log^2 n) by prefix doubling ----------
    // RECOGNIZE: distinct substrings, longest repeated substring, substring queries over one fixed text.
    static int[] suffixArray(String s) {
        int n = s.length();
        Integer[] sa = new Integer[n];
        int[] rank = new int[n], tmp = new int[n];
        for (int i = 0; i < n; i++) { sa[i] = i; rank[i] = s.charAt(i); }
        for (int k = 1; n > 1; k <<= 1) {
            final int kk = k;
            Comparator<Integer> cmp = (a, b) -> {
                if (rank[a] != rank[b]) return Integer.compare(rank[a], rank[b]);
                int ra = a + kk < n ? rank[a + kk] : -1, rb = b + kk < n ? rank[b + kk] : -1;
                return Integer.compare(ra, rb);
            };
            Arrays.sort(sa, cmp);
            tmp[sa[0]] = 0;
            for (int i = 1; i < n; i++) tmp[sa[i]] = tmp[sa[i - 1]] + (cmp.compare(sa[i - 1], sa[i]) < 0 ? 1 : 0);
            System.arraycopy(tmp, 0, rank, 0, n);
            if (rank[sa[n - 1]] == n - 1) break;
        }
        int[] res = new int[n];
        for (int i = 0; i < n; i++) res[i] = sa[i];
        return res;
    }

    // Kasai: lcp[i] = longest common prefix of suffixes sa[i - 1] and sa[i], O(n)
    static int[] lcpArray(String s, int[] sa) {
        int n = s.length();
        int[] rank = new int[n], lcp = new int[n];
        for (int i = 0; i < n; i++) rank[sa[i]] = i;
        for (int i = 0, h = 0; i < n; i++) {
            if (rank[i] > 0) {
                int j = sa[rank[i] - 1];
                while (i + h < n && j + h < n && s.charAt(i + h) == s.charAt(j + h)) h++;
                lcp[rank[i]] = h;
                if (h > 0) h--;
            } else h = 0;
        }
        return lcp;
    }

    // LC 1698
    // Number of distinct substrings = n(n+1)/2 - sum(lcp)
    static long countDistinctSubstrings(String s) {
        int n = s.length();
        if (n == 0) return 0;
        long total = (long) n * (n + 1) / 2;
        for (int v : lcpArray(s, suffixArray(s))) total -= v;
        return total;
    }

    // Longest repeated substring (may overlap) = max lcp
    static String longestRepeatedSubstring(String s) {
        if (s.length() < 2) return "";
        int[] sa = suffixArray(s), lcp = lcpArray(s, sa);
        int best = 0, at = 0;
        for (int i = 1; i < s.length(); i++) if (lcp[i] > best) { best = lcp[i]; at = sa[i]; }
        return s.substring(at, at + best);
    }

    // ---------- Longest Duplicate Substring (LC 1044): binary search on length + rolling hash, verify on hit ----------
    static String longestDupSubstring(String s) {
        int lo = 1, hi = s.length() - 1;
        String best = "";
        while (lo <= hi) {
            int mid = (lo + hi) >>> 1;
            String dup = dupOfLength(s, mid);
            if (dup != null) { best = dup; lo = mid + 1; } else hi = mid - 1;
        }
        return best;
    }

    private static String dupOfLength(String s, int len) {
        final long B = 131, M1 = 1_000_000_007L, M2 = 998_244_353L;
        long h1 = 0, h2 = 0, p1 = 1, p2 = 1;
        for (int i = 0; i < len; i++) { h1 = (h1 * B + s.charAt(i)) % M1; h2 = (h2 * B + s.charAt(i)) % M2; if (i > 0) { p1 = p1 * B % M1; p2 = p2 * B % M2; } }
        Map<Long, List<Integer>> seen = new HashMap<>();
        for (int i = 0; ; i++) {
            long key = h1 * M2 + h2;
            List<Integer> starts = seen.computeIfAbsent(key, k -> new ArrayList<>());
            for (int st : starts) if (s.regionMatches(st, s, i, len)) return s.substring(i, i + len); // verify: hashes can collide
            starts.add(i);
            if (i + len >= s.length()) break;
            h1 = ((h1 - s.charAt(i) * p1 % M1 + M1) * B + s.charAt(i + len)) % M1;
            h2 = ((h2 - s.charAt(i) * p2 % M2 + M2) * B + s.charAt(i + len)) % M2;
        }
        return null;
    }

    // LC 1032
    // ---------- Aho-Corasick: many patterns, one pass over the text ----------
    // RECOGNIZE: dictionary of words to find inside a long text (censoring, DNA motifs, log scanning).
    static class AhoCorasick {
        private final List<int[]> next = new ArrayList<>();      // 26 children per state
        private final List<Integer> fail = new ArrayList<>();
        private final List<List<Integer>> out = new ArrayList<>(); // pattern ids ending at (or via suffix links of) the state
        private final int patterns;

        AhoCorasick(String[] words) {
            patterns = words.length;
            newState();
            for (int id = 0; id < words.length; id++) {
                int s = 0;
                for (char c : words[id].toCharArray()) {
                    if (next.get(s)[c - 'a'] == -1) { int t = newState(); next.get(s)[c - 'a'] = t; }
                    s = next.get(s)[c - 'a'];
                }
                out.get(s).add(id);
            }
            Deque<Integer> q = new ArrayDeque<>();
            for (int c = 0; c < 26; c++) {
                int t = next.get(0)[c];
                if (t == -1) next.get(0)[c] = 0; else { fail.set(t, 0); q.add(t); }
            }
            while (!q.isEmpty()) {
                int s = q.poll();
                out.get(s).addAll(out.get(fail.get(s))); // inherit matches from the longest proper suffix state
                for (int c = 0; c < 26; c++) {
                    int t = next.get(s)[c];
                    if (t == -1) next.get(s)[c] = next.get(fail.get(s))[c];
                    else { fail.set(t, next.get(fail.get(s))[c]); q.add(t); }
                }
            }
        }

        private int newState() {
            int[] row = new int[26];
            Arrays.fill(row, -1);
            next.add(row);
            fail.add(0);
            out.add(new ArrayList<>());
            return next.size() - 1;
        }

        // occurrences of each pattern in text (overlaps counted)
        int[] countAll(String text) {
            int[] cnt = new int[patterns];
            int s = 0;
            for (char ch : text.toCharArray()) {
                s = next.get(s)[ch - 'a'];
                for (int id : out.get(s)) cnt[id]++;
            }
            return cnt;
        }
    }

    // ---------- Word Break II (LC 140): memoized DFS over start index ----------
    static List<String> wordBreakAll(String s, List<String> dict) {
        return wb(s, 0, new HashSet<>(dict), new HashMap<>());
    }

    private static List<String> wb(String s, int i, Set<String> dict, Map<Integer, List<String>> memo) {
        if (memo.containsKey(i)) return memo.get(i);
        List<String> res = new ArrayList<>();
        if (i == s.length()) res.add("");
        for (int j = i + 1; j <= s.length(); j++) {
            String w = s.substring(i, j);
            if (!dict.contains(w)) continue;
            for (String rest : wb(s, j, dict, memo)) res.add(rest.isEmpty() ? w : w + " " + rest);
        }
        memo.put(i, res);
        return res;
    }

    // ---------- Palindrome Pairs (LC 336): split each word, look the reversed half up in a map ----------
    static List<List<Integer>> palindromePairs(String[] words) {
        Map<String, Integer> idx = new HashMap<>();
        for (int i = 0; i < words.length; i++) idx.put(words[i], i);
        List<List<Integer>> res = new ArrayList<>();
        for (int i = 0; i < words.length; i++) {
            String w = words[i];
            for (int j = 0; j <= w.length(); j++) {
                String left = w.substring(0, j), right = w.substring(j);
                if (isPal(left)) { // word k + word i, k = reverse(right)
                    Integer k = idx.get(new StringBuilder(right).reverse().toString());
                    if (k != null && k != i) res.add(List.of(k, i));
                }
                if (!right.isEmpty() && isPal(right)) { // word i + word k, k = reverse(left); empty right would duplicate the case above
                    Integer k = idx.get(new StringBuilder(left).reverse().toString());
                    if (k != null && k != i) res.add(List.of(i, k));
                }
            }
        }
        return res;
    }

    private static boolean isPal(String s) {
        for (int l = 0, r = s.length() - 1; l < r; l++, r--) if (s.charAt(l) != s.charAt(r)) return false;
        return true;
    }

    // ---------- Longest Happy Prefix (LC 1392): the KMP failure value of the whole string ----------
    static String longestHappyPrefix(String s) {
        if (s.isEmpty()) return "";
        return s.substring(0, StringAlgorithms.prefixFunction(s)[s.length() - 1]);
    }

    // LC 899
    // ---------- Lexicographically smallest rotation in O(n) (Booth / two-candidate scan) ----------
    static String leastRotation(String s) {
        int n = s.length();
        if (n == 0) return "";
        String t = s + s;
        int i = 0, j = 1, k = 0;
        while (i < n && j < n && k < n) {
            char a = t.charAt(i + k), b = t.charAt(j + k);
            if (a == b) { k++; continue; }
            if (a > b) i += k + 1; else j += k + 1;
            if (i == j) j++;
            k = 0;
        }
        int st = Math.min(i, j);
        return t.substring(st, st + n);
    }

    // ---------- Text Justification (LC 68) ----------
    static List<String> fullJustify(String[] words, int maxWidth) {
        List<String> res = new ArrayList<>();
        int i = 0, n = words.length;
        while (i < n) {
            int j = i, len = 0;
            while (j < n && len + words[j].length() + (j - i) <= maxWidth) len += words[j++].length();
            int gaps = j - i - 1;
            StringBuilder sb = new StringBuilder();
            if (j == n || gaps == 0) { // last line or single word: left justify
                for (int t = i; t < j; t++) { sb.append(words[t]); if (t < j - 1) sb.append(' '); }
                while (sb.length() < maxWidth) sb.append(' ');
            } else {
                int spaces = maxWidth - len, each = spaces / gaps, extra = spaces % gaps;
                for (int t = i; t < j; t++) {
                    sb.append(words[t]);
                    if (t < j - 1) for (int s = 0; s < each + (t - i < extra ? 1 : 0); s++) sb.append(' ');
                }
            }
            res.add(sb.toString());
            i = j;
        }
        return res;
    }

    // ---------- Valid Number (LC 65): flags for digit, dot, exponent ----------
    static boolean isNumber(String s) {
        boolean digit = false, dot = false, exp = false, digitAfterExp = true;
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            if (Character.isDigit(c)) { digit = true; if (exp) digitAfterExp = true; }
            else if (c == '+' || c == '-') { if (i != 0 && s.charAt(i - 1) != 'e' && s.charAt(i - 1) != 'E') return false; }
            else if (c == '.') { if (dot || exp) return false; dot = true; }
            else if (c == 'e' || c == 'E') { if (exp || !digit) return false; exp = true; digitAfterExp = false; }
            else return false;
        }
        return digit && digitAfterExp;
    }

    // ---------- Encode and Decode Strings (LC 271): length-prefix framing survives any delimiter inside the data ----------
    static String encode(List<String> strs) {
        StringBuilder sb = new StringBuilder();
        for (String s : strs) sb.append(s.length()).append('#').append(s);
        return sb.toString();
    }

    static List<String> decode(String s) {
        List<String> res = new ArrayList<>();
        int i = 0;
        while (i < s.length()) {
            int h = s.indexOf('#', i), len = Integer.parseInt(s.substring(i, h));
            res.add(s.substring(h + 1, h + 1 + len));
            i = h + 1 + len;
        }
        return res;
    }

    // ---------- Compare Version Numbers (LC 165) ----------
    static int compareVersion(String a, String b) {
        String[] x = a.split("\\."), y = b.split("\\.");
        for (int i = 0; i < Math.max(x.length, y.length); i++) {
            int p = i < x.length ? Integer.parseInt(x[i]) : 0, q = i < y.length ? Integer.parseInt(y[i]) : 0;
            if (p != q) return Integer.compare(p, q);
        }
        return 0;
    }

    // ---------- Zigzag Conversion (LC 6): bounce a row index ----------
    static String zigzag(String s, int rows) {
        if (rows == 1 || rows >= s.length()) return s;
        StringBuilder[] r = new StringBuilder[rows];
        for (int i = 0; i < rows; i++) r[i] = new StringBuilder();
        int row = 0, dir = 1;
        for (char c : s.toCharArray()) {
            r[row].append(c);
            if (row == 0) dir = 1; else if (row == rows - 1) dir = -1;
            row += dir;
        }
        StringBuilder out = new StringBuilder();
        for (StringBuilder b : r) out.append(b);
        return out.toString();
    }
}
