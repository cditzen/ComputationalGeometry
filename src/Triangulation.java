import DoublyConnectedEdgeList.HalfEdge;
import DoublyConnectedEdgeList.Vertex;

import java.util.*;
import java.util.jar.Pack200;

/**
 * Created by Cory Itzen on 3/14/2017.
 */
public class Triangulation {


    public static DCEL makeMonotone(DCEL simplePolygon) {

        return null;
    }

    public static DCEL triangulateMonotonePolygon(DCEL monotonePolygon) {

        // Get vertices
        ArrayList<Vertex> vertices = monotonePolygon.getVertices();

        // Get start and end vertex
        Vertex start = vertices.get(0);
        Vertex end = vertices.get(0);
        for (int i = 1; i < vertices.size(); i++) {
            if (vertices.get(i).getY() > start.getY() || (vertices.get(i).getY() == start.getY() && vertices.get(i).getX() < start.getX())) {
                start = vertices.get(i);
            }

            if (vertices.get(i).getY() < end.getY() || (vertices.get(i).getY() == end.getY() && vertices.get(i).getX() > end.getX())) {
                end = vertices.get(i);
            }
        }

        // Stack for holding vertices
        Stack<Vertex> stack = new Stack<>();

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
        // TODO Need to check hasNext()
        Vertex leftVertex = leftChainIterator.next().getNext().getOrigin();
        Vertex rightVertex = rightChainIterator.next().getNext().getOrigin();

        // Push first two vertices to the stack

        // First vertex is easy
        stack.push(start);

        Direction lastChainDirection;

        // Push next highest vertex onto the stack
        if (leftVertex.getY() > rightVertex.getY() || (leftVertex.getY() == rightVertex.getY() && leftVertex.getX() < rightVertex.getX())) {
            stack.push(leftVertex);
            // TODO Need to check that another vertex is available
            leftVertex = leftChainIterator.next().getNext().getOrigin();
            lastChainDirection = Direction.LEFT;
        } else {
            stack.push(rightVertex);
            rightVertex = rightChainIterator.next().getNext().getOrigin();
            lastChainDirection = Direction.RIGHT;
        }

        // TODO Check this statement. All nodes should be iterated through
        while (leftChainIterator.hasNext() || rightChainIterator.hasNext()) {
            Vertex current = null;
            Direction currentChainDirection;

            // TODO Check this condition
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
                monotonePolygon.addEdge(topOfStack.getName(), current.getName());

                // Pop all vertices except that bottom-most one, adding these diagonals into the monotone DCEL
                while (stack.size() > 1) {
                    Vertex vertexToConnect = stack.pop();
                    monotonePolygon.addEdge(vertexToConnect.getName(), current.getName());
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
                    monotonePolygon.addEdge(lastVertexPopped.getName(), current.getName());
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

enum Direction {
    LEFT, RIGHT
}
