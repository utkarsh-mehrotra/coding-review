import java.util.*;

public class Greedy {

    // PROOF HABIT: greedy works when an exchange argument holds (swap the optimal's choice for yours without loss)
    // or when a "stays ahead" invariant holds. If you cannot argue it in one sentence, think DP.

    // Jump Game (LC 55): furthest reachable
    static boolean canJump(int[] a) {
        int far = 0;
        for (int i = 0; i < a.length; i++) {
            if (i > far) return false;
            far = Math.max(far, i + a[i]);
        }
        return true;
    }

    // Jump Game II (LC 45): BFS levels implicitly -- extend the frontier when the current one is exhausted
    static int jump(int[] a) {
        int jumps = 0, curEnd = 0, far = 0;
        for (int i = 0; i < a.length - 1; i++) {
            far = Math.max(far, i + a[i]);
            if (i == curEnd) { jumps++; curEnd = far; }
        }
        return jumps;
    }

    // Gas Station (LC 134): if total >= 0 a start exists; restart after any prefix that goes negative
    static int canCompleteCircuit(int[] gas, int[] cost) {
        int total = 0, tank = 0, start = 0;
        for (int i = 0; i < gas.length; i++) {
            int d = gas[i] - cost[i];
            total += d;
            tank += d;
            if (tank < 0) { start = i + 1; tank = 0; }
        }
        return total >= 0 ? start : -1;
    }

    // Candy (LC 135): left pass, right pass
    static int candy(int[] r) {
        int n = r.length;
        int[] c = new int[n];
        Arrays.fill(c, 1);
        for (int i = 1; i < n; i++) if (r[i] > r[i - 1]) c[i] = c[i - 1] + 1;
        for (int i = n - 2; i >= 0; i--) if (r[i] > r[i + 1]) c[i] = Math.max(c[i], c[i + 1] + 1);
        int sum = 0;
        for (int x : c) sum += x;
        return sum;
    }

    // Partition Labels (LC 763): extend to the last occurrence
    static List<Integer> partitionLabels(String s) {
        int[] last = new int[26];
        for (int i = 0; i < s.length(); i++) last[s.charAt(i) - 'a'] = i;
        List<Integer> res = new ArrayList<>();
        int start = 0, end = 0;
        for (int i = 0; i < s.length(); i++) {
            end = Math.max(end, last[s.charAt(i) - 'a']);
            if (i == end) { res.add(end - start + 1); start = i + 1; }
        }
        return res;
    }

    // Queue Reconstruction by Height (LC 406): tallest first, insert at index k
    static int[][] reconstructQueue(int[][] people) {
        Arrays.sort(people, (a, b) -> a[0] != b[0] ? b[0] - a[0] : a[1] - b[1]);
        List<int[]> out = new ArrayList<>();
        for (int[] p : people) out.add(p[1], p);
        return out.toArray(new int[0][]);
    }

    // Assign Cookies (LC 455): smallest cookie that satisfies the least greedy child
    static int findContentChildren(int[] g, int[] s) {
        Arrays.sort(g);
        Arrays.sort(s);
        int i = 0;
        for (int j = 0; j < s.length && i < g.length; j++) if (s[j] >= g[i]) i++;
        return i;
    }

    // Largest Number (LC 179): comparator a+b vs b+a
    static String largestNumber(int[] a) {
        String[] s = new String[a.length];
        for (int i = 0; i < a.length; i++) s[i] = String.valueOf(a[i]);
        Arrays.sort(s, (x, y) -> (y + x).compareTo(x + y));
        if (s[0].equals("0")) return "0";
        return String.join("", s);
    }

    // Task order minimizing lateness / weighted completion: sort by ratio (exchange argument)
    // Example: Minimum Cost to Hire Workers style ratios live in HeapsTopK; here: Maximum units on a truck (LC 1710)
    static int maximumUnits(int[][] boxes, int truckSize) {
        Arrays.sort(boxes, (a, b) -> b[1] - a[1]);
        int units = 0;
        for (int[] b : boxes) {
            int take = Math.min(b[0], truckSize);
            units += take * b[1];
            truckSize -= take;
            if (truckSize == 0) break;
        }
        return units;
    }

    // Valid Parenthesis String (LC 678): track the range [lo, hi] of possible open counts
    static boolean checkValidString(String s) {
        int lo = 0, hi = 0;
        for (char c : s.toCharArray()) {
            lo += c == '(' ? 1 : -1;
            hi += c == ')' ? -1 : 1;
            if (hi < 0) return false;
            lo = Math.max(lo, 0);
        }
        return lo == 0;
    }

    // Boats to Save People (LC 881): pair heaviest with lightest
    static int numRescueBoats(int[] p, int limit) {
        Arrays.sort(p);
        int i = 0, j = p.length - 1, boats = 0;
        while (i <= j) { if (p[i] + p[j] <= limit) i++; j--; boats++; }
        return boats;
    }

    // Minimum Number of Refueling Stops (LC 871): greedy with a max-heap of passed stations
    static int minRefuelStops(int target, int startFuel, int[][] stations) {
        PriorityQueue<Integer> pq = new PriorityQueue<>(Collections.reverseOrder());
        long fuel = startFuel;
        int stops = 0, i = 0;
        while (fuel < target) {
            while (i < stations.length && stations[i][0] <= fuel) pq.add(stations[i++][1]);
            if (pq.isEmpty()) return -1;
            fuel += pq.poll();
            stops++;
        }
        return stops;
    }

    // Hand of Straights (LC 846): always start from the smallest remaining card
    static boolean isNStraightHand(int[] hand, int w) {
        if (hand.length % w != 0) return false;
        TreeMap<Integer, Integer> cnt = new TreeMap<>();
        for (int h : hand) cnt.merge(h, 1, Integer::sum);
        while (!cnt.isEmpty()) {
            int first = cnt.firstKey();
            for (int c = first; c < first + w; c++) {
                Integer v = cnt.get(c);
                if (v == null) return false;
                if (v == 1) cnt.remove(c); else cnt.put(c, v - 1);
            }
        }
        return true;
    }

    // Lemonade Change (LC 860)
    static boolean lemonadeChange(int[] bills) {
        int five = 0, ten = 0;
        for (int b : bills) {
            if (b == 5) five++;
            else if (b == 10) { if (five == 0) return false; five--; ten++; }
            else {
                if (ten > 0 && five > 0) { ten--; five--; }
                else if (five >= 3) five -= 3;
                else return false;
            }
        }
        return true;
    }
}
