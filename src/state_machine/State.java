package state_machine;

public abstract class State {
    long startTime;
    
    public State() {
        startTime = System.currentTimeMillis();
    }
    
    public State step() {
        State next = this.transition();
        next.run();
        return next;
    }
    
    protected abstract State transition();
    protected abstract void run();
}
