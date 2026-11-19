import java.util.*;

public class MonotonicStructures {

    // ===== MONOTONIC STACK =====
    // Question it answers: "for each i, where is the nearest element to the left/right that is bigger/smaller?"
    // Keep indices on the stack in monotone order; pop while the new element breaks the order -- the pop IS the answer.

    // Next Greater Element (LC 496 / 503 circular via 2n pass) (partial: the core of LC 496, a stack pass over one array; LC 496 itself also maps nums1 into nums2, and the circular variant is nextGreaterCircular)
    static int[] nextGreater(int[] a) {
        int n = a.length;
        int[] res = new int[n];
        Arrays.fill(res, -1);
        Deque<Integer> st = new ArrayDeque<>(); // indices with decreasing values
        for (int i = 0; i < n; i++) {
            while (!st.isEmpty() && a[st.peek()] < a[i]) res[st.pop()] = a[i];
            st.push(i);
        }
        return res;
    }

    static int[] nextGreaterCircular(int[] a) {
        int n = a.length;
        int[] res = new int[n];
        Arrays.fill(res, -1);
        Deque<Integer> st = new ArrayDeque<>();
        for (int i = 0; i < 2 * n; i++) {
            int x = a[i % n];
            while (!st.isEmpty() && a[st.peek()] < x) res[st.pop()] = x;
            if (i < n) st.push(i);
        }
        return res;
    }

    // Daily Temperatures (LC 739): distance to next warmer day
    static int[] dailyTemperatures(int[] t) {
        int[] res = new int[t.length];
        Deque<Integer> st = new ArrayDeque<>();
        for (int i = 0; i < t.length; i++) {
            while (!st.isEmpty() && t[st.peek()] < t[i]) { int j = st.pop(); res[j] = i - j; }
            st.push(i);
        }
        return res;
    }

    // Largest Rectangle in Histogram (LC 84): sentinel 0 at the end flushes the stack
    static int largestRectangleArea(int[] h) {
        Deque<Integer> st = new ArrayDeque<>();
        int best = 0;
        for (int i = 0; i <= h.length; i++) {
            int cur = i == h.length ? 0 : h[i];
            while (!st.isEmpty() && h[st.peek()] >= cur) {
                int height = h[st.pop()];
                int left = st.isEmpty() ? -1 : st.peek();
                best = Math.max(best, height * (i - left - 1));
            }
            st.push(i);
        }
        return best;
    }

    // Maximal Rectangle (LC 85): histogram per row
    static int maximalRectangle(char[][] g) {
        if (g.length == 0) return 0;
        int[] h = new int[g[0].length];
        int best = 0;
        for (char[] row : g) {
            for (int j = 0; j < row.length; j++) h[j] = row[j] == '1' ? h[j] + 1 : 0;
            best = Math.max(best, largestRectangleArea(h));
        }
        return best;
    }

    // Sum of Subarray Minimums (LC 907): contribution = a[i] * (i - prevLessOrEqual) * (nextLess - i)
    static int sumSubarrayMins(int[] a) {
        int n = a.length;
        long MOD = 1_000_000_007L, res = 0;
        int[] left = new int[n], right = new int[n];
        Deque<Integer> st = new ArrayDeque<>();
        for (int i = 0; i < n; i++) {
            while (!st.isEmpty() && a[st.peek()] > a[i]) st.pop();
            left[i] = st.isEmpty() ? -1 : st.peek();
            st.push(i);
        }
        st.clear();
        for (int i = n - 1; i >= 0; i--) {
            while (!st.isEmpty() && a[st.peek()] >= a[i]) st.pop();
            right[i] = st.isEmpty() ? n : st.peek();
            st.push(i);
        }
        for (int i = 0; i < n; i++) res = (res + (long) a[i] * (i - left[i]) % MOD * (right[i] - i)) % MOD;
        return (int) res;
    }

    // Remove K Digits (LC 402) / smallest subsequence: greedy with stack
    static String removeKdigits(String num, int k) {
        StringBuilder st = new StringBuilder();
        for (char c : num.toCharArray()) {
            while (k > 0 && st.length() > 0 && st.charAt(st.length() - 1) > c) { st.setLength(st.length() - 1); k--; }
            st.append(c);
        }
        st.setLength(Math.max(0, st.length() - k));
        int i = 0;
        while (i < st.length() - 1 && st.charAt(i) == '0') i++;
        String res = st.substring(i);
        return res.isEmpty() ? "0" : res;
    }

    // Online Stock Span (LC 901)
    static class StockSpanner {
        private final Deque<int[]> st = new ArrayDeque<>(); // {price, span}
        int next(int price) {
            int span = 1;
            while (!st.isEmpty() && st.peek()[0] <= price) span += st.pop()[1];
            st.push(new int[]{price, span});
            return span;
        }
    }

    // ===== STACK AS PARSER =====
    // Valid Parentheses (LC 20)
    static boolean isValid(String s) {
        Deque<Character> st = new ArrayDeque<>();
        for (char c : s.toCharArray()) {
            if (c == '(') st.push(')'); else if (c == '[') st.push(']'); else if (c == '{') st.push('}');
            else if (st.isEmpty() || st.pop() != c) return false;
        }
        return st.isEmpty();
    }

    // Longest Valid Parentheses (LC 32): stack of indices with a base sentinel
    static int longestValidParentheses(String s) {
        Deque<Integer> st = new ArrayDeque<>();
        st.push(-1);
        int best = 0;
        for (int i = 0; i < s.length(); i++) {
            if (s.charAt(i) == '(') st.push(i);
            else {
                st.pop();
                if (st.isEmpty()) st.push(i); else best = Math.max(best, i - st.peek());
            }
        }
        return best;
    }

