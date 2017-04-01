package Helpers;

import DoublyConnectedEdgeList.HalfEdge;

import java.util.Random;

/**
 * Implements a treap.
 * Note that all "matching" is based on the compareTo method.
 */
public class Treap <T extends Comparable<? super T>> {

    private TreapNode root;
    private TreapNode nullNode;

    public Treap() {
        nullNode = new TreapNode(null);
        nullNode.left = nullNode.right = nullNode;
        nullNode.priority = Integer.MAX_VALUE;
        root = nullNode;
    }

    public <T extends Comparable> void insert(T x) {
        TreapNode node = new TreapNode(x, nullNode, nullNode);
        root = insert(node, root);
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

    public Treap<T> makeCopy() {
        Treap<T> copy = new Treap<T>();
        TreapNode copyRoot = new TreapNode(this.root.element, copy.nullNode, copy.nullNode, this.root.priority);
        copyChildren(this.root, copyRoot, copy.nullNode);
        copy.root = copyRoot;
        return copy;
    }

    private void copyChildren(TreapNode actualNode, TreapNode copyParent, TreapNode copyNullNode) {
        if (actualNode.left != nullNode) {
            TreapNode copyLeft = new TreapNode(actualNode.left.element, copyNullNode, copyNullNode, actualNode.priority);
            copyParent.left = copyLeft;
            copyChildren(actualNode.left, copyLeft, copyNullNode);
        }
        if (actualNode.right != nullNode) {
            TreapNode copyRight = new TreapNode(actualNode.right.element, copyNullNode, copyNullNode, actualNode.priority);
            copyParent.right = copyRight;
            copyChildren(actualNode.right, copyRight, copyNullNode);
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
    private <T extends Comparable<? super T>> TreapNode insert(TreapNode x, TreapNode t) {
        if (t == nullNode) {
            t = x;
        } else if (x.element.compareTo(t.element) < 0) {
            t.left = insert(x, t.left);

            if (t.left.priority < t.priority) {
                t = rotateWithLeftChild(t);
            }

        } else if (x.element.compareTo(t.element) > 0) {
            t.right = insert(x, t.right);

            if (t.right.priority < t.priority) {
                t = rotateWithRightChild(t);
            }

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

    public void printTree() {
        printTree(this.root);
    }

    /**
     * Internal method to print a subtree in sorted order.
     * @param t the node that roots the tree.
     */
    private void printTree(TreapNode t)
    {
        if( t != t.left )
        {
            printTree( t.left );
            System.out.println(  ( (HalfEdge) t.element).getName());
            printTree( t.right );
        }
    }

    public static void main(String [ ] args) {

        System.out.println("Making first treap:");
        Treap t = new Treap();
        t.insert(10);
        t.insert(12);
        t.insert(11);
        t.insert(5);
        t.insert(4);
        t.insert(3);
        t.printTree(t.root);

        System.out.println("\n\nMaking copy:");
        Treap copy = t.makeCopy();
        copy.printTree();

        System.out.println("\n\nRemoving 12 from original:");
        t.remove(12);
        t.printTree();

        System.out.println("\n\nCopy should not be changed:");
        copy.printTree();

        System.out.println("\n\nRemoving 30 from copy (30 does not exist):");
        copy.remove(30);
        copy.printTree();

        System.out.println("\n\nCopy.find(4) (Does exist):");
        System.out.println();

        System.out.println("\n\nCopy.find(30) (Does not exist):");
        System.out.println();

        System.out.println("Adding 40 to copy");
        copy.insert(40);
        copy.printTree();
    }
}

class TreapNode
{
    Comparable element;
    TreapNode left;
    TreapNode right;
    int  priority;

    TreapNode(Comparable theElement) {
        this(theElement, null, null);
    }

    TreapNode(Comparable theElement, TreapNode lt, TreapNode rt) {
        this.element = theElement;
        this.left = lt;
        this.right = rt;
        this.priority = new Random().nextInt();
    }

    TreapNode(Comparable theElement, TreapNode lt, TreapNode rt, int priority) {
        this.element = theElement;
        this.left = lt;
        this.right = rt;
        this.priority = priority;
    }

}