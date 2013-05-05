package map;

import java.util.LinkedList;
import java.util.List;

public class Obstacle extends Polygon {
    private Polygon minCSpace = null;
    private Polygon maxCSpace = null;

	public Polygon getMaxCSpace() {
		if (maxCSpace == null) {
	    	double r = Map.getInstance().bot.getMaxRadius();
			maxCSpace = computeNaiveCSpace(r);
		}
		return maxCSpace;
	}
	
	public Polygon getMinCSpace() {
		if (minCSpace == null) {
	    	double r = Map.getInstance().bot.getMinRadius();
			minCSpace = computeNaiveCSpace(r);
		}
		return minCSpace;
	}
	
    private Polygon computeNaiveCSpace(double r) {
    	
        List<Point> csoPoints = new LinkedList<Point>();

        List<Point> roVertices = this.getVertices();
        
        for (Point p : roVertices) {
            for (double t = 0; t <= Math.PI*2; t += Math.PI/4)
                csoPoints.add(new Point(p.x + r*Math.cos(t), p.y + r*Math.sin(t)));
        }

        return GeomUtils.convexHull(csoPoints);
    }

    private Polygon getPolyCSpace(Polygon bot) {
        List<Point> csoPoints = new LinkedList<Point>();

        List<Point> roVertices = getVertices();

        for (Point v : roVertices)
            for (Point p : bot.getVertices())
                csoPoints.add(new Point(v.x - p.x, v.y - p.y));

        return GeomUtils.convexHull(csoPoints);
    }
    
    public boolean intersects(Segment seg) {
    	//return getMaxCSpace().intersects(seg);
    	if (getMinCSpace().intersects(seg)) {
    		Polygon polyC = getPolyCSpace((Map.getInstance().bot.getRotated(seg.theta)));
    		if (polyC.intersects(seg));
    			return true;
    	}
    	
    	return false;
    }
    
    @Override
    public String toString(){
    	String s = "[";
    	for (Point p : points){
    		s += "("+p.x+", "+p.y+")";
    	}
    	s = s.substring(0,s.length()-1);
    	s += "]";
    	return s;
    }
}