    // Basic Calculator I (LC 224): + - ( ) with a sign stack
    static int calculate(String s) {
        Deque<Integer> st = new ArrayDeque<>();
        int res = 0, num = 0, sign = 1;
        for (char c : s.toCharArray()) {
            if (Character.isDigit(c)) num = num * 10 + (c - '0');
            else if (c == '+' || c == '-') { res += sign * num; num = 0; sign = c == '+' ? 1 : -1; }
            else if (c == '(') { st.push(res); st.push(sign); res = 0; sign = 1; }
            else if (c == ')') { res += sign * num; num = 0; res *= st.pop(); res += st.pop(); }
        }
        return res + sign * num;
    }

    // Basic Calculator II (LC 227): + - * / precedence
    static int calculate2(String s) {
        Deque<Integer> st = new ArrayDeque<>();
        int num = 0;
        char op = '+';
        for (int i = 0; i <= s.length(); i++) {
            char c = i < s.length() ? s.charAt(i) : '+';
            if (Character.isDigit(c)) { num = num * 10 + (c - '0'); continue; }
            if (c == ' ') continue;
            if (op == '+') st.push(num);
            else if (op == '-') st.push(-num);
            else if (op == '*') st.push(st.pop() * num);
            else st.push(st.pop() / num);
            op = c;
            num = 0;
        }
        int sum = 0;
        for (int x : st) sum += x;
        return sum;
    }

    // Decode String (LC 394): "3[a2[c]]" -> "accaccacc"
    static String decodeString(String s) {
        Deque<StringBuilder> strs = new ArrayDeque<>();
        Deque<Integer> counts = new ArrayDeque<>();
        StringBuilder cur = new StringBuilder();
        int k = 0;
        for (char c : s.toCharArray()) {
            if (Character.isDigit(c)) k = k * 10 + (c - '0');
            else if (c == '[') { counts.push(k); strs.push(cur); cur = new StringBuilder(); k = 0; }
            else if (c == ']') {
                StringBuilder prev = strs.pop();
                for (int t = counts.pop(); t > 0; t--) prev.append(cur);
                cur = prev;
            } else cur.append(c);
        }
        return cur.toString();
    }

    // Evaluate RPN (LC 150)
    static int evalRPN(String[] tokens) {
        Deque<Integer> st = new ArrayDeque<>();
        for (String t : tokens) {
            switch (t) {
                case "+": st.push(st.pop() + st.pop()); break;
                case "*": st.push(st.pop() * st.pop()); break;
                case "-": { int b = st.pop(), a = st.pop(); st.push(a - b); break; }
                case "/": { int b = st.pop(), a = st.pop(); st.push(a / b); break; }
                default: st.push(Integer.parseInt(t));
            }
        }
        return st.pop();
    }

    // Min Stack (LC 155): store (value, min so far)
    static class MinStack {
        private final Deque<int[]> st = new ArrayDeque<>();
        void push(int x) { st.push(new int[]{x, st.isEmpty() ? x : Math.min(x, st.peek()[1])}); }
        void pop() { st.pop(); }
        int top() { return st.peek()[0]; }
        int getMin() { return st.peek()[1]; }
    }

    // ===== MONOTONIC DEQUE: sliding window maximum (LC 239) =====
    static int[] maxSlidingWindow(int[] a, int k) {
        int n = a.length;
        int[] res = new int[n - k + 1];
        Deque<Integer> dq = new ArrayDeque<>(); // indices, values decreasing
        for (int i = 0; i < n; i++) {
            while (!dq.isEmpty() && dq.peekFirst() <= i - k) dq.pollFirst();
            while (!dq.isEmpty() && a[dq.peekLast()] <= a[i]) dq.pollLast();
            dq.addLast(i);
            if (i >= k - 1) res[i - k + 1] = a[dq.peekFirst()];
        }
        return res;
    }

    // Shortest Subarray with Sum at Least K (LC 862): negatives allowed -> monotonic deque over prefix sums
    static int shortestSubarray(int[] a, int k) {
        int n = a.length;
        long[] p = new long[n + 1];
        for (int i = 0; i < n; i++) p[i + 1] = p[i] + a[i];
        Deque<Integer> dq = new ArrayDeque<>();
        int best = Integer.MAX_VALUE;
        for (int i = 0; i <= n; i++) {
            while (!dq.isEmpty() && p[i] - p[dq.peekFirst()] >= k) best = Math.min(best, i - dq.pollFirst());
            while (!dq.isEmpty() && p[dq.peekLast()] >= p[i]) dq.pollLast();
            dq.addLast(i);
        }
        return best == Integer.MAX_VALUE ? -1 : best;
    }

    // Longest Subarray with |max - min| <= limit (LC 1438): two deques
    static int longestSubarray(int[] a, int limit) {
        Deque<Integer> mx = new ArrayDeque<>(), mn = new ArrayDeque<>();
        int l = 0, best = 0;
        for (int r = 0; r < a.length; r++) {
            while (!mx.isEmpty() && mx.peekLast() < a[r]) mx.pollLast();
            while (!mn.isEmpty() && mn.peekLast() > a[r]) mn.pollLast();
            mx.addLast(a[r]);
            mn.addLast(a[r]);
            while (mx.peekFirst() - mn.peekFirst() > limit) {
                if (mx.peekFirst() == a[l]) mx.pollFirst();
                if (mn.peekFirst() == a[l]) mn.pollFirst();
                l++;
            }
            best = Math.max(best, r - l + 1);
        }
        return best;
    }
}
