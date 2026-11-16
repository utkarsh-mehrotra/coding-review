import java.util.*;

public class LinkedLists {

    static class ListNode {
        int val;
        ListNode next;
        ListNode(int v) { val = v; }
    }

    // Test helpers
    static ListNode of(int... a) {
        ListNode dummy = new ListNode(0), t = dummy;
        for (int x : a) { t.next = new ListNode(x); t = t.next; }
        return dummy.next;
    }

    static String str(ListNode n) {
        StringBuilder sb = new StringBuilder("[");
        for (; n != null; n = n.next) sb.append(n.val).append(n.next != null ? "," : "");
        return sb.append("]").toString();
    }

    // DUMMY HEAD: use it whenever the head itself might change (delete, merge, partition, reverse range).

    // Reverse (LC 206): iterative
    static ListNode reverse(ListNode head) {
        ListNode prev = null;
        while (head != null) { ListNode nx = head.next; head.next = prev; prev = head; head = nx; }
        return prev;
    }

    // Reverse Between (LC 92)
    static ListNode reverseBetween(ListNode head, int left, int right) {
        ListNode dummy = new ListNode(0);
        dummy.next = head;
        ListNode pre = dummy;
        for (int i = 1; i < left; i++) pre = pre.next;
        ListNode cur = pre.next;
        for (int i = 0; i < right - left; i++) { // pull the next node to the front of the segment
            ListNode nx = cur.next;
            cur.next = nx.next;
            nx.next = pre.next;
            pre.next = nx;
        }
        return dummy.next;
    }

    // Reverse Nodes in k-Group (LC 25)
    static ListNode reverseKGroup(ListNode head, int k) {
        ListNode probe = head;
        for (int i = 0; i < k; i++) { if (probe == null) return head; probe = probe.next; }
        ListNode prev = reverseKGroup(probe, k), cur = head;
        for (int i = 0; i < k; i++) { ListNode nx = cur.next; cur.next = prev; prev = cur; cur = nx; }
        return prev;
    }

    // Swap Nodes in Pairs (LC 24)
    static ListNode swapPairs(ListNode head) {
        ListNode dummy = new ListNode(0), prev = dummy;
        dummy.next = head;
        while (prev.next != null && prev.next.next != null) {
            ListNode a = prev.next, b = a.next;
            a.next = b.next;
            b.next = a;
            prev.next = b;
            prev = a;
        }
        return dummy.next;
    }

    // Middle (LC 876): slow / fast; for even length returns the second middle
    static ListNode middle(ListNode head) {
        ListNode s = head, f = head;
        while (f != null && f.next != null) { s = s.next; f = f.next.next; }
        return s;
    }

    // Cycle detection + entry (LC 141 / 142): Floyd
    static ListNode detectCycle(ListNode head) {
        ListNode s = head, f = head;
        while (f != null && f.next != null) {
            s = s.next;
            f = f.next.next;
            if (s == f) {
                s = head;
                while (s != f) { s = s.next; f = f.next; }
                return s;
            }
        }
        return null;
    }

    // Remove Nth From End (LC 19): gap of n between two pointers
    static ListNode removeNthFromEnd(ListNode head, int n) {
        ListNode dummy = new ListNode(0), fast = dummy, slow = dummy;
        dummy.next = head;
        for (int i = 0; i <= n; i++) fast = fast.next;
        while (fast != null) { fast = fast.next; slow = slow.next; }
        slow.next = slow.next.next;
        return dummy.next;
    }

    // Merge Two Sorted Lists (LC 21)
    static ListNode mergeTwo(ListNode a, ListNode b) {
        ListNode dummy = new ListNode(0), t = dummy;
        while (a != null && b != null) {
            if (a.val <= b.val) { t.next = a; a = a.next; } else { t.next = b; b = b.next; }
            t = t.next;
        }
        t.next = a != null ? a : b;
        return dummy.next;
    }

    // Merge K Lists (LC 23): divide and conquer, O(N log k), no heap needed
    static ListNode mergeKLists(ListNode[] lists) {
        if (lists.length == 0) return null;
        for (int step = 1; step < lists.length; step *= 2)
            for (int i = 0; i + step < lists.length; i += 2 * step) lists[i] = mergeTwo(lists[i], lists[i + step]);
        return lists[0];
    }

