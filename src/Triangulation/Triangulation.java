package Triangulation;

import DoublyConnectedEdgeList.DCEL;
import DoublyConnectedEdgeList.Face;
import DoublyConnectedEdgeList.HalfEdge;
import DoublyConnectedEdgeList.Vertex;
import Helpers.*;

import java.util.*;

/**
 * Created by Cory Itzen on 3/14/2017.
 */
public class Triangulation {

    public static DCEL makeMonotone(DCEL simplePolygon) {
        HalfEdgeTreap treap = new HalfEdgeTreap();

        HashMap<HalfEdge, Vertex> helpers = new HashMap<>();
        Set<Vertex> mergeVertices = new HashSet<>();

        // Sort vertices in decreasing priority
        ArrayList<Vertex> vertices = simplePolygon.getVertices();
        Collections.sort(vertices, CartesianComparator.yAxisComparator().reversed());

        Face outerFace = simplePolygon.getOuterFace();

        for (int i = 0; i < vertices.size(); i++) {

            Vertex currentVertex = vertices.get(i);

            // Get emergent edge that in inside the simplePolygon and its previous edge
            HalfEdge incidentEdge = currentVertex.getIncidentEdge();
            HalfEdge incidentInteriorEdge = incidentEdge.getIncidentFace() != outerFace ? incidentEdge : incidentEdge.getTwin().getNext();
            HalfEdge prevEdge = incidentInteriorEdge.getPrev();

            Vertex incidentVertex = incidentInteriorEdge.getNext().getOrigin();
            Vertex prevVertex = prevEdge.getOrigin();

            int incidentVertexAngle = currentVertex.getAngleToVertex(incidentVertex);
            int prevVertexAngle = currentVertex.getAngleToVertex(prevVertex);

            if (Helpers.vertexAboveNeighbors(currentVertex, incidentVertex, prevVertex)) {
                if (incidentVertexAngle > prevVertexAngle) {
                    // Split Vertex

                    HalfEdge leftOfCurrent = (HalfEdge) treap.findTargetOrSmaller(currentVertex);
                    simplePolygon.addEdge(currentVertex.getName(), helpers.get(leftOfCurrent).getName());
                    helpers.put(leftOfCurrent, currentVertex);
                    treap.insert(incidentInteriorEdge);
                    helpers.put(incidentInteriorEdge, currentVertex);

                } else {
                    // Start Vertex

                    treap.insert(incidentInteriorEdge);
                    helpers.put(incidentInteriorEdge, currentVertex);

                }
            } else if (Helpers.vertexBelowNeighbors(currentVertex, incidentVertex, prevVertex)){
                if (incidentVertexAngle > prevVertexAngle) {
                    // Merge Vertex

                    if (mergeVertices.contains(helpers.get(prevEdge))) {
                        simplePolygon.addEdge(currentVertex.getName(), helpers.get(prevEdge).getName());
                    }
                    treap.remove(prevEdge);
                    HalfEdge leftOfCurrent = (HalfEdge) treap.findTargetOrSmaller(currentVertex);
                    if (mergeVertices.contains(helpers.get(leftOfCurrent))) {
                        simplePolygon.addEdge(currentVertex.getName(), helpers.get(leftOfCurrent).getName());
                    }
                    helpers.put(leftOfCurrent, currentVertex);
                    mergeVertices.add(currentVertex);
                } else {
                    // End Vertex

                    if (mergeVertices.contains(helpers.get(prevEdge))) {
                        simplePolygon.addEdge(currentVertex.getName(), helpers.get(prevEdge).getName());
                    }
                    treap.remove(prevEdge);
                }
            } else {
                // Regular Vertex

                if (interiorOnRightOfVertex(currentVertex, incidentInteriorEdge)) {
                    if (mergeVertices.contains(helpers.get(prevEdge))) {
                        simplePolygon.addEdge(currentVertex.getName(), helpers.get(prevEdge).getName());
                    }
                    treap.remove(prevEdge);
                    treap.insert(incidentInteriorEdge);
                    helpers.put(incidentInteriorEdge, currentVertex);
                } else {
                    HalfEdge leftOfCurrent = (HalfEdge) treap.findTargetOrSmaller(currentVertex);
                    if (mergeVertices.contains(helpers.get(leftOfCurrent))) {
                        simplePolygon.addEdge(currentVertex.getName(), helpers.get(leftOfCurrent).getName());
                    }
                    helpers.put(leftOfCurrent, currentVertex);
                }
            }
        }
        return simplePolygon;
    }

    private static boolean interiorOnRightOfVertex(Vertex regularVertex, HalfEdge incidentInternalEdge) {
        return regularVertex.compareTo(incidentInternalEdge.getNext().getOrigin()) > 0;
    }

