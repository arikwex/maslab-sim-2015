package map.elements;

import java.awt.Color;

import map.geom.Obstacle;
import map.geom.Point;

public class Wall extends Obstacle {
	public Point start;
	public Point end;
	
	public Wall(Point A, Point B) {
		start = A;
		end = B;
	}
	
	public void generateObstacleData() {
		double width = 0.7/24.0;
		double dx = end.x - start.x;
		double dy = end.y - start.y;
		// Generate a perpendicular unit vector
		double uxPerp = -dy / Math.sqrt(dx * dx + dy * dy);
		double uyPerp = dx / Math.sqrt(dx * dx + dy * dy);
		// Create the Obstacle object itself
		addVertex(new Point((start.x + uxPerp * width), (start.y + uyPerp * width)));
		addVertex(new Point((start.x - uxPerp * width), (start.y - uyPerp * width)));
		addVertex(new Point((end.x - uxPerp * width), (end.y - uyPerp * width)));
		addVertex(new Point((end.x + uxPerp * width), (end.y + uyPerp * width)));
	}
	
	public Color getColor() {
		return new Color(0, 0, 255);
	}
}
