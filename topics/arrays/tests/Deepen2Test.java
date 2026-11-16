import java.util.*;

public class Deepen2Test {
    static Random rnd = new Random(20260316);
    static int[] arr(int n, int lo, int hi) { int[] a = new int[n]; for (int i = 0; i < n; i++) a[i] = lo + rnd.nextInt(hi - lo + 1); return a; }
    static List<Integer> sorted(Collection<Integer> c) { List<Integer> l = new ArrayList<>(c); Collections.sort(l); return l; }
    static String str(int n, int alpha) { StringBuilder sb = new StringBuilder(); for (int i = 0; i < n; i++) sb.append((char) ('a' + rnd.nextInt(alpha))); return sb.toString(); }

    public static void main(String[] x) {
        arrays();
        windows();
        stacks();
        binarySearch();
        backtracking();
        Check.done("Deepen2Test");
    }

    // ------------------------------------------------------------------ arrays
    static void arrays() {
        for (int t = 0; t < 400; t++) {
            int n = 1 + rnd.nextInt(9);
            int[] a = arr(n, -6, 6);
            int best = Integer.MIN_VALUE;
            for (int s = 0; s < n; s++) { int sum = 0; for (int len = 1; len <= n; len++) { sum += a[(s + len - 1) % n]; best = Math.max(best, sum); } }
            Check.eq("circular", ArraysVariants.maxSubarraySumCircular(a), best);

            int[] m = arr(1 + rnd.nextInt(12), 0, 3);
            Map<Integer, Integer> cnt = new HashMap<>();
            for (int v : m) cnt.merge(v, 1, Integer::sum);
            List<Integer> want = new ArrayList<>();
            for (var e : cnt.entrySet()) if (e.getValue() > m.length / 3) want.add(e.getKey());
            Check.eq("majorityII", sorted(ArraysVariants.majorityElementII(m)), sorted(want));

            int dn = 1 + rnd.nextInt(8);
            int[] d = new int[dn];
            for (int i = 0; i < dn; i++) d[i] = 1 + rnd.nextInt(dn);
            Map<Integer, Integer> dc = new HashMap<>();
            for (int v : d) dc.merge(v, 1, Integer::sum);
            List<Integer> dw = new ArrayList<>();
            for (var e : dc.entrySet()) if (e.getValue() == 2) dw.add(e.getKey());
            boolean allLe2 = dc.values().stream().allMatch(c -> c <= 2);
            if (allLe2) Check.eq("findDup", sorted(ArraysVariants.findDuplicates(d.clone())), sorted(dw));

            int[] r = arr(1 + rnd.nextInt(10), -8, 8);
            int lo = -5 + rnd.nextInt(5), hi = lo + rnd.nextInt(8);
            long[] p = new long[r.length + 1];
            for (int i = 0; i < r.length; i++) p[i + 1] = p[i] + r[i];
            int c1 = 0, c2 = 0;
            for (int i = 0; i < p.length; i++) for (int j = i + 1; j < p.length; j++) if (p[j] - p[i] >= lo && p[j] - p[i] <= hi) c1++;
            Check.eq("rangeSum", ArraysVariants.countRangeSum(r, lo, hi), c1);
            for (int i = 0; i < r.length; i++) for (int j = i + 1; j < r.length; j++) if ((long) r[i] > 2L * r[j]) c2++;
            Check.eq("reversePairs", ArraysVariants.reversePairs(r), c2);

            int pn = 1 + rnd.nextInt(8);
            int[] perm = new int[pn];
            for (int i = 0; i < pn; i++) perm[i] = i;
            for (int i = pn - 1; i > 0; i--) { int j = rnd.nextInt(i + 1); int tmp = perm[i]; perm[i] = perm[j]; perm[j] = tmp; }
            int ch = 0;
            for (int i = 0; i < pn; i++) { boolean ok = true; for (int k = 0; k <= i; k++) if (perm[k] > i) ok = false; if (ok) ch++; }
            Check.eq("chunks", ArraysVariants.maxChunksToSorted(perm), ch);

            int[] g = arr(1 + rnd.nextInt(9), 0, 6);
            int chunks = 1;
            for (int i = 0; i + 1 < g.length; i++) {
                int mx = Integer.MIN_VALUE, mn = Integer.MAX_VALUE;
                for (int k = 0; k <= i; k++) mx = Math.max(mx, g[k]);
                for (int k = i + 1; k < g.length; k++) mn = Math.min(mn, g[k]);
                if (mx <= mn) chunks++;
            }
            Check.eq("chunksII", ArraysVariants.maxChunksToSortedII(g), chunks);

            int[] mt = arr(1 + rnd.nextInt(10), 0, 4);
            int mbest = 0;
            for (int pk = 1; pk + 1 < mt.length; pk++) {
                int l = pk, rr = pk;
                while (l > 0 && mt[l - 1] < mt[l]) l--;
                while (rr + 1 < mt.length && mt[rr + 1] < mt[rr]) rr++;
                if (l < pk && rr > pk) mbest = Math.max(mbest, rr - l + 1);
            }
            Check.eq("mountain", ArraysVariants.longestMountain(mt), mbest);

            int[] sp = arr(2 + rnd.nextInt(8), 1, 9);
            int sb = Integer.MIN_VALUE;
            for (int i = 0; i < sp.length; i++) for (int j = i + 1; j < sp.length; j++) sb = Math.max(sb, sp[i] + sp[j] + i - j);
            Check.eq("sightseeing", ArraysVariants.maxScoreSightseeingPair(sp), sb);

            int[] bm = arr(1 + rnd.nextInt(9), 0, 6);
            int bl = rnd.nextInt(4), br = bl + rnd.nextInt(4), bc = 0;
            for (int i = 0; i < bm.length; i++) { int mx = -1; for (int j = i; j < bm.length; j++) { mx = Math.max(mx, bm[j]); if (mx >= bl && mx <= br) bc++; } }
            Check.eq("boundedMax", ArraysVariants.numSubarrayBoundedMax(bm, bl, br), bc);

            int[] us = arr(1 + rnd.nextInt(9), 0, 6);
            int[] srt = us.clone();
            Arrays.sort(srt);
            int s0 = 0, e0 = us.length - 1;
            while (s0 < us.length && us[s0] == srt[s0]) s0++;
            while (e0 >= 0 && us[e0] == srt[e0]) e0--;
            Check.eq("unsorted", ArraysVariants.findUnsortedSubarray(us), s0 >= us.length ? 0 : e0 - s0 + 1);

            int[] rp = arr(1 + rnd.nextInt(10), 0, 7);
            int rw = 0;
            for (int i = 0; i < rp.length; i++) for (int j = i; j < rp.length; j++) if (rp[i] <= rp[j]) rw = Math.max(rw, j - i);
            Check.eq("ramp", ArraysVariants.maxWidthRamp(rp), rw);

            int[] tw = arr(3 + rnd.nextInt(9), 0, 9);
            int L = 1 + rnd.nextInt(3), M = 1 + rnd.nextInt(3);
            if (L + M <= tw.length) {
                int tb = 0;
                for (int i = 0; i + L <= tw.length; i++) for (int j = 0; j + M <= tw.length; j++) {
                    if (i + L <= j || j + M <= i) {
                        int sum = 0;
                        for (int k = 0; k < L; k++) sum += tw[i + k];
                        for (int k = 0; k < M; k++) sum += tw[j + k];
                        tb = Math.max(tb, sum);
                    }
                }
                Check.eq("twoNoOverlap", ArraysVariants.maxSumTwoNoOverlap(tw, L, M), tb);
            }

            int[] so = arr(rnd.nextInt(30), 0, 5000);
            int[] w1 = so.clone(), w2 = so.clone(), w3 = so.clone(), want0 = so.clone();
            Arrays.sort(want0);
            ArraysVariants.radixSort(w1);
            Check.eq("radix", Check.s(w1), Check.s(want0));
            int[] small = arr(rnd.nextInt(30), 0, 9);
            int[] sw = small.clone(), sw2 = small.clone();
            Arrays.sort(sw2);
            ArraysVariants.countingSort(sw, 9);
            Check.eq("countingSort", Check.s(sw), Check.s(sw2));
            ArraysVariants.quickSort3(w3);
            Check.eq("quick3", Check.s(w3), Check.s(want0));
        }
        Check.eq("circularAllNeg", ArraysVariants.maxSubarraySumCircular(new int[]{-3, -2, -3}), -2);
        Check.eq("rangeSumEx", ArraysVariants.countRangeSum(new int[]{-2, 5, -1}, -2, 2), 3);
        Check.eq("reverseEx", ArraysVariants.reversePairs(new int[]{1, 3, 2, 3, 1}), 2);
    }

