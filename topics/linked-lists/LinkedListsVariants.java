import java.util.*;

public class LinkedListsVariants {
    // Reuses LinkedLists.ListNode, LinkedLists.of and LinkedLists.str.
    // Rule of thumb: dummy head when the head can change; two pointers (gap or fast/slow) when a length is needed; stack when "next greater".

    // ---------- Remove Zero Sum Consecutive Nodes (LC 1171): equal prefix sums bracket a zero-sum run ----------
    static LinkedLists.ListNode removeZeroSumSublists(LinkedLists.ListNode head) {
        LinkedLists.ListNode dummy = new LinkedLists.ListNode(0);
        dummy.next = head;
        Map<Integer, LinkedLists.ListNode> seen = new HashMap<>();
        int prefix = 0;
        for (LinkedLists.ListNode n = dummy; n != null; n = n.next) { // pass 1: remember the LAST node for each prefix sum
            prefix += n.val;
            seen.put(prefix, n);
        }
        prefix = 0;
        for (LinkedLists.ListNode n = dummy; n != null; n = n.next) { // pass 2: jump over everything up to that last node
            prefix += n.val;
            n.next = seen.get(prefix).next;
        }
        return dummy.next;
    }

    // ---------- Next Greater Node in Linked List (LC 1019): copy to an array, monotone stack ----------
    static int[] nextLargerNodes(LinkedLists.ListNode head) {
        List<Integer> vals = new ArrayList<>();
        for (LinkedLists.ListNode n = head; n != null; n = n.next) vals.add(n.val);
        int[] res = new int[vals.size()];
        Deque<Integer> st = new ArrayDeque<>();
        for (int i = 0; i < res.length; i++) {
            while (!st.isEmpty() && vals.get(st.peek()) < vals.get(i)) res[st.pop()] = vals.get(i);
            st.push(i);
        }
        return res;
    }

    // ---------- Split Linked List in Parts (LC 725): sizes differ by at most one, earlier parts larger ----------
    static LinkedLists.ListNode[] splitListToParts(LinkedLists.ListNode head, int k) {
        int n = 0;
        for (LinkedLists.ListNode c = head; c != null; c = c.next) n++;
        LinkedLists.ListNode[] parts = new LinkedLists.ListNode[k];
        LinkedLists.ListNode cur = head;
        for (int i = 0; i < k; i++) {
            int size = n / k + (i < n % k ? 1 : 0);
            parts[i] = cur;
            for (int j = 0; j < size - 1 && cur != null; j++) cur = cur.next;
            if (cur != null) { LinkedLists.ListNode nx = cur.next; cur.next = null; cur = nx; }
        }
        return parts;
    }

    // ---------- Plus One Linked List (LC 369): rightmost non-9 node gets +1, everything after becomes 0 ----------
    static LinkedLists.ListNode plusOne(LinkedLists.ListNode head) {
        LinkedLists.ListNode dummy = new LinkedLists.ListNode(0), notNine = dummy;
        dummy.next = head;
        for (LinkedLists.ListNode n = head; n != null; n = n.next) if (n.val != 9) notNine = n;
        notNine.val++;
        for (LinkedLists.ListNode n = notNine.next; n != null; n = n.next) n.val = 0;
        return dummy.val == 0 ? head : dummy;
    }

    // ---------- Swapping Nodes in a Linked List (LC 1721): kth from the start and kth from the end, swap values ----------
    static LinkedLists.ListNode swapNodes(LinkedLists.ListNode head, int k) {
        LinkedLists.ListNode first = head;
        for (int i = 1; i < k; i++) first = first.next;
        LinkedLists.ListNode second = head, fast = first;
        while (fast.next != null) { fast = fast.next; second = second.next; } // gap of k - 1 puts `second` kth from the end
        int t = first.val; first.val = second.val; second.val = t;
        return head;
    }

    // ---------- Delete the Middle Node (LC 2095): slow stops one before the middle if fast starts two ahead ----------
    static LinkedLists.ListNode deleteMiddle(LinkedLists.ListNode head) {
        if (head.next == null) return null;
        LinkedLists.ListNode slow = head, fast = head.next.next;
        while (fast != null && fast.next != null) { slow = slow.next; fast = fast.next.next; }
        slow.next = slow.next.next;
        return head;
    }

    // ---------- Maximum Twin Sum (LC 2130): reverse the second half, then walk both halves ----------
    static int pairSum(LinkedLists.ListNode head) {
        LinkedLists.ListNode slow = head, fast = head;
        while (fast != null && fast.next != null) { slow = slow.next; fast = fast.next.next; }
        LinkedLists.ListNode prev = null;
        while (slow != null) { LinkedLists.ListNode nx = slow.next; slow.next = prev; prev = slow; slow = nx; }
        int best = 0;
        for (LinkedLists.ListNode a = head, b = prev; b != null; a = a.next, b = b.next) best = Math.max(best, a.val + b.val);
        return best;
    }

    // ---------- Merge Nodes in Between Zeros (LC 2181) ----------
    static LinkedLists.ListNode mergeNodes(LinkedLists.ListNode head) {
        LinkedLists.ListNode dummy = new LinkedLists.ListNode(0), tail = dummy;
        int sum = 0;
        for (LinkedLists.ListNode n = head.next; n != null; n = n.next) {
            if (n.val == 0) { tail.next = new LinkedLists.ListNode(sum); tail = tail.next; sum = 0; }
            else sum += n.val;
        }
        return dummy.next;
    }

