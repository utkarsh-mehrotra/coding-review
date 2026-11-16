import java.util.*;

// heaps, intervals, greedy, design, strings variants
public class Deepen1Test {
    public static void main(String[] x) {
        Random r = new Random(21);

        // ---------- heaps ----------
        Check.eq("medianSets", Arrays.toString(HeapsVariants.medianSlidingWindowTwoSets(new int[]{1, 3, -1, -3, 5, 3, 6, 7}, 3)), "[1.0, -1.0, -1.0, 3.0, 5.0, 6.0]");
        Check.eq("hireWorkers", String.format("%.5f", HeapsVariants.mincostToHireWorkers(new int[]{10, 20, 5}, new int[]{70, 50, 30}, 2)), "105.00000");
        Check.eq("perf", HeapsVariants.maxPerformance(6, new int[]{2, 10, 3, 1, 5, 8}, new int[]{5, 4, 3, 9, 7, 2}, 2), 60);
        Check.eq("kPairs", HeapsVariants.kSmallestPairs(new int[]{1, 7, 11}, new int[]{2, 4, 6}, 3), "[[1, 2], [1, 4], [1, 6]]");
        Check.eq("primeFraction", Check.s(HeapsVariants.kthSmallestPrimeFraction(new int[]{1, 2, 3, 5}, 3)), "[2, 5]");
        Check.eq("courseIII", HeapsVariants.scheduleCourse(new int[][]{{100, 200}, {200, 1300}, {1000, 1250}, {2000, 3200}}), 3);
        Check.eq("sticks", HeapsVariants.connectSticks(new int[]{2, 4, 3}) + "," + HeapsVariants.connectSticks(new int[]{1, 8, 3, 5}), "14,30");
        Check.eq("rain2", HeapsVariants.trapRainWater(new int[][]{{1, 4, 3, 1, 3, 2}, {3, 2, 1, 3, 2, 4}, {2, 3, 3, 2, 3, 1}}), 4);
        Check.eq("cpu", Check.s(HeapsVariants.getOrder(new int[][]{{1, 2}, {2, 4}, {3, 2}, {4, 1}})), "[0, 2, 3, 1]");
        Check.eq("deviation", HeapsVariants.minimumDeviation(new int[]{1, 2, 3, 4}) + "," + HeapsVariants.minimumDeviation(new int[]{4, 1, 5, 20, 3}), "1,3");
        HeapsVariants.KthLargest kl = new HeapsVariants.KthLargest(3, new int[]{4, 5, 8, 2});
        Check.eq("kthStream", kl.add(3) + "," + kl.add(5) + "," + kl.add(10) + "," + kl.add(9) + "," + kl.add(4), "4,5,5,8,8");
        for (int t = 0; t < 300; t++) {
            int n = 1 + r.nextInt(14);
            int[] a = new int[n];
            for (int i = 0; i < n; i++) a[i] = r.nextInt(15) - 7;
            int k = 1 + r.nextInt(n);
            double[] got = HeapsVariants.medianSlidingWindowTwoSets(a, k);
            for (int i = 0; i + k <= n; i++) {
                int[] w = Arrays.copyOfRange(a, i, i + k);
                Arrays.sort(w);
                double want = k % 2 == 1 ? w[k / 2] : ((double) w[k / 2 - 1] + w[k / 2]) / 2;
                Check.eq("medianRand", got[i], want);
            }
            // indexed PQ vs sorted order
            HeapsVariants.IndexedMinPQ pq = new HeapsVariants.IndexedMinPQ(n);
            long[] keys = new long[n];
            for (int i = 0; i < n; i++) { keys[i] = r.nextInt(100); pq.insert(i, keys[i]); }
            for (int i = 0; i < n; i++) if (r.nextBoolean()) { long nk = keys[i] - r.nextInt(50); pq.decreaseKey(i, nk); keys[i] = Math.min(keys[i], nk); }
            long prev = Long.MIN_VALUE;
            boolean ok = true;
            for (int i = 0; i < n; i++) { int id = pq.extractMin(); if (keys[id] < prev) ok = false; prev = keys[id]; }
            Check.eq("indexedPQ", ok, true);
            // k pairs vs brute
            int[] b = new int[1 + r.nextInt(5)];
            for (int i = 0; i < b.length; i++) b[i] = r.nextInt(10);
            int[] sa = a.clone(), sb = b.clone();
            Arrays.sort(sa); Arrays.sort(sb);
            int kk = 1 + r.nextInt(6);
            List<Integer> brute = new ArrayList<>();
            for (int p : sa) for (int q : sb) brute.add(p + q);
            Collections.sort(brute);
            List<Integer> mine = new ArrayList<>();
            for (List<Integer> pr : HeapsVariants.kSmallestPairs(sa, sb, kk)) mine.add(pr.get(0) + pr.get(1));
            Check.eq("kPairsRand", mine, brute.subList(0, Math.min(kk, brute.size())));
        }

        // ---------- intervals ----------
        Check.eq("minInterval", Check.s(IntervalsVariants.minInterval(new int[][]{{1, 4}, {2, 4}, {3, 6}, {4, 4}}, new int[]{2, 3, 4, 5})), "[3, 3, 1, 4]");
        Check.eq("maxEvents", IntervalsVariants.maxEvents(new int[][]{{1, 2}, {2, 3}, {3, 4}, {1, 2}}), 4);
        Check.eq("mostBooked", IntervalsVariants.mostBooked(2, new int[][]{{0, 10}, {1, 5}, {2, 7}, {3, 4}}) + "," + IntervalsVariants.mostBooked(3, new int[][]{{1, 20}, {2, 10}, {3, 5}, {4, 9}, {6, 8}}), "0,1");
        Check.eq("minGroups", IntervalsVariants.minGroups(new int[][]{{5, 10}, {6, 8}, {1, 5}, {2, 3}, {1, 10}}) + "," + IntervalsVariants.minGroups(new int[][]{{1, 3}, {5, 6}, {8, 10}, {11, 13}}), "3,1");
        Check.eq("videoStitch", IntervalsVariants.videoStitching(new int[][]{{0, 2}, {4, 6}, {8, 10}, {1, 9}, {1, 5}, {5, 9}}, 10), 3);
        Check.eq("taps", IntervalsVariants.minTaps(5, new int[]{3, 4, 1, 1, 0, 0}), 1);
        Check.eq("removeInterval", IntervalsVariants.removeInterval(new int[][]{{0, 2}, {3, 4}, {5, 7}}, new int[]{1, 6}), "[[0, 1], [6, 7]]");
        IntervalsVariants.RangeModule rm = new IntervalsVariants.RangeModule();
        rm.addRange(10, 20); rm.removeRange(14, 16);
        Check.eq("rangeModule", rm.queryRange(10, 14) + "," + rm.queryRange(13, 15) + "," + rm.queryRange(16, 17), "true,false,true");
        Check.eq("rectArea", IntervalsVariants.rectangleArea(new int[][]{{0, 0, 2, 2}, {1, 0, 2, 3}, {1, 0, 3, 1}}), 6);
        Check.eq("rectArea2", IntervalsVariants.rectangleArea(new int[][]{{0, 0, 1000000000, 1000000000}}), 49);
        for (int t = 0; t < 200; t++) {
            int m = 1 + r.nextInt(6);
            int[][] iv = new int[m][2];
            for (int i = 0; i < m; i++) { int s = r.nextInt(20); iv[i] = new int[]{s, s + r.nextInt(8)}; }
            IntervalsVariants.StabbingCounter sc = new IntervalsVariants.StabbingCounter(iv);
            int p = r.nextInt(30), brute = 0;
            for (int[] v : iv) if (v[0] <= p && p <= v[1]) brute++;
            Check.eq("stabRand", sc.count(p), brute);

            // minInterval vs brute
            int[] qs = {r.nextInt(25), r.nextInt(25), r.nextInt(25)};
            int[] got = IntervalsVariants.minInterval(iv.clone(), qs);
            for (int qi = 0; qi < 3; qi++) {
                int best = -1;
                for (int[] v : iv) if (v[0] <= qs[qi] && qs[qi] <= v[1]) { int sz = v[1] - v[0] + 1; if (best == -1 || sz < best) best = sz; }
                Check.eq("minIntervalRand", got[qi], best);
            }
            // rectangle area vs grid
            int rc = 1 + r.nextInt(4);
            int[][] rects = new int[rc][];
            boolean[][] grid = new boolean[12][12];
            for (int i = 0; i < rc; i++) {
                int x1 = r.nextInt(8), y1 = r.nextInt(8);
                rects[i] = new int[]{x1, y1, x1 + 1 + r.nextInt(4), y1 + 1 + r.nextInt(4)};
                for (int a = rects[i][0]; a < rects[i][2]; a++) for (int c = rects[i][1]; c < rects[i][3]; c++) grid[a][c] = true;
            }
            int area = 0;
            for (boolean[] row : grid) for (boolean v : row) if (v) area++;
            Check.eq("rectAreaRand", IntervalsVariants.rectangleArea(rects), area);

            // RangeModule vs boolean array
            IntervalsVariants.RangeModule mod = new IntervalsVariants.RangeModule();
            boolean[] cells = new boolean[40];
            for (int op = 0; op < 20; op++) {
                int l = r.nextInt(30), rr = l + 1 + r.nextInt(8), kind = r.nextInt(3);
                if (kind == 0) { mod.addRange(l, rr); for (int c = l; c < rr; c++) cells[c] = true; }
                else if (kind == 1) { mod.removeRange(l, rr); for (int c = l; c < rr; c++) cells[c] = false; }
                else { boolean all = true; for (int c = l; c < rr; c++) all &= cells[c]; Check.eq("rangeModRand", mod.queryRange(l, rr), all); }
            }
        }

        // ---------- greedy ----------
        Check.eq("negations", GreedyVariants.largestSumAfterKNegations(new int[]{4, 2, 3}, 1) + "," + GreedyVariants.largestSumAfterKNegations(new int[]{2, -3, -1, 5, -4}, 2), "5,13");
        Check.eq("wiggle", GreedyVariants.wiggleMaxLength(new int[]{1, 7, 4, 9, 2, 5}) + "," + GreedyVariants.wiggleMaxLength(new int[]{1, 17, 5, 10, 13, 15, 10, 5, 16, 8}), "6,7");
        Check.eq("dupLetters", GreedyVariants.removeDuplicateLetters("bcabc") + "," + GreedyVariants.removeDuplicateLetters("cbacdcbc"), "abc,acdb");
        Check.eq("twoCity", GreedyVariants.twoCitySchedCost(new int[][]{{10, 20}, {30, 200}, {400, 50}, {30, 20}}), 110);
        Check.eq("tokens", GreedyVariants.bagOfTokensScore(new int[]{100, 200, 300, 400}, 200), 2);
        Check.eq("brokenCalc", GreedyVariants.brokenCalc(2, 3) + "," + GreedyVariants.brokenCalc(5, 8) + "," + GreedyVariants.brokenCalc(3, 10), "2,2,3");
        Check.eq("monotone", GreedyVariants.monotoneIncreasingDigits(332) + "," + GreedyVariants.monotoneIncreasingDigits(10) + "," + GreedyVariants.monotoneIncreasingDigits(1234), "299,9,1234");
        Check.eq("split659", GreedyVariants.isPossibleSplit(new int[]{1, 2, 3, 3, 4, 5}) + "," + GreedyVariants.isPossibleSplit(new int[]{1, 2, 3, 4, 4, 5}), "true,false");
        Check.eq("swap", GreedyVariants.maximumSwap(2736) + "," + GreedyVariants.maximumSwap(9973), "7236,9973");
        Check.eq("nonDecr", GreedyVariants.checkPossibility(new int[]{4, 2, 3}) + "," + GreedyVariants.checkPossibility(new int[]{4, 2, 1}), "true,false");
        Check.eq("advantage", Check.s(GreedyVariants.advantageCount(new int[]{2, 7, 11, 15}, new int[]{1, 10, 4, 11})), "[2, 11, 7, 15]");
        Check.eq("fracKnap", String.format("%.2f", GreedyVariants.fractionalKnapsack(new int[]{10, 20, 30}, new int[]{60, 100, 120}, 50)), "240.00");
        Check.eq("domino", GreedyVariants.minDominoRotations(new int[]{2, 1, 2, 4, 2, 2}, new int[]{5, 2, 6, 2, 3, 2}) + "," + GreedyVariants.minDominoRotations(new int[]{3, 5, 1, 2, 3}, new int[]{3, 6, 3, 3, 4}), "2,-1");
        Check.eq("maxNumber", Check.s(GreedyVariants.maxNumber(new int[]{3, 4, 6, 5}, new int[]{9, 1, 2, 5, 8, 3}, 5)), "[9, 8, 6, 5, 3]");
        Check.eq("maxNumber2", Check.s(GreedyVariants.maxNumber(new int[]{6, 7}, new int[]{6, 0, 4}, 5)), "[6, 7, 6, 0, 4]");
        Check.eq("minAdd", GreedyVariants.minAddToMakeValid("())") + "," + GreedyVariants.minAddToMakeValid("((("), "1,3");
        Check.eq("incSub", GreedyVariants.minNumberOperations(new int[]{1, 2, 3, 2, 1}) + "," + GreedyVariants.minNumberOperations(new int[]{3, 1, 5, 4, 2}), "3,7");
        Check.eq("pairChain", GreedyVariants.findLongestChain(new int[][]{{1, 2}, {2, 3}, {3, 4}}), 2);
        for (int t = 0; t < 300; t++) {
            int n = 1 + r.nextInt(9);
            int[] a = new int[n];
            for (int i = 0; i < n; i++) a[i] = r.nextInt(6);
            Check.eq("wiggleRand", GreedyVariants.wiggleMaxLength(a), wiggleBrute(a));
            Check.eq("nonDecrRand", GreedyVariants.checkPossibility(a), nonDecrBrute(a));
            int num = 1 + r.nextInt(100000);
            Check.eq("maxSwapRand", GreedyVariants.maximumSwap(num), maxSwapBrute(num));
            Check.eq("monotoneRand", GreedyVariants.monotoneIncreasingDigits(num), monotoneBrute(num));
            int st = 1 + r.nextInt(20), tg = 1 + r.nextInt(40);
            Check.eq("brokenRand", GreedyVariants.brokenCalc(st, tg), brokenBfs(st, tg));
            String s = "";
            for (int i = 0; i < 1 + r.nextInt(8); i++) s += (char) ('a' + r.nextInt(4));
            Check.eq("dupLettersRand", GreedyVariants.removeDuplicateLetters(s), dupLettersBrute(s));
            int[] p1 = new int[r.nextInt(5)], p2 = new int[r.nextInt(5)];
            for (int i = 0; i < p1.length; i++) p1[i] = r.nextInt(4);
            for (int i = 0; i < p2.length; i++) p2[i] = r.nextInt(4);
            int kk = r.nextInt(p1.length + p2.length + 1);
            if (kk > 0) Check.eq("maxNumberRand", Check.s(GreedyVariants.maxNumber(p1, p2, kk)), Check.s(maxNumberBrute(p1, p2, kk)));
            int c = 2 * (1 + r.nextInt(3));
            int[][] costs = new int[c][2];
            for (int[] row : costs) { row[0] = r.nextInt(50); row[1] = r.nextInt(50); }
            Check.eq("twoCityRand", GreedyVariants.twoCitySchedCost(costs), twoCityBrute(costs, 0, 0, 0));
        }

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

        // ---------- strings ----------
        Check.eq("suffixArray", Check.s(StringsVariants.suffixArray("banana")), "[5, 3, 1, 0, 4, 2]");
        Check.eq("lcp", Check.s(StringsVariants.lcpArray("banana", StringsVariants.suffixArray("banana"))), "[0, 1, 3, 0, 0, 2]");
        Check.eq("distinctSubs", StringsVariants.countDistinctSubstrings("banana") + "," + StringsVariants.countDistinctSubstrings("aaa"), "15,3");
        Check.eq("repeated", StringsVariants.longestRepeatedSubstring("banana") + "," + StringsVariants.longestRepeatedSubstring("abcd").isEmpty(), "ana,true");
        Check.eq("dupSub", StringsVariants.longestDupSubstring("banana") + "," + StringsVariants.longestDupSubstring("abcd").isEmpty(), "ana,true");
        StringsVariants.AhoCorasick ahoC = new StringsVariants.AhoCorasick(new String[]{"he", "she", "his", "hers"});
        Check.eq("ahoCorasick", Check.s(ahoC.countAll("ahishers")), "[1, 1, 1, 1]");
        Check.eq("wordBreak2", new TreeSet<>(StringsVariants.wordBreakAll("catsanddog", List.of("cat", "cats", "and", "sand", "dog"))), "[cat sand dog, cats and dog]");
        List<List<Integer>> pp = StringsVariants.palindromePairs(new String[]{"abcd", "dcba", "lls", "s", "sssll"});
        Check.eq("palPairs", new TreeSet<>(pp.stream().map(Object::toString).toList()), "[[0, 1], [1, 0], [2, 4], [3, 2]]");
        Check.eq("happyPrefix", StringsVariants.longestHappyPrefix("level") + "," + StringsVariants.longestHappyPrefix("ababab"), "l,abab");
        Check.eq("leastRotation", StringsVariants.leastRotation("cba") + "," + StringsVariants.leastRotation("bbaaccaadd") + "," + StringsVariants.leastRotation("aaaa"), "acb,aaccaaddbb,aaaa");
        Check.eq("justify", StringsVariants.fullJustify(new String[]{"This", "is", "an", "example", "of", "text", "justification."}, 16), "[This    is    an, example  of text, justification.  ]");
        Check.eq("isNumber", StringsVariants.isNumber("2") + "," + StringsVariants.isNumber("0089") + "," + StringsVariants.isNumber("-0.1") + "," + StringsVariants.isNumber("3.") + "," + StringsVariants.isNumber("e3") + "," + StringsVariants.isNumber("99e2.5") + "," + StringsVariants.isNumber("--6") + "," + StringsVariants.isNumber("-+3") + "," + StringsVariants.isNumber(".") + "," + StringsVariants.isNumber("4e+"), "true,true,true,true,false,false,false,false,false,false");
        Check.eq("encodeDecode", StringsVariants.decode(StringsVariants.encode(List.of("a#b", "", "12#", "xyz"))), "[a#b, , 12#, xyz]");
        Check.eq("compareVersion", StringsVariants.compareVersion("1.01", "1.001") + "," + StringsVariants.compareVersion("1.0", "1.0.0") + "," + StringsVariants.compareVersion("0.1", "1.1"), "0,0,-1");
        Check.eq("zigzag", StringsVariants.zigzag("PAYPALISHIRING", 3) + "," + StringsVariants.zigzag("PAYPALISHIRING", 4), "PAHNAPLSIIGYIR,PINALSIGYAHRPI");
        for (int t = 0; t < 300; t++) {
            StringBuilder sb = new StringBuilder();
            int len = r.nextInt(14);
            for (int i = 0; i < len; i++) sb.append((char) ('a' + r.nextInt(3)));
            String s = sb.toString();
            // suffix array vs sorting suffixes
            Integer[] idx = new Integer[len];
            for (int i = 0; i < len; i++) idx[i] = i;
            Arrays.sort(idx, (p, q) -> s.substring(p).compareTo(s.substring(q)));
            int[] want = new int[len];
            for (int i = 0; i < len; i++) want[i] = idx[i];
            Check.eq("saRand", Check.s(StringsVariants.suffixArray(s)), Check.s(want));
            Set<String> subs = new HashSet<>();
            for (int i = 0; i < len; i++) for (int j = i + 1; j <= len; j++) subs.add(s.substring(i, j));
            Check.eq("distinctRand", StringsVariants.countDistinctSubstrings(s), subs.size());
            // longest repeated substring length vs brute
            int best = 0;
            for (int L = 1; L < len; L++) { Set<String> seen = new HashSet<>(); for (int i = 0; i + L <= len; i++) if (!seen.add(s.substring(i, i + L))) { best = L; break; } }
            Check.eq("repeatedRand", StringsVariants.longestRepeatedSubstring(s).length(), best);
            Check.eq("dupSubRand", StringsVariants.longestDupSubstring(s).length(), best);
            // Aho-Corasick vs indexOf counting
            String[] pats = {rndStr(r, 1 + r.nextInt(3)), rndStr(r, 1 + r.nextInt(3)), rndStr(r, 1 + r.nextInt(2))};
            int[] got = new StringsVariants.AhoCorasick(pats).countAll(s);
            for (int i = 0; i < 3; i++) { int c = 0; for (int j = s.indexOf(pats[i]); j >= 0; j = s.indexOf(pats[i], j + 1)) c++; Check.eq("ahoRand", got[i], c); }
            // least rotation vs brute
            String minRot = s;
            for (int i = 0; i < len; i++) { String rot = s.substring(i) + s.substring(0, i); if (rot.compareTo(minRot) < 0) minRot = rot; }
            Check.eq("rotationRand", StringsVariants.leastRotation(s), minRot);
            // happy prefix vs brute
            String hp = "";
            for (int L = len - 1; L >= 1; L--) if (s.substring(0, L).equals(s.substring(len - L))) { hp = s.substring(0, L); break; }
            Check.eq("happyRand", StringsVariants.longestHappyPrefix(s), hp);
            // palindrome pairs vs brute
            int w = 2 + r.nextInt(4);
            Set<String> uniq = new LinkedHashSet<>();
            for (int i = 0; i < w; i++) uniq.add(rndStr(r, r.nextInt(4)));
            String[] words = uniq.toArray(new String[0]);
            Set<String> wantPairs = new TreeSet<>(), gotPairs = new TreeSet<>();
            for (int i = 0; i < words.length; i++) for (int j = 0; j < words.length; j++)
                if (i != j && isPalS(words[i] + words[j])) wantPairs.add("[" + i + ", " + j + "]");
            for (List<Integer> pr : StringsVariants.palindromePairs(words)) gotPairs.add(pr.toString());
            Check.eq("palPairsRand", gotPairs, wantPairs);
            // encode / decode round trip
            List<String> strs = List.of(s, rndStr(r, 3) + "#" + rndStr(r, 2), "");
            Check.eq("encodeRand", StringsVariants.decode(StringsVariants.encode(strs)), strs);
        }
        Check.done("Deepen1Test");
    }

