package map;

import java.awt.geom.*;
import java.util.*;
import java.awt.Color;
import java.awt.Rectangle;

public class Polygon {

    protected Path2D.Double path = new Path2D.Double();
    protected ArrayList<Point> points = new ArrayList<Point>();
    protected ArrayList<Segment> segments = new ArrayList<Segment>();
    
    protected boolean started = false;
    protected boolean closed = false;

    public Color color = null;
	private Rectangle bounds;

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

        if (!started) {
            path.moveTo(p.getX(), p.getY());
        } else {
            path.lineTo(p.getX(), p.getY());
            segments.add(new Segment(points.get(points.size()-1), p));
        }

        started = true;
        
        points.add(p);
    }

    public void close() {
        if (!started || closed)
            throw new IllegalStateException("already closed");

        path.closePath();
        segments.add(new Segment(points.get(points.size()-1), points.get(0)));

        closed = true;
    }

    public boolean intersects(Segment seg) {
        for (Segment s : segments)
            if (s.intersects(seg))
                return true;
        
        return false;
    }
    
    public ArrayList<Point> getVertices() {
        return points;
    }
    
    public Path2D.Double getPath() {
        return path;
    }
    
    public boolean contains(Point p) {
    	return path.contains(p);
    }
}