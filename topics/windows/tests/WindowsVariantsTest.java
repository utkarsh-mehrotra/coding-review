import java.util.*;

public class WindowsVariantsTest {
    public static void main(String[] x) {
        windows();
        Check.done("WindowsVariantsTest");
    }

    static Random rnd = new Random(20260316);
    static int[] arr(int n, int lo, int hi) { int[] a = new int[n]; for (int i = 0; i < n; i++) a[i] = lo + rnd.nextInt(hi - lo + 1); return a; }
    static List<Integer> sorted(Collection<Integer> c) { List<Integer> l = new ArrayList<>(c); Collections.sort(l); return l; }
    static String str(int n, int alpha) { StringBuilder sb = new StringBuilder(); for (int i = 0; i < n; i++) sb.append((char) ('a' + rnd.nextInt(alpha))); return sb.toString(); }
    static boolean isSubseq(String t, String s) { int j = 0; for (int i = 0; i < s.length() && j < t.length(); i++) if (s.charAt(i) == t.charAt(j)) j++; return j == t.length(); }
    static void windows() {
        for (int t = 0; t < 400; t++) {
            String s = str(1 + rnd.nextInt(12), 3), pat = str(1 + rnd.nextInt(3), 3);
            String best = "";
            for (int i = 0; i < s.length(); i++) for (int j = i + 1; j <= s.length(); j++) {
                String w = s.substring(i, j);
                if (isSubseq(pat, w) && (best.isEmpty() || w.length() < best.length())) best = w;
            }
            Check.eq("minWindowSubseq", WindowsVariants.minWindowSubsequence(s, pat), best);

            String q = str(1 + rnd.nextInt(14), 2);
            int k = 1 + rnd.nextInt(3), bl = 0;
            for (int i = 0; i < q.length(); i++) for (int j = i + 1; j <= q.length(); j++) {
                int[] c = new int[26];
                for (int p = i; p < j; p++) c[q.charAt(p) - 'a']++;
                boolean ok = true;
                for (int v : c) if (v > 0 && v < k) ok = false;
                if (ok) bl = Math.max(bl, j - i);
            }
            Check.eq("atLeastK", WindowsVariants.longestSubstringAtLeastK(q, k), bl);

            int[] cards = arr(1 + rnd.nextInt(8), 1, 9);
            int kk = 1 + rnd.nextInt(cards.length), cb = 0;
            for (int left = 0; left <= kk; left++) {
                int sum = 0;
                for (int i = 0; i < left; i++) sum += cards[i];
                for (int i = 0; i < kk - left; i++) sum += cards[cards.length - 1 - i];
                cb = Math.max(cb, sum);
            }
            Check.eq("cards", WindowsVariants.maxScoreCards(cards, kk), cb);

            int[] nice = arr(1 + rnd.nextInt(10), 0, 4);
            int nk = 1 + rnd.nextInt(3), nc = 0;
            for (int i = 0; i < nice.length; i++) { int odds = 0; for (int j = i; j < nice.length; j++) { odds += nice[j] & 1; if (odds == nk) nc++; } }
            Check.eq("nice", WindowsVariants.numberOfSubarrays(nice, nk), nc);

            int[] red = arr(1 + rnd.nextInt(8), 1, 5);
            int xx = 1 + rnd.nextInt(15), rb = -1;
            for (int l = 0; l <= red.length; l++) for (int r = 0; l + r <= red.length; r++) {
                int sum = 0;
                for (int i = 0; i < l; i++) sum += red[i];
                for (int i = 0; i < r; i++) sum += red[red.length - 1 - i];
                if (sum == xx && (rb == -1 || l + r < rb)) rb = l + r;
            }
            Check.eq("reduceX", WindowsVariants.minOperationsToZero(red, xx), rb);

            int[] tb = arr(1 + rnd.nextInt(10), 0, 4);
            int tbest = 1;
            for (int i = 0; i < tb.length; i++) for (int j = i; j < tb.length; j++) {
                boolean ok = true;
                for (int p = i; p < j; p++) {
                    if (tb[p] == tb[p + 1]) ok = false;
                    if (p + 1 < j && !((tb[p] < tb[p + 1]) != (tb[p + 1] < tb[p + 2]))) ok = false;
                }
                if (ok) tbest = Math.max(tbest, j - i + 1);
            }
            Check.eq("turbulent", WindowsVariants.maxTurbulenceSize(tb), tbest);

            int[] nd = arr(1 + rnd.nextInt(8), -6, 6);
            int idx = 1 + rnd.nextInt(3), val = rnd.nextInt(4);
            boolean found = false;
            for (int i = 0; i < nd.length; i++) for (int j = i + 1; j < nd.length && j - i <= idx; j++) if (Math.abs((long) nd[i] - nd[j]) <= val) found = true;
            Check.eq("almostDup", WindowsVariants.containsNearbyAlmostDuplicate(nd, idx, val), found);

            int[] fs = arr(4 + rnd.nextInt(5), -3, 3);
            int tgt = -3 + rnd.nextInt(7);
            Set<List<Integer>> wantQ = new TreeSet<>(Comparator.comparing(Object::toString));
            for (int a = 0; a < fs.length; a++) for (int b = a + 1; b < fs.length; b++) for (int c = b + 1; c < fs.length; c++) for (int d = c + 1; d < fs.length; d++)
                if (fs[a] + fs[b] + fs[c] + fs[d] == tgt) { int[] q4 = {fs[a], fs[b], fs[c], fs[d]}; Arrays.sort(q4); wantQ.add(List.of(q4[0], q4[1], q4[2], q4[3])); }
            Set<List<Integer>> gotQ = new TreeSet<>(Comparator.comparing(Object::toString));
            List<List<Integer>> gq = WindowsVariants.fourSum(fs.clone(), tgt);
            gotQ.addAll(gq);
            Check.eq("4sum", gotQ.toString(), wantQ.toString());
            Check.eq("4sumNoDup", gq.size(), wantQ.size());

            String abc = str(1 + rnd.nextInt(12), 3);
            int ac = 0;
            for (int i = 0; i < abc.length(); i++) for (int j = i + 3; j <= abc.length(); j++) {
                String w = abc.substring(i, j);
                if (w.contains("a") && w.contains("b") && w.contains("c")) ac++;
            }
            Check.eq("allThree", WindowsVariants.numberOfSubstrings(abc), ac);

            String vw = str(1 + rnd.nextInt(12), 5);
            int vk = 1 + rnd.nextInt(vw.length()), vb = 0;
            for (int i = 0; i + vk <= vw.length(); i++) { int c = 0; for (int j = i; j < i + vk; j++) if ("aeiou".indexOf(vw.charAt(j)) >= 0) c++; vb = Math.max(vb, c); }
            Check.eq("vowels", WindowsVariants.maxVowels(vw, vk), vb);

            int[] fr = arr(1 + rnd.nextInt(8), 1, 9);
            int fk = rnd.nextInt(10), fb = 1;
            int[] sf = fr.clone();
            Arrays.sort(sf);
            for (int i = 0; i < sf.length; i++) { int cost = 0, c = 1; for (int j = i - 1; j >= 0 && cost + sf[i] - sf[j] <= fk; j--) { cost += sf[i] - sf[j]; c++; } fb = Math.max(fb, c); }
            Check.eq("freq", WindowsVariants.maxFrequency(fr, fk), fb);

            // concatenation of words
            int wl = 1 + rnd.nextInt(2), wc = 1 + rnd.nextInt(3);
            String[] words = new String[wc];
            for (int i = 0; i < wc; i++) words[i] = str(wl, 2);
            String hay = str(2 + rnd.nextInt(10), 2);
            List<Integer> wantIdx = new ArrayList<>();
            List<String> sortedWords = new ArrayList<>(Arrays.asList(words));
            Collections.sort(sortedWords);
            for (int i = 0; i + wl * wc <= hay.length(); i++) {
                List<String> parts = new ArrayList<>();
                for (int p = 0; p < wc; p++) parts.add(hay.substring(i + p * wl, i + (p + 1) * wl));
                Collections.sort(parts);
                if (parts.equals(sortedWords)) wantIdx.add(i);
            }
            Check.eq("concatWords", sorted(WindowsVariants.findSubstring(hay, words)), sorted(wantIdx));
        }
    }
}