    static String rndStr(Random r, int n) { StringBuilder sb = new StringBuilder(); for (int i = 0; i < n; i++) sb.append((char) ('a' + r.nextInt(3))); return sb.toString(); }
    static boolean isPalS(String s) { return new StringBuilder(s).reverse().toString().equals(s); }

    static int wiggleBrute(int[] a) {
        int n = a.length, best = 1;
        for (int m = 1; m < (1 << n); m++) {
            List<Integer> sub = new ArrayList<>();
            for (int i = 0; i < n; i++) if ((m >> i & 1) != 0) sub.add(a[i]);
            boolean ok = true;
            for (int i = 1; i < sub.size(); i++) {
                int d = sub.get(i) - sub.get(i - 1);
                if (d == 0 || (i > 1 && (d > 0) == (sub.get(i - 1) - sub.get(i - 2) > 0))) { ok = false; break; }
            }
            if (ok) best = Math.max(best, sub.size());
        }
        return best;
    }

    static boolean nonDecrBrute(int[] a) {
        if (sorted(a)) return true;
        for (int i = 0; i < a.length; i++) {
            int[] b = a.clone();
            for (int v = -1; v <= 7; v++) { b[i] = v; if (sorted(b)) return true; }
        }
        return false;
    }

    static boolean sorted(int[] a) { for (int i = 1; i < a.length; i++) if (a[i - 1] > a[i]) return false; return true; }

