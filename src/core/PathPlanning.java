package core;

import state_machine.StateMachine;

public class PathPlanning {
    StateMachine sm;
    StateEstimator se;

    public PathPlanning(StateMachine sm, StateEstimator se) {
        this.sm = sm;
        this.se = se;
    }

    public void step() {
    }
}