    // ------------------------------------------------------------------ windows
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

    // ------------------------------------------------------------------ stacks
    static int evalExpr(String s, int[] p) { // independent reference evaluator
        int v = term(s, p);
        while (p[0] < s.length() && (s.charAt(p[0]) == '+' || s.charAt(p[0]) == '-')) { char op = s.charAt(p[0]++); int r = term(s, p); v = op == '+' ? v + r : v - r; }
        return v;
    }
    static int term(String s, int[] p) {
        int v = factor(s, p);
        while (p[0] < s.length() && (s.charAt(p[0]) == '*' || s.charAt(p[0]) == '/')) { char op = s.charAt(p[0]++); int r = factor(s, p); v = op == '*' ? v * r : v / r; }
        return v;
    }
    static int factor(String s, int[] p) {
        if (s.charAt(p[0]) == '(') { p[0]++; int v = evalExpr(s, p); p[0]++; return v; }
        int v = 0;
        while (p[0] < s.length() && Character.isDigit(s.charAt(p[0]))) v = v * 10 + s.charAt(p[0]++) - '0';
        return v;
    }
    static String genExpr(int depth) {
        StringBuilder sb = new StringBuilder();
        int terms = 1 + rnd.nextInt(3);
        for (int i = 0; i < terms; i++) {
            if (i > 0) sb.append("+-*/".charAt(rnd.nextInt(4)) == '/' ? "+" : String.valueOf("+-*".charAt(rnd.nextInt(3))));
            sb.append(depth > 0 && rnd.nextInt(3) == 0 ? "(" + genExpr(depth - 1) + ")" : String.valueOf(1 + rnd.nextInt(9)));
        }
        return sb.toString();
    }

