package Helpers;

import DoublyConnectedEdgeList.Cartesian;
import DoublyConnectedEdgeList.HalfEdge;
import DoublyConnectedEdgeList.Vertex;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/**
 * Created by Cory Itzen on 3/30/2017.
 */
public class CartesianComparator {

    /**
     * Return positive if o1 has a larger y value
     * Return negative if o1 has a smaller y value
     * Ties are broken by X coordinate
     * @return
     */
    public static Comparator<Vertex> yAxisComparator() {
        return (o1, o2) -> {
            if (o1.getY() == o2.getY()) {
                return o2.getX() - o1.getX();
            } else {
                return o1.getY() - o2.getY();
            }
        };
    }

    /**
     * Return positive if o1 has a larger x value
     * Return negative if o1 has a smaller x value
     * Ties are broken by Y coordinate
     * @return
     */
    public static Comparator<Vertex> xAxisComparator() {
        return (o1, o2) -> {
            if (o1.getX() == o2.getX()) {
                return o2.getY() - o1.getY();
            } else {
                return o1.getX() - o2.getX();
            }
        };
    }
}
