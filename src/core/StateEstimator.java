package core;

import hardware.Hardware;

import java.util.ArrayList;

import map.Map;
import map.MapBlock;
import map.PointPolar;
import map.Pose;
import map.Robot;
import map.Segment;
import utils.Utils;
import vision.Vector2D;
import vision.Vision;

import comm.BotClientMap.Point;

import data_collection.DataCollection;

public class StateEstimator implements Runnable {
    private static StateEstimator instance;

    private DataCollection dc;
    private Hardware hw;

    boolean ready = false;
    public boolean[] tooClose;
    public boolean anyTooClose;

    public int numCollectedBlocks;

    public int numBlocksLeft;

    public Map map;

    boolean started;

    private StateEstimator() {
        map = Map.getInstance();
        dc = DataCollection.getInstance();
        hw = Hardware.getInstance();
        started = false;
    }

    public static StateEstimator getInstance() {
        if (instance == null)
            instance = new StateEstimator();
        return instance;
    }

    public void step() {
        updatePoseEncoder();
        correctPoseVision();
        // updateBlocks();
        // sonarCheck();

        //Log.log(this.toString());
    }

    private void updatePoseEncoder() {
        hw.updateSensorData();
        double dl = hw.encoderLeft.getDeltaAngularDistance() * Config.WHEEL_RADIUS;
        double dr = hw.encoderRight.getDeltaAngularDistance() * Config.WHEEL_RADIUS;

        if (dr == 0 && dl == 0)
            return; // we haven't moved at all

        double dTheta = (dr - dl) / Config.WHEELBASE;
        Robot bot = Map.getInstance().bot;

        double newX = bot.pose.x + (dl + dr) * Math.cos(bot.pose.theta) / 2.0;
        double newY = bot.pose.y + (dl + dr) * Math.sin(bot.pose.theta) / 2.0;
        double newTheta = bot.pose.theta + dTheta;
        
        newTheta = Utils.wrapAngle(newTheta);
        
        Pose nextPose = new Pose(newX, newY, newTheta);
        bot.pose = nextPose;

        if (map.checkSegment(new Segment(bot.pose,nextPose),bot.pose.theta))
            bot.pose = nextPose;
    }
    
    // Apply law of cosines, where angle gamma is opposite from side c
    private double lawOfCosinesTheta(double a, double b, double c) {
    	return Math.acos((c*c - a*a - b*b) / (-2*a*b));
    }
    
    // Transform a wall segment composed of a local left vector, pointing from the
    // robot to the left endpoint of the wall, and a similar local right vector,
    // to an (r,theta) line representation.
    private PointPolar getPolarLine(Vector2D left, Vector2D right) {
    	System.out.println("getPolarLine " + left.x + "," + left.y + " - " + right.x + "," + right.y);
		double oppositeMagnitude = new Vector2D(left.x - right.x, left.y - right.y).getMagnitude();
		
		// Law of cosines for both angles
		double thetaRight = lawOfCosinesTheta(oppositeMagnitude, right.getMagnitude(), left.getMagnitude());
		double thetaLeft = lawOfCosinesTheta(oppositeMagnitude, left.getMagnitude(), right.getMagnitude());
		double r, theta;
		if (thetaRight < Math.PI/2) {
			r = Math.sin(thetaRight)*right.getMagnitude();
		}
		else if (thetaLeft < Math.PI/2) {
			r = Math.sin(thetaLeft)*left.getMagnitude();
		}
		else { // Both lines are parallel
			r = 0;
		}
		
		// Left is past our perpendicular
		if (thetaLeft < Math.PI/2) {
			theta = Math.acos(left.y/left.getMagnitude()) - Math.acos(r/left.getMagnitude());
		}
		else { // Left is before our perpendicular
			theta = Math.acos(left.y/left.getMagnitude()) + Math.acos(r/left.getMagnitude());
		}
		
		System.out.println("getPolarLine " + r + "," + theta);
		return new PointPolar(r, theta);
    }
    
