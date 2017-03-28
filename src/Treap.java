import java.util.Random;

/**
 * Created by cditz_000 on 3/24/2017.
 */
public class Treap<T extends Comparable> {

    Node root;

    public Treap() {
        this.root = null;
    }

    public Node search(T key) {
        return search(root, key);
    }

    public Node search(Node node, T key) {
        if (root == null || root.getData() == key) {
            return root;
        }

        if (root.getData().compareTo(key) < 0) {
            return search(root.getRight(), key);
        } else {
            return search(root.getLeft(), key);
        }
    }

    public Node insert(T key) {
        return insert(this.root, key);
    }

    public Node insert(Node root, T key) {
        if (root == null) {
            Node temp = new Node(key);
        }

        if (key.compareTo(root.getData()) < 0) {
            root.setLeft(insert(root.getLeft(), key));

            if (root.getLeft().getPriority() > root.getPriority()) {
                // right rotate root
            }
        } else {
            root.setRight(insert(root.getRight(), key));

            if (root.getRight().getPriority() > root.getPriority()) {
                // left rotate root
            }
        }
        return root;
    }


    public static void main(String[] args) {
        System.out.println("Treap.java");
        Treap<Integer> treap = new Treap<>();
        treap.insert(10);
        treap.insert(4);
        treap.insert(11);
        treap.insert(5);
        treap.insert(15);
        treap.insert(20);
        treap.insert(1);
    }

}

class Node<T extends Comparable> {

    private T data;
    private Node left;
    private Node right;
    private final int priority;

    public Node(T data) {
        this.data = data;
        this.left = null;
        this.right = null;
        Random r = new Random();
        priority = r.nextInt();
    }

    public T getData() {
        return this.data;
    }

    public Node getLeft() {
        return left;
    }

    public Node getRight() {
        return right;
    }

    public void setLeft(Node left) {
        this.left = left;
    }

    public void setRight(Node right) {
        this.right = right;
    }

    public int getPriority() {
        return priority;
    }
}

