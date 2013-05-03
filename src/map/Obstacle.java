package map;

import java.util.LinkedList;
import java.util.List;

public class Obstacle extends Polygon {
    Polygon naiveCSpace;

    public void computeNaiveCSpace(double r) {
        List<Point> csoPoints = new LinkedList<Point>();

        List<Point> roVertices = this.getVertices();
        
        for (Point p : roVertices) {
            for (double t = 0; t <= Math.PI*2; t += Math.PI/4)
                csoPoints.add(new Point(p.x + r*Math.cos(t), p.y + r*Math.sin(t)));
        }

        naiveCSpace = GeomUtils.convexHull(csoPoints);
    }

    private Polygon getBotCSpace(Robot bot) {
        List<Point> csoPoints = new LinkedList<Point>();

        List<Point> roVertices = getVertices();

        for (Point v : roVertices)
            for (Point p : bot.getVertices())
                csoPoints.add(new Point(v.x - p.x, v.y - p.y));

        return GeomUtils.convexHull(csoPoints);
    }
    
    public boolean intersects(Segment seg) {
    	return naiveCSpace.intersects(seg);
        //boolean i = path.intersects(new Double(seg.start.x,seg.start.y,seg.end.x,seg.end.y));
        //System.out.println("does it intersect? "+i);
        //return i;
    	
        //if (naiveCSpace.intersects(seg))
        //    if (getBotCSpace(bot).intersects(seg))
        //        return true;
        //return false;
    }

    public Polygon getNaiveCSpace() {
        return naiveCSpace;
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
