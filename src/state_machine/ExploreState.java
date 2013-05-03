package state_machine;

import map.Map;
import core.Config;
import core.StateEstimator;

public class ExploreState extends State{

	public ExploreState(StateMachine sm) {
        super(sm);
        tooLong = Config.EXPLORE_TOO_LONG;
	}

    protected State transition() {
    	if (se.numBlocksLeft == 0){
    		return new StopState(sm);
    	}
    	else if (se.map.bot.pose.distance(sm.goal)<Config.CLOSE_ENOUGH){
    		return new CollectState(sm);	
    	}
    	return this;
    }
    
    protected void run() {
        StateMachine sm = StateMachine.getInstance();
        StateEstimator se = StateEstimator.getInstance();
        
        sm.setGoal(se.getClosestBlock());
    }
}
