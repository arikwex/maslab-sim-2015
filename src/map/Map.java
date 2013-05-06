package map;

import java.awt.geom.Rectangle2D;
import java.util.ArrayList;

import core.Block;
import core.Config.BlockColor;
import core.Config;
import core.StateEstimator;
import data_collection.DataCollection;

public class Map {
    private static Map instance;
    
    public java.awt.geom.Rectangle2D.Double worldRect = null;
    private ArrayList<Obstacle> worldBounds = null; // world rect in obstacle format.

    public ArrayList<Obstacle> obstacles;
    private ArrayList<MapBlock> blocks;
    public Robot bot;

    protected Point robotStart;
    protected Point robotGoal;

	public Point ShelterLocation;

	public Fiducial[] fiducials;

	private ArrayList<MapBlock> blocksIShouldSee;

    Map() {
        this.bot = new Robot(0,0,0);
        this.worldRect = new Rectangle2D.Double();
        this.blocks = new ArrayList<MapBlock>();
    }
    
    public ArrayList<Obstacle> getObstacles() {
        return obstacles;
    }
    
    public static Map getInstance() {
        if (instance == null)
            instance = new Map();
        return instance;   
    }
    
    public boolean addBlock(MapBlock b) {        
        if (this.isOnMap(b)== null) {
            blocks.add(b);
            return true;
        }
        
        return false;
    }

    public boolean checkSegment(Segment seg, double theta) {
    	if (obstacles == null){
    		return false;
    	}
    	
        for (Obstacle o : obstacles)
            if (o.intersects(seg, theta))
            	return false;
        
        return true;
    }

    public void nearestIntersectingSegment(Segment seg) {

    }

    public MapBlock isOnMap(MapBlock block) {
    	MapBlock closest = null;
        for (MapBlock b : blocks) {
            BlockColor color = b.getColor();
            if (b.distance(block) < Config.minDist && color == block.getColor()) {
                if (closest == null || block.distance(b)<block.distance(closest)){
                	closest = b;
                }
            }
        }
        return closest;
    }

    public MapBlock closestBlock() {
    	return closestBlock(bot.pose);
    }
    
    public MapBlock closestBlock(Point from) {
        MapBlock bestBlock = null;
        double minDist = Double.POSITIVE_INFINITY;

        for (MapBlock b : blocks) {
        	boolean bad = false;
            double d = b.distance(from);
            for (Obstacle obs : obstacles){
            	if (obs.getMinCSpace().contains(b)){
            		bad = true;
            		break;
            	}
            }
            if (bad)
            	continue;
            if (d < minDist) {
                minDist = d;
                bestBlock = b;
            }
        }

        return bestBlock;
    }
    
    public Point randomPoint() {
    	double x = Math.random()*(worldRect.width) + worldRect.x;
        double y = Math.random()*(worldRect.height) + worldRect.y;
        return new Point(x,y);
    }

	public void throwAwayBadBlocks() {
		for (int i = blocks.size()-1; i >= 0; i--){
			if ((blocks.get(i).x < worldRect.x ||blocks.get(i).x > (worldRect.x+worldRect.width))||
					(blocks.get(i).y < worldRect.y ||blocks.get(i).y > (worldRect.y+worldRect.height))){
				blocks.remove(i);
				continue;
			}
			for (Obstacle obs : obstacles){
				if (obs.contains(blocks.get(i))){
					blocks.remove(i);
					break;
				}
			}
		}		
	}
	
	public void removeBlock(MapBlock b) {
		blocks.remove(b);
	}

	public synchronized ArrayList<MapBlock> getBlocks() {
		return blocks;
	}

	public synchronized void setBlocks(ArrayList<MapBlock> blocks) {
		this.blocks = blocks;
	}

	public void setMap(Map m){
		this.blocks = m.blocks;
		this.obstacles = m.obstacles;
		this.bot = m.bot;
		this.fiducials = m.fiducials;
		this.worldBounds = m.worldBounds;
		this.worldRect = m.worldRect;
		this.robotGoal = m.robotGoal;
		this.robotStart = m.robotStart;
		this.ShelterLocation = m.ShelterLocation;
	}

	public void update() {
		
		for (Block b : DataCollection.getInstance().getBlocks()) {
			MapBlock block = new MapBlock(b);
			MapBlock closest = isOnMap(block);
			if (closest != null){
				closest.setLocation(block);
			}
			else{
				boolean duplicate = false;
				for (int i = blocks.size()-1; i>=0;i--){
					 
					if (blocks.get(i)==block){
						duplicate = true;
						break;
					}
				}
				if (!duplicate)
					addBlock(block);
			}
		}
		blocksIShouldSee = getBlocksIShouldSee();
		for (MapBlock mb : blocksIShouldSee){
			Block found = null;
			for (Block b : DataCollection.getInstance().getBlocks()){
				if (isOnMap(new MapBlock(b)) == mb &&
						(found == null || mb.distance(b)<mb.distance(found))){
					found = b;
				}
			}
			if (found == null){
				blocks.remove(mb);
			}
			else {
				mb.setLocation(found);
			}
		}
		
		
	}

	private ArrayList<MapBlock> getBlocksIShouldSee() {
		// TODO Auto-generated method stub
		return null;
	}
}
