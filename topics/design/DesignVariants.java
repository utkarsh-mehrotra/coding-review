import java.util.*;

public class DesignVariants {

    // ---------- HashMap from scratch (LC 706): array of chains, resize at load factor 0.75 ----------
    static class MyHashMap {
        private static class Node { int k, v; Node next; Node(int k, int v, Node n) { this.k = k; this.v = v; next = n; } }
        private Node[] table = new Node[16];
        private int size;

        private int slot(int key, int len) { int h = Integer.hashCode(key); return (h ^ (h >>> 16)) & (len - 1); }

        void put(int key, int val) {
            for (Node n = table[slot(key, table.length)]; n != null; n = n.next) if (n.k == key) { n.v = val; return; }
            if (size + 1 > table.length * 3 / 4) resize();
            int s = slot(key, table.length);
            table[s] = new Node(key, val, table[s]);
            size++;
        }

        int get(int key) {
            for (Node n = table[slot(key, table.length)]; n != null; n = n.next) if (n.k == key) return n.v;
            return -1;
        }

        void remove(int key) {
            int s = slot(key, table.length);
            Node prev = null;
            for (Node n = table[s]; n != null; prev = n, n = n.next)
                if (n.k == key) {
                    if (prev == null) table[s] = n.next; else prev.next = n.next;
                    size--;
                    return;
                }
        }

        int size() { return size; }

        private void resize() {
            Node[] old = table;
            table = new Node[old.length * 2];
            for (Node head : old)
                for (Node n = head; n != null; n = n.next) {
                    int s = slot(n.k, table.length);
                    table[s] = new Node(n.k, n.v, table[s]);
                }
        }
    }

    // ---------- Binary min-heap from scratch (array, sift up / down) ----------
    static class MinHeap {
        private int[] a = new int[16];
        private int size;
        void push(int x) {
            if (size == a.length) a = Arrays.copyOf(a, size * 2);
            a[size] = x;
            int i = size++;
            while (i > 0 && a[(i - 1) / 2] > a[i]) { swap(i, (i - 1) / 2); i = (i - 1) / 2; }
        }
        int peek() { return a[0]; }
        int pop() {
            int top = a[0];
            a[0] = a[--size];
            int i = 0;
            while (true) {
                int l = 2 * i + 1, r = l + 1, s = i;
                if (l < size && a[l] < a[s]) s = l;
                if (r < size && a[r] < a[s]) s = r;
                if (s == i) break;
                swap(i, s);
                i = s;
            }
            return top;
        }
        int size() { return size; }
        private void swap(int i, int j) { int t = a[i]; a[i] = a[j]; a[j] = t; }
    }

    // ---------- Skiplist (LC 1206): expected O(log n) add / search / erase, duplicates allowed ----------
    static class Skiplist {
        private static final int MAX = 16;
        private static class Node { int val; Node[] next; Node(int v, int lvl) { val = v; next = new Node[lvl]; } }
        private final Node head = new Node(-1, MAX);
        private final Random rnd = new Random(42);
        private int level = 1;

        private int randomLevel() { int l = 1; while (l < MAX && rnd.nextBoolean()) l++; return l; }

        boolean search(int target) {
            Node cur = head;
            for (int i = level - 1; i >= 0; i--) {
                while (cur.next[i] != null && cur.next[i].val < target) cur = cur.next[i];
            }
            cur = cur.next[0];
            return cur != null && cur.val == target;
        }

        void add(int num) {
            Node[] update = new Node[MAX];
            Node cur = head;
            for (int i = level - 1; i >= 0; i--) {
                while (cur.next[i] != null && cur.next[i].val < num) cur = cur.next[i];
                update[i] = cur;
            }
            int lvl = randomLevel();
            if (lvl > level) { for (int i = level; i < lvl; i++) update[i] = head; level = lvl; }
            Node n = new Node(num, lvl);
            for (int i = 0; i < lvl; i++) { n.next[i] = update[i].next[i]; update[i].next[i] = n; }
        }

