package state_machine;

import core.Config;

public class TimeoutState extends State{
    
    
    private State prev;

	public TimeoutState(StateMachine sm) {
        super(sm);
        tooLong = Config.CHALLENGE_TIME;
    }
    public TimeoutState(StateMachine sm, State prev) {
        super(sm);
        this.prev = prev;
        tooLong = Config.CHALLENGE_TIME;
    }

    protected State transition() {
    	if (prev.getClass() == AssemblyState.class || prev.getClass() == FindShelterState.class)
    		machine.state = new ExploreState(machine);
    	else if (prev.getClass() == CollectState.class || prev.getClass() == ExploreState.class)
    		machine.state = new FindShelterState(machine);
        return machine.state;
    }
    
    protected void run() {
        
    }
}
