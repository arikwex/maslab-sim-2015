package rrt;

import hardware.Hardware;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import control.Control;
import control.ControlMode;
import logging.Log;
import map.Map;
import map.Pose;
import map.geom.Obstacle;
import map.geom.Point;
import map.geom.Polygon;
import map.geom.Segment;
import state_machine.StateMachine;
import utils.Utils;
import core.Config;
import core.StateEstimator;

public class PathPlanning extends Thread {
	private static PathPlanning instance;

	private StateMachine sm;
	private StateEstimator se;
	private Map map;
	private volatile boolean running = true;
	
	public HashMap<String, Long> timeMap = new HashMap<String, Long>();
	public ArrayList<Segment> rrtEdges;
	public LinkedList<Point> path;
	public Point nextWaypoint;
	public Point goal;

	public PathPlanning() {
		this.sm = StateMachine.getInstance();
		this.se = StateEstimator.getInstance();
		this.map = Map.getInstance();
	}

	public static PathPlanning getInstance() {
		if (instance == null)
			instance = new PathPlanning();
		return instance;
	}
	
	public void run() {
		while (running) {
			step();
			try {
				Thread.sleep(100);
			} catch (Exception e) {
			}
		}
	}
	
	public void end() {
		running = false;
	}

	public void step() {
		if (Control.getInstance().getMode() != ControlMode.TRAVEL_PLAN) {
			return;
		}
		
		Pose curLoc = map.bot.pose;
		Point newGoal = sm.getGoal();
		
		if (newGoal == null) {
			nextWaypoint = null;
			return;
		}
		
		Control.getInstance().linkTarget(getNextWaypoint());
		
		//Log.log("New: " + newGoal + " Old: " + goal);
		
		if (nextWaypoint == null || goal == null || newGoal.distance(goal) > .05) {
			Log.log("MOVE GOAL");
			goal = newGoal;
			findPath(newGoal);
			//Log.log("NEW PATH?");
			nextWaypoint = path.getFirst();
		}
		
        Polygon rotBot = map.bot.getRotated(curLoc.theta);

		for (Obstacle o : map.getObstacles()) {
		    if (o.getPolyCSpace(rotBot).contains(curLoc)) {
		    	Log.log("WTF? Inside Obsticle?");
		        Log.log("" + map.checkSegment(new Segment(curLoc, nextWaypoint), curLoc.theta));
		    }
		}
		
		if (!map.checkSegment(new Segment(curLoc, nextWaypoint), curLoc.theta)) {
			Log.log("BROKEN PATH");
			
			map.checkSegment(new Segment(curLoc, nextWaypoint), curLoc.theta);
			Log.log("Cur: " + curLoc + " next: " + nextWaypoint);
			
			findPath(newGoal);
			nextWaypoint = path.getFirst();
		}

		if (path.size() > 1) {
		    if (map.checkSegment(new Segment(curLoc, path.get(1)), curLoc.theta)) {
		        path.removeFirst();
		        nextWaypoint = path.getFirst();
		    }
		}
		
		
		if (curLoc.distance(nextWaypoint) < .03) {
		    
	        double thetaErr = Utils.thetaDiff(curLoc.theta, curLoc.angleTo(nextWaypoint));
		    
	        Log.log("THETA ERROR!!! " + thetaErr);
	        if (Math.abs(thetaErr) < Math.PI/8) {
		    	if (path.size() > 1) {
    				path.removeFirst();
    				nextWaypoint = path.getFirst();
		    	} else {
		    		Hardware hw = Hardware.getInstance();
		    		hw.motorLeft.setSpeed(0);
		    		hw.motorRight.setSpeed(0);
		    		
		    	    findPath(newGoal);
		            nextWaypoint = path.getFirst();
		    	}
	        }
		}

		// stop when we arrive at goal
		if (curLoc.distance(newGoal) < .05) {
			nextWaypoint = curLoc;
			path.clear();
			return;
		}
		
		/*
		String acc = "";
		for (Point p : path)
			acc += (p + ", ");
		Log.log("Path: " + acc);
		*/
	}

	public Point getNextWaypoint() {
		return nextWaypoint;
	}
	
