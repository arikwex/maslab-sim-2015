package map;

import java.awt.Shape;
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;
import java.util.LinkedList;
import java.util.List;

public class Obstacle extends Polygon {
    Path2D.Double naiveCSpace;

    public void computeNaiveCSpace(double r) {
        List<Point> csoPoints = new LinkedList<Point>();

        List<Point> roVertices = this.getVertices();
        
        for (Point p : roVertices) {
            for (double t = 0; t <= Math.PI*2; t += Math.PI/2)
                csoPoints.add(new Point(p.x + Math.cos(t), p.y + Math.sin(t)));
        }

        naiveCSpace = GeomUtils.convexHull(csoPoints).getPath();
    }

    private void computePolygonCSpace(Robot bot) {
        List<Point> csoPoints = new LinkedList<Point>();

        List<Point> roVertices = getVertices();

        for (Point v : roVertices)
            for (Point p : bot.getVertices())
                csoPoints.add(new Point(v.x - p.x, v.y - p.y));

        Polygon ret = GeomUtils.convexHull(csoPoints);
    }

    public Path2D getNaiveCSpace() {
        return naiveCSpace;
    }
}
