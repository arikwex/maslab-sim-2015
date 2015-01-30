package state_machine.game;

import hardware.enums.ElevatorState;
import hardware.enums.GripperState;

import java.io.File;
import java.util.LinkedList;
import java.util.Queue;

import rrt.PathPlanning;
import state_machine.State;
import state_machine.StateMachine;
import map.Map;
import map.geom.Point;
import mission.gameplan.PlanLoader;
import core.StateEstimator;

public class PlannerState extends State {
	
	Queue<State> actionQueue;
	//private int stackIndex = 0;
	public final static float HUB_DISTANCE = 0.2f;
	
	public PlannerState() {
		actionQueue = new LinkedList<State>();
		// Initialize
		actionQueue.add(new ApplyGripperState(this, GripperState.OPEN));
    	actionQueue.add(new ApplyElevatorState(this, ElevatorState.TRANSIT));
    	
    	// Enqueue actions
    	actionQueue.addAll(PlanLoader.load(this, new File("mapPlans/practice_field.txt")));
	}
	
    public State transition() {
    	if (actionQueue.size() > 0) {
    		return actionQueue.poll();
    	}
        return this;
    }
    
    public void run() {
    	StateMachine sm = StateMachine.getInstance();
    	StateEstimator se = StateEstimator.getInstance();
    	PathPlanning pp = PathPlanning.getInstance();
    	Map m = Map.getInstance();
    	/*
    	if (actionQueue.isEmpty()) {
    		Point dest = m.getStacks().get(stackIndex).pt;
    		Point hubCore = new Point(dest.x, dest.y + HUB_DISTANCE);
    		
    		// Go to location
    		travelToHub(hubCore);
    		
	    	// Grab stack
	    	grabStack(hubCore, 1, ElevatorState.MIDDLE);
	    	dropAndGrabStack(hubCore, 0, ElevatorState.BOTTOM, ElevatorState.MIDDLE);
	    	dropStack(hubCore, 1, ElevatorState.MIDDLE);
	    	stackIndex++;
    	}*/
    }
    
    public Point[] getPorts(Point hubCore, double heading) {
    	double split = 0.4;
    	double HUB_NEAR = HUB_DISTANCE - 0.13;
    	Point hubA = new Point(hubCore.x + Math.cos(split + heading) * HUB_NEAR, hubCore.y + Math.sin(split + heading) * HUB_NEAR);
		Point hubB = new Point(hubCore.x + Math.cos(heading) * HUB_NEAR, hubCore.y + Math.sin(split + heading) * HUB_NEAR);
		Point hubC = new Point(hubCore.x + Math.cos(-split + heading) * HUB_NEAR, hubCore.y + Math.sin(-split + heading) * HUB_NEAR);
    	return new Point[]{hubA, hubB, hubC};
    }
}