import java.util.*;

public class StacksVariants {

    // ---------- Asteroid Collision (LC 735): only a right-mover followed by a left-mover collide ----------
    static int[] asteroidCollision(int[] a) {
        Deque<Integer> st = new ArrayDeque<>();
        for (int x : a) {
            boolean alive = true;
            while (alive && x < 0 && !st.isEmpty() && st.peek() > 0) {
                if (st.peek() < -x) st.pop();             // top is smaller: it explodes, keep colliding
                else { if (st.peek() == -x) st.pop(); alive = false; } // equal: both die; bigger top: x dies
            }
            if (alive) st.push(x);
        }
        int[] res = new int[st.size()];
        for (int i = res.length - 1; i >= 0; i--) res[i] = st.pop();
        return res;
    }

    // ---------- Remove All Adjacent Duplicates II (LC 1209): stack of (char, run length) ----------
    static String removeDuplicates(String s, int k) {
        Deque<int[]> st = new ArrayDeque<>();
        for (char c : s.toCharArray()) {
            if (!st.isEmpty() && st.peek()[0] == c) { if (++st.peek()[1] == k) st.pop(); }
            else st.push(new int[]{c, 1});
        }
        StringBuilder sb = new StringBuilder();
        Iterator<int[]> it = st.descendingIterator();
        while (it.hasNext()) { int[] e = it.next(); for (int i = 0; i < e[1]; i++) sb.append((char) e[0]); }
        return sb.toString();
    }

    // ---------- Car Fleet (LC 853): sort by position desc, a car merges into the fleet ahead if it would arrive no later ----------
    static int carFleet(int target, int[] position, int[] speed) {
        int n = position.length;
        Integer[] idx = new Integer[n];
        for (int i = 0; i < n; i++) idx[i] = i;
        Arrays.sort(idx, (a, b) -> Integer.compare(position[b], position[a]));
        int fleets = 0;
        double lastTime = 0;
        for (int i : idx) {
            double t = (double) (target - position[i]) / speed[i];
            if (t > lastTime) { fleets++; lastTime = t; }
        }
        return fleets;
    }

    // ---------- Score of Parentheses (LC 856): "()" = 1, AB = A + B, (A) = 2A ----------
    static int scoreOfParentheses(String s) {
        Deque<Integer> st = new ArrayDeque<>();
        st.push(0);
        for (char c : s.toCharArray()) {
            if (c == '(') st.push(0);
            else { int v = st.pop(); st.push(st.pop() + Math.max(2 * v, 1)); }
        }
        return st.pop();
    }

