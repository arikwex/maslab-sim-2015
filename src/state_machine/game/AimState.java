package state_machine.game;

import java.util.List;

import control.Control;
import control.ControlMode;
import rrt.PathPlanning;
import state_machine.State;
import state_machine.StateMachine;
import map.Map;
import map.geom.Point;
import core.StateEstimator;

public class AimState extends State {
	
	private Point curGoal = null;
	private boolean issued = false;
	private boolean finished = false;
	private State parentState = null;
	
	public AimState(State parent, Point curGoal) {
		this.curGoal = new Point(curGoal.x, curGoal.y);
		this.parentState = parent; 
	}
	
    public State transition() {
    	if (finished) {
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
    		Control.getInstance().setControlMode(ControlMode.AIM);
    		Control.getInstance().setTarget(curGoal);
    		issued = true;
    	} else {
	    	if (Math.abs(Control.getInstance().getAngleToTarget()) < 5) {
	    		finished = true;
	    	}
    	}
    }
}