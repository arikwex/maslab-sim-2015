package state_machine.game;

import hardware.enums.ElevatorState;
import hardware.enums.GripperState;

import java.util.LinkedList;
import java.util.Queue;

import rrt.PathPlanning;
import state_machine.State;
import state_machine.StateMachine;
import map.Map;
import map.geom.Point;
import core.StateEstimator;

public class PlannerState extends State {
	
	Queue<State> actionQueue;
	private int stackIndex = 0;
	private final float HUB_DISTANCE = 0.4f;
	
	public PlannerState() {
		actionQueue = new LinkedList<State>();
		// Initialize
		actionQueue.add(new ApplyGripperState(this, GripperState.OPEN));
    	actionQueue.add(new ApplyElevatorState(this, ElevatorState.TRANSIT));
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
    	}
    }
    
    private Point[] getHubs(Point hubCore) {
    	double split = 1.0;
    	double HUB_NEAR = HUB_DISTANCE - 0.13;
    	Point hubA = new Point(hubCore.x + Math.cos(split) * HUB_NEAR, hubCore.y - Math.sin(split) * HUB_NEAR);
		Point hubB = new Point(hubCore.x, hubCore.y - HUB_NEAR);
		Point hubC = new Point(hubCore.x + Math.cos(-split) * HUB_NEAR, hubCore.y - Math.sin(-split) * HUB_NEAR);
    	return new Point[]{hubA, hubB, hubC};
    }
    	
	private void travelToHub(Point hubCore) {
		actionQueue.add(new TravelState(this, hubCore));
	}
	
	/* 
	 * Assumes the robot is current at a hub core
	 * Assumes there is nothing currently being held
	 */
	private void grabStack(Point hubCore, int index, ElevatorState collectionSetting) {
		Point[] hubs = getHubs(hubCore);
		
		actionQueue.add(new ApplyGripperState(this, GripperState.OPEN));
    	actionQueue.add(new ApplyElevatorState(this, ElevatorState.TRANSIT));
    	
    	actionQueue.add(new AimState(this, hubs[index]));
    	actionQueue.add(new DriveToStackState(this, hubs[index]));
    	
    	actionQueue.add(new ApplyElevatorState(this, collectionSetting));
    	actionQueue.add(new ApplyGripperState(this, GripperState.CLOSE));
    	actionQueue.add(new ApplyElevatorState(this, ElevatorState.TRANSIT));
    	actionQueue.add(new BackTravelState(this, hubCore));
	}
	
	/* 
	 * Assumes the robot is current at a hub core
	 * Assumes that there is something to drop
	 */
	private void dropStack(Point hubCore, int index, ElevatorState deploySetting) {
		Point[] hubs = getHubs(hubCore);

		actionQueue.add(new ApplyElevatorState(this, deploySetting));
    	
    	actionQueue.add(new AimState(this, hubs[index]));
    	actionQueue.add(new DriveToStackState(this, hubs[index]));
    	
    	actionQueue.add(new ApplyGripperState(this, GripperState.OPEN));
    	actionQueue.add(new BackTravelState(this, hubCore));
	}
	
	/* 
	 * Assumes the robot is current at a hub core
	 * Assumes that there is something to drop
	 * Assumes that you want to pick something else up before leaving
	 */
	private void dropAndGrabStack(Point hubCore, int index, ElevatorState deploySetting, ElevatorState collectionSetting) {
		Point[] hubs = getHubs(hubCore);

		// DROP
		actionQueue.add(new ApplyElevatorState(this, deploySetting));
    	
    	actionQueue.add(new AimState(this, hubs[index]));
    	actionQueue.add(new DriveToStackState(this, hubs[index]));
    	
    	actionQueue.add(new ApplyGripperState(this, GripperState.OPEN));
    	
    	// GRAB
    	actionQueue.add(new ApplyElevatorState(this, collectionSetting));
    	actionQueue.add(new ApplyGripperState(this, GripperState.CLOSE));
    	actionQueue.add(new ApplyElevatorState(this, ElevatorState.TRANSIT));
    	
    	actionQueue.add(new BackTravelState(this, hubCore));
	}
}