package state_machine;

import map.Map;
import core.Config;

public class StopState extends State{
    
    
    public StopState() {
        super();
        tooLong = Config.CHALLENGE_TIME;
    }

    protected State transition() {
        return this;
    }
    
    protected void run() {
        sm.setGoal(Map.getInstance().bot.pose);
    }
}
