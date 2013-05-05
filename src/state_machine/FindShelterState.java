package state_machine;

import map.Map;
import core.Config;

public class FindShelterState extends State{
    public FindShelterState() {
    }

    protected State transition() {
    	if (se.map.bot.pose.distance(se.map.ShelterLocation)<Config.CLOSE_ENOUGH){
    		return new AssemblyState();	
    	}
    	return this;
    }
    
    protected void run() {
    	sm.setGoal(se.map.ShelterLocation);
    }
}
