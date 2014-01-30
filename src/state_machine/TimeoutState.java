package state_machine;

import map.Map;
import core.Config;

public class TimeoutState extends State{
    
    
    public TimeoutState() {
        tooLong = Config.CHALLENGE_TIME;
    }

    protected State transition() {
    	// TODO: Implement a reasonable timeout action
        return this;
    }
    
    protected void run() {
        
    }
}
