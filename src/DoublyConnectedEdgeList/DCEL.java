package DoublyConnectedEdgeList;

import Helpers.Helpers;
import PointLocation.PointLocation;
import Triangulation.Triangulation;
import UI.DCELJPanel;

import javax.swing.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;

/**
 * Created by Cory Itzen on 3/14/2017.
 */
public class DCEL {

    // List of vertices in the DoublyConnectedEdgeList
    private final HashMap<String, Vertex> vertexMap;
    private ArrayList<HalfEdge> edgeList;

    private ArrayList<Face> faces;
    private Face outerFace;


    public DCEL() {
        this.vertexMap = new HashMap<>();
        this.edgeList = new ArrayList<>();

        this.faces = new ArrayList<>();
        this.outerFace = new Face("Outer Face");
        this.faces.add(outerFace);
    }

    public DCEL(Face outerFace) {
        this.vertexMap = new HashMap<>();
        this.edgeList = new ArrayList<>();

        this.faces = new ArrayList<>();
        this.outerFace = outerFace;
        this.faces.add(outerFace);
    }

    public ArrayList<Vertex> getVertices() {
        ArrayList<Vertex> vertices = new ArrayList<>();
        for (Map.Entry<String, Vertex> map : vertexMap.entrySet()) {
            vertices.add(map.getValue());
        }
        return vertices;
    }

    public Face getOuterFace() {
        return outerFace;
    }

    public ArrayList<Face> getFaces() {
        return faces;
    }

    public void addVertex(String vertexName, Vertex vertex) {
        if (vertexMap.containsKey(vertexName)) {
            throw new DuplicateVertexException();
        }
        vertexMap.put(vertexName, vertex);
    }

    public void addVertex(Vertex vertex) {
        addVertex(vertex.getName(), vertex);
    }

    public void addEdge(String vertex1, String vertex2) {

        // Get the two vertices to be added
        Vertex v1 = vertexMap.get(vertex1);
        Vertex v2 = vertexMap.get(vertex2);
        if (v1 == null || v2 == null) {
            throw new NoSuchVertexException();
        }

        // Check if there is a path from v1 to v2.
        HalfEdge pathEdge = getAntiClockwisePathToVertex(v1, v2);
        Vertex endOfPath = v2;
        if (pathEdge == null) {
            pathEdge = getAntiClockwisePathToVertex(v2, v1);
            endOfPath = v1;
        }

        // Create two half edges between the two vertices
        HalfEdge e1 = new HalfEdge(v1, vertex1 + ", " + vertex2);
        HalfEdge e2 = new HalfEdge(v2, vertex2 + ", " + vertex1);
        edgeList.add(e1);                                               // debug
        edgeList.add(e2);                                               // debug

        // Set both half edges to be twins of each other
        e1.setTwin(e2);
        e2.setTwin(e1);

        // Set both edge's incident face to be the outerface
        e1.setIncidentFace(outerFace);
        e2.setIncidentFace(outerFace);

        // Get V1EdgeToUpdate, V2EdgeToUpdate
        HalfEdge v1EdgeToUpdate = findBoundingEdge(v1, v2);
        HalfEdge v2EdgeToUpdate = findBoundingEdge(v2, v1);

        if (v1EdgeToUpdate != null) {
            // Has at least one edge

            e2.setNext(v1EdgeToUpdate);
            e1.setPrev(v1EdgeToUpdate.getPrev());

            v1EdgeToUpdate.getPrev().setNext(e1);
            v1EdgeToUpdate.setPrev(e2);
        } else {
            // Does not have an edge
            e2.setNext(e1);
            e1.setPrev(e2);
        }

        if (v2EdgeToUpdate != null) {
            // Has at least one edge

            e1.setNext(v2EdgeToUpdate);
            e2.setPrev(v2EdgeToUpdate.getPrev());

            v2EdgeToUpdate.getPrev().setNext(e2);
            v2EdgeToUpdate.setPrev(e1);
        } else {
            e1.setNext(e2);
            e2.setPrev(e1);
        }

        // Set incident edge of both vertices. Not sure if this is necessary
        v1.setIncidentEdge(e1);
        v2.setIncidentEdge(e2);

        // If a vertex path existed from v1 to v2 before adding the edge
        // Add a new face
        // Iterate around the vertices in the path and update the references in these to the new face
        if (pathEdge != null) {
            Face generatedFace = new Face("Face " + faces.size());
            faces.add(generatedFace);

            Iterator<HalfEdge> iterator = pathEdge.getPathIterator(endOfPath);
            while (iterator.hasNext()) {
                HalfEdge current = iterator.next();
                if (current.getIncidentFace().getOuterComponent() == current) {
                    // Update this face's outerComponent
                    current.getIncidentFace().setOuterComponent(endOfPath == v2 ? e1 : e2);
                }
                current.setIncidentFace(generatedFace);
            }
            if (endOfPath == v2) {
                generatedFace.setOuterComponent(e2);
                e2.setIncidentFace(generatedFace);
                e1.setIncidentFace(e1.getPrev().getIncidentFace());
            } else {
                generatedFace.setOuterComponent(e1);
                e1.setIncidentFace(generatedFace);
                e2.setIncidentFace(e2.getPrev().getIncidentFace());
            }
        }
    }

