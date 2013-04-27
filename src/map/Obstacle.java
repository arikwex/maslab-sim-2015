package map;

import java.awt.Shape;
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;
import java.util.LinkedList;
import java.util.List;

public class Obstacle extends Polygon {
    Polygon naiveCSpace;

    public void computeNaiveCSpace(double r) {
        List<Point> csoPoints = new LinkedList<Point>();

        List<Point> roVertices = this.getVertices();
        
        for (Point p : roVertices) {
            for (double t = 0; t <= Math.PI*2; t += Math.PI/2)
                csoPoints.add(new Point(p.x + Math.cos(t), p.y + Math.sin(t)));
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
    
    public boolean intersects(Segment seg, Robot bot) {
        return naiveCSpace.intersects(seg);
        
        //if (naiveCSpace.intersects(seg))
        //    if (getBotCSpace(bot).intersects(seg))
        //        return true;
        //return false;
    }

    public Polygon getNaiveCSpace() {
        return naiveCSpace;
    }
}
