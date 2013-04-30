package state_machine;

import core.Config;

public class CollectState extends State{
    
	boolean blockCollected;
	
    public CollectState(StateMachine sm) {
        super(sm);
        tooLong = Config.COLLECT_TOO_LONG;
    }

    protected State transition() {
    	if (blockCollected){
    		machine.se.numCollectedBlocks++;
    		if (machine.se.numCollectedBlocks >= Config.BIN_CAPACITY){
    			machine.state = new FindShelterState(machine);
    		}
    		else {
    			machine.state = new ExploreState(machine);
    		}
    	}
		return machine.state;
    }
    
    protected void run() {
        
    }
}
