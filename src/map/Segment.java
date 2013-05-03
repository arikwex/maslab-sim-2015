package map;

import java.util.LinkedList;

public class Segment {
    public final Point start;
    public final Point end;
    
    public final double m;
    public final double b;
    
    public final double theta;

    public Segment(double x1, double y1, double x2, double y2) {
        this(new Point(x1, y1), new Point(x2, y2));
    }
    
    public Segment(Point start, Point end) {
        this.start = start;
        this.end = end;
        
        this.m = getSlope();
        this.b = getIntersept();
        
        this.theta = start.angleTo(start);
    }

	private double getSlope() {
        double dx = end.x - start.x;
        if (dx == 0)
            return Double.POSITIVE_INFINITY;
        
        double dy = end.y - start.y;
        return dy/dx;
    }
    
    private double getIntersept() {
        if (m == Double.POSITIVE_INFINITY)
            return start.x;
        
        return start.y - m*start.x;
    }
    
    public boolean intersects(Segment seg) {
        if (this.m == seg.m)
            return false;
        
        if (side(start, seg) == side(end, seg))
            return false;
        
        if (side(seg.start, this) == side(seg.end, this))
            return false;
        
        return true;
    }
    
    private boolean side(Point p, Segment seg) {
        if (seg.m == Double.POSITIVE_INFINITY) {
            return p.x > seg.b;
        }
        return (seg.m*p.x + seg.b - p.y) < 0;
    }
    
    public Segment trim(double length) {
        double ratio = length/length();
        
        if (ratio >= 1)
            return this;
        double dx = end.x - start.x;
        double dy = end.y - start.y;
        return new Segment(start, new Point(start.x + dx * ratio, start.y + dy * ratio));
    }
    
    public LinkedList<Segment> split(int n) {
        LinkedList<Segment> segments = new LinkedList<Segment>();
        
        double ratio = 1.0/n;
        double dx = end.x - start.x;
        double dy = end.y - start.y;
        
        Segment rem = this;
        Segment temp = null;
        
        while (segments.size() < n - 1) {
            temp = new Segment(rem.start, new Point(rem.start.x + dx * ratio, rem.start.y + dy * ratio));
            rem = new Segment(temp.end, rem.end);   
            segments.add(temp);
        }
        
        segments.add(rem);
        return segments;
    }
    
    public double length() {
        return start.distance(end);
    }
    
    @Override
    public String toString(){
    	return "("+start.x+", "+start.y+") to ("+end.x+", "+end.y+")";
    }
}