    /**
     * Iterate through all edges of a given vertex, returning the outgoing half edge that bounds the given toFind vertex
     * @param source vertex with edges that are to be iterated around. Must have at least one edge
     * @param toFind vertex that is not already connected to source that is to be bound
     * @return
     */
    private HalfEdge findBoundingEdge(Vertex source, Vertex toFind) {

        if (source.getIncidentEdge() == null) {
            return null;
        }

        final int targetAngle = source.getAngleToVertex(toFind);

        if (source.getIncidentEdge() == source.getIncidentEdge().getPrev().getTwin()) {
            // There's only one option, just go with it...
            return source.getIncidentEdge();
        }

        Iterator<HalfEdge> edgeIterator = source.getIncidentEdgeIterator();

        while (edgeIterator.hasNext()) {
            HalfEdge current = edgeIterator.next();
            HalfEdge prev = current.getPrev().getTwin();

            int currentAngle = source.getAngleToVertex(current.getNext().getOrigin());
            int prevAngle = source.getAngleToVertex(prev.getNext().getOrigin());

            if (prevAngle > targetAngle && targetAngle > currentAngle) {
                return current;
            } else if (currentAngle > prevAngle){
                // Shift everything down by the lowest point, check and see if still bounded. I coded this at 2am :(
                int difference = -180 - Math.min(currentAngle, prevAngle);
                currentAngle = currentAngle + difference;
                prevAngle = prevAngle + difference;
                int tempTarget = targetAngle + difference;
                if (currentAngle <= -180) {
                    currentAngle += 360;
                }
                if (prevAngle <= -180) {
                    prevAngle += 360;
                }
                if (tempTarget <= -180) {
                    tempTarget += 360;
                }
                if (prevAngle > tempTarget && tempTarget > currentAngle) {
                    return current;
                }
            }
        }
        System.out.println("Something messed up, this should not get here..." + source.getName() + " " + toFind.getName());
        throw new RuntimeException();
    }

    /**
     * Returns the HalfEdge whose origin is the starting vertex and whose path can be followed to find the target vertex
     * If a path to target does not exist, this returns null
     * @param startingVertex
     * @param target
     * @return
     */
    private HalfEdge getAntiClockwisePathToVertex(Vertex startingVertex, Vertex target) {

        if (startingVertex.getIncidentEdge() == null) {
            return null;
        }

        // Iterates around each incident edge of the starting vertex
        Iterator<HalfEdge> edgeIterator = startingVertex.getIncidentEdgeIterator();

        // Edge coming from vertex. If the target is found, this HalfEdge is returned
        HalfEdge emergentEdge;

        // Iterator Edge. Searches through edges coming from startingVertex
        HalfEdge currentEdge;

        while (edgeIterator.hasNext()) {
            ArrayList<Vertex> vertexPath = new ArrayList<>();
            emergentEdge = edgeIterator.next();
            currentEdge = emergentEdge.getNext();
            vertexPath.add(emergentEdge.getOrigin());
            vertexPath.add(currentEdge.getOrigin());

            while (currentEdge.getOrigin() != startingVertex && currentEdge.getOrigin() != target) {
                currentEdge = currentEdge.getNext();
                vertexPath.add(currentEdge.getOrigin());
            }

            // If target is found and path is anticlockwise
            if (currentEdge.getOrigin() == target && Helpers.isAntiClockwise(vertexPath)) {
                return emergentEdge;
            }
        }

        // Target was not found
        return null;
    }