        boolean erase(int num) {
            Node[] update = new Node[MAX];
            Node cur = head;
            for (int i = level - 1; i >= 0; i--) {
                while (cur.next[i] != null && cur.next[i].val < num) cur = cur.next[i];
                update[i] = cur;
            }
            Node target = cur.next[0];
            if (target == null || target.val != num) return false;
            for (int i = 0; i < level; i++) {
                if (update[i].next[i] != target) break;
                update[i].next[i] = target.next[i];
            }
            while (level > 1 && head.next[level - 1] == null) level--;
            return true;
        }
    }

    // ---------- All O`one Data Structure (LC 432): doubly linked list of count buckets, O(1) inc / dec / max / min ----------
    static class AllOne {
        private static class Bucket {
            int count;
            Set<String> keys = new LinkedHashSet<>();
            Bucket prev, next;
            Bucket(int c) { count = c; }
        }
        private final Bucket head = new Bucket(0), tail = new Bucket(Integer.MAX_VALUE); // sentinels
        private final Map<String, Bucket> where = new HashMap<>();

        AllOne() { head.next = tail; tail.prev = head; }

        void inc(String key) {
            Bucket cur = where.get(key);
            int c = cur == null ? 0 : cur.count;
            Bucket after = cur == null ? head : cur;
            Bucket target = after.next.count == c + 1 ? after.next : insertAfter(after, c + 1);
            target.keys.add(key);
            where.put(key, target);
            if (cur != null) removeKey(cur, key);
        }

        void dec(String key) {
            Bucket cur = where.get(key);
            if (cur == null) return;
            if (cur.count == 1) where.remove(key);
            else {
                Bucket target = cur.prev.count == cur.count - 1 ? cur.prev : insertAfter(cur.prev, cur.count - 1);
                target.keys.add(key);
                where.put(key, target);
            }
            removeKey(cur, key);
        }

        String getMaxKey() { return tail.prev == head ? "" : tail.prev.keys.iterator().next(); }
        String getMinKey() { return head.next == tail ? "" : head.next.keys.iterator().next(); }

        private Bucket insertAfter(Bucket b, int count) {
            Bucket n = new Bucket(count);
            n.prev = b; n.next = b.next;
            b.next.prev = n; b.next = n;
            return n;
        }

        private void removeKey(Bucket b, String key) {
            b.keys.remove(key);
            if (b.keys.isEmpty()) { b.prev.next = b.next; b.next.prev = b.prev; }
        }
    }

    // ---------- Search Autocomplete System (LC 642): trie where every node remembers hot sentences below it ----------
    static class AutocompleteSystem {
        private static class Node {
            Map<Character, Node> kids = new HashMap<>();
            Map<String, Integer> hot = new HashMap<>();
        }
        private final Node root = new Node();
        private Node cur = root;
        private final StringBuilder typed = new StringBuilder();

        AutocompleteSystem(String[] sentences, int[] times) {
            for (int i = 0; i < sentences.length; i++) add(sentences[i], times[i]);
        }

        List<String> input(char c) {
            if (c == '#') {
                add(typed.toString(), 1);
                typed.setLength(0);
                cur = root;
                return new ArrayList<>();
            }
            typed.append(c);
            if (cur != null) cur = cur.kids.get(c);
            if (cur == null) return new ArrayList<>();
            List<String> all = new ArrayList<>(cur.hot.keySet());
            all.sort((a, b) -> cur.hot.get(a).equals(cur.hot.get(b)) ? a.compareTo(b) : Integer.compare(cur.hot.get(b), cur.hot.get(a)));
            return all.subList(0, Math.min(3, all.size()));
        }

        private void add(String s, int times) {
            Node n = root;
            for (char ch : s.toCharArray()) {
                n = n.kids.computeIfAbsent(ch, k -> new Node());
                n.hot.merge(s, times, Integer::sum);
            }
        }
    }

    // LC 1797
    // ---------- Cache with per-entry TTL: lazy expiry on read + min-heap sweep on write; time is injected for testing ----------
    static class TTLCache<K, V> {
        private static class Entry<V> { V val; long expiry; Entry(V v, long e) { val = v; expiry = e; } }
        private final Map<K, Entry<V>> map = new HashMap<>();
        private final PriorityQueue<Object[]> byExpiry = new PriorityQueue<>((a, b) -> Long.compare((long) a[0], (long) b[0])); // {expiry, key}

