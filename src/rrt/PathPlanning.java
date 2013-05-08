package rrt;

import java.util.ArrayList;
import java.util.LinkedList;

import core.Config;
import core.StateEstimator;

import logging.Log;
import map.Map;
import map.Obstacle;
import map.Point;
import map.Polygon;
import map.Pose;
import map.Segment;
import state_machine.StateMachine;
import uORCInterface.OrcController;
import utils.Utils;

public class PathPlanning {
	private static PathPlanning instance;

	private StateMachine sm;
	private StateEstimator se;
	private Map map;

	public LinkedList<Point> path;
	public Point nextWaypoint;
	public Point goal;
	
	public ArrayList<Segment> rrtEdges = new ArrayList<Segment>();

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

	public void step() {
		Pose curLoc = map.bot.pose;
		Point newGoal = sm.getGoal();
		
		if (newGoal == null) {
			nextWaypoint = curLoc;
			return;
		}
		
		System.out.println("New: " + newGoal + " Old: " + goal);
		
		if (goal == null || newGoal.distance(goal) > .05) {
			System.out.println("MOVE GOAL");
			goal = newGoal;
			findPath(newGoal);
			System.out.println("NEW PATH?");
			nextWaypoint = path.getFirst();
		}
		
        Polygon rotBot = map.bot.getRotated(curLoc.theta);

		for (Obstacle o : map.getObstacles()) {
		    if (o.getPolyCSpace(rotBot).contains(curLoc)) {
		        System.out.println("WTF?");
		        System.out.println(map.checkSegment(new Segment(curLoc, nextWaypoint), curLoc.theta));
		        //while(true);
		    }
		}
		
		if (!map.checkSegment(new Segment(curLoc, nextWaypoint), curLoc.theta)) {
			System.out.println("BROKEN PATH");
			
			map.checkSegment(new Segment(curLoc, nextWaypoint), curLoc.theta);
			System.out.println("Cur: " + curLoc + " next: " + nextWaypoint);
			
			findPath(newGoal);
			nextWaypoint = path.getFirst();
		}

		/*
		// try to shortcut paths
		for (int i = path.size()-1; i >= 0; i--) {
			if (map.checkSegment(new Segment(curLoc, path.get(i)), curLoc.theta)) {
				System.out.println("SHORTCUT AT " + i);

				nextWaypoint = path.get(i);
				i--;
				for (; i>= 0; i--)
					path.remove(i);
			}
		}*/

		if (path.size() > 1) {
		    if (map.checkSegment(new Segment(curLoc, path.get(1)), curLoc.theta)) {
		        path.removeFirst();
		        nextWaypoint = path.getFirst();
		    }
		}
		
		
		if (curLoc.distance(nextWaypoint) < .03) {
		    
	        double thetaErr = Utils.thetaDiff(curLoc.theta, curLoc.angleTo(nextWaypoint));
		    
	        System.out.println("THETA ERROR!!! " + thetaErr);
	        if (Math.abs(thetaErr) < Math.PI/8) {
		    	if (path.size() > 1) {
    				path.removeFirst();
    				nextWaypoint = path.getFirst();
		    	} else {
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
		
		System.out.print("Path: ");
		for (Point p : path)
			System.out.print(p + ", ");
		System.out.println("");
	}

	public Point getNextWaypoint() {
		return nextWaypoint;
	}

	public void findPath(Point goal) {
	    rrtEdges.clear();
	    
		Point start = new Point(map.bot.pose.x, map.bot.pose.y);
		TreeNode root = new TreeNode(start);
		Tree rrt = new Tree(root);

		Point p;
		TreeNode closest, newNode, goalNode;
		Segment seg;
		if (map.checkSegment(new Segment(start, goal), map.bot.pose.theta)) {
			path = new LinkedList<Point>();
			path.add(goal);
			return;
		}
		
		OrcController orc = new OrcController(new int[] {0,1});
		orc.motorSet(0, 0);
		orc.motorSet(1, 0);

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
		            System.out.println("FUCK IT GO TO RANDOM NODE");
		            
	                goalNode = rrt.nodes.get((int)(Math.random()*rrt.nodes.size()));
	                break;		        
		        }
		        Log.getInstance().updatePose();
		        System.out.println(count + " nodes " + rrt.nodes.size());
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
		path = new LinkedList<Point>();
		while (!pathcomplete) {
			path.add(0, curNode.loc);
			if (curNode == rrt.root) {
				pathcomplete = true;
				break;
			}
			curNode = curNode.parent;
		}
	}
}
