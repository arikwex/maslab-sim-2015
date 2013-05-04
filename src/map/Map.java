package map;

import java.awt.Color;
import java.awt.geom.Rectangle2D;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;

import com.sun.org.apache.xerces.internal.impl.xpath.regex.ParseException;

import core.Config.BlockColor;
import core.Config;

public class Map {
    private static Map instance;
    
    public java.awt.geom.Rectangle2D.Double worldRect = null;
    public ArrayList<Obstacle> worldBounds = null; // world rect in obstacle format.

    public ArrayList<Obstacle> obstacles;
    private ArrayList<MapBlock> blocks;
    public Robot bot;

    protected Point robotStart;
    protected Point robotGoal;

	public Point ShelterLocation;

    public LinkedList<Point> path;
	public Fiducial[] fiducials;

    // takes bot +
    Map() {
        this.bot = new Robot(0,0,0);
        this.worldRect = new Rectangle2D.Double();
        this.blocks = new ArrayList<MapBlock>();
    }
    
    public static Map getInstance() {
        if (instance == null)
            instance = new Map();
        return instance;   
    }
    
    public boolean addBlock(MapBlock b) {        
        if (!this.isOnMap(b)) {
            blocks.add(b);
            return true;
        }
        
        return false;
    }

    public boolean checkSegment(Segment seg) {
    	if (obstacles == null){
    		return false;
    	}
 //   	System.out.println("Checking Segment "+seg );
        for (Obstacle o : obstacles) {
        	//System.out.println("Checking obs "+o + " against seg " + seg);
            if (o.intersects(seg))
            {
//            	System.out.println("obs "+o + " intersects seg " + seg);

            	return false;
            }
        }
        return true;
    }

    public void nearestIntersectingSegment(Segment seg) {

    }

    public boolean isOnMap(MapBlock block) {
        for (MapBlock b : blocks) {
            BlockColor color = b.getColor();
            if (b.distance(block) < Config.minDist && color == block.getColor()) {
                return true;
            }
        }
        return false;
    }

    public MapBlock closestBlock() {
        MapBlock bestBlock = null;
        double minDist = worldRect.height*worldRect.width;

        for (MapBlock b : blocks) {
        	boolean bad = false;
            double d = b.distance(bot.pose);
            for (Obstacle obs : obstacles){
            	if (obs.naiveCSpace == null)
            		obs.computeNaiveCSpace(Config.ROBOT_RADIUS);
            	if (obs.naiveCSpace.contains(b) || b.distance(new Point(bot.pose.x,bot.pose.y))<0.2){
            		System.out.println(b.x+","+b.y+" is bad");
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
        System.out.println("bestBlock = "+bestBlock.x+", "+bestBlock.y);
        return bestBlock;
    }


    private void parsePoint(Point point, BufferedReader br, String name, int lineNumber) throws IOException,
            ParseException, NumberFormatException {

        String line = br.readLine();
        String[] tok = (line != null) ? line.split("\\s+") : null;

        if ((tok == null) || (tok.length < 2)) {
            throw new ParseException(name + " (line " + lineNumber + ")", lineNumber);
        }

        point.x = Double.parseDouble(tok[0]);
        point.y = Double.parseDouble(tok[1]);
    }

    private void parseRect(Rectangle2D.Double rect, BufferedReader br, String name, int lineNumber) throws IOException,
            ParseException, NumberFormatException {

        String line = br.readLine();
        String[] tok = (line != null) ? line.split("\\s+") : null;

        if ((tok == null) || (tok.length < 4))
            throw new ParseException(name + " (line " + lineNumber + ")", lineNumber);

        rect.x = Double.parseDouble(tok[0]);
        rect.y = Double.parseDouble(tok[1]);
        rect.width = Double.parseDouble(tok[2]);
        rect.height = Double.parseDouble(tok[3]);
    }

    private Obstacle parseObs(BufferedReader br, String name, int lineNumber) throws IOException, ParseException,
            NumberFormatException {

        String line = br.readLine();

        if (line == null)
            return null;

        String[] tok = line.trim().split("\\s+");

        if (tok.length == 0)
            return null;

        if (tok.length % 2 != 0)
            throw new ParseException(name + " (line " + lineNumber + ")", lineNumber);

        Obstacle poly = new Obstacle();

        for (int i = 0; i < tok.length / 2; i++)
            poly.addVertex(new Point(Double.parseDouble(tok[2 * i]), Double.parseDouble(tok[2 * i + 1])));

        poly.close();

        return poly;
    }

    protected void parse(File mapFile) throws IOException, ParseException {
        int lineNumber = 1;
        try {

            BufferedReader br = new BufferedReader(new FileReader(mapFile));

            parsePoint(robotStart, br, "robot start", lineNumber++);
            parsePoint(robotGoal, br, "robot goal", lineNumber++);
            parseRect(worldRect, br, "world rect", lineNumber++);

            for (int obstacleNumber = 0;; obstacleNumber++) {

                Obstacle obs = parseObs(br, "obstacle " + obstacleNumber, lineNumber++);
                if (obs != null) {
                    obs.color = Color.blue;
                    obstacles.add(obs);
                } else
                    break;
            }

        } catch (NumberFormatException e) {
            throw new ParseException("malformed number on line " + lineNumber, lineNumber);
        }
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
		this.path = m.path;
	}
}
