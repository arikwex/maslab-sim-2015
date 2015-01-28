package map.elements;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Path2D;

import map.geom.Obstacle;
import map.geom.Point;
import map.geom.Polygon;

public class Stack extends Obstacle {
	public Point pt;
	public String cubes;
	
	private final double sz = 0.75 / 24.0;
	private final Polygon cube = new Polygon();
	
	public Stack(Point pt, String cubes) {
		this.pt = pt;
		this.cubes = cubes;
		
		cube.addVertex(new Point((+ sz), (-sz)));
		cube.addVertex(new Point((- sz), (-sz)));
		cube.addVertex(new Point((- sz), (sz)));
		cube.addVertex(new Point((sz), (sz)));
	}
	
	public Color getColor() {
		char topCube = this.getTopCube();
		return charToColor(topCube);
	}
	
	public Color charToColor(char c) {
		if (c == 'R')
			return new Color(255,0,0);
		else
			return new Color(0,200,0);
	}
	
	public void paint(Graphics2D g) {
		//cube.
		Path2D.Double path = cube.getPath();
		
		for (int i = 0; i < cubes.length(); i++) {
			g.setColor(charToColor(cubes.charAt(i)));
			Shape s = path.createTransformedShape(new AffineTransform(1, 0, 0, 1, pt.x, sz * 2.2 * i + pt.y));
			g.fill(s);
		}
	}
	
	public char getTopCube() {
		return cubes.charAt(cubes.length() - 1);
	}
	
	public void generateObstacleData() {
		// Create the Obstacle object itself
		addVertex(new Point((pt.x + sz), (pt.y + sz)));
		addVertex(new Point((pt.x - sz), (pt.y + sz)));
		addVertex(new Point((pt.x - sz), (pt.y - sz)));
		addVertex(new Point((pt.x + sz), (pt.y - sz)));
	}
}
