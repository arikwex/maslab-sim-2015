package state_machine;

import core.Config;

public class FindShelterState extends State{
    public FindShelterState(StateMachine sm) {
        super(sm);
    }

    protected State transition() {
    	if (machine.se.map.bot.pose.distance(machine.goal)<Config.CLOSE_ENOUGH){
    		machine.state = new AssemblyState(machine);	
    	}
    	return machine.state;
    }
    
    protected void run() {
    	this.machine.setGoal(this.machine.se.map.ShelterLocation);
    }
}
