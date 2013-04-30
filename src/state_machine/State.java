package state_machine;

import core.StateEstimator;

public abstract class State {
    protected StateMachine sm;
    protected StateEstimator se;
    
    long startTime;
	protected long tooLong;
    
    public State() {
        startTime = System.currentTimeMillis();
        sm = StateMachine.getInstance();
        se = StateEstimator.getInstance();
    }
    
    public State step() {
        State next = this.transition();
        if (System.currentTimeMillis() - startTime >= this.tooLong){
        	return new TimeoutState(next);
        }
        else {
        	next.run();
        }
        return next;
        
    }
    
    protected abstract State transition();
    protected abstract void run();
}
