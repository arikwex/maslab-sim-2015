package map;

import java.awt.geom.*;
import java.util.*;
import java.awt.Color;

public class Polygon {

    protected Path2D path = new Path2D.Float();
    protected ArrayList<Point> points = new ArrayList<Point>();
    
    protected boolean started = false;
    protected boolean closed = false;

    public Color color = null;

    public void addVertex(Point2D vertex) {
        addVertex(vertex.getX(), vertex.getY());
    }

    public void addVertex(double x, double y) {
        addVertex((float) x, (float) y);
    }

    public void addVertex(float x, float y) {
        if (closed)
            throw new IllegalStateException("already closed");

        if (!started)
            path.moveTo(x, y);
        else
            path.lineTo(x, y);

        started = true;
    }

    public void close() {

        if (!started || closed)
            throw new IllegalStateException("already closed");

        path.closePath();

        closed = true;
    }

    public boolean contains(double x, double y) {
        return path.contains(x, y);
    }

    public boolean contains(Point2D p) {
        return path.contains(p);
    }

    public List<Point2D.Double> getVertices() {
        return getVertices(null);
    }

    public List<Point2D.Double> getVertices(List<Point2D.Double> vertices) {

        if (vertices == null)
            vertices = new LinkedList<Point2D.Double>();

        double[] coords = new double[6];
        for (PathIterator it = path.getPathIterator(null); !it.isDone(); it.next()) {
            if (it.currentSegment(coords) != PathIterator.SEG_CLOSE)
                vertices.add(new Point2D.Double(coords[0], coords[1]));
        }

        return vertices;
    }
}