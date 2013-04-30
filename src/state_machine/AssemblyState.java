package state_machine;

import core.Config;

public class AssemblyState extends State{
    
    public AssemblyState(StateMachine sm) {
        super(sm);
        tooLong = Config.ASSEMBLY_TOO_LONG;
        // TODO Auto-generated constructor stub
    }

    protected State transition() {
    	if (machine.se.numCollectedBlocks==0){
    		machine.state = new ExploreState(machine);
    	}
        return machine.state;
    }
    
    protected void run() {
        
    }
}
