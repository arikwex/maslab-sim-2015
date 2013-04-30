package state_machine;

import core.Config;

public class TimeoutState extends State{
    private State prev;
    
    
    public TimeoutState() {
        super();
        tooLong = Config.CHALLENGE_TIME;
    }

    public TimeoutState(State prev) {
        super();
        this.prev = prev;
        tooLong = Config.CHALLENGE_TIME;
    }

    protected State transition() {
    	if (prev.getClass() == AssemblyState.class || prev.getClass() == FindShelterState.class)
    		return new ExploreState();
    	else if (prev.getClass() == CollectState.class || prev.getClass() == ExploreState.class)
    		return new FindShelterState();
        return this;
    }
    
    protected void run() {
        
    }
}
