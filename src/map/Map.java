package map;

import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;

import rrt.PathPlanning;
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
    
    private List<Wall> walls;
    private List<Platform> platforms;
    private List<Stack> stacks;
    private List<Pose> locations;
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
	
	public synchronized List<Stack> getStacks() {
		return stacks;
	}
	
	/* PLATFORMS */
    
    public synchronized void addPlatform(Platform obs) {        
        platforms.add(obs);
    }

	public synchronized void removePlatform(Platform obs) {
		platforms.remove(obs);
	}
	
	public synchronized List<Platform> getPlatforms() {
		return platforms;
	}
	
	/* WALLS */
    
    public synchronized void addWall(Wall obs) {        
        walls.add(obs);
    }

	public synchronized void removeWall(Wall obs) {
		walls.remove(obs);
	}
	
	public synchronized List<Wall> getWalls() {
		return walls;
	}
	
	/* HOME BASE */
	
	public synchronized HomeBase getHomeBase() {
		return homeBase;
	}
	
	public synchronized void setHomeBase(HomeBase hb) {
		homeBase = hb;
	}
	
	/* LOCATIONS */
	
	public synchronized void setLocations(List<Pose> locations) {
		this.locations = locations;
	}
	
	public synchronized List<Pose> getLocations() {
		return this.locations;
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
    
    public ArrayList<Point> getWallCollisions(Segment seg) {
    	ArrayList<Point> collisions = new ArrayList<Point>();

    	for (Wall w : walls) {
			Segment wallSeg = new Segment(w.start, w.end);
			Point collision = wallSeg.intersetionPoint(seg);
			
			if (collision != null) {
				collisions.add(collision);
			}
		}
    	
    	return collisions;
    }

    public Segment getBestApproach(Point target) {
    	return getBestApproach(target, 1.5, false);
    }
    
    public Segment getBestApproach(Point target, double max, boolean useAvg) {
    	int samples = 64;
    	double sampleWidth = Math.PI*2/samples;
    	double scan = Math.PI/3;
    	int scanWidth = (int)(scan/sampleWidth);
    	ArrayList<Segment> segments = new ArrayList<Segment>();
    	
    	for (int i = 0; i < samples; i++) {
    		Point ray = new Point(max,0).getRotated(i * sampleWidth);
    		Segment seg = new Segment(target, target.add(ray));
    		
    		for (Wall w : walls) {
    			Segment wallSeg = new Segment(w.start, w.end);
    			Point collision = wallSeg.intersetionPoint(seg);
    			if (collision != null && seg.start.distance(collision) < seg.length()) {
    				seg = new Segment(seg.start, collision);
    			}
    		}
    		
    		segments.add(seg);
    	}
    	
    	double maxScore = 0;
    	Segment maxSegment = segments.get(0);
    	ArrayList<Segment> newSegments = new ArrayList<Segment>();
    	for (int i = 0; i<segments.size(); i++) {
    		double score = 0;
    		if (useAvg)
    			score = minScore(segments, i, scanWidth);
    		else 
    			score = avgScore(segments, i, scanWidth);
    		
    		if (score > maxScore) {
    			maxScore = score;
    			maxSegment = segments.get(i);
    		}
			newSegments.add(segments.get(i).trim(score));

    	}

    	return maxSegment;
    }
    
    
    public double minScore(ArrayList<Segment> segments, int index, int scanWidth) {
    	int i = index;
    	double min = Double.POSITIVE_INFINITY;
		for (int j = i-scanWidth/2; j<i+scanWidth/2; j++) {
			Segment seg = segments.get((j+segments.size()) % segments.size());
			if (seg.length() < min) {
				min = seg.length();
			}
		}
		
		return min;
    }
    
    public double avgScore(ArrayList<Segment> segments, int index, int scanWidth) {
    	int i = index;
    	double avg = 0;
		for (int j = i-scanWidth/2; j<i+scanWidth/2; j++) {
			Segment seg = segments.get((j+segments.size()) % segments.size());
			avg += seg.length();
		}
		
		return avg/scanWidth;
    }
    
    public Segment getBestHomePose() {
    	homeBase.getPolygon().getVertices();
    	
    	double maxScore = 0;
    	Segment maxSegment = null;
    	
    	int counter = 0;
    	outer:
    	while(true) {
    		Point p = randomPoint();

    		if (homeBase.getPolygon().contains(p)) {
    			for (Obstacle o : this.getObstacles()) {
        	        if (o.getMaxCSpace().contains(p)) {
        	        	continue outer;
        	        }
                }
    			
    			
    			Segment seg = getBestApproach(p);
    			double min = seg.length();
    			
    			for (Stack s : stacks) {
    				if (s.pt.distance(p) < min) {
    					min = s.pt.distance(p);
    				}
    			}
    			
    			if (min > maxScore) {
    				maxScore = min;
    				maxSegment = seg;
    			}
    			

    			counter++;
    			if (counter > 20) {
    		    	return maxSegment;
    			}
    		}
    	}
    }
    
    public Segment getBestPlatformApproach(Platform plat) {
    	Point vector = plat.end.subtract(plat.start).scaleToMagnitude(0.3).getRotated(Math.PI/2);
    	Point midPoint = plat.start.average(plat.end);
    	
    	PathPlanning p = PathPlanning.getInstance();
    	Object result = p.RRTSearch(bot.pose, midPoint.add(vector), 1000);
    	if (result != null)
        	return new Segment(midPoint, midPoint.add(vector.scaleToMagnitude(1.5)));
    	    	
    	return new Segment(midPoint, midPoint.subtract(vector.scaleToMagnitude(1.5)));
    }
}