    // Sort List (LC 148): merge sort, split with slow/fast
    static ListNode sortList(ListNode head) {
        if (head == null || head.next == null) return head;
        ListNode slow = head, fast = head.next;
        while (fast != null && fast.next != null) { slow = slow.next; fast = fast.next.next; }
        ListNode right = slow.next;
        slow.next = null;
        return mergeTwo(sortList(head), sortList(right));
    }

    // Reorder List (LC 143): middle -> reverse second half -> interleave
    static void reorderList(ListNode head) {
        if (head == null || head.next == null) return;
        ListNode mid = middle(head), second = reverse(mid.next);
        mid.next = null;
        ListNode first = head;
        while (second != null) {
            ListNode a = first.next, b = second.next;
            first.next = second;
            second.next = a;
            first = a;
            second = b;
        }
    }

    // Palindrome List (LC 234)
    static boolean isPalindrome(ListNode head) {
        ListNode mid = middle(head), second = reverse(mid), first = head;
        while (second != null) {
            if (first.val != second.val) return false;
            first = first.next;
            second = second.next;
        }
        return true;
    }

    // Add Two Numbers (LC 2): digits in reverse order
    static ListNode addTwoNumbers(ListNode a, ListNode b) {
        ListNode dummy = new ListNode(0), t = dummy;
        int carry = 0;
        while (a != null || b != null || carry > 0) {
            int s = carry + (a != null ? a.val : 0) + (b != null ? b.val : 0);
            t.next = new ListNode(s % 10);
            carry = s / 10;
            t = t.next;
            if (a != null) a = a.next;
            if (b != null) b = b.next;
        }
        return dummy.next;
    }

    // Intersection of Two Lists (LC 160): switch heads at the end, meet at the intersection (or both null)
    static ListNode getIntersectionNode(ListNode a, ListNode b) {
        ListNode p = a, q = b;
        while (p != q) {
            p = p == null ? b : p.next;
            q = q == null ? a : q.next;
        }
        return p;
    }

    // Remove Duplicates from Sorted List II (LC 82): drop every value that repeats
    static ListNode deleteDuplicates(ListNode head) {
        ListNode dummy = new ListNode(0), prev = dummy;
        dummy.next = head;
        while (head != null) {
            if (head.next != null && head.val == head.next.val) {
                while (head.next != null && head.val == head.next.val) head = head.next;
                prev.next = head.next;
            } else prev = prev.next;
            head = head.next;
        }
        return dummy.next;
    }

    // Partition List (LC 86): two dummy lists
    static ListNode partition(ListNode head, int x) {
        ListNode lo = new ListNode(0), hi = new ListNode(0), a = lo, b = hi;
        for (; head != null; head = head.next) {
            if (head.val < x) { a.next = head; a = a.next; } else { b.next = head; b = b.next; }
        }
        b.next = null;
        a.next = hi.next;
        return lo.next;
    }

    // Rotate List (LC 61)
    static ListNode rotateRight(ListNode head, int k) {
        if (head == null || head.next == null || k == 0) return head;
        int n = 1;
        ListNode tail = head;
        while (tail.next != null) { tail = tail.next; n++; }
        k %= n;
        if (k == 0) return head;
        ListNode newTail = head;
        for (int i = 0; i < n - k - 1; i++) newTail = newTail.next;
        ListNode newHead = newTail.next;
        newTail.next = null;
        tail.next = head;
        return newHead;
    }

    // Odd Even Linked List (LC 328)
    static ListNode oddEvenList(ListNode head) {
        if (head == null) return null;
        ListNode odd = head, even = head.next, evenHead = even;
        while (even != null && even.next != null) {
            odd.next = even.next;
            odd = odd.next;
            even.next = odd.next;
            even = even.next;
        }
        odd.next = evenHead;
        return head;
    }

    // Copy List with Random Pointer (LC 138): interleave copies, set randoms, split. O(1) extra space.
    static class RNode {
        int val;
        RNode next, random;
        RNode(int v) { val = v; }
    }

    static RNode copyRandomList(RNode head) {
        if (head == null) return null;
        for (RNode c = head; c != null; c = c.next.next) {
            RNode cp = new RNode(c.val);
            cp.next = c.next;
            c.next = cp;
        }
        for (RNode c = head; c != null; c = c.next.next) if (c.random != null) c.next.random = c.random.next;
        RNode newHead = head.next;
        for (RNode c = head; c != null; c = c.next) {
            RNode cp = c.next;
            c.next = cp.next;
            if (cp.next != null) cp.next = cp.next.next;
        }
        return newHead;
    }
}