    static boolean validParens(String s) { int o = 0; for (char c : s.toCharArray()) { if (c == '(') o++; else if (c == ')' && --o < 0) return false; } return o == 0; }

    static void stacks() {
        for (int t = 0; t < 400; t++) {
            int[] ast = new int[1 + rnd.nextInt(8)];
            for (int i = 0; i < ast.length; i++) { int v = 1 + rnd.nextInt(6); ast[i] = rnd.nextBoolean() ? v : -v; }
            List<Integer> sim = new ArrayList<>();
            for (int v : ast) sim.add(v);
            boolean changed = true;
            while (changed) {
                changed = false;
                for (int i = 0; i + 1 < sim.size(); i++) if (sim.get(i) > 0 && sim.get(i + 1) < 0) {
                    int a = sim.get(i), b = -sim.get(i + 1);
                    if (a > b) sim.remove(i + 1); else if (a < b) sim.remove(i); else { sim.remove(i + 1); sim.remove(i); }
                    changed = true;
                    break;
                }
            }
            Check.eq("asteroid", Check.s(StacksVariants.asteroidCollision(ast)), Check.s(sim.stream().mapToInt(Integer::intValue).toArray()));

            String dup = str(rnd.nextInt(14), 2);
            int k = 2 + rnd.nextInt(2);
            String cur = dup;
            String prev;
            do {
                prev = cur;
                cur = cur.replaceFirst("(a{" + k + "}|b{" + k + "})", "");
            } while (!cur.equals(prev));
            // replaceFirst removes the leftmost run of k; repeating until stable equals the stack result because removals are confluent
            Check.eq("dupII", StacksVariants.removeDuplicates(dup, k), cur);

            StringBuilder sb = new StringBuilder();
            int len = rnd.nextInt(10);
            for (int i = 0; i < len; i++) sb.append("()a".charAt(rnd.nextInt(3)));
            String raw = sb.toString();
            String res = StacksVariants.minRemoveToMakeValid(raw);
            Check.eq("validParens", validParens(res), true);
            int bal = 0, unmatchedClose = 0;
            for (char c : raw.toCharArray()) { if (c == '(') bal++; else if (c == ')') { if (bal > 0) bal--; else unmatchedClose++; } }
            Check.eq("minRemoveLen", res.length(), raw.length() - bal - unmatchedClose);

            String e = genExpr(2);
            Check.eq("calc3 " + e, StacksVariants.calculate3(e), evalExpr(e, new int[]{0}));
            String dv = (1 + rnd.nextInt(9)) + "*" + (1 + rnd.nextInt(9)) + "/" + (1 + rnd.nextInt(9)) + "-(" + (1 + rnd.nextInt(9)) + "+" + (1 + rnd.nextInt(9)) + ")/" + (1 + rnd.nextInt(5));
            Check.eq("calc3div " + dv, StacksVariants.calculate3(dv), evalExpr(dv, new int[]{0}));

            int[] p3 = arr(rnd.nextInt(8), -4, 4);
            boolean f = false;
            for (int i = 0; i < p3.length; i++) for (int j = i + 1; j < p3.length; j++) for (int l = j + 1; l < p3.length; l++) if (p3[i] < p3[l] && p3[l] < p3[j]) f = true;
            Check.eq("132", StacksVariants.find132pattern(p3), f);

            int[] sr = arr(1 + rnd.nextInt(8), -4, 4);
            long srw = 0;
            for (int i = 0; i < sr.length; i++) { int mx = sr[i], mn = sr[i]; for (int j = i; j < sr.length; j++) { mx = Math.max(mx, sr[j]); mn = Math.min(mn, sr[j]); srw += mx - mn; } }
            Check.eq("ranges", StacksVariants.subArrayRanges(sr), srw);

            int hn = 1 + rnd.nextInt(8);
            int[] h = new int[hn];
            for (int i = 0; i < hn; i++) h[i] = i + 1;
            for (int i = hn - 1; i > 0; i--) { int j = rnd.nextInt(i + 1); int tmp = h[i]; h[i] = h[j]; h[j] = tmp; }
            int[] see = new int[hn];
            for (int i = 0; i < hn; i++) for (int j = i + 1; j < hn; j++) {
                boolean ok = true;
                for (int q = i + 1; q < j; q++) if (h[q] > Math.min(h[i], h[j])) ok = false;
                if (ok) see[i]++;
            }
            Check.eq("visible", Check.s(StacksVariants.canSeePersonsCount(h)), Check.s(see));

            int[] hours = arr(1 + rnd.nextInt(9), 4, 12);
            int wb = 0;
            for (int i = 0; i < hours.length; i++) { int bl = 0; for (int j = i; j < hours.length; j++) { bl += hours[j] > 8 ? 1 : -1; if (bl > 0) wb = Math.max(wb, j - i + 1); } }
            Check.eq("wpi", StacksVariants.longestWPI(hours), wb);

            int[] tr = arr(rnd.nextInt(10), 0, 5);
            int water = 0;
            for (int i = 0; i < tr.length; i++) {
                int lm = 0, rm = 0;
                for (int j = 0; j <= i; j++) lm = Math.max(lm, tr[j]);
                for (int j = i; j < tr.length; j++) rm = Math.max(rm, tr[j]);
                water += Math.min(lm, rm) - tr[i];
            }
            Check.eq("trapStack", StacksVariants.trapStack(tr), water);
        }
        // FreqStack versus brute
        for (int t = 0; t < 200; t++) {
            StacksVariants.FreqStack fs = new StacksVariants.FreqStack();
            List<Integer> all = new ArrayList<>();
            for (int op = 0; op < 20; op++) {
                if (all.isEmpty() || rnd.nextInt(3) > 0) { int v = rnd.nextInt(4); fs.push(v); all.add(v); }
                else {
                    int bestF = 0, bestV = -1;
                    for (int i = all.size() - 1; i >= 0; i--) { int f = Collections.frequency(all, all.get(i)); if (f > bestF) { bestF = f; bestV = all.get(i); } }
                    all.remove(all.lastIndexOf(bestV));
                    Check.eq("freqStack", fs.pop(), bestV);
                }
            }
            StacksVariants.MyQueue q = new StacksVariants.MyQueue();
            Deque<Integer> ref = new ArrayDeque<>();
            for (int op = 0; op < 30; op++) {
                if (ref.isEmpty() || rnd.nextInt(3) > 0) { int v = rnd.nextInt(100); q.push(v); ref.add(v); }
                else if (rnd.nextBoolean()) Check.eq("queuePeek", q.peek(), ref.peek());
                else Check.eq("queuePop", q.pop(), ref.poll());
                Check.eq("queueEmpty", q.empty(), ref.isEmpty());
            }
        }
        Check.eq("carFleet", StacksVariants.carFleet(12, new int[]{10, 8, 0, 5, 3}, new int[]{2, 4, 1, 1, 3}), 3);
        Check.eq("score", StacksVariants.scoreOfParentheses("(()(()))"), 6);
        Check.eq("exclusive", Check.s(StacksVariants.exclusiveTime(2, List.of("0:start:0", "1:start:2", "1:end:5", "0:end:6"))), "[3, 4]");
        Check.eq("path", StacksVariants.simplifyPath("/a/./b/../../c/"), "/c");
        Check.eq("pathRoot", StacksVariants.simplifyPath("/../"), "/");
    }

