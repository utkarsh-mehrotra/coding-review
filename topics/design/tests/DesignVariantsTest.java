import java.util.*;

public class DesignVariantsTest {
    public static void main(String[] x) {
        Random r = new Random(21);
        // ---------- design ----------
        DesignVariants.MyHashMap hm = new DesignVariants.MyHashMap();
        Map<Integer, Integer> oracle = new HashMap<>();
        for (int op = 0; op < 5000; op++) {
            int k = r.nextInt(300), kind = r.nextInt(3);
            if (kind == 0) { int v = r.nextInt(1000); hm.put(k, v); oracle.put(k, v); }
            else if (kind == 1) { hm.remove(k); oracle.remove(k); }
            else Check.eq("hashMapRand", hm.get(k), oracle.getOrDefault(k, -1));
        }
        Check.eq("hashMapSize", hm.size(), oracle.size());
        DesignVariants.MinHeap mh = new DesignVariants.MinHeap();
        PriorityQueue<Integer> ref = new PriorityQueue<>();
        for (int op = 0; op < 3000; op++) {
            if (r.nextInt(3) > 0 || ref.isEmpty()) { int v = r.nextInt(500); mh.push(v); ref.add(v); }
            else Check.eq("minHeapRand", mh.pop(), ref.poll());
        }
        DesignVariants.Skiplist sl = new DesignVariants.Skiplist();
        TreeMap<Integer, Integer> bag = new TreeMap<>();
        for (int op = 0; op < 4000; op++) {
            int v = r.nextInt(200), kind = r.nextInt(3);
            if (kind == 0) { sl.add(v); bag.merge(v, 1, Integer::sum); }
            else if (kind == 1) {
                boolean had = bag.containsKey(v);
                Check.eq("skipErase", sl.erase(v), had);
                if (had && bag.merge(v, -1, Integer::sum) == 0) bag.remove(v);
            } else Check.eq("skipSearch", sl.search(v), bag.containsKey(v));
        }
        DesignVariants.AllOne ao = new DesignVariants.AllOne();
        ao.inc("hello"); ao.inc("hello"); ao.inc("leet");
        String max1 = ao.getMaxKey(), min1 = ao.getMinKey();
        ao.inc("leet"); ao.inc("leet");
        Check.eq("allOne", max1 + "," + min1 + "," + ao.getMaxKey() + "," + ao.getMinKey(), "hello,leet,leet,hello");
        Map<String, Integer> counts = new HashMap<>();
        DesignVariants.AllOne ao2 = new DesignVariants.AllOne();
        String[] keys = {"a", "b", "c", "d"};
        for (int op = 0; op < 3000; op++) {
            String k = keys[r.nextInt(4)];
            if (r.nextBoolean()) { ao2.inc(k); counts.merge(k, 1, Integer::sum); }
            else if (counts.containsKey(k)) { ao2.dec(k); if (counts.merge(k, -1, Integer::sum) == 0) counts.remove(k); }
            int mx = counts.isEmpty() ? 0 : Collections.max(counts.values()), mn = counts.isEmpty() ? 0 : Collections.min(counts.values());
            String gotMax = ao2.getMaxKey(), gotMin = ao2.getMinKey();
            Check.eq("allOneMax", gotMax.isEmpty() ? 0 : counts.get(gotMax), mx);
            Check.eq("allOneMin", gotMin.isEmpty() ? 0 : counts.get(gotMin), mn);
        }
        DesignVariants.AutocompleteSystem ac = new DesignVariants.AutocompleteSystem(new String[]{"i love you", "island", "iroman", "i love leetcode"}, new int[]{5, 3, 2, 2});
        Check.eq("autocomplete", ac.input('i') + "," + ac.input(' ') + "," + ac.input('a') + "," + ac.input('#'), "[i love you, island, i love leetcode],[i love you, i love leetcode],[],[]");
        DesignVariants.TTLCache<String, Integer> cache = new DesignVariants.TTLCache<>();
        cache.put("a", 1, 10, 0);
        cache.put("b", 2, 5, 0);
        Check.eq("ttl", cache.get("a", 9) + "," + cache.get("b", 5) + "," + cache.size(9) + "," + cache.get("a", 10), "1,null,1,null");
        cache.put("c", 3, 5, 20);
        cache.put("c", 4, 100, 21);
        Check.eq("ttlReput", cache.get("c", 30) + "," + cache.size(30), "4,1");
        DesignVariants.BloomFilter bf = new DesignVariants.BloomFilter(1000, 0.01);
        for (int i = 0; i < 1000; i++) bf.add("key" + i);
        boolean noFalseNeg = true;
        for (int i = 0; i < 1000; i++) noFalseNeg &= bf.mightContain("key" + i);
        int fp = 0;
        for (int i = 0; i < 5000; i++) if (bf.mightContain("other" + i)) fp++;
        Check.eq("bloomNoFalseNeg", noFalseNeg, true);
        Check.eq("bloomFpRate", fp < 250, true); // target 1% of 5000 = 50; allow generous slack
        DesignVariants.BrowserHistory bh = new DesignVariants.BrowserHistory("leetcode.com");
        bh.visit("google.com"); bh.visit("facebook.com"); bh.visit("youtube.com");
        String b1 = bh.back(1), b2 = bh.back(1), f1 = bh.forward(1);
        bh.visit("linkedin.com");
        Check.eq("browser", b1 + "," + b2 + "," + f1 + "," + bh.forward(2) + "," + bh.back(2) + "," + bh.back(7), "facebook.com,google.com,facebook.com,linkedin.com,google.com,leetcode.com");
        DesignVariants.UndergroundSystem us = new DesignVariants.UndergroundSystem();
        us.checkIn(45, "Leyton", 3); us.checkOut(45, "Waterloo", 15);
        us.checkIn(32, "Paradise", 8); us.checkOut(32, "Cambridge", 22);
        us.checkIn(10, "Leyton", 24); us.checkOut(10, "Waterloo", 38);
        Check.eq("underground", us.getAverageTime("Leyton", "Waterloo"), 13.0);
        DesignVariants.CircularQueue cq = new DesignVariants.CircularQueue(3);
        Check.eq("circQueue", cq.enQueue(1) + "," + cq.enQueue(2) + "," + cq.enQueue(3) + "," + cq.enQueue(4) + "," + cq.rear() + "," + cq.isFull() + "," + cq.deQueue() + "," + cq.enQueue(4) + "," + cq.rear(), "true,true,true,false,3,true,true,true,4");
        DesignVariants.MovingAverage ma = new DesignVariants.MovingAverage(3);
        Check.eq("movingAvg", ma.next(1) + "," + ma.next(10) + "," + String.format("%.5f", ma.next(3)) + "," + ma.next(5), "1.0,5.5,4.66667,6.0");

        Check.done("DesignVariantsTest");
    }

}
