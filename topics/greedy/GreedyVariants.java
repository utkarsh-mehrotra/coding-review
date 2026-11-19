import java.util.*;

public class GreedyVariants {

    // Each entry: the greedy choice, then WHY it is safe (the exchange argument in one line).

    // Maximize Sum After K Negations (LC 1005): always flip the current smallest value (a negative if any remains)
    static int largestSumAfterKNegations(int[] a, int k) {
        PriorityQueue<Integer> pq = new PriorityQueue<>();
        for (int x : a) pq.add(x);
        while (k-- > 0) pq.add(-pq.poll());
        int sum = 0;
        for (int x : pq) sum += x;
        return sum;
    }

    // Wiggle Subsequence (LC 376): count direction changes (a peak or valley is always worth keeping)
    static int wiggleMaxLength(int[] a) {
        if (a.length < 2) return a.length;
        int up = 1, down = 1;
        for (int i = 1; i < a.length; i++) {
            if (a[i] > a[i - 1]) up = down + 1;
            else if (a[i] < a[i - 1]) down = up + 1;
        }
        return Math.max(up, down);
    }

    // Remove Duplicate Letters / Smallest Subsequence of Distinct Characters (LC 316 / 1081)
    // Pop a bigger letter off the stack only if it appears again later; skip letters already on the stack.
    static String removeDuplicateLetters(String s) {
        int[] last = new int[26];
        for (int i = 0; i < s.length(); i++) last[s.charAt(i) - 'a'] = i;
        boolean[] in = new boolean[26];
        StringBuilder st = new StringBuilder();
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            if (in[c - 'a']) continue;
            while (st.length() > 0 && st.charAt(st.length() - 1) > c && last[st.charAt(st.length() - 1) - 'a'] > i) {
                in[st.charAt(st.length() - 1) - 'a'] = false;
                st.setLength(st.length() - 1);
            }
            st.append(c);
            in[c - 'a'] = true;
        }
        return st.toString();
    }

    // Two City Scheduling (LC 1029): send everyone to A, then move the n people with the smallest (costB - costA)
    static int twoCitySchedCost(int[][] costs) {
        Arrays.sort(costs, (x, y) -> Integer.compare(x[1] - x[0], y[1] - y[0]));
        int n = costs.length / 2, total = 0;
        for (int i = 0; i < costs.length; i++) total += i < n ? costs[i][1] : costs[i][0];
        return total;
    }

    // Bag of Tokens (LC 948): spend power on the cheapest token, gain it back by selling the dearest
    static int bagOfTokensScore(int[] tokens, int power) {
        Arrays.sort(tokens);
        int l = 0, r = tokens.length - 1, score = 0, best = 0;
        while (l <= r) {
            if (power >= tokens[l]) { power -= tokens[l++]; best = Math.max(best, ++score); }
            else if (score > 0) { power += tokens[r--]; score--; }
            else break;
        }
        return best;
    }

    // Broken Calculator (LC 991): go BACKWARD from target: halve if even, else add one
    static int brokenCalc(int start, int target) {
        int ops = 0;
        while (target > start) { target = target % 2 == 0 ? target / 2 : target + 1; ops++; }
        return ops + start - target;
    }

    // Monotone Increasing Digits (LC 738): find the first drop, decrement it, fill the rest with 9s
    static int monotoneIncreasingDigits(int n) {
        char[] d = String.valueOf(n).toCharArray();
        int mark = d.length;
        for (int i = d.length - 1; i > 0; i--) if (d[i - 1] > d[i]) { d[i - 1]--; mark = i; }
        for (int i = mark; i < d.length; i++) d[i] = '9';
        return Integer.parseInt(new String(d));
    }

    // Split Array into Consecutive Subsequences (LC 659): append to an existing run if possible, else start a new run of 3
    static boolean isPossibleSplit(int[] nums) {
        Map<Integer, Integer> count = new HashMap<>(), tail = new HashMap<>();
        for (int x : nums) count.merge(x, 1, Integer::sum);
        for (int x : nums) {
            if (count.get(x) == 0) continue;
            count.merge(x, -1, Integer::sum);
            if (tail.getOrDefault(x - 1, 0) > 0) { // extend a run ending at x - 1
                tail.merge(x - 1, -1, Integer::sum);
                tail.merge(x, 1, Integer::sum);
            } else if (count.getOrDefault(x + 1, 0) > 0 && count.getOrDefault(x + 2, 0) > 0) {
                count.merge(x + 1, -1, Integer::sum);
                count.merge(x + 2, -1, Integer::sum);
                tail.merge(x + 2, 1, Integer::sum);
            } else return false;
        }
        return true;
    }

    // Maximum Swap (LC 670): swap the first digit that has a larger digit to its right with the LAST occurrence of that larger digit
    static int maximumSwap(int num) {
        char[] d = String.valueOf(num).toCharArray();
        int[] last = new int[10];
        for (int i = 0; i < d.length; i++) last[d[i] - '0'] = i;
        for (int i = 0; i < d.length; i++)
            for (int v = 9; v > d[i] - '0'; v--)
                if (last[v] > i) {
                    char t = d[i]; d[i] = d[last[v]]; d[last[v]] = t;
                    return Integer.parseInt(new String(d));
                }
        return num;
    }

    // Non-decreasing Array with at most one change (LC 665): on a drop prefer lowering a[i - 1]; else raise a[i]
    static boolean checkPossibility(int[] a) {
        int[] b = a.clone();
        int changes = 0;
        for (int i = 1; i < b.length; i++) {
            if (b[i - 1] <= b[i]) continue;
            if (++changes > 1) return false;
            if (i < 2 || b[i - 2] <= b[i]) b[i - 1] = b[i]; else b[i] = b[i - 1];
        }
        return true;
    }

    // Advantage Shuffle (LC 870): beat each of B with the smallest A that wins; throw leftovers at what cannot be beaten
    static int[] advantageCount(int[] a, int[] b) {
        int n = a.length;
        int[] sa = a.clone();
        Arrays.sort(sa);
        Integer[] idx = new Integer[n];
        for (int i = 0; i < n; i++) idx[i] = i;
        Arrays.sort(idx, (x, y) -> Integer.compare(b[x], b[y]));
        int[] res = new int[n];
        int lo = 0, hi = n - 1;
        for (int i = n - 1; i >= 0; i--) { // largest B first: use the largest A if it wins, else sacrifice the smallest A
            int bi = idx[i];
            if (sa[hi] > b[bi]) res[bi] = sa[hi--]; else res[bi] = sa[lo++];
        }
        return res;
    }

    // Fractional Knapsack: sort by value / weight (the only knapsack where greedy is optimal)
    static double fractionalKnapsack(int[] wt, int[] val, int cap) {
        int n = wt.length;
        Integer[] idx = new Integer[n];
        for (int i = 0; i < n; i++) idx[i] = i;
        Arrays.sort(idx, (x, y) -> Double.compare((double) val[y] / wt[y], (double) val[x] / wt[x]));
        double total = 0;
        for (int i : idx) {
            int take = Math.min(wt[i], cap);
            total += (double) val[i] * take / wt[i];
            cap -= take;
            if (cap == 0) break;
        }
        return total;
    }

    // Minimum Domino Rotations (LC 1007): only tops[0] or bottoms[0] can be the final value
    static int minDominoRotations(int[] tops, int[] bottoms) {
        int best = Math.min(rotations(tops, bottoms, tops[0]), rotations(tops, bottoms, bottoms[0]));
        return best == Integer.MAX_VALUE ? -1 : best;
    }

    private static int rotations(int[] t, int[] b, int target) {
        int topSwaps = 0, botSwaps = 0;
        for (int i = 0; i < t.length; i++) {
            if (t[i] != target && b[i] != target) return Integer.MAX_VALUE;
            if (t[i] != target) topSwaps++;
            if (b[i] != target) botSwaps++;
        }
        return Math.min(topSwaps, botSwaps);
    }

    // Create Maximum Number (LC 321): best i digits of a + best k-i of b, merge by lexicographic suffix comparison
    static int[] maxNumber(int[] a, int[] b, int k) {
        int[] best = new int[k];
        for (int i = Math.max(0, k - b.length); i <= Math.min(k, a.length); i++) {
            int[] cand = merge(pick(a, i), pick(b, k - i));
            if (greater(cand, 0, best, 0)) best = cand;
        }
        return best;
    }

    private static int[] pick(int[] a, int k) { // largest subsequence of length k
        int[] st = new int[k];
        int top = 0, drop = a.length - k;
        for (int x : a) {
            while (drop > 0 && top > 0 && st[top - 1] < x) { top--; drop--; }
            if (top < k) st[top++] = x; else drop--;
        }
        return st;
    }

    private static int[] merge(int[] a, int[] b) {
        int[] res = new int[a.length + b.length];
        int i = 0, j = 0;
        for (int r = 0; r < res.length; r++) res[r] = greater(a, i, b, j) ? a[i++] : b[j++];
        return res;
    }

    private static boolean greater(int[] a, int i, int[] b, int j) { // is suffix a[i..] lexicographically > b[j..]?
        while (i < a.length && j < b.length && a[i] == b[j]) { i++; j++; }
        return j == b.length || (i < a.length && a[i] > b[j]);
    }

    // Minimum Add to Make Parentheses Valid (LC 921)
    static int minAddToMakeValid(String s) {
        int open = 0, add = 0;
        for (char c : s.toCharArray()) {
            if (c == '(') open++; else if (open > 0) open--; else add++;
        }
        return add + open;
    }

    // Minimum Number of Increments on Subarrays to Form Target (LC 1526): sum of positive steps up
    static int minNumberOperations(int[] target) {
        int ops = target[0];
        for (int i = 1; i < target.length; i++) ops += Math.max(0, target[i] - target[i - 1]);
        return ops;
    }

    // Maximum Length of Pair Chain (LC 646): sort by END, take whenever compatible
    static int findLongestChain(int[][] pairs) {
        Arrays.sort(pairs, (x, y) -> Integer.compare(x[1], y[1]));
        int count = 0;
        long end = Long.MIN_VALUE;
        for (int[] p : pairs) if (p[0] > end) { count++; end = p[1]; }
        return count;
    }
}