    // ---------- Minimum Remove to Make Valid Parentheses (LC 1249): mark indices to drop ----------
    static String minRemoveToMakeValid(String s) {
        boolean[] drop = new boolean[s.length()];
        Deque<Integer> open = new ArrayDeque<>();
        for (int i = 0; i < s.length(); i++) {
            if (s.charAt(i) == '(') open.push(i);
            else if (s.charAt(i) == ')') { if (open.isEmpty()) drop[i] = true; else open.pop(); }
        }
        while (!open.isEmpty()) drop[open.pop()] = true;
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < s.length(); i++) if (!drop[i]) sb.append(s.charAt(i));
        return sb.toString();
    }

    // ---------- Basic Calculator III (LC 772): + - * / and parentheses, recursive descent over one index ----------
    // Static cursor: reset at each calculate3 entry, but NOT reentrant / thread-safe (one call at a time).
    private static int pos;

    static int calculate3(String s) {
        pos = 0;
        return expr(s);
    }

    private static int expr(String s) {
        Deque<Integer> st = new ArrayDeque<>();
        int num = 0;
        char op = '+';
        while (pos < s.length()) {
            char c = s.charAt(pos++);
            if (Character.isDigit(c)) num = num * 10 + (c - '0');
            else if (c == '(') num = expr(s);
            if ((!Character.isDigit(c) && c != ' ' && c != '(') || pos == s.length()) {
                if (op == '+') st.push(num);
                else if (op == '-') st.push(-num);
                else if (op == '*') st.push(st.pop() * num);
                else if (op == '/') st.push(st.pop() / num);
                op = c;
                num = 0;
                if (c == ')') break;
            }
        }
        int sum = 0;
        for (int v : st) sum += v;
        return sum;
    }

    // ---------- Exclusive Time of Functions (LC 636): stack of running function ids ----------
    static int[] exclusiveTime(int n, List<String> logs) {
        int[] res = new int[n];
        Deque<Integer> st = new ArrayDeque<>();
        int prev = 0;
        for (String log : logs) {
            String[] p = log.split(":");
            int id = Integer.parseInt(p[0]), t = Integer.parseInt(p[2]);
            if (p[1].equals("start")) {
                if (!st.isEmpty()) res[st.peek()] += t - prev;
                st.push(id);
                prev = t;
            } else {
                res[st.pop()] += t - prev + 1; // end is inclusive of its timestamp
                prev = t + 1;
            }
        }
        return res;
    }

    // ---------- Simplify Path (LC 71) ----------
    static String simplifyPath(String path) {
        Deque<String> st = new ArrayDeque<>();
        for (String part : path.split("/")) {
            if (part.isEmpty() || part.equals(".")) continue;
            if (part.equals("..")) { if (!st.isEmpty()) st.pop(); } else st.push(part);
        }
        if (st.isEmpty()) return "/";
        StringBuilder sb = new StringBuilder();
        Iterator<String> it = st.descendingIterator();
        while (it.hasNext()) sb.append('/').append(it.next());
        return sb.toString();
    }

    // ---------- 132 Pattern (LC 456): scan right to left, stack keeps candidate "3"s, `third` is the best "2" ----------
    static boolean find132pattern(int[] a) {
        Deque<Integer> st = new ArrayDeque<>();
        int third = Integer.MIN_VALUE;
        for (int i = a.length - 1; i >= 0; i--) {
            if (a[i] < third) return true;
            while (!st.isEmpty() && st.peek() < a[i]) third = st.pop();
            st.push(a[i]);
        }
        return false;
    }

    // ---------- Sum of Subarray Ranges (LC 2104): sum of (max - min) = contribution of each element as max minus as min ----------
    static long subArrayRanges(int[] a) { return sumContribution(a, true) - sumContribution(a, false); }

    private static long sumContribution(int[] a, boolean asMax) {
        int n = a.length;
        int[] left = new int[n], right = new int[n];
        Deque<Integer> st = new ArrayDeque<>();
        for (int i = 0; i < n; i++) { // strict on the left, non-strict on the right: every tie is counted exactly once
            while (!st.isEmpty() && (asMax ? a[st.peek()] <= a[i] : a[st.peek()] >= a[i])) st.pop();
            left[i] = st.isEmpty() ? -1 : st.peek();
            st.push(i);
        }
        st.clear();
        for (int i = n - 1; i >= 0; i--) {
            while (!st.isEmpty() && (asMax ? a[st.peek()] < a[i] : a[st.peek()] > a[i])) st.pop();
            right[i] = st.isEmpty() ? n : st.peek();
            st.push(i);
        }
        long sum = 0;
        for (int i = 0; i < n; i++) sum += (long) a[i] * (i - left[i]) * (right[i] - i);
        return sum;
    }

    // ---------- Number of Visible People in a Queue (LC 1944): decreasing stack, each pop is one visible person ----------
    static int[] canSeePersonsCount(int[] h) {
        int n = h.length;
        int[] res = new int[n];
        Deque<Integer> st = new ArrayDeque<>();
        for (int i = n - 1; i >= 0; i--) {
            while (!st.isEmpty() && st.peek() < h[i]) { st.pop(); res[i]++; }
            if (!st.isEmpty()) res[i]++; // the first person at least as tall also blocks the view
            st.push(h[i]);
        }
        return res;
    }

    // ---------- Maximum Frequency Stack (LC 895): freq -> stack of values, track maxFreq ----------
    static class FreqStack {
        private final Map<Integer, Integer> freq = new HashMap<>();
        private final Map<Integer, Deque<Integer>> byFreq = new HashMap<>();
        private int maxFreq;
        void push(int x) {
            int f = freq.merge(x, 1, Integer::sum);
            maxFreq = Math.max(maxFreq, f);
            byFreq.computeIfAbsent(f, k -> new ArrayDeque<>()).push(x);
        }
        int pop() {
            int x = byFreq.get(maxFreq).pop();
            if (byFreq.get(maxFreq).isEmpty()) maxFreq--;
            freq.merge(x, -1, Integer::sum);
            return x;
        }
    }

    // ---------- Queue using two stacks (LC 232): amortized O(1) by moving only when the output stack is empty ----------
    static class MyQueue {
        private final Deque<Integer> in = new ArrayDeque<>(), out = new ArrayDeque<>();
        void push(int x) { in.push(x); }
        int pop() { peek(); return out.pop(); }
        int peek() { if (out.isEmpty()) while (!in.isEmpty()) out.push(in.pop()); return out.peek(); }
        boolean empty() { return in.isEmpty() && out.isEmpty(); }
    }

    // ---------- Longest Well-Performing Interval (LC 1124): +1 / -1 balance, earliest index of each balance or a decreasing stack ----------
    static int longestWPI(int[] hours) {
        int n = hours.length, best = 0, bal = 0;
        Map<Integer, Integer> first = new HashMap<>();
        for (int i = 0; i < n; i++) {
            bal += hours[i] > 8 ? 1 : -1;
            if (bal > 0) best = i + 1;
            else {
                first.putIfAbsent(bal, i);
                Integer j = first.get(bal - 1); // need a strictly smaller earlier balance; balances change by 1, so bal - 1 is the first candidate
                if (j != null) best = Math.max(best, i - j);
            }
        }
        return best;
    }

    // ---------- Trapping Rain Water, stack form (LC 42): when a taller bar arrives, fill the basin on top of the popped bar ----------
    static int trapStack(int[] h) {
        Deque<Integer> st = new ArrayDeque<>();
        int water = 0;
        for (int i = 0; i < h.length; i++) {
            while (!st.isEmpty() && h[st.peek()] < h[i]) {
                int bottom = st.pop();
                if (st.isEmpty()) break;
                int width = i - st.peek() - 1, height = Math.min(h[i], h[st.peek()]) - h[bottom];
                water += width * height;
            }
            st.push(i);
        }
        return water;
    }
}
