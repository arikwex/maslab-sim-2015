package state_machine;

import core.Config;
import core.StateEstimator;

public class ExploreState extends State{

	public ExploreState() {
        tooLong = Config.EXPLORE_TOO_LONG;
	}

    protected State transition() {
    	/*
    	if (se.numBlocksLeft == 0){
    		return new StopState();
    	}
    	*/
    	if (se.map.bot.pose.distance(se.getClosestBlock())<Config.CLOSE_ENOUGH){
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
