package state_machine;

import core.Config;

public abstract class State {
    StateMachine machine;
    long startTime;
	protected long tooLong;
    
    public State(StateMachine sm) {
        this.machine = sm;
        startTime = System.currentTimeMillis();
    }
    
    public State step() {
        State next = this.transition();
        if (System.currentTimeMillis() - startTime >= this.tooLong){
        	machine.state = new TimeoutState(machine, this);
        }
        else {
        	next.run();
        }
        return next;
        
    }
    
    protected abstract State transition();
    protected abstract void run();
}
