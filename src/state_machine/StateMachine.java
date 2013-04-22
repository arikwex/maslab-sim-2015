package state_machine;

import core.StateEstimator;

public class StateMachine {
    private StateEstimator se;

    private State state;
    
    public StateMachine(StateEstimator se) {
        this.se = se;
        state = new ExploreState();
    }
    
    public void step() {
        state = state.step();
    }
}
