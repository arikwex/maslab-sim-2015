package state_machine;

import map.Point;
import core.StateEstimator;

public class StateMachine {
    protected StateEstimator se;

    protected State state;
    public Point goal;
    
    public StateMachine(StateEstimator se) {
        this.se = se;
        state = new ExploreState(this);
    }
    
    public void step() {
        state = state.step();
    }
    
    protected void setGoal(Point p) {
        this.goal = p;
    }
    
    public Point getGoal() {
        return goal;
    }
}
