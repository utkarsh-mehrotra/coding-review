import java.util.*;

public class StacksVariantsTest {
    public static void main(String[] x) {
        stacks();
        Check.done("StacksVariantsTest");
    }

    static Random rnd = new Random(20260316);
    static int[] arr(int n, int lo, int hi) { int[] a = new int[n]; for (int i = 0; i < n; i++) a[i] = lo + rnd.nextInt(hi - lo + 1); return a; }
    static String str(int n, int alpha) { StringBuilder sb = new StringBuilder(); for (int i = 0; i < n; i++) sb.append((char) ('a' + rnd.nextInt(alpha))); return sb.toString(); }
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
}
