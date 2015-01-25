package map;

import java.awt.geom.Rectangle2D;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;

import logging.Log;
import map.elements.Platform;
import map.elements.Stack;
import map.elements.Wall;
import map.geom.Point;

public class MapLoader {
	public static final double GRID_SIZE = 24;
	public static final double scaleFactor = 1.0;//Config.METERS_PER_INCH * GRID_SIZE;
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
		map.setWorldRect(new Rectangle2D.Double(minX * scaleFactor,
											    minY * scaleFactor,
											    maxX * scaleFactor,
											    maxY * scaleFactor));
		System.out.println("World rect: " + map.getWorldRect());
	}
	
	public static void process(Map map, String line) {
		String[] parts = line.split(",");
		
		// Start Location
		if (parts[0].equals("L")) {
			map.bot.pose = new Pose(Integer.parseInt(parts[1]) * scaleFactor,
									Integer.parseInt(parts[2]) * scaleFactor,
									Math.random());
		}
		
		// Wall
		if (parts[0].equals("W")) {
			Wall wall = new Wall(new Point(Integer.parseInt(parts[1]) * scaleFactor, Integer.parseInt(parts[2]) * scaleFactor),
								 new Point(Integer.parseInt(parts[3]) * scaleFactor, Integer.parseInt(parts[4]) *scaleFactor));
			wall.generateObstacleData();
			accountAsBoundPoint(wall.start);
			accountAsBoundPoint(wall.end);
			map.addWall(wall);
		}
		
		// Stack
		if (parts[0].equals("S")) {
			Stack stack = new Stack(new Point(Integer.parseInt(parts[1]) * scaleFactor, Integer.parseInt(parts[2]) * scaleFactor),
								    parts[3] + parts[4] + parts[5]);
			stack.generateObstacleData();
			map.addStack(stack);
		}
		
		// Platform
		if (parts[0].equals("P")) {
			Platform plat = new Platform(new Point(Integer.parseInt(parts[1]) * scaleFactor, Integer.parseInt(parts[2]) * scaleFactor),
					 					 new Point(Integer.parseInt(parts[3]) * scaleFactor, Integer.parseInt(parts[4]) *scaleFactor));
			plat.generateObstacleData();
			accountAsBoundPoint(plat.start);
			accountAsBoundPoint(plat.end);
			map.addPlatform(plat);
		}
	}
	
	public static void accountAsBoundPoint(Point w) {	
		// Min/max computing for world box
		if (w.x < minX) minX = w.x / scaleFactor;
		if (w.x > maxX) maxX = w.x / scaleFactor;
		if (w.y < minY) minY = w.y / scaleFactor;
		if (w.y > maxY) maxY = w.y / scaleFactor;
	}
}