    static int maxSwapBrute(int num) {
        char[] d = String.valueOf(num).toCharArray();
        int best = num;
        for (int i = 0; i < d.length; i++) for (int j = i + 1; j < d.length; j++) {
            char[] c = d.clone();
            char t = c[i]; c[i] = c[j]; c[j] = t;
            best = Math.max(best, Integer.parseInt(new String(c)));
        }
        return best;
    }

    static int monotoneBrute(int n) {
        for (int v = n; v >= 0; v--) {
            String s = String.valueOf(v);
            boolean ok = true;
            for (int i = 1; i < s.length(); i++) if (s.charAt(i - 1) > s.charAt(i)) ok = false;
            if (ok) return v;
        }
        return 0;
    }

    static int brokenBfs(int start, int target) {
        Map<Integer, Integer> dist = new HashMap<>();
        Deque<Integer> q = new ArrayDeque<>();
        dist.put(start, 0);
        q.add(start);
        while (!q.isEmpty()) {
            int v = q.poll();
            if (v == target) return dist.get(v);
            for (int nx : new int[]{v * 2, v - 1}) if (nx >= 1 && nx <= 200 && !dist.containsKey(nx)) { dist.put(nx, dist.get(v) + 1); q.add(nx); }
        }
        return -1;
    }

    static String dupLettersBrute(String s) {
        Set<Character> need = new HashSet<>();
        for (char c : s.toCharArray()) need.add(c);
        String best = null;
        for (int m = 1; m < (1 << s.length()); m++) {
            StringBuilder sb = new StringBuilder();
            Set<Character> seen = new HashSet<>();
            boolean ok = true;
            for (int i = 0; i < s.length(); i++) if ((m >> i & 1) != 0) { if (!seen.add(s.charAt(i))) { ok = false; break; } sb.append(s.charAt(i)); }
            if (ok && seen.equals(need) && (best == null || sb.toString().compareTo(best) < 0)) best = sb.toString();
        }
        return best;
    }

