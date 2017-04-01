package DoublyConnectedEdgeList;

import Helpers.Helpers;
import PointLocation.PointLocation;
import Triangulation.Triangulation;
import UI.DCELJPanel;

import javax.swing.*;
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
        vertexMap = new HashMap<>();
        edgeList = new ArrayList<>();

        faces = new ArrayList<>();
        outerFace = new Face("Outer Face");
        faces.add(outerFace);
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

        // TODO Check duplicate edges

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

            if (Helpers.targetInRange(currentAngle, prevAngle, targetAngle)) {
                return current;
            } else {
                // Shift everything down by the lowest point, check and see if still bounded. I coded this at 2am :(
                int difference = -180 - Math.min(currentAngle, prevAngle);
                currentAngle = currentAngle + difference;
                prevAngle = prevAngle + difference;
                int tempTarget = targetAngle + difference;
                if (currentAngle <= -180) {
                    currentAngle += 380;
                }
                if (prevAngle <= -180) {
                    prevAngle += 360;
                }
                if (tempTarget <= -180) {
                    tempTarget += 360;
                }
                if (Helpers.targetInRange(currentAngle, prevAngle, tempTarget)) {
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
        // TODO Check if not simple polygon
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
            System.out.println("Face: " + face.getName());
            if (face != this.getOuterFace()) {
                ArrayList<Vertex> verticesInFace = new ArrayList<>();
                FaceIterator faceIterator = face.getFaceIterator();
                while (faceIterator.hasNext()) {
                    Vertex vertex = faceIterator.next();
                    // Make duplicate Vertex
                    Vertex copy = new Vertex(vertex.getX(), vertex.getY(), vertex.getName());
                    verticesInFace.add(copy);
                    System.out.println("\t\tVertex: " + vertex.getName());
                }
                DCEL subDivision = new DCEL();
                subDivision.constructSimplePolygon(verticesInFace);
                subDivisions.add(subDivision);
            }
        }
        return subDivisions;
    }

    public static void main(String[] args) {
        System.out.println("Creating DoublyConnectedEdgeList.DCEL");


        ArrayList<Vertex> zigZagVertices = new ArrayList<>();
        zigZagVertices.add(new Vertex(100, 150, "V0"));
        zigZagVertices.add(new Vertex(150, 100, "V1"));
        zigZagVertices.add(new Vertex(200, 200, "V2"));
        zigZagVertices.add(new Vertex(250, 150, "V3"));
        zigZagVertices.add(new Vertex(300, 250, "V4"));
        zigZagVertices.add(new Vertex(400, 200, "V5"));
        zigZagVertices.add(new Vertex(450, 300, "V6"));
        zigZagVertices.add(new Vertex(400, 350, "V7"));
        zigZagVertices.add(new Vertex(350, 300, "V8"));
        zigZagVertices.add(new Vertex(300, 350, "V9"));
        zigZagVertices.add(new Vertex(250, 250, "V10"));
        zigZagVertices.add(new Vertex(200, 300, "V11"));
        zigZagVertices.add(new Vertex(150, 200, "V12"));
        zigZagVertices.add(new Vertex(100, 250, "V13"));

        DCEL zigZag = new DCEL();
        zigZag.constructSimplePolygon(zigZagVertices);

        ArrayList<Vertex> list = new ArrayList<>();
        list.add(new Vertex(230, 350, "V0"));
        list.add(new Vertex(150, 290, "V1"));
        list.add(new Vertex(220, 270, "V2"));
        list.add(new Vertex(200, 220, "V3"));
        list.add(new Vertex(100, 250, "V4"));
        list.add(new Vertex(120, 120, "V5"));
        list.add(new Vertex(260, 100, "V6"));
        list.add(new Vertex(280, 230, "V7"));
        list.add(new Vertex(310, 200, "V8"));
        list.add(new Vertex(320, 270, "V9"));
        list.add(new Vertex(300, 270, "V10"));
        list.add(new Vertex(290, 350, "V11"));
        list.add(new Vertex(240, 320, "V12"));

        DCEL dcel = new DCEL();
        dcel.constructSimplePolygon(list);

        ArrayList<Vertex> yMonotoneVertices = new ArrayList<>();
        yMonotoneVertices.add(new Vertex(300, 550, "V0"));
        yMonotoneVertices.add(new Vertex(270, 484, "V1"));
        yMonotoneVertices.add(new Vertex(225, 425, "V2"));
        yMonotoneVertices.add(new Vertex(170, 380, "V3"));
        yMonotoneVertices.add(new Vertex(100, 350, "V4"));
        yMonotoneVertices.add(new Vertex(230, 200, "V5"));
        yMonotoneVertices.add(new Vertex(170, 140, "V6"));
        yMonotoneVertices.add(new Vertex(300, 90, "V7"));
        yMonotoneVertices.add(new Vertex(380, 150, "V8"));
        yMonotoneVertices.add(new Vertex(360, 180, "V9"));
        yMonotoneVertices.add(new Vertex(440, 270, "V10"));
        yMonotoneVertices.add(new Vertex(455, 245, "V11"));
        yMonotoneVertices.add(new Vertex(475, 230, "V12"));
        yMonotoneVertices.add(new Vertex(500, 300, "V13"));
        yMonotoneVertices.add(new Vertex(270, 290, "V14"));
        yMonotoneVertices.add(new Vertex(245, 320, "V15"));
        yMonotoneVertices.add(new Vertex(320, 405, "V16"));

        DCEL thirdPolygon = new DCEL();
        thirdPolygon.constructSimplePolygon(yMonotoneVertices);

        Triangulation.makeMonotone(zigZag);
        Triangulation.makeMonotone(dcel);
        Triangulation.makeMonotone(thirdPolygon);

        for (DCEL subDivision : zigZag.getSubDivisions()) {
            Triangulation.triangulateMonotonePolygon(subDivision, zigZag);
        }

        for (DCEL subDivision : dcel.getSubDivisions()) {
            Triangulation.triangulateMonotonePolygon(subDivision, dcel);
        }

        for (DCEL subDivision : thirdPolygon.getSubDivisions()) {
            Triangulation.triangulateMonotonePolygon(subDivision, thirdPolygon);
        }

        SwingUtilities.invokeLater(() -> new DCELJPanel(zigZag));
        SwingUtilities.invokeLater(() -> new DCELJPanel(dcel));
        SwingUtilities.invokeLater(() -> new DCELJPanel(thirdPolygon));

        ArrayList<Vertex> pointLocationVertices = new ArrayList<>();
        pointLocationVertices.add(new Vertex(200, 100, "V0"));
        pointLocationVertices.add(new Vertex(300, 150, "V1"));
        pointLocationVertices.add(new Vertex(300, 300, "V2"));
        pointLocationVertices.add(new Vertex(200, 400, "V3"));
        pointLocationVertices.add(new Vertex(100, 350, "V4"));
        pointLocationVertices.add(new Vertex(100, 200, "V5"));

        DCEL pointLocationDcel = new DCEL();
        pointLocationDcel.constructSimplePolygon(pointLocationVertices);

        pointLocationDcel.addVertex(new Vertex(200, 250, "V6"));
        pointLocationDcel.addEdge("V0", "V6");
        pointLocationDcel.addEdge("V1", "V6");
        pointLocationDcel.addEdge("V2", "V6");
        pointLocationDcel.addEdge("V3", "V6");
        pointLocationDcel.addEdge("V4", "V6");
        pointLocationDcel.addEdge("V5", "V6");

        SwingUtilities.invokeLater(() -> new DCELJPanel(pointLocationDcel));

        PointLocation pointLocation = new PointLocation(pointLocationDcel);

        pointLocation.Query(0, 0);
        pointLocation.Query(0, 110);
        pointLocation.Query(0, 160);
        pointLocation.Query(0, 210);
        pointLocation.Query(0, 260);
        pointLocation.Query(0, 310);
        pointLocation.Query(0, 360);
        pointLocation.Query(0, 410);

        System.out.println("---");

        pointLocation.Query(0, 0);
        pointLocation.Query(0, 100);
        pointLocation.Query(0, 150);
        pointLocation.Query(0, 200);
        pointLocation.Query(0, 250);
        pointLocation.Query(0, 300);
        pointLocation.Query(0, 350);
        pointLocation.Query(0, 400);
    }

    public final static class DuplicateVertexException extends RuntimeException {}
    public final static class NoSuchVertexException extends RuntimeException {}
    public final static class NotSimplePolygonException extends RuntimeException {}
}