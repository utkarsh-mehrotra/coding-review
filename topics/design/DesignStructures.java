import java.util.*;

public class DesignStructures {

    // RECOGNIZE: "design a data structure supporting X, Y, Z in O(1) / O(log n)". Pair a hash map with an ordering structure.

    // ---------- LRU Cache (LC 146): hash map + doubly linked list; move to front on touch, evict tail ----------
    static class LRUCache {
        private static class Node { int k, v; Node prev, next; Node(int k, int v) { this.k = k; this.v = v; } }
        private final int cap;
        private final Map<Integer, Node> map = new HashMap<>();
        private final Node head = new Node(0, 0), tail = new Node(0, 0); // sentinels

        LRUCache(int cap) { this.cap = cap; head.next = tail; tail.prev = head; }

        int get(int key) {
            Node n = map.get(key);
            if (n == null) return -1;
            unlink(n);
            addFront(n);
            return n.v;
        }

        void put(int key, int val) {
            Node n = map.get(key);
            if (n != null) { n.v = val; unlink(n); addFront(n); return; }
            if (map.size() == cap) { Node lru = tail.prev; unlink(lru); map.remove(lru.k); }
            n = new Node(key, val);
            map.put(key, n);
            addFront(n);
        }

        private void unlink(Node n) { n.prev.next = n.next; n.next.prev = n.prev; }
        private void addFront(Node n) { n.next = head.next; n.prev = head; head.next.prev = n; head.next = n; }
    }

    // Shortcut: LinkedHashMap with accessOrder = true (state this in an interview, then offer to hand-roll)
    static class LRUCacheLHM extends LinkedHashMap<Integer, Integer> {
        private final int cap;
        LRUCacheLHM(int cap) { super(16, 0.75f, true); this.cap = cap; }
        @Override protected boolean removeEldestEntry(Map.Entry<Integer, Integer> e) { return size() > cap; }
    }

    // ---------- LFU Cache (LC 460): key -> (value, freq), freq -> LinkedHashSet of keys, track minFreq ----------
    static class LFUCache {
        private final int cap;
        private int minFreq;
        private final Map<Integer, Integer> val = new HashMap<>(), freq = new HashMap<>();
        private final Map<Integer, LinkedHashSet<Integer>> buckets = new HashMap<>();

        LFUCache(int cap) { this.cap = cap; }

        int get(int key) {
            if (!val.containsKey(key)) return -1;
            touch(key);
            return val.get(key);
        }

        void put(int key, int v) {
            if (cap == 0) return;
            if (val.containsKey(key)) { val.put(key, v); touch(key); return; }
            if (val.size() == cap) {
                LinkedHashSet<Integer> s = buckets.get(minFreq);
                int evict = s.iterator().next();
                s.remove(evict);
                val.remove(evict);
                freq.remove(evict);
            }
            val.put(key, v);
            freq.put(key, 1);
            buckets.computeIfAbsent(1, f -> new LinkedHashSet<>()).add(key);
            minFreq = 1;
        }

        private void touch(int key) {
            int f = freq.get(key);
            freq.put(key, f + 1);
            buckets.get(f).remove(key);
            if (buckets.get(f).isEmpty() && minFreq == f) minFreq++;
            buckets.computeIfAbsent(f + 1, x -> new LinkedHashSet<>()).add(key);
        }
    }

    // ---------- Insert Delete GetRandom O(1) (LC 380): array + index map, swap-with-last on delete ----------
    static class RandomizedSet {
        private final List<Integer> list = new ArrayList<>();
        private final Map<Integer, Integer> idx = new HashMap<>();
        private final Random rnd = new Random();

        boolean insert(int v) {
            if (idx.containsKey(v)) return false;
            idx.put(v, list.size());
            list.add(v);
            return true;
        }

        boolean remove(int v) {
            Integer i = idx.get(v);
            if (i == null) return false;
            int last = list.get(list.size() - 1);
            list.set(i, last);
            idx.put(last, i);
            list.remove(list.size() - 1);
            idx.remove(v);
            return true;
        }

        int getRandom() { return list.get(rnd.nextInt(list.size())); }
    }

    // ---------- Min Stack is in MonotonicStructures. Max Stack (LC 716): TreeMap of value -> stack of ids ----------
    static class MaxStack {
        private final TreeMap<Integer, Deque<Integer>> byVal = new TreeMap<>();
        private final Map<Integer, Integer> idVal = new HashMap<>();
        private final TreeSet<Integer> ids = new TreeSet<>(); // ids as stack order
        private int next = 0;

        void push(int x) {
            int id = next++;
            idVal.put(id, x);
            ids.add(id);
            byVal.computeIfAbsent(x, k -> new ArrayDeque<>()).push(id);
        }
        int pop() { int id = ids.last(); return remove(id); }
        int top() { return idVal.get(ids.last()); }
        int peekMax() { return byVal.lastKey(); }
        int popMax() { return remove(byVal.lastEntry().getValue().peek()); }

        private int remove(int id) {
            int x = idVal.remove(id);
            ids.remove(id);
            Deque<Integer> d = byVal.get(x);
            d.remove(id);
            if (d.isEmpty()) byVal.remove(x);
            return x;
        }
    }

    // ---------- Design Twitter / news feed (LC 355): per-user tweet lists + k-way merge heap ----------
    static class Twitter {
        private int time = 0;
        private final Map<Integer, List<int[]>> tweets = new HashMap<>(); // {time, id}
        private final Map<Integer, Set<Integer>> follows = new HashMap<>();

