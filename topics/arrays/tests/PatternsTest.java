import java.util.*;

// Covers arrays, windows, stacks, heaps, intervals, binary-search, backtracking, greedy.
public class PatternsTest {
    static String L(Object o) { return String.valueOf(o); }

    public static void main(String[] x) {
        // ---------- arrays ----------
        Check.eq("twoSum", Check.s(ArraysHashing.twoSum(new int[]{2, 7, 11, 15}, 9)), "[0, 1]");
        Check.eq("subSumK", ArraysHashing.subarraySum(new int[]{1, 1, 1}, 2) + "," + ArraysHashing.subarraySum(new int[]{1, -1, 0}, 0), "2,3");
        Check.eq("divK", ArraysHashing.subarraysDivByK(new int[]{4, 5, 0, -2, -3, 1}, 5), 7);
        Check.eq("contig", ArraysHashing.findMaxLength(new int[]{0, 1, 0}), 2);
        Check.eq("prodSelf", Check.s(ArraysHashing.productExceptSelf(new int[]{1, 2, 3, 4})), "[24, 12, 8, 6]");
        Check.eq("anagrams", ArraysHashing.groupAnagrams(new String[]{"eat", "tea", "tan", "ate", "nat", "bat"}).size(), 3);
        Check.eq("consec", ArraysHashing.longestConsecutive(new int[]{100, 4, 200, 1, 3, 2}), 4);
        int[] tk = ArraysHashing.topKFrequent(new int[]{1, 1, 1, 2, 2, 3}, 2);
        Arrays.sort(tk);
        Check.eq("topK", Check.s(tk), "[1, 2]");
        Check.eq("firstMissing", ArraysHashing.firstMissingPositive(new int[]{3, 4, -1, 1}) + "," + ArraysHashing.firstMissingPositive(new int[]{7, 8, 9}), "2,1");
        Check.eq("dup", ArraysHashing.findDuplicate(new int[]{1, 3, 4, 2, 2}), 2);
        Check.eq("majority", ArraysHashing.majorityElement(new int[]{2, 2, 1, 1, 1, 2, 2}), 2);
        int[] col = {2, 0, 2, 1, 1, 0};
        ArraysHashing.sortColors(col);
        Check.eq("flag", Check.s(col), "[0, 0, 1, 1, 2, 2]");
        int[] np = {1, 2, 3};
        ArraysHashing.nextPermutation(np);
        Check.eq("nextPerm", Check.s(np), "[1, 3, 2]");
        int[] np2 = {3, 2, 1};
        ArraysHashing.nextPermutation(np2);
        Check.eq("nextPermWrap", Check.s(np2), "[1, 2, 3]");
        int[] rot = {1, 2, 3, 4, 5, 6, 7};
        ArraysHashing.rotate(rot, 3);
        Check.eq("rotate", Check.s(rot), "[5, 6, 7, 1, 2, 3, 4]");
        int[][] rm = {{1, 2, 3}, {4, 5, 6}, {7, 8, 9}};
        ArraysHashing.rotateMatrix(rm);
        Check.eq("rotMatrix", Arrays.deepToString(rm), "[[7, 4, 1], [8, 5, 2], [9, 6, 3]]");
        Check.eq("spiral", ArraysHashing.spiralOrder(new int[][]{{1, 2, 3, 4}, {5, 6, 7, 8}, {9, 10, 11, 12}}), "[1, 2, 3, 4, 8, 12, 11, 10, 9, 5, 6, 7]");
        int[][] sz = {{1, 1, 1}, {1, 0, 1}, {1, 1, 1}};
        ArraysHashing.setZeroes(sz);
        Check.eq("setZeroes", Arrays.deepToString(sz), "[[1, 0, 1], [0, 0, 0], [1, 0, 1]]");
        Check.eq("trap", ArraysHashing.trap(new int[]{0, 1, 0, 2, 1, 0, 1, 3, 2, 1, 2, 1}), 6);

        // ---------- windows ----------
        Check.eq("longestSub", SlidingWindowPointers.lengthOfLongestSubstring("abcabcbb") + "," + SlidingWindowPointers.lengthOfLongestSubstring("pwwkew"), "3,3");
        Check.eq("charReplace", SlidingWindowPointers.characterReplacement("AABABBA", 1), 4);
        Check.eq("minWindow", SlidingWindowPointers.minWindow("ADOBECODEBANC", "ABC"), "BANC");
        Check.eq("minSubLen", SlidingWindowPointers.minSubArrayLen(7, new int[]{2, 3, 1, 2, 4, 3}), 2);
        Check.eq("kDistinct", SlidingWindowPointers.lengthOfLongestSubstringKDistinct("eceba", 2), 3);
        Check.eq("exactK", SlidingWindowPointers.subarraysWithKDistinct(new int[]{1, 2, 1, 2, 3}, 2), 7);
        Check.eq("onesIII", SlidingWindowPointers.longestOnes(new int[]{1, 1, 1, 0, 0, 0, 1, 1, 1, 1, 0}, 2), 6);
        Check.eq("anagramsAll", SlidingWindowPointers.findAnagrams("cbaebabacd", "abc"), "[0, 6]");
        Check.eq("maxAvg", SlidingWindowPointers.findMaxAverage(new int[]{1, 12, -5, -6, 50, 3}, 4), 12.75);
        Check.eq("twoSumSorted", Check.s(SlidingWindowPointers.twoSumSorted(new int[]{2, 7, 11, 15}, 9)), "[1, 2]");
        Check.eq("threeSum", SlidingWindowPointers.threeSum(new int[]{-1, 0, 1, 2, -1, -4}), "[[-1, -1, 2], [-1, 0, 1]]");
        Check.eq("threeClosest", SlidingWindowPointers.threeSumClosest(new int[]{-1, 2, 1, -4}, 1), 2);
        Check.eq("container", SlidingWindowPointers.maxArea(new int[]{1, 8, 6, 2, 5, 4, 8, 3, 7}), 49);
        int[] dd = {0, 0, 1, 1, 1, 2, 2, 3, 3, 4};
        Check.eq("removeDup", SlidingWindowPointers.removeDuplicates(dd), 5);
        int[] mz = {0, 1, 0, 3, 12};
        SlidingWindowPointers.moveZeroes(mz);
        Check.eq("moveZeroes", Check.s(mz), "[1, 3, 12, 0, 0]");
        Check.eq("palin", SlidingWindowPointers.isPalindrome("A man, a plan, a canal: Panama"), true);
        int[] mg = {1, 2, 3, 0, 0, 0};
        SlidingWindowPointers.merge(mg, 3, new int[]{2, 5, 6}, 3);
        Check.eq("merge", Check.s(mg), "[1, 2, 2, 3, 5, 6]");
        Check.eq("prodLessK", SlidingWindowPointers.numSubarrayProductLessThanK(new int[]{10, 5, 2, 6}, 100), 8);

        // ---------- stacks ----------
        Check.eq("nge", Check.s(MonotonicStructures.nextGreater(new int[]{2, 1, 2, 4, 3})), "[4, 2, 4, -1, -1]");
        Check.eq("ngeCirc", Check.s(MonotonicStructures.nextGreaterCircular(new int[]{1, 2, 1})), "[2, -1, 2]");
        Check.eq("daily", Check.s(MonotonicStructures.dailyTemperatures(new int[]{73, 74, 75, 71, 69, 72, 76, 73})), "[1, 1, 4, 2, 1, 1, 0, 0]");
        Check.eq("histogram", MonotonicStructures.largestRectangleArea(new int[]{2, 1, 5, 6, 2, 3}), 10);
        Check.eq("maxRect", MonotonicStructures.maximalRectangle(new char[][]{"10100".toCharArray(), "10111".toCharArray(), "11111".toCharArray(), "10010".toCharArray()}), 6);
        Check.eq("sumMins", MonotonicStructures.sumSubarrayMins(new int[]{3, 1, 2, 4}), 17);
        Check.eq("removeK", MonotonicStructures.removeKdigits("1432219", 3) + "," + MonotonicStructures.removeKdigits("10200", 1) + "," + MonotonicStructures.removeKdigits("10", 2), "1219,200,0");
        MonotonicStructures.StockSpanner ss = new MonotonicStructures.StockSpanner();
        int[] spans = new int[7];
        int[] prices = {100, 80, 60, 70, 60, 75, 85};
        for (int i = 0; i < 7; i++) spans[i] = ss.next(prices[i]);
        Check.eq("span", Check.s(spans), "[1, 1, 1, 2, 1, 4, 6]");
        Check.eq("valid", MonotonicStructures.isValid("()[]{}") + "," + MonotonicStructures.isValid("(]"), "true,false");
        Check.eq("longestValid", MonotonicStructures.longestValidParentheses(")()())"), 4);
        Check.eq("calc1", MonotonicStructures.calculate("(1+(4+5+2)-3)+(6+8)"), 23);
        Check.eq("calc2", MonotonicStructures.calculate2(" 3+5 / 2 "), 5);
        Check.eq("decode", MonotonicStructures.decodeString("3[a2[c]]"), "accaccacc");
        Check.eq("rpn", MonotonicStructures.evalRPN(new String[]{"4", "13", "5", "/", "+"}), 6);
        MonotonicStructures.MinStack ms = new MonotonicStructures.MinStack();
        ms.push(-2); ms.push(0); ms.push(-3);
        int m1 = ms.getMin();
        ms.pop();
        Check.eq("minStack", m1 + "," + ms.top() + "," + ms.getMin(), "-3,0,-2");
        Check.eq("slideMax", Check.s(MonotonicStructures.maxSlidingWindow(new int[]{1, 3, -1, -3, 5, 3, 6, 7}, 3)), "[3, 3, 5, 5, 6, 7]");
        Check.eq("shortestK", MonotonicStructures.shortestSubarray(new int[]{2, -1, 2}, 3), 3);
        Check.eq("limit", MonotonicStructures.longestSubarray(new int[]{8, 2, 4, 7}, 4), 2);

        // ---------- heaps ----------
        Check.eq("kth", HeapsTopK.findKthLargest(new int[]{3, 2, 1, 5, 6, 4}, 2), 5);
        Check.eq("quickselect", HeapsTopK.quickselectKthLargest(new int[]{3, 2, 3, 1, 2, 4, 5, 5, 6}, 4), 4);
        Check.eq("kClosest", HeapsTopK.kClosest(new int[][]{{1, 3}, {-2, 2}}, 1)[0][0], -2);
        Check.eq("topWords", HeapsTopK.topKFrequentWords(new String[]{"i", "love", "leetcode", "i", "love", "coding"}, 2), "[i, love]");
        HeapsTopK.MedianFinder mf = new HeapsTopK.MedianFinder();
        mf.addNum(1); mf.addNum(2);
        double m12 = mf.findMedian();
        mf.addNum(3);
        Check.eq("median", m12 + "," + mf.findMedian(), "1.5,2.0");
        Check.eq("slideMedian", Arrays.toString(HeapsTopK.medianSlidingWindow(new int[]{1, 3, -1, -3, 5, 3, 6, 7}, 3)), "[1.0, -1.0, -1.0, 3.0, 5.0, 6.0]");
        Check.eq("mergeK", HeapsTopK.mergeKSorted(List.of(new int[]{1, 4, 5}, new int[]{1, 3, 4}, new int[]{2, 6})), "[1, 1, 2, 3, 4, 4, 5, 6]");
        Check.eq("kthMatrix", HeapsTopK.kthSmallest(new int[][]{{1, 5, 9}, {10, 11, 13}, {12, 13, 15}}, 8), 13);
        Check.eq("smallestRange", Check.s(HeapsTopK.smallestRange(List.of(List.of(4, 10, 15, 24, 26), List.of(0, 9, 12, 20), List.of(5, 18, 22, 30)))), "[20, 24]");
        Check.eq("taskSched", HeapsTopK.leastInterval(new char[]{'A', 'A', 'A', 'B', 'B', 'B'}, 2), 8);
        String re = HeapsTopK.reorganizeString("aab");
        Check.eq("reorg", re.length() == 3 && re.charAt(0) != re.charAt(1) && re.charAt(1) != re.charAt(2), true);
        Check.eq("reorgImpossible", HeapsTopK.reorganizeString("aaab"), "");
        Check.eq("rooms", HeapsTopK.minMeetingRooms(new int[][]{{0, 30}, {5, 10}, {15, 20}}), 2);
        Check.eq("ipo", HeapsTopK.findMaximizedCapital(2, 0, new int[]{1, 2, 3}, new int[]{0, 1, 1}), 4);
        Check.eq("buildings", HeapsTopK.furthestBuilding(new int[]{4, 2, 7, 6, 9, 14, 12}, 5, 1), 4);
        Check.eq("ugly", HeapsTopK.nthUglyNumber(10), 12);

        // ---------- intervals ----------
        Check.eq("mergeIv", Arrays.deepToString(Intervals.merge(new int[][]{{1, 3}, {2, 6}, {8, 10}, {15, 18}})), "[[1, 6], [8, 10], [15, 18]]");
        Check.eq("insertIv", Arrays.deepToString(Intervals.insert(new int[][]{{1, 3}, {6, 9}}, new int[]{2, 5})), "[[1, 5], [6, 9]]");
        Check.eq("eraseOverlap", Intervals.eraseOverlapIntervals(new int[][]{{1, 2}, {2, 3}, {3, 4}, {1, 3}}), 1);
        Check.eq("arrows", Intervals.findMinArrowShots(new int[][]{{10, 16}, {2, 8}, {1, 6}, {7, 12}}), 2);
        Check.eq("intersect", Arrays.deepToString(Intervals.intervalIntersection(new int[][]{{0, 2}, {5, 10}}, new int[][]{{1, 5}, {8, 12}})), "[[1, 2], [5, 5], [8, 10]]");
        Check.eq("attend", Intervals.canAttendMeetings(new int[][]{{0, 30}, {5, 10}}), false);
        Check.eq("overlap", Intervals.maxOverlap(new int[][]{{0, 30}, {5, 10}, {15, 20}}), 2);
        Check.eq("carpool", Intervals.carPooling(new int[][]{{2, 1, 5}, {3, 3, 7}}, 4) + "," + Intervals.carPooling(new int[][]{{2, 1, 5}, {3, 5, 7}}, 3), "false,true");
        Intervals.MyCalendar cal = new Intervals.MyCalendar();
        Check.eq("calendar", cal.book(10, 20) + "," + cal.book(15, 25) + "," + cal.book(20, 30), "true,false,true");
        Intervals.MyCalendarThree cal3 = new Intervals.MyCalendarThree();
        cal3.book(10, 20); cal3.book(50, 60); cal3.book(10, 40);
        Check.eq("calendar3", cal3.book(5, 15), 3);
        Check.eq("covered", Intervals.removeCoveredIntervals(new int[][]{{1, 4}, {3, 6}, {2, 8}}), 2);
        Intervals.SummaryRanges sr = new Intervals.SummaryRanges();
        for (int v : new int[]{1, 3, 7, 2, 6}) sr.addNum(v);
        Check.eq("summary", Arrays.deepToString(sr.getIntervals()), "[[1, 3], [6, 7]]");
        List<List<int[]>> sched = List.of(List.of(new int[]{1, 2}, new int[]{5, 6}), List.of(new int[]{1, 3}), List.of(new int[]{4, 10}));
        Check.eq("freeTime", Arrays.deepToString(Intervals.employeeFreeTime(sched).toArray()), "[[3, 4]]");

        // ---------- binary search ----------
        int[] bs = {5, 7, 7, 8, 8, 10};
        Check.eq("range", Check.s(BinarySearch.searchRange(bs, 8)) + Check.s(BinarySearch.searchRange(bs, 6)), "[3, 4][-1, -1]");
        Check.eq("insertPos", BinarySearch.searchInsert(new int[]{1, 3, 5, 6}, 2), 1);
        Check.eq("rotSearch", BinarySearch.searchRotated(new int[]{4, 5, 6, 7, 0, 1, 2}, 0) + "," + BinarySearch.searchRotated(new int[]{4, 5, 6, 7, 0, 1, 2}, 3), "4,-1");
        Check.eq("rotMin", BinarySearch.findMin(new int[]{3, 4, 5, 1, 2}) + "," + BinarySearch.findMin(new int[]{2, 2, 2, 0, 1}), "1,0");
        Check.eq("peak", BinarySearch.findPeakElement(new int[]{1, 2, 1, 3, 5, 6, 4}) == 5 || BinarySearch.findPeakElement(new int[]{1, 2, 1, 3, 5, 6, 4}) == 1, true);
        Check.eq("matrix", BinarySearch.searchMatrix(new int[][]{{1, 3, 5, 7}, {10, 11, 16, 20}, {23, 30, 34, 60}}, 3) + "," + BinarySearch.searchMatrixII(new int[][]{{1, 4, 7}, {2, 5, 8}, {3, 6, 9}}, 5), "true,true");
        Check.eq("koko", BinarySearch.minEatingSpeed(new int[]{3, 6, 7, 11}, 8), 4);
        Check.eq("ship", BinarySearch.shipWithinDays(new int[]{1, 2, 3, 4, 5, 6, 7, 8, 9, 10}, 5), 15);
        Check.eq("split", BinarySearch.splitArray(new int[]{7, 2, 5, 10, 8}, 2), 18);
        Check.eq("bouquets", BinarySearch.minDays(new int[]{1, 10, 3, 10, 2}, 3, 1), 3);
        Check.eq("multTable", BinarySearch.findKthNumber(3, 3, 5), 3);
        Check.eq("median2", BinarySearch.findMedianSortedArrays(new int[]{1, 3}, new int[]{2}) + "," + BinarySearch.findMedianSortedArrays(new int[]{1, 2}, new int[]{3, 4}), "2.0,2.5");
        Check.eq("sqrt", BinarySearch.mySqrt(8) + "," + BinarySearch.mySqrt(2147395599), "2,46339");
        Check.eq("sqrtD", String.format("%.4f", BinarySearch.sqrt(2)), "1.4142");
        BinarySearch.TimeMap tm = new BinarySearch.TimeMap();
        tm.set("foo", "bar", 1);
        tm.set("foo", "bar2", 4);
        Check.eq("timeMap", tm.get("foo", 3) + "," + tm.get("foo", 4) + "," + tm.get("foo", 0), "bar,bar2,");
        Check.eq("closestK", BinarySearch.findClosestElements(new int[]{1, 2, 3, 4, 5}, 4, 3), "[1, 2, 3, 4]");

        // ---------- backtracking ----------
        Check.eq("subsets", Backtracking.subsets(new int[]{1, 2, 3}).size(), 8);
        Check.eq("subsetsDup", Backtracking.subsetsWithDup(new int[]{1, 2, 2}).size(), 6);
        Check.eq("perms", Backtracking.permute(new int[]{1, 2, 3}).size(), 6);
        Check.eq("permsUniq", Backtracking.permuteUnique(new int[]{1, 1, 2}).size(), 3);
        Check.eq("combine", Backtracking.combine(4, 2).size(), 6);
        Check.eq("comboSum", Backtracking.combinationSum(new int[]{2, 3, 6, 7}, 7), "[[2, 2, 3], [7]]");
        Check.eq("comboSum2", Backtracking.combinationSum2(new int[]{10, 1, 2, 7, 6, 1, 5}, 8), "[[1, 1, 6], [1, 2, 5], [1, 7], [2, 6]]");
        Check.eq("parens", Backtracking.generateParenthesis(3), "[((())), (()()), (())(), ()(()), ()()()]");
        Check.eq("phone", Backtracking.letterCombinations("23").size(), 9);
        Check.eq("palPart", Backtracking.partition("aab"), "[[a, a, b], [aa, b]]");
        Check.eq("wordSearch", Backtracking.exist(new char[][]{"ABCE".toCharArray(), "SFCS".toCharArray(), "ADEE".toCharArray()}, "ABCCED") + "," + Backtracking.exist(new char[][]{"ABCE".toCharArray(), "SFCS".toCharArray(), "ADEE".toCharArray()}, "ABCB"), "true,false");
        Check.eq("queens", Backtracking.solveNQueens(4).size() + "," + Backtracking.solveNQueens(6).size() + "," + Backtracking.totalNQueens(8), "2,4,92");
        char[][] sud = {
            "53..7....".toCharArray(), "6..195...".toCharArray(), ".98....6.".toCharArray(),
            "8...6...3".toCharArray(), "4..8.3..1".toCharArray(), "7...2...6".toCharArray(),
            ".6....28.".toCharArray(), "...419..5".toCharArray(), "....8..79".toCharArray()};
        Check.eq("sudoku", Backtracking.solveSudoku(sud) + "," + new String(sud[0]), "true,534678912");
        Check.eq("ips", Backtracking.restoreIpAddresses("25525511135"), "[255.255.11.135, 255.255.111.35]");
        List<String> ops = Backtracking.addOperators("123", 6);
        Collections.sort(ops);
        Check.eq("addOps", ops, "[1*2*3, 1+2+3]");
        Check.eq("gray", Backtracking.grayCode(2), "[0, 1, 3, 2]");

        // ---------- greedy ----------
        Check.eq("jump1", Greedy.canJump(new int[]{2, 3, 1, 1, 4}) + "," + Greedy.canJump(new int[]{3, 2, 1, 0, 4}), "true,false");
        Check.eq("jump2", Greedy.jump(new int[]{2, 3, 1, 1, 4}), 2);
        Check.eq("gas", Greedy.canCompleteCircuit(new int[]{1, 2, 3, 4, 5}, new int[]{3, 4, 5, 1, 2}), 3);
        Check.eq("candy", Greedy.candy(new int[]{1, 0, 2}), 5);
        Check.eq("labels", Greedy.partitionLabels("ababcbacadefegdehijhklij"), "[9, 7, 8]");
        Check.eq("queue", Arrays.deepToString(Greedy.reconstructQueue(new int[][]{{7, 0}, {4, 4}, {7, 1}, {5, 0}, {6, 1}, {5, 2}})), "[[5, 0], [7, 0], [5, 2], [6, 1], [4, 4], [7, 1]]");
        Check.eq("cookies", Greedy.findContentChildren(new int[]{1, 2, 3}, new int[]{1, 1}), 1);
        Check.eq("largestNum", Greedy.largestNumber(new int[]{3, 30, 34, 5, 9}), "9534330");
        Check.eq("units", Greedy.maximumUnits(new int[][]{{1, 3}, {2, 2}, {3, 1}}, 4), 8);
        Check.eq("validStr", Greedy.checkValidString("(*))") + "," + Greedy.checkValidString("(*)))"), "true,false");
        Check.eq("boats", Greedy.numRescueBoats(new int[]{3, 2, 2, 1}, 3), 3);
        Check.eq("refuel", Greedy.minRefuelStops(100, 10, new int[][]{{10, 60}, {20, 30}, {30, 30}, {60, 40}}), 2);
        Check.eq("straights", Greedy.isNStraightHand(new int[]{1, 2, 3, 6, 2, 3, 4, 7, 8}, 3) + "," + Greedy.isNStraightHand(new int[]{1, 2, 3, 4, 5}, 4), "true,false");
        Check.eq("lemonade", Greedy.lemonadeChange(new int[]{5, 5, 5, 10, 20}) + "," + Greedy.lemonadeChange(new int[]{10, 10}), "true,false");

        // ---------- randomized cross-checks ----------
        Random r = new Random(5);
        for (int t = 0; t < 300; t++) {
            int n = 1 + r.nextInt(15);
            int[] a = new int[n];
            for (int i = 0; i < n; i++) a[i] = r.nextInt(20) - 10;
            int[] sortedA = a.clone();
            Arrays.sort(sortedA);
            int val = r.nextInt(24) - 12;
            Check.eq("lowerBound", BinarySearch.lowerBound(sortedA, val), linearLower(sortedA, val));
            Check.eq("upperBound", BinarySearch.upperBound(sortedA, val), linearUpper(sortedA, val));

            int k = 1 + r.nextInt(n);
            int[] sc = sortedA.clone();
            Check.eq("kthHeap", HeapsTopK.findKthLargest(a, k), sc[n - k]);
            Check.eq("kthQuick", HeapsTopK.quickselectKthLargest(a, k), sc[n - k]);

            // sliding window max vs naive
            int w = 1 + r.nextInt(n);
            int[] got = MonotonicStructures.maxSlidingWindow(a, w);
            for (int i = 0; i + w <= n; i++) {
                int mx = Integer.MIN_VALUE;
                for (int j = i; j < i + w; j++) mx = Math.max(mx, a[j]);
                Check.eq("slideRand", got[i], mx);
            }

            // subarraySum vs brute
            int target = r.nextInt(7) - 3, brute = 0;
            for (int i = 0; i < n; i++) { int s = 0; for (int j = i; j < n; j++) { s += a[j]; if (s == target) brute++; } }
            Check.eq("subSumRand", ArraysHashing.subarraySum(a, target), brute);

            // largest rectangle vs brute (non-negative heights)
            int[] hgt = new int[n];
            for (int i = 0; i < n; i++) hgt[i] = r.nextInt(6);
            int best = 0;
            for (int i = 0; i < n; i++) { int mn = Integer.MAX_VALUE; for (int j = i; j < n; j++) { mn = Math.min(mn, hgt[j]); best = Math.max(best, mn * (j - i + 1)); } }
            Check.eq("histRand", MonotonicStructures.largestRectangleArea(hgt), best);

            // merge intervals vs coverage count
            int m = 1 + r.nextInt(6);
            int[][] iv = new int[m][2];
            boolean[] cov = new boolean[40];
            for (int i = 0; i < m; i++) { int s = r.nextInt(30), e = s + 1 + r.nextInt(8); iv[i] = new int[]{s, e}; for (int c = s; c < e; c++) cov[c] = true; }
            int covered = 0;
            for (boolean b : cov) if (b) covered++;
            int sum = 0;
            for (int[] seg : Intervals.merge(iv)) sum += seg[1] - seg[0];
            Check.eq("mergeRand", sum, covered);

            // koko binary search vs linear scan
            int[] piles = new int[1 + r.nextInt(5)];
            for (int i = 0; i < piles.length; i++) piles[i] = 1 + r.nextInt(12);
            int hrs = piles.length + r.nextInt(10);
            int lin = 1;
            while (true) { long need = 0; for (int p : piles) need += (p + lin - 1) / lin; if (need <= hrs) break; lin++; }
            Check.eq("kokoRand", BinarySearch.minEatingSpeed(piles, hrs), lin);
        }
        Check.done("PatternsTest");
    }

    static int linearLower(int[] a, int v) { int i = 0; while (i < a.length && a[i] < v) i++; return i; }
    static int linearUpper(int[] a, int v) { int i = 0; while (i < a.length && a[i] <= v) i++; return i; }
}
