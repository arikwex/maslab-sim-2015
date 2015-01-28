package state_machine.game;

import hardware.Hardware;
import hardware.enums.ElevatorState;
import state_machine.State;

public class ApplyElevatorState extends State {
	
	private long startTime = -1;
	private ElevatorState state = null;
	private State parentState = null;
	
	public ApplyElevatorState(State parent, ElevatorState state) {
		this.state = state;
		this.parentState = parent; 
	}
	
    public State transition() {
    	if (System.currentTimeMillis() - startTime > 500) {
    		return parentState;
    	} else {
    		return this;
    	}
    }
    
    public void run() {
    	if (startTime < 0) {
    		startTime = System.currentTimeMillis();
    		Hardware.getInstance().servoElevation.setValue(this.state.value);
    	}
    }
}