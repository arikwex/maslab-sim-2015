package core;

import map.Map;
import map.Point;
import map.Segment;
import state_machine.StateMachine;
import utils.Tree;
import utils.TreeNode;

public class PathPlanning {
    private static final int MAXLENGTH = 5;
	StateMachine sm;
    StateEstimator se;

    public PathPlanning(StateMachine sm, StateEstimator se) {
        this.sm = sm;
        this.se = se;
    }

    public void step() {
    }

    public void findPath() {
    	Point goal = sm.goal;
    	Point start = new Point(se.map.bot.center.x, se.map.bot.center.y);
    	TreeNode root = new TreeNode(start);
    	Tree rrt = new Tree(root);
    	
    	Point p;	
    	TreeNode closest, newNode;
    	Segment seg;
    	
    	while (true){
    		p = se.map.randomPoint();
    		closest = root;
    		for (TreeNode node : rrt.nodes){
    			if (node.loc.distance(p) < closest.loc.distance(p)){
    				closest = node;
    			}
    		}
    		
    		seg = new Segment(closest.loc, p);
    		seg = seg.trim(MAXLENGTH);
    		
    		if (!se.map.checkSegment(seg)){
    			continue;
    		}
    		
    		newNode = new TreeNode(p);
    		closest.addChild(newNode);
    	}
    	
    }
}
