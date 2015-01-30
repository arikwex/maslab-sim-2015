package state_machine.game;

import hardware.enums.ElevatorState;
import hardware.enums.GripperState;

import java.io.File;
import java.util.LinkedList;
import java.util.Queue;

import state_machine.State;
import map.geom.Point;
import mission.gameplan.PlanLoader;
import core.Config;

public class PlannerState extends State {
	
	Queue<State> actionQueue;
	
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
    }
    
    public static Point[] getPorts(Point hubCore, double heading) {
    	double split = 1.2;
    	double HUB_NEAR = Config.HUB_DISTANCE - 0.13;
    	Point hubA = new Point(hubCore.x + Math.cos(split + heading) * HUB_NEAR, hubCore.y + Math.sin(split + heading) * HUB_NEAR);
		Point hubB = new Point(hubCore.x + Math.cos(heading) * HUB_NEAR, hubCore.y + Math.sin(heading) * HUB_NEAR);
		Point hubC = new Point(hubCore.x + Math.cos(-split + heading) * HUB_NEAR, hubCore.y + Math.sin(-split + heading) * HUB_NEAR);
    	return new Point[]{hubA, hubB, hubC};
    }
}