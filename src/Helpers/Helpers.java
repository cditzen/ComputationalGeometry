package Helpers;

import DoublyConnectedEdgeList.Vertex;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Created by Cory Itzen on 3/28/2017.
 */
public class Helpers {

    /**
     * Returns true if the target is in the range x and y, non-inclusive
     * @param x
     * @param y
     * @param target
     * @return
     */
    public static boolean targetInRange(int x, int y, int target) {
        int max = Math.max(x, y);
        int min = Math.min(x, y);
        return target < max && target > min;
    }

    /**
     * Returns true if a list of vertices are listed in anti clockwise order around a polygon
     * Credit: http://stackoverflow.com/questions/1165647/how-to-determine-if-a-list-of-polygon-points-are-in-clockwise-order
     * @param
     * @return
     */
    public static boolean isAntiClockwise(ArrayList<Vertex> vertexPath) {
        int sum = 0;
        for (int i = 0; i < vertexPath.size() - 1; i++) {
            sum += ((vertexPath.get(i + 1).getX() - vertexPath.get(i).getX()) *
                    (vertexPath.get(i + 1).getY() + vertexPath.get(i).getY()));
        }
        int leftSide = (vertexPath.get(0).getX() - vertexPath.get(vertexPath.size() - 1).getX());
        int rightSide = (vertexPath.get(0)).getY() + vertexPath.get(vertexPath.size() - 1).getY();

        sum += leftSide * rightSide;
        return sum < 0;
    }

//    /**
//     * Sorts a list of edges based on Edge Weight
//     * All edges must implement IWeight
//     * @param vertices list of edges to sort
//     * @param
//     */
//    public static void mergeSort(List<Vertex> vertices) {
//        mergeSort(vertices, 0, vertices.size() - 1);
//    }

    /**
     * Divides and conquer a list of edges
     * @param vertices list of edges to sort
     * @param left left index
     * @param right right index
     * @param
     */
    private static void mergeSort(List<Vertex> vertices, int left, int right) {
        if (left < right) {
            int mid = left + (right - left) / 2;
            mergeSort(vertices, left, mid);
            mergeSort(vertices, mid + 1, right);
            merge(vertices, left, mid, right);
        }
    }

    /**
     * Merges two array subsets into one ordered subset
     * @param vertices List of edges to sort
     * @param left left index of the first subset
     * @param mid dividing index between two subsets
     * @param right right index of the second subset
     * @param
     */
    private static void merge(List<Vertex> vertices, int left, int mid, int right) {
        /** Make a copy of the list of edges */
        Vertex[] copy = new Vertex[vertices.size()];
        for (int i = left; i <= right; i++) {
            copy[i] = vertices.get(i);
        }

        /** Indices to iterate over */
        int leftIndex = left;
        int rightIndex = mid + 1;
        int index = left;

        /** Merge two array subsets based in increasing weight */
        while (leftIndex <= mid && rightIndex <= right) {
//            if (copy[leftIndex].compareTo(copy[rightIndex]) > 0) {
//                vertices.set(index, copy[leftIndex]);
//                leftIndex++;
//            } else {
//                vertices.set(index, copy[rightIndex]);
//                rightIndex++;
//            }
            index++;
        }

        /** Fill in remaining edges */
        while (leftIndex <= mid) {
            vertices.set(index, copy[leftIndex]);
            index++;
            leftIndex++;
        }
    }

    public static boolean vertexAboveNeighbors(Vertex target, Vertex n1, Vertex n2) {
        Comparator<Vertex> comparator = CartesianComparator.yAxisComparator();
        return comparator.compare(target, n1) > 0 && comparator.compare(target, n2) > 0;
    }

    public static boolean vertexBelowNeighbors(Vertex target, Vertex n1, Vertex n2) {
        Comparator<Vertex> comparator = CartesianComparator.yAxisComparator();
        return comparator.compare(target, n1) < 0 && comparator.compare(target, n2) < 0;
    }

}