	public void findPath(Point goal) {
		Hardware hw = Hardware.getInstance();
		hw.motorLeft.setSpeed(0);
		hw.motorRight.setSpeed(0);
		path = RRTSearch(map.bot.pose, goal);
	}
	
	public LinkedList<Point> RRTSearch(Pose start, Point goal) {
		return RRTSearch(start, goal, Integer.MAX_VALUE);
	}
	
	public LinkedList<Point> RRTSearch(Pose start, Point goal, int maxNodes) {
		LinkedList<Point> path = new LinkedList<Point>();
	    
		rrtEdges = new ArrayList<Segment>();

		TreeNode root = new TreeNode(start);
		Tree rrt = new Tree(root);

		TreeNode closest, newNode, goalNode;
		
		// try to shortcut to goal
		if (map.checkSegment(new Segment(start, goal), start.theta)) {
			path.add(goal);
			return path;
		}

		// can use a counter to reset?
		while (true) {
			if (rrt.nodes.size() > maxNodes)
				return null;
			
			Point p = map.randomPoint();
			if (Math.random() < Config.RRT_GOAL_BIAS)
			    p = new Point(goal); 
			
		    closest = root;
			for (TreeNode node : rrt.nodes) { // faster search? Quad tree?
				if (node.loc.distance(p) < closest.loc.distance(p)) {
					closest = node;
				}
			}

			Segment seg = new Segment(closest.loc, p);
			seg = seg.trim(Config.MAXLENGTH);

			double startAngle;
			if (closest == root)
				startAngle = start.theta;
			else
				startAngle = closest.parent.loc.angleTo(closest.loc);
			
			if (!map.checkSegment(seg, startAngle)) {
				continue;
			}
			
			newNode = new TreeNode(seg.end);
			closest.addChild(newNode);

            rrtEdges.add(seg);

			if (p.distance(goal) < .1) {
			    goalNode = new TreeNode(goal);
	            newNode.addChild(goalNode);
			    break;
			}
		}

		TreeNode curNode = goalNode;
		Boolean pathcomplete = false;
		while (!pathcomplete) {
			path.add(0, curNode.loc);
			if (curNode == rrt.root) {
				pathcomplete = true;
				break;
			}
			curNode = curNode.parent;
		}
		
		return path;
	}
	
	public LinkedList<Point> trimPath(LinkedList<Point> initialPath) {
		LinkedList<Point> brokenPath = breakPath(initialPath);
		
		int trimPasses = 1000;
		for (int i = 0; i<trimPasses; i++) {
			
		}
		
		return brokenPath;
	}
	
	public static double getTotalLength(LinkedList<Point> path) {
		double totalLength = 0;
		Point prev = null;
		if (path == null) {
			return 0;
		}
		for (Point curr : path) {
			if (prev != null)
				totalLength += prev.distance(curr);
			prev = curr;
		}
		return totalLength;
	}
	
	public LinkedList<Point> breakPath(LinkedList<Point> initialPath) {
		double totalLength = getTotalLength(initialPath);
		double avgLength = 0.1;
		ArrayList<Double> breakDistances = new ArrayList<Double>();
		for (int i = 0; i<totalLength/avgLength; i++)
			breakDistances.add(Math.random()*totalLength);
		Collections.sort(breakDistances);
		
		LinkedList<Point> brokenPath = new LinkedList<Point>();
		brokenPath.add(initialPath.get(0));
		double accumDist = 0;

		Iterator<Point> pathIter = initialPath.iterator();
		Segment currSeg = new Segment(pathIter.next(), pathIter.next());
		for (double d : breakDistances) {
			while (accumDist + currSeg.length() < d) {
				accumDist += currSeg.length();
				currSeg = new Segment(currSeg.end, pathIter.next());
			}
			brokenPath.add(currSeg.trim(d-accumDist).end);
		}
			
		return brokenPath;
	}
	
	public long estimateTravelTime(Point a, Point b) {
		String hash = a.toString() + b.toString();
		if (timeMap.containsKey(hash)) {
			return timeMap.get(hash);
		} else {
			LinkedList<Point> path = RRTSearch(new Pose(a.x, a.y, 0), new Pose(b.x, b.y, 0), 1000);
			double totalLength = PathPlanning.getTotalLength(path);
			long value = (long)(totalLength * 5 * 1000);
			timeMap.put(hash, value);
			return value;
		}
	}
}
