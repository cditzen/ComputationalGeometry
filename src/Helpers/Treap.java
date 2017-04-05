package Helpers;

import java.util.Random;

/**
 * Implementation of the Treap Data Structure
 * Credit: http://users.cs.fiu.edu/~weiss/dsaajava/code/DataStructures/Treap.java
 * @param <T>
 */
public class Treap <T extends Comparable<? super T>> {

    protected TreapNode root;
    protected TreapNode nullNode;

    public Treap() {
        nullNode = new TreapNode(null);
        nullNode.left = nullNode;
        nullNode.right = nullNode;
        nullNode.priority = Integer.MAX_VALUE;
        root = nullNode;
    }

    /**
     * Inserts a data element into the Tree. Rotate if necessary
     * @param dataToInsert
     */
    public void insert(T dataToInsert) {
        TreapNode node = new TreapNode(dataToInsert, nullNode, nullNode);
        root = insert(node, root);
    }

    /**
     * Removes a data element from the tree, if it exists
     * @param dataToRemove
     */
    public void remove(T dataToRemove) {
        root = remove(dataToRemove, root);
    }

    /**
     * Returns data if it's in the tree
     * Null if not found
     * @param data
     * @return
     */
    public T find(T data) {
        TreapNode<T> current = root;
        nullNode.element = data;

        while (true) {
            if (data.compareTo(current.element) < 0) {
                current = current.left;
            } else if (data.compareTo(current.element) > 0) {
                current = current.right;
            } else if (current != nullNode) {
                return current.element;
            } else {
                return null;
            }
        }
    }

    /**
     * Insert data into the tree
     * @param dataToInsert Node to insert
     * @param node Root node of a (sub) tree
     * @return the new root.
     */
    private TreapNode insert(TreapNode dataToInsert, TreapNode node) {
        if (node == nullNode) {
            node = dataToInsert;
        } else if (dataToInsert.element.compareTo(node.element) < 0) {
            node.left = insert(dataToInsert, node.left);

            if (node.left.priority < node.priority) {
                node = leftRotate(node);
            }

        } else if (dataToInsert.element.compareTo(node.element) > 0) {
            node.right = insert(dataToInsert, node.right);

            if (node.right.priority < node.priority) {
                node = rightRotate(node);
            }
        }
        return node;
    }

    /**
     * Removes a data element from the tree
     * @param dataToRemove
     * @param node
     * @return
     */
    private TreapNode remove(T dataToRemove, TreapNode<T> node) {
        if (node != nullNode) {
            if (dataToRemove.compareTo(node.element) < 0) {
                node.left = remove(dataToRemove, node.left);
            } else if (dataToRemove.compareTo(node.element) > 0) {
                node.right = remove(dataToRemove, node.right);
            } else {
                if (node.left.priority < node.right.priority) {
                    node = leftRotate(node);
                } else {
                    node = rightRotate(node);
                }
                if (node != nullNode) {
                    node = remove(dataToRemove, node);
                } else {
                    node.left = nullNode;  // At a leaf
                }
            }
        }
        return node;
    }

    /**
     * Left rotate around the given node
     * @param rotateAround
     * @return
     */
    private TreapNode leftRotate(TreapNode rotateAround) {
        TreapNode left = rotateAround.left;
        rotateAround.left = left.right;
        left.right = rotateAround;
        return left;
    }

    /**
     * Right rotate around the given node
     * @param rotateAround
     * @return
     */
    private TreapNode rightRotate(TreapNode rotateAround) {
        TreapNode right = rotateAround.right;
        rotateAround.right = right.left;
        right.left = rotateAround;
        return right;
    }
}

class TreapNode<T extends Comparable<? super T>>
{
    protected T element;
    protected TreapNode left;
    protected TreapNode right;
    protected int  priority;

    TreapNode(T element) {
        this(element, null, null);
    }

    TreapNode(T element, TreapNode leftChild, TreapNode rightChild) {
        this.element = element;
        this.left = leftChild;
        this.right = rightChild;
        this.priority = new Random().nextInt();
    }

    TreapNode(T theElement, TreapNode leftChild, TreapNode rightChild, int priority) {
        this.element = theElement;
        this.left = leftChild;
        this.right = rightChild;
        this.priority = priority;
    }
}