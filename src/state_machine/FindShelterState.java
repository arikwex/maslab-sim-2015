package state_machine;

import map.Map;
import core.Config;

public class FindShelterState extends State{
    public FindShelterState(StateMachine sm) {
        super(sm);
    }

    protected State transition() {
    	if (se.map.bot.pose.distance(sm.goal)<Config.CLOSE_ENOUGH){
    		return new AssemblyState(sm);	
    	}
    	return this;
    }
    
    protected void run() {
    	sm.setGoal(se.map.ShelterLocation);
    }
}