        void postTweet(int u, int id) { tweets.computeIfAbsent(u, k -> new ArrayList<>()).add(new int[]{time++, id}); }

        List<Integer> getNewsFeed(int u) {
            PriorityQueue<int[]> pq = new PriorityQueue<>((a, b) -> Integer.compare(b[0], a[0])); // {time, id, user, idxInUserList}
            Set<Integer> users = new HashSet<>(follows.getOrDefault(u, Set.of()));
            users.add(u);
            for (int v : users) {
                List<int[]> l = tweets.get(v);
                if (l != null && !l.isEmpty()) pq.add(new int[]{l.get(l.size() - 1)[0], l.get(l.size() - 1)[1], v, l.size() - 1});
            }
            List<Integer> feed = new ArrayList<>();
            while (!pq.isEmpty() && feed.size() < 10) {
                int[] t = pq.poll();
                feed.add(t[1]);
                if (t[3] > 0) {
                    int[] prev = tweets.get(t[2]).get(t[3] - 1);
                    pq.add(new int[]{prev[0], prev[1], t[2], t[3] - 1});
                }
            }
            return feed;
        }

        void follow(int a, int b) { follows.computeIfAbsent(a, k -> new HashSet<>()).add(b); }
        void unfollow(int a, int b) { Set<Integer> s = follows.get(a); if (s != null) s.remove(b); }
    }

    // LC 359
    // ---------- Sliding-window rate limiter (log-based): O(1) amortized with a deque of timestamps ----------
    static class RateLimiter {
        private final int limit;
        private final long windowMs;
        private final Deque<Long> times = new ArrayDeque<>();
        RateLimiter(int limit, long windowMs) { this.limit = limit; this.windowMs = windowMs; }
        boolean allow(long now) {
            while (!times.isEmpty() && times.peekFirst() <= now - windowMs) times.pollFirst();
            if (times.size() >= limit) return false;
            times.addLast(now);
            return true;
        }
    }

    // Token bucket variant: refill continuously, O(1) state
    static class TokenBucket {
        private final double capacity, perMs;
        private double tokens;
        private long last;
        TokenBucket(double capacity, double perSecond, long now) { this.capacity = capacity; this.perMs = perSecond / 1000.0; this.tokens = capacity; this.last = now; }
        boolean allow(long now) {
            tokens = Math.min(capacity, tokens + (now - last) * perMs);
            last = now;
            if (tokens < 1) return false;
            tokens -= 1;
            return true;
        }
    }

    // ---------- Snapshot Array (LC 1146): per-index history of (snapId, value), binary search on read ----------
    static class SnapshotArray {
        private final List<int[]>[] hist;
        private int snap = 0;
        @SuppressWarnings("unchecked")
        SnapshotArray(int n) {
            hist = new List[n];
            for (int i = 0; i < n; i++) { hist[i] = new ArrayList<>(); hist[i].add(new int[]{0, 0}); }
        }
        void set(int i, int v) {
            List<int[]> h = hist[i];
            if (h.get(h.size() - 1)[0] == snap) h.get(h.size() - 1)[1] = v; else h.add(new int[]{snap, v});
        }
        int snap() { return snap++; }
        int get(int i, int snapId) {
            List<int[]> h = hist[i];
            int lo = 0, hi = h.size();
            while (lo < hi) { int mid = (lo + hi) >>> 1; if (h.get(mid)[0] > snapId) hi = mid; else lo = mid + 1; }
            return h.get(lo - 1)[1];
        }
    }

    // ---------- Design Hit Counter (LC 362) with circular buckets of 300 seconds ----------
    static class HitCounter {
        private final int[] times = new int[300], hits = new int[300];
        void hit(int t) {
            int i = t % 300;
            if (times[i] != t) { times[i] = t; hits[i] = 1; } else hits[i]++;
        }
        int getHits(int t) {
            int total = 0;
            for (int i = 0; i < 300; i++) if (t - times[i] < 300) total += hits[i];
            return total;
        }
    }

    // ---------- Iterator designs: Flatten Nested List (LC 341) via stack of iterators; Peeking Iterator (LC 284) ----------
    static class PeekingIterator implements Iterator<Integer> {
        private final Iterator<Integer> it;
        private Integer buffered;
        PeekingIterator(Iterator<Integer> it) { this.it = it; if (it.hasNext()) buffered = it.next(); }
        Integer peek() { return buffered; }
        @Override public Integer next() { Integer r = buffered; buffered = it.hasNext() ? it.next() : null; return r; }
        @Override public boolean hasNext() { return buffered != null; }
    }

    // ---------- Autocomplete / Trie-backed designs live in the trees topic (Trie.java). ----------
    // ---------- Consistent hashing ring (interview favorite): TreeMap of hash -> node, clockwise successor ----------
    static class ConsistentHash {
        private final TreeMap<Integer, String> ring = new TreeMap<>();
        private final int replicas;
        ConsistentHash(int replicas) { this.replicas = replicas; }
        void addNode(String node) { for (int i = 0; i < replicas; i++) ring.put((node + "#" + i).hashCode() & 0x7fffffff, node); }
        void removeNode(String node) { for (int i = 0; i < replicas; i++) ring.remove((node + "#" + i).hashCode() & 0x7fffffff); }
        String route(String key) {
            if (ring.isEmpty()) return null;
            Map.Entry<Integer, String> e = ring.ceilingEntry(key.hashCode() & 0x7fffffff);
            return (e != null ? e : ring.firstEntry()).getValue();
        }
    }
}
