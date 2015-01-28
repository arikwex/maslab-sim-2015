package state_machine.game;

import control.Control;
import control.ControlMode;
import map.Map;
import map.elements.Stack;
import map.geom.Point;
import hardware.Hardware;
import hardware.enums.ElevatorState;
import state_machine.State;
import utils.Utils;

public class DriveToStackState extends State {
	
	private boolean done = false;
	private Point extractionPoint = null;
	private Point alt = null;
	private int attempts = 0;
	
	private State parentState = null;
	
	public DriveToStackState(State parent, Point alt) {
		this.parentState = parent; 
		this.alt = alt;
	}
	
    public State transition() {
    	if (done) {
    		return parentState;
    	}
    	return this;
    }
    
    public void run() {
    	Map m = Map.getInstance();
    	
    	if (extractionPoint == null) {
	    	// TODO: select the stack based on vision (most centered)
	    	double minDiff = 15;
	    	double targetDist = 100000;
	    	Point targetStack = null;
	    	for (int i = 0; i < m.getStacks().size(); i++) {
	    		Stack stack = m.getStacks().get(i);
	    		double dist = stack.pt.distance(new Point(m.bot.pose.x, m.bot.pose.y));
	    		if (dist < 0.55) {
	    			double diff = Math.abs(Math.toDegrees(Utils.thetaDiff(m.bot.pose.theta, m.bot.pose.angleTo(stack.pt))));
	    			if (diff < minDiff) {
	    				minDiff = diff;
	    				targetDist = dist;
	    				targetStack = stack.pt;
	    			}
	    		}
	    	}
	    	
	    	if (targetStack != null) {
	    		double angleTo = m.bot.pose.angleTo(targetStack);
	    		extractionPoint = new Point(m.bot.pose.x, m.bot.pose.y);
	    		extractionPoint.x += Math.cos(angleTo) * (targetDist - 0.13);
	    		extractionPoint.y += Math.sin(angleTo) * (targetDist - 0.13);
	    		Control.getInstance().setControlMode(ControlMode.DRIVE_FORWARD);
	    		Control.getInstance().setTarget(extractionPoint);
	    	}
	    	
	    	// If there is no discovered stack, then simply travel
	    	// to the alternate expected location.
	    	if (attempts > 50) {
	    		extractionPoint = new Point(alt.x, alt.y);
	    		Control.getInstance().setControlMode(ControlMode.DRIVE_FORWARD);
	    		Control.getInstance().setTarget(extractionPoint);
	    	}
	    	
	    	attempts++;
    	} else {
	    	if (Control.getInstance().getDistanceToTarget() < 0.04) {
	    		done = true;
	    		Control.getInstance().setTarget(null);
	    	}
    	}
    }
}