    static int[] maxNumberBrute(int[] a, int[] b, int k) {
        int[] best = null;
        for (int ma = 0; ma < (1 << a.length); ma++) for (int mb = 0; mb < (1 << b.length); mb++) {
            if (Integer.bitCount(ma) + Integer.bitCount(mb) != k) continue;
            List<Integer> sa = new ArrayList<>(), sb = new ArrayList<>();
            for (int i = 0; i < a.length; i++) if ((ma >> i & 1) != 0) sa.add(a[i]);
            for (int i = 0; i < b.length; i++) if ((mb >> i & 1) != 0) sb.add(b[i]);
            // all interleavings
            List<int[]> all = new ArrayList<>();
            interleave(sa, 0, sb, 0, new ArrayList<>(), all);
            for (int[] c : all) if (best == null || cmp(c, best) > 0) best = c;
        }
        return best;
    }

    static void interleave(List<Integer> a, int i, List<Integer> b, int j, List<Integer> cur, List<int[]> out) {
        if (i == a.size() && j == b.size()) { out.add(cur.stream().mapToInt(Integer::intValue).toArray()); return; }
        if (i < a.size()) { cur.add(a.get(i)); interleave(a, i + 1, b, j, cur, out); cur.remove(cur.size() - 1); }
        if (j < b.size()) { cur.add(b.get(j)); interleave(a, i, b, j + 1, cur, out); cur.remove(cur.size() - 1); }
    }

    static int cmp(int[] x, int[] y) { for (int i = 0; i < x.length; i++) if (x[i] != y[i]) return Integer.compare(x[i], y[i]); return 0; }

    static int twoCityBrute(int[][] c, int i, int a, int b) {
        if (i == c.length) return a == b ? 0 : 1_000_000;
        return Math.min(c[i][0] + twoCityBrute(c, i + 1, a + 1, b), c[i][1] + twoCityBrute(c, i + 1, a, b + 1));
    }
}
