package DoublyConnectedEdgeList;

import java.util.ArrayList;

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
}
