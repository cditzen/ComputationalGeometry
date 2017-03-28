import DoublyConnectedEdgeList.HalfEdge;
import DoublyConnectedEdgeList.Vertex;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * Created by Cory Itzen on 3/23/2017.
 */
public class DCELJPanel extends JPanel {

    private DCEL dcel;
    private final int size = 700;

    public DCELJPanel(DCEL dcel) {

        this.dcel = dcel;

        JFrame frame = new JFrame("Points");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.add(this);
        frame.setSize(size, size);
        frame.setVisible(true);

    }

    @Override
    public void paintComponent(Graphics g) {
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



//        for (int i = 0; i <= 100000; i++) {
//            Dimension size = getSize();
//            int w = size.width ;
//            int h = size.height;
//
//            Random r = new Random();
//            int x = Math.abs(r.nextInt()) % w;
//            int y = Math.abs(r.nextInt()) % h;
//            g2d.drawLine(x, y, x, y);
//        }
    }

}
