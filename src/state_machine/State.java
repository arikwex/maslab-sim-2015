package state_machine;

import core.StateEstimator;

public abstract class State {
    protected StateMachine sm;
    protected StateEstimator se;
    
    long startTime;
	protected long tooLong;
    
    public State(StateMachine sm) {
        startTime = System.currentTimeMillis();
        this.sm = sm;
        se = StateEstimator.getInstance();
    }
    
    public State step() {
        State next = this.transition();
        if (System.currentTimeMillis() - startTime >= this.tooLong){
        	System.out.println("Timeout State");
        	return new TimeoutState(sm,next);
        }
        else {
//        	System.out.println(next.getClass());
        	next.run();
        }
        return next;
        
    }
    
    protected abstract State transition();
    protected abstract void run();
}
