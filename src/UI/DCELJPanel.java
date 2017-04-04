package UI;

import DoublyConnectedEdgeList.DCEL;
import DoublyConnectedEdgeList.HalfEdge;
import DoublyConnectedEdgeList.Vertex;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * Created by Cory Itzen on 3/23/2017.
 */
public class DCELJPanel {

    private DCEL dcel;
    private final int size = 600;
    private JFrame jFrame;


    public DCELJPanel(DCEL dcel) {
        this.dcel = dcel;
        initComponents();
    }

    private void initComponents() {
        jFrame = new JFrame();

        jFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        JPanel p = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);

                Graphics2D g2d = (Graphics2D) g;

                ArrayList<Vertex> vertices = dcel.getVertices();
                for (Vertex vertex: vertices) {
                    Ellipse2D.Double point = new Ellipse2D.Double(vertex.getX() - 5, size - vertex.getY() - 5, 10, 10);
                    g2d.fill(point);

                    g.drawString(vertex.getName(), vertex.getX() + 7, size - vertex.getY() - 7);

                    Iterator<HalfEdge> edgeIterator = vertex.getIncidentEdgeIterator();

                    while (edgeIterator.hasNext()) {
                        HalfEdge edge = edgeIterator.next();
                        Vertex other = edge.getTwin().getOrigin();
                        g2d.drawLine(vertex.getX(), size - vertex.getY(), other.getX(), size - other.getY());
                    }
                }
            }

            @Override
            public Dimension getPreferredSize() {
                return new Dimension(size, size);
            }
        };
        jFrame.add(p);
        jFrame.pack();
        jFrame.setVisible(true);
    }
}