    public static DCEL triangulateMonotonePolygon(DCEL monotonePolygon) {
        return triangulateMonotonePolygon(monotonePolygon, monotonePolygon);
    }

    public static DCEL triangulateMonotonePolygon(DCEL monotonePolygon, DCEL source) {

        ArrayList<Vertex> vertices = monotonePolygon.getVertices();

        // Get start and end vertex
        Vertex start = vertices.get(0);
        Vertex end = vertices.get(0);
        for (int i = 1; i < vertices.size(); i++) {
            if (vertices.get(i).compareTo(start) > 0) {
                start = vertices.get(i);
            }

            if (vertices.get(i).compareTo(end) < 0) {
                end = vertices.get(i);
            }
        }

        // Two path iterators that start at edges coming from start and terminate at the end
        Iterator<HalfEdge> leftChainIterator;
        Iterator<HalfEdge> rightChainIterator;
        if (start.getAngleToVertex(start.getIncidentEdge().getNext().getOrigin()) < start.getAngleToVertex(start.getIncidentEdge().getTwin().getNext().getNext().getOrigin())) {
            leftChainIterator = start.getIncidentEdge().getPathIterator(end);
            rightChainIterator = start.getIncidentEdge().getTwin().getNext().getPathIterator(end);
        } else {
            leftChainIterator = start.getIncidentEdge().getTwin().getNext().getPathIterator(end);
            rightChainIterator = start.getIncidentEdge().getPathIterator(end);
        }

        // Get vertices at the top of each chain
        Vertex leftVertex = leftChainIterator.next().getNext().getOrigin();
        Vertex rightVertex = rightChainIterator.next().getNext().getOrigin();

        // Stack for holding vertices
        Stack<Vertex> stack = new Stack<>();

        // Push first two vertices to the stack
        // First vertex is easy
        stack.push(start);

        // Triangulation.Direction of the last chain
        Direction lastChainDirection;

        // Push next highest vertex onto the stack
        if (leftVertex.compareTo(rightVertex) > 0) {
            stack.push(leftVertex);
            leftVertex = leftChainIterator.next().getNext().getOrigin();
            lastChainDirection = Direction.LEFT;
        } else {
            stack.push(rightVertex);
            rightVertex = rightChainIterator.next().getNext().getOrigin();
            lastChainDirection = Direction.RIGHT;
        }

        while (leftChainIterator.hasNext() || rightChainIterator.hasNext()) {
            Vertex current;
            Direction currentChainDirection;

            if (!rightChainIterator.hasNext() || (leftChainIterator.hasNext() && (leftVertex.getY() > rightVertex.getY() || (leftVertex.getY() == rightVertex.getY() && leftVertex.getX() < rightVertex.getX())))) {
                // choose left chain
                current = leftVertex;
                leftVertex = leftChainIterator.next().getNext().getOrigin();
                currentChainDirection = Direction.LEFT;
            } else {
                // Choose right chain
                current = rightVertex;
                rightVertex = rightChainIterator.next().getNext().getOrigin();
                currentChainDirection = Direction.RIGHT;
            }

            if (lastChainDirection != currentChainDirection) {
                // Different chains

                // Connect first diagonal with current, and save this to add back onto the stack
                Vertex topOfStack = stack.pop();
                source.addEdge(topOfStack.getName(), current.getName());

                // Pop all vertices except that bottom-most one, adding these diagonals into the monotone DoublyConnectedEdgeList.DCEL
                while (stack.size() > 1) {
                    Vertex vertexToConnect = stack.pop();
                    source.addEdge(vertexToConnect.getName(), current.getName());
                }

                // Remove the last vertex, but do not add an edge to it.
                stack.pop();

                // Push topOfStack and current onto the stack
                stack.push(topOfStack);
                stack.push(current);
            } else {
                // Same chain

                Vertex lastVertexPopped = stack.pop();

                while (stack.size() > 0 && getDirectionOfNextVertex(current, lastVertexPopped, stack.peek()) != currentChainDirection) {
                    lastVertexPopped = stack.pop();
                    source.addEdge(lastVertexPopped.getName(), current.getName());
                }

                // Push last vertex that was popped and current back onto the stack
                stack.push(lastVertexPopped);
                stack.push(current);
            }
            lastChainDirection = currentChainDirection;
        }
        return monotonePolygon;
    }

    private static Direction getDirectionOfNextVertex(Vertex current, Vertex lastPopped, Vertex next) {
        return current.getAngleToVertex(lastPopped) < current.getAngleToVertex(next) ? Direction.LEFT : Direction.RIGHT;
    }
}