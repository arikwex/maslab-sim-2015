package state_machine.game;

import hardware.Hardware;
import hardware.enums.GripperState;
import state_machine.State;

public class ApplyGripperState extends State {
	
	private long startTime = -1;
	private GripperState state = null;
	private State parentState = null;
	
	public ApplyGripperState(State parent, GripperState state) {
		this.state = state;
		this.parentState = parent; 
	}
	
    public State transition() {
    	if (System.currentTimeMillis() - startTime > 300) {
    		return parentState;
    	} else {
    		return this;
    	}
    }
    
    public void run() {
    	if (startTime < 0) {
    		startTime = System.currentTimeMillis();
    		Hardware.getInstance().servoGrip.setValue(this.state.value);
    	}
    }
}