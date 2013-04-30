package state_machine;

import core.StateEstimator;

public class ExploreState extends State{
    
    public ExploreState() {
        super();
    }

    protected State transition() {
        return this;
    }
    
    protected void run() {
        StateMachine sm = StateMachine.getInstance();
        StateEstimator se = StateEstimator.getInstance();
        sm.setGoal(se.getClosestBlock());
    }
}
