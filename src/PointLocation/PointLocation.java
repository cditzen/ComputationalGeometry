package PointLocation;

import DoublyConnectedEdgeList.DCEL;
import DoublyConnectedEdgeList.Face;
import DoublyConnectedEdgeList.HalfEdge;
import DoublyConnectedEdgeList.Vertex;
import Helpers.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;

/**
 * Created by Cory Itzen on 3/30/2017.
 */
public class PointLocation {

    private final int NEGATIVE_INFINITY = Integer.MIN_VALUE / 3;
    private final int POSITIVE_INFINITY = Integer.MAX_VALUE / 3;

    private ArrayList<Vertex> boundingVertices;
    private HashMap<Vertex, HalfEdgeTreap> sets;

    public PointLocation(DCEL dcel) {
        this.boundingVertices = new ArrayList<>();
        this.sets = new HashMap<>();
        constructPointLocation(dcel);
    }

    public Face Query(int x, int y) {

        // binary search on boundingVertices
        Vertex queryPoint = new Vertex(x, y, "queryPoint");
        HalfEdge halfEdge = sets.get(findBoundingVertex(queryPoint)).findTargetOrSmaller(queryPoint);

        return halfEdge.getIncidentFace();
    }

    private void constructPointLocation(DCEL dcel) {

        ArrayList<Vertex> vertices = dcel.getVertices();

        vertices.addAll(infinityBound(dcel.getOuterFace()).getVertices());

        Collections.sort(vertices, CartesianComparator.yAxisComparator().reversed());

        // Initialize the first treap
        HalfEdgeTreap treap = new HalfEdgeTreap();

        // Insert edges into the treap
        Iterator<HalfEdge> iterator = vertices.get(0).getIncidentEdgeIterator();
        while (iterator.hasNext()) {
            treap.insert(iterator.next());
        }

        // Store treap in tree and save the Y position of the vertex
        boundingVertices.add(vertices.get(0));
        sets.put(vertices.get(0), treap);
        int lastYPosition = vertices.get(0).getY();

        for (int i = 1; i < vertices.size() - 1; i++) {

            Vertex currentVertex = vertices.get(i);

            if (currentVertex.getY() != lastYPosition) {
                // Add new treap

                treap = (HalfEdgeTreap) treap.makeCopy();

                handleVertexEvent(currentVertex, treap);

                // Store treap in array and update last Y position
                lastYPosition = currentVertex.getY();

                boundingVertices.add(currentVertex);
                sets.put(currentVertex, treap);
            } else {
                // Modify last treap
                handleVertexEvent(currentVertex, treap);
            }
        }
    }

    private void handleVertexEvent(Vertex vertex, Treap<HalfEdge> treap) {

        Iterator<HalfEdge> iterator = vertex.getIncidentEdgeIterator();

        while (iterator.hasNext()) {
            HalfEdge currentEdge = iterator.next();

            if (treap.find(currentEdge.getTwin()) == null) {
                // Does not have edge, add a new one
                treap.insert(currentEdge);
            } else {
                // Does have edge, delete it
                treap.remove(currentEdge.getTwin());
            }
        }
    }

    private DCEL infinityBound(Face outerFace) {
        DCEL infinity = new DCEL(outerFace);
        infinity.addVertex(new Vertex(NEGATIVE_INFINITY, POSITIVE_INFINITY, "Positive Infinity %%%"));
        infinity.addVertex(new Vertex(NEGATIVE_INFINITY, NEGATIVE_INFINITY, "Negative Infinity %%%"));
        infinity.addEdge("Positive Infinity %%%", "Negative Infinity %%%");
        return infinity;
    }

    private Vertex findBoundingVertex(Vertex target) {

        int yTarget = target.getY();

        int left = 0;
        int right = boundingVertices.size() - 1;

        while (left <= right) {
            int mid = left + (right - left) / 2;
            if (mid == boundingVertices.size() - 1 || (boundingVertices.get(mid).getY() >= yTarget && boundingVertices.get(mid + 1).getY() < yTarget)) {
                return boundingVertices.get(mid);
            } else if (yTarget > boundingVertices.get(mid).getY()) {
                right = right - 1;
            } else {
                left = mid + 1;
            }
        }

        return null;
    }
}