    // ------------------------------------------------------------------ binary search
    static void binarySearch() {
        for (int t = 0; t < 300; t++) {
            int[] a = arr(2 + rnd.nextInt(8), 0, 20);
            List<Integer> dists = new ArrayList<>();
            for (int i = 0; i < a.length; i++) for (int j = i + 1; j < a.length; j++) dists.add(Math.abs(a[i] - a[j]));
            Collections.sort(dists);
            int k = 1 + rnd.nextInt(dists.size());
            Check.eq("pairDistance", BinarySearchVariants.smallestDistancePair(a.clone(), k), dists.get(k - 1));

            int[] dv = arr(1 + rnd.nextInt(6), 1, 30);
            int th = dv.length + rnd.nextInt(40), div = 1;
            while (true) { long s = 0; for (int v : dv) s += (v + div - 1) / div; if (s <= th) break; div++; }
            Check.eq("divisor", BinarySearchVariants.smallestDivisor(dv, th), div);

            int[] pos = new int[2 + rnd.nextInt(7)];
            Set<Integer> used = new TreeSet<>();
            while (used.size() < pos.length) used.add(rnd.nextInt(40));
            int pi = 0;
            for (int v : used) pos[pi++] = v;
            int m = 2 + rnd.nextInt(pos.length - 1), bestD = 0;
            for (int d = 1; d <= 40; d++) { int placed = 1, last = pos[0]; for (int p : pos) if (p - last >= d) { placed++; last = p; } if (placed >= m) bestD = d; }
            Check.eq("aggressive", BinarySearchVariants.maxDistance(pos.clone(), m), bestD);

            int[] miss = new int[1 + rnd.nextInt(7)];
            Set<Integer> ms = new TreeSet<>();
            while (ms.size() < miss.length) ms.add(1 + rnd.nextInt(15));
            int mi = 0;
            for (int v : ms) miss[mi++] = v;
            int kth = 1 + rnd.nextInt(8), cnt = 0, cand = 0;
            while (cnt < kth) { cand++; if (!ms.contains(cand)) cnt++; }
            Check.eq("kthMissing", BinarySearchVariants.findKthPositive(miss, kth), cand);

            int pairs = rnd.nextInt(5), spos = rnd.nextInt(pairs + 1);
            List<Integer> arrL = new ArrayList<>();
            for (int i = 0; i < pairs; i++) { arrL.add(10 + i * 3); arrL.add(10 + i * 3); }
            arrL.add(10 + spos * 3 - 1);
            Collections.sort(arrL);
            Check.eq("single", BinarySearchVariants.singleNonDuplicate(arrL.stream().mapToInt(Integer::intValue).toArray()), 10 + spos * 3 - 1);

            int n = 1 + rnd.nextInt(8), index = rnd.nextInt(n), maxSum = n + rnd.nextInt(30), bv = 1;
            for (int v = 1; v <= maxSum; v++) { long s = 0; for (int i = 0; i < n; i++) s += Math.max(v - Math.abs(i - index), 1); if (s <= maxSum) bv = v; }
            Check.eq("boundedIndex", BinarySearchVariants.maxValue(n, index, maxSum), bv);

            int[] tm = arr(1 + rnd.nextInt(4), 1, 6);
            int trips = 1 + rnd.nextInt(12);
            long tt = 1;
            while (true) { long c = 0; for (int v : tm) c += tt / v; if (c >= trips) break; tt++; }
            Check.eq("trips", BinarySearchVariants.minimumTime(tm, trips), tt);

            int[] mm = arr(2 + rnd.nextInt(6), 0, 15);
            int p = rnd.nextInt(mm.length / 2 + 1);
            Check.eq("minimizeMax", BinarySearchVariants.minimizeMax(mm.clone(), p), bruteMinMax(mm, p));

            int[] ci = arr(1 + rnd.nextInt(8), 0, 9);
            Arrays.sort(ci);
            int hb = 0;
            for (int h = 0; h <= ci.length; h++) { int c = 0; for (int v : ci) if (v >= h) c++; if (c >= h) hb = h; }
            Check.eq("hIndex", BinarySearchVariants.hIndex(ci), hb);

            int in = 1 + rnd.nextInt(6);
            int[][] iv = new int[in][2];
            for (int i = 0; i < in; i++) { iv[i][0] = i * 3 + rnd.nextInt(3) + (rnd.nextInt(4) == 0 ? 0 : 0); iv[i][1] = iv[i][0] + rnd.nextInt(8); }
            Set<Integer> starts = new HashSet<>();
            boolean uniq = true;
            for (int[] e : iv) uniq &= starts.add(e[0]);
            if (uniq) {
                int[] want = new int[in];
                for (int i = 0; i < in; i++) { want[i] = -1; int bs = Integer.MAX_VALUE; for (int j = 0; j < in; j++) if (iv[j][0] >= iv[i][1] && iv[j][0] < bs) { bs = iv[j][0]; want[i] = j; } }
                Check.eq("rightInterval", Check.s(BinarySearchVariants.findRightInterval(iv)), Check.s(want));
            }

            int R = 1 + rnd.nextInt(3), C = 1 + rnd.nextInt(3);
            int[][] mat = new int[R][C];
            for (int[] row : mat) for (int j = 0; j < C; j++) row[j] = rnd.nextInt(11) - 5;
            int kk = rnd.nextInt(8) - 2, mb = Integer.MIN_VALUE;
            for (int r1 = 0; r1 < R; r1++) for (int r2 = r1; r2 < R; r2++) for (int c1 = 0; c1 < C; c1++) for (int c2 = c1; c2 < C; c2++) {
                int s = 0;
                for (int r = r1; r <= r2; r++) for (int c = c1; c <= c2; c++) s += mat[r][c];
                if (s <= kk) mb = Math.max(mb, s);
            }
            if (mb != Integer.MIN_VALUE) Check.eq("submatrix", BinarySearchVariants.maxSumSubmatrix(mat, kk), mb);

            int up = 1 + rnd.nextInt(5), down = 1 + rnd.nextInt(5);
            List<Integer> pool = new ArrayList<>();
            for (int i = 0; i < up + down + 1; i++) pool.add(i * 2);
            Collections.shuffle(pool, rnd);
            List<Integer> upPart = new ArrayList<>(pool.subList(0, up)), downPart = new ArrayList<>(pool.subList(up + 1, pool.size()));
            int peak = Collections.max(pool);
            upPart.removeIf(v -> v == peak);
            downPart.removeIf(v -> v == peak);
            Collections.sort(upPart);
            downPart.sort(Collections.reverseOrder());
            List<Integer> mtn = new ArrayList<>(upPart);
            mtn.add(peak);
            mtn.addAll(downPart);
            int[] ma = mtn.stream().mapToInt(Integer::intValue).toArray();
            for (int target = -1; target <= peak + 1; target++) {
                int w = -1;
                for (int i = 0; i < ma.length; i++) if (ma[i] == target) { w = i; break; }
                Check.eq("mountainSearch", BinarySearchVariants.searchMountain(ma, target), w);
            }
        }
        Check.eq("gas", Math.round(BinarySearchVariants.minmaxGasDist(new int[]{1, 2, 3, 4, 5, 6, 7, 8, 9, 10}, 9) * 1e5) / 1e5, 0.5);
        Check.eq("gas2", Math.round(BinarySearchVariants.minmaxGasDist(new int[]{23, 24, 36, 39, 46, 56, 57, 65, 84, 98}, 1) * 1e5) / 1e5, 14.0);
    }