        void put(K key, V val, long ttl, long now) {
            sweep(now);
            long exp = now + ttl;
            map.put(key, new Entry<>(val, exp));
            byExpiry.add(new Object[]{exp, key});
        }

        V get(K key, long now) {
            Entry<V> e = map.get(key);
            if (e == null) return null;
            if (e.expiry <= now) { map.remove(key); return null; }
            return e.val;
        }

        int size(long now) { sweep(now); return map.size(); }

        @SuppressWarnings("unchecked")
        private void sweep(long now) {
            while (!byExpiry.isEmpty() && (long) byExpiry.peek()[0] <= now) {
                Object[] t = byExpiry.poll();
                Entry<V> e = map.get((K) t[1]);
                if (e != null && e.expiry <= now) map.remove((K) t[1]); // skip stale heap entries from re-puts
            }
        }
    }

    // ---------- Bloom filter: no false negatives, tunable false positives; double hashing g_i = h1 + i * h2 ----------
    static class BloomFilter {
        private final BitSet bits;
        private final int m, k;
        BloomFilter(int expectedItems, double fpRate) {
            this.m = Math.max(8, (int) Math.ceil(-expectedItems * Math.log(fpRate) / (Math.log(2) * Math.log(2))));
            this.k = Math.max(1, (int) Math.round((double) m / expectedItems * Math.log(2)));
            this.bits = new BitSet(m);
        }
        void add(String s) { for (int i = 0; i < k; i++) bits.set(index(s, i)); }
        boolean mightContain(String s) { for (int i = 0; i < k; i++) if (!bits.get(index(s, i))) return false; return true; }
        private int index(String s, int i) {
            int h1 = s.hashCode(), h2 = (h1 >>> 16) | 1; // odd step so probes differ
            return Math.floorMod(h1 + i * h2, m);
        }
    }

    // ---------- Browser History (LC 1472): array + cursor; visiting truncates the forward history ----------
    static class BrowserHistory {
        private final List<String> pages = new ArrayList<>();
        private int cur;
        BrowserHistory(String home) { pages.add(home); }
        void visit(String url) {
            while (pages.size() > cur + 1) pages.remove(pages.size() - 1);
            pages.add(url);
            cur++;
        }
        String back(int steps) { cur = Math.max(0, cur - steps); return pages.get(cur); }
        String forward(int steps) { cur = Math.min(pages.size() - 1, cur + steps); return pages.get(cur); }
    }

    // ---------- Underground System (LC 1396): open check-ins + (from, to) -> (sum, count) ----------
    static class UndergroundSystem {
        private final Map<Integer, Object[]> open = new HashMap<>(); // id -> {station, time}
        private final Map<String, long[]> stats = new HashMap<>();
        void checkIn(int id, String station, int t) { open.put(id, new Object[]{station, t}); }
        void checkOut(int id, String station, int t) {
            Object[] in = open.remove(id);
            long[] s = stats.computeIfAbsent(in[0] + "->" + station, k -> new long[2]);
            s[0] += t - (int) in[1];
            s[1]++;
        }
        double getAverageTime(String from, String to) { long[] s = stats.get(from + "->" + to); return (double) s[0] / s[1]; }
    }

    // ---------- Circular queue (LC 622): fixed array, head index + count ----------
    static class CircularQueue {
        private final int[] a;
        private int head, count;
        CircularQueue(int k) { a = new int[k]; }
        boolean enQueue(int v) { if (isFull()) return false; a[(head + count++) % a.length] = v; return true; }
        boolean deQueue() { if (isEmpty()) return false; head = (head + 1) % a.length; count--; return true; }
        int front() { return isEmpty() ? -1 : a[head]; }
        int rear() { return isEmpty() ? -1 : a[(head + count - 1) % a.length]; }
        boolean isEmpty() { return count == 0; }
        boolean isFull() { return count == a.length; }
    }

    // ---------- Moving Average from Data Stream (LC 346) ----------
    static class MovingAverage {
        private final int[] win;
        private int idx, n;
        private long sum;
        MovingAverage(int size) { win = new int[size]; }
        double next(int v) {
            sum += v - win[idx];
            win[idx] = v;
            idx = (idx + 1) % win.length;
            if (n < win.length) n++;
            return (double) sum / n;
        }
    }
}
