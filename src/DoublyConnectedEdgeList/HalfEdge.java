package DoublyConnectedEdgeList;

import java.nio.file.Path;
import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * Created by Cory Itzen on 3/14/2017.
 */
public class HalfEdge {

    // For debugging
    String name;

    // Vertex from which this edge comes from
    private Vertex origin;

    // Twin halfEdge.
    // IDEA: Create two edges, and then match them after construction
    private HalfEdge twin;

    // Face this halfEdge bounds
    private Face incidentFace;

    // Next edge on the boundary
    private HalfEdge next;

    // Previous edge on the boundary
    private HalfEdge prev;

    public HalfEdge(Vertex origin, String name) {
        this.origin = origin;
        this.twin = null;           // TODO Should be set later
        this.incidentFace = null;   // TODO Should be set somehow...
        this.next = null;           // TODO
        this.prev = null;           // TODO
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

