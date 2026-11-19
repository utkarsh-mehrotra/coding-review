import java.util.*;

public class Trie {

    // ---------- Basic trie (LC 208) ----------
    // RECOGNIZE: many prefix queries, autocomplete, dictionary + wildcard, word search over a grid.
    private final Trie[] next = new Trie[26];
    private boolean end;
    private int passCount; // how many inserted words pass through this node

    void insert(String w) {
        Trie t = this;
        for (char c : w.toCharArray()) {
            if (t.next[c - 'a'] == null) t.next[c - 'a'] = new Trie();
            t = t.next[c - 'a'];
            t.passCount++;
        }
        t.end = true;
    }

    private Trie walk(String s) {
        Trie t = this;
        for (char c : s.toCharArray()) {
            t = t.next[c - 'a'];
            if (t == null) return null;
        }
        return t;
    }

    boolean search(String w) { Trie t = walk(w); return t != null && t.end; }
    boolean startsWith(String p) { return walk(p) != null; }
    int countPrefix(String p) { Trie t = walk(p); return t == null ? 0 : t.passCount; }

    // ---------- Wildcard '.' (LC 211): DFS over children on '.' ----------
    boolean searchWildcard(String w) { return wild(w, 0, this); }

    private boolean wild(String w, int i, Trie t) {
        if (t == null) return false;
        if (i == w.length()) return t.end;
        char c = w.charAt(i);
        if (c != '.') return wild(w, i + 1, t.next[c - 'a']);
        for (Trie ch : t.next) if (ch != null && wild(w, i + 1, ch)) return true;
        return false;
    }

    // ---------- Word Search II (LC 212): trie + grid DFS, dedupe: a found word is cleared from its trie node so it is reported once ----------
    static List<String> findWords(char[][] board, String[] words) {
        Node root = new Node();
        for (String w : words) {
            Node n = root;
            for (char c : w.toCharArray()) {
                if (n.kids[c - 'a'] == null) n.kids[c - 'a'] = new Node();
                n = n.kids[c - 'a'];
            }
            n.word = w;
        }
        List<String> out = new ArrayList<>();
        for (int i = 0; i < board.length; i++)
            for (int j = 0; j < board[0].length; j++) dfs(board, i, j, root, out);
        return out;
    }

    private static class Node {
        Node[] kids = new Node[26];
        String word;
    }

    private static void dfs(char[][] b, int i, int j, Node p, List<String> out) {
        if (i < 0 || j < 0 || i >= b.length || j >= b[0].length) return;
        char c = b[i][j];
        if (c == '#' || p.kids[c - 'a'] == null) return;
        p = p.kids[c - 'a'];
        if (p.word != null) { out.add(p.word); p.word = null; } // dedupe
        b[i][j] = '#';
        dfs(b, i + 1, j, p, out); dfs(b, i - 1, j, p, out); dfs(b, i, j + 1, p, out); dfs(b, i, j - 1, p, out);
        b[i][j] = c;
    }

    // ---------- Binary trie: Maximum XOR of two numbers (LC 421) ----------
    // Assumes non-negative ints (bits 30..0 only; a sign bit would need bit 31).
    // Memory: allocates (n * 32 + 2) x 2 ints up front, about 256 bytes per number.
    static int findMaximumXOR(int[] nums) {
        int[][] ch = new int[nums.length * 32 + 2][2];
        int cnt = 1, best = 0;
        for (int x : nums) {
            int node = 0, other = 0, xr = 0;
            // insert x
            for (int b = 30; b >= 0; b--) {
                int bit = x >> b & 1;
                if (ch[node][bit] == 0) ch[node][bit] = cnt++;
                node = ch[node][bit];
            }
            // query best partner
            node = 0;
            for (int b = 30; b >= 0; b--) {
                int bit = x >> b & 1, want = bit ^ 1;
                if (ch[node][want] != 0) { xr |= 1 << b; node = ch[node][want]; } else node = ch[node][bit];
            }
            best = Math.max(best, xr);
        }
        return best;
    }

    // ---------- Replace Words (LC 648): shortest root prefix ----------
    static String replaceWords(List<String> dict, String sentence) {
        Node root = new Node();
        for (String w : dict) {
            Node n = root;
            for (char c : w.toCharArray()) {
                if (n.kids[c - 'a'] == null) n.kids[c - 'a'] = new Node();
                n = n.kids[c - 'a'];
            }
            n.word = w;
        }
        StringBuilder sb = new StringBuilder();
        for (String tok : sentence.split(" ")) {
            Node n = root;
            String rep = tok;
            for (char c : tok.toCharArray()) {
                n = n.kids[c - 'a'];
                if (n == null) break;
                if (n.word != null) { rep = n.word; break; }
            }
            if (sb.length() > 0) sb.append(' ');
            sb.append(rep);
        }
        return sb.toString();
    }

    // ---------- Autocomplete top-3 (LC 1268 style): sorted words + binary search is the simple robust form ----------
    static List<List<String>> suggestedProducts(String[] products, String search) {
        Arrays.sort(products);
        List<List<String>> res = new ArrayList<>();
        String prefix = "";
        for (char c : search.toCharArray()) {
            prefix += c;
            int lo = 0, hi = products.length;
            while (lo < hi) {
                int mid = (lo + hi) >>> 1;
                if (products[mid].compareTo(prefix) < 0) lo = mid + 1; else hi = mid;
            }
            List<String> top = new ArrayList<>();
            for (int i = lo; i < Math.min(lo + 3, products.length) && products[i].startsWith(prefix); i++) top.add(products[i]);
            res.add(top);
        }
        return res;
    }
}