    private void correctPoseVision() {
    	// Get all vision (local) and map (global) walls
    	// Vision uses bot pointing in positive y direction as reference
    	Vision v = Vision.getInstance();
    	Map m = Map.getInstance();
    	ArrayList<vision.Wall> visWalls = v.getWalls();
    	ArrayList<comm.BotClientMap.Wall> mapWalls = m.walls;
    	ArrayList<comm.BotClientMap.Wall> mapWallsLocal = new ArrayList<comm.BotClientMap.Wall>(); 
    	ArrayList<PointPolar> visLines = new ArrayList<PointPolar>();
    	ArrayList<PointPolar> mapLines = new ArrayList<PointPolar>();
    	// Bot position
    	double rx = m.bot.pose.x;
    	double ry = m.bot.pose.y;
    	// Bot pointing unit vector
    	Vector2D unitPointing = new Vector2D(Math.cos(m.bot.pose.theta), Math.sin(m.bot.pose.theta));
    	Vector2D unitPerp = unitPointing.perp();
    	
    	// Transform all map (global) walls to local space, then transform to (r,theta) lines
    	for (comm.BotClientMap.Wall w : mapWalls) {
    		Vector2D start = new Vector2D(w.start.x - rx, w.start.y - ry);
    		double startY = start.dot(unitPointing);
    		double startX = start.dot(unitPerp);
    		start = new Vector2D(startX, startY);
    		Vector2D end = new Vector2D(w.end.x - rx, w.end.y - ry);
    		double endY = end.dot(unitPointing);
    		double endX = end.dot(unitPerp);
    		end = new Vector2D(endX, endY);
    		
    		mapWallsLocal.add(new comm.BotClientMap.Wall(new Point(start.x, start.y), new Point(end.x, end.y), comm.BotClientMap.Wall.WallType.NORMAL));
    		mapLines.add(getPolarLine(start, end));
    	}
    	
    	// Transform all vision (local) walls to (r,theta) lines
    	for (vision.Wall w : visWalls) {
    		Vector2D left = new Vector2D(w.left.x, w.left.y);
    		Vector2D right = new Vector2D(w.right.x, w.right.y);
    		visLines.add(getPolarLine(left, right));
    	}
    	
    	
    	System.out.println("Maplines: ");
    	for (int i = 0; i < mapLines.size(); i++) {
    		PointPolar pp = mapLines.get(i);
    		System.out.print("\t" + pp);
    		comm.BotClientMap.Wall w = mapWalls.get(i);
    		comm.BotClientMap.Wall lw = mapWallsLocal.get(i);
    		System.out.println(String.format(" %f %f %f %f", w.start.x, w.start.y, w.end.x, w.end.y));
    		System.out.println(String.format("\t\t%f %f %f %f", lw.start.x, lw.start.y, lw.end.x, lw.end.y));
    	}
    	System.out.println("Vislines: ");
    	for (PointPolar pp : visLines) {
    		System.out.println("\t" + pp);
    	}
    }

    public void updateBlocks() {

        MapBlock tempBlock;
        // for (Block b : dc.getBlocks()) {
        ArrayList<Block> blocks = dc.getBlocks();
        for (int b = blocks.size() - 1; b >= 0; b--) {
            tempBlock = new MapBlock(Map.getInstance().bot.getAbsolute(blocks.get(b).relX, blocks.get(b).relY),
                    blocks.get(b).color);

            map.addBlock(tempBlock);
        }
    }

    /*
    public void sonarCheck() {
        anyTooClose = false;
        if (tooClose == null)
            return;
        for (int i = 0; i < tooClose.length; i++) {
            tooClose[i] = (dc.getSonars().get(i).meas < Config.TOOCLOSE);
            if (tooClose[i])
                anyTooClose = true;
        }
    }
    */

    // returns zero for no block, 1 for single block 2 for double block;
    public int getCaptureStatus() {
        // TODO: Update this based on state?
        return 0;
    }

    public MapBlock getClosestBlock() {
        return map.closestBlock();
    }

    public String toString() {
        return map.bot.pose.toString();
    }

    @Override
    public void run() {
        while (true) {
            step();
        }

    }
    
    public static void main(String [] args) {
    	Vector2D left = new Vector2D(0.558, 0.0);
    	Vector2D right = new Vector2D(0.558, 0.558);
    	PointPolar line = StateEstimator.getInstance().getPolarLine(left, right);
    	System.out.println(line);
    }
}
