package state_machine;

import core.Config;

public class ExploreState extends State{

	public ExploreState(StateMachine sm) {
        super(sm);
        tooLong = Config.EXPLORE_TOO_LONG;
	}

    protected State transition() {
    	if (machine.se.numBlocksLeft == 0){
    		machine.state = new StopState(machine);
    	}
    	else if (machine.se.map.bot.pose.distance(machine.goal)<Config.CLOSE_ENOUGH){
    		machine.state = new CollectState(machine);	
    	}
    	return machine.state;
    }
    
    protected void run() {
        this.machine.setGoal(this.machine.se.getClosestBlock());
    }
}
