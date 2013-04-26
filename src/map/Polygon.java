package map;

import java.awt.geom.*;
import java.util.*;
import java.awt.Color;

public class Polygon {

    protected Path2D.Double path = new Path2D.Double();
    protected ArrayList<Point> points = new ArrayList<Point>();
    
    protected boolean started = false;
    protected boolean closed = false;

    public Color color = null;

    public Polygon(ArrayList<Point> points) {
        for (Point p : points) {
            addVertex(p);
        }
        this.close();
    }

    public Polygon() {
        super();
    }

    public void addVertex(Point p) {
        if (closed)
            throw new IllegalStateException("already closed");

        if (!started)
            path.moveTo(p.getX(), p.getY());
        else
            path.lineTo(p.getX(), p.getY());

        started = true;
        
        points.add(p);
    }

    public void close() {
        if (!started || closed)
            throw new IllegalStateException("already closed");

        path.closePath();

        closed = true;
    }
    
    public ArrayList<Point> getVertices(List<Point2D.Double> vertices) {
        return points;
    }
}