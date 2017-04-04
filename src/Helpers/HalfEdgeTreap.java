package Helpers;

import DoublyConnectedEdgeList.HalfEdge;
import DoublyConnectedEdgeList.Vertex;

/**
 * Created by Cory Itzen on 4/2/2017.
 */
public class HalfEdgeTreap extends Treap<HalfEdge>{

    public HalfEdge findTargetOrSmaller(Vertex target) {
        TreapNode<HalfEdge> current = super.root;
        HalfEdge largestElementLessThanX = null;

        while (true) {
            if (current != nullNode && current.element.compareTo(target) <= 0 && (largestElementLessThanX == null || current.element.compareTo(largestElementLessThanX) > 0)) {
                largestElementLessThanX = current.element;
            }
            if (current == nullNode) {
                return largestElementLessThanX;
            }
            if (current.element.compareTo(target) > 0) {
                current = current.left;
            } else if (current.element.compareTo(target) < 0) {
                current = current.right;
            } else if (current != nullNode) {
                return current.element;
            } else {
                return largestElementLessThanX;
            }
        }
    }

    /**
     * Makes a deep copy of the tree
     * @return
     */
    public HalfEdgeTreap makeCopy() {
        HalfEdgeTreap copy = new HalfEdgeTreap();
        TreapNode copyRoot = new TreapNode(this.root.element, copy.nullNode, copy.nullNode, this.root.priority);
        copyChildren(this.root, copyRoot, copy.nullNode);
        copy.root = copyRoot;
        return copy;
    }

    /**
     * Recursively copies children of the node
     * @param actualNode
     * @param copyParent
     * @param copyNullNode
     */
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
}