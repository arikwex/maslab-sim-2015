package rrt;

import hardware.Hardware;

import java.util.ArrayList;
import java.util.LinkedList;

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
		    	Log.log("WTF?");
		        Log.log("" + map.checkSegment(new Segment(curLoc, nextWaypoint), curLoc.theta));
		        //while(true);
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
		path = RRTSearch(goal, true);
	}

	public LinkedList<Point> RRTSearch(Point goal, boolean allowRandom) {

		rrtEdges = new ArrayList<Segment>();
		LinkedList<Point> path = new LinkedList<Point>();
	    
		Point start = new Point(map.bot.pose.x, map.bot.pose.y);
		TreeNode root = new TreeNode(start);
		Tree rrt = new Tree(root);

		Point p;
		TreeNode closest, newNode, goalNode;
		Segment seg;
		if (map.checkSegment(new Segment(start, goal), map.bot.pose.theta)) {
			path.add(goal);
			return path;
		}

		long count = 0;
		while (true) {
		    count++;
		    if (count % 5000 == 0) {
		        if (rrt.nodes.size() > 5000 || count % 50000 == 0) {
		            rrtEdges.clear();
		            start = new Point(map.bot.pose.x, map.bot.pose.y);
		            root = new TreeNode(start);
		            rrt = new Tree(root);
		        }
		        if (count > 100000 && rrt.nodes.size() > 5) {
		        	if (allowRandom) {
		        		Log.log("FUCK IT GO TO RANDOM NODE");
			            
		                goalNode = rrt.nodes.get((int)(Math.random()*rrt.nodes.size()));
		                break;		        
		        	}
		        	else {
		        		return null;
		        	}
		        }
		        Log.getInstance().updatePose();
		    }
		    
			p = map.randomPoint();
			if (Math.random() < Config.RRT_GOAL_BIAS)
			    p = new Point(map.bot.pose); 
			
		    closest = root;
			
			for (TreeNode node : rrt.nodes) {
				if (node.loc.distance(p) < closest.loc.distance(p)) {
					closest = node;
				}
			}

			seg = new Segment(closest.loc, p);
			seg = seg.trim(Config.MAXLENGTH);

			double startAngle;
			if (closest == root)
				startAngle = map.bot.pose.theta;
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
}
