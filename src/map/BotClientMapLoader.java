package map;

import java.awt.geom.Rectangle2D;
import java.util.ArrayList;

import logging.Log;

import rrt.PathPlanning;

import comm.BotClientMap;
import comm.BotClientMap.Wall;

import core.BotClientSingleton;
import core.Config;

public class BotClientMapLoader {
	private static final String MAP_STRING = "22.00:3.00,2.00,3.14:0.00,0.00,0.00,2.00,N:0.00,2.00,1.00,3.00,N:1.00,3.00,3.00,3.00,N:3.00,3.00,4.00,3.00,R:4.00,3.00,4.00,1.00,N:4.00,1.00,2.00,1.00,N:2.00,1.00,1.00,0.00,N:1.00,0.00,0.00,0.00,R:2,1,1.2,1.8,N";
	//private static final String MAP_STRING = "22.00:3.00,1.00,1.57:2.00,2.00,2.00,4.00,N:4.00,4.00,2.00,4.00,N:4.00,4.00,1.00,7.00,N:1.00,7.00,3.00,7.00,N:3.00,7.00,6.00,7.00,N:6.00,7.00,6.00,5.00,N:6.00,5.00,6.00,3.00,N:6.00,3.00,8.00,3.00,N:8.00,3.00,8.00,9.00,N:8.00,9.00,2.00,9.00,N:2.00,9.00,2.00,11.00,N:2.00,11.00,4.00,13.00,N:4.00,13.00,6.00,11.00,N:6.00,11.00,10.00,11.00,N:10.00,11.00,10.00,1.00,N:10.00,1.00,5.00,0.00,N:5.00,0.00,2.00,0.00,N:2.00,0.00,2.00,2.00,N:";
	public static Map loadMap() {
		Map m = new Map();
		BotClientSingleton bc = BotClientSingleton.getInstance();
		// TODO: Load real map
		BotClientMap bcMap = new BotClientMap();
		bcMap.load(MAP_STRING);

		final double scaleFactor = Config.METERS_PER_INCH * bcMap.gridSize;

		double minX = Double.POSITIVE_INFINITY,
				maxX = Double.NEGATIVE_INFINITY,
				minY = Double.POSITIVE_INFINITY,
				maxY = Double.NEGATIVE_INFINITY;
		System.out.println("Loading obstacles");
		
		// Build walls ArrayList (copy with scaling)
		m.walls = new ArrayList<Wall>();
		for (Wall w : bcMap.walls) {
			Wall scaledWall = new Wall(
					new BotClientMap.Point(w.start.x*scaleFactor, w.start.y*scaleFactor),
					new BotClientMap.Point(w.end.x*scaleFactor, w.end.y*scaleFactor),
					Wall.WallType.NORMAL);
			m.walls.add(scaledWall);
		}
		
		m.obstacles = new ArrayList<Obstacle>();
		m.reactors = new ArrayList<Reactor>();
		for (int i = 0; i < bcMap.walls.size(); i++) {
			Wall w = bcMap.walls.get(i);
			double width;
			if (w.type == Wall.WallType.NORMAL || w.type == Wall.WallType.OPPONENT) {
				width = 0.25 * Config.METERS_PER_INCH;
			}
			else if (w.type == Wall.WallType.REACTOR) {
				width = 0.25 * Config.METERS_PER_INCH;
				Reactor r = new Reactor();
				r.mid = new Point(scaleFactor*(w.start.x+w.end.x)/2.0, scaleFactor*(w.start.y+w.end.y)/2.0);
				// Set initial norm vectors for reactors
				double normX = -(w.end.y - w.start.y);
				double normY = (w.end.x - w.start.x);
				double uNormX = normX / Math.sqrt(normX*normX + normY*normY);
				double uNormY = normY / Math.sqrt(normX*normX + normY*normY);
				r.nx = uNormX;
				r.ny = uNormY;
				m.reactors.add(r);
				System.out.println("Added reactor: " + r.mid);
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
		
		m.worldRect = new Rectangle2D.Double(minX * scaleFactor, minY * scaleFactor, maxX * scaleFactor, maxY * scaleFactor);
		System.out.println("World rect: " + m.worldRect);

		return m;
	}
	
	public static void setReactorNormVectors(Map m) {
		final double reactorSpacing = 0.2;
		
		// Set norm vector signs for all reactors to point inwards (i.e. in the direction we can reach)
		for (Reactor r : m.reactors) {
			// Try finding a path to the negative side of the reactor
			if (PathPlanning.getInstance().RRTSearch(
					new Point(r.mid.x - r.nx*reactorSpacing, r.mid.y - r.ny*reactorSpacing), false) != null) {
				r.nx *= -1;
				r.ny *= -1;
			}
			else if (PathPlanning.getInstance().RRTSearch(
					new Point(r.mid.x + r.nx*reactorSpacing, r.mid.y + r.ny*reactorSpacing), false) != null) {
				// No change
			}
			else {
				// Don't fail, just log and guess positive direction
				Log.log("Failed to find path to either side of reactor: " + r.mid.x + "," + r.mid.y);
			}
		}
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
