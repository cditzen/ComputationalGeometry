package DoublyConnectedEdgeList;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * Created by Cory Itzen on 3/14/2017.
 */
public class Face {
    HalfEdge outerComponent;
    ArrayList<HalfEdge> innerComponents;
    String name;

    public Face() {
        outerComponent = null;
        innerComponents = new ArrayList<>();
        this.name = "";
    }

    public Face(String name) {
        outerComponent = null;
        innerComponents = new ArrayList<>();
        this.name = name;
    }

    public HalfEdge getOuterComponent() {
        return outerComponent;
    }

    public void setOuterComponent(HalfEdge outerComponent) {
        this.outerComponent = outerComponent;
    }

    public void addInnerComponent(HalfEdge innerComponent) {
        innerComponents.add(innerComponent);
    }

    public String getName() {
        return name;
    }

    public FaceIterator getFaceIterator() {
        return new FaceIterator(this);
    }
}

class FaceIterator implements Iterator<Vertex> {

    private HalfEdge startingEdge;
    private HalfEdge current;
    private boolean hasVisitedStart;

    public FaceIterator(Face face) {
        startingEdge = face.getOuterComponent();
        current = startingEdge;
        hasVisitedStart = false;
    }

    @Override
    public boolean hasNext() {
        if ((current == startingEdge && hasVisitedStart == true) || current == null) {
            return false;
        } else {
            return true;
        }
    }

    @Override
    public Vertex next() {
        if (this.hasNext()) {
            hasVisitedStart = true;
            Vertex toReturn = current.getOrigin();
            current = current.getNext();
            return toReturn;
        } else {
            throw new NoSuchElementException();
        }
    }
}
