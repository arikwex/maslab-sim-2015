package state_machine

public abstract class State {
    long startTime;
    
    public State() {
        startTime = System.currentTimeMillis();
    }
    
    public State step() {
        State next = self.transition()
        next.run()
        return next
    }
    
    private State transition();
    private void run();
}
