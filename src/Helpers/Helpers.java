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

    public static boolean vertexAboveNeighbors(Vertex target, Vertex n1, Vertex n2) {
        Comparator<Vertex> comparator = CartesianComparator.yAxisComparator();
        return comparator.compare(target, n1) > 0 && comparator.compare(target, n2) > 0;
    }

    public static boolean vertexBelowNeighbors(Vertex target, Vertex n1, Vertex n2) {
        Comparator<Vertex> comparator = CartesianComparator.yAxisComparator();
        return comparator.compare(target, n1) < 0 && comparator.compare(target, n2) < 0;
    }

}
