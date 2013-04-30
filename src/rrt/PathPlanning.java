package rrt;

import java.util.LinkedList;

import core.StateEstimator;

import map.Map;
import map.Point;
import map.Segment;
import state_machine.StateMachine;

public class PathPlanning {
    private static PathPlanning instance;
    
    private static final int MAXLENGTH = 5;
    
	private StateMachine sm;
    private StateEstimator se;
    private Map map;
    
    LinkedList<Point> path;
    Point nextWaypoint;
    Point goal;

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
    	Point curLoc= new Point(map.bot.pose.x, map.bot.pose.y);
    	if(sm.goal != goal || nextWaypoint == null || !map.checkSegment(new Segment(curLoc, nextWaypoint))){
    		findPath();
    	}
    	for (Point p : path){
    		if (curLoc.distance(p) < curLoc.distance(nextWaypoint)){
    			nextWaypoint = p;
    		}
    	}
    }

    public Point getNextWaypoint(){
    	return nextWaypoint;
    }
    
    public void findPath() {
    	Point goal = sm.goal;
    	Point start = new Point(map.bot.pose.x, map.bot.pose.y);
    	TreeNode root = new TreeNode(start);
    	Tree rrt = new Tree(root);
    	
    	Point p;	
    	TreeNode closest, newNode, goalNode;
    	Segment seg;
    	
    	
    	while (true){
    		p = map.randomPoint();
    		closest = root;
    		for (TreeNode node : rrt.nodes){
    			if (node.loc.distance(p) < closest.loc.distance(p)){
    				closest = node;
    			}
    		}
    		
    		seg = new Segment(closest.loc, p);
    		seg = seg.trim(MAXLENGTH);
    		
    		if (!map.checkSegment(seg)){
    			continue;
    		}
    		
    		newNode = new TreeNode(goal);
    		closest.addChild(newNode);
    		closest = root;
    		for (TreeNode node : rrt.nodes){
    			if (node.loc.distance(goal) < closest.loc.distance(goal)){
    				closest = node;
    			}
    		}
    		
    		seg = new Segment(closest.loc, p);
    	
    		if (!map.checkSegment(seg) || closest.loc.distance(goal) > MAXLENGTH){
    			continue;
    		}
    		
    		goalNode = new TreeNode(goal);
    		closest.addChild(goalNode);
    		break;
    	}
    	
    	TreeNode curNode = goalNode;
    	Boolean pathcomplete = false;
    	path = new LinkedList<Point>();
    	while (!pathcomplete){
    		path.add(0, curNode.loc);
    		curNode = curNode.parent;
    	}
    	
    }
}
