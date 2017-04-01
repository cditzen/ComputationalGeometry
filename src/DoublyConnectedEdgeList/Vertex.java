package DoublyConnectedEdgeList;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * Created by Cory Itzen on 3/14/2017.
 */
public class Vertex implements Comparable<Vertex>, Cartesian {

    // X and Y position of this vertex
    private final int x;
    private final int y;

    // Incident HalfEdge
    // TODO Initially set to null. Question: Can this change? Does it need to?
    // Set to the first edge that has this as its origin
    private HalfEdge incidentEdge;

    private String name;

    public Vertex(int x, int y, String name) {
        this.x = x;
        this.y = y;
        incidentEdge = null;
        this.name = name;
    }

    /**
     * Sets the incident edge to the most recently added edge
     */
    public void setIncidentEdge(HalfEdge e) {
        incidentEdge = e;
    }

    public HalfEdge getIncidentEdge() {
        return incidentEdge;
    }

    public String getName() {
        return name;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getEndPointX() {
        return x;
    }

    public int getEndPointY() {
        return y;
    }

    public int getAngleToVertex(Vertex target) {
        return (int) Math.toDegrees(Math.atan2(target.getY() - this.getY(), target.getX() - this.getX()));
    }

    public IncidentEdgeIterator getIncidentEdgeIterator() {
        return new IncidentEdgeIterator(this);
    }

    /**
     * Return positive if this has a larger y value
     * Return negative if this has a smaller y value
     * Ties are broken by X coordinate
     * @param o
     * @return
     */
    @Override
    public int compareTo(Vertex o) {
        if (this.getY() == o.getY()) {
            return o.getX() - this.getX();
        } else {
            return this.getY() - o.getY();
        }
    }
}

/**
 * Iterates around the outgoing edges of a given vertex one time
 */
class IncidentEdgeIterator implements Iterator<HalfEdge> {

    private final HalfEdge startingEdge;
    private HalfEdge currentEdge;
    private boolean hasVisitedStartingVertex;

    public IncidentEdgeIterator(Vertex vertex) {
        if (vertex.getIncidentEdge() != null) {
            this.startingEdge = vertex.getIncidentEdge();
            this.currentEdge = vertex.getIncidentEdge();
            hasVisitedStartingVertex = false;
        } else {
            // TODO idk
            startingEdge = null;
            currentEdge = null;
            hasVisitedStartingVertex = false;
        }
    }

    @Override
    public boolean hasNext() {
        if ((currentEdge == startingEdge && hasVisitedStartingVertex) || startingEdge == null) {
            return false;
        } else {
            return true;
        }
    }

    @Override
    public HalfEdge next() {
        if (this.hasNext()) {
            HalfEdge toReturn = currentEdge;
            currentEdge = currentEdge.getTwin().getNext();
            hasVisitedStartingVertex = true;
            return toReturn;
        } else {
            throw new NoSuchElementException();
        }
    }
}