package state_machine;

import map.Map;
import core.Config;
import core.StateEstimator;

public class ExploreState extends State{

	public ExploreState() {
        super();
        tooLong = Config.EXPLORE_TOO_LONG;
	}

    protected State transition() {
    	if (se.numBlocksLeft == 0){
    		return new StopState();
    	}
    	else if (Map.getInstance().bot.pose.distance(sm.goal)<Config.CLOSE_ENOUGH){
    		return new CollectState();	
    	}
    	return this;
    }
    
    protected void run() {
        StateMachine sm = StateMachine.getInstance();
        StateEstimator se = StateEstimator.getInstance();
        sm.setGoal(se.getClosestBlock());
    }
}
