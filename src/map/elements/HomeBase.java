package map.elements;

import java.awt.geom.Path2D.Double;

import map.geom.Point;
import map.geom.Polygon;

public class HomeBase {
	private final Polygon path;
	
	public HomeBase(Point[] poly) {
        path = new Polygon();
        for (int i = 0; i < poly.length; i++) {
            Point curr = poly[i];
            path.addVertex(curr);
        }
	}
	
    public Double getPath() {
    	return path.getPath();
    }
    
    public Polygon getPolygon() {
    	return path;
    }
}
