package state_machine.game;

import java.util.List;

import control.Control;
import control.ControlMode;
import rrt.PathPlanning;
import state_machine.State;
import state_machine.StateMachine;
import map.Map;
import map.elements.Stack;
import map.geom.Point;
import core.StateEstimator;

public class TravelState extends State {
	
	private Point curGoal = null;
	private boolean issued = false;
	private boolean arrived = false;
	private State parentState = null;
	
	public TravelState(State parent, Point curGoal) {
		this.curGoal = new Point(curGoal.x, curGoal.y);
		this.parentState = parent; 
	}
	
    public State transition() {
    	if (arrived) {
    		return parentState;
    	} else {
    		return this;
    	}
    }
    
    public void run() {
    	StateMachine sm = StateMachine.getInstance();
    	StateEstimator se = StateEstimator.getInstance();
    	PathPlanning pp = PathPlanning.getInstance();
    	Map m = Map.getInstance();
    	
    	if (!issued) {
    		Control.getInstance().setControlMode(ControlMode.TRAVEL_PLAN);
    		Control.getInstance().setTarget(curGoal);
    		issued = true;
    	} else {
	    	if (m.bot.pose.distance(curGoal) < 0.1) {
	    		arrived = true;
	    		Control.getInstance().setTarget(null);
	    	}
    	}
    }
}