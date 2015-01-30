package map.elements;

import java.awt.Color;

import map.Pose;
import map.geom.Obstacle;
import map.geom.Point;

public class Platform extends Obstacle {
	public Point start;
	public Point end;
	
	public Platform(Point A, Point B) {
		start = A;
		end = B;
	}
	
	public Color getColor() {
		return new Color(220,220,0);
	}
	
	public void generateObstacleData() {
		double width = 1.0/24.0;
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
}
