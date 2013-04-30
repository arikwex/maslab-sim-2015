package state_machine;

import core.Config;

public class StopState extends State{
    
    
    public StopState(StateMachine sm) {
        super(sm);
        tooLong = Config.CHALLENGE_TIME;
    }

    protected State transition() {
        return machine.state;
    }
    
    protected void run() {
        this.machine.setGoal(this.machine.se.map.bot.pose);
    }
}
