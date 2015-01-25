package state_machine;

import logging.Log;
import map.geom.Point;

public class StateMachine {

    private static StateMachine instance;
    
    private State state;
    private Point goal;
    
    public StateMachine() {
    }
    
    public static StateMachine getInstance() {
        if (instance == null)
            instance = new StateMachine();
        return instance;   
    } 
    
    public void step() {
    	if (state == null) {
    	    state = new ExploreState();
    	}
        state = state.step();
    }
    
    protected void setGoal(Point p) {
        this.goal = p;
        Log.log(this.goal.toString());
    }
    
    public Point getGoal() {
        return goal;
    }
}
