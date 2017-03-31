package Helpers;

import java.util.Random;

/**
 * Implements a treap.
 * Note that all "matching" is based on the compareTo method.
 * @author Mark Allen Weiss
 */
public class Treap <T extends Comparable<? super T>> {

    public Treap() {
        root = nullNode;
    }

    public void insert(T x) {
        root = insert(x, root);
    }

    public void remove(T x) {
        root = remove(x, root);
    }

    public <T extends Comparable> Comparable find(T x) {
        TreapNode current = root;
        nullNode.element = x;

        while (true) {
            if (x.compareTo(current.element) < 0) {
                current = current.left;
            } else if (x.compareTo(current.element) > 0) {
                current = current.right;
            } else if (current != nullNode) {
                return current.element;
            } else {
                return null;
            }
        }
    }

    public <T extends Comparable<? super T>> Comparable findTargetOrSmaller(Comparable<T> target) {
        TreapNode current = root;
        Comparable largestElementLessThanX = null;

        while (true) {
            if (current.element != null && current.element.compareTo(target) <= 0 && (largestElementLessThanX == null || current.element.compareTo(largestElementLessThanX) > 0)) {
                largestElementLessThanX = current.element;
            }
            if (current.element == null) {
                return largestElementLessThanX;
            }
            if (current.element.compareTo(target) > 0) {
                current = current.left;
            } else if (current.element.compareTo(target) < 0) {
                current = current.right;
            } else if (current != nullNode)
                return current.element;
            else {
                return largestElementLessThanX;
            }
        }
    }

    public void makeEmpty() {
        root = nullNode;
    }

    public boolean isEmpty() {
        return root == nullNode;
    }

    /**
     * Internal method to insert into a subtree.
     * @param x the item to insert.
     * @param t the node that roots the tree.
     * @return the new root.
     */
    private <T extends Comparable<? super T>> TreapNode insert(Comparable x, TreapNode t) {
        if (t == nullNode) {
            t = new TreapNode(x, nullNode, nullNode);
        } else if (x.compareTo(t.element) < 0) {
            t.left = insert(x, t.left);
            if (t.left.priority < t.priority)
                t = rotateWithLeftChild(t);
        } else if (x.compareTo(t.element) > 0 ) {
            t.right = insert(x, t.right);
            if (t.right.priority < t.priority)
                t = rotateWithRightChild(t);
        }
        return t;
    }

    private <T extends Comparable<? super T>> TreapNode remove(Comparable x, TreapNode t) {
        if (t != nullNode) {
            if (x.compareTo(t.element) < 0) {
                t.left = remove(x, t.left);
            } else if (x.compareTo(t.element) > 0) {
                t.right = remove(x, t.right);
            } else {
                if (t.left.priority < t.right.priority) {
                    t = rotateWithLeftChild(t);
                } else {
                    t = rotateWithRightChild(t);
                }
                if (t != nullNode) {
                    t = remove(x, t);
                } else {
                    t.left = nullNode;  // At a leaf
                }
            }
        }
        return t;
    }

    static TreapNode rotateWithLeftChild(TreapNode k2) {
        TreapNode k1 = k2.left;
        k2.left = k1.right;
        k1.right = k2;
        return k1;
    }

    static TreapNode rotateWithRightChild(TreapNode k1) {
        TreapNode k2 = k1.right;
        k1.right = k2.left;
        k2.left = k1;
        return k2;
    }

    private TreapNode root;
    private static TreapNode nullNode;
    static {
        nullNode = new TreapNode(null);
        nullNode.left = nullNode.right = nullNode;
        nullNode.priority = Integer.MAX_VALUE;
    }

    // Test program
    public static void main(String [ ] args) {
        Treap t = new Treap();
        t.insert(10);
        t.insert(12);
        t.insert(11);
        t.insert(5);
        t.insert(4);
        t.insert(3);
        t.remove(5);
    }
}

class TreapNode
{
    // Constructors
    TreapNode(Comparable theElement) {
        this(theElement, null, null);
    }

    TreapNode(Comparable theElement, TreapNode lt, TreapNode rt) {
        element  = theElement;
        left     = lt;
        right    = rt;
        priority = new Random().nextInt();
    }

    // Friendly data; accessible by other package routines
    Comparable element;      // The data in the node
    TreapNode  left;         // Left child
    TreapNode  right;        // Right child
    int        priority;     // Priority

}