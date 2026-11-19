import java.util.*;

public class DesignStructuresTest {
    public static void main(String[] x) {
        // ---------- design ----------
        DesignStructures.LRUCache lru = new DesignStructures.LRUCache(2);
        lru.put(1, 1); lru.put(2, 2);
        int g1 = lru.get(1);
        lru.put(3, 3);
        int g2 = lru.get(2);
        lru.put(4, 4);
        Check.eq("lru", g1 + "," + g2 + "," + lru.get(1) + "," + lru.get(3) + "," + lru.get(4), "1,-1,-1,3,4");
        DesignStructures.LRUCacheLHM lhm = new DesignStructures.LRUCacheLHM(2);
        lhm.put(1, 1); lhm.put(2, 2); lhm.get(1); lhm.put(3, 3);
        Check.eq("lruLHM", lhm.containsKey(2) + "," + lhm.containsKey(1), "false,true");
        DesignStructures.LFUCache lfu = new DesignStructures.LFUCache(2);
        lfu.put(1, 1); lfu.put(2, 2);
        int f1 = lfu.get(1);
        lfu.put(3, 3);
        int f2 = lfu.get(2), f3 = lfu.get(3);
        lfu.put(4, 4);
        Check.eq("lfu", f1 + "," + f2 + "," + f3 + "," + lfu.get(1) + "," + lfu.get(3) + "," + lfu.get(4), "1,-1,3,-1,3,4");
        DesignStructures.RandomizedSet rs = new DesignStructures.RandomizedSet();
        Check.eq("randSet", rs.insert(1) + "," + rs.remove(2) + "," + rs.insert(2) + "," + rs.remove(1) + "," + rs.insert(2) + "," + rs.getRandom(), "true,false,true,true,false,2");
        DesignStructures.MaxStack mxs = new DesignStructures.MaxStack();
        mxs.push(5); mxs.push(1); mxs.push(5);
        Check.eq("maxStack", mxs.top() + "," + mxs.popMax() + "," + mxs.top() + "," + mxs.peekMax() + "," + mxs.pop() + "," + mxs.top(), "5,5,1,5,1,5");
        DesignStructures.Twitter tw = new DesignStructures.Twitter();
        tw.postTweet(1, 5);
        tw.follow(1, 2);
        tw.postTweet(2, 6);
        Check.eq("feed", tw.getNewsFeed(1), "[6, 5]");
        tw.unfollow(1, 2);
        Check.eq("feedAfterUnfollow", tw.getNewsFeed(1), "[5]");
        DesignStructures.RateLimiter rl = new DesignStructures.RateLimiter(2, 1000);
        Check.eq("rateLimit", rl.allow(0) + "," + rl.allow(100) + "," + rl.allow(200) + "," + rl.allow(1000) + "," + rl.allow(1100), "true,true,false,true,true");
        DesignStructures.TokenBucket tb = new DesignStructures.TokenBucket(2, 1, 0);
        Check.eq("tokenBucket", tb.allow(0) + "," + tb.allow(0) + "," + tb.allow(0) + "," + tb.allow(1000), "true,true,false,true");
        DesignStructures.SnapshotArray sa = new DesignStructures.SnapshotArray(3);
        sa.set(0, 5);
        int sid = sa.snap();
        sa.set(0, 6);
        Check.eq("snapshot", sa.get(0, sid), 5);
        DesignStructures.HitCounter hc = new DesignStructures.HitCounter();
        hc.hit(1); hc.hit(2); hc.hit(3);
        Check.eq("hits", hc.getHits(4) + "," + hc.getHits(300) + "," + hc.getHits(301), "3,3,2");
        DesignStructures.PeekingIterator pi = new DesignStructures.PeekingIterator(List.of(1, 2, 3).iterator());
        Check.eq("peek", pi.next() + "," + pi.peek() + "," + pi.next() + "," + pi.next() + "," + pi.hasNext(), "1,2,2,3,false");
        DesignStructures.ConsistentHash ch = new DesignStructures.ConsistentHash(20);
        ch.addNode("A"); ch.addNode("B"); ch.addNode("C");
        String owner = ch.route("user-42");
        ch.addNode("D");
        String after = ch.route("user-42");
        Check.eq("consistentHash", owner.equals(after) || after.equals("D"), true);

        // LRU vs LinkedHashMap oracle
        Random r = new Random(9);
        for (int t = 0; t < 50; t++) {
            int cap = 1 + r.nextInt(4);
            DesignStructures.LRUCache mine = new DesignStructures.LRUCache(cap);
            DesignStructures.LRUCacheLHM oracle = new DesignStructures.LRUCacheLHM(cap);
            for (int op = 0; op < 60; op++) {
                int k = r.nextInt(6);
                if (r.nextBoolean()) { int v = r.nextInt(100); mine.put(k, v); oracle.put(k, v); }
                else { Integer want = oracle.get(k); Check.eq("lruRand", mine.get(k), want == null ? -1 : want); }
            }
        }
        // consistent hashing: removeNode sends every key to a surviving node
        DesignStructures.ConsistentHash chash = new DesignStructures.ConsistentHash(3);
        chash.addNode("A");
        chash.addNode("B");
        chash.removeNode("B");
        boolean allA = true;
        for (int i = 0; i < 100; i++) if (!"A".equals(chash.route("key" + i))) allA = false;
        Check.eq("removeNode routes to survivor", allA, true);
        chash.removeNode("A");
        Check.eq("removeNode empty ring", chash.route("key1"), null);
        chash.removeNode("never-added"); // no-op
        Check.eq("removeNode unknown no-op", chash.route("key1"), null);
        Check.done("DesignStructuresTest");
    }

}
