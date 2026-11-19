import java.util.*;

public class LinkedListsTest {
    public static void main(String[] x) {
        // ---------- linked lists ----------
        Check.eq("reverse", LinkedLists.str(LinkedLists.reverse(LinkedLists.of(1, 2, 3, 4, 5))), "[5,4,3,2,1]");
        Check.eq("reverseBetween", LinkedLists.str(LinkedLists.reverseBetween(LinkedLists.of(1, 2, 3, 4, 5), 2, 4)), "[1,4,3,2,5]");
        Check.eq("reverseK", LinkedLists.str(LinkedLists.reverseKGroup(LinkedLists.of(1, 2, 3, 4, 5), 2)), "[2,1,4,3,5]");
        Check.eq("swapPairs", LinkedLists.str(LinkedLists.swapPairs(LinkedLists.of(1, 2, 3, 4))), "[2,1,4,3]");
        Check.eq("middle", LinkedLists.middle(LinkedLists.of(1, 2, 3, 4, 5)).val + "," + LinkedLists.middle(LinkedLists.of(1, 2, 3, 4)).val, "3,3");
        LinkedLists.ListNode cyc = LinkedLists.of(3, 2, 0, -4);
        LinkedLists.ListNode tail = cyc;
        while (tail.next != null) tail = tail.next;
        tail.next = cyc.next;
        Check.eq("cycle", LinkedLists.detectCycle(cyc).val, 2);
        Check.eq("noCycle", LinkedLists.detectCycle(LinkedLists.of(1, 2)) == null, true);
        Check.eq("removeNth", LinkedLists.str(LinkedLists.removeNthFromEnd(LinkedLists.of(1, 2, 3, 4, 5), 2)), "[1,2,3,5]");
        Check.eq("removeNthHead", LinkedLists.str(LinkedLists.removeNthFromEnd(LinkedLists.of(1), 1)), "[]");
        Check.eq("mergeTwo", LinkedLists.str(LinkedLists.mergeTwo(LinkedLists.of(1, 2, 4), LinkedLists.of(1, 3, 4))), "[1,1,2,3,4,4]");
        Check.eq("mergeK", LinkedLists.str(LinkedLists.mergeKLists(new LinkedLists.ListNode[]{LinkedLists.of(1, 4, 5), LinkedLists.of(1, 3, 4), LinkedLists.of(2, 6)})), "[1,1,2,3,4,4,5,6]");
        Check.eq("sortList", LinkedLists.str(LinkedLists.sortList(LinkedLists.of(4, 2, 1, 3))), "[1,2,3,4]");
        LinkedLists.ListNode ro = LinkedLists.of(1, 2, 3, 4, 5);
        LinkedLists.reorderList(ro);
        Check.eq("reorder", LinkedLists.str(ro), "[1,5,2,4,3]");
        Check.eq("palList", LinkedLists.isPalindrome(LinkedLists.of(1, 2, 2, 1)) + "," + LinkedLists.isPalindrome(LinkedLists.of(1, 2)), "true,false");
        Check.eq("addNumbers", LinkedLists.str(LinkedLists.addTwoNumbers(LinkedLists.of(2, 4, 3), LinkedLists.of(5, 6, 4))), "[7,0,8]");
        LinkedLists.ListNode shared = LinkedLists.of(8, 4, 5), a = LinkedLists.of(4, 1), b = LinkedLists.of(5, 6, 1);
        a.next.next = shared;
        b.next.next.next = shared;
        Check.eq("intersect", LinkedLists.getIntersectionNode(a, b).val, 8);
        Check.eq("dedupe2", LinkedLists.str(LinkedLists.deleteDuplicates(LinkedLists.of(1, 2, 3, 3, 4, 4, 5))), "[1,2,5]");
        Check.eq("partition", LinkedLists.str(LinkedLists.partition(LinkedLists.of(1, 4, 3, 2, 5, 2), 3)), "[1,2,2,4,3,5]");
        Check.eq("rotateList", LinkedLists.str(LinkedLists.rotateRight(LinkedLists.of(1, 2, 3, 4, 5), 2)), "[4,5,1,2,3]");
        Check.eq("oddEven", LinkedLists.str(LinkedLists.oddEvenList(LinkedLists.of(1, 2, 3, 4, 5))), "[1,3,5,2,4]");
        LinkedLists.RNode r1 = new LinkedLists.RNode(1), r2 = new LinkedLists.RNode(2);
        r1.next = r2; r1.random = r2; r2.random = r2;
        LinkedLists.RNode cp = LinkedLists.copyRandomList(r1);
        Check.eq("copyRandom", (cp != r1) + "," + (cp.random == cp.next) + "," + (cp.next.random == cp.next) + "," + (r1.next == r2), "true,true,true,true");

        // ---------- randomized cross-check: sort vs array sort ----------
        Random r = new Random(9);
        for (int t = 0; t < 300; t++) {
            int len = r.nextInt(10);
            int[] arr = new int[len];
            for (int i = 0; i < len; i++) arr[i] = r.nextInt(10);
            int[] sorted = arr.clone();
            Arrays.sort(sorted);
            Check.eq("sortListRand", LinkedLists.str(LinkedLists.sortList(LinkedLists.of(arr))), LinkedLists.str(LinkedLists.of(sorted)));
        }
        Check.done("LinkedListsTest");
    }

}
