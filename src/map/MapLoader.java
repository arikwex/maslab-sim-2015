package map;

import java.awt.geom.Rectangle2D;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;

import logging.Log;
import map.elements.HomeBase;
import map.elements.Platform;
import map.elements.Stack;
import map.elements.Wall;
import map.geom.Point;

public class MapLoader {
	public static final double GRID_SIZE = 24;
	public static final double SCALE_FACTOR = 0.5588;//Config.METERS_PER_INCH * GRID_SIZE;
	private static double minX, maxX, minY, maxY;
	
	public static void load(Map map, File mapFile) {
		map.clear();

		minX = Double.POSITIVE_INFINITY;
		maxX = Double.NEGATIVE_INFINITY;
		minY = Double.POSITIVE_INFINITY;
		maxY = Double.NEGATIVE_INFINITY;
		
		System.out.println("Loading map");
		try {
			BufferedReader br = new BufferedReader(new FileReader(mapFile));
			String line;
			while ((line = br.readLine()) != null) {
				line = line.trim();
			   process(map, line);
			}
			br.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		map.setWorldRect(new Rectangle2D.Double(minX, minY, maxX-minX, maxY-minY));
		System.out.println("World rect: " + map.getWorldRect());
	}
	
	public static void process(Map map, String line) {
		String[] parts = line.split(",");
		
		// Start Location
		if (parts[0].equals("L")) {
			map.bot.pose = new Pose(parsePoint(parts[1], parts[2]),	Math.random());
		}
		
		// Wall
		if (parts[0].equals("W")) {
			Wall wall = new Wall(parsePoint(parts[1], parts[2], true), parsePoint(parts[3], parts[4], true));
			wall.generateObstacleData();
			map.addWall(wall);
		}
		
		// Stack
		if (parts[0].equals("S")) {
			Stack stack = new Stack(parsePoint(parts[1], parts[2]), parts[3] + parts[4] + parts[5]);
			map.addStack(stack);
		}
		
		// Platform
		if (parts[0].equals("P")) {
			Platform plat = new Platform(parsePoint(parts[1], parts[2], true), parsePoint(parts[3], parts[4], true));
			plat.generateObstacleData();
			map.addPlatform(plat);
		}
		
		// HomeBase
		if (parts[0].equals("H")) {
			int N = Integer.parseInt(parts[1]);
			Point[] poly = new Point[N];
			for (int q = 0; q < N; q++) {
				poly[q] = new Point(Integer.parseInt(parts[q*2 + 2]) * SCALE_FACTOR,
									Integer.parseInt(parts[q*2 + 3]) * SCALE_FACTOR);
			}
			map.setHomeBase(new HomeBase(poly));
		}
	}
	
	public static Point parsePoint(String x, String y) {
		return parsePoint(x, y, false);
	}
	
	public static Point parsePoint(String x, String y, boolean addToBounds) {
		Point p = new Point(Integer.parseInt(x) * SCALE_FACTOR, Integer.parseInt(y) * SCALE_FACTOR);
		if (addToBounds)
			accountAsBoundPoint(p);
		return p;
	}
	
	public static void accountAsBoundPoint(Point w) {	
		// Min/max computing for world box
		if (w.x < minX) minX = w.x;
		if (w.x > maxX) maxX = w.x;
		if (w.y < minY) minY = w.y;
		if (w.y > maxY) maxY = w.y;
	}
}