    public void constructSimplePolygon(List<Vertex> listOfVertices) {

        // Check if not simple polygon. (This isn't very accurate)
        if (listOfVertices.size() < 3) {
            throw new NotSimplePolygonException();
        }

        // Add first vertex
        addVertex(listOfVertices.get(0));

        for (int i = 1; i < listOfVertices.size(); i++) {
            addVertex(listOfVertices.get(i));
            addEdge(listOfVertices.get(i - 1).getName(), listOfVertices.get(i).getName());
        }

        addEdge(listOfVertices.get(listOfVertices.size() - 1).getName(), listOfVertices.get(0).getName());
    }

    public void printDebug() {
        System.out.println("\tGetting vertices");
        System.out.println("Vertex\t\tEdges\t\tTwin\t\tNext\t\tPrev\t\tFace");
        System.out.println("--------------------");
        Iterator vertexIterator = vertexMap.keySet().iterator();
        while (vertexIterator.hasNext()) {
            Vertex vertex = vertexMap.get(vertexIterator.next());
            System.out.println(vertex.getName() + "\t\t\t");

            Iterator<HalfEdge> edgeIterator = vertex.getIncidentEdgeIterator();

            while (edgeIterator.hasNext()) {
                HalfEdge current = edgeIterator.next();

                System.out.println("\t\t\t" + current.getName() +
                        "\t\t" + current.getTwin().getName() +
                        "\t\t" + current.getNext().getName() +
                        "\t\t" + current.getPrev().getName() +
                        "\t\t" + current.getIncidentFace().getName());
            }
        }
    }

    public ArrayList<DCEL> getSubDivisions() {
        ArrayList<DCEL> subDivisions = new ArrayList<>();

        for (Face face : this.getFaces()) {
            if (face != this.getOuterFace()) {
                subDivisions.add(getSubdivision(face));
            }
        }
        return subDivisions;
    }

    public DCEL getSubdivision(Face face) {
        ArrayList<Vertex> verticesInFace = new ArrayList<>();
        FaceIterator faceIterator = face.getFaceIterator();
        while (faceIterator.hasNext()) {
            Vertex vertex = faceIterator.next();
            Vertex copy = new Vertex(vertex.getX(), vertex.getY(), vertex.getName());
            verticesInFace.add(copy);
        }
        DCEL subDivision = new DCEL();
        subDivision.constructSimplePolygon(verticesInFace);
        return subDivision;
    }

    public static ArrayList<Vertex> loadVertices(String fileName) throws FileNotFoundException{
        ArrayList<Vertex> vertices = new ArrayList<>();

        File vertexFile = new File(fileName);
        Scanner scanner = new Scanner(vertexFile);
        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();
            Scanner lineScanner = new Scanner(line);
            int x = Integer.valueOf(lineScanner.next());
            int y = Integer.valueOf(lineScanner.next());
            String name = lineScanner.next();
            vertices.add(new Vertex(x, y, name));
        }

        return vertices;
    }

    public static void main(String[] args) throws FileNotFoundException {

        String polygonFileString = args[0];
        DCEL dcel = new DCEL();
        dcel.constructSimplePolygon(loadVertices(polygonFileString));

        // Make the DCEL Y Monotone
        Triangulation.makeMonotone(dcel);

        // Triangulate the Subdivisions
        for (DCEL subDivision : dcel.getSubDivisions()) {
            Triangulation.triangulateMonotonePolygon(subDivision, dcel);
        }

        // Create a Point Location
        PointLocation pointLocation = new PointLocation(dcel);

        // Query at a point
        Vertex query = new Vertex(240, 210, "Query");
        dcel.addVertex(query);
        Face faceQuery = pointLocation.Query(query.getX(), query.getY());
        System.out.println(faceQuery.getName());
        if (faceQuery != dcel.getOuterFace()) {
            SwingUtilities.invokeLater(() -> new DCELJPanel(dcel.getSubdivision(faceQuery)));
        }

        // Display on a UI
        SwingUtilities.invokeLater(() -> new DCELJPanel(dcel));
    }

    public final static class DuplicateVertexException extends RuntimeException {}
    public final static class NoSuchVertexException extends RuntimeException {}
    public final static class NotSimplePolygonException extends RuntimeException {}
}