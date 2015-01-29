package map;

import java.awt.geom.Rectangle2D;
import java.util.ArrayList;

import map.elements.HomeBase;
import map.elements.Platform;
import map.elements.Stack;
import map.elements.Wall;
import map.geom.Obstacle;
import map.geom.Point;
import map.geom.Robot;
import map.geom.Segment;

public class Map {
    private static Map instance;
    
    public java.awt.geom.Rectangle2D.Double worldRect = null;
    
    private ArrayList<Wall> walls;
    private ArrayList<Platform> platforms;
    private ArrayList<Stack> stacks;
    private HomeBase homeBase;
    
    public Robot bot;
    protected Point robotGoal;

    public Map() {
        this.bot = new Robot(0,0,0);
        this.worldRect = new Rectangle2D.Double();
        this.clear();
    }
    
    public static Map getInstance() {
        if (instance == null)
            instance = new Map();
        return instance;   
    }
    
    public void clear() {
    	worldRect = null;
    	walls = new ArrayList<Wall>();
    	platforms = new ArrayList<Platform>();
    	stacks = new ArrayList<Stack>();
    	homeBase = null;
    }
    
    /* WORLD RECTANGLE */
    
    public void setWorldRect(java.awt.geom.Rectangle2D.Double worldRect) {
    	this.worldRect = worldRect;
    }
    
    public java.awt.geom.Rectangle2D.Double getWorldRect() {
    	return worldRect;
    }
    
    /* OBSTACLES */
    
    public synchronized void addStack(Stack obs) {
		obs.generateObstacleData();
        stacks.add(obs);
    }

	public synchronized void removeStack(Stack obs) {
		stacks.remove(obs);
	}
	
	public synchronized ArrayList<Stack> getStacks() {
		return stacks;
	}
	
	/* PLATFORMS */
    
    public synchronized void addPlatform(Platform obs) {        
        platforms.add(obs);
    }

	public synchronized void removePlatform(Platform obs) {
		platforms.remove(obs);
	}
	
	public synchronized ArrayList<Platform> getPlatforms() {
		return platforms;
	}
	
	/* WALLS */
    
    public synchronized void addWall(Wall obs) {        
        walls.add(obs);
    }

	public synchronized void removeWall(Wall obs) {
		walls.remove(obs);
	}
	
	public synchronized ArrayList<Wall> getWalls() {
		return walls;
	}
	
	/* HOME BASE */
	
	public synchronized HomeBase getHomeBase() {
		return homeBase;
	}
	
	public synchronized void setHomeBase(HomeBase hb) {
		homeBase = hb;
	}
	
	public synchronized ArrayList<Obstacle> getObstacles() {
		ArrayList<Obstacle> obstacles = new ArrayList<Obstacle>();
		for (Wall w : walls) {
			obstacles.add(w);
		}
		for (Platform p : platforms) {
			obstacles.add(p);
		}
		for (Stack s : stacks) {
			obstacles.add(s);
		}
		return obstacles;
	}

    public boolean checkSegment(Segment seg, double theta) {
    	ArrayList<Obstacle> obstacles = this.getObstacles();
    	if (obstacles == null){
    		return false;
    	}
    	
        for (Obstacle o : obstacles) {
	        if (o.intersects(seg, theta)) {
	        	return false;
	        }
        }
        
        return true;
    }
    
    public Point randomPoint() {
    	double x = Math.random()*(worldRect.width) + worldRect.x;
        double y = Math.random()*(worldRect.height) + worldRect.y;
        return new Point(x,y);
    }
}
