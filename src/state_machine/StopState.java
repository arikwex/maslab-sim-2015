package state_machine;

import core.Config;

public class StopState extends State{
    public StopState() {
        tooLong = Config.CHALLENGE_TIME;
    }

    protected State transition() {
        return this;
    }
    
    protected void run() {
        sm.setGoal(se.map.bot.pose);
    }
}