    // ---------- Merge In Between Linked Lists (LC 1669): replace nodes a..b of list1 with all of list2 ----------
    static LinkedLists.ListNode mergeInBetween(LinkedLists.ListNode list1, int a, int b, LinkedLists.ListNode list2) {
        LinkedLists.ListNode before = list1;
        for (int i = 0; i < a - 1; i++) before = before.next;
        LinkedLists.ListNode after = before;
        for (int i = 0; i < b - a + 2; i++) after = after.next;
        before.next = list2;
        LinkedLists.ListNode tail = list2;
        while (tail.next != null) tail = tail.next;
        tail.next = after;
        return list1;
    }

    // ---------- Reverse Nodes in Even Length Groups (LC 2074): group sizes 1, 2, 3, ...; reverse a group if its real size is even ----------
    static LinkedLists.ListNode reverseEvenLengthGroups(LinkedLists.ListNode head) {
        LinkedLists.ListNode dummy = new LinkedLists.ListNode(0), prev = dummy;
        dummy.next = head;
        for (int size = 1; prev.next != null; size++) {
            int count = 0;
            LinkedLists.ListNode probe = prev.next;
            while (count < size && probe != null) { count++; probe = probe.next; }
            if (count % 2 == 0) {
                LinkedLists.ListNode groupHead = prev.next, cur = groupHead, rev = probe;
                for (int i = 0; i < count; i++) { LinkedLists.ListNode nx = cur.next; cur.next = rev; rev = cur; cur = nx; }
                prev.next = rev;
                prev = groupHead; // old head is now the group's tail
            } else {
                for (int i = 0; i < count; i++) prev = prev.next;
            }
        }
        return dummy.next;
    }

    // ---------- Insertion Sort List (LC 147): O(n^2) time, O(1) space, dummy head ----------
    static LinkedLists.ListNode insertionSortList(LinkedLists.ListNode head) {
        LinkedLists.ListNode dummy = new LinkedLists.ListNode(Integer.MIN_VALUE);
        while (head != null) {
            LinkedLists.ListNode nx = head.next, p = dummy;
            while (p.next != null && p.next.val < head.val) p = p.next;
            head.next = p.next;
            p.next = head;
            head = nx;
        }
        return dummy.next;
    }

    // ---------- Convert Binary Number in a Linked List to Integer (LC 1290) ----------
    static int getDecimalValue(LinkedLists.ListNode head) {
        int v = 0;
        for (LinkedLists.ListNode n = head; n != null; n = n.next) v = (v << 1) | n.val;
        return v;
    }

    // ---------- Remove Linked List Elements (LC 203) ----------
    static LinkedLists.ListNode removeElements(LinkedLists.ListNode head, int val) {
        LinkedLists.ListNode dummy = new LinkedLists.ListNode(0), tail = dummy;
        dummy.next = head;
        while (tail.next != null) { if (tail.next.val == val) tail.next = tail.next.next; else tail = tail.next; }
        return dummy.next;
    }

    // ---------- Insert into a Sorted Circular Linked List (LC 708) ----------
    static LinkedLists.ListNode insertCircular(LinkedLists.ListNode head, int x) {
        LinkedLists.ListNode node = new LinkedLists.ListNode(x);
        if (head == null) { node.next = node; return node; }
        LinkedLists.ListNode cur = head;
        while (true) {
            boolean between = cur.val <= x && x <= cur.next.val;
            boolean atWrap = cur.val > cur.next.val && (x >= cur.val || x <= cur.next.val); // cur is the max, cur.next the min
            if (between || atWrap || cur.next == head) { // cur.next == head: all equal values, any slot works
                node.next = cur.next;
                cur.next = node;
                return head;
            }
            cur = cur.next;
        }
    }

    // ---------- Cycle length and cycle entry together: Floyd phase 1 finds a meeting point, then count a lap ----------
    static int cycleLength(LinkedLists.ListNode head) {
        LinkedLists.ListNode slow = head, fast = head;
        while (fast != null && fast.next != null) {
            slow = slow.next;
            fast = fast.next.next;
            if (slow == fast) {
                int len = 1;
                for (LinkedLists.ListNode n = slow.next; n != slow; n = n.next) len++;
                return len;
            }
        }
        return 0;
    }

    // ---------- Flatten a Multilevel Doubly Linked List (LC 430): iterative with a stack of "next" pointers ----------
    static class DNode {
        int val;
        DNode prev, next, child;
        DNode(int v) { val = v; }
    }

    static DNode flatten(DNode head) {
        Deque<DNode> st = new ArrayDeque<>();
        DNode cur = head, last = null;
        while (cur != null) {
            if (cur.child != null) {
                if (cur.next != null) st.push(cur.next);
                cur.next = cur.child;
                cur.child.prev = cur;
                cur.child = null;
            }
            last = cur;
            if (cur.next == null && !st.isEmpty()) {
                cur.next = st.pop();
                cur.next.prev = cur;
            }
            cur = cur.next;
        }
        return head;
    }
}
