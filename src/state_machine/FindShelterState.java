package state_machine;

import map.Map;
import core.Config;

public class FindShelterState extends State{
    public FindShelterState() {
        super();
    }

    protected State transition() {
    	if (Map.getInstance().bot.pose.distance(sm.goal)<Config.CLOSE_ENOUGH){
    		return new AssemblyState();	
    	}
    	return this;
    }
    
    protected void run() {
    	sm.setGoal(Map.getInstance().ShelterLocation);
    }
}
