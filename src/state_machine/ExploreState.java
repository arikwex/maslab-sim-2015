package state_machine;

import map.Map;
import map.elements.Stack;
import map.geom.Point;
import core.StateEstimator;

public class ExploreState extends State {
	
	private Point curGoal = null;
	
    public State transition() {
        return this;
    }
    
    public void run() {
    	StateMachine sm = StateMachine.getInstance();
    	StateEstimator se = StateEstimator.getInstance();
    	Map m = Map.getInstance();
    	
    	if (curGoal == null) {
    		Stack dest = m.getStacks().get((int)(Math.random() * m.getStacks().size() * 0.999));
    		curGoal = new Point(dest.pt.x, dest.pt.y + 0.33);
    		sm.setGoal(curGoal);
    	} else {
	    	if (m.bot.pose.distance(curGoal) < 0.1) {
	    		curGoal = null;
	    	}
    	}
    }
}