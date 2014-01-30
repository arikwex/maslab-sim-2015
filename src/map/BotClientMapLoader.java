package map;

import java.awt.geom.Rectangle2D;
import java.util.ArrayList;

import comm.BotClientMap;
import comm.BotClientMap.Wall;

import core.BotClientSingleton;
import core.Config;

public class BotClientMapLoader {
	private static final String MAP_STRING = "22.00:3.00,2.00,3.14:0.00,0.00,0.00,2.00,N:0.00,2.00,1.00,3.00,N:1.00,3.00,3.00,3.00,N:3.00,3.00,4.00,3.00,R:4.00,3.00,4.00,1.00,N:4.00,1.00,2.00,1.00,N:2.00,1.00,1.00,0.00,N:1.00,0.00,0.00,0.00,R:";
	
	public static Map loadMap() {
		Map m = new Map();
		BotClientSingleton bc = BotClientSingleton.getInstance();

		// TODO: Load real map
		BotClientMap bcMap = new BotClientMap();
		bcMap.load(MAP_STRING);

		final double scaleFactor = Config.METERS_PER_INCH * bcMap.gridSize;

		double minX = Double.POSITIVE_INFINITY,
				maxX = Double.POSITIVE_INFINITY,
				minY = Double.POSITIVE_INFINITY,
				maxY = Double.POSITIVE_INFINITY;
		System.out.println("Loading obstacles");
		m.obstacles = new ArrayList<Obstacle>();
		for (int i = 0; i < bcMap.walls.size(); i++) {
			Wall w = bcMap.walls.get(i);
			double width;
			if (w.type == Wall.WallType.NORMAL || w.type == Wall.WallType.OPPONENT) {
				width = 0.25 * Config.METERS_PER_INCH;
			}
			else if (w.type == Wall.WallType.REACTOR) {
				width = 0.25 * Config.METERS_PER_INCH;
				Point mid = new Point((w.start.x+w.end.x)/2.0, (w.start.y+w.end.y)/2.0);
				m.reactors.add(mid);
				System.out.println("Added reactor: " + mid);
			} else { // w.type == Wall.WallType.SILO
				width = 6 * Config.METERS_PER_INCH;
			}
			Obstacle obs = convertWallToObstacle(w, width, scaleFactor);
			m.obstacles.add(obs);
			System.out.println(obs);
			
			// Min/max computing for world box
			if (w.start.x < minX) minX = w.start.x;
			if (w.start.x > maxX) maxX = w.start.x;
			if (w.start.y < minY) minY = w.start.y;
			if (w.start.y > maxY) maxY = w.start.y;
			if (w.end.x < minX) minX = w.end.x;
			if (w.end.x > maxX) maxX = w.end.x;
			if (w.end.y < minY) minY = w.end.y;
			if (w.end.y > maxY) maxY = w.end.y;
		}

		m.bot = new Robot(bcMap.startPose.x * scaleFactor, bcMap.startPose.y * scaleFactor, bcMap.startPose.theta);
		
		m.worldRect = new Rectangle2D.Double(minX, minY, maxX, maxY);

		return m;
	}

	private static Obstacle convertWallToObstacle(Wall w, double width, double scaleFactor) {
		// Compute the parallel vector
		double dx = w.end.x - w.start.x;
		double dy = w.end.y - w.start.y;
		// Generate a perpendicular unit vector
		double uxPerp = -dy / Math.sqrt(dx * dx + dy * dy);
		double uyPerp = dx / Math.sqrt(dx * dx + dy * dy);
		// Create the Obstacle object itself
		Obstacle obs = new Obstacle();
		obs.addVertex(new Point((w.start.x + uxPerp * width) * scaleFactor, (w.start.y + uyPerp * width) * scaleFactor));
		obs.addVertex(new Point((w.start.x - uxPerp * width) * scaleFactor, (w.start.y - uyPerp * width) * scaleFactor));
		obs.addVertex(new Point((w.end.x - uxPerp * width) * scaleFactor, (w.end.y - uyPerp * width) * scaleFactor));
		obs.addVertex(new Point((w.end.x + uxPerp * width) * scaleFactor, (w.end.y + uyPerp * width) * scaleFactor));
		return obs;
	}
}