    static int bruteMinMax(int[] a, int p) {
        int[] s = a.clone();
        Arrays.sort(s);
        return pairUp(s, 0, p, 0);
    }
    // choose p disjoint pairs minimizing the largest difference: lowest unused element is either skipped or paired
    static int pairUp(int[] s, int mask, int p, int curMax) {
        if (p == 0) return curMax;
        int i = 0;
        while (i < s.length && (mask >> i & 1) != 0) i++;
        if (i == s.length) return Integer.MAX_VALUE;
        int best = pairUp(s, mask | 1 << i, p, curMax);
        for (int j = i + 1; j < s.length; j++)
            if ((mask >> j & 1) == 0) best = Math.min(best, pairUp(s, mask | 1 << i | 1 << j, p - 1, Math.max(curMax, Math.abs(s[i] - s[j]))));
        return best;
    }

    // ------------------------------------------------------------------ backtracking
    static void backtracking() {
        for (int t = 0; t < 200; t++) {
            int[] sticks = arr(4 + rnd.nextInt(4), 1, 5);
            int sum = 0;
            for (int v : sticks) sum += v;
            boolean can = false;
            if (sum % 4 == 0) {
                int total = 1;
                for (int i = 0; i < sticks.length; i++) total *= 4;
                for (int code = 0; code < total && !can; code++) {
                    int[] side = new int[4];
                    int c = code;
                    for (int v : sticks) { side[c % 4] += v; c /= 4; }
                    can = side[0] == sum / 4 && side[1] == sum / 4 && side[2] == sum / 4 && side[3] == sum / 4;
                }
            }
            Check.eq("makesquare", BacktrackingVariants.makesquare(sticks), can);

            int k = 1 + rnd.nextInt(4), n = rnd.nextInt(30) + 1, c3 = 0;
            Set<String> want = new TreeSet<>();
            for (int mask = 0; mask < 512; mask++) {
                if (Integer.bitCount(mask) != k) continue;
                int s = 0;
                List<Integer> l = new ArrayList<>();
                for (int d = 0; d < 9; d++) if ((mask >> d & 1) != 0) { s += d + 1; l.add(d + 1); }
                if (s == n) want.add(l.toString());
            }
            Set<String> got = new TreeSet<>();
            for (List<Integer> l : BacktrackingVariants.combinationSum3(k, n)) got.add(l.toString());
            Check.eq("comboSum3", got.toString(), want.toString());

            int pn = 1 + rnd.nextInt(6), pk = 1 + rnd.nextInt(fact(pn));
            List<String> perms = new ArrayList<>();
            permute("", "123456".substring(0, pn), perms);
            Collections.sort(perms);
            Check.eq("permSeq", BacktrackingVariants.getPermutation(pn, pk), perms.get(pk - 1));

            String ps = str(1 + rnd.nextInt(6), 2);
            List<String> allPerms = new ArrayList<>();
            permute("", ps, allPerms);
            Set<String> wp = new TreeSet<>();
            for (String s : allPerms) if (new StringBuilder(s).reverse().toString().equals(s)) wp.add(s);
            List<String> gp = BacktrackingVariants.generatePalindromes(ps);
            Check.eq("palPerm", new TreeSet<>(gp).toString(), wp.toString());
            Check.eq("palPermNoDup", gp.size(), wp.size());

            String lc = str(1 + rnd.nextInt(4), 2) + (rnd.nextBoolean() ? "1" : "") + str(rnd.nextInt(2), 2);
            List<String> lcr = BacktrackingVariants.letterCasePermutation(lc);
            int letters = 0;
            for (char c : lc.toCharArray()) if (Character.isLetter(c)) letters++;
            Check.eq("caseCount", lcr.size(), 1 << letters);
            Check.eq("caseDistinct", new HashSet<>(lcr).size(), lcr.size());

            StringBuilder pb = new StringBuilder();
            int pl = 1 + rnd.nextInt(8);
            for (int i = 0; i < pl; i++) pb.append("()a".charAt(rnd.nextInt(3)));
            String raw = pb.toString();
            int bestLen = -1;
            Set<String> wantInv = new TreeSet<>();
            for (int mask = 0; mask < (1 << raw.length()); mask++) {
                StringBuilder sb = new StringBuilder();
                for (int i = 0; i < raw.length(); i++) if ((mask >> i & 1) != 0) sb.append(raw.charAt(i));
                String sub = sb.toString();
                if (!validParens(sub)) continue;
                if (sub.length() > bestLen) { bestLen = sub.length(); wantInv.clear(); }
                if (sub.length() == bestLen) wantInv.add(sub);
            }
            Check.eq("removeInvalid", new TreeSet<>(BacktrackingVariants.removeInvalidParentheses(raw)).toString(), wantInv.toString());

            List<String> words = new ArrayList<>();
            for (int i = 0; i < 1 + rnd.nextInt(5); i++) words.add(str(1 + rnd.nextInt(4), 5));
            int bl = 0;
            for (int mask = 0; mask < (1 << words.size()); mask++) {
                StringBuilder sb = new StringBuilder();
                for (int i = 0; i < words.size(); i++) if ((mask >> i & 1) != 0) sb.append(words.get(i));
                if (sb.chars().distinct().count() == sb.length()) bl = Math.max(bl, sb.length());
            }
            Check.eq("uniqueConcat", BacktrackingVariants.maxLength(words), bl);

            String ab = str(1 + rnd.nextInt(6), 26);
            List<String> abbr = BacktrackingVariants.generateAbbreviations(ab);
            Check.eq("abbrCount", abbr.size(), 1 << ab.length());
            Check.eq("abbrDistinct", new HashSet<>(abbr).size(), abbr.size());
            for (String a : abbr) {
                int len = 0;
                for (int i = 0; i < a.length(); ) {
                    if (Character.isDigit(a.charAt(i))) { int j = i; while (j < a.length() && Character.isDigit(a.charAt(j))) j++; len += Integer.parseInt(a.substring(i, j)); i = j; } else { len++; i++; }
                }
                Check.eq("abbrLen", len, ab.length());
            }
        }
        // known counts of Android unlock patterns by length 1..9
        int[] byLen = {9, 56, 320, 1624, 7152, 26016, 72912, 140704, 140704};
        int total = 0;
        for (int len = 1; len <= 9; len++) { Check.eq("unlock" + len, BacktrackingVariants.numberOfPatterns(len, len), byLen[len - 1]); total += byLen[len - 1]; }
        Check.eq("unlockAll", BacktrackingVariants.numberOfPatterns(1, 9), total);
        Check.eq("unique3", BacktrackingVariants.uniquePathsIII(new int[][]{{1, 0, 0, 0}, {0, 0, 0, 0}, {0, 0, 2, -1}}), 2);
        Check.eq("unique3b", BacktrackingVariants.uniquePathsIII(new int[][]{{1, 0, 0, 0}, {0, 0, 0, 0}, {0, 0, 0, 2}}), 4);
        List<Integer> dw = BacktrackingVariants.diffWaysToCompute("2*3-4*5");
        Collections.sort(dw);
        Check.eq("diffWays", dw.toString(), "[-34, -14, -10, -10, 10]");
        Check.eq("transfers", BacktrackingVariants.minTransfers(new int[][]{{0, 1, 10}, {2, 0, 5}}), 2);
        Check.eq("transfers2", BacktrackingVariants.minTransfers(new int[][]{{0, 1, 10}, {1, 0, 1}, {1, 2, 5}, {2, 0, 5}}), 1);
        Check.eq("dagPaths", BacktrackingVariants.allPathsSourceTarget(new int[][]{{1, 2}, {3}, {3}, {}}).toString(), "[[0, 1, 3], [0, 2, 3]]");
    }

    static int fact(int n) { return n <= 1 ? 1 : n * fact(n - 1); }
    static void permute(String prefix, String rest, List<String> out) {
        if (rest.isEmpty()) { out.add(prefix); return; }
        for (int i = 0; i < rest.length(); i++) permute(prefix + rest.charAt(i), rest.substring(0, i) + rest.substring(i + 1), out);
    }
}
