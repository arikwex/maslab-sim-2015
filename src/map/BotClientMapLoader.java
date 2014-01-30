package map;

import java.awt.geom.Rectangle2D;
import java.util.ArrayList;

import comm.BotClientMap;
import comm.BotClientMap.Wall;

import core.BotClientSingleton;
import core.Config;

public class BotClientMapLoader {
	public static Map loadMap() {
		Map m = new Map();
		BotClientSingleton bc = BotClientSingleton.getInstance();

		// TODO: Load real map
		BotClientMap bcMap = BotClientMap.getDefaultMap();

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
			if (w.type == Wall.WallType.NORMAL || w.type == Wall.WallType.OPPONENT || w.type == Wall.WallType.REACTOR) {
				width = 0.25 * Config.METERS_PER_INCH;
			} else { // w.type == Wall.WallType.SILO
				width = 6 * Config.METERS_PER_INCH;
			}
			Obstacle obs = convertWallToObstacle(w, width, scaleFactor);
			m.obstacles.add(obs);
			System.out.println(obs);
			
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
