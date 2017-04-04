package DoublyConnectedEdgeList;

import Helpers.Helpers;

import java.nio.file.Path;
import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * Created by Cory Itzen on 3/14/2017.
 */
public class HalfEdge implements Comparable<Cartesian>, Cartesian {

    // Name of this Half Edge
    String name;

    // Vertex from which this edge comes from
    private Vertex origin;

    // Twin halfEdge.
    private HalfEdge twin;

    // Face this halfEdge bounds
    private Face incidentFace;

    // Next edge on the boundary
    private HalfEdge next;

    // Previous edge on the boundary
    private HalfEdge prev;

    public HalfEdge(Vertex origin, String name) {
        this.origin = origin;
        this.twin = null;
        this.incidentFace = null;
        this.next = null;
        this.prev = null;
        this.name = name;
    }

    public Vertex getOrigin() {
        return origin;
    }

    public HalfEdge getTwin() {
        return twin;
    }

    public void setTwin(HalfEdge other) {
        this.twin = other;
    }

    public Face getIncidentFace() {
        return incidentFace;
    }

    public void setIncidentFace(Face incidentFace) {
        this.incidentFace = incidentFace;
    }

    public HalfEdge getNext() {
        return next;
    }

    public void setNext(HalfEdge next) {
        this.next = next;
    }

    public HalfEdge getPrev() {
        return prev;
    }

    public void setPrev(HalfEdge prev) {
        this.prev = prev;
    }

    public String getName() {
        return name;
    }

    public PathIterator getPathIterator(Vertex targetVertex) {
        return new PathIterator(this, targetVertex);
    }

    public int getY() {
        return this.origin.getY();
    }

    public int getEndPointY() {
        return getTwin().getOrigin().getY();
    }

    public int getX() {
        return this.origin.getX();
    }

    public int getEndPointX() {
        return getTwin().getOrigin().getX();
    }

    /**
     * Return positive if this edge lies to the right of the other vertex
     * Returns positive if this edge's midpoint lies to the right of the other midpoint
     * @param o
     * @return
     */
    @Override
    public int compareTo(Cartesian o) {
        Vertex endVertex = this.getTwin().getOrigin();
        if (endVertex.getX() - this.getX() == 0 || o instanceof HalfEdge) {
            int xDiff = (this.getX() + this.getEndPointX()) - (o.getX() + o.getEndPointX());
            if (xDiff == 0) {
                return (this.getY() + this.getEndPointY()) - (o.getY() + o.getEndPointY());
            } else {
                return xDiff;
            }
        }
        double slope = ((double) (endVertex.getY() - this.getY()) / (double) (endVertex.getX() - this.getX()));
        double b = this.getY() - (this.getX() * slope);
        int intersection = (int) ((o.getY() - b) / slope);
        return intersection - o.getX();
    }
}

/**
 * Iterates through a sequence of edges until the targetVertex is found
 */
class PathIterator implements Iterator<HalfEdge> {

    private HalfEdge currentEdge;
    private final Vertex targetVertex;

    public PathIterator(HalfEdge startingEdge, Vertex targetVertex) {
        this.currentEdge = startingEdge;
        this.targetVertex = targetVertex;
    }

    @Override
    public boolean hasNext() {
        return currentEdge.getOrigin() != targetVertex;
    }

    @Override
    public HalfEdge next() {
        if (this.hasNext()) {
            HalfEdge toReturn = currentEdge;
            currentEdge = currentEdge.getNext();
            return toReturn;
        } else {
            throw new NoSuchElementException();
        }
    }
